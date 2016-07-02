/**
 * @Title: PersonalFirstLogin.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月11日 上午11:47:58

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
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
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: PersonalFirstLogin
 * @Description: 进入注册页面
 * @author: helen.yang
 * @date: 2014年7月11日 上午11:47:58
 * 
 */

public class PersonalFirstLogin extends IHaiGoActivity implements
		OnClickListener {

	private EditText enter_login_msg;

	private TextView tv_time_flag;
	private TextView userlogin_next;
	private String msgCode;
	private String code;
	private String userName;
	private static String TAG = "PersonalFirstLogin";
	/**
	 * 时间倒计时
	 */
	private MyCount mc;

	private String channel;
	private Object myChannel;

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

		setContentView(R.layout.personal_first_userlogin);
		initTitle();
		initComponents();

		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(),
					PackageManager.GET_CONFIGURATIONS
							| PackageManager.GET_META_DATA);
			myChannel = info.applicationInfo.metaData.get("channel");
			channel = myChannel.toString();
			Log.i(TAG, myChannel.toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		if (PersonalLoginActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			userName = getIntent().getStringExtra("key");
			code = getIntent().getStringExtra("code");
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
		} else if (ShippingActivity.class.equals(parentClass)) {
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
		}

		mc = new MyCount(60000, 1000);
		mc.start();
	}

	@Override
	protected void back() {
		super.back();
	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		enter_login_msg = (EditText) findViewById(R.id.et_msg_login);
		tv_time_flag = (TextView) findViewById(R.id.tv_time_flag);
		findViewById(R.id.tv_again_msg).setOnClickListener(this);
		findViewById(R.id.userlogin_first_tv_confirm).setOnClickListener(this);

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userlogin_first_tv_confirm:
//			Toast.makeText(this, "弹框确认", 0).show();
			msgCode = enter_login_msg.getText().toString();
			regUser();
			Utils.hideInputMethod(PersonalFirstLogin.this);
			break;
		case R.id.title_left:
			Intent intent = new Intent(PersonalFirstLogin.this,
					PersonalLoginActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.tv_time_flag:
			getMsgCode();
			break;
		default:
			break;
		}
	}

	/* 定义一个倒计时的内部类 */
	class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv_time_flag.setEnabled(false);
			findViewById(R.id.tv_again_msg).setVisibility(View.VISIBLE);
			tv_time_flag.setText(" " + millisUntilFinished / 1000);
		}

		@Override
		public void onFinish() {
			tv_time_flag.setEnabled(true);
			findViewById(R.id.tv_again_msg).setVisibility(View.GONE);
			tv_time_flag.setText("重发验证码");

		}
	}

	/**
	 * 
	 * @Title: regUser
	 * @user: helen.yang
	 * @Description: 注册 void
	 * @throws
	 */
	private void regUser() {
		final String url = Constants.REGUSER_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", userName);
		map.put("msgType", "1");
		map.put("msgCode", msgCode);
		map.put("code", code);
		map.put("channel", channel);
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

								if (("-501").equals(uid)) {
									Toast.makeText(PersonalFirstLogin.this,
											"验证码错误", 0).show();
								} else if (("0").equals(uid)) {
									Toast.makeText(PersonalFirstLogin.this,
											"失败", 0).show();
								} else if (("-505").equals(uid)) {
									Toast.makeText(PersonalFirstLogin.this,
											"验证码过期", 0).show();
								} else {
									Constants.USER_ID = uid;

									DialogUtil.showLoginAffirmDialog(
											PersonalFirstLogin.this.getParent(),
											new BackCall() {
												Intent intent = new Intent();

												@Override
												public void deal(
														int whichButton,
														Object... obj) {
													switch (whichButton) {
													case R.id.exit_oks:
														((Dialog) obj[0])
																.dismiss();

														if ("TopicResponse".equals(flag)) {
															intent.setClass(
																	PersonalFirstLogin.this,
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
														} else if ("TopicPublishActivity".equals(flag)) {
															intent.setClass(
																	PersonalFirstLogin.this,
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
															intent.setClass(
																	PersonalFirstLogin.this,
																	ShippingActivity.class);
															intent.putExtra("mobile",userName);
															intent.putExtra("which","PersonalUserLogin");
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
																	true);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
																	true);
															intent.setAction(Constants.LOGON_LOGOUT_ACTION);
															intent.putExtra(
																	Constants.LOG_STATUS,
																	true);
															sendBroadcast(intent);
															ShippingGroupActiviy.group
																	.startiHaiGoActivity(intent);
														} else if("PersonalGoodsDetailsActivity".equals(flag)){
															intent.setClass(PersonalFirstLogin.this,
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
															intent.setClass(PersonalFirstLogin.this,
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
																	PersonalFirstLogin.this,
																	SettingOpinion.class);
															intent.putExtra("mobile",userName);
															intent.putExtra("which","PersonalUserLogin");
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
																	true);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
																	true);
															intent.setAction(Constants.LOGON_LOGOUT_ACTION);
															intent.putExtra(
																	Constants.LOG_STATUS,
																	true);
															sendBroadcast(intent);
															PersonalGroupActivity.group
																	.startiHaiGoActivity(intent);
														}
														else {
															intent.setClass(
																	PersonalFirstLogin.this,
																	PersonalActivity.class);
															intent.putExtra("mobile",userName);
															intent.putExtra("which","PersonalUserLogin");
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
																	true);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
																	true);
															intent.setAction(Constants.LOGON_LOGOUT_ACTION);
															intent.putExtra(
																	Constants.LOG_STATUS,
																	true);
															sendBroadcast(intent);
															PersonalGroupActivity.group
																	.startiHaiGoActivity(intent);
														}
														break;

													default:
														break;
													}
												}
											}, null);
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
	 * @Description: 获取验证码——注册再次获取验证码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", userName);
		map.put("msgType", "1");

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
											PersonalFirstLogin.this, "发送成功");
								} else if ("0".equals(st)) {
									Toast.makeText(PersonalFirstLogin.this,
											"失败", 0).show();
								} else if ("-503".equals(st)) {
									Toast.makeText(PersonalFirstLogin.this,
											"没有此用户", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(PersonalFirstLogin.this,
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
