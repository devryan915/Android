/**
 * 
 */
package com.broadchance.utils;

import android.util.Log;

/**
 * @author ryan.wang
 * 
 */
public class FilterUtil {
	private FilterUtil() {
	}

	private static final String TAG = FilterUtil.class.getSimpleName();
	public static FilterUtil Instance = new FilterUtil();
	// private boolean isUIUsed;
	//
	// /**
	// * 是否由画图使用
	// *
	// * @return
	// */
	// public boolean isUIUsed() {
	// return isUIUsed;
	// }
	//
	// public void setUIUsed(boolean isUIUsed) {
	// this.isUIUsed = isUIUsed;
	// }

	static {
		System.loadLibrary("ecgfilter");
	}

	/**
	 * 初始化过滤器 初始化三个通道
	 */
	// public native void initFilter();

	/**
	 * 重置三通道
	 */
	public native void resetFilter();

	public native void resetFilterR();

	/**
	 * 获取第一通道滤波后心电数据
	 * 
	 * @param inputData
	 *            原始心电数据
	 * @return
	 */
	public native int[] getECGDataII(int[] inputData);

	public native int[] getECGDataIIR(int[] inputData);

	/**
	 * 获取第二通道滤波后心电数据
	 * 
	 * @param inputData
	 *            原始心电数据
	 * @return
	 */
	public native int[] getECGDataV1(int[] inputData);

	public native int[] getECGDataV1R(int[] inputData);

	/**
	 * 获取第三通道滤波后心电数据
	 * 
	 * @param inputData
	 *            原始心电数据
	 * @return
	 */
	public native int[] getECGDataV5(int[] inputData);

	public native int[] getECGDataV5R(int[] inputData);

	/**
	 * 获取心率
	 * 
	 * @return
	 */
	public native int getHeartRate();

	public native int getHeartRateR();

	/**
	 * 关闭滤波释放内存
	 */
	// public native void closeFilter();
	public void logJNI(String log) {
		if (ConstantConfig.Debug) {
			LogUtil.d(TAG, log);
		}
	}
}
