package com.example.bleforcert.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.example.bleforcert.R;
import com.example.bleforcert.R.layout;
import com.example.bleforcert.R.menu;
import com.langlang.activity.EcgPainterBase.ECGGLSurfaceView;
import com.langlang.ble.BleConnectionNotifiaction;
import com.langlang.global.UserInfo;
import com.langlang.service.BleConnectionService;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
import com.langlang.utils.Filter;
import com.langlang.utils.UIUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final int UPDATE_CANVAS = 1;
	
	private final static int UPDATA_BLE_STATE=40;
	
	private final static int SHOW_MAC = 42;
	
	private final static int SHOW_IS_PAITING_STOPPED = 52;
	
	private final static int UPDATE_SHOW_EXPlAIN = 60;
	
	private static final String NT_CONNECTED = "设备已连接";
	private static final String NT_CONNECTTING = "正在尝试连接设备...";
	private static final String NT_DISCONNECTED = "设备未连接";
	private static final String NT_SCANNING = "正在搜索设备";
	private static final String NT_UNKNOWN = "未知设备状态";	
	
	private long exitTime = 0;
	
	private static final int MAX_POINT = 750;
	private final int UPDATE_NETWORK_DATE=9;
	Filter filter = new Filter();
	Queue<Integer> queue = new LinkedList<Integer>();
	int pointNumber = 0;
	float[] ECGData = new float[MAX_POINT];
	ECGGLSurfaceView mSurfaceView;
	boolean isECGCanvasInitialized = false;
	
	private int mBleState = BleConnectionService.STATE_DISCONNECTED;
	
	private volatile boolean mCapture = false;
	private Object lockCapture = new Object();
	
	private static final String ACTION_ALARM 
		= "com.example.bleforcert.MainActivity.ACTION_ALARM";
	
	BleConnectionNotifiaction mBleConnectionNotifiaction
	= new BleConnectionNotifiaction(this);
	
	private String mMac = null;
	private TextView txtMac;
	
	private String mState = null;
	private TextView txtState;
	
	private boolean stopPainting = false;
	
	private boolean mIsServiceStarted = false;
	
	private Object lockResetECGView = new Object();
	
	private LinearLayout explain_layout;
	private TextView explainY_tw;
	private TextView explainX_tw; 
	private float mYGridValue = 0.0f;
	private boolean mIsExplainShowed = false;
	
