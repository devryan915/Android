/**
 * 
 */
package com.broadchance.entity;

/**
 * @author ryan.wang
 * 
 */
public enum FileType {
	// 0数据未做处理1补传文件
	Default(0),
	/**
	 * 补传
	 */
	Supplement(1);
	private int value = 0;

	// 必须是private的，否则编译错误
	private FileType(int value) {
		this.value = value;
	}

	public FileType getFileStatus(int value) {
		FileType status = null;
		switch (value) {
		case 0:
			status = Default;
			break;
		case 1:
			status = Supplement;
			break;
		default:
			break;
		}
		return status;
	}

	public int getValue() {
		return this.value;
	}
}
