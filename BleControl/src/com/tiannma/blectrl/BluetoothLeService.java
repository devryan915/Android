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

package com.tiannma.blectrl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
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
import android.util.Log;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	private final static int ALIVE_PERIOD = 10 * 1000;
	private final static int CHECK_PERIOD = 3 * 1000;
	private final static int BLE_CON_TIMEOUT = 15 * 1000;

	/**
	 * 由于ble本身对蓝牙断开响应不够及时，所以自定义检查蓝牙是否超时 当蓝牙实际已经超时时，ble本身的状态依然是连接状态，延迟发出断开信号
	 * 而在蓝牙实际断开和ble发出断开信号之间
	 * ，如果关闭蓝牙mBluetoothGatt.disconnect()；mBluetoothGatt.close()；
	 * 此时依然会触发蓝牙连接状态改变，而此时的改变值是连接状态，为了避免此错误信号，定义以下标志位
	 */
	// private boolean bleStateEnable = false;

	private long DataAliveTime;

	public final static String ACTION_GATT_CONNECTED = "com.tiannma.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.tiannma.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.tiannma.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.tiannma.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String ACTION_GATT_RECONNECTING = "com.tiannma.bluetooth.le.ACTION_GATT_RECONNECTING";
	private final static String ACTION_ALARM_SEND_ALIVE_FRAME = "com.tiannma.aliveframe";
	private final static String ACTION_ALARM_SEND_CHECKBLESTATE = "com.tiannma.blestate";

	public final static String EXTRA_DATA = "com.tiannma.bluetooth.le.EXTRA_DATA";

	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
			.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

	public static final String LIST_NAME = "NAME";
	public static final String LIST_UUID = "UUID";

	private static final int SEND_FRAME_TYPE_KEEPALIVE = 1;

	ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
	ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	ArrayList<String> serviceUUIDList = new ArrayList<String>();

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_ALARM_SEND_ALIVE_FRAME.equals(action)) {
				if (mConnectionState == STATE_CONNECTED) {
					sendCtrlFrame(SEND_FRAME_TYPE_KEEPALIVE);
				}
			} else if (BluetoothLeService.ACTION_ALARM_SEND_CHECKBLESTATE
					.equals(action)) {
				if (mConnectionState == STATE_CONNECTING) {
					broadcastUpdate(ACTION_GATT_RECONNECTING);
					return;
				}
				if (System.currentTimeMillis() - DataAliveTime > CHECK_PERIOD) {
					mConnectionState = STATE_DISCONNECTED;
					broadcastUpdate(ACTION_GATT_DISCONNECTED);
					clearGattServices();
					disconnect();
					close();
					connect(mBluetoothDeviceAddress);
					broadcastUpdate(ACTION_GATT_RECONNECTING);
					DataAliveTime = System.currentTimeMillis();
				}
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				DataAliveTime = System.currentTimeMillis();
			} else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {

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
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful
				// connection.
				Log.i(TAG, "Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				displayGattServices();
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				Log.d(TAG, "Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				Log.d(TAG, "Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			Log.d(TAG, String.format("Received heart rate: %d", heartRate));
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			intent.putExtra(EXTRA_DATA, data);
			// if (data != null && data.length > 0) {
			// final StringBuilder stringBuilder = new StringBuilder(
			// data.length);
			// for (byte byteChar : data)
			// stringBuilder.append(String.format("%02X ", byteChar));
			// intent.putExtra(EXTRA_DATA, new String(data) + "\n"
			// + stringBuilder.toString());
			// }
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	private void startAliveFrameTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_SEND_ALIVE_FRAME);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, ALIVE_PERIOD, pi);
	}

	private void cancelAliveFrameTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_SEND_ALIVE_FRAME);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(sender);
	}

	private void startCheckBleStateTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_SEND_CHECKBLESTATE);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.setRepeating(AlarmManager.RTC, triggerAtTime + BLE_CON_TIMEOUT,
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
	public IBinder onBind(Intent intent) {
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		startAliveFrameTimer();
		startCheckBleStateTimer();
		return mBinder;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter
				.addAction(BluetoothLeService.ACTION_ALARM_SEND_ALIVE_FRAME);
		intentFilter
				.addAction(BluetoothLeService.ACTION_ALARM_SEND_CHECKBLESTATE);
		return intentFilter;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// tiannma, close() is
		// invoked when the UI is disconnected from the Service.
		unregisterReceiver(mGattUpdateReceiver);
		cancelAliveFrameTimer();
		cancelCheckBleStateTimer();
		close();
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
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

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
	public boolean connect(final String address) {
		// bleStateEnable = true;
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.此方法效果很差
		// if (mBluetoothDeviceAddress != null
		// && address.equals(mBluetoothDeviceAddress)
		// && mBluetoothGatt != null) {
		// Log.d(TAG,
		// "Trying to use an existing mBluetoothGatt for connection.");
		// if (mBluetoothGatt.connect()) {
		// mConnectionState = STATE_CONNECTING;
		// return true;
		// } else {
		// return false;
		// }
		// }

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
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
			Log.w(TAG, "BluetoothAdapter not initialized");
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
			Log.w(TAG, "BluetoothAdapter not initialized");
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
			Log.w(TAG, "BluetoothAdapter not initialized");
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
		System.out.println("action displayGattServices.");
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
		BluetoothGattCharacteristic characteristic = getCharacteristicByUUID("0000fff4-0000-1000-8000-00805f9b34fb");
		if (characteristic != null) {
			System.out.println("action UUID = ["
					+ characteristic.getUuid().toString() + "]");
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
		System.out.println("Can not find characteristic by UUID [" + strUUID
				+ "]");
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

	public byte[] createAliveFrame() {
		byte[] frame = new byte[20];
		Calendar c = Calendar.getInstance();

		frame[0] = (byte) 0x5A;
		frame[1] = (byte) 0x5A;
		frame[2] = (byte) 0x12;
		frame[3] = (byte) 0x86;
		frame[4] = (byte) 0x00;

		frame[5] = (byte) 0xFF;
		frame[6] = (byte) 0xAA;
		frame[7] = (byte) 0xBB;
		frame[8] = (byte) 0xCC;
		frame[9] = (byte) 0xFF;

		int year = c.get(Calendar.YEAR);
		frame[10] = (byte) (year >>> 8);
		frame[11] = (byte) year;

		int month = c.get(Calendar.MONTH) + 1;
		frame[12] = (byte) month;

		int day = c.get(Calendar.DATE);
		frame[13] = (byte) day;

		int hour = c.get(Calendar.HOUR_OF_DAY);
		frame[14] = (byte) hour;

		int minute = c.get(Calendar.MINUTE);
		frame[15] = (byte) minute;

		int second = c.get(Calendar.SECOND);
		frame[16] = (byte) second;

		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		frame[17] = (byte) dayOfWeek;

		frame[18] = 0x00;

		frame[19] = 0x12;

		return frame;
	}

	public void writeFrame(BluetoothGattCharacteristic characteristic,
			byte[] frame) {
		if (mBluetoothGatt != null) {
			characteristic.setValue(frame);
			mBluetoothGatt.writeCharacteristic(characteristic);
		}
	}

	public void sendCtrlFrame(int frameType) {
		byte[] sendFrame = null;
		if (frameType == SEND_FRAME_TYPE_KEEPALIVE) {
			sendFrame = createAliveFrame();
		}
		final String UUID_TO_WRITE = "0000fff1-0000-1000-8000-00805f9b34fb";
		BluetoothGattCharacteristic characteristicToWrite = getCharacteristicByUUID(UUID_TO_WRITE);
		if (characteristicToWrite != null) {
			if (sendFrame != null) {
				writeFrame(characteristicToWrite, sendFrame);
			}
		}
	}

}
