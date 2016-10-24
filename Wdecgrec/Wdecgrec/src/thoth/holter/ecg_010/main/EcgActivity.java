package thoth.holter.ecg_010.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.broadchance.ecgview.ECGGLSurfaceView;
import com.broadchance.ecgview.ECGGLSurfaceView.EcgLevel;
import com.broadchance.ecgview.ECGGLSurfaceView.EcgType;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.utils.BleDataUtil;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FilterUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;

import thoth.holter.ecg_010.R;
import thoth.holter.ecg_010.manager.DataManager;
import thoth.holter.ecg_010.manager.FrameDataMachine;
import thoth.holter.ecg_010.manager.PlayerManager;
import thoth.holter.ecg_010.manager.SettingsManager;
import thoth.holter.ecg_010.services.BleDataParserService;
import thoth.holter.ecg_010.services.BleDomainService;
import thoth.holter.ecg_010.services.BluetoothLeService;
import thoth.holter.ecg_010.services.GuardService;

public class EcgActivity extends BaseActivity {
	/**
	 * 第一通道
	 */
	private ECGGLSurfaceView ecgGLSurfaceViewChannelMII;
	// private RadioGroup rgEcgSpeed;
	// private RadioGroup rgEcgRage;
	/**
	 * 当前速度
	 */
	private View rbSpeed;
	/**
	 * 当前幅度
	 */
	private View rbRage;
	private TextView ecg_curhearrate;
	private TextView ecg_curspeedvalue;
	private TextView tvCurDate;
	private TextView tvCurTime;
	private TextView tvRunTime;
	private View buttonTitleBack;

	private final static int GRID_VNUM_20 = 20;
	private final static int GRID_VNUM_30 = 30;
	private final static int GRID_VNUM_60 = 60;
	public static final int UPDATE_MIICANVAS = 1;
	// private int mEcgMode = ECGGLSurfaceView.ECG_MODE_LOW;

