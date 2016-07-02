package com.example.testskin;

import android.util.Log;
import android.widget.Toast;

public class LogUtil {

	public static void d(String tag, Object obj) {
		Log.d(tag, obj == null ? "" : obj.toString());
	}

	public static void i(String tag, Object obj) {
		Log.i(tag, obj == null ? "" : obj.toString());
	}

	public static void w(String tag, Object obj) {
		Log.w(tag, obj == null ? "" : obj.toString());
	}

	public static void e(String tag, Exception e) {
		Log.e(tag, e == null ? "" : e.toString());
		if (ConstantConfig.Debug) {
			e.printStackTrace();
		}
	}

	public static void e(String tag, String error) {
		Log.e(tag, error);
	}

	public static void e(String tag, Object obj, Exception e) {
		Log.e(tag, obj == null ? "" : obj.toString(), e);
	}
}
