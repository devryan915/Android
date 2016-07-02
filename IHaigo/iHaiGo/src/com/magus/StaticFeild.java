package com.magus;

public final class StaticFeild {
	/**
	 * log 标识
	 */
	public static String TAG = "magus";

	public final static String METHOD_NAME = "method";

	public static String CACHEDIR = "/magusCache";

	/**
	 * 日志文件路径名称
	 */
	public static String logFile = MagusTools.getDataPath() + TAG + ".log";

	/**
	 * 图片缓存后缀名
	 */
	public static String bitmap_postfix = "b";
	/**
	 * 数据缓存后缀名
	 */
	public static String string_postfix = "s";
	@Deprecated
	/**
	 * 请改用HttpConst.GET
	 */
	public final static String GET = "GET";
	@Deprecated
	/**
	 * 请改用HttpConst.POST
	 */
	public final static String POST = "POST";
	public final static String SUCCESS = "0000";

	public static String packageName = "";

	public static String ver = "1.0";

	/** 弹出框确定按钮标识 */
	public final static int ALERT_OK = 1000;

	/** 弹出框取消按钮标识 */
	public final static int ALERT_CANCEL = 1001;

	/** 弹出框其他按钮标识 */
	public final static int ALERT_OTHER = 1002;

	public static String fgHost = MagusTools.isQA() ? "http://qa.fun-guide.mobi:3000" : "http://fgapi.fun-guide.mobi";

	/**
	 * 设备id
	 */
	public static String deviceId = "";
	/**
	 * 屏幕宽
	 */
	public static int width;
	/**
	 * 屏幕高
	 */
	public static int height;

	public static float density;
	public static int densityDip;
	/**
	 * 支付终端号 "LOTTERY_TICKET"; //购买彩票 "MOBILE_CHANGE"; //手机充值 "MOVIE_TICKET";
	 * //买电影票 "DEPOSIT"; //储值 "LIFE_SHUIFEI"; //水费 "LIFE_DIANFEI"; //电费
	 * "LIFE_MEIQIFEI"; //煤气费
	 */
	public final static String LOTTERY_TICKET = "LOTTERY_TICKET"; // 购买彩票
	public final static String MOBILE_CHANGE = "MOBILE_CHANGE"; // 手机充值
	public final static String MOVIE_TICKET = "MOVIE_TICKET"; // 买电影票
	public final static String DEPOSIT = "DEPOSIT"; // 储值
	public final static String LIFE_SHUIFEI = "LIFE_SHUIFEI"; // 水费
	public final static String LIFE_DIANFEI = "LIFE_DIANFEI"; // 电费
	public final static String LIFE_MEIQIFEI = "LIFE_MEIQIFEI"; // 煤气费

}
