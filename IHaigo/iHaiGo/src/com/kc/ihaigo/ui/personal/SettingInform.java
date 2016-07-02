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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.SettingsConfig;

/**
 * @ClassName: SettingInform
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月18日 下午3:38:17
 * 
 */

public class SettingInform extends IHaiGoActivity {
	private SettingsConfig config;
	private CheckBox settings_voice;
	private CheckBox settings_shake;
	private CheckBox settings_flash;
	private TextView infortime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_inform);
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
		findViewById(R.id.informtype).setOnClickListener(this);
		config = new SettingsConfig(SettingInform.this);
		boolean IsReceiveMsg = config.isReceiveMsgSwitch();
		if (IsReceiveMsg) {
			((TextView) findViewById(R.id.setinfo_receive_status))
					.setText(R.string.inform_start);
		} else {
			((TextView) findViewById(R.id.setinfo_receive_status))
					.setText(R.string.close);
		}
		findViewById(R.id.settings_seltime).setOnClickListener(this);
		settings_voice = (CheckBox) findViewById(R.id.settings_voice);
		settings_voice.setOnClickListener(this);
		settings_shake = (CheckBox) findViewById(R.id.settings_shake);
		settings_shake.setOnClickListener(this);
		settings_flash = (CheckBox) findViewById(R.id.settings_flash);
		settings_flash.setOnClickListener(this);
		infortime = (TextView) findViewById(R.id.infortime);
		infortime.setText(config.getStartTime() + "~" + config.getEndTime());
	}
	@Override
	public void refresh() {
		super.refresh();
		boolean IsReceiveMsg = config.isReceiveMsgSwitch();
		if (IsReceiveMsg) {
			((TextView) findViewById(R.id.setinfo_receive_status))
					.setText(R.string.inform_start);
		} else {
			((TextView) findViewById(R.id.setinfo_receive_status))
					.setText(R.string.close);
		}
		saveConfig();
	}
	private void saveConfig() {
		String url = Constants.MESSAGE_URL + Constants.USER_ID + "/update";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("type", (config.isReceiveMsgSwitch() ? 1 : 0) + "");
		reqParams.put("waybill", (config.isReceiveShippingMsg() ? 1 : 0) + "");
		reqParams.put("orders", (config.isReceiveOrderMsg() ? 1 : 0) + "");
		reqParams.put("topic", (config.isReceiveTopicMsg() ? 1 : 0) + "");
		reqParams
				.put("good", (config.isReceiveGoodsWarnningMsg() ? 1 : 0) + "");
		reqParams.put("activity", (config.isReceiveActivityMsg() ? 1 : 0) + "");
		reqParams.put("startTime", config.getStartTime());
		reqParams.put("endTime", config.getStartTime());
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								if ("1".equals(resData.getString("code"))) {
									Toast.makeText(SettingInform.this, result,
											Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
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

			case R.id.informtype :
				intent.setClass(SettingInform.this, SettingInformType.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.settings_seltime :
				DialogUtil.showSettiingTimeDialog(
						SettingInform.this.getParent(), new BackCall() {

							@Override
							public void deal(int which, Object... obj) {
								((Dialog) obj[0]).dismiss();
								switch (which) {
									case R.id.addr_complete :
										infortime
												.setText(obj[1] + "~" + obj[2]);
										config.setStartTime(obj[1].toString());
										config.setEndTime(obj[2].toString());
										saveConfig();
										break;
									default :
										break;
								}
							}
						}).show();
				break;
			case R.id.settings_voice :
				config.setReceiveByVoice(settings_voice.isChecked());
				break;
			case R.id.settings_shake :
				config.setReceiveByShake(settings_shake.isChecked());
				break;
			case R.id.settings_flash :
				config.setReceiveByFlash(settings_flash.isChecked());
				break;
			default :
				break;
		}
	}
}
