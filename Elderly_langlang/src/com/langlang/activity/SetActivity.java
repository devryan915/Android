package com.langlang.activity;

import java.util.ArrayList;

import com.langlang.adapter.SetAdapter;
import com.langlang.elderly_langlang.R;
import com.langlang.global.SettingInfo;
import com.langlang.global.UserInfo;
import com.langlang.service.BleConnectionService;
import com.langlang.service.DataStorageService;
import com.langlang.service.UploadService;
import com.langlang.utils.UIUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SetActivity extends BaseActivity {
	private ListView listView;
	private SetAdapter adapter;
	private TextView name_tw;
	
	private LinearLayout layout;
	private ArrayList<Class<?>> arrayList;
	
	private int mMode = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
//		View view = View.inflate(Config.skin_context, R.layout.activity_set,
//				null);
//		setContentView(view);
		
		
		
		
		getListData();
		listView = (ListView) this.findViewById(R.id.set_listview);
		name_tw = (TextView) this.findViewById(R.id.set_name_tw);
		layout = (LinearLayout) this.findViewById(R.id.set_quit_layout);
		name_tw.setText(UserInfo.getIntance().getUserData().getFinal_name());
		adapter = new SetAdapter(this);
		listView.setAdapter(adapter);
		System.out.println("setactivity role:"
				+ UserInfo.getIntance().getUserData().getRole());
		if ("guardian".equals(UserInfo.getIntance().getUserData().getRole())) {

		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SetActivity.this, arrayList
						.get(position)));
				SetActivity.this.finish();
			}
		});
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});
		findViewById(R.id.set_switch_mode_tw).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogShowRadio(settingInfo.getChangeMode());
				
//				final Intent changeModeIntent = new Intent(BleConnectionService.ACTION_SEND_SWITCH_MODE_FRAME);
//				changeModeIntent.putExtra("MODE", mMode);
//				sendBroadcast(changeModeIntent);
//				
//				if (mMode == 1) {
//					mMode = 2;
//					UIUtil.setToast(SetActivity.this, "已经切换到心率模式");
//				} else if (mMode == 2){					
//					mMode = 1;
//					UIUtil.setToast(SetActivity.this, "已经切换到心电模式");
//				}
			}
		});
		
		settingInfo=new SettingInfo(SetActivity.this);
	}

	private void getListData() {
		System.out.println("setactivity role ------:"
				+ UserInfo.getIntance().getUserData().getRole());
		arrayList = new ArrayList<Class<?>>();
		if ("guardian".equals(UserInfo.getIntance().getUserData().getRole())) {
			arrayList.add(MyMessageActivity.class);
			arrayList.add(SetPasswordActivity.class);
			arrayList.add(SkinActivity.class);
			arrayList.add(UpdateVersionActivity.class);
			arrayList.add(AboutActivity.class);
			System.out.println("setactivity role ------1:");
		} else {
			if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
				arrayList.add(InitializePoseActivity.class);
				arrayList.add(SkinActivity.class);
				arrayList.add(AboutActivity.class);
				System.out.println("setactivity getLogin_mode 1:");				
			}
			else {
				arrayList.add(InitializePoseActivity.class);
				arrayList.add(MyMessageActivity.class);
				arrayList.add(SetPasswordActivity.class);
				arrayList.add(ConfiguratorActivity.class);
				arrayList.add(SkinActivity.class);
				arrayList.add(UpdateVersionActivity.class);
				arrayList.add(AboutActivity.class);
				System.out.println("setactivity role ------2:");
			}
		}
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示！");
		builder.setMessage("确定要退出登录么?")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {						

						stopService(new Intent(SetActivity.this,
								BleConnectionService.class));
						stopService(new Intent(SetActivity.this,
								UploadService.class));
						UserInfo.getIntance().reset();
						startActivity(new Intent(SetActivity.this,
								LoginActivity.class));
						SetActivity.this.finish();						
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(SetActivity.this, MainActivity.class));
			SetActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	////////////////////////////////////////////
	private String []check_off_line={"全速模式","普通模式(心电:心率)1:1","普通模式(心电:心率)2:1","低功耗模式"};
	private SettingInfo settingInfo;
	/** 
	 * 单选框 
	 */  
	private void dialogShowRadio(int index) {  
	    new AlertDialog.Builder(this)  
	    .setTitle("切换模式")  
	    .setIcon(R.drawable.xindian_xin_02)  
	    .setSingleChoiceItems(check_off_line, index, new DialogInterface.OnClickListener() {  
	          
	        public void onClick(DialogInterface dialog, int which) {  
	            // TODO Auto-generated method stub
	        	if (which == 0) {
	        		sendSetEcgPolicyIntent(BleConnectionService.POLICY_ECG_ONLY,
	        							   BleConnectionService.POLICY_SUB_UNKNOWN);
//	        		sendSwitchModeIntent(DataStorageService.MODE_ECG_ECG);
	        	}
	        	else if (which == 3) {
	        		sendSetEcgPolicyIntent(BleConnectionService.POLICY_ECG_HEART_RATE,
							   BleConnectionService.POLICY_SUB_UNKNOWN);
//	        		sendSwitchModeIntent(DataStorageService.MODE_ECG_HEART_RATE);
	        	}
	        	else if (which == 1) {
	        		sendSetEcgPolicyIntent(BleConnectionService.POLICY_MIXED,
							   BleConnectionService.POLICY_SUB_POLICY_MIXED_0);	        		
	        	}
	        	else if (which == 2) {
	        		sendSetEcgPolicyIntent(BleConnectionService.POLICY_MIXED,
							   BleConnectionService.POLICY_SUB_POLICY_MIXED_1);	        		
	        	}
	        	
	        	settingInfo.setChangeMode(which);
								
	            dialog.dismiss();  
	        }  
	    })  
	    .setNegativeButton("取消", null)  
	    .show();  
	} 
	
	private void sendSetEcgPolicyIntent(int policy, int subPolicy) {
		final Intent intent = new Intent(BleConnectionService.ACTION_SET_ECG_POLICY);
		intent.putExtra("POLICY", policy);
		intent.putExtra("SUB_POLICY", subPolicy);
		sendBroadcast(intent);
	}
	
	private void sendSwitchModeIntent(int mode) {
		final Intent intent = new Intent(BleConnectionService.ACTION_SEND_SWITCH_MODE_FRAME);
		intent.putExtra("MODE", mode);
		sendBroadcast(intent);
	}
}
