package com.kc.ihaigo.ui.personal;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;

public class WebViewActivity extends IHaiGoActivity {

	private WebView webview;
	private String link;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		webview = (WebView) findViewById(R.id.webview);
		webview.setWebViewClient(new WebViewClient());

	}

	@Override
	public void refresh() {
		super.refresh();
		Bundle resParams = getIntent().getExtras();
		link = resParams.getString("link");
		webview.loadUrl(link);

	}

}
