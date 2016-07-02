package com.langlang.activity;


import com.langlang.elderly_langlang.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;


public class WeWebsiteActivity extends Activity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setFullScrean();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wewebsite);
		
		
		webView = (WebView) this.findViewById(R.id.we_webview);
		// Intent intent = getIntent();
		// String URL = intent.getStringExtra("URL");
		webView.loadUrl("http://www.langlangit.com/index.action");
		// 设置此属性，可任意比例缩放
		// webView.getSettings().setUseWideViewPort(true);
		// 自适应屏幕
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setLoadWithOverviewMode(true);
	}
	/**
	 * 设置全屏
	 */
	public void setFullScrean() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置竖屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(WeWebsiteActivity.this,
					AboutActivity.class));
			WeWebsiteActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

