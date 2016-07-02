/**
 * @Title: PersonalLoginActivity.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月11日 上午10:02:40

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.kc.ihaigo.model.User;
import com.kc.ihaigo.ui.shipping.ShippingActivity;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.ui.topic.TopicActivity;
import com.kc.ihaigo.ui.topic.TopicDetailActivity;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.ui.topic.TopicPublishActivity;
import com.kc.ihaigo.ui.topic.TopicResponse;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.login.QQLogin;
import com.kc.ihaigo.util.login.SinaLogin;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * @ClassName: PersonalLoginActivity
 * @Description: 进入输入手机号码页面
 * @author: helen.yang
 * @date: 2014年7月11日 上午10:02:40
 * 
 */

public class PersonalLoginActivity extends IHaiGoActivity
		implements
			OnClickListener {
	private EditText login_tel;
	private String enter_user_tel;
	private static String TAG = "PersonalLoginActivity";
	private TextView userlogin;
	private String code;
	private SsoHandler mSsoHandler;

	private String image_url;
	private String fastId;// 第三方id
	private String name;
	private String introduce;
	private String udid;
	private Class<IHaiGoActivity> lparentClass;
	private String FLAG;
	private String topicid;
	private IHaiGoGroupActivity lparentGroupActivity;
	private String type;
	private String pid;
	private String topicNickName;
	private String tag;//从PersonalPublishEvaluationActivity传过来的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_personal_userlogin);
		initTitle();
		initComponents();
		
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	public void dealDatas(Object... datas) {
		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (mSsoHandler != null) {
			int requestCode = (Integer) datas[0];
			int resultCode = (Integer) datas[1];
			Intent data = (Intent) datas[2];
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		userlogin = (TextView) findViewById(R.id.userlogin_tv_next);
		userlogin.setEnabled(true);
		userlogin.setOnClickListener(this);
		login_tel = (EditText) findViewById(R.id.personal_login_tv_tel);
		login_tel
				.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		findViewById(R.id.sina_login_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 新浪登录
						mSsoHandler = new SinaLogin().loginSina(
								PersonalLoginActivity.this.getParent(),
								new BackCall() {

									@Override
									public void deal(int which, Object... obj) {
										if (Constants.Debug) {
											Toast.makeText(
													PersonalLoginActivity.this,
													obj[0].toString(), 1000)
													.show();
										}
										// 调用 User#parse 将JSON串解析成User对象
										User user = User.parse(obj[0]
												.toString());
										if (user != null) {

										} else {
											
										}
									}
								});

					}
				});

		findViewById(R.id.qq_login_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// qq登录
						QQLogin.loginQQ(PersonalLoginActivity.this.getParent(),
								new BackCall() {
									@Override
									public void deal(int which, Object... obj) {

										if (Constants.Debug) {
											QQLogin.getUserInfo(
													PersonalLoginActivity.this
															.getParent(),
													new BackCall() {

														@Override
														public void deal(
																int which,
																Object... obj) {
															Log.i("geek",
																	"xixi"
																			+ obj[0].toString());
															try {
																String result = obj[0]
																		.toString();
																JSONObject jsonobj = new JSONObject(
																		result);
																String nickName = jsonobj
																		.getString("nickname");
																String qqimageurl = jsonobj
																		.getString("figureurl_qq_1");
																image_url = qqimageurl;
																name = nickName;
																Log.i("geek",
																		":::::::::::::::"
																				+ nickName
																				+ qqimageurl);
															} catch (JSONException e) {
																e.printStackTrace();
															}
														}
													});
											Toast.makeText(
													PersonalLoginActivity.this,
													obj[0].toString(), 1000)
													.show();

											try {
												String str = obj[0].toString();
												JSONObject jsonObject = new JSONObject(
														str);
												String openId = jsonObject
														.getString("openid");
												fastId = openId;
											} catch (JSONException e) {
												e.printStackTrace();
											}
											Log.i("geek",
													"haha" + obj[0].toString()
															+ fastId);
											// 将第三方传过来的数据传给服务器
											fastLogin();
										}
									}
								});
					}
				});
	}


	@Override
	public void refresh() {
		super.refresh();
		if(PersonalActivity.class.equals(parentClass)){
			lparentGroupActivity  = parentGroupActivity;
			lparentClass= parentClass;
			code = getIntent().getStringExtra("code");
		}else if(ShippingActivity.class.equals(parentClass)){
			lparentGroupActivity  = parentGroupActivity;
			lparentClass= parentClass;
			code = getIntent().getStringExtra("code");
			FLAG = "ShippingActivity";
		}else if(TopicResponse.class.equals(parentClass)){
			lparentGroupActivity  = parentGroupActivity;
			lparentClass= parentClass;
			code = getIntent().getStringExtra("code");
			topicid = getIntent().getStringExtra("topicid");
			type = getIntent().getStringExtra("type");
			if("2".equals(type)){
				pid = getIntent().getStringExtra("pid");
				topicNickName = getIntent().getStringExtra("nickName");
			}
			FLAG = "TopicResponse";
		}else if(PersonalGoodsDetailsActivity.class.equals(parentClass)){
			lparentGroupActivity  = parentGroupActivity;
			lparentClass= parentClass;
			code = getIntent().getStringExtra("code");
			FLAG = "PersonalGoodsDetailsActivity";
		}else if(PersonalPublishEvaluationActivity.class.equals(parentClass)){
			lparentGroupActivity  = parentGroupActivity;
			lparentClass= parentClass;
			code = getIntent().getStringExtra("code");
			tag = getIntent().getStringExtra("tag");
			FLAG = "PersonalPublishEvaluationActivity";
		}else if(TopicPublishActivity.class.equals(parentClass)){
			lparentClass= parentClass;
			code = getIntent().getStringExtra("code");
			FLAG = "TopicPublishActivity";
		}else if(SettingOpinion.class.equals(parentClass)){
			lparentClass= parentClass;
			code = getIntent().getStringExtra("code");
			FLAG = "SettingOpinion";
		}
		userlogin.setEnabled(true);
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
	protected void back() {
//		parentGroupActivity = lparentGroupActivity;
		parentClass = lparentClass;
		if(TopicResponse.class.equals(parentClass)){
			
		}else if(ShippingActivity.class.equals(parentClass)||PersonalActivity.class.equals(parentClass)){
			showTabHost = true;
		}else{
			
		}
		super.back();
	}
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.userlogin_tv_next :
//				Toast.makeText(this, "点击了", 1).show();
				enter_user_tel = login_tel.getText().toString();
				boolean checkPhoneNumber = CheckUtil.checkPhoneNumber(enter_user_tel);
				if("".equals(enter_user_tel)){
					ToastUtil.showLocation(PersonalLoginActivity.this, PersonalLoginActivity.this.getResources().getString(R.string.personal_enter_phone));
				}else if(checkPhoneNumber){
					checkUser();
				}else{
					ToastUtil.showLocation(PersonalLoginActivity.this, PersonalLoginActivity.this.getResources().getString(R.string.personal_enter_phone_check));
				}
				Utils.hideInputMethod(PersonalLoginActivity.this);
				userlogin.setEnabled(false);
				break;
			case R.id.title_left :
				intent = new Intent(PersonalLoginActivity.this,
						PersonalActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			default :
				break;
		}
	}

	/**
	 * 
	 * @Title: checkUser
	 * @user: helen.yang
	 * @Description: 根据用户名检查用户是否注册
	 * @throws
	 */
	private void checkUser() {
		final String url = Constants.CHECKUSER_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		Log.i(TAG, enter_user_tel);
		map.put("mobile", enter_user_tel);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent;
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject json = new JSONObject(result);
								String st = json.getString("status");
								Log.i(TAG, "/-----" + st);
								//去注册登录页面
								if ("-505".equals(st)) {
									if("TopicResponse".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalFirstLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("code", code);
										intent.putExtra("flag", FLAG);
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
									}else if("TopicPublishActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalFirstLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("code", code);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}
									else if("ShippingActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalFirstLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("code", code);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										ShippingGroupActiviy.group.startiHaiGoActivity(intent);
									}else if("PersonalGoodsDetailsActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalFirstLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("PersonalPublishEvaluationActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalFirstLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra("tag", tag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("SettingOpinion".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalFirstLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
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
												PersonalLoginActivity.this,
												PersonalFirstLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("code", code);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										PersonalGroupActivity.group
										.startiHaiGoActivity(intent);
									}
									
									
								} else if ("-501".equals(st)) {
									//去登录页面
									if("TopicResponse".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalUserLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
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
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}else if("TopicPublishActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalUserLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("code", code);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}
									else if("ShippingActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalUserLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										ShippingGroupActiviy.group.startiHaiGoActivity(intent);
									}else if("PersonalGoodsDetailsActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalUserLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("PersonalPublishEvaluationActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalUserLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra("tag", tag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("SettingOpinion".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalUserLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
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
												PersonalLoginActivity.this,
												PersonalUserLogin.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										PersonalGroupActivity.group
										.startiHaiGoActivity(intent);
									}
								} else if ("-502".equals(st)) {
									//去设置登录密码页面
									if("TopicResponse".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalSettingPassword.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
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
									}else if("TopicPublishActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalSettingPassword.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("code", code);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										TopicGroupActivity.group.startiHaiGoActivity(intent);
									}
									else if("ShippingActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalSettingPassword.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
								
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										ShippingGroupActiviy.group.startiHaiGoActivity(intent);
									}else if("PersonalGoodsDetailsActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalSettingPassword.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										lparentGroupActivity.startiHaiGoActivity(intent);
									}else if("PersonalPublishEvaluationActivity".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalSettingPassword.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra("tag", tag);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										ShippingGroupActiviy.group.startiHaiGoActivity(intent);
									}else if("SettingOpinion".equals(FLAG)){
										intent = new Intent(
												PersonalLoginActivity.this,
												PersonalSettingPassword.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
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
												PersonalLoginActivity.this,
												PersonalSettingPassword.class);
										intent.putExtra("key", enter_user_tel);
										intent.putExtra("flag", FLAG);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
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
	 * @Title: fastLogin
	 * @user: helen.yang
	 * @Description: 第三方登录 void
	 * @throws
	 */
	private void fastLogin() {
		final String url = Constants.FASTLOGIN_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		udid = getIntent().getStringExtra("uuid");
		map.put("id", fastId);// 第三方id
		map.put("name", name);// 昵称
		map.put("introduce", "");// 个性签名
		map.put("image", image_url);// 图像
		map.put("sns", "1");// 第三方类型
		map.put("code", code);// 激活码
		map.put("channel", "android market");// 应用发布渠道
		map.put("userToken", "");// 手机唯一标示
		map.put("udid", udid);// 手机唯一标示
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject json = new JSONObject(result);
								String st = json.getString("userId");
								Log.i(TAG, "/-----" + st);
								if ("0".equals(st)) {
									ToastUtil.showShort(
											PersonalLoginActivity.this,
											"第三方登录失败");
								} else {
									ToastUtil.showShort(
											PersonalLoginActivity.this,
											"第三方登录成功");
									intent.setClass(PersonalLoginActivity.this,
											PersonalActivity.class);
									intent.putExtra("userId", st);

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
