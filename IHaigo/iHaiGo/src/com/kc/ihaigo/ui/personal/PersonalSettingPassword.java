/**
 * @Title: PersonalSettingPassword.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月12日 下午3:54:36

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.shipping.ShippingActivity;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.ui.topic.TopicDetailActivity;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: PersonalSettingPassword
 * @Description: 进入设置登录密码页面
 * @author: helen.yang
 * @date: 2014年7月12日 下午3:54:36
 * 
 */

public class PersonalSettingPassword extends IHaiGoActivity
		implements
			OnClickListener {

	private TextView userlogin_next;
	private EditText et_msg_login;
	private String TAG = "PersonalSettingPassword";
	private String msgCode;
	private TextView tv_time_flag;
	private String extra;

	/**
	 * 时间倒计时
	 */
	private MyCount mc;
	private String flag;
	private String topicid;
	private String type;
	private String pid;
	private String topicNickName;
	private String tag;

	private IHaiGoGroupActivity lparentGroupActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_userlogin_setting_password_first);
		initTitle();
		initComponents();

	}

	/*
	 * <p>Title: onResume</p> <p>Description: </p>
	 * 
	 * @see android.app.Activity#onResume()
	 */

	@Override
	protected void onResume() {
		super.onResume();
		userlogin_next.setEnabled(true);
	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		userlogin_next = (TextView) findViewById(R.id.userlogin_tv_affirm);
		userlogin_next.setOnClickListener(this);
		et_msg_login = (EditText) findViewById(R.id.et_setting_msg_login);
		tv_time_flag = (TextView) findViewById(R.id.tv_time_flag);
		tv_time_flag.setOnClickListener(this);
		mc = new MyCount(60000, 1000);
		mc.start();
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
		if(PersonalLoginActivity.class.equals(parentClass)){
			lparentGroupActivity = parentGroupActivity;
			extra = getIntent().getStringExtra("key");
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
			type = getIntent().getStringExtra("type");
			if("2".equals(type)){
				pid = getIntent().getStringExtra("pid");
				topicNickName = getIntent().getStringExtra("nickName");
			}
			tag = getIntent().getStringExtra("tag");
		} else if (TopicDetailActivity.class.equals(parentClass)) {
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
		}else if(ShippingActivity.class.equals(parentClass)){
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(PersonalSettingPassword.this,
						PersonalLoginActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.tv_time_flag :
				getMsgCode();
			case R.id.userlogin_tv_affirm :
				userlogin_next.setEnabled(false);
				msgCode = et_msg_login.getText().toString();
				checkMsgCode();
				Utils.hideInputMethod(PersonalSettingPassword.this);
				break;
			default :
				break;
		}
	}

	/**
	 * 
	 * @Title: checkMsgCode
	 * @user: helen.yang
	 * @Description:检查验证码 void
	 * @throws
	 */
	private void checkMsgCode() {
		final String url = Constants.CHECKMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", extra);
		map.put("msgType", "2");
		map.put("msgCode", msgCode);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent;
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++验证码" + result);
							try {
								JSONObject json = new JSONObject(result);
								String st = json.getString("status");
								Log.i(TAG, "/-----" + st);
								if ("-200".equals(st)) {
									Toast.makeText(
											PersonalSettingPassword.this,
											"验证成功", 0).show();
									
									if("TopicResponse".equals(flag)){
										intent = new Intent(
												PersonalSettingPassword.this,
												PersonalConfirmSettingPassword.class);
										intent.putExtra("key", extra);
										intent.putExtra("flag", flag);
										intent.putExtra("topicid", topicid);
										intent.putExtra("type", type);
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
										
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}if("TopicPublishActivity".equals(flag)){
										intent = new Intent(
												PersonalSettingPassword.this,
												PersonalConfirmSettingPassword.class);
										intent.putExtra("key", extra);
										intent.putExtra("flag", flag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}
									else if("ShippingActivity".equals(parentClass)){
										intent = new Intent(
												PersonalSettingPassword.this,
												PersonalConfirmSettingPassword.class);
										intent.putExtra("key", extra);
										intent.putExtra("flag", flag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										
										ShippingGroupActiviy.group.startiHaiGoActivity(intent);
									}else if("PersonalGoodsDetailsActivity".equals(flag)){
										intent = new Intent(
												PersonalSettingPassword.this,
												PersonalConfirmSettingPassword.class);
										intent.putExtra("key", extra);
										intent.putExtra("flag", flag);
										intent.putExtra("tag", tag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("PersonalPublishEvaluationActivity".equals(flag)){
										intent = new Intent(
												PersonalSettingPassword.this,
												PersonalConfirmSettingPassword.class);
										intent.putExtra("key", extra);
										intent.putExtra("flag", flag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("SettingOpinion".equals(flag)){
										intent = new Intent(
												PersonalSettingPassword.this,
												PersonalConfirmSettingPassword.class);
										intent.putExtra("key", extra);
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
									else{
										intent = new Intent(
												PersonalSettingPassword.this,
												PersonalConfirmSettingPassword.class);
										intent.putExtra("key", extra);
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
								} else if ("-500".equals(st)) {
									Toast.makeText(
											PersonalSettingPassword.this,
											"验证失败", 0).show();
								} else if ("-501".equals(st)) {
									Toast.makeText(
											PersonalSettingPassword.this,
											"验证码输入错误", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(
											PersonalSettingPassword.this,
											"验证码已过期", 0).show();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i(TAG, "*****************验证码" + result);
						}
					}
				}, 1);
	}

	/* 定义一个倒计时的内部类 */
	class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void onTick(long millisUntilFinished) {
			tv_time_flag.setEnabled(false);
			findViewById(R.id.tv_again_msg).setVisibility(View.VISIBLE);
			tv_time_flag.setText(" " + millisUntilFinished / 1000);
			tv_time_flag.setTextColor(R.color.personal_blueBg);
		}

		@Override
		public void onFinish() {
			tv_time_flag.setEnabled(true);
			findViewById(R.id.tv_again_msg).setVisibility(View.GONE);
			tv_time_flag.setText("重新发送验证码");
			tv_time_flag.setTextColor(Color.BLACK);
		}
	}

	/**
	 * 
	 * @Title: getMsgCode
	 * @user: helen.yang
	 * @Description: 获取验证码——找回密码再次获取验证码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", extra);
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
									ToastUtil.showShort(
											PersonalSettingPassword.this,
											"发送成功");
								} else if ("0".equals(st)) {
									Toast.makeText(
											PersonalSettingPassword.this, "失败",
											0).show();
								} else if ("-503".equals(st)) {
									Toast.makeText(
											PersonalSettingPassword.this,
											"没有此用户", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(
											PersonalSettingPassword.this,
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
