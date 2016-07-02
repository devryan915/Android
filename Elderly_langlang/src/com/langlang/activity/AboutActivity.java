package com.langlang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.langlang.activity.BaseActivity;
import com.langlang.elderly_langlang.R;

public class AboutActivity extends BaseActivity {
	private TextView textView;
	private TextView verson_tw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		verson_tw=(TextView)this.findViewById(R.id.about_version_tw);
		verson_tw.setText(m_version_base);
		textView=(TextView)this.findViewById(R.id.official_website);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				startActivity(new Intent(AboutActivity.this,WeWebsiteActivity.class));
//				AboutActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(AboutActivity.this, SetActivity.class));
			AboutActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
