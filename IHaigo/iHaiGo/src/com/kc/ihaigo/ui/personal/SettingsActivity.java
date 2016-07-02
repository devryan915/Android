package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.FileDownload;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.SettingsConfig;
import com.kc.ihaigo.util.Utils;

public class SettingsActivity extends IHaiGoActivity {
	protected final String TAG = "SettingsActivity";

	private TextView quit_now_account;
	private Dialog clearCacheDialog;
	private Dialog infoDialog;
	private BackCall call;
	private TextView settings_version;
	private String website;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initTitle();
		initComponents();
	}

	@Override
	public void refresh() {
		super.refresh();
		refreshLoginStatus(checkLogin());
	}
	private void sysnSettings() {
		String url = Constants.MESSAGE_URL + Constants.USER_ID;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								JSONObject message = resData
										.getJSONObject("notificifations");
								if (message.length() > 0) {
									if (Constants.Debug) {
										Log.d(TAG, "已获取消息推送配置 信息：" + result);
									}
									SettingsConfig config = new SettingsConfig(
											getApplicationContext());
									config.setReceiveMsgSwitch(message
											.getInt("type") == 1 ? true : false);
									config.setReceiveActivityMsg(message
											.getInt("activity") == 1
											? true
											: false);
									config.setReceiveGoodsWarnningMsg(message
											.getInt("good") == 1 ? true : false);
									config.setReceiveOrderMsg(message
											.getInt("orders") == 1
											? true
											: false);
									config.setReceiveShippingMsg(message
											.getInt("waybill") == 1
											? true
											: false);
									config.setReceiveTopicMsg(message
											.getInt("topic") == 1
											? true
											: false);
									config.setStartTime(message
											.getString("startTime"));
									config.setEndTime(message
											.getString("endTime"));
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	@Override
	public void refreshNetStatus(boolean linked) {
		if (linked) {
			// 同步用户推送消息
			if (checkLogin()) {
				sysnSettings();
			}

		}
	}

	/**
	 * 
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {

	}

	/**
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		findViewById(R.id.setting_inform).setOnClickListener(this);
		findViewById(R.id.setting_recommend).setOnClickListener(this);
		findViewById(R.id.setting_love).setOnClickListener(this);
		findViewById(R.id.setting_opinion).setOnClickListener(this);
		findViewById(R.id.setting_version).setOnClickListener(this);
		findViewById(R.id.setting_about).setOnClickListener(this);
		findViewById(R.id.setting_clear_cache).setOnClickListener(this);
		quit_now_account = (TextView) findViewById(R.id.quit_now_account);
		quit_now_account.setOnClickListener(this);
		refreshLoginStatus(checkLogin());
		clearCacheDialog = DialogUtil.showLoadingDialog(
				SettingsActivity.this.getParent(), "");
		call = new MyBackCall();
		infoDialog = DialogUtil.showInfoDialog(
				SettingsActivity.this.getParent(), call);
		settings_version = (TextView) findViewById(R.id.settings_version);
		settings_version.setText(Utils.getAppVersion());
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
			showTabHost = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}

	@Override
	public void refreshLoginStatus(boolean logon) {
		if (logon) {
			quit_now_account.setVisibility(View.VISIBLE);
			sysnSettings();
		} else {
			quit_now_account.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 * @Title: updateApp
	 * @user: ryan.wang
	 * @Description: 下载app并安装
	 * 
	 * @param website
	 *            void
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private void updateApp(String website) {
		FileDownload.downLoadFile(website, new Handler() {
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String res = (String) msg.obj;
				if (!TextUtils.isEmpty(res)) {
					String[] datas = res.split(":");
					long totalsize = Long.parseLong(datas[0]);
					long downloadedsize = Long.parseLong(datas[1]);
					String filePath = datas[2];
					if (totalsize == downloadedsize) {
						Utils.installApk(currentActivity, filePath);
						// 定义通知栏展现的内容信息
						int icon = R.drawable.ic_launcher;
						CharSequence tickerText = getResources().getText(
								R.string.dialog_downloading);
						long when = System.currentTimeMillis();
						Notification notification = new Notification(icon,
								tickerText, when);
						// 定义下拉通知栏时要展现的内容信息
						Context context = getApplicationContext();
						CharSequence contentTitle = getResources().getString(
								R.string.ihaigo_update);
						CharSequence contentText = getResources().getString(
								R.string.ihaigo_update_complete);
						Intent notificationIntent = new Intent(currentActivity,
								IHaiGoMainActivity.class);
						PendingIntent contentIntent = PendingIntent
								.getActivity(currentActivity, 0,
										notificationIntent, 0);
						notification.setLatestEventInfo(context, contentTitle,
								contentText, contentIntent);
						// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
						manager.notify(1, notification);
					}
				}
			}
		});
	}

	/**
	 * 
	 * @Title: checkVersion
	 * @user: ryan.wang
	 * @Description: 检查版本信息 void
	 * @throws
	 */
	private void checkVersion() {
		String url = Constants.VERSION_URL + "getVersion";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		// Android 默认传2
		reqParams.put("type", 2 + "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								JSONObject version = resData
										.getJSONObject("version");
								String verno = version.getString("vernum");
								String title = version.getString("vertitle");
								String desc = version.getString("description");
								int status = version.getInt("status");
								website = version.getString("website");
								if (!Utils.compareVersion(currentActivity,
										verno)) {
									((TextView) infoDialog
											.findViewById(R.id.content))
											.setText(R.string.settings_noversion);
									infoDialog.show();
									return;
								}
								if (1 == status) {
									// 正常状态
									((TextView) infoDialog
											.findViewById(R.id.content))
											.setText(getResources()
													.getString(
															R.string.dialog_finded_newversion)
													+ "\r\n"
													+ title
													+ "\r\n"
													+ verno + "\r\n" + desc);
									((TextView) infoDialog
											.findViewById(R.id.confirm))
											.setText(getResources()
													.getString(
															R.string.dialog_confirm_update));
									call.whichId = 1;
									infoDialog.show();

								} else if (2 == status) {
									// 强制更新
									// 更新App
									updateApp(website);
									Toast.makeText(currentActivity,
											R.string.dialog_update_app,
											Toast.LENGTH_SHORT).show();
								}

							} catch (JSONException e) {
								e.printStackTrace();
								((TextView) infoDialog
										.findViewById(R.id.content))
										.setText(R.string.settings_noversion);
								infoDialog.show();
								return;
							}
						} else {
							((TextView) infoDialog.findViewById(R.id.content))
									.setText(R.string.settings_noversion);
							infoDialog.show();
							return;
						}
					}
				}, 0, R.string.settings_checkversion);
	}

	class MyBackCall extends BackCall {
		@Override
		public void deal(int which, Object... obj) {
			((Dialog) obj[0]).dismiss();
			switch (which) {
				case R.id.choose_oks :
					Intent brd = new Intent();
					brd.setClass(SettingsActivity.this, PersonalActivity.class);
					brd.setAction(Constants.LOGON_LOGOUT_ACTION);
					brd.putExtra(Constants.LOG_STATUS, false);
					sendBroadcast(brd);
					break;
				case 1 :
					((Dialog) obj[0]).cancel();
					// 更新App
					// if (Constants.Debug) {
					// updateApp("http://d1.apk8.com:8020/game/%E5%9B%B4%E4%BD%8F%E7%A5%9E%E7%BB%8F%E7%8C%AB.apk");
					// }
					updateApp(website);
					break;
				default :
					break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.setting_inform :
				intent.setClass(SettingsActivity.this, SettingInform.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.setting_recommend :
				break;
			case R.id.setting_love :
				break;
			case R.id.setting_opinion :
				intent.setClass(SettingsActivity.this, SettingOpinion.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.setting_version :
				checkVersion();
				break;
			case R.id.setting_about :
				intent.setClass(SettingsActivity.this, SettingsAboutUs.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.setting_clear_cache :
				((TextView) clearCacheDialog.findViewById(R.id.content))
						.setText(R.string.setttings_clearing);
				clearCacheDialog.show();
				long clearCache = Utils.clearCache();
				((TextView) infoDialog.findViewById(R.id.content))
						.setText(getText(R.string.settings_clear_complete)
								.toString()
								+ Utils.format(clearCache * 0.001 * 0.001)
								+ "M"
								+ getText(R.string.settings_clear_cachedata)
										.toString());
				clearCacheDialog.cancel();
				infoDialog.show();
				break;
			case R.id.quit_now_account :
				DialogUtil.showLogout(parentGroupActivity, new MyBackCall());
				break;
			default :
				break;
		}
	}
}
