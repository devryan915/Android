/**
 * @Title: SettingOpinion.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月18日 下午4:45:26

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
import android.widget.EditText;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.ui.topic.TopicResponse;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: SettingOpinion
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月18日 下午4:45:26
 * 
 */

public class SettingOpinion extends IHaiGoActivity {
	private Dialog inforDialog;
	private EditText content;
	// private BackCall call;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_opinion);
		initTitle();
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		// call = new BackCall() {
		// @Override
		// public void deal(int which, Object... obj) {
		// }
		// };
		inforDialog = DialogUtil.showInfoDialog(
				SettingOpinion.this.getParent(), null);
		content = (EditText) findViewById(R.id.content);
	}
	
	
	@Override
	public void refresh() {
		super.refresh();
		if(SettingsActivity.class.equals(parentClass)){
			content.setText("");
		}
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
	private void commitOpinion() {
		String url = Constants.VERSION_URL + "insertActive";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("userId", Constants.USER_ID);
		reqParams.put("content", ((EditText) findViewById(R.id.content))
				.getText().toString());
		// reqParams.put("contact", "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								if ("1".equals(resData.getString("code"))) {
									((TextView) inforDialog
											.findViewById(R.id.content))
											.setText(R.string.reback_ihaigo);
									Intent intent = new Intent();
									intent.setClass(SettingOpinion.this,
											SettingsActivity.class);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0, R.string.loading);
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_right :
				if(checkLogin()){
					Utils.hideInputMethod(currentActivity);
					commitOpinion();
				}else{
					jumpLogin();
				}
				
				break;
			default :
				break;
		}

	}
	
	public void jumpLogin() {
		Intent intent = new Intent();
		intent.setClass(SettingOpinion.this, PersonalLoginActivity.class);
		intent.putExtra("code", "");
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		PersonalGroupActivity.group.startiHaiGoActivity(intent);
	}

}
