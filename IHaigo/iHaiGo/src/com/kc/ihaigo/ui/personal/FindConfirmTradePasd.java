/**
 * @Title: FindConfirmTradePasd.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月22日 上午7:14:12

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
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: FindConfirmTradePasd
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月22日 上午7:14:12
 * 
 */

public class FindConfirmTradePasd extends IHaiGoActivity
		implements
			OnClickListener {

	private String TAG = "FindConfirmTradePasd";
	private TextView tv_time_flag;
	private String extra;
	private EditText enter_forg_pasd;
	private EditText re_enter_forg_pasd;
	private String enter_psd;
	private String re_enter_psd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_confirm_trade_pasd);
		initTitle();
		initComponents();
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
	}

	private void initComponents() {
		findViewById(R.id.userlogin_tv_confirm).setOnClickListener(this);
		tv_time_flag = (TextView) findViewById(R.id.tv_time_flag);
		enter_forg_pasd = (EditText) findViewById(R.id.enter_trade_pasd);
		re_enter_forg_pasd = (EditText) findViewById(R.id.re_enter_trade_pasd);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :

				break;
			case R.id.userlogin_tv_confirm :
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

			default :
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
		map.put("type", "2");
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
									Toast.makeText(FindConfirmTradePasd.this,
											"设置密码成功", 0).show();
									DialogUtil.showEditPsdDialog(
											FindConfirmTradePasd.this
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
																	FindConfirmTradePasd.this,
																	AccountSafety.class);
															intent.putExtra(
																	"key",
																	extra);
															intent.putExtra(
																	"password",
																	enter_psd);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
																	true);
															intent.putExtra(
																	IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
																	false);
															PersonalGroupActivity.group
																	.startiHaiGoActivity(intent);
															break;

														default :
															break;
													}
												}
											}, null);
								} else if ("-500".equals(st)) {
									Toast.makeText(FindConfirmTradePasd.this,
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
