package com.broadchance.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.broadchance.manager.AppApplication;

public class NetUtil {

	/**
	 * 判断网络是否连接
	 * 
	 * @return
	 */
	public static boolean isConnectNet() {
		NetworkInfo networkInfo = ((ConnectivityManager) AppApplication.Instance
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	/**
	 * 是否有可用网络
	 * 
	 * @return
	 */
	public static boolean isNetAvailable() {
		NetworkInfo networkInfo = ((ConnectivityManager) AppApplication.Instance
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isAvailable();
	}

	/**
	 * 是否是wifi网络
	 * 
	 * @return
	 */
	public static boolean isWifi() {
		NetworkInfo networkInfo = ((ConnectivityManager) AppApplication.Instance
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是手机网络
	 * 
	 * @return
	 */
	public static boolean isMobileNet() {
		NetworkInfo networkInfo = ((ConnectivityManager) AppApplication.Instance
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				return true;
			}
		}
		return false;
	}

}
