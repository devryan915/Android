package com.broadchance.xiaojian.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

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
import android.util.JsonReader;
import android.util.Log;

import com.broadchance.xiaojian.entity.ECGEntity;
import com.broadchance.xiaojian.entity.SleepEntity;
import com.broadchance.xiaojian.entity.SportsEntity;
import com.broadchance.xiaojian.utils.CommonUtil;
import com.broadchance.xiaojian.utils.Constant;
import com.broadchance.xiaojian.utils.HttpAsyncTask;
import com.broadchance.xiaojian.utils.HttpAsyncTask.HttpReqCallBack;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.manager.WarningHteManager;
import com.langlang.utils.Filter;

public class BleDataDomainService extends Service {
	private static final String ACTION_UPLOAD_COLLECT = "com.xiaoyun.upload_collect";
	private static final String ACTION_UPLOAD_UPLOAD = "com.xiaoyun.upload_upload";
	private static final int DATA_COLLECT_PERIOD = 1 * 1000;
	private static final int DATA_COLLECT_UPLOAD = 12 * 1000 * 60 * 60;
	private Queue<Integer> queue = new LinkedList<Integer>();
	public static final int DataType_ECG = 1;
	public static final int DataType_Sleep = 2;
	public static final int DataType_Sports = 3;
	private Filter filter = new Filter();
	private static String TAG = BleDataDomainService.class.getSimpleName();
	private JSONArray sleepData = new JSONArray();
	private JSONArray ecgData = new JSONArray();
	private JSONArray sportsData = new JSONArray();
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (ACTION_UPLOAD_COLLECT.equals(action)) {
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
						ECGEntity ecgEntity = new ECGEntity();
						ecgEntity.setBleDatas(ecgData.toString());
						ecgEntity.setDataID(UUID.randomUUID().toString());
						ecgEntity.setDataTime(CommonUtil
								.getCurTime("yyyy-MM-dd HH:mm:ss"));
						ecgEntity.setDeviceNo(UserInfo.getIntance()
								.getUserData().getDevice_number());
						ecgEntity.setIsError(true);
						ecgEntity.setMobileNo(UserInfo.getIntance()
								.getUserData().getMobile_phone());
						ecgEntity.addData(BleDataDomainService.this);
						Log.i(TAG, "本地存储心电：" + ecgData.toString());
						ecgData = new JSONArray();
					}
					if (GlobalStatus.getInstance().getBreath() > 0) {
						JSONObject data = new JSONObject();
						data.put("datetime",
								CommonUtil.getCurTime("yyyy-MM-dd HH:mm:ss"));
						data.put("fanshen", GlobalStatus.getInstance()
								.getBreath());
						data.put("breathval", GlobalStatus.getInstance()
								.getBreath());
						data.put("fastbreathval", GlobalStatus.getInstance()
								.getBreath());
						data.put("slowbreathval", GlobalStatus.getInstance()
								.getBreath());
						data.put("sleepTime", GlobalStatus.getInstance()
								.getBreath());
						data.put("fallsleepTime", "5/1 22:00");
						data.put("awakesleepTime", "5/2 6:00");
						JSONObject greenTimeSeries=new JSONObject();
						greenTimeSeries.put("X", "1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12");
						greenTimeSeries.put("Y", "12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4,26.1, 23.6, 20.3, 17.2, 13.9");
						JSONObject violetTimeSeries=new JSONObject();
						violetTimeSeries.put("X", "3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14");
						violetTimeSeries.put("Y", "2, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14,11");
						JSONObject orangeTimeSeries=new JSONObject();
						orangeTimeSeries.put("X", "7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,18");
						orangeTimeSeries.put("Y", "5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15,9, 6 ");
						data.put("greenTimeSeries", greenTimeSeries);
						data.put("violetTimeSeries", violetTimeSeries);
						data.put("orangeTimeSeries", orangeTimeSeries);
						sleepData.put(data);
					}
					if (sleepData.length() > 50) {
						SleepEntity sleepEntity = new SleepEntity();
						sleepEntity.setBleDatas(sleepData.toString());
						sleepEntity.setDataID(UUID.randomUUID().toString());
						sleepEntity.setDataTime(CommonUtil
								.getCurTime("yyyy-MM-dd HH:mm:ss"));
						sleepEntity.setDeviceNo(UserInfo.getIntance()
								.getUserData().getDevice_number());
						sleepEntity.setMobileNo(UserInfo.getIntance()
								.getUserData().getMobile_phone());
						sleepEntity.addData(BleDataDomainService.this);
						Log.i(TAG, "本地存储睡眠：" + sleepData.toString());
						sleepData = new JSONArray();
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
						data.put("calories",  GlobalStatus.getInstance().getCalories());
						data.put("heartrate", hrHeartRate.getHeartRate());
						sportsData.put(data);
					}
					if (sportsData.length() > 50) {
						SportsEntity sportsEntity = new SportsEntity();
						sportsEntity.setBleDatas(sportsData.toString());
						sportsEntity.setDataID(UUID.randomUUID().toString());
						sportsEntity.setDataTime(CommonUtil
								.getCurTime("yyyy-MM-dd HH:mm:ss"));
						sportsEntity.setDeviceNo(UserInfo.getIntance()
								.getUserData().getDevice_number());
						sportsEntity.setMobileNo(UserInfo.getIntance()
								.getUserData().getMobile_phone());
						sportsEntity.addData(BleDataDomainService.this);
						Log.i(TAG, "本地存储运动：" + sportsData.toString());
						sportsData = new JSONArray();
					}
				} catch (JSONException e) {
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
			} else if (ACTION_UPLOAD_UPLOAD.equals(action)) {
				uploadData();
				releaseData();
			}
		}
	};

	private void releaseData() {
		String dataId;
		ECGEntity ecgEntity = new ECGEntity();
		Queue<String> queue = ecgEntity.getOutofDateDataIDs(this);
		while ((dataId = queue.poll()) != null) {
			ecgEntity.setDataID(dataId);
			ecgEntity.deleteData(this);
		}
		SleepEntity sleepEntity = new SleepEntity();
		queue = sleepEntity.getOutofDateDataIDs(this);
		while ((dataId = queue.poll()) != null) {
			sleepEntity.setDataID(dataId);
			sleepEntity.deleteData(this);
		}
		SportsEntity sportsEntity = new SportsEntity();
		queue = ecgEntity.getOutofDateDataIDs(this);
		while ((dataId = queue.poll()) != null) {
			sportsEntity.setDataID(dataId);
			sportsEntity.deleteData(this);
		}
	}

	private void uploadData() {
		JSONArray uploadData = new JSONArray();
		JSONArray temp;
		temp = new ECGEntity().getUploadData(this);
		try {
			if (temp != null && temp.length() > 0) {
				for (int i = 0; i < temp.length(); i++) {
					uploadData.put(temp.getJSONObject(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		temp = new SleepEntity().getUploadData(this);
		try {
			if (temp != null && temp.length() > 0) {
				for (int i = 0; i < temp.length(); i++) {
					uploadData.put(temp.getJSONObject(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		temp = new SportsEntity().getUploadData(this);
		try {
			if (temp != null && temp.length() > 0) {
				for (int i = 0; i < temp.length(); i++) {
					uploadData.put(temp.getJSONObject(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HashMap<String, Object> propertys = new HashMap<String, Object>();
		propertys.put("datas", uploadData.toString());
		HttpAsyncTask.fetchData(Constant.METHOD_UPLOAD_DATABatch, propertys,
				new HttpReqCallBack() {
					@Override
					public void deal(SoapObject result) {
						if (result != null) {
							try {
								JSONArray datas = new JSONArray(result
										.getPropertyAsString(0));
								String dataId;
								int datatype;
								ECGEntity ecgEntity;
								SleepEntity sleepEntity;
								SportsEntity sportsEntity;
								for (int i = 0; i < datas.length(); i++) {
									dataId = datas.getJSONObject(i).getString(
											"dataid");
									datatype = datas.getJSONObject(i).getInt(
											"datatype");

									switch (datatype) {
									case DataType_ECG:
										ecgEntity = new ECGEntity();
										ecgEntity.setDataID(dataId);
										ecgEntity
												.updateUploadDataStatus(BleDataDomainService.this);
										break;
									case DataType_Sleep:
										sleepEntity = new SleepEntity();
										sleepEntity.setDataID(dataId);
										sleepEntity
												.updateUploadDataStatus(BleDataDomainService.this);
										break;
									case DataType_Sports:
										sportsEntity = new SportsEntity();
										sportsEntity.setDataID(dataId);
										sportsEntity
												.updateUploadDataStatus(BleDataDomainService.this);
										break;
									default:
										break;
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

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
		startCollect();
		startUpload();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		endCollect();
		endUpload();
		unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
	}

	private void startCollect() {
		Intent reIntent = new Intent();
		reIntent.setAction(ACTION_UPLOAD_COLLECT);
		AlarmManager am = (AlarmManager) this
				.getSystemService(BleDataDomainService.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, reIntent, 0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200,
				DATA_COLLECT_PERIOD, pi);
	}

	private void endCollect() {
		Intent intent = new Intent();
		intent.setAction(ACTION_UPLOAD_COLLECT);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.cancel(pi);
	}

	private void startUpload() {
		uploadData();
		Intent reIntent = new Intent();
		reIntent.setAction(ACTION_UPLOAD_UPLOAD);
		AlarmManager am = (AlarmManager) this
				.getSystemService(BleDataDomainService.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, reIntent, 0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200,
				DATA_COLLECT_UPLOAD, pi);
	}

	private void endUpload() {
		Intent intent = new Intent();
		intent.setAction(ACTION_UPLOAD_UPLOAD);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.cancel(pi);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_UPLOAD_COLLECT);
		intentFilter.addAction(BleDataParserService.ACTION_ECG_DATA_AVAILABLE);
		return intentFilter;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
