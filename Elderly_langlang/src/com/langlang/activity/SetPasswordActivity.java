package com.langlang.activity;

import java.util.Date;

import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.OfflineLoginManager;
import com.langlang.global.UserInfo;
import com.langlang.utils.UIUtil;
import com.langlang.utils.UtilStr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetPasswordActivity extends BaseActivity {

	private final int RESET_DATA=0;
	private final int IS_WARNING=1;

	private String mClient_data;
	private Context context;
	private TextView sure;
	private EditText old_password;
	private EditText new_password;
	private EditText sure_password;
	private String oldString;
	private String newString;
	private String sureString;
	private String moble_phone;
	private SaveDialog SetPasswordDialog;
	
	private OfflineLoginManager mOfflineLoginManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpassword);
		
		mOfflineLoginManager = new OfflineLoginManager(this);
		
		context = SetPasswordActivity.this;
		getViewId();
		getOnclick();
		mappingData();

	}
	/**
	 * 设置密码时的dialog
	 */
	private void showSaveDialog(){
		SetPasswordDialog=new SaveDialog(SetPasswordActivity.this,saveCallBack);
		 // 设置进度条风格，风格为圆形，旋转的  
		SetPasswordDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  

        // 设置ProgressDialog 标题  
//		progressDialog.setTitle("提示");  

        // 设置ProgressDialog提示信息  
		SetPasswordDialog.setMessage("正在保存,请稍候...");  

//        // 设置ProgressDialog标题图标  
//		progressDialog.setIcon(R.drawable.img1);  

        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确  
		SetPasswordDialog.setIndeterminate(false);
//		progressDialog.setIndeterminate(true);

        // 设置ProgressDialog 是否可以按退回键取消  
		SetPasswordDialog.setCancelable(true);
		SetPasswordDialog.setCancelable(false);
		SetPasswordDialog.show();
	}
	SaveCallBack saveCallBack=new SaveCallBack() {
		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			SetPasswordDialog.cancel();
		}
	};
	/**
	 * 映射数据
	 */
	private void mappingData() {
		moble_phone=UserInfo.getIntance().getUserData().getMy_name();
		System.out.println("my moble_phong"+moble_phone);
	}

	/**
	 * 获取控件Id
	 */
	private void getViewId() {
		sure = (TextView) this.findViewById(R.id.spd_sure_button);
		old_password = (EditText) this.findViewById(R.id.spd_old_password);
		new_password = (EditText) this.findViewById(R.id.spd_new_password);
		sure_password = (EditText) this.findViewById(R.id.spd_sure_password);
	}

	/**
	 * 获取控件点击事件
	 */
	private void getOnclick() {
		sure.setOnClickListener(listener);
	}

	/**
	 * 设置控件点击事件
	 */
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.spd_sure_button:
				oldString = old_password.getText().toString().trim();
				newString = new_password.getText().toString().trim();
				sureString = sure_password.getText().toString().trim();
				if (TextUtils.isEmpty(oldString)
						|| TextUtils.isEmpty(newString)
						|| TextUtils.isEmpty(sureString)) {
					Toast.makeText(SetPasswordActivity.this, "输入框不能为空",
							Toast.LENGTH_SHORT).show();
				} else if (!newString.equals(sureString)) {
					Toast.makeText(SetPasswordActivity.this, "新密码和确认密码不符",
							Toast.LENGTH_SHORT).show();
				}else if (newString.length()<6||newString.length()>18) {
					Toast.makeText(SetPasswordActivity.this, "密码长度必须是6到18位",
							Toast.LENGTH_SHORT).show();
				} else {
					showSaveDialog();
					new mythread().start();
				}
				break;
			default:
				break;
			}
		}
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			SetPasswordDialog.cancel();
			if (msg.what == RESET_DATA) {
				System.out.println("action setpassword" + mClient_data);
				if (mClient_data.equals("1")) {
					Toast.makeText(SetPasswordActivity.this, "密码修改成功",
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(SetPasswordActivity.this,
							SetActivity.class));
					SetPasswordActivity.this.finish();
				} else {
					Toast.makeText(SetPasswordActivity.this, "原始密码错误",
							Toast.LENGTH_SHORT).show();
				}
			}
			if (msg.what == IS_WARNING) {
				UIUtil.setToast(SetPasswordActivity.this, "网络异常");
			}
		};
	};

	class mythread extends Thread {
		@Override
		public void run() {
			String userInfo = " [{username:\"" + moble_phone + "\",password:\""
					+ UtilStr.getEncryptPassword(oldString)
					+ "\",newpassword:\""
					+ UtilStr.getEncryptPassword(newString) + "\"}]";
			
			mClient_data=Client.getSetPassword(userInfo);
			if(mClient_data==null||"".equals(mClient_data)){
				UIUtil.setMessage(handler, IS_WARNING);
			}
			else{
				updateOfflinePassword(newString);
				
				UIUtil.setMessage(handler, RESET_DATA);
			}
			
//			Message message = Message.obtain();
//			message.what = 1;
//			message.obj = Client.getSetPassword(userInfo);
//			System.out.println("action setpassword" + message.obj.toString());
//			handler.sendMessage(message);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(SetPasswordActivity.this,
					SetActivity.class));
			SetPasswordActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void updateOfflinePassword(String newPassword) {
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		mOfflineLoginManager.setPassword(uid, newPassword);
		Date now = new Date();
		mOfflineLoginManager.setLastLogin(uid, now);
	}
}
