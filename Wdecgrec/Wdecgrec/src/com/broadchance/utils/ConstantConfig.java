package com.broadchance.utils;

public class ConstantConfig {
	public static boolean Debug = true;

	public final static String ACTION_PREFIX = "com.broadchance.wdecgrec";
	public final static String PKG_NAME = "com.broadchance.wdecgrec";
	// public final static String BLE_UUID_READ =
	// 单通道"0000fff4-0000-1000-8000-00805f9b34fb";
	// 三通道8ac32d3f-5cb9-4d44-bec2-ee689169f626
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

	/**
	 * APP目录
	 */
	public final static String APP_DIR = "wdecgrec";
	public final static String ECGDATA_SUFFIX = ".dat";
	// public final static String BREATHDATA_SUFFIX = ".bdat";
	// http://192.168.0.196:60015/index.php/Api/index/mobileinterface.html
	// http://123.59.129.119:8080/index.php/Api/index/mobileinterface.html
	public static final String SERVER_URL = "http://123.59.129.119:8080/index.php/Api/index/mobileinterface.html";
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
	 * 传感器电极脱落 A00001预警延迟发生时间s
	 */
	public static int AlertA00001_Delay_Raise = 8;
	/**
	 * 传感器电极脱落 A00001预警延迟取消时间
	 */
	public static int AlertA00001_Delay_Clear = 8;
	/**
	 * BLE信号断联 A00002预警延迟发生时间
	 */
	public static int AlertA00002_Delay_Raise = 8;
	/**
	 * BLE信号断联 A00002预警延迟取消时间
	 */
	public static int AlertA00002_Delay_Clear = 8;
	/**
	 * 网络无信号 A00003预警延迟发生时间
	 */
	public static int AlertA00003_Delay_Raise = 0;
	/**
	 * 网络无信号 A00003预警延迟取消时间
	 */
	public static int AlertA00003_Delay_Clear = 0;
	/**
	 * 网关电量低 A00004预警延迟发生时间
	 */
	public static int AlertA00004_Delay_Raise = 8;
	/**
	 * 网关电量低 <网关电量低百分比
	 */
	public static float AlertA00004_Limit_Raise = 0.15f;
	/**
	 * 网关电量低 A00004预警延迟取消时间
	 */
	public static int AlertA00004_Delay_Clear = 8;
	/**
	 * 网关电量低 >手机电量
	 */
	public static float AlertA00004_Limit_Clear = 0.25f;
	/**
	 * 传感器电量低 A00005预警延迟发生时间
	 */
	public static int AlertA00005_Delay_Raise = 0;
	/**
	 * 传感器电量低 <传感器电量低
	 */
	public static float AlertA00005_Limit_Raise = 2.6f;
	/**
	 * 传感器电量低 A00005预警延迟取消时间
	 */
	public static int AlertA00005_Delay_Clear = 0;
	/**
	 * 传感器电量低 >传感器电量取消预警阈值
	 */
	public static float AlertA00005_Limit_Clear = 2.8f;
	/**
	 * 心动过速 A00006预警延迟发生时间
	 */
	public static int AlertA00006_Delay_Raise = 8;
	/**
	 * 心动过速 >心动过速阈值
	 */
	public static int AlertA00006_Limit_Raise = 120;
	/**
	 * 心动过速 A00006预警延迟取消时间
	 */
	public static int AlertA00006_Delay_Clear = 8;
	/**
	 * 心动过速 <=阈值
	 */
	public static int AlertA00006_Limit_Clear = 120;
	/**
	 * 心动过缓 A00007预警延迟发生时间
	 */
	public static int AlertA00007_Delay_Raise = 8;
	/**
	 * 心动过缓 <心电过缓阈值
	 */
	public static int AlertA00007_Limit_Raise = 60;
	/**
	 * 心动过缓 A00007预警延迟取消时间
	 */
	public static int AlertA00007_Delay_Clear = 8;
	/**
	 * 心动过缓 >=阈值
	 */
	public static int AlertA00007_Limit_Clear = 60;
	/**
	 * 停搏 A00008预警延迟发生时间
	 */
	public static int AlertA00008_Delay_Raise = 4;
	public static int AlertA00008_Limit_Raise = 0;
	/**
	 * 停搏 A00008预警延迟取消时间
	 */
	public static int AlertA00008_Delay_Clear = 4;
	public static int AlertA00008_Limit_Clear = 0;
	/**
	 * 动态验证码
	 */
	public static String CERTKEY;
	/**
	 * 业务单号
	 */
	public static String ORDERNO;
	/**
	 * 心率保留数据
	 */
	public static int HeartRateFrequency = 3800;

}
