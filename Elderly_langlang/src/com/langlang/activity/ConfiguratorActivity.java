package com.langlang.activity;

import java.util.List;

import com.langlang.elderly_langlang.R;
import com.langlang.global.OfflineLoginManager;
import com.langlang.global.SettingInfo;
import com.langlang.global.UserInfo;
import com.langlang.utils.UIUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConfiguratorActivity extends BaseActivity {
	private final String UPLOAD_TITLE_NAME="上传方式";
	private final String RSSI_TITLE_NAME="信号声音提示";
	private final String BLE_TITLE_NAME="蓝牙声音提示";
	private final String GPS_TITLE_NAME="是否开启GPS";
	private final String LOGIN_TITLE_NAME="离线登录期限";
	private final String LOGIN_KEY=UserInfo.getIntance().getUserData().getMy_name();
	
	private RelativeLayout off_line_rl;
	private RelativeLayout update_file_rl;
	private RelativeLayout rssi_sount_rl;
	private RelativeLayout ble_sount_rl;
	private RelativeLayout gps_on_rl;
	private TextView message_tw;
	private TextView update_file_tw;
	private TextView rssi_sount_tw;
	private TextView ble_sount_tw;
	private TextView gps_on_tw;
	private TextView off_line_tw;
	
	private String []mselect={"所有网络","仅在Wi-Fi网络下上传"};
	private String []check_off_rssi={"打开","关闭"};
	private String []check_off_ble={"打开","关闭"};
	private String []check_off_gps={"打开","关闭"};
	private String []check_off_line={"3天","7天","不允许离线登录"};
	
	private SettingInfo settingInfo;
	private OfflineLoginManager offlineLoginManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configurator);
		settingInfo=new SettingInfo(this);
		offlineLoginManager=new OfflineLoginManager(this);
		getViewId();
		getOnClick();
		System.out.println("sssssss:"+offlineLoginManager.getAllowDays(LOGIN_KEY));
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkStartGPS();
		update_file_tw.setText(mselect[settingInfo.getUploadNetwork()]);
		rssi_sount_tw.setText(check_off_rssi[settingInfo.getWeaknessNotification()]);
		ble_sount_tw.setText(check_off_ble[settingInfo.getDisconnectedNotification()]);
		gps_on_tw.setText(check_off_gps[settingInfo.getGpsState()]);
		off_line_tw.setText(check_off_line[offlineLoginManager.getAllowDays(LOGIN_KEY)]);
		
	}
	/**
	 * 获取控件ID
	 */
	private void getViewId() {
		off_line_rl=(RelativeLayout)this.findViewById(R.id.con_login_rl);
		gps_on_rl=(RelativeLayout)this.findViewById(R.id.con_gps_rl);
		update_file_rl = (RelativeLayout) this.findViewById(R.id.con_updata_rl);
		rssi_sount_rl = (RelativeLayout) this.findViewById(R.id.con_rssi_rl);
		ble_sount_rl = (RelativeLayout) this.findViewById(R.id.con_ble_rl);
		message_tw = (TextView) this.findViewById(R.id.incon_message_tw);
		update_file_tw = (TextView) this.findViewById(R.id.incon_updata_tw);
		rssi_sount_tw = (TextView) this.findViewById(R.id.incon_rssi_tw);
		ble_sount_tw = (TextView) this.findViewById(R.id.incon_ble_tw);
		gps_on_tw=(TextView)this.findViewById(R.id.incon_gps_tw);
		off_line_tw=(TextView)this.findViewById(R.id.incon_login_tw);
	}

	/**
	 * 获取点击事件
	 */
	private void getOnClick() {
		update_file_rl.setOnClickListener(listener);
		rssi_sount_rl.setOnClickListener(listener);
		ble_sount_rl.setOnClickListener(listener);
		gps_on_rl.setOnClickListener(listener);
		message_tw.setOnClickListener(listener);
		off_line_rl.setOnClickListener(listener);
	}

	/**
	 * 设置点击事件
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.con_updata_rl:
				
				dialogShowRadio(
						mselect,
						UPLOAD_TITLE_NAME,
						settingInfo.getUploadNetwork());
				break;
			case R.id.con_rssi_rl:
				
				dialogShowRadio(
						check_off_rssi,
						RSSI_TITLE_NAME,
						settingInfo.getWeaknessNotification());
				
				break;
			case R.id.con_ble_rl:
				dialogShowRadio(
						check_off_ble,
						BLE_TITLE_NAME,
						settingInfo.getDisconnectedNotification());
				break;
			case R.id.con_gps_rl:
				if(hasGPSDevice(ConfiguratorActivity.this)){
					dialogShowRadio(
							check_off_gps,
							GPS_TITLE_NAME,
							settingInfo.getGpsState());
					
				}else{
					UIUtil.setToast(ConfiguratorActivity.this,"对不起，该手机不支持此功能");
				}
				
				break;
				
			case R.id.con_login_rl:
				dialogShowRadio(
						check_off_line,
						LOGIN_TITLE_NAME,
						offlineLoginManager.getAllowDays(LOGIN_KEY));
				break;		
				
			case R.id.incon_message_tw:
				startActivity(new Intent(ConfiguratorActivity.this,DeviceActivity.class));
				ConfiguratorActivity.this.finish();
				break;

			default:
				break;
			}
		}
	};
	/** 
	 * 单选框 
	 */  
	private void dialogShowRadio(String [] select,final String title,int index) {  
	    new AlertDialog.Builder(this)  
	    .setTitle(title)  
	    .setIcon(R.drawable.xindian_xin_02)  
	    .setSingleChoiceItems(select, index, new DialogInterface.OnClickListener() {  
	          
	        public void onClick(DialogInterface dialog, int which) {  
	            // TODO Auto-generated method stub  

								if (UPLOAD_TITLE_NAME.equals(title)) {

									settingInfo.setUploadNetwork(which);
									update_file_tw.setText(mselect[settingInfo
											.getUploadNetwork()]);

								} else if (RSSI_TITLE_NAME.equals(title)) {

									settingInfo.setWeaknessNotification(which);
									rssi_sount_tw
											.setText(check_off_rssi[settingInfo
													.getWeaknessNotification()]);
								}else if (GPS_TITLE_NAME.equals(title)) {

									if (settingInfo.getGpsState() == which) {
										UIUtil.setToast(
												ConfiguratorActivity.this, "GPS已"+check_off_gps[which]);
									} else {
										Intent intent = new Intent(
												Settings.ACTION_LOCATION_SOURCE_SETTINGS);
										startActivityForResult(intent, 0);
//										return;
									}
								}
								else if (LOGIN_TITLE_NAME.equals(title)) {

									offlineLoginManager.setAllowDays(LOGIN_KEY, which);
									off_line_tw
											.setText(check_off_line[offlineLoginManager
													.getAllowDays(LOGIN_KEY)]);
								
								}
								else {

									settingInfo
											.setDisconnectedNotification(which);
									ble_sount_tw
											.setText(check_off_ble[settingInfo
													.getDisconnectedNotification()]);

								}
	            dialog.dismiss();  
	        }  
	    })  
	    .setNegativeButton("取消", null)  
	    .show();  
	}  
	/**
	 * 判断手机是否支持GPS
	 * @param context
	 * @return
	 */
	public boolean hasGPSDevice(Context context)
	{
		final LocationManager mgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if ( mgr == null ) 
			return false;
		final List<String> providers = mgr.getAllProviders();
		if ( providers == null ) 
			return false;
		return providers.contains(LocationManager.GPS_PROVIDER);
	}
	
	private void checkStartGPS(){
		LocationManager	lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
	     
	     //判断GPS是否正常启动
	     if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	    	 settingInfo.setGpsState(1);
	     }	else{
	    	 settingInfo.setGpsState(0);
	     }
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			startActivity(new Intent(ConfiguratorActivity.this,SetActivity.class));
			ConfiguratorActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	
	}
}
