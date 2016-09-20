package com.langlang.activity;

import java.util.Date;

import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.UpdateVersionDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.LoggerToServer;
import com.langlang.global.OfflineLoginManager;
import com.langlang.global.UpdateManager;
import com.langlang.global.UserInfo;
import com.langlang.service.BleConnectionService;
import com.langlang.utils.HttpUtils;
import com.langlang.utils.UIUtil;
import com.langlang.utils.UtilStr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 登录界面
 * 
 * @author Administrator
 * 
 */
public class LoginActivity extends BaseActivity {
	private final int IS_FINISH = 1;
	private final int IS_WRA = 2;
	private final int IS_NULL = 3;
	private final int IS_CLIENT_WRA = 4;

	private LinearLayout inputLayout;
	private LinearLayout nameLayout;
	private LinearLayout passwordLayout;
	private TextView nameImage;
	private TextView passwordImage;
	private RelativeLayout loginbg_layout;
	private LinearLayout bg_layout;
	private TextView login_tw;

	private Button login;
	private EditText username;
	private EditText password;
	private CheckBox checkBox, auto_login;
	private String name;
	private String pwd;
	private String data;
	private SharedPreferences sp;
	private SharedPreferences app_skin;
	private SharedPreferences device_mac;
	private long exitTime = 0;
	private SaveDialog loginDialog;
	private loginThread lThread = null;
	private UpdateManager myAutoUpdate;
	private OfflineLoginManager mOfflineLoginManager;
	
