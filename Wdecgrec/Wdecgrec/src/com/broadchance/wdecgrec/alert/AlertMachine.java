package com.broadchance.wdecgrec.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.broadchance.entity.AlertBody;
import com.broadchance.entity.AlertCFG;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.serverentity.ServerResponse;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.HttpReqCallBack;

/**
 * 预警波管理器
 * 
 * @author ryan.wang
 * 
 */
public class AlertMachine {

	public static final String TAG = AlertMachine.class.getSimpleName();
	private ClientGameService clientService = ClientGameService.getInstance();
	private Map<AlertType, AlertWorker> alertWorkers = new HashMap<AlertType, AlertWorker>();
	private Map<AlertType, AlertCFG> alertCFGs = new HashMap<AlertType, AlertCFG>();
	List<Integer> alert_ids = new ArrayList<Integer>();
	/**
	 * 传感器电极脱落 A00001预警延迟发生时间s
	 */
	private final static int AlertA00001_Delay_Raise = 8;
	/**
	 * 传感器电极脱落 A00001预警延迟取消时间
	 */
	private final static int AlertA00001_Delay_Clear = 8;
	/**
	 * BLE信号断联 A00002预警延迟发生时间
	 */
	private final static int AlertA00002_Delay_Raise = 8;
	/**
	 * BLE信号断联 A00002预警延迟取消时间
	 */
	private final static int AlertA00002_Delay_Clear = 8;
	/**
	 * 网络无信号 A00003预警延迟发生时间
	 */
	private final static int AlertA00003_Delay_Raise = 0;
	/**
	 * 网络无信号 A00003预警延迟取消时间
	 */
	private final static int AlertA00003_Delay_Clear = 0;
	/**
	 * 网关电量低 A00004预警延迟发生时间
	 */
	private final static int AlertA00004_Delay_Raise = 8;
	/**
	 * 网关电量低 <网关电量低百分比
	 */
	private final static float AlertA00004_Limit_Raise = 0.15f;
	/**
	 * 网关电量低 A00004预警延迟取消时间
	 */
	private final static int AlertA00004_Delay_Clear = 8;
	/**
	 * 网关电量低 >手机电量
	 */
	private final static float AlertA00004_Limit_Clear = 0.25f;
	/**
	 * 传感器电量低 A00005预警延迟发生时间
	 */
	private final static int AlertA00005_Delay_Raise = 180;
	/**
	 * 传感器电量低 <传感器电量低
	 */
	private final static float AlertA00005_Limit_Raise = 2.6f;
	/**
	 * 传感器电量低 A00005预警延迟取消时间
	 */
	private final static int AlertA00005_Delay_Clear = 60;
	/**
	 * 传感器电量低 >传感器电量取消预警阈值
	 */
	private final static float AlertA00005_Limit_Clear = 2.8f;
	/**
	 * 心动过速 B00001预警延迟发生时间
	 */
	private final static int AlertB00001_Delay_Raise = 8;
	/**
	 * 心动过速 >心动过速阈值
	 */
	private final static int AlertB00001_Limit_Raise = 120;
	/**
	 * 心动过速 B00001预警延迟取消时间
	 */
	private final static int AlertB00001_Delay_Clear = 8;
	/**
	 * 心动过速 <=阈值
	 */
	private final static int AlertB00001_Limit_Clear = 120;
	/**
	 * 心动过缓 B00002预警延迟发生时间
	 */
	private final static int AlertB00002_Delay_Raise = 8;
	/**
	 * 心动过缓 <心电过缓阈值
	 */
	private final static int AlertB00002_Limit_Raise = 60;
	/**
	 * 心动过缓 B00002预警延迟取消时间
	 */
	private final static int AlertB00002_Delay_Clear = 8;
	/**
	 * 心动过缓 >=阈值
	 */
	private final static int AlertB00002_Limit_Clear = 60;
	/**
	 * 停搏 B00003预警延迟发生时间
	 */
	private final static int AlertB00003_Delay_Raise = 4;
	private final static int AlertB00003_Limit_Raise = 0;
	/**
	 * 停搏 B00003预警延迟取消时间
	 */
	private final static int AlertB00003_Delay_Clear = 4;
	private final static int AlertB00003_Limit_Clear = 0;

	private AlertMachine() throws Exception {
		init();
	}

