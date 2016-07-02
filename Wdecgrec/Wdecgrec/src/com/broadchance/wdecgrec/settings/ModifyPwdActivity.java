package com.broadchance.wdecgrec.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.R.id;
import com.broadchance.wdecgrec.R.layout;
import com.broadchance.wdecgrec.R.string;
import com.broadchance.wdecgrec.login.LoginActivity;
import com.broadchance.wdecgrec.login.ResetPwdActivity;

public class ModifyPwdActivity extends BaseActivity {
	private EditText modifyPwdOldPwd;
	private EditText modifyPwd_NewPwd;
	private EditText modifyPwdConfirmpwd;
	private Button buttonSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_pwd);
		findViewById(R.id.buttonTitleBack).setOnClickListener(this);
		modifyPwdOldPwd = (EditText) findViewById(R.id.modifyPwdOldPwd);
		modifyPwd_NewPwd = (EditText) findViewById(R.id.modifyPwd_NewPwd);
		modifyPwdConfirmpwd = (EditText) findViewById(R.id.modifyPwdConfirmpwd);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(this);
		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getLoginName());
	}

	private void returnSettingsAcitivity() {
		Intent myIntent = new Intent();
		myIntent = new Intent(ModifyPwdActivity.this, SettingsActivity.class);
		startActivity(myIntent);
		this.finish();
	}

	/**
	 * 保存用户数据
	 */
	private void saveData() {
		String old = modifyPwdOldPwd.getText().toString();
		if (old.isEmpty()) {
			showToast(localRes.getString(R.string.modifypwd_oldpwdhint));
			// 重新获取焦点
			modifyPwdOldPwd.requestFocusFromTouch();
			return;
		}
		String newPwd = modifyPwd_NewPwd.getText().toString();
		if (newPwd.isEmpty()) {
			showToast(localRes.getString(R.string.modifypwd_newpwdhint));
			// 重新获取焦点
			modifyPwd_NewPwd.requestFocusFromTouch();
			return;
		} else if (newPwd.length() != 8) {
			modifyPwd_NewPwd.requestFocusFromTouch();
			showToast("密码必须为8为数字");
			return;
		}
		String newConfirmPwd = modifyPwdConfirmpwd.getText().toString();
		if (newConfirmPwd.isEmpty()) {
			showToast(localRes.getString(R.string.modifypwd_confirmpwdhint));
			// 重新获取焦点
			modifyPwdConfirmpwd.requestFocusFromTouch();
			return;
		}
		if (!newPwd.equals(newConfirmPwd)) {
			showToast("两次输入密码不同");
			// 重新获取焦点
			modifyPwdConfirmpwd.requestFocusFromTouch();
			return;
		}
		serverService.ChangePwd(DataManager.getUserInfo().getUserID(), newPwd,
				old, new HttpReqCallBack<StringResponse>() {
					@Override
					public Activity getReqActivity() {
						return ModifyPwdActivity.this;
					}

					@Override
					public void doSuccess(StringResponse result) {
						if (result.isOk()) {
							showToast("密码修改成功，请重新登录");
							// returnSettingsAcitivity();
							finish();
							Intent intent = new Intent(ModifyPwdActivity.this,
									LoginActivity.class);
							startActivity(intent);
						} else {
							showToast(result.getMessage());
							if (ConstantConfig.Debug) {
								// showResponseMsg(result.Code);
							}
						}
					}

					@Override
					public void doError(String result) {
						if (ConstantConfig.Debug) {
							showToast(result);
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonSave:
			saveData();
			break;
		case R.id.buttonTitleBack:
			returnSettingsAcitivity();
			break;
		default:
			break;
		}
	}
}
