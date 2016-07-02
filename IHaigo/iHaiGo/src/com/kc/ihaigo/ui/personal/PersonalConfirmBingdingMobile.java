/**
 * @Title: PersonalConfirmBingdingMobile.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月15日 下午10:32:46

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
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: PersonalConfirmBingdingMobile
 * @Description: 确认绑定手机号码页面
 * @author: helen.yang
 * @date: 2014年7月15日 下午10:32:46
 * 
 */

public class PersonalConfirmBingdingMobile extends IHaiGoActivity
		implements
			OnClickListener {

	private String mobile;
	private EditText et_enter_msg_info;
	private String enter_info;
	private String TAG = "PersonalConfirmBingdingMobile";
	private TextView tv_time_flag;

	/**
	 * 时间倒计时
	 */
	private MyCount mc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_confirm_binding_mobile_phone);
		initTitle();
		initComponents();

		mc = new MyCount(60000, 1000);
		mc.start();

	}

	@Override
	public void refresh() {
		super.refresh();
		findViewById(R.id.userlogin_first_tv_confirm).setEnabled(true);
	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		et_enter_msg_info = (EditText) findViewById(R.id.et_msg_login);
		tv_time_flag = (TextView) findViewById(R.id.tv_time_flag);
		tv_time_flag.setOnClickListener(this);
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
		findViewById(R.id.userlogin_first_tv_confirm).setOnClickListener(this);

		mobile = getIntent().getStringExtra("mobile");
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(PersonalConfirmBingdingMobile.this,
						PersonalEditUserInfo.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.userlogin_first_tv_confirm :
				Toast.makeText(this, "点击了", 1).show();
				findViewById(R.id.userlogin_first_tv_confirm).setEnabled(false);
				enter_info = et_enter_msg_info.getText().toString();
				bindingMobile();
			case R.id.tv_time_flag :
				getMsgCode();
				break;
			default :
				break;
		}
	}

	/**
	 * 
	 * @Title: bindingMobile
	 * @user: helen.yang
	 * @Description: 绑定手机 void
	 * @throws
	 */
	private void bindingMobile() {
		final String url = Constants.BINDINGMOBILE_URL;
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", Constants.USER_ID);
			map.put("mobile", mobile);
			map.put("msgType", "4");
			map.put("msgCode", enter_info);
			HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
					new HttpReqCallBack() {

						@Override
						public void deal(String result) {
							Intent intent = new Intent();
							if (!TextUtils.isEmpty(result)) {
								Log.i(TAG, "+++++++++++++++++++收到信息" + result);
								try {
									JSONObject json = new JSONObject(result);
									int code = json.getInt("code");
									Log.i(TAG, "/-----" + Constants.USER_ID);
									if(code == 1){
										intent.setClass(PersonalConfirmBingdingMobile.this,
												PersonalEditUserInfo.class);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										PersonalGroupActivity.group
												.startiHaiGoActivity(intent);
									}else if(code == 0){
										Toast.makeText(PersonalConfirmBingdingMobile.this,
												"失败", 0).show();
									}else if(code == 2){
										Toast.makeText(PersonalConfirmBingdingMobile.this,
												"验证码错误", 0).show();
									}else if(code == 3){
										Toast.makeText(PersonalConfirmBingdingMobile.this,
												"验证码过期", 0).show();
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
	 * @Description: 获取验证码——绑定手机再次获取验证码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", mobile);
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
											PersonalConfirmBingdingMobile.this,
											"发送成功");
								} else if ("0".equals(st)) {
									Toast.makeText(
											PersonalConfirmBingdingMobile.this,
											"失败", 0).show();
								} else if ("-503".equals(st)) {
									Toast.makeText(
											PersonalConfirmBingdingMobile.this,
											"没有此用户", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(
											PersonalConfirmBingdingMobile.this,
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
