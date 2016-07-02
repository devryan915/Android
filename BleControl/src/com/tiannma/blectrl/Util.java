package com.tiannma.blectrl;

import android.content.Context;
import android.widget.Toast;

public class Util {
	public static void showMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showMessage(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	public static String bytesToString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b: data) 
			sb.append(String.format("%02X ", b));
		return sb.toString();
	}
}
