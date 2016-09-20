package com.langlang.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.langlang.activity.EcgPainterBase.ECGGLSurfaceView;
import com.langlang.ble.BleConnectionNotifiaction;
import com.langlang.data.Knowledge;
import com.langlang.data.ValueEntry;
import com.langlang.data.WarningHteInfo;
import com.langlang.dialog.ECGProgressDialog;
import com.langlang.dialog.HeartWDialog;
import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.manager.WarningHteManager;
import com.langlang.service.BleConnectionService;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
import com.langlang.utils.Filter;
import com.langlang.utils.Program;
import com.langlang.utils.SDChecker;
import com.langlang.utils.ScreenShotUtils;
import com.langlang.utils.UIUtil;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
                        
public class HeartRateActivity extends BaseActivity {

	private SharedPreferences sp;
	private SharedPreferences app_skin;
	
	private final static int UPDATE_ON_CONNECT = 112;
	private final static int UPDATE_OFF_CONNECT = 111;
	private final static int UPDATE_NOT_MESSAGE = 113;
	private final static int UPDATE_OFF_LINE = 1114;
	private final int UPDATE_ON_LINE_NO_DATA=1115;
	public static final int UPDATE_CANVAS = 1;
	public static final int UPDATE_WARNING = 2;
	public static final int SHOW_PROGRESS = 3;
	public static final int HIDE_PROGRESS = 4;
	public static final int QUIT_ACTIVITY = 5;
	
	public static final int UPDATE_HEARTRATE = 6;
	public static final int UPDATE_HTE_WARNING = 7;
	private final int KNOWLEDGE=8;
//	private final int ALARM_DEFAIL=10;
	private final int ALARM_DEFAIL_WARNING=11;
	
	private final static int ALERT_SD_STATUS = 30;
	
	private final static int UPDATA_BLE_STATE=40;
	
	private final static int SHARE_IMAGE = 50; 
	private final static int SHOW_CAPTURE_SUCCESS = 51;
	
	private final static int SHOW_IS_PAITING_STOPPED = 52;
	
	private static final String ACTION_ALARM 
					= "com.langlang.activity.HeartRateActivity.ACTION_ALARM";
	private static final int MAX_POINT = 750;
	private final int UPDATE_NETWORK_DATE=9;
	Filter filter = new Filter();
	Queue<Integer> queue = new LinkedList<Integer>();
	int pointNumber = 0;
	float[] ECGData = new float[MAX_POINT];
	ECGGLSurfaceView mSurfaceView;
	
	ECGProgressDialog progressDialog = null; 
	boolean isECGCanvasInitialized = false;
	
	private int mHeartRate = 0;	
	private int[] mDisplayHte = {0, 0, 0, 0, 0};
	private int mCurrHte = 0;
	private int mHteCount = 0;
	private Timer timer = new Timer();
	private Timer timer1m = new Timer();
	
	private Timer hreWarningTimer = new Timer();
	
	private LinearLayout warning_layout;
	private TextView useriamge_tw;
	private TextView usertext_tw;
	private RelativeLayout hte_bg_layout;
	private RelativeLayout hte_count_layout;
	private RelativeLayout noticete_layout;
	private LinearLayout bg_layout;
	private TextView namelogo_tw;
	
	private TextView knowledge_tw;
	private TextView suggest_tw;
	private TextView evaluate_tw;
	
	private TextView left_tw;
	private TextView up_tw;
	private TextView right_tw;
	private TextView down_tw;
	private TextView heart_c;
	private TextView waring_image;
	private TextView txtWarningCount;
	private TextView share_textView;
	private TextView txtHeartRate;
//	private WakeLock mWakeLock;
//	private ListView listView;
//	private HeartAdapter adapter;
	private String mStatus;
	private String result;
	private String compare;
	private String knowledge;
	private HeartWDialog praiseDialog;
	private String mNetwork_result;
	private String mNetwork_compare;
	private WarningHteManager warningHteManager = new WarningHteManager(this);
	private int mWarningHteCount = 0;
	private String path_image=Program.getSDLangLangImagePath() + "/heart_image.png";
//	private String ecg_image=Program.getSDLangLangImagePath() + "/ecg_image.png";
	
	SDChecker sdChecker = new SDChecker(this, SDChecker.SPACE_M_0);
	SDChecker sdAlert = new SDChecker(this);
	private ArrayList<ValueEntry>alarmDefail=new ArrayList<ValueEntry>();
	private int mBleState = BleConnectionService.STATE_DISCONNECTED;
	BleConnectionNotifiaction mBleConnectionNotifiaction
									= new BleConnectionNotifiaction(this);
	
	private Bitmap mScreenBmp = null;
	private Bitmap mEcgBmp = null;
	private Object lockBmps = new Object();
	int ecg_x;
	int ecg_y;
//	int ecg_wight;
//	int ecg_height;	
	private volatile boolean mCapture = false;
	private Object lockCapture = new Object();
	
	private Bitmap mLeftBitmap = null;
	private int left_x;
	private int left_y;
	private Bitmap mRightBitmap = null;
	private int right_x;
	private int right_y;
	private Bitmap mUpBitmap = null;
	private int up_x;
	private int up_y;
	private Bitmap mDownBitmap = null;
	private int down_x;
	private int down_y;
	
