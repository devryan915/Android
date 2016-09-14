/**
 * 
 */
package com.broadchance.wdecgrec.test;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertType;

/**
 * @author ryan.wang
 * 
 */
public class Test {
	private static final String TAG = Test.class.getSimpleName();

	public void test() {
		// testTimer();
	}

	private void testAlert(boolean isSend) throws JSONException {
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (isSend) {
			LogUtil.d(TAG, "发送预警");
			JSONObject alertObj = new JSONObject();
			try {
				alertObj.put("id", AlertType.B00003.getValue());
				alertObj.put("state", 1);
				alertObj.put("time", CommonUtil.getTime_B());
				JSONObject value = new JSONObject();
				alertObj.put("value", value);
				AlertMachine.getInstance()
						.sendAlert(AlertType.B00003, alertObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			LogUtil.d(TAG, "取消预警");
			AlertMachine.getInstance().cancelAlert(AlertType.B00003);
		}
	}

	long t;

	public void testTimer() {
		t = System.currentTimeMillis();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				Log.e(TAG, "testTimer" + (System.currentTimeMillis() - t)
						/ 1000);
			}
		};
		timer.schedule(task, 10, 1000);
		timer.cancel();
		t = System.currentTimeMillis();
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				try {
					testAlert(true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e(TAG, "testTimer" + (System.currentTimeMillis() - t)
						/ 1000);
			}
		};
		timer.schedule(task, 10);
		Timer cancelTimer = new Timer();
		TimerTask cancelTask = new TimerTask() {

			@Override
			public void run() {
				try {
					testAlert(false);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e(TAG, "testTimer" + (System.currentTimeMillis() - t)
						/ 1000);
			}
		};
		cancelTimer.schedule(cancelTask, 10000);
	}

	public void testJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("test", "haha");
			obj.put("test", "haha1");
			obj.put("test", "haha2");
			System.out.println(obj.get("test"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void testBit() {
		// 测试bit位
		byte b = (byte) -255;
		int bit1 = (b & 0x80) >> 7;
		int bit2 = (b & 0x40) >> 6;
		int bit3 = (b & 0x20) >> 5;
		int bit4 = (b & 0x10) >> 4;
		int bit5 = (b & 0x08) >> 3;
		int bit6 = (b & 0x04) >> 2;
		int bit7 = (b & 0x02) >> 1;
		int bit8 = (b & 0x01) >> 0;
		StringBuffer bitStr = new StringBuffer();
		bitStr.append(bit1);
		bitStr.append(bit2);
		bitStr.append(bit3);
		bitStr.append(bit4);
		bitStr.append(bit5);
		bitStr.append(bit6);
		bitStr.append(bit7);
		bitStr.append(bit8);
		LogUtil.d(TAG, bitStr);
	}
}
