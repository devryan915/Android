package com.langlang.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;

public class SaveDialog extends ProgressDialog{
	private Context context;
	private SaveCallBack saveCallBack;
	public SaveDialog(Context context,SaveCallBack saveCallBack) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.saveCallBack=saveCallBack;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (saveCallBack != null) {
				saveCallBack.Cancel();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public interface SaveCallBack{
		public void Cancel();
	}
}
