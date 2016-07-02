package com.broadchance.wdecgrec.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.SSXLXService;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.main.ModeActivity;
import com.broadchance.wdecgrec.services.GpsService;

public class GPSReceiver extends BroadcastReceiver {
	protected static final String TAG = GPSReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();
		final String lon = bundle.getString("lon");
		final String lat = bundle.getString("lat");
		if (ConstantConfig.Debug) {
			UIUtil.showToast(context, "当前经纬度\n经度：" + lon + "\n纬度：" + lat);
		}
		if (lon != null && !lon.isEmpty() && lat != null && !lat.isEmpty()) {
			context.stopService(new Intent(context, GpsService.class));
			// 定位数据暂存
			SettingsManager.getInstance().setGPSLon(lon);
			SettingsManager.getInstance().setGPSLat(lat);
		}
	}
}