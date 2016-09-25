package com.broadchance.wdecgrec.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertType;
import com.broadchance.wdecgrec.services.BleDomainService;

public class PowerChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			// 得到系统当前电量
			int level = intent.getIntExtra("level", 0);
			// 取得系统总电量
			int total = intent.getIntExtra("scale", 100);
			float power = (level * 1f) / total;
			// 网关电量预警
			if (power < AlertMachine.getInstance()
					.getAlertConfig(AlertType.A00004).getFloatValueRaise()) {
				JSONObject alertObj = new JSONObject();
				try {
					if (AlertMachine.getInstance().canSendAlert(
							AlertType.A00004, 1)) {
						alertObj.put("id", AlertType.A00004.getValue());
						alertObj.put("state", 1);
						alertObj.put("time", CommonUtil.getTime_B());
						JSONObject value = new JSONObject();
						value.put("batteryperc", power);
						alertObj.put("value", value);
						AlertMachine.getInstance().sendAlert(AlertType.A00004,
								alertObj);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (power > AlertMachine.getInstance()
					.getAlertConfig(AlertType.A00004).getFloatValueClear()) {
				if (AlertMachine.getInstance()
						.canSendAlert(AlertType.A00004, 0)) {
					AlertMachine.getInstance().cancelAlert(AlertType.A00004);
				}
			}
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
					|| status == BatteryManager.BATTERY_STATUS_FULL;
			if (isCharging) {
				context.sendBroadcast(new Intent(
						BleDomainService.ACTION_UPLOAD_STARTREALMODE));
			} else {
				context.sendBroadcast(new Intent(
						BleDomainService.ACTION_UPLOAD_ENDREALMODE));
			}
			// if (ConstantConfig.Debug) {
			// UIUtil.showBleToast("触发 " + (isCharging ? "开启" : "关闭") + "实时上传");
			// }
			int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
					-1);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			if (ConstantConfig.Debug) {
				// 当电量小于15%时触发
				// UIUtil.showBleToast(
				// context,
				// (isCharging ? ("充电中,充电方式：" + (usbCharge ? "usb" : "ac"))
				// : "")
				// + "\n"
				// + "当前网关电量level/total:"
				// + level
				// + "/"
				// + total + " power:" + power);
			}
		}
	}

}
