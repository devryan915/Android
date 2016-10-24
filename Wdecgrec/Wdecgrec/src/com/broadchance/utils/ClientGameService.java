/**
 * 
 */
package com.broadchance.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;

import thoth.holter.ecg_010.manager.AppApplication;
import thoth.holter.ecg_010.manager.DataManager;
import thoth.holter.ecg_010.manager.PreferencesManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;

import com.broadchance.entity.DownLoadAPPResponse;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.UploadFileResponse;
import com.broadchance.entity.UserInfo;
import com.broadchance.entity.serverentity.ServerResponse;
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

	private AtomicBoolean isRrefreshCK = new AtomicBoolean(false);

	public void refreshCertKey() {
		if (isRrefreshCK.compareAndSet(false, true)) {
			final UserInfo user = DataManager.getUserInfo();
			if (DataManager.isLogin()) {
				try {
					JSONObject param;
					param = new JSONObject();
					param.put("mobile", user.getUserName());
					getKey(param, new HttpReqCallBack<ServerResponse>() {

						@Override
						public Activity getReqActivity() {
							return null;
						}

						@Override
						public void doSuccess(ServerResponse result) {
							if (result.isOK()) {
								try {
									String certKey = result.getDATA()
											.getString("certkey");
									user.setCertkey(certKey);
									DataManager.saveUser(user);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							isRrefreshCK.set(false);
						}

						@Override
						public void doError(String result) {
							isRrefreshCK.set(false);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
					isRrefreshCK.set(false);
				}

			}
		}
	}

	/**********************/
	/**
	 * 获取动态验证码
	 * 
	 * @param param
	 * @param backCall
	 */
	public void getKey(JSONObject param,
			final HttpReqCallBack<ServerResponse> backCall) {
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
	public void login(JSONObject param, String certKey,
			HttpReqCallBack<ServerResponse> backCall) {
		try {
			param.put("appname", AppApplication.PKG_NAME);
			param.put("versioncode", AppApplication.verCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "login");
		reparams.put("indata", indata);
		reparams.put("verify", MD5Util.MD5(indata + certKey));
		HttpAsyncTaskUtil.fetchData(reparams, backCall);
	}

	public void getAlertCFG() {
		try {
			JSONObject param = new JSONObject();
			UserInfo user = DataManager.getUserInfo();
			param.put("holtermobile", user.getUserName());
			param.put("mobile", user.getUserName());
			param.put("orderno", user.getOrderNo());
			param.put("device", user.getMacAddress());
			String indata = param.toString();
			Map<String, Object> reparams = new HashMap<String, Object>();
			reparams.put("action", "get_alertcfg");
			reparams.put("indata", indata);
			reparams.put("verify", MD5Util.MD5(indata + user.getCertkey()));
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
		// UIUserInfoLogin user = DataManager.getUserInfo();
		try {
			param.put("mobile", DataManager.getUserInfo().getUserName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "send_alert");
		reparams.put("indata", indata);
		reparams.put("verify",
				MD5Util.MD5(indata + DataManager.getUserInfo().getCertkey()));
		HttpAsyncTaskUtil.fetchData(reparams, backCall);
	}

	@SuppressWarnings("unchecked")
	public void uploadRealBleFile(JSONObject param,
			final HttpReqCallBack<UploadFileResponse> backCall) {
		try {
			UserInfo user = DataManager.getUserInfo();
			try {
				param.put("mobile", user.getUserName());
				param.put("device", user.getMacAddress());
				param.put("orderno", user.getOrderNo());
				param.put("filetype", "1");
				// ecgFile
				// breathFile
				// param.put("starttime", "");
				// param.put("endtime", "");
				// param.put("hrs", "");
				// param.put("fileinfo", "");
			} catch (JSONException e) {
				if (backCall != null) {
					backCall.doError(e.toString());
				}
			}
			String indata = param.toString();
			Map<String, Object> reparams = new HashMap<String, Object>();
			reparams.put("action", "send_data");
			reparams.put("indata", indata);
			reparams.put("verify", MD5Util.MD5(indata + user.getCertkey()));
			new AsyncTask<Map<String, Object>, Integer, UploadFileResponse>() {
				// HttpReqCallBack<UploadFileResponse> backCall;

				@Override
				protected UploadFileResponse doInBackground(
						Map<String, Object>... params) {
					Map<String, Object> paramsIn = params[0];
					// backCall = (HttpReqCallBack<UploadFileResponse>) paramsIn
					// .get("backCall");
					// String url =
					// "http://dx2.9ht.com/xf/9ht.com.coc-xiaomi.apk";
					// url = ConstantConfig.SERVER_URL;
					// url =
					// "http://192.168.1.109:56285/api/Data/AddRemote_Data";
					return HttpUtil.uploadRealBleFile(
							ConstantConfig.SERVER_URL, paramsIn);
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
		} catch (Exception e) {
			if (backCall != null) {
				backCall.doError(e.toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void uploadBleFile(JSONObject param,
			final HttpReqCallBack<UploadFileResponse> backCall) {
		UserInfo user = DataManager.getUserInfo();
		try {
			param.put("mobile", user.getUserName());
			param.put("device", user.getMacAddress());
			param.put("orderno", user.getOrderNo());
			param.put("filetype", "2");
			// zipFile
			// param.put("starttime", "");
			// param.put("endtime", "");
			// param.put("hrs", "");
			// param.put("fileinfo", "");
		} catch (JSONException e) {
			if (backCall != null) {
				backCall.doError(e.toString());
			}
		}
		File zipFile = null;
		try {
			zipFile = new File(param.getString("zipFile"));
			if (!zipFile.exists()) {
				if (backCall != null) {
					backCall.doError("批量文件不存在" + zipFile.getName());
				}
				return;
			}
			param.remove("zipFile");
		} catch (JSONException e) {
			if (backCall != null) {
				backCall.doError(e.toString());
			}
		}
		String indata = param.toString();
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("action", "send_data");
		reparams.put("indata", indata);
		reparams.put("zipFile", zipFile);
		reparams.put("verify", MD5Util.MD5(indata + user.getCertkey()));
		new AsyncTask<Map<String, Object>, Integer, UploadFileResponse>() {
			// HttpReqCallBack<UploadFileResponse> backCall;

			@Override
			protected UploadFileResponse doInBackground(
					Map<String, Object>... params) {
				Map<String, Object> paramsIn = params[0];
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "正在上传" + paramsIn.get("zipFile") + "\n"
							+ paramsIn.get("indata"));
					UIUtil.showRemoteToast("正在上传" + paramsIn.get("zipFile"));
				}
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
