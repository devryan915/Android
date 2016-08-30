/** 
 * @Title: SSXLX.java
 * @Package: com.xx.xx.util
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:shanghai
 *
 * @author Comsys-ryan.wang
 * @version V1.0
 */
package com.broadchance.utils;

import java.util.HashMap;
import java.util.Map;

import com.broadchance.entity.serverentity.CurVerResponse;
import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.entity.serverentity.UIDeviceResponse;
import com.broadchance.entity.serverentity.UIDeviceResponseList;
import com.broadchance.entity.serverentity.UIFamilyResponse;
import com.broadchance.entity.serverentity.UIFamilyResponseList;
import com.broadchance.entity.serverentity.UIPixelResponse;
import com.broadchance.entity.serverentity.UIUserInfoBaseResponse;
import com.broadchance.entity.serverentity.UIUserInfoRegistResponse;
import com.broadchance.wdecgrec.HttpReqCallBack;

public class SSXLXService {
	private SSXLXService() {
	}

	private static SSXLXService _Instance;

	public static SSXLXService getInstance() {
		if (_Instance == null)
			_Instance = new SSXLXService();
		return _Instance;
	}

	/**
	 * 
	 * @param userID
	 * @param iDCode
	 * @param backCall
	 */
	public void GetFreeDeviceVerify(String userID, String iDCode,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("iDCode", iDCode);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Device/GetFreeDeviceVerify", reparams, backCall);
	}

	public void FreeDevice(String userID, String iDCode, String verifyCode,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("iDCode", iDCode);
		reparams.put("verifyCode", verifyCode);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Device/FreeDevice", reparams, backCall);
	}

	public void GetUserDevice(String userID,
			HttpReqCallBack<UIDeviceResponseList> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Device/GetUserDevice", reparams, backCall);
	}

	public void AddRemote_Data(String userID, int UploadWay, String zipData,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("UploadWay", UploadWay);
		reparams.put("zipData", zipData);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Data/AddRemote_Data", reparams, backCall);
	}

	public void GetDeviceInfo(String deviceID,
			HttpReqCallBack<UIDeviceResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("deviceID", deviceID);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Device/GetDeviceInfo", reparams, backCall);
	}

	public void CheckUserDevice(String userID, String iDCode,
			HttpReqCallBack<UIDeviceResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("iDCode", iDCode);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Device/CheckUserDevice", reparams, backCall);
	}

	public void ChangeDeviceUserID(String userID, String iDCode,
			String password, String oldpassword,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("iDCode", iDCode);
		reparams.put("password", password);
		reparams.put("oldpassword", oldpassword);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Device/ChangeDeviceUserID", reparams, backCall);
	}

	public void GetStaticPixel(String brand, String model,
			HttpReqCallBack<UIPixelResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("brand", brand);
		reparams.put("model", model);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Static/GetStaticPixel", reparams, backCall);
	}

	public void GetPreSetMd5(HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Static/GetPreSetMd5", reparams, backCall);
	}

	public void GetPreSetUrls(HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Static/GetPreSetUrls", reparams, backCall);
	}

	public void GetCurVer(int platform, HttpReqCallBack<CurVerResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("platform", platform);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/Static/GetCurVer", reparams, backCall);
	}

	public void Regist(String loginName, String password, String validCode,
			String IDCard, HttpReqCallBack<UIUserInfoRegistResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("loginName", loginName);
		reparams.put("password", password);
		reparams.put("validCode", validCode);
		reparams.put("IDCard", IDCard);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL + "/api/User/Regist",
				reparams, backCall);
	}

	public void ChangePwd(String userID, String password, String passwordOld,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("password", password);
		reparams.put("passwordOld", passwordOld);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/ChangePwd", reparams, backCall);
	}

	public void UpdateUserBase(String userID, String iDCard, float height,
			float weight, String zipCode, String realName, String remark,
			String fullAddr, String moblieNum,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("iDCard", iDCard);
		reparams.put("height", height);
		reparams.put("weight", weight);
		reparams.put("zipCode", zipCode);
		reparams.put("realName", realName);
		reparams.put("remark", remark);
		reparams.put("fullAddr", fullAddr);
		reparams.put("moblieNum", moblieNum);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/UpdateUserBase", reparams, backCall);
	}

	public void AddUserFamily(String userID, String loginName, String fName,
			HttpReqCallBack<UIFamilyResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("loginName", loginName);
		reparams.put("fName", fName);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/AddUserFamily", reparams, backCall);
	}

	public void DeleteUserFamily(String userID, long iD,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("iD", iD);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/DeleteUserFamily", reparams, backCall);
	}

	public void ForgetPwd(String longName, String vaildCode, String password,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("longName", longName);
		reparams.put("vaildCode", vaildCode);
		reparams.put("password", password);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/ForgetPwd", reparams, backCall);
	}

	public void GetMyFamily(String userID,
			HttpReqCallBack<UIFamilyResponseList> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/GetMyFamily", reparams, backCall);
	}

	public void GetMyWearer(String userID,
			HttpReqCallBack<UIFamilyResponseList> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/GetMyWearer", reparams, backCall);
	}

	public void GetRegisterVerifyCode(String loginName,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("loginName", loginName);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/GetRegisterVerifyCode", reparams, backCall);
	}

	public void GetForgetVerifyCode(String moblieNo,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("moblieNo", moblieNo);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/GetForgetVerifyCode", reparams, backCall);
	}

	public void InserGPS(String userID, String gPSLo, String gPSLa,
			HttpReqCallBack<StringResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		reparams.put("gPSLo", gPSLo);
		reparams.put("gPSLa", gPSLa);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/InserGPS", reparams, backCall);
	}

	public void GetUserBase(String userID,
			HttpReqCallBack<UIUserInfoBaseResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("userID", userID);
		HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL
				+ "/api/User/GetUserBase", reparams, backCall);
	}
}
