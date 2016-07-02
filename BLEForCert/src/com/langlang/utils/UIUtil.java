package com.langlang.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class UIUtil {
	public static void setToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
	
	public static void setLongToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public static void setMessage(Handler handler, int what) {
		Message message = Message.obtain();
		message.what = what;
		handler.sendMessage(message);
	}
	public static void setMessage(Handler handler, int what,Object obj) {
		Message message = Message.obtain();
		message.what = what;
		message.obj=obj;
		handler.sendMessage(message);
	}
}
