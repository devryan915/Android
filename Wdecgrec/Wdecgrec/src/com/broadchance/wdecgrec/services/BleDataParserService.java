package com.broadchance.wdecgrec.services;

import java.nio.IntBuffer;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.broadchance.entity.AlertCFG;
import com.broadchance.entity.FrameData;
import com.broadchance.entity.HeartRate;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.PlayerManager;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FilterUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertStatus;
import com.broadchance.wdecgrec.alert.AlertType;

@SuppressLint("NewApi")
public class BleDataParserService extends Service {
	private final static String TAG = BleDataParserService.class
			.getSimpleName();
	public final static String ACTION_ECGMII_DATA_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMII_DATA_AVAILABLE";
	public final static String ACTION_ECGMV1_DATA_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMV1_DATA_AVAILABLE";
	public final static String ACTION_ECGMV5_DATA_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMV5_DATA_AVAILABLE";
	private ScheduledExecutorService mEService = Executors
			.newScheduledThreadPool(2);
	/**
	 * 延迟判断心率是否可用
	 */
	private ScheduledFuture<?> mSendSdl;
	/**
	 * 心率是否有效
	 */
	private AtomicBoolean isHeartAvl = new AtomicBoolean(false);
	private final static int heartDelay = 10;
	public final static int SIGNAL_MAX = -50;
	public final static int SIGNAL_MIN = -70;
	// public final static float POWER_MAX = 2.9f;
	// public final static float POWER_MIN = 2.8f;
	// public static int rssiValue = 0;
	/**
	 * 每一次最大处理ble数据帧数
	 */
	public final static int DEAL_MAX = 500;
	/**
	 * 每一次最大接受数据
	 */
	public final static int REC_MAX = 1000;

	private LinkedBlockingQueue<FrameData> receivedQueue = new LinkedBlockingQueue<FrameData>();
	private LinkedBlockingQueue<FrameData> dealQueue = new LinkedBlockingQueue<FrameData>();
	private Timer processFrameDataTimer;
	private TimerTask processFrameDataTask;
	// private ScheduledExecutorService eServie = Executors
	// .newScheduledThreadPool(3);

	// private Timer mTimer = new Timer();
	// private TimerTask mTask;
	/**
	 * 一帧的数据点数
	 */
	private IntBuffer miiBuffer = IntBuffer.allocate(10);
	private IntBuffer mv1Buffer = IntBuffer.allocate(10);
	private IntBuffer mv5Buffer = IntBuffer.allocate(10);
	private AtomicBoolean atomicBooleanPro = new AtomicBoolean(false);

	// private int[] miiArray = new int[10];
	private static BleDataParserService _Instance;

	public static BleDataParserService getInstance() {
		return _Instance;
	}

	private int lastHeart = 0;

	private HeartRate lastHeartRate;

