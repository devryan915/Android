/**
 * @Title: SettingInform.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月18日 下午3:38:17

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.SettingsConfig;

/**
 * @ClassName: SettingInform
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月18日 下午3:38:17
 * 
 */

public class SettingInformType extends IHaiGoActivity
		implements
			OnClickListener {
	private CheckBox settings_shipping_latesttip;
	private CheckBox settings_order_latesttip;
	private CheckBox settings_topic_latesttip;
	private CheckBox settings_goodswarning_latesttip;
	private CheckBox settings_activity_latesttip;
	private LinearLayout settings_ll;
	private CheckBox settings_receivemsg;
	private SettingsConfig config;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_inform_type);
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
		config = new SettingsConfig(SettingInformType.this);
		settings_receivemsg = (CheckBox) findViewById(R.id.settings_receivemsg);
		settings_shipping_latesttip = (CheckBox) findViewById(R.id.settings_shipping_latesttip);
		settings_order_latesttip = (CheckBox) findViewById(R.id.settings_order_latesttip);
		settings_topic_latesttip = (CheckBox) findViewById(R.id.settings_topic_latesttip);
		settings_goodswarning_latesttip = (CheckBox) findViewById(R.id.settings_goodswarning_latesttip);
		settings_activity_latesttip = (CheckBox) findViewById(R.id.settings_activity_latesttip);
		settings_ll = (LinearLayout) findViewById(R.id.settings_ll);
		settings_receivemsg.setOnClickListener(this);
		settings_shipping_latesttip.setOnClickListener(this);
		settings_order_latesttip.setOnClickListener(this);
		settings_topic_latesttip.setOnClickListener(this);
		settings_goodswarning_latesttip.setOnClickListener(this);
		settings_activity_latesttip.setOnClickListener(this);
		SettingsConfig config = new SettingsConfig(SettingInformType.this);
		settings_receivemsg.setChecked(config.isReceiveMsgSwitch());
		if (config.isReceiveMsgSwitch()) {
			settings_ll.setVisibility(View.VISIBLE);
		} else {
			settings_ll.setVisibility(View.GONE);
		}
		settings_shipping_latesttip.setChecked(config.isReceiveShippingMsg());
		settings_order_latesttip.setChecked(config.isReceiveOrderMsg());
		settings_topic_latesttip.setChecked(config.isReceiveTopicMsg());
		settings_goodswarning_latesttip.setChecked(config
				.isReceiveGoodsWarnningMsg());
		settings_activity_latesttip.setChecked(config.isReceiveActivityMsg());
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.SettingInform");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {

			case R.id.settings_receivemsg :
				if (settings_receivemsg.isChecked()) {
					settings_ll.setVisibility(View.VISIBLE);
				} else {
					settings_ll.setVisibility(View.GONE);
				}
				config.setReceiveMsgSwitch(settings_receivemsg.isChecked());
				break;
			case R.id.settings_shipping_latesttip :
				config.setReceiveShippingMsg(settings_shipping_latesttip
						.isChecked());
				break;
			case R.id.settings_order_latesttip :
				config.setReceiveOrderMsg(settings_order_latesttip.isChecked());
				break;
			case R.id.settings_topic_latesttip :
				config.setReceiveTopicMsg(settings_topic_latesttip.isChecked());
				break;
			case R.id.settings_goodswarning_latesttip :
				config.setReceiveGoodsWarnningMsg(settings_goodswarning_latesttip
						.isChecked());
				break;
			case R.id.settings_activity_latesttip :
				config.setReceiveActivityMsg(settings_activity_latesttip
						.isChecked());
				break;
			default :
				break;
		}

	}
}
