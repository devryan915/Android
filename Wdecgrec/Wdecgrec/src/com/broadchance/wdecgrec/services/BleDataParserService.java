package com.broadchance.wdecgrec.services;

import java.nio.IntBuffer;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.broadchance.entity.FrameData;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.PlayerManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.main.EcgActivity;

@SuppressLint("NewApi")
public class BleDataParserService extends Service {
	private final static String TAG = BleDataParserService.class
			.getSimpleName();
	public final static String ACTION_ECGMII_DATA_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMII_DATA_AVAILABLE";
	public final static String ACTION_ECGMV1_DATA_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMV1_DATA_AVAILABLE";
	public final static String ACTION_ECGMV5_DATA_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMV5_DATA_AVAILABLE";

	public final static int SIGNAL_MAX = -50;
	public final static int SIGNAL_MIN = -70;
	public final static float POWER_MAX = 2.9f;
	public final static float POWER_MIN = 2.8f;
	public static int rssiValue = 0;

	private Queue<FrameData> receivedQueue = new LinkedList<FrameData>();
	private Queue<FrameData> dealQueue = new LinkedList<FrameData>();
	private Timer processFrameDataTimer;
	private TimerTask processFrameDataTask;

	// private Timer mTimer = new Timer();
	// private TimerTask mTask;
	/**
	 * 一帧的数据点数
	 */
	private IntBuffer miiBuffer = IntBuffer.allocate(10);
	private IntBuffer mv1Buffer = IntBuffer.allocate(10);
	private IntBuffer mv5Buffer = IntBuffer.allocate(10);

	/**
	 * 校验蓝牙数据 1、错位补零 2、丢帧补帧
	 * 
	 * @param bleData
	 * @return
	 */
	// private int[] checkBleData() {
	// FrameDataMachine fdm = FrameDataMachine.getInstance();
	// int length = dealQueue.size();
	// int[] bleData = new byte[20 * length];
	// for (int i = 0; i < length; i++) {
	// byte[] temp = dealQueue.poll().data;
	// fdm.processFrameData(dealQueue.poll());
	// for (int j = 0; j < 20; j++) {
	// bleData[i * 20 + j] = temp[j];
	// }
	// }
	// return bleData;
	// }
	//
	// private byte[] splitChannel(byte[] bleData) {
	// return bleData;
	// }
	//
	// /**
	// * 滤波算法
	// *
	// * @param bleData
	// * @return
	// */
	// private byte[] filterBleData(byte[] bleData) {
	// return bleData;
	// }

	private void sendECGData(IntBuffer buffer, Integer value, String action) {
		if ((!buffer.hasRemaining() || value == null) && buffer.position() > 0) {
			Intent intent = new Intent();
			intent.setAction(action);
			int[] dst = new int[buffer.position()];
			// System.arraycopy(buffer.array(), 0, dst, 0, dst.length);
			buffer.position(0);
			buffer.get(dst);
			buffer.position(0);
			intent.putExtra(BluetoothLeService.EXTRA_DATA, dst);
			sendBroadcast(intent);
		}
	}

	private void processReceivedByte() {
		int length = dealQueue.size();
		if (length <= 0)
			return;
		FrameDataMachine fdm = FrameDataMachine.getInstance();
		Integer miiValue = null;
		Integer mv1Value = null;
		Integer mv5Value = null;
		for (int i = 0; i < length; i++) {
			fdm.processFrameData(dealQueue.poll());
			while (true) {
				miiValue = fdm.getMII();
				if (miiValue != null) {
					miiBuffer.put(miiValue);
				}
				mv1Value = fdm.getMV1();
				if (mv1Value != null) {
					mv1Buffer.put(mv1Value);
				}
				mv5Value = fdm.getMV5();
				if (mv5Value != null) {
					mv5Buffer.put(mv5Value);
				}
				sendECGData(miiBuffer, miiValue, ACTION_ECGMII_DATA_AVAILABLE);
				sendECGData(mv1Buffer, mv1Value, ACTION_ECGMV1_DATA_AVAILABLE);
				sendECGData(mv5Buffer, mv5Value, ACTION_ECGMV5_DATA_AVAILABLE);
				if (miiValue == null && mv1Value == null && mv5Value == null) {
					break;
				}
			}
		}
	}

