package com.langlang.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.broadchance.xiaojian.service.DataStorageService;
import com.langlang.global.UserInfo;

public class SDChecker {
	public static final long MIN_SPACE_M = 100L;
	public static final long SPACE_M_0 = 10L;
	public static final long SPACE_M_1 = 50L;
	public static final long SPACE_M_2 = 95L;
	
	private boolean alerted = false;
	private Object lock = new Object();
	
	private Context context;
	private long checkSpace;
	
	public SDChecker(Context context, long checkSpace) {
		this.context = context;
		this.checkSpace = checkSpace; 
		alerted = false;
	}
	
	public SDChecker(Context context) {
		this.context = context;
		this.checkSpace = 0;
		alerted = false;
	}
	
	public boolean hasEnoughSpace() {
		if (SDCardUtils.getAvaiableSpace() > MIN_SPACE_M) {
			return true;
		}
		
		boolean isAvailable = SDCardUtils.isAvailableSpace(checkSpace);
		if (!isAvailable) {
			sendIntentToClearStorage();
		}
		
		return isAvailable;
	}
	
	public void checkAndAlert() {
		if (checkSpace == 0) {
			alertMsg();
		}
		else if (!hasEnoughSpace()) {
			alertMsg();
		}
	}
	
	private void alertMsg() {
		if (getAlerted() == true) {
			return;
		}
		else {
			setAlerted(true);
			
			long available = SDCardUtils.getAvaiableSpace();
			
			String msg;
			
			if (available <= 0) {
				msg = "您的外部存储空间目前不可用,";
				msg += "在这种情况下,应用程序将无法保存您的数据, 请您检查";
			}
			else {
				msg = "您的外部存储空间剩余" + available + "M,";
				if (available < MIN_SPACE_M) {
					msg += ("小于" + MIN_SPACE_M + "M,");
				}
				msg += "如果存储空间不足,应用程序将无法保存您的数据,建议您清理一下外部存储空间";
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("提示");
			builder.setMessage(msg)
					.setCancelable(false)
					.setPositiveButton("好的,我知道了", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							setAlerted(false);
							return;
						}
					}).show();
		}
	}
	
	public boolean isAlertShowed() {
		return alerted;
	}
	
	private void setAlerted(boolean hasAlerted) {
		synchronized (lock) {
			this.alerted = hasAlerted;
		}
	}
	
	private boolean getAlerted() {
		synchronized (lock) {
			return this.alerted;
		}
	}
	
	private void sendIntentToClearStorage() {
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		if ((uid != null) && (uid.length() >0)) {
			Intent intent = new Intent(DataStorageService.ACTION_CLEAR_STORAGE);
			intent.putExtra(DataStorageService.CLEAR_UID, uid);
			context.sendBroadcast(intent);
		}
	}
}
