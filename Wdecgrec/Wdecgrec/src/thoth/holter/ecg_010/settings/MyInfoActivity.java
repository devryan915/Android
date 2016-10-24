package thoth.holter.ecg_010.settings;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.entity.serverentity.UIUserInfoBaseResponse;
import com.broadchance.utils.BleDataUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;

import thoth.holter.ecg_010.R;
import thoth.holter.ecg_010.manager.DataManager;

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

	// private View viewBindedDev;

	// private TextView editTextDevPwd;

	// private TextView myinfoBindedDev;
	// private Dialog dialogUnbind;
	public class PartialRegexInputFilter implements InputFilter {

		private Pattern mPattern;

		public PartialRegexInputFilter(String pattern) {
			mPattern = Pattern.compile(pattern);
		}

		@Override
		public CharSequence filter(CharSequence source, int sourceStart,
				int sourceEnd, Spanned destination, int destinationStart,
				int destinationEnd) {
			String textToCheck = destination.subSequence(0, destinationStart)
					.toString()
					+ source.subSequence(sourceStart, sourceEnd)
					+ destination.subSequence(destinationEnd,
							destination.length()).toString();
			// try {
			// DecimalFormat decimalFormat = new DecimalFormat("###.0");
			// textToCheck = decimalFormat.format(Float
			// .parseFloat(textToCheck));
			// } catch (Exception e) {
			// showToast("输入有误");
			// } finally {
			// return textToCheck;
			// }
			Matcher matcher = mPattern.matcher(textToCheck);
			if (!matcher.matches()) {
				return "";
			}
			return null;
		}
	}

	public class CustomTextWatcher implements TextWatcher {
		private boolean isChanged = false;
		EditText edt;

		public CustomTextWatcher(EditText edt) {
			super();
			this.edt = edt;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (isChanged) {// ----->如果字符未改变则返回
				return;
			}
			String str = s.toString();

			isChanged = true;
			String cuttedStr = null;
			boolean flag = false;
			/* 删除字符串中的dot */
			// for (int i = str.length() - 1; i >= 0; i--) {
			// char c = str.charAt(i);
			// if ('.' == c && i == str.length() - 3) {
			// cuttedStr = str.substring(0, i + 2);
			// if (cuttedStr.endsWith(".")) {
			// cuttedStr = cuttedStr.substring(0, i + 1);
			// }
			// flag = true;
			// break;
			// }
			// }
			int dotIndex = str.indexOf(".");
			if (dotIndex != -1) {
				String sufixx = ".";
				String prefix = "";
				try {
					sufixx = str.substring(dotIndex, dotIndex + 2);
					prefix = str.substring(Math.max(0, dotIndex - 3), dotIndex);
					cuttedStr = prefix + sufixx;
					flag = !str.equals(cuttedStr);
				} catch (Exception e) {
				}
			} else {
				cuttedStr = str.substring(Math.max(0, str.length() - 3),
						str.length());
				flag = !str.equals(cuttedStr);
			}
			// try {
			// DecimalFormat decimalFormat = new DecimalFormat("0.0");
			// cuttedStr = decimalFormat.format(Float.parseFloat(str));
			// flag = !str.equals(cuttedStr);
			// } catch (Exception e) {
			// }
			if (flag) {
				edt.setText(cuttedStr);
			}
			// edit.setSelection(edit.length());
			isChanged = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		findViewById(R.id.buttonTitleBack).setOnClickListener(this);
		// viewBindedDev = findViewById(R.id.viewBindedDev);
		// myinfoBindedDev = (TextView) findViewById(R.id.myinfoBindedDev);
		// viewBindedDev.setOnClickListener(this);
		textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getNickName());
		myinfoDevNo = (TextView) findViewById(R.id.myinfoDevNo);
		// UIUserInfoLogin user = DataManager.getUserInfo();
		String deviceNumber = "";
		// if (user != null) {
		deviceNumber = DataManager.getUserInfo().getMacAddress();
		if (deviceNumber != null && !deviceNumber.trim().isEmpty()) {
			deviceNumber = BleDataUtil.getDeviceName(deviceNumber);
		}
		// }
		myinfoDevNo.setText(deviceNumber);
		myinfoNameSexAge = (TextView) findViewById(R.id.myinfoNameSexAge);
		myinfoName = (EditText) findViewById(R.id.myinfoName);
		myinfoName.setEnabled(false);
		myinfoID = (TextView) findViewById(R.id.myinfoID);
		myinfoHeight = (EditText) findViewById(R.id.myinfoHeight);
		myinfoHeight
				.addTextChangedListener(new CustomTextWatcher(myinfoHeight));
		// myinfoHeight
		// .setFilters(new InputFilter[] { new PartialRegexInputFilter(
		// "^[0-9]{1,3}//.[0-9]{0,1}") });
		myinfoWeight = (EditText) findViewById(R.id.myinfoWeight);
		myinfoWeight
				.addTextChangedListener(new CustomTextWatcher(myinfoWeight));
		// myinfoWeight
		// .setFilters(new InputFilter[] { new PartialRegexInputFilter(
		// "^[0-9]{1,3}//.{0,1}[0-9]{0,1}$") });
		myinfoMobilePhone = (EditText) findViewById(R.id.myinfoMobilePhone);
		myinfoAddress = (EditText) findViewById(R.id.myinfoAddress);
		myinfoZipCode = (EditText) findViewById(R.id.myinfoZipCode);
		myinfoMemo = (EditText) findViewById(R.id.myinfoMemo);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(this);
		// TextView textViewUseName = (TextView)
		// findViewById(R.id.textViewUseName);
		// textViewUseName.setText(DataManager.getUserInfo().getNickName());
		loadData();
	}

	/**
	 * 从服务端拉取数据
	 */
	private void loadData() {
		serverService.GetUserBase(DataManager.getUserInfo().getUserName(),
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
							// myinfoMobilePhone.setText(DataManager.getUserInfo()
							// .getMobileNum() != null ? DataManager
							// .getUserInfo().getMobileNum() : "");
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
						} else {
							showToast("操作失败");
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
		final String name = myinfoName.getText().toString();
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
			if (height < 100 || height > 250) {
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
			if (weight < 30 || weight > 200) {
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
		serverService.UpdateUserBase(DataManager.getUserInfo().getUserName(),
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
							DataManager.getUserInfo().setNickName(name);
							// DataManager.getUserInfo().setMobileNum(tokenPhone);
							returnSettingsAcitivity();
						} else {
							showToast(result.getMessage());
							// if (ConstantConfig.Debug) {
							// showResponseMsg(result.Code);
							// }
						}
					}

					@Override
					public void doError(String result) {
						if (ConstantConfig.Debug) {
							showToast(result);
						} else {
							showToast("操作失败");
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
