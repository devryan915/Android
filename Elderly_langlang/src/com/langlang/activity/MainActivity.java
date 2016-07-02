package com.langlang.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.langlang.dialog.PraiseDialog;
import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.dialog.SelectUserDialog;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.LoggerToServer;
import com.langlang.global.UserInfo;
import com.langlang.interfaces.RoleCallBack;
import com.langlang.manager.WarningHteManager;
import com.langlang.manager.WarningTumbleManager;
import com.langlang.service.BleConnectionService;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
import com.langlang.service.UploadService;
import com.langlang.utils.HttpTools;
import com.langlang.utils.MiscUtils;
import com.langlang.utils.Program;
import com.langlang.utils.SDChecker;
import com.langlang.utils.ScreenShotUtils;
import com.langlang.utils.UIUtil;
import com.langlang.view.MyToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	private final static int UPDATE_WEATHER = 1;
//	private final static int UPDATE_ON_CONNECT = 1112;
	private final static int UPDATE_OFF_CONNECT = 1111;
	private final static int UPDATE_OFF_LINE = 1113;
	private final int UPDATE_ON_LINE_NO_DATA=1115;
	private final static int UPDATE_STEP = 2;
	private final static int UPDATE_HEARTRATE = 3;
	private final static int UPDATE_HTE_WARNING = 4;
	private final static int UPDATE_TUMBLE_WARNING = 5;
	private final static int UPDATE_MENTAL_STRESS = 6;
	private final static int UPDATE_SLEEP_QUALITY = 7;
	private final static int UPDATE_POSTURE_TUMBLE = 8;
	private final static int UPDATE_POSTURE_AND_GAIT = 9;
	private final static int UPDATE_RESULT_DATE_ZERO = 19;
	
	
//	private final int RSSI_DISCOVERED=110;
	private final static int UPDATE_ROLE_DATA = 10;
	private final static int SEND_ALARM = 11;
	private final static int SEND_ALARM_WRAING = 12;
	
	private final static int SEND_ALARM_WRAING_HEART = 112;
	private final static int SEND_PRISE_WRAING=113;
	private final static int ACCEPT_PRISE_WRAING=114;
	private final static int EXAMINE_PRISE_WRAING=115;
	private final static int UPDATE_PRAISE_COUNT = 13;
	private final static int UPDATE_NULL=16;
	private final static int UPDATE_EXAMINE=14;
	private final static float RATING_BAR_STEP_SIZE = 0.5f;
	private final static int SEND_PRAISE = 15;
	
	private final static int UPDATA_VOLTAGE = 20;
	private final static int UPDATA_RSSI=21;
	
	private final static int UPDATA_BLE_STATE=30;
	
	private final static int ALERT_SD_STATUS = 40;
	
	private final int SHARE_IMAGE = 50; 
	private final int SHOW_CAPTURE_SUCCESS = 51;
	
	private final static int POSTURE_STAND = 0;
	private final static int POSTURE_SIT = 1;
	private final static int POSTURE_LAY= 23;
	private final static int POSTURE_WALK = 2;
	private final static int POSTURE_RUN = 3;
	private final static int POSTURE_TUMBLE = 10000;

	private final static String TUMBLE_FLAG = "TUMBLE_FLAG";
	private final static int TUMBLE_FLAG_ON = 1;
	private final static int TUMBLE_FLAG_OFF = 0;
	private final static String TUMBLE_TIME = "TUMBLE_TIME";

	private SharedPreferences spTumbleFlag;
	private SharedPreferences app_skin;
	private SharedPreferences raise_sp;
	private int mPostureData = POSTURE_STAND;

	private LinearLayout bg_layout;
	private LinearLayout main_notify;
	private LinearLayout username_layout;
	
	private TextView useriamge_tw;
	private TextView usertext_tw;
	private TextView yueliang_tw;
	private TextView mentalimage_tw;
	
	private TextView baffle_1;
	private TextView baffle_2;
	private TextView baffle_4;
	private TextView baffle_7;
	private TextView baffle_8;
	private TextView baffle_11;
	private TextView baffle_12;
	private TextView baffle_13;
	private TextView baffle_14;
	private RelativeLayout sleep_relative;
	private RelativeLayout heartRate_relative;
	private RelativeLayout mentalStress_relative;
	private RelativeLayout activity_relative;
//	private ListView listView;
//	private MessageAdapter adapter;
	private TextView set_ui;
	private TextView sosOrLogo;
	private TextView sleep_praise;
	private TextView heart_praise;
	private TextView activity_praise;
	private TextView mts_praise;
	private TextView share_textview;
	private TextView voltage_tw;//
	private ImageView ble_rssi_image;//蓝牙信号
	private TextView ble_rssi_tw;//蓝牙信号
	private TextView weather_tw;
	private TextView result_tw;
//	private Button name_textview;
	private RatingBar ratingBar;// 睡眠指数
	private String path_image=Program.getSDLangLangImagePath() + "/main_image.png";
	private long exitTime = 0;
	private Context context;
	private PraiseDialog praiseDialog;
//	private String weather_data;// 天气信息
	private float voltage;
	
	private RelativeLayout titlebg_layout;
	private TextView blerssi_tw;
	private TextView blevoltage_tw;
	private TextView txtHeartRate;
	private TextView txtStepCount;
	private TextView txtHteWarning;
	private TextView txtMentalStress;
	private TextView txtTumble;
	private ImageView pose_image;
	private ImageView heart_imageview;
	private ImageView user_pull_im;
	private ImageView result_iamge;
	private AnimationDrawable animationDrawable;
	// private Spinner spinner;
	private ArrayList<String> monitoring_list = new ArrayList<String>();
	private ArrayList<String> user_names = new ArrayList<String>();
//	private ArrayAdapter<String> adapter_sp;
	private ArrayList<String> heart_List = new ArrayList<String>();
	private ArrayList<String> activity_List = new ArrayList<String>();
	private ArrayList<String> sleep_List = new ArrayList<String>();
	private ArrayList<String> mental_stress_List = new ArrayList<String>();
	private ExamineThread examineThread=null;
	
	private int mStepCount = 0;
	private int mHeartRate = 0;
	private int mHteWarning = 0;
//	private int mPosture = 0;
	private int mMentalStress = 0;
	private int mTumble = 0;
	private int mCalories = 0;
	private String step_string;
	private String hearw_string;
	private String mental_string;
	// private String role;
	private String my_name;
	private String tips;
	private float mRatingbar_count = 0.0f;
	private String mSleep_Quotient;
	private String mRole_user = UserInfo.getIntance().getUserData().getUserRole();
	private String mType;
	private String mPraise_pose_count;
	private String mPraise_heart_count;
	private String mPraise_sleep_count;
	private String mPraise_mental_count;
	private String mStatus;
	private int[] mDisplayHte = { 0, 0, 0, 0, 0 };
	int mCurrHte = 0;
	int mHteCount = 0;

	private Timer timer1s = new Timer();
	private Timer timer5s = new Timer();
	private Timer timer10s = new Timer();

	private Timer timer20s = new Timer();
	private Timer timer2h = new Timer();
	private Timer timer1m=null ;

	private WarningHteManager warningHteManager = new WarningHteManager(this);
	private WarningTumbleManager warningTumbleManager = new WarningTumbleManager(
			this);

//	ArrayList<MessageInfo> m_arrayList = new ArrayList<MessageInfo>();
	private SharedPreferences sp;
	
	private int mBleState = BleConnectionService.STATE_DISCONNECTED; 
	
	SDChecker sdChecker = new SDChecker(this, SDChecker.SPACE_M_0);
	SDChecker sdAlert = new SDChecker(this);
	SDChecker sdCheckFirst = new SDChecker(this, SDChecker.SPACE_M_1);
	
	private boolean mIsFromScan = false;
	private Bitmap mScreenBitmap = null;
	
	private Object lockFinish = new Object();
	private boolean mFinishFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		System.out.println("是否支持GPS:"+hasGPSDevice(this));
//		if(hasGPSDevice(this)){
//			System.out.println("是否支持GPSs:"+hasGPSDevice(this));
//			checkStartGPS();
//		}
	
		app_skin=getSharedPreferences("app_skin",MODE_PRIVATE);
		raise_sp=getSharedPreferences("raise_data",MODE_PRIVATE);
		setContentView(R.layout.activity_main);

		context = MainActivity.this;
		sp = this.getSharedPreferences("mainkonwledge", MODE_PRIVATE);
		spTumbleFlag = this.getSharedPreferences(TUMBLE_FLAG, MODE_PRIVATE);
		System.out.println("初始角色："+UserInfo.getIntance().getUserData().getUserRole()+":"+UserInfo.getIntance().getUserData().getRole());
		mapping();
		getViewId();
		
//		initializeUIData();
		checkImage();
		 changeSkin();
		checkOnclick();
		setLogo();
		getonClick();
//		new KnowledgeThread().start();
		initSDCardDir();
		checkStartServices();
		sosOrLogo.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(mGaurdian_role){
					System.out.println("action MainActivity sos onLongClick");
					if (HttpTools.isNetworkAvailable(MainActivity.this) == false) {
						Toast.makeText(MainActivity.this, "网络连接失败,请连接网络",
								Toast.LENGTH_SHORT).show();
						System.out.println("action MainActivity sos network is not available.");
					} else {
						System.out.println("action MainActivity sos network is available.");
						new AlarmThread().start();
					}
				}
				return false;
			}
		});
		
