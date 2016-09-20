package com.langlang.global;

import java.util.Date;

import com.langlang.data.UserData;

import android.content.Context;
import android.content.SharedPreferences;

public class OfflineLoginManager {
	private SharedPreferences spOfflineLoginManager;	

	private static final String OFFLINE_LOGIN_SP = "OFFLINE_LOGIN_SP";
	
	private static final String PWD_PREFIX = "PWD_";
	private static final String LAST_LOGIN_PREFIX = "LAST_LOGIN_";
	private static final String ALLOW_PREFIX = "ALLOW_";
	private static final String USER_DATA_PREFIX = "USER_DATA_";
	
	public OfflineLoginManager(Context context) {
		spOfflineLoginManager = context.getSharedPreferences(OFFLINE_LOGIN_SP, Context.MODE_PRIVATE);
	}

	// AllowDays
	// 0, 3天
	// 1, 7天
	// 2, 永不
	public int getAllowDays(String uid) {
		String key = ALLOW_PREFIX + uid;
		return spOfflineLoginManager.getInt(key, 0);
	}
	public void setAllowDays(String uid, int days) {
		String key = ALLOW_PREFIX + uid;
		
		SharedPreferences.Editor editor = spOfflineLoginManager.edit();
		editor.putInt(key, days);
		editor.commit();
	}
	public int getAllowOfflineLoginDays(String uid) {
		int allow = getAllowDays(uid);
		if (allow == 0) {
			return 3;
		} else if (allow == 1) {
			return 7;
		} else {
			return 0;
		}
	}
	
	// Last login
	// 获取最后一次登录时间
	public Date getLastLogin(String uid) {
		String key = LAST_LOGIN_PREFIX + uid;
		
		long time = spOfflineLoginManager.getLong(key, 0);
		if (time == 0) {
			return null;
		}
		else {
			Date date = new Date();
			date.setTime(time);
			return date;
		}
	}
	public void setLastLogin(String uid, Date date) {
		String key = LAST_LOGIN_PREFIX + uid;
		
		SharedPreferences.Editor editor = spOfflineLoginManager.edit();
		editor.putLong(key, date.getTime());
		editor.commit();
	}

	// Password
	public String getPassword(String uid) {
		String key = PWD_PREFIX + uid;
		
		return spOfflineLoginManager.getString(key, null);
	}
	public void setPassword(String uid, String pwd) {
		String key = PWD_PREFIX + uid;
		
		SharedPreferences.Editor editor = spOfflineLoginManager.edit();
		editor.putString(key, pwd);
		editor.commit();
	}
	
	// UserData
	public String getUserDataAsString(String uid) {
		String key = USER_DATA_PREFIX + uid;
		
		return spOfflineLoginManager.getString(key, null);
	}
	public void setUserData(String uid, String userDataStr) {
		String key = USER_DATA_PREFIX + uid;
		
		SharedPreferences.Editor editor = spOfflineLoginManager.edit();
		editor.putString(key, userDataStr);
		editor.commit();
	}
}
