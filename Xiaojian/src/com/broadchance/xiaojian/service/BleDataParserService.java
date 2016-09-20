package com.broadchance.xiaojian.service;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.langlang.ble.AccelAnalyzer;
import com.langlang.ble.DataErrorException;
import com.langlang.ble.DetectC7StateMachine;
import com.langlang.ble.FrameStateMachine;
import com.langlang.ble.InvalidStepCheckStateMachine;
import com.langlang.ble.StepCountManager;
import com.langlang.cutils.ECGHeartRateCalculator;
import com.langlang.cutils.GlobalAccelCalculator;
import com.langlang.cutils.RespCalculator;
import com.langlang.data.AccelVector;
import com.langlang.data.ECGResult;
import com.langlang.data.StepCaloriesEntry;
import com.langlang.data.StressLevelItem;
import com.langlang.data.ValueEntry;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.manager.LastUploadManager;
import com.langlang.manager.MinuteECGResultManager;
import com.langlang.manager.UploadTaskManager;
import com.langlang.manager.WarningHteManager;
import com.langlang.manager.WarningTempManager;
import com.langlang.manager.WarningTumbleManager;
import com.langlang.utils.BreathFilter;
import com.langlang.utils.CompressedFilePool;
import com.langlang.utils.DateUtil;
import com.langlang.utils.EventLogger;
import com.langlang.utils.Filter;
import com.langlang.utils.MiscUtils;
import com.langlang.utils.Program;

@SuppressLint("NewApi")
public class BleDataParserService extends Service {
	private final static String TAG = BleDataParserService.class
			.getSimpleName();

	private Queue<Byte> receivedQueue = new LinkedList<Byte>();
	private byte[] receivedBuffer = new byte[1024 * 10];
	private int receivedNumber = 0;
	private FrameStateMachine frameStateMachine = new FrameStateMachine();
	private Timer mTimer = new Timer();
	private TimerTask mTask;
	private int mDataCount = 0;

	public static final int DATA_FRAME_UNKNOWN = -1;
	public static final int DATA_FRAME_60 = 0;
	public static final int DATA_FRAME_61 = 1;
	public static final int DATA_FRAME_62 = 2;
	public static final int DATA_FRAME_63 = 3;
	public static final int DATA_FRAME_64 = 4;
	public static final int DATA_FRAME_65 = 5;
	public static final int DATA_FRAME_69 = 9;

	public static final String ACTION_ACCELERATED_X = "action_accelerated_x";
	public static final String ACTION_ACCELERATED_Y = "action_accelerated_y";
	public static final String ACTION_ACCELERATED_Z = "action_accelerated_z";
	public static final String ACTION_ACCELERATED_X_DATA = "action_accelerated_x_data";
	public static final String ACTION_ACCELERATED_Y_DATA = "action_accelerated_y_data";
	public static final String ACTION_ACCELERATED_Z_DATA = "action_accelerated_z_date";

	List<ValueEntry> ecgList = new ArrayList<ValueEntry>(20000);
	List<ValueEntry> accelerationXlist = new ArrayList<ValueEntry>(1000);
	List<ValueEntry> accelerationYlist = new ArrayList<ValueEntry>(1000);
	List<ValueEntry> accelerationZlist = new ArrayList<ValueEntry>(1000);
	List<ValueEntry> temDatalist = new ArrayList<ValueEntry>(1000);
	List<ValueEntry> tumbleDatalist = new ArrayList<ValueEntry>(1000);
	List<ValueEntry> respList = new ArrayList<ValueEntry>(5000);

	List<ValueEntry> hteWarningList = new ArrayList<ValueEntry>();

	LastUploadManager lastUploadManager = new LastUploadManager(this);
	UploadTaskManager uploadTaskManager = new UploadTaskManager(this);

	List<AccelVector> accelList = new ArrayList<AccelVector>(1000);
	List<ValueEntry> voltageList = new ArrayList<ValueEntry>(1000);

	List<StepCaloriesEntry> stepCaloriesList = new ArrayList<StepCaloriesEntry>(
			1000);

	List<ValueEntry> heartRateList = new ArrayList<ValueEntry>(1000);

	WarningTempManager warningTempManager = new WarningTempManager(this);

	MinuteECGResultManager minuteECGResultManager = new MinuteECGResultManager(
			this);

	Filter filter = new Filter();
	ECGHeartRateCalculator ecgHeartRateCal;

	BreathFilter breathFilter = new BreathFilter();
	RespCalculator respCalculator;

	public final static String ACTION_START_UPLOAD = "com.tiannma.android.bluetooth.le.ACTION_START_UPLOAD";
	public final static String ACTION_CLEAR_STORAGE = "com.tiannma.android.bluetooth.le.ACTION_CLEAR_STORAGE";
	public final static String CLEAR_UID = "com.tiannma.android.bluetooth.le.CLEAR_UID";
	public final static String ACTION_ALERT_SD_STATUS = "com.tiannma.android.bluetooth.le.ACTION_ALERT_SD_STATUS";
	public final static String ALERT_SD_STATUS_LEVEL = "com.tiannma.android.bluetooth.le.ALERT_SD_STATUS_LEVEL";
	public final static String ACTION_RESET_STEP_COUNTER = "com.tiannma.android.bluetooth.le.ACTION_RESET_STEP_COUNTER";
	public final static String ACTION_UPDATE_STEP_AND_CALORIES = "com.tiannma.android.bluetooth.le.ACTION_UPDATE_STEP_AND_CALORIES";
	public final static String STEP_COUNT = "com.tiannma.android.bluetooth.le.STEP_COUNT";
	public final static String CALORIES = "com.tiannma.android.bluetooth.le.CALORIES";
	public final static String ACTION_CURRENT_STATE_CHANGE = "action_current_state_change";
	public final static String ACTION_TEMP = "action_temp";
	public final static String ACTION_UPDATE_ECG_HEART_RATE = "com.tiannma.android.bluetooth.le.ACTION_UPDATE_ECG_HEART_RATE";
	public final static String ECG_HEART_RATE = "com.tiannma.android.bluetooth.le.ECG_HEART_RATE";
	public final static String ACTION_ECG_DATA_AVAILABLE = "com.tiannma.android.bluetooth.le.ACTION_ECG_DATA_AVAILABLE";
	public final static String ACTION_RESP_DATA_AVAILABLE = "com.tiannma.android.bluetooth.le.ACTION_RESP_DATA_AVAILABLE";
	public final static String ACTION_POSTURE_DATA_AVAILABLE = "com.tiannma.android.bluetooth.le.ACTION_POSTURE_DATA_AVAILABLE";
	public final static String ACTION_GAIT_DATA_AVAILABLE = "com.tiannma.android.bluetooth.le.ACTION_GAIT_DATA_AVAILABLE";
	public final static String ACTION_STRESS_DATA_AVAILABLE = "com.tiannma.android.bluetooth.le.ACTION_STRESS_DATA_AVAILABLE";
	public final static String STRESS_DATA = "com.tiannma.android.bluetooth.le.STRESS_DATA";
	public final static String POSTURE_DATA = "com.tiannma.android.bluetooth.le.POSTURE_DATA";
	private static final String ACTION_ALARM_GET_STEP_COUNT_SNAPSHOT = "com.tiannma.android.bluetooth.le.ACTION_ALARM_GET_STEP_COUNT_SNAPSHOT";
	public static final String ACTION_TUMBLE_HAPPENED = "com.tiannma.android.bluetooth.le.ACTION_TUMBLE_HAPPENED";
	private static final String ACTION_ALARM_JUDGE_TUMBLE = "com.tiannma.android.bluetooth.le.ACTION_ALARM_JUDGE_TUMBLE";
	private static final String ACTION_SAVE_ALARM_TO_FILE = "com.tiannma.android.bluetooth.le.ACTION_SAVE_ALARM_TO_FILE";
	private static final String ALARM_TYPE = "com.tiannma.android.bluetooth.le.ALARM_TYPE";
	private static final String ALARM_TIME = "com.tiannma.android.bluetooth.le.ALARM_TIME";
	private static final String ALARM_VALUE = "com.tiannma.android.bluetooth.le.ALARM_VALUE";
	public final static String ACTION_NOTIFY_INVALID_ECG = "com.tiannma.android.bluetooth.le.ACTION_NOTIFY_INVALID_ECG";

	private long mPrevStepCountTimestamp = 0;
	private int mPrevStepCount = 0;
	private boolean isFirstStepCountSnapshot = true;
	private int mLastDeltaStep = 0;
	private final static int WALK_STATE_STAND = 0;
	private final static int WALK_STATE_WALK = 1;
	private final static int WALK_STATE_RUNNING = 2;
	private int mContinuousRunning = 0;
	private Queue<Integer> mHistoryStep = new LinkedList<Integer>();
	private final static int MAX_HISTORY_STEP = 5;
	public byte bPreFrame60SequenceNO = (byte) 0xff;
	public boolean isFirstPoint = false;

	private Object lockPersist = new Object();

	int countFrame60 = 0;
	int countFrame90 = 0;
	int step_state = 0;
	// 换行写入
	String nextLine = System.getProperty("line.separator");
	private byte Current_state;
	private byte Prev_state = 0x20;

	private boolean isFirstX = true;
	private boolean isFirstY = true;
	private boolean isFirstZ = true;

	private byte prevXSeqNo = 0x00;
	private byte prevYSeqNo = 0x00;
	private byte prevZSeqNo = 0x00;

	AccelAnalyzer accelAnalyzer = new AccelAnalyzer();

	private int mGender = 0;
	private int mAge = 0;
	private int mHeight = 0;
	private int mWeight = 0;
	private int mLimitHeartUp = 0;
	private int mLimitHeartDown = 0;

	private static final String ACTION_ALARM_GET_STRESS_LEVEL_ITEM = "com.tiannma.android.bluetooth.le.ACTION_ALARM_GET_STRESS_LEVEL_ITEM";
	WarningHteManager warningHteManager = new WarningHteManager(this);
	WarningTumbleManager warningTumbleManager = new WarningTumbleManager(this);

	public static final String ACTION_STAND_STILL_DATA_AVAILABLE = "com.tiannma.android.bluetooth.le.ACTION_GAIT_DATA_AVAILABLE";
	public static final String STAND_STILL_POSTURE = "com.tiannma.android.bluetooth.le.STAND_STILL_POSTURE";
	public static final String STAND_STILL_TIME = "com.tiannma.android.bluetooth.le.STAND_STILL_TIME";

	public static final int STAND_STILL_STAND = 0;
	public static final int STAND_STILL_SIT = 1;
	public static final int STAND_STILL_UNKNOWN = -1;
	public int mStandStill = STAND_STILL_UNKNOWN;
	private long mStandStillTime = 0;
	private Date mPrevStandStillDate = null;
	private boolean mIsFirstStandStill = true;

	private int mStepCountSnapshot = 0;
	private boolean mIsJudgeTumbleTimerStarted = false;

	private CompressedFilePool mCompressedFilePool = new CompressedFilePool();

	private String mUID = null;

	private int mCountFrameFromConnected = 0;

	private boolean mIsBleConnected = false;
	private final static int SKIP_FRAMES = 100;

	private final static int BACKWARD_TIME = 1000 * 60;

	private int mCountInvalidEcg = 0;

	private StepCountManager mStepCountManager;

	private DetectC7StateMachine mDetectC7StateMachine;

	private Queue<Integer> queueHte = new LinkedList<Integer>();

	private SharedPreferences spTumbleFlag;

	private final static String TUMBLE_FLAG = "TUMBLE_FLAG";
	private final static int TUMBLE_FLAG_ON = 1;
	private final static String TUMBLE_TIME = "TUMBLE_TIME";

	private InvalidStepCheckStateMachine stepCheckStateMachine;

	// ECG / Heart Rate switch mode
	public static final int MODE_ECG_ECG = 1; // ECG
	public static final int MODE_ECG_HEART_RATE = 2; // HEART RATE

	private int mLastEcgMode = MODE_ECG_ECG;
	private int mCurrentEcgMode = MODE_ECG_ECG;

	private Queue<Integer> historyStepCounts = new LinkedList<Integer>();

	private int mLastRecordedStep = 0;
	private Date mLastRecordedStepDate = null;

	private Map<Integer, Float> mTemperatureMap;
	private Map<Integer, Float> mUnitTemperatureMap;
	private boolean mHasTemperatureMapData = false;
	private static final int DELTA_TEMPERATURE = 2;
	private static final int START_TEMPERATURE = 20;
	private static final int END_TEMPERATURE = 40;
	private float mUnitBelowStart = 0.0f;
	private float mUnitUpEnd = 0.0f;

	private void processReceivedByte() {
		for (int i = 0; i < receivedNumber; i++) {
			frameStateMachine.consume(receivedBuffer[i]);
			if (frameStateMachine.isFrameReady()) {
				byte[] frameData = frameStateMachine.getFrame();
				if (frameData.length == 17) {
					int intValueOfByte = Program
							.convertByteToUnsignedInt(frameData[0]);
					if (intValueOfByte != 112) {
						byte[] frame = new byte[frameData.length + 3];
						frame[0] = 0x5A;
						frame[1] = 0x5A;
						frame[2] = 0x12;
						for (int j = 0; j < frameData.length; j++) {
							frame[j + 3] = frameData[j];
						}
						parserData(frame);
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mDataCount++;
						if (mDataCount >= 75) {
							Intent notifyDataAlive = new Intent(
									BluetoothLeService.ACTION_GATT_DATA_ALIVE);
							sendBroadcast(notifyDataAlive);
							mDataCount = 0;
						}
					}
				}
			}
		}
	}

	private void parserData(byte[] data) {
		// Date traceStart = new Date();
		if (mCountFrameFromConnected >= 20000) {
		} else {
			mCountFrameFromConnected++;
		}

		if (!mIsBleConnected)
			return; // 屏蔽掉蓝牙断开之后的数据

		final StringBuilder stringBuilder = new StringBuilder(data.length);
		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}