//	private static final int RESET_TICK_COUNT = 45000;
	private static final int RESET_TICK_COUNT = 3000;
	private int ecgTickCount = 0;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPDATE_CANVAS) {
				float[] fVal = new float[10];				
				int[] queueData = (int[]) msg.obj;
				
				if ((queueData != null) && (queueData.length >= 10)) {
					for (int i = 0; i < 10; i++) {						
//						fVal[i] = ((queueData[i] - 32000) * 6) / 1000f;  //for ECG
						fVal[i] = (queueData[i] * 1.0f) / 128;  //for ECG
//						fVal[i] = (queueData[i] * 1.0f) / 36550;// 128;  //for ECG
					}
					if (pointNumber <= 740) {
						if (pointNumber == 0) {
							System.out.println("action EcgPainterActivity pointNumber is 0.");
						} else {
							for (int i = pointNumber - 1; i >= 0; i--) {
								ECGData[(i + 10)] = ECGData[i];
							}
						}
					} else {
						for (int i = pointNumber - 10 - 1; i >= 0; i--) {
							ECGData[(i + 10)] = ECGData[i];
						}
					}
					
					for (int i = 0; i < 10; i++) {
						ECGData[(9 - i)] = fVal[i];
					}
					
					pointNumber += 10;
					if (pointNumber >= 750) pointNumber = 750;
				
					mSurfaceView.drawECG(ECGData, pointNumber);
				}
			}
			else if (msg.what == UPDATA_BLE_STATE) {
//				mBleConnectionNotifiaction.show(mBleState);
				showState(mBleState);
			}
			else if (msg.what == SHOW_MAC) {
				System.out.println("action MainActivity MAC:" + mMac);
				String showMac = "";
				if (mMac ==  null || mMac.length() != 12) {					
				} else {
					showMac += mMac.substring(0, 2);
					showMac += ":";
					showMac += mMac.substring(2, 4);
					showMac += ":";
					showMac += mMac.substring(4, 6);
					showMac += ":";
					showMac += mMac.substring(6, 8);
					showMac += ":";
					showMac += mMac.substring(8, 10);
					showMac += ":";
					showMac += mMac.substring(10, 12);
					
					txtMac.setText("当前设备MAC地址:" + showMac);
				}
			}
			
			else if (msg.what == UPDATE_SHOW_EXPlAIN) {
				Boolean isShowExplain = (Boolean) msg.obj;
				if (isShowExplain) {
					DecimalFormat formator = new DecimalFormat("#.######");
					explainY_tw.setText("Y轴:每小格" + formator.format(mYGridValue) + "V");
					explainX_tw.setText("X轴:每小格40毫秒");
					explain_layout.setVisibility(View.VISIBLE);
				}
				else {
					explain_layout.setVisibility(View.GONE);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		
		mSurfaceView =(ECGGLSurfaceView)this.findViewById(R.id.hte_GLSurfaceView_ecg);
		txtMac = (TextView) this.findViewById(R.id.main_mac_tw);
		txtState = (TextView) this.findViewById(R.id.main_status_tw);
		

		explain_layout = (LinearLayout) this.findViewById(R.id.hte_explain_layout);
		explain_layout.setVisibility(View.GONE);
		explainY_tw = (TextView) this.findViewById(R.id.hte_explainY_tv);
		explainX_tw = (TextView) this.findViewById(R.id.hte_explainX_tv);
		
		mSurfaceView.setCallback(new ECGGLSurfaceView.CanvasReadyCallback() {
			@Override
			public void notifyCanvasReady() {
				isECGCanvasInitialized = true;
//				hideProgressBar();
			}
			@Override
			public boolean getCapture() {
				synchronized(lockCapture) {
					return mCapture;
				}
			}
			@Override
			public void onCaptured(Bitmap bitmap) {
//				setCapture(false);
//				onECGCaptured(bitmap);
			}
			@Override
			public boolean stopPainting() {
				return stopPainting;
			}
			@Override
			public void onPaintingStopped(float yGridValue) {
				if (!mIsExplainShowed) {
					mYGridValue = yGridValue;
					UIUtil.setMessage(handler, UPDATE_SHOW_EXPlAIN, true);
					mIsExplainShowed = true;
				}
			}
		});
		
		mSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (stopPainting) {
					stopPainting = false;
					UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED, false);
					UIUtil.setMessage(handler, UPDATE_SHOW_EXPlAIN, false);
					mIsExplainShowed = false;
				} else {
					stopPainting = true;
					UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED, true);
				}
			}
		});
		
		mIsServiceStarted = false;
		
		mMac = UserInfo.getIntance().getMacAddr();
		
//		startService(new Intent(MainActivity.this,
//				BleConnectionService.class));
		startServices();
		
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM);
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();
//		am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime + 200, 40, pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, 40, pi);
		
        PowerManager powerManager = ((PowerManager) getSystemService(POWER_SERVICE));
	}
	
	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	@Override
	protected void onResume() {
		super.onResume();
		
		filter.reset();
		pointNumber = 0;
		queue.clear();
//		mSurfaceView.reset();
		isECGCanvasInitialized = false;
		
		mMac = UserInfo.getIntance().getMacAddr();
		UIUtil.setMessage(handler, SHOW_MAC);
		
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		registerReceiver(mGattUpdateReceiver, makeAlarmIntentFilter());
		
		ecgTickCount = 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		stopService(new Intent(MainActivity.this, BleConnectionService.class));
		stopServices();
		
		Intent intent =new Intent();  
		intent.setAction(ACTION_ALARM);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}	

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				this.exitApp();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 退出程序
	 */
	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			UIUtil.setToast(MainActivity.this, "再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			
//			stopServices();
			// cancelTimer();
//			new exitThread().start();
			MainActivity.this.finish();
			// System.exit(0);
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.select_device_again:
			stopServices();
			final Intent intent = new Intent(MainActivity.this, 
									SampleLeBluetoothDeviceScanActivity.class);
			startActivity(intent);
			
			MainActivity.this.finish();
			
			break;
		case R.id.quit_app:
			stopServices();
			MainActivity.this.finish();
			
			break;
		}
		return true;
	}
	
	private void setBleState(int state) {
		mBleState = state;
		UIUtil.setMessage(handler, UPDATA_BLE_STATE);
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(DataStorageService.ACTION_ECG_DATA_AVAILABLE);
		intentFilter.addAction(DataStorageService.ACTION_ALERT_SD_STATUS);
		intentFilter.addAction(BleConnectionService.ACTION_RESPONSE_BLE_STATE);
		return intentFilter;
	}
	
	private static IntentFilter makeAlarmIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);		
		intentFilter.addAction(ACTION_ALARM);
		return intentFilter;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
