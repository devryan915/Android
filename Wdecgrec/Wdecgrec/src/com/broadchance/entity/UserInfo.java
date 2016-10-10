package com.broadchance.entity;

public class UserInfo {
	private String userName;
	private String nickName;
	private String macAddress;
	private String certkey;
	private String orderNo;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getCertkey() {
		return certkey;
	}

	public void setCertkey(String certkey) {
		this.certkey = certkey;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
