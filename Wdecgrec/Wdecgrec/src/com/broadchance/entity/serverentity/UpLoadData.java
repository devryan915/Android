package com.broadchance.entity.serverentity;

public class UpLoadData extends Object {
	public UpLoadData() {
	}

	private String FileName;

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	private String UserID;

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String _UserID) {
		this.UserID = _UserID;
	}

	private String DeviceID;

	public String getDeviceID() {
		return DeviceID;
	}

	public void setDeviceID(String _DeviceID) {
		this.DeviceID = _DeviceID;
	}

	private String BeginDT;

	public String getBeginDT() {
		return BeginDT;
	}

	public void setBeginDT(String _BeginDT) {
		this.BeginDT = _BeginDT;
	}

	private String EndDT;

	public String getEndDT() {
		return EndDT;
	}

	public void setEndDT(String _EndDT) {
		this.EndDT = _EndDT;
	}

}
