package com.broadchance.wdecgrec.login;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.serverentity.CurVerResponse;
import com.broadchance.entity.serverentity.ServerResponse;
import com.broadchance.entity.serverentity.UIDevice;
import com.broadchance.entity.serverentity.UIDeviceResponseList;
import com.broadchance.manager.AppApplication;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.manager.SkinManager;
import com.broadchance.utils.AESEncryptor;
import com.broadchance.utils.AppDownLoadUtil;
import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.GPSUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.NetUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.main.EcgActivity;
import com.broadchance.wdecgrec.main.ModeActivity;
import com.broadchance.wdecgrec.services.BluetoothLeService;
import com.broadchance.wdecgrec.services.GpsService;
import com.broadchance.wdecgrec.services.GuardService;
import com.broadchance.wdecgrec.test.Test;
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
	LoginActivity current;
	private UIUserInfoLogin user;
	Dialog offDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// getNewVer();
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(this);
		buttonRegister = (Button) findViewById(R.id.buttonResetPwd);
		buttonRegister.setOnClickListener(this);
		editTextUserName = (LabelEditText) findViewById(R.id.editTextUserName);
		editTextPwd = (LabelEditText) findViewById(R.id.editTextPwd);
		editTextPwd.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_GO:
					logon();
					break;
				default:
					break;
				}
				return false;
			}
		});
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
		user = DataManager.getUserInfo();
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
		// 暂时取消gps定位
		// boolean gps = SettingsManager.getInstance().getSettingsGPS();
		// if (gps) {
		// initLocation();
		// }
		// 暂时取消app更新
		// new AppDownLoadUtil().showAppUpdateDialog(LoginActivity.this);
		if (ConstantConfig.Debug) {
			new Test().test();
		}

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
				} else {
					showToast("操作失败");
				}
			}
		});
	}

	private void register() {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	private void logon() {
		if (current != null)
			return;
		current = this;
		final String loginName = editTextUserName.getText().toString();
		if (loginName.isEmpty()) {
			showToast("请输入用户名");
			// 重新获取焦点
			editTextUserName.requestFocusFromTouch();
			current = null;
			return;
		}
		final JSONObject param;
		try {
			param = new JSONObject();
			param.put("mobile", loginName);
			clientService.getKey(param, new HttpReqCallBack<ServerResponse>() {

				@Override
				public Activity getReqActivity() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void doSuccess(ServerResponse result) {
					current = null;
					if (result.isOK()) {
						try {
							ConstantConfig.CERTKEY = result.getDATA()
									.getString("certkey");
							param.put("holtermobile", loginName);
							_logon(param);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						showToast(result.getErrmsg());
					}
				}

				@Override
				public void doError(String result) {
					current = null;
					if (!ConstantConfig.Debug) {
						showToast(result);
					} else {
						if (!NetUtil.isConnectNet()) {
							user = DataManager.getUserInfo();
							if (user != null && !user.getLoginName().isEmpty()) {
								String pwd = DataManager.getUserPwd();
								if (pwd != null && !pwd.isEmpty()) {
									try {
										offDialog = UIUtil
												.buildTipDialog(
														LoginActivity.this,
														getString(R.string.dialog_title_offlogin),
														getString(R.string.dialog_offlogin_content),
														new OnClickListener() {
															@Override
															public void onClick(
																	View v) {
																if (offDialog != null) {
																	offDialog
																			.cancel();
																	offDialog
																			.dismiss();
																}
																Intent intent = new Intent(
																		LoginActivity.this,
																		ModeActivity.class);
																startActivity(intent);
																finish();
															}
														},
														new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																if (offDialog != null) {
																	offDialog
																			.cancel();
																	offDialog
																			.dismiss();
																}
															}
														},
														getString(R.string.dialog_button_ok),
														getString(R.string.dialog_button_cancel));
										offDialog.show();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

						} else {
							if (ConstantConfig.Debug) {
								showToast(result);
							} else {
								showToast("操作失败");
							}
						}
					}

				}
			});
		} catch (JSONException e) {
			current = null;
			e.printStackTrace();
		}
	}

	private void _logon(final JSONObject pram) {
		clientService.login(pram, new HttpReqCallBack<ServerResponse>() {

			@Override
			public Activity getReqActivity() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void doSuccess(ServerResponse result) {
				current = null;
				if (result.isOK()) {
					try {
						String orderno = result.getDATA().getString("orderno");
						UIUserInfoLogin user = new UIUserInfoLogin();
						String logName = pram.getString("holtermobile");
						user.setUserID(logName);
						user.setAccess_token(orderno);
						user.setLoginName(logName);
						user.setNickName(pram.getString("holtermobile"));
						user.setMacAddress(result.getDATA().getString("device"));
						user.isOverTime = 0;
						// user.setMacAddress("74:DA:EA:9F:93:36");
						// user.setMacAddress("D4:F5:13:79:80:E7");
						// user.setMacAddress("D4:F5:13:79:C3:AE");
						DataManager.saveUser(user, "mima");
						// 初始化用户皮肤
						SkinManager.getInstance().initSkin();
						if (GuardService.Instance != null) {
							GuardService.Instance.resetBleCon();
						}
						/**
						 * 更新配置
						 */
						clientService.getAlertCFG();
						finish();
						// Intent intent = new Intent(LoginActivity.this,
						// EcgActivity.class);
						Intent intent = new Intent(LoginActivity.this,
								ModeActivity.class);
						startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					showToast(result.getErrmsg());
				}
			}

			@Override
			public void doError(String result) {
				current = null;
				showToast(result);
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
			// 返回桌面
			Intent home = new Intent(Intent.ACTION_MAIN);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			home.addCategory(Intent.CATEGORY_HOME);
			startActivity(home);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonLogin:
			logon();
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
