package com.broadchance.wdecgrec.alert;

/**
 * 预警类型
 * 
 * @author ryan.wang
 * 
 */
public enum AlertType {

	/**
	 * 传感器脱落
	 */
	A00001("A00001"),
	/**
	 * BLE信号断联
	 */
	A00002("A00002"),
	/**
	 * 无网络
	 */
	A00003("A00003"),
	/**
	 * 手机电量低
	 */
	A00004("A00004"),
	/**
	 * 设备电量低
	 */
	A00005("A00005"),
	/**
	 * 心动过速
	 */
	B00001("B00001"),
	/**
	 * 心动过缓
	 */
	B00002("B00002"),
	/**
	 * 心电直线
	 */
	B00003("B00003");

	private String value = "";

	// 必须是private的，否则编译错误
	private AlertType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}