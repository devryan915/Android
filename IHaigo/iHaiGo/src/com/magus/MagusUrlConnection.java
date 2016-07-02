package com.magus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.magus.MagusTools;
import com.magus.excption.HttpServiceErrorException;
import com.magus.excption.NoNetworkActivityException;

public class MagusUrlConnection {
	public static String sessionId;
	private static String verString, ver, mt = "";
	private static HashMap<String, String> propertyMap = new HashMap<String, String>();
	private static int connect_outTime = 30000;
	private final static int TIME_OUT_CODE = 504;
	private final static int SUCCEED_CODE = 200;
	private static String UAstr;

	public static void setPropertyMap(String propertyKey, String propertyValue) {
		propertyMap.put(propertyKey, propertyValue);
	}

	public static void setConnect_outTime(int connect_outTime) {
		MagusUrlConnection.connect_outTime = connect_outTime;
	}

	static {
		mt = (android.os.Build.BRAND + "," + android.os.Build.PRODUCT + "," + android.os.Build.VERSION.RELEASE).replaceAll(" ", "").replace("_", "").toUpperCase();
	}

	/**
	 * 设置统计参数mt,仅当此参数有其他用途的时候才设置。
	 * 
	 * @param mtStr
	 */
	public static void setMt(String mtStr) {
		mt = mtStr;
	}

