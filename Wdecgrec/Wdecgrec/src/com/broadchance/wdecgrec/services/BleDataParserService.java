package com.broadchance.wdecgrec.services;

import java.nio.IntBuffer;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
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

	public final static int SIGNAL_MAX = -50;
	public final static int SIGNAL_MIN = -70;
	public final static float POWER_MAX = 2.9f;
	public final static float POWER_MIN = 2.8f;
	// public static int rssiValue = 0;

	private LinkedBlockingQueue<FrameData> receivedQueue = new LinkedBlockingQueue<FrameData>();
	private LinkedBlockingQueue<FrameData> dealQueue = new LinkedBlockingQueue<FrameData>();
	private Timer processFrameDataTimer;
	private TimerTask processFrameDataTask;

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

	/**
	 * 校验蓝牙数据 1、错位补零 2、丢帧补帧
	 * 
	 * @param bleData
	 * @return
	 * @throws Exception
	 */
	// private int[] checkBleData() {
	// FrameDataMachine fdm = FrameDataMachine.getInstance();
	// int length = dealQueue.size();
	// int[] bleData = new byte[20 * length];
	// for (int i = 0; i < length; i++) {
	// byte[] temp = dealQueue.poll().data;
	// fdm.processFrameData(dealQueue.poll());
	// for (int j = 0; j < 20; j++) {
	// bleData[i * 20 + j] = temp[j];
	// }
	// }
	// return bleData;
	// }
	//
	// private byte[] splitChannel(byte[] bleData) {
	// return bleData;
	// }
	//
	// /**
	// * 滤波算法
	// *
	// * @param bleData
	// * @return
	// */
	// private byte[] filterBleData(byte[] bleData) {
	// return bleData;
	// }
	private int lastHeart = 0;
	private boolean isHeartFast = false;
	private boolean isHearLow = false;
	private boolean isHeartStop = false;
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
				if (lastHeart != heart) {
					lastHeart = heart;
					// 心动过速
					if (heart > ConstantConfig.AlertA00006_Limit_Raise) {
						JSONObject alertObj = new JSONObject();
						try {
							isHeartFast = true;
							alertObj.put("id", AlertType.A00006.getValue());
							alertObj.put("state", 1);
							alertObj.put("time", CommonUtil.getTime_B());
							JSONObject value = new JSONObject();
							value.put("hr", heart);
							value.put("hrlimithi",
									ConstantConfig.AlertA00006_Limit_Raise);
							alertObj.put("value", value);
							AlertMachine.getInstance().sendAlert(
									AlertType.A00006, alertObj);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if (heart <= ConstantConfig.AlertA00006_Limit_Clear
							&& isHeartFast) {
						isHeartFast = false;
						AlertMachine.getInstance()
								.cancelAlert(AlertType.A00006);
					}
					// 心动过缓
					if (heart < ConstantConfig.AlertA00007_Limit_Raise) {
						JSONObject alertObj = new JSONObject();
						try {
							isHearLow = true;
							alertObj.put("id", AlertType.A00007.getValue());
							alertObj.put("state", 1);
							alertObj.put("time", CommonUtil.getTime_B());
							JSONObject value = new JSONObject();
							value.put("hr", heart);
							value.put("hrlimitlow",
									ConstantConfig.AlertA00007_Limit_Raise);
							alertObj.put("value", value);
							AlertMachine.getInstance().sendAlert(
									AlertType.A00007, alertObj);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if (heart >= ConstantConfig.AlertA00007_Limit_Clear
							&& isHearLow) {
						isHearLow = false;
						AlertMachine.getInstance()
								.cancelAlert(AlertType.A00007);
					}

					if (heart <= ConstantConfig.AlertA00008_Limit_Raise) {
						JSONObject alertObj = new JSONObject();
						try {
							isHeartStop = true;
							alertObj.put("id", AlertType.A00008.getValue());
							alertObj.put("state", 1);
							alertObj.put("time", CommonUtil.getTime_B());
							JSONObject value = new JSONObject();
							alertObj.put("value", value);
							AlertMachine.getInstance().sendAlert(
									AlertType.A00008, alertObj);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if (heart > ConstantConfig.AlertA00008_Limit_Clear
							&& isHeartStop) {
						isHeartStop = false;
						AlertMachine.getInstance()
								.cancelAlert(AlertType.A00008);
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
		while ((data = dealQueue.poll()) != null) {
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
				if (miiValue == null && mv1Value == null && mv5Value == null) {
					break;
				}
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
		startTimer();
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
		cancelTimer();
		receivedQueue.clear();
		receivedQueue = null;
		dealQueue.clear();
		dealQueue = null;
		// releaseWarkLock();
		super.onDestroy();
	};

	public void startTimer() {
		processFrameDataTask = new TimerTask() {
			@Override
			public void run() {
				try {
					if (atomicBooleanPro.compareAndSet(false, true)) {
						// int qLen = receivedQueue.size();
						// if (ConstantConfig.Debug) {
						// LogUtil.d(TAG, "当前接受的数据帧数:" + qLen);
						// }
						// for (int i = 0; i < qLen; i++) {
						FrameData data;
						while ((data = receivedQueue.poll()) != null) {
							dealQueue.offer(data);
						}
						// }
						processReceivedByte();
						atomicBooleanPro.set(false);
					}
				} catch (Exception e) {
					LogUtil.e(TAG, e);
				}
			}
		};
		processFrameDataTimer = new Timer();
		processFrameDataTimer.schedule(processFrameDataTask, 0, 80);

	}

	public void cancelTimer() {
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
	private boolean isDevOff = false;
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
				if (isDevOff) {
					AlertMachine.getInstance().cancelAlert(AlertType.A00002);
					isDevOff = false;
				}
				startTimer();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)
					|| BluetoothLeService.ACTION_GATT_RECONNECTING
							.equals(action)) {
				UIUserInfoLogin user = DataManager.getUserInfo();
				if (user != null && user.getMacAddress() != null
						&& !user.getMacAddress().isEmpty()) {
					// 设备断开
					PlayerManager.getInstance().playDevOff();
					JSONObject alertObj = new JSONObject();
					try {
						isDevOff = true;
						alertObj.put("id", AlertType.A00002.getValue());
						alertObj.put("state", 1);
						alertObj.put("time", CommonUtil.getTime_B());
						JSONObject value = new JSONObject();
						value.put("bledevice", user.getMacAddress());
						alertObj.put("value", value);
						AlertMachine.getInstance().sendAlert(AlertType.A00002,
								alertObj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				cancelTimer();
				FrameDataMachine.getInstance().resetData();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (action
					.equals(BluetoothLeService.ACTION_GATT_RSSICHANGED)) {
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

			} else if (action
					.equals(BluetoothLeService.ACTION_GATT_POWERCHANGED)) {
				float power = intent.getFloatExtra(
						BluetoothLeService.EXTRA_DATA, 0);
				// UIUtil.showToast(context, "蓝牙电量 power:" + power);
				// 传感器电量预警
				if (power < ConstantConfig.AlertA00005_Limit_Raise) {
					UIUserInfoLogin user = DataManager.getUserInfo();
					if (user != null && user.getMacAddress() != null
							&& !user.getMacAddress().isEmpty()) {
						JSONObject alertObj = new JSONObject();
						try {
							alertObj.put("id", AlertType.A00005.getValue());
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
				} else if (power > ConstantConfig.AlertA00005_Limit_Clear) {
					AlertMachine.getInstance().cancelAlert(AlertType.A00005);
				}

				if (power <= POWER_MIN && power > 0) {
					// 电量低
					PlayerManager.getInstance().playLowPower();
					Notification notification = new Notification(
							R.drawable.ic_launcher,
							getString(R.string.app_name),
							System.currentTimeMillis());
					// Intent intent = new Intent(context, EcgActivity.class);
					PendingIntent pendingintent = PendingIntent.getActivity(
							context, 0, null, 0);
					notification.setLatestEventInfo(context, "穿戴设备",
							"设备电量低，请更换电极片！", pendingintent);
					startForeground(0x111, notification);
					UIUtil.showLongToast(context, "设备电量低，请更换电极片！");
				}
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RECONNECTING);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);

		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSICHANGED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_POWERCHANGED);
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
