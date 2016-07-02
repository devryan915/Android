/**
 * @Title: PersonalBingdingMobile.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月15日 下午1:30:48

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
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * @ClassName: PersonalBingdingMobile
 * @Description: 进入绑定手机号码页面
 * @author: helen.yang
 * @date: 2014年7月15日 下午1:30:48
 * 
 */

public class PersonalBingdingMobile extends IHaiGoActivity{

	private EditText et_bingding_tel;
	private String bingding_mobile;
	private String TAG = "PersonalBingdingMobile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_binding_mobile_phone);
		initTitle();
		initComponents();

	}

	@Override
	public void refresh() {
		super.refresh();
		findViewById(R.id.userlogin_tv_next).setEnabled(true);
	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		et_bingding_tel = (EditText) findViewById(R.id.et_bingding_tel);

		findViewById(R.id.userlogin_tv_next).setOnClickListener(this);
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
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalEditUserInfo");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		super.back();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(PersonalBingdingMobile.this,
						PersonalEditUserInfo.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;

			case R.id.userlogin_tv_next :
				Toast.makeText(this, "点击了", 1).show();
				findViewById(R.id.userlogin_tv_next).setEnabled(false);
				bingding_mobile = et_bingding_tel.getText().toString();
				getMsgCode();
				break;
			default :
				break;
		}
	}

	/**
	 * 
	 * @Title: getMsgCode
	 * @user: helen.yang
	 * @Description: 得到验证码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", bingding_mobile);
		map.put("msgType", "4");
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
									intent.setClass(
											PersonalBingdingMobile.this,
											PersonalConfirmBingdingMobile.class);
									intent.putExtra("mobile", bingding_mobile);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if ("0".equals(st)) {
									Toast.makeText(PersonalBingdingMobile.this,
											"失败", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(PersonalBingdingMobile.this,
											"该号码已存在", 0).show();
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