	private boolean isAnonymousLogin = false;
	private UpdateVersionDialog updateVersionDialog=null;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == IS_FINISH) {
				data = msg.obj.toString();
				System.out
						.println("action LoginActivity debug login start handler."
								+ data);
				if (UserInfo.getIntance().setUserData(data) != true) {
					UIUtil.setToast(LoginActivity.this, "初始化用户数据出错,请管理员联系.");
					loginDialog.cancel();
					login.setClickable(true);
					return;
				}
				device_mac = LoginActivity.this.getSharedPreferences(
						"device_add", MODE_PRIVATE);
				SharedPreferences.Editor mac_editor = device_mac.edit();
				String device_addString = UserInfo.getIntance().getUserData()
						.getDevice_number();
				String user_name = UserInfo.getIntance().getUserData()
						.getUser_name();
				if (!UserInfo.getIntance().getUserData().getRole()
						.equals("guardian")) {
					if ((device_addString == null)	
							|| (device_addString.length() <= 0)) {
						UIUtil.setToast(LoginActivity.this, "设备号为空,请管理员联系.");
						login.setClickable(true);
						return;
					}
				}

				mac_editor.putString("device_add", device_addString);
				mac_editor.putString("user_name", user_name);
				mac_editor.commit();
//				//版本的判断
				System.out.println("我的版本号："+UserInfo.getIntance().getUserData().getVersion()+":"+m_version_code);
				 if(!UserInfo.getIntance().getUserData().getVersion().equalsIgnoreCase(m_version_code+"")){
					 if(sp.getBoolean("DIALOGISCHECK", false)==false){
						 checkVersionDialog();
						 return;
					 }
					
//					 myAutoUpdate.check();
					
				 }
				LoggerToServer.log("登录成功");
				
				updateOfflineInfo(user_name, 
								  password.getText().toString().trim(), 
								  data);

				forwardToNextActivity();
//				if (UserInfo.getIntance().getUserData().getRole()
//						.equals("guardian")) {
//					startActivity(new Intent(LoginActivity.this,
//							MainActivity.class));
//					loginDialog.cancel();
//					LoginActivity.this.finish();
//				} else {
//					if(checkSupportBle()){
//						startActivity(new Intent(LoginActivity.this,
//								LeBluetoothDeviceScanActivity.class));
//						loginDialog.cancel();
//						LoginActivity.this.finish();
//					}else{
//						showExitDialog();
//					}
//				}
			}
			if (msg.what == IS_WRA) {
				loginDialog.cancel();
				UIUtil.setToast(LoginActivity.this, "密码错误");
				login.setClickable(true);
			}
			if (msg.what == IS_NULL) {
				loginDialog.cancel();
				UIUtil.setToast(LoginActivity.this, "用户名不存在");
				login.setClickable(true);
			}
			if (msg.what == IS_CLIENT_WRA) {
//				loginDialog.cancel();
//				UIUtil.setToast(LoginActivity.this, "网络异常,请稍候再试");
//				login.setClickable(true);
				
//				String strUID = username.getText().toString().trim();
//				String strPassword = password.getText().toString().trim();
//				
//				int offlineLoginResult = offlineLogin(strUID, strPassword);				
//				if (offlineLoginResult == 0) {
//					String userData = mOfflineLoginManager.getUserDataAsString(strUID);
//					
//					// 将存储在本地的用户信息设置到UserInfo里面
//					if (UserInfo.getIntance().setUserData(userData) != true) {
//						notifyOfflineLoginFailed("登录失败,请稍候再试");
//						return;
//					}
//					
//					// 设置为离线登录模式
//					UserInfo.getIntance().setOfflineLogin(true);
//					// 转跳到下一个界面
//					forwardToNextActivity();
//				}
//				else if (offlineLoginResult == -1) {
//					int allowDays = mOfflineLoginManager.getAllowOfflineLoginDays(strUID);
//					if (allowDays == 0) {
//						notifyOfflineLoginFailed("登录失败,请稍候再试");
//						return;						
//					}
//					else {
//						notifyOfflineLoginFailed("由于您已经超过" 
//										    	+ allowDays 
//										    	+ "天没有登录系统,存储在本地的用户信息已经失效," 
//										    	+ "请您连接好网络以后再重新登录");
//						return;
//					}
//				}
//				else {
//					notifyOfflineLoginFailed("登录失败,请稍候再试");
//					return;
//				}
				if (tryOfflineLogin()) {
					forwardToNextActivity();
				}
			}
		};
	};

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		myAutoUpdate=new UpdateManager(this);
		app_skin = getSharedPreferences("app_skin", MODE_PRIVATE);
		setContentView(R.layout.activity_login);
		getViewId();
		changeSkin();
		getOnClick();

		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		ischeckbox();
		cbonclick();
		
		isAnonymousLogin = false;
		mOfflineLoginManager = new OfflineLoginManager(this);
	}

	/**
	 * 获取控件Id
	 */
	private void getViewId() {
		login = (Button) this.findViewById(R.id.login_login_button);
		username = (EditText) this.findViewById(R.id.login_username_edittext);
		password = (EditText) this.findViewById(R.id.login_password_edittext);
		checkBox = (CheckBox) this.findViewById(R.id.login_rember);
		auto_login = (CheckBox) this.findViewById(R.id.login_remberlogin);

		bg_layout = (LinearLayout) this.findViewById(R.id.login_layout);
		login_tw = (TextView) this.findViewById(R.id.login_logo_tw);
		loginbg_layout = (RelativeLayout) this
				.findViewById(R.id.login_loginbg_layout);

		inputLayout = (LinearLayout) this.findViewById(R.id.login_input_layout);
		nameLayout = (LinearLayout) this
				.findViewById(R.id.login_username_layout);
		nameImage = (TextView) this.findViewById(R.id.login_username_image);
		passwordLayout = (LinearLayout) this
				.findViewById(R.id.login_password_layout);
		passwordImage = (TextView) this.findViewById(R.id.login_password_image);

	}

	/**
	 * 换肤
	 */
	private void changeSkin() {
		if ("skin_one".equals(app_skin.getString("skin", "defaul"))) {
			bg_layout.setBackgroundResource(R.drawable.bg_item);
			login_tw.setBackgroundResource(R.drawable.loginbg_02_item);
			login.setBackgroundResource(R.drawable.login_item);
			loginbg_layout.setBackgroundResource(R.drawable.loginbg_05_item);
			inputLayout.setBackgroundResource(R.drawable.main_distance_33);
			nameLayout.setBackgroundResource(R.drawable.loginlist_02_item);
			passwordLayout.setBackgroundResource(R.drawable.loginlist_02_item);
			nameImage.setBackgroundResource(R.drawable.login_user_item);
			passwordImage.setBackgroundResource(R.drawable.login_lock_item);
			username.setTextColor(getResources().getColor(R.color.background));
			password.setTextColor(getResources().getColor(R.color.background));
		}
	}

	/**
	 * 获取控件点击事件
	 */
	private void getOnClick() {
		login.setOnClickListener(listener);
	}

	/**
	 * 设置控件点击事件
	 */
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_login_button:// 用户登录
				UserInfo.getIntance().reset();

				// ConnectivityManager connManager = (ConnectivityManager)
				// getSystemService(CONNECTIVITY_SERVICE);
				// NetworkInfo mWifi =
				// connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				//
				// if (mWifi.isConnected() == false) {
				// showCheckWifiDialog();
				// }

				if (HttpUtils.isNetworkAvailable(getApplicationContext()) == false) {
//					if(checkSupportBle()){
//						showCheckWifiDialog();
//					}else{
//						showExitDialog();
//					}
					showHintDialog();
					
					String strUID = username.getText().toString().trim();
					if ((strUID != null) && (strUID.length() > 0)) {
						if (tryOfflineLogin()) {
							forwardToNextActivity();
						} else {
							tryAnonymousLogin();
						}
					} else {
						tryAnonymousLogin();
					}
					
				} else {

					name = username.getText().toString().trim();
					pwd = password.getText().toString().trim();
					if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
						Toast.makeText(LoginActivity.this, "用户名和密码不能为空",
								Toast.LENGTH_SHORT).show();
					} else {
						login.setClickable(false);
						showHintDialog();
						lThread = new loginThread();
						lThread.start();
						// 登录成功和记住密码框为选中状态才保存用户信息
						if (checkBox.isChecked()) {
							// 记住用户名、密码、
							Editor editor = sp.edit();
							editor.putString("USER_NAME", username.getText()
									.toString());
							editor.putString("PASSWORD", password.getText()
									.toString());
							editor.commit();
						}
					}
				}

				break;
			default:
				break;
			}
		}
	};

	class loginThread extends Thread {
		private volatile boolean state = false;
		private Date first_date = new Date();

		public void setCancel() {
			state = true;
		}

		@SuppressWarnings("static-access")
		@Override
		public void run() {
			String MD5pwd = UtilStr.getEncryptPassword(pwd);
			String userInfo = "[{username:\"" + name + "\",password:\""
					+ MD5pwd + "\",version:\"" + m_version_base + "\"}]";
			String result = Client.getLogin(userInfo);
			System.out.println("result:" + result);
			Date second_date = new Date();
			if ((second_date.getTime() - first_date.getTime()) < (1000 * 2)) {
				try {
					this.sleep((long) (1000 * 1.0));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (state) {
				return;
			}
			if (result == null) {
				UIUtil.setMessage(handler, IS_CLIENT_WRA);
			} else if (result.equals("0")) {
				UIUtil.setMessage(handler, IS_NULL);
			} else if (result.equals("1")) {
				UIUtil.setMessage(handler, IS_WRA);
			} else {
				UIUtil.setMessage(handler, IS_FINISH, result);
			}
		}
	}
	/**
	 * 判断记住密码多选框的状态s
	 */
	private void ischeckbox() {
		if (sp.getBoolean("ISCHECK", false)) {
			checkBox.setChecked(true);
			username.setText(sp.getString("USER_NAME", ""));
			password.setText(sp.getString("PASSWORD", ""));
			if (sp.getBoolean("AUTO_ISCHECK", false)) {
				// 设置默认是自动登录状态
				auto_login.setChecked(true);
				// 跳转界面
				// Intent intent = new Intent(LoginActivity.this,
				// MainsActivity.class);
				// LoginActivity.this.startActivity(intent);
				// LoginActivity.this.finish();
			}
		}
	}

	/**
	 * 记住密码
	 */
	private void cbonclick() {
		// 监听记住密码多选框按钮事件
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (checkBox.isChecked()) {
					sp.edit().putBoolean("ISCHECK", true).commit();
				} else {
					sp.edit().putBoolean("ISCHECK", false).commit();
				}
			}
		});
		// 监听自动登录多选框事件
		auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (auto_login.isChecked()) {
					System.out.println("自动登录已选中");
					sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
				} else {
					System.out.println("自动登录没有选中");
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}
			}
		});
	}

	private void showHintDialog() {
		loginDialog = new SaveDialog(LoginActivity.this, saveCallBack);
		// 设置进度条风格，风格为圆形，旋转的
		loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loginDialog.setMessage("正在登录,请稍候...");
		loginDialog.setIndeterminate(false);
		loginDialog.setCancelable(false);
		loginDialog.show();
	}

	SaveCallBack saveCallBack = new SaveCallBack() {

		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			loginDialog.cancel();
			if (lThread != null) {
				lThread.setCancel();
				lThread = null;
				login.setClickable(true);
			}
		}
	};

	/**
	 * 退出程序
	 */
	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(LoginActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				this.exitApp();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("loginActivity destroy");
	}

	private void showCheckWifiDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
