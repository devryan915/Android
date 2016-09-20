package com.langlang.dialog;


import com.langlang.elderly_langlang.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class DataDialog extends Dialog {
	private Context context;

	public DataDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setFullScrean();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_data);
	}

	public void setFullScrean() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