//		checkStartThread();
		
		LoggerToServer.log("成功进入主界面");
		
		mIsFromScan = false;
		Intent intent=getIntent();
		if (intent != null) {
	        String fromActivity = intent.getStringExtra("EXTRA_FROM_ACTIVITY");
	        if (fromActivity != null && fromActivity.equals("ScanActivity")) {
	        	mIsFromScan = true;
	        	sendIntentToGetBleState();
	        }
	        else {
				if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
		        	setBleState(GlobalStatus.getInstance().getBleState());	        	
		        	UIUtil.setMessage(handler, UPDATA_RSSI, GlobalStatus.getInstance().getRssi());
				}
				else {
					if (UserInfo.getIntance().getUserData().getUserRole().equals("user")
							|| UserInfo.getIntance().getUserData().getUserRole().equals("dualRole")) {
			        	setBleState(GlobalStatus.getInstance().getBleState());	        	
			        	UIUtil.setMessage(handler, UPDATA_RSSI, GlobalStatus.getInstance().getRssi());
					}
				}
	        }
		}
		
		setFinishFlag(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		acquireWakeLock();
		setTimerTask();

		init();
		setBroadcastReceiver();
		// registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregisterReceiver(mGattUpdateReceiver);
//		releaseWarkLock();
		unsetBroadcastReceiver();
		cancelTimer();
	}
	/**
	 * 根据皮肤判断图片资源
	 */
	private void checkImage(){
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			pose_image.setBackgroundResource(R.drawable.stand_28_item);
		}else{
			pose_image.setBackgroundResource(R.drawable.stand_28);
		}
	}
	private void init() {
		System.out.println("init role："
				+ UserInfo.getIntance().getUserData().getUserRole());
		if (UserInfo.getIntance().getUserData().getUserRole() != null) {
			if (UserInfo.getIntance().getUserData().getUserRole()
					.equals("user")
					|| UserInfo.getIntance().getUserData().getUserRole()
							.equals("dualRole")) {
				if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {					
				}
				else {
					new SleepDataThread().start();
				}
				initData();
				initUI();
				System.out.println("init role："
						+ UserInfo.getIntance().getUserData().getUserRole());
				
				if (mIsFromScan) {
					mIsFromScan = false;
					
					sdCheckFirst.checkAndAlert();
				} else {
					sdChecker.checkAndAlert();
				}
				
				setBleState(GlobalStatus.getInstance().getBleState());
			} else {
				System.out.println("init role："
						+ UserInfo.getIntance().getUserData().getUserRole());
//				new SelectUserThread().start();
			}
		}
	}

	private void initData() {
		mStepCount = GlobalStatus.getInstance().getCurrentStep();
		mHeartRate = GlobalStatus.getInstance().getHeartRate();
		mCalories = GlobalStatus.getInstance().getCalories();
		updateHteWarningData();
		mMentalStress = GlobalStatus.getInstance().getStressLevel();
		updateTumbleWarningData();
		
		if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			voltage = GlobalStatus.getInstance().getVoltage();
		}
		else {
			if (UserInfo.getIntance().getUserData().getUserRole()
					.equals("user")
					|| UserInfo.getIntance().getUserData().getUserRole()
							.equals("dualRole")) {
				voltage = GlobalStatus.getInstance().getVoltage();
			} else {
			}
		}		

		if (getTumbleFlagForSP() == TUMBLE_FLAG_ON) {
			mPostureData = POSTURE_TUMBLE;
			showPrompt();
		} else {
			int posture = GlobalStatus.getInstance().getPosture();
			int gait = GlobalStatus.getInstance().getGait();
			updatePostureData(posture, gait);
		}
	}

	private void showPrompt() {
		if (getTumbleFlagForSP() == TUMBLE_FLAG_ON) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("姿态已经被设置为[跌倒],是否现在清除该标识?")
					.setCancelable(false)
					.setPositiveButton("清除标识",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									clearTumbleFlag();
									mPostureData = POSTURE_STAND;
									updateTumbleUI();
								}
							})
					.setNegativeButton("以后再清除",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
		}
	}

	private void initUI() {
		updateStepCountUI();
		updateHeartRateUI();
		updateHteWarningUI();
		updateMentalStressUI();

		updateTumbleWarningUI();
		updatePostureAndGaitUI();
	}

	/**
	 * 获取控件Id
	 */
	private void getViewId() {
		baffle_1 = (TextView) this.findViewById(R.id.main_baffle_o);
		baffle_2 = (TextView) this.findViewById(R.id.main_baffle_t);
		baffle_4 = (TextView) this.findViewById(R.id.main_baffle_f);
		baffle_7 = (TextView) this.findViewById(R.id.main_baffle_severn);
		baffle_8 = (TextView) this.findViewById(R.id.main_baffle_eight);
		baffle_11 = (TextView) this.findViewById(R.id.main_baffle_eleven);
		baffle_12 = (TextView) this.findViewById(R.id.main_baffle_twelve);
		baffle_13 = (TextView) this.findViewById(R.id.main_baffle_thirteen);
		baffle_14 = (TextView) this.findViewById(R.id.main_baffle_fourteen);
		sosOrLogo = (TextView) this.findViewById(R.id.main_sos_textviews);
		voltage_tw=(TextView)this.findViewById(R.id.main_voltage_tw);
		ble_rssi_image=(ImageView)this.findViewById(R.id.main_rssi_image);
		sleep_relative = (RelativeLayout) this
				.findViewById(R.id.main_sleep_relative);
		heartRate_relative = (RelativeLayout) this
				.findViewById(R.id.main_hearrate_relative);
		mentalStress_relative = (RelativeLayout) this
				.findViewById(R.id.main_mentalstreess_relative);
		activity_relative = (RelativeLayout) this
				.findViewById(R.id.main_activity_relative);
		heart_praise = (TextView) this.findViewById(R.id.main_heart_raise);
		activity_praise = (TextView) this.findViewById(R.id.main_sleepp_raise);
		sleep_praise = (TextView) this.findViewById(R.id.main_activity_praise);
		mts_praise = (TextView) this.findViewById(R.id.main_mts_praise);
		set_ui = (TextView) this.findViewById(R.id.main_set_textview);
		share_textview = (TextView) this.findViewById(R.id.main_share_textview);
		ratingBar = (RatingBar) this.findViewById(R.id.main_sleep_ratingBar);
		ratingBar.setStepSize(RATING_BAR_STEP_SIZE);
//		ratingBar.setRating(3f);
		ratingBar.setRating(0.0f);
		System.out.print("my retingbar size:" + ratingBar.getRating());
//		listView = (ListView) this.findViewById(R.id.main_notify_listview);
//		adapter = new MessageAdapter(context);
//		listView.setAdapter(adapter);
		txtHeartRate = (TextView) this.findViewById(R.id.main_heatcount_tw);
		txtStepCount = (TextView) this.findViewById(R.id.main_stepcount_image);
		txtHteWarning = (TextView) this
				.findViewById(R.id.main_hte_warning_textview);
		txtMentalStress = (TextView) this
				.findViewById(R.id.main_mentalstress_textview);
		txtTumble = (TextView) this
				.findViewById(R.id.main_tumble_times_textview);
		user_pull_im=(ImageView)this.findViewById(R.id.main_pulldown_tw);
		pose_image = (ImageView) this.findViewById(R.id.main_pose_image);
		heart_imageview=(ImageView)this.findViewById(R.id.main_heart_iamgeview);
		ble_rssi_tw=(TextView)this.findViewById(R.id.main_rssi_tw);
		bg_layout=(LinearLayout)this.findViewById(R.id.main_layout);
		main_notify=(LinearLayout)this.findViewById(R.id.main_notify_layout);
		titlebg_layout=(RelativeLayout)this.findViewById(R.id.main_titlebg_layout);
		username_layout=(LinearLayout)this.findViewById(R.id.main_username_layout);
		useriamge_tw=(TextView)this.findViewById(R.id.main_userimage_tw);
		usertext_tw=(TextView)this.findViewById(R.id.main_userimage_text);
		usertext_tw.setText(my_name);
		mentalimage_tw=(TextView)this.findViewById(R.id.main_mental_im);
		yueliang_tw=(TextView)this.findViewById(R.id.main_yueliang);
		
		blerssi_tw=(TextView)this.findViewById(R.id.main_blerssi_tw);
		blevoltage_tw=(TextView)this.findViewById(R.id.main_blevoltage_tw);
//		heart_imageview.setBackgroundResource(R.anim.anim_heart);
//		animationDrawable = (AnimationDrawable) heart_imageview.getBackground();
//		animationDrawable.start();
		
		weather_tw=(TextView)this.findViewById(R.id.main_weather_tw);
		result_tw=(TextView)this.findViewById(R.id.main_result_tw);
		result_iamge=(ImageView)this.findViewById(R.id.main_result_image);
		if(!UserInfo.getIntance().getUserData().getUserRole().equals("guardian")){
			result_tw.setText(getFinalResult());
		}
		weather_tw.setText(sp.getString("knowledge", ""));
		checkshowimage();
		if (UserInfo.getIntance().getUserData().getUserRole().equals("guardian")) {
			result_iamge.setVisibility(View.GONE);
		}
	}	
	/**
	 * 根据角色判断部分控件是否显示
	 */
	private void checkshowimage(){
		if(UserInfo.getIntance().getUserData().getUserRole().equals("guardian")){
			titlebg_layout.setVisibility(View.GONE);
		}else{
			titlebg_layout.setVisibility(View.VISIBLE);
		}
		
	}
	/**
	 * 初始化数据
	 */
	private void initializeUIData() {
		txtTumble.setText("0");
		txtStepCount.setText("0");
		txtMentalStress.setText("0%");
		txtHteWarning.setText("0");
		txtHeartRate.setText("0");
		heart_praise.setText("0");
		activity_praise.setText("0");
		mts_praise.setText("0");
		sleep_praise.setText("0");
		result_tw.setText("");
		if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			voltage = GlobalStatus.getInstance().getVoltage();
			UIUtil.setMessage(handler, UPDATA_VOLTAGE);
		}
		else {
			if (UserInfo.getIntance().getUserData().getUserRole()
					.equals("user")
					|| UserInfo.getIntance().getUserData().getUserRole()
							.equals("dualRole")) {
				voltage = GlobalStatus.getInstance().getVoltage();
				UIUtil.setMessage(handler, UPDATA_VOLTAGE);
			}
			else {
//				voltage_tw.setText("-");
				voltage = 0;
				UIUtil.setMessage(handler, UPDATA_VOLTAGE);
			}
		}		
		
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			pose_image.setBackgroundResource(R.drawable.stand_28_item);
			ble_rssi_image.setBackgroundResource(R.drawable.main_wave_zero_item);
		}else{
			pose_image.setBackgroundResource(R.drawable.stand_28);
			ble_rssi_image.setBackgroundResource(R.drawable.main_wave_zero);
		}
	
		ratingBar.setRating(0.0f);
	}
	/**
	 * 换肤
	 */
	private void changeSkin(){
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			bg_layout.setBackgroundResource(R.drawable.bg_item);
			main_notify.setBackgroundResource(R.drawable.main_notice_item);
			useriamge_tw.setBackgroundResource(R.drawable.user_jin_03_item);
			set_ui.setBackgroundResource(R.drawable.setting_click_item);
			share_textview.setBackgroundResource(R.drawable.share_click_item);
			activity_praise.setBackgroundResource(R.drawable.praise_activity_item);
			heart_praise.setBackgroundResource(R.drawable.praise_item);
			sleep_praise.setBackgroundResource(R.drawable.praise_item);
			mts_praise.setBackgroundResource(R.drawable.praise_item);
			sleep_relative.setBackgroundResource(R.drawable.pose_click_item);
			activity_relative.setBackgroundResource(R.drawable.sleep_click_item);
			mentalStress_relative.setBackgroundResource(R.drawable.mental_click_item);
			heartRate_relative.setBackgroundResource(R.drawable.hear_click_item);
			heart_imageview.setBackgroundResource(R.drawable.heart_001_item);
			mentalimage_tw.setBackgroundResource(R.drawable.icon_22_item);
			yueliang_tw.setBackgroundResource(R.drawable.icon_20_item);
			usertext_tw.setTextColor(getResources().getColor(R.color.white));
			blerssi_tw.setTextColor(getResources().getColor(R.color.background));
			blevoltage_tw.setTextColor(getResources().getColor(R.color.background));
			voltage_tw.setTextColor(getResources().getColor(R.color.background));
			titlebg_layout.setBackgroundResource(R.drawable.main_wave_blackbg_item);
			ble_rssi_tw.setTextColor(getResources().getColor(R.color.background));
		}
	}
	private void initSDCardDir() {
		Program.createSDLangLangDir();
		Program.createSDLangLangDataDir();
//		Program.createSDLangLangTempDir();
		Program.createSDLangLangImageDir();
	}

	/**
	 * 获取控件点击事件
	 */

	private void getonClick() {
		baffle_1.setOnClickListener(listener);
		baffle_2.setOnClickListener(listener);
		baffle_4.setOnClickListener(listener);
		baffle_7.setOnClickListener(listener);
		baffle_8.setOnClickListener(listener);
		baffle_11.setOnClickListener(listener);
		baffle_12.setOnClickListener(listener);
		baffle_13.setOnClickListener(listener);
		baffle_14.setOnClickListener(listener);
		sleep_relative.setOnClickListener(listener);
		heartRate_relative.setOnClickListener(listener);
		mentalStress_relative.setOnClickListener(listener);
		activity_relative.setOnClickListener(listener);
		activity_praise.setOnClickListener(listener);
		sleep_praise.setOnClickListener(listener);
		mts_praise.setOnClickListener(listener);
		heart_praise.setOnClickListener(listener);
		set_ui.setOnClickListener(listener);
		share_textview.setOnClickListener(listener);
		System.out.println("角色值："+UserInfo.getIntance().getUserData().getRole());
		if (UserInfo.getIntance().getUserData().getRole().equals("user")) {

		} else {
			username_layout.setOnClickListener(listener);
		}

	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.main_sleep_relative:
				if (checkIntoItem()) {
					startActivity(new Intent(MainActivity.this,
							ActivityLevelActivity.class));
					MainActivity.this.finish();
				}
				break;
			case R.id.main_hearrate_relative:
				if (checkIntoItem()) {
					startActivity(new Intent(MainActivity.this,
							HeartRateActivity.class));
					MainActivity.this.finish();
				}
				break;
			case R.id.main_mentalstreess_relative:
				if (checkIntoItem()) {
					startActivity(new Intent(MainActivity.this,
							MentalStressActivity.class));
					MainActivity.this.finish();
				}
				break;
			case R.id.main_activity_relative:
				if (checkIntoItem()) {
					if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {// Not
																					// login
						UserInfo.getIntance();
						if (!UserInfo.isInDemoMode()) {
							UIUtil.setToast(MainActivity.this,
									UserInfo.PROMPT_NOT_LOGIN);
							
						} else {
							startActivity(new Intent(MainActivity.this,
									SleepNewActivity.class));
							MainActivity.this.finish();
						}
					} else {
//						startActivity(new Intent(MainActivity.this,
//								SleepActivity.class));
//						MainActivity.this.finish();
						startActivity(new Intent(MainActivity.this,
								SleepNewActivity.class));
						MainActivity.this.finish();
					}
				}
				break;
			case R.id.main_set_textview:
				startActivity(new Intent(MainActivity.this, SetActivity.class));
				MainActivity.this.finish();
				break;
			case R.id.main_share_textview:
//				UIUtil.setToast(MainActivity.this, "aaaaa");
				setShareClickable(false);
				mScreenBitmap = ScreenShotUtils.takeScreenShot(MainActivity.this);
				if (mScreenBitmap == null) {
					setShareClickable(true);
					UIUtil.setMessage(handler, SHARE_IMAGE, false);
					return;
				}
				UIUtil.setMessage(handler, SHOW_CAPTURE_SUCCESS);
				new ScreenShotThread().start();
				
				break;
			case R.id.main_sleepp_raise:
				mType="4";
				checkPriaseOrElse();
				break;
			case R.id.main_heart_raise:
				mType="3";
				checkPriaseOrElse();
				break;
			case R.id.main_activity_praise:
				mType="1";
				checkPriaseOrElse();
				break;
			case R.id.main_mts_praise:
				mType="2";
				checkPriaseOrElse();
				break;
			case R.id.main_username_layout:
			
				showUserDialog();
				break;
			default:
				break;
			}
		}
	};
	SelectUserDialog selectUserDialog;

	private void showUserDialog() {
		selectUserDialog = new SelectUserDialog(MainActivity.this,
				monitoring_list, roleCallBack);
		selectUserDialog.show();
		selectUserDialog.setCancelable(true);
	}
	/**
	 * 判断在监护人的情况下必须选择监护对象才可以进入子界面
	 * @return
	 */
	private boolean checkIntoItem(){
		if("guardian".equals(UserInfo.getIntance().getUserData().getUserRole())&&
							 UserInfo.getIntance().getUserData().getName().equals(
									 UserInfo.getIntance().getUserData().getFinal_name())){
			UIUtil.setToast(MainActivity.this, "请选择监护对象，再进入此界面");
			return false;
		}
		return true;
	}
	private RoleCallBack roleCallBack = new RoleCallBack() {
		@Override
		public void gaurdianCallBacks() {
			// TODO Auto-generated method stub
			initializeUIData();
//			checkStartThread();
			mGaurdian_role = true;
			my_name = UserInfo.getIntance().getUserData().getName();
			usertext_tw.setText(my_name);
			if (UserInfo.getIntance().getUserData().getRole()
					.equals("guardian")) {
				mRole_user = "guardian";
				UserInfo.getIntance().getUserData().setUserRole(mRole_user);
				result_tw.setText("");
				result_iamge.setVisibility(View.GONE);
//				resetBroadcastReceiver();
				resetTimer();
			} else if (UserInfo.getIntance().getUserData().getRole()
					.equals("dualRole")) {
				new SleepDataThread().start();
				mRole_user = "dualRole";
				UserInfo.getIntance().getUserData().setUserRole(mRole_user);
				result_tw.setText(getFinalResult());
				setBroadcastReceiver();
				if(timer1m!=null){
					timer1m.cancel();
				}
				resetTimer();
				updateRoleToUser();
			}
			setLogo();
//			UserInfo.getIntance().getUserData().setUserRole(mRole_user);
//			result_tw.setText(getFinalResult());
			checkshowimage();
			selectUserDialog.cancel();
			System.out.println("初始角色："+UserInfo.getIntance().getUserData().getUserRole()+":"+UserInfo.getIntance().getUserData().getRole());
		}

		@Override
		public void userCallBacks() {
			// TODO Auto-generated method stub
			result_iamge.setVisibility(View.GONE);
			initializeUIData();
//			checkStartThread();
			mGaurdian_role = false;
			unsetBroadcastReceiver();
			my_name = UserInfo.getIntance().getUserData().getName();
			usertext_tw.setText(my_name);
			mRole_user = "guardian";
			UserInfo.getIntance().getUserData().setUserRole(mRole_user);
			checkshowimage();
//			resetBroadcastReceiver();
			resetTimer();
			setLogo();
			selectUserDialog.cancel();
			System.out.println("初始角色："+UserInfo.getIntance().getUserData().getUserRole()+":"+UserInfo.getIntance().getUserData().getRole());
		}
	};
	


	/**
	 * 根据角色判断是查看还是点赞
	 * 
	 * @param arrayList
	 */
	private void checkPriaseOrElse() {
		if (UserInfo.getIntance().getUserData().getRole().equals("guardian")) {
			if (mGaurdian_role) {// 当用户角色是监护人的时候判断是否是自己
//				UIUtil.setToast(MainActivity.this, "oo");
			} else {
				new sendThread().start();
			}
		} else {
			if (UserInfo.getIntance().getUserData().getUserRole()
					.equals("guardian")) {
				new sendThread().start();
				checkStartThread();
			} else {
				showThreadDialog();
				examineThread=new ExamineThread();
				examineThread.start();
			}
		}

	}
	private void showInteract(ArrayList<String> arrayList){
		praiseDialog = new PraiseDialog(context, arrayList);
		praiseDialog.show();
		praiseDialog.setCancelable(false);
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

	/**
	 * 退出程序
	 */
	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			UIUtil.setToast(MainActivity.this, "再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			
			stopServices();
			// cancelTimer();
//			new exitThread().start();
			MainActivity.this.finish();
			// System.exit(0);
			
		}
	}

	/**
	 * 分享图片
	 * 
	 * @param path
	 */
	private void shareImage(String path) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
		// intent.putExtra(Intent.EXTRA_TEXT,
		// "I have successfully share my message through my app (分享自city丽人馆)");
		File file = new File(path);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	/**
	 * 根据角色判断是否开启服务
	 */
	private void checkStartServices() {
		if (!UserInfo.getIntance().getUserData().getRole().equals("guardian")) {
			if (!MiscUtils.isServiceRunning(MainActivity.this,
					UploadService.SERVICE_NAME)) {
				Intent startUploadServiceIntent = new Intent(MainActivity.this,
						UploadService.class);
				MainActivity.this.startService(startUploadServiceIntent);
			}
			System.out
					.println("action MainActivity startServices BleConnectionService");
			startService(new Intent(MainActivity.this,
					BleConnectionService.class));
		}
	}

	private void stopServices() {
		stopService(new Intent(MainActivity.this, BleConnectionService.class));
		stopService(new Intent(MainActivity.this, UploadService.class));
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPDATE_WEATHER) {
//				adapter.clear();
//				List<MessageInfo> list = getList();
//				for (MessageInfo info : list) {
//					adapter.addItem(info);
//				}
//				adapter.notifyDataSetChanged();
				
				weather_tw.setText(sp.getString("knowledge", ""));
			
			}
			else if(msg.what==UPDATE_RESULT_DATE_ZERO){
				result_iamge.setVisibility(View.GONE);
				UIUtil.setToast(MainActivity.this, "服务器访问失败");
			}
			else if (msg.what == UPDATE_SLEEP_QUALITY) {
				if (mSleep_Quotient == null) {
					mRatingbar_count = 0.0f;
				} else if (mSleep_Quotient.equals("1")) {
					mRatingbar_count = 5.0f;
				} else if (mSleep_Quotient.equals("2")) {
					mRatingbar_count = 4.0f;
				} else if (mSleep_Quotient.equals("3")) {
					mRatingbar_count = 2.5f;
				}else if (mSleep_Quotient.equals("4")) {
					mRatingbar_count = 2.5f;
				}else{
					mRatingbar_count = 0.0f;
				}
				ratingBar.setRating(mRatingbar_count);
			}
			else if (msg.what == UPDATE_HEARTRATE) {
				txtHeartRate.setText(Integer.toString(mHeartRate));

				System.out.println("ratingBar===mHeartRate Heart");

			}
			else if (msg.what == UPDATE_STEP) {
				txtStepCount.setText(Integer.toString(mStepCount));
				System.out.println("ratingBar===mHeartRate step");
			}
			else if (msg.what == UPDATE_HTE_WARNING) {
				txtHteWarning.setText(Integer.toString(mHteWarning));
				txtStepCount.setText(Integer.toString(mStepCount));
				System.out.println("ratingBar===mHeartRate htewr"+mHteWarning);
			}
			else if (msg.what == UPDATE_MENTAL_STRESS) {
				txtMentalStress.setText(String.format("%3d", mMentalStress)
						+ "%");
				System.out.println("ratingBar===mHeartRate mental");
			}
			else if (msg.what == UPDATE_TUMBLE_WARNING) {
				txtTumble.setText(Integer.toString(mTumble));
				System.out.println("ratingBar===mHeartRate tumble");
			}
			else if (msg.what == UPDATE_POSTURE_AND_GAIT) {
				updatePostureUI();
			}
			else if (msg.what == UPDATE_POSTURE_TUMBLE) {
				updatePostureUI();
			}
			else if (msg.what == UPDATE_ROLE_DATA) {
				result_iamge.setVisibility(View.VISIBLE);
				updatePostureUI();
				txtHeartRate.setText(Integer.toString(mHeartRate));
				txtHteWarning.setText(Integer.toString(mHteWarning));
				txtTumble.setText(Integer.toString(mTumble));
				txtMentalStress.setText(String.format("%3d", mMentalStress)
						+ "%");
				txtStepCount.setText(Integer.toString(mStepCount));
				if (mSleep_Quotient.equals("1")) {
					mRatingbar_count = 5.0f;
				} else if (mSleep_Quotient.equals("2")) {
					mRatingbar_count = 4.0f;
				} else if (mSleep_Quotient.equals("3")) {
					mRatingbar_count = 2.5f;
				}else if (mSleep_Quotient.equals("4")) {
					mRatingbar_count = 2.5f;
				}else{
					mRatingbar_count = 0.0f;
				}
				result_tw.setText(getFinalResult());
				ratingBar.setRating(mRatingbar_count);
				System.out.println("ratingBar===mHeartRate:" + mHeartRate
						+ " mTumble:" + mTumble + " mHteWarning:" + mHteWarning
						+ " mMentalStress：" + mMentalStress + " mStepCount:"
						+ mStepCount + " mRatingbar_count:" + mRatingbar_count);
				// onShowChanged(mRole_user);
			}
			else if (msg.what == SEND_ALARM) {
				String statedate = msg.obj.toString();
				if ("1".equals(statedate)) {
					Toast.makeText(MainActivity.this, "呼叫信号已发出",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_LONG)
							.show();
				}
			}
			else if (msg.what == SEND_ALARM_WRAING) {
				if (threadDialog!=null) {
					threadDialog.cancel();
				}
				UIUtil.setToast(MainActivity.this, "紧急呼救时，网络异常");
			}
			else if (msg.what == SEND_PRAISE) {
				String send_result = msg.obj.toString();
				if (send_result.equals("1")) {
					UIUtil.setToast(MainActivity.this, "发送成功");
				} else {
					UIUtil.setToast(MainActivity.this, "发送失败");
				}
			}
			else if(msg.what==UPDATE_PRAISE_COUNT){
				System.out.println("getMessageCount getAcceptInvite result:0");
				//判断在有新的点赞消息来时，用提示音提示用户
				try {
				SharedPreferences.Editor raise_ed=raise_sp.edit();
				
				synchronized (lockFinish) {
					if (mFinishFlag) {
						return;
					}
				}
				
				if(!UserInfo.getIntance().getUserData().getRole().equals("guardian")&&
				    UserInfo.getIntance().getUserData().getUserRole().equals(
				    UserInfo.getIntance().getUserData().getRole())){
					if(mPraise_heart_count.equals("null")&&mPraise_sleep_count.equals("null")&&
							mPraise_pose_count.equals("null")&&mPraise_mental_count.equals("null")){
						raise_ed.putString("mPraise_heart_count", mPraise_heart_count);
						raise_ed.putString("mPraise_sleep_count", mPraise_sleep_count);
						raise_ed.putString("mPraise_pose_count", mPraise_pose_count);
						raise_ed.putString("mPraise_mental_count", mPraise_mental_count);
						raise_ed.commit();
						System.out.println("getMessageCount getAcceptInvite result:1");
					}else{
						System.out.println("getMessageCount getAcceptInvite result:2");	
						if(!raise_sp.getString("mPraise_heart_count", "0").equalsIgnoreCase(mPraise_heart_count)||
						   !raise_sp.getString("mPraise_sleep_count", "0").equalsIgnoreCase(mPraise_sleep_count)||
						   !raise_sp.getString("mPraise_pose_count", "0").equalsIgnoreCase(mPraise_pose_count)||
						   !raise_sp.getString("mPraise_mental_count", "0").equalsIgnoreCase(mPraise_mental_count)){
							
							MyToast.show(MainActivity.this, "", true, Toast.LENGTH_SHORT,R.raw.prsise).show();
							raise_ed.putString("mPraise_heart_count", mPraise_heart_count);
							raise_ed.putString("mPraise_sleep_count", mPraise_sleep_count);
							raise_ed.putString("mPraise_pose_count", mPraise_pose_count);
							raise_ed.putString("mPraise_mental_count", mPraise_mental_count);
							raise_ed.commit();
							System.out.println("getMessageCount getAcceptInvite result:3");	
						}
					}
				}
				heart_praise.setText(StringToZero(mPraise_heart_count));
				sleep_praise.setText(StringToZero(mPraise_sleep_count));
				activity_praise.setText(StringToZero(mPraise_pose_count));
				mts_praise.setText(StringToZero(mPraise_mental_count));
				
				}
				catch (Exception e) {
					System.out.println("action MainActivity UPDATE_PRAISE_COUNT exception e=" + e.toString());
				}
			}
			else if(msg.what==UPDATE_EXAMINE){
				if(threadDialog!=null){
					threadDialog.cancel();
				}
				if(mType.equals("1")){
					showInteract(sleep_List);
				}
				else if(mType.equals("2")){
					showInteract(mental_stress_List);
				}
				else if(mType.equals("3")){
					showInteract(heart_List);
				}
				else if(mType.equals("4")){
					showInteract(activity_List);
				}
			}
			else if (msg.what == UPDATA_VOLTAGE) {
				if(voltage==0){
					voltage_tw.setText("-");
				}
				else if(voltage<2.31){
					voltage_tw.setText("低");
				}else if(voltage>2.3&&voltage<2.6){
					voltage_tw.setText("中");
				}else{
					voltage_tw.setText("高");
				}
			}			
			else if(msg.what==UPDATA_RSSI){
				if(!UserInfo.getIntance().getUserData().getUserRole().equals("guardian")){
					
				
				ble_rssi_image.setVisibility(View.VISIBLE);
				ble_rssi_tw.setVisibility(View.GONE);
				int getRssi = Integer.valueOf(msg.obj.toString());
				if("skin_one".equals(app_skin.getString("skin","defaul"))){
					if (getRssi < -80) {
						System.out.println("信号1："+getRssi);
						ble_rssi_image.setBackgroundResource(R.drawable.main_wave_low_item);
					}
					else if (getRssi > -81 && getRssi < -55) {
						System.out.println("信号2："+getRssi);
						ble_rssi_image.setBackgroundResource(R.drawable.main_wave_nomal_item);
					}else{
						if(getRssi!=0){
							System.out.println("信号3："+getRssi);
							ble_rssi_image.setBackgroundResource(R.drawable.main_wave_high_item);
						}else{
							ble_rssi_image.setBackgroundResource(R.drawable.main_wave_zero_item);
							System.out.println("信号4："+getRssi);
						}					
					}
				}else{
				if (getRssi < -80) {
					System.out.println("信号1："+getRssi);
					ble_rssi_image.setBackgroundResource(R.drawable.main_wave_low);
				}
				else if (getRssi > -81 && getRssi < -55) {
					System.out.println("信号2："+getRssi);
					ble_rssi_image.setBackgroundResource(R.drawable.main_wave_nomal);
				}else{
					if(getRssi!=0){
						System.out.println("信号3："+getRssi);
						ble_rssi_image.setBackgroundResource(R.drawable.main_wave_high);
					}else{
						ble_rssi_image.setBackgroundResource(R.drawable.main_wave_zero);
						System.out.println("信号4："+getRssi);
					}					
				}}
//				rssi_tw.setText("" + getRssi);
				}
				}
			else if(msg.what==UPDATE_NULL){
				if(threadDialog!=null){
					threadDialog.cancel();
				}
				UIUtil.setToast(MainActivity.this, "列表为空");
			}			
			else if (msg.what == UPDATA_BLE_STATE) {
				if (mBleState == BleConnectionService.STATE_CONNECTED) {
					ble_rssi_tw.setVisibility(View.GONE);
					ble_rssi_image.setVisibility(View.VISIBLE);					
				} else {
					ble_rssi_image.setVisibility(View.GONE);
					ble_rssi_tw.setVisibility(View.VISIBLE);
					
					if (mBleState == BleConnectionService.STATE_DISCONNECTED) {
						ble_rssi_tw.setText("设备未连接");
					}
					else if (mBleState == BleConnectionService.STATE_CONNECTING) {
						ble_rssi_tw.setText("正在连接...");
					}
					else if (mBleState == BleConnectionService.STATE_SCANNING) {
						ble_rssi_tw.setText("正在搜索设备");
					}
					else {
						ble_rssi_tw.setText("未知设备状态");
					}
				}
			}			
			else if (msg.what == ALERT_SD_STATUS) {
				sdAlert.checkAndAlert();
			}
			else if (msg.what == SHOW_CAPTURE_SUCCESS) {
				UIUtil.setToast(MainActivity.this, "截图成功");
			}
			
			else if (msg.what == SHARE_IMAGE) {
				boolean success = (Boolean) msg.obj;
				if (success) {
					shareImage(path_image);
				}
				else {
					UIUtil.setToast(MainActivity.this, "无法截图");
				}
				
				setShareClickable(true);
			}
			else if(msg.what==SEND_ALARM_WRAING_HEART){
				UIUtil.setToast(MainActivity.this, "请求用户数据时，网络异常");
			}
			else if(msg.what==SEND_PRISE_WRAING){
				UIUtil.setToast(MainActivity.this, "发送点赞时，网络异常");
			}
			else if(msg.what==ACCEPT_PRISE_WRAING){

//				UIUtil.setToast(MainActivity.this, "接受点赞时，网络异常");

			}
			else if(msg.what==EXAMINE_PRISE_WRAING){
				UIUtil.setToast(MainActivity.this, "查看点赞时，网络异常");
			}
			else if (msg.what == UPDATE_ON_LINE_NO_DATA) {
				
				UIUtil.setToast(MainActivity.this, "没有最新数据");
			}
			else if (msg.what == UPDATE_OFF_LINE) {
				
				UIUtil.setToast(MainActivity.this,  UserInfo.getIntance().getUserData().getName()+"不在线");
			}
			else if (msg.what == UPDATE_OFF_CONNECT) {
				
				UIUtil.setToast(MainActivity.this, UserInfo.getIntance().getUserData().getName()+"的设备未连接");
			}
		};
	};
	
	private void setBleState(int state) {
		mBleState = state;
		UIUtil.setMessage(handler, UPDATA_BLE_STATE);
		
		voltage = GlobalStatus.getInstance().getVoltage();
		UIUtil.setMessage(handler, UPDATA_VOLTAGE);
	}
	
	private void sendIntentToGetBleState() {
		Intent intent = new Intent(BleConnectionService.ACTION_GET_BLE_STATE);
		sendBroadcast(intent);
	}

	private void updatePostureUI() {
		System.out.println("mPostureData pose"+mPostureData);
		if("skin_one".equals(app_skin.getString("skin","defaul"))){//换肤
			if (mPostureData == POSTURE_STAND) {
				pose_image.setBackgroundResource(R.drawable.stand_28_item);
			}
			else if (mPostureData == POSTURE_SIT) {
				pose_image.setBackgroundResource(R.drawable.sit_28_item);
			}
			else if (mPostureData==POSTURE_LAY) {
				pose_image.setBackgroundResource(R.drawable.main_lay_item);
			}
			else if (mPostureData == POSTURE_WALK) {
				pose_image.setBackgroundResource(R.anim.anim_mainwalk_item);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_RUN) {
				pose_image.setBackgroundResource(R.anim.anim_mainrun_item);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_TUMBLE) {
				pose_image.setBackgroundResource(R.anim.anim_main_tumble_item);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
		}else{
			if (mPostureData == POSTURE_STAND) {
				pose_image.setBackgroundResource(R.drawable.stand_28);
			}
			else if (mPostureData == POSTURE_SIT) {
				pose_image.setBackgroundResource(R.drawable.sit_28);
			}
			else if (mPostureData==POSTURE_LAY) {
				pose_image.setBackgroundResource(R.drawable.main_lay);
			}
			else if (mPostureData == POSTURE_WALK) {
				pose_image.setBackgroundResource(R.anim.anim_mainwalk);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_RUN) {
				pose_image.setBackgroundResource(R.anim.anim_mainrun);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_TUMBLE) {
				pose_image.setBackgroundResource(R.anim.anim_main_tumble);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
		}
		
	}

	private void updateTumbleUI() {
		UIUtil.setMessage(handler, UPDATE_POSTURE_TUMBLE);
	}
	/**
	 * 获取天气
	 * @author Administrator
	 *
	 */
	class KnowledgeThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			double longitude=UserInfo.getIntance().getUserData().getLongitude();
			double latitude=UserInfo.getIntance().getUserData().getLatitude();
			String weath_city=UserInfo.getIntance().getUserData().getProvince();
//			String param = "[{city:\"上海\",num:\"1\"}]";
			String param = "[{days:\""+1+"\",longitude:\""+longitude+"\",latitude:\""+latitude+"\",city:\""+weath_city+"\"}]";
			String weather_data = Client.getweatherData(param);
			System.out.println("mainactivity weather_data:" + weather_data);
			try {
				if (weather_data != null) {
					SharedPreferences.Editor editor = sp.edit();

					JSONArray jsonArray = new JSONArray(weather_data);
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					String w_date = jsonObject.getString("savedate_weather");
					String city = jsonObject.getString("city");
					String up_weather = jsonObject.getString("status1");
					String down_weather = jsonObject.getString("status2");
					String up_temp = jsonObject.getString("temperature2");
					String down_temp = jsonObject.getString("temperature1");
					String gm_s = jsonObject.getString("gm_s");
					weather_data = w_date + " " + city + " " + up_weather + "转"
							+ down_weather + " " + up_temp + "～" + down_temp
							+ "°C " + gm_s;
					editor.putString("knowledge", weather_data);
					editor.commit();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UIUtil.setMessage(handler, UPDATE_WEATHER);
		}
	}

//	private ArrayList<MessageInfo> getList() {
//		return m_arrayList;
//	}
//
//	private void clear() {
//		m_arrayList.clear();
//	}
//
//	private void AddList(MessageInfo messageInfo) {
//		m_arrayList.add(messageInfo);
//	}

	/**
	 * 获取天气信息
	 * 
	 * @return
	 */
//	private MessageInfo getWeatherInfo() {
//		MessageInfo messageInfo = new MessageInfo();
//		String w_data = sp.getString("knowledge", "");
//		messageInfo.setData(w_data);
//		messageInfo.setImage(R.drawable.weatherse);
//		return messageInfo;
//	}

	private String getStepResult() {
		System.out.println("getStepResult mRole_user:" + mRole_user);
//		if (mRole_user.equals("guardian")) {
//			step_string = tips;
//		} else {
			if (mStepCount < 3001) {
				step_string = "累计步数过少,多多动一动哦！";
			} else if (mStepCount > 3000 && mStepCount < 5001) {
				step_string = "累计步数接近目标了,继续加油！";
			} else if (mStepCount > 5000 && mStepCount < 10001) {
				step_string = "步数目标已完成，真棒！";
			} else if (mStepCount > 10000) {
				step_string = "步数目标已完成,注意休息！";
			}
//		}
		return step_string;
	}

	private String getHearWarResult() {
		if (mHteWarning == 0) {
			hearw_string = "心率异常次数较低，保持的不错！";
		} else if (mHteWarning > 0 && mHteWarning < 4) {
			hearw_string = "心率异常偏高，情绪不稳？活动量过大？";
		} else {
			hearw_string = "心率异常次数过高，需加强注意及调整！";
		}
		return hearw_string;
	}

	private String getMentalResult() {
		if (mMentalStress < 51) {
			mental_string = "心态平和，状态不错！";
		} else if (mMentalStress > 50 && mMentalStress < 81) {
			mental_string = "有点压力，慢慢调节哦！";
		} else {
			mental_string = "注意调整心态哦！";
		}
		return mental_string;
	}

	/**
	 * 获取知识信息
	 * 
	 * @return
	 */
	private String getFinalResult() {
//		MessageInfo messageInfo = new MessageInfo();
//		String final_result = "运动状况：" + getStepResult() + "心率异常："
//				+ getHearWarResult() + "精神状态：" + getMentalResult();
		String final_result;
		
		if (mRole_user.equals("guardian")) {
			final_result = tips;
		} else {
			final_result = "运动状况：" + getStepResult() + "心率异常："
					+ getHearWarResult() + "精神状态：" + getMentalResult();
		}
		
//		messageInfo.setData(final_result);
//		messageInfo.setImage(R.drawable.main_massage_80);
		return final_result;
	}

	/**
	 * 设置定时器 一秒钟发送一次数据
	 */
	private void setTimerTask() {
		System.out.println("user my role:" + mRole_user);
		if (mRole_user.equals("user") || mRole_user.equals("dualRole")) {
			timer1s = new Timer();
			timer1s.schedule(new TimerTask() {
				@Override
				public void run() {
					updateHeartRateData();
					updateHeartRateUI();
					voltage = GlobalStatus.getInstance().getVoltage();
					UIUtil.setMessage(handler, UPDATA_VOLTAGE);
				}
			}, 0, 1000 * 1);
			timer5s = new Timer();
			timer5s.schedule(new TimerTask() {
				@Override
				public void run() {
					updateTumbleWarningData();
					updateTumbleWarningUI();
				}
			}, 0, 1000 * 5);
			timer10s = new Timer();
			timer10s.schedule(new TimerTask() {
				@Override
				public void run() {
					updateHteWarningData();
					updateMentalStressData();

					updateHteWarningUI();
					updateMentalStressUI();

				}
			}, 0, 1000 * 10);
			timer20s = new Timer();
			timer20s.schedule(new TimerTask() {
				@Override
				public void run() {
//					clear();
//					AddList(getWeatherInfo());
					getFinalResult();
//					UIUtil.setMessage(handler, UPDATE_WEATHER);
					checkStartThread();
				}
			}, 0, 1000 * 60);
			timer2h = new Timer();
			timer2h.schedule(new TimerTask() {
				@Override
				public void run() {
					new KnowledgeThread().start();
				}
			}, 0, 1000 * 60 * 60 * 2);
		} else {
			timer20s = new Timer();
			timer20s.schedule(new TimerTask() {
				@Override
				public void run() {
					checkStartThread();
				}
			}, 0, 1000 * 60);
			timer2h = new Timer();
			timer2h.schedule(new TimerTask() {
				@Override
				public void run() {
					new KnowledgeThread().start();
				}
			}, 0, 1000 * 60 * 60 * 2);
			System.out.println("role:"+UserInfo.getIntance().getUserData().getFinal_name()+""+UserInfo.getIntance().getUserData().getName());
			if(!UserInfo.getIntance().getUserData().getFinal_name().equals(
			     UserInfo.getIntance().getUserData().getName())){
			timer1m = new Timer();
			timer1m.schedule(new TimerTask() {
				@Override
				public void run() {
					System.out.println("new thread updateDeviceUserData1");
//					clear();
//					AddList(getWeatherInfo());
//					AddList(getFinalResult());
//					UIUtil.setMessage(handler, UPDATE_WEATHER);
			
					new SelectUserThread().start();
				}
			}, 0, 1000 * 60);}
		}
	}

	private void cancelTimer() {
		if (UserInfo.getIntance().getUserData().getRole().equals("user")
				|| UserInfo.getIntance().getUserData().getRole()
						.equals("dualRole")) {
			timer1s.cancel();
			timer5s.cancel();
			timer10s.cancel();
			timer2h.cancel();
			timer20s.cancel();
			if(timer1m!=null){
				timer1m.cancel();
			}
		} else {
			timer2h.cancel();
			timer20s.cancel();
			if(timer1m!=null){
				timer1m.cancel();
			}
		
		}
	}

	private void updateStepCountData() {
		mStepCount = GlobalStatus.getInstance().getCurrentStep();
	}

	private void updateHeartRateData() {
		int currHte = GlobalStatus.getInstance().getHeartRate();
		if (mHteCount < 4) {
			mHeartRate = currHte;
		} else {
			int averageHte = getAverageHte();
			if ((currHte > ((int) (averageHte * 1.3f)))
					|| (currHte < ((int) (averageHte * 0.7f)))) {
				System.out.println("action MainActivity currHte[" + currHte
						+ "] average hte [" + averageHte + "]");
			} else {
				mHeartRate = currHte;
			}
		}
		mDisplayHte[mCurrHte] = currHte;
		mCurrHte++;
		if (mCurrHte >= 5)
			mCurrHte = 0;

		mHteCount++;
		if (mHteCount > 5)
			mHteCount = 5;
	}

	private void updateHteWarningData() {
//		mHteWarning = warningHteManager.getWarningCount(new Date());
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		if ((uid != null) && (uid.length() > 0)) {
			mHteWarning = warningHteManager.getWarningCount(new Date(), uid);
		} else {
			mHteWarning = 0;
		}
	}

	private void updateTumbleWarningData() {
//		mTumble = warningTumbleManager.getWarningCount(new Date());
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		if ((uid != null) && (uid.length() > 0)) {
			mTumble = warningTumbleManager.getWarningCount(new Date(), uid);
		} else {
			mTumble = 0;
		}
	}

	private void updateMentalStressData() {
		mMentalStress = GlobalStatus.getInstance().getStressLevel();
	}

	private int getAverageHte() {
		if (mHteCount == 0)
			return 0;
		int sum = 0;
		for (int i = 0; i < mHteCount; i++) {
			sum += mDisplayHte[i];
		}
		return (int) ((sum * 1.0f) / mHteCount);
	}

	private void updateHeartRateUI() {
		UIUtil.setMessage(handler, UPDATE_HEARTRATE);
	}

	private void updateStepCountUI() {
		UIUtil.setMessage(handler, UPDATE_STEP);
	}

	private void updateHteWarningUI() {
		UIUtil.setMessage(handler, UPDATE_HTE_WARNING);
	}

	private void updateMentalStressUI() {
		UIUtil.setMessage(handler, UPDATE_MENTAL_STRESS);
	}

	private void updateTumbleWarningUI() {
		UIUtil.setMessage(handler, UPDATE_TUMBLE_WARNING);
	}

	private void updatePostureAndGaitUI() {
		UIUtil.setMessage(handler, UPDATE_POSTURE_AND_GAIT);
	}

	/**
	 * 映射个人信息数据
	 */
	private void mapping() {
		my_name = UserInfo.getIntance().getUserData().getName();
		// role = UserInfo.getIntance().getUserData().getUserRole();
		String monitoringString = UserInfo.getIntance().getUserData()
				.getString_monitoring_object();
		if (monitoringString != null && !"".equals(monitoringString)) {
			try {
				JSONArray jsonArray = new JSONArray(monitoringString);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					monitoring_list.add(jsonObject.getString("name"));
					user_names.add(jsonObject.getString("userName"));
				}
			} catch (Exception e) {
				System.out.println("action MainActivity mapping[" + e.toString() + "]");
				e.printStackTrace();
			}
		} else {
			
		}
	}

	/**
	 * 根据角色设置sosOrLogo控件图片
	 */
	private void setLogo() {
		System.out.println("my main role:" + mRole_user);

		if ("skin_one".equals(app_skin.getString("skin", "defaul"))) {
			if(UserInfo.getIntance().getUserData().getRole().equals("user")){
				user_pull_im.setVisibility(View.GONE);
			}else{
				user_pull_im.setVisibility(View.VISIBLE);
				user_pull_im.setBackgroundResource(R.drawable.listdown_19_item);
			}
			if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
				sosOrLogo.setBackgroundResource(R.drawable.logo_14_item);
			} else {
				if ("guardian".equals(mRole_user)) {
					sosOrLogo.setBackgroundResource(R.drawable.logo_14_item);
				} else {
					sosOrLogo.setBackgroundResource(R.drawable.sos_click_item);
				}
			}
		} else {
			if(UserInfo.getIntance().getUserData().getRole().equals("user")){
				user_pull_im.setVisibility(View.GONE);
			}else{
				user_pull_im.setVisibility(View.VISIBLE);
				user_pull_im.setBackgroundResource(R.drawable.listdown_19);
			}
			if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
				sosOrLogo.setBackgroundResource(R.drawable.logo_14);
			} else {
				if ("guardian".equals(mRole_user)) {
					sosOrLogo.setBackgroundResource(R.drawable.logo_14);
				} else {
					sosOrLogo.setBackgroundResource(R.drawable.sos_click);
				}
			}
		}
	}

	private void clearTumbleFlag() {
		SharedPreferences.Editor editor = spTumbleFlag.edit();
		editor.putInt(TUMBLE_FLAG, TUMBLE_FLAG_OFF);
		editor.putLong(TUMBLE_TIME, 0);
		editor.commit();
	}

	private void setTumbleFlag() {
//		SharedPreferences.Editor editor = spTumbleFlag.edit();
//		editor.putInt(TUMBLE_FLAG, TUMBLE_FLAG_ON);
//		editor.commit();
		SharedPreferences.Editor editor = spTumbleFlag.edit();
		editor.putInt(TUMBLE_FLAG, TUMBLE_FLAG_ON);
		Date now = new Date();
		editor.putLong(TUMBLE_TIME, now.getTime());
		editor.commit();
	}

	private int getTumbleFlagForSP() {
//		return spTumbleFlag.getInt(TUMBLE_FLAG, TUMBLE_FLAG_OFF);
		long tumbleTime = spTumbleFlag.getLong(TUMBLE_TIME, 0);
		if (tumbleTime == 0) {
			return TUMBLE_FLAG_OFF;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Date d = calendar.getTime();
		if (tumbleTime < d.getTime()) {
			return TUMBLE_FLAG_OFF;
		}
		else { 
			return spTumbleFlag.getInt(TUMBLE_FLAG, TUMBLE_FLAG_OFF);
		}
	}

	private void updatePostureData(int posture, int gait) {
		if (getTumbleFlagForSP() == TUMBLE_FLAG_ON)
			return;

//		if (posture == 0 || gait == 0) {
//			// Unknown posture set to default POSTURE_STAND
//			mPostureData = POSTURE_STAND;
//		} else if (posture == 10) {
//			mPostureData = POSTURE_STAND;
//		} else if (posture == 11) {
//			mPostureData = POSTURE_SIT;
//		} else if (gait == 2 || gait == 3) {
//			mPostureData = POSTURE_WALK;
//		} else if (gait == 4 || gait == 5) {
//			mPostureData = POSTURE_RUN;
//		} else {
//			mPostureData = POSTURE_STAND;
//		}
		
//		if (posture == 0 || gait == 0) {
//			// Unknown posture set to default POSTURE_STAND
//			mPostureData = POSTURE_STAND;
//		} else if (posture == 13) {
//			mPostureData = POSTURE_STAND;
//		} else if (posture == 14) {
//			mPostureData = POSTURE_SIT;
//		} else {
//			mPostureData = POSTURE_STAND;
//		}
		
		if (posture == 0 || gait == 0) {
			// Unknown posture set to default POSTURE_STAND
			mPostureData = POSTURE_STAND;			
		}
		else {
			if (gait == 1) {
				if (posture == 20) {
					mPostureData = POSTURE_STAND;
				} 
				else if (posture == 21) {
					mPostureData = POSTURE_SIT;
				}
				else if (posture >= 23&&posture <=27) {
					mPostureData = POSTURE_LAY;
				}
				else {
					mPostureData = POSTURE_STAND;
				}
			}
			else if (gait == 2 || gait == 3) {
				mPostureData = POSTURE_WALK;
			}
			else if (gait == 4 || gait == 5) {
				mPostureData = POSTURE_RUN;
			}
			else {
				mPostureData = POSTURE_STAND;
			}
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter
				.addAction(DataStorageService.ACTION_UPDATE_STEP_AND_CALORIES);
		intentFilter
				.addAction(DataStorageService.ACTION_POSTURE_DATA_AVAILABLE);
		intentFilter.addAction(DataStorageService.ACTION_TUMBLE_HAPPENED);
		intentFilter.addAction(BleConnectionService.ACTION_RESPONSE_BLE_STATE);
		intentFilter.addAction(DataStorageService.ACTION_ALERT_SD_STATUS);
		return intentFilter;
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				
				setBleState(BleConnectionService.STATE_CONNECTED);
				
				mStepCount = GlobalStatus.getInstance().getLastStepCount();
				updateStepCountUI();
				
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				
				setBleState(BleConnectionService.STATE_DISCONNECTED);
				resetPosture();
				resetStepCount();
				resetMentalStress();
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (DataStorageService.ACTION_UPDATE_STEP_AND_CALORIES
					.equals(action)) {
				mStepCount = GlobalStatus.getInstance().getCurrentStep();
				mCalories = GlobalStatus.getInstance().getCalories();
//				mStepCount = intent.getIntExtra(DataStorageService.STEP_COUNT,
//						0);
//				mCalories = intent.getIntExtra(DataStorageService.CALORIES, 0);
				updateStepCountUI();
			} else if (DataStorageService.ACTION_CURRENT_STATE_CHANGE
					.equals(action)) {
			} else if (DataStorageService.ACTION_TEMP.equals(action)) {
			} else if (DataStorageService.ACTION_UPDATE_ECG_HEART_RATE
					.equals(action)) {
			} else if (BluetoothLeService.ACTION_GATT_RSSI.equals(action)) {
				
				UIUtil.setMessage(handler, UPDATA_RSSI,intent.getIntExtra("rssi", 0));
			
			} else if (DataStorageService.ACTION_POSTURE_DATA_AVAILABLE
					.equals(action)) {
				int[] postureData = intent
						.getIntArrayExtra(DataStorageService.POSTURE_DATA);
				if (postureData != null) {
					updatePostureData(postureData[0], postureData[1]);
					updatePostureAndGaitUI();
				}
			} else if (DataStorageService.ACTION_TUMBLE_HAPPENED.equals(action)) {
				mPostureData = POSTURE_TUMBLE;
//				setTumbleFlag();
				updateTumbleUI();
			}
			else if (BleConnectionService.ACTION_RESPONSE_BLE_STATE.equals(action)) {
				int bleState = intent.getIntExtra(BleConnectionService.BLE_STATE, 
												  BleConnectionService.STATE_UNKOWN
												  );
				setBleState(bleState);
			}
			else if (DataStorageService.ACTION_ALERT_SD_STATUS.equals(action)) {
				if (sdChecker.isAlertShowed() || sdCheckFirst.isAlertShowed()) {
				}
				else {
					UIUtil.setMessage(handler, ALERT_SD_STATUS);
				}
			}
		}
	};
	/**
	 * 家属查看被监护人的时候所调用的线程
	 * @author Administrator
	 *
	 */
	class SelectUserThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name = UserInfo.getIntance().getUserData().getUser_name();
			String userInfo = "[{username:\"" + user_name + "\"}]";
			String result = Client.getRealTimeByUserInfo(userInfo);
			System.out.println("getRealTimeByUserInfo result:" + result);
			if (result == null) {
				UIUtil.setMessage(handler, SEND_ALARM_WRAING_HEART);
				return;
			}
			if(result.equals("0")){
				UIUtil.setMessage(handler, UPDATE_RESULT_DATE_ZERO);
				return;
			}
			if(result.equals("-1")){
				UIUtil.setMessage(handler, UPDATE_OFF_LINE);
				return;
			}
			try {
				JSONArray jsonArray = new JSONArray(result);
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				mStepCount = StringToInt(jsonObject.getString("stepsValue"));
				mHeartRate = StringToInt(jsonObject.getString("heartValue"));
				mHteWarning = StringToInt(jsonObject.getString("heartCount"));
				mMentalStress=StringToInt(jsonObject.getString("stressValue"));
				mTumble = StringToInt(jsonObject.getString("fallCount"));
				mSleep_Quotient = jsonObject.getString("sleep");
				mPostureData=StringToInt(jsonObject.getString("pose"));
				tips = jsonObject.getString("tips");
				mStatus= jsonObject.getString("status");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(checkBleState(mStatus)==2){
//				UIUtil.setMessage(handler, UPDATE_ON_CONNECT);
			}else if(checkBleState(mStatus)==-2){
				UIUtil.setMessage(handler, UPDATE_ON_LINE_NO_DATA);
			}
			else{
				System.out.println("当前角色："+UserInfo.getIntance().getUserData().getUserRole());
				if("guardian".equals(UserInfo.getIntance().getUserData().getUserRole())){
					UIUtil.setMessage(handler, UPDATE_OFF_CONNECT);
				}
				
			}
			UIUtil.setMessage(handler, UPDATE_ROLE_DATA);
			// System.out.println("getRealTimeByUserInfo mStepCount:" +
			// mStepCount
			// + " mHeartRate:" + mHeartRate + " mHteWarning:" + mHteWarning
			// + " mTumble:" + mTumble + " mRatingbar_count:"
			// + mRatingbar_count);

		}
	}

	private int StringToInt(String daString) {
		if (daString == null || daString.equals("")) {
			return 0;
		}
		return Integer.valueOf(daString);
	}


	private void setBroadcastReceiver() {
		if (mRole_user.equals("user") || mRole_user.equals("dualRole")) {
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			System.out.println("信号6");
		}
	}

	private void unsetBroadcastReceiver() {
		if (mRole_user.equals("user") || mRole_user.equals("dualRole")) {
			unregisterReceiver(mGattUpdateReceiver);
		}
	}

