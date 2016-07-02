/**
 * @Title: MessageService.java
 * @Package: com.kc.ihaigo.service
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年8月4日 下午2:58:37

 * @version V1.0

 */

package com.kc.ihaigo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;

/**
 * @ClassName: MessageService
 * @Description: 消息推送服务
 * @author: ryan.wang
 * @date: 2014年8月4日 下午2:58:37
 * 
 */

public class MessageService extends Service {

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (true) {
			// 暂时注释掉代码
			return;
		}
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = getResources().getText(
				R.string.dialog_downloading);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		// 定义下拉通知栏时要展现的内容信息
		Context context = getApplicationContext();
		CharSequence contentTitle = getResources().getString(
				R.string.ihaigo_update);
		CharSequence contentText = getResources().getString(
				R.string.ihaigo_update_complete);
		Intent notificationIntent = new Intent(this, IHaiGoMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
		manager.notify(1, notification);
	}

}
