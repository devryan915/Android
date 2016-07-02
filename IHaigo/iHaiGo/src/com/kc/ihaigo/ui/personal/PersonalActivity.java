package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.myorder.OrderTabActivity;
import com.kc.ihaigo.ui.personal.adapter.PersonalGridViewAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalActivity extends IHaiGoActivity {

	private GridView gv_personal;
	private int[] gv_image = new int[] { R.drawable.personal_order,
			R.drawable.personal_logistics, R.drawable.personal_topic,
			R.drawable.personal_info, R.drawable.personal_goods_collect,
			R.drawable.personal_quality_transport, R.drawable.personal_account };
	// R.drawable.personal_bill,
	private int[] gv_title = new int[] { R.string.personal_order,
			R.string.personal_logistics, R.string.personal_topic,
			R.string.personal_info, R.string.personal_goods_collect,
			R.string.personal_quality_transport, R.string.personal_account };
	// R.string.personal_bill,

	private LinearLayout enter_edit_userinfo;
	private LinearLayout enter_userlogin;
	private LinearLayout ll_pay;

	private TextView tv_username;
	private TextView tv_userlevel;
	private TextView tv_userintroduce;
	private ImageView iv_personal_user_header;

	private String nickName;
	private String renickName;
	private String introduce;
	private String rank;
	private String sex;
	private String head_image_url;
	private String mobile;
	private String TAG = "PersonalActivity";
	private String channel;// 应用发布渠道
	private Object myChannel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		initTitle();
		initComponents();
		findViewById(R.id.iv_enter_userlogin).setEnabled(true);

		refreshLoginStatus(checkLogin());
		// enter_userlogin.setVisibility(View.VISIBLE);
		// enter_edit_userinfo.setVisibility(View.GONE);

		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(),
					PackageManager.GET_CONFIGURATIONS
							| PackageManager.GET_META_DATA);
			if (info.applicationInfo.metaData != null) {
				myChannel = info.applicationInfo.metaData.get("channel");
				channel = myChannel.toString();
				Log.i(TAG, myChannel.toString());
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Constants.MYUUID = getMyUUID();
	}

	@Override
	public void refreshLoginStatus(boolean logon) {
		super.refreshLoginStatus(logon);
		if (logon) {
			findUser();
			enter_userlogin.setVisibility(View.GONE);
			enter_edit_userinfo.setVisibility(View.VISIBLE);
		} else {
			enter_userlogin.setVisibility(View.VISIBLE);
			enter_edit_userinfo.setVisibility(View.GONE);
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		if (PersonalEditUserInfo.class.equals(parentClass)) {
			Bundle resParams = getIntent().getExtras();
			nickName = resParams.getString("nickName");
			tv_username.setText(nickName);
			introduce = resParams.getString("introduce");
			tv_userintroduce.setText(introduce);
			// head_image_url = resParams.getString("headPortrait");
			// ImageLoader.getInstance().displayImage(head_image_url+"",
			// ((ImageView) findViewById(R.id.iv_edit_personal_user_head)));
		} else if (PersonalFirstLogin.class.equals(parentClass)) {
			mobile = getIntent().getStringExtra("mobile");
		} else if (PersonalConfirmSettingPassword.class.equals(parentClass)) {
			mobile = getIntent().getStringExtra("mobile");
		} else if (PersonalUserLogin.class.equals(parentClass)) {
			mobile = getIntent().getStringExtra("mobile");
		} else if (MyMessageActivity.class.equals(parentClass)) {
			mobile = getIntent().getStringExtra("mobile");
			findUser();
		} else if (SettingsActivity.class.equals(parentClass)) {
			// boolean checkLogin = checkLogin();
			// checkLogin= false;
			// refreshLoginStatus(checkLogin);
			Intent intent = new Intent();
			intent.setAction(Constants.LOGON_LOGOUT_ACTION);
			intent.putExtra(Constants.LOG_STATUS, false);
			sendBroadcast(intent);
		} else if (SettingsActivity.class.equals(parentClass)) {
			refreshLoginStatus(checkLogin());
		}
		refreshLoginStatus(checkLogin());
		findViewById(R.id.iv_enter_userlogin).setEnabled(true);
	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		// findViewById(R.id.personal_user_pay).setOnClickListener(this);
		gv_personal = (GridView) findViewById(R.id.personal_gridview);
		gv_personal.setSelector(new ColorDrawable(Color.TRANSPARENT));
		PersonalGridViewAdapter adapter = new PersonalGridViewAdapter(
				PersonalActivity.this, gv_image, gv_title);
		gv_personal.setAdapter(adapter);

		gv_personal.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				int titleId = position;
				Intent intent = new Intent();
				switch (titleId) {
				case 0:
					if (checkLogin()) {
						intent.setClass(PersonalActivity.this,
								OrderTabActivity.class);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								true);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					} else {
						jumpLogin();
					}
					

					break;

				case 1:
					if (checkLogin()) {
						intent.setClass(PersonalActivity.this,
								PersonalLogisticsActivity.class);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								true);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					} else {
						jumpLogin();
					}
					break;
				case 2:
					if (checkLogin()) {
						intent.setClass(PersonalActivity.this,
								PersonalTopicActivity.class);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,

						false);
						intent.putExtra(
								IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					} else {
						jumpLogin();
					}
					break;

				case 3:
					/**
					 * 判断用户是否已经登录，如果未登录让用户去登录，注册
					 */
					if (checkLogin()) {
						intent.setClass(PersonalActivity.this,
								MyMessageActivity.class);
						intent.putExtra("mobile", mobile);
						intent.putExtra("headPortnextrait", head_image_url);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								false);
						intent.putExtra(
								IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					} else {
						jumpLogin();
					}

					break;
				case 4:
					if (checkLogin()) {
						intent.setClass(PersonalActivity.this,
								PersonalCollectionActivity.class);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								false);
						intent.putExtra(
								IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					} else {
						jumpLogin();
					}
					break;
				case 5:
					if (checkLogin()) {
						intent.setClass(PersonalActivity.this,
								QualityTransportActivity.class);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								false);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					} else {
						jumpLogin();
					}
					break;
				// case 6:
				// if (checkLogin()) {
				// intent.setClass(PersonalActivity.this,
				// BillRecordActivity.class);
				// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
				// false);
				// intent.putExtra(
				// IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				// PersonalGroupActivity.group.startiHaiGoActivity(intent);
				// } else {
				// jumpLogin();
				// }
				//
				// break;
				case 6:
					if (checkLogin()) {
						intent.setClass(PersonalActivity.this,
								AccountSafety.class);
						intent.putExtra("mobile", mobile);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								false);
						intent.putExtra(
								IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					} else {
						jumpLogin();
					}

				default:
					break;
				}

			}

		});
	}

	/**
	 * 
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {
		findViewById(R.id.iv_enter_userlogin).setOnClickListener(this);
		findViewById(R.id.user_login_success).setOnClickListener(this);
		enter_edit_userinfo = (LinearLayout) findViewById(R.id.enter_edit_userinfo);
		enter_userlogin = (LinearLayout) findViewById(R.id.enter_userlogin);
		// ll_pay = (LinearLayout) findViewById(R.id.ll_pay);
		// findViewById(R.id.personal_user_pay).setOnClickListener(this);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_userlevel = (TextView) findViewById(R.id.tv_userlevel);
		tv_userintroduce = (TextView) findViewById(R.id.tv_userintroduce);
		iv_personal_user_header = (ImageView) findViewById(R.id.iv_personal_user_header);

		findViewById(R.id.title_right).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.iv_enter_userlogin:
			// Toast.makeText(this, "点击了", 1).show();
			findViewById(R.id.iv_enter_userlogin).setEnabled(false);
			// insertActive();
			jumpLogin();
			break;
		case R.id.user_login_success:
			intent.setClass(PersonalActivity.this, PersonalEditUserInfo.class);
			intent.putExtra("nickName", nickName);
			intent.putExtra("introduce", introduce);
			intent.putExtra("rank", rank);
			intent.putExtra("sex", sex);
			intent.putExtra("headPortnextrait", head_image_url);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		// case R.id.personal_user_pay :
		// /**
		// * 判断用户是否设置交易密码
		// */
		// check();
		// break;
		case R.id.title_right:
			// AliPayUtil.payBill(PersonalActivity.this.getParent(),
			// new Handler(PersonalActivity.this.getParent()
			// .getMainLooper()), "dsfdsgsg", "subject",
			// "sdfdsags", "0.01");
			intent.setClass(PersonalActivity.this, SettingsActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);

			break;
		default:
			break;

		}
	}

	// 获取设备UUID
	private String getMyUUID() {

		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}

	/**
	 * 
	 * @Title: insertActive
	 * @user: helen.yang
	 * @Description: 激活手机用户 void
	 * @throws
	 */
	private void insertActive() {
		final String url = Constants.INSERTACTIVE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		final String myUUID = getMyUUID();
		map.put("deviceid", myUUID);
		map.put("channel", channel);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject json = new JSONObject(result);
								String code = json.getString("code");
								Log.i(TAG, "/-----" + code);
								if ("-500".equals(code)) {
									// Toast.makeText(PersonalActivity.this,
									// "用户激活失败", 0).show();
									intent.setClass(PersonalActivity.this,
											PersonalLoginActivity.class);
									intent.putExtra("which", "PersonalActivity");
									intent.putExtra("code", "");
									intent.putExtra("uuid", myUUID);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else {
									intent.setClass(PersonalActivity.this,
											PersonalLoginActivity.class);
									intent.putExtra("code", code);
									intent.putExtra("uuid", myUUID);
									intent.putExtra("which", "PersonalActivity");
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
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

	/**
	 * 
	 * @Title: findUser
	 * @user: helen.yang
	 * @Description:查询用户信息
	 * @throws
	 */
	private void findUser() {
		final String url = Constants.FINDUSER_URL;
		final Map<String, Object> map = new HashMap<String, Object>();

		Log.i("geek", " +++++++++++++++++????????????" + Constants.USER_ID);
		map.put("userId", Constants.USER_ID);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject obj = new JSONObject(result);
								nickName = obj.getString("nickName");
								tv_username.setText(nickName);
								introduce = obj.getString("introduce");
								tv_userintroduce.setText(introduce);
								rank = obj.getString("rank");
								tv_userlevel.setText(rank);
								sex = obj.getString("sex");
								head_image_url = obj
										.getString("headPortnextrait");
								ImageLoader
										.getInstance()
										.displayImage(
												head_image_url + "",
												((ImageView) findViewById(R.id.iv_personal_user_header)));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i(TAG, "*****************收到信息" + result);
						}
					}
				}, 1);
	}

	/**
	 * 判断用户是否设置交易密码
	 * 
	 * @Title: check
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void check() {

		final String url = Constants.REC_PERSONAL_LOGIN + Constants.USER_ID
				+ "/check";
		Log.i("geek", url);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						final Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject obj = new JSONObject(result);
								String code = obj.getString("code");
								if ("0".equals(code)) {
									Toast.makeText(PersonalActivity.this, "失败",
											1).show();
								} else if ("1".equals(code)) {
									// Toast.makeText(PersonalActivity.this,
									// "修改交易密码流程", 1).show();
									intent.setClass(PersonalActivity.this,
											PersonalUserPay.class);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if ("2".equals(code)) {
									// Toast.makeText(PersonalActivity.this,
									// "设置交易密码流程", 1).show();
									// 弹框提示
									DialogUtil.showSettingTradePsdDialog(
											PersonalActivity.this.getParent(),
											new BackCall() {

												@Override
												public void deal(int which,
														Object... obj) {
													switch (which) {
													case R.id.exit_oks:
														((Dialog) obj[0])
																.dismiss();
														getMsgCode();
														break;

													default:
														break;
													}
												}
											}, null);
								} else if ("3".equals(code)) {
									// Toast.makeText(PersonalActivity.this,
									// "绑定手机流程", 1).show();
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

	/**
	 * 
	 * @Title: getMsgCode
	 * @user: helen.yang
	 * @Description: 获取验证码——设置交易密码 void
	 * @throws
	 */
	private void getMsgCode() {
		final String url = Constants.GETMSGCODE_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		// mobile = getIntent().getStringExtra("mobile");
		map.put("mobile", mobile);
		map.put("msgType", "5");

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
									intent.setClass(PersonalActivity.this,
											SettingTradePasd.class);
									intent.putExtra("key", mobile);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if ("0".equals(st)) {
									Toast.makeText(PersonalActivity.this, "失败",
											0).show();
								} else if ("-503".equals(st)) {
									Toast.makeText(PersonalActivity.this,
											"没有此用户", 0).show();
								} else if ("-505".equals(st)) {
									Toast.makeText(PersonalActivity.this,
											"该用户已存在", 0).show();
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

	public void jumpLogin() {
		Intent intent = new Intent();
		intent.setClass(PersonalActivity.this, PersonalLoginActivity.class);
		intent.putExtra("code", "");
		intent.putExtra("uuid", Constants.MYUUID);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		PersonalGroupActivity.group.startiHaiGoActivity(intent);
	}

	@Override
	protected void back() {
		exitApp();
	}

}
