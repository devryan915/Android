package com.broadchance.wdecgrec.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.CommonUtil;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertType;

public class NetChangeReceiver extends BroadcastReceiver {
	// private boolean isNetOff = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// NetworkInfo mobileInfo = manager
		// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// NetworkInfo wifiInfo = manager
		// .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();

		Toast.makeText(
				context,
				activeInfo == null ? "网络断开" : activeInfo.getTypeName() + " "
						+ activeInfo.getSubtypeName(), 1).show();
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user != null) {
			JSONObject alertObj = new JSONObject();
			try {
				alertObj.put("id", AlertType.A00003.getValue());
				alertObj.put("state", activeInfo == null ? 1 : 0);
				alertObj.put("time", CommonUtil.getTime_B());
				JSONObject value = new JSONObject();
				alertObj.put("value", value);
				AlertMachine.getInstance()
						.sendAlert(AlertType.A00003, alertObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
