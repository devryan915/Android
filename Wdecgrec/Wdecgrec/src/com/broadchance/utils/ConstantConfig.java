package com.broadchance.utils;

import com.broadchance.manager.SettingsManager;

public class ConstantConfig {
	public static boolean Debug = false;
	public final static String DebugTAG = "DebugTAG";

	public final static String PKG_NAME = "com.broadchance.wdecgrec";
	public final static String ACTION_PREFIX = PKG_NAME + ".";
	// public final static String BLE_UUID_READ =
	// 单通道"0000fff4-0000-1000-8000-00805f9b34fb";
	// 三通道8ac32d3f-5cb9-4d44-bec2-ee689169f626
	public final static String BLE_UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	public final static String BLE_UUID_READ = "0000fff4-0000-1000-8000-00805f9b34fb";
	public final static String BLE_UUID_WRITE = "0000fff1-0000-1000-8000-00805f9b34fb";
	/**
	 * 已选择的皮肤的名称
	 */
	public final static String PREFERENCES_SKINID = "settings_skinid";
	// public final static String PREFERENCES_USERNAME = "login_username";
	public final static String PREFERENCES_USERPWDCHK = "login_userrempwd";
	public final static String PREFERENCES_ISHIDEWELCOME = "welcome_ishide";
	public final static String PREFERENCES_DEVIVCENUMBER = "device_number";

	public final static String PREFERENCES_UPLOADNETTYPE = "upload_nettype";
	public final static String PREFERENCES_LOWSINGNAL = "lowsingal";
	public final static String PREFERENCES_LOWPOWER = "lowpower";
	public final static String PREFERENCES_DEVOFF = "devoff";
	public final static String PREFERENCES_DEVFALLOFF = "devfalloff";
	public final static String PREFERENCES_GPS = "gps";
	public final static String PREFERENCES_GPSLON = "gps_lon";
	public final static String PREFERENCES_XDPI = "dpiconfig_x";
	public final static String PREFERENCES_YDPI = "dpiconfig_y";
	public final static String PREFERENCES_GPSLAT = "gps_lat";
	public final static String PREFERENCES_OFFDATA = "offdata";
	public final static String PREFERENCES_NEWAPPVER = "newappver";
	public final static String PREFERENCES_NEWAPPURL = "newappurl";
	public final static String PREFERENCES_SERVERURL = "serverurl";
	/**
	 * APP目录
	 */
	public final static String APP_DIR = "wdecgrec";
	public final static String ECGDATA_SUFFIX = ".dat";
	// public final static String BREATHDATA_SUFFIX = ".bdat";
	// http://192.168.0.196:60015/index.php/Api/index/mobileinterface.html
	// http://123.59.129.119:8080/index.php/Api/index/mobileinterface.html
	// http://106.75.62.31:8080/index.php/Api/index/mobileinterface.html
	public final static String SERVER_URL_DEF = "http://106.75.62.31:8080/index.php/Api/index/mobileinterface.html";
	public static String SERVER_URL = SettingsManager.getInstance()
			.getServerURL();
	// public static final String SERVER_URL = "http://192.168.1.134:56285";
	// public static final String SERVER_URL = "http://www.thoth-health.com";
	/**
	 * 实时上传
	 */
	// public static final String SERVER_REALTIME_URL = "192.168.1.202";
	// public static final String SERVER_REALTIME_URL = "192.168.1.134";
	public static final String SERVER_REALTIME_URL = "thoth-health.com";
	/**
	 * 实时上传端口
	 */
	public static final int SERVER_REALTIME_PORT = 9999;
	public static String AUTHOR_TOKEN = "";

	// public static final String DATA_DATE_FORMAT = "yyyyMMddHHmmssSSSZ";
	/**
	 * 网络延迟时间,单位ms
	 */
	public static int TIME_DELAY = 5000;
	/**
	 * 批量上传间隔时间s的数据125*Batch_Interval个点
	 */
	public static int Batch_Interval = 60;
	// public static int MAX_BATCH_LIMIT=0;
	/**
	 * 实时间隔s 125*Batch_Interval个点
	 */
	public static int Real_Interval = 4;
	// public static int MAX_REAL_LIMIT = 4;

	/**
	 * 动态验证码
	 */
	public static String CERTKEY;
	/**
	 * 业务单号
	 */
	// public static String ORDERNO;
	/**
	 * 心率保留数据
	 */
	public static int HeartRateFrequency = 3800;
	public final static int Alert_HR_Up = 200;
	public final static int Alert_HR_Down = 36;

}
