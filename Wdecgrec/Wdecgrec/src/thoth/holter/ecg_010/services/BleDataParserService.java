package thoth.holter.ecg_010.services;

import java.nio.IntBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;

import thoth.holter.ecg_010.R;
import thoth.holter.ecg_010.manager.DataManager;
import thoth.holter.ecg_010.manager.FrameDataMachine;
import thoth.holter.ecg_010.manager.PlayerManager;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import com.broadchance.entity.FrameData;
import com.broadchance.entity.HeartRate;
import com.broadchance.entity.UserInfo;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FilterUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.alert.AlertMachine;
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

	public final static String ACTION_PROCESS_DATA = ConstantConfig.ACTION_PREFIX
			+ "ACTION_PROCESS_DATA";
	// private ScheduledExecutorService mEService = null;
	/**
	 * 延迟判断心率是否可用
	 */
	// private ScheduledFuture<?> mSendSdl;
	/**
	 * 心率是否有效
	 */
	private Long conectTime = null;
	private final static int heartDelay = 10 * 1000;
	public final static int SIGNAL_MAX = -50;
	public final static int SIGNAL_MIN = -70;
	// public final static float POWER_MAX = 2.9f;
	// public final static float POWER_MIN = 2.8f;
	// public static int rssiValue = 0;
	private static final int deal_interval = 2000;
	/**
	 * 每一次最大处理ble数据帧数
	 */
	private final static int DEAL_MAX = 500;
	/**
	 * 每一次最大接受数据
	 */
	// public final static int REC_MAX = 1000;

	private LinkedBlockingQueue<FrameData> receivedQueue = new LinkedBlockingQueue<FrameData>();;
	// private LinkedBlockingQueue<FrameData> dealQueue = new
	// LinkedBlockingQueue<FrameData>();
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
	// private IntBuffer mv1Buffer = IntBuffer.allocate(10);
	// private IntBuffer mv5Buffer = IntBuffer.allocate(10);
	private AtomicBoolean atomicBooleanPro = new AtomicBoolean(false);

	// private int[] miiArray = new int[10];
	// private static BleDataParserService _Instance;
	//
	// public static BleDataParserService getInstance() {
	// return _Instance;
	// }

	private int lastHeart = 0;
	private int mConnectionState = BluetoothLeService.STATE_DISCONNECTED;
	private HeartRate lastHeartRate;

	private void sendECGData(IntBuffer buffer, String action) throws Exception {
		// if (ConstantConfig.Debug) {
		// Log.d(ConstantConfig.DebugTAG,
		// TAG + "\nreceivedQueue:" + receivedQueue.size());
		// }
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
			int[] filterData = dst;
			// if (ACTION_ECGMII_DATA_AVAILABLE.equals(action)) {
			filterData = FilterUtil.Instance.getECGDataII(dst);
			int heart = FilterUtil.Instance.getHeartRate();
			FrameDataMachine machine = FrameDataMachine.getInstance();
			if (lastHeartRate == null
					|| (lastHeartRate != null && CommonUtil.getDate().getTime()
							- lastHeartRate.date.getTime() > ConstantConfig.HeartRateFrequency)) {
				lastHeartRate = new HeartRate();
				lastHeartRate.heart = heart;
				lastHeartRate.date = CommonUtil.getDate();
				machine.addHeartRate(lastHeartRate);
			}
			if (lastHeart != heart && heart >= 0) {
				if (conectTime != null
						&& System.currentTimeMillis() - conectTime > heartDelay
						&& mConnectionState == BluetoothLeService.STATE_CONNECTED) {
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
					// if (heart <= AlertMachine.getInstance()
					// .getAlertConfig(AlertType.B00003)
					// .getIntValueRaise()) {
					// if (AlertMachine.getInstance().canSendAlert(
					// AlertType.B00003, 1)) {
					// JSONObject alertObj = new JSONObject();
					// try {
					// alertObj.put("id", AlertType.B00003.getValue());
					// alertObj.put("state", 1);
					// alertObj.put("time", CommonUtil.getTime_B());
					// JSONObject value = new JSONObject();
					// alertObj.put("value", value);
					// AlertMachine.getInstance().sendAlert(
					// AlertType.B00003, alertObj);
					// } catch (JSONException e) {
					// e.printStackTrace();
					// }
					// }
					// } else if (heart > AlertMachine.getInstance()
					// .getAlertConfig(AlertType.B00003)
					// .getIntValueClear()) {
					// if (AlertMachine.getInstance().canSendAlert(
					// AlertType.B00003, 0)) {
					// AlertMachine.getInstance().cancelAlert(
					// AlertType.B00003);
					// }
					// }
				} else {
					if (ConstantConfig.Debug) {
						UIUtil.showRemoteToast("延迟生理报警heart:" + heart);
					}
				}
			}
			// }
			// else if (ACTION_ECGMV1_DATA_AVAILABLE.equals(action)) {
			// // filterData = FilterUtil.Instance.getECGDataV1(dst);
			// filterData = dst;
			// } else if (ACTION_ECGMV5_DATA_AVAILABLE.equals(action)) {
			// // filterData = FilterUtil.Instance.getECGDataV5(dst);
			// filterData = dst;
			// }
			intent.putExtra(BluetoothLeService.EXTRA_DATA, filterData);
			// intent.putExtra(BluetoothLeService.EXTRA_DATA, buffer.array());
			sendBroadcast(intent);
		}
	}

	// private void processReceivedByte() throws Exception {
	// int length = dealQueue.size();
	// if (length <= 0)
	// return;
	// FrameDataMachine fdm = FrameDataMachine.getInstance();
	// Integer miiValue = null;
	// Integer mv1Value = null;
	// Integer mv5Value = null;
	// FrameData data;
	// int dCount = 0;
	// while (dCount++ < DEAL_MAX) {
	// if ((data = dealQueue.poll()) != null) {
	// fdm.processFrameData(data);
	// while (true) {
	// miiValue = fdm.getMII();
	// if (miiValue != null) {
	// if (!miiBuffer.hasRemaining()) {
	// sendECGData(miiBuffer, ACTION_ECGMII_DATA_AVAILABLE);
	// }
	// miiBuffer.put(miiValue);
	// }
	// mv1Value = fdm.getMV1();
	// if (mv1Value != null) {
	// if (!mv1Buffer.hasRemaining()) {
	// sendECGData(mv1Buffer, ACTION_ECGMV1_DATA_AVAILABLE);
	// }
	// mv1Buffer.put(mv1Value);
	// }
	// mv5Value = fdm.getMV5();
	// if (mv5Value != null) {
	// if (!mv5Buffer.hasRemaining()) {
	// sendECGData(mv5Buffer, ACTION_ECGMV5_DATA_AVAILABLE);
	// }
	// mv5Buffer.put(mv5Value);
	// }
	// if (miiValue == null && mv1Value == null
	// && mv5Value == null) {
	// break;
	// }
	// }
	// } else {
	// break;
	// }
	// }
	// }

	public class LocalBinder extends Binder {
		public BleDataParserService getService() {
			return BleDataParserService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// receivedQueue.clear();
		// dealQueue.clear();
		// startExeService();
		// acquireWakeLock();
	}

	// public void clearData() {
	// if (receivedQueue != null)
	// receivedQueue.clear();
	// if (dealQueue != null)
	// dealQueue.clear();
	// }

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mGattUpdateReceiver);
		cancelProcessService();
		// receivedQueue = null;
		// dealQueue.clear();
		releaseWarkLock();
		super.onDestroy();
	};

	int lostTimes = 0;
	long lastExeTime = 0;

	private void executeData() {
		if (ConstantConfig.Debug) {
			long diff = (System.currentTimeMillis() - lastExeTime);
			lastExeTime = System.currentTimeMillis();
			if (diff > 1000) {
				LogUtil.d(
						TAG,
						"executeData:"
								+ diff
								+ "ms 当前cpu频率："
								+ (Integer.parseInt(CommonUtil.getCurCpuFreq()) / 1000f)
								+ "MHZ 已接收的数据长度 " + receivedQueue.size() + " ");
				Notification notification = new Notification.Builder(
						BleDataParserService.this)
						.setContentTitle("警告")
						.setContentText(
								"executeData:"
										+ diff
										+ " 当前cpu频率："
										+ (Integer.parseInt(CommonUtil
												.getCurCpuFreq()) / 1000f)
										+ "MHZ ")
						.setSmallIcon(R.drawable.ic_launcher).build();
				startForeground(0x111, notification);
			}
		}
		if (atomicBooleanPro.compareAndSet(false, true)) {
			try {
				// Log.d(ConstantConfig.DebugTAG, TAG + "正在处理数据:"
				// + receivedQueue.size());
				final long time = System.currentTimeMillis();
				final int presize = receivedQueue.size();
				processData();
				final int csize = receivedQueue.size() - presize;
				final long cost = System.currentTimeMillis() - time;
				if (ConstantConfig.Debug) {
					Log.d(ConstantConfig.DebugTAG, TAG + " 处理能力" + csize
							+ " 处理耗时:" + (cost) + " 错过次数" + lostTimes);
					if (cost > 10000) {
						UIUtil.showToast("cpu不给力  处理能力" + csize + " 处理耗时:"
								+ (cost));
						Notification notification = new Notification.Builder(
								BleDataParserService.this)
								.setContentTitle("警告")
								.setContentText(
										"处理能力:"
												+ csize
												+ " 耗时:"
												+ (cost)
												+ " cpu:"
												+ (Integer.parseInt(CommonUtil
														.getCurCpuFreq()) / 1000f)
												+ "MHZ ")
								.setSmallIcon(R.drawable.ic_launcher).build();
						// Intent intent = new Intent(context,
						// EcgActivity.class);
						// PendingIntent pendingintent = PendingIntent
						// .getActivity(BleDataParserService.this, 0,
						// null, 0);
						// notification.setLatestEventInfo(
						// BleDataParserService.this, "穿戴设备",
						// "设备电量低，请更换电极片！", pendingintent);
						startForeground(0x111, notification);
					} else {
						stopForeground(true);
					}
				}
				lostTimes = 0;
			} catch (Exception e) {
				e.printStackTrace();
				// Log.d(ConstantConfig.DebugTAG, TAG
				// + "\n处理数据异常:" + e.toString());
			} finally {
				atomicBooleanPro.set(false);
			}
		} else {
			// Log.d(ConstantConfig.DebugTAG, TAG + "\n来不及处理数据:"
			// + receivedQueue.size());
			lostTimes++;
		}
	}

	public void startProcessService() {
		// mEService.scheduleAtFixedRate(new Runnable() {
		//
		// @Override
		// public void run() {
		// executeData();
		// }
		// }, 0, deal_interval, TimeUnit.MILLISECONDS);
		// processFrameDataTask = new TimerTask() {
		// @Override
		// public void run() {
		// executeData();
		// }
		// };
		// processFrameDataTimer = new Timer();
		// processFrameDataTimer.schedule(processFrameDataTask, 0,
		// deal_interval);
		//
		// startAlarm();
	}

	private void startAlarm() {
		Intent intent = new Intent();
		intent.setAction(ACTION_PROCESS_DATA);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		// am.setRepeating(AlarmManager.RTC,
		// System.currentTimeMillis(), deal_interval, pi);
		// am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		// SystemClock.elapsedRealtime(), deal_interval, pi);
		// 改为兼容api19以上定时任务
		// am.setExact(type, triggerAtMillis, operation)
		am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + deal_interval, pi);
	}

	private void cancelAlarm() {
		Intent intent = new Intent();
		intent.setAction(ACTION_PROCESS_DATA);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(sender);
	}

	public void cancelProcessService() {
		// mEService.shutdown();
		// if (processFrameDataTask != null) {
		// processFrameDataTask.cancel();
		// processFrameDataTask = null;
		// }
		// if (processFrameDataTimer != null) {
		// processFrameDataTimer.cancel();
		// processFrameDataTimer = null;
		// }
		// cancelAlarm();
	}

	// private String lastSeq = null;
	// private long index = 0;

	private void processData() throws Exception {
		FrameData frameData = null;
		FrameDataMachine fdm = FrameDataMachine.getInstance();
		Integer miiValue = null;
		// Integer mv1Value = null;
		// Integer mv5Value = null;
		int dCount = 0;
		while (dCount++ < DEAL_MAX) {
			if ((frameData = receivedQueue.poll()) != null) {
				fdm.processFrameData(frameData);
				while ((miiValue = fdm.getMII()) != null) {
					if (!miiBuffer.hasRemaining()) {
						sendECGData(miiBuffer, ACTION_ECGMII_DATA_AVAILABLE);
					}
					miiBuffer.put(miiValue);
					// mv1Value = fdm.getMV1();
					// if (mv1Value != null) {
					// if (!mv1Buffer.hasRemaining()) {
					// sendECGData(mv1Buffer,
					// ACTION_ECGMV1_DATA_AVAILABLE);
					// }
					// mv1Buffer.put(mv1Value);
					// }
					// mv5Value = fdm.getMV5();
					// if (mv5Value != null) {
					// if (!mv5Buffer.hasRemaining()) {
					// sendECGData(mv5Buffer,
					// ACTION_ECGMV5_DATA_AVAILABLE);
					// }
					// mv5Buffer.put(mv5Value);
					// }
					// if (miiValue == null && mv1Value == null
					// && mv5Value == null) {
					// break;
					// }
				}
			} else {
				break;
			}
		}
	}

	private void receiveData(byte[] data) {
		// synchronized (receivedQueue) {
		try {
			FrameData frameData = new FrameData(data, CommonUtil.getDate()
					.getTime());
			receivedQueue.offer(frameData);
		} catch (Exception e) {
			LogUtil.e(TAG, e);
		}
		// }
	}

	private long lastrecTime = 0;
	private long recedataslength = 0;
	/**
	 * 是否设备断开
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_TIME_TICK.equals(action)) {
				if (ConstantConfig.Debug) {
					// UIUtil.showToast("TimeTick broadcast start executeData");
					LogUtil.d(TAG, "TimeTick broadcast start executeData");
				}
				executeData();
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				final byte[] data = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				receiveData(data);
				if (ConstantConfig.Debug) {
					recedataslength++;
					long diff = System.currentTimeMillis() - lastrecTime;
					if (diff > 5000) {
						LogUtil.d(TAG, diff + " 内收到了 " + recedataslength
								+ " 帧 "
								+ ((recedataslength * 1f / diff) * 1000) + "/s");
						recedataslength = 0;
						lastrecTime = System.currentTimeMillis();
					}
				}
				executeData();
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
				mConnectionState = BluetoothLeService.STATE_CONNECTED;

			} else if (BleDataParserService.ACTION_PROCESS_DATA.equals(action)) {
				startAlarm();
				executeData();
			} else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnectionState = BluetoothLeService.STATE_CONNECTED;
				if (AlertMachine.getInstance()
						.canSendAlert(AlertType.A00002, 0)) {
					AlertMachine.getInstance().cancelAlert(AlertType.A00002);
				}
				// startExeService();
				// if (mSendSdl != null && !mSendSdl.isDone()) {
				// mSendSdl.cancel(true);
				// }
				conectTime = System.currentTimeMillis();
				if (ConstantConfig.Debug) {
					UIUtil.showRemoteToast("蓝牙已连接，准备生理报警");
				}
				// mSendSdl = mEService.schedule(new Runnable() {
				// @Override
				// public void run() {
				// isHeartAvl.set(true);
				// if (ConstantConfig.Debug) {
				// UIUtil.showRemoteToast("蓝牙已连接，开始生理报警");
				// }
				// }
				// }, heartDelay, TimeUnit.SECONDS);
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				if (ConstantConfig.Debug) {
					UIUtil.showRemoteToast("蓝牙断开");
				}
				mConnectionState = BluetoothLeService.STATE_DISCONNECTED;
				conectTime = null;
				UserInfo user = DataManager.getUserInfo();
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
				// cancelExeService();
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
				// UIUtil.showBleToast(context, "蓝牙信号 rssi:" + rssi +
				// " rssiValue:"
				// + rssiValue);

			} else if (action.equals(GuardService.ACTION_GATT_POWERCHANGED)) {
				// float power = intent.getFloatExtra(
				// BluetoothLeService.EXTRA_DATA, 0);
				Float power = FrameDataMachine.getInstance().getPower();
				if (power != null) {
					// UIUtil.showBleToast(context, "蓝牙电量 power:" + power);
					// 传感器电量预警
					if (power < AlertMachine.getInstance()
							.getAlertConfig(AlertType.A00005)
							.getFloatValueRaise()) {
						UserInfo user = DataManager.getUserInfo();
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
						UIUtil.showRemoteToast("设备电量低，请更换电极片！");
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
		intentFilter.addAction(BleDataParserService.ACTION_PROCESS_DATA);
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		return intentFilter;
	}

	@Override
	public boolean stopService(Intent name) {
		LogUtil.w(ConstantConfig.DebugTAG, TAG + "\n" + "stopService");
		return super.stopService(name);
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	@Override
	public void onLowMemory() {
		LogUtil.w(ConstantConfig.DebugTAG, TAG + "\n" + "onLowMemory");
		UIUtil.showLongToast("LowMemory");
		super.onLowMemory();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		flags = START_STICKY;
		// mEService = Executors.newScheduledThreadPool(2);
		receivedQueue.clear();
		startProcessService();
		acquireWakeLock();
		// Notification notification = new Notification();
		// notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		// startForeground(3, notification);
		return super.onStartCommand(intent, flags, startId);
	}

	private WakeLock wakeLock = null;

	private void acquireWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}

	private void releaseWarkLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}
