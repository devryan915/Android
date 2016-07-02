package com.broadchance.entity.serverentity;

public class ResponseCode {
	/**
	 * 服务异常
	 */
	public final static String Exception = "-999";
	/**
	 * 操作成功
	 */
	public final static String Ok = "0";
	/**
	 * 操作无效
	 */
	public final static String InvalidParam = "3";
	/**
	 * 设备不存在
	 */
	public final static String DeviceNot = "1000";
	/**
	 * 密码不正确
	 */
	public final static String PwdNo = "1001";
	/**
	 * 设备已经被使用
	 */
	public final static String DeviceIsUse = "1002";
	/**
	 * 设备的使用人不正确
	 */
	public final static String DeviceUserIDErr = "1003";
	/**
	 * 设备未启用
	 */
	public final static String DeviceNotEnable = "1004";
	/**
	 * 设备已关闭
	 */
	public final static String DeviceClose = "1005";
	/**
	 * 更换设备
	 */
	public final static String DeviceChange = "1006";
	/**
	 * 登录人员不存在
	 */
	public final static String LoginNameNot = "2000";
	/**
	 * 用户名或密码不正确
	 */
	public final static String LoginNamePwdErr = "2001";
	/**
	 * 登录手机号已经存在
	 */
	public final static String LoginNameExist = "2002";
	/**
	 * 超出最大家属限制
	 */
	public final static String OverFamilyMax = "2003";
	/**
	 * 注册超出最大短信次数
	 */
	public final static String OverRegistMax = "2004";
	/**
	 * 身份证信息错误
	 */
	public final static String IDCardErr = "2005";
	/**
	 * 原密码不正确
	 */
	public final static String OldPasswordErr = "2006";
	/**
	 * 超出最大短信次数
	 */
	public final static String OverSMSMax = "2007";
	/**
	 * 验证码失效，或验证码不正确
	 */
	public final static String VerifyCodeErr = "3000";
	/**
	 * 手机型号不存在
	 */
	public final static String MoblieModelErr = "9001";
}
