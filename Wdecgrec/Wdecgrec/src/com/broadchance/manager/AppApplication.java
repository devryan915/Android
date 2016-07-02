package com.broadchance.manager;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.broadchance.utils.ConstantConfig;

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
		// TODO 上线前打开
		CrashHandler.getInstance().init(AppApplication.this);
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo(ConstantConfig.PKG_NAME,
					PackageManager.GET_CONFIGURATIONS);
			curVer = pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// 初始化动作由各单例控制,加快APP启动速度
		// 初始化数据
		// DBHelper.getInstance().init(AppApplication.this);
		// 初始化缓存
		// ACache.getInstance().init(AppApplication.this);
		// 初始化设置
		// PreferencesManager.getInstance().init(AppApplication.this);
		// 初始化皮肤
		// SkinManager.getInstance().init(AppApplication.this);
	}
}
