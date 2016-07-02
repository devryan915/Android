/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.broadchance.wdecgrec.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import com.broadchance.entity.BleDev;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.SampleGattAttributes;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.main.EcgActivity;
import com.broadchance.wdecgrec.main.ModeActivity;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();
	private static BluetoothLeService Instance;

	public static BluetoothLeService getInstance() {
		return Instance;
	}

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	private static final int STATE_SERVICES_DISCOVERED = 3;

	// private final static int ALIVE_PERIOD = 15 * 1000;
	private final static int CHECK_PERIOD = 10 * 1000;
	private final static int BLE_CON_DELAY = 3 * 1000;

	/**
	 * 由于ble本身对蓝牙断开响应不够及时，所以自定义检查蓝牙是否超时 当蓝牙实际已经超时时，ble本身的状态依然是连接状态，延迟发出断开信号
	 * 而在蓝牙实际断开和ble发出断开信号之间
	 * ，如果关闭蓝牙mBluetoothGatt.disconnect()；mBluetoothGatt.close()；
	 * 此时依然会触发蓝牙连接状态改变，而此时的改变值是连接状态，为了避免此错误信号，定义以下标志位
	 */
	// private boolean bleStateEnable = false;

	private long DataAliveTime;

	public final static String ACTION_GATT_CONNECTED = ConstantConfig.ACTION_PREFIX
			+ "bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = ConstantConfig.ACTION_PREFIX
			+ "bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = ConstantConfig.ACTION_PREFIX
			+ "bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_GATT_RECONNECTING = ConstantConfig.ACTION_PREFIX
			+ "bluetooth.le.ACTION_GATT_RECONNECTING";
	public final static String ACTION_GATT_CONNECTING = ConstantConfig.ACTION_PREFIX
			+ "bluetooth.le.ACTION_GATT_CONNECTING";
	public final static String ACTION_DATA_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "bluetooth.le.ACTION_DATA_AVAILABLE";
	// private final static String ACTION_ALARM_SEND_ALIVE_FRAME =
	// ConstantConfig.ACTION_PREFIX
	// + "aliveframe";
	private final static String ACTION_ALARM_SEND_CHECKBLESTATE = ConstantConfig.ACTION_PREFIX
			+ "blestate";
	public final static String ACTION_GATT_RSSICHANGED = ConstantConfig.ACTION_PREFIX
			+ "ACTION_GATT_RSSICHANGED";
	public final static String ACTION_GATT_POWERCHANGED = ConstantConfig.ACTION_PREFIX
			+ "ACTION_GATT_POWERCHANGED";
	public final static String EXTRA_DATA = ConstantConfig.ACTION_PREFIX
			+ "bluetooth.le.EXTRA_DATA";

	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
			.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
	private BluetoothGattCharacteristic characteristicToWrite;
	private byte[] setTimeFrame;

	public static final String LIST_NAME = "NAME";
	public static final String LIST_UUID = "UUID";

	private static final int SEND_FRAME_TYPE_SETTIME = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 5000;
	private boolean mScanning;
	private Handler mHandler;

	ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
	ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	ArrayList<String> serviceUUIDList = new ArrayList<String>();

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// if
			// (BluetoothLeService.ACTION_ALARM_SEND_ALIVE_FRAME.equals(action))
			// {
			// if (mConnectionState == STATE_SERVICES_DISCOVERED) {
			// sendCtrlFrame(SEND_FRAME_TYPE_SETTIME);
			// }
			// } else
			if (BluetoothLeService.ACTION_ALARM_SEND_CHECKBLESTATE
					.equals(action)) {
				// if (mConnectionState == STATE_CONNECTING) {
				// broadcastUpdate(ACTION_GATT_CONNECTING);
				// return;
				// }
				// if (mConnectionState == STATE_SERVICES_DISCOVERED
				// || mConnectionState == STATE_CONNECTED) {
				// return;
				// }
				if (mBluetoothGatt != null)
					mBluetoothGatt.readRemoteRssi();
				if (System.currentTimeMillis() - DataAliveTime > CHECK_PERIOD) {
					if (mScanning) {
						DataAliveTime = System.currentTimeMillis();
						return;
					}
					reconnect();
				}
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				DataAliveTime = System.currentTimeMillis();
				// LogUtil.i(TAG, "BluetoothLeService received data");
			} else if (Intent.ACTION_TIME_TICK.equals(action)) {
				boolean isServiceRunning = false;
				String serviceName = ConstantConfig.PKG_NAME
						+ ".services.BluetoothLeService";
				// 检查Service状态
				ActivityManager manager = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningServiceInfo> serviceInfos = manager
						.getRunningServices(Integer.MAX_VALUE);
				for (RunningServiceInfo service : serviceInfos) {
					if (serviceName.equals(service.service.getClassName())) {
						isServiceRunning = true;
						break;
					}
				}
				if (!isServiceRunning) {
					LogUtil.i(TAG, "restart BluetoothLeService");
					Intent serviceIntent = new Intent(context,
							BluetoothLeService.class);
					context.startService(serviceIntent);
				} else {
					LogUtil.i(TAG, "BluetoothLeService running");
				}
			}
		}
	};

	// Implements callback methods for GATT events that the app cares about. For
	// tiannma,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				boolean isDiscoverServcies = mBluetoothGatt.discoverServices();
				DataAliveTime = System.currentTimeMillis();
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				LogUtil.i(TAG, "ble STATE_CONNECTED");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				broadcastUpdate(intentAction);
				LogUtil.i(TAG, "ble STATE_DISCONNECTED");
			} else if (newState == BluetoothProfile.STATE_CONNECTING) {
				intentAction = ACTION_GATT_CONNECTING;
				mConnectionState = STATE_CONNECTING;
				broadcastUpdate(intentAction);
				LogUtil.i(TAG, "ble STATE_CONNECTING");
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			LogUtil.i(TAG, "ble rssi" + rssi);
			Intent intent = new Intent(ACTION_GATT_RSSICHANGED);
			intent.putExtra(EXTRA_DATA, rssi);
			sendBroadcast(intent);
			intent = new Intent(ACTION_GATT_POWERCHANGED);
			intent.putExtra(EXTRA_DATA, FrameDataMachine.getInstance()
					.getPower());
			sendBroadcast(intent);
			super.onReadRemoteRssi(gatt, rssi, status);
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				DataAliveTime = System.currentTimeMillis();
				displayGattServices();
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				mConnectionState = STATE_SERVICES_DISCOVERED;
				sendCtrlFrame(SEND_FRAME_TYPE_SETTIME);
			} else {
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
				// LogUtil.i(
				// TAG,
				// "onCharacteristicRead\n"
				// + BleDataUtil.dumpBytesAsString(characteristic
				// .getValue()));
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			// if (ConstantConfig.Debug) {
			// SimpleDateFormat sdf = new SimpleDateFormat(
			// "yyyy-MM-dd HH:mm:ss");
			// Date date = new Date();
			// LogUtil.d(
			// TAG,
			// "onCharacteristicChanged\n"
			// + "DateTime "
			// + sdf.format(date)
			// + " "
			// + BleDataUtil.dumpBytesAsString(characteristic
			// .getValue()));
			// }

		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				LogUtil.d(TAG, "Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				LogUtil.d(TAG, "Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			LogUtil.d(TAG, String.format("Received heart rate: %d", heartRate));
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			intent.putExtra(EXTRA_DATA, data);
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	// private void startAliveFrameTimer() {
	// Intent intent = new Intent();
	// intent.setAction(ACTION_ALARM_SEND_ALIVE_FRAME);
	// AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
	// PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
	// Calendar c = Calendar.getInstance();
	// long triggerAtTime = c.getTimeInMillis();
	// am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, ALIVE_PERIOD, pi);
	// }
	//
	// private void cancelAliveFrameTimer() {
	// Intent intent = new Intent();
	// intent.setAction(ACTION_ALARM_SEND_ALIVE_FRAME);
	// PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
	// AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
	// alarm.cancel(sender);
	// }

	private void startCheckBleStateTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_SEND_CHECKBLESTATE);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.setRepeating(AlarmManager.RTC, triggerAtTime + BLE_CON_DELAY,
				CHECK_PERIOD, pi);
	}

	private void cancelCheckBleStateTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_SEND_CHECKBLESTATE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(sender);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		start();
		flags = START_STICKY;
		// Notification notification = new Notification(R.drawable.ic_launcher,
		// getString(R.string.app_name), System.currentTimeMillis());
		// PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
		// new Intent(this, EcgActivity.class), 0);
		// notification.setLatestEventInfo(this, "穿戴设备", "请保持程序在后台运行",
		// pendingintent);
		// startForeground(0x111, notification);
		LogUtil.e(TAG, "BluetoothLeService start");
		Instance = this;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		end();
		LogUtil.e(TAG, "BluetoothLeService destroy");
		super.onDestroy();
	}

	private void start() {
		if (initialize()) {
			createAliveFrame();
			connect();
			// scanLeDevice(true);
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			// startAliveFrameTimer();
			startCheckBleStateTimer();
			Intent dataParserIntent = new Intent(BluetoothLeService.this,
					BleDataParserService.class);
			startService(dataParserIntent);
			Intent bleDomainIntent = new Intent(BluetoothLeService.this,
					BleDomainService.class);
			startService(bleDomainIntent);
		}
	}

	private void end() {
		unregisterReceiver(mGattUpdateReceiver);
		// cancelAliveFrameTimer();
		cancelCheckBleStateTimer();
		close();
		Intent dataParserIntent = new Intent(BluetoothLeService.this,
				BleDataParserService.class);
		stopService(dataParserIntent);
		Intent bleDomainIntent = new Intent(BluetoothLeService.this,
				BleDomainService.class);
		stopService(bleDomainIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		// intentFilter
		// .addAction(BluetoothLeService.ACTION_ALARM_SEND_ALIVE_FRAME);
		intentFilter
				.addAction(BluetoothLeService.ACTION_ALARM_SEND_CHECKBLESTATE);
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		return intentFilter;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// tiannma, close() is
		// invoked when the UI is disconnected from the Service.

		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				LogUtil.e(TAG, new Exception(
						"Unable to initialize BluetoothManager."));
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			LogUtil.e(TAG,
					new Exception("Unable to obtain a BluetoothAdapter."));
			return false;
		}
		mHandler = new Handler();
		return true;
	}

	/**
	 * 扫描蓝牙设备
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		LogUtil.i(TAG, "scanLeDevice " + enable);
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					LogUtil.i(TAG, " stop scanLeDevice ");
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			String deviceNumber = SettingsManager.getInstance()
					.getDeviceNumber();
			if (deviceNumber.equals(device.getAddress())) {
				LogUtil.i(TAG, "扫描到指定蓝牙" + deviceNumber + " 尝试连接");
				connect();
			}
		}
	};

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect() {
		String deviceNumber = SettingsManager.getInstance().getDeviceNumber();
		if (deviceNumber.trim().isEmpty()) {
			LogUtil.i(TAG, "无设备可连接");
			return false;
		}
		if (mBluetoothAdapter == null) {
			return false;
		}
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(deviceNumber);
		if (device == null) {
			return false;
		}
		mConnectionState = STATE_CONNECTING;
		DataAliveTime = System.currentTimeMillis();
		mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
		return true;
	}

	private void reconnect() {
		LogUtil.i(TAG, "正在重连" + SettingsManager.getInstance().getDeviceNumber());
		mConnectionState = STATE_CONNECTING;
		broadcastUpdate(ACTION_GATT_RECONNECTING);
		DataAliveTime = System.currentTimeMillis();
		// if (mBluetoothGatt != null) {
		// System.gc();
		// mBluetoothGatt.close();
		// mBluetoothGatt.connect();
		// } else {
		close();
		connect();
		// }
		// close();
		// scanLeDevice(true);
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		// bleStateEnable = false;
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
		mBluetoothGatt.close();
		mBluetoothGatt = null;
		mConnectionState = STATE_DISCONNECTED;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LogUtil.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LogUtil.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		// This is specific to Heart Rate Measurement.
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;
		return mBluetoothGatt.getServices();
	}

	private void clearGattServices() {
		serviceUUIDList.clear();
		gattServiceData.clear();
		mGattCharacteristics.clear();
		gattCharacteristicData.clear();
	}

	private void displayGattServices() {
		clearGattServices();
		if (mBluetoothGatt == null)
			return;
		List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
		String uuid = null;
		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			serviceUUIDList.add(uuid);
			currentServiceData
					.put(LIST_NAME, SampleGattAttributes.lookup(uuid));
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);
			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME,
						SampleGattAttributes.lookup(uuid));
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}
		BluetoothGattCharacteristic characteristic = getCharacteristicByUUID(ConstantConfig.BLE_UUID_READ);
		if (characteristic != null) {
			LogUtil.i(TAG, "[" + ConstantConfig.BLE_UUID_READ + "]"
					+ "唤醒蓝牙数据，准备读取数据");
			setCharacteristicNotification(characteristic, true);
		}
	}

	public BluetoothGattCharacteristic getCharacteristicByUUID(String strUUID) {
		for (int i = 0; i < mGattCharacteristics.size(); i++) {
			ArrayList<BluetoothGattCharacteristic> gatCharList = mGattCharacteristics
					.get(i);
			for (int j = 0; j < gatCharList.size(); j++) {
				BluetoothGattCharacteristic characteristic = gatCharList.get(j);
				if (characteristic.getUuid().toString().equals(strUUID)) {
					return characteristic;
				}
			}
		}
		LogUtil.e(TAG, new Exception("Can not find characteristic by UUID ["
				+ strUUID + "]"));
		return null;
	}

	/**
	 * Return all gatt characteristic instances of specified service.
	 * 
	 * @param serviceUUID
	 * @param charaUUID
	 * @return
	 */
	public ArrayList<BluetoothGattCharacteristic> getGattCharacteristicList(
			String serviceUUID) {
		int sUuid = serviceUUIDList.indexOf(serviceUUID);
		if (sUuid >= 0) {
			return mGattCharacteristics.get(sUuid);
		}
		return null;
	}

	/**
	 * 设置蓝牙时间
	 */
	public void createAliveFrame() {
		// 1、取得本地时间：
		Calendar c = Calendar.getInstance();
		// 2、取得时间偏移量：
		int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
		int dstOffset = c.get(java.util.Calendar.DST_OFFSET);
		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		if (setTimeFrame == null) {
			setTimeFrame = new byte[20];
			// 固定的三个字节
			setTimeFrame[0] = (byte) 0x5A;
			setTimeFrame[1] = (byte) 0x5A;
			setTimeFrame[2] = (byte) 0x12;
			// 帧类型
			setTimeFrame[3] = (byte) 0x38;
			// 序列SEQ，默认给0
			setTimeFrame[4] = (byte) 0x00;
		}
		int year = c.get(Calendar.YEAR);
		// 取year的最后两个字节
		byte hYear = (byte) (year >>> 8);
		byte lYear = (byte) year;
		setTimeFrame[5] = hYear;
		setTimeFrame[6] = lYear;

		int month = c.get(Calendar.MONTH) + 1;
		setTimeFrame[7] = (byte) month;
		int day = c.get(Calendar.DATE);
		setTimeFrame[8] = (byte) day;

		int hour = c.get(Calendar.HOUR_OF_DAY);
		setTimeFrame[9] = (byte) hour;
		int minute = c.get(Calendar.MINUTE);
		setTimeFrame[10] = (byte) minute;

		int second = c.get(Calendar.SECOND);
		setTimeFrame[11] = (byte) second;
		// year的前两位年
		setTimeFrame[12] = hYear;

		// year的后两位年
		setTimeFrame[13] = lYear;
		setTimeFrame[14] = (byte) month;

		setTimeFrame[15] = (byte) day;
		setTimeFrame[16] = (byte) hour;

		setTimeFrame[17] = (byte) minute;
		setTimeFrame[18] = (byte) second;

		setTimeFrame[19] = (byte) 0xC0;
	}

	public void writeFrame(BluetoothGattCharacteristic characteristic,
			byte[] frame) {
		if (mBluetoothGatt != null) {
			characteristic.setValue(frame);
			mBluetoothGatt.writeCharacteristic(characteristic);
		}
	}

	/**
	 * 向蓝牙发送控制数据
	 * 
	 * @param frameType
	 */
	public void sendCtrlFrame(int frameType) {
		if (characteristicToWrite == null) {
			characteristicToWrite = getCharacteristicByUUID(ConstantConfig.BLE_UUID_WRITE);
		}
		if (characteristicToWrite != null) {
			if (frameType == SEND_FRAME_TYPE_SETTIME && setTimeFrame != null) {
				writeFrame(characteristicToWrite, setTimeFrame);
			}
		}
	}
}
