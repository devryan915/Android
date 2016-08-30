package com.broadchance.wdecgrec.alert;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.serverentity.ServerResponse;
import com.broadchance.manager.AppApplication;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.login.LoginActivity;

/**
 * 预警波管理器
 * 
 * @author ryan.wang
 * 
 */
public class AlertMachine {

	protected static final String TAG = AlertMachine.class.getSimpleName();
	private ClientGameService clientService = ClientGameService.getInstance();

	private AlertMachine() {
	}

	public static AlertMachine _Instance;

	public static AlertMachine getInstance() {
		if (_Instance == null) {
			_Instance = new AlertMachine();
		}
		return _Instance;
	}

	/**
	 * 传感器电极脱落
	 */
	private Timer timerA00001;
	private Timer timerA00001X;
	/**
	 * BLE信号断联
	 */
	private Timer timerA00002;
	private Timer timerA00002X;
	/**
	 * 网络无信号
	 */
	private Timer timerA00003;
	private Timer timerA00003X;
	/**
	 * 记录上一次网络断开预警
	 */
	private JSONObject lastA00003Obj;
	/**
	 * 网关电量低
	 */
	private Timer timerA00004;
	private Timer timerA00004X;
	/**
	 * 传感器电量低
	 */
	private Timer timerA00005;
	private Timer timerA00005X;
	/**
	 * 心动过速
	 */
	private Timer timerA00006;
	private Timer timerA00006X;
	/**
	 * 心动过缓
	 */
	private Timer timerA00007;
	private Timer timerA00007X;
	/**
	 * 停搏
	 */
	private Timer timerA00008;
	private Timer timerA00008X;

