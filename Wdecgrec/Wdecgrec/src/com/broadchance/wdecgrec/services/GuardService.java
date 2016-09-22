package com.broadchance.wdecgrec.services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.main.ModeActivity;
import com.broadchance.wdecgrec.receiver.PowerChangeReceiver;

public class GuardService extends Service {
	protected static final String TAG = GuardService.class.getSimpleName();
	private PowerChangeReceiver batteryReceiver = new PowerChangeReceiver();
	private BluetoothLeService mBluetoothLeService;
	private final static String ServiceName = ConstantConfig.PKG_NAME
			+ ".services.GuardService";
	private ScheduledExecutorService mEService = Executors
			.newScheduledThreadPool(2);
	private ScheduledFuture<?> mFuture = null;
	private ScheduledFuture<?> mDelayFuture = null;
	public static GuardService Instance = null;
	public final static String ACTION_GATT_RSSICHANGED = ConstantConfig.ACTION_PREFIX
			+ "ACTION_GATT_RSSICHANGED";
	public final static String ACTION_GATT_POWERCHANGED = ConstantConfig.ACTION_PREFIX
			+ "ACTION_GATT_POWERCHANGED";
	private long DataALiveTime = 0;
	/**
	 * 蓝牙服务启动后延迟初始化蓝牙
	 */
	private final static int BLE_INIT_DELAY = 5 * 1000;
	/**
	 * 检查ble连接是否超时
	 */
	private final static int CHECK_BLE_TIMEOUT = 5 * 1000;
	private final static int CHECK_BLE_DELAY = 15 * 1000;
	private static final long SCAN_PERIOD = 3000;
	private boolean mScanning;
	private Handler mHandler = new Handler();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager mBluetoothManager;

	class LostBleEL {
		public long FirstLostTime;
		public long lastLostTime;
		public int lostTimes;
	}

