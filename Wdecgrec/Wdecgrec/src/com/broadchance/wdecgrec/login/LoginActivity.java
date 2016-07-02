package com.broadchance.wdecgrec.login;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.serverentity.CurVerResponse;
import com.broadchance.manager.AppApplication;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.manager.SkinManager;
import com.broadchance.utils.AESEncryptor;
import com.broadchance.utils.AppDownLoadUtil;
import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.GPSUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.main.ModeActivity;
import com.broadchance.wdecgrec.services.BluetoothLeService;
import com.broadchance.wdecgrec.services.GpsService;
import com.broadchance.wdecgrec.settings.SettingsActivity;
import com.broadchance.wdecgrec.widget.LabelEditText;

public class LoginActivity extends BaseActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();
	private Button buttonLogin;
	private Button buttonRegister;
	private LabelEditText editTextUserName;
	private LabelEditText editTextPwd;
	private CheckBox checkBoxSavePwd;
	private Button buttonForgotPwd;
	private Dialog dialogAppUpdate;
	private final static int REQUEST_GPS_CODE = 188;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getNewVer();
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(this);
		buttonRegister = (Button) findViewById(R.id.buttonResetPwd);
		buttonRegister.setOnClickListener(this);
		editTextUserName = (LabelEditText) findViewById(R.id.editTextUserName);
		editTextPwd = (LabelEditText) findViewById(R.id.editTextPwd);
		checkBoxSavePwd = (CheckBox) findViewById(R.id.checkBoxSavePwd);
		buttonForgotPwd = (Button) findViewById(R.id.buttonForgotPwd);
		buttonForgotPwd.setOnClickListener(this);
		checkBoxSavePwd
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (!isChecked) {
							DataManager.deleteUserPwd(editTextUserName
									.getText().toString());
						}
					}
				});
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user != null) {
			editTextUserName.setText(user.getLoginName());
			if (!user.getLoginName().isEmpty()) {
				String pwd = DataManager.getUserPwd();
				boolean isChked = getPreferencesBoolean(user.getUserID()
						+ ConstantConfig.PREFERENCES_USERPWDCHK);
				if (pwd != null && !pwd.isEmpty() && isChked) {
					String pwdString = pwd;
					try {
						pwdString = AESEncryptor.decrypt(user.getLoginName(),
								pwdString);
					} catch (Exception e) {
						e.printStackTrace();
					}
					editTextPwd.setText(pwdString);
					checkBoxSavePwd.setChecked(isChked);
				}
			}
		}
		TextView textViewVerionValue = (TextView) findViewById(R.id.textViewVerionValue);
		textViewVerionValue.setText(AppApplication.curVer);
		initLocation();
		new AppDownLoadUtil().showAppUpdateDialog(LoginActivity.this);
	}

	/**
	 * 初始化定位服务
	 */
	private void initLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS是否可用
		boolean isOpengGPS = GPSUtil.isOpenGPS(locationManager);
		LogUtil.d(TAG, GPSUtil.isLocationEnabled(locationManager) + "");
		if (!isOpengGPS) {
			showToast("GSP当前已禁用，请在您的系统设置屏幕启动。");
			Intent callGPSSettingIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(callGPSSettingIntent, REQUEST_GPS_CODE);
		}
		// 启动服务
		startService(new Intent(this, GpsService.class));
		// 注册广播
		// receiver = new GPSReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(GpsService.ACTION_GPS);
		// registerReceiver(receiver, filter);
	}

	private void getNewVer() {
		serverService.GetCurVer(1, new HttpReqCallBack<CurVerResponse>() {
			@Override
			public Activity getReqActivity() {
				return null;
			}

			@Override
			public void doSuccess(CurVerResponse result) {
				if (result.isOk()) {
					LoginActivity.this.putPreferencesString(
							ConstantConfig.PREFERENCES_NEWAPPVER,
							result.Data.getVerNo());
					LoginActivity.this.putPreferencesString(
							ConstantConfig.PREFERENCES_NEWAPPURL,
							result.Data.getUrl());
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

	private void register() {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	private void login() {
		final String loginName = editTextUserName.getText().toString();
		if (loginName.isEmpty()) {
			showToast("请输入用户名");
			// 重新获取焦点
			editTextUserName.requestFocusFromTouch();
			return;
		}
		final String pwd = editTextPwd.getText().toString();
		if (pwd.isEmpty()) {
			showToast("请输入密码");
			// 重新获取焦点
			editTextUserName.requestFocusFromTouch();
			return;
		}
		// 保存用户名
		// putPreferencesString(ConstantConfig.PREFERENCES_USERNAME, loginName);
		new ClientGameService().loginServer(loginName, pwd,
				new HttpReqCallBack<UIUserInfoLogin>() {

					@Override
					public Activity getReqActivity() {
						return LoginActivity.this;
					}

					@Override
					public void doSuccess(UIUserInfoLogin result) {
						UIUserInfoLogin user = DataManager.getUserInfo();
						if (user != null
								&& !user.getLoginName().equals(
										result.getLoginName())) {
							SettingsManager.getInstance().setDeviceNumber("");
							if (BluetoothLeService.getInstance() != null) {
								BluetoothLeService.getInstance().disconnect();
								BluetoothLeService.getInstance().connect();
							}
						}
						// if (result.isOk()) {
						ConstantConfig.AUTHOR_TOKEN = result.getAccess_token();
						// 初始化用户皮肤
						SkinManager.getInstance().initSkin();
						// 保存密码
						// DataManager.saveUser(loginName,
						// checkBoxSavePwd.isChecked() ? pwd : "");
						String pwdString = pwd;
						try {
							pwdString = AESEncryptor.encrypt(
									user.getLoginName(), pwd);
						} catch (Exception e) {
							e.printStackTrace();
						}
						DataManager.saveUser(result,
								checkBoxSavePwd.isChecked() ? pwdString : "");
						putPreferencesBoolean(result.getUserID()
								+ ConstantConfig.PREFERENCES_USERPWDCHK,
								checkBoxSavePwd.isChecked());
						finish();
						Intent intent = new Intent(LoginActivity.this,
								ModeActivity.class);
						startActivity(intent);
						// }
					}

					@Override
					public void doError(String result) {
						if (ConstantConfig.Debug) {
							showToast(result);
						}
					}
				});

	}

	private void forgotPwd() {
		Intent intent = new Intent(LoginActivity.this, ResetPwdActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonLogin:
			login();
			break;
		case R.id.buttonResetPwd:
			register();
			break;
		case R.id.buttonForgotPwd:
			forgotPwd();
			break;
		case R.id.buttonAllowed:
		default:
			break;
		}
	}

}
