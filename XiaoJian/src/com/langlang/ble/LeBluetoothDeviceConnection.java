package com.langlang.ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.broadchance.xiaojian.service.BluetoothLeLanglangService;

/**
 * This class wraps functions of bluetooth device connection and read/write.
 * 
 * @author Henry
 * 
 */
@SuppressLint("NewApi")
public class LeBluetoothDeviceConnection {
	private String TAG = LeBluetoothDeviceConnection.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private String mDeviceName;
	private String mDeviceAddress;
	private BluetoothLeLanglangService mBluetoothLeService;
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	// Gatt service intent.
	private Intent gattServiceIntent;

	private Activity activity;

	// a receiver to receive message from bluetooth device.
	private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			if (BluetoothLeLanglangService.ACTION_GATT_CONNECTED.equals(action)) {
		
				mConnected = true;
				onGattConnected(context, intent);
				System.out.println("action ssssssmainsactivity disconnectide oldaddress CONNECTED received.");
			} else if (BluetoothLeLanglangService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				
				mConnected = false;
				onGattDisconnected(context, intent);
				System.out.println("action ssssssmainsactivity disconnectide oldaddress DISCONNECTED received.");
			} else if (BluetoothLeLanglangService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {

				onGattServicesDiscovered(context, intent);
				System.out.println("action ssssssmainsactivity disconnectide oldaddress DISCOVERED received.");
			} else if (BluetoothLeLanglangService.ACTION_DATA_AVAILABLE.equals(action)) {
				onGattDataRecevied(context, intent);
			}
		}
	};

	/**
	 * Calls when gatt bluetooth device is connected.
	 * 
	 * @param context
	 * @param intent
	 */
	public void onGattConnected(Context context, Intent intent) {
		// Do nothing, caller should override this method to add customized
		// logic.
	}

	/**
	 * Calls when gatt bluetooth device is disconnected.
	 * 
	 * @param context
	 * @param intent
	 */
	public void onGattDisconnected(Context context, Intent intent) {
		// Do nothing, caller should override this method to add customized
		// logic.
	}

	/**
	 * Calls when gatt bluetooth services are discovered.
	 * 
	 * @param context
	 * @param intent
	 */
	public void onGattServicesDiscovered(Context context, Intent intent) {
	}

	/**
	 * Calls when receiving data from gatt device.
	 * 
	 * @param context
	 * @param intent
	 */
	public void onGattDataRecevied(Context context, Intent intent) {
		// Do nothing, caller should override this method to add customized
		// logic.
	}

	// Code to manage Service lifecycle.
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeLanglangService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				activity.finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			System.out.println("action: LeBluetoothDeviceConnection connect()");
			connect();
		}
		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.i(TAG, "Bluetooth service is disconnected");
			// mBluetoothLeService = null;
		}
	};
	/**
	 * Constructor of this class.
	 * 
	 * @param activity
	 */
	public LeBluetoothDeviceConnection(Activity activity) {
		this.activity = activity;
		TAG = activity.getClass().getSimpleName();
	}
	public LeBluetoothDeviceConnection() {
	}

	/**
	 * Set service connection.
	 * 
	 * @param sConneciton
	 */
	public void setServiceConnection(ServiceConnection sConneciton) {
		mServiceConnection = sConneciton;
	}

	/**
	 * get service connection.
	 * 
	 * @return
	 */
	protected ServiceConnection getServiceConnection() {
		return mServiceConnection;
	}

	/**
	 * Set a receiver to monitor update data.
	 * 
	 * @param receiver
	 */
	public void setGattUpdateReceiver(BroadcastReceiver receiver) {
		mGattUpdateReceiver = receiver;
	}

	/**
	 * Return gatt update receiver.
	 * 
	 * @return
	 */
	protected BroadcastReceiver getGattUpdateRecevier() {
		return mGattUpdateReceiver;
	}

	/**
	 * Write value to characteristic.
	 * @param characteristic
	 * @param notified
	 */
	public void writeCharacteristic(BluetoothGattCharacteristic characteristic,
			boolean notified) {
		final int charaProp = characteristic.getProperties();
		Log.i(TAG, "charaProp = " + charaProp + ",UUID = "
				+ characteristic.getUuid().toString());
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {

			if (characteristic.getUuid().toString()
					.equals("0000fff6-0000-1000-8000-00805f9b34fb")
					|| characteristic.getUuid().toString()
							.equals("0000fff4-0000-1000-8000-00805f9b34fb")) {
				System.out.println("enable notification");
				mNotifyCharacteristic = characteristic;
				mBluetoothLeService.setCharacteristicNotification(
						characteristic, notified);
			}
		}

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
			mNotifyCharacteristic = characteristic;
			mBluetoothLeService.setCharacteristicNotification(characteristic,
					true);
		}
	}

	/**
	 * Write value to characteristic.
	 * 
	 * @param servicePosition
	 * @param charaPosition
	 * @param value
	 * @param notified
	 */
	public void writeCharacteristic(int servicePosition, int charaPosition,
			String value, boolean notified) {
		final BluetoothGattCharacteristic characteristic = mBluetoothLeService
				.getGattServicesInfo(false, null).getGattCharacteristic(
						servicePosition, charaPosition);
		characteristic.setValue(value.getBytes());
		writeCharacteristic(characteristic, notified);
	}

	/**
	 * Read characteristic from bluetooth device, the register receiver will
	 * receive update data from bluetooth device.
	 * 
	 * @param characteristic
	 * @param notified
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic,
			boolean notified) {
		final int charaProp = characteristic.getProperties();
		Log.i(TAG, "charaProp = " + charaProp + ",UUID = "
				+ characteristic.getUuid().toString());
		if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
			// If there is an active notification on a
			// characteristic, clear
			// it first so it doesn't update the data field on the
			// user interface.
			if (mNotifyCharacteristic != null) {
				mBluetoothLeService.setCharacteristicNotification(
						mNotifyCharacteristic, false);
				mNotifyCharacteristic = null;
			}
			mBluetoothLeService.readCharacteristic(characteristic);
		}

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
			mNotifyCharacteristic = characteristic;
			mBluetoothLeService.setCharacteristicNotification(characteristic,
					true);
		}
	}

	/**
	 * Read characteristic from bluetooth device, the register receiver will
	 * receive update data from bluetooth device.
	 * @param servicePosition
	 * @param charaPosition
	 * @param notified
	 */
	public void readCharacteristic(int servicePosition, int charaPosition,
			boolean notified) {
		final BluetoothGattCharacteristic characteristic = mBluetoothLeService
				.getGattServicesInfo(false, null).getGattCharacteristic(
						servicePosition, charaPosition);
		readCharacteristic(characteristic, notified);
	}

	public void createConnection() {
		final Intent intent = activity.getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		
		System.out.println("action createConnection mDeviceAddress: "
				+ mDeviceAddress);

		// Try to start gatt service under independent mode.
		gattServiceIntent = new Intent(activity, BluetoothLeLanglangService.class);
		activity.startService(gattServiceIntent);
		System.out.println("action:stateservice");
		boolean bll = activity.bindService(gattServiceIntent,
				getServiceConnection(), Context.BIND_AUTO_CREATE);
		if (bll) {
			Log.i(TAG, "--- Binding service is success.");
		} else {
			Log.i(TAG, "--- Binding service is failed.");
		}
	}
	
	public void resume() {
		activity.registerReceiver(mGattUpdateReceiver,
				makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	public void pause() {
		if (getGattUpdateRecevier() != null) {
			activity.unregisterReceiver(mGattUpdateReceiver);
		}
	}

	public void destroy() {
		activity.unbindService(getServiceConnection()); // First unbind service
		activity.stopService(gattServiceIntent); // Then stop the service
		
		disconnect();
		mBluetoothLeService = null;
	}

	/**
	 * Connect to current device.
	 * 
	 * @return
	 */
	public boolean connect() {
		System.out.println("action: LeBluetoothDeviceConnection connect() MAC:"
				+ mDeviceAddress);
		return mBluetoothLeService.connect(mDeviceAddress);
	}

	/**
	 * Disconnect with current device.
	 */
	public void disconnect() {
		if(mBluetoothLeService!=null){
			mBluetoothLeService.disconnect();
		}
	}

	/**
	 * Return device name.
	 * 
	 * @return
	 */
	public String getDeviceName() {
		return mDeviceName;
	}

	/**
	 * Return device address.
	 * 
	 * @return
	 */
	public String getDeviceAddress() {
		return mDeviceAddress;
	}

	/**
	 * Check if device is connected.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return mConnected;
	}

	/**
	 * Return current gatt service intent object.
	 * 
	 * @return
	 */
	public Intent getGattServiceIntent() {
		return gattServiceIntent;
	}

	/**
	 * Stop current gatt service
	 */
	public void stopDeviceService() {
		activity.unbindService(getServiceConnection());
		mBluetoothLeService.stopService(gattServiceIntent);
	}

	/**
	 * Set default gatt updata intent filters.
	 * 
	 * @return
	 */
	protected IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeLanglangService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeLanglangService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeLanglangService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeLanglangService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/**
	 * Return instance of bluetooth le service.
	 * 
	 * @return
	 */
	public BluetoothLeLanglangService getBluetoothLeService() {
		return mBluetoothLeService;
	}

	public void registerGattUpdateReceiver() {
		activity.registerReceiver(mGattUpdateReceiver,
				makeGattUpdateIntentFilter());
		/****
		 * if (mBluetoothLeService != null) { final boolean result =
		 * mBluetoothLeService.connect(mDeviceAddress); Log.d(TAG,
		 * "Connect request result=" + result); }
		 ****/
	}
	
//	public BluetoothAdapter getBluetoothAdapter(){
//		return mBluetoothLeService.getBluetoothAdapter().startLeScan(callback);
//	}
}
