/**
 * @Title: UpdateTradePasd.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月22日 上午7:11:33

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
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: UpdateTradePasd
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月22日 上午7:11:33
 * 
 */

public class UpdateTradePasd extends IHaiGoActivity implements OnClickListener {

	private String TAG = "UpdateTradePasd";
	private String mobile;
	private String oldpassword;
	private String password;
	private String repasd;

	private EditText enter_oldpasd;
	private EditText enter_pasd;
	private EditText enter_repasd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_trade_pasd);
		initTitle();
		initComponents();
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
	}

	private void initComponents() {
		enter_oldpasd = (EditText) findViewById(R.id.enter_old_trade_pasd);
		enter_pasd = (EditText) findViewById(R.id.enter_trade_pasd);
		enter_repasd = (EditText) findViewById(R.id.re_enter_trade_pasd);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(UpdateTradePasd.this, AccountSafety.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.userlogin_tv_confirm :
				oldpassword = enter_oldpasd.getText().toString().trim();
				password = enter_pasd.getText().toString().trim();
				repasd = enter_repasd.getText().toString().trim();
				if ("".equals(password)) {
					ToastUtil.showShort(getApplicationContext(), "请输入密码");
				} else if (password.length() < 6) {
					ToastUtil.showShort(this, "密码至少六位");
				} else if (CheckUtil.checkHanzi(password)) {
					ToastUtil.showShort(this, "密码不能为汉字");
				} else if (!password.equals(repasd)) {
					ToastUtil.showShort(this, "密码输入不一致");
				} else {
					updatePassword();
				}
				break;
			default :
				break;
		}

	}

	private void updatePassword() {
		final String url = Constants.UPDATEPASSWORD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("oldPassword", oldpassword);
		map.put("password", password);
		map.put("type", "2");

		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject json = new JSONObject(result);
								String code = json.getString("code");
								Log.i(TAG, "/-----" + code);
								if ("0".equals(code)) {
									Toast.makeText(UpdateTradePasd.this, "失败",
											0).show();
								} else if ("1".equals(code)) {
									Toast.makeText(UpdateTradePasd.this, "成功",
											0).show();
								} else if ("2".equals(code)) {
									Toast.makeText(UpdateTradePasd.this,
											"原密码错误", 0).show();
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