//			System.out.println("action HeartRateActivity = " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				
				setBleState(BleConnectionService.STATE_CONNECTED);
				
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				
				setBleState(BleConnectionService.STATE_DISCONNECTED);
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (ACTION_ALARM.equals(action)) {
//					System.out.println("action HeartRateActivity ACTION_ALARM queu_size[" + queue.size() + "]");
					int[] queueData = new int[10];
					boolean hasData = false;
					
					synchronized (queue) {
						if (queue.size() >= 10) {
							for (int i = 0; i < 10; i++) {
								queueData[i] = queue.poll();
							}
							
							hasData = true;
						}
					}
					
					if (hasData) {
						UIUtil.setMessage(handler, UPDATE_CANVAS, queueData);
					}
					
					if (ecgTickCount > RESET_TICK_COUNT) {
						resetAlarm();
						
						int[] queueData2 = new int[10];
						boolean hasData2 = false;
						
						synchronized (queue) {
							if (queue.size() >= 10) {
								for (int i = 0; i < 10; i++) {
									queueData2[i] = queue.poll();
								}
								
								hasData2 = true;
							}
						}
						
						if (hasData2) {
							UIUtil.setMessage(handler, UPDATE_CANVAS, queueData2);
						}
						
						int[] queueData3 = new int[10];
						boolean hasData3 = false;
						
						synchronized (queue) {
							if (queue.size() >= 10) {
								for (int i = 0; i < 10; i++) {
									queueData3[i] = queue.poll();
								}
								
								hasData3 = true;
							}
						}
						
						if (hasData3) {
							UIUtil.setMessage(handler, UPDATE_CANVAS, queueData3);
						}
					}
					
			} else if (DataStorageService.ACTION_ECG_DATA_AVAILABLE.equals(action)) {
					synchronized (queue) {				
						int[] ecg = intent.getIntArrayExtra("ECGData");
						
						if (ecg != null) {
							for (int i = 0; i < ecg.length; i++) {
								try {
										filter.addData(((ecg[i] - 28000)));
								} catch (Exception e) {
										e.printStackTrace();
								}							
							}
							for (int i = 0; i < ecg.length; i++) {
								if (filter.canGetOne()) {
									try {
										queue.offer(filter.getOne());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									break;
								}
							}
						}
					}
			}
			else if (BleConnectionService.ACTION_RESPONSE_BLE_STATE.equals(action)) {
				int bleState = intent.getIntExtra(BleConnectionService.BLE_STATE, 
												  BleConnectionService.STATE_UNKOWN
												  );
				setBleState(bleState);
			}
		}
	};
	
	public void showState(int state) {
		if (state == BleConnectionService.STATE_CONNECTED) {
			txtState.setText(NT_CONNECTED);
		}
		else if (state == BleConnectionService.STATE_DISCONNECTED) {
			txtState.setText(NT_DISCONNECTED);
		}
		else if (state == BleConnectionService.STATE_CONNECTING) {
			txtState.setText(NT_CONNECTTING);
		}
		else if (state == BleConnectionService.STATE_SCANNING) {
			txtState.setText(NT_SCANNING);
		}
		else if (state == BleConnectionService.STATE_UNKOWN) {
			txtState.setText(NT_UNKNOWN);
		}
		else {
			// do nothing
		}
	}
	
	private void startServices() {
		if (mIsServiceStarted) { return; }
		else { mIsServiceStarted = true; }
		
		startService(new Intent(MainActivity.this,
				BleConnectionService.class));
	}
	
	private void stopServices() {
		if (!mIsServiceStarted) { return; }
		else { mIsServiceStarted = false; }
		
		stopService(new Intent(MainActivity.this, BleConnectionService.class));
	}
	
	private void resetAlarm() {
		ecgTickCount = 0;
		
		Intent intent =new Intent();  
		intent.setAction(ACTION_ALARM);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
//		am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime + 200, 40, pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, 40, pi);		
	}
}
