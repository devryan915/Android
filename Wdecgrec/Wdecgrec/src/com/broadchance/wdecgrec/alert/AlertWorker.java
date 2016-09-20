package com.broadchance.wdecgrec.alert;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.broadchance.entity.AlertCFG;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;

public class AlertWorker {

	private final static String TAG = AlertMachine.class.getSimpleName();

	private ScheduledFuture<?> mSendSdl;
	private ScheduledFuture<?> mCancelSendSdl;
	private AlertStatus mStatus = AlertStatus.NONE;
	private String typeKey = null;
	private AlertType type;
	private int raiseDelay;
	private int clearDelay;
	private JSONObject alertValue;

	/**
	 * 记录上一次网络断开预警
	 * 
	 * @param state
	 *            1触发0取消
	 */
	public boolean canAlert(int state) {
		// 关闭预警
		// if (true)
		// return false;
		return state == 1 ? (!isLastStatusIsSend() && (mStatus != AlertStatus.AlertSending || (mStatus == AlertStatus.AlertSending && isCancelRun())))
				: (isLastStatusIsSend() && (mStatus != AlertStatus.AlertCancelSending || (mStatus == AlertStatus.AlertCancelSending && isSendRun())));
	}

	/**
	 * 取消报警是否在发送
	 * 
	 * @return
	 */
	private boolean isCancelRun() {
		return mCancelSendSdl != null && !mCancelSendSdl.isDone();
	}

	/**
	 * 取消取消报警
	 */
	private void cancelCancel() {
		if (isCancelRun()) {
			mCancelSendSdl.cancel(true);
			mCancelSendSdl = null;
		}
	}

	/**
	 * 触发报警是否在发送
	 * 
	 * @return
	 */
	private boolean isSendRun() {
		return mSendSdl != null && !mSendSdl.isDone();
	}

	/**
	 * 取消发送报警
	 */
	private void cancelSend() {
		if (isSendRun()) {
			mSendSdl.cancel(true);
			mSendSdl = null;
		}
	}

	public AlertWorker(AlertType type, int raiseDelay, int clearDelay)
			throws Exception {
		this.type = type;
		this.typeKey = AlertMachine.getStatusKeyType(type);
		if (typeKey != null) {
			this.raiseDelay = raiseDelay;
			this.clearDelay = clearDelay;
		} else {
			throw new Exception("用户数据不存在");
		}
	}

	/**
	 * 更新配置
	 * 
	 * @param raiseDelay
	 * @param clearDelay
	 */
	public void updateCFG(int raiseDelay, int clearDelay) {
		this.raiseDelay = raiseDelay;
		this.clearDelay = clearDelay;
	}

	/**
	 * 是否上一次是触发预警
	 * 
	 * @return
	 */
	private boolean isLastStatusIsSend() {
		return PreferencesManager.getInstance().getBoolean(typeKey, false);
	}

	private void setLastStatus(boolean isSend) {
		PreferencesManager.getInstance().putBoolean(typeKey, isSend);
	}

	public synchronized void send(JSONObject value) {
		// if (ConstantConfig.Debug) {
		// LogUtil.d(TAG, "取消预警" + type.getValue() + "\t" + value.toString());
		// }
		cancelCancel();
		// 如果已经正在上传，或者上一次预警已经发出了，则无需再次发送预警
		if (mStatus == AlertStatus.AlertSending || isLastStatusIsSend()) {
			return;
		}
		alertValue = value;
		mStatus = AlertStatus.AlertSending;
		cancelSend();
		mSendSdl = AlertMachine.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				// 取消预警
				boolean ret = DataManager.saveAlert(type, 1,
						CommonUtil.getTime_B(), alertValue);
				if (ConstantConfig.Debug) {
					AlertCFG cfg = AlertMachine.getInstance().getAlertConfig(
							type);
					LogUtil.d(TAG, "保存触发预警" + type.getValue()
							+ (ret ? "成功" : "失败") + " " + cfg.getLimit_raise());
					UIUtil.showToast("保存触发预警" + type.getValue()
							+ (ret ? "成功" : "失败") + " " + cfg.getLimit_raise());
				}
				mStatus = AlertStatus.NONE;
				setLastStatus(true);
				mSendSdl = null;
				AlertMachine.getInstance().sendAlert();
			}
		}, raiseDelay, TimeUnit.SECONDS);

	}

	public synchronized void cancel() {
		// if (ConstantConfig.Debug) {
		// LogUtil.d(TAG, "取消预警" + type.getValue());
		// }
		cancelSend();
		if (mStatus == AlertStatus.AlertCancelSending || !isLastStatusIsSend()) {
			return;
		}
		mStatus = AlertStatus.AlertCancelSending;
		cancelCancel();
		mCancelSendSdl = AlertMachine.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				boolean ret = DataManager.saveAlert(type, 0,
						CommonUtil.getTime_B(), null);
				if (ConstantConfig.Debug) {
					AlertCFG cfg = AlertMachine.getInstance().getAlertConfig(
							type);
					LogUtil.d(TAG, "保存取消预警" + type.getValue()
							+ (ret ? "成功" : "失败") + " " + cfg.getLimit_clear());
					UIUtil.showToast("保存取消预警" + type.getValue()
							+ (ret ? "成功" : "失败") + " " + cfg.getLimit_clear());
				}
				mStatus = AlertStatus.NONE;
				setLastStatus(false);
				mCancelSendSdl = null;
				AlertMachine.getInstance().sendAlert();
			}
		}, clearDelay, TimeUnit.SECONDS);
	}
}
