/**
 * 
 */
package com.broadchance.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.broadchance.entity.DownLoadAPPResponse;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.UploadFileResponse;
import com.broadchance.entity.UploadWay;
import com.broadchance.entity.serverentity.Const;
import com.broadchance.entity.serverentity.ServerResponse;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertType;

/**
 * @author ryan.wang
 * 
 */
public class ClientGameService {
	private static final String TAG = ClientGameService.class.getSimpleName();

	private ClientGameService() {
	}

	private static ClientGameService _Instance;

	public static ClientGameService getInstance() {
		if (_Instance == null)
			_Instance = new ClientGameService();
		return _Instance;
	}

	/**********************/
	/**
	 * 获取动态验证码
	 * 
	 * @param param
	 * @param backCall
	 */
	public void getKey(JSONObject param,
			HttpReqCallBack<ServerResponse> backCall) {
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "get_key");
		reparams.put("indata", indata);
		// reparams.put("verify", MD5Util.MD5(indata + ConstantConfig.CERTKEY));
		HttpAsyncTaskUtil.fetchData(reparams, backCall);
	}

	/**
	 * 登录
	 * 
	 * @param param
	 * @param backCall
	 */
	public void login(JSONObject param, HttpReqCallBack<ServerResponse> backCall) {
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "login");
		reparams.put("indata", indata);
		reparams.put("verify", MD5Util.MD5(indata + ConstantConfig.CERTKEY));
		HttpAsyncTaskUtil.fetchData(reparams, backCall);
	}

	public void getAlertCFG() {
		try {
			JSONObject param = new JSONObject();
			UIUserInfoLogin user = DataManager.getUserInfo();
			param.put("holtermobile", user.getLoginName());
			param.put("mobile", user.getLoginName());
			param.put("orderno", user.getAccess_token());
			param.put("device", user.getMacAddress());
			String indata = param.toString();
			Map<String, Object> reparams = new HashMap<String, Object>();
			reparams.put("action", "get_alertcfg");
			reparams.put("indata", indata);
			reparams.put("verify", MD5Util.MD5(indata + ConstantConfig.CERTKEY));
			HttpAsyncTaskUtil.fetchData(reparams,
					new HttpReqCallBack<ServerResponse>() {

						@Override
						public Activity getReqActivity() {
							return null;
						}

						private void setCFG(JSONObject cfg, AlertType type) {
							PreferencesManager.getInstance().putString(
									AlertMachine.getCFGKeyType(type),
									cfg.toString());
						}

						@Override
						public void doSuccess(ServerResponse result) {
							if (result.isOK()) {
								JSONObject data = result.getDATA();
								try {
									JSONObject cfgs = data
											.getJSONObject("alertcfg");
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.A00001
														.getValue()),
												AlertType.A00001);
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.A00002
														.getValue()),
												AlertType.A00002);
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.A00003
														.getValue()),
												AlertType.A00003);
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.A00004
														.getValue()),
												AlertType.A00004);
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.A00005
														.getValue()),
												AlertType.A00005);
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.B00001
														.getValue()),
												AlertType.B00001);
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.B00002
														.getValue()),
												AlertType.B00002);
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										setCFG(cfgs
												.getJSONObject(AlertType.B00003
														.getValue()),
												AlertType.B00003);
									} catch (Exception e) {
										e.printStackTrace();
									}
									AlertMachine alertMachine = AlertMachine
											.getInstance();
									if (alertMachine != null) {
										alertMachine.refreshCFG();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								UIUtil.showToast(result.getErrmsg());
							}
						}

						@Override
						public void doError(String result) {
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送报警信息
	 * 
	 * @param param
	 * @param backCall
	 */
	public void sendAlert(JSONObject param,
			HttpReqCallBack<ServerResponse> backCall) {
		UIUserInfoLogin user = DataManager.getUserInfo();
		try {
			param.put("mobile", user.getLoginName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "send_alert");
		reparams.put("indata", indata);
		reparams.put("verify", MD5Util.MD5(indata + ConstantConfig.CERTKEY));
		HttpAsyncTaskUtil.fetchData(reparams, backCall);
	}

	@SuppressWarnings("unchecked")
	public void uploadRealBleFile(JSONObject param,
			final HttpReqCallBack<UploadFileResponse> backCall) {
		UIUserInfoLogin user = DataManager.getUserInfo();
		try {
			param.put("mobile", user.getLoginName());
			param.put("device", user.getMacAddress());
			param.put("orderno", user.getAccess_token());
			param.put("filetype", "1");
			// ecgFile
			// breathFile
			// param.put("starttime", "");
			// param.put("endtime", "");
			// param.put("hrs", "");
			// param.put("fileinfo", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "send_data");
		reparams.put("indata", indata);
		reparams.put("verify", MD5Util.MD5(indata + ConstantConfig.CERTKEY));
		new AsyncTask<Map<String, Object>, Integer, UploadFileResponse>() {
			// HttpReqCallBack<UploadFileResponse> backCall;

			@Override
			protected UploadFileResponse doInBackground(
					Map<String, Object>... params) {
				Map<String, Object> paramsIn = params[0];
				// backCall = (HttpReqCallBack<UploadFileResponse>) paramsIn
				// .get("backCall");
				// String url = "http://dx2.9ht.com/xf/9ht.com.coc-xiaomi.apk";
				// url = ConstantConfig.SERVER_URL;
				// url = "http://192.168.1.109:56285/api/Data/AddRemote_Data";
				return HttpUtil.uploadRealBleFile(ConstantConfig.SERVER_URL,
						paramsIn);
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
	public void uploadBleFile(JSONObject param,
			final HttpReqCallBack<UploadFileResponse> backCall) {
		UIUserInfoLogin user = DataManager.getUserInfo();
		try {
			param.put("mobile", user.getLoginName());
			param.put("device", user.getMacAddress());
			param.put("orderno", user.getAccess_token());
			param.put("filetype", "2");
			// zipFile
			// param.put("starttime", "");
			// param.put("endtime", "");
			// param.put("hrs", "");
			// param.put("fileinfo", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		File zipFile = null;
		try {
			zipFile = new File(param.getString("zipFile"));
			if (!zipFile.exists()) {
				return;
			}
			param.remove("zipFile");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "send_data");
		reparams.put("indata", indata);
		reparams.put("zipFile", zipFile);

		reparams.put("verify", MD5Util.MD5(indata + ConstantConfig.CERTKEY));
		new AsyncTask<Map<String, Object>, Integer, UploadFileResponse>() {
			// HttpReqCallBack<UploadFileResponse> backCall;

			@Override
			protected UploadFileResponse doInBackground(
					Map<String, Object>... params) {
				Map<String, Object> paramsIn = params[0];
				// backCall = (HttpReqCallBack<UploadFileResponse>) paramsIn
				// .get("backCall");
				// String url = "http://dx2.9ht.com/xf/9ht.com.coc-xiaomi.apk";
				// url = ConstantConfig.SERVER_URL;
				// url = "http://192.168.1.109:56285/api/Data/AddRemote_Data";
				return HttpUtil.uploadBleFile(ConstantConfig.SERVER_URL,
						paramsIn);
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

	/***********************/

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
