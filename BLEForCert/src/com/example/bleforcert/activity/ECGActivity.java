package com.example.bleforcert.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ECGActivity extends Activity {
	public static final int UPDATE_CANVAS = 1;
	private final static int UPDATA_BLE_STATE = 40;
	private final static int SHOW_MAC = 42;
	private final static int SHOW_IS_PAITING_STOPPED = 52;
	private final static int UPDATE_SHOW_EXPlAIN = 60;

	private final static int UPDATE_SPEED = 65;
	private final static int UPDATE_GRAPHIC_STATUS = 66;
	private final static int UPDATE_CURRENT_TIME = 67;

	// private static final String NT_CONNECTED = "设备已连接";
	private static final String NT_CONNECTED = "已连接";
	private static final String NT_CONNECTTING = "正在尝试连接设备...";
	// private static final String NT_DISCONNECTED = "设备未连接";
	private static final String NT_DISCONNECTED = "未连接";
	private static final String NT_SCANNING = "正在搜索设备";
	private static final String NT_UNKNOWN = "未知设备状态";

	private long exitTime = 0;

	// private static final int MAX_POINT = 750;
	private final int UPDATE_NETWORK_DATE = 9;
	Filter filter = new Filter();
	Queue<Integer> queue = new LinkedList<Integer>();
	int pointNumber = 0;
	float[] ECGData = new float[ECGGLSurfaceView.MAX_POINT];
	ECGGLSurfaceView mSurfaceView;
	boolean isECGCanvasInitialized = false;

	private int mBleState = BleConnectionService.STATE_DISCONNECTED;

	private volatile boolean mCapture = false;
	private Object lockCapture = new Object();

	private boolean stopPainting = false;

	private boolean mIsServiceStarted = false;

	private Object lockResetECGView = new Object();

	private static final String ACTION_ALARM = "com.example.bleforcert.MainActivity.ACTION_ALARM";

	BleConnectionNotifiaction mBleConnectionNotifiaction = new BleConnectionNotifiaction(
			this);

	private LinearLayout explain_layout;
	private TextView explainY_tw;
	private TextView explainX_tw;
	private float mYGridValue = 0.0f;
	private boolean mIsExplainShowed = false;

	private String mMac = null;
	private TextView txtMac;

	private String mState = null;
	private TextView txtState;

	private TextView txtSpeed;

	private TextView txtGraphicStatus;
	private TextView txtCurrentTime;

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");

	// 设置ECG控件尺寸
	private int mPixelsPerMm = 0;
	// 默认ECG走纸速度为10mm/s
	private int mEcgMode = ECGGLSurfaceView.ECG_MODE_0;
	private int mTotalEcgPoint = ECGGLSurfaceView.ECG_MODE_0_TOTAL_POINT;

	private static final int GRAPHIC_MODE_INITIAL = 0;
	private static final int GRAPHIC_MODE_STOPPED = 1;
	private static final int GRAPHIC_MODE_STARTED = 2;
	private int graphicStatus = GRAPHIC_MODE_INITIAL;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPDATE_CANVAS) {
				float[] fVal = new float[10];
				int[] queueData = (int[]) msg.obj;

				if ((queueData != null) && (queueData.length >= 10)) {
					for (int i = 0; i < 10; i++) {
						// fVal[i] = ((queueData[i] - 32000) * 6) / 1000f; //for
						// ECG
						fVal[i] = (queueData[i] * 1.0f) / 128; // for ECG
						// fVal[i] = (queueData[i] * 1.0f) / 36550;// 128; //for
						// ECG
					}
					// if (pointNumber <= 740) {
					if (pointNumber <= (mTotalEcgPoint - 10)) {
						if (pointNumber == 0) {
							System.out
									.println("action EcgPainterActivity pointNumber is 0.");
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
					// if (pointNumber >= 750) pointNumber = 750;
					if (pointNumber >= mTotalEcgPoint)
						pointNumber = mTotalEcgPoint;

					mSurfaceView.drawECG(ECGData, pointNumber);
				}
			} else if (msg.what == UPDATA_BLE_STATE) {
				// mBleConnectionNotifiaction.show(mBleState);
				showState(mBleState);
			} else if (msg.what == SHOW_MAC) {
				System.out.println("action MainActivity MAC:" + mMac);
				String showMac = "";
				if (mMac == null || mMac.length() != 12) {
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

					txtMac.setText("MAC:" + showMac);
				}
			} else if (msg.what == UPDATE_SHOW_EXPlAIN) {
			} else if (msg.what == UPDATE_SPEED) {
				int mode = (Integer) msg.obj;

				String prefix = "走纸速度:";
				if (ECGGLSurfaceView.ECG_MODE_0 == mode) {
					txtSpeed.setText(prefix + "10mm/s ");
				} else if (ECGGLSurfaceView.ECG_MODE_1 == mode) {
					txtSpeed.setText(prefix + "20mm/s ");
				}
			} else if (msg.what == UPDATE_GRAPHIC_STATUS) {
				String prefix = "画图状态:";

				int status = (Integer) msg.obj;
				if (GRAPHIC_MODE_INITIAL == status) {
					txtGraphicStatus.setText(prefix + "正在初始化...");
				} else if (GRAPHIC_MODE_STOPPED == status) {
					txtGraphicStatus.setText(prefix + "已停止(点击屏幕开始画图)");
				} else if (GRAPHIC_MODE_STARTED == status) {
					txtGraphicStatus.setText(prefix + "已开始(点击屏幕停止画图)");
				}
			} else if (msg.what == UPDATE_CURRENT_TIME) {
				if (!stopPainting) {
					txtCurrentTime.setText("当前时间:" + sdf.format(new Date()));
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置竖屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_ecg);

		getPixelsPerMm();

		mSurfaceView = (ECGGLSurfaceView) findViewById(R.id.hte_GLSurfaceView_ecg);
		txtMac = (TextView) this.findViewById(R.id.ecg_mac_tw);
		txtState = (TextView) this.findViewById(R.id.ecg_status_tw);
		txtSpeed = (TextView) this.findViewById(R.id.ecg_speed_tw);
		txtGraphicStatus = (TextView) this
				.findViewById(R.id.ecg_graphic_status_tws);
		txtCurrentTime = (TextView) this.findViewById(R.id.ecg_curr_time_tw);

		setGraphicStatus(GRAPHIC_MODE_INITIAL);

		// 设置默认走纸速度为10mm/s
		setEcgMode(ECGGLSurfaceView.ECG_MODE_0);

		mSurfaceView.setCallback(new ECGGLSurfaceView.CanvasReadyCallback() {
			@Override
			public void notifyCanvasReady() {
				isECGCanvasInitialized = true;
				// hideProgressBar();

				setGraphicStatus(GRAPHIC_MODE_STARTED);
			}

			@Override
			public boolean getCapture() {
				synchronized (lockCapture) {
					return mCapture;
				}
			}

			@Override
			public void onCaptured(Bitmap bitmap) {
				// setCapture(false);
				// onECGCaptured(bitmap);
			}

			@Override
			public boolean stopPainting() {
				return stopPainting;
			}

			@Override
			public void onPaintingStopped(float yGridValue) {
				// if (!mIsExplainShowed) {
				// mYGridValue = yGridValue;
				// UIUtil.setMessage(handler, UPDATE_SHOW_EXPlAIN, true);
				// mIsExplainShowed = true;
				// }
			}
		});

		mSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (stopPainting) {
					stopPainting = false;
					// UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED,
					// false);
					// UIUtil.setMessage(handler, UPDATE_SHOW_EXPlAIN, false);
					// if (isECGCanvasInitialized) {
					setGraphicStatus(GRAPHIC_MODE_STARTED);
					// }
					mIsExplainShowed = false;
				} else {
					stopPainting = true;
					// if (isECGCanvasInitialized) {
					setGraphicStatus(GRAPHIC_MODE_STOPPED);
					// }
					// UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED,
					// true);
				}
			}
		});

		initButtons();

		initSurfaceView();

		startServices();
		startAlarm();
	}

	@Override
	protected void onResume() {
		super.onResume();

		filter.reset();
		pointNumber = 0;
		queue.clear();
		// mSurfaceView.reset();
		isECGCanvasInitialized = false;

		mMac = UserInfo.getIntance().getMacAddr();
		UIUtil.setMessage(handler, SHOW_MAC);

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		registerReceiver(mGattUpdateReceiver, makeAlarmIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAlarm();
		stopServices();
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

	private void initSurfaceView() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView
				.getLayoutParams();
		// LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
		// mSurfaceView.getLayoutParams();
		lp.height = 50 * mPixelsPerMm;
		lp.width = 75 * mPixelsPerMm;
		mSurfaceView.setLayoutParams(lp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ecg, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_10mm_per_second:
			setEcgMode(ECGGLSurfaceView.ECG_MODE_0);

			break;
		case R.id.menu_20mm_per_second:
			setEcgMode(ECGGLSurfaceView.ECG_MODE_1);

			break;
		case R.id.select_device_again:
			selectNewDevice();

			break;
		case R.id.quit_app:
			quitApp();

			break;
		}
		return true;
	}

	private void getPixelsPerMm() {
		DisplayMetrics dm = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int widthPixels = dm.widthPixels;
		int heightPixels = dm.heightPixels;
		float density = dm.density;
		// int pixelsPerInchCalcu = (int) density * 160;
		float pixelsPerInchCalcu = density * 160;
		float pixelsPerInch = dm.densityDpi;

		// float scaledDensity = dm.scaledDensity;

		double diagonalPixels = Math.sqrt(Math.pow(widthPixels, 2)
				+ Math.pow(heightPixels, 2));
		// double screenSize
		// = diagonalPixels * 1.0 / (160 * density);
		double screenSize = diagonalPixels * 1.0 / (pixelsPerInchCalcu);

		System.out.println(screenSize);

		// textShow.setWidth(5 * pixPerCm(dm.densityDpi));
		mPixelsPerMm = 10 * pixPerCm(dm.densityDpi);
	}

	private int pixPerCm(double pixelsPerInch) {
		// if (pixelsPerInch <= 125) {
		// return (int) (pixelsPerInch / 2.54f * 0.89f);
		// }
		// else if (pixelsPerInch <= 165) {
		// return (int) (pixelsPerInch / 2.54f * 0.90f);
		// }
		// else if (pixelsPerInch <= 245) {
		// return (int) (pixelsPerInch / 2.54f * 0.91f);
		// }

		return (int) (pixelsPerInch / 2.54f * 0.93f);
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
			// System.out.println("action HeartRateActivity = " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

				setBleState(BleConnectionService.STATE_CONNECTED);

				filter.reset();
				pointNumber = 0;
				queue.clear();
				// mSurfaceView.reset();
				isECGCanvasInitialized = false;

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {

				setBleState(BleConnectionService.STATE_DISCONNECTED);

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (ACTION_ALARM.equals(action)) {
				// System.out.println("action HeartRateActivity ACTION_ALARM queu_size["
				// + queue.size() + "]");
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

				UIUtil.setMessage(handler, UPDATE_CURRENT_TIME);
			} else if (DataStorageService.ACTION_ECG_DATA_AVAILABLE
					.equals(action)) {
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
			} else if (BleConnectionService.ACTION_RESPONSE_BLE_STATE
					.equals(action)) {
				int bleState = intent.getIntExtra(
						BleConnectionService.BLE_STATE,
						BleConnectionService.STATE_UNKOWN);
				setBleState(bleState);
			}
		}
	};

	private void setBleState(int state) {
		mBleState = state;
		UIUtil.setMessage(handler, UPDATA_BLE_STATE);
	}

	private void startAlarm() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		// am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime + 200, 40,
		// pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, 40, pi);

		PowerManager powerManager = ((PowerManager) getSystemService(POWER_SERVICE));
	}

	private void stopAlarm() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(sender);
	}

	private void startServices() {
		if (mIsServiceStarted) {
			return;
		} else {
			mIsServiceStarted = true;
		}

		startService(new Intent(this, BleConnectionService.class));
	}

	private void stopServices() {
		if (!mIsServiceStarted) {
			return;
		} else {
			mIsServiceStarted = false;
		}

		stopService(new Intent(this, BleConnectionService.class));
	}

	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			UIUtil.setToast(this, "再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			stopServices();
			this.finish();
		}
	}

	private void setEcgMode(int mode) {
		mEcgMode = mode;
		mSurfaceView.setEcgMode(mEcgMode);

		synchronized (queue) {
			filter.reset();
			pointNumber = 0;
			queue.clear();
			// mSurfaceView.reset();
			isECGCanvasInitialized = false;
		}

		if (ECGGLSurfaceView.ECG_MODE_0 == mEcgMode) {
			// 走纸速度10mm/s
			mTotalEcgPoint = ECGGLSurfaceView.ECG_MODE_0_TOTAL_POINT;
			UIUtil.setMessage(handler, UPDATE_SPEED,
					ECGGLSurfaceView.ECG_MODE_0);
		} else if (ECGGLSurfaceView.ECG_MODE_1 == mEcgMode) {
			// 走纸速度20mm/s
			mTotalEcgPoint = ECGGLSurfaceView.ECG_MODE_1_TOTAL_POINT;
			UIUtil.setMessage(handler, UPDATE_SPEED,
					ECGGLSurfaceView.ECG_MODE_1);
		} else {
			// 默认走纸速度25mm/s
			mTotalEcgPoint = ECGGLSurfaceView.ECG_MODE_DEFAULT_TOTAL_POINT;
		}
	}

	private void selectNewDevice() {
		stopServices();
		final Intent intent = new Intent(this,
				SampleLeBluetoothDeviceScanActivity.class);
		startActivity(intent);

		this.finish();
	}

	private void quitApp() {
		stopServices();
		this.finish();
	}

	private void initButtons() {
		Button bt10mmPerS = (Button) findViewById(R.id.ecg_10_mm_per_s_bt);
		Button bt20mmPerS = (Button) findViewById(R.id.ecg_20_mm_per_s_bt);
		Button btSelectNewDevice = (Button) findViewById(R.id.ecg_select_new_bt);
		Button btQuitApp = (Button) findViewById(R.id.ecg_quit_bt);

		bt10mmPerS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setEcgMode(ECGGLSurfaceView.ECG_MODE_0);
			}
		});
		bt20mmPerS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setEcgMode(ECGGLSurfaceView.ECG_MODE_1);
			}
		});
		btSelectNewDevice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectNewDevice();
			}
		});
		btQuitApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				quitApp();
			}
		});
	}

	public void showState(int state) {
		String prefix = "设备状态:";
		if (state == BleConnectionService.STATE_CONNECTED) {
			txtState.setText(prefix + NT_CONNECTED);
		} else if (state == BleConnectionService.STATE_DISCONNECTED) {
			txtState.setText(prefix + NT_DISCONNECTED);
		} else if (state == BleConnectionService.STATE_CONNECTING) {
			txtState.setText(prefix + NT_CONNECTTING);
		} else if (state == BleConnectionService.STATE_SCANNING) {
			txtState.setText(prefix + NT_SCANNING);
		} else if (state == BleConnectionService.STATE_UNKOWN) {
			txtState.setText(prefix + NT_UNKNOWN);
		} else {
			// do nothing
		}
	}

	private void setGraphicStatus(int status) {
		graphicStatus = status;
		UIUtil.setMessage(handler, UPDATE_GRAPHIC_STATUS, status);
	}
}