	private boolean stopPainting = false;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPDATE_CANVAS) {
				float[] fVal = new float[10];				
				int[] queueData = (int[]) msg.obj;
				
				if ((queueData != null) && (queueData.length >= 10)) {
					for (int i = 0; i < 10; i++) {						
						fVal[i] = ((queueData[i] - 32000) * 6) / 1000f;  //for ECG
					}
					if (pointNumber <= 740) {
						if (pointNumber == 0) {
							System.out.println("action EcgPainterActivity pointNumber is 0.");
						} else {
							for (int i = pointNumber - 1; i >= 0; i--) {
								ECGData[(i + 10)] = ECGData[i];
							}
						}
					} else {
						for (int i = pointNumber - 10 - 1; i >= 0; i--) {
							ECGData[(i + 10)] = ECGData[i];
						}
					}
					
					for (int i = 0; i < 10; i++) {
						ECGData[(9 - i)] = fVal[i];
					}
					
					pointNumber += 10;
					if (pointNumber >= 750) pointNumber = 750;
				
					mSurfaceView.drawECG(ECGData, pointNumber);
				}
			}
			if(msg.what==KNOWLEDGE){
				Knowledge knowledge = (Knowledge) msg.obj;
				
				if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
//					adapter.clear();
//					adapter.addListItem(knowledge.compare);
//					adapter.addListItem(knowledge.knowledge);
//					adapter.notifyDataSetChanged();
					evaluate_tw.setText(knowledge.compare);
//					suggest_tw.setText(knowledge.result);
					knowledge_tw.setText(knowledge.knowledge);
				}else{
//					adapter.clear();
//					adapter.addListItem(knowledge.result);
//					adapter.addListItem(knowledge.compare);
//					adapter.addListItem(knowledge.knowledge);
//					adapter.notifyDataSetChanged();
					evaluate_tw.setText(knowledge.compare);
					suggest_tw.setText(knowledge.result);
					knowledge_tw.setText(knowledge.knowledge);
				}
				
			}
			
			else if (msg.what == UPDATE_WARNING) {
			}
			
			else if (msg.what == SHOW_PROGRESS) {
				if (progressDialog == null) {
					progressDialog = new ECGProgressDialog(HeartRateActivity.this, onCancelCallback);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
					progressDialog.setMessage("正在初始化,请稍候...");  
					progressDialog.setIndeterminate(false);
					progressDialog.setCancelable(false);
				
					progressDialog.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							if (!isECGCanvasInitialized) {
							}
						}
					});
					
					progressDialog.show();
				} else {
					progressDialog.show();
				}
			}
			
			else if (msg.what == HIDE_PROGRESS) {
//				UIUtil.setToast(HeartRateActivity.this, "初始化成功");
				
				if (progressDialog != null) {
					progressDialog.cancel(); 
				}
			}
			
			else if (msg.what == QUIT_ACTIVITY) {
				quitActivity();
			}
			else if (msg.what == UPDATE_NOT_MESSAGE) {
				init_Dialog_cancel();
				UIUtil.setToast(HeartRateActivity.this, "服务器访问失败");
			}
			else if (msg.what == UPDATE_HEARTRATE) {
				txtHeartRate.setText(Integer.toString(mHeartRate));
			}
			
			else if (msg.what == UPDATE_HTE_WARNING) {
				txtWarningCount.setText(Integer.toString(mWarningHteCount));
			}
			
			else if(msg.what==UPDATE_NETWORK_DATE){
				init_Dialog_cancel();
				txtHeartRate.setText(Integer.toString(mHeartRate));
				txtWarningCount.setText(Integer.toString(mWarningHteCount));
			}
			
			else if (msg.what == ALERT_SD_STATUS) {
				sdAlert.checkAndAlert();
			}

