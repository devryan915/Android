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
	private static final String TAG = FilterUtil.class.getSimpleName();

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

	/**
	 * 获取第一通道滤波后心电数据
	 * 
	 * @param inputData
	 *            原始心电数据
	 * @return
	 */
	public native int[] getECGDataII(int[] inputData);

	/**
	 * 获取第二通道滤波后心电数据
	 * 
	 * @param inputData
	 *            原始心电数据
	 * @return
	 */
	public native int[] getECGDataV1(int[] inputData);

	/**
	 * 获取第三通道滤波后心电数据
	 * 
	 * @param inputData
	 *            原始心电数据
	 * @return
	 */
	public native int[] getECGDataV5(int[] inputData);

	/**
	 * 获取心率
	 * 
	 * @return
	 */
	public native int getHeartRate();

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