	LostBleEL lostble = null;
	/**
	 * 判断时间内蓝牙服务频繁开启次数，以此来判断蓝牙是否稳定
	 */
	private final static int BLE_LOST = 5 * 1000;
	private final static int BLE_LOST_TIMES = 3;
	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				LogUtil.e(TAG, "Unable to initialize Bluetooth");
				return;
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			scanLeDevice();
			LogUtil.d(TAG, "ble Service connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			LogUtil.d(TAG, "ble Service disconnected");
		}
	};

	private final BroadcastReceiver bluetoothStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			DataALiveTime = System.currentTimeMillis() + 10 * CHECK_BLE_DELAY;
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
					LogUtil.d(TAG, "蓝牙服务关闭");
					if (mBluetoothLeService != null) {
						mBluetoothLeService.enableBleService();
						mBluetoothLeService.lostService();
					}
				} else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
					LogUtil.d(TAG, "蓝牙服务开启");
					if (mDelayFuture != null && !mDelayFuture.isDone()) {
						mDelayFuture.cancel(true);
					}
					// 延迟初始化
					mDelayFuture = mEService.schedule(new Runnable() {

						@Override
						public void run() {
							if (mBluetoothLeService != null) {
								mBluetoothLeService.initialize();
								DataALiveTime = System.currentTimeMillis()
										+ CHECK_BLE_DELAY;
							}
						}
					}, BLE_INIT_DELAY, TimeUnit.MILLISECONDS);
				}
			}
		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_TIME_TICK.equals(action)) {
				boolean isServiceRunning = false;
				// 检查Service状态
				ActivityManager manager = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningServiceInfo> serviceInfos = manager
						.getRunningServices(Integer.MAX_VALUE);
				for (RunningServiceInfo service : serviceInfos) {
					if (ServiceName.equals(service.service.getClassName())) {
						isServiceRunning = true;
						break;
					}
				}
				if (!isServiceRunning) {
					LogUtil.i(TAG, "restart " + ServiceName);
					Intent serviceIntent = new Intent(context,
							GuardService.class);
					context.startService(serviceIntent);
				} else {
					LogUtil.i(TAG, ServiceName + " running");
				}
			}
		}
	};

	/**
	 * 重新连接
	 */
	public void resetBleCon() {
		if (mBluetoothLeService != null) {
			mBluetoothLeService.close();
			scanLeDevice();
		}
	}

	/**
	 * 扫描蓝牙设备
	 * 
	 * @param enable
	 */
	private void scanLeDevice() {
		if (!mScanning) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					LogUtil.i(TAG, " stop scanLeDevice ");
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					// if (ConstantConfig.Debug) {
					UIUtil.showToast("未能找到指定设备");
					// }
					// _connect();
				}
			}, SCAN_PERIOD);
			setDataAliveTime(System.currentTimeMillis() + SCAN_PERIOD);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}
	}

	public void setDataAliveTime(long time) {
		DataALiveTime = time;
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			UIUserInfoLogin user = DataManager.getUserInfo();
			if (user != null) {
				String deviceNumber = user.getMacAddress();
				if (deviceNumber.equals(device.getAddress())) {
					// if (ConstantConfig.Debug) {
					UIUtil.showToast("扫描到指定蓝牙并开始连接");
					// }
					_connect();
				}
			}
		}
	};

	private void _connect() {
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect();
			LogUtil.d(TAG, "Connect request result=" + result);
		}
	}

	private void checkLost(boolean isStart) {
		try {
			if (lostble == null) {
				lostble = new LostBleEL();
				lostble.FirstLostTime = System.currentTimeMillis();
				lostble.lastLostTime = lostble.FirstLostTime;
				lostble.lostTimes = 1;
			} else {
				if (lostble.lastLostTime - lostble.FirstLostTime < BLE_LOST
						&& lostble.lostTimes > BLE_LOST_TIMES) {
					lostble = null;

				} else {
					if (lostble.lastLostTime - lostble.FirstLostTime >= BLE_LOST) {
						lostble = null;
					} else {
						lostble.lastLostTime = System.currentTimeMillis();
						lostble.lostTimes++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkBleStatus() {
		mFuture = mEService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					if (mBluetoothLeService != null) {
						mBluetoothLeService.readRemoteRssi();
					}
					long timeout = System.currentTimeMillis() - DataALiveTime;
					if (timeout > CHECK_BLE_TIMEOUT) {
						if (mBluetoothLeService != null) {
							if (timeout < 3 * CHECK_BLE_TIMEOUT) {
								// 断开连接BluetoothGatt
								mBluetoothLeService.disconnect();
								if (ConstantConfig.Debug) {
									UIUtil.showToast("连接蓝牙");
								}
								_connect();
							} else {
								// 关闭BluetoothGatt，重新获取新的BluetoothGatt
								mBluetoothLeService.close();
								scanLeDevice();
								DataALiveTime = System.currentTimeMillis()
										+ SCAN_PERIOD;
								if (ConstantConfig.Debug) {
									UIUtil.showToast("扫描蓝牙");
								}
							}
						} else {
							DataALiveTime = System.currentTimeMillis();
						}
						LogUtil.d(TAG, "Connect request timeout");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, CHECK_BLE_DELAY, CHECK_BLE_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	private void cancelCheckBleStatus() {
		if (mFuture != null && !mFuture.isDone()) {
			mFuture.cancel(true);
		}
		ActivityManager a;
	}

	public void bindBleService() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ModeActivity.Instance != null) {
			Intent gattServiceIntent = new Intent(ModeActivity.Instance,
					BluetoothLeService.class);
			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		}
	}

	public void unBindBLeService() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ModeActivity.Instance != null) {
			unbindService(mServiceConnection);
			mBluetoothLeService = null;
		}
	}

	private void start() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return;
			}
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return;
		}
		registerReceiver(batteryReceiver, makeBatteryIntentFilter());
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		registerReceiver(bluetoothStatusChangeReceiver,
				makeBLEStatusIntentFilter());
		bindBleService();
		Intent dataParserIntent = new Intent(GuardService.this,
				BleDataParserService.class);
		startService(dataParserIntent);
		// Intent bleDomainIntent = new Intent(GuardService.this,
		// BleDomainService.class);
		// startService(bleDomainIntent);
		checkBleStatus();
		Instance = this;
		acquireWakeLock();
	}

	private void end() {
		unregisterReceiver(batteryReceiver);
		unregisterReceiver(mGattUpdateReceiver);
		unregisterReceiver(bluetoothStatusChangeReceiver);
		unBindBLeService();
		Intent dataParserIntent = new Intent(GuardService.this,
				BleDataParserService.class);
		stopService(dataParserIntent);
		// Intent bleDomainIntent = new Intent(GuardService.this,
		// BleDomainService.class);
		// stopService(bleDomainIntent);
		cancelCheckBleStatus();
		Instance = null;
		releaseWarkLock();
	}

	private static IntentFilter makeBLEStatusIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		return intentFilter;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction(Intent.ACTION_TIME_TICK);
		return intentFilter;
	}

	private static IntentFilter makeBatteryIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		return intentFilter;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		start();
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		end();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	private WakeLock wakeLock = null;

	/**
	 * 请求唤醒锁防止休眠
	 */
	private void acquireWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}

	/**
	 * 释放唤醒锁
	 */
	private void releaseWarkLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}
