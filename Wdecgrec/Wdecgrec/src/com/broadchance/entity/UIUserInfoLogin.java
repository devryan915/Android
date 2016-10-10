package com.broadchance.entity;

import com.broadchance.entity.serverentity.BaseResponse;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.BleDataUtil;

public class UIUserInfoLogin extends BaseResponse<String> {
	public UIUserInfoLogin() {
	}

	/**
	 * -1未定义，0已绑定，1绑定了但设备过期
	 */
	public int isOverTime = -1;
	private String macAddress;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
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

	// public String getShowName() {
	// if (NickName != null && !NickName.toString().trim().isEmpty()) {
	// return NickName;
	// } else {
	// return DataManager.getUserInfo().getLoginName();
	// }
	// }

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

	/**
	 * 对应服务端orderNo
	 * 
	 * @return
	 */
	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	private int expires_in;

	private String refresh_token;
	
	private String certkey;

	public String getCertkey() {
		return certkey;
	}

	public void setCertkey(String certkey) {
		this.certkey = certkey;
	}
	
}
