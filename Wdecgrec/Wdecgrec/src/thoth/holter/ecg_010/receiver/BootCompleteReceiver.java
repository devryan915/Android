package thoth.holter.ecg_010.receiver;

import thoth.holter.ecg_010.services.GuardService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			// String pkgName = "com.broadchance.wdecgrec";
			// Intent intentApp = context.getPackageManager()
			// .getLaunchIntentForPackage(pkgName);
			// context.startActivity(intentApp);
			// UIUtil.showToast(context, "start app " + pkgName);
			Intent bleServiceintent = new Intent(context, GuardService.class);
			context.startService(bleServiceintent);
		}

	}

}
