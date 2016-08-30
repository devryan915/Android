package com.broadchance.wdecgrec.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class PowerChangeReceiver extends BroadcastReceiver {
	private boolean isPowerLow = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			// 得到系统当前电量
			int level = intent.getIntExtra("level", 0);
			// 取得系统总电量
			int total = intent.getIntExtra("scale", 100);
			float power = (level * 100f) / total;
			// 网关电量预警
			if (power < ConstantConfig.AlertA00004_Limit_Raise) {
				JSONObject alertObj = new JSONObject();
				try {
					isPowerLow = true;
					alertObj.put("id", AlertType.A00004.getValue());
					alertObj.put("state", 1);
					alertObj.put("time", CommonUtil.getTime_B());
					JSONObject value = new JSONObject();
					value.put("batteryperc", power);
					alertObj.put("value", value);
					AlertMachine.getInstance().sendAlert(AlertType.A00004,
							alertObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (power > ConstantConfig.AlertA00004_Limit_Clear
					&& isPowerLow) {
				isPowerLow = false;
				AlertMachine.getInstance().cancelAlert(AlertType.A00004);
			}
			// 当电量小于15%时触发
			Toast.makeText(context, "当前电量已小于" + power, Toast.LENGTH_LONG)
					.show();
		}

	}

}
