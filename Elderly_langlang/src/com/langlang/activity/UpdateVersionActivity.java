package com.langlang.activity;

import com.langlang.elderly_langlang.R;
import com.langlang.global.UpdateManager;
import com.langlang.global.UserInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UpdateVersionActivity extends BaseActivity {
	private TextView not_found_tw;
	private LinearLayout linearLayout;
	private UpdateManager myAutoUpdate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_version);
		myAutoUpdate=new UpdateManager(this);
		not_found_tw=(TextView)this.findViewById(R.id.atupdateversion_not_found_tw);
		linearLayout=(LinearLayout)this.findViewById(R.id.atupdateversion_layout);
		if(UserInfo.getIntance().getUserData().getVersion().equalsIgnoreCase(m_version_code+"")){
			not_found_tw.setVisibility(View.VISIBLE);
			linearLayout.setVisibility(View.GONE);
		 }
		else{
			not_found_tw.setVisibility(View.GONE);
			linearLayout.setVisibility(View.VISIBLE);
		}
		findViewById(R.id.atupdateversion_at_once_bt).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						 myAutoUpdate.showDownloadDialog();
					}
				});
		findViewById(R.id.atupdateversion_later_bt).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						startActivity(new Intent(UpdateVersionActivity.this,
								SetActivity.class));
						UpdateVersionActivity.this.finish();
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(UpdateVersionActivity.this,
					SetActivity.class));
			UpdateVersionActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
