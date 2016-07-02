package com.broadchance.wdecgrec.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetChangeReceiver extends BroadcastReceiver {

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
	}

}