//		builder.setMessage("未发现网络，是否继续登录？")
		builder.setMessage("登录失败，是否使用匿名登录(您将无法保存你的数据和上传数据)？")
				.setCancelable(false)
				.setPositiveButton("匿名登录", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						UserInfo.getIntance().getUserData().setLogin_mode(1);
						UserInfo.getIntance().getUserData().setMy_name(null);
						UserInfo.getIntance().getUserData().setName("我");
						UserInfo.getIntance().getUserData().setRole("user");
						UserInfo.getIntance().getUserData().setUserRole("user");
						UserInfo.getIntance().getUserData()
								.setLimit_heart_dw(30);
						UserInfo.getIntance().getUserData()
								.setLimit_heart_up(180);
						device_mac = LoginActivity.this.getSharedPreferences(
								"device_add", MODE_PRIVATE);
						UserInfo.getIntance()
								.getUserData()
								.setDevice_number(
										device_mac.getString("device_add",
												"000000000000"));
						UserInfo.getIntance()
								.getUserData()
								.setUser_name(
										device_mac.getString("user_name", ""));
						
						isAnonymousLogin = true;
						anonymousLogin();
//						startActivity(new Intent(LoginActivity.this,
//								LeBluetoothDeviceScanActivity.class));
//						LoginActivity.this.finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						isAnonymousLogin = false;
						dialog.cancel();
						return;
					}
				}).show();
	}