	public class LocalBinder extends Binder {
		public BleDataParserService getService() {
			return BleDataParserService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		receivedQueue.clear();
		startTimer();
		acquireWakeLock();
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mGattUpdateReceiver);
		cancelTimer();
		receivedQueue.clear();
		receivedQueue = null;
		releaseWarkLock();
		super.onDestroy();
	};

	public void startTimer() {
		processFrameDataTask = new TimerTask() {
			@Override
			public void run() {
				synchronized (receivedQueue) {
					int qLen = receivedQueue.size();
					for (int i = 0; i < qLen; i++) {
						dealQueue.offer(receivedQueue.poll());
					}
					processReceivedByte();
				}
			}
		};
		processFrameDataTimer = new Timer();
		processFrameDataTimer.schedule(processFrameDataTask, 0, 40);

	}

	public void cancelTimer() {
		if (processFrameDataTimer != null) {
			processFrameDataTimer.cancel();
			processFrameDataTimer = null;
		}
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				byte[] data = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				synchronized (receivedQueue) {
					// for (int i = 0; i < data.length; i++) {
					// receivedQueue.offer(data[i]);
					// }
					try {
						FrameData FrameData = new FrameData(data, new Date());
						receivedQueue.offer(FrameData);
					} catch (Exception e) {
						LogUtil.e(TAG, e);
					}
				}
				// if (ConstantConfig.Debug) {
				// String bleData = BleDataUtil.dumpBytesAsString(data);
				// if (ConstantConfig.Debug) {
				// SimpleDateFormat sdf = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");
				// Date date = new Date();
				// LogUtil.d(TAG, "DateTime " + sdf.format(date) + " "
				// + bleData);
				// }
				// }
			} else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				startTimer();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)
					|| BluetoothLeService.ACTION_GATT_RECONNECTING
							.equals(action)) {
				// 设备断开
				PlayerManager.getInstance().playDevOff();
				cancelTimer();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (action
					.equals(BluetoothLeService.ACTION_GATT_RSSICHANGED)) {
				int rssi = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
				if (rssiValue == 0) {
					rssiValue = rssi;
				} else {
					rssiValue = (int) (rssiValue * 0.9f + rssi * 0.1f);
					if (rssi <= SIGNAL_MIN) {
						// 信号低
						PlayerManager.getInstance().playLowSignal();
					}
				}
				UIUtil.showToast(context, "蓝牙信号 rssi:" + rssi + " rssiValue:"
						+ rssiValue);

			} else if (action
					.equals(BluetoothLeService.ACTION_GATT_POWERCHANGED)) {
				float power = intent.getFloatExtra(
						BluetoothLeService.EXTRA_DATA, 0);
				UIUtil.showToast(context, "蓝牙电量 power:" + power);
				if (power <= POWER_MIN && power > 0) {
					// 电量低
					PlayerManager.getInstance().playLowPower();
					Notification notification = new Notification(
							R.drawable.ic_launcher,
							getString(R.string.app_name),
							System.currentTimeMillis());
					// Intent intent = new Intent(context, EcgActivity.class);
					PendingIntent pendingintent = PendingIntent.getActivity(
							context, 0, null, 0);
					notification.setLatestEventInfo(context, "穿戴设备",
							"设备电量低，请更换电极片！", pendingintent);
					startForeground(0x111, notification);
					UIUtil.showLongToast(context, "设备电量低，请更换电极片！");
				}
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RECONNECTING);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);

		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSICHANGED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_POWERCHANGED);
		return intentFilter;
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	private WakeLock wakeLock = null;

	private void acquireWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}

	private void releaseWarkLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}
