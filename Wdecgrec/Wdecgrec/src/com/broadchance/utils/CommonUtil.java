package com.broadchance.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.graphics.Color;

public class CommonUtil {
	private final static String TAG = CommonUtil.class.getSimpleName();

	public static Date getDate() {
		return Calendar.getInstance(Locale.getDefault()).getTime();
	}

	private final static String formatA = "yyyy-MM-dd";
	private final static String formatB = "yyyyMMdd HH:mm:ss.SSS";
	private final static String formatC = "yyyyMMddHHmmssSSSZ";
	private final static String formatD = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 * 
	 * @return
	 */
	public static String getTime_D() {
		return getTime(formatD);
	}

	public static Date parseDate_C(String dateStr) throws ParseException {
		return new SimpleDateFormat(formatC, Locale.getDefault())
				.parse(dateStr);
	}

	/**
	 * yyyyMMddHHmmssSSSZ
	 * 
	 * @param date
	 * @return
	 */
	public static String getTime_C(Date date) {
		return getTime(formatC, date);
	}

	/**
	 * yyyyMMddHHmmssSSSZ
	 * 
	 * @return
	 */
	public static String getTime_C() {
		return getTime(formatC);
	}

	public static Date parseDate_B(String dateStr) throws ParseException {
		return new SimpleDateFormat(formatB, Locale.getDefault())
				.parse(dateStr);
	}

	public static String getTime_B(Date date) {
		return getTime(formatB, date);
	}

	/**
	 * yyyyMMdd HH:mm:ss.SSS
	 * 
	 * @return
	 */
	public static String getTime_B() {
		return getTime(formatB);
	}

	/**
	 * yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getTime_A() {
		return getTime(formatA);
	}

	/**
	 * 
	 * @param format
	 * @return
	 */
	private static String getTime(String format) {
		return getTime(format, getDate());
	}

	private static String getTime(String format, Date date) {
		return new SimpleDateFormat(format, Locale.getDefault()).format(date);
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
