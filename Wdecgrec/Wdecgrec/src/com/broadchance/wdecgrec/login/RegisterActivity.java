package com.broadchance.wdecgrec.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.entity.serverentity.UIUserInfoRegistResponse;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.widget.LabelEditText;
import com.broadchance.wdecgrec.widget.WebDialog;

public class RegisterActivity extends BaseActivity {
	/**
	 * 注册按钮
	 */
	private Button buttonRegister;
	/**
	 * 用户名(手机号)
	 */
	private LabelEditText editTextUserName;
	/**
	 * 获取验证码按钮
	 */
	private ImageButton imageButtonGetToken;
	/**
	 * 距离下次获取验证码的时间
	 */
	private TextView textViewLeftTime;
	private View registerDelaySendMsg;
	/**
	 * 用户输入的验证码
	 */
	private LabelEditText editTextToken;
	/**
	 * 密码
	 */
	private LabelEditText editTextPwd;
	/**
	 * 重复输入密码
	 */
	private LabelEditText editTextConfirmPwd;
	/**
	 * 用户输入的身份证ID
	 */
	private LabelEditText editTextID;
	/**
	 * 是否同意用户协议
	 */
	private CheckBox checkBoxAgreedAgreement;

	private Handler leftTimeHandler = new Handler();;
	/**
	 * 获取验证码间隔时间
	 */
	public final static int GETVERFY_INTERVALTIME = 60;
	/**
	 * 倒计时时间，单位毫秒，默认1s倒计时一次
	 */
	public final static int LEFT_INTERVALTIME = 1000;
	/**
	 * 剩余验证码提示时间
	 */
	private int leftTime;
	private TextView agreementLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		buttonRegister = (Button) findViewById(R.id.buttonResetPwd);
		buttonRegister.setOnClickListener(this);
		editTextUserName = (LabelEditText) findViewById(R.id.editTextUserName);
		imageButtonGetToken = (ImageButton) findViewById(R.id.imageButtonGetToken);
		imageButtonGetToken.setOnClickListener(this);
		textViewLeftTime = (TextView) findViewById(R.id.textViewLeftTime);
		editTextToken = (LabelEditText) findViewById(R.id.editTextToken);
		editTextPwd = (LabelEditText) findViewById(R.id.editTextPwd);
		editTextConfirmPwd = (LabelEditText) findViewById(R.id.editTextConfirmPwd);
		editTextID = (LabelEditText) findViewById(R.id.editTextID);
		checkBoxAgreedAgreement = (CheckBox) findViewById(R.id.checkBoxAgreedAgreement);
		registerDelaySendMsg = findViewById(R.id.registerDelaySendMsg);
		agreementLabel = (TextView) findViewById(R.id.agreementLabel);
		agreementLabel.setOnClickListener(this);

