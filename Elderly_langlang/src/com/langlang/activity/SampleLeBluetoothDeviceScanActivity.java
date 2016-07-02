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

package com.langlang.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.UserInfo;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@SuppressLint("NewApi")
public class SampleLeBluetoothDeviceScanActivity extends ListActivity {

	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private SharedPreferences bleaddress;
	private SharedPreferences.Editor bleEditor;
	private static final int REQUEST_ENABLE_BT = 1;
	// 10秒后停止查找搜索.
	private static final long SCAN_PERIOD = 10000;
	private final int UPLOAD_MAC = 2;
	private String device_mac;
	private String device_name;
	private SharedPreferences peopledatas;
	private String mName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(R.string.title_device);
		mappingData();
		mHandler = new Handler();
		bleaddress = getSharedPreferences("bleAddress", MODE_PRIVATE);
		bleEditor = bleaddress.edit();
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (!mScanning) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.menu_stop).setVisible(true);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(
					R.layout.ble_actionbar_indeterminate_progress);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_scan:
			mLeDeviceListAdapter.clear();
			scanLeDevice(true);
			break;
		case R.id.menu_stop:
			scanLeDevice(false);
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		setListAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		if (device == null)
			return;
		device_mac = device.getAddress();
		device_name = device.getAddress();
		device_name=device_name.replace(":","");
		UserInfo.getIntance().getUserData().setDevice_number(device_name);
		bleEditor.putString("deviceAddress", device.getAddress());
		bleEditor.commit();
		System.out.println("samplelebluetoothdeviceactivity mname:"
				+ mName);
		
//		if (mName.substring(0, 4).equals("test")) {
			System.out.println("samplelebluetoothdeviceactivity mname test:"
					+ mName);
			final Intent intent = new Intent(
					SampleLeBluetoothDeviceScanActivity.this,
					MainActivity.class);
//			intent.putExtra(LeBluetoothDeviceConnection.EXTRAS_DEVICE_NAME,
//					device.getName());
//			intent.putExtra(LeBluetoothDeviceConnection.EXTRAS_DEVICE_ADDRESS,
//					device.getAddress());
					
		SharedPreferences peopledatas = this.getSharedPreferences("peopleinfo", MODE_PRIVATE);
		SharedPreferences.Editor editor = peopledatas.edit();
		editor.putString("MYMAC", device.getAddress());
		editor.commit();
		
		System.out.println("action SampleLeBluetooth MYMAC:" + device.getAddress());
		
			System.out.println("设备地址：" + device.getAddress());
			if (mScanning) {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mScanning = false;
			}
			Client.service_state = 2;
			startActivity(intent);
			SampleLeBluetoothDeviceScanActivity.this.finish();
//		}else{
//			new uploadMacThread().start();
//		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = SampleLeBluetoothDeviceScanActivity.this
					.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.ble_listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			return view;
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(SampleLeBluetoothDeviceScanActivity.this,
					MainActivity.class));
			SampleLeBluetoothDeviceScanActivity.this.finish();
			Client.service_state = 1;
		}
		return super.onKeyDown(keyCode, event);
	}

	class uploadMacThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String tuminfo = "[{username:\"" + mName + "\",equipmentNumber:\""
					+ device_mac.replace(":", "") + "\"}]";

			Message message = Message.obtain();
			message.what = UPLOAD_MAC;
			message.obj = Client.getuploadMAC(tuminfo);
			handler.sendMessage(message);

		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPLOAD_MAC) {
				System.out.println("msg.objsssssssssssssssssssssss:" + msg.obj);
				if (msg.obj.equals("1")) {
					final Intent intent = new Intent(
							SampleLeBluetoothDeviceScanActivity.this,
							MainActivity.class);
//					intent.putExtra(
//							LeBluetoothDeviceConnection.EXTRAS_DEVICE_NAME,
//							device_name);
//					intent.putExtra(
//							LeBluetoothDeviceConnection.EXTRAS_DEVICE_ADDRESS,
//							device_mac);
					System.out.println("设备地址：" + device_mac);
					if (mScanning) {
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						mScanning = false;
					}
					Client.service_state = 2;
					startActivity(intent);
					SampleLeBluetoothDeviceScanActivity.this.finish();
				} else {
					Toast.makeText(SampleLeBluetoothDeviceScanActivity.this,
							"用户名与设备号不符", Toast.LENGTH_SHORT).show();
				}
			}
		};
	};

	/**
	 * 映射数据
	 */
	private void mappingData() {
		peopledatas = this.getSharedPreferences("peopleinfo", MODE_PRIVATE);
		String info = peopledatas.getString("userinfo", "");
		try {
			JSONArray jsonArray = new JSONArray(info);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			mName = jsonObject.getString("userName");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}