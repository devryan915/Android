/**
 * @Title: PersonalSelfIntroduction.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月15日 下午1:29:29

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * @ClassName: PersonalSelfIntroduction
 * @Description: 编辑一句话介绍
 * @author: helen.yang
 * @date: 2014年7月15日 下午1:29:29
 * 
 */

public class PersonalSelfIntroduction extends IHaiGoActivity
		implements
			OnClickListener {

	private EditText et_edit_user_introduce;
	private String editintroduce;
	private String sex;
	private String introduce;
	private String nickName;
	private String head_image_url;

	private int code;
	private String TAG = "PersonalSelfIntroduction";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_self_introduction);
		initTitle();
		initComponents();

	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
	}

	
	
	@Override
	public void refresh() {
		super.refresh();
		String which = getIntent().getStringExtra("which");
		if("PersonalEditUserInfo".equals(which)){
			introduce = getIntent().getStringExtra("introduce");
			et_edit_user_introduce.setText(introduce);
			nickName = getIntent().getStringExtra("nickName");
			sex = getIntent().getStringExtra("sex");
			head_image_url = getIntent().getStringExtra("headPortnextrait");
		}
	}

	@Override
	protected void back() {
		resParams = new Bundle();
		resParams.putString("nickName", nickName);
		resParams.putString("introduce", introduce);
		resParams.putString("headPortrait", head_image_url);
		resParams.putString("sex", sex);
		super.back();
	}
	/**
	 * 
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		et_edit_user_introduce = (EditText) findViewById(R.id.et_edit_user_introduce);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(PersonalSelfIntroduction.this,
						PersonalEditUserInfo.class);
				intent.putExtra("introduce", introduce);
				intent.putExtra("nickName", nickName);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;

			case R.id.title_right :
				editintroduce = et_edit_user_introduce.getText().toString();
				updateUser();
				break;
			default :
				break;
		}
	}

	/**
	 * 
	 * @Title: updateUser
	 * @user: helen.yang
	 * @Description: 修改用户信息——一句话介绍 void
	 * @throws
	 */
	private void updateUser() {
		final String url = Constants.UPDATEUSER_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		nickName = getIntent().getStringExtra("nickName");
		sex = getIntent().getStringExtra("sex");
		Log.i("geek", " +++++++++++++++++????????????" + Constants.USER_ID);
		map.put("userId", Constants.USER_ID);
		map.put("nickName", nickName);
		map.put("sex", sex);
		map.put("introduce", editintroduce);
		map.put("headPortnextrait", head_image_url);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject obj = new JSONObject(result);
								code = obj.getInt("code");
								if (code == 1) {
									introduce= editintroduce; 
									intent.setClass(
											PersonalSelfIntroduction.this,
											PersonalEditUserInfo.class);
									intent.putExtra("introduce", editintroduce);
									intent.putExtra("nickName", nickName);
									intent.putExtra("sex", sex);
									intent.putExtra("headPortnextrait", head_image_url);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if (code == 0) {
									Toast.makeText(
											PersonalSelfIntroduction.this,
											"修改信息失败", 0).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i(TAG, "*****************收到信息" + result);
						}
					}
				}, 1);
	}
}
