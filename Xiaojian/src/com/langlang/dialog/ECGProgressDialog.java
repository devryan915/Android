package com.langlang.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;

public class ECGProgressDialog extends ProgressDialog{
	private Context context;
	private OnCancelCallback callback;
	public ECGProgressDialog(Context context, OnCancelCallback callback) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.callback = callback;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (callback != null) {
				callback.onCancel();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public interface OnCancelCallback {
		public void onCancel();
	}
}
