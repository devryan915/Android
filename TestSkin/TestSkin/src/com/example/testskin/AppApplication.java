package com.example.testskin;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppApplication extends Application {

	public static String curVer;

	@Override
	public void onCreate() {
		super.onCreate();
		Instance = this;
		initInstance();
	}

	public static AppApplication Instance;

	void initInstance() {
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo(ConstantConfig.PKG_NAME,
					PackageManager.GET_CONFIGURATIONS);
			curVer = pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
