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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.langlang.ble.LeBluetoothDeviceConnection;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.LoggerToServer;
import com.langlang.global.UserInfo;
import com.langlang.service.BleConnectionService;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
import com.langlang.utils.UIUtil;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@SuppressLint("NewApi")
public class LeBluetoothDeviceScanActivity extends Activity {

	// private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private SharedPreferences bleaddress;
	private SharedPreferences.Editor bleEditor;
	private static final int REQUEST_ENABLE_BT = 1;
	private final int DOWNLOAD_FINISH = 3;
	// 10秒后停止查找搜索.
	private static final long SCAN_PERIOD = 10000;
	private final int UPLOAD_MAC = 2;
	private String device_name;
	private SharedPreferences peopledatas;
	private String my_userName;

	private ArrayList<BluetoothDevice> m_LeDevices;
	private String m_device_mac;

	private ImageView imageView;
	private AnimationDrawable scanAnimation;
	private TextView state;
	private Button scanButton;
	private Button loginButton;
	
	private ScanResultChecker scanResultChecker;
	
	private Object lockForward = new Object();
	private boolean mIsActivityFinished = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_scan);
		
		getActionBar().setTitle(R.string.title_device);
		setViewId();
		getOnclick();
		System.out.println("my device m_device_mac");
		mappingData();

		m_LeDevices = new ArrayList<BluetoothDevice>();
		
		scanResultChecker = new ScanResultChecker(m_device_mac, m_LeDevices);
		
		mHandler = new Handler();
		bleaddress = getSharedPreferences("bleAddress", MODE_PRIVATE);
		bleEditor = bleaddress.edit();
		
		LoggerToServer.log("开始检查手机是否支持BLE");
		
//		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
//		if (!getPackageManager().hasSystemFeature(
//				PackageManager.FEATURE_BLUETOOTH_LE)) {
//			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
//					.show();
//			finish();
//		}
		
		LoggerToServer.log("开始获取BT Adapter");
		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		if (bluetoothManager == null) {
			UIUtil.setLongToast(LeBluetoothDeviceScanActivity.this, "无法获取蓝牙服务");
			LoggerToServer.log("获取蓝牙服务失败");
			finish();
		}
		
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		// new downloadThread().start();
		
		LoggerToServer.log("成功获取BT Adapter");
		
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		
		mIsActivityFinished = false;
		
