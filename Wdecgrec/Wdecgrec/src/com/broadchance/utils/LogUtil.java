package com.broadchance.utils;

import android.util.Log;
import android.widget.Toast;

public class LogUtil {
	private final static int I = 1;
	private final static int D = 2;
	private final static int W = 3;
	private final static int E = 4;
	private final static int C = I;

	public static void d(String tag, Object obj) {
		if (D >= C && ConstantConfig.Debug) {
			Log.d(tag, obj == null ? "" : obj.toString());
		}
	}

	public static void i(String tag, Object obj) {
		if (I >= C && ConstantConfig.Debug) {
			Log.i(tag, obj == null ? "" : obj.toString());
		}
	}

	public static void w(String tag, Object obj) {
		if (W >= C && ConstantConfig.Debug) {
			Log.w(tag, obj == null ? "" : obj.toString());
		}
	}

	public static void e(String tag, Exception e) {
		if (E >= C && ConstantConfig.Debug) {
			Log.e(tag, e == null ? "" : e.toString());
			if (ConstantConfig.Debug) {
				e.printStackTrace();
			}
		}
	}

	public static void e(String tag, String error) {
		if (E >= C && ConstantConfig.Debug) {
			Log.e(tag, error);
		}
	}

	public static void e(String tag, Object obj, Exception e) {
		if (E >= C && ConstantConfig.Debug) {
			Log.e(tag, obj == null ? "" : obj.toString(), e);
		}
	}
}
