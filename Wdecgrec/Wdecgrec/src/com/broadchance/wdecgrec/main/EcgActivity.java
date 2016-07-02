package com.broadchance.wdecgrec.main;

import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.broadchance.ecgview.ECGGLSurfaceView;
import com.broadchance.ecgview.ECGGLSurfaceView.CanvasReadyCallback;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.PlayerManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FilterUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.login.LoginActivity;
import com.broadchance.wdecgrec.services.BleDataParserService;
import com.broadchance.wdecgrec.services.BleDomainService;
import com.broadchance.wdecgrec.services.BluetoothLeService;

public class EcgActivity extends BaseActivity implements CanvasReadyCallback {
	/**
	 * 第一通道
	 */
	private ECGGLSurfaceView ecgGLSurfaceViewChannelMII;
	/**
	 * 第二通道
	 */
	private ECGGLSurfaceView ecgGLSurfaceViewChannelMV1;
	/**
	 * 第三通道
	 */
	private ECGGLSurfaceView ecgGLSurfaceViewChannelMV5;
	private View viewChannel1;
	private View viewChannel2;
	private View viewChannel3;
	private View llECG;
	private View viewEcgChannel;
	private CheckBox checkBox3Channel;
	private RadioGroup rgEcgSpeed;
	private RadioGroup rgEcgChannel;
	private TextView ecg_curhearrate;
	private TextView ecg_curspeedvalue;
	private ScrollView scrollViewContent;

	private Button buttonUploadType;
	private Button buttonTitleBack;
	private boolean isLongPress;

	private final static int GRID_VNUM_20 = 20;
	private final static int GRID_VNUM_60 = 60;
	public static final int UPDATE_MIICANVAS = 1;
	public static final int UPDATE_MV1CANVAS = 2;
	public static final int UPDATE_MV5CANVAS = 3;
	private int mEcgMode = ECGGLSurfaceView.ECG_MODE_LOW;

	private ConcurrentLinkedQueue<Integer> miiQueue = new ConcurrentLinkedQueue<Integer>();
	// private ConcurrentLinkedQueue<Integer> queue = new
	// ConcurrentLinkedQueue<Integer>();
	long totalReceivePoints = 0;
	private int miiPointNumber = 0;
	// private float[] miiECGData = new float[ECGGLSurfaceView.MAX_POINT];

	private ConcurrentLinkedQueue<Integer> mv1Queue = new ConcurrentLinkedQueue<Integer>();
	private int mv1PointNumber = 0;
	// private float[] mv1ECGData = new float[ECGGLSurfaceView.MAX_POINT];

