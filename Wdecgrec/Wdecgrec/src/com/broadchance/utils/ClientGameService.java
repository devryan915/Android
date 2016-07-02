/**
 * 
 */
package com.broadchance.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.broadchance.entity.DownLoadAPPResponse;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.UploadFileResponse;
import com.broadchance.entity.UploadWay;
import com.broadchance.entity.serverentity.Const;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.wdecgrec.HttpReqCallBack;

/**
 * @author ryan.wang
 * 
 */
public class ClientGameService {
	private static final String TAG = ClientGameService.class.getSimpleName();

	public void loginServer(String loginName, String password,
			HttpReqCallBack<UIUserInfoLogin> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("UserName", loginName);
		reparams.put("Password", password);
		reparams.put("grant_type", "password");
		String url;
		// url = "http://192.168.1.109:56285/api/User/Login";
		url = ConstantConfig.SERVER_URL + "/api/User/Login";
		HttpAsyncTask.fetchData(url, reparams, backCall);
	}

	@SuppressWarnings("unchecked")
	public void uploadRealTimeFile(File uploadFile,
			HttpReqCallBack<UploadFileResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("uploadFile", uploadFile);
		reparams.put("backCall", backCall);
		new AsyncTask<Map<String, Object>, Integer, UploadFileResponse>() {
			HttpReqCallBack<UploadFileResponse> backCall;

			@Override
			protected UploadFileResponse doInBackground(
					Map<String, Object>... params) {
				Map<String, Object> paramsIn = params[0];
				backCall = (HttpReqCallBack<UploadFileResponse>) paramsIn
						.get("backCall");
				String url = ConstantConfig.SERVER_REALTIME_URL;
				int port = ConstantConfig.SERVER_REALTIME_PORT;
				// url = ConstantConfig.NEWAPP_DOWNLOADURL;
				return HttpUtil.uploadRealTimeFile(url, port, paramsIn);
			}

			@Override
			protected void onPostExecute(UploadFileResponse result) {
				if (result.isOk()) {
					backCall.doSuccess(result);
				} else {
					backCall.doError(result.getData());
				}
			}

			@Override
			protected void onProgressUpdate(Integer... values) {

			}

		}.execute(reparams);
	}

	@SuppressWarnings("unchecked")
	public void uploadFile(File uploadFile, String desDataJson,
			UploadWay uploadWay, HttpReqCallBack<UploadFileResponse> backCall) {
		UIUserInfoLogin user = DataManager.getUserInfo();
		// if (user == null) {
		// // LogUtil.d(TAG, "用户数据不存在");
		// if (backCall != null) {
		// backCall.doError("用户数据不存在");
		// }
		// return;
		// }
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("uploadFile", uploadFile);
		reparams.put("backCall", backCall);
		reparams.put("desDataJson", desDataJson);
		reparams.put("userID", user.getUserID());
		reparams.put("upLoadWay", uploadWay.getValue());

		new AsyncTask<Map<String, Object>, Integer, UploadFileResponse>() {
			HttpReqCallBack<UploadFileResponse> backCall;

			@Override
			protected UploadFileResponse doInBackground(
					Map<String, Object>... params) {
				Map<String, Object> paramsIn = params[0];
				backCall = (HttpReqCallBack<UploadFileResponse>) paramsIn
						.get("backCall");
				String url = "http://dx2.9ht.com/xf/9ht.com.coc-xiaomi.apk";
				url = ConstantConfig.SERVER_URL + "/api/Data/AddRemote_Data";
				// url = "http://192.168.1.109:56285/api/Data/AddRemote_Data";
				return HttpUtil.uploadFile(url, paramsIn);
			}

			@Override
			protected void onPostExecute(UploadFileResponse result) {
				if (result.isOk()) {
					backCall.doSuccess(result);
				} else {
					backCall.doError(result.getData());
				}
			}

			@Override
			protected void onProgressUpdate(Integer... values) {

			}

		}.execute(reparams);
	}

	@SuppressWarnings("unchecked")
	public void downLoadApp(String url, File downFile, Handler handler,
			HttpReqCallBack<DownLoadAPPResponse> backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("url", url);
		reparams.put("downFile", downFile);
		reparams.put("handler", handler);
		reparams.put("backCall", backCall);
		// HttpAsyncTask.fetchData(ConstantConfig.SERVER_URL +
		// "/api/User/Login",
		// reparams, backCall);
		new AsyncTask<Map<String, Object>, Integer, DownLoadAPPResponse>() {
			HttpReqCallBack<DownLoadAPPResponse> backCall;

			@Override
			protected DownLoadAPPResponse doInBackground(
					Map<String, Object>... params) {
				Map<String, Object> paramsIn = params[0];
				backCall = (HttpReqCallBack<DownLoadAPPResponse>) paramsIn
						.get("backCall");
				String url = (String) paramsIn.get("url");
				return HttpUtil.downloadFile(url, paramsIn);
			}

			@Override
			protected void onPostExecute(DownLoadAPPResponse result) {
				if (result.isOk()) {
					backCall.doSuccess(result);
				} else {
					backCall.doError(result.getData());
				}
			}

			@Override
			protected void onProgressUpdate(Integer... values) {

			}

		}.execute(reparams);
	}
}
