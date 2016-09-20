package com.broadchance.xiaojian.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
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
import android.view.WindowManager;

import com.langlang.ble.GattServicesInfo;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.utils.EventLogger;
import com.langlang.utils.UIUtil;

public class BleConnectionService extends Service {
//	LeBluetoothDeviceConnection leBluetoothDeviceConnection = new LeBluetoothDeviceConnection();
	private BluetoothLeLanglangService mBluetoothLeService;
	private Intent bleService;
//	private String mDeviceAddress;
//	private SharedPreferences peopledatas;
//	private ReceiveBroadCast receiveBroadCast;
//	String nextLine = System.getProperty("line.separator");
	private String m_device_mac;
	private String index_1;
	private String index_2;
	private String index_3;
	private String index_4;
	private String index_5;
	private String index_6;
//	private Date m_date = null;
//	private Timer mTimer15s = null;
	
	public final static String ACTION_GET_BLE_STATE 
							= "com.langlang.android.bluetooth.le.ACTION_GET_BLE_STATE";
	public final static String ACTION_RESPONSE_BLE_STATE 
							= "com.langlang.android.bluetooth.le.ACTION_RESPONSE_BLE_STATE";
	public final static String BLE_STATE 
							= "com.langlang.android.bluetooth.le.BLE_STATE";
	public final static int STATE_UNKOWN = -1;
	public final static int STATE_DISCONNECTED = 0;
	public final static int STATE_CONNECTING = 1;
	public final static int STATE_CONNECTED = 2;
	public final static int STATE_SCANNING = 3;
	
	private volatile int mBleState = STATE_DISCONNECTED;
	private Object lockBleState = new Object();
	
	private final static  String ACTION_BLE_RECONNECT 
							= "com.langlang.android.bluetooth.le.ACTION_BLE_RECONNECT";
	private final static int BLE_RECONNECT_TIME_SECOND = 15;
	private final static int BLE_JUDGE_DISCONNECT_TIME_SECOND = 15;
	
	private Date mLastDataDate = null;
	private BleConnectionMonitor mBleConnectionMonitor = null;
	
	private boolean isConnectingTimeoutTimerStarted = false;
	private final static String ACTION_ALARM_CONNECTING_TIMEOUT
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_CONNECTING_TIMEOUT";

	private boolean isCheckDataAliveTimerStarted = false;
	private final static String ACTION_ALARM_CHECK_DATA_ALIVE
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_CHECK_DATA_ALIVE";

	private boolean isDelayReconnectTimerStarted = false;
	private final static String ACTION_ALARM_DELAY_RECONNECT
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_DELAY_RECONNECT";
	
	private BleStateMachine mBleStateMachine;
	
	private final static String ACTION_ALARM_SEND_ALIVE_FRAME
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_SEND_ALIVE_FRAME";
	
	private final static String ACTION_ALARM_SEND_RESET_FRAME
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_SEND_RESET_FRAME";
	private boolean mIsSendResetTimerStarted = false;
	
	// ECG Mode
	public final static String ACTION_SEND_SWITCH_MODE_FRAME
							= "com.langlang.android.bluetooth.le.ACTION_SEND_SWITCH_MODE_FRAME";
	
	private final static String ACTION_ALARM_CHECK_CURRENT_ECG_MODE
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_CHECK_CURRENT_ECG_MODE";
	private int mExceptedEcgMode = 0;
	private boolean isCheckCurrentEcgModeTimerStarted = false;
	
	private EcgModeMonitor mEcgModeMonitor = new EcgModeMonitor();
	
	// ECG Policy
	public final static int POLICY_UNKNOWN = 0;
	public final static int POLICY_ECG_ONLY = 1;
	public final static int POLICY_ECG_HEART_RATE = 2;
	public final static int POLICY_MIXED = 3;
	
	public final static int POLICY_SUB_UNKNOWN = 0;
	public final static int POLICY_SUB_POLICY_MIXED_0 = 31;
	public final static int POLICY_SUB_POLICY_MIXED_1 = 32;
	
	public final static int POLICY_CTRL_UNKNOWN = 0;
	public final static int POLICY_CTRL_FROM_CLIENT = 1;
	public final static int POLICY_CTRL_FROM_SERVER = 2;
		
	private final static String ACTION_ALARM_ECG_POLICY_ECG_TIMEOUT
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_ECG_POLICY_ECG_TIMEOUT";
	private final static String ACTION_ALARM_ECG_POLICY_HEART_RATE_TIMEOUT
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_ECG_POLICY_HEART_RATE_TIMEOUT";
	private boolean isEcgPolicyEcgTimeoutTimerStarted = false;
	private boolean isEcgPolicyHeartRateTimeoutTimerStarted = false;
	
	public final static String ACTION_SET_ECG_POLICY
							= "com.langlang.android.bluetooth.le.ACTION_SET_ECG_POLICY";
	private EcgModePolicyManager mEcgModePolicyManager = new EcgModePolicyManager();
	
	public final static String ACTION_SET_ECG_MODE
							= "com.langlang.android.bluetooth.le.ACTION_SET_ECG_MODE";
	public final static String ACTION_UNSET_ECG_MODE
							= "com.langlang.android.bluetooth.le.ACTION_UNSET_ECG_MODE";
	private EcgModeManager mEcgModeManager = null;
	
	private static final int SEND_FRAME_TYPE_NONE = 0;
	private static final int SEND_FRAME_TYPE_KEEPALIVE = 1;
	private static final int SEND_FRAME_TYPE_SWITCH_TO_ECG = 2;
	private static final int SEND_FRAME_TYPE_SWITCH_TO_HEARTRATE = 3;

	private int mPengdingSendFrameType = SEND_FRAME_TYPE_NONE;
	private Date mLastSendDate = null;
	private final static String ACTION_ALARM_DELAY_SEND_FRAME
							= "com.langlang.android.bluetooth.le.ACTION_ALARM_DELAY_SEND_FRAME";
	
	private boolean mToggle = false;
	
//	private void setTimer() {
////		mTimer15s.schedule(new TimerTask() {
////			@Override
////			public void run() {
////				Date now = new Date();
////			
////				if(m_date!=null){
////					System.out.println("蓝牙连接状态 action time:"+now.getTime()+"---"+m_date.getTime()+"--"+(now.getTime() - m_date.getTime()));
////					if (now.getTime() - m_date.getTime() > (1000 * 10)) {
////					
////						sendIntent(BluetoothLeService.ACTION_GATT_DISCONNECTED);
////					}
////				}
////			}
////		}, 0, 1000 * 5);
//		mTimer15s = new Timer();
//		mTimer15s.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				Date now = new Date();
//			
//				if(m_date!=null){
//					System.out.println("action BleConnection setTimer time:" + now.getTime() + "---" 
//										+ m_date.getTime() + "--" + (now.getTime() - m_date.getTime()));
//					if (now.getTime() - m_date.getTime() > (1000 * 5)) {
//						if (getBleState() != STATE_CONNECTED) {
//							sendIntent(BluetoothLeService.ACTION_GATT_DISCONNECTED);
//						}
//					}
//				}
//			}
//		}, 1000 * 5, 1000 * 5);
//	}
//	
//	private void cancelTimer(){
//		if (mTimer15s != null) {
//			mTimer15s.cancel();
//			mTimer15s = null;
//		}
//	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("action BleConnectionService onCreate22");
		super.onCreate();
//		reastOncreat();
		acquireWakeLock();
		// bleAddress=getSharedPreferences("bleAddress",MODE_PRIVATE);
		System.out.println("action BleConnectionService onCreate");
		mappingData();
		this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		
		isCheckCurrentEcgModeTimerStarted = false;
		