	public void init() throws Exception {
		AlertCFG value = null;
		value = getAlertCFG(AlertType.A00001);
		alertCFGs.put(AlertType.A00001, value);
		alertWorkers.put(AlertType.A00001, new AlertWorker(AlertType.A00001,
				value.getDelay_raise(), value.getDelay_clear()));

		value = getAlertCFG(AlertType.A00002);
		alertCFGs.put(AlertType.A00002, value);
		alertWorkers.put(AlertType.A00002, new AlertWorker(AlertType.A00002,
				value.getDelay_raise(), value.getDelay_clear()));

		value = getAlertCFG(AlertType.A00003);
		alertCFGs.put(AlertType.A00003, value);
		alertWorkers.put(AlertType.A00003, new AlertWorker(AlertType.A00003,
				value.getDelay_raise(), value.getDelay_clear()));

		value = getAlertCFG(AlertType.A00004);
		alertCFGs.put(AlertType.A00004, value);
		alertWorkers.put(AlertType.A00004, new AlertWorker(AlertType.A00004,
				value.getDelay_raise(), value.getDelay_clear()));

		value = getAlertCFG(AlertType.A00005);
		alertCFGs.put(AlertType.A00005, value);
		alertWorkers.put(AlertType.A00005, new AlertWorker(AlertType.A00005,
				value.getDelay_raise(), value.getDelay_clear()));

		value = getAlertCFG(AlertType.B00001);
		alertCFGs.put(AlertType.B00001, value);
		alertWorkers.put(AlertType.B00001, new AlertWorker(AlertType.B00001,
				value.getDelay_raise(), value.getDelay_clear()));

		value = getAlertCFG(AlertType.B00002);
		alertCFGs.put(AlertType.B00002, value);
		alertWorkers.put(AlertType.B00002, new AlertWorker(AlertType.B00002,
				value.getDelay_raise(), value.getDelay_clear()));

		value = getAlertCFG(AlertType.B00003);
		alertCFGs.put(AlertType.B00003, value);
		alertWorkers.put(AlertType.B00003, new AlertWorker(AlertType.B00003,
				value.getDelay_raise(), value.getDelay_clear()));
	}

	ScheduledExecutorService mEService = Executors.newScheduledThreadPool(16);
	private static AlertMachine _Instance;

	public static AlertMachine getInstance() {
		if (_Instance == null) {
			try {
				_Instance = new AlertMachine();
			} catch (Exception e) {
			}
		}
		return _Instance;
	}

	public AlertCFG getAlertConfig(AlertType type) {
		if (alertCFGs.containsKey(type)) {
			return alertCFGs.get(type);
		}
		return null;
	}

	private AtomicBoolean atSend = new AtomicBoolean(false);

	private JSONObject getAlertParam() {
		List<AlertBody> bodys = DataManager.getAlert();
		JSONObject param = null;
		if (bodys != null && bodys.size() > 0) {
			param = new JSONObject();
			try {
				UIUserInfoLogin user = DataManager.getUserInfo();
				param.put("device", user.getMacAddress());
				param.put("orderno", user.getAccess_token());
				param.put("time", CommonUtil.getTime_B());
				JSONArray alertArray = new JSONArray();
				alert_ids.clear();
				for (int i = 0; i < bodys.size(); i++) {
					JSONObject value = new JSONObject();
					AlertBody body = bodys.get(i);
					alert_ids.add(body.get_id());
					value.put("id", body.getId());
					value.put("state", body.getState());
					value.put("time", body.getTime());
					value.put("value", body.getState() == 1 ? body.getValue()
							: new JSONObject());
					alertArray.put(value);
				}
				param.put("alerteventarray", alertArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return param;
	}

	// private JSONObject getCancelAlert(AlertType type) {
	// JSONObject alertObj = new JSONObject();
	// try {
	// alertObj.put("id", type.getValue());
	// alertObj.put("state", 0);
	// alertObj.put("time", CommonUtil.getTime_B());
	// JSONObject value = new JSONObject();
	// alertObj.put("value", value);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// return alertObj;
	// }
	public void refreshCFG() {
		AlertCFG value = getAlertCFG(AlertType.A00001);
		alertCFGs.put(AlertType.A00001, value);
		alertWorkers.get(AlertType.A00001).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());

		value = getAlertCFG(AlertType.A00002);
		alertCFGs.put(AlertType.A00002, value);
		alertWorkers.get(AlertType.A00002).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());

		value = getAlertCFG(AlertType.A00003);
		alertCFGs.put(AlertType.A00003, value);
		alertWorkers.get(AlertType.A00003).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());

		value = getAlertCFG(AlertType.A00004);
		alertCFGs.put(AlertType.A00004, value);
		alertWorkers.get(AlertType.A00004).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());

