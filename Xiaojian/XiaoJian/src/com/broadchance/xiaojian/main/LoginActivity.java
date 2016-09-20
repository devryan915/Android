package com.broadchance.xiaojian.main;

import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.broadchance.xiaojian.BaseActivity;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.utils.Constant;
import com.broadchance.xiaojian.utils.HttpAsyncTask;
import com.broadchance.xiaojian.utils.HttpAsyncTask.HttpReqCallBack;
import com.langlang.global.UserInfo;

public class LoginActivity extends BaseActivity {

	protected static final String TAG = LoginActivity.class.getSimpleName();
	private EditText mobileno;
	private EditText password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mobileno = (EditText) findViewById(R.id.mobileno);
		password = (EditText) findViewById(R.id.password);
		SharedPreferences sp = getSharedPreferences(Constant.USER, MODE_PRIVATE);
		mobileno.setText(sp.getString(Constant.USER_NAME, null));
		password.setText(sp.getString(Constant.USER_PWD, null));
		findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> propertys = new HashMap<String, Object>();
				propertys.put("act", mobileno.getText().toString());
				propertys.put("pwd", password.getText().toString());
				HttpAsyncTask.fetchData("Login", propertys,
						new HttpReqCallBack() {
							@Override
							public void deal(SoapObject result) {
								if (result != null) {
									SharedPreferences sp = getSharedPreferences(
											Constant.USER, MODE_PRIVATE);
									Editor editor = sp.edit();
									editor.putString(Constant.USER_NAME,
											mobileno.getText().toString());
									editor.putString(Constant.USER_PWD,
											password.getText().toString());
									editor.commit();
									UserInfo.getIntance()
											.getUserData()
											.setAccountCode(
													mobileno.getText()
															.toString());
									Toast.makeText(LoginActivity.this,
											result.getProperty(0).toString(),
											1000).show();
									Intent intent = new Intent(
											LoginActivity.this,
											MainActivity.class);
									startActivity(intent);
								} else {
									Intent intent = new Intent(
											LoginActivity.this,
											MainActivity.class);
									startActivity(intent);
								}
								finish();
							}
						}, new Object[] { LoginActivity.this, "正在登陆中" });
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