//			else if(msg.what==ALARM_DEFAIL){
//				if(msg.obj!=null&&!msg.obj.equals("")){
////					UIUtil.setToast(HeartRateActivity.this,"数据");
//				}
//			}
			
			else if(msg.what==ALARM_DEFAIL_WARNING){
				init_Dialog_cancel();
				UIUtil.setToast(HeartRateActivity.this,"网络异常");
			}

			else if (msg.what == UPDATA_BLE_STATE) {
				mBleConnectionNotifiaction.show(mBleState);
			}
			
			else if (msg.what == SHOW_CAPTURE_SUCCESS) {
				UIUtil.setLongToast(HeartRateActivity.this, "截图成功");
			}
			
			else if (msg.what == SHARE_IMAGE) {
				boolean success = (Boolean) msg.obj;
				if (success) {
					shareImage(path_image);
				}
				else {
					UIUtil.setToast(HeartRateActivity.this, "无法截图");
				}
				
				share_textView.setClickable(true);
			}
			else if (msg.what == UPDATE_ON_CONNECT) {
				
//				UIUtil.setToast(HeartRateActivity.this, "设备已连接");
			}
			else if (msg.what == UPDATE_OFF_LINE) {
				init_Dialog_cancel();
				UIUtil.setToast(HeartRateActivity.this,  UserInfo.getIntance().getUserData().getName()+"不在线");
			}
			else if (msg.what == UPDATE_OFF_CONNECT) {
				init_Dialog_cancel();
				UIUtil.setToast(HeartRateActivity.this,UserInfo.getIntance().getUserData().getName()+"的设备未连接");
			}
			else if (msg.what == UPDATE_ON_LINE_NO_DATA) {
				init_Dialog_cancel();
				UIUtil.setToast(HeartRateActivity.this, "没有最新数据");
			}
			else if (msg.what == SHOW_IS_PAITING_STOPPED) {
				boolean paitingStopped = (Boolean) msg.obj;
				if (paitingStopped) {
					UIUtil.setToast(HeartRateActivity.this, "画图已停止");
				}
				else {
					UIUtil.setToast(HeartRateActivity.this, "画图已开始");
				}
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_heart_rate);		
		showpreDialog();
		app_skin=getSharedPreferences("app_skin",MODE_PRIVATE);
		sp=HeartRateActivity.this.getSharedPreferences("heatknowledge", MODE_PRIVATE);
		getViewId();
		 changeSkin();
		getOnclick();
		 mapping();
		 checkWidgetShow();
		new knowledgeThread().start(); 
		
		stopPainting = false;
		
//		sendSetEcgPolicyIntent(BleConnectionService.POLICY_ECG_ONLY,
//				   BleConnectionService.POLICY_SUB_UNKNOWN);
		
		mSurfaceView.setCallback(new ECGGLSurfaceView.CanvasReadyCallback() {
			@Override
			public void notifyCanvasReady() {
				isECGCanvasInitialized = true;
				hideProgressBar();
			}
			@Override
			public boolean getCapture() {
				synchronized(lockCapture) {
					return mCapture;
				}
			}
			@Override
			public void onCaptured(Bitmap bitmap) {
				setCapture(false);
				onECGCaptured(bitmap);
			}
			@Override
			public boolean stopPainting() {
				return stopPainting;
			}
		});
		
		mSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (stopPainting) {
					stopPainting = false;
					UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED, false);
				} else {
					stopPainting = true;
					UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED, true);
				}
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		filter.reset();
		pointNumber = 0;
		queue.clear();
		mSurfaceView.reset();
		isECGCanvasInitialized = false;
		
		if(UserInfo.getIntance().getUserData().getUserRole().equals("guardian")){
//			new AlarmDetailThread().start();
			setNetWorkTimerTask();
		}else{
		setTimerTask();
		initUI();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		
		sdChecker.checkAndAlert();
		}
		
//		showProgressBar();
		registerReceiver(mGattUpdateReceiver, makeAlarmIntentFilter());

		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();
//		am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime + 200, 40, pi);
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, 40, pi);
		
        PowerManager powerManager = ((PowerManager) getSystemService(POWER_SERVICE));
//        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK  
//                | PowerManager.ON_AFTER_RELEASE, TAG); 
//        mWakeLock.acquire();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (UserInfo.getIntance().getUserData().getUserRole().equals("guardian")) {
		
			cancelNetWorkTimer();
		
			unregisterReceiver(mGattUpdateReceiver);
		}
		else {
//			if (null != mWakeLock) {
//				mWakeLock.release();
//				mWakeLock = null;
//			}

			cancelTimer();

			unregisterReceiver(mGattUpdateReceiver);
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
//		sendSetEcgPolicyIntent(BleConnectionService.POLICY_MIXED,
//				   BleConnectionService.POLICY_SUB_POLICY_MIXED_0);	
	}

	/**
	 * 获取控件ID
	 */
	private void getViewId(){
		warning_layout=(LinearLayout)this.findViewById(R.id.heart_warnin_layout);
		heart_c=(TextView)this.findViewById(R.id.heart_waringcoutn_tw);
		waring_image=(TextView)this.findViewById(R.id.heart_waring_image);
		share_textView=(TextView)this.findViewById(R.id.heart_share_textview);
		txtHeartRate=(TextView) this.findViewById(R.id.heart_hcount_tw);
		txtWarningCount=(TextView)this.findViewById(R.id.heart_whearcount_tw);
		mSurfaceView =(ECGGLSurfaceView)this.findViewById(R.id.hte_GLSurfaceView_ecg);
//		listView=(ListView)this.findViewById(R.id.heart_listview);
//		adapter=new HeartAdapter(HeartRateActivity.this);
//		listView.setAdapter(adapter);
		hte_count_layout=(RelativeLayout)this.findViewById(R.id.heart_hcount_rl);
		hte_bg_layout=(RelativeLayout)this.findViewById(R.id.hte_bg_layout);
		bg_layout=(LinearLayout)this.findViewById(R.id.heart_layout);
		noticete_layout=(RelativeLayout)this.findViewById(R.id.heart_notiva_layout);
		namelogo_tw=(TextView)this.findViewById(R.id.heart_set_textview);
		useriamge_tw=(TextView) this.findViewById(R.id.heart_userimage_tw);
		usertext_tw=(TextView) this.findViewById(R.id.heart_userimage_text);
		
		
		
		left_tw=(TextView) this.findViewById(R.id.hte_left_tw);
		up_tw=(TextView) this.findViewById(R.id.hte_up_tw);
		right_tw=(TextView) this.findViewById(R.id.hte_right_tw);
		down_tw=(TextView) this.findViewById(R.id.hte_down_tw);
		
		suggest_tw=(TextView)this.findViewById(R.id.heart_suggest_textview);
		evaluate_tw=(TextView)this.findViewById(R.id.heart_evaluate_textview);
		knowledge_tw=(TextView)this.findViewById(R.id.heart_knowledge_textview);
		
	}
	
	
	
	/**
	 * 换肤
	 */
	private void changeSkin(){
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			bg_layout.setBackgroundResource(R.drawable.bg_item);
			namelogo_tw.setTextColor(getResources().getColor(R.color.white));
			share_textView.setBackgroundResource(R.drawable.share_click_item);
			hte_count_layout.setBackgroundResource(R.drawable.xiabian_03_item);
			txtHeartRate.setTextColor(getResources().getColor(R.color.blue_text));
			noticete_layout.setBackgroundResource(R.drawable.item_notice_item);
			left_tw.setVisibility(View.GONE);
			up_tw.setVisibility(View.GONE);
			right_tw.setVisibility(View.GONE);
			down_tw.setVisibility(View.GONE);
			useriamge_tw.setBackgroundResource(R.drawable.user_jin_03_item);
			usertext_tw.setTextColor(getResources().getColor(R.color.white));
			
			hte_bg_layout.setBackgroundResource(R.drawable.xindian_02_item);
		}
	}
	
	
	
	/**
	 * 获取控件点击事件
	 */
	private void getOnclick(){
		share_textView.setOnClickListener(listener);
		warning_layout.setOnClickListener(listener);
	}
	/**
	 * 设置控件点击事件
	 */
	private OnClickListener listener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.heart_share_textview:
