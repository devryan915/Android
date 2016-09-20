package com.broadchance.xiaojian.cloudservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.broadchance.xiaojian.BaseActivity;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.main.MainActivity;
import com.broadchance.xiaojian.person.PersonalHealthActivity;
import com.broadchance.xiaojian.person.ecg.PersonalECGActivity;
import com.broadchance.xiaojian.person.sleep.PersonalSleepActivity;
import com.broadchance.xiaojian.person.sports.PersonalSportsActivity;

public class CloudServiceMainActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ShowLeftTitle = true;
		super.onCreate(savedInstanceState);
		setTitle(R.string.cloudservice);
		setContentView(R.layout.activity_cloud_service_main);
		mleftTitle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(CloudServiceMainActivity.this, MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
}
