package com.magus;

public class HttpConst {

	/**
	 * 缓存标识的key
	 */
	public final static String CACHE_MODE = "cacheMode";

	/**
	 * 数据缓存标识。 表示从缓存里获取数据，如果缓存里没有，则连网获取数据，然后放入缓存中。
	 */
	public final static int DATA_CACHE_MODE = 0;
	/**
	 * 数据缓存标识。 表示连网获取最新的数据，并同时更新到缓存里。
	 */
	public final static int DATA_UPDATE_MODE = 1;

	public final static String POST = "POST";
	public final static String GET = "GET";
	public final static String CHARSET = "UTF-8";
	public final static String WAP = "wap";
	public final static String CTWAP = "ctwap";
	public final static String WAP_PROXY = "10.0.0.172";
	public final static String CTWAP_PROXY = "10.0.0.200";
	public final static int PROXY_PORT = 80;
	public final static String NO_CONNECTION = "没有可用的网络连接,请检查网络.";

}
