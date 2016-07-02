package com.broadchance.entity.serverentity;

public class UIUserInfoRegist extends Object {
	public UIUserInfoRegist() {
	}

	private String UserID;

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String _UserID) {
		this.UserID = _UserID;
	}

	private String LoginName;

	public String getLoginName() {
		return LoginName;
	}

	public void setLoginName(String _LoginName) {
		this.LoginName = _LoginName;
	}

	private String NickName;

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String _NickName) {
		this.NickName = _NickName;
	}

	private String MobileNum;

	public String getMobileNum() {
		return MobileNum;
	}

	public void setMobileNum(String _MobileNum) {
		this.MobileNum = _MobileNum;
	}

	private int VTimes;

	public int getVTimes() {
		return VTimes;
	}

	public void setVTimes(int _VTimes) {
		this.VTimes = _VTimes;
	}

	private String access_token;

	public String getaccess_token() {
		return access_token;
	}

	public void setaccess_token(String _access_token) {
		this.access_token = _access_token;
	}

	private int expires_in;

	public int getexpires_in() {
		return expires_in;
	}

	public void setexpires_in(int _expires_in) {
		this.expires_in = _expires_in;
	}

	private String refresh_token;

	public String getrefresh_token() {
		return refresh_token;
	}

	public void setrefresh_token(String _refresh_token) {
		this.refresh_token = _refresh_token;
	}

	private Boolean IsFull;

	public Boolean getIsFull() {
		return IsFull;
	}

	public void setIsFull(Boolean _IsFull) {
		this.IsFull = _IsFull;
	}
}