		if (data != null && data.length > 0) {
			// ---System.out.println("action: data[data != null && data.length > 0]");

			// 获取帧类型
			int dataFrameType = DATA_FRAME_UNKNOWN;
			int intValueOfByte = Program.byteToInt(data[3]);
			if (intValueOfByte == 96) {
				dataFrameType = DATA_FRAME_60;
			}
			if (intValueOfByte == 97) {
				dataFrameType = DATA_FRAME_61;
			}
			if (intValueOfByte == 98) {
				dataFrameType = DATA_FRAME_62;
			}
			if (intValueOfByte == 99) {
				dataFrameType = DATA_FRAME_63;
			}
			if (intValueOfByte == 100) {
				dataFrameType = DATA_FRAME_64;
			}
			if (intValueOfByte == 101) {
				dataFrameType = DATA_FRAME_65;
			}
			if (intValueOfByte == 105) {
				dataFrameType = DATA_FRAME_69;
			}

			if (dataFrameType == DATA_FRAME_60
					|| dataFrameType == DATA_FRAME_61
					|| dataFrameType == DATA_FRAME_62
					|| dataFrameType == DATA_FRAME_63
					|| dataFrameType == DATA_FRAME_64
					|| dataFrameType == DATA_FRAME_65
					|| dataFrameType == DATA_FRAME_69) {

			} else {
				return;
			}

			if (dataFrameType == DATA_FRAME_69) {
				// 心率
				mCurrentEcgMode = MODE_ECG_HEART_RATE;
				// 给连接服务发出心跳信息
				// final Intent notifyDataAlive = new Intent(
				// BluetoothLeService.ACTION_GATT_DATA_ALIVE);
				// sendBroadcast(notifyDataAlive);
			} else {
				// 心电
				mCurrentEcgMode = MODE_ECG_ECG;
			}
			// 如果存在心电心率模式切换则需要清空脏数据
			checkEcgModeChanged();

			if (dataFrameType == DATA_FRAME_69) {
				return;
			}
			// 上传数据
			Date now = new Date();
			// -------------------------------------------
			if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
				// 非登录用户不存储数据
			} else {
				// String lastUpload = lastUploadManager.query();
				String lastUpload = lastUploadManager.queryByUser(mUID);

				if (lastUpload == null) {
					// lastUploadManager.increase(DateUtil.parseDateToInput(
					// now, Program.MINUTE_FORMAT));
					lastUploadManager.increase(DateUtil.parseDateToInput(now,
							Program.MINUTE_FORMAT), mUID);
				} else {
					if (!lastUpload.equals(DateUtil.parseDateToInput(now,
							Program.MINUTE_FORMAT))) {
						// uploadTaskManager.increase(lastUpload);
						// lastUploadManager.update(DateUtil.parseDateToInput(
						// now, Program.MINUTE_FORMAT));

						SimpleDateFormat sdfLastUpload = new SimpleDateFormat(
								Program.MINUTE_FORMAT);
						Date lastUploadDate;
						try {
							lastUploadDate = sdfLastUpload.parse(lastUpload);
						} catch (ParseException e) {
							e.printStackTrace();
							lastUploadDate = null;
						}
						if (lastUploadDate != null) {
							if (!DateUtil.isInTheSameDay(now, lastUploadDate)) {
								mStepCountManager.reset();
								stepCheckStateMachine.reset();
								GlobalStatus.getInstance().setCalories(0);
								GlobalStatus.getInstance().setCurrentStep(0);

								// Notify UI that new step and calories
								// data are available
								final Intent updateIntent = new Intent(
										ACTION_UPDATE_STEP_AND_CALORIES);
								sendBroadcast(updateIntent);
							}
						}

						uploadTaskManager.increase(lastUpload, mUID);
						lastUploadManager.update(DateUtil.parseDateToInput(now,
								Program.MINUTE_FORMAT), mUID);

						if (UserInfo.isInDebugMode()) {
						} else {

							if (isLastUploadLastMinute(lastUpload)) {
								persistData(lastUpload);
							}

							mCompressedFilePool.flushCompressFiles();
							mCompressedFilePool.openCompressFiles(DateUtil
									.parseDateToInput(now,
											Program.MINUTE_FORMAT), mUID);
						}

						// notifyUpload(lastUpload);
						// saveECGResultToDB(lastUpload);
						notifyUpload(lastUpload, mUID);
						saveECGResultToDB(lastUpload, mUID);
					}
				}
			}
			// 心率模式为69低功耗帧,其他60-65
			if (dataFrameType != DATA_FRAME_69) {
				// 取5-14共10字节按照5,6；7,8；9,10；11,12；13,14组成5字节int数组
				int[] origECGData = Program.getECGValues(data);

				mDetectC7StateMachine.consume(Program.isEcgC7(data));

				// 10个字节
				int[] ECGData = new int[origECGData.length * 2];
				for (int i = 0; i < origECGData.length; i++) {
					ECGData[i * 2] = origECGData[i];
					ECGData[i * 2 + 1] = origECGData[i];
				}
				// ---System.out.println("action StorageService ecgdata "+Arrays.toString(ECGData));
				// 取15-16字节转为一个int
				int[] origRespData = Program.getResp(data);
				/**
				 * 取15-16字节转为一个int
				 */
				int[] respData = new int[origRespData.length * 2];
				for (int i = 0; i < origRespData.length; i++) {
					respData[i * 2] = origRespData[i];
					respData[i * 2 + 1] = origRespData[i];
				}

				// isFirstPoint默认值为true第一次执行此方法是为true
				if (isFirstPoint) {
					isFirstPoint = false;
				} else {
					int uintValOfFrame60SeqNo = Program
							.convertByteToUnsignedInt(data[4]);
					int uintValOfPreFrame60SeqNo = Program
							.convertByteToUnsignedInt(bPreFrame60SequenceNO);

					if ((uintValOfFrame60SeqNo - uintValOfPreFrame60SeqNo) > 1) {

						// 修补漏掉的帧数据
						// fix the lost data
						int lostFrame = uintValOfFrame60SeqNo
								- uintValOfPreFrame60SeqNo - 1;

						lostFrame = lostFrame * 2;

						int[] lostECGData = new int[lostFrame * 5];
						for (int i = 0; i < lostFrame * 5; i++) {
							lostECGData[i] = ECGData[0];
						}

						int[] lostRespData = new int[lostFrame * 2];
						for (int i = 0; i < lostFrame * 2; i++) {
							lostRespData[i] = respData[0];
						}

						notifyDataAvailable(ACTION_ECG_DATA_AVAILABLE,
								"ECGData", lostECGData);
						notifyDataAvailable(ACTION_RESP_DATA_AVAILABLE,
								"RespData", lostRespData);

						if (!mDetectC7StateMachine.isInMaskStatus()) {
							addDataToList(ecgList, lostECGData);
							addDataToList(respList, lostRespData);
						} else {
							int[] tmpECGData = new int[lostECGData.length];
							for (int i = 0; i < lostECGData.length; i++) {
								tmpECGData[i] = Program.INVALID_ECG_VALUE;
							}
							addDataToList(ecgList, tmpECGData);

							int[] tmpResp = new int[lostRespData.length];
							for (int i = 0; i < lostRespData.length; i++) {
								tmpResp[i] = Program.INVALID_ECG_VALUE;
							}
							addDataToList(respList, tmpResp);
						}

						// ---添加滤波器---
						for (int i = 0; i < lostECGData.length; i++) {
							try {
								filter.addData(lostECGData[i]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < lostECGData.length; i++) {
							Date ecgDate = new Date();
							try {
								ecgHeartRateCal.pushECGData(ecgDate.getTime(),
										filter.getOne());
								// checkECGCalculateDone();
								// checkECGCalculateDone(mUID);
								checkECGCalculateDone(mUID,
										Program.isValidEcg(data));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// for (int i = 0; i < lostECGData.length; i++)
						// {
						// Date ecgDate = new Date();
						// ecgHeartRateCal.pushECGData(ecgDate.getTime(),
						// lostECGData[i]);
						// }

						for (int i = 0; i < lostRespData.length; i++) {
							try {
								breathFilter.addData(lostRespData[i]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < lostRespData.length; i++) {
							if (breathFilter.canGetOne()) {
								Date respDate = new Date();
								try {
									respCalculator.pushRespData(
											respDate.getTime(),
											breathFilter.getOne());
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								break;
							}
						}

						// totalFrame60fixed += lostFrame;

					} else if (uintValOfFrame60SeqNo < uintValOfPreFrame60SeqNo) {
						if (uintValOfFrame60SeqNo > 10)
							return;

						// fix the lost data
						int lostFrame = (255 - uintValOfPreFrame60SeqNo)
								+ uintValOfFrame60SeqNo;

						lostFrame = lostFrame * 2;

						int[] lostECGData = new int[lostFrame * 5];
						for (int i = 0; i < lostFrame * 5; i++) {
							lostECGData[i] = ECGData[0];
						}

						int[] lostRespData = new int[lostFrame * 2];
						for (int i = 0; i < lostFrame * 2; i++) {
							lostRespData[i] = respData[0];
						}

						notifyDataAvailable(ACTION_ECG_DATA_AVAILABLE,
								"ECGData", lostECGData);
						notifyDataAvailable(ACTION_RESP_DATA_AVAILABLE,
								"RespData", lostRespData);

						if (!mDetectC7StateMachine.isInMaskStatus()) {
							addDataToList(ecgList, lostECGData);
							addDataToList(respList, lostRespData);
						} else {
							int[] tmpECGData = new int[lostECGData.length];
							for (int i = 0; i < lostECGData.length; i++) {
								tmpECGData[i] = Program.INVALID_ECG_VALUE;
							}
							addDataToList(ecgList, tmpECGData);

							int[] tmpResp = new int[lostRespData.length];
							for (int i = 0; i < lostRespData.length; i++) {
								tmpResp[i] = Program.INVALID_ECG_VALUE;
							}
							addDataToList(respList, tmpResp);
						}

						// ---添加滤波器---
						for (int i = 0; i < lostECGData.length; i++) {
							try {
								filter.addData(lostECGData[i]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < lostECGData.length; i++) {
							Date ecgDate = new Date();
							try {
								ecgHeartRateCal.pushECGData(ecgDate.getTime(),
										filter.getOne());
								// checkECGCalculateDone();
								// checkECGCalculateDone(mUID);
								checkECGCalculateDone(mUID,
										Program.isValidEcg(data));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// for (int i = 0; i < lostECGData.length; i++)
						// {
						// Date ecgDate = new Date();
						// ecgHeartRateCal.pushECGData(ecgDate.getTime(),
						// lostECGData[i]);
						// }
						for (int i = 0; i < lostRespData.length; i++) {
							try {
								breathFilter.addData(lostRespData[i]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < lostRespData.length; i++) {
							if (breathFilter.canGetOne()) {
								Date respDate = new Date();
								try {
									respCalculator.pushRespData(
											respDate.getTime(),
											breathFilter.getOne());
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								break;
							}
						}

						// totalFrame60fixed += lostFrame;
					}
				}
				// 取帧序号
				bPreFrame60SequenceNO = data[4];

				// ---添加滤波器---
				for (int i = 0; i < ECGData.length; i++) {
					try {
						filter.addData(ECGData[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < ECGData.length; i++) {
					if (filter.canGetOne()) {
						Date ecgDate = new Date();
						try {
							ecgHeartRateCal.pushECGData(ecgDate.getTime(),
									filter.getOne());
							// checkECGCalculateDone();
							// checkECGCalculateDone(mUID);
							checkECGCalculateDone(mUID,
									Program.isValidEcg(data));
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
				// for (int i = 0; i < ECGData.length; i++) {
				// Date ecgDate = new Date();
				// ecgHeartRateCal.pushECGData(ecgDate.getTime(),
				// ECGData[i]);
				// }
				for (int i = 0; i < respData.length; i++) {
					try {
						breathFilter.addData(respData[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < respData.length; i++) {
					if (breathFilter.canGetOne()) {
						Date respDate = new Date();
						try {
							respCalculator.pushRespData(respDate.getTime(),
									breathFilter.getOne());
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}

				notifyDataAvailable(ACTION_ECG_DATA_AVAILABLE, "ECGData",
						ECGData);
				notifyDataAvailable(ACTION_RESP_DATA_AVAILABLE, "RespData",
						respData);

				if (!mDetectC7StateMachine.isInMaskStatus()) {
					addDataToList(ecgList, ECGData);
					addDataToList(respList, respData);
				} else {
					int[] tmpECGData = new int[ECGData.length];
					for (int i = 0; i < ECGData.length; i++) {
						tmpECGData[i] = Program.INVALID_ECG_VALUE;
					}
					addDataToList(ecgList, tmpECGData);

					int[] tmpResp = new int[respData.length];
					for (int i = 0; i < respData.length; i++) {
						tmpResp[i] = Program.INVALID_ECG_VALUE;
					}
					addDataToList(respList, tmpResp);
				}

				GlobalStatus.getInstance().setBreath(
						respCalculator.getRespRate());
			}

			if (dataFrameType == DATA_FRAME_69) {
				int[] heartRate = Program.getHeartRate(data);
				addDataToList(heartRateList, heartRate);
				checkECGCalculateDoneByHeartRate(heartRate[0], mUID,
						Program.isValidEcg(data));
			}

			// if (dataFrameType == DATA_FRAME_60) {
			if (dataFrameType == DATA_FRAME_60
					|| dataFrameType == DATA_FRAME_69) {
				if (Program.isValidEcg(data)) {

					if (dataFrameType == DATA_FRAME_60) {
						byte[] tumbleData = Program.getTumbleDatas(data);
						int firstBit = Program.getFirstBitOf(tumbleData[0]);
						if (firstBit == 1) {
							EventLogger.logEvent("TUMBLE frame detected ["
									+ MiscUtils.dumpBytesAsString(data) + "]");
							// Date nowDate = new Date();
							// warningTumbleManager.increase(Program.getMinuteDataByDate(nowDate));
							// // tmblStrList.add(new
							// TumbleStrEntry(nowDate.getTime(),
							// stringBuilder.toString()));
							// GlobalAccelCalculator.getInstance().setFreeFalling();
							// notifyUITumbleHappened();
							if (mCountFrameFromConnected > (SKIP_FRAMES * 3)) {
								mStepCountSnapshot = GlobalStatus.getInstance()
										.getCurrentStep();
								if (mIsJudgeTumbleTimerStarted == true) {
									stopJudgeTumbleTimer();
								}
								// startJudgeTumbleTimer();
								startJudgeTumbleTimer(mUID);
							}
						}
						// if
						// (Program.convertByteToUnsignedInt(tumbleData[0])
						// == 170) {
						// EventLogger.logEvent("TUMBLE frame detected ["
						// + MiscUtils.dumpBytesAsString(data) + "]");
						// mStepCountSnapshot =
						// GlobalStatus.getInstance().getCurrentStep();
						// if (mIsJudgeTumbleTimerStarted == true) {
						// stopJudgeTumbleTimer();
						// }
						// startJudgeTumbleTimer(mUID);
						// }
					} else { // Frame 69
						byte[] tumbleData = Program
								.getTumbleDataByNewFrame(data);
						int firstBit = Program.getFirstBitOf(tumbleData[0]);
						if (firstBit == 1) {
							EventLogger.logEvent("TUMBLE frame detected ["
									+ MiscUtils.dumpBytesAsString(data) + "]");
							if (mCountFrameFromConnected > (SKIP_FRAMES * 2)) {
								mStepCountSnapshot = GlobalStatus.getInstance()
										.getCurrentStep();
								if (mIsJudgeTumbleTimerStarted == true) {
									stopJudgeTumbleTimer();
								}
								// startJudgeTumbleTimer();
								startJudgeTumbleTimer(mUID);
							}
						}
						// // if (tumbleData[0] == 0xAA) {
						// if
						// (Program.convertByteToUnsignedInt(tumbleData[0])
						// == 170) {
						// EventLogger.logEvent("TUMBLE frame detected2 ["
						// + MiscUtils.dumpBytesAsString(data) + "]");
						// mStepCountSnapshot =
						// GlobalStatus.getInstance().getCurrentStep();
						// if (mIsJudgeTumbleTimerStarted == true) {
						// stopJudgeTumbleTimer();
						// }
						// startJudgeTumbleTimer(mUID);
						// }
					}
				}
			}

			if (dataFrameType == DATA_FRAME_61) {

				if (isFirstX) {
					isFirstX = false;
					prevXSeqNo = getSeqNo(data);

					// int[] accelerationX =
					// Program.getAccelerationXData(data);
					int[] accelerationX = duplicateData(Program
							.getAccelerationXData(data));

					accelAceptX(Program.convertByteToUnsignedInt(prevXSeqNo),
							accelerationX[0]);

					addDataToList(accelerationXlist, accelerationX);
				} else {
					byte xSeqNo = getSeqNo(data);
					int xVal = Program.convertByteToUnsignedInt(xSeqNo);
					int prevXVal = Program.convertByteToUnsignedInt(prevXSeqNo);

					if (xVal > prevXVal) {
						if ((xVal - prevXVal) < 6) {
						}
						if ((xVal - prevXVal) == 6) { // 没有丢帧
							// int[] accelerationX =
							// Program.getAccelerationXData(data);
							int[] accelerationX = duplicateData(Program
									.getAccelerationXData(data));

							accelAceptX(xVal, accelerationX[0]);

							addDataToList(accelerationXlist, accelerationX);

							Intent accelerated_x = new Intent(
									ACTION_ACCELERATED_X);
							accelerated_x.putExtra(ACTION_ACCELERATED_X_DATA,
									accelerationX[0]);
							sendBroadcast(accelerated_x);

							prevXSeqNo = xSeqNo;
						}
						if ((xVal - prevXVal) > 6) {
							if ((xVal - prevXVal) % 6 != 0) {
								// error data
								// skip
							} else {
								int[] accelerationX = duplicateData(Program
										.getAccelerationXData(data));

								int lostNum = ((xVal - prevXVal) / 6) - 1;
								int[] lostData = new int[lostNum * 2];
								for (int i = 0; i < lostNum; i++) {
									lostData[i * 2] = accelerationX[0];
									lostData[i * 2 + 1] = accelerationX[0];

									accelAceptX(getNextVal(prevXVal, i + 1),
											accelerationX[0]);
								}
								addDataToList(accelerationXlist, lostData);

								accelAceptX(xVal, accelerationX[0]);
								addDataToList(accelerationXlist, accelerationX);

								prevXSeqNo = xSeqNo;
							}
						}
					} else { // 序号反转
						if ((255 - prevXVal + xVal + 1) == 6) {// 没有丢帧
							int[] accelerationX = duplicateData(Program
									.getAccelerationXData(data));

							accelAceptX(xVal, accelerationX[0]);
							addDataToList(accelerationXlist, accelerationX);

							prevXSeqNo = xSeqNo;
						}
						if ((255 - prevXVal + xVal + 1) > 6) {
							int[] accelerationX = duplicateData(Program
									.getAccelerationXData(data));

							int lostNum = (255 - prevXVal + xVal + 1) / 6 - 1;

							int[] lostData = new int[lostNum * 2];
							for (int i = 0; i < lostNum; i++) {
								lostData[i * 2] = accelerationX[0];
								lostData[i * 2 + 1] = accelerationX[0];

								accelAceptX(getNextVal(prevXVal, i + 1),
										accelerationX[0]);
							}
							addDataToList(accelerationXlist, lostData);

							accelAceptX(xVal, accelerationX[0]);
							addDataToList(accelerationXlist, accelerationX);

							prevXSeqNo = xSeqNo;
						}
					}
				}

				// int[] accelerationX =
				// Program.getAccelerationXData(data);
				// System.out.println("action DataStorage accelerationX:"
				// + accelerationX[0]);
				// addDataToList(accelerationXlist, accelerationX);
			}

			if (dataFrameType == DATA_FRAME_62) {

				if (isFirstY) {
					isFirstY = false;
					prevYSeqNo = getSeqNo(data);

					// int[] accelerationX =
					// Program.getAccelerationXData(data);
					int[] accelerationY = duplicateData(Program
							.getAccelerationYData(data));

					accelAceptY(prevYSeqNo, accelerationY[0]);

					addDataToList(accelerationYlist, accelerationY);
				} else {
					byte ySeqNo = getSeqNo(data);
					int yVal = Program.convertByteToUnsignedInt(ySeqNo);
					int prevYVal = Program.convertByteToUnsignedInt(prevYSeqNo);

					if (yVal > prevYVal) {
						if ((yVal - prevYVal) < 6) {
						}
						if ((yVal - prevYVal) == 6) { // 没有丢帧
							// int[] accelerationX =
							// Program.getAccelerationXData(data);
							int[] accelerationY = duplicateData(Program
									.getAccelerationYData(data));

							accelAceptY(yVal, accelerationY[0]);
							addDataToList(accelerationYlist, accelerationY);

							Intent accelerated_y = new Intent(
									ACTION_ACCELERATED_Y);
							accelerated_y.putExtra(ACTION_ACCELERATED_Y_DATA,
									accelerationY[0]);
							sendBroadcast(accelerated_y);

							prevYSeqNo = ySeqNo;
						}
						if ((yVal - prevYVal) > 6) {
							if ((yVal - prevYVal) % 6 != 0) {
								// error data
								// skip
							} else {
								int[] accelerationY = duplicateData(Program
										.getAccelerationYData(data));

								int lostNum = ((yVal - prevYVal) / 6) - 1;
								int[] lostData = new int[lostNum * 2];
								for (int i = 0; i < lostNum; i++) {
									lostData[i * 2] = accelerationY[0];
									lostData[i * 2 + 1] = accelerationY[0];

									accelAceptY(getNextVal(prevYVal, i + 1),
											accelerationY[0]);
								}
								addDataToList(accelerationYlist, lostData);

								accelAceptY(yVal, accelerationY[0]);
								addDataToList(accelerationYlist, accelerationY);

								prevYSeqNo = ySeqNo;
							}
						}
					} else { // 序号反转
						if ((255 - prevYVal + yVal + 1) == 6) {// 没有丢帧
							int[] accelerationY = duplicateData(Program
									.getAccelerationYData(data));

							accelAceptY(yVal, accelerationY[0]);
							addDataToList(accelerationYlist, accelerationY);

							prevYSeqNo = ySeqNo;
						}
						if ((255 - prevYVal + yVal + 1) > 6) {
							int[] accelerationY = duplicateData(Program
									.getAccelerationYData(data));

							int lostNum = (255 - prevYVal + yVal + 1) / 6 - 1;

							int[] lostData = new int[lostNum * 2];
							for (int i = 0; i < lostNum; i++) {
								lostData[i * 2] = accelerationY[0];
								lostData[i * 2 + 1] = accelerationY[0];

								accelAceptY(getNextVal(prevYVal, i + 1),
										accelerationY[0]);
							}
							addDataToList(accelerationYlist, lostData);

							accelAceptY(yVal, accelerationY[0]);
							addDataToList(accelerationYlist, accelerationY);

							prevYSeqNo = ySeqNo;
						}
					}
				}

			}

			if (dataFrameType == DATA_FRAME_63) {

				if (isFirstZ) {
					isFirstZ = false;
					prevZSeqNo = getSeqNo(data);

					// int[] accelerationX =
					// Program.getAccelerationXData(data);
					int[] accelerationZ = duplicateData(Program
							.getAccelerationZData(data));

					accelAceptZ(prevZSeqNo, accelerationZ[0]);
					addDataToList(accelerationZlist, accelerationZ);
				} else {
					byte zSeqNo = getSeqNo(data);
					int zVal = Program.convertByteToUnsignedInt(zSeqNo);
					int prevZVal = Program.convertByteToUnsignedInt(prevZSeqNo);

					if (zVal > prevZVal) {
						if ((zVal - prevZVal) < 6) {
						}
						if ((zVal - prevZVal) == 6) { // 没有丢帧
							// int[] accelerationX =
							// Program.getAccelerationXData(data);
							int[] accelerationZ = duplicateData(Program
									.getAccelerationZData(data));

							accelAceptZ(zVal, accelerationZ[0]);
							addDataToList(accelerationZlist, accelerationZ);

							Intent accelerated_z = new Intent(
									ACTION_ACCELERATED_Z);
							accelerated_z.putExtra(ACTION_ACCELERATED_Z_DATA,
									accelerationZ[0]);
							sendBroadcast(accelerated_z);
							prevZSeqNo = zSeqNo;
						}
						if ((zVal - prevZVal) > 6) {
							if ((zVal - prevZVal) % 6 != 0) {
								// error data
								// skip
							} else {
								int[] accelerationZ = duplicateData(Program
										.getAccelerationZData(data));

								int lostNum = ((zVal - prevZVal) / 6) - 1;
								int[] lostData = new int[lostNum * 2];
								for (int i = 0; i < lostNum; i++) {
									lostData[i * 2] = accelerationZ[0];
									lostData[i * 2 + 1] = accelerationZ[0];

									accelAceptZ(getNextVal(prevZVal, i + 1),
											accelerationZ[0]);
								}
								addDataToList(accelerationZlist, lostData);

								accelAceptZ(zVal, accelerationZ[0]);
								addDataToList(accelerationZlist, accelerationZ);

								prevZSeqNo = zSeqNo;
							}
						}
					} else { // 序号反转
						if ((255 - prevZVal + zVal + 1) == 6) {// 没有丢帧
							int[] accelerationZ = duplicateData(Program
									.getAccelerationZData(data));

							accelAceptZ(zVal, accelerationZ[0]);
							addDataToList(accelerationZlist, accelerationZ);

							prevZSeqNo = zSeqNo;
						}
						if ((255 - prevZVal + zVal + 1) > 6) {
							int[] accelerationZ = duplicateData(Program
									.getAccelerationZData(data));

							int lostNum = (255 - prevZVal + zVal + 1) / 6 - 1;

							int[] lostData = new int[lostNum * 2];
							for (int i = 0; i < lostNum; i++) {
								lostData[i * 2] = accelerationZ[0];
								lostData[i * 2 + 1] = accelerationZ[0];

								accelAceptZ(getNextVal(prevZVal, i + 1),
										accelerationZ[0]);
							}
							addDataToList(accelerationZlist, lostData);

							accelAceptZ(zVal, accelerationZ[0]);
							addDataToList(accelerationZlist, accelerationZ);

							prevZSeqNo = zSeqNo;
						}
					}
				}

				int loop = 0;
				while (isAccelVectorReady()) {
					loop++;

					int[] xyz = getAccelXYZ();
					if (xyz != null) {
						AccelVector accelVector = new AccelVector();

						Date accelDate = new Date();
						accelVector.timestamp = accelDate.getTime();

						accelVector.acceX = xyz[0];
						accelVector.acceY = xyz[1];
						accelVector.acceZ = xyz[2];

						accelList.add(accelVector);

						GlobalAccelCalculator.getInstance().pushAccelData(
								accelDate.getTime(), accelVector.acceX,
								accelVector.acceY, accelVector.acceZ);

						if (UserInfo.isDebugVersion()) {
							checkAccelCalculateDoneDebug();
						} else {
							checkAccelCalculateDone();
						}

						GlobalAccelCalculator.getInstance().pushAccelData(
								accelDate.getTime(), accelVector.acceX,
								accelVector.acceY, accelVector.acceZ);

						if (UserInfo.isDebugVersion()) {
							checkAccelCalculateDoneDebug();
						} else {
							checkAccelCalculateDone();
						}

						// int posture =
						// GlobalAccelCalculator.getInstance().getPosture();
						// System.out.println("action DataStorage GlobalAccelCalculator getPosture:"
						// + posture);
						// int[] postureData = new int[2];
						// postureData[0] = posture;
						// // sendData(ACTION_POSTURE_DATA_AVAILABLE,
						// "PostureData", postureData);
						//
						// int gait =
						// GlobalAccelCalculator.getInstance().getGait();
						// System.out.println("action DataStorage GlobalAccelCalculator getGait:"
						// + gait);
						// // int[] gaitData = new int[1];
						// // gaitData[0] = gait;
						// postureData[1] = gait;
						// // sendData(ACTION_GAIT_DATA_AVAILABLE,
						// "GaitData", gaitData);
						//
						// sendData(ACTION_POSTURE_DATA_AVAILABLE,
						// POSTURE_DATA, postureData);
					}

					if (loop > 1000) {
						accelAnalyzer.reset();
					}
				}

			}

			if (dataFrameType == DATA_FRAME_64
					|| dataFrameType == DATA_FRAME_69) {
				if (dataFrameType == DATA_FRAME_64) {
					if (UserInfo.isDebugVersion()) {
						checkStepAndCaloriesDebug(data);
					} else {
						// if (Program.isValidEcg(data)) {
						checkStepAndCalories(data);
						// }
					}
					// int[] stepCount = Program.getStepCountData(data);
					// System.out.println("action DataStorageService stepCount:"
					// + stepCount[0]);
					// if(step_state!=stepCount[0]){
					// step_state=stepCount[0];
					//
					// Intent updateIntent = new
					// Intent(ACTION_UPDATE_STEP_AND_CALORIES);
					// updateIntent.putExtra(STEP_COUNT,stepCount[0]);
					// sendBroadcast(updateIntent);
					// }
					//
					// GlobalStatus.getInstance().setCurrentStep(stepCount[0]);
				} else {
					// if (Program.isValidEcg(data)) {
					checkStepAndCaloriesByNewFrame(data);
					// }
				}
			}

			if (dataFrameType == DATA_FRAME_65
					|| dataFrameType == DATA_FRAME_69) {
				if (dataFrameType == DATA_FRAME_65) {
					int[] TEMPData = Program.getTemp(data);

					float mappedTemp = getMappedTemperature(TEMPData[0] * 1.0f / 5);

					// GlobalStatus.getInstance().setTemp(TEMPData[0] *
					// 1.0f / 5);
					GlobalStatus.getInstance().setTemp(mappedTemp);

					// addDataToLists(temDatalist, TEMPData[0]);
					addDataToList(temDatalist, TEMPData);

					int[] voltageData = Program.getVoltageData(data);
					GlobalStatus.getInstance().setVoltage(
							voltageData[0] * 1.0f / 50);
					// addDataToLists(voltageList, voltageData[0]);
					addDataToList(voltageList, voltageData);

				} else {
					int[] TEMPData = Program.getTempByNewFrame(data);

					GlobalStatus.getInstance().setTemp(TEMPData[0] * 1.0f / 5);
					// addDataToLists(temDatalist, TEMPData[0]);
					addDataToList(temDatalist, TEMPData);

					int[] voltageData = Program.getVoltageDataByNewFrame(data);
					GlobalStatus.getInstance().setVoltage(
							voltageData[0] * 1.0f / 50);
					// addDataToLists(voltageList, voltageData[0]);
					addDataToList(voltageList, voltageData);

				}
			}

			if (UserInfo.isInDebugMode()) {
				countFrame60++;
				if (countFrame60 == 50) {
					if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
					} else {
						// saveFrame60(DateUtil.parseDateToInput(now,
						// Program.MINUTE_FORMAT));
						//
						// saveFrame90(DateUtil.parseDateToInput(now,Program.MINUTE_FORMAT));
						saveFrame60(DateUtil.parseDateToInput(now,
								Program.MINUTE_FORMAT), mUID);
						saveFrame90(DateUtil.parseDateToInput(now,
								Program.MINUTE_FORMAT), mUID);
					}

					ecgList.clear();
					heartRateList.clear();
					respList.clear();

					tumbleDatalist.clear();
					// tmblStrList.clear();

					temDatalist.clear();
					voltageList.clear();
					accelerationXlist.clear();
					accelerationYlist.clear();
					accelerationZlist.clear();
					accelList.clear();

					hteWarningList.clear();
					stepCaloriesList.clear();

					countFrame60 = 0;
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
		frameStateMachine.reset();
		mDataCount = 0;
		receivedNumber = 0;
		receivedQueue.clear();
		startTimer();

		acquireWakeLock();
		// reastOncreat();
		ecgHeartRateCal = new ECGHeartRateCalculator();
		filter.reset();
		isFirstX = true;
		isFirstY = true;
		isFirstZ = true;

		prevXSeqNo = 0x00;
		prevYSeqNo = 0x00;
		prevZSeqNo = 0x00;

		respCalculator = new RespCalculator();
		breathFilter.reset();
		// ecgHeartRateCal.setECGOFiles(null, null,
		// null,
		// Program.getSDPath() + File.separator + "ecgDebug_angle.csv");
		isFirstPoint = true;

		mStepCountManager = new StepCountManager(this);

		mCountFrameFromConnected = 0;
		mIsBleConnected = false;

		mCountInvalidEcg = 0;

		queueHte.clear();

		// ecgNotificationTime = 0;

		initEcgMode();

		accelAnalyzer.reset();
		mCompressedFilePool.reset();

		getUserInfo();
		// ecgHeartRateCal.setGender((char) mGender);
		if (mGender == 1) { // men
			ecgHeartRateCal.setGender((char) 1);
		} else if (mGender == 0) { // women
			ecgHeartRateCal.setGender((char) 2);
		} else {
			ecgHeartRateCal.setGender((char) 0);
		}

		ecgHeartRateCal.setGender((char) mGender);
		ecgHeartRateCal.setAge((short) mAge);

		GlobalAccelCalculator.getInstance().setHeight((short) mHeight);
		GlobalAccelCalculator.getInstance().setWeight((short) mWeight);

		// GlobalAccelCalculator.getInstance().setAccelInitXYZ(1002, -78, -164);
		GlobalAccelCalculator.getInstance().setAccelInitXYZ(-7, 1030, -55);

		mHasTemperatureMapData = false;
		mUnitTemperatureMap = null;
		mTemperatureMap = null;
		getTemperatureMap();

		startGetStressLevelTimer();
		startGetStepCountSnapshotTimer();

		mDetectC7StateMachine = new DetectC7StateMachine();
		mDetectC7StateMachine.reset();

		spTumbleFlag = this.getSharedPreferences(TUMBLE_FLAG, MODE_PRIVATE);

		stepCheckStateMachine = new InvalidStepCheckStateMachine();
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mGattUpdateReceiver);
		cancelTimer();
		receivedQueue.clear();
		receivedQueue = null;

		releaseWarkLock();
		stopGetStressLevelTimer();
		stopGetStepCountSnapshotTimer();
		super.onDestroy();
	};

	public void startTimer() {
		mTask = new TimerTask() {
			@Override
			public void run() {
				synchronized (receivedQueue) {
					receivedNumber = 0;
					int qLen = receivedQueue.size();
					for (int i = 0; i < receivedBuffer.length && i < qLen; i++) {
						receivedBuffer[i] = receivedQueue.poll();
						receivedNumber++;
					}
				}
				processReceivedByte();
			}
		};
		mTimer.schedule(mTask, 0, 40);
		// mTimer.schedule(mTask, 0, 40);
	}

	public void cancelTimer() {
		mTimer.cancel();
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				byte[] data = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				synchronized (receivedQueue) {
					for (int i = 0; i < data.length; i++) {
						receivedQueue.offer(data[i]);
					}
				}
			} else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

				isFirstStepCountSnapshot = true;
				mLastDeltaStep = 0;
				mContinuousRunning = 0;
				mHistoryStep.clear();

				mCountFrameFromConnected = 0;
				mIsBleConnected = true;

				queueHte.clear();

				step_state = 0;

				GlobalStatus.getInstance().setCurrentStep(
						mStepCountManager.getLastStepCount());
				GlobalStatus.getInstance().setCalories(
						GlobalAccelCalculator.getInstance().getCalories());

				stepCheckStateMachine.reset();

				historyStepCounts.clear();

				clearLastRecordedStep();

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {

				mIsBleConnected = false;
				step_state = 0;

				if (UserInfo.isInDebugMode()) {
				} else {
					if (mCompressedFilePool.getMinuteData() != null) {
						persistData(mCompressedFilePool.getMinuteData());

						mCompressedFilePool.flushCompressFiles();
					}
				}

				if (stepCheckStateMachine != null) {
					if ((InvalidStepCheckStateMachine.S_FIRST_INVALID == stepCheckStateMachine
							.getCurrentState())
							|| (InvalidStepCheckStateMachine.S_INVALID == stepCheckStateMachine
									.getCurrentState())) {

						int stepCountBase = mStepCountManager
								.getStepCountBase();
						int subInvalidCount = stepCheckStateMachine
								.getSubInvalidStep();

						mStepCountManager.setStepCountBase(stepCountBase
								- subInvalidCount);
					}
				}

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {

			} else if (ACTION_RESET_STEP_COUNTER.equals(action)) {
			} else if (ACTION_ALARM_GET_STRESS_LEVEL_ITEM.equals(action)) {
				StressLevelItem item = ecgHeartRateCal.getStressLevelItem();
				GlobalStatus.getInstance().appendStressLevelItem(item);
			} else if (ACTION_ALARM_GET_STEP_COUNT_SNAPSHOT.equals(action)) {
				if (isFirstStepCountSnapshot) {
					isFirstStepCountSnapshot = false;
					Date now = new Date();
					mPrevStepCountTimestamp = now.getTime();
					mPrevStepCount = GlobalStatus.getInstance()
							.getCurrentStep();
				} else {
					Date now = new Date();
					long timestamp = now.getTime();
					int stepCount = GlobalStatus.getInstance().getCurrentStep();

					long deltaTime = timestamp - mPrevStepCountTimestamp;
					int deltaSteps = GlobalStatus.getInstance()
							.getCurrentStep() - mPrevStepCount;

					mPrevStepCountTimestamp = timestamp;
					mPrevStepCount = stepCount;
					GlobalAccelCalculator.getInstance().pushDeltaSteps(
							deltaTime, deltaSteps, stepCount);
				}
			} else if (ACTION_ALARM_JUDGE_TUMBLE.equals(action)) {
				mIsJudgeTumbleTimerStarted = false;
				if (GlobalStatus.getInstance().getCurrentStep() == mStepCountSnapshot) {
					Date nowDate = new Date();
					// warningTumbleManager.increase(Program.getMinuteDataByDate(nowDate));
					String uid = intent.getStringExtra("UID");
					// warningTumbleManager.increase(Program.getMinuteDataByDate(nowDate),
					// mUID);
					warningTumbleManager.increase(
							Program.getMinuteDataByDate(nowDate), uid);
					// tmblStrList.add(new TumbleStrEntry(nowDate.getTime(),
					// stringBuilder.toString()));
					GlobalAccelCalculator.getInstance().setFreeFalling();

					EventLogger.logEvent("TUMBLE detected");

					// int[] tumble = new int[1];
					// tumble[0] = 1;
					// addDataToList(tumbleDatalist, tumble);
					if (uid != null && uid.length() > 0) {
						Date now = new Date();
						new SendRealTimeAlarmThread("5", now.getTime(), 1)
								.start();
					}

					setTumbleFlag();
					notifyUITumbleHappened();
				}
			} else if (ACTION_SAVE_ALARM_TO_FILE.equals(action)) {
				String type = intent.getStringExtra(ALARM_TYPE);
				long timestamp = intent.getLongExtra(ALARM_TIME, 0);
				int value = intent.getIntExtra(ALARM_VALUE, 0);

				ValueEntry entry = new ValueEntry(timestamp, value);

				if ("1".equals(type)) {
					hteWarningList.add(entry);
				}

				if ("5".equals(type)) {
					tumbleDatalist.add(entry);
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
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(ACTION_RESET_STEP_COUNTER);
		intentFilter.addAction(ACTION_ALARM_GET_STRESS_LEVEL_ITEM);
		intentFilter.addAction(ACTION_ALARM_GET_STEP_COUNT_SNAPSHOT);
		intentFilter.addAction(ACTION_ALARM_JUDGE_TUMBLE);
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

	private void sendIntentToSaveAlarm(String type, long timestamp, int value) {
		Intent intent = new Intent(ACTION_SAVE_ALARM_TO_FILE);
		intent.putExtra(ALARM_TYPE, type);
		intent.putExtra(ALARM_TIME, timestamp);
		intent.putExtra(ALARM_VALUE, value);

		sendBroadcast(intent);
	}

	class SendRealTimeAlarmThread extends Thread {
		String type;
		long timestamp;
		int value;

		public SendRealTimeAlarmThread(String type, long timestamp, int value) {
			this.type = type;
			this.timestamp = timestamp;
			this.value = value;
		}

		@Override
		public void run() {
			String uid = UserInfo.getIntance().getUserData().getMy_name();
			if ((uid != null) && (uid.length() > 0)) {
				String upperLimit;
				String floorLimit;

				if ("1".equals(type)) {
					int heartUp = UserInfo.getIntance().getUserData()
							.getLimit_heart_up();
					upperLimit = Integer.toString(heartUp);
					int heartDw = UserInfo.getIntance().getUserData()
							.getLimit_heart_dw();
					floorLimit = Integer.toString(heartDw);
				} else if ("5".equals(type)) {
					upperLimit = "1";
					floorLimit = "1";
				} else {
					return;
				}

				Date date = new Date();
				date.setTime(this.timestamp);
				String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(date);

				// String requestInfo = "[{username:\""
				// + UserInfo.getIntance().getUserData().getMy_name()
				// + "\",alertType:\"" + this.type + "\",alertTime:\"" +
				// currDate
				// + "\",value:\"" + this.value + "\",deviceNumber:\""
				// + UserInfo.getIntance().getUserData().getDevice_number() +
				// "\""
				// + ",upperLimit:\"" + upperLimit + "\""
				// + ",floorLimit:\"" + floorLimit + "\""
				// + "}]";
				String weath_city = UserInfo.getIntance().getUserData()
						.getProvince();
				String update_GPS_time = UserInfo.getIntance().getUserData()
						.getUpdate_GPS_time();
				double longitude = UserInfo.getIntance().getUserData()
						.getLongitude();
				double latitude = UserInfo.getIntance().getUserData()
						.getLatitude();
				String requestInfo = "[{username:\""
						+ UserInfo.getIntance().getUserData().getMy_name()
						+ "\",alertType:\""
						+ this.type
						+ "\",alertTime:\""
						+ currDate
						+ "\",value:\""
						+ this.value
						+ "\",upperLimit:\""
						+ ""
						+ "\",floorLimit:\""
						+ ""
						+ "\",deviceNumber:\""
						+ UserInfo.getIntance().getUserData()
								.getDevice_number() + "\",longitude:\""
						+ longitude + "\",latitude:\"" + latitude
						+ "\",positioningTime:\"" + update_GPS_time
						+ "\",city:\"" + weath_city + "\"}]";

				String responseInfo = Client.getsendAlarm(requestInfo);

				if (responseInfo == null) {
					sendIntentToSaveAlarm(type, timestamp, value);
				} else {
					if ("1".equals(responseInfo)) {
						// The server has received the alarm
					} else {
						sendIntentToSaveAlarm(type, timestamp, value);
					}
				}
			}
		}
	}

	/**
	 * 写文件
	 * 
	 * @param list
	 *            文件列表
	 * @param data
	 *            文件类容
	 */
	private void addDataToList(List<ValueEntry> list, int[] data) {
		synchronized (lockPersist) {
			for (int i = 0; i < data.length; i++) {
				Date currDate = new Date();
				list.add(new ValueEntry(currDate.getTime(), data[i]));
			}
		}
	}

	private void addStepCaloriesToList(List<StepCaloriesEntry> list,
			StepCaloriesEntry[] data) {
		for (int i = 0; i < data.length; i++) {
			Date currDate = new Date();
			list.add(new StepCaloriesEntry(currDate.getTime(),
					data[i].stepCount, data[i].calories));
		}
	}

	private void notifyDataAvailable(String action, String extra, int[] data) {
		final Intent intent = new Intent(action);
		intent.putExtra(extra, data);
		sendBroadcast(intent);
	}

	private void saveFrame60(String currMinute) {
		try {
			Program.createDataDirForCurrMinute(currMinute);
			Program.createTempDirForCurrMinute(currMinute);

			saveListToFile(Program.ECG_DATA, ecgList, currMinute);
			saveListToFile(Program.RESP_DATA, respList, currMinute);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveFrame60(String currMinute, String uid) {
		try {
			Program.createDataDirForCurrMinute(currMinute, uid);
			// Program.createTempDirForCurrMinute(currMinute, uid);

			saveListToFile(Program.ECG_DATA, ecgList, currMinute, uid);
			saveListToFile(Program.HEART_RATE_DATA, heartRateList, currMinute,
					uid);
			saveListToFile(Program.RESP_DATA, respList, currMinute, uid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void notifyUpload(String lastUpload, String uid) {
		Intent intent = new Intent(ACTION_START_UPLOAD);
		intent.putExtra("LAST_UPLOAD", lastUpload);
		intent.putExtra("UID", uid);
		sendBroadcast(intent);
	}

	/**
	 * 把一批数据从List里保存到文件
	 */
	private void saveFrame90(String currMinute) {
		try {
			// if the directory yyyyMMdd/MINUTE_FORMAT dose not exist, we create
			// one.
			Program.createDataDirForCurrMinute(currMinute);
			Program.createTempDirForCurrMinute(currMinute);
			// ---System.out.println("action save Program.TEMP_DATA");
			saveListToFile(Program.TEMP_DATA, temDatalist, currMinute);

			// ---System.out.println("action save Program.TUMBLE_DATA");
			saveListToFile(Program.TUMBLE_DATA, tumbleDatalist, currMinute);

			saveListToFile(Program.ACCELERATION_X_DATA, accelerationXlist,
					currMinute);
			saveListToFile(Program.ACCELERATION_Y_DATA, accelerationYlist,
					currMinute);
			saveListToFile(Program.ACCELERATION_Z_DATA, accelerationZlist,
					currMinute);

			saveAccelVectorListToFile(Program.ACCE_VECTOR_DATA, accelList,
					currMinute);

			saveListToFile(Program.VOLTAGE_DATA, voltageList, currMinute);

			saveListToFile(Program.HTE_WARNING_DATA, hteWarningList, currMinute);

			saveStepCaloriesListToFile(Program.STEP_CALORIES_DATA,
					stepCaloriesList, currMinute);

		} catch (IOException e) {
			// ---System.out.println("action saveFrame90 IOException");
			e.printStackTrace();
		}
	}

	/**
	 * 把一批数据从List里保存到文件
	 */
	private void saveFrame90(String currMinute, String uid) {
		try {
			// if the directory yyyyMMdd/MINUTE_FORMAT dose not exist, we create
			// one.
			Program.createDataDirForCurrMinute(currMinute, uid);
			// Program.createTempDirForCurrMinute(currMinute, uid);

			// ---System.out.println("action save Program.TEMP_DATA");
			saveListToFile(Program.TEMP_DATA, temDatalist, currMinute, uid);

			// ---System.out.println("action save Program.TUMBLE_DATA");
			saveListToFile(Program.TUMBLE_DATA, tumbleDatalist, currMinute, uid);

			saveListToFile(Program.ACCELERATION_X_DATA, accelerationXlist,
					currMinute, uid);
			saveListToFile(Program.ACCELERATION_Y_DATA, accelerationYlist,
					currMinute, uid);
			saveListToFile(Program.ACCELERATION_Z_DATA, accelerationZlist,
					currMinute, uid);

			saveAccelVectorListToFile(Program.ACCE_VECTOR_DATA, accelList,
					currMinute, uid);

			saveListToFile(Program.VOLTAGE_DATA, voltageList, currMinute, uid);

			saveListToFile(Program.HTE_WARNING_DATA, hteWarningList,
					currMinute, uid);

			saveStepCaloriesListToFile(Program.STEP_CALORIES_DATA,
					stepCaloriesList, currMinute, uid);

		} catch (IOException e) {
			// ---System.out.println("action saveFrame90 IOException");
			e.printStackTrace();
		}
	}

	private void saveStepCaloriesListToFile(String dataType,
			List<StepCaloriesEntry> list, String currMinute) throws IOException {
		if (list.size() == 0) {
			StepCaloriesEntry[] stepCalories = new StepCaloriesEntry[1];
			StepCaloriesEntry stepCaloriesEntry = new StepCaloriesEntry();
			stepCaloriesEntry.stepCount = GlobalStatus.getInstance()
					.getCurrentStep();
			stepCaloriesEntry.calories = GlobalStatus.getInstance()
					.getCalories();
			stepCalories[0] = stepCaloriesEntry;
			addStepCaloriesToList(list, stepCalories);

			stepCaloriesEntry = null;
			stepCalories = null;
		}

		if (UserInfo.isInDebugMode()) {
			String filePath = Program.getSDPathByDataType(dataType, currMinute);
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath, true)));
				Iterator<StepCaloriesEntry> it = list.iterator();
				while (it.hasNext()) {
					StepCaloriesEntry entry = it.next();
					out.write(entry.timestamp + "\t" + entry.stepCount + "\t"
							+ entry.calories + nextLine);
				}
			} catch (IOException e) {
				// ---System.out.println("action saveListToFile IOException");
				e.printStackTrace();
				throw e;
			} finally {
				out.flush();
				out.close();
				out = null;
			}
		} else {
			String dataPath = Program.getSDDataPathByDataType(dataType,
					currMinute);
			if (dataPath != null && dataPath.length() > 0) {
				File file = new File(dataPath);
				if (!file.exists()) {
					mCompressedFilePool.createDataOutputStream(dataType,
							currMinute);
				}
			}

			DataOutputStream out = mCompressedFilePool
					.getOutputStream(dataType);
			if (out != null) {
				Iterator<StepCaloriesEntry> it = list.iterator();
				while (it.hasNext()) {
					StepCaloriesEntry entry = it.next();
					out.writeLong(entry.timestamp - (1000 * 60));
					out.writeInt(entry.stepCount);
					out.writeInt(entry.calories);
				}
			}
		}
	}

	private void saveStepCaloriesListToFile(String dataType,
			List<StepCaloriesEntry> list, String currMinute, String uid)
			throws IOException {

		if (list.size() == 0) {
			StepCaloriesEntry[] stepCalories = new StepCaloriesEntry[1];
			StepCaloriesEntry stepCaloriesEntry = new StepCaloriesEntry();
			stepCaloriesEntry.stepCount = GlobalStatus.getInstance()
					.getCurrentStep();
			stepCaloriesEntry.calories = GlobalStatus.getInstance()
					.getCalories();
			stepCalories[0] = stepCaloriesEntry;
			addStepCaloriesToList(list, stepCalories);
		}

		if (UserInfo.isInDebugMode()) {
			String filePath = Program.getSDPathByDataType(dataType, currMinute,
					uid);
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath, true)));
				Iterator<StepCaloriesEntry> it = list.iterator();
				while (it.hasNext()) {
					StepCaloriesEntry entry = it.next();
					out.write(entry.timestamp + "\t" + entry.stepCount + "\t"
							+ entry.calories + nextLine);
				}
			} catch (IOException e) {
				// ---System.out.println("action saveListToFile IOException");
				e.printStackTrace();
				throw e;
			} finally {
				out.flush();
				out.close();
				out = null;
			}
		} else {
			String dataPath = Program.getSDDataPathByDataType(dataType,
					currMinute, uid);
			if (dataPath != null && dataPath.length() > 0) {
				File file = new File(dataPath);
				if (!file.exists()) {
					mCompressedFilePool.createDataOutputStream(dataType,
							currMinute, uid);
				}
			}

			DataOutputStream out = mCompressedFilePool
					.getOutputStream(dataType);
			if (out != null) {
				Iterator<StepCaloriesEntry> it = list.iterator();
				while (it.hasNext()) {
					StepCaloriesEntry entry = it.next();
					out.writeLong(entry.timestamp);
					out.writeInt(entry.stepCount);
					out.writeInt(entry.calories);
				}
			}
		}
	}

	private void saveAccelVectorListToFile(String dataType,
			List<AccelVector> accelList, String currMinute) throws IOException {
		if (UserInfo.isInDebugMode()) {
			String filePath = Program.getSDPathByDataType(dataType, currMinute);
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath, true)));
				Iterator<AccelVector> it = accelList.iterator();
				while (it.hasNext()) {
					AccelVector entry = it.next();
					out.write(entry.timestamp + "\t" + entry.acceX + "\t"
							+ entry.acceY + "\t" + entry.acceZ + nextLine);
				}
			} catch (IOException e) {
				// ---System.out.println("action saveListToFile IOException");
				e.printStackTrace();
				throw e;
			} finally {
				if (out != null) {
					out.flush();
					out.close();
					out = null;
				}
			}
		} else {
			String dataPath = Program.getSDDataPathByDataType(dataType,
					currMinute);
			if (dataPath != null && dataPath.length() > 0) {
				File file = new File(dataPath);
				if (!file.exists()) {
					mCompressedFilePool.createDataOutputStream(dataType,
							currMinute);
				}
			}

			DataOutputStream out = mCompressedFilePool
					.getOutputStream(dataType);
			if (out != null) {
				if (UserInfo.CMPR_DATX == UserInfo.dataCompressType()) {
					out.writeInt(Integer.MAX_VALUE);
					out.writeInt(Integer.MAX_VALUE);
					Date now = new Date();
					out.writeLong(now.getTime());
				}

				Iterator<AccelVector> it = accelList.iterator();
				while (it.hasNext()) {
					AccelVector entry = it.next();
					if (UserInfo.CMPR_DAT == UserInfo.dataCompressType()) {
						out.writeLong(entry.timestamp);
					}
					out.writeInt(entry.acceX);
					out.writeInt(entry.acceY);
					out.writeInt(entry.acceZ);
				}
			}
		}
	}

	/**
	 * 保存数据到文件
	 * 
	 * @param dataType
	 *            数据类型
	 * @param list
	 *            数据列表
	 * @param currMinute
	 *            文件夹名
	 * @throws IOException
	 */
	private void saveListToFile(String dataType, List<ValueEntry> list,
			String currMinute) throws IOException {
		if (UserInfo.isInDebugMode()) {
			String filePath = Program.getSDPathByDataType(dataType, currMinute);

			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath, true)));
				Iterator<ValueEntry> it = list.iterator();
				while (it.hasNext()) {
					ValueEntry entry = it.next();
					out.write(entry.timestamp + "\t" + entry.value + nextLine);
				}
			} catch (IOException e) {
				// ---System.out.println("action saveListToFile IOException");
				e.printStackTrace();
				throw e;
			} finally {
				if (out != null) {
					out.flush();
					out.close();
					out = null;
				}
			}
		} else {
			String dataPath = Program.getSDDataPathByDataType(dataType,
					currMinute);
			if (dataPath != null && dataPath.length() > 0) {
				File file = new File(dataPath);
				if (!file.exists()) {
					mCompressedFilePool.createDataOutputStream(dataType,
							currMinute);
				}
			}

			DataOutputStream out = mCompressedFilePool
					.getOutputStream(dataType);
			if (out != null) {
				if (UserInfo.CMPR_DATX == UserInfo.dataCompressType()) {
					out.writeInt(Integer.MAX_VALUE);
					out.writeInt(Integer.MAX_VALUE);
					Date now = new Date();
					out.writeLong(now.getTime());
				}

				Iterator<ValueEntry> it = list.iterator();
				while (it.hasNext()) {
					ValueEntry entry = it.next();
					if (UserInfo.CMPR_DAT == UserInfo.dataCompressType()) {
						out.writeLong(entry.timestamp);
					}
					out.writeInt(entry.value);
				}
			}
		}
	}

	private void saveListToFile(String dataType, List<ValueEntry> list,
			String currMinute, String uid) throws IOException {
		if (UserInfo.isInDebugMode()) {
			String filePath = Program.getSDPathByDataType(dataType, currMinute,
					uid);

			System.out.println("action saveListToFile filePath[" + filePath
					+ "]");

			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath, true)));
				Iterator<ValueEntry> it = list.iterator();
				while (it.hasNext()) {
					ValueEntry entry = it.next();
					out.write(entry.timestamp + "\t" + entry.value + nextLine);
				}
			} catch (IOException e) {
				// ---System.out.println("action saveListToFile IOException");
				e.printStackTrace();
				throw e;
			} finally {
				if (out != null) {
					out.flush();
					out.close();
					out = null;
				}
			}
		} else {
			String dataPath = Program.getSDDataPathByDataType(dataType,
					currMinute, uid);
			if (dataPath != null && dataPath.length() > 0) {
				File file = new File(dataPath);
				if (!file.exists()) {
					mCompressedFilePool.createDataOutputStream(dataType,
							currMinute, uid);
				}
			}

			if (isWarningType(dataType)) {
				DataOutputStream out = mCompressedFilePool
						.getOutputStream(dataType);
				if (out != null) {
					int warningUp = 0;
					int warningDw = 0;

					if (dataType.equals(Program.HTE_WARNING_DATA)) {
						warningUp = UserInfo.getIntance().getUserData()
								.getLimit_heart_up();
						warningDw = UserInfo.getIntance().getUserData()
								.getLimit_heart_dw();
					} else if (dataType.equals(Program.TUMBLE_DATA)) {
						warningUp = 1;
						warningDw = 1;
					}

					Iterator<ValueEntry> it = list.iterator();
					while (it.hasNext()) {
						ValueEntry entry = it.next();
						out.writeLong(entry.timestamp);
						out.writeInt(entry.value);
						out.writeInt(warningDw);
						out.writeInt(warningUp);
					}
				}

				return;
			}

			DataOutputStream out = mCompressedFilePool
					.getOutputStream(dataType);
			if (out != null) {
				if (UserInfo.CMPR_DATX == UserInfo.dataCompressType()
						&& (!isWarningType(dataType))) {
					out.writeInt(Integer.MAX_VALUE);
					out.writeInt(Integer.MAX_VALUE);
					Date now = new Date();
					out.writeLong(now.getTime() - BACKWARD_TIME);
				}

				// int countECG = 0;
				Iterator<ValueEntry> it = list.iterator();
				while (it.hasNext()) {
					// countECG++;
					ValueEntry entry = it.next();
					if (UserInfo.CMPR_DAT == UserInfo.dataCompressType()) {
						out.writeLong(entry.timestamp);
					}
					out.writeInt(entry.value);
				}

				// if (dataType.equals(Program.ECG_DATA)) {
				// System.out.println("action DataStorageService countECG " +
				// countECG);
				// }
			}
		}
	}

	private boolean isWarningType(String dataType) {
		if (dataType == Program.TUMBLE_DATA) {
			return true;
		}
		if (dataType == Program.HTE_WARNING_DATA) {
			return true;
		}

		return false;
	}

	private void saveAccelVectorListToFile(String dataType,
			List<AccelVector> accelList, String currMinute, String uid)
			throws IOException {
		if (UserInfo.isInDebugMode()) {
			String filePath = Program.getSDPathByDataType(dataType, currMinute,
					uid);
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath, true)));
				Iterator<AccelVector> it = accelList.iterator();
				while (it.hasNext()) {
					AccelVector entry = it.next();
					out.write(entry.timestamp + "\t" + entry.acceX + "\t"
							+ entry.acceY + "\t" + entry.acceZ + nextLine);
				}
			} catch (IOException e) {
				// ---System.out.println("action saveListToFile IOException");
				e.printStackTrace();
				throw e;
			} finally {
				if (out != null) {
					out.flush();
					out.close();
					out = null;
				}
			}
		} else {
			String dataPath = Program.getSDDataPathByDataType(dataType,
					currMinute, uid);
			if (dataPath != null && dataPath.length() > 0) {
				File file = new File(dataPath);
				if (!file.exists()) {
					mCompressedFilePool.createDataOutputStream(dataType,
							currMinute, uid);
				}
			}

			DataOutputStream out = mCompressedFilePool
					.getOutputStream(dataType);
			if (out != null) {
				if (UserInfo.CMPR_DATX == UserInfo.dataCompressType()) {
					out.writeInt(Integer.MAX_VALUE);
					out.writeInt(Integer.MAX_VALUE);
					Date now = new Date();
					out.writeLong(now.getTime() - BACKWARD_TIME);
				}

				Iterator<AccelVector> it = accelList.iterator();
				while (it.hasNext()) {
					AccelVector entry = it.next();
					if (UserInfo.CMPR_DAT == UserInfo.dataCompressType()) {
						out.writeLong(entry.timestamp);
					}
					out.writeInt(entry.acceX);
					out.writeInt(entry.acceY);
					out.writeInt(entry.acceZ);
				}
			}
		}
	}

	private byte getSeqNo(byte[] data) {
		return data[4];
	}

	private int[] duplicateData(int[] origData) {
		int[] data = new int[origData.length * 2];
		for (int i = 0; i < origData.length; i++) {
			data[i * 2] = origData[i];
			data[i * 2 + 1] = origData[i];
		}
		return data;
	}

	// private void addDataToLists(List<ValueEntry> list, int data) {
	//
	// Date currDate = new Date();
	// list.add(new ValueEntry(currDate.getTime(), data));
	//
	// }

	private void accelAceptX(int seq, int val) {
		try {
			accelAnalyzer.acceptX(seq, val);
		} catch (DataErrorException e) {
			e.printStackTrace();
			accelAnalyzer.reset();
		}
	}

	private void accelAceptY(int seq, int val) {
		try {
			accelAnalyzer.acceptY(seq, val);
		} catch (DataErrorException e) {
			e.printStackTrace();
			accelAnalyzer.reset();
		}
	}

	private void accelAceptZ(int seq, int val) {
		try {
			accelAnalyzer.acceptZ(seq, val);
		} catch (DataErrorException e) {
			e.printStackTrace();
			accelAnalyzer.reset();
		}
	}

	private boolean isAccelVectorReady() {
		boolean isAccelReady = false;
		try {
			isAccelReady = accelAnalyzer.isAccelVectorReady();
		} catch (DataErrorException e) {
			e.printStackTrace();
			accelAnalyzer.reset();
		}

		return isAccelReady;
	}

	private int[] getAccelXYZ() {
		int[] xyz = null;

		try {
			xyz = accelAnalyzer.getAccelXYZ();
		} catch (DataErrorException e) {
			e.printStackTrace();
			xyz = null;
			accelAnalyzer.reset();
		}

		return xyz;
	}

	private int getNextVal(int start, int step) {
		return (start + step) > 255 ? (start + step) - 256 : (start + step);
	}

	private void sendData(String action, String extra, int[] data) {
		final Intent intent = new Intent(action);
		intent.putExtra(extra, data);
		sendBroadcast(intent);
	}

	private void sendStressData(String action, String extra,
			StressLevelItem stressLevelItem) {
		final Intent intent = new Intent(action);
		String stressData = "";

		stressData += "stressA: ";
		stressData += Float.toString(stressLevelItem.stressA);
		stressData += "\n";

		stressData += "stressB1: ";
		stressData += Float.toString(stressLevelItem.stressB1);
		stressData += "\n";

		stressData += "stressB2: ";
		stressData += Float.toString(stressLevelItem.stressB2);
		stressData += "\n";

		stressData += "stressC1: ";
		stressData += Float.toString(stressLevelItem.stressC1);
		stressData += "\n";

		stressData += "stressC2: ";
		stressData += Float.toString(stressLevelItem.stressC2);
		stressData += "\n";

		stressData += "stressC3: ";
		stressData += Float.toString(stressLevelItem.stressC3);
		stressData += "\n";

		stressData += "stressD: ";
		stressData += Float.toString(stressLevelItem.stressD);
		stressData += "\n";

		stressData += "stressE1: ";
		stressData += Float.toString(stressLevelItem.stressE1);
		stressData += "\n";

		stressData += "stressE2: ";
		stressData += Float.toString(stressLevelItem.stressE2);
		stressData += "\n";

		intent.putExtra(extra, stressData);
		sendBroadcast(intent);
	}

	private void getUserInfo() {
		// peopledatas = this.getSharedPreferences("peopleinfo", MODE_PRIVATE);
		// String info = peopledatas.getString("userinfo", "");
		// try {
		// JSONArray jsonArray = new JSONArray(info);
		// JSONObject jsonObject = jsonArray.getJSONObject(0);
		// String gender = jsonObject.getString("sex");
		// if (gender != null){
		// mGender = Integer.parseInt(gender);
		// } else {
		// mGender = 0;
		// }
		//
		// String age = jsonObject.getString("age");
		// if (age != null) {
		// mAge = Integer.parseInt(age);
		// } else {
		// mAge = 0;
		// }
		// } catch (Exception e) {
		// mGender = 0;
		// mAge = 0;
		// }
		mGender = UserInfo.getIntance().getUserData().getSex();
		mAge = UserInfo.getIntance().getUserData().getAge();
		mHeight = UserInfo.getIntance().getUserData().getHeight();
		mWeight = UserInfo.getIntance().getUserData().getWeight();
		mLimitHeartUp = UserInfo.getIntance().getUserData().getLimit_heart_up();
		mLimitHeartDown = UserInfo.getIntance().getUserData()
				.getLimit_heart_dw();

	}

	private void startGetStressLevelTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_GET_STRESS_LEVEL_ITEM);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();

		// am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 1000 * 5,
		// pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime, 1000 * 5, pi);
	}

	private void stopGetStressLevelTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_GET_STRESS_LEVEL_ITEM);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(sender);
	}

	private void startJudgeTumbleTimer() {
		mIsJudgeTumbleTimerStarted = true;

		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_JUDGE_TUMBLE);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();

		// am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 1000 * 5,
		// pi);
		am.set(AlarmManager.RTC, triggerAtTime + 1000, pi);
	}

	private void startJudgeTumbleTimer(String uid) {
		mIsJudgeTumbleTimerStarted = true;

		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_JUDGE_TUMBLE);
		intent.putExtra("UID", uid);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();

		// am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 1000 * 5,
		// pi);
		// am.set(AlarmManager.RTC_WAKEUP, triggerAtTime + 1000, pi);
		am.set(AlarmManager.RTC, triggerAtTime + 1000, pi);
	}

	private void stopJudgeTumbleTimer() {
		mIsJudgeTumbleTimerStarted = false;

		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_JUDGE_TUMBLE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(sender);
	}

	private void checkECGCalculateDone() {
		if (ecgHeartRateCal.isECGCalculateDone()) {
			int realTimeHeartRate = ecgHeartRateCal.getHeartRateRealtime();
			GlobalStatus.getInstance().setHeartRate(realTimeHeartRate);

			if (realTimeHeartRate < mLimitHeartDown
					|| realTimeHeartRate > mLimitHeartUp) {
				warningHteManager.increase(
						Program.getMinuteDataByDate(new Date()),
						realTimeHeartRate);

				// int[] heartRate = new int[1];
				// heartRate[0] = realTimeHeartRate;
				// addDataToList(hteWarningList, heartRate);
				Date now = new Date();
				new SendRealTimeAlarmThread("1", now.getTime(),
						realTimeHeartRate).start();
			}

			int mentalStress = ecgHeartRateCal.getStress();
			GlobalStatus.getInstance().setStressLevel(mentalStress);
		}
	}

	private void checkECGCalculateDone(String uid, boolean isValidEcg) {
		if (isValidEcg) {
			if (ecgHeartRateCal.isECGCalculateDone()) {
				int realTimeHeartRate = ecgHeartRateCal.getHeartRateRealtime();
				if (realTimeHeartRate == 0) {
					return;
				}

				GlobalStatus.getInstance().setHeartRate(realTimeHeartRate);

				if (!mDetectC7StateMachine.isInMaskStatus()) {
					if (queueHte.size() < 10) {
						queueHte.offer(realTimeHeartRate);
						mCountInvalidEcg = 0;

						int mentalStress = ecgHeartRateCal.getStress();
						GlobalStatus.getInstance().setStressLevel(mentalStress);

						return;
					} else {
						queueHte.offer(realTimeHeartRate);

						int delta = queueHte.size() - 10;
						for (int i = 0; i < delta; i++) {
							queueHte.poll();
						}

						int sum = 0;
						for (Integer iVal : queueHte) {
							sum += iVal;
						}

						realTimeHeartRate = (int) (sum * 1.0f / (queueHte
								.size()));
					}

					// GlobalStatus.getInstance().setHeartRate(realTimeHeartRate);

					if ((uid != null) && (uid.length() > 0)) {
						if ((realTimeHeartRate != 0)
								&& (realTimeHeartRate < mLimitHeartDown || realTimeHeartRate > mLimitHeartUp)) {
							if (mCountFrameFromConnected > SKIP_FRAMES) {

								warningHteManager
										.increase(
												Program.getSecondDataByDate(new Date()),
												realTimeHeartRate, uid);

								// int[] heartRate = new int[1];
								// heartRate[0] = realTimeHeartRate;
								// addDataToList(hteWarningList, heartRate);
								Date now = new Date();
								new SendRealTimeAlarmThread("1", now.getTime(),
										realTimeHeartRate).start();

							}
						}

						int mentalStress = ecgHeartRateCal.getStress();
						GlobalStatus.getInstance().setStressLevel(mentalStress);
					}
				} else {
				}
			}

			mCountInvalidEcg = 0;
		} else {
			// if (ecgHeartRateCal.isECGCalculateDone()) {

			// int realTimeHeartRate
			// = ecgHeartRateCal.getHeartRateRealtime();
			// System.out.println("action DataStorageService getECGHeartRate real time: "
			// + realTimeHeartRate);
			//
			// GlobalStatus.getInstance().setHeartRate(0);
			//
			// int mentalStress = ecgHeartRateCal.getStress();
			// GlobalStatus.getInstance().setStressLevel(mentalStress);

			// }

			mCountInvalidEcg++;
			if (mCountInvalidEcg > 2000) {
				final Intent intent = new Intent(ACTION_NOTIFY_INVALID_ECG);
				sendBroadcast(intent);
				mCountInvalidEcg = 0;
			}
		}
	}

	private void checkECGCalculateDoneByHeartRate(int heartRate, String uid,
			boolean isValidEcg) {
		if (isValidEcg) {
			int realTimeHeartRate = heartRate;
			if (realTimeHeartRate == 0) {
				return;
			}

			// if (!mDetectC7StateMachine.isInMaskStatus()) {
			if (queueHte.size() < 10) {
				queueHte.offer(realTimeHeartRate);
				mCountInvalidEcg = 0;

				int mentalStress = ecgHeartRateCal.getStress();
				GlobalStatus.getInstance().setStressLevel(mentalStress);

				return;
			} else {
				queueHte.offer(realTimeHeartRate);

				int delta = queueHte.size() - 10;
				for (int i = 0; i < delta; i++) {
					queueHte.poll();
				}

				int sum = 0;
				for (Integer iVal : queueHte) {
					sum += iVal;
				}

				realTimeHeartRate = (int) (sum * 1.0f / (queueHte.size()));
			}

			// GlobalStatus.getInstance().setHeartRate(realTimeHeartRate);

			if ((uid != null) && (uid.length() > 0)) {
				if ((realTimeHeartRate != 0)
						&& (realTimeHeartRate < mLimitHeartDown || realTimeHeartRate > mLimitHeartUp)) {
					warningHteManager.increase(
							Program.getSecondDataByDate(new Date()),
							realTimeHeartRate, uid);
					Date now = new Date();
					new SendRealTimeAlarmThread("1", now.getTime(),
							realTimeHeartRate).start();
				}

				int mentalStress = ecgHeartRateCal.getStress();
				GlobalStatus.getInstance().setStressLevel(mentalStress);
			}
			// } else {
			// System.out.println("action DataStorageService checkECGCalculateDone in C7 mask status");
			// }

			mCountInvalidEcg = 0;
		} else {
			mCountInvalidEcg++;
			if (mCountInvalidEcg > 10) {
				final Intent intent = new Intent(ACTION_NOTIFY_INVALID_ECG);
				sendBroadcast(intent);
				mCountInvalidEcg = 0;
			}
		}
	}

	private void checkRespCalculateDone() {
	}

	private void checkAccelCalculateDone() {
		int posture = GlobalAccelCalculator.getInstance().getPosture();
		int[] postureData = new int[2];
		postureData[0] = posture;

		int gait = GlobalAccelCalculator.getInstance().getGait();
		postureData[1] = gait;

		int currentStep = mStepCountManager.getLastStepCount();
		int currentDeltaStap = 0;
		currentDeltaStap = getDeltaStepByHistory(currentStep);
		int walkState = getWalkStateByDeltaStep(currentDeltaStap);

		// if (mLastDeltaStep > 10) {
		// // Skip the update
		// mContinuousRunning = 0;
		// System.out.println("action DataStorage GlobalAccelCalculator mContinuousRunning, mLastDeltaStep:"
		// + mContinuousRunning + "," + mLastDeltaStep);
		// return;
		// }
		// else {

		if (WALK_STATE_WALK == walkState) {
			gait = 2;
			mContinuousRunning = 0;
		} else if (WALK_STATE_RUNNING == walkState) {
			// if (mContinuousRunning <= 22) {
			// if (mContinuousRunning <= 25) {
			if (mContinuousRunning <= 30) {
				mContinuousRunning++;

				gait = 2;
				return;
			} else {
				gait = 4;
			}
		} else {
			// System.out.println("action DataStorage GlobalAccelCalculator WALK_STATE_STAND");
			mContinuousRunning = 0;
			// if (gait == 1) {
			// mContinuousRunning = 0;
			// // if (currentDeltaStap > 0) {
			// // // Skip the update
			// // return;
			// // }
			// // else {
			// // GlobalStatus.getInstance().setPosture(posture);
			// // GlobalStatus.getInstance().setGait(gait);
			// // }
			// if (posture >= 23 && posture <= 27) {
			// }
			// else {
			// }
			// }
			if (gait == 1 && (posture >= 23 && posture <= 27)) {
				// System.out.println("action DataStorage GlobalAccelCalculator gait == 1 && (posture >= 23 && posture <= 27");
			} else {
				// System.out.println("action DataStorage GlobalAccelCalculator set gait to 1");
				gait = 1;
				// return;
			}
		}

		// if (gait == 1) {
		// mContinuousRunning = 0;
		//
		// if (WALK_STATE_STAND != walkState) {
		// return;
		// }
		// }
		// else {
		// if (WALK_STATE_WALK == walkState) {
		// gait = 2;
		// mContinuousRunning = 0;
		// }
		// else if (WALK_STATE_RUNNING == walkState) {
		// if (mContinuousRunning <= 20) {
		// mContinuousRunning++;
		//
		// gait = 2;
		// return;
		// }
		// else {
		// gait = 4;
		// }
		// }
		// else {
		// gait = 1;
		// mContinuousRunning = 0;
		// }
		// }

		// }

		GlobalStatus.getInstance().setPosture(posture);
		GlobalStatus.getInstance().setGait(gait);

		postureData[0] = posture;
		postureData[1] = gait;

		if ((stepCheckStateMachine != null)
				// && (InvalidStepCheckStateMachine.S_VALID ==
				// stepCheckStateMachine.getCurrentState())) {
				&& (InvalidStepCheckStateMachine.S_VALID == stepCheckStateMachine
						.getCurrentState() || (InvalidStepCheckStateMachine.S_START == stepCheckStateMachine
						.getCurrentState()))) {
			// System.out.println("action DataStorage GlobalAccelCalculator ACTION_POSTURE_DATA_AVAILABLE");
			sendData(ACTION_POSTURE_DATA_AVAILABLE, POSTURE_DATA, postureData);
		} else {
			int[] invalidPostureData = new int[2];
			invalidPostureData[0] = 0;
			invalidPostureData[1] = 0;
			// System.out.println("action DataStorage GlobalAccelCalculator ACTION_POSTURE_DATA_AVAILABLE invalid");
			sendData(ACTION_POSTURE_DATA_AVAILABLE, POSTURE_DATA,
					invalidPostureData);
		}

		if ((stepCheckStateMachine != null)
				&& (InvalidStepCheckStateMachine.S_VALID == stepCheckStateMachine
						.getCurrentState())) {
			updateStandStill(posture, gait);
		} else {
			updateStandStill(0, 0);
		}
		// }
	}

	private void checkAccelCalculateDoneDebug() {
		// if (GlobalAccelCalculator.getInstance().isAccelCalculateDone()) {
		int posture = GlobalAccelCalculator.getInstance().getPosture();
		int[] postureData = new int[2];
		postureData[0] = posture;

		int gait = GlobalAccelCalculator.getInstance().getGait();
		postureData[1] = gait;

		GlobalStatus.getInstance().setPosture(posture);
		GlobalStatus.getInstance().setGait(gait);

		sendData(ACTION_POSTURE_DATA_AVAILABLE, POSTURE_DATA, postureData);

		updateStandStill(posture, gait);
		// }
	}

	private void checkStepAndCalories(byte[] data) {
		int[] stepCount = Program.getStepCountData(data);

		int currentStepCount = stepCount[0];
		if (currentStepCount == 0) {
			return;
		}

		Date lastRecordDate = new Date();
		if (mLastRecordedStep != 0) {
			if (currentStepCount > mLastRecordedStep) {
				if ((currentStepCount - mLastRecordedStep) > (((lastRecordDate
						.getTime() - mLastRecordedStepDate.getTime()) / 1000 + 1) * 6)) {
					EventLogger.logEvent("Invalid STEP0:0:" + currentStepCount);
					return;
				}

				setLastRecordedStep(currentStepCount, lastRecordDate);
			} else if (currentStepCount < mLastRecordedStep) {
				if (currentStepCount > (((lastRecordDate.getTime() - mLastRecordedStepDate
						.getTime()) / 1000 + 1) * 6)) {
					EventLogger.logEvent("Invalid STEP0:1:" + currentStepCount);
					return;
				}

				setLastRecordedStep(currentStepCount, lastRecordDate);
			} else {
				setLastRecordedStep(currentStepCount, lastRecordDate);
			}
		} else {
			setLastRecordedStep(currentStepCount, lastRecordDate);
			return;
		}

		EventLogger.logStep("STEP 0:" + stepCount[0] + " LAST:"
				+ mStepCountManager.getLastStepCount() + " BASE:"
				+ mStepCountManager.getStepCountBase() + " Step_Count:"
				+ mStepCountManager.getStepCount());

		addToHistoryStepCount(currentStepCount);
		int averageStepCount = averageHistoryStepCount();
		if (currentStepCount > averageStepCount * 1.2f) {
			EventLogger.logEvent("Too large step count 0 " + currentStepCount
					+ ":" + MiscUtils.dumpBytesAsString(data));
			return;
		}

		boolean isValid = Program.isValidEcg(data);

		Date lastStepDate = mStepCountManager.getLastStepDate();
		if (lastStepDate == null) {
			mStepCountManager.setLastStepDate(new Date());
			mStepCountManager.setStepCountBase(0);
			mStepCountManager.setStepCount(currentStepCount);

			stepCheckStateMachine.consume(isValid, currentStepCount);

			return;
		}

		// 只处理S_FIRST_VALID和S_VALID两种情况
		stepCheckStateMachine.consume(isValid, currentStepCount);
		if ((InvalidStepCheckStateMachine.S_VALID == stepCheckStateMachine
				.getCurrentState())
				|| (InvalidStepCheckStateMachine.S_FIRST_VALID == stepCheckStateMachine
						.getCurrentState())) {
		} else {
			return;
		}

		if (InvalidStepCheckStateMachine.S_FIRST_VALID == stepCheckStateMachine
				.getCurrentState()) {
			int stepCountBase = mStepCountManager.getStepCountBase();
			int subStepCount = stepCheckStateMachine.getSubtractionStep();
			mStepCountManager.setStepCountBase(stepCountBase - subStepCount);
		}

		if (currentStepCount == 0) {
			GlobalStatus.getInstance().setCurrentStep(currentStepCount);
			GlobalStatus.getInstance().setCalories(0);
			return;
		}

		int lastStepCount = mStepCountManager.getLastStepCount();

		Date now = new Date();
		if (DateUtil.isInTheSameDay(now, lastStepDate)) {
			if (currentStepCount + mStepCountManager.getStepCountBase() < lastStepCount) {
				mStepCountManager.setStepCountBase(lastStepCount);
			} else {
			}
			mStepCountManager.setStepCount(currentStepCount);
		} else {
			// mStepCountManager.setStepCountBase(0);
			// mStepCountManager.setStepCount(0);
		}

		mStepCountManager.setLastStepDate(now);

		if (step_state != mStepCountManager.getLastStepCount()) {
			mLastDeltaStep = mStepCountManager.getLastStepCount() - step_state;

			step_state = mStepCountManager.getLastStepCount();

			int calories = GlobalAccelCalculator.getInstance().getCalories();

			if (mStepCountManager.getLastStepCount() < 0) {
				GlobalStatus.getInstance().setCalories(0);
				GlobalStatus.getInstance().setCurrentStep(0);
			} else {
				GlobalStatus.getInstance().setCalories(calories);
				GlobalStatus.getInstance().setCurrentStep(
						mStepCountManager.getLastStepCount());

				GlobalStatus.getInstance().setLastCalories(calories);
				GlobalStatus.getInstance().setLastStepCount(
						mStepCountManager.getLastStepCount());
			}

			// Notify UI that new step and calories data are available
			final Intent updateIntent = new Intent(
					ACTION_UPDATE_STEP_AND_CALORIES);
			sendBroadcast(updateIntent);

			StepCaloriesEntry[] stepCalories = new StepCaloriesEntry[1];
			StepCaloriesEntry stepCaloriesEntry = new StepCaloriesEntry();

			stepCaloriesEntry.stepCount = (mStepCountManager.getLastStepCount() >= 0) ? mStepCountManager
					.getLastStepCount() : 0;
			if (0 >= mStepCountManager.getLastStepCount()) {
				calories = 0;
			}
			stepCaloriesEntry.calories = calories;
			stepCalories[0] = stepCaloriesEntry;
			addStepCaloriesToList(stepCaloriesList, stepCalories);
		}

		recordCurrentStep(mStepCountManager.getLastStepCount());
	}

	private void checkStepAndCaloriesByNewFrame(byte[] data) {
		int[] stepCount = Program.getStepCountDataByNewFrame(data);

		int currentStepCount = stepCount[0];
		if (currentStepCount == 0) {
			return;
		}

		Date lastRecordDate = new Date();
		if (mLastRecordedStep != 0) {
			if (currentStepCount > mLastRecordedStep) {
				if ((currentStepCount - mLastRecordedStep) > (((lastRecordDate
						.getTime() - mLastRecordedStepDate.getTime()) / 1000 + 1) * 6)) {
					EventLogger.logEvent("Invalid STEP1:0:" + currentStepCount);
					return;
				}

				setLastRecordedStep(currentStepCount, lastRecordDate);
			} else if (currentStepCount < mLastRecordedStep) {
				if (currentStepCount > (((lastRecordDate.getTime() - mLastRecordedStepDate
						.getTime()) / 1000 + 1) * 6)) {
					EventLogger.logEvent("Invalid STEP1:1:" + currentStepCount);
					return;
				}

				setLastRecordedStep(currentStepCount, lastRecordDate);
			} else {
				setLastRecordedStep(currentStepCount, lastRecordDate);
			}
		} else {
			setLastRecordedStep(currentStepCount, lastRecordDate);
			return;
		}

		// EventLogger.logStep("STEP 0:" + stepCount[0] + " LAST:" +
		// mStepCountManager.getLastStepCount());
		EventLogger.logStep("STEP 1:" + stepCount[0] + " LAST:"
				+ mStepCountManager.getLastStepCount() + " BASE:"
				+ mStepCountManager.getStepCountBase() + " Step_Count:"
				+ mStepCountManager.getStepCount());

		addToHistoryStepCount(currentStepCount);
		int averageStepCount = averageHistoryStepCount();
		if (currentStepCount > averageStepCount * 1.2f) {
			EventLogger.logEvent("Too large step count 1 " + currentStepCount
					+ ":" + MiscUtils.dumpBytesAsString(data));
			return;
		}

		boolean isValid = Program.isValidEcg(data);

		Date lastStepDate = mStepCountManager.getLastStepDate();
		if (lastStepDate == null) {
			mStepCountManager.setLastStepDate(new Date());
			mStepCountManager.setStepCountBase(0);
			mStepCountManager.setStepCount(currentStepCount);

			stepCheckStateMachine.consume(isValid, currentStepCount);

			return;
		}

		// 只处理S_FIRST_VALID和S_VALID两种情况
		stepCheckStateMachine.consume(isValid, currentStepCount);
		if ((InvalidStepCheckStateMachine.S_VALID == stepCheckStateMachine
				.getCurrentState())
				|| (InvalidStepCheckStateMachine.S_FIRST_VALID == stepCheckStateMachine
						.getCurrentState())) {
		} else {
			return;
		}

		if (InvalidStepCheckStateMachine.S_FIRST_VALID == stepCheckStateMachine
				.getCurrentState()) {
			int stepCountBase = mStepCountManager.getStepCountBase();
			int subStepCount = stepCheckStateMachine.getSubtractionStep();
			mStepCountManager.setStepCountBase(stepCountBase - subStepCount);
		}

		if (currentStepCount == 0) {
			GlobalStatus.getInstance().setCurrentStep(currentStepCount);
			GlobalStatus.getInstance().setCalories(0);
			return;
		}

		int lastStepCount = mStepCountManager.getLastStepCount();

		Date now = new Date();
		if (DateUtil.isInTheSameDay(now, lastStepDate)) {
			if (currentStepCount + mStepCountManager.getStepCountBase() < lastStepCount) {
				mStepCountManager.setStepCountBase(lastStepCount);
			} else {
			}
			mStepCountManager.setStepCount(currentStepCount);
		} else {
			// mStepCountManager.setStepCountBase(0);
			// mStepCountManager.setStepCount(0);
		}

		mStepCountManager.setLastStepDate(now);

		if (step_state != mStepCountManager.getLastStepCount()) {
			step_state = mStepCountManager.getLastStepCount();

			int calories = GlobalAccelCalculator.getInstance().getCalories();

			if (mStepCountManager.getLastStepCount() < 0) {
				GlobalStatus.getInstance().setCalories(0);
				GlobalStatus.getInstance().setCurrentStep(0);
			} else {
				GlobalStatus.getInstance().setCalories(calories);
				GlobalStatus.getInstance().setCurrentStep(
						mStepCountManager.getLastStepCount());

				GlobalStatus.getInstance().setLastCalories(calories);
				GlobalStatus.getInstance().setLastStepCount(
						mStepCountManager.getLastStepCount());
			}

			// Notify UI that new step and calories data are available
			final Intent updateIntent = new Intent(
					ACTION_UPDATE_STEP_AND_CALORIES);
			sendBroadcast(updateIntent);

			StepCaloriesEntry[] stepCalories = new StepCaloriesEntry[1];
			StepCaloriesEntry stepCaloriesEntry = new StepCaloriesEntry();

			stepCaloriesEntry.stepCount = (mStepCountManager.getLastStepCount() >= 0) ? mStepCountManager
					.getLastStepCount() : 0;
			if (0 >= mStepCountManager.getLastStepCount()) {
				calories = 0;
			}
			stepCaloriesEntry.calories = calories;
			stepCalories[0] = stepCaloriesEntry;
			addStepCaloriesToList(stepCaloriesList, stepCalories);
		}
	}

	private void checkStepAndCaloriesDebug(byte[] data) {
		int[] stepCount = Program.getStepCountData(data);

		int currentStepCount = stepCount[0];
		if (currentStepCount == 0) {
			GlobalStatus.getInstance().setCurrentStep(currentStepCount);
			GlobalStatus.getInstance().setCalories(0);
			return;
		}

		Date lastStepDate = mStepCountManager.getLastStepDate();
		int lastStepCount = mStepCountManager.getLastStepCount();

		if (lastStepDate == null) {
			mStepCountManager.setLastStepDate(new Date());
			mStepCountManager.setStepCountBase(0);
			mStepCountManager.setStepCount(currentStepCount);

			return;
		}

		Date now = new Date();
		if (DateUtil.isInTheSameDay(now, lastStepDate)) {
			if (currentStepCount + mStepCountManager.getStepCountBase() < lastStepCount) {
				mStepCountManager.setStepCountBase(lastStepCount);
			} else {
			}
			mStepCountManager.setStepCount(currentStepCount);
		} else {
			mStepCountManager.setStepCountBase(0);
			mStepCountManager.setStepCount(currentStepCount);
		}

		mStepCountManager.setLastStepDate(now);

		if (step_state != mStepCountManager.getLastStepCount()) {
			step_state = mStepCountManager.getLastStepCount();

			int calories = GlobalAccelCalculator.getInstance().getCalories();
			GlobalStatus.getInstance().setCalories(calories);
			GlobalStatus.getInstance().setCurrentStep(
					mStepCountManager.getLastStepCount());

			// Notify UI that new step and calories data are available
			final Intent updateIntent = new Intent(
					ACTION_UPDATE_STEP_AND_CALORIES);
			sendBroadcast(updateIntent);

			StepCaloriesEntry[] stepCalories = new StepCaloriesEntry[1];
			StepCaloriesEntry stepCaloriesEntry = new StepCaloriesEntry();

			stepCaloriesEntry.stepCount = mStepCountManager.getLastStepCount();
			if (0 == mStepCountManager.getLastStepCount()) {
				calories = 0;
			}
			stepCaloriesEntry.calories = calories;
			stepCalories[0] = stepCaloriesEntry;
			addStepCaloriesToList(stepCaloriesList, stepCalories);
		}

		// int[] stepCount = Program.getStepCountData(data);
		// System.out.println("action DataStorageService stepCount:" +
		// stepCount[0]);
		//
		// int calories = GlobalAccelCalculator.getInstance().getCalories();
		// if(step_state!=stepCount[0]){
		// step_state=stepCount[0];
		//
		// Intent updateIntent = new Intent(ACTION_UPDATE_STEP_AND_CALORIES);
		// updateIntent.putExtra(STEP_COUNT,stepCount[0]);
		//
		// // int calories = GlobalAccelCalculator.getInstance().getCalories();
		//
		// System.out.println("action DataStorageService calories[" + calories +
		// "]");
		// updateIntent.putExtra(CALORIES, calories);
		//
		// sendBroadcast(updateIntent);
		//
		// StepCaloriesEntry[] stepCalories = new StepCaloriesEntry[1];
		// StepCaloriesEntry stepCaloriesEntry = new StepCaloriesEntry();
		// stepCaloriesEntry.stepCount = stepCount[0];
		//
		// if (0 == stepCount[0]) {
		// calories = 0;
		// }
		//
		// stepCaloriesEntry.calories = calories;
		// stepCalories[0] = stepCaloriesEntry;
		// addStepCaloriesToList(stepCaloriesList, stepCalories);
		//
		// stepCaloriesEntry = null;
		// stepCalories = null;
		//
		// // sendBroadcast(updateIntent);
		// }
		//
		// GlobalStatus.getInstance().setCurrentStep(stepCount[0]);
		//
		// if (0 == stepCount[0]) {
		// GlobalStatus.getInstance().setCalories(0);
		// }
		// else {
		// GlobalStatus.getInstance().setCalories(calories);
		// }
	}

	private void saveECGResultToDB(String minuteData) {
		int stressLevel = ecgHeartRateCal.getStress();
		// minuteStressLevelManager.increase(minuteData, stressLevel);

		ECGResult ecgResult = new ECGResult();
		ecgResult.LF = ecgHeartRateCal.getLf();
		ecgResult.HF = ecgHeartRateCal.getHf();
		minuteECGResultManager.increase(minuteData, ecgResult);
	}

	private void saveECGResultToDB(String minuteData, String uid) {
		ECGResult ecgResult = new ECGResult();
		ecgResult.LF = ecgHeartRateCal.getLf();
		ecgResult.HF = ecgHeartRateCal.getHf();
		minuteECGResultManager.increase(minuteData, ecgResult, uid);
	}

	private void startGetStepCountSnapshotTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_GET_STEP_COUNT_SNAPSHOT);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();

		// am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 1000 * 1,
		// pi);
		// am.setRepeating(AlarmManager.RTC, triggerAtTime, 1000 * 1, pi);
		// am.setRepeating(AlarmManager.RTC, triggerAtTime, 1000 * 3, pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime, 1000 * 2, pi);
	}

