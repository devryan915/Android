/**
 * @Title: PersonalAffirmSettingPassword.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月14日 上午9:48:44

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: PersonalAffirmSettingPassword
 * @Description: 确认设置登录密码页面
 * @author: helen.yang
 * @date: 2014年7月14日 上午9:48:44
 * 
 */

public class PersonalConfirmSettingPassword extends IHaiGoActivity implements
		OnClickListener {

	private EditText et_re_login_psd;
	private EditText et_login_psd;
	private String enter_psd;
	private String enter_re_psd;
	private String TAG = "PersonalConfirmSettingPassword";
	private String userName;
	private String flag;
	private String topicid;
	private String type;
	private String pid;
	private String topicNickName;
	private IHaiGoGroupActivity lparentGroupActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_userlogin_password_confirm);
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
		findViewById(R.id.userlogin_tv_confirm).setOnClickListener(this);
		et_login_psd = (EditText) findViewById(R.id.et_login_psd);
		et_re_login_psd = (EditText) findViewById(R.id.et_re_login_psd);
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
		String which = getIntent().getStringExtra("which");
		if (PersonalSettingPassword.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			userName = getIntent().getStringExtra("key");
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
			type = getIntent().getStringExtra("type");
			if ("2".equals(type)) {
				pid = getIntent().getStringExtra("pid");
				topicNickName = getIntent().getStringExtra("nickName");
			}
		} else if (TopicDetailActivity.class.equals(parentClass)) {
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
		} else if (ShippingActivity.class.equals(parentClass)) {
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(PersonalConfirmSettingPassword.this,
					PersonalSettingPassword.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.tv_again_msg:
			break;
		case R.id.userlogin_tv_confirm:
			enter_psd = et_login_psd.getText().toString().trim();
			enter_re_psd = et_re_login_psd.getText().toString().trim();
			if ("".equals(enter_psd)) {
				ToastUtil.showShort(getApplicationContext(), "请输入密码");
			} else if (enter_psd.length() < 6) {
				ToastUtil.showShort(this, "密码至少六位");
			} else if (CheckUtil.checkHanzi(enter_psd)) {
				ToastUtil.showShort(this, "密码不能为汉字");
			} else if (!enter_psd.equals(enter_re_psd)) {
				ToastUtil.showShort(this, "密码输入不一致");
			} else {
				resetPassword();
			}
			Utils.hideInputMethod(PersonalConfirmSettingPassword.this);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: resetPassword
	 * @user: helen.yang
	 * @Description: 用户设置密码
	 * @throws
	 */
	private void resetPassword() {
		final String url = Constants.RESETPASSWORD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", userName);
		map.put("password", enter_psd);
		map.put("userToken", "");
		map.put("udid", "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++设置登录密码" + result);
							try {
								JSONObject json = new JSONObject(result);
								int userid = json.getInt("userId");
								String uid = String.valueOf(userid);
								Log.i(TAG, "/+++++++++" + Constants.USER_ID);
								if ("0".equals(Constants.USER_ID)) {
									Toast.makeText(
											PersonalConfirmSettingPassword.this,
											"设置密码失败", 0).show();

								} else {
									Constants.USER_ID = uid;
									DialogUtil.showSettingPsdDialog(
											PersonalConfirmSettingPassword.this
													.getParent(),
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
														if ("TopicResponse"
																.equals(flag)) {
															intent.setClass(
																	PersonalConfirmSettingPassword.this,
																	TopicResponse.class);
															intent.putExtra("mobile",userName);
															intent.putExtra("topicid",topicid);
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
																	Constants.LOG_STATUS,
																	true);
															sendBroadcast(intent);
															TopicGroupActivity.group
																	.startiHaiGoActivity(intent);
														} if ("TopicPublishActivity"
																.equals(flag)) {
															intent.setClass(
																	PersonalConfirmSettingPassword.this,
																	TopicPublishActivity.class);
															intent.putExtra("mobile",userName);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
																	true);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
																	false);
															intent.setAction(Constants.LOGON_LOGOUT_ACTION);
															intent.putExtra(
																	Constants.LOG_STATUS,
																	true);
															sendBroadcast(intent);
															TopicGroupActivity.group
																	.startiHaiGoActivity(intent);
														} 
														else if ("ShippingActivity".equals(flag)) {
															intent.setClass(
																	PersonalConfirmSettingPassword.this,
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
														}else if("PersonalGoodsDetailsActivity".equals(flag)){
															intent.setClass(PersonalConfirmSettingPassword.this,
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
														}
														else if("PersonalPublishEvaluationActivity".equals(flag)){
															intent.setClass(PersonalConfirmSettingPassword.this,
																	PersonalPublishEvaluationActivity.class);
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
														}else if("SettingOpinion".equals(flag)){
															intent.setClass(
																	PersonalConfirmSettingPassword.this,
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
																	PersonalConfirmSettingPassword.this,
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
														// }
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
							Log.i(TAG, "*****************设置登录密码" + result);
						}
					}
				}, 1);
	}

}
