package com.langlang.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.langlang.data.Account;
import com.langlang.dialog.DataDialog;
import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.SelectCityDialog;
import com.langlang.dialog.SelectUserDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.OfflineLoginManager;
import com.langlang.global.UserInfo;
import com.langlang.interfaces.CityCallBack;
import com.langlang.interfaces.RoleCallBack;
import com.langlang.service.BleConnectionService;
import com.langlang.utils.HttpTools;
import com.langlang.utils.UIUtil;
import com.langlang.utils.UtilStr;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MyMessageActivity extends BaseActivity {
	private final int RESET_DATA=0;
	private final int IS_WARNING=1;

	private String mClient_data;
	
	private Context context;
	private TextView sure;
	private String accountCode;
	private EditText name;// 姓名
	private TextView date;// 出生日期
	private EditText heght;// 身高
	private EditText weight;// 体重
	private EditText add;// 地址
	private EditText postcode;// 邮编
	private EditText telphone;// 电话号码
	private EditText mphone;// 手机号码
	private EditText elses;// 其他补充信息
	private TextView province;//省份
	private RadioGroup sex;// 性别
	private RadioButton male;// 男
	private RadioButton wife;// 女
	private TextView device_name;//设备号
	private Spinner position;//设备号位置
	private String sexString;
	private DataDialog dataDialog;
	private DatePicker datePicker;
	private String datetime;
	private Button sures;
	private String [] m_countries={"1.上部","2.中部","3.下部"};
	private ArrayAdapter<String> adapter;
	SelectCityDialog selectUserDialog;
	private ArrayList<String> monitoring_list = new ArrayList<String>();
	private SaveDialog saveDialog;
	
    private BackgroundLogin mBackgroundLogin = null;
    private OfflineLoginManager mOfflineLoginManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymessage);
		city_index_sp = this.getSharedPreferences("city_index",
				MODE_PRIVATE);

		mOfflineLoginManager = new OfflineLoginManager(getApplicationContext());
		mBackgroundLogin = new BackgroundLogin();
		
		getlist_data();
		context = MyMessageActivity.this;
		getViewId();
		getOnclick();
		mappingData();
	}
	
	/**
	 * 保存时的dialog
	 */
	private void showSaveDialog(){
		saveDialog=new SaveDialog(MyMessageActivity.this,saveCallBack);
		 // 设置进度条风格，风格为圆形，旋转的  
		saveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  

        // 设置ProgressDialog 标题  
//		progressDialog.setTitle("提示");  

        // 设置ProgressDialog提示信息  
		saveDialog.setMessage("正在保存,请稍候...");  

//        // 设置ProgressDialog标题图标  
//		progressDialog.setIcon(R.drawable.img1);  

        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确  
		saveDialog.setIndeterminate(false);
//		progressDialog.setIndeterminate(true);

        // 设置ProgressDialog 是否可以按退回键取消  
		saveDialog.setCancelable(true);
		saveDialog.setCancelable(false);
		saveDialog.show();
	}
	SaveCallBack saveCallBack=new SaveCallBack() {
		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			saveDialog.cancel();
		}
	};
	/**
	 * 映射数据
	 */
	private void mappingData() {
		accountCode=UserInfo.getIntance().getUserData().getAccountCode();
			name.setText(UserInfo.getIntance().getUserData().getFinal_name());
			date.setText(UserInfo.getIntance().getUserData().getDate_of_birth());
			heght.setText(UserInfo.getIntance().getUserData().getHeight()+"");
			weight.setText(UserInfo.getIntance().getUserData().getWeight()+"");
			add.setText(UserInfo.getIntance().getUserData().getAddress());
			postcode.setText(UserInfo.getIntance().getUserData().getPostcode());
			telphone.setText(UserInfo.getIntance().getUserData().getTelephone());
			mphone.setText(UserInfo.getIntance().getUserData().getMobile_phone());
			elses.setText(UserInfo.getIntance().getUserData().getElses());
			device_name.setText(UserInfo.getIntance().getUserData().getDevice_number());
			province.setText(UserInfo.getIntance().getUserData().getProvince());
			
			if ("1".equals(UserInfo.getIntance().getUserData().getSex()+"")) {
				sexString="1";
				sex.check(R.id.mymessage_male);
			} else {
				sexString="0";
				sex.check(R.id.mymessage_wife);
			}
		
	}
	private void setmapping(){
		UserInfo.getIntance().getUserData().setFinal_name(name.getText().toString());
		UserInfo.getIntance().getUserData().setName(name.getText().toString());
		UserInfo.getIntance().getUserData().setDate_of_birth(date.getText().toString());
		UserInfo.getIntance().getUserData().setSex(Integer.valueOf(sexString));
		UserInfo.getIntance().getUserData().setHeight(Integer.valueOf(heght.getText().toString()));
		UserInfo.getIntance().getUserData().setWeight(Integer.valueOf(weight.getText().toString()));
		UserInfo.getIntance().getUserData().setAddress(add.getText().toString());
		UserInfo.getIntance().getUserData().setPostcode(postcode.getText().toString());
		UserInfo.getIntance().getUserData().setTelephone(telphone.getText().toString());
		UserInfo.getIntance().getUserData().setMobile_phone(mphone.getText().toString());
		UserInfo.getIntance().getUserData().setElses(elses.getText().toString());
		UserInfo.getIntance().getUserData().setProvince(province.getText().toString());
		
		if(!UserInfo.getIntance().getUserData().getRole().equals("guardian")){
			stopService(new Intent(MyMessageActivity.this,
					BleConnectionService.class));
			startService(new Intent(MyMessageActivity.this,
					BleConnectionService.class));
		}

	}
	/**
	 * 获取控件Id
	 */
	private void getViewId() {
		province=(TextView)this.findViewById(R.id.mymessage_province_edittext);
		sure = (TextView) this.findViewById(R.id.mymessage_mmsure_button);
		name = (EditText) this.findViewById(R.id.mymessage_name_edittext);
		date = (TextView) this.findViewById(R.id.mymessage_date_edittext);
		heght = (EditText) this.findViewById(R.id.mymessage_heght_edittext);
		weight = (EditText) this.findViewById(R.id.mymessage_weight_edittext);
		add = (EditText) this.findViewById(R.id.mymessage_add_edittext);
		postcode = (EditText) this
				.findViewById(R.id.mymessage_postcode_edittext);
		telphone = (EditText) this.findViewById(R.id.mymessage_phone_edittext);
		mphone = (EditText) this.findViewById(R.id.mymessage_mphone_edittext);
		elses = (EditText) this.findViewById(R.id.mymessage_else_edittext);
		device_name=(TextView)this.findViewById(R.id.mymessage_devicenumber_edittext);
		sex = (RadioGroup) this.findViewById(R.id.mymessage_sex_radiogroup);
		male = (RadioButton) this.findViewById(R.id.mymessage_male);
		wife = (RadioButton) this.findViewById(R.id.mymessage_wife);
		position=(Spinner)this.findViewById(R.id.mymessage_spinner);
		adapter=new ArrayAdapter<String>(MyMessageActivity.this,R.layout.spinner_item_layout,m_countries);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		position.setAdapter(adapter);
		position.setSelection(1);
		position.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				UIUtil.setToast(MyMessageActivity.this, m_countries[position]+"");
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 获取控件点击事件
	 */
	private void getOnclick() {
		sure.setOnClickListener(listener);
		sex.setOnCheckedChangeListener(radioChangeListener);
		date.setOnClickListener(listener);
		province.setOnClickListener(listener);
	}

	/**
	 * 设置控件点击事件
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mymessage_mmsure_button:
				showSaveDialog();
				new mythread().start();

				break;
			case R.id.mymessage_date_edittext:
				 showDateDialog();
				break;
			case R.id.mymessage_province_edittext:
				showCityDialog(); 
				break;
			default:
				break;
			}
		}
	};
	/**
	 * 性别选项
	 */
	private RadioGroup.OnCheckedChangeListener radioChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == male.getId()) {
				sexString="1";
			} else {
				sexString="0";
			}
		}
	};
	String strJsonStr;
	/**
	 * 对象转数组
	 */
	private void TurnObjectArray() {
			List<Account> list = new ArrayList<Account>();
			Account acc = new Account();
			acc.setAccountCode(accountCode);
			acc.setMbirthDay(date.getText().toString());
			acc.setHeight(heght.getText().toString());
			acc.setWeight(weight.getText().toString());
			acc.setAddress(add.getText().toString());
			acc.setPostCode(postcode.getText().toString());
			acc.setTelephoneNo(telphone.getText().toString());
			acc.setMobile(mphone.getText().toString());
			acc.setName(name.getText().toString());
			acc.setRemark(elses.getText().toString());
			acc.setCity(province.getText().toString());
			acc.setSex(sexString);
			list.add(acc);
			Gson sonGson = new Gson();
			strJsonStr = sonGson.toJson(list);
			System.out.println("=dd=" + strJsonStr);
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what==RESET_DATA){
				System.out.println("msg.obj:"+mClient_data);
				saveDialog.cancel();
				if("1".equals(mClient_data)){
					editor=city_index_sp.edit();
					editor.putInt(UserInfo.getIntance().getUserData().getMy_name(), city_index);
					editor.commit();
					 setmapping();
					 UIUtil.setToast(MyMessageActivity.this, "保存成功");
					 startActivity(new Intent(MyMessageActivity.this,
								SetActivity.class));
						MyMessageActivity.this.finish();
				}else if("0".equals(mClient_data)){
					UIUtil.setToast(MyMessageActivity.this, "保存失败");
				}
					
			}
			if (msg.what == IS_WARNING) {
				saveDialog.cancel();
				UIUtil.setToast(MyMessageActivity.this, "网络异常");
			}
		};
	};
	
	class mythread extends Thread{
		@Override
		public void run() {
	
			if (HttpTools.isNetworkAvailable(MyMessageActivity.this) == false) {
				UIUtil.setMessage(handler, IS_WARNING);
				return;
			}
			TurnObjectArray();
			
			mClient_data=Client.getupdateUserInfo(strJsonStr);
			System.out.println("mClient_data:"+mClient_data);
			if(mClient_data==null||"".equals(mClient_data)){
				UIUtil.setMessage(handler, IS_WARNING);
			}
			else{
				if (mBackgroundLogin != null) {
					mBackgroundLogin.start();
				}
				UIUtil.setMessage(handler, RESET_DATA);
			}
			
		}
	}
	/**
	 * 显示日期dialog
	 * 
	 * @author sa
	 */
	private void showDateDialog() {
		dataDialog = new DataDialog(MyMessageActivity.this);
		dataDialog.show();
		datePicker = (DatePicker) dataDialog.findViewById(R.id.data_datePicker);
		sures = (Button) dataDialog.findViewById(R.id.date_sure_button);
		sures.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setData();
				dataDialog.cancel();
			}
		});
		datePicker.init(this.datePicker.getYear(),
				this.datePicker.getMonth(),
				this.datePicker.getDayOfMonth(),
				new MyonDateChangedListener());
	}

	/**
	 * 设置datePicker的点击事件
	 * @author sa
	 */
	private class MyonDateChangedListener implements OnDateChangedListener {
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			MyMessageActivity.this.setData();
		}
	}

	/**
	 * 获取日期
	 */
	private void setData() {
		datetime = this.datePicker.getYear() + "-"
				+ (this.datePicker.getMonth() + 1) + "-"
				+ this.datePicker.getDayOfMonth();
		date.setText(datetime);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(MyMessageActivity.this, SetActivity.class));
			MyMessageActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 获取省份的集合
	 */
	private void getlist_data(){
		String list_data=UserInfo.getIntance().getUserData().getProvince_list();
		try {
			JSONArray jsonArray=new JSONArray(list_data);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject=jsonArray.getJSONObject(i);
				monitoring_list.add(jsonObject.getString("capital"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			monitoring_list.add("数据为空");
			e.printStackTrace();
			
		}
	}
	/**
	 * 选择省份的dialog
	 */
	private void showCityDialog() {
		selectUserDialog = new SelectCityDialog(MyMessageActivity.this,
				monitoring_list,callBack);
		selectUserDialog.show();
		selectUserDialog.setCancelable(true);
	}
	private SharedPreferences city_index_sp;
	private SharedPreferences.Editor editor;
	private int city_index;
	private CityCallBack callBack=new CityCallBack() {
		
		@Override
		public void callBacks(int possions) {
			// TODO Auto-generated method stub
			city_index=possions;
			province.setText(UserInfo.getIntance().getUserData().getTemporary_province());
			selectUserDialog.cancel();
		}
	};
	
	private class BackgroundLogin {
		private BackLoginThread mBackLoginThread = null;
		private boolean loginStarted = false;
		private Object lock = new Object();
		
		public void start() {
			synchronized (lock) {
				if (loginStarted) {
					return;
				}
				loginStarted = true;
				
				mBackLoginThread = new BackLoginThread(this);
				mBackLoginThread.start();
			}
		}
		
		public void stop() {
			synchronized (lock) {
				if (!loginStarted) {
					return;
				}
				loginStarted = false;
				
				if (mBackLoginThread != null) {
					mBackLoginThread.setCancel();
				}
				mBackLoginThread = null;
			}			
		}
		
		public void resetStatus() {
			synchronized (lock) {
				loginStarted = false;
			}				
		}
	}
	class BackLoginThread extends Thread {
		private volatile boolean state = false;
		private BackgroundLogin mBackgroundLogin = null;
		
		public BackLoginThread(BackgroundLogin backgroundLogin) {
			mBackgroundLogin = backgroundLogin;
		}

		public void setCancel() {
			state = true;
		}

		@SuppressWarnings("static-access")
		@Override
		public void run() {
			String uid = UserInfo.getIntance().getUserData().getMy_name();
			if (uid == null || uid.length() <= 0) {
			}
			else {
				String pwd = mOfflineLoginManager.getPassword(uid);
				String MD5pwd = UtilStr.getEncryptPassword(pwd);
				
				String userInfo = "[{username:\"" + uid + "\",password:\""
						+ MD5pwd + "\",version:\"" 
						+ BaseActivity.m_version_base + "\"}]";
				
				String result = Client.getLogin(userInfo);
				System.out.println("action UploadService result:" + result);

				if (state) {
					return;
				}
				
				if (result == null) {
					// 网络异常
				} else if (result.equals("0")) {
					// 用户名不存在
				} else if (result.equals("1")) {
					// 密码不正确
				} else {
					Date now = new Date();
					mOfflineLoginManager.setLastLogin(uid, now);
					UserInfo.getIntance().setLoginMode(UserInfo.LOGIN_MODE_ONLINE);
				}
			}
			mBackgroundLogin.resetStatus();
		}
	}	
}
