package com.example.testskin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class UIUtil {
	public static void showToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public static void setMessage(Handler handler, int what) {
		Message message = Message.obtain();
		message.what = what;
		handler.sendMessage(message);
	}

	public static void setMessage(Handler handler, int what, Object obj) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = obj;
		handler.sendMessage(message);
	}

	public static void sendBroadcast(String action) {
		AppApplication.Instance.sendBroadcast(new Intent(action));
	}


	public static Dialog buildDialog(Context context, View layout) {
		final Dialog dlg = new Dialog(context, R.style.DialogThemeNoAnimation);
		final int cFullFillWidth = 1000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		// lp.x = 0;
		// final int cMakeBottom = -1000;
		// lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}
}
