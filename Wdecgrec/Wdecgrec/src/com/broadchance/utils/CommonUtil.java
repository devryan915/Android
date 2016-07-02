package com.broadchance.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.graphics.Color;

public class CommonUtil {
	private final static String TAG = CommonUtil.class.getSimpleName();

	public static String getCurTime() {
		return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(Calendar.getInstance(Locale.getDefault()).getTime());
	}

	public static String getCurTime(String format) {
		return new SimpleDateFormat(format, Locale.getDefault())
				.format(Calendar.getInstance(Locale.getDefault()).getTime());
	}

	/**
	 * 将32位颜色值转为单通道色值
	 * 
	 * @return float[] [0]=alpha [1]=red [2]=green [3]=blue
	 */
	public static float[] colorToRGB(int color) {
		int a = (color & 0xff000000) >>> 24;
		int red = (color & 0x00ff0000) >>> 16;
		int green = (color & 0x0000ff00) >>> 8;
		int blue = (color & 0x000000ff);
		float[] ret = new float[4];
		ret[0] = a / 255f;
		ret[1] = red / 255f;
		ret[2] = green / 255f;
		ret[3] = blue / 255f;
		return ret;
	}

	/**
	 * 将int转为byte，int不大于255且为自然数，否则截断，取最后一个字节值
	 * 
	 * @param value
	 * @return
	 */
	// public static byte int2Byte(int value) {
	// byte b = (byte) (value & 0xFF);
	// return b;
	// }

	/**
	 * 将int转为byte[]，下标从低到高代表int高位到低位
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] int2ByteArray(int value) {
		return new byte[] { (byte) ((value >> 24) & 0xFF),
				(byte) ((value >> 16) & 0xFF), (byte) ((value >> 8) & 0xFF),
				(byte) (value & 0xFF) };
	}

	/**
	 * 将字节转为int
	 * 
	 * @param byteArray
	 *            下标0到高代表int高位到低位
	 * @return
	 * @throws Exception
	 */
	public static int bytes2Int(byte[] byteArray) throws Exception {
		// 非空判断
		if (byteArray == null)
			throw new Exception("入参不能为空");
		if (byteArray.length != 4)
			throw new Exception("入参长度必须为4字节");
		// 默认填充0
		byte[] b = new byte[] { 0, 0, 0, 0 };
		// 按照入参byteArray从高位到低位填充到b[]中
		int length = 4;
		for (int i = 0; i < length; i++) {
			b[i] = byteArray[i];
		}
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
				| (b[0] & 0xFF) << 24;
	}

	/**
	 * 大端字节转为小端字节
	 * 
	 * @param arr
	 * @return
	 */
	public static FloatBuffer floatBufferUtil(float[] arr) {
		FloatBuffer mBuffer;
		// 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
		ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
		// 数组排列用nativeOrder
		qbb.order(ByteOrder.nativeOrder());
		mBuffer = qbb.asFloatBuffer();
		mBuffer.put(arr);
		mBuffer.position(0);
		return mBuffer;
	}

}