	private void sendECGData(IntBuffer buffer, String action) throws Exception {
		if (buffer.position() > 0) {
			Intent intent = new Intent();
			intent.setAction(action);
			int[] dst = new int[buffer.position()];
			// System.arraycopy(buffer.array(), 0, dst, 0, dst.length);
			buffer.position(0);
			try {
				buffer.get(dst, 0, dst.length);
			} catch (Exception e) {
				throw e;
			} finally {
				buffer.position(0);
			}
			// 将滤波算法提前计算
			// 如果是第一通道数据可以计算心率
			int[] filterData = null;
			if (ACTION_ECGMII_DATA_AVAILABLE.equals(action)) {
				filterData = FilterUtil.Instance.getECGDataII(dst);
				int heart = FilterUtil.Instance.getHeartRate();
				FrameDataMachine machine = FrameDataMachine.getInstance();
				if (lastHeartRate == null
						|| (lastHeartRate != null && CommonUtil.getDate()
								.getTime() - lastHeartRate.date.getTime() > ConstantConfig.HeartRateFrequency)) {
					lastHeartRate = new HeartRate();
					lastHeartRate.heart = heart;
					lastHeartRate.date = CommonUtil.getDate();
					machine.addHeartRate(lastHeartRate);
				}
				if (lastHeart != heart && heart >= 0 && isHeartAvl.get()) {
					lastHeart = heart;
					// 心动过速
					if (heart > AlertMachine.getInstance()
							.getAlertConfig(AlertType.B00001)
							.getIntValueRaise()) {
						if (AlertMachine.getInstance().canSendAlert(
								AlertType.B00001, 1)) {
							JSONObject alertObj = new JSONObject();
							try {
								alertObj.put("id", AlertType.B00001.getValue());
								alertObj.put("state", 1);
								alertObj.put("time", CommonUtil.getTime_B());
								JSONObject value = new JSONObject();
								value.put("hr", heart);
								value.put(
										"hrlimithi",
										AlertMachine
												.getInstance()
												.getAlertConfig(
														AlertType.B00001)
												.getIntValueRaise());
								alertObj.put("value", value);
								AlertMachine.getInstance().sendAlert(
										AlertType.B00001, alertObj);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} else if (heart <= AlertMachine.getInstance()
							.getAlertConfig(AlertType.B00001)
							.getIntValueClear()) {
						if (AlertMachine.getInstance().canSendAlert(
								AlertType.B00001, 0)) {
							AlertMachine.getInstance().cancelAlert(
									AlertType.B00001);
						}
					}
					// 心动过缓
					if (heart < AlertMachine.getInstance()
							.getAlertConfig(AlertType.B00002)
							.getIntValueRaise()) {
						if (AlertMachine.getInstance().canSendAlert(
								AlertType.B00002, 1)) {
							JSONObject alertObj = new JSONObject();
							try {
								alertObj.put("id", AlertType.B00002.getValue());
								alertObj.put("state", 1);
								alertObj.put("time", CommonUtil.getTime_B());
								JSONObject value = new JSONObject();
								value.put("hr", heart);
								value.put(
										"hrlimitlow",
										AlertMachine
												.getInstance()
												.getAlertConfig(
														AlertType.B00002)
												.getIntValueRaise());
								alertObj.put("value", value);
								AlertMachine.getInstance().sendAlert(
										AlertType.B00002, alertObj);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} else if (heart >= AlertMachine.getInstance()
							.getAlertConfig(AlertType.B00002)
							.getIntValueClear()) {
						if (AlertMachine.getInstance().canSendAlert(
								AlertType.B00002, 0)) {
							AlertMachine.getInstance().cancelAlert(
									AlertType.B00002);
						}
					}
					if (heart <= AlertMachine.getInstance()
							.getAlertConfig(AlertType.B00003)
							.getIntValueRaise()) {
						if (AlertMachine.getInstance().canSendAlert(
								AlertType.B00003, 1)) {
							JSONObject alertObj = new JSONObject();
							try {
								alertObj.put("id", AlertType.B00003.getValue());
								alertObj.put("state", 1);
								alertObj.put("time", CommonUtil.getTime_B());
								JSONObject value = new JSONObject();
								alertObj.put("value", value);
								AlertMachine.getInstance().sendAlert(
										AlertType.B00003, alertObj);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} else if (heart > AlertMachine.getInstance()
							.getAlertConfig(AlertType.B00003)
							.getIntValueClear()) {
						if (AlertMachine.getInstance().canSendAlert(
								AlertType.B00003, 0)) {
							AlertMachine.getInstance().cancelAlert(
									AlertType.B00003);
						}
					}
				}

			} else if (ACTION_ECGMV1_DATA_AVAILABLE.equals(action)) {
				// filterData = FilterUtil.Instance.getECGDataV1(dst);
				filterData = dst;
			} else if (ACTION_ECGMV5_DATA_AVAILABLE.equals(action)) {
				// filterData = FilterUtil.Instance.getECGDataV5(dst);
				filterData = dst;
			}
			intent.putExtra(BluetoothLeService.EXTRA_DATA, filterData);
			// intent.putExtra(BluetoothLeService.EXTRA_DATA, buffer.array());
			sendBroadcast(intent);
		}
	}

	private void processReceivedByte() throws Exception {
		int length = dealQueue.size();
		if (length <= 0)
			return;
		FrameDataMachine fdm = FrameDataMachine.getInstance();
		Integer miiValue = null;
		Integer mv1Value = null;
		Integer mv5Value = null;
		FrameData data;
		int dCount = 0;
		while (dCount++ < DEAL_MAX) {
			if ((data = dealQueue.poll()) != null) {
				fdm.processFrameData(data);
				while (true) {
					miiValue = fdm.getMII();
					if (miiValue != null) {
						if (!miiBuffer.hasRemaining()) {
							sendECGData(miiBuffer, ACTION_ECGMII_DATA_AVAILABLE);
						}
						miiBuffer.put(miiValue);
					}
					mv1Value = fdm.getMV1();
					if (mv1Value != null) {
						if (!mv1Buffer.hasRemaining()) {
							sendECGData(mv1Buffer, ACTION_ECGMV1_DATA_AVAILABLE);
						}
						mv1Buffer.put(mv1Value);
					}
					mv5Value = fdm.getMV5();
					if (mv5Value != null) {
						if (!mv5Buffer.hasRemaining()) {
							sendECGData(mv5Buffer, ACTION_ECGMV5_DATA_AVAILABLE);
						}
						mv5Buffer.put(mv5Value);
					}
					if (miiValue == null && mv1Value == null
							&& mv5Value == null) {
						break;
					}
				}
			} else {
				break;
			}
		}
	}

	public class LocalBinder extends Binder {
		public BleDataParserService getService() {
			return BleDataParserService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		_Instance = this;
		receivedQueue.clear();
		dealQueue.clear();
		startExeService();
		// acquireWakeLock();
	}

	public void clearData() {
		if (receivedQueue != null)
			receivedQueue.clear();
		if (dealQueue != null)
			dealQueue.clear();
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mGattUpdateReceiver);
		cancelExeService();
		receivedQueue.clear();
		receivedQueue = null;
		dealQueue.clear();
		dealQueue = null;
		// releaseWarkLock();
		super.onDestroy();
	};

	public void startExeService() {
		// eServie.scheduleAtFixedRate(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// if (atomicBooleanPro.compareAndSet(false, true)) {
		// // int qLen = receivedQueue.size();
		// // if (ConstantConfig.Debug) {
		// // LogUtil.d(TAG, "当前接受的数据帧数:" + qLen);
		// // }
		// // for (int i = 0; i < qLen; i++) {
		// FrameData data;
		// int dCount = 0;
		// while (dCount++ < REC_MAX) {
		// if ((data = receivedQueue.poll()) != null) {
		// dealQueue.offer(data);
		// } else {
		// break;
		// }
		// }
		// processReceivedByte();
		// atomicBooleanPro.set(false);
		// }
		// } catch (Exception e) {
		// LogUtil.e(TAG, e);
		// }
		// }
		// }, 0, 80, TimeUnit.MILLISECONDS);

		processFrameDataTask = new TimerTask() {
			@Override
			public void run() {
				if (atomicBooleanPro.compareAndSet(false, true)) {
					try {
						FrameData data;
						int dCount = 0;
						while (dCount++ < REC_MAX) {
							if ((data = receivedQueue.poll()) != null) {
								dealQueue.offer(data);
							} else {
								break;
							}
						}
						processReceivedByte();
						// if (ConstantConfig.Debug) {
						// LogUtil.d(TAG, "已接收数据：" + receivedQueue.size()
						// + " 待处理数据：" + dealQueue.size());
						// }
					} catch (Exception e) {
						LogUtil.e(TAG, e);
					} finally {
						atomicBooleanPro.set(false);
					}
				}
			}
		};
		processFrameDataTimer = new Timer();
		processFrameDataTimer.schedule(processFrameDataTask, 0, 160);

	}

	public void cancelExeService() {
		// eServie.shutdown();
		if (processFrameDataTimer != null) {
			processFrameDataTimer.cancel();
			processFrameDataTimer = null;
		}
	}

	private String lastSeq = null;
	private long index = 0;

	private void receiveData(byte[] data) {
		synchronized (receivedQueue) {
			try {
				// if (ConstantConfig.Debug) {
				// String frameTypeHex = String.format("%02X ", data[0])
				// .toUpperCase().trim();
				// if (frameTypeHex.startsWith("8")) {
				// String seqHex = String.format("%02X ", data[1])
				// .toUpperCase().trim();
				// if (lastSeq != null) {
				// Integer lastValue = Integer.parseInt(lastSeq, 16);
				// Integer curValue = Integer.parseInt(seqHex, 16);
				// if (curValue == lastValue) {
				// LogUtil.e(TAG, "出现重复 " + (index - 1) + ":"
				// + index);
				// // UIUtil.showToast(BleDataParserService.this,
				// // "出现重复");
				// }
				// }
				// lastSeq = seqHex;
				// index = (++index) % Integer.MAX_VALUE;
				// String ecgData = BleDataUtil.dumpBytesAsString(data);
				// String ecgStr = index + "\t " + ecgData;
				// // FileUtil.writeECGSRC(ecgStr + "\r\n");
				// LogUtil.d("BLESRCDATA", ecgStr);
				// }
				// }
				FrameData frameData = new FrameData(data, CommonUtil.getDate());
				receivedQueue.offer(frameData);
			} catch (Exception e) {
				LogUtil.e(TAG, e);
			}
		}
	}

	/**
	 * 是否设备断开
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				final byte[] data = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				receiveData(data);
				// synchronized (receivedQueue) {
				// byte[] data = intent
				// .getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				// // for (int i = 0; i < data.length; i++) {
				// // receivedQueue.offer(data[i]);
				// // }
				// try {
				// if (ConstantConfig.Debug) {
				// LogUtil.d("BLESRCDATA",
				// BleDataUtil.dumpBytesAsString(data));
				// }
				// FrameData frameData = new FrameData(data, new Date());
				// receivedQueue.offer(frameData);
				// } catch (Exception e) {
				// LogUtil.e(TAG, e);
				// }
				// }
				// if (ConstantConfig.Debug) {
				// String bleData = BleDataUtil.dumpBytesAsString(data);
				// if (ConstantConfig.Debug) {
				// SimpleDateFormat sdf = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");
				// Date date = new Date();
				// LogUtil.d(TAG, "DateTime " + sdf.format(date) + " "
				// + bleData);
				// }
				// }
			} else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				if (AlertMachine.getInstance()
						.canSendAlert(AlertType.A00002, 0)) {
					AlertMachine.getInstance().cancelAlert(AlertType.A00002);
				}
				startExeService();
				if (mSendSdl != null && !mSendSdl.isDone()) {
					mSendSdl.cancel(true);
				}
				isHeartAvl.set(false);
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mSendSdl = mEService.schedule(new Runnable() {
					@Override
					public void run() {
						isHeartAvl.set(true);
					}
				}, heartDelay, TimeUnit.SECONDS);
				UIUserInfoLogin user = DataManager.getUserInfo();
				if (user != null && user.getMacAddress() != null
						&& !user.getMacAddress().isEmpty()) {
					// 设备断开
					PlayerManager.getInstance().playDevOff();
					if (AlertMachine.getInstance().canSendAlert(
							AlertType.A00002, 1)) {
						JSONObject alertObj = new JSONObject();
						try {
							alertObj.put("id", AlertType.A00002.getValue());
							alertObj.put("state", 1);
							alertObj.put("time", CommonUtil.getTime_B());
							JSONObject value = new JSONObject();
							value.put("bledevice", user.getMacAddress());
							alertObj.put("value", value);
							AlertMachine.getInstance().sendAlert(
									AlertType.A00002, alertObj);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				cancelExeService();
				FrameDataMachine.getInstance().resetData();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (action.equals(GuardService.ACTION_GATT_RSSICHANGED)) {
				Integer rssi;
				// rssi = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
				rssi = BluetoothLeService.rssiValue;
				// if (rssiValue == 0) {
				// rssiValue = rssi;
				// } else {
				// rssiValue = (int) (rssiValue * 0.9f + rssi * 0.1f);
				if (rssi != null && rssi <= SIGNAL_MIN) {
					// 信号低
					PlayerManager.getInstance().playLowSignal();
				}
				// }
				// UIUtil.showToast(context, "蓝牙信号 rssi:" + rssi + " rssiValue:"
				// + rssiValue);

			} else if (action.equals(GuardService.ACTION_GATT_POWERCHANGED)) {
				// float power = intent.getFloatExtra(
				// BluetoothLeService.EXTRA_DATA, 0);
				Float power = FrameDataMachine.getInstance().getPower();
				if (power != null) {
					// UIUtil.showToast(context, "蓝牙电量 power:" + power);
					// 传感器电量预警
					if (power < AlertMachine.getInstance()
							.getAlertConfig(AlertType.A00005)
							.getFloatValueRaise()) {
						UIUserInfoLogin user = DataManager.getUserInfo();
						if (user != null && user.getMacAddress() != null
								&& !user.getMacAddress().isEmpty()) {
							if (AlertMachine.getInstance().canSendAlert(
									AlertType.A00005, 1)) {
								JSONObject alertObj = new JSONObject();
								try {
									alertObj.put("id",
											AlertType.A00005.getValue());
									alertObj.put("state", 1);
									alertObj.put("time", CommonUtil.getTime_B());
									JSONObject value = new JSONObject();
									value.put("bledevice", user.getMacAddress());
									value.put("volt", power);
									alertObj.put("value", value);
									AlertMachine.getInstance().sendAlert(
											AlertType.A00005, alertObj);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					} else if (power > AlertMachine.getInstance()
							.getAlertConfig(AlertType.A00005)
							.getFloatValueClear()) {
						if (AlertMachine.getInstance().canSendAlert(
								AlertType.A00005, 0)) {
							AlertMachine.getInstance().cancelAlert(
									AlertType.A00005);
						}
					}

					if (power <= AlertMachine.getInstance()
							.getAlertConfig(AlertType.A00005)
							.getFloatValueRaise()
							&& power > 0) {
						// 电量低
						PlayerManager.getInstance().playLowPower();
						// Notification notification = new Notification(
						// R.drawable.ic_launcher,
						// getString(R.string.app_name),
						// System.currentTimeMillis());
						// // Intent intent = new Intent(context,
						// // EcgActivity.class);
						// PendingIntent pendingintent = PendingIntent
						// .getActivity(context, 0, null, 0);
						// notification.setLatestEventInfo(context, "穿戴设备",
						// "设备电量低，请更换电极片！", pendingintent);
						// startForeground(0x111, notification);
						UIUtil.showToast(context, "设备电量低，请更换电极片！");
					}
				}
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(GuardService.ACTION_GATT_RSSICHANGED);
		intentFilter.addAction(GuardService.ACTION_GATT_POWERCHANGED);
		return intentFilter;
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	// private WakeLock wakeLock = null;
	//
	// private void acquireWakeLock() {
	// if (wakeLock == null) {
	// PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	// wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
	// .getClass().getCanonicalName());
	// wakeLock.acquire();
	// }
	// }
	//
	// private void releaseWarkLock() {
	// if (wakeLock != null && wakeLock.isHeld()) {
	// wakeLock.release();
	// wakeLock = null;
	// }
	// }
}
