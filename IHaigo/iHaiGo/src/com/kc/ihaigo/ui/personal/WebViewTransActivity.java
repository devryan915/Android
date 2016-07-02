package com.kc.ihaigo.ui.personal;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;

public class WebViewTransActivity extends Activity {

	private WebView webview;
	private String link;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		webview = (WebView) findViewById(R.id.webview);
		webview.setWebViewClient(new WebViewClient());
		Bundle resParams = getIntent().getExtras();
		link = resParams.getString("link");
		webview.loadUrl(link);
		findViewById(R.id.title_left).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WebViewTransActivity.this.finish();
				
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
			webview.goBack();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

}
