package com.broadchance.entity.serverentity;

import java.util.Date;

public class UIDevice extends Object {
	public UIDevice() {
	}

	private String DeviceID;

	public String getDeviceID() {
		return DeviceID;
	}

	public void setDeviceID(String _DeviceID) {
		this.DeviceID = _DeviceID;
	}

	private Date ExpireDate;

	public Date getExpireDate() {
		return ExpireDate;
	}

	public void setExpireDate(Date _ExpireDate) {
		this.ExpireDate = _ExpireDate;
	}

	private String UUID;

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String _UUID) {
		this.UUID = _UUID;
	}

	private String MAC;

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String _MAC) {
		this.MAC = _MAC;
	}

	private Date MadeDate;

	public Date getMadeDate() {
		return MadeDate;
	}

	public void setMadeDate(Date _MadeDate) {
		this.MadeDate = _MadeDate;
	}

	private int ExpireYear;

	public int getExpireYear() {
		return ExpireYear;
	}

	public void setExpireYear(int _ExpireYear) {
		this.ExpireYear = _ExpireYear;
	}
}