		// WebView webView = (WebView) findViewById(R.id.webView);
		// webView.loadUrl("file:///android_asset/a.html");
		// webView.setWebViewClient(new WebViewClient() {
		// @Override
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
		// view.loadUrl(url);
		// return true;
		// }
		// });
		initView();
	}

	void initView() {
		textViewLeftTime.setVisibility(View.INVISIBLE);
		registerDelaySendMsg.setVisibility(View.INVISIBLE);
	}

	private void register() {
		String mobileNumber = editTextUserName.getText().toString();
		if (!checkUserName()) {
			// 重新获取焦点
			editTextUserName.requestFocusFromTouch();
			return;
		}
		String verfyCode = editTextToken.getText().toString();
		if (!checkVerfyCode()) {
			// 重新获取焦点
			editTextToken.requestFocusFromTouch();
			return;
		}
		String pwd = editTextPwd.getText().toString();
		if (pwd.isEmpty()) {
			showToast(localRes.getString(R.string.register_pwd_hint));
			// 重新获取焦点
			editTextPwd.requestFocusFromTouch();
			return;
		} else if (pwd.length() != 8) {
			editTextPwd.requestFocusFromTouch();
			showToast("密码必须为8为数字");
			return;
		}
		String rePwd = editTextConfirmPwd.getText().toString();
		if (rePwd.isEmpty()) {
			showToast(localRes.getString(R.string.register_confirmpwd_hint));
			// 重新获取焦点
			editTextConfirmPwd.requestFocusFromTouch();
			return;
		}
		if (!pwd.equals(rePwd)) {
			showToast("两次输入密码不同");
			// 重新获取焦点
			editTextConfirmPwd.requestFocusFromTouch();
			return;
		}
		String IDCard = editTextID.getText().toString();
		if (IDCard.isEmpty()) {
			showToast(localRes.getString(R.string.register_ID_hint));
			// 重新获取焦点
			editTextID.requestFocusFromTouch();
			return;
		} else {
			if (IDCard.trim().length() < 18) {
				showToast("请输入18位身份证");
				editTextID.requestFocusFromTouch();
				return;
			}
		}
		if (!checkBoxAgreedAgreement.isChecked()) {
			showToast("请同意用户协议");
			return;
		}
		serverService.Regist(mobileNumber, pwd, verfyCode,
				IDCard.toUpperCase(),
				new HttpReqCallBack<UIUserInfoRegistResponse>() {

					@Override
					public Activity getReqActivity() {
						return RegisterActivity.this;
					}

					@Override
					public void doSuccess(UIUserInfoRegistResponse result) {
						if (result.isOk()) {
							showToast("恭喜您，注册成功，请登录！");
							finish();
							Intent intent = new Intent(RegisterActivity.this,
									LoginActivity.class);
							startActivity(intent);
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

	private void getLeftTime() {
		leftTime = GETVERFY_INTERVALTIME;
		textViewLeftTime.setText(leftTime + "s");
		textViewLeftTime.setVisibility(View.VISIBLE);
		registerDelaySendMsg.setVisibility(View.VISIBLE);
		leftTimeHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (leftTime-- > 0) {
					textViewLeftTime.setText(leftTime + "s");
					leftTimeHandler.postDelayed(this, LEFT_INTERVALTIME);
				} else {
					textViewLeftTime.setVisibility(View.INVISIBLE);
					registerDelaySendMsg.setVisibility(View.INVISIBLE);
				}
			}
		}, LEFT_INTERVALTIME);
	}

	private boolean checkUserName() {
		String mobileNumber = editTextUserName.getText().toString();
		if (mobileNumber.isEmpty() || mobileNumber.trim().length() < 11) {
			showToast("请输入11位手机号");
			return false;
		}
		return true;
	}

	private boolean checkVerfyCode() {
		String verfyCode = editTextToken.getText().toString();
		if (verfyCode.isEmpty()) {
			showToast("请输入验证码");
			return false;
		}
		return true;
	}

	/**
	 * 获取验证码
	 */
	private void getVerfyToken() {
		String mobileNumber = editTextUserName.getText().toString();
		if (!checkUserName()) {
			// 重新获取焦点
			editTextUserName.requestFocusFromTouch();
			return;
		}
		if (leftTime > 0) {
			showToast("请稍候获取");
			return;
		}
		serverService.GetRegisterVerifyCode(mobileNumber,
				new HttpReqCallBack<StringResponse>() {

					@Override
					public Activity getReqActivity() {
						return RegisterActivity.this;
					}

					@Override
					public void doSuccess(StringResponse result) {
						if (result.isOk()) {
							getLeftTime();
							editTextToken.requestFocusFromTouch();
							showToast("发送成功");
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

	private void showAgreement() {
		WebDialog webDialog = new WebDialog(RegisterActivity.this,
				"file:///android_asset/agreement.html");
		webDialog.setCanceledOnTouchOutside(true);
		webDialog.show();
		// LayoutInflater inflater = (LayoutInflater) RegisterActivity.this
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// LinearLayout layout = (LinearLayout) inflater.inflate(
		// R.layout.dialog_agreement, null);
		// WebView webView = (WebView) layout.findViewById(R.id.webView);
		// webView.loadUrl("file:///android_asset/a.html");
		// // webView.loadUrl("http://www.baidu.com");
		// // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		// webView.setWebViewClient(new WebViewClient() {
		// @Override
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
		// view.loadUrl(url);
		// return true;
		// }
		// });
		// Dialog dialog = UIUtil.buildDialog(RegisterActivity.this, layout);
		// dialog.show();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonResetPwd:
			register();
			break;
		case R.id.imageButtonGetToken:
			getVerfyToken();
			break;
		case R.id.agreementLabel:
			showAgreement();
			break;
		default:
			break;
		}
	}
}
