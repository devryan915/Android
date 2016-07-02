/**
 * 
 */
package com.broadchance.entity;

/**
 * @author ryan.wang
 * 
 */
public enum UserStatus {
	None(0), Login(1);
	private int value = 0;

	// 必须是private的，否则编译错误
	private UserStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