		mEcgModeMonitor.reset();
		
		mEcgModeManager = new EcgModeManager();
		
		startBleService();
		
//		mBleConnectionMonitor = new BleConnectionMonitor();
//		mBleConnectionMonitor.start();
		
		isConnectingTimeoutTimerStarted = false;
		isCheckDataAliveTimerStarted = false;
		isDelayReconnectTimerStarted = false;
		mBleStateMachine = new BleStateMachine();
		
		setTimer();
		
		startAliveFrameTimer();
		
		isCheckCurrentEcgModeTimerStarted = false;
		
//		receiveBroadCast = new ReceiveBroadCast();
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction("action");
//		registerReceiver(receiveBroadCast, intentFilter);
//		System.out.println("action bleconnection onCreate");
		
//		setTimer();
	}
	
	private synchronized void setBleState(int state) {
		synchronized (lockBleState) {
			mBleState = state;
			
//			if ((state == BleConnectionService.STATE_CONNECTING)
//			 || (state == BleConnectionService.STATE_SCANNING)) {				
				Intent intent = new Intent(ACTION_RESPONSE_BLE_STATE);
				intent.putExtra(BLE_STATE, mBleState);
				sendBroadcast(intent);
//			}
			
			GlobalStatus.getInstance().setBleState(mBleState);
		}
		
		String stateStr;
		if (state == STATE_CONNECTED) {
			stateStr = "CONNECTED";
		} else if (state == STATE_DISCONNECTED) {
			stateStr = "DISCONNECTED";
		} else if (state == STATE_CONNECTING) {
			stateStr = "CONNECTING";
		} else {
			stateStr = "UNKNOWN_STATE";
		}
		EventLogger.logEvent(stateStr);
	}
	
	private int getBleState() {
		synchronized (lockBleState) {
			return mBleState;
		}
	}

	private void mappingData() {

		m_device_mac = UserInfo.getIntance().getUserData().getDevice_number();
		if (m_device_mac == null || m_device_mac.length() < 12) {
			return;
		}
		System.out.println("action bleconnection onCreate m_device_mac:"
				+ m_device_mac.length());
		index_1 = m_device_mac.substring(0, 2);
		index_2 = m_device_mac.substring(2, 4);
		index_3 = m_device_mac.substring(4, 6);
		index_4 = m_device_mac.substring(6, 8);
		index_5 = m_device_mac.substring(8, 10);
		index_6 = m_device_mac.substring(10, 12);
		System.out.println("my device m_device_macdd:" + index_1 + ":"
				+ index_2 + ":" + index_3 + ":" + index_4 + ":" + index_5 + ":"
				+ index_6);
		m_device_mac = index_1 + ":" + index_2 + ":" + index_3 + ":" + index_4
				+ ":" + index_5 + ":" + index_6;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		System.out
				.println("action data ===========bleconnectionservice onStart ");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// this.unregisterReceiver(mGattUpdateReceiver);
		releaseWarkLock();
		cancelTimer();
//		mBleConnectionMonitor.stopBleConnectionMonitor();
		
		destroy();
		
		cancelAliveFrameTimer();
//		cancelResetFrameTimer();
		
		cancelEcgPolicyEcgTimeoutTimer();
		cancelEcgPolicyHeartRateTimeoutTimer();
		
//		cancelTimer();
		System.out
				.println("action data ===========bleconnectionservice onDestroy ");
	}

	private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("蓝牙连接状态action ： " + action);
			if (BluetoothLeLanglangService.ACTION_GATT_CONNECTED.equals(action)) {
				System.out.println("action BleConnectionService CONNECTED received.");
				
//				m_date = new Date();
	
//				cancelTimer();
//				m_date = null;
				
//				setBleState(STATE_CONNECTED);
				mBleStateMachine.onConnected();
				
//				startResetFrameTimer();
				
			} else if (BluetoothLeLanglangService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				
				System.out.println("action BleConnectionService ACTION_GATT_DISCONNECTED received");
				
				System.out.println("action BleConnectionService call mBleStateMachine.onDisconnected() ACTION_GATT_DISCONNECTED received");
				mBleStateMachine.onDisconnected();
				
//				cancelResetFrameTimer();
				
//				synchronized (lockBleState) {
//					
//					System.out
//					.println("action bleconnection DISCONNECTED received.");
//
////				cancelTimer();
//				setBleState(STATE_DISCONNECTED);
//				
////				BaseActivity.ble_state = 2;
//				
////				Date date = new Date();
//				// String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//				// .format(date);
//				// Intent intentb = new Intent();
//				// intentb.setAction("action");
//				// sendBroadcast(intentb);
//				
//				GlobalStatus.getInstance().reset();
//				
////				m_date = null;
////				destroy();
////				startBleService();
////				registerGattUpdateReceiver();
//				
//				if (mBluetoothLeService != null) {
//					System.out.println("action BleConnectionService destroy try to disconnect BLE");
//					mBluetoothLeService.disconnect();
//					mBluetoothLeService.close();
//				}
//				if (!mBluetoothLeService.initialize()) {
//					showAlert();
//					return;
//				}
//				System.out.println("action bleconnection reconnect when receive ACTION_GATT_DISCONNECTED");
//				connect();
//				setBleState(STATE_CONNECTING);
////				m_date = new Date();
////				setTimer();
//				
//				}
				
//				System.out
//						.println("action bleconnection DISCONNECTED received.");
				
			} else if (BluetoothLeLanglangService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
//				BaseActivity.ble_state = 1;
				displayGattServices();
				System.out.println("action BleConnectionService ACTION_GATT_SERVICES_DISCOVERED received");
			} 
