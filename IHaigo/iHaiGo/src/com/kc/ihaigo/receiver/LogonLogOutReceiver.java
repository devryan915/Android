/**
 * @Title: LogonLogOutReceiver.java
 * @Package: com.kc.ihaigo.receiver
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月31日 上午9:47:43

 * @version V1.0

 */

package com.kc.ihaigo.receiver;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.util.Constants;

/**
 * @ClassName: LogonLogOutReceiver
 * @Description: 监控登入登出，并通知相关页面更新数据
 * @author: ryan.wang
 * @date: 2014年7月31日 上午9:47:43
 * 
 */

public class LogonLogOutReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		Constants.USER_ID = null;
		LocalActivityManager lam = PersonalGroupActivity.group
				.getLocalActivityManager();
		try {
			ActivityInfo info = context.getPackageManager().getReceiverInfo(
					intent.getComponent(), PackageManager.GET_META_DATA);
			String receiverActivity = info.metaData
					.getString("receiverActivity");
			String[] activityIds = receiverActivity.split(";");
			boolean login = intent.getBooleanExtra(Constants.LOG_STATUS, false);
			for (int i = 0; i < activityIds.length; i++) {
				Activity activity = lam.getActivity(activityIds[i]);
				if (activity instanceof IHaiGoActivity) {
					((IHaiGoActivity) activity).refreshLoginStatus(login);
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
