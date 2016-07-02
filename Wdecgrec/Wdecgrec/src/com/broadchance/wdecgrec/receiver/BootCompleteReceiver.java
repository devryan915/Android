package com.broadchance.wdecgrec.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.services.BluetoothLeService;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			String pkgName = "com.broadchance.wdecgrec";
			Intent intentApp = context.getPackageManager()
					.getLaunchIntentForPackage(pkgName);
			context.startActivity(intentApp);
			UIUtil.showToast(context, "start app " + pkgName);
		}

	}

}