	private void stopGetStepCountSnapshotTimer() {
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM_GET_STEP_COUNT_SNAPSHOT);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(sender);
	}

	private void notifyUITumbleHappened() {
		Intent intent = new Intent(ACTION_TUMBLE_HAPPENED);
		Date now = new Date();
		intent.putExtra(TUMBLE_TIME, now.getTime());
		sendBroadcast(intent);
	}

	private void updateStandStill(int posture, int gait) {
		if (mIsFirstStandStill) {
			if (gait == 1) {
				if (posture == 20) {
					mStandStill = STAND_STILL_STAND;
				} else if (posture == 21) {
					mStandStill = STAND_STILL_SIT;
				} else {
					mStandStill = STAND_STILL_UNKNOWN;
				}
			} else {
				mStandStill = STAND_STILL_UNKNOWN;
			}

			mPrevStandStillDate = new Date();
			mStandStillTime = 0;

			mIsFirstStandStill = false;

			GlobalStatus.getInstance().setStandStillTime(0);
			return;
		}

		Date now = new Date();
		if (gait == 1) {
			if (posture == 20) {
				if (mStandStill == STAND_STILL_STAND) {
					mStandStillTime += (now.getTime() - mPrevStandStillDate
							.getTime());
				} else {
					mStandStill = STAND_STILL_STAND;
					mStandStillTime = 0;
				}
			} else if (posture == 21) {
				if (mStandStill == STAND_STILL_SIT) {
					mStandStillTime += (now.getTime() - mPrevStandStillDate
							.getTime());
				} else {
					mStandStill = STAND_STILL_SIT;
					mStandStillTime = 0;
				}
			} else {
				mStandStill = STAND_STILL_UNKNOWN;
				mStandStillTime = 0;
			}
		} else {
			mStandStill = STAND_STILL_UNKNOWN;
			mStandStillTime = 0;
		}

		GlobalStatus.getInstance().setStandStillTime(mStandStillTime);
		notifyUIStandStill(mStandStill, mStandStillTime);
		mPrevStandStillDate = now;
	}

	private void notifyUIStandStill(int standStillPosture, long standStillTime) {
		final Intent intent = new Intent(ACTION_STAND_STILL_DATA_AVAILABLE);
		intent.putExtra(STAND_STILL_POSTURE, standStillPosture);
		intent.putExtra(STAND_STILL_TIME, standStillTime);

		sendBroadcast(intent);
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

	private synchronized void persistData(String nowStr) {
		synchronized (lockPersist) {
			if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			} else {
				// saveFrame60(DateUtil.parseDateToInput(now,
				// Program.MINUTE_FORMAT), mUID);
				// saveFrame90(DateUtil.parseDateToInput(now,Program.MINUTE_FORMAT),
				// mUID);
				saveFrame60(nowStr, mUID);
				saveFrame90(nowStr, mUID);
			}

			ecgList.clear();
			heartRateList.clear();
			respList.clear();

			tumbleDatalist.clear();
			// tmblStrList.clear();

			temDatalist.clear();
			voltageList.clear();
			accelerationXlist.clear();
			accelerationYlist.clear();
			accelerationZlist.clear();
			accelList.clear();

			hteWarningList.clear();
			stepCaloriesList.clear();
		}
	}

	private boolean isLastUploadLastMinute(String lastUpload) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MINUTE, -1);

		Date lastMinute = calendar.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat(Program.MINUTE_FORMAT);
		String strLastMinute = sdf.format(lastMinute);

		return strLastMinute.equals(lastUpload);
	}

	private void setTumbleFlag() {
		SharedPreferences.Editor editor = spTumbleFlag.edit();
		editor.putInt(TUMBLE_FLAG, TUMBLE_FLAG_ON);
		Date now = new Date();
		editor.putLong(TUMBLE_TIME, now.getTime());
		editor.commit();
	}

	private void initEcgMode() {
		mLastEcgMode = MODE_ECG_ECG;
		mCurrentEcgMode = MODE_ECG_ECG;
	}

	private void checkEcgModeChanged() {
		GlobalStatus.getInstance().setCurrentECGMode(mCurrentEcgMode);
		if (mLastEcgMode == mCurrentEcgMode) {
			// Do nothing;
		} else {
			// if (mCurrentEcgMode == MODE_ECG_HEART_RATE) {
			// mCompressedFilePool.flushEcgFile();
			// mCompressedFilePool.createHeartRateFile();
			// }
			// else if (mCurrentEcgMode == MODE_ECG_ECG) {
			// mCompressedFilePool.flushHeartRate();
			// mCompressedFilePool.createECGFile();
			// }
			if (UserInfo.isInDebugMode()) {
			} else {
				if (mCompressedFilePool.getMinuteData() != null) {
					persistData(mCompressedFilePool.getMinuteData());

					mCompressedFilePool.flushCompressFiles();
				}
			}
		}
		mLastEcgMode = mCurrentEcgMode;
	}

	private void addToHistoryStepCount(int stepCount) {
		historyStepCounts.offer(stepCount);

		int popCount = historyStepCounts.size() - 10;
		for (int i = 0; i < popCount; i++) {
			historyStepCounts.poll();
		}
	}

	private int averageHistoryStepCount() {
		if (historyStepCounts.size() == 0) {
			return 0;
		}
		int sum = 0;
		for (Integer iVal : historyStepCounts) {
			sum += iVal;
		}

		return sum / historyStepCounts.size();
	}

	private void clearLastRecordedStep() {
		mLastRecordedStep = 0;
		mLastRecordedStepDate = null;
	}

	private void setLastRecordedStep(int step, Date date) {
		mLastRecordedStep = step;
		mLastRecordedStepDate = date;
	}

	private int getWalkStateByDeltaStep(int deltaStep) {
		// if (deltaStep == 0) {
		// return WALK_STATE_STAND;
		// }
		// else if (deltaStep > 0 && deltaStep <= 5) {
		// return WALK_STATE_WALK;
		// }
		// else if (deltaStep > 5 && deltaStep <= 8) {
		// return WALK_STATE_RUNNING;
		// }
		// else {
		// return WALK_STATE_STAND;
		// }
		if (deltaStep == 0) {
			return WALK_STATE_STAND;
		} else if (deltaStep > 0 && deltaStep <= 1) {
			return WALK_STATE_WALK;
		} else if (deltaStep > 1 && deltaStep <= 5) {
			return WALK_STATE_RUNNING;
		} else {
			return WALK_STATE_STAND;
		}
	}

	private void recordCurrentStep(int currentStep) {
		mHistoryStep.offer(currentStep);
		int popCount = mHistoryStep.size() - MAX_HISTORY_STEP;
		for (int i = 0; i < popCount; i++) {
			mHistoryStep.poll();
		}
	}

	private int getWalkStateByCurrentStep(int currentStep) {
		return getWalkStateByDeltaStep(getDeltaStepByHistory(currentStep));
	}

	private int getDeltaStepByHistory(int currentStep) {
		int deltaStep = 0;
		if (mHistoryStep.size() < MAX_HISTORY_STEP) {
			return currentStep;
		} else {
			int history = mHistoryStep.peek();
			return (currentStep - history);
		}
	}

	private void getTemperatureMap() {
		Map<String, Object> temperatureMap = UserInfo.getIntance()
				.getTemperatureMap();
		if (temperatureMap == null) {
			mHasTemperatureMapData = false;
			mUnitTemperatureMap = null;
			mTemperatureMap = null;
			return;
		}

		// 检测数据完整性
		boolean isValidMap = true;
		mTemperatureMap = new HashMap<Integer, Float>();
		for (int iVal = START_TEMPERATURE; iVal <= END_TEMPERATURE; iVal += DELTA_TEMPERATURE) {
			String key = Integer.toString(iVal);
			if (temperatureMap.get(key) == null) {
				isValidMap = false;
				break;
			} else {
				String value = (String) temperatureMap.get(key);
				try {
					float floatValue = Float.parseFloat(value);
					mTemperatureMap.put(iVal, floatValue);
				} catch (Exception e) {
					isValidMap = false;
					break;
				}
			}
		}

		if (isValidMap) {
			mUnitTemperatureMap = new HashMap<Integer, Float>();

			for (int iVal = START_TEMPERATURE; iVal <= END_TEMPERATURE; iVal += DELTA_TEMPERATURE) {
				if (iVal <= END_TEMPERATURE - DELTA_TEMPERATURE) {
					float currVal = mTemperatureMap.get(iVal);
					float nextVal = mTemperatureMap.get(iVal
							+ DELTA_TEMPERATURE);

					if (Float.compare(currVal, nextVal) == 0) {
						mUnitTemperatureMap.put(iVal, currVal);
					} else {
						mUnitTemperatureMap.put(iVal, DELTA_TEMPERATURE * 1.0f
								/ (nextVal - currVal));
					}
				} else {
					mUnitTemperatureMap.put(
							iVal,
							mUnitTemperatureMap.get(END_TEMPERATURE
									- DELTA_TEMPERATURE));
				}
			}

			mUnitBelowStart = mUnitTemperatureMap.get(START_TEMPERATURE);
			mUnitUpEnd = mUnitTemperatureMap.get(END_TEMPERATURE);

			mHasTemperatureMapData = true;
		}

		if (!isValidMap) {
			mHasTemperatureMapData = false;
			mUnitTemperatureMap = null;
			mTemperatureMap = null;
			return;
		}
	}

	private float getMappedTemperature(float temperature) {
		Map<String, Object> temperatureMap = UserInfo.getIntance()
				.getTemperatureMap();
		if (!mHasTemperatureMapData) {
			return temperature;
		}

		if (temperature <= mTemperatureMap.get(START_TEMPERATURE)) {
			return ((START_TEMPERATURE * 1.0f) - (mUnitBelowStart * (mTemperatureMap
					.get(START_TEMPERATURE) - temperature)));
		} else if (temperature >= mTemperatureMap.get(END_TEMPERATURE)) {
			return ((END_TEMPERATURE * 1.0f) + mUnitUpEnd
					* (temperature - mTemperatureMap.get(END_TEMPERATURE)));
		} else {
			float lastTemp = START_TEMPERATURE;
			int lastVal = START_TEMPERATURE;

			for (int iVal = START_TEMPERATURE; iVal <= END_TEMPERATURE; iVal += DELTA_TEMPERATURE) {
				float currVal = mTemperatureMap.get(iVal);

				if (currVal >= temperature) {
					return (lastTemp + (temperature - mTemperatureMap
							.get(lastVal)) * mUnitTemperatureMap.get(lastVal));
				}
				lastTemp = iVal;
				lastVal = iVal;
			}

			return temperature;
		}
	}

	private String dumpMap(Map<String, Object> map) {
		if (map == null) {
		}
		Set<Entry<String, Object>> entrySet = map.entrySet();
		String msg = "";
		for (Entry<String, Object> entry : entrySet) {
			msg += entry.getKey();
			msg += ",";
			msg += (String) entry.getValue() + ";";
		}

		return msg;
	}
}
