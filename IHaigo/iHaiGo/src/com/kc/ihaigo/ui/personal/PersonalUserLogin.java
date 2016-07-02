/**
 * @Title: PersonalUserLogin.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月12日 下午3:51:24

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
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.shipping.ShippingActivity;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.ui.topic.TopicDetailActivity;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.ui.topic.TopicPublishActivity;
import com.kc.ihaigo.ui.topic.TopicResponse;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: PersonalUserLogin
 * @Description: 进入用户登录操作
 * @author: helen.yang
 * @date: 2014年7月12日 下午3:51:24
 * 
 */

public class PersonalUserLogin extends IHaiGoActivity {

	private EditText et_msg_login;
	private String TAG = "PersonalUserLogin";
	private String enter_user_password;
	private String userName;
	private String flag;
	private String topicid;
	private String type;
	private IHaiGoGroupActivity lparentGroupActivity;
	private String pid;
	private String topicNickName;
	private String tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_userlogin_password);
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
		findViewById(R.id.tv_find_psd).setOnClickListener(this);
		findViewById(R.id.userlogin_tv_affirm).setOnClickListener(this);
		et_msg_login = (EditText) findViewById(R.id.et_msg_login);
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
	}

	@Override
	public void refresh() {
		super.refresh();
		if (PersonalLoginActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			userName = getIntent().getStringExtra("key");
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
			type = getIntent().getStringExtra("type");
			if("2".equals(type)){
				pid = getIntent().getStringExtra("pid");
				topicNickName = getIntent().getStringExtra("nickName");
			}
			tag = getIntent().getStringExtra("tag");
		} else if (PersonalConfirmFindPassword.class.equals(parentClass)) {
			userName = getIntent().getStringExtra("key");
		}else if(ShippingActivity.class.equals(parentClass)){
			flag = getIntent().getStringExtra("flag");
		}else if(TopicResponse.class.equals(parentClass)){
			lparentGroupActivity  = parentGroupActivity;
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
			type = getIntent().getStringExtra("type");
			if("2".equals(type)){
				pid = getIntent().getStringExtra("pid");
			}
		}else if(PersonalGoodsDetailsActivity.class.equals(parentClass)){
			flag = getIntent().getStringExtra("flag");
		}
	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalLoginActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_again_msg:
			break;
		case R.id.tv_find_psd:
			getMsgCode();
			break;
		case R.id.userlogin_tv_affirm:
			enter_user_password = et_msg_login.getText().toString();
			loginUser();
			Utils.hideInputMethod(PersonalUserLogin.this);
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: loginUser
	 * @user: helen.yang
	 * @Description: 用户登录 void
	 * @throws
	 */
	private void loginUser() {
		final String url = Constants.LOGINUSER_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userName", userName);
		map.put("password", enter_user_password);
		map.put("userToken", "");
		map.put("udid", "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject json = new JSONObject(result);
								int userid = json.getInt("userId");
								String uid = String.valueOf(userid);
								Log.i(TAG, "/-----" + uid);
								if ("0".equals(uid)) {
									ToastUtil.showLocation(
											PersonalUserLogin.this,
											"登录失败，请重新登录！");
								} else {
									Constants.USER_ID = uid;
									if ("TopicResponse".equals(flag)) {
										intent.setClass(
												PersonalUserLogin.this,
												TopicResponse.class);
										intent.putExtra("mobile", userName);
										intent.putExtra("topicid", topicid);
										intent.putExtra("type", type);
										if("2".equals(type)){
											intent.putExtra("pid", pid);
											intent.putExtra("nickName", topicNickName);
										}
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(
												Constants.LOG_STATUS, true);
										sendBroadcast(intent);
										TopicGroupActivity.group
												.startiHaiGoActivity(intent);
									}if ("TopicPublishActivity".equals(flag)) {
										intent.setClass(
												PersonalUserLogin.this,
												TopicPublishActivity.class);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(
												Constants.LOG_STATUS, true);
										sendBroadcast(intent);
										TopicGroupActivity.group
												.startiHaiGoActivity(intent);
									}
									else if ("ShippingActivity".equals(flag)) {
										intent.setClass(PersonalUserLogin.this,
												ShippingActivity.class);
										intent.putExtra("mobile", userName);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												true);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(Constants.LOG_STATUS,
												true);
										sendBroadcast(intent);
										ShippingGroupActiviy.group
												.startiHaiGoActivity(
														intent);
									} else if("PersonalGoodsDetailsActivity".equals(flag)){
										intent.setClass(PersonalUserLogin.this,
												PersonalGoodsDetailsActivity.class);
										intent.putExtra("mobile", userName);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(Constants.LOG_STATUS,
												true);
										sendBroadcast(intent);
										lparentGroupActivity
												.startiHaiGoActivity(
														intent);
									}else if("PersonalPublishEvaluationActivity".equals(flag)){
										intent.setClass(PersonalUserLogin.this,
												PersonalPublishEvaluationActivity.class);
										intent.putExtra("mobile", userName);
										intent.putExtra("tag", tag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(Constants.LOG_STATUS,
												true);
										sendBroadcast(intent);
										lparentGroupActivity
												.startiHaiGoActivity(
														intent);
									}else if("SettingOpinion".equals(flag)){
										intent.setClass(
												PersonalUserLogin.this,
												SettingOpinion.class);
										intent.putExtra("mobile", userName);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												true);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(
												Constants.LOG_STATUS, true);
										sendBroadcast(intent);
										PersonalGroupActivity.group
												.startiHaiGoActivity(intent);
									}
									else {
										intent.setClass(
												PersonalUserLogin.this,
												PersonalActivity.class);
										intent.putExtra("mobile", userName);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												true);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(
												Constants.LOG_STATUS, true);
										sendBroadcast(intent);
										PersonalGroupActivity.group
												.startiHaiGoActivity(intent);
									}
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

	/**
	 * 
	 * @Title: getMsgCode
	 * @user: helen.yang
	 * @Description: 获取验证码——为找回密码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", userName);
		map.put("msgType", "3");

		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject json = new JSONObject(result);
								String st = json.getString("status");
								Log.i(TAG, "/-----" + st);
								if ("-200".equals(st)) {
									
									if("TopicResponse".equals(flag)){
										intent.setClass(PersonalUserLogin.this,
												PersonalFindPassword.class);
										intent.putExtra("key", userName);
										intent.putExtra("flag", flag);
										intent.putExtra("topicid", topicid);
										intent.putExtra("type", type);
										if("2".equals(type)){
											intent.putExtra("pid", pid);
										}
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}if("TopicPublishActivity".equals(flag)){
										intent.setClass(PersonalUserLogin.this,
												PersonalFindPassword.class);
										intent.putExtra("key", userName);
										intent.putExtra("flag", flag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}
									else if("ShippingActivity".equals(flag)){
										intent.setClass(PersonalUserLogin.this,
												PersonalFindPassword.class);
										intent.putExtra("key", userName);
										intent.putExtra("flag", flag);
								
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										ShippingGroupActiviy.group.startiHaiGoActivity(intent);
									}else if("PersonalGoodsDetailsActivity".equals(flag)){
										intent.setClass(PersonalUserLogin.this,
												PersonalFindPassword.class);
										intent.putExtra("key", userName);
										intent.putExtra("flag", flag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("PersonalPublishEvaluationActivity".equals(flag)){
										intent.setClass(PersonalUserLogin.this,
												PersonalFindPassword.class);
										intent.putExtra("key", userName);
										intent.putExtra("flag", flag);
										intent.putExtra("tag", tag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("SettingOpinion".equals(flag)){
										intent.setClass(
												PersonalUserLogin.this,
												PersonalFindPassword.class);
										intent.putExtra("mobile", userName);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												true);
										intent.setAction(Constants.LOGON_LOGOUT_ACTION);
										intent.putExtra(
												Constants.LOG_STATUS, true);
										sendBroadcast(intent);
										PersonalGroupActivity.group
												.startiHaiGoActivity(intent);
									}
									else{
										intent.setClass(PersonalUserLogin.this,
												PersonalFindPassword.class);
										intent.putExtra("key", userName);
										intent.putExtra("flag", flag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										PersonalGroupActivity.group
										.startiHaiGoActivity(intent);
									}
								} else if ("0".equals(st)) {
									Toast.makeText(PersonalUserLogin.this,
											"失败", 0).show();
								} else if ("-503".equals(st)) {
									Toast.makeText(PersonalUserLogin.this,
											"没有此用户", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(PersonalUserLogin.this,
											"该用户已存在", 0).show();
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
