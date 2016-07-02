/**
 * @Title: NetChangeReceiver.java
 * @Package: com.kc.ihaigo.receiver
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年8月4日 下午3:58:34

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;

/**
 * @ClassName: NetChangeReceiver
 * @Description: 监听网络状态变化
 * @author: ryan.wang
 * @date: 2014年8月4日 下午3:58:34
 * 
 */

public class NetChangeReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			boolean linked = false;
			if ((networkInfo != null) && (networkInfo.getType() == 0))
				// 手机网路
				// NetConnectionType.getTypeByCode(networkInfo.getSubtype());
				linked = true;
			if ((networkInfo != null) && (networkInfo.getType() == 1)) {
				// wifi
				// NetConnectionType.WIFI;
				linked = true;
			} else {
				// 网络尚未连接
				// NetConnectionType.NONE;
				linked = false;
			}
			LocalActivityManager lam = PersonalGroupActivity.group
					.getLocalActivityManager();
			ActivityInfo info = context.getPackageManager().getReceiverInfo(
					intent.getComponent(), PackageManager.GET_META_DATA);
			String receiverActivity = info.metaData
					.getString("receiverActivity");
			String[] activityIds = receiverActivity.split(";");
			for (int i = 0; i < activityIds.length; i++) {
				Activity activity = lam.getActivity(activityIds[i]);
				if (activity instanceof IHaiGoActivity) {
					((IHaiGoActivity) activity).refreshNetStatus(linked);
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
