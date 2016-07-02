/**
 * 
 */
package com.broadchance.wdecgrec.test;

/**
 * @author ryan.wang
 * 
 */
public class TestJNI {
	static {
		System.loadLibrary("testjni");
	}
	public String str = null;
	public int intValue = 0;

	public native String stringFromJNI();

	public static native String stringFromJNIStatic();

	/**
	 * 本地方法对应c中的Java_com_broadchance_utils_FilterUtil_testObjParam
	 * 
	 * @return
	 */
	public native TestJNI testObjParam();

	/**
	 * 测试本地方法对象内存和C是同一块
	 * 
	 * @param value
	 */
	public native void testSetValue(int value);

	/**
	 * 获取传入的值
	 * 
	 * @return
	 */
	public native int testGetValue();

	/**
	 * 测试数组
	 * 
	 * @param inputData
	 * @return
	 */
	public native int[] testIntArray(int[] inputData);
}
