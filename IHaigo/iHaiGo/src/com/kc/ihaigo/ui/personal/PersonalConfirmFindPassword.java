/**
 * @Title: PersonalConfirmFindPassword.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月14日 上午10:18:22

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
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.shipping.ShippingActivity;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.ui.topic.TopicResponse;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: PersonalConfirmFindPassword
 * @Description: 确认找回密码页面
 * @author: helen.yang
 * @date: 2014年7月14日 上午10:18:22
 * 
 */

public class PersonalConfirmFindPassword extends IHaiGoActivity {
	private TextView tv_time_flag;
	private String extra;
	private EditText enter_forg_pasd;
	private EditText re_enter_forg_pasd;
	private String enter_psd;
	private String re_enter_psd;
	private String TAG = "PersonalConfirmFindPassword";
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

		setContentView(R.layout.personal_find_password_confirm);
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
		tv_time_flag = (TextView) findViewById(R.id.tv_time_flag);
		enter_forg_pasd = (EditText) findViewById(R.id.enter_forg_pasd);
		re_enter_forg_pasd = (EditText) findViewById(R.id.re_enter_forg_pasd);
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
		if (PersonalFindPassword.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			extra = getIntent().getStringExtra("key");
			flag = getIntent().getStringExtra("flag");
			topicid = getIntent().getStringExtra("topicid");
			type = getIntent().getStringExtra("type");
			if ("2".equals(type)) {
				pid = getIntent().getStringExtra("pid");
				topicNickName = getIntent().getStringExtra("nickName");
			}
			tag = getIntent().getStringExtra("tag");
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(PersonalConfirmFindPassword.this,
					PersonalFindPassword.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.tv_again_msg:
			break;
		case R.id.userlogin_tv_confirm:
			enter_psd = enter_forg_pasd.getText().toString().trim();
			re_enter_psd = re_enter_forg_pasd.getText().toString().trim();
			if ("".equals(enter_psd)) {
				ToastUtil.showShort(getApplicationContext(), "请输入密码");
			} else if (enter_psd.length() < 6) {
				ToastUtil.showShort(this, "密码至少六位");
			} else if (CheckUtil.checkHanzi(enter_psd)) {
				ToastUtil.showShort(this, "密码不能为汉字");
			} else if (!enter_psd.equals(re_enter_psd)) {
				ToastUtil.showShort(this, "密码输入不一致");
			} else {
				forgetPwd();
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: forgetPwd
	 * @user: helen.yang
	 * @Description: 找回密码 void
	 * @throws
	 */
	private void forgetPwd() {
		final String url = Constants.FORGETPWD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		extra = getIntent().getStringExtra("key");
		map.put("mobile", extra);
		map.put("password", enter_psd);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++验证码" + result);
							try {
								JSONObject json = new JSONObject(result);
								String st = json.getString("status");
								Log.i(TAG, "/-----" + st);
								if ("-200".equals(st)) {
									Toast.makeText(
											PersonalConfirmFindPassword.this,
											"设置密码成功", 0).show();
									DialogUtil.showEditPsdDialog(
											PersonalConfirmFindPassword.this
													.getParent(),
											new BackCall() {

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
															Intent intent = new Intent(
																	PersonalConfirmFindPassword.this,
																	PersonalUserLogin.class);
															intent.putExtra("key",extra);
															intent.putExtra("password",enter_psd);
															intent.putExtra("topicid",topicid);
															intent.putExtra("type",type);
															if ("2".equals(type)) {
																intent.putExtra("pid",pid);
																intent.putExtra("nickName",topicNickName);
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
															Intent intent = new Intent(
																	PersonalConfirmFindPassword.this,
																	PersonalUserLogin.class);
															intent.putExtra("key",extra);
															intent.putExtra("password",enter_psd);
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
														else if ("ShippingActivity"
																.equals(flag)) {
															Intent intent = new Intent(
																	PersonalConfirmFindPassword.this,
																	PersonalUserLogin.class);
															intent.putExtra("key",extra);
															intent.putExtra("password",enter_psd);
															intent.putExtra("which","PersonalUserLogin");
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
															ShippingGroupActiviy.group
																	.startiHaiGoActivity(intent);
														} else if ("PersonalGoodsDetailsActivity"
																.equals(flag)) {
															Intent intent = new Intent(
																	PersonalConfirmFindPassword.this,
																	PersonalUserLogin.class);
															intent.putExtra("key",extra);
															intent.putExtra("password",enter_psd);
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
															lparentGroupActivity
																	.startiHaiGoActivity(intent);
														}else if ("PersonalPublishEvaluationActivity"
																.equals(flag)) {
															Intent intent = new Intent(
																	PersonalConfirmFindPassword.this,
																	PersonalUserLogin.class);
															intent.putExtra("key",extra);
															intent.putExtra("password",enter_psd);
															intent.putExtra("tag", tag);
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
															lparentGroupActivity
																	.startiHaiGoActivity(intent);
														}else if("SettingOpinion".equals(flag)){
															Intent intent = new Intent(
																	PersonalConfirmFindPassword.this,
																	SettingOpinion.class);
															intent.putExtra("key",extra);
															intent.putExtra("password",enter_psd);
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
															Intent intent = new Intent(
																	PersonalConfirmFindPassword.this,
																	PersonalUserLogin.class);
															intent.putExtra("key",extra);
															intent.putExtra("password",enter_psd);
															intent.putExtra("which","PersonalUserLogin");
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
															PersonalGroupActivity.group
																	.startiHaiGoActivity(intent);
														}
														break;

													default:
														break;
													}
												}
											}, null);
								} else if ("-500".equals(st)) {
									Toast.makeText(
											PersonalConfirmFindPassword.this,
											"设置密码失败", 0).show();
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

}