//			else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//				mLastDataDate = new Date();
//			} 
			else if (BluetoothLeLanglangService.ACTION_GATT_DATA_ALIVE.equals(action)) {
				mLastDataDate = new Date();
				System.out.println("action BleConnectionService ACTION_GATT_DATA_ALIVE received");
			}
			else if (ACTION_GET_BLE_STATE.equals(action)) {
				Intent intentResponse = new Intent(ACTION_RESPONSE_BLE_STATE);
				intentResponse.putExtra(BLE_STATE, mBleState);
				sendBroadcast(intentResponse);
				
				System.out.println("action BleConnectionService ACTION_GET_BLE_STATE received");
			}
			else if (ACTION_BLE_RECONNECT.equals(action)) {
				if (mBluetoothLeService != null) {
					mBluetoothLeService.disconnect();
					mBluetoothLeService.close();
				}
				if (!mBluetoothLeService.initialize()) {
					showAlert();
					return;
				}
				System.out.println("action bleconnection try to reconnect");
				connect();
				setBleState(STATE_CONNECTING);
//				m_date = new Date();
				
				System.out.println("action BleConnectionService ACTION_BLE_RECONNECT received");
			}
			else if (ACTION_ALARM_CONNECTING_TIMEOUT.equals(action)) {
				mBleStateMachine.onConnectingTimeout();
				
				System.out.println("action BleConnectionService ACTION_ALARM_CONNECTING_TIMEOUT received");
			}
			else if (ACTION_ALARM_DELAY_RECONNECT.equals(action)) {
				mBleStateMachine.onDelayReconnect();
				
				System.out.println("action BleConnectionService ACTION_ALARM_DELAY_RECONNECT received");
			}
			else if (ACTION_ALARM_CHECK_DATA_ALIVE.equals(action)) {
				if (mLastDataDate == null) {
					System.out.println("action BleConnectionService call onDisconnected mLastDataDate null ACTION_ALARM_CHECK_DATA_ALIVE received");
					mBleStateMachine.onDisconnected();
				} else {
					Date now = new Date();
					if (now.getTime() - mLastDataDate.getTime() > (10 * 1000)) {
						System.out.println("action BleConnectionService call onDisconnected data timeout ACTION_ALARM_CHECK_DATA_ALIVE received");
						mBleStateMachine.onDisconnected();
					}
				}
				
				System.out.println("action BleConnectionService ACTION_ALARM_CHECK_DATA_ALIVE received");
			}
			else if (ACTION_ALARM_SEND_ALIVE_FRAME.equals(action)) {
				System.out.println("action ACTION_ALARM_SEND_ALIVE_FRAME");
				sendAliveFrame();
			}
			else if (ACTION_ALARM_SEND_RESET_FRAME.equals(action)) {
				System.out.println("action ACTION_ALARM_SEND_RESET_FRAME");
				sendResetFrame();
			}
			else if (ACTION_SEND_SWITCH_MODE_FRAME.equals(action)) {
//				System.out.println("action ACTION_SEND_SWITCH_MODE_FRAME");
				int mode = intent.getIntExtra("MODE", 0);
				String ratiosStr = intent.getStringExtra("RATIOS");
				int[] ratios = getEcgModeRatios(ratiosStr);
				
				System.out.println("action ACTION_SEND_SWITCH_MODE_FRAME mode " + mode);
				
				switchEcgMode(mode, ratios);
			}
			else if (ACTION_ALARM_CHECK_CURRENT_ECG_MODE.equals(action)) {
				mEcgModeMonitor.ackEcgMode(GlobalStatus.getInstance().getCurrentECGMode());
			}
			else if (ACTION_SET_ECG_POLICY.equals(action)) {
				int policy = intent.getIntExtra("POLICY", 0);
				int subPolicy = intent.getIntExtra("SUB_POLICY", 0);
				
				mEcgModePolicyManager.setPolicy(policy, subPolicy);
			}
			else if (ACTION_ALARM_ECG_POLICY_ECG_TIMEOUT.equals(action)) {
				mEcgModePolicyManager.onEcgModeTimeout();
			}
			else if (ACTION_ALARM_ECG_POLICY_HEART_RATE_TIMEOUT.equals(action)) {
				mEcgModePolicyManager.onHeartRateModeTimeout();
			}
			else if (ACTION_SET_ECG_MODE.equals(action)) {
				int policy = intent.getIntExtra("POLICY", 0);
				int subPolicy = intent.getIntExtra("SUB_POLICY", 0);
				int from = intent.getIntExtra("FROM", 0);
				
				if ((from != POLICY_CTRL_UNKNOWN) && (policy != POLICY_UNKNOWN))
				{
					if (from == POLICY_CTRL_FROM_SERVER) {
						mEcgModeManager.startServerControl(policy, subPolicy);
					}
					else if (from == POLICY_CTRL_FROM_CLIENT) {
						System.out.println("action BleConnectionService POLICY_CTRL_FROM_CLIENT "
										+ policy + "," + subPolicy);
						mEcgModeManager.startClientControl(policy, subPolicy);
					}
				}
			}
			else if (ACTION_UNSET_ECG_MODE.equals(action)) {
				int from = intent.getIntExtra("FROM", 0);
				
				if ((from != POLICY_CTRL_UNKNOWN))
				{
					if (from == POLICY_CTRL_FROM_SERVER) {
						mEcgModeManager.stopServerControl();
					}
					else if (from == POLICY_CTRL_FROM_CLIENT) { 
						mEcgModeManager.stopClientControl();
					}
				}				
			}
			else if (ACTION_ALARM_DELAY_SEND_FRAME.equals(action)) {
				if (mPengdingSendFrameType != SEND_FRAME_TYPE_NONE) {
					xmitFrame(mPengdingSendFrameType);
					mLastSendDate = new Date();
					
					mPengdingSendFrameType = SEND_FRAME_TYPE_NONE;					
				}
			}
		}
	};
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeLanglangService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeLanglangService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeLanglangService.ACTION_GATT_SERVICES_DISCOVERED);
//		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeLanglangService.ACTION_GATT_DATA_ALIVE);
		intentFilter.addAction(ACTION_GET_BLE_STATE);
		intentFilter.addAction(ACTION_ALARM_CONNECTING_TIMEOUT);
		intentFilter.addAction(ACTION_ALARM_DELAY_RECONNECT);
		intentFilter.addAction(ACTION_ALARM_CHECK_DATA_ALIVE);
		
		intentFilter.addAction(ACTION_ALARM_SEND_ALIVE_FRAME);
//		intentFilter.addAction(ACTION_ALARM_SEND_RESET_FRAME);
		
		intentFilter.addAction(ACTION_SEND_SWITCH_MODE_FRAME);
		
		intentFilter.addAction(ACTION_ALARM_CHECK_CURRENT_ECG_MODE);
		
		intentFilter.addAction(ACTION_SET_ECG_POLICY);
		intentFilter.addAction(ACTION_ALARM_ECG_POLICY_ECG_TIMEOUT);
		intentFilter.addAction(ACTION_ALARM_ECG_POLICY_HEART_RATE_TIMEOUT);
		
		intentFilter.addAction(ACTION_SET_ECG_MODE);
		intentFilter.addAction(ACTION_UNSET_ECG_MODE);
		
		intentFilter.addAction(ACTION_ALARM_DELAY_SEND_FRAME);
		
		return intentFilter;
	}

	private void startBleService() {
		setBleState(STATE_DISCONNECTED);
		
		bleService = new Intent(BleConnectionService.this,
				BluetoothLeLanglangService.class);
		this.startService(bleService);
		System.out.println("action bleconnection stateservice");
		boolean bll = this.bindService(bleService, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		startService(new Intent(BleConnectionService.this,
				DataStorageService.class));
		
//		sendSetEcgPolicyIntent(BleConnectionService.POLICY_MIXED,
//				   BleConnectionService.POLICY_SUB_POLICY_MIXED_0);	
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeLanglangService.LocalBinder) service)
					.getService();
			
//			if (!mBluetoothLeService.initialize()) {
//				// Log.e(TAG, "Unable to initialize Bluetooth");
//				// activity.finish();
//				showAlert();
//				return;
//			}
//			// Automatically connects to the device upon successful start-up
//			// initialization.
//			System.out.println("action bleconnection connect()");
//			connect();
//			
//			setBleState(STATE_CONNECTING);
			
			mBleStateMachine.onStarted();
			
