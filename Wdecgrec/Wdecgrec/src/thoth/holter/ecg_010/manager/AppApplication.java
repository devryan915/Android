package thoth.holter.ecg_010.manager;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.broadchance.utils.LogUtil;

public class AppApplication extends Application {

	public static String curVer;
	public static int verCode;
	public final static String PKG_NAME = "thoth.holter.ecg_010";

	// public Activity currentActivity;

	@Override
	public void onCreate() {
		super.onCreate();
		Instance = this;
		initInstance();
		// registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
		// @Override
		// public void onActivityCreated(Activity activity,
		// Bundle savedInstanceState) {
		//
		// }
		//
		// @Override
		// public void onActivityStarted(Activity activity) {
		//
		// }
		//
		// @Override
		// public void onActivityResumed(Activity activity) {
		// currentActivity = activity;
		// }
		//
		// @Override
		// public void onActivityPaused(Activity activity) {
		//
		// }
		//
		// @Override
		// public void onActivityStopped(Activity activity) {
		//
		// }
		//
		// @Override
		// public void onActivitySaveInstanceState(Activity activity,
		// Bundle outState) {
		//
		// }
		//
		// @Override
		// public void onActivityDestroyed(Activity activity) {
		//
		// }
		//
		// });
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public static AppApplication Instance;

	void initInstance() {
		// if (!ConstantConfig.Debug) {
		CrashHandler.getInstance().init(AppApplication.this);
		// }
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo(getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			curVer = pinfo.versionName;
			verCode = pinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		LogUtil.init();
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