	private LinkedBlockingQueue<Integer> miiQueue = new LinkedBlockingQueue<Integer>();
	long totalReceivePoints = 0;
	private ScheduledExecutorService executor;
	private ExecutorService executorReceive;
	protected static final String TAG = EcgActivity.class.getSimpleName();
	public static EcgActivity Instance;
	/**
	 * 心率
	 */
	private int hearRate = 0;
	// int tempMax;
	// int tempMin;
	// int tempSrcMax;
	// int tempSrcMin;
	long lastTime = 0;
	long lastMIITime = 0;
	// 上一次点击脱落时间
	long lastMIIOffTime = 0;
	// private FilterUtil filter = FilterUtil.Instance;
	private boolean isStopMII = false;
	// private Object handlerLock = new Object();
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// synchronized (handlerLock) {
			try {
				if (msg.what == UPDATE_MIICANVAS) {
					EcgData data = (EcgData) msg.obj;
					// ecgGLSurfaceViewChannelMII.drawECG(data.queueArray,
					// data.pointNumber);
					ecgGLSurfaceViewChannelMII.drawECG(data.queueArray);
					// setHeartRate();
				}
			} catch (Exception e) {
				LogUtil.e(TAG, e);
			}
			// }
		}
	};
	private SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-ddHH:mm:ss.SSS");
	private Handler handlerTime = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == BleDomainService.MSG_SET_HEART) {
				hearRate = msg.getData().getInt("heart");
				if (ConstantConfig.Debug) {
					ecg_curhearrate.setText(hearRate + "");
					if (runTime != null) {
						long time = new Date().getTime() - runTime.getTime();
						int hours = (int) (time / (1000 * 60 * 60));
						int min = (int) (time % (1000 * 60 * 60) / (1000 * 60));
						int sec = (int) (time % (1000 * 60 * 60) % (1000 * 60) / 1000);
						tvRunTime.setText(hours + ":" + min + ":" + sec);
					}
				} else {
					if (hearRate >= ConstantConfig.Alert_HR_Down
							&& hearRate <= ConstantConfig.Alert_HR_Up) {
						ecg_curhearrate.setText(hearRate + "");
					} else {
						// ecg_curhearrate.setText("0  ");
						if (ConstantConfig.Debug) {
							LogUtil.w(TAG, "当前心率" + hearRate);
						}
						ecg_curhearrate.setText("-");
					}
				}
			}
		}
	};
	private Messenger mMesg = new Messenger(handlerTime);
	public static long count = 0;
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				miiQueue.clear();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				if (ConstantConfig.Debug) {
					ecgGLSurfaceViewChannelMII.clearDraw();
				}
			} else if (BleDataParserService.ACTION_ECGMII_DATA_AVAILABLE
					.equals(action)) {
				synchronized (miiQueue) {
					int[] ecgData = intent
							.getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
					receiveEcgData(R.id.ecgGLSurfaceViewChannelMII, ecgData);
				}
				// if (ConstantConfig.Debug) {
				// Log.d(ConstantConfig.DebugTAG,
				// TAG + "\nmiiQueue:" + miiQueue.size());
				// }
			} else if (BleDataParserService.ACTION_ECGMV1_DATA_AVAILABLE
					.equals(action)) {
				// synchronized (mv1Queue) {
				// int[] ecgData = intent
				// .getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
				// receiveEcgData(R.id.ecgGLSurfaceViewChannelMV1, ecgData);
				// }
			} else if (BleDataParserService.ACTION_ECGMV5_DATA_AVAILABLE
					.equals(action)) {
				// executorReceive.execute(new Runnable() {
				// @Override
				// public void run() {
				// synchronized (mv5Queue) {
				// int[] ecgData = intent
				// .getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
				// receiveEcgData(R.id.ecgGLSurfaceViewChannelMV5,
				// ecgData);
				// }
				// }
				// });

			} else if (FrameDataMachine.ACTION_ECGMII_DATAOFF_AVAILABLE
					.equals(action)) {
				if (System.currentTimeMillis() - lastMIIOffTime > 5000) {
					PlayerManager.getInstance().playDevFallOff();
					showToast("MⅡ电极脱落");
					lastMIIOffTime = System.currentTimeMillis();
				}
			} else if (FrameDataMachine.ACTION_ECGMV1_DATAOFF_AVAILABLE
					.equals(action)) {
				PlayerManager.getInstance().playDevFallOff();
				showToast("MV1电极脱落");
			} else if (FrameDataMachine.ACTION_ECGMV5_DATAOFF_AVAILABLE
					.equals(action)) {
				PlayerManager.getInstance().playDevFallOff();
				showToast("MV5电极脱落");
			}
		}
	};

	private void receiveEcgData(int viewID, int[] ecg) {
		int totalEcg;
		Queue<Integer> queue = null;
		switch (viewID) {
		case R.id.ecgGLSurfaceViewChannelMII:
			totalEcg = ecgGLSurfaceViewChannelMII.getCurrTotalPointNumber();
			queue = miiQueue;
			break;
		default:
			return;
		}
		if (ecg != null) {
			for (int i = 0; i < ecg.length; i++) {
				try {
					Integer data = ecg[i];
					queue.offer(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			int length = queue.size();

			// 防止队列无限增大，检查队列是否超过最大允许上限，如果超出则移除老数据
			// int maxLength = totalEcg * 3;
			int maxLength = (int) Math.max(1000, totalEcg * 1.8f);
			if (length > maxLength) {
				length = (int) (totalEcg * 0.5f);
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "超过最大" + maxLength + " 抛掉" + length);
				}
				// 删除一个周期保留三个周期缓冲
				for (int j = 0; j < length; j++) {
					queue.poll();
				}
			}
		}
	}

	class EcgData {
		public Integer[] queueArray;
		public int pointNumber;
	}

	private void drawEcgData(int viewID) {
		// long useTime = System.currentTimeMillis();
		ECGGLSurfaceView ecgglSurfaceView = null;
		Queue<Integer> queue = null;
		int action;
		long msdif = 0;
		switch (viewID) {
		case R.id.ecgGLSurfaceViewChannelMII:
			if (isStopMII)
				return;
			ecgglSurfaceView = ecgGLSurfaceViewChannelMII;
			queue = miiQueue;
			if (queue.size() < ecgglSurfaceView.getCurrTotalPointNumber() * 0.5f) {
				lastMIITime = -1;
				return;
			}
			if (lastMIITime > 0) {
				msdif = System.currentTimeMillis() - lastMIITime;
			}
			lastMIITime = System.currentTimeMillis();
			action = UPDATE_MIICANVAS;
			// setHeartRate();
			break;
		default:
			return;
		}

		int pointNumber = (int) Math.floor(msdif
				* FrameDataMachine.FRAME_DOTS_FREQUENCY * 0.001f);
		pointNumber = Math.min(pointNumber, queue.size());
		Integer[] queueArray = new Integer[pointNumber];
		for (int j = 0; j < pointNumber; j++) {
			queueArray[j] = queue.poll();
		}
		EcgData data = new EcgData();
		data.queueArray = queueArray;
		UIUtil.setMessage(handler, action, data);
	}

	private void setHeartRate() {
		handlerTime.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 小于20大于200的异常心率值，过滤一下，让不要显示
				// if (GuardService.Instance != null)
				// GuardService.Instance.getHeartRate(mMesg);

				hearRate = FilterUtil.Instance.getHeartRate();
				if (ConstantConfig.Debug) {
					ecg_curhearrate.setText(hearRate + "");
					if (runTime != null) {
						long time = new Date().getTime() - runTime.getTime();
						int hours = (int) (time / (1000 * 60 * 60));
						int min = (int) (time % (1000 * 60 * 60) / (1000 * 60));
						int sec = (int) (time % (1000 * 60 * 60) % (1000 * 60) / 1000);
						// tvRunTime.setText(hours + ":" + min + ":" + sec);
						tvRunTime.setText((Integer.parseInt(CommonUtil
								.getCurCpuFreq()) / 1000f) + "MHZ ");
					}
				} else {
					if (hearRate >= ConstantConfig.Alert_HR_Down
							&& hearRate <= ConstantConfig.Alert_HR_Up) {
						ecg_curhearrate.setText(hearRate + "");
					} else {
						// ecg_curhearrate.setText("0  ");
						if (ConstantConfig.Debug) {
							LogUtil.w(TAG, "当前心率" + hearRate);
						}
						ecg_curhearrate.setText("-");
					}
				}

				setHeartRate();
			}
		}, 2000);
	}

	// private static final long SCAN_PERIOD = 3000;
	// private boolean mScanning;
	// private Handler mHandler = new Handler();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager mBluetoothManager;
	private Date runTime;

	// private BluetoothAdapter.LeScanCallback mLeScanCallback = new
	// BluetoothAdapter.LeScanCallback() {
	// @Override
	// public void onLeScan(final BluetoothDevice device, int rssi,
	// byte[] scanRecord) {
	// UIUserInfoLogin user = DataManager.getUserInfo();
	// if (user != null) {
	// String deviceNumber = user.getMacAddress();
	// if (deviceNumber.equals(device.getAddress())) {
	// UIUtil.showToast(EcgActivity.this, "扫描到指定蓝牙");
	// }
	// }
	// }
	// };

	/**
	 * 扫描蓝牙设备
	 * 
	 * @param enable
	 */
	// public void scanLeDevice() {
	// // BluetoothManager.
	// if (mBluetoothManager == null) {
	// mBluetoothManager = (BluetoothManager)
	// getSystemService(Context.BLUETOOTH_SERVICE);
	// if (mBluetoothManager == null) {
	// return;
	// }
	// }
	// mBluetoothAdapter = mBluetoothManager.getAdapter();
	// if (mBluetoothAdapter == null) {
	// return;
	// }
	// if (!mScanning) {
	// // Stops scanning after a pre-defined scan period.
	// mHandler.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// LogUtil.i(TAG, " stop scanLeDevice ");
	// mScanning = false;
	// mBluetoothAdapter.stopLeScan(mLeScanCallback);
	// UIUtil.showToast(EcgActivity.this, "扫描超时");
	// }
	// }, SCAN_PERIOD);
	// mScanning = true;
	// mBluetoothAdapter.startLeScan(mLeScanCallback);
	// } else {
	// mScanning = false;
	// mBluetoothAdapter.stopLeScan(mLeScanCallback);
	// }
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_ecg);
		Instance = this;
		runTime = CommonUtil.getDate();
		// executorReceive = Executors.newCachedThreadPool();
		ecgGLSurfaceViewChannelMII = (ECGGLSurfaceView) findViewById(R.id.ecgGLSurfaceViewChannelMII);
		// 修改为限定单通道
		ecgGLSurfaceViewChannelMII.initView(EcgActivity.this, GRID_VNUM_30);
		ecgGLSurfaceViewChannelMII.setOnClickListener(EcgActivity.this);
		ecg_curhearrate = (TextView) findViewById(R.id.ecg_curhearrate);
		ecg_curspeedvalue = (TextView) findViewById(R.id.ecg_curspeedvalue);
		buttonTitleBack = (View) findViewById(R.id.buttonTitleBack);
		buttonTitleBack.setOnClickListener(this);
		findViewById(R.id.ecgSpeedLevel1).setOnClickListener(this);
		findViewById(R.id.ecgSpeedLevel2).setOnClickListener(this);
		findViewById(R.id.ecgSpeedLevel3).setOnClickListener(this);
		findViewById(R.id.ecgRangeLevel1).setOnClickListener(this);
		findViewById(R.id.ecgRangeLevel2).setOnClickListener(this);
		findViewById(R.id.ecgRangeLevel3).setOnClickListener(this);
		findViewById(R.id.ecgRangeLevel4).setOnClickListener(this);
		tvCurDate = (TextView) findViewById(R.id.tvCurDate);
		tvCurTime = (TextView) findViewById(R.id.tvCurTime);
		tvRunTime = (TextView) findViewById(R.id.tvRunTime);
		findViewById(R.id.lbl2).setVisibility(View.INVISIBLE);
		tvRunTime.setVisibility(View.INVISIBLE);

		setDefault();
		// UIUserInfoLogin user = DataManager.getUserInfo();
		if (DataManager.isLogin()) {
			String deviceNumber = DataManager.getUserInfo().getMacAddress();
			((TextView) findViewById(R.id.tvMacAddress)).setText(deviceNumber);
		}
		if (ConstantConfig.Debug) {
			View ecg_heartrate = null;
			ecg_heartrate = findViewById(R.id.ecg_curhearrate);
			ecg_heartrate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LayoutInflater inflater = (LayoutInflater) EcgActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					LinearLayout layout = (LinearLayout) inflater.inflate(
							R.layout.dialog_input_test, null);
					final EditText editTextValue = (EditText) layout
							.findViewById(R.id.editTextValue);
					editTextValue.setText(ECGGLSurfaceView.BASEFACTOR + "");
					final EditText editTextGridXValue = (EditText) layout
							.findViewById(R.id.editTextGridXValue);
					editTextGridXValue.setText(Integer
							.toHexString(ECGGLSurfaceView.gridLightColor));
					final EditText editTextGridDValue = (EditText) layout
							.findViewById(R.id.editTextGridDValue);
					editTextGridDValue.setText(Integer
							.toHexString(ECGGLSurfaceView.gridDarkColor));
					final EditText editTextECGValue = (EditText) layout
							.findViewById(R.id.editTextECGValue);
					editTextECGValue.setText(Integer
							.toHexString(ECGGLSurfaceView.ecgLineColor));

					float xDPI = SettingsManager.getInstance().getDpiConfigX();
					float yDPI = SettingsManager.getInstance().getDpiConfigY();
					DisplayMetrics outMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getRealMetrics(
							outMetrics);
					xDPI = xDPI <= 0 ? outMetrics.xdpi : xDPI;
					yDPI = yDPI <= 0 ? outMetrics.ydpi : yDPI;

					final EditText editTextXDPI = (EditText) layout
							.findViewById(R.id.editTextXDPI);
					editTextXDPI.setText(xDPI + "");
					final EditText editTextYDPI = (EditText) layout
							.findViewById(R.id.editTextYDPI);
					editTextYDPI.setText(yDPI + "");
					final EditText editTextURL = (EditText) layout
							.findViewById(R.id.editTextURL);
					editTextURL.setText("" + ConstantConfig.SERVER_URL);

					Button buttonOK = (Button) layout
							.findViewById(R.id.buttonOK);
					final Dialog dialog = UIUtil.buildDialog(EcgActivity.this,
							layout);
					buttonOK.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								ECGGLSurfaceView.BASEFACTOR = Float
										.parseFloat(editTextValue.getText()
												.toString());
								ECGGLSurfaceView.gridLightColor = Color
										.parseColor("#"
												+ editTextGridXValue.getText()
														.toString().trim());
								ECGGLSurfaceView.gridDarkColor = Color
										.parseColor("#"
												+ editTextGridDValue.getText()
														.toString().trim());
								ECGGLSurfaceView.ecgLineColor = Color
										.parseColor("#"
												+ editTextECGValue.getText()
														.toString().trim());
								SettingsManager.getInstance().setDpiConfigX(
										Float.parseFloat(editTextXDPI.getText()
												.toString()));
								SettingsManager.getInstance().setDpiConfigY(
										Float.parseFloat(editTextYDPI.getText()
												.toString()));
								SettingsManager.getInstance().setServerURL(
										editTextURL.getText().toString());
								ecgGLSurfaceViewChannelMII.initView(
										EcgActivity.this, GRID_VNUM_30);
								ecgGLSurfaceViewChannelMII.setEcgMode(
										EcgType.Range, EcgLevel.Level2);
								ecgGLSurfaceViewChannelMII.requestRender();
								dialog.cancel();
								dialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					dialog.show();
				}
			});
		}
		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getNickName());
	}

	private void setDefault() {
		rbSpeed = findViewById(R.id.ecgSpeedLevel2);
		rbRage = findViewById(R.id.ecgRangeLevel2);
		onClick(rbSpeed);
		onClick(rbRage);
	}

	private BroadcastReceiver testReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("test")) {

			}
		}
	};
	Handler testHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = new Intent(BluetoothLeService.ACTION_DATA_AVAILABLE);
			intent.putExtra(BluetoothLeService.EXTRA_DATA, (byte[]) msg.obj);
			sendBroadcast(intent);
		}

	};
	int hexIndex = 0;
	int chkCount = 0;
	static String[] ecgDataAs;

	// static {
	// String ecgDataA =
	// "212,212,96,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,52,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,96,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,68,52,44,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,52,48,48,56,48,52,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,124,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,52,48,48,48,48,48,48,48,48,48,48,52,48,52,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,156,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,184,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,188,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,52,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,204,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,160,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,52,48,48,52,48,48,48,48,48,48,48,52,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,132,48,48,48,48,48,52,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,64,212,212,212,212,212,212,212,212,212,208,212,212,208,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,208,212,208,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,100,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,88,212,212,212,212,208,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,76,52,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,52,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,52,48,48,48,48,48,48,48,52,48,48,48,48,52,120,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,208,212,212,56,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,152,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,180,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,208,212,208,212,212,208,212,212,212,212,208,212,208,212,212,208,212,212,212,212,200,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,188,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,196,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,192,212,208,212,208,212,212,212,212,208,212,212,212,212,208,212,208,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,208,212,212,212,212,208,212,212,212,212,208,212,212,192,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,200,208,212,212,212,212,208,212,212,208,212,208,212,212,208,212,208,212,212,208,212,208,212,212,208,212,208,212,212,208,212,208,212,212,212,212,208,212,212,208,212,208,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,188,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,204,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,184,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,204,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,176,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,172,48,48,48,48,52,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,168,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,160,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,156,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,48,48,52,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,148,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,140,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,208,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,136,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,52,48,48,48,48,52,56,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212,212";
	// ecgDataAs = ecgDataA.split(",");
	// StringBuffer stringBuffer = new StringBuffer();
	// for (int i = 0; i < ecgDataAs.length; i++) {
	// if (i % 10 == 0 && i > 0) {
	// LogUtil.d("BLEECGDEMODATA",
	// (i / 10) + "\t    " + stringBuffer.toString());
	// stringBuffer = new StringBuffer();
	// }
	// stringBuffer.append(ecgDataAs[i] + "\t ");
	// }
	// }

	void test() {
		IntentFilter testFilter = new IntentFilter();
		testFilter.addAction("test");
		registerReceiver(testReceiver, testFilter);
		// if (BleDataParserService.getInstance() != null)
		// BleDataParserService.getInstance().clearData();
		ScheduledExecutorService testExecutorService = Executors
				.newScheduledThreadPool(1);
		String ecgDataStr = "80960350 3501800d 00c74400 c00c00c0 0c00c589 809800c0 0c00c00c 00c02f00 c00c00c0 0c00c4f6 809a00c0 0c00c00d 00c09500 c00c00c0 0c00c4b9 809c00c0 0c00c00c 00c0b600 c00c00c0 0d00c4a2 809e00c0 0c00c00d 00c0ac00 c00c00c0 0d00c4d1 80a000c0 0c00c00d 00c05000 c00c00c0 0d00c566 80a200c0 0c00c00c 01877c03 50350350 3503466c 80a40350 35035035 03464e03 50350350 350347ba 80a60350 35035035 0344dc03 50350350 35034146 80a80350 35035035 03433503 50350350 350343d9 80aa0350 35035035 03474703 50350350 35034075 80ac0350 35035035 0342ec03 50350350 3503441b 80ae0350 35035035 0347e203 50350110 0d00b66f 80b000c0 0c00c00c 00c63e00 c00c00c0 0d00c7a0 80b200c0 0c00c00d 00c56000 c00d00c0 0c00e04f 80b400c0 0d00c00c 00c4e100 c00c00c0 0d00c09f 80b600c0 0c00c00c 00c4ac00 c00c00c0 0d00c0be 80b800c0 0c00c00d 00c4a700 c00c00c0 0c00c0a4 80ba00c0 0c00c00c 00c4fe00 c00c00c0 0d01f7dd 80bc0350 35035035 03568003 50350350 350345b5 80be0350 35035035 03512303 50350350 3503525f 80c00350 35035035 0354d203 50350350 35035688 80c20350 35035035 0350e503 50350350 350352ae 80c40350 35035035 03546d03 50350350 350357bc 80c60350 35035035 03568103 50350350 35035648 80c80350 3500d00c 00c79c00 c00c00c0 0c00c56d 80ca00c0 0c00c00d 00c03e00 d00c00c0 0c00c4f6 80cc00c0 0c00c00c 00c09000 c00c00c0 0d00c4bd 80ce00c0 0c00c00d 00c0b400 c00c00c0 0c00c4a9 80d000c0 0c00c00d 00c0bd00 c00c00c0 0c00c4b9 80d200c0 0c00c00c 00c08300 c00c00c0 0c00c548 80d400c0 0c00c00c 02778303 50350350 350356d3 80d60350 35035035 03558503 50350350 35035142 80d80350 34035035 03529e03 50350350 35035464 80da0350 35035035 03575c03 50350350 35035006 80dc0350 35035035 0353ac03 50350350 35035341 80de0350 35035035 0350d503 50350350 350355c0 80e00350 35035035 0356c703 503400c0 0c00c732 80e200c0 0c00c00c 00c5b800 c00c00c0 0d00c7fe 80e400c0 0c00c00c 00c52900 c00c00c0 0c00c069 80e600c0 0c00c00d 00c4d800 d00c00c0 0c00c0a7 80e800c0 0c00c00c 00c4b200 c00c00c0 0c00c0bb 80ea00c0 0c00c00c 00c4a400 c00c00c0 0c00c0a9 80ec00d0 0c00c00c 00c4ec00 c00c00c0 0c02e033 80ee0350 34035035 0355e403 50350350 350356cb 80f00350 35035035 0357b803 50340350 35035494 80f20350 35035035 03525003 50350350 3503519a 80f40350 35035035 03556103 50350350 35035631 80f60350 35035035 03513d03 50350350 350351e0 80f80350 35035035 03552d03 50350350 3503572c 80fa0350 2f00c00c 00c70900 d00c00c0 0c00c5cd 80fc00c0 0c00c00c 00c00600 d00c00c0 0c00c511 80fe00c0 0c00c00c 00c08000 d00c00c0 0c00c4d1 800000d0 0c00d00c 00c0a400 d00c00c0 0c00c4b7 800200d0 0c00c00c 00c0b700 c00c00c0 0c00c4a8 800400d0 0c00c00c 00c0c000 d00c00c0 0c00c4a8 800600c0 0c00c00c 0330c403 50340350 350354c0 80080350 34035035 03508f03 50340350 35035528 800a0350 34035035 03579c03 50340350 350356a8 800c0350 35035035 03558703 50350350 35035134 800e0350 34035035 03528803 50340350 350354b2 80100350 35035035 0356f703 50340350 3503504b 80120350 35035035 03534803 502800c0 0c00c3b9 801400d0 0c00c00c 00c06a00 c00c00c0 0c00c5ef 801600c0 0c00c00c 00c6aa00 d00c00c0 0c00c769 801800d0 0c00c00c 00c58900 c00c00c0 0c00c023 801a00c0 0c00c00c 00c51400 d00c00c0 0c00c084 801c00c0 0c00c00c 00c4c700 c00c00d0 0c00c0b0 801e00d0 0c00c00c 00c4b200 c00c00c0 0d0350cb 80200350 35035035 0354ab03 50340350 350350b1 80220350 35035035 0354db03 50350350 3503503d 80240350 35035035 0355a503 50350350 35035719 80260350 35035035 03571103 50340350 3503557f 80280350 35035035 03510003 50350350 3503531f 802a0350 35035034 0353da03 50350350 350357d8 802c0350 2100c00c 00c71600 c00c00d0 0c00c4c6 802e00d0 0c00c00c 00c23900 c00c00c0 0c00c1c6 803000c0 0c00c00c 00c4d600 c00c00c0 0c00c7a5 803200c0 0c00c00c 00c66800 c00c00c0 0c00c662 803400c0 0c00c00c 00c78100 c00c00c0 0c00c596 803600c0 0c00c00c 00c02600 c00c00c0 0c00c513 803800c0 0c00c010 03509003 50350350 350354ce 803a0350 35035034 0350b503 50340350 350354af 803c0350 35035034 0350c503 50350350 340354b9 803e0350 35035034 03509003 50350350 3403553f 80400350 34035034 03578103 50350350 350356ed 80420350 35035034 03555103 50350350 34035167 80440350 35035035 03529f03 501900c0 0c00c407 804600c0 0c00c00c 00c7e600 c00c00c0 0c00c709 804800c0 0c00d00c 00c53300 c00c00c0 0c00c182 804a00c0 0c00c00c 00c2c900 c00c00d0 0c00c3c6 804c00c0 0c00d00c 00c0a800 c00c00c0 0c00c5a4 804e00c0 0c00d00c 00c71400 c00c00c0 0c00c6d6 805000c0 0c00d00c 00c63000 c00c00c0 160357ad 80520350 35035034 03557a03 50350350 35035036 80540350 35035034 03551a03 50350350 3403507c 80560350 35035035 0354d403 50350350 340350b2 80580350 35035034 0354b203 50350350 340350c4 805a0350 35035034 0354b103 50350350 3503509d 805c0350 35035035 03552403 50350350 340357b3 805e0350 1300d00c 00c6a500 c00c00d0 0c00c5fa 806000c0 0c00d00c 00c00f00 c00c00c0 0c00c4d1 806200c0 0c00d00c 00d13000 c00c00d0 0c00c38e 806400c0 0c00c00c 00c29a00 c00c00d0 0c00c236 806600c0 0c00c00c 00c3c900 c00c00d0 0c00d0f0 806800c0 0c00c00c 00c54300 c00c00d0 0c00c6e1 806a00c0 0c00d01e 03501f03 50350350 35035406 806c0350 35035035 0352b703 50350350 350351dd 806e0350 35035035 0354ab03 40350350 35035000 80700340 35035035 03562903 50350350 3503569c 80720340 35035035 03574003 50350350 350355e1 80740350 35035035 0357e203 40350350 3503555f 80760350 35035034 03504a03 500e00c0 0c00c502 807800c0 0c00c00c 00c08e00 c00c00c0 0c00c4ca 807a00c0 0c00c00c 00c0b900 c00c00c0 0c00c4b4 807c00c0 0c00c00c 00d0c900 c00c00c0 0c00c4b0 807e00c0 0c00c00c 00c0b100 c00c00c0 0c00c4e2 808000c0 0c00c00c 00c05400 c00c00c0 0c00d58a 808200c0 0c00c00c 00c73b00 c00c00c0 26035711 80840340 35035035 03555703 40350350 3503516b 80860340 35035035 0352f003 40350350 350353c8 80880340 35035035 03507103 40350350 35035662 808a0340 35035035 0355bd03 40350350 35035111 808c0340 35035035 03532b03 40350350 35035367 808e0340 35035035 03511b03 40350350 35035534 80900340 0c00c00c 00d79300 c00c00c0 0c00c68c 809200c0 0c00c00c 00d66600 c00c00c0 0c00c76e 809400c0 0c00c00c 00c5b900 c00c00c0 0c00c7ff 809600c0 0c00c00c 00c53e00 c00c00c0 0c00d05c 809800c0 0c00c00c 00c4fb00 c00c00c0 0c00d094 809a00c0 0c00c00c 00c4ce00 c00c00c0 0c00c0b5 809c00c0 0c00c02d 0354b903 50350350 350350c5 809e0340 35035035 0354ad03 50350350 350350c1 80a00350 35035035 0354c403 40350350 3503507f 80a20340 35035035 03555403 40350350 350357a2 80a40350 35035035 03569903 40350340 35035600 80a60340 35035035 03505603 40350340 3503543b 80a80340 35035035 03525303 200c00c0 0c00c1ee 80aa00c0 0c00c00c 00c48600 c00c00c0 0c00c779 80ac00c0 0c00c00c 00c73d00 c00c00c0 0c00c4ec 80ae00c0 0c00c00c 00c21400 c00c00c0 0c00c237 80b000c0 0d00c00c 00c44200 c00c00c0 0c00d02b 80b200c0 0c00c00c 00c60800 c00c00c0 0c00c6cb 80b400c0 0c00c00c 00d71700 c00c00c0 2f035605 80b60350 35035035 0357d403 50350340 3503554e 80b80350 35035035 03505a03 50350350 350354f2 80ba0350 35035035 03509d03 50350340 350354bf 80bc0350 35034035 0350bf03 50350340 350354ae 80be0350 35034035 0350c003 50350340 350354ac 80c00350 35034035 0350b503 50350340 350354c2 80c20310 0d00c00c 00c09d00 c00d00c0 0c00c4db 80c400c0 0c00c00c 00c08100 c00d00c0 0c00c4f8 80c600c0 0d00c00c 00c05f00 c00c00c0 0c00c522 80c800c0 0d00c00c 00c03800 c00d00c0 0c00c592 80ca00c0 0c00c00c 00c76400 c00d00c0 0c00c6a4 80cc00c0 0c00c00c 00c61900 c00c00c0 0c00c02b 80ce00c0 0d00c030 03549903 40350340 3503519a 80d00350 35034035 0352f603 50350340 35034394 80d20350 35034035 03508a03 50350340 3503563d 80d40350 35034035 03565103 50350340 35035050 80d60350 35034035 0353ec03 50350350 350352b6 80d80350 35034035 0351a903 50350340 350354ab 80da0350 35034035 0357de03 000d00c0 0c00c62b 80dc00c0 0c00c00c 00c6ac00 c00c00c0 0c00c736 80de00c0 0c00c00c 00c5d400 c00c00c0 0c00c7e1 80e000c0 0c00c00c 00c55000 c00c00c0 0c00c044 80e200c0 0c00c00c 00c50300 c00c00c0 0c00c082 80e400c0 0c00c00c 00c4c900 c00c00c0 0c00c0a7 80e600c0 0c00c00c 00c4b200 c00d00c0 320340be 80e80350 35035035 0344a903 50350340 350340bc 80ea0350 35034035 0344b403 50350340 3503409c 80ec0350 35034035 0344ee03 50350340 3503402b 80ee0350 35035035 03458e03 50350340 35034772 80f00350 35035035 03468a03 50350350 3503564d 80f20350 35035035 0357e803 50350340 350354a1 80f402f0 0c00c00c 00c20400 c00c00c0 0c00c24a 80f600c0 0c00c00c 00c47800 c00c00c0 0c00c7a3 80f800c0 0c00c00c 00c76700 c00c00c0 0c00c49b 80fa00c0 0c00c00c 00c21600 c00c00c0 0d00c257 80fc00c0 0c00c00c 00c40a00 c00c00c0 0c00c092 80fe00c0 0c00c00d 00c59600 c00c00c0 0c00c731 800000c0 0c00c033 0356c703 50350350 35035625 80020350 35035035 0357a003 50350350 3503558d 80040350 35035035 03501a03 50350350 35035522 80060350 35035035 03506f03 50350350 350354db 80080350 35035035 0350a003 50350350 350354bd 800a0350 35035035 0350b303 50350350 350354ae 800c0350 35035035 0350bd02 e00c00c0 0c00c4a8 800e00c0 0c00c00c 00c0ba00 c00c00c0 0c00c4bb 801000c0 0c00c00d 00c09300 c00c00c0 0c00c509 801200c0 0c00c00c 00c01b00 c00c00c0 0c00c5a0 801400c0 0c00c00c 00c75400 c00c00c0 0d00c692 801600c0 0c00c00c 00c62200 c00c00c0 0c00c07f 801800c0 0c00c00c 00c39500 d00c00c0 3303535e 801a0350 35035035 03507d03 50350350 3503568e 801c0350 35035035 03551803 50350350 3503522d 801e0350 35035035 0351e303 50350350 3503544b 80200350 35035035 0350c903 50350350 350354ea 80220350 35035035 03500703 50350350 350355bd 80240350 35035035 03576503 50350350 35035632 802602c0 0c00c00c 00c71100 c00c00c0 0c00c693 802800d0 0c00c00c 00c69a00 c00c00c0 0c00c72d 802a00d0 0c00c00c 00c5ff00 c00c00c0 0c00c7a0 802c00c0 0c00c00c 00c59a00 c00c00c0 0c00c006 802e00c0 0c00c00c 00c54300 c00c00c0 0c00c04f 803000c0 0c00c00c 00c50400 c00c00c0 0c00c07d 803200c0 0c00c034 0354e303 50350350 3503509f 80340350 35035035 0354c403 50350350 350350b4 80360350 35035035 0354b603 50350350 350350bf 80380350 35035035 0354a803 50350350 350350c4 803a0350 35035035 0354ae03 50350350 350350b4 803c0350 35035035 0354c803 50350350 3503506a 803e0350 35035035 03555f02 b00c00c0 0c00c77a 804000d0 0c00c00c 00c6aa00 c00c00c0 0c00c5e3 804200c0 0c00c00c 00c0a200 c00c00c0 0c00c3a4 804400c0 0c00c00c 00c32700 c00c00c0 0c00c0cc 804600c0 0c00c00c 00c61b00 c00c00c0 0c00c5aa 804800c0 0c00c00c 00c11800 c00c00c0 0c00c2bd 804a00c0 0c00c00c 00c3db00 c00c00c0 340350c9 804c0350 35035035 03554f03 50350350 35035773 804e0350 35035035 03568803 50350350 3503568b 80500350 35035035 03574e03 50350350 350355d4 80520350 35035035 0357dc03 50350350 35035553 80540350 35035035 03504303 50350350 35035511 80560350 35035035 03507703 50350350 350354e3 805802a0 0c00c00c 00c0a000 c00c00c0 0c00c4c3 805a00c0 0c00c00c 00c0b900 c00c00c0 0c00c4b0 805c00c0 0c00c00c 00c0c800 c00c00c0 0c00c4ac 805e00c0 0c00c00c 00c0bf00 c00c00c0 0c00c4d0 806000c0 0c00c00c 00c07300 c00c00c0 0c00c54d 806200c0 0c00c00c 00c7af00 c00c00c0 0c00c672 806400c0 0c00d034 03561d03 50350350 3503504f 80660350 35035035 03540f03 50350350 3503529e 80680350 35035035 0351a003 50350350 3503554a 806a0350 35035035 03570c03 50350350 350357d6 806c0350 35035035 03547603 50350350 3403526a 806e0350 35035035 03521a03 50350350 34035443 80700350 35035035 03506602 800c00c0 0c00c5a2 807200c0 0c00c00c 00c75600 c00c00c0 0c00c697 807400c0 0c00c00c 00c67a00 c00c00c0 0c00c758 807600c0 0c00c00c 00c5c200 c00c00c0 0c00c006 807800c0 0c00c00c 00c53200 c00c00c0 0c00c073 807a00c0 0c00c00c 00c4e500 c00c00c0 0c00c0a8 807c00c0 0c00c00c 00c4c000 c00c00d0 340350c3 807e0350 35035035 0354ad03 50350350 350350c3 80800350 35035035 0354ae03 50350350 350350c3 80820350 35035035 0354ac03 50350350 350350c7 80840350 35035035 0354ae03 50350350 350350c2 80860350 35035035 0354b003 50350350 350350c0 80880350 35035035 0354b203 50350350 350350b2 808a0270 0c00c00c 00c4d500 c00c00c0 0c00c072 808c00c0 0c00c00c 00c53800 c00c00c0 0c00c7e0 808e00c0 0c00c00c 00c5f400 c00c00c0 0c00c702 809000c0 0c00c00c 00c6f000 c00c00c0 0c00c5eb 809200c0 0c00c00c 00c04c00 c00c00c0 0c00c440 809400c0 0c00c00c 00d21200 c00c00c0 0c00d270 809600c0 0c00d035 03541503 50350350 35035048 80980350 35035035 03563003 50350350 35035666 809a0350 35035035 03504103 50350350 35035478 809c0350 35035035 03520503 50350350 3503526b 809e0350 35035035 0353b803 50350350 35035118 80a00350 35035035 0354f503 50350350 350357ee 80a20340 35035035 03560702 500c00c0 0c00c70b 80a400c0 0c00c00c 00c6bb00 c00c00c0 0c00c665 80a600c0 0c00c00c 00c75700 c00c00c0 0c00c5d9 80a800c0 0c00c00c 00c7d500 c00c00c0 0c00c562 80aa00c0 0c00c00c 00c03a00 c00c00c0 0c00c518 80ac00c0 0c00c00c 00c07900 c00c00c0 0c00c4e4 80ae00c0 0c00c00c 00c0a300 c00c00d0 350354cc 80b00350 35035035 0350b603 50350350 350354b6 80b20350 35035035 0350c103 50350350 350354b0 80b40350 35035035 0350c503 50350350 350354af 80b60350 35035035 0350bf03 50350350 350354bc 80b80350 35035035 03509d03 40350350 350354f6 80ba0350 35035035 03504a03 50350350 35035581 80bc0230 0c00c00c 00c78500 c00c00c0 0c00c667 80be00c0 0c00c00c 00c66d00 c00c00c0 0c00c7b5 80c000c0 0c00c00c 00c50d00 c00c00c0 0c00c15c 80c200c0 0c00c00c 00c33600 c00c00c0 0c00c319 80c400c0 0c00c00c 00c16b00 c00c00c0 0c00c503 80c600c0 0c00c00c 00c79100 c00c00c0 0c00c6c6 80c800c0 0c00d035 0355db03 50350350 35035041 80ca0350 35035035 03545d03 50350350 350351de 80cc0350 35035035 0352aa03 50350350 35035386 80ce0350 35035035 03515303 50350350 350354a4 80d00350 35035035 03502f03 50350340 350355d5 80d20350 35035035 03572e03 50350350 350356a5 80d40350 35035035 03567a02 200c00c0 0c00c74c 80d600c0 0c00c00c 00c5cb00 c00c00c0 0c00c7f3 80d800c0 0c00c00c 00c54800 c00c00c0 0c00c05c 80da00c0 0c00c00c 00c4f100 c00c00c0 0c00c096 80dc00c0 0c00c00c 00c4cb00 c00c00c0 0c00c0b1 80de00c0 0c00c00c 00c4b100 c00c00c0 0c00c0be 80e000c0 0d00c00c 00c4b000 c00d00e0 350350c3 80e20350 35035035 0354ae03 50350350 350350bf 80e40350 35035035 0354b103 50350350 350350ba 80e60350 35035035 0354b003 50350350 350350bf 80e80350 35035035 0354b003 50350350 350350c3 80ea0350 35035035 0354ad03 50350350 350350c5 80ec0350 35035035 0354ab03 50350350 350350c3 80ee0210 0c00c00c 00c4b000 c00c00c0 0c00c0b6 80f000c0 0c00c00c 00c4cb00 c00c00c0 0c00c081 80f200c0 0c00c00c 00c50500 c00c00c0 0c00c036 80f400c0 0c00c00c 00c56d00 c00c00c0 0c00c7b8 80f600c0 0c00c00c 00c60600 c00c00c0 0c00c6f7 80f800c0 0c00c00c 00c6ff00 c00c00c0 0c00c60a 80fa00c0 0c00e035 0347e803 50350350 35035510 80fc0350 35035035 03511403 50350350 350353bc 80fe0350 35035035 03424003 50350350 35035271 80000350 35034035 0353d203 50350350 350350df 80020350 35035035 03557f03 50350350 35035700 80040350 35035035 03574903 50350350 35035523 80060350 35035035 03513f01 f00c00c0 0c00c382 800800c0 0c00c00c 00c2ad00 c00c00c0 0c00c209 800a00c0 0c00c00c 00c3f300 c00c00c0 0c00c0f8 800c00c0 0c00c00c 00c52700 c00c00c0 0c00c7cb 800e00c0 0c00c00c 00c5f800 c00c00c0 0c00c72b 801000c0 0c00c00c 00c6a000 c00c00c0 0c00c68a 801200c0 0c00c00c 00c73800 c00c00f0 350355e5 80140350 35035035 0357b903 50350350 35034580 80160350 35035035 03501b03 50350350 35035533 80180350 35035035 03505a03 50350350 350354fc 801a0350 35035035 03508603 50350350 350354d9 801c0350 35035035 0350a003 50350350 350354c4 801e0350 35035035 0340af03 50350350 350344b5 802001f0 0c00c00c 00c0bc00 c00c00c0 0c00c4a8 802200c0 0c00c00c 00c0bd00 c00c00c0 0c00c4b6 802400c0 0c00c00c 00c09500 c00c00c0 0c00c518 802600c0 0c00c00c 00c7da00 c00c00c0 0c00c619 802800d0 0c00c00c 00c6a700 c00c00c0 0c00c7a3 802a00d0 0c00c00c 00c4cf00 c00c00c0 0c00c1ca 802c00c0 0c010035 03524303 50350350 35035481 802e0350 35035035 03579b03 50350350 35035757 80300350 35035035 0354b603 50350350 35035208 80320350 35035035 03523203 50350350 3503544b 80340350 35035035 03503f03 50350350 350355e4 80360350 35035035 0356c403 50350350 35035741 80380350 35035035 0355ba01 e00c00c0 0c00c00b 803a00c0 0c00c00c 00c51d00 c00c00c0 0c00c07e 803c00c0 0c00c00c 00c4d100 c00c00c0 0c00c0ae 803e00c0 0c00c00c 00c4b200 c00c00c0 0c00c0c1 804000d0 0c00c00c 00c4ad00 d00c00c0 0c00c0c2 804200c0 0c00c00c 00c4ac00 c00c00c0 0c00c0c3 804400c0 0c00c00c 00c4ae00 c00c0110 350350c3 80460350 35035035 0354b203 50350350 350350b8 80480350 35035035 0354b303 50350350 350350b8 804a0350 35035035 0354cb03 50350350 3503508d 804c0350 35035035 03550703 50350350 35035025 804e0350 35035035 03558603 50340350 350357ab 80500350 35035035 03562403 50350350 350356d8 805201c0 0c00c00c 00c71700 c00c00c0 0c00c5e8 805400c0 0c00c00c 00c04800 c00c00c0 0c00c456 805600c0 0c00c00c 00c1d200 c00c00c0 0c00c2dd 805800c0 0c00c00c 00c37000 c00c00d0 0c00c14a 805a00c0 0c00c00c 00c4da00 c00c00c0 0c00c7c3 805c00c0 0c00c00c 00c68b00 c00c00c0 0c00c631 805e00c0 0c012035 03500b03 50350350 350354b2 80600350 34035035 03516b03 50350350 35035319 80620350 35035035 0352d803 50350350 3503521e 80640350 35035035 03540403 50350350 350350e9 80660350 35035035 0354f003 50350350 35035017 80680350 35035034 0355b903 50350350 35035776 806a0350 35035035 03564301 b00c00c0 0c00c6e2 806c00c0 0c00c00c 00c6d100 c00c00c0 0c00c65a 806e00c0 0c00c00c 00c75400 c00c00c0 0c00c5f0 807000c0 0c00c00c 00c7b400 c00c00c0 0c00c597 807200c0 0c00c00c 00c7fe00 c00c00c0 0c00c55d 807400c0 0c00c00c 00c02d00 c00c00c0 0c00c528 807600c0 0c00c00c 00c05a00 c00c0130 3503550a 80780350 35035034 03507603 50350350 340354ee 807a0350 35035035 03508f03 50350350 350354d9 807c0350 35035035 03509b03 50350350 340354c8 807e0350 35035035 0350b003 50350350 350354ba 80800350 35035035 0350be03 50350350 340354b3 80820350 35035035 0350c303 50350350 350354ad 80840190 0c00c00c 00c0c700 c00c00c0 0c00c4b0 808600c0 0c00c00c 00c0c500 c00c00c0 0c00c4b8 808800c0 0c00c00c 00c0b100 c00c00c0 0c00c4d3 808a00c0 0c00c00c 00c09000 c00c00c0 0c00c4fc";
		final String[] hexStrs = ecgDataStr.split(" ");
		testExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				byte[] data = BleDataUtil.hexStringToBytes(hexStrs[hexIndex]
						+ hexStrs[hexIndex + 1] + hexStrs[hexIndex + 2]
						+ hexStrs[hexIndex + 3] + hexStrs[hexIndex + 4]);
				hexIndex += 5;
				if (ConstantConfig.Debug) {
					LogUtil.d("BLEDEMOSRCDATA",
							BleDataUtil.dumpBytesAsString(data));
				}
				Message msg = testHandler.obtainMessage();
				msg.obj = data;
				testHandler.sendMessage(msg);
			}
		}, 1000, 10, TimeUnit.MILLISECONDS);
	}

	private void setEcgMode(EcgType type, EcgLevel level) {
		ecgGLSurfaceViewChannelMII.setEcgMode(type, level);
		// miiQueue.clear();
		// filter.resetFilter();
	}

	/**
	 * 
	 * @param viewID
	 */

	// private Object executorLock = new Object();
	private AtomicBoolean atomicBooleanDraw = new AtomicBoolean(false);

	// private AtomicBoolean atomicBooleanDrawMII = new AtomicBoolean(false);
	// private AtomicBoolean atomicBooleanDrawMV1 = new AtomicBoolean(false);
	// private AtomicBoolean atomicBooleanDrawMV5 = new AtomicBoolean(false);

	private void startExecutor() {
		executor = Executors.newScheduledThreadPool(3);
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (atomicBooleanDraw.compareAndSet(false, true)) {
					try {
						drawEcgData(R.id.ecgGLSurfaceViewChannelMII);
					} catch (Exception e) {
						if (ConstantConfig.Debug) {
							LogUtil.e(TAG, e);
						}
					} finally {
						atomicBooleanDraw.set(false);
					}
				} else {

				}
			}
		}, 0, 40, TimeUnit.MILLISECONDS);
		// executor.scheduleWithFixedDelay(new Runnable() {
		// @Override
		// public void run() {
		// UIUtil.setMessage(handlerTime, 0);
		// }
		// }, 0, 111, TimeUnit.MILLISECONDS);
		setTime();
		setHeartRate();
		// executor.scheduleWithFixedDelay(new Runnable() {
		// @Override
		// public void run() {
		// UIUtil.setMessage(handlerTime, 1);
		// }
		// }, 0, 1000, TimeUnit.MILLISECONDS);
	}

	private void setTime() {
		handlerTime.postDelayed(new Runnable() {

			@Override
			public void run() {
				String dStr = sdf.format(Calendar.getInstance().getTime());
				tvCurDate.setText(dStr.subSequence(0, 10));
				tvCurTime.setText(dStr.subSequence(10, dStr.length()));
				setTime();
			}
		}, 111);
	}

	private void endExecutor() {
		if (executor != null) {
			executor.shutdown();
		}
	}

	private IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter
				.addAction(BleDataParserService.ACTION_ECGMII_DATA_AVAILABLE);
		intentFilter
				.addAction(FrameDataMachine.ACTION_ECGMII_DATAOFF_AVAILABLE);
		return intentFilter;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// startBleService();
		ecgGLSurfaceViewChannelMII.onResume();
		startExecutor();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (ConstantConfig.Debug) {
			findViewById(R.id.lbl2).setVisibility(View.VISIBLE);
			tvRunTime.setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.lbl2).setVisibility(View.INVISIBLE);
			tvRunTime.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		if (executorReceive != null) {
			executorReceive.shutdown();
		}
		Instance = null;
		LogUtil.e(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ecgGLSurfaceViewChannelMII.onPause();
		unregisterReceiver(mGattUpdateReceiver);
		endExecutor();
		// stopAlarm();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			returnModeAcitivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void returnModeAcitivity() {
		Intent myIntent = new Intent();
		myIntent = new Intent(EcgActivity.this, ModeActivity.class);
		startActivity(myIntent);
		this.finish();
		Runtime.getRuntime().gc();
	}

	private void setSpeed(View v) {
		if (rbSpeed != null) {
			rbSpeed.setBackground(null);
		}
		rbSpeed = v;
		rbSpeed.setBackgroundResource(R.drawable.ecg_radio_bg);
		ecg_curspeedvalue.setText("当前速度：" + ((RadioButton) rbSpeed).getText()
				+ "mm/s,幅频：" + ((RadioButton) rbRage).getText() + "mV/mm");
	}

	private void setRange(View v) {
		if (rbRage != null) {
			rbRage.setBackground(null);
		}
		rbRage = v;
		rbRage.setBackgroundResource(R.drawable.ecg_radio_bg);
		ecg_curspeedvalue.setText("当前速度：" + ((RadioButton) rbSpeed).getText()
				+ "mm/s,幅频：" + ((RadioButton) rbRage).getText() + "mV/mm");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonTitleBack:
			returnModeAcitivity();
			break;
		case R.id.ecgGLSurfaceViewChannelMII:
			isStopMII = !isStopMII;
			showToast(isStopMII ? "停止画图" : "开始画图");
			break;
		case R.id.ecgSpeedLevel1:
			setSpeed(v);
			setEcgMode(EcgType.Speed, EcgLevel.Level1);
			break;
		case R.id.ecgSpeedLevel2:
			setSpeed(v);
			setEcgMode(EcgType.Speed, EcgLevel.Level2);
			break;
		case R.id.ecgSpeedLevel3:
			setSpeed(v);
			setEcgMode(EcgType.Speed, EcgLevel.Level3);
			break;
		case R.id.ecgRangeLevel1:
			setRange(v);
			setEcgMode(EcgType.Range, EcgLevel.Level1);
			break;
		case R.id.ecgRangeLevel2:
			setRange(v);
			setEcgMode(EcgType.Range, EcgLevel.Level2);
			break;
		case R.id.ecgRangeLevel3:
			setRange(v);
			setEcgMode(EcgType.Range, EcgLevel.Level3);
			break;
		case R.id.ecgRangeLevel4:
			setRange(v);
			setEcgMode(EcgType.Range, EcgLevel.Level4);
			break;
		default:
			break;
		}
	}
}