//			m_date = null;
//			m_date = new Date();
//			setTimer();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			// Log.i(TAG, "Bluetooth service is disconnected");
			// mBluetoothLeService = null;
		}
	};
	
	private void showBox(final Context context) {
		System.out.println("action BluetoothLeService showBox called3.");

		Builder builder = new Builder(getApplicationContext());
		builder.setTitle("提示");
		builder.setMessage("无法初始化蓝牙设备,请关闭手机蓝牙然后再试.");
		builder.setNegativeButton("OK", null);
		Dialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

		System.out.println("action BluetoothLeService showBox completed.");
	}

	private void showAlert() {
		// <showBox(BluetoothLeService.this);
		System.out.println("action BleConnectionService showAlert()");
		showBox(getApplicationContext());
	}

	public void destroy() {
		stopService(new Intent(BleConnectionService.this,
				DataStorageService.class));
//		if (leBluetoothDeviceConnection != null) {
//			leBluetoothDeviceConnection.disconnect();
//		}
		System.out.println("action BleConnectionService destroy");
		this.unregisterReceiver(mGattUpdateReceiver);
		if (mBluetoothLeService != null) {
			System.out.println("action BleConnectionService destroy try to disconnect BLE");
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
			mBluetoothLeService = null;
		}
		this.unbindService(mServiceConnection); // First unbind service
		this.stopService(bleService); // Then stop the service
		System.out.println("action bleconnection destroy");
	}

	/**
	 * Connect to current device.
	 * 
	 * @return
	 */
	public boolean connect() {
		System.out.println("action bleconnection MAC:" + m_device_mac);
		if (m_device_mac != null && m_device_mac.length() > 0) { 
			return mBluetoothLeService.connect(m_device_mac);
		} else {			
		}
		
		return false;
	}

	public void registerGattUpdateReceiver() {
		this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		/****
		 * if (mBluetoothLeService != null) { final boolean result =
		 * mBluetoothLeService.connect(mDeviceAddress); Log.d(TAG,
		 * "Connect request result=" + result); }
		 ****/
	}

	@SuppressLint("NewApi")
	private void displayGattServices() {
		System.out.println("action displayGattServices.");
		GattServicesInfo gsi = mBluetoothLeService.getGattServicesInfo(false,
				null);
		if (gsi == null) {
			return;
		}

		ArrayList<ArrayList<BluetoothGattCharacteristic>> hierarchicalGattCharList = gsi
				.getHierarchicalGattCharacteristicsList();

		if (hierarchicalGattCharList != null) {
			BluetoothGattCharacteristic characteristic = getCharacteristicByUUID(
					hierarchicalGattCharList,
					"0000fff4-0000-1000-8000-00805f9b34fb");

			if (characteristic != null) {
				System.out.println("action UUID = ["
						+ characteristic.getUuid().toString() + "]");
				mBluetoothLeService.setCharacteristicNotification(
						characteristic, true);
			}
		}
	}

	/**
	 * @param gattChars
	 * @param strUUID
	 * @return
	 */
	@SuppressLint("NewApi")
	BluetoothGattCharacteristic getCharacteristicByUUID(
			ArrayList<ArrayList<BluetoothGattCharacteristic>> gattChars,
			String strUUID) {
		for (int i = 0; i < gattChars.size(); i++) {
			ArrayList<BluetoothGattCharacteristic> gatCharList = gattChars
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

//	class ReceiveBroadCast extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			stopService(bleService);
//
//		}
//	}
	
	private WakeLock wakeLock;
	private void acquireWakeLock(){
		if(wakeLock==null){
			PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
			wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,this.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}
	private void releaseWarkLock(){
		if(wakeLock!=null&&wakeLock.isHeld()){
				wakeLock.release();
				wakeLock=null;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags=START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void reastOncreat(){
		Notification notification=new Notification();
		startForeground(1, notification);
	}	
	
	private class BleConnectionMonitor extends Thread {
		private volatile boolean stop = false;
		
		public synchronized void  stopBleConnectionMonitor() {
			stop = true;
		}
		
		public void run() {
			while (!stop) {
				try {
					sleep(BLE_RECONNECT_TIME_SECOND * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				synchronized (lockBleState) {				
				if (getBleState() != STATE_CONNECTED) {
//					sendIntentToReconnect();					
				} else {
					if (mLastDataDate == null) {
						System.out.println("action BleConnectionService BleConnectionMonitor mLastDataDate is null");
						sendIntentToReconnect();
					} else {
						Date now = new Date();
						if (now.getTime() - mLastDataDate.getTime() 
									> BLE_JUDGE_DISCONNECT_TIME_SECOND * 1000) {
							
							System.out.println("action BleConnectionService BleConnectionMonitor DATA TIMEOUT");
							sendIntentToReconnect();
						}
					}
				}
				}
			}
		}
	}
	private void sendIntentToReconnect() {
		Intent intent = new Intent(ACTION_BLE_RECONNECT);
		sendBroadcast(intent);
	}
	
	
	
	private class BleStateMachine {
		private final static int S_STARTED = 0;
		private final static int S_CONNECTING = 1;
		private final static int S_CONNECTED = 2;
		private final static int S_DISCONNECTED = 3;
		
		private int _state = S_STARTED;
		
		public BleStateMachine() {
			reset();
			System.out.println("action BleConnectionService BleStateMachine started");
		}
		public void reset() {
			_state = S_STARTED;
			setBleState(STATE_DISCONNECTED);
		}
		public void onStarted() {
			System.out.println("action BleConnectionService BleStateMachine onStarted");
			if (bleStart()) {
				transTo(S_CONNECTING);
			}
			else { // on some error happened
				reset();
			}
		}
		public void onConnectingTimeout() {
			System.out.println("action BleConnectionService BleStateMachine onConnectingTimeout");
			if (_state != S_CONNECTING) return;
			
			bleDisconnect();
			transTo(S_DISCONNECTED);
		}
		public void onConnected() {
			System.out.println("action BleConnectionService BleStateMachine onConnected");
			if (_state != S_CONNECTING) return;
			transTo(S_CONNECTED);
		}
		public void onDisconnected() {
			System.out.println("action BleConnectionService BleStateMachine onDisconnected");
			if (_state == S_DISCONNECTED) return;
			
			bleDisconnect();
			transTo(S_DISCONNECTED);
		}
		public void onDelayReconnect() {
			System.out.println("action BleConnectionService BleStateMachine onDelayReconnect");
			if (bleConnect()) {
				transTo(S_CONNECTING);
			} else{
				reset();
			}
		}
		private void transTo(int newState) {
			mLastDataDate = new Date();
			_state = newState;
			
			String strState;
			if (newState == S_STARTED) strState = "S_STARTED";
			else if (newState == S_CONNECTING) strState = "S_CONNECTING";
			else if (newState == S_CONNECTED) strState = "S_CONNECTED";
			else if (newState == S_DISCONNECTED) strState = "S_DISCONNECTED";
			else {  strState = "S_UNKOWN"; }
			
			System.out.println("action BleConnectionService blestatemachine transTo " + strState);
			
			if (newState == S_CONNECTING) {			
				stopDelayReconnectTimer();
				stopCheckDataAliveTimer();
				
				setBleState(STATE_CONNECTING);
				startConnectingTimeoutTimer();
			}
			
			if (newState == S_DISCONNECTED) {
				stopConnectingTimeoutTimer();
				stopCheckDataAliveTimer();
				
				setBleState(STATE_DISCONNECTED);
				GlobalStatus.getInstance().reset();
				startDelayReconnectTimer();
			}
			
			if (newState == S_CONNECTED) {
				stopConnectingTimeoutTimer();
				stopDelayReconnectTimer();
				
				setBleState(STATE_CONNECTED);
				startCheckDataAliveTimer();
			}
		}	
	}
	private boolean bleStart() {	
		return bleConnect();
	}
	private boolean bleConnect() {
		if (!mBluetoothLeService.initialize()) {
			showAlert();
			return false;
		}
		
		return connect();
	}
	private void bleDisconnect() {
		if (mBluetoothLeService != null) {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
		}
	}
	private void startConnectingTimeoutTimer() {
		if (isConnectingTimeoutTimerStarted) return;
		isConnectingTimeoutTimerStarted = true;
		
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_CONNECTING_TIMEOUT);
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();

		am.set(AlarmManager.RTC, triggerAtTime + 1000 * 7, pi);
	}
	private void stopConnectingTimeoutTimer() {	
		if (!isConnectingTimeoutTimerStarted) return;
		isConnectingTimeoutTimerStarted = false;
		
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_CONNECTING_TIMEOUT);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);	
	}
	private void startDelayReconnectTimer() {
		if (isDelayReconnectTimerStarted) return;
		isDelayReconnectTimerStarted = true;
		
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_DELAY_RECONNECT);
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();

		am.set(AlarmManager.RTC, triggerAtTime + 1000 * 3, pi);
	}
	private void stopDelayReconnectTimer() {
		if (!isDelayReconnectTimerStarted) return;
		isDelayReconnectTimerStarted = false;
		
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_DELAY_RECONNECT);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}
	private void startCheckDataAliveTimer() {
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_CHECK_DATA_ALIVE);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();

		am.setRepeating(AlarmManager.RTC, triggerAtTime + 1000 * 15, 1000 * 15, pi);	
	}
	private void stopCheckDataAliveTimer() {
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_CHECK_DATA_ALIVE);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}

	

	private Timer timer;

	private void setTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new updatehearThread().start();
			}
		}, 500, 1000 * 30);
	}
	private void cancelTimer(){
		if(timer!=null){
			timer.cancel();
		}
	}
	
	class updatehearThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name=UserInfo.getIntance().getUserData().getMy_name();
			String userInfo="[{username:\""+user_name+"\",heart:\""+mBleState+"\"}]";
			String resultString=Client.getupdateHeart(userInfo);
			System.out.println("resultString:"+resultString);
			if(resultString==null){
				return;
			}
			if(!resultString.equals("1")){
				resultString=Client.getupdateHeart(userInfo);
			}
			UIUtil.setMessage(handler, UPDATE_HEAR,mBleState);
		}
	}
	private final int UPDATE_HEAR=111;
	private final int SHOW_MODE_FRAME = 112;
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==UPDATE_HEAR){
				System.out.println("实时更新数据："+msg.obj.toString());
			}
			else if (msg.what == SHOW_MODE_FRAME) {
				UIUtil.setToast(getApplicationContext(), msg.obj.toString());
			}
			
		};
	};
	
	private void sendAliveFrame() {
//		if (isShouldSendFrameAtOnce()) {
//			xmitFrame(SEND_FRAME_TYPE_KEEPALIVE);
//		}
//		else {
//			if (mPengdingSendFrameType == SEND_FRAME_TYPE_NONE) {
//				Date now = new Date();
//				startDelayStartSendTimer(now.getTime() - mLastSendDate.getTime());
//				
//				setPengdingSendFrameType(SEND_FRAME_TYPE_KEEPALIVE);
//			}
//		}
		
//		if (mPengdingSendFrameType == SEND_FRAME_TYPE_NONE) {
//			xmitFrame(SEND_FRAME_TYPE_KEEPALIVE);
//		}
//		else {
//			xmitFrame(mPengdingSendFrameType);
//		}
//		mPengdingSendFrameType = SEND_FRAME_TYPE_NONE;
		
		xmitFrame(SEND_FRAME_TYPE_KEEPALIVE);
		
//		if (mToggle) {
//			mToggle = false;
//			xmitFrame(SEND_FRAME_TYPE_SWITCH_TO_ECG);
//		}
//		else {
//			mToggle = true;
//			xmitFrame(SEND_FRAME_TYPE_SWITCH_TO_HEARTRATE);
//		}
		
//		if (mBluetoothLeService == null) {
//			System.out.println("action BleConnectionService sendAliveFrame mBluetoothLeService is null, return");
//			return;
//		}
//		
//		final String serviceUUID = "0000fff0-0000-1000-8000-00805f9b34fb";
//		
//		GattServicesInfo gsi = mBluetoothLeService.getGattServicesInfo(false, null);
//		if (gsi == null) {
//			return;
//		}
//		
//		ArrayList<BluetoothGattCharacteristic> chList = gsi.getGattCharacteristicList(serviceUUID);
//		
//		byte[] aliveFrame = createAliveFrame();
//		
//		UIUtil.setMessage(handler, SHOW_MODE_FRAME, bytesToString(aliveFrame));
//		System.out.println("action BleConnectionService sendAliveFrame:" + bytesToString(aliveFrame));
//		
//		final String UUID_TO_WRITE = "0000fff1-0000-1000-8000-00805f9b34fb";
//		
//		ArrayList<ArrayList<BluetoothGattCharacteristic>> hierarchicalGattCharList = gsi
//				.getHierarchicalGattCharacteristicsList();
//		BluetoothGattCharacteristic characteristicToWrite = getCharacteristicByUUID(
//				hierarchicalGattCharList, UUID_TO_WRITE);
//		if (characteristicToWrite != null) {
//			mBluetoothLeService.writeFrame(characteristicToWrite, aliveFrame);
//		} else {
//			System.out.println("action BleConnectionService characteristicToWrite is null.");
//		}		
	}
	public byte[] createAliveFrame() {
		byte[] frame = new byte[20];
		
		Calendar c = Calendar.getInstance();
		
		System.out.println("action BleConnectionService create80Frame yy[" 
				+ c.get(Calendar.YEAR)  + "] MM [" + (c.get(Calendar.MONTH) + 1) + "] dd ["
				+ c.get(Calendar.DATE) + "] HH [" + c.get(Calendar.HOUR_OF_DAY) + "] mm ["
				+ c.get(Calendar.MINUTE) + "] ss [" + c.get(Calendar.SECOND) + "] day_of_week ["
				+ c.get(Calendar.DAY_OF_WEEK) + "]");
		
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
		
//		frame[12] = 0x00;
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		frame[17] = (byte) dayOfWeek;
		
		frame[18] = 0x00;
		
		frame[19] = 0x12;
		
		return frame;
	}
	
	private void sendChangeECGModeFrame(int mode) {
//		if (isShouldSendFrameAtOnce()) {
//			if (mode == DataStorageService.MODE_ECG_ECG) {
//				xmitFrame(SEND_FRAME_TYPE_SWITCH_TO_ECG);
//			}
//			else if (mode == DataStorageService.MODE_ECG_HEART_RATE){
//				xmitFrame(SEND_FRAME_TYPE_SWITCH_TO_HEARTRATE);
//			}
//		}
//		else {
//			if (mPengdingSendFrameType == SEND_FRAME_TYPE_NONE) {
//				Date now = new Date();
//				startDelayStartSendTimer(now.getTime() - mLastSendDate.getTime());				
//			}
//			
//			if (mode == DataStorageService.MODE_ECG_ECG) {
//				setPengdingSendFrameType(SEND_FRAME_TYPE_SWITCH_TO_ECG);
//			}
//			else if (mode == DataStorageService.MODE_ECG_HEART_RATE){
//				setPengdingSendFrameType(SEND_FRAME_TYPE_SWITCH_TO_HEARTRATE);
//			}
//		}
		if (mode == DataStorageService.MODE_ECG_ECG) {
			setPengdingSendFrameType(SEND_FRAME_TYPE_SWITCH_TO_ECG);
		}
		else if (mode == DataStorageService.MODE_ECG_HEART_RATE){
			setPengdingSendFrameType(SEND_FRAME_TYPE_SWITCH_TO_HEARTRATE);
		}
		
//		if (mBluetoothLeService == null) return;
//		
//		final String serviceUUID = "0000fff0-0000-1000-8000-00805f9b34fb";
//		
//		GattServicesInfo gsi = mBluetoothLeService.getGattServicesInfo(false, null);
//		if (gsi == null) {
//			return;
//		}
//		
//		ArrayList<BluetoothGattCharacteristic> chList = gsi.getGattCharacteristicList(serviceUUID);
//		byte[] switchECGModeFrame = createSwitchECGFrame(mode);
//		
//		System.out.println("action BleConnectionService sendChangeECGModeFrame:" + bytesToString(switchECGModeFrame));
//		
////		UIUtil.setMessage(handler, SHOW_MODE_FRAME, bytesToString(switchECGModeFrame));
//		
//		final String UUID_TO_WRITE = "0000fff1-0000-1000-8000-00805f9b34fb";
//		
//		ArrayList<ArrayList<BluetoothGattCharacteristic>> hierarchicalGattCharList = gsi
//				.getHierarchicalGattCharacteristicsList();
//		BluetoothGattCharacteristic characteristicToWrite = getCharacteristicByUUID(
//				hierarchicalGattCharList, UUID_TO_WRITE);
//		if (characteristicToWrite != null) {
//			UIUtil.setMessage(handler, SHOW_MODE_FRAME, bytesToString(switchECGModeFrame));
//			
//			mBluetoothLeService.writeFrame(characteristicToWrite, switchECGModeFrame);
//		} else {
//			System.out.println("action BleConnectionService sendChangeECGModeFrame characteristicToWrite is null.");
//		}
	}
	
	public byte[] createSwitchECGFrame(int mode) {
		if (mode == DataStorageService.MODE_ECG_HEART_RATE 
				|| mode == DataStorageService.MODE_ECG_ECG) {
		} else {
			return null;
		}
		
		byte[] frame = new byte[20];
		
		Calendar c = Calendar.getInstance();
		
		System.out.println("action BleConnectionService create80Frame yy[" 
				+ c.get(Calendar.YEAR)  + "] MM [" + (c.get(Calendar.MONTH) + 1) + "] dd ["
				+ c.get(Calendar.DATE) + "] HH [" + c.get(Calendar.HOUR_OF_DAY) + "] mm ["
				+ c.get(Calendar.MINUTE) + "] ss [" + c.get(Calendar.SECOND) + "] day_of_week ["
				+ c.get(Calendar.DAY_OF_WEEK) + "]");
		
		frame[0] = (byte) 0x5A;
		frame[1] = (byte) 0x5A;
		frame[2] = (byte) 0x12;
		frame[3] = (byte) 0x86;
		frame[4] = (byte) 0x00;
		
		frame[5] = (byte) 0xFF;
		
		if (mode == DataStorageService.MODE_ECG_HEART_RATE) { //切换到心率		
			frame[6] = (byte) 0xAA;
			frame[7] = (byte) 0xBB;
			frame[8] = (byte) 0xBB;
		} else if (mode == DataStorageService.MODE_ECG_ECG) { //切换到心电
			frame[6] = (byte) 0xAA;
			frame[7] = (byte) 0xBB;
			frame[8] = (byte) 0xAA;
		}
		
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
		
//		frame[12] = 0x00;
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		frame[17] = (byte) dayOfWeek;
		
		frame[18] = 0x00;
		
		frame[19] = 0x12;
		
		return frame;
	}	
	
	private void startAliveFrameTimer() {
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_SEND_ALIVE_FRAME);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();
		
