package com.langlang.dialog;

import com.langlang.elderly_langlang.R;
import com.langlang.global.UserInfo;
import com.langlang.utils.UIUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

public class UpdateVersionDialog extends Dialog{
	private Context context;
	public UpdateVersionDialog(Context context) {
		super(context);
		this.context=context;
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_update_version);
		
	}
	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if("1".equals(UserInfo.getIntance().getUserData().getVersion_mark())){
					UIUtil.setToast(context, "服务器接口更改，必须立即更新");
				}else{
					this.cancel();
				}
				
			}

		return super.onKeyDown(keyCode, event);
	}
}
