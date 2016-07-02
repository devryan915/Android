package com.broadchance.wdecgrec.settings;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.broadchance.entity.DownLoadAPPResponse;
import com.broadchance.entity.serverentity.CurVerResponse;
import com.broadchance.manager.AppApplication;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.manager.SkinManager;
import com.broadchance.utils.AppDownLoadUtil;
import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FileUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.SDCardUtils;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.Skinable;
import com.broadchance.wdecgrec.adapter.DialogSkinListAdapter;
import com.broadchance.wdecgrec.login.LoginActivity;
import com.broadchance.wdecgrec.main.ModeActivity;
import com.broadchance.wdecgrec.services.BleDomainService;
import com.broadchance.wdecgrec.services.BluetoothLeService;

public class SettingsActivity extends BaseActivity implements Skinable {
	protected static final String TAG = SettingsActivity.class.getSimpleName();
	private View llUpload;
	private View llMyinfo;
	private View llAddFamily;
	private View llSetpwd;
	private View llSettingsDevInfo;
	private View llChangeSkin;
	private View llAppUpdate;
	private View viewNewVer;

	private View viewChangeSkinIcon;

	// private DialogSkinListAdapter.ViewHolder viewHolder;
	private String selSkinID;

	private Button buttonTitleBack;
	private TextView textViewWaitUpload;
	private TextView textViewCurUpload;
	private Dialog oneKeyUploadDialog;
	private Dialog dialogChangeSkin;
	private Dialog lodingDialog;

	DialogSkinListAdapter adapterSkin;
	boolean isOneUpload = false;

