/**
 * @Title: SettingTradePasd.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月21日 上午11:51:52

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
 * @ClassName: SettingTradePasd
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月21日 上午11:51:52
 * 
 */

public class SettingTradePasd extends IHaiGoActivity implements OnClickListener {
	private String TAG = "SettingTradePasd";
	private TextView next;
	private EditText et_enter_msgCode;
	private String enter_msgCode;
	private String mobile;
	private TextView tv_time_flag;

	/**
	 * 时间倒计时
	 */
	private MyCount mc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_trade_pasd);
		initTitle();
		initComponents();
	}

	private void initComponents() {
		next = (TextView) findViewById(R.id.userlogin_tv_next);
		next.setOnClickListener(this);
		et_enter_msgCode = (EditText) findViewById(R.id.et_msg_trade);
		tv_time_flag = (TextView) findViewById(R.id.tv_time_flag);
		tv_time_flag.setOnClickListener(this);
		mc = new MyCount(60000, 1000);
		mc.start();

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		mobile = getIntent().getStringExtra("key");
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(SettingTradePasd.this, PersonalActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;

			case R.id.userlogin_tv_next :
				enter_msgCode = et_enter_msgCode.getText().toString();
				checkMsgCode();
				break;
			case R.id.tv_time_flag :
				getMsgCode();
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
		map.put("mobile", mobile);
		map.put("msgType", "5");
		map.put("msgCode", enter_msgCode);
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
									Toast.makeText(SettingTradePasd.this,
											"验证成功", 0).show();
									intent = new Intent(SettingTradePasd.this,
											SettingConfirmTradePasd.class);
									intent.putExtra("key", mobile);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if ("-500".equals(st)) {
									Toast.makeText(SettingTradePasd.this,
											"验证失败", 0).show();
								} else if ("-501".equals(st)) {
									Toast.makeText(SettingTradePasd.this,
											"验证码输入错误", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(SettingTradePasd.this,
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
	 * @Description: 获取验证码——设置交易密码再次获取验证码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", mobile);
		map.put("msgType", "5");

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
									ToastUtil.showShort(SettingTradePasd.this,
											"发送成功");
								} else if ("0".equals(st)) {
									Toast.makeText(SettingTradePasd.this, "失败",
											0).show();
								} else if ("-503".equals(st)) {
									Toast.makeText(SettingTradePasd.this,
											"没有此用户", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(SettingTradePasd.this,
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