//				boolean result = ScreenShotUtils.shotBitmap(HeartRateActivity.this,path_image);
//				if(result)
//				{
//					UIUtil.setToast(HeartRateActivity.this, "截图成功.");
//					shareImage(path_image);
//				}else {
//					UIUtil.setToast(HeartRateActivity.this, "截图失败.");
//				}
				share_textView.setClickable(false);
				
				mScreenBmp = ScreenShotUtils.takeScreenShot(HeartRateActivity.this);
				setCapture(true);				
				
				break;
			case R.id.heart_warnin_layout:
				
				if(UserInfo.getIntance().getUserData().getUserRole().equals("guardian")){
					if(mWarningHteCount==0||alarmDefail.size()==0){
						UIUtil.setToast(HeartRateActivity.this,"报警消息为空");
					}else{
						showInteract(alarmDefail);
					}
				}else{
					if(mWarningHteCount==0){
						UIUtil.setToast(HeartRateActivity.this,"报警消息为空");
					}else{
						showInteract(getArraylist());
					}
					
				}
				
				
				break;

			default:
				break;
			}
			
		}
	};
	/**
	 * 获取心率异常信息
	 * @return
	 */
	private ArrayList<ValueEntry> getArraylist(){
		ArrayList<ValueEntry>arrayList=new ArrayList<ValueEntry>();
		if(UserInfo.getIntance().getUserData().getUserRole().equals("guardian")){
			// from Network
		}else{
			String uid = UserInfo.getIntance().getUserData().getMy_name();
			if ((uid != null) && (uid.length() > 0)) {
				List<WarningHteInfo> list = warningHteManager.getWarnings(new Date(), uid);
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						WarningHteInfo info = list.get(i);
						SimpleDateFormat sdf = new SimpleDateFormat(Program.SECOND_FORMAT);
						try {
							Date date = sdf.parse(info.minuteData);
							ValueEntry entry = new ValueEntry(date.getTime(), info.heartRate);
							arrayList.add(entry);
						} catch (Exception e) {
							System.out.println("action HeartRateActivity getArraylist e[" + e.toString() + "]");
						}						
					}
				}
			}
		}
		return arrayList;
	}
	private void showInteract(ArrayList<ValueEntry> arrayList) {
		praiseDialog = new HeartWDialog(HeartRateActivity.this,arrayList);
		praiseDialog.show();
		praiseDialog.setCancelable(false);
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
			File file = new File(path);
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(intent, getTitle()));
		}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent =new Intent();  
			intent.setAction(ACTION_ALARM);  
			PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
			AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
			alarm.cancel(sender);
			
			startActivity(new Intent(HeartRateActivity.this, MainActivity.class));
			HeartRateActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	ECGProgressDialog.OnCancelCallback onCancelCallback = new ECGProgressDialog.OnCancelCallback() {
		@Override
		public void onCancel() {
			UIUtil.setMessage(handler, QUIT_ACTIVITY);
		}
	};
	
	private void hideProgressBar() {
		UIUtil.setMessage(handler, HIDE_PROGRESS);
	}
	
