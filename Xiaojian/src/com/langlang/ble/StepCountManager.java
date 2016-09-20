package com.langlang.ble;

import java.util.Date;

import com.langlang.global.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;

public class StepCountManager {
	private SharedPreferences spStepCountManager;	
	
	private static final String STEP_COUNT_SP = "STEP_COUNT_SP";
//	private static final String LAST_STEP_DATE = "LAST_STEP_DATE";
//	private static final String STEP_COUNT_BASE = "STEP_COUNT_BASE";
//	private static final String STEP_COUNT = "STEP_COUNT";
	
	private String LAST_STEP_DATE;
	private String STEP_COUNT_BASE;
	private String STEP_COUNT;
	
	public StepCountManager(Context context) {
		spStepCountManager = context.getSharedPreferences(STEP_COUNT_SP, Context.MODE_PRIVATE);
		
		LAST_STEP_DATE = "LAST_STEP_DATE_" + UserInfo.getIntance().getUserData().getDevice_number();
		STEP_COUNT_BASE = "STEP_COUNT_BASE_" + UserInfo.getIntance().getUserData().getDevice_number();
		STEP_COUNT = "STEP_COUNT_" + UserInfo.getIntance().getUserData().getDevice_number();
	}
	
	public Date getLastStepDate() {
		long lastStep = spStepCountManager.getLong(LAST_STEP_DATE, 0);
		if (lastStep == 0) {
			return null;
		}
		else {
			Date date = new Date();
			date.setTime(lastStep);
			return date;
		}
	}	
	public void setLastStepDate(Date date) {
		SharedPreferences.Editor editor = spStepCountManager.edit();
		editor.putLong(LAST_STEP_DATE, date.getTime());
		editor.commit();
	}
	
	public int getStepCount() {
		return spStepCountManager.getInt(STEP_COUNT, 0);
	}	
	public void setStepCount(int stepCount) {
		SharedPreferences.Editor editor = spStepCountManager.edit();
		editor.putInt(STEP_COUNT, stepCount);
		editor.commit();
	}
	
	public int getStepCountBase() {
		return spStepCountManager.getInt(STEP_COUNT_BASE, 0);
	}
	public void setStepCountBase(int base) {
		SharedPreferences.Editor editor = spStepCountManager.edit();
		editor.putInt(STEP_COUNT_BASE, base);
		editor.commit();
	}
	
	public int getLastStepCount() {
		int lastStepCount = getStepCountBase() + getStepCount();
		if (lastStepCount <= 0) {
			return 0;
		}
		else {
			return lastStepCount;
		}
	}
	
	public void reset() {
		int stepCount = getStepCount();
		setStepCountBase(-stepCount);		
//		setStepCount(0);
	}
}