	private JSONObject getAlertParam(JSONObject... value) {
		JSONObject param = new JSONObject();
		try {
			UIUserInfoLogin user = DataManager.getUserInfo();
			param.put("device", user.getMacAddress());
			param.put("orderno", ConstantConfig.ORDERNO);
			param.put("time", CommonUtil.getTime_B());
			JSONArray alertArray = new JSONArray();
			for (int i = 0; i < value.length; i++) {
				alertArray.put(value[i]);
			}
			param.put("alerteventarray", alertArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return param;
	}

	private JSONObject getCancelAlert(AlertType type) {
		JSONObject alertObj = new JSONObject();
		try {
			alertObj.put("id", type.getValue());
			alertObj.put("state", 0);
			alertObj.put("time", CommonUtil.getTime_B());
			JSONObject value = new JSONObject();
			alertObj.put("value", value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return alertObj;
	}

	interface BackCall {
		void execute();
	}

	private void sendAlert(JSONObject param, final BackCall call) {
		clientService.sendAlert(param, new HttpReqCallBack<ServerResponse>() {

			@Override
			public Activity getReqActivity() {
				return null;
			}

			@Override
			public void doSuccess(ServerResponse result) {
				if (!result.isOK())
					return;
				if (call != null)
					call.execute();
			}

			@Override
			public void doError(String result) {

			}
		});
	}

	/**
	 * 发送预警
	 * 
	 * @param type
	 * @param value
	 */
	public void sendAlert(AlertType type, final JSONObject value) {
		try {
			TimerTask task;
			switch (type) {
			case A00001:
				// 如果发生脱落则取消停博报警
				cancelAlert(AlertType.A00008);
				if (timerA00001 != null) {
					return;
				}
				if (timerA00001X != null) {
					timerA00001X.cancel();
					timerA00001X = null;
				}
				timerA00001 = new Timer();
				task = new TimerTask() {
					@Override
					public void run() {
						// 取消预警
						timerA00001 = null;
						JSONObject param = getAlertParam(value);
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								LogUtil.d(TAG, "发送报警timerA00001");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00001X = new Timer();
								timerA00001X.schedule(
										new TimerTask() {
											@Override
											public void run() {
												if (timerA00001 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00001));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00001X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00001_Delay_Clear * 1000);
							}
						});

					}
				};
				timerA00001.schedule(task,
						ConstantConfig.AlertA00001_Delay_Raise * 1000);
				break;
			case A00002:
				if (timerA00002 != null) {
					return;
				}
				if (timerA00002X != null) {
					timerA00002X.cancel();
					timerA00002X = null;
				}
				timerA00002 = new Timer();
				task = new TimerTask() {

					@Override
					public void run() {

						// 取消预警
						timerA00002 = null;
						JSONObject param = getAlertParam(value);
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								LogUtil.d(TAG, "发送报警timerA00002");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00002X = new Timer();
								timerA00002X.schedule(
										new TimerTask() {

											@Override
											public void run() {
												if (timerA00002 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00002));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00002X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00002_Delay_Clear * 1000);
							}
						});
					}
				};
				timerA00002.schedule(task,
						ConstantConfig.AlertA00002_Delay_Raise * 1000);
				break;
			case A00003:
				if (timerA00003 != null) {
					return;
				}
				if (timerA00003X != null) {
					timerA00003X.cancel();
					timerA00003X = null;
				}
				timerA00003 = new Timer();

				task = new TimerTask() {

					@SuppressWarnings("unused")
					@Override
					public void run() {
						// 取消预警
						timerA00003 = null;
						JSONObject param = null;
						if (lastA00003Obj != null) {
							param = getAlertParam(value, lastA00003Obj);
						} else {
							param = getAlertParam(value);
						}
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								if (true) {
									// 如果发送成功则清除上次记录
									lastA00003Obj = null;
								} else {
									// 如果发送失败记录当前网络预警等待下次发送
									lastA00003Obj = value;
								}
								LogUtil.d(TAG, "发送报警timerA00003");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00003X = new Timer();
								timerA00003X.schedule(
										new TimerTask() {

											@Override
											public void run() {
												if (timerA00003 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00003));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00003X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00003_Delay_Clear * 1000);
							}
						});
					}
				};
				timerA00003.schedule(task,
						ConstantConfig.AlertA00003_Delay_Raise * 1000);
				break;
			case A00004:
				if (timerA00004 != null) {
					return;
				}
				if (timerA00004X != null) {
					timerA00004X.cancel();
					timerA00004X = null;
				}
				timerA00004 = new Timer();
				task = new TimerTask() {

					@Override
					public void run() {
						// 取消预警
						timerA00004 = null;
						JSONObject param = getAlertParam(value);
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								LogUtil.d(TAG, "发送报警timerA00004");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00004X = new Timer();
								timerA00004X.schedule(
										new TimerTask() {

											@Override
											public void run() {
												if (timerA00004 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00004));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00004X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00004_Delay_Clear * 1000);
							}
						});
					}
				};
				timerA00004.schedule(task,
						ConstantConfig.AlertA00004_Delay_Raise * 1000);
				break;
			case A00005:
				if (timerA00005 != null) {
					return;
				}
				if (timerA00005X != null) {
					timerA00005X.cancel();
					timerA00005X = null;
				}
				timerA00005 = new Timer();
				task = new TimerTask() {

					@Override
					public void run() {
						// 取消预警
						timerA00005 = null;
						JSONObject param = getAlertParam(value);
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								LogUtil.d(TAG, "发送报警timerA00005");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00005X = new Timer();
								timerA00005X.schedule(
										new TimerTask() {

											@Override
											public void run() {
												if (timerA00005 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00005));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00005X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00005_Delay_Clear * 1000);
							}
						});
					}
				};
				timerA00005.schedule(task,
						ConstantConfig.AlertA00005_Delay_Raise * 1000);
				break;
			case A00006:
				if (timerA00006 != null) {
					return;
				}
				if (timerA00006X != null) {
					timerA00006X.cancel();
					timerA00006X = null;
				}
				timerA00006 = new Timer();
				task = new TimerTask() {

					@Override
					public void run() {
						// 取消预警
						timerA00006 = null;
						JSONObject param = getAlertParam(value);
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								LogUtil.d(TAG, "发送报警timerA00006");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00006X = new Timer();
								timerA00006X.schedule(
										new TimerTask() {

											@Override
											public void run() {
												if (timerA00006 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00006));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00006X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00006_Delay_Clear * 1000);
							}
						});
					}
				};
				timerA00006.schedule(task,
						ConstantConfig.AlertA00006_Delay_Raise * 1000);
				break;
			case A00007:
				if (timerA00007 != null) {
					return;
				}
				if (timerA00007X != null) {
					timerA00007X.cancel();
					timerA00007X = null;
				}
				timerA00007 = new Timer();
				task = new TimerTask() {

					@Override
					public void run() {
						// 取消预警
						timerA00007 = null;
						JSONObject param = getAlertParam(value);
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								LogUtil.d(TAG, "发送报警timerA00007");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00007X = new Timer();
								timerA00007X.schedule(
										new TimerTask() {

											@Override
											public void run() {
												if (timerA00007 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00007));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00007X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00007_Delay_Clear * 1000);
							}
						});
					}
				};
				timerA00007.schedule(task,
						ConstantConfig.AlertA00007_Delay_Raise * 1000);
				break;
			case A00008:
				if (timerA00008 != null) {
					return;
				}
				if (timerA00008X != null) {
					timerA00008X.cancel();
					timerA00008X = null;
				}
				timerA00008 = new Timer();
				task = new TimerTask() {

					@Override
					public void run() {
						// 取消预警
						timerA00008 = null;
						JSONObject param = getAlertParam(value);
						sendAlert(param, new BackCall() {
							@Override
							public void execute() {
								LogUtil.d(TAG, "发送报警timerA00008");
								// 发送预警信息 param
								// 预报成功后，准备取消报警
								timerA00008X = new Timer();
								timerA00008X.schedule(
										new TimerTask() {

											@Override
											public void run() {
												if (timerA00008 == null) {
													JSONObject param = getAlertParam(getCancelAlert(AlertType.A00008));
													sendAlert(param,
															new BackCall() {
																@Override
																public void execute() {
																	LogUtil.d(
																			TAG,
																			"取消报警timerA00008X");
																}
															});
												}
											}
										},
										ConstantConfig.AlertA00008_Delay_Clear * 1000);
							}
						});
					}
				};
				timerA00008.schedule(task,
						ConstantConfig.AlertA00008_Delay_Raise * 1000);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消预警
	 * 
	 * @param type
	 */
	public void cancelAlert(AlertType type) {
		switch (type) {
		case A00001:
			if (timerA00001 != null) {
				timerA00001.cancel();
				timerA00001 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00001X");
				return;
			}
			break;
		case A00002:
			if (timerA00002 != null) {
				timerA00002.cancel();
				timerA00002 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00002X");
				return;
			}
			break;
		case A00003:
			if (timerA00003 != null) {
				timerA00003.cancel();
				timerA00003 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00003X");
				return;
			}
			break;
		case A00004:
			if (timerA00004 != null) {
				timerA00004.cancel();
				timerA00004 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00004X");
				return;
			}
			break;
		case A00005:
			if (timerA00005 != null) {
				timerA00005.cancel();
				timerA00005 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00005X");
				return;
			}
			break;
		case A00006:
			if (timerA00006 != null) {
				timerA00006.cancel();
				timerA00006 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00006X");
				return;
			}
			break;
		case A00007:
			if (timerA00007 != null) {
				timerA00007.cancel();
				timerA00007 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00007X");
				return;
			}
			break;
		case A00008:
			if (timerA00008 != null) {
				timerA00008.cancel();
				timerA00008 = null;
				// 取消预警
				LogUtil.d(TAG, "解除报警timerA00008X");
				return;
			}
			break;
		default:
			break;
		}
	}

}