//		LoggerToServer.log("开始调用蓝牙接口进行扫描(" + m_device_mac + ")");		
//		scanLeDevice(true);
//		
//		LoggerToServer.log("开始检测蓝牙扫描结果(" + m_device_mac + ")");
//		scanResultChecker.startCheck();		
	}

	/**
	 * 获取控件Id
	 */
	private void setViewId() {
		imageView = (ImageView) this.findViewById(R.id.scan_anim_imageview);
		state = (TextView) this.findViewById(R.id.scan_state_textview);
		scanButton = (Button) this.findViewById(R.id.scan_scan_button);
		loginButton = (Button) this.findViewById(R.id.scan_login_button);
		imageView.setBackgroundResource(R.anim.anim_sacn);
		scanAnimation = (AnimationDrawable) imageView.getBackground();
	}

	/**
	 * 获取控件点击事件
	 */
	private void getOnclick() {
		scanButton.setOnClickListener(listener);
		loginButton.setOnClickListener(listener);

	}

	/**
	 * 设置控件点击事件
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.scan_scan_button:
				LoggerToServer.log("开始调用蓝牙接口进行扫描(通过点击按钮)(" + m_device_mac + ")");				
				scanLeDevice(true);
				LoggerToServer.log("开始检测蓝牙扫描结果(通过点击按钮)(" + m_device_mac + ")");
				scanResultChecker.startCheck();
				
				scanButton.setVisibility(View.GONE);
				loginButton.setVisibility(View.GONE);
				break;
			case R.id.scan_login_button:
				synchronized (lockForward) {
					if (!mIsActivityFinished) {
						mIsActivityFinished = true;
						scanResultChecker.stopCheck();
						
						startActivity(new Intent(LeBluetoothDeviceScanActivity.this,
								MainActivity.class));
						LeBluetoothDeviceScanActivity.this.finish();
					}
				}
				break;
			default:
				break;
			}
		}
	};

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
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
//		if (!mBluetoothAdapter.isEnabled()) {
//			if (!mBluetoothAdapter.isEnabled()) {
//				Intent enableBtIntent = new Intent(
//						BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//			}
//		}
//		
//		LoggerToServer.log("开始调用蓝牙接口进行扫描(" + m_device_mac + ")");		
//		scanLeDevice(true);
//		
//		LoggerToServer.log("开始检测蓝牙扫描结果(" + m_device_mac + ")");
//		scanResultChecker.startCheck();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		
		scanLeDevice(true);
		scanResultChecker.startCheck();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		unregisterReceiver(mGattUpdateReceiver);
		
		scanLeDevice(false);
		scanResultChecker.stopCheck();
		scanAnimation.stop();
	}

	volatile boolean stopCheckConnection = false;

	synchronized void setStopCheckConnection(boolean stop) {
		stopCheckConnection = stop;
	}

	synchronized boolean getStopCheckConnection() {
		return stopCheckConnection;
	}

	Thread thread = null;

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					scanAnimation.stop();
					LoggerToServer.log("超时停止扫描" + m_device_mac);
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					LoggerToServer.log("无法扫描到设备" + m_device_mac);
					invalidateOptionsMenu();
					state.setText("无匹配的连接设备");
					scanButton.setVisibility(View.VISIBLE);
					loginButton.setVisibility(View.VISIBLE);
//					if (thread != null) {
//						setStopCheckConnection(true);
//					}
					scanResultChecker.stopCheck();
					
					System.out.println("my device mac:" + m_LeDevices.size());
					
					sendIntentToGetBleState();
				}
			}, SCAN_PERIOD);
			
			scanAnimation.start();
			state.setText("正在扫描......");
			
			scanButton.setVisibility(View.GONE);
			loginButton.setVisibility(View.GONE);
			
			mScanning = true;
			
			LoggerToServer.log("开始调用scanLeDevice(" + m_device_mac + ")");
			boolean startResult = mBluetoothAdapter.startLeScan(mLeScanCallback);
			LoggerToServer.log("调用scanLeDevice返回(" + m_device_mac + ")(" + startResult + ")");

//			thread = new Thread() {
//				private boolean stop = false;
//
//				public void run() {
//					setStopCheckConnection(false);
//
//					try {
//						Thread.sleep(1500);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					boolean deviceFound = false;
//
//					// System.out.println("dddddddddddddssaads222:"+m_LeDevices.size());
//
//					stop = getStopCheckConnection();
//					while (!stop) {
//						System.out.println("action LeBluetoothDeviceScanActivity running while...");
//						// System.out.println("dddddddddddddssaads2223333");
//						deviceFound = false;
//
//						ArrayList<BluetoothDevice> devicesScaned = new ArrayList<BluetoothDevice>();
//						devicesScaned.clear();
//						synchronized (m_LeDevices) {
//							for (BluetoothDevice device : m_LeDevices) {
//								devicesScaned.add(device);
//								// System.out.println("dddddddddddddssaad:"+device.getAddress());
//							}
//							// System.out.println("dddddddddddddssaads:"+m_LeDevices.size());
//						}
//
//						for (BluetoothDevice device : devicesScaned) {
////							System.out.println("my userinfo names:"
////									+ m_device_mac);
//							if (m_device_mac == null) {
//								return;
//							}
//							if (m_device_mac.equals(device.getAddress()
//									.replace(":", ""))) {
//								deviceFound = true;
//								stop = true;
//								break;
//							}
//						}
//
//						devicesScaned.clear();
//						devicesScaned = null;
//
//						if (deviceFound) {
//							stop = true;
//						} else {
//							stop = getStopCheckConnection();
//						}
//					}
//
//					if (deviceFound) {
//						// Message message=Message.obtain();
//						// message.what=UPLOAD_MAC;
//						// handler.sendMessage(message);
//						new uploadMacThread().start();
//					}
//				};
//			};
//
//			thread.start();

		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					synchronized (m_LeDevices) {
						m_LeDevices.add(device);
					}

				}
			});
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			scanResultChecker.stopCheck();
//			startActivity(new Intent(LeBluetoothDeviceScanActivity.this,
//					MainActivity.class));
//			LeBluetoothDeviceScanActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private final int ELSE_LOGIN=4;
	class uploadMacThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String tuminfo = "[{username:\"" + my_userName
					+ "\",equipmentNumber:\"" + m_device_mac + "\"}]";
			System.out.println("my userinfo name:" + my_userName + " "
					+ m_device_mac);
			if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
				UIUtil.setMessage(handler, ELSE_LOGIN);
			}else{
			Message message = Message.obtain();
			message.what = UPLOAD_MAC;
			message.obj = Client.getuploadMAC(tuminfo);
			handler.sendMessage(message);}

		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPLOAD_MAC) {
				// -----------------------------------------------------------------------
				// state.setText("正在连接......");
				// final Intent intent = new Intent(
				// SampleLeBluetoothDeviceScanActivity.this,
				// MainsActivity.class);
				// intent.putExtra(
				// LeBluetoothDeviceConnection.EXTRAS_DEVICE_NAME,
				// device_name);
				// intent.putExtra(
				// LeBluetoothDeviceConnection.EXTRAS_DEVICE_ADDRESS,
				// m_device_mac);
				// System.out.println("设备地址：" + m_device_mac);
				// if (mScanning) {
				// mBluetoothAdapter.stopLeScan(mLeScanCallback);
				// mScanning = false;
				// }
				// startActivity(intent);
				// SampleLeBluetoothDeviceScanActivity.this.finish();
				// -----------------------------------------------------------------------
				// --------------------------------------dsa------------------------------------------
				if (msg.obj.equals("1")) {
//					state.setText("正在连接......");
					state.setText("");
					
					LoggerToServer.log("已经成功扫描到设备" + m_device_mac);
					
					sendIntentToClearStorage();
					
					final Intent intent = new Intent(
							LeBluetoothDeviceScanActivity.this,
							// MainsActivity.class);
							MainActivity.class);
					intent.putExtra(
							LeBluetoothDeviceConnection.EXTRAS_DEVICE_NAME,
							device_name);
					intent.putExtra(
							LeBluetoothDeviceConnection.EXTRAS_DEVICE_ADDRESS,
							m_device_mac);
					intent.putExtra("EXTRA_FROM_ACTIVITY", "ScanActivity");
					
					System.out.println("设备地址：" + m_device_mac);
					if (mScanning) {
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						mScanning = false;
					}
					Client.service_state = 2;
//					Toast.makeText(LeBluetoothDeviceScanActivity.this, "连接成功",
//							Toast.LENGTH_SHORT).show();
					synchronized (lockForward) {
						if (!mIsActivityFinished) {
							mIsActivityFinished = true;
							
							mScanning = false;
							scanAnimation.stop();
							mBluetoothAdapter.stopLeScan(mLeScanCallback);
							scanResultChecker.stopCheck();
							
							startActivity(intent);
							LeBluetoothDeviceScanActivity.this.finish();
						}
					}
				} else {
					Toast.makeText(LeBluetoothDeviceScanActivity.this,
							"用户名与设备号不符", Toast.LENGTH_SHORT).show();
				}
				// --------------------------------------dsa------------------------------------------
			}
			if(msg.what==ELSE_LOGIN){
				final Intent intent = new Intent(
						LeBluetoothDeviceScanActivity.this,
						// MainsActivity.class);
						MainActivity.class);
				intent.putExtra(
						LeBluetoothDeviceConnection.EXTRAS_DEVICE_NAME,
						device_name);
				intent.putExtra(
						LeBluetoothDeviceConnection.EXTRAS_DEVICE_ADDRESS,
						m_device_mac);
				System.out.println("设备地址：" + m_device_mac);
				if (mScanning) {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mScanning = false;
				}
				
				synchronized (lockForward) {
					if (!mIsActivityFinished) {
						mIsActivityFinished = true;
						
						mScanning = false;
						scanAnimation.stop();
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						scanResultChecker.stopCheck();
						
						startActivity(intent);
						LeBluetoothDeviceScanActivity.this.finish();
					}
				}
			}
			if (msg.what == DOWNLOAD_FINISH) {
				sportsp = LeBluetoothDeviceScanActivity.this
						.getSharedPreferences("sportdata", MODE_PRIVATE);
				SharedPreferences.Editor sportspEditor = sportsp.edit();
				sportspEditor.putString("tempdportdata", tempSport);
				sportspEditor.putString("accpdportdata", accSport);
				sportspEditor.commit();

				sp_attention = getSharedPreferences("attention", MODE_PRIVATE);
				SharedPreferences.Editor attention_editor = sp_attention.edit();
				attention_editor.putString("attentions", get_attention);
				attention_editor.commit();

				sp_notify = getSharedPreferences("sp_notify", MODE_PRIVATE);
				SharedPreferences.Editor notify_editor = sp_notify.edit();
				notify_editor.putString("get_systemNotifity",
						get_systemNotifity);
				notify_editor.commit();
				System.out.println("------------------------tempSport:"
						+ tempSport);
				System.out.println("------------------------accSport:"
						+ accSport);
				System.out.println("------------------------get_attention:"
						+ get_attention);
				System.out
						.println("------------------------get_systemNotifity:"
								+ get_systemNotifity);
			}
		};
	};
	
	private void sendIntentToClearStorage() {
		Intent intent = new Intent(DataStorageService.ACTION_CLEAR_STORAGE);
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		sendBroadcast(intent);
	}

	/**
	 * 映射数据
	 */
	private void mappingData() {
		m_device_mac = UserInfo.getIntance().getUserData().getDevice_number();
		my_userName = UserInfo.getIntance().getUserData().getUser_name();
		System.out.println("my device m_device_mac:" + m_device_mac);
	}

	private String accSport;
	private String tempSport;
	private String get_attention;// 关注
	private String get_systemNotifity;
	private SharedPreferences sportsp;
	private SharedPreferences sp_attention;
	private SharedPreferences sp_notify;

	class downloadThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String usernames = "[{username:\"" + my_userName + "\"}]";
			accSport = Client.getUserExercise(usernames);
			tempSport = Client.getUserCalorie(usernames);
			get_attention = Client.getMessage(usernames);
			get_systemNotifity = Client.getaccountNotice(usernames);
			Message message = Message.obtain();
			message.what = DOWNLOAD_FINISH;
			handler.sendMessage(message);
		}
	}
	
	public class ScanResultChecker {
		private Object lock = new Object();
		private final String mac;
		private final ArrayList<BluetoothDevice> leDevices;
		
		private boolean isStarted = false;
		
		private boolean isThreadStarted = false;
		private boolean isThreadFinished = true;
		
		private CheckerThread checkerThread = null;
		
		public ScanResultChecker(String mac, final ArrayList<BluetoothDevice> leDevices) {
			this.mac = mac;
			this.leDevices = leDevices;
			
			isStarted = false;
		}

		public synchronized void notifyFinished() {
			synchronized(lock) {
				isThreadFinished = true;
				isThreadStarted = false;
				isStarted = false;
				checkerThread = null;
			}		
		}
		
		public synchronized void notifyStarted() {
			synchronized(lock) {
				isThreadStarted = true;
				isThreadFinished = false;
			}		
		}
		
		public synchronized void startCheck() {
			System.out.println("action LeBluetoothDeviceScanActivity ScanResultChecker startCheck.");
			if (mac == null || mac.length() <= 0) {
				return;
			}
			if (leDevices == null) { return; }
			
			synchronized(lock) {
				if (isStarted) return;
				isStarted = true;
			}
			
			System.out.println("action LeBluetoothDeviceScanActivity ScanResultChecker startCheck new Thread");
			checkerThread 
						= new CheckerThread(mac, leDevices, this);
			checkerThread.start();
		}
		
		public synchronized void stopCheck() {
			System.out.println("action LeBluetoothDeviceScanActivity ScanResultChecker startCheck.");
			synchronized(lock) {
				if ((checkerThread != null) && (!isThreadFinished)) {
					checkerThread.stopThread();
				}
			}
		}
	}
	
	private class CheckerThread extends Thread {
		private Object lock = new Object();
		private volatile boolean stop = false;
		
		private final String mac;
		private final ArrayList<BluetoothDevice> leDevices;
		private final ScanResultChecker context;
		
		public synchronized void stopThread() {
			stop = true;
		}
		
		public CheckerThread(final String mac, final ArrayList<BluetoothDevice> list, 
										final ScanResultChecker context) {
			this.mac = mac;
			this.leDevices = list;
			this.context = context;
			stop = false;
		}
		
		public void run() {
			System.out.println("action LeBluetoothDeviceScanActivity CheckerThread run...");
			context.notifyStarted();                                                                                                                     
			
			LoggerToServer.log("run开始");
			
			boolean deviceFound = false;
			while (!stop) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			

				ArrayList<BluetoothDevice> devicesScaned = new ArrayList<BluetoothDevice>();
				devicesScaned.clear();
				
				synchronized (leDevices) {
					for (BluetoothDevice device : leDevices) {
						devicesScaned.add(device);
					}
				}

				for (BluetoothDevice device : devicesScaned) {
					if (mac == null) {
						return;
					}
					if (mac.equals(device.getAddress().replace(":", ""))) {
						deviceFound = true;
						stop = true;
						break;
					}
				}

				devicesScaned.clear();
				devicesScaned = null;

				if (deviceFound) {
					LoggerToServer.log("deviceFound=true");
					stop = true;
				}
			}
			
			if (deviceFound) {
				if (UserInfo.getIntance().getLoginMode() == UserInfo.LOGIN_MODE_ONLINE) {
					new uploadMacThread().start();
				}
				else {
					UIUtil.setMessage(handler, UPLOAD_MAC, "1");
				}
			}
			
			context.notifyFinished();
			
			LoggerToServer.log("run结束");
		}
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BleConnectionService.ACTION_RESPONSE_BLE_STATE);
		return intentFilter;
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {		
				forwardToMain();
			} 
			else if (BleConnectionService.ACTION_RESPONSE_BLE_STATE.equals(action)) {
				int bleState = intent.getIntExtra(BleConnectionService.BLE_STATE, 
												  BleConnectionService.STATE_UNKOWN
												  );
				if (bleState == BleConnectionService.STATE_CONNECTED) {
					forwardToMain();
				}
			}
		}
	};
	
	private void forwardToMain() {
		synchronized (lockForward) {
			if (!mIsActivityFinished) {
				mIsActivityFinished = true;
				
				mScanning = false;
				scanAnimation.stop();
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				scanResultChecker.stopCheck();
				
				startActivity(new Intent(LeBluetoothDeviceScanActivity.this,
						MainActivity.class));
				LeBluetoothDeviceScanActivity.this.finish();
			}
		}
	}
	
	private void sendIntentToGetBleState() {
		Intent intent = new Intent(BleConnectionService.ACTION_GET_BLE_STATE);
		sendBroadcast(intent);
	}
}