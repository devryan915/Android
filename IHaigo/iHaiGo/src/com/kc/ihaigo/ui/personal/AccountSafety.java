/**
 * @Title: AccountSafety.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月22日 上午7:06:01

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
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * @ClassName: AccountSafety
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月22日 上午7:06:01
 * 
 */

public class AccountSafety extends IHaiGoActivity {

	private String TAG = "AccountSafety";
	private String mobile;

	private TextView tv_update_trade_pasd;
	private TextView tv_update_login_pasd;
	private TextView tv_find_trade_pasd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_account_safe);
		initTitle();
		initComponents();
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
	}

	private void initComponents() {
		tv_update_login_pasd = (TextView) findViewById(R.id.update_login_pasd);
		tv_update_login_pasd.setOnClickListener(this);
//		tv_find_trade_pasd = (TextView) findViewById(R.id.find_trade_pasd);
//		tv_find_trade_pasd.setOnClickListener(this);
//		tv_update_trade_pasd = (TextView) findViewById(R.id.update_trade_pasd);
//		tv_update_trade_pasd.setOnClickListener(this);
	}

	
	@Override
	public void refresh() {
		super.refresh();
		if(PersonalActivity.class.equals(parentClass)){
			mobile = getIntent().getStringExtra("mobile");
		}
	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		showTabHost = true;
		super.back();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(AccountSafety.this, PersonalActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;

		case R.id.update_login_pasd:
			intent.setClass(AccountSafety.this, UpdateLoginPasd.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
//		case R.id.update_trade_pasd:
//			intent.setClass(AccountSafety.this, UpdateTradePasd.class);
//			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
//			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
//			PersonalGroupActivity.group.startiHaiGoActivity(intent);
//			break;
//		case R.id.find_trade_pasd:
//			intent.setClass(AccountSafety.this, FindTradePasd.class);
//			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
//			intent.putExtra("key", mobile);
//			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
//			PersonalGroupActivity.group.startiHaiGoActivity(intent);
//			break;
		default:
			break;

		}

	}

	/**
	 * 
	 * @Title: getMsgCode
	 * @user: helen.yang
	 * @Description: 获取验证码——找回交易密码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("mobile", mobile);
		map.put("msgType", "6");

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
									intent.setClass(AccountSafety.this,
											SettingTradePasd.class);
									intent.putExtra("key", mobile);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if ("0".equals(st)) {
									Toast.makeText(AccountSafety.this, "失败", 0)
											.show();
								} else if ("-503".equals(st)) {
									Toast.makeText(AccountSafety.this, "没有此用户",
											0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(AccountSafety.this,
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