	/**
	 * 获取给定链接的数据，默认使用post方法。
	 * 
	 * @param context
	 * @param path
	 * @return 获取的数据
	 * @throws NoNetworkActivityException
	 * @throws IOException
	 * @throws HttpServiceErrorException
	 * @throws HttpServiceTimeOutException
	 */
	public static String getConnectionStr(Context context, String path) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		String responseStr = getConnectionStr(context, path, HttpConst.POST);
		return responseStr;
	}

	/**
	 * 获取给定链接的数据
	 * 
	 * @param context
	 * @param path
	 * @param method
	 *            请求方法
	 * 
	 * @return 获取的数据
	 * @throws IOException
	 * @throws HttpServiceErrorException
	 * @throws HttpServiceTimeOutException
	 */
	public static String getConnectionStr(Context context, String path, String method) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		InputStream is = getInputStream(context, path, method);
		String responseStr = MagusTools.inputStreamToString(is);
		return responseStr;
	}

	/**
	 * 获取给定链接的数据,默认使用post方法
	 * 
	 * @param context
	 * @param path
	 * 
	 * @return 获取的输入流
	 * @throws NoNetworkActivityException
	 * @throws IOException
	 * @throws HttpServiceErrorException
	 * @throws HttpServiceTimeOutException
	 */
	public static InputStream getInputStream(Context context, String path) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		return getInputStream(context, path, HttpConst.POST);
	}

	/**
	 * 获取给定链接的数据
	 * 
	 * @param context
	 * @param path
	 * @param method
	 *            请求方法
	 * 
	 * @return 获取的输入流
	 * @throws IOException
	 * @throws HttpServiceErrorException
	 * @throws HttpServiceTimeOutException
	 */
	public static InputStream getInputStream(Context context, String path, String method) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		InputStream is = null;
		URLConnection connection = getConnection(context, path, method);
		if (connection != null) {
			is = connection.getInputStream();
			String cookieval = connection.getHeaderField("set-cookie");
			if (cookieval != null) {
				sessionId = cookieval.substring(0, cookieval.indexOf(";"));
			}
		}
		return is;
	}

	/**
	 * 得到统计参数
	 */
	public static String getUA(Context context) {
		if (UAstr != null) {
			return UAstr;
		}

		// String uid = null;
		// try {
		// SharedPreferences sp =
		// PreferenceManager.getDefaultSharedPreferences(context);
		// if (ver == null || verString == null) {
		// PackageInfo info =
		// context.getPackageManager().getPackageInfo(context.getPackageName(),
		// 0);
		// ver = info.versionName;
		// verString = ver + '_' + MagusTools.getProperty("map") + '_' +
		// MagusTools.getProperty("channel");
		// }
		//
		// uid = sp.getString("uid", null);
		// if (uid == null) {
		// uid = StaticFeild.deviceId;
		// }
		// } catch (Exception e) {
		// MagusTools.writeLog(e);
		// }
		// UAstr = "_pro=" + MagusTools.getProperty("pro") + "&_pla=" +
		// MagusTools.getProperty("product") + "_andr_" + verString + "&_ver=" +
		// ver + "&_mt=" + mt + "&_uid=" + uid + "&_ss="
		// + StaticFeild.width + "x" + StaticFeild.height;

		return UAstr;
	}

	/**
	 * 
	 * 设置统计参数
	 */
	public static void setUA(String ua) {
		UAstr = ua;
	}

	private static HttpURLConnection getConnection(Context context, String path, String method) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		if (path == null || !path.startsWith("http")) {
			throw new IOException("请求连接" + path + "为空或者不是http请求");
		}

		HttpURLConnection conn = null;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo == null) {
			throw new NoNetworkActivityException("检测到无网络活动状态!");
		}

		if (!activeNetworkInfo.isAvailable()) {
			throw new NoNetworkActivityException("检测到无网络活动状态");
		}

		String netType = activeNetworkInfo.getExtraInfo();
		int type = activeNetworkInfo.getType();
		if (type == ConnectivityManager.TYPE_MOBILE) {
			String proxy = null;
			if (netType.contains("wap")) {
				if (netType.contains("uniwap")) {
					proxy = "http://10.0.0.172:80";
				} else if (netType.contains("ctwap")) {
					proxy = "http://10.0.0.200:80";
				} else {
					proxy = "http://10.0.0.172:80";
				}
			}
			conn = getConnection(context, path, method, proxy);
			String s = conn.getHeaderField("Content-Type");
			if (s != null && s.startsWith("text/vnd.wap")) {
				conn = getConnection(context, path, method, proxy);
			}
		} else {
			conn = getConnection(context, path, method, null);
		}

		int responseCode = conn.getResponseCode();
		if (responseCode != SUCCEED_CODE) {
			if (responseCode == TIME_OUT_CODE) {
				throw new SocketTimeoutException("连接服务器超时!");
			} else {
				throw new HttpServiceErrorException(conn.getResponseMessage());
			}
		}
		return conn;
	}

	private static HttpURLConnection getConnection(Context context, String path, String method, String proxy) throws SocketTimeoutException, IOException {
		HttpURLConnection conn = null;
		String postParam = "";
		URL url = null;

		if ("POST".equalsIgnoreCase(method)) {
			if (path.indexOf("?", 10) < 0) {// 如果path地址中未追加参数，则将UA统计参数追加到path后面
				path += "?" + getUA(context);
			} else {// 若path地址中已经有参数了，则将用户追加的参数截取出来，然后将UA统计参数追加到path后面
				postParam = path.substring(path.indexOf("?") + 1, path.length());
				path = path.substring(0, path.indexOf("?") + 1) + getUA(context);
			}
		} else {
			if (path.indexOf("?", 10) < 0)
				path += "?" + getUA(context);
			else {
				path = path.substring(0, path.indexOf("?") + 1) + getUA(context) + "&" + path.substring(path.indexOf("?") + 1, path.length());
			}
		}

		// // 打印数据请求的日志
		// if ("".equals(postParam)) {
		// MagusTools.writeLog(path);
		// } else {
		// MagusTools.writeLog(path + "&" + postParam);
		// }

		if (proxy == null) {// 如果代理为null，即不需要代理
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
		} else {
			if (path.startsWith("https")) {
				url = new URL(path);
				Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.substring(7, proxy.length() - 3), 80));
				conn = (HttpURLConnection) url.openConnection(p);
			} else {
				String proxyPath = proxy + path.substring(path.indexOf('/', 11));
				MagusTools.writeLog("使用代理之后的path:\n" + proxyPath);
				url = new URL(proxyPath);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("X-Online-Host", path.substring(7, path.indexOf("/", 11)));
			}
		}
		conn.setConnectTimeout(connect_outTime);
		if (conn instanceof HttpsURLConnection) {
			TrustManager[] tm = { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

				}
			} };

			HostnameVerifier hv = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					MagusTools.writeLog("hostname:" + hostname);
					MagusTools.writeLog("SSLSession:" + session);
					return true;
				}
			};

			try {
				SSLContext sslc = SSLContext.getInstance("TLS");
				sslc.init(new KeyManager[0], tm, new SecureRandom());
				SSLSocketFactory socketFactory = sslc.getSocketFactory();
				((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);
				((HttpsURLConnection) conn).setHostnameVerifier(hv);
			} catch (Exception e) {
				MagusTools.writeLog(e);
			}
		}

		// 为相应的Http链接添加请求头参数
		initRequestHeaders(conn);

		if ("POST".equalsIgnoreCase(method)) {
			conn.setRequestMethod("POST");
			if (!"".equals(postParam)) {
				conn.setDoOutput(true);
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(postParam.getBytes());
			}
		}
		return conn;
	}

	/**
	 * 为相应的Http链接添加请求头参数
	 * 
	 * @param conn
	 * 
	 **/

	private static void initRequestHeaders(HttpURLConnection conn) {
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Connection", "Keep-Alive");

		if (propertyMap.size() > 0) {
			for (Entry<String, String> entry : propertyMap.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		if (sessionId != null) {
			conn.setRequestProperty("cookie", sessionId);
		}
	}
}