/**
 * 蓝牙版本过低的dialog
 */
	private void showExitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("Android系统或蓝牙版本过低,无法使用").setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						System.exit(0);
					}
				}).show();
	}
//	/**
//	 * 发现新版本的dialog
//	 */
//	private void checkVersionDialog() {
//		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("提示");
//		builder.setMessage("发现新版本，是否立即更新").setCancelable(false)
//				.setPositiveButton("立即", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int id) {
//						loginDialog.cancel();
//						if (lThread != null) {
//							lThread.setCancel();
//							lThread = null;
//							login.setClickable(true);
//						}
//						dialog.cancel();
//						 myAutoUpdate.showDownloadDialog();
//					}
//				}).setNegativeButton("稍后", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int id) {
//						if("1".equals(UserInfo.getIntance().getUserData().getVersion_mark())){
//							UIUtil.setToast(LoginActivity.this, "服务器接口更改，必须立即更新");
//							if(loginDialog!=null){
//								loginDialog.cancel();
//							}
//							login.setClickable(true);
//						}
//						else{
//							if (UserInfo.getIntance().getUserData().getRole()
//									.equals("guardian")) {
//								startActivity(new Intent(LoginActivity.this,
//										MainActivity.class));
//								loginDialog.cancel();
//								LoginActivity.this.finish();
//							} else {
//								if(checkSupportBle()){
//									startActivity(new Intent(LoginActivity.this,
//											LeBluetoothDeviceScanActivity.class));
//									loginDialog.cancel();
//									LoginActivity.this.finish();
//								}else{
//									showExitDialog();
//								}
//							}
//							dialog.cancel();
//						}
//						
//					}
//				}).show();
//	}
	@Override
	public void openOptionsMenu() {
		// TODO Auto-generated method stub
		super.openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 1, "退出程序");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) { // 响应每个菜单项(通过菜单项的ID)
		case 1: // do something here
			LoginActivity.this.finish();
			break;
		default: // 对没有处理的事件，交给父类来处理
			return super.onOptionsItemSelected(item);
		} // 返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
		return true;
	}

	/**
	 * 获取系统sdk版本号
	 * @return
	 */
	public int getAndroidOSVersion() {
		int osVersion;
		try {
			osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			osVersion = 0;
		}
		System.out.println("sdk版本号：" + osVersion);
		return osVersion;
	}
	/**
	 * 判断是否支持ble 蓝牙
	 */
	private Boolean checkSupportBle(){
		if( getAndroidOSVersion() <18 ){
			// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
			if (!getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				return false;
			}
			
		}
		return true;
	}

	/**
	 * 发现新版本的dialog
	 */
	private void checkVersionDialog() {
		updateVersionDialog=new UpdateVersionDialog(LoginActivity.this);
		updateVersionDialog.show();
		updateVersionDialog.setTitle("提示");
		updateVersionDialog.setCancelable(false);
		final CheckBox dialog_checkbox=(CheckBox)updateVersionDialog.findViewById(R.id.updateversion_check_warn_cb);
		Button later=(Button)updateVersionDialog.findViewById(R.id.updateversion_later_bt);
		Button at_once=(Button)updateVersionDialog.findViewById(R.id.updateversion_at_once_bt);
		dialog_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(!"1".equals(UserInfo.getIntance().getUserData().getVersion_mark())){
					if(dialog_checkbox.isChecked()){
						sp.edit().putBoolean("DIALOGISCHECK", true).commit();
					}else{
						sp.edit().putBoolean("DIALOGISCHECK", false).commit();	
					}
				}
			}
		});
		later.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if("1".equals(UserInfo.getIntance().getUserData().getVersion_mark())){
					UIUtil.setToast(LoginActivity.this, "服务器接口更改，必须立即更新");
					if(loginDialog!=null){
						loginDialog.cancel();
					}
					login.setClickable(true);
				}
				else{
					if (UserInfo.getIntance().getUserData().getRole()
							.equals("guardian")) {
						startActivity(new Intent(LoginActivity.this,
								MainActivity.class));
						loginDialog.cancel();
						LoginActivity.this.finish();
					} else {
						if(checkSupportBle()){
							startActivity(new Intent(LoginActivity.this,
									LeBluetoothDeviceScanActivity.class));
							loginDialog.cancel();
							LoginActivity.this.finish();
						}else{
							showExitDialog();
						}
					}
					updateVersionDialog.cancel();
				}
			}
		});
		at_once.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginDialog.cancel();
				if (lThread != null) {
					lThread.setCancel();
					lThread = null;
					login.setClickable(true);
				}
				updateVersionDialog.cancel();
				 myAutoUpdate.showDownloadDialog();
			}
		});
	}
	
	/**
	 *  成功登录后将服务器返回信息保存到sp
	 * 
	 **/
	private void updateOfflineInfo(String uid, String password, String userDataStr) {
		mOfflineLoginManager.setLastLogin(uid, new Date());
		mOfflineLoginManager.setPassword(uid, password);
		mOfflineLoginManager.setUserData(uid, userDataStr);
	}
	/**
	 *  离线登录,取用户输入的用户名和密码与sp中的内容进行比较
	 * 
	 * 	return:
	 *  0:  成功
	 *  -1: 本地保存密码失效
	 *  -2: 用户名或者密码不正确
	 *  -10000: 其他登录失败出错
	 **/	
	private int offlineLogin(String userName, String password) {
		
		if (userName == null || password == null) {
			System.out.println("action LoginActivity offlineLogin user name or password null");
			return -2;
		}
		
		Date now = new Date();
		
		System.out.println("action LoginActivity allow:" + mOfflineLoginManager.getAllowDays(userName));
		
		Date lastLogin = mOfflineLoginManager.getLastLogin(userName);
		if (lastLogin == null) {
			return -10000;
		}
		
		int allowDays = mOfflineLoginManager.getAllowOfflineLoginDays(userName);
		
		if (allowDays <= 0) {
			System.out.println("action LoginActivity offlineLogin allow days is 0");
			return -10000;
		}
		
		if ((now.getTime() - lastLogin.getTime()) > allowDays * 24 * 60 * 60 * 1000L) {
			System.out.println("action LoginActivity offlineLogin time out");
			return -1;
		}
		
		if (password.equals(mOfflineLoginManager.getPassword(userName))) {
			System.out.println("action LoginActivity offlineLogin login successfully");
			return 0;
		}
		
		System.out.println("action LoginActivity offlineLogin can not login");
		return -10000;
	}
	/**
	 *  登录失败后提示用户
	 * 
	 **/
	private void notifyOfflineLoginFailed(String msg) {
		loginDialog.cancel();
		UIUtil.setToast(LoginActivity.this, msg);
		login.setClickable(true);
	}
	/**
	 *  转跳到下一个Activity
	 *  并在转跳之前判断是否支持BLE以及Android版本是否
	 **/
	private void forwardToNextActivity() {
		if (UserInfo.getIntance().getUserData().getRole()
				.equals("guardian")) {
			startActivity(new Intent(LoginActivity.this,
					MainActivity.class));
			loginDialog.cancel();
			LoginActivity.this.finish();
		} else {
			if(checkSupportBle()){
				startActivity(new Intent(LoginActivity.this,
						LeBluetoothDeviceScanActivity.class));
				loginDialog.cancel();
				LoginActivity.this.finish();
			}else{
				showExitDialog();
			}
		}		
	}
	

	private boolean tryOfflineLogin() {
		String strUID = username.getText().toString().trim();
		String strPassword = password.getText().toString().trim();
		
		int offlineLoginResult = offlineLogin(strUID, strPassword);				
		if (offlineLoginResult == 0) {
			String userData = mOfflineLoginManager.getUserDataAsString(strUID);
			
			// 将存储在本地的用户信息设置到UserInfo里面
			if (UserInfo.getIntance().setUserData(userData) != true) {
				notifyOfflineLoginFailed("登录失败,请稍候再试");
				return false;
			}
			
			// 设置为离线登录模式
			UserInfo.getIntance().setLoginMode(UserInfo.LOGIN_MODE_OFFLINE);
			
			return true;
		}
		else if (offlineLoginResult == -1) {
			int allowDays = mOfflineLoginManager.getAllowOfflineLoginDays(strUID);
			if (allowDays == 0) {
				notifyOfflineLoginFailed("登录失败,请稍候再试");
				return false;						
			}
			else {
				notifyOfflineLoginFailed("由于您已经超过" 
								    	+ allowDays 
								    	+ "天没有登录系统,存储在本地的用户信息已经失效," 
								    	+ "请您连接好网络以后再重新登录");
				return false;
			}
		}
		else {
			notifyOfflineLoginFailed("登录失败,请稍候再试");
			return false;
		}
	}
	

	private synchronized void tryAnonymousLogin() {
		setIsAnonymousLogin(false);
		showCheckWifiDialog();
	}
	
	private synchronized void anonymousLogin() {
		System.out.println("action LoginActivity tryAnonymousLogin0 " + isAnonymousLogin);
		if (isAnonymousLogin) {
			if(checkSupportBle()){
				System.out.println("action LoginActivity tryAnonymousLogin 0");
//				showCheckWifiDialog();				
				startActivity(new Intent(LoginActivity.this,
						LeBluetoothDeviceScanActivity.class));
				LoginActivity.this.finish();
			}else{
				System.out.println("action LoginActivity tryAnonymousLogin 1");
				
				isAnonymousLogin = false;
				showExitDialog();
			}
		}
	}
	
	private void setIsAnonymousLogin(boolean anonymousLogin) {
		isAnonymousLogin = anonymousLogin;
	}
}
