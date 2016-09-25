/**
 * 
 */
package com.broadchance.entity;

import java.util.Date;

/**
 * @author ryan.wang
 * 
 */
public class FileFrameData {
	/**
	 * 第一通道一个点数据
	 */
	public Short ch1;
	/**
	 * 第二通道
	 */
	public Short ch2;
	/**
	 * 第三通道
	 */
	public Short ch3;
	/**
	 * 蓝牙收到的时间
	 */
	public Long date;
	public static int pointsLength = 1;
	public static int pointBytesLength = pointsLength * 2;
}
