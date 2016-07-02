/**
 * 
 */
package com.broadchance.entity;

/**
 * @author ryan.wang
 * 
 */
public enum UploadFileStatus {
	// 0数据未做处理1正在上传2上传成功3上传失败
	UnDeal(0), Uploading(1), Uploaded(2), UploadFailed(3);
	private int value = 0;

	// 必须是private的，否则编译错误
	private UploadFileStatus(int value) {
		this.value = value;
	}

	public UploadFileStatus getFileStatus(int value) {
		UploadFileStatus status = null;
		switch (value) {
		case 0:
			status = UnDeal;
			break;
		case 1:
			status = Uploading;
			break;
		case 2:
			status = Uploaded;
			break;
		case 3:
			status = UploadFailed;
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
