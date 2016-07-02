/**
 * @Title: SettingConfirmTradePasd.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月21日 上午11:58:25

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
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: SettingConfirmTradePasd
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月21日 上午11:58:25
 * 
 */

public class SettingConfirmTradePasd extends IHaiGoActivity
		implements
			OnClickListener {

	private String mobile;
	private String TAG = "SettingConfirmTradePasd";
	private EditText et_trade_psd;
	private EditText et_re_trade_psd;

	private String enter_psd;
	private String enter_re_psd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_confirm_trade_pasd);
		initTitle();
		initComponents();
	}

	private void initComponents() {
		findViewById(R.id.userlogin_tv_confirm).setOnClickListener(this);
		et_trade_psd = (EditText) findViewById(R.id.enter_trade_pasd);
		et_re_trade_psd = (EditText) findViewById(R.id.re_enter_trade_pasd);
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(SettingConfirmTradePasd.this,
						SettingTradePasd.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.userlogin_tv_confirm :
				enter_psd = et_trade_psd.getText().toString().trim();
				enter_re_psd = et_re_trade_psd.getText().toString().trim();
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

				break;
			default :
				break;
		}

	}

	/**
	 * 
	 * @Title: resetPassword
	 * @user: helen.yang
	 * @Description: 用户设置交易密码
	 * @throws
	 */
	private void resetPassword() {
		final String url = Constants.RESETPASSWORD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		mobile = getIntent().getStringExtra("key");
		map.put("mobile", mobile);
		map.put("password", enter_psd);
		map.put("type", "2");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++设置登录密码" + result);
							try {
								JSONObject json = new JSONObject(result);
								int numberId = json.getInt("userId");
								Log.i(TAG, "/+++++++++" + numberId);
								if (numberId > 0) {
									DialogUtil.showSettingPsdDialog(
											SettingConfirmTradePasd.this
													.getParent(),
											new BackCall() {

												@Override
												public void deal(
														int whichButton,
														Object... obj) {
													switch (whichButton) {
														case R.id.exit_oks :
															((Dialog) obj[0])
																	.dismiss();
															Intent intent = new Intent(
																	SettingConfirmTradePasd.this,
																	PersonalUserPay.class);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
																	false);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
																	true);
															PersonalGroupActivity.group
																	.startiHaiGoActivity(intent);
															break;

														default :
															break;
													}
												}
											}, null);
								} else if (numberId == 0) {
									Toast.makeText(
											SettingConfirmTradePasd.this,
											"设置密码失败", 0).show();
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