		value = getAlertCFG(AlertType.A00005);
		alertCFGs.put(AlertType.A00005, value);
		alertWorkers.get(AlertType.A00005).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());

		value = getAlertCFG(AlertType.B00001);
		alertCFGs.put(AlertType.B00001, value);
		alertWorkers.get(AlertType.B00001).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());

		value = getAlertCFG(AlertType.B00002);
		alertCFGs.put(AlertType.B00002, value);
		alertWorkers.get(AlertType.B00002).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());

		value = getAlertCFG(AlertType.B00003);
		alertCFGs.put(AlertType.B00003, value);
		alertWorkers.get(AlertType.B00003).updateCFG(value.getDelay_raise(),
				value.getDelay_clear());
		LogUtil.d(TAG,
				"更新预警配置：B00001：\nraise:"
						+ alertCFGs.get(AlertType.B00001).getIntValueRaise()
						+ " clear"
						+ alertCFGs.get(AlertType.B00001).getIntValueClear()
						+ "\n B00002：\nraise:"
						+ alertCFGs.get(AlertType.B00002).getIntValueRaise()
						+ " clear"
						+ alertCFGs.get(AlertType.B00002).getIntValueClear());
	}

	private void updateCFG() {
		clientService.getAlertCFG();
	}

	public void sendAlert() {
		if (atSend.compareAndSet(false, true)) {
			JSONObject param = getAlertParam();
			if (param != null) {
				clientService.sendAlert(param,
						new HttpReqCallBack<ServerResponse>() {

							@Override
							public Activity getReqActivity() {
								return null;
							}

							@Override
							public void doSuccess(ServerResponse result) {
								if (result.isOK()) {
									if (ConstantConfig.Debug) {
										LogUtil.d(TAG,
												"预警发送成功" + alert_ids.size()
														+ "条" + "\noutdata:"
														+ result.getOutdata());
										UIUtil.showRemoteToast("预警发送成功"
												+ alert_ids.size() + "条");
									}
									String outData = result.getOutdata();
									if (outData != null
											&& outData.trim().length() > 0) {
										updateCFG();
									}
									int count = 0;
									for (int i = 0; i < alert_ids.size(); i++) {
										boolean ret = DataManager
												.deleteAlert(alert_ids.get(i));
										if (ret) {
											count++;
										}
									}
									if (ConstantConfig.Debug) {
										LogUtil.d(TAG, "删除本地预警数据" + count + "条");
									}
								}
								atSend.set(false);
							}

							@Override
							public void doError(String result) {
								atSend.set(false);
							}
						});
			} else {
				atSend.set(false);
			}
		}
	}

	public ScheduledFuture<?> schedule(Runnable command, long delay,
			TimeUnit unit) {
		return mEService.schedule(command, delay, unit);
	}

	public boolean canSendAlert(AlertType type, int state) {
		return alertWorkers.get(type).canAlert(state);
	}

	private AlertCFG getAlertCFG(AlertType type) {
		AlertCFG cfg = new AlertCFG();
		JSONObject jobj = null;
		try {
			try {
				jobj = new JSONObject(PreferencesManager.getInstance()
						.getString(getCFGKeyType(type)));
			} catch (Exception e1) {
			}
			AlertType id = type;
			Integer limit_raise = null;
			Float limit_raisef = null;
			int delay_raise = 0;
			Integer limit_clear = null;
			Float limit_clearf = null;
			int delay_clear = 0;
			switch (type) {
			case A00001:
				delay_raise = AlertA00001_Delay_Raise;
				delay_clear = AlertA00001_Delay_Clear;
				break;
			case A00002:
				delay_raise = AlertA00002_Delay_Raise;
				delay_clear = AlertA00002_Delay_Clear;
				break;
			case A00003:
				delay_raise = AlertA00003_Delay_Raise;
				delay_clear = AlertA00003_Delay_Clear;
				break;
			case A00004:
				limit_raisef = AlertA00004_Limit_Raise;
				limit_clearf = AlertA00004_Limit_Clear;
				delay_raise = AlertA00004_Delay_Raise;
				delay_clear = AlertA00004_Delay_Clear;
				break;
			case A00005:
				limit_raisef = AlertA00005_Limit_Raise;
				limit_clearf = AlertA00005_Limit_Clear;
				delay_raise = AlertA00005_Delay_Raise;
				delay_clear = AlertA00005_Delay_Clear;
				break;
			case B00001:
				limit_raise = AlertB00001_Limit_Raise;
				limit_clear = AlertB00001_Limit_Clear;
				delay_raise = AlertB00001_Delay_Raise;
				delay_clear = AlertB00001_Delay_Clear;
				break;
			case B00002:
				limit_raise = AlertB00002_Limit_Raise;
				limit_clear = AlertB00002_Limit_Clear;
				delay_raise = AlertB00002_Delay_Raise;
				delay_clear = AlertB00002_Delay_Clear;
				break;
			case B00003:
				limit_raise = AlertB00003_Limit_Raise;
				limit_clear = AlertB00003_Limit_Clear;
				delay_raise = AlertB00003_Delay_Raise;
				delay_clear = AlertB00003_Delay_Clear;
				break;
			default:
				break;
			}
			if (jobj != null) {
				cfg.setDelay_clear(Integer.parseInt(jobj
						.getString("delay_clear")));
				cfg.setDelay_raise(Integer.parseInt(jobj
						.getString("delay_raise")));
				String lclr = jobj.getString("limit_clear");
				String lrse = jobj.getString("limit_raise");
				cfg.setLimit_clear(lclr);
				cfg.setLimit_raise(lrse);
				try {
					limit_raisef = Float.parseFloat(lrse);
					limit_clearf = Float.parseFloat(lclr);
				} catch (NumberFormatException e) {
				}
				if (limit_raisef != null) {
					cfg.setFloatValueRaise(limit_raisef);
					cfg.setFloatValueClear(limit_clearf);
				}
				cfg.setId(id);
				try {
					limit_raise = Integer.parseInt(lrse);
					limit_clear = Integer.parseInt(lclr);
				} catch (NumberFormatException e) {
				}
				if (limit_raise != null) {
					cfg.setIntValueRaise(limit_raise);
					cfg.setIntValueClear(limit_clear);
				}
			} else {
				cfg.setDelay_clear(delay_clear);
				cfg.setDelay_raise(delay_raise);
				if (limit_raisef != null) {
					cfg.setFloatValueRaise(limit_raisef);
					cfg.setFloatValueClear(limit_clearf);
					cfg.setLimit_clear(limit_clear + "");
					cfg.setLimit_raise(limit_raise + "");
				}
				cfg.setId(id);
				if (limit_raise != null) {
					cfg.setIntValueRaise(limit_raise);
					cfg.setIntValueClear(limit_clear);
					cfg.setLimit_clear(limit_clear + "");
					cfg.setLimit_raise(limit_raise + "");
				}
			}
		} catch (JSONException e) {
		}
		return cfg;
	}

	public static String getStatusKeyType(AlertType type) {
		return getKeyType(type, "Status");
	}

	public static String getCFGKeyType(AlertType type) {
		return getKeyType(type, "CFG");
	}

	private static String getKeyType(AlertType type, String suffix) {
		try {
			UIUserInfoLogin user = DataManager.getUserInfo();
			return user.getUserID() + type.getValue() + "_" + suffix;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 发送预警
	 * 
	 * @param type
	 * @param value
	 */
	public void sendAlert(AlertType type, JSONObject value) {
		try {
			alertWorkers.get(type).send(value);
			// TimerTask task;
			// switch (type) {
			// case A00001:
			// // 如果发生脱落则取消停博报警
			// break;
			// case A00002:
			// break;
			// case A00003:
			// break;
			// case A00004:
			// break;
			// case A00005:
			// break;
			// case B00001:
			// break;
			// case B00002:
			// break;
			// case B00003:
			// break;
			// default:
			// break;
			// }
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
		alertWorkers.get(type).cancel();
	}

}
