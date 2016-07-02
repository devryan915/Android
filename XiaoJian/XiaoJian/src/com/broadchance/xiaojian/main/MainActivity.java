package com.broadchance.xiaojian.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.broadchance.xiaojian.BaseActivity;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.cloudservice.CloudServiceMainActivity;
import com.broadchance.xiaojian.person.PersonalHealthActivity;
import com.broadchance.xiaojian.service.BleDataParserService;
import com.broadchance.xiaojian.service.BluetoothLeService;
import com.broadchance.xiaojian.service.BleDataDomainService;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		NoTitle = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SlidingMenu sm = getSlidingMenu();
		sm.setSlidingEnabled(true);
		sm.setMode(SlidingMenu.RIGHT);
		sm.setSecondaryMenu(R.layout.fragment_settings_container);
		// 设置左右滑动的drawer
		// sm.setSecondaryShadowDrawable(R.drawable.main_settings);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			startBleService();
		}
		FragmentTransaction trans = getSupportFragmentManager()
				.beginTransaction();
		trans.replace(R.id.menu_frame_two, new SettingsFragment());
		trans.replace(R.id.main_nav, new MainFragment());
		trans.commit();
	}

	@Override
	protected void onDestroy() {
		stopBleService();
		super.onDestroy();
	}

	private void startBleService() {
		Intent bleServiceintent = new Intent(MainActivity.this,
				BluetoothLeService.class);
		startService(bleServiceintent);
		Intent dataParserIntent = new Intent(MainActivity.this,
				BleDataParserService.class);
		startService(dataParserIntent);
		Intent uploadDataIntent = new Intent(MainActivity.this,
				BleDataDomainService.class);
		startService(uploadDataIntent);
	}

	private void stopBleService() {
		Intent bleServiceintent = new Intent(MainActivity.this,
				BluetoothLeService.class);
		stopService(bleServiceintent);
		Intent dataParserIntent = new Intent(MainActivity.this,
				BleDataParserService.class);
		stopService(dataParserIntent);
		Intent uploadDataIntent = new Intent(MainActivity.this,
				BleDataDomainService.class);
		stopService(uploadDataIntent);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.main_settings:
			getSlidingMenu().showSecondaryMenu(true);
			break;
		case R.id.main_healthcall:
			break;
		case R.id.main_personalhealth:
			intent.setClass(MainActivity.this, PersonalHealthActivity.class);
			startActivity(intent);
			break;
		case R.id.main_lovecare:
			break;
		case R.id.main_cloudservice:
			intent.setClass(MainActivity.this, CloudServiceMainActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
}
