package com.broadchance.entity.serverentity;

import java.util.Date;

public class UserInfoRegist {
	private String _UserID;
	private String _LoginName;
	private String _Password;
	private int _RoleNo;
	private String _NickName;
	private String _MobileNum;
	private int _VTimes;
	public String get_UserID() {
		return _UserID;
	}
	public void set_UserID(String _UserID) {
		this._UserID = _UserID;
	}
	public String get_LoginName() {
		return _LoginName;
	}
	public void set_LoginName(String _LoginName) {
		this._LoginName = _LoginName;
	}
	public String get_Password() {
		return _Password;
	}
	public void set_Password(String _Password) {
		this._Password = _Password;
	}
	public int get_RoleNo() {
		return _RoleNo;
	}
	public void set_RoleNo(int _RoleNo) {
		this._RoleNo = _RoleNo;
	}
	public String get_NickName() {
		return _NickName;
	}
	public void set_NickName(String _NickName) {
		this._NickName = _NickName;
	}
	public String get_MobileNum() {
		return _MobileNum;
	}
	public void set_MobileNum(String _MobileNum) {
		this._MobileNum = _MobileNum;
	}
	public int get_VTimes() {
		return _VTimes;
	}
	public void set_VTimes(int _VTimes) {
		this._VTimes = _VTimes;
	}
	public boolean is_IsValid() {
		return _IsValid;
	}
	public void set_IsValid(boolean _IsValid) {
		this._IsValid = _IsValid;
	}
	public Date get_ValidDT() {
		return _ValidDT;
	}
	public void set_ValidDT(Date _ValidDT) {
		this._ValidDT = _ValidDT;
	}
	public Date get_CreateDT() {
		return _CreateDT;
	}
	public void set_CreateDT(Date _CreateDT) {
		this._CreateDT = _CreateDT;
	}
	public String get_Email() {
		return _Email;
	}
	public void set_Email(String _Email) {
		this._Email = _Email;
	}
	private boolean _IsValid;
	private Date _ValidDT;
	private Date _CreateDT;
	private String _Email;

}
