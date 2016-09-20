package com.langlang.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class HttpUtils {

	public static String getData(String urlPath) {
		String result = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlPath);
			HttpResponse httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity(), null);
				Log.i("HttpTools---------", result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public static boolean isWifiConnected(Context context) {
		return getNetworkState(context, ConnectivityManager.TYPE_WIFI) == State.CONNECTED;

	}

	public static boolean isMobileConnected(Context context) {

		return getNetworkState(context, ConnectivityManager.TYPE_MOBILE) == State.CONNECTED;

	}

	private static State getNetworkState(Context context, int networkType) {

		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING) {

		}

		else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
		}

		NetworkInfo info = connec.getNetworkInfo(networkType);

		return info == null ? null : info.getState();

	}

	// 判断网络是否连接
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				int i_info = info.length;
				for (int i = 0; i < i_info; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
