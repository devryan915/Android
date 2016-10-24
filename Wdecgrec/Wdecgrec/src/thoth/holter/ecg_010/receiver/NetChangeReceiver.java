package thoth.holter.ecg_010.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import thoth.holter.ecg_010.manager.DataManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.broadchance.entity.UIUserInfoLogin;
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
		// UIUserInfoLogin user = DataManager.getUserInfo();
		if (DataManager.isLogin()) {
			if (activeInfo == null) {
				if (AlertMachine.getInstance()
						.canSendAlert(AlertType.A00003, 1)) {
					JSONObject alertObj = new JSONObject();
					try {
						alertObj.put("id", AlertType.A00003.getValue());
						alertObj.put("state", 1);
						alertObj.put("time", CommonUtil.getTime_B());
						JSONObject value = new JSONObject();
						alertObj.put("value", value);
						AlertMachine.getInstance().sendAlert(AlertType.A00003,
								alertObj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				if (AlertMachine.getInstance()
						.canSendAlert(AlertType.A00003, 0)) {
					AlertMachine.getInstance().cancelAlert(AlertType.A00003);
				}
			}
		}
	}

}
