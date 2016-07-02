package com.broadchance.wdecgrec.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.entity.serverentity.UIUserInfoBaseResponse;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;

public class MyInfoActivity extends BaseActivity {
	private TextView textViewUseName;
	private TextView myinfoDevNo;
	private EditText myinfoName;
	private TextView myinfoNameSexAge;
	private TextView myinfoID;
	private EditText myinfoHeight;
	private EditText myinfoWeight;
	private EditText myinfoMobilePhone;
	private EditText myinfoAddress;
	private EditText myinfoZipCode;
	private EditText myinfoMemo;
	private Button buttonSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		findViewById(R.id.buttonTitleBack).setOnClickListener(this);
		textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getLoginName());
		myinfoDevNo = (TextView) findViewById(R.id.myinfoDevNo);
		String deviceNumber = SettingsManager.getInstance().getDeviceNumber();
		myinfoDevNo.setText(deviceNumber);
		myinfoNameSexAge = (TextView) findViewById(R.id.myinfoNameSexAge);
		myinfoName = (EditText) findViewById(R.id.myinfoName);
		myinfoID = (TextView) findViewById(R.id.myinfoID);
		myinfoHeight = (EditText) findViewById(R.id.myinfoHeight);
		myinfoWeight = (EditText) findViewById(R.id.myinfoWeight);
		myinfoMobilePhone = (EditText) findViewById(R.id.myinfoMobilePhone);
		myinfoAddress = (EditText) findViewById(R.id.myinfoAddress);
		myinfoZipCode = (EditText) findViewById(R.id.myinfoZipCode);
		myinfoMemo = (EditText) findViewById(R.id.myinfoMemo);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(this);
		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getLoginName());
		loadData();
	}

	/**
	 * 从服务端拉取数据
	 */
	private void loadData() {
		serverService.GetUserBase(DataManager.getUserInfo().getUserID(),
				new HttpReqCallBack<UIUserInfoBaseResponse>() {

					@Override
					public Activity getReqActivity() {
						return MyInfoActivity.this;
					}

					@Override
					public void doSuccess(UIUserInfoBaseResponse result) {
						if (result.isOk()) {
							myinfoName.setEnabled(!(result.Data.getRealName() != null && !result.Data
									.getRealName().trim().isEmpty()));
							myinfoName.setText(result.Data.getRealName());

							myinfoNameSexAge.setText("，"
									+ (result.Data.getSex() == 0 ? "女" : "男")
									+ "，" + result.Data.getAge() + "岁");
							myinfoID.setText(result.Data.getIDCard());
							myinfoHeight.setText(result.Data.getHeight() + "");
							myinfoWeight.setText(result.Data.getWeight() + "");
							myinfoMobilePhone.setText(DataManager.getUserInfo()
									.getMobileNum() != null ? DataManager
									.getUserInfo().getMobileNum() : "");
							myinfoAddress
									.setText(result.Data.getFullAddr() != null ? result.Data
											.getFullAddr() : "");
							myinfoZipCode
									.setText(result.Data.getZipCode() != null ? result.Data
											.getZipCode() : "");
							myinfoMemo.setText(result.Data.getRemarks() != null ? result.Data
									.getRemarks() : "");
						} else {
							showToast(result.getMessage());
							// showResponseMsg(result.Code);
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

	private void returnSettingsAcitivity() {
		Intent myIntent = new Intent();
		myIntent = new Intent(MyInfoActivity.this, SettingsActivity.class);
		startActivity(myIntent);
		this.finish();
	}

	/**
	 * 保存用户数据
	 */
	private void saveData() {
		String name = myinfoName.getText().toString();
		if (name.trim().isEmpty()) {
			showToast("请输入姓名");
			return;
		}
		String heightStr = myinfoHeight.getText().toString();
		float height = 0;
		if (heightStr.trim().isEmpty()) {
			myinfoHeight.requestFocusFromTouch();
			showToast("请输入身高");
			return;
		} else {
			height = Float.parseFloat(heightStr);
			if (height < 1) {
				myinfoHeight.requestFocusFromTouch();
				showToast("请输入合法身高");
				return;
			}
		}
		String weightStr = myinfoWeight.getText().toString();
		float weight = 0;
		if (weightStr.trim().isEmpty()) {
			myinfoWeight.requestFocusFromTouch();
			showToast("请输入体重");
			return;
		} else {
			weight = Float.parseFloat(weightStr);
			if (weight < 1) {
				myinfoWeight.requestFocusFromTouch();
				showToast("请输入合法体重");
				return;
			}
		}

		final String tokenPhone = myinfoMobilePhone.getText().toString();
		if (tokenPhone.trim().isEmpty()) {
			myinfoMobilePhone.requestFocusFromTouch();
			showToast("请输入验证手机号");
			return;
		} else {
			if (tokenPhone.trim().length() < 11) {
				myinfoMobilePhone.requestFocusFromTouch();
				showToast("请输入11位验证手机号");
				return;
			}
		}

		String address = myinfoAddress.getText().toString();
		String zipCode = myinfoZipCode.getText().toString();
		String remark = myinfoMemo.getText().toString();
		serverService.UpdateUserBase(DataManager.getUserInfo().getUserID(),
				myinfoID.getText().toString(), height, weight, zipCode, name,
				remark, address, tokenPhone,
				new HttpReqCallBack<StringResponse>() {
					@Override
					public Activity getReqActivity() {
						return MyInfoActivity.this;
					}

					@Override
					public void doSuccess(StringResponse result) {
						if (result.isOk()) {
							showToast("保存成功");
							// 回写数据
							DataManager.getUserInfo().setMobileNum(tokenPhone);
							returnSettingsAcitivity();
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