	private boolean isRegistUpload = false;
	// private String curVer;
	// private String newVer = null;
	private final BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleDomainService.ACTION_UPLOAD_UPLOADCHANGED.equals(action)) {
				isOneUpload = false;
				if (lodingDialog != null) {
					lodingDialog.cancel();
					lodingDialog.dismiss();
				}
				String data = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				String[] counts = data.split(":");
				{
					// 上传结束
					if (counts[1].equals(counts[0]) || counts[1].equals("0")) {
						if (oneKeyUploadDialog != null) {
							oneKeyUploadDialog.cancel();
							oneKeyUploadDialog.dismiss();
						}
						if (isRegistUpload) {
							unregisterReceiver(uploadBroadcastReceiver);
						}
						if (counts[1].equals("0")) {
							showToast("无文件可上传");
						} else {
							showToast("一键上传完毕");
						}
						return;
					}
					if (!counts[1].equals("0")
							&& (oneKeyUploadDialog == null || (oneKeyUploadDialog != null && !oneKeyUploadDialog
									.isShowing()))) {
						oneKeyUpload();
					}
					if (textViewWaitUpload != null && textViewCurUpload != null) {
						textViewWaitUpload.setText(counts[1]);
						textViewCurUpload.setText(counts[0] + "/" + counts[1]);
					}

				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		llUpload = findViewById(R.id.llUpload);
		llUpload.setOnClickListener(this);
		llMyinfo = findViewById(R.id.llMyinfo);
		llMyinfo.setOnClickListener(this);
		llAddFamily = findViewById(R.id.llAddFamily);
		llAddFamily.setOnClickListener(this);
		llSetpwd = findViewById(R.id.llSetpwd);
		llSetpwd.setOnClickListener(this);
		llSettingsDevInfo = findViewById(R.id.llSettingsDevInfo);
		llSettingsDevInfo.setOnClickListener(this);
		llChangeSkin = findViewById(R.id.llChangeSkin);
		llChangeSkin.setOnClickListener(this);
		llAppUpdate = findViewById(R.id.llAppUpdate);
		llAppUpdate.setOnClickListener(this);
		viewNewVer = findViewById(R.id.viewNewVer);
		viewChangeSkinIcon = findViewById(R.id.viewChangeSkinIcon);
		buttonTitleBack = (Button) findViewById(R.id.buttonTitleBack);
		buttonTitleBack.setOnClickListener(this);

		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getLoginName());
		// getNewVer();
		String newVer = PreferencesManager.getInstance().getString(
				ConstantConfig.PREFERENCES_NEWAPPVER);
		// 有新的版本
		viewNewVer.setVisibility(!newVer.trim().isEmpty()
				&& newVer.compareTo(AppApplication.curVer) > 0 ? View.VISIBLE
				: View.INVISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SkinManager.getInstance().registerSkin(this);
		loadSkin();
	}

	private IntentFilter makeGattUpdateIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleDomainService.ACTION_UPLOAD_UPLOADCHANGED);
		return intentFilter;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SkinManager.getInstance().unRegisterSkin(this);
	}

	private void showChangeSkin() {
		LayoutInflater inflater = (LayoutInflater) SettingsActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_changeskin, null);
		ListView listViewChangeSkin = (ListView) layout
				.findViewById(R.id.listViewChangeSkin);
		selSkinID = getPreferencesString(DataManager.getUserInfo().getUserID()
				+ ConstantConfig.PREFERENCES_SKINID);
		adapterSkin = new DialogSkinListAdapter(SettingsActivity.this,
				selSkinID);
		listViewChangeSkin.setAdapter(adapterSkin);
		listViewChangeSkin.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DialogSkinListAdapter.ViewHolder holder = (com.broadchance.wdecgrec.adapter.DialogSkinListAdapter.ViewHolder) view
						.getTag();
				selSkinID = holder.skin.getSkinID();
				adapterSkin.setSelSkin(selSkinID);
				adapterSkin.notifyDataSetChanged();
			}
		});
		layout.findViewById(R.id.buttonSave).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!(selSkinID != null && !selSkinID.isEmpty())) {
							showToast("请选择皮肤");
							return;
						}
						if (dialogChangeSkin != null) {
							dialogChangeSkin.cancel();
							dialogChangeSkin.dismiss();
						}
						putPreferencesString(DataManager.getUserInfo()
								.getUserID()
								+ ConstantConfig.PREFERENCES_SKINID, selSkinID);
						SkinManager.getInstance().initSkin();
					}
				});
		dialogChangeSkin = UIUtil.buildDialog(SettingsActivity.this, layout);
		dialogChangeSkin.show();
	}

	// private void getNewVer() {
	// serverService.GetCurVer(1, new HttpReqCallBack<CurVerResponse>() {
	//
	// @Override
	// public Activity getReqActivity() {
	// return SettingsActivity.this;
	// }
	//
	// @Override
	// public void doSuccess(CurVerResponse result) {
	// if (result.isOk()) {
	// try {
	// newVer = result.Data.getVerNo();
	// PackageManager pm = getPackageManager();
	// PackageInfo pinfo = pm.getPackageInfo(
	// ConstantConfig.PKG_NAME,
	// PackageManager.GET_CONFIGURATIONS);
	// curVer = pinfo.versionName;
	// // 有新的版本
	// if (newVer.compareTo(curVer) > 0) {
	// viewNewVer.setVisibility(View.VISIBLE);
	// } else {
	// viewNewVer.setVisibility(View.INVISIBLE);
	// }
	// } catch (NameNotFoundException e) {
	// LogUtil.e(TAG, e);
	// }
	// }
	// }
	//
	// @Override
	// public void doError(String result) {
	// if (ConstantConfig.Debug) {
	// showToast(result);
	// }
	// }
	// });
	// }

	private void showOptionSettings() {
		Intent intent = new Intent(SettingsActivity.this,
				OptionSettingsActivity.class);
		startActivity(intent);
	}

	private void showMyInfo() {
		Intent intent = new Intent(SettingsActivity.this, MyInfoActivity.class);
		startActivity(intent);
	}

	private void addFamily() {
		Intent intent = new Intent(SettingsActivity.this,
				AddFamilyActivity.class);
		startActivity(intent);
	}

	private void modifyPwd() {
		Intent intent = new Intent(SettingsActivity.this,
				ModifyPwdActivity.class);
		startActivity(intent);
	}

	private void returnModeAcitivity() {
		Intent myIntent = new Intent();
		myIntent = new Intent(SettingsActivity.this, ModeActivity.class);
		startActivity(myIntent);
		this.finish();
	}

	private void oneKeyUpload() {
		LayoutInflater inflater = (LayoutInflater) SettingsActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_onekey_upload, null);
		textViewWaitUpload = (TextView) layout
				.findViewById(R.id.textViewWaitUpload);
		textViewCurUpload = (TextView) layout
				.findViewById(R.id.textViewCurUpload);
		textViewWaitUpload.setText("0");
		textViewCurUpload.setText("0");
		oneKeyUploadDialog = UIUtil.buildDialog(SettingsActivity.this, layout);
		oneKeyUploadDialog.show();

		oneKeyUploadDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				try {
					if (isRegistUpload) {
						unregisterReceiver(uploadBroadcastReceiver);
					}
				} catch (Exception e) {
					LogUtil.e(TAG, e);
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			returnModeAcitivity();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.llUpload:
			lodingDialog = UIUtil.showLoadingDialog(SettingsActivity.this,
					"正在初始化...");
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (lodingDialog != null && isOneUpload) {
						lodingDialog.show();
					}
				}
			}, 500);
			sendBroadcast(new Intent(
					BleDomainService.ACTION_UPLOAD_STARTONEKEYMODE));
			registerReceiver(uploadBroadcastReceiver,
					makeGattUpdateIntentFilter());
			isRegistUpload = true;
			isOneUpload = true;
			break;
		case R.id.llMyinfo:
			showMyInfo();
			break;
		case R.id.llAddFamily:
			addFamily();
			break;
		case R.id.llSetpwd:
			modifyPwd();
			break;
		case R.id.llSettingsDevInfo:
			showOptionSettings();
			break;
		case R.id.llChangeSkin:
			showChangeSkin();
			break;
		case R.id.llAppUpdate:
			new AppDownLoadUtil().showAppUpdateDialog(SettingsActivity.this);
			break;
		case R.id.buttonTitleBack:
			returnModeAcitivity();
			break;
		default:
			break;
		}
	}

	@Override
	public void loadSkin() {
		viewChangeSkinIcon
				.setBackground(getSkinDrawable(R.string.skin_settings_changeskin));
	}
}
