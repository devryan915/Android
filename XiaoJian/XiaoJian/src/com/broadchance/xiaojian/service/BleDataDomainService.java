package com.broadchance.xiaojian.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.broadchance.xiaojian.utils.CommonUtil;
import com.broadchance.xiaojian.utils.HttpAsyncTask;
import com.broadchance.xiaojian.utils.HttpAsyncTask.HttpReqCallBack;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.manager.WarningHteManager;
import com.langlang.utils.Filter;

public class BleDataDomainService extends Service {
	private static final String ACTION_ALARM = "com.xiaoyun.uploadservice";
	private Queue<Integer> queue = new LinkedList<Integer>();
	public static final int DataType_ECG = 1;
	public static final int DataType_Breath = 2;
	public static final int DataType_Sports = 3;
	private Filter filter = new Filter();
	private static String TAG = BleDataDomainService.class.getSimpleName();
	private JSONArray breathRate = new JSONArray();
	private JSONArray ecgData = new JSONArray();
	private JSONArray sportsData = new JSONArray();
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (ACTION_ALARM.equals(action)) {
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
				try {
					if (hasData) {
						JSONObject data = new JSONObject();
						data.put("data", CommonUtil.IntArray2String(queueData));
						data.put("datetime",
								CommonUtil.getCurTime("yyyy-MM-dd HH:mm:ss"));
						ecgData.put(data);
					}
					// 一分钟产生一条数据
					if (ecgData.length() > 60) {
						HashMap<String, Object> propertys = new HashMap<String, Object>();
						propertys.put("mobileno", UserInfo.getIntance()
								.getUserData().getAccountCode());
						propertys.put("deviceno", UserInfo.getIntance()
								.getUserData().getDevice_number());
						propertys.put("bledatas", ecgData.toString());
						propertys.put("datatime",
								CommonUtil.getCurTime("yyyy-MM-dd HH:mm:ss"));
						propertys.put("datatype", DataType_ECG);
						HttpAsyncTask.fetchData("UploadData", propertys,
								new HttpReqCallBack() {
									@Override
									public void deal(SoapObject result) {
										if (result != null) {
											Log.i(TAG, "上传心电数据成功");
										}
									}
								});
						ecgData = new JSONArray();
					}
					if (GlobalStatus.getInstance().getBreath() > 0) {
						JSONObject data = new JSONObject();
						data.put("datetime",
								CommonUtil.getCurTime("yyyy-MM-dd HH:mm:ss"));
						data.put("breathval", GlobalStatus.getInstance()
								.getBreath());
						breathRate.put(data);
					}
					if (breathRate.length() > 50) {
						HashMap<String, Object> sleeppropertys = new HashMap<String, Object>();
						sleeppropertys.put("mobileno", UserInfo.getIntance()
								.getUserData().getAccountCode());
						sleeppropertys.put("deviceno", UserInfo.getIntance()
								.getUserData().getDevice_number());
						sleeppropertys.put("bledatas", breathRate.toString());
						sleeppropertys.put("datatime", CommonUtil.getCurTime());
						sleeppropertys.put("datatype", DataType_Breath);
						HttpAsyncTask.fetchData("UploadData", sleeppropertys,
								new HttpReqCallBack() {
									@Override
									public void deal(SoapObject result) {
										if (result != null) {
											Log.i(TAG, "上传睡眠数据成功");
										}
									}
								});
						breathRate = new JSONArray();
					}
					TiannmaHeartRate hrHeartRate = new TiannmaHeartRate();
					if (GlobalStatus.getInstance().getCurrentStep() > 0
							|| hrHeartRate.getHeartRate() > 0
							|| GlobalStatus.getInstance().getBreath() > 0) {
						JSONObject data = new JSONObject();
						data.put("step", GlobalStatus.getInstance()
								.getCurrentStep());
						data.put("breathrate", GlobalStatus.getInstance()
								.getBreath());
						data.put("heartrate", hrHeartRate.getHeartRate());
						sportsData.put(data);
					}
					if (sportsData.length() > 50) {
						HashMap<String, Object> sleeppropertys = new HashMap<String, Object>();
						sleeppropertys.put("mobileno", UserInfo.getIntance()
								.getUserData().getAccountCode());
						sleeppropertys.put("deviceno", UserInfo.getIntance()
								.getUserData().getDevice_number());
						sleeppropertys.put("bledatas", sportsData.toString());
						sleeppropertys.put("datatime", CommonUtil.getCurTime());
						sleeppropertys.put("datatype", DataType_Sports);
						HttpAsyncTask.fetchData("UploadData", sleeppropertys,
								new HttpReqCallBack() {
									@Override
									public void deal(SoapObject result) {
										if (result != null) {
											Log.i(TAG, "上传运动数据成功");
										}
									}
								});
						sportsData = new JSONArray();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (BleDataParserService.ACTION_ECG_DATA_AVAILABLE
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
			}
		}
	};

	class TiannmaHeartRate {
		private int[] mDisplayHte = { 0, 0, 0, 0, 0 };
		private int mCurrHte = 0;
		private int mHteCount = 0;
		private int mHeartRate = 0;

		public int getHeartRate() {
			updateHeartRateData();
			return mHeartRate;
		}

		private void updateHeartRateData() {
			int currHte = GlobalStatus.getInstance().getHeartRate();
			if (mHteCount < 4) {
				mHeartRate = currHte;
			} else {
				int averageHte = getAverageHte();
				if ((currHte > ((int) (averageHte * 1.3f)))
						|| (currHte < ((int) (averageHte * 0.7f)))) {
					System.out.println("action MainActivity currHte[" + currHte
							+ "] average hte [" + averageHte + "]");
				} else {
					mHeartRate = currHte;
				}
			}
			mDisplayHte[mCurrHte] = currHte;
			mCurrHte++;
			if (mCurrHte >= 5)
				mCurrHte = 0;

			mHteCount++;
			if (mHteCount > 5)
				mHteCount = 5;
		}

		private int getAverageHte() {
			if (mHteCount == 0)
				return 0;
			int sum = 0;
			for (int i = 0; i < mHteCount; i++) {
				sum += mDisplayHte[i];
			}
			return (int) ((sum * 1.0f) / mHteCount);
		}
	}

	class TiannmaHteWarning {
		private WarningHteManager warningHteManager = new WarningHteManager(
				BleDataDomainService.this);
		private int mHteWarning = 0;

		private void updateHteWarningData() {
			String uid = UserInfo.getIntance().getUserData().getMy_name();
			if ((uid != null) && (uid.length() > 0)) {
				mHteWarning = warningHteManager
						.getWarningCount(new Date(), uid);
			} else {
				mHteWarning = 0;
			}
		}

		public int getHteWarning() {
			updateHteWarningData();
			return mHteWarning;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent reIntent = new Intent();
		reIntent.setAction(ACTION_ALARM);
		AlarmManager am = (AlarmManager) this
				.getSystemService(this.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, reIntent, 0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, 1000, pi);
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mGattUpdateReceiver);
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.cancel(pi);
		super.onDestroy();
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(ACTION_ALARM);
		intentFilter.addAction(BleDataParserService.ACTION_ECG_DATA_AVAILABLE);
		intentFilter.addAction(BleDataParserService.ACTION_RESP_DATA_AVAILABLE);
		intentFilter
				.addAction(BleDataParserService.ACTION_UPDATE_STEP_AND_CALORIES);
		intentFilter
				.addAction(BleDataParserService.ACTION_CURRENT_STATE_CHANGE);
		intentFilter.addAction(BleDataParserService.ACTION_TEMP);
		intentFilter
				.addAction(BleDataParserService.ACTION_UPDATE_ECG_HEART_RATE);
		intentFilter.addAction(BleDataParserService.ACTION_TUMBLE_HAPPENED);
		intentFilter.addAction(BleDataParserService.ACTION_ALERT_SD_STATUS);
		return intentFilter;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
