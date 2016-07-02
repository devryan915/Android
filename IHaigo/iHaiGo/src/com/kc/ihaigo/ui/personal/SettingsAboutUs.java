package com.kc.ihaigo.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.welcome.IhaigoWelcome;
import com.kc.ihaigo.util.DialogUtil;

public class SettingsAboutUs extends IHaiGoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_aboutus);
		initTitle();
		initComponents();
	}

	/**
	 * 
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {

	}

	/**
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		findViewById(R.id.settings_contact).setOnClickListener(this);
		findViewById(R.id.settings_welcome).setOnClickListener(this);
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.SettingsActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.settings_contact :
				DialogUtil.showContactUs(SettingsAboutUs.this.getParent());
				break;
			case R.id.settings_welcome :
				intent.setClass(SettingsAboutUs.this, IhaigoWelcome.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			default :
				break;
		}
	}

}