//		am.setRepeating(AlarmManager.RTC, triggerAtTime, 2000, pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime, 1000 * 10, pi);
	}
	private void cancelAliveFrameTimer() {		
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_SEND_ALIVE_FRAME);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}
	
	private void startResetFrameTimer() {
		if (!mIsSendResetTimerStarted) { mIsSendResetTimerStarted = true; }
		else { return; }
		
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_SEND_RESET_FRAME);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();
		
//		am.setRepeating(AlarmManager.RTC, triggerAtTime, 2000, pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 1000 * 30, 1000 * 60 * 30, pi);
	}
	private void cancelResetFrameTimer() {
		if (mIsSendResetTimerStarted) { mIsSendResetTimerStarted = false; }
		else { return; }
		
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_SEND_RESET_FRAME);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}
	
	public String bytesToString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b: data) 
			sb.append(String.format("%02X ", b));
		return sb.toString();
	}
	private void startCheckCurrentEcgModeTimer() {
		if (isCheckCurrentEcgModeTimerStarted) return;
		isCheckCurrentEcgModeTimerStarted = true;
		
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_CHECK_CURRENT_ECG_MODE);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();
		
//		am.set(AlarmManager.RTC, triggerAtTime + 1000 * 3, pi);
//		am.set(AlarmManager.RTC, triggerAtTime + 1000 * 5, pi);
		am.set(AlarmManager.RTC, triggerAtTime + 1000 * 10, pi);
	}
	private void cancelCheckCurrentEcgModeTimer() {
		if (!isCheckCurrentEcgModeTimerStarted) return;
		isCheckCurrentEcgModeTimerStarted = false;
		
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_CHECK_CURRENT_ECG_MODE);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}
	private int[] getEcgModeRatios(String ratioStr) {
		// TODO Auto-generated method stub
		return null;
	}
	private void switchEcgMode(int mode, int[] ratios) {
		if (mode == DataStorageService.MODE_ECG_ECG 
					|| mode == DataStorageService.MODE_ECG_HEART_RATE) {
			mEcgModeMonitor.setEcgMode(mode);
		}
	}
	private void setExceptedEcgMode(int mode) {
		mExceptedEcgMode = mode;
	}
	private class EcgModeMonitor {
		private final static int M_STARTED  = 0;
		private final static int M_ECG_0 = 1;
		private final static int M_ECG_1 = 2;
		private final static int M_HEART_RATE_0 = 3;
		private final static int M_HEART_RATE_1 = 4;
		
		private int mCountHeartRateCmd = 0;
		
		private int currMode = M_STARTED;
		
		public EcgModeMonitor() {
			reset();
		}
		public void reset() {
			System.out.println("action BleConnection EcgModeMonitor reset");
			currMode = M_STARTED;
			cancelCheckCurrentEcgModeTimer();
		}
		public synchronized void setEcgMode(int mode) {//0: ECG only, //1: HeartRate
			reset();
			
			if (mode == DataStorageService.MODE_ECG_ECG) {
				mCountHeartRateCmd = 0;
				if (currMode == M_ECG_0) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
				}
				else if (currMode == M_ECG_1) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
				}
				else if (currMode == M_HEART_RATE_0) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
				}
				else if (currMode == M_HEART_RATE_1) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
				}
				else if (currMode == M_STARTED) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();					
				}
				
				transToMode(M_ECG_0);
			}
			else if (mode == DataStorageService.MODE_ECG_HEART_RATE) {
				if (currMode == M_HEART_RATE_0) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
					
//					mCountHeartRateCmd++;
//					if (mCountHeartRateCmd >= 5) {
//						sendResetFrame();
//					}
//					if (mCountHeartRateCmd >= 6) {
//						sendResetFrame();
//						mCountHeartRateCmd = 0;
//					}
				}
				else if (currMode == M_HEART_RATE_1) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
					
					mCountHeartRateCmd = 0;
				}
				else if (currMode == M_ECG_0) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
					
					mCountHeartRateCmd = 0;
				}
				else if (currMode == M_ECG_1) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
				}
				else if (currMode == M_STARTED) {
					cancelCheckCurrentEcgModeTimer();
					setExceptedEcgMode(mode);
					sendChangeECGModeFrame(mode);
					startCheckCurrentEcgModeTimer();
					
					mCountHeartRateCmd = 0;
				}
				
				transToMode(M_HEART_RATE_0);
			}
		}
		public void ackEcgMode(int mode) {
			if (mode == DataStorageService.MODE_ECG_ECG) {
//				if (currMode == M_ECG_1) {
//					return;
//				}
//				else 
				if (currMode == M_ECG_0) {
					transToMode(M_ECG_1);
				}
				else {
					reset();
					setEcgMode(mExceptedEcgMode);
				}
			}
			else if (mode == DataStorageService.MODE_ECG_HEART_RATE) {
//				if (currMode == M_HEART_RATE_1) {
//					return;
//				}
//				else 
				if (currMode == M_HEART_RATE_0) {
					transToMode(M_HEART_RATE_1);
				}
				else {
					reset();
					setEcgMode(mExceptedEcgMode);
				}
			}		
		}
		private void transToMode(int mode) {
			currMode = mode;
			
			if (currMode == M_ECG_1 || currMode == M_HEART_RATE_1) {
				cancelCheckCurrentEcgModeTimer();
			}
		}
	}
	
	private void startEcgPolicyEcgTimeoutTimer(int seconds) {
		if (isEcgPolicyEcgTimeoutTimerStarted) return;
		isEcgPolicyEcgTimeoutTimerStarted = true;
		
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_ECG_POLICY_ECG_TIMEOUT);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();
		
		am.set(AlarmManager.RTC, triggerAtTime + 1000 * seconds, pi);
	}
	private void cancelEcgPolicyEcgTimeoutTimer() {
		if (!isEcgPolicyEcgTimeoutTimerStarted) return;
		isEcgPolicyEcgTimeoutTimerStarted = false;
		
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_ECG_POLICY_ECG_TIMEOUT);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}
	
	private void startEcgPolicyHeartRateTimeoutTimer(int seconds) {
		if (isEcgPolicyHeartRateTimeoutTimerStarted) return;
		isEcgPolicyHeartRateTimeoutTimerStarted = true;
		
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_ECG_POLICY_HEART_RATE_TIMEOUT);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();
		
		am.set(AlarmManager.RTC, triggerAtTime + 1000 * seconds, pi);
	}
	private void cancelEcgPolicyHeartRateTimeoutTimer() {
		if (!isEcgPolicyHeartRateTimeoutTimerStarted) return;
		isEcgPolicyHeartRateTimeoutTimerStarted = false;
		
		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_ECG_POLICY_HEART_RATE_TIMEOUT);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}
	
	private class EcgModePolicyManager {		
		private int mPolicy = POLICY_UNKNOWN;
		private int mSubPolicy = POLICY_SUB_UNKNOWN;
		
		public EcgModePolicyManager() {
			mPolicy = POLICY_UNKNOWN;
			mSubPolicy = POLICY_SUB_UNKNOWN;
		}
		
		public synchronized void setPolicy(int policy, int subPolicy) {
			mPolicy = policy;
			mSubPolicy = subPolicy;
			
			cancelCheckCurrentEcgModeTimer();
			cancelEcgPolicyEcgTimeoutTimer();
			cancelEcgPolicyHeartRateTimeoutTimer();
			setEcgMode();
		}
		
		private void setEcgMode() {
			if (mPolicy == POLICY_ECG_ONLY) {
//				sendSwitchModeIntent(DataStorageService.MODE_ECG_ECG);
				mEcgModeMonitor.setEcgMode(DataStorageService.MODE_ECG_ECG);
			}
			else if (mPolicy == POLICY_ECG_HEART_RATE) {
//				sendSwitchModeIntent(DataStorageService.MODE_ECG_HEART_RATE);
				mEcgModeMonitor.setEcgMode(DataStorageService.MODE_ECG_HEART_RATE);
			}
			else if (mPolicy == POLICY_MIXED) {
				startMixedMode();
			}
		}
		
		private void startMixedMode() {
			onHeartRateModeTimeout();
		}
		
		private void onEcgModeTimeout() {
//			sendSwitchModeIntent(DataStorageService.MODE_ECG_HEART_RATE);
			cancelEcgPolicyEcgTimeoutTimer();
			mEcgModeMonitor.setEcgMode(DataStorageService.MODE_ECG_HEART_RATE);
//			startEcgPolicyHeartRateTimeoutTimer(30);
			startEcgPolicyHeartRateTimeoutTimer(60);
//			startEcgPolicyHeartRateTimeoutTimer(10);
//			startEcgPolicyHeartRateTimeoutTimer(30);
		}
		
		private void onHeartRateModeTimeout() {
//			sendSwitchModeIntent(DataStorageService.MODE_ECG_ECG);
			cancelEcgPolicyHeartRateTimeoutTimer();
			mEcgModeMonitor.setEcgMode(DataStorageService.MODE_ECG_ECG);
			int duration ;
			if (mSubPolicy == POLICY_SUB_POLICY_MIXED_0) {
//				duration = 90;
				duration = 60;
//				duration = 10;
//				duration = 30;
			}
			else {
				duration = 120;
			}
			startEcgPolicyEcgTimeoutTimer(duration);
		}	
	}
	
	private void sendSwitchModeIntent(int mode) {
		final Intent intent = new Intent(ACTION_SEND_SWITCH_MODE_FRAME);
		intent.putExtra("MODE", mode);
		sendBroadcast(intent);
	}
	
	private void sendSetEcgPolicyIntent(int policy, int subPolicy) {
		final Intent intent = new Intent(BleConnectionService.ACTION_SET_ECG_MODE);
		intent.putExtra("POLICY", policy);
		intent.putExtra("SUB_POLICY", subPolicy);
		intent.putExtra("FROM", BleConnectionService.POLICY_CTRL_FROM_CLIENT);
		sendBroadcast(intent);
	}
	
	private class EcgModeManager {
		private boolean clientControl = false;
		private boolean serverControl = false;
		
		private int clientPolicy = POLICY_UNKNOWN;
		private int clientSubPolicy = POLICY_SUB_UNKNOWN;
		private int serverPolicy = POLICY_UNKNOWN;
		private int serverSubPolicy = POLICY_SUB_UNKNOWN;
		
		public EcgModeManager() {
			clientControl = false;
			serverControl = false;
		
			clientPolicy = POLICY_UNKNOWN;
			clientSubPolicy = POLICY_SUB_UNKNOWN;
			serverPolicy = POLICY_UNKNOWN;
			serverSubPolicy = POLICY_SUB_UNKNOWN;
		}
		public void startClientControl(int policy, int subPolicy) {
			clientControl = true;
			clientPolicy = policy;
			clientSubPolicy = subPolicy;
			
			changeEcgModePolicy();
		}
		public void stopClientControl() {
			clientControl = false;
			clientPolicy = POLICY_UNKNOWN;
			clientSubPolicy = POLICY_SUB_UNKNOWN;
			
			changeEcgModePolicy();
		}
		public void startServerControl(int policy, int subPolicy) {
			serverControl = true;
			serverPolicy = policy;
			serverSubPolicy = subPolicy;
			
			changeEcgModePolicy();
		}
		public void stopServerControl() {
			serverControl = false;
			serverPolicy = POLICY_UNKNOWN;
			serverSubPolicy = POLICY_SUB_UNKNOWN;

			changeEcgModePolicy();
		}
		private void changeEcgModePolicy() {
			if (serverControl) {
				mEcgModePolicyManager.setPolicy(serverPolicy, serverSubPolicy);
			}
			else {
				if (clientControl) {
					mEcgModePolicyManager.setPolicy(clientPolicy, clientSubPolicy);
				}
				else {
					mEcgModePolicyManager.setPolicy(POLICY_ECG_ONLY, POLICY_SUB_UNKNOWN);
				}
			}
		}
	}
	
	private void sendResetFrame() {
		if (mBluetoothLeService == null) return;
		
		final String serviceUUID = "0000fff0-0000-1000-8000-00805f9b34fb";
		
		GattServicesInfo gsi = mBluetoothLeService.getGattServicesInfo(false, null);
		if (gsi == null) {
			return;
		}
		
		ArrayList<BluetoothGattCharacteristic> chList = gsi.getGattCharacteristicList(serviceUUID);
		byte[] resetFrame = createResetFrame();
		
		System.out.println("action BleConnectionService sendResetFrame:" + bytesToString(resetFrame));
		
//		UIUtil.setMessage(handler, SHOW_MODE_FRAME, bytesToString(switchECGModeFrame));
		
		final String UUID_TO_WRITE = "0000fff1-0000-1000-8000-00805f9b34fb";
		
		ArrayList<ArrayList<BluetoothGattCharacteristic>> hierarchicalGattCharList = gsi
				.getHierarchicalGattCharacteristicsList();
		BluetoothGattCharacteristic characteristicToWrite = getCharacteristicByUUID(
				hierarchicalGattCharList, UUID_TO_WRITE);
		if (characteristicToWrite != null) {
			UIUtil.setMessage(handler, SHOW_MODE_FRAME, bytesToString(resetFrame));
			mBluetoothLeService.writeFrame(characteristicToWrite, resetFrame);
		} else {
			System.out.println("action BleConnectionService sendChangeECGModeFrame characteristicToWrite is null.");
		}		
	}
	
	public byte[] createResetFrame() {
		byte[] frame = new byte[20];
		
		Calendar c = Calendar.getInstance();
		
		System.out.println("action BleConnectionService create80Frame yy[" 
				+ c.get(Calendar.YEAR)  + "] MM [" + (c.get(Calendar.MONTH) + 1) + "] dd ["
				+ c.get(Calendar.DATE) + "] HH [" + c.get(Calendar.HOUR_OF_DAY) + "] mm ["
				+ c.get(Calendar.MINUTE) + "] ss [" + c.get(Calendar.SECOND) + "] day_of_week ["
				+ c.get(Calendar.DAY_OF_WEEK) + "]");
		
		frame[0] = (byte) 0x5A;
		frame[1] = (byte) 0x5A;
		frame[2] = (byte) 0x12;
		frame[3] = (byte) 0x86;
		frame[4] = (byte) 0x00;
		
		frame[5] = (byte) 0xFF;
		
		frame[6] = (byte) 0xAA;
		frame[7] = (byte) 0xBB;
		frame[8] = (byte) 0xDD;
		
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
		
//		frame[12] = 0x00;
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		frame[17] = (byte) dayOfWeek;
		
		frame[18] = 0x00;
		
		frame[19] = 0x12;
		
		return frame;
	}
	
	private void xmitFrame(int frameType) {
		if (frameType == SEND_FRAME_TYPE_KEEPALIVE || frameType == SEND_FRAME_TYPE_SWITCH_TO_ECG 
								|| frameType == SEND_FRAME_TYPE_SWITCH_TO_HEARTRATE) {
		} else {
			return;
		}
		
		if (mBluetoothLeService == null) return;
		
		final String serviceUUID = "0000fff0-0000-1000-8000-00805f9b34fb";
		
		GattServicesInfo gsi = mBluetoothLeService.getGattServicesInfo(false, null);
		if (gsi == null) {
			return;
		}
		
		ArrayList<BluetoothGattCharacteristic> chList = gsi.getGattCharacteristicList(serviceUUID);
		byte[] sendFrame = null;
		if (frameType == SEND_FRAME_TYPE_KEEPALIVE) {
			sendFrame = createAliveFrame();
		} 
		else if (frameType == SEND_FRAME_TYPE_SWITCH_TO_ECG) {
			sendFrame = createSwitchECGFrame(DataStorageService.MODE_ECG_ECG);
		}
		else if (frameType == SEND_FRAME_TYPE_SWITCH_TO_HEARTRATE) {
			sendFrame = createSwitchECGFrame(DataStorageService.MODE_ECG_HEART_RATE);
		}
		
		System.out.println("action BleConnectionService xmitFrame:" + bytesToString(sendFrame));
		
//			UIUtil.setMessage(handler, SHOW_MODE_FRAME, bytesToString(switchECGModeFrame));
		
		final String UUID_TO_WRITE = "0000fff1-0000-1000-8000-00805f9b34fb";
		
		ArrayList<ArrayList<BluetoothGattCharacteristic>> hierarchicalGattCharList = gsi
				.getHierarchicalGattCharacteristicsList();
		BluetoothGattCharacteristic characteristicToWrite = getCharacteristicByUUID(
				hierarchicalGattCharList, UUID_TO_WRITE);
		if (characteristicToWrite != null) {
			if (sendFrame != null) {
//				UIUtil.setMessage(handler, SHOW_MODE_FRAME, bytesToString(sendFrame));
				mBluetoothLeService.writeFrame(characteristicToWrite, sendFrame);
				
				mLastSendDate = new Date();
			}
		} else {
			System.out.println("action BleConnectionService xmitFrame characteristicToWrite is null.");
		}
	}
	
	private boolean isShouldSendFrameAtOnce() {
		if (mLastSendDate != null) {
			Date now = new Date();
			if (now.getTime() - mLastSendDate.getTime() > 1000 * 3) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}	
	private synchronized void setPengdingSendFrameType(int frameType) {
		mPengdingSendFrameType = frameType;
	}
	private synchronized void startDelayStartSendTimer(long delayInMs) {
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_DELAY_SEND_FRAME);
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();

		am.set(AlarmManager.RTC, triggerAtTime + delayInMs, pi);
	}
}
