package com.broadchance.wdecgrec.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.services.BleDomainService;
import com.broadchance.wdecgrec.services.BluetoothLeService;

public class UploadActivity extends BaseActivity {
	TextView tvUpload;
	private final BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleDomainService.ACTION_UPLOAD_UPLOADCHANGED.equals(action)) {
				String data = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				String[] counts = data.split(":");
				// 上传结束
				if (counts[1].equals(counts[0]) || counts[1].equals("0")) {
					if (counts[1].equals("0")) {
						showToast("无文件可上传");
					} else {
						showToast("一键上传完毕");
					}
					tvUpload.setText("当前有待上传文件" + counts[1] + "/" + counts[1]);
					return;
				}
				tvUpload.setText("当前有待上传文件" + counts[0] + "/" + counts[1]);

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		tvUpload = (TextView) findViewById(R.id.tvUpload);
		findViewById(R.id.buttonTitleBack).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(uploadBroadcastReceiver, makeGattUpdateIntentFilter());
		sendBroadcast(new Intent(BleDomainService.ACTION_UPLOAD_STARTONEKEYMODE));
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(uploadBroadcastReceiver);
		super.onDestroy();
	}

	private IntentFilter makeGattUpdateIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleDomainService.ACTION_UPLOAD_UPLOADCHANGED);
		return intentFilter;
	}

	private void returnSettingsAcitivity() {
		Intent myIntent = new Intent();
		myIntent = new Intent(UploadActivity.this, SettingsActivity.class);
		startActivity(myIntent);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonTitleBack:
			returnSettingsAcitivity();
		default:
			break;
		}
	}

}
