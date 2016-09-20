package com.broadchance.xiaojian.person;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.broadchance.xiaojian.BaseActivity;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.main.MainActivity;
import com.broadchance.xiaojian.person.ecg.PersonalECGActivity;
import com.broadchance.xiaojian.person.sleep.PersonalSleepActivity;
import com.broadchance.xiaojian.person.sports.PersonalSportsActivity;

public class PersonalHealthActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ShowLeftTitle = true;
		super.onCreate(savedInstanceState);
		setTitle(R.string.personalhealth);
		setContentView(R.layout.activity_personal_health);
		mleftTitle.setOnClickListener(this);
		findViewById(R.id.personalhealth_ecg).setOnClickListener(this);
		findViewById(R.id.personalhealth_sleep).setOnClickListener(this);
		findViewById(R.id.personalhealth_sports).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(PersonalHealthActivity.this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.personalhealth_ecg:
			intent.setClass(PersonalHealthActivity.this,
					PersonalECGActivity.class);
			startActivity(intent);
			break;
		case R.id.personalhealth_sleep:
			intent.setClass(PersonalHealthActivity.this,
					PersonalSleepActivity.class);
			startActivity(intent);
			break;
		case R.id.personalhealth_sports:
			intent.setClass(PersonalHealthActivity.this,
					PersonalSportsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
}