//	private void showProgressBar() {
//		UIUtil.setMessage(handler, SHOW_PROGRESS);
//	}
	
	public void quitActivity() {
		startActivity(new Intent(HeartRateActivity.this, MainActivity.class));
		HeartRateActivity.this.finish();
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(DataStorageService.ACTION_ECG_DATA_AVAILABLE);
		intentFilter.addAction(DataStorageService.ACTION_ALERT_SD_STATUS);
		intentFilter.addAction(BleConnectionService.ACTION_RESPONSE_BLE_STATE);
		return intentFilter;
	}
	
	private static IntentFilter makeAlarmIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);		
		intentFilter.addAction(ACTION_ALARM);
		return intentFilter;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
//			System.out.println("action HeartRateActivity = " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				
				setBleState(BleConnectionService.STATE_CONNECTED);
				
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				
				setBleState(BleConnectionService.STATE_DISCONNECTED);
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (ACTION_ALARM.equals(action)) {
//					System.out.println("action HeartRateActivity ACTION_ALARM queu_size[" + queue.size() + "]");
					int[] queueData = new int[10];
					boolean hasData = false;
					
					synchronized (queue) {
						if (queue.size() >= 10) {
							for (int i = 0; i < 10; i++) {
								queueData[i] = queue.poll();
							}
							
							hasData = true;
						}
					}
					
					if (hasData) {
						UIUtil.setMessage(handler, UPDATE_CANVAS, queueData);
					}
			} else if (DataStorageService.ACTION_ECG_DATA_AVAILABLE.equals(action)) {
					synchronized (queue) {				
						int[] ecg = intent.getIntArrayExtra("ECGData");
						
						if (ecg != null) {
							for (int i = 0; i < ecg.length; i++) {
								try {
										filter.addData(((ecg[i] - 28000)));
								} catch (Exception e) {
										e.printStackTrace();
								}							
							}
							for (int i = 0; i < ecg.length; i++) {
								if (filter.canGetOne()) {
									try {
										queue.offer(filter.getOne());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									break;
								}
							}
						}
					}
			}
			else if (DataStorageService.ACTION_ALERT_SD_STATUS.equals(action)) {
				if (sdChecker.isAlertShowed()) {
				}
				else {
					UIUtil.setMessage(handler, ALERT_SD_STATUS);
				}
			}			
			else if (BleConnectionService.ACTION_RESPONSE_BLE_STATE.equals(action)) {
				int bleState = intent.getIntExtra(BleConnectionService.BLE_STATE, 
												  BleConnectionService.STATE_UNKOWN
												  );
				setBleState(bleState);
			}
		}
	};
	
	private int getAverageHte() {
		if (mHteCount == 0)
			return 0;
		int sum = 0;
		for (int i = 0; i < mHteCount; i++) {
			sum += mDisplayHte[i];
		}
		return (int) ((sum * 1.0f) / mHteCount);
	}	
	
	private void setTimerTask() {
		timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateHeartRateData();
				updateHeartRateUI();
			}
		}, 500, 1000 * 1);
		
		hreWarningTimer=new Timer();
		hreWarningTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateHeartRateWarningData();
				updateHteWarningUI();
				getResult();
				getcompare();
				getKnowledge();
				updateKnowledge(result,compare,knowledge);
			}
		}, 500, 1000 * 5);
	}
	private void setNetWorkTimerTask() {
		timer1m=new Timer();
		timer1m.schedule(new TimerTask() {
			@Override
			public void run() {
				updataDataThread=new UpdataDataThread();
			updataDataThread.start();
			}
		}, 500, 1000 * 60);
	}
	private void cancelNetWorkTimer() {
		timer1m.cancel();
	}
	
	private void cancelTimer() {
		timer.cancel();
		hreWarningTimer.cancel();
	}
	
	private void initUI() {
		updateHeartRateData();		
		updateHeartRateWarningData();
		
		updateHeartRateUI();
		updateHteWarningUI();
		
		if (GlobalStatus.getInstance().getBleState() == BleConnectionService.STATE_CONNECTED) {
			// We don't need to show the ble status if it has been connected.
		}
		else {
			setBleState(GlobalStatus.getInstance().getBleState());
		}
	}	

	private void updateHeartRateData() {
		int currHte = GlobalStatus.getInstance().getHeartRate();
//		if (mHteCount < 4) {
//			mHeartRate = currHte;
//		} else {
//			int averageHte = getAverageHte();
//			if ((currHte > ((int) (averageHte * 1.3f)))
//					|| (currHte < ((int) (averageHte * 0.7f)))) {
//				System.out.println("action HeartRateActivity currHte[" + currHte + "] average hte [" + averageHte + "]");
//			} else {
//				mHeartRate = currHte;
//			}
//		}
//		mDisplayHte[mCurrHte] = currHte;
//		mCurrHte++;
//		if (mCurrHte >= 5)
//			mCurrHte = 0;
//
//		mHteCount++;
//		if (mHteCount > 5)
//			mHteCount = 5;
		
		mHeartRate = GlobalStatus.getInstance().getHeartRate();
	}

	private void updateHeartRateUI() {
		UIUtil.setMessage(handler, UPDATE_HEARTRATE);
	}
	
	private void updateHteWarningUI() {
		UIUtil.setMessage(handler, UPDATE_HTE_WARNING);
	}
	
	private void updateHeartRateWarningData() {
//		mWarningHteCount = warningHteManager.getWarningCount(new Date());
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		if ((uid != null) && (uid.length() > 0)) {
			mWarningHteCount = warningHteManager.getWarningCount(new Date(), uid);
		}
		else {
			mWarningHteCount = 0;
		}
	}
	/**
	 * 映射数据
	 */
	private void mapping(){
		usertext_tw.setText(UserInfo.getIntance().getUserData().getName());
	}
	class knowledgeThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			SharedPreferences.Editor editor=sp.edit();
			
			String param = "[{type:\"1\"}]";
			StringBuffer sb=new StringBuffer();
			try {
				String knowladgeResult=Client.getTips(param);
				System.out.println("mentalstressactivity knowladgeResult:"+knowladgeResult);
				if(knowladgeResult!=null){
				JSONArray jsonArray=new JSONArray(knowladgeResult);
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					sb.append(jsonObject.getString("content")+"/");
				}
				if (sb.length() > 0 || !sb.toString().equals("")) {
					editor.putString("kndata", sb.toString());
					editor.commit();
				}}
				System.out.println("my sb:"+sb.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void updateKnowledge(String result, String compare, String knowledge) {
		UIUtil.setMessage(handler, KNOWLEDGE, new Knowledge(result, compare, knowledge));
	}
	/**
	 * 当天结果
	 * @return
	 */
	private String getResult(){
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
			result = mNetwork_result;
		}
		if(mWarningHteCount==0){
			result="心率异常次数为 0 次，保持的不错！";
		}else if(mWarningHteCount>0&&mWarningHteCount<4){
			result="心率异常次数为1-3次，情绪不稳？活动量过大？";
		}else{
			result="心率异常次数为＞3次，需加强注意及调整！";
		}
		return result;
	}
	/**
	 * 历史比较
	 * @return
	 */
	private String getcompare(){
		
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
			compare = mNetwork_compare;
		}else{
		if(mWarningHteCount==0){
//			compare="注意保持平稳的心态，避免波动哦！";
			compare="心脏健康指数优化100%。真棒！";
		}else if(mWarningHteCount>0&&mWarningHteCount<4){
//			compare="压力的调整得到了有效改善，真棒！";
			compare="心脏健康指数无优化，请继续改善！";
		}else{
//			compare="慢慢来，循序渐进调节心态！";
			compare="心脏健康指数呈下降趋势，请注意！";
		}}
		return compare;
	}
	/**
	 * 小知识
	 * @return
	 */
	private String getKnowledge(){
		String knowledge_data=sp.getString("kndata", "");
		String [] kle=knowledge_data.split("/");
		int index=new Random().nextInt(kle.length);
		knowledge=kle[index];
		return knowledge;
	}
	class UpdataDataThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			//报警明细
			String userInfo_detail="[{username:\""+UserInfo.getIntance().getUserData().getUser_name()+ "\",alertType:\"" + 1 +"\"}]";
			String detailString=Client.getAlarmDetail(userInfo_detail);
			System.out.println("detailString:"+detailString);
			
		
			//----------------------------------------------------------------------------	
			String user_name = UserInfo.getIntance().getUserData()
					.getUser_name();
			System.out.println("actact:" + user_name);
			String userInfo = "[{username:\"" + user_name + "\"}]";
			String user_data = Client.getHeartRateTips(userInfo);
			System.out.println("user_data:"+user_data);
			
			if (user_data == null) {
				UIUtil.setMessage(handler, ALARM_DEFAIL_WARNING);
			} else {
				if ("0".equals(user_data)&&"0".equals(detailString)) {
					UIUtil.setMessage(handler, UPDATE_NOT_MESSAGE);
					return;
				}
				if("-1".equals(user_data)){
					UIUtil.setMessage(handler, UPDATE_OFF_LINE);
					return;
				}
				if(detailString != null){
					try {
						JSONArray jsonArray_detail = new JSONArray(detailString);
						alarmDefail.clear();
						for (int i = 0; i < jsonArray_detail.length(); i++) {
							JSONObject jsonObject_detail = jsonArray_detail
									.getJSONObject(i);
							String reString = jsonObject_detail
									.getString("alertDate");
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date dt = simpleDateFormat.parse(reString);
							ValueEntry entry = new ValueEntry(dt.getTime(),
									StringToInt(jsonObject_detail
											.getString("value")));
							alarmDefail.add(entry);
							System.out.println("detailString sdfsdf:"
									+ entry.timestamp);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// UIUtil.setMessage(handler, ALARM_DEFAIL, detailString);

				}
			
				try {
					JSONArray jsonArray = new JSONArray(user_data);
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					mHeartRate = StringToInt(jsonObject.getString("heartRate"));
				
					mNetwork_compare=jsonObject.getString("compareTips");
					mNetwork_result=jsonObject.getString("resultTips");
					System.out.println("user_data--"+mNetwork_compare);
					mWarningHteCount = StringToInt(jsonObject
							.getString("alterCount"));
					mStatus = jsonObject.getString("status");
					String dataList = jsonObject.getString("dataList");
					JSONArray ecgArray = new JSONArray(dataList);
					System.out.println("DDD_Lengh:" + ecgArray.length());
					if (ecgArray.length() > 0) {

						for (int i = 0; i < ecgArray.length(); i++) {
							String ecgVal = ecgArray.get(i).toString();
							System.out.println("DDD:" + ecgVal);
							int ecg = Integer.parseInt(ecgVal);
							synchronized (queue) {
								try {
									filter.addData(ecg - 28000);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (filter.canGetOne()) {
									try {
										queue.offer(filter.getOne());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					System.out
							.println("action HeartRateActivity json Exception.");
					e.printStackTrace();
				}

				if(checkBleState(mStatus)==2){
//					UIUtil.setMessage(handler, UPDATE_ON_CONNECT);
				}else if(checkBleState(mStatus)==-2){
					UIUtil.setMessage(handler, UPDATE_ON_LINE_NO_DATA);
				}
				else{
					UIUtil.setMessage(handler, UPDATE_OFF_CONNECT);
				}
				getResult();
				getcompare();
				getKnowledge();
			
				updateKnowledge(result,compare,knowledge);
				UIUtil.setMessage(handler, UPDATE_NETWORK_DATE);
			}
			
			System.out.println("HeartRateActicity data :" + mHeartRate+":"+mWarningHteCount);
			
		}
	}
//	class AlarmDetailThread extends Thread{
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			String userInfo_detail="[{username:\""+UserInfo.getIntance().getUserData().getUser_name()+ "\",alertType:\"" + 1 +"\"}]";
//			String detailString=Client.getAlarmDetail(userInfo_detail);
//			System.out.println("detailString:"+detailString);
//			
//			if(detailString==null){
////				UIUtil.setMessage(handler, ALARM_DEFAIL_WARNING);
//			}else{
//				try {
//					JSONArray jsonArray_detail=new JSONArray(detailString);
//					for (int i = 0; i < jsonArray_detail.length(); i++) {
//						JSONObject jsonObject_detail=jsonArray_detail.getJSONObject(i);
//						String reString=jsonObject_detail.getString("alertDate");
//						SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						Date dt=simpleDateFormat.parse(reString);
//						ValueEntry entry = new ValueEntry(dt.getTime(), StringToInt(jsonObject_detail.getString("value")));
//						alarmDefail.add(entry);
//						System.out.println("detailString sdfsdf:"+entry.timestamp);
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				UIUtil.setMessage(handler, ALARM_DEFAIL, detailString);
//			}
//		
//		}
//	}
	private int StringToInt(String daString) {
		if (daString == null || daString.equals("")) {
			return 0;
		}
		return Integer.valueOf(daString);
	}
	/**
	 * 根据登录模式判断部分控件是否显示
	 */
	private void checkWidgetShow(){
		if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
			heart_c.setVisibility(View.GONE);
			waring_image.setVisibility(View.GONE);
			txtWarningCount.setVisibility(View.GONE);
		}
	}
	
//	private void sendIntentToGetBleState() {
//		Intent intent = new Intent(BleConnectionService.ACTION_GET_BLE_STATE);
//		sendBroadcast(intent);
//	}
	
	private void setBleState(int state) {
		mBleState = state;
		UIUtil.setMessage(handler, UPDATA_BLE_STATE);
	}
	
	class MergeBitmapThread extends Thread {	
		public void run() {
			boolean success = mergeBitmap();
			UIUtil.setMessage(handler, SHARE_IMAGE, success);
		}
	}

	public synchronized boolean mergeBitmap() {
		synchronized (lockBmps) {
		    if (mScreenBmp == null || mEcgBmp == null) {  
		        return false;  
		    }
		    
		    if (isSkinOne()) {
		    } else {
			    if (mLeftBitmap == null || mRightBitmap == null
			    		|| mUpBitmap == null || mDownBitmap == null) {
			    	return false;
			    }
		    }
			
		    int bgWidth = mScreenBmp.getWidth();  
		    int bgHeight = mScreenBmp.getHeight(); 
			
//		    int fgWidth = mEcgBmp.getWidth();  
//		    int fgHeight = mEcgBmp.getHeight();
			
		    Bitmap newBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);  
			
		    Canvas canvas = new Canvas(newBitmap);  
		    
		    canvas.drawBitmap(mScreenBmp, 0, 0, null);
		    mScreenBmp.recycle();
			mScreenBmp = null;

//			canvas.drawBitmap(mEcgBmp, ecg_x + 100 + 2 + 1 + 1 + 1, ecg_y - 50  + 0 + 10 + 5, null);
//			canvas.drawBitmap(mEcgBmp, ecg_x + 5, ecg_y + 10, null);
			canvas.drawBitmap(mEcgBmp, ecg_x, ecg_y, null);
			mEcgBmp.recycle();
			mEcgBmp = null;
	//	    canvas.drawBitmap(mEcgBmp, (bgWidth - fgWidth) / 2,  
	//	            (bgHeight - fgHeight) / 2, null); 
			
			if (isSkinOne()) {
			} else {			
				canvas.drawBitmap(mLeftBitmap, left_x, left_y, null);
				mLeftBitmap.recycle();
				mLeftBitmap = null;
				
				canvas.drawBitmap(mRightBitmap, right_x, right_y, null);
				mRightBitmap.recycle();
				mRightBitmap = null;
				
				canvas.drawBitmap(mUpBitmap, up_x, up_y, null);
				mUpBitmap.recycle();
				mUpBitmap = null;
				
				canvas.drawBitmap(mDownBitmap, down_x, down_y, null);
				mDownBitmap.recycle();
				mDownBitmap = null;
			}
			
		    canvas.save(Canvas.ALL_SAVE_FLAG);  
		    canvas.restore(); 
			
			boolean success = ScreenShotUtils.savePic(newBitmap, path_image);
			
			if (success) {
//				UIUtil.setMessage(handler, SHOW_CAPTURE_SUCCESS);
			}
			
			if (newBitmap != null) {
				newBitmap.recycle();
				newBitmap = null;
			}
			
		    return success;
		}
	}
	
	private void onECGCaptured(Bitmap bitmap) {
//		ecg_x = mSurfaceView.getLeft();
//		ecg_y = mSurfaceView.getTop();
//		ecg_wight = mSurfaceView.getRight();
//		ecg_height = mSurfaceView.getBottom() - mSurfaceView.getTop();

		synchronized (lockBmps) {
//			mScreenBmp = ScreenShotUtils.takeScreenShot(HeartRateActivity.this);
			mEcgBmp = bitmap;
//			ScreenShotUtils.savePic(mEcgBmp, ecg_image);
			
			if (isSkinOne()) {
			} else {
				mLeftBitmap = ScreenShotUtils.saveViewToBitmap(left_tw);
				mRightBitmap = ScreenShotUtils.saveViewToBitmap(right_tw);
				mUpBitmap = ScreenShotUtils.saveViewToBitmap(up_tw);
				mDownBitmap = ScreenShotUtils.saveViewToBitmap(down_tw);
			}
		}
		UIUtil.setMessage(handler, SHOW_CAPTURE_SUCCESS);
		new MergeBitmapThread().start();		
	}
	
	private void setCapture(boolean capture) {
		synchronized(lockCapture) {
			mCapture = capture;
			
			if (capture) {
				int[] location = new int[2];
				
				mSurfaceView.getLocationOnScreen(location);
				ecg_x = location[0];
				ecg_y = location[1];
				
//				ecg_x = mSurfaceView.getLeft();
//				ecg_y = mSurfaceView.getTop();
//				ecg_wight = mSurfaceView.getRight();
//				ecg_height = mSurfaceView.getBottom() - mSurfaceView.getTop();
				if (isSkinOne()) {
				} else {
					left_tw.getLocationOnScreen(location);
					left_x = location[0];
					left_y = location[1];
					
					right_tw.getLocationOnScreen(location);
					right_x = location[0];
					right_y = location[1];
					
					up_tw.getLocationOnScreen(location);
					up_x = location[0];
					up_y = location[1];
					
					down_tw.getLocationOnScreen(location);
					down_x = location[0];
					down_y = location[1];
				}
				
				System.out.println("action HeartRateActivity setCapture22 " + ecg_x + "," + ecg_y);
				
//				mSurfaceView.drawECG(ECGData, pointNumber);
				mSurfaceView.redrawForCapture();
			}
		}
	}
	
	private boolean isSkinOne() {	
		if (app_skin == null) return false;
		return "skin_one".equals(app_skin.getString("skin","defaul"));
	}
	private SaveDialog init_Dialog=null;
	private UpdataDataThread updataDataThread=null;

	private void showpreDialog() {
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {

			init_Dialog = new SaveDialog(HeartRateActivity.this, saveCallBack);
			init_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			init_Dialog.setMessage("正在获取数据,请稍候...");
			init_Dialog.setIndeterminate(false);
			init_Dialog.setCancelable(false);
			init_Dialog.show();
		}

	}
	private void init_Dialog_cancel(){
		if(init_Dialog!=null){
			init_Dialog.cancel();
		}
	}
	private SaveCallBack saveCallBack=new SaveCallBack() {
		
		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			startActivity(new Intent(HeartRateActivity.this,MainActivity.class));
			init_Dialog_cancel();
			HeartRateActivity.this.finish();
		}
	};
	/**
	 * 判断被监护人的设备连接状态
	 * @param status
	 * @return
	 * 2:设备已连接
	 * -2：设备已连接但无数据
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
	
	private void sendSetEcgPolicyIntent(int policy, int subPolicy) {
		final Intent intent = new Intent(BleConnectionService.ACTION_SET_ECG_MODE);
		intent.putExtra("POLICY", policy);
		intent.putExtra("SUB_POLICY", subPolicy);
		intent.putExtra("FROM", BleConnectionService.POLICY_CTRL_FROM_CLIENT);
		sendBroadcast(intent);
	}
}