//	private void resetBroadcastReceiver() {
//		unsetBroadcastReceiver();
//		setBroadcastReceiver();
//	}

	// 被监护人更新界面数据
	private void updateData() {
		updateStepCountData();
		updateHeartRateData();
		updateTumbleWarningData();
		updateHteWarningData();
		updateMentalStressData();
	}

	// 监护人角色初始化数据
	private void updateDeviceUserData() {
		System.out.println("new thread updateDeviceUserData");
		// new SelectUserThread().start();
	}

	private void invalidateUI() {
		updateStepCountUI();
		updateHeartRateUI();
		updateHteWarningUI();
		updateMentalStressUI();
		updateTumbleWarningUI();
		updatePostureAndGaitUI();
	}

	// 从监护人改回被监护人的时候才调
	private void updateRoleToUser() {
		if (mRole_user.equals("user") || mRole_user.equals("dualRole")) {
			updateData();
		}
		// else {
		// updateDeviceUserData();
		// }

		invalidateUI();
	}

	// private void onShowChanged(String role) {
	// // 如果是切换回被监护人
	// updateByRole(role);
	// resetBroadcastReceiver();
	// resetTimer();
	// }

	private void resetTimer() {
		cancelTimer();
		setTimerTask();
	}

	/**
	 * 紧急呼救
	 * @author Administrator
	 * 
	 */
	class AlarmThread extends Thread {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void run() {
			// Client.getEmergencyCall();
			Date date = new Date();
			String weath_city=UserInfo.getIntance().getUserData().getProvince();
			String update_GPS_time=UserInfo.getIntance().getUserData().getUpdate_GPS_time();
			double longitude=UserInfo.getIntance().getUserData().getLongitude();
			double latitude=UserInfo.getIntance().getUserData().getLatitude();
			String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(date);
			
			
//			String tuminfo = "[{username:\""
//					+ UserInfo.getIntance().getUserData().getMy_name()
//					+ "\",alertType:\"" + "6" + "\",alertTime:\"" + currDate
//					+ "\",value:\"" + "1" + "\",upperLimit:\"" + "" +"\",floorLimit:\"" + "" +"\",deviceNumber:\""
//					+ UserInfo.getIntance().getUserData().getDevice_number()
//					+ "\"}]";
			
			String tuminfo = "[{username:\""
					+ UserInfo.getIntance().getUserData().getMy_name()
					+ "\",alertType:\"" + "6" + "\",alertTime:\"" + currDate
					+ "\",value:\"" + "1" + "\",upperLimit:\"" + ""
					+ "\",floorLimit:\"" + "" + "\",deviceNumber:\""
					+ UserInfo.getIntance().getUserData().getDevice_number()
					+ "\",longitude:\"" + longitude + "\",latitude:\""
					+ latitude + "\",positioningTime:\"" + update_GPS_time
					+ "\",city:\"" + weath_city+ "\"}]";
			String alarm_dataString = Client.getsendAlarm(tuminfo);
//			System.out.println("紧急呼救："+alarm_dataString);
			System.out.println("action MainActivity sos alarm_dataString:" + alarm_dataString);
			if (alarm_dataString == null) {
				UIUtil.setMessage(handler, SEND_ALARM_WRAING);
				return;
			}
			UIUtil.setMessage(handler, SEND_ALARM, alarm_dataString);
		}
	}
	/**
	 * 获取睡眠指数
	 * @author Administrator
	 *
	 */
	class SleepDataThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name = UserInfo.getIntance().getUserData().getUser_name();
			String userInfo = "[{username:\"" + user_name + "\"}]";
			String sleep_data = Client.getSleepTips(userInfo);
			System.out.println("sleepactivity sleepdata:" + sleep_data);
			try {
				if (sleep_data != null) {
					System.out.println("sleepactivity sleepdata:" + sleep_data);
					JSONArray jsonArray = new JSONArray(sleep_data);
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					mSleep_Quotient = jsonObject.getString("result");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("JSONException0000000000000000000000");
			}
			System.out.println("sleepactivity sleepdata:" + mSleep_Quotient);
			UIUtil.setMessage(handler, UPDATE_SLEEP_QUALITY);
		}
	}
	/**
	 * 点赞时发送的线程
	 * @author Administrator
	 *
	 */
	class sendThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String my_mobile_phone=UserInfo.getIntance().getUserData().getMy_name();
			String user_name = UserInfo.getIntance().getUserData().getUser_name();
			String userInfo="[{username:\""+my_mobile_phone+"\",acceptObj:\""+user_name+"\",type:\""+mType+"\"}]";
			String sleep_data = Client.sendRequest(userInfo);
			if(sleep_data==null){
				if (HttpTools.isNetworkAvailable(MainActivity.this) == false) {
					UIUtil.setMessage(handler, SEND_PRISE_WRAING);
				}
				return;
			}
			
			UIUtil.setMessage(handler, SEND_PRAISE, sleep_data);
		}
	}
	/**
	 * 点赞时接受的线程
	 * @author Administrator
	 *
	 */
	class acceptThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name = UserInfo.getIntance().getUserData().getUser_name();
			String userInfo="[{userName:\""+user_name+"\"}]";
			String sleep_data = Client.getMessageCount(userInfo);
			if(sleep_data==null){
				UIUtil.setMessage(handler, ACCEPT_PRISE_WRAING);
				return;
			}
			try {
				JSONArray jsonArray=new JSONArray(sleep_data);
				JSONObject jsonObject=jsonArray.getJSONObject(0);
				mPraise_pose_count=jsonObject.getString("sport");
				mPraise_heart_count=jsonObject.getString("heartRate");
				mPraise_sleep_count=jsonObject.getString("sleep");
				mPraise_mental_count=jsonObject.getString("press");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				UIUtil.setMessage(handler, UPDATE_PRAISE_COUNT);
		}
	}

	/**
	 * 点赞时查看的线程
	 * @author Administrator
	 *
	 */
	class ExamineThread extends Thread{
		private volatile boolean state = false;
		private Date first_date = new Date();
		public void setCancel() {
			state = true;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name = UserInfo.getIntance().getUserData().getUser_name();
			String userInfo="[{userName:\""+user_name+"\",type:\""+mType+"\"}]";
			String sleep_data = Client.getNewMessage(userInfo);
			Date second_date = new Date();
			if ((second_date.getTime() - first_date.getTime()) < (1000 * 2)) {
				try {
					this.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(state){
				return;
			}
			if(sleep_data==null){
				if (HttpTools.isNetworkAvailable(MainActivity.this) == false) {
					UIUtil.setMessage(handler, EXAMINE_PRISE_WRAING);
				}else{
					UIUtil.setMessage(handler, UPDATE_NULL);
				}
				
				return;
			}
			
			try {
				sleep_List.clear();
				mental_stress_List.clear();
				heart_List.clear();
				activity_List.clear();
				JSONArray jsonArray=new JSONArray(sleep_data);
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					String send_user=jsonObject.getString("name");
					String send_time=jsonObject.getString("mCreateDate");
					String send_type=jsonObject.getString("type");
					String type_result = null;
					String final_result;
			
					if(send_type.equals("1")){
						type_result="赞了我的睡眠";
						final_result=send_user+"在"+send_time+type_result;
						sleep_List.add(final_result);
					}
					else if(send_type.equals("2")){
						
						type_result="赞了我的压力";
						final_result=send_user+"在"+send_time+type_result;
						mental_stress_List.add(final_result);
					}
					else if(send_type.equals("3")){
						
						type_result="赞了我的心率";
						final_result=send_user+"在"+send_time+type_result;
						heart_List.add(final_result);
					}
					else if(send_type.equals("4")){
						
						type_result="提醒我动一动";
						final_result=send_user+"在"+send_time+type_result;
						activity_List.add(final_result);
						
					}
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
				UIUtil.setMessage(handler, UPDATE_EXAMINE);
		}
	}
	/**
	 * 根据角色判断是否启动线程
	 */
	private void checkStartThread(){
		if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
		} else {
			new acceptThread().start();
		}
	}
	private String StringToZero(String pare){
		if(pare.equals("null")){
			return "0";
		}
		return pare;
	}
	/**
	 * 根据角色判断控件是否可点
	 */
	private void checkOnclick(){
		if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
			sleep_praise.setEnabled(false);
			heart_praise.setEnabled(false);
			activity_praise.setEnabled(false);
			mts_praise.setEnabled(false);
			sosOrLogo.setEnabled(false);
		}
	}
	
//	private WakeLock wakeLock;
//	private void acquireWakeLock(){
//		if(wakeLock==null){
//			PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
//			wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,this.getClass().getCanonicalName());
//			wakeLock.acquire();
//		}
//	}
//	private void releaseWarkLock(){
//		if(wakeLock!=null&&wakeLock.isHeld()){
//				wakeLock.release();
//				wakeLock=null;
//		}
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
			        menu.add(1,1, 1, "退出登录");  
				return true;	
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		 switch (item.getItemId()) { // 响应每个菜单项(通过菜单项的ID)  
	        case 1: // do something here  
	        	stopServices();
	        	
	        	setFinishFlag(true);
	        	
				startActivity(new Intent(MainActivity.this,
						LoginActivity.class));
				MainActivity.this.finish();		
	            break;  
	        default: // 对没有处理的事件，交给父类来处理  
	            return super.onOptionsItemSelected(item);  
	        } // 返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了  
		return true;
	}
	
	private void setShareClickable(boolean clickable) {
		share_textview.setClickable(clickable);
	}
	
	private class ScreenShotThread extends Thread {
		public void run() {
			if (mScreenBitmap == null) return;
			
			boolean success = ScreenShotUtils.savePic(mScreenBitmap, path_image);
			UIUtil.setMessage(handler, SHARE_IMAGE, success);
		}
	}
	private SaveDialog threadDialog=null;
	private void showThreadDialog(){
		threadDialog=new SaveDialog(MainActivity.this, saveCallBack);
		threadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		threadDialog.setMessage("正在加载数据，请稍后...");
		threadDialog.setIndeterminate(false);
		threadDialog.setCancelable(false);
		threadDialog.show();
	}
	private SaveCallBack saveCallBack=new SaveCallBack() {
		
		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			threadDialog.cancel();
			if (examineThread != null) {
				examineThread.setCancel();
				examineThread = null;
			}
		}
	};
	
	/**
	 * 判断被监护人的设备连接状态
	 * @param status
	 * @return
	 * 2:设备已连接
	 * -2:设备已连接无数据
	 * 0:设备未连接
	 */
	private int checkBleState(String status){
		if("2".equals(status)){
			return 2;	
		}else if("-2".equals(status)){
			return -2;
		}else{
			return 0;
		}
		
	}
	private void resetPosture() {
		mPostureData = POSTURE_STAND;
		updatePostureAndGaitUI();
	}
	
	private void resetStepCount() {
		mStepCount = 0;
		updateStepCountUI();
	}
	
	private void resetMentalStress() {
		mMentalStress = 0;
		updateMentalStressUI();
	}
	
	private void setFinishFlag(boolean finish) {
		synchronized (lockFinish) {
			mFinishFlag = finish;
		}
	}
}
