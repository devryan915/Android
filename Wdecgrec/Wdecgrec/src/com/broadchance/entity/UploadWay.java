/**
 * 
 */
package com.broadchance.entity;

/**
 * @author ryan.wang
 * 
 */
public enum UploadWay {
	Batch(1), OneKey(2), RealTime(3);
	private int value = 0;

	// 必须是private的，否则编译错误
	private UploadWay(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
