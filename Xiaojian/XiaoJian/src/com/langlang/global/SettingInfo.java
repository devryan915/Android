package com.langlang.global;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * 一个选择设置的SharedPreferences控制类
 * 
 * @author Administrator
 *
 */
public class SettingInfo {
	private final String SP_NAME = "sp_setting";
	private final String UPLOAD_FILE_KEY_INDEX=UserInfo.getIntance().getUserData().getMy_name()+"_upload_file";
	private final String DISCONNECTED_NOTIFICATION_KEY_INDEX=UserInfo.getIntance().getUserData().getMy_name()+"_ble";
	private final String WEAKNESS_NOTIFICATION_KEY_INDEX=UserInfo.getIntance().getUserData().getMy_name()+"_rssi";
	private final String CHECK_GPS_KEY_INDEX=UserInfo.getIntance().getUserData().getMy_name()+"_gps";
	public static final int CHECK_UPLOAD_WF=1;
	
	private SharedPreferences sharedPreferences;

	public SettingInfo(Context context) {
		sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
	}

	public int getUploadNetwork() {
		return sharedPreferences.getInt(UPLOAD_FILE_KEY_INDEX,0);
	};

	public void setUploadNetwork(int value) {
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putInt(UPLOAD_FILE_KEY_INDEX, value);
		editor.commit();
	};

	public int getWeaknessNotification() {
		return sharedPreferences.getInt(WEAKNESS_NOTIFICATION_KEY_INDEX, 1);
	};

	public void setWeaknessNotification(int value) {
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putInt(WEAKNESS_NOTIFICATION_KEY_INDEX, value);
		editor.commit();
	};

	public int getDisconnectedNotification() {
		return sharedPreferences.getInt(DISCONNECTED_NOTIFICATION_KEY_INDEX, 1);
	};

	public void setDisconnectedNotification(int value) {
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putInt(DISCONNECTED_NOTIFICATION_KEY_INDEX, value);
		editor.commit();
	};
	public void setGpsState(int value){
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putInt(CHECK_GPS_KEY_INDEX, value);
		editor.commit();
	}
	public int getGpsState(){
		return sharedPreferences.getInt(CHECK_GPS_KEY_INDEX, 0);
	}
	
	private final String CHANGE_MODE_INDEX=UserInfo.getIntance().getUserData().getMy_name()+"_mode";
	public void setChangeMode(int value){
			SharedPreferences.Editor editor=sharedPreferences.edit();
			editor.putInt(CHANGE_MODE_INDEX, value);
			editor.commit();
		}
		public int getChangeMode(){
			return sharedPreferences.getInt(CHANGE_MODE_INDEX, 0);
		}
}