	private ConcurrentLinkedQueue<Integer> mv5Queue = new ConcurrentLinkedQueue<Integer>();
	private int mv5PointNumber = 0;
	// private float[] mv5ECGData = new float[ECGGLSurfaceView.MAX_POINT];
	private ScheduledExecutorService executor;
	private ExecutorService executorReceive;
	private static final String ACTION_ALARM = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ALARM";
	private static final String ACTION_MIIALARM = ConstantConfig.ACTION_PREFIX
			+ "ACTION_MIIALARM";
	private static final String ACTION_MV1ALARM = ConstantConfig.ACTION_PREFIX
			+ "ACTION_MV1ALARM";
	private static final String ACTION_MV5ALARM = ConstantConfig.ACTION_PREFIX
			+ "ACTION_MV5ALARM";
	protected static final String TAG = EcgActivity.class.getSimpleName();
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
	long lastMV1Time = 0;
	long lastMV5Time = 0;
	private FilterUtil filter = new FilterUtil();
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPDATE_MIICANVAS) {
				EcgData data = (EcgData) msg.obj;
				ecgGLSurfaceViewChannelMII.drawECG(data.queueArray,
						data.pointNumber);
				setHeartRate();
			} else if (msg.what == UPDATE_MV1CANVAS) {
				EcgData data = (EcgData) msg.obj;
				ecgGLSurfaceViewChannelMV1.drawECG(data.queueArray,
						data.pointNumber);
			} else if (msg.what == UPDATE_MV5CANVAS) {
				EcgData data = (EcgData) msg.obj;
				ecgGLSurfaceViewChannelMV5.drawECG(data.queueArray,
						data.pointNumber);
			}
		}
	};
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				miiPointNumber = 0;
				miiQueue.clear();
				mv1PointNumber = 0;
				mv1Queue.clear();
				mv5PointNumber = 0;
				mv5Queue.clear();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (ACTION_ALARM.equals(action)) {
				drawEcgData(R.id.ecgGLSurfaceViewChannelMII);
				drawEcgData(R.id.ecgGLSurfaceViewChannelMV1);
				drawEcgData(R.id.ecgGLSurfaceViewChannelMV5);
			} else if (ACTION_MIIALARM.equals(action)) {
				drawEcgData(R.id.ecgGLSurfaceViewChannelMII);
				// drawEcgData(ecgGLSurfaceViewChannelMV1, mv1Queue, 0);
				// drawEcgData(ecgGLSurfaceViewChannelMV5, mv5Queue, 0);
				// int totalEcg = ecgGLSurfaceViewChannelMII
				// .getCurrTotalPointNumber();
				// Float[] queueArray = new Float[0];
				// synchronized (miiQueue) {
				// if (miiQueue.size() >= 10) {
				// queueArray = miiQueue.toArray(queueArray);
				// }
				// }
				// miiPointNumber = queueArray.length;
				// int length = miiQueue.size();
				// if (length > totalEcg * 1.5f) {
				// for (int j = 0; j < 10; j++) {
				// miiQueue.poll();
				// }
				// }
				// // 防止队列无限增大，检查队列是否超过最大允许上限，如果超出则移除老数据
				// int maxLength = totalEcg * 4;
				// if (length > maxLength) {
				// if (ConstantConfig.Debug) {
				// LogUtil.d(TAG, "miiQueue Size 超过最大" + maxLength + " 抛掉"
				// + totalEcg);
				// }
				// // 删除一个周期保留三个周期缓冲
				// for (int j = 0; j < totalEcg; j++) {
				// miiQueue.poll();
				// }
				// }
				//
				// if (miiPointNumber > totalEcg) {
				// miiPointNumber = totalEcg;
				// }
				// // if (ConstantConfig.Debug) {
				// // LogUtil.d(TAG, "drawECG MII points:" + miiPointNumber);
				// // }
				// if (miiPointNumber > 0) {
				// ecgGLSurfaceViewChannelMII.drawECG(queueArray,
				// miiPointNumber);
				// }
				// setHeartRate();
			} else if (ACTION_MV1ALARM.equals(action)) {
				drawEcgData(R.id.ecgGLSurfaceViewChannelMV1);
				// int totalEcg = ecgGLSurfaceViewChannelMV1
				// .getCurrTotalPointNumber();
				// Float[] queueArray = new Float[0];
				// synchronized (mv1Queue) {
				// if (mv1Queue.size() >= 10) {
				// queueArray = mv1Queue.toArray(queueArray);
				// }
				// }
				// mv1PointNumber = queueArray.length;
				// int length = mv1Queue.size();
				// if (length > totalEcg * 1.5f) {
				// for (int j = 0; j < 10; j++) {
				// mv1Queue.poll();
				// }
				// }
				// // 防止队列无限增大，检查队列是否超过最大允许上限，如果超出则移除老数据
				// int maxLength = totalEcg * 4;
				// if (length > maxLength) {
				// if (ConstantConfig.Debug) {
				// LogUtil.d(TAG, "mv1Queue Size 超过最大" + maxLength + " 抛掉"
				// + totalEcg);
				// }
				// // 删除一个周期保留三个周期缓冲
				// for (int j = 0; j < totalEcg; j++) {
				// mv1Queue.poll();
				// }
				// }
				//
				// if (mv1PointNumber > totalEcg) {
				// mv1PointNumber = totalEcg;
				// }
				// // if (ConstantConfig.Debug) {
				// // LogUtil.d(TAG, "drawECG MV1 points:" + mv1PointNumber);
				// // }
				// if (mv1PointNumber > 0) {
				// ecgGLSurfaceViewChannelMV1.drawECG(queueArray,
				// mv1PointNumber);
				// }
			} else if (ACTION_MV5ALARM.equals(action)) {
				drawEcgData(R.id.ecgGLSurfaceViewChannelMV5);
				// int totalEcg = ecgGLSurfaceViewChannelMV5
				// .getCurrTotalPointNumber();
				// Float[] queueArray = new Float[0];
				// synchronized (mv5Queue) {
				// if (mv5Queue.size() >= 10) {
				// queueArray = mv5Queue.toArray(queueArray);
				// }
				// }
				// mv5PointNumber = queueArray.length;
				// int length = mv5Queue.size();
				// if (length > totalEcg * 1.5f) {
				// for (int j = 0; j < 10; j++) {
				// mv5Queue.poll();
				// }
				// }
				// // 防止队列无限增大，检查队列是否超过最大允许上限，如果超出则移除老数据
				// int maxLength = totalEcg * 4;
				// if (length > maxLength) {
				// if (ConstantConfig.Debug) {
				// LogUtil.d(TAG, "mv5Queue Size 超过最大" + maxLength + " 抛掉"
				// + totalEcg);
				// }
				// // 删除一个周期保留三个周期缓冲
				// for (int j = 0; j < totalEcg; j++) {
				// mv5Queue.poll();
				// }
				// }
				//
				// if (mv5PointNumber > totalEcg) {
				// mv5PointNumber = totalEcg;
				// }
				// // if (ConstantConfig.Debug) {
				// // LogUtil.d(TAG, "drawECG MV5 points:" + mv5PointNumber);
				// // }
				// if (mv5PointNumber > 0) {
				// ecgGLSurfaceViewChannelMV5.drawECG(queueArray,
				// mv5PointNumber);
				// }
			} else if (BleDataParserService.ACTION_ECGMII_DATA_AVAILABLE
					.equals(action)) {
				// int[] ecgData = intent
				// .getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
				// int[] outData = filter.getECGDataII(ecgData);
				// receiveEcgData(R.id.ecgGLSurfaceViewChannelMII, outData);
				executorReceive.execute(new Runnable() {
					@Override
					public void run() {
						int[] ecgData = intent
								.getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
						int[] outData = filter.getECGDataII(ecgData);
						receiveEcgData(R.id.ecgGLSurfaceViewChannelMII, outData);
					}
				});
				// if (ConstantConfig.Debug) {
				// if (lastTime == 0) {
				// lastTime = System.currentTimeMillis();
				// } else {
				// totalReceivePoints += ecgData.length;
				// float time = (System.currentTimeMillis() - lastTime) / 1000f;
				// LogUtil.d(TAG, "points/s:"
				// + (totalReceivePoints / time) + " totalpoints:"
				// + totalReceivePoints + " time:" + time);
				// }
				// }
			} else if (BleDataParserService.ACTION_ECGMV1_DATA_AVAILABLE
					.equals(action)) {
				executorReceive.execute(new Runnable() {
					@Override
					public void run() {
						int[] ecgData = intent
								.getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
						receiveEcgData(R.id.ecgGLSurfaceViewChannelMV1, ecgData);
					}
				});
				// synchronized (mv1Queue) {
				// int[] ecg = intent
				// .getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
				// if (ecg != null) {
				// int[] outData = filter.getECGDataV1(ecg);
				// for (int i = 0; i < outData.length; i++) {
				// try {
				// float data = outData[i];
				// mv1Queue.offer(data);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
			} else if (BleDataParserService.ACTION_ECGMV5_DATA_AVAILABLE
					.equals(action)) {
				executorReceive.execute(new Runnable() {
					@Override
					public void run() {
						int[] ecgData = intent
								.getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
						receiveEcgData(R.id.ecgGLSurfaceViewChannelMV5, ecgData);
					}
				});

				// synchronized (mv5Queue) {
				// int[] ecg = intent
				// .getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
				// if (ecg != null) {
				// // int[] outData = filter.getECGDataV1(ecg);
				// for (int i = 0; i < ecg.length; i++) {
				// try {
				// float data = ecg[i];
				// mv5Queue.offer(data);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
			} else if (FrameDataMachine.ACTION_ECGMII_DATAOFF_AVAILABLE
					.equals(action)) {
				PlayerManager.getInstance().playDevFallOff();
				showToast("MII电极脱落");
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
		case R.id.ecgGLSurfaceViewChannelMV1:
			totalEcg = ecgGLSurfaceViewChannelMV1.getCurrTotalPointNumber();
			queue = mv1Queue;
			break;
		case R.id.ecgGLSurfaceViewChannelMV5:
			totalEcg = ecgGLSurfaceViewChannelMV5.getCurrTotalPointNumber();
			queue = mv5Queue;
			break;
		default:
			return;
		}
		// synchronized (queue) {
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
			int maxLength = Math.max(1000, totalEcg * 3);
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
		// }
	}

	class EcgData {
		public Integer[] queueArray;
		public int pointNumber;
	}

	private void drawEcgData(int viewID) {
		ECGGLSurfaceView ecgglSurfaceView = null;
		Queue<Integer> queue = null;
		int action;
		long msdif = 0;
		switch (viewID) {
		case R.id.ecgGLSurfaceViewChannelMII:
			ecgglSurfaceView = ecgGLSurfaceViewChannelMII;
			queue = miiQueue;
			if (lastMIITime > 0) {
				msdif = System.currentTimeMillis() - lastMIITime;
			}
			lastMIITime = System.currentTimeMillis();
			action = UPDATE_MIICANVAS;
			// setHeartRate();
			break;
		case R.id.ecgGLSurfaceViewChannelMV1:
			ecgglSurfaceView = ecgGLSurfaceViewChannelMV1;
			queue = mv1Queue;
			if (lastMV1Time > 0) {
				msdif = System.currentTimeMillis() - lastMV1Time;
			}
			lastMV1Time = System.currentTimeMillis();
			action = UPDATE_MV1CANVAS;
			break;
		case R.id.ecgGLSurfaceViewChannelMV5:
			ecgglSurfaceView = ecgGLSurfaceViewChannelMV5;
			queue = mv5Queue;
			if (lastMV5Time > 0) {
				msdif = System.currentTimeMillis() - lastMV5Time;
			}
			lastMV5Time = System.currentTimeMillis();
			action = UPDATE_MV5CANVAS;
			break;
		default:
			return;
		}

		// LogUtil.d(TAG, "ACTION_MV1ALARM时间差" + msdif);
		int totalEcg = ecgglSurfaceView.getCurrTotalPointNumber();
		Integer[] queueArray = new Integer[0];
		// synchronized (queue) {
		if (queue.size() >= 10) {
			queueArray = queue.toArray(queueArray);
		}
		// }
		// if (ConstantConfig.Debug) {
		// LogUtil.d(TAG, "当前"
		// + (action == UPDATE_MIICANVAS ? "II"
		// : (action == UPDATE_MV1CANVAS ? "V1" : "V5"))
		// + "长度" + queueArray.length);
		// }
		int pointNumber = queueArray.length;
		int length = queue.size();
		if (length > totalEcg * 1.5f) {
			int throwLength = (int) Math.floor(msdif * 0.125f);
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "msdif:" + msdif + " 丢弃:" + throwLength
						+ " size:" + queue.size());
			}
			for (int j = 0; j < throwLength; j++) {
				queue.poll();
			}
		}
		if (pointNumber > totalEcg) {
			pointNumber = totalEcg;
		}
		// if (ConstantConfig.Debug) {
		// LogUtil.d(TAG, "drawECG MII points:" + miiPointNumber);
		// }
		if (pointNumber > 10) {
			EcgData data = new EcgData();
			data.pointNumber = pointNumber;
			data.queueArray = queueArray;
			UIUtil.setMessage(handler, action, data);
			// ecgglSurfaceView.drawECG(queueArray, pointNumber);
		}

	}

	private void setHeartRate() {
		if (hearRate == 0) {
			hearRate = filter.getHeartRate();
		} else {
			hearRate = (int) (hearRate * 0.9f + filter.getHeartRate() * 0.1f);
		}
		ecg_curhearrate.setText(hearRate + "");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_ecg);
		executorReceive = Executors.newCachedThreadPool();
		ecgGLSurfaceViewChannelMII = (ECGGLSurfaceView) findViewById(R.id.ecgGLSurfaceViewChannelMII);
		ecgGLSurfaceViewChannelMV1 = (ECGGLSurfaceView) findViewById(R.id.ecgGLSurfaceViewChannelMV1);
		ecgGLSurfaceViewChannelMV5 = (ECGGLSurfaceView) findViewById(R.id.ecgGLSurfaceViewChannelMV5);
		ecgGLSurfaceViewChannelMII.initView(EcgActivity.this, GRID_VNUM_20);
		ecgGLSurfaceViewChannelMII.setCallback(EcgActivity.this);
		ecgGLSurfaceViewChannelMV1.initView(EcgActivity.this, GRID_VNUM_20);
		ecgGLSurfaceViewChannelMV1.setCallback(EcgActivity.this);
		ecgGLSurfaceViewChannelMV5.initView(EcgActivity.this, GRID_VNUM_20);
		ecgGLSurfaceViewChannelMV5.setCallback(EcgActivity.this);
		scrollViewContent = (ScrollView) findViewById(R.id.scrollViewContent);
		viewChannel1 = findViewById(R.id.viewChannel1);
		viewChannel2 = findViewById(R.id.viewChannel2);
		viewChannel3 = findViewById(R.id.viewChannel3);
		ecg_curhearrate = (TextView) findViewById(R.id.ecg_curhearrate);
		ecg_curspeedvalue = (TextView) findViewById(R.id.ecg_curspeedvalue);
		llECG = findViewById(R.id.llECG);
		viewEcgChannel = findViewById(R.id.viewEcgChannel);
		buttonTitleBack = (Button) findViewById(R.id.buttonTitleBack);
		buttonTitleBack.setOnClickListener(this);
		checkBox3Channel = (CheckBox) findViewById(R.id.checkBox3Channel);
		checkBox3Channel
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							checkBox3Channel
									.setText(R.string.ecg_threechannels);
							viewEcgChannel.setVisibility(View.GONE);
							viewChannel1.setVisibility(View.VISIBLE);
							ecgGLSurfaceViewChannelMII
									.setVisibility(View.VISIBLE);
							ecgGLSurfaceViewChannelMII.initView(
									EcgActivity.this, GRID_VNUM_20);
							viewChannel2.setVisibility(View.VISIBLE);
							ecgGLSurfaceViewChannelMV1
									.setVisibility(View.VISIBLE);
							ecgGLSurfaceViewChannelMV1.initView(
									EcgActivity.this, GRID_VNUM_20);
							viewChannel3.setVisibility(View.VISIBLE);
							ecgGLSurfaceViewChannelMV5
									.setVisibility(View.VISIBLE);
							ecgGLSurfaceViewChannelMV5.initView(
									EcgActivity.this, GRID_VNUM_20);
						} else {
							checkBox3Channel.setText(R.string.ecg_onechannel);
							// 默认显示第一通道
							showSingleChannel(R.id.viewChannel1);
							viewEcgChannel.setVisibility(View.VISIBLE);
						}
					}
				});
		setEcgMode(ECGGLSurfaceView.ECG_MODE_NORMAL);
		rgEcgSpeed = (RadioGroup) findViewById(R.id.rgEcgSpeed);
		rgEcgSpeed
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.ecgSpeedLow:
							ecg_curspeedvalue.setText("5mm/mv 12.5mm/s");
							setEcgMode(ECGGLSurfaceView.ECG_MODE_LOW);
							break;
						case R.id.ecgSpeedNormal:
							ecg_curspeedvalue.setText("10mm/mv 25mm/s");
							setEcgMode(ECGGLSurfaceView.ECG_MODE_NORMAL);
							break;
						case R.id.ecgSpeedHigh:
							ecg_curspeedvalue.setText("20mm/mv 50mm/s");
							setEcgMode(ECGGLSurfaceView.ECG_MODE_HIGH);
							break;
						default:
							break;
						}
					}
				});

		rgEcgChannel = (RadioGroup) findViewById(R.id.rgEcgChannel);
		rgEcgChannel
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.ecgChannel1:
							showSingleChannel(R.id.viewChannel1);
							break;
						case R.id.ecgChannel2:
							showSingleChannel(R.id.viewChannel2);
							break;
						case R.id.ecgChannel3:
							showSingleChannel(R.id.viewChannel3);
							break;
						default:
							break;
						}
					}
				});
		buttonUploadType = (Button) findViewById(R.id.buttonUploadType);
		buttonUploadType.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				buttonUploadType.setText(R.string.ecg_realtime_mode);
				isLongPress = true;
				sendBroadcast(new Intent(
						BleDomainService.ACTION_UPLOAD_STARTREALMODE));
				return true;
			}
		});
		buttonUploadType.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isLongPress) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						isLongPress = false;
						buttonUploadType.setText(R.string.ecg_batch_mode);
						sendBroadcast(new Intent(
								BleDomainService.ACTION_UPLOAD_ENDREALMODE));
					}
				}
				return false;
			}
		});
		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getLoginName());
		setScrollContentHeight();
	}

	private void setScrollContentHeight() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
		float scale = outMetrics.density;
		int height = outMetrics.heightPixels
				- ((int) ((30 + 72 + 25 + 50) * scale + 0.5f));
		LayoutParams layParams = scrollViewContent.getLayoutParams();
		layParams.height = height;
		scrollViewContent.setLayoutParams(layParams);
	}

	private void setEcgMode(final int mode) {
		executorReceive.execute(new Runnable() {
			@Override
			public void run() {
				mEcgMode = mode;
				ecgGLSurfaceViewChannelMII.setEcgMode(mEcgMode);
				ecgGLSurfaceViewChannelMV1.setEcgMode(mEcgMode);
				ecgGLSurfaceViewChannelMV5.setEcgMode(mEcgMode);
				synchronized (miiQueue) {
					miiPointNumber = 0;
					miiQueue.clear();
				}
				synchronized (mv1Queue) {
					mv1PointNumber = 0;
					mv1Queue.clear();
				}
				synchronized (mv5Queue) {
					mv5PointNumber = 0;
					mv5Queue.clear();
				}
				filter.resetFilter();
			}
		});
	}

	/**
	 * 
	 * @param viewID
	 */
	private void showSingleChannel(int viewID) {
		// LayoutParams layoutParams = null;
		switch (viewID) {
		case R.id.viewChannel1:
			viewChannel1.setVisibility(View.VISIBLE);
			ecgGLSurfaceViewChannelMII.setVisibility(View.VISIBLE);
			viewChannel2.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMV1.setVisibility(View.GONE);
			viewChannel3.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMV5.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMII.initView(EcgActivity.this, GRID_VNUM_60);
			break;
		case R.id.viewChannel2:
			viewChannel1.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMII.setVisibility(View.GONE);
			viewChannel2.setVisibility(View.VISIBLE);
			ecgGLSurfaceViewChannelMV1.setVisibility(View.VISIBLE);
			viewChannel3.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMV5.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMV1.initView(EcgActivity.this, GRID_VNUM_60);
			break;
		case R.id.viewChannel3:
			viewChannel1.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMII.setVisibility(View.GONE);
			viewChannel2.setVisibility(View.GONE);
			ecgGLSurfaceViewChannelMV1.setVisibility(View.GONE);
			viewChannel3.setVisibility(View.VISIBLE);
			ecgGLSurfaceViewChannelMV5.setVisibility(View.VISIBLE);
			ecgGLSurfaceViewChannelMV5.initView(EcgActivity.this, GRID_VNUM_60);
			break;
		default:
			break;
		}
		llECG.invalidate();
	}

	private void startExecutor() {
		executor = Executors.newScheduledThreadPool(3);
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					// if (ConstantConfig.Debug) {
					// LogUtil.d(TAG, Runtime.getRuntime().freeMemory() + "/"
					// + Runtime.getRuntime().totalMemory());
					// }

					drawEcgData(R.id.ecgGLSurfaceViewChannelMII);
					drawEcgData(R.id.ecgGLSurfaceViewChannelMV1);
					drawEcgData(R.id.ecgGLSurfaceViewChannelMV5);
					// if (ConstantConfig.Debug) {
					// LogUtil.d(TAG, Runtime.getRuntime().freeMemory() + "/"
					// + Runtime.getRuntime().totalMemory());
					// }
				} catch (Exception e) {
					if (ConstantConfig.Debug) {
						LogUtil.e(TAG, e);
					}
				}
			}
		}, 1000, 40, TimeUnit.MILLISECONDS);
		// executor.scheduleAtFixedRate(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// drawEcgData(R.id.ecgGLSurfaceViewChannelMII);
		// } catch (Exception e) {
		// if (ConstantConfig.Debug) {
		// LogUtil.e(TAG, e);
		// }
		// }
		// }
		// },1000, 40, TimeUnit.MILLISECONDS);
		// executor.scheduleAtFixedRate(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// drawEcgData(R.id.ecgGLSurfaceViewChannelMV1);
		// } catch (Exception e) {
		// if (ConstantConfig.Debug) {
		// LogUtil.e(TAG, e);
		// }
		// }
		// }
		// }, 0, 40, TimeUnit.MILLISECONDS);
		// executor.scheduleAtFixedRate(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// drawEcgData(R.id.ecgGLSurfaceViewChannelMV5);
		// } catch (Exception e) {
		// if (ConstantConfig.Debug) {
		// LogUtil.e(TAG, e);
		// }
		// }
		// }
		// }, 0, 40, TimeUnit.MILLISECONDS);
	}

	private void endExecutor() {
		if (executor != null) {
			executor.shutdown();
		}

	}

	private void startAlarm() {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar c = Calendar.getInstance();
		PendingIntent pi = null;
		Intent intent = null;
		intent = new Intent();
		intent.setAction(ACTION_ALARM);
		pi = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), 20, pi);
		// intent = new Intent();
		// intent.setAction(ACTION_MIIALARM);
		// pi = PendingIntent.getBroadcast(this, 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// am.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), 20, pi);
		// intent = new Intent();
		// intent.setAction(ACTION_MV1ALARM);
		// pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		// am.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), 20, pi);
		// intent = new Intent();
		// intent.setAction(ACTION_MV5ALARM);
		// pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		// am.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), 20, pi);
	}

	private void stopAlarm() {
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = null;
		PendingIntent sender = null;
		intent = new Intent();
		intent.setAction(ACTION_ALARM);
		sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(sender);
		// intent = new Intent();
		// intent.setAction(ACTION_MIIALARM);
		// sender = PendingIntent.getBroadcast(this, 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// alarm.cancel(sender);
		// intent = new Intent();
		// intent.setAction(ACTION_MV1ALARM);
		// sender = PendingIntent.getBroadcast(this, 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// alarm.cancel(sender);
		// intent = new Intent();
		// intent.setAction(ACTION_MV5ALARM);
		// sender = PendingIntent.getBroadcast(this, 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// alarm.cancel(sender);
	}

	private void wearMode() {
		Intent intent = new Intent(EcgActivity.this, LoginActivity.class);
		startActivity(intent);
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
				.addAction(BleDataParserService.ACTION_ECGMV1_DATA_AVAILABLE);
		intentFilter
				.addAction(BleDataParserService.ACTION_ECGMV5_DATA_AVAILABLE);
		intentFilter
				.addAction(FrameDataMachine.ACTION_ECGMII_DATAOFF_AVAILABLE);
		intentFilter
				.addAction(FrameDataMachine.ACTION_ECGMV1_DATAOFF_AVAILABLE);
		intentFilter
				.addAction(FrameDataMachine.ACTION_ECGMV5_DATAOFF_AVAILABLE);
		return intentFilter;
	}

	private static IntentFilter makeAlarmIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		intentFilter.addAction(ACTION_ALARM);
		intentFilter.addAction(ACTION_MIIALARM);
		intentFilter.addAction(ACTION_MV1ALARM);
		intentFilter.addAction(ACTION_MV5ALARM);
		return intentFilter;
	}

	@Override
	protected void onResume() {
		super.onResume();
		ecgGLSurfaceViewChannelMII.onResume();
		ecgGLSurfaceViewChannelMV1.onResume();
		ecgGLSurfaceViewChannelMV5.onResume();
		// startAlarm();
		startExecutor();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		registerReceiver(mGattUpdateReceiver, makeAlarmIntentFilter());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (executorReceive != null) {
			executorReceive.shutdown();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		ecgGLSurfaceViewChannelMII.onPause();
		ecgGLSurfaceViewChannelMV1.onPause();
		ecgGLSurfaceViewChannelMV5.onPause();
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

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonWearMode:
			wearMode();
			break;
		case R.id.buttonTitleBack:
			returnModeAcitivity();
			break;
		default:
			break;
		}
	}

	@Override
	public void notifyCanvasReady() {

	}

	@Override
	public boolean getCapture() {
		return false;
	}

	@Override
	public void onCaptured(Bitmap bitmap) {

	}

	@Override
	public boolean stopPainting() {
		return false;
	}

	@Override
	public void onPaintingStopped(float yGridValue) {

	}
}
