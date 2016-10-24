package thoth.holter.ecg_010.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.entity.serverentity.UIUserInfoRegistResponse;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;

import thoth.holter.ecg_010.R;
import thoth.holter.ecg_010.widget.LabelEditText;

public class ResetPwdActivity extends BaseActivity {
	/**
	 * 注册按钮
	 */
	private Button buttonResetPwd;
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
	private Handler leftTimeHandler = new Handler();
	/**
	 * 剩余验证码提示时间
	 */
	private int leftTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_resetpwd);
		buttonResetPwd = (Button) findViewById(R.id.buttonResetPwd);
		buttonResetPwd.setOnClickListener(this);
		editTextUserName = (LabelEditText) findViewById(R.id.editTextUserName);
		imageButtonGetToken = (ImageButton) findViewById(R.id.imageButtonGetToken);
		imageButtonGetToken.setOnClickListener(this);
		textViewLeftTime = (TextView) findViewById(R.id.textViewLeftTime);
		editTextToken = (LabelEditText) findViewById(R.id.editTextToken);
		editTextPwd = (LabelEditText) findViewById(R.id.editTextPwd);
		editTextConfirmPwd = (LabelEditText) findViewById(R.id.editTextConfirmPwd);
		registerDelaySendMsg = findViewById(R.id.registerDelaySendMsg);
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
			showToast("密码必须为8位数字");
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
		serverService.ForgetPwd(mobileNumber, verfyCode, pwd,
				new HttpReqCallBack<StringResponse>() {

					@Override
					public Activity getReqActivity() {
						return ResetPwdActivity.this;
					}

					@Override
					public void doSuccess(StringResponse result) {
						if (result.isOk()) {
							showToast("恭喜您，重置成功，请重新登录！");
							finish();
							Intent intent = new Intent(ResetPwdActivity.this,
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
						} else {
							showToast("操作失败");
						}
					}
				});

	}

	private void getLeftTime() {
		leftTime = RegisterActivity.GETVERFY_INTERVALTIME;
		textViewLeftTime.setText(leftTime + "s");
		textViewLeftTime.setVisibility(View.VISIBLE);
		registerDelaySendMsg.setVisibility(View.VISIBLE);
		leftTimeHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (leftTime-- > 0) {
					textViewLeftTime.setText(leftTime + "s");
					leftTimeHandler.postDelayed(this,
							RegisterActivity.LEFT_INTERVALTIME);
				} else {
					textViewLeftTime.setVisibility(View.INVISIBLE);
					registerDelaySendMsg.setVisibility(View.INVISIBLE);
				}
			}
		}, RegisterActivity.LEFT_INTERVALTIME);
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
		serverService.GetForgetVerifyCode(mobileNumber,
				new HttpReqCallBack<StringResponse>() {
					@Override
					public Activity getReqActivity() {
						return ResetPwdActivity.this;
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
		case R.id.buttonResetPwd:
			register();
			break;
		case R.id.imageButtonGetToken:
			getVerfyToken();
			break;
		default:
			break;
		}
	}
}
