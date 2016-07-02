package com.langlang.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.langlang.adapter.SleepAdapter;
import com.langlang.ble.BleConnectionNotifiaction;
import com.langlang.data.ECGResult;
import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.manager.MinuteECGResultManager;
import com.langlang.service.BleConnectionService;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
import com.langlang.utils.MiscUtils;
import com.langlang.utils.Program;
import com.langlang.utils.SDChecker;
import com.langlang.utils.ScreenShotUtils;
import com.langlang.utils.UIUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SleepActivity extends BaseActivity {
	public static final int UPDATE_DATA_GRAPH = 1;
	private final int UPDATE_DATA_SLEEP_HISTORY=2;
	private final int UPDATE_DATA_KNOWLEDGE=3;
	private final int UPDATE_NOTHING=4;
	private final int UPDATE_DATA_WRANNING=5;
	private final static int UPDATE_ON_CONNECT = 112;
	private final static int UPDATE_OFF_CONNECT = 111;
	private final static int UPDATE_OFF_LINE = 1113;
	private final static int ALERT_SD_STATUS = 30;
	
	private final static int UPDATA_BLE_STATE=40;
	
	private final int SHARE_IMAGE = 50; 
	private final int SHOW_CAPTURE_SUCCESS = 51;
	
	private LinearLayout bg_layout;
	private RelativeLayout noticete_layout;
	private TextView up_tw;
	private TextView left_tw;
	private TextView right_tw;
	private TextView down_tw;
	private TextView namelogo_tw;
//	private ListView listView;
	private TextView useriamge_tw;
	private TextView usertext_tw;
//	private SleepAdapter adapter;
	private TextView gotosleep_tw;//入睡
	private TextView wake_up;//睡醒
	private TextView valid_sleep;//有效睡眠
	private TextView respiratory_arrest_count;//呼吸停止次数
	private TextView Tachypnoea_count;//呼吸过快次数
	private TextView toc_tw;//翻身次数
	private TextView result_tw;//睡眠评价
	private TextView share_tw;
	private TextView knowledge_tw;
	private TextView suggest_tw;
	private TextView evaluates_tw;
	
	private static final int COUNT_DATE = 6 + 1;
	private static final int COUNT_GRAPH_DATA = 24 * 60;
	
	private static final String MINUTE_DATE_FORMAT = "HH:mm";
	private static final String SECOND_DATE_FORMAT = "MM/dd/yyyy HH:mm";
	
	
	private WebView wvDataGraph;	
	private String[] mDateArray = new String[COUNT_DATE];
	private float[] mLflfhfGraphData = new float[COUNT_GRAPH_DATA];

	private int mGraphDataCount = 0;
	private static final int COUNT_NETWORK_GRAPH_DATA = COUNT_GRAPH_DATA;
	private float[] mNetworkGraphData = new float[COUNT_NETWORK_GRAPH_DATA];
	
	private Timer timer60s = new Timer();
	
	private MinuteECGResultManager minuteECGResultManager = new MinuteECGResultManager(this); 
	
	private SharedPreferences sp;
	private SharedPreferences app_skin;
	private String knowledge;
	private String path_image=Program.getSDLangLangImagePath() + "/sleep_image.png";
	private SaveDialog uploaDialog=null;
	SDChecker sdChecker = new SDChecker(this, SDChecker.SPACE_M_0);
	SDChecker sdAlert = new SDChecker(this);
	
	private int mBleState = BleConnectionService.STATE_DISCONNECTED;
	BleConnectionNotifiaction mBleConnectionNotifiaction
									= new BleConnectionNotifiaction(this);
	
	private Bitmap mScreenBitmap = null;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_DATA_GRAPH) {
				wvDataGraph.getSettings().setJavaScriptEnabled(true);
				wvDataGraph.getSettings().setBuiltInZoomControls(false);
//				wvDataGraph.getSettings().setLoadWithOverviewMode(true);
				wvDataGraph.loadUrl("file:///android_asset/sleep/lflfhf.html");
				WebViewClient wvc = new WebViewClient() {
					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						
						String dateArr = MiscUtils.parseArrayAsString(mDateArray);
						String lflfhfData = "";
						
//						if (UserInfo.getIntance().getUserData().getUserRole().equals("guardian")) {
							lflfhfData = MiscUtils.parseArrayAsString(mNetworkGraphData);
//						}
//						else {
//							lflfhfData = MiscUtils.parseArrayAsString(mLflfhfGraphData);
//						}
						System.out.println("action SleepActivity lflfhfData:" + lflfhfData);
						System.out.println("action SleepActivity dateArr:" + dateArr);
						
						wvDataGraph.loadUrl("javascript:addLFLFHF('" 
								+ lflfhfData + "', '" 
								+ dateArr + "', " 
								+ mGraphDataCount + ")");
					}
				};
				wvDataGraph.setWebViewClient(wvc);
			}
			else if(msg.what==UPDATE_DATA_SLEEP_HISTORY){
				if(uploaDialog!=null){
					uploaDialog.cancel();
				}
				loadData();
				loadAdapter();
			}
			else if(msg.what==UPDATE_DATA_KNOWLEDGE){				
			}
			else if (msg.what == ALERT_SD_STATUS) {
				sdAlert.checkAndAlert();
			}
			
			else if(msg.what==UPDATE_NOTHING){
				if(uploaDialog!=null){
					uploaDialog.cancel();
				}
				UIUtil.setToast(SleepActivity.this,"服务器访问失败");
				initializeUIData();
			}
			else if(msg.what==UPDATE_DATA_WRANNING){
				if(uploaDialog!=null){
					uploaDialog.cancel();
				}
				UIUtil.setToast(SleepActivity.this,"网络异常");
			}
			else if (msg.what == UPDATA_BLE_STATE) {
				mBleConnectionNotifiaction.show(mBleState);
			}
			
			else if (msg.what == SHOW_CAPTURE_SUCCESS) {
				UIUtil.setToast(SleepActivity.this, "截图成功");
			}
			else if (msg.what == UPDATE_ON_CONNECT) {
				
//				UIUtil.setToast(SleepActivity.this, "设备已连接");
			}
			else if (msg.what == UPDATE_OFF_LINE) {
				
				UIUtil.setToast(SleepActivity.this, UserInfo.getIntance().getUserData().getName()+"不在线");
			}
			else if (msg.what == UPDATE_OFF_CONNECT) {
				
				UIUtil.setToast(SleepActivity.this, UserInfo.getIntance().getUserData().getName()+"的设备未连接");
			}
			else if (msg.what == SHARE_IMAGE) {
				boolean success = (Boolean) msg.obj;
				if (success) {
					shareImage(path_image);
				}
				else {
					UIUtil.setToast(SleepActivity.this, "无法截图");
				}
				
				setShareClickable(true);
			}
		}
	};
	/**
	 * 载入数据
	 */
	private void loadData(){
		if("1".equals(sp.getString("result",""))){
			result_tw.setText("优");
		}else if("2".equals(sp.getString("result",""))){
			result_tw.setText("良");
		}else if("3".equals(sp.getString("result",""))){
			result_tw.setText("中");
		}
		else if("4".equals(sp.getString("result",""))){
			result_tw.setText("中");
		}else if("0".equals(sp.getString("result",""))){
			result_tw.setText(" ");
		}
		if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
			gotosleep_tw.setText("");
			wake_up.setText("");
			valid_sleep.setText("");
			respiratory_arrest_count.setText("");
			Tachypnoea_count.setText("");
			toc_tw.setText("");
			result_tw.setText(" ");
		}else{
			gotosleep_tw.setText(sbuStirng(sp.getString("sleepBegTime","")));
			wake_up.setText(sbuStirng(sp.getString("sleepEndTime","")));
			valid_sleep.setText(sp.getString("sleepTime","0")+"h");
			respiratory_arrest_count.setText(sp.getString("breathStop",""));
			Tachypnoea_count.setText(sp.getString("breathFast",""));
			toc_tw.setText(sp.getString("turnOverCount",""));
		}
		
		System.out.println("knowledge sleep:"+knowledge);
		
	}
	private String sbuStirng(String data_sp){
		if(data_sp.length()>16){
			data_sp=data_sp.substring(0,16);
			return data_sp;
			
		}
		return data_sp;
	}
	/**
	 * 载入适配器数据
	 */
	private void loadAdapter(){
//		adapter.clear();
		knowledge=setRandom(sp.getString("content",""));
		if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
//			adapter.addListItem(sp.getString("resultTips",""));
//			adapter.addListItem(knowledge);
//			adapter.notifyDataSetChanged();
			
			
			evaluates_tw.setText(sp.getString("compareTips",""));
			suggest_tw.setText(sp.getString("resultTips",""));
			knowledge_tw.setText(knowledge);
			
		}else{
//			adapter.addListItem(sp.getString("resultTips",""));
//			adapter.addListItem(sp.getString("compareTips",""));
//			adapter.addListItem(knowledge);
//			adapter.notifyDataSetChanged();
			
			evaluates_tw.setText(sp.getString("compareTips",""));
			suggest_tw.setText(sp.getString("resultTips",""));
			knowledge_tw.setText(knowledge);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep);
		app_skin=getSharedPreferences("app_skin",MODE_PRIVATE);
		 showUploadDialog();
		
		
		
		sp=this.getSharedPreferences("sleepData",MODE_PRIVATE);
		getViewID();
		changeSkin();
		getOnclick();
		mapping();
//		loadData();
		setTimerTask();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		init();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
		} else {
			if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			} else {
				unregisterReceiver(mGattUpdateReceiver);
			}
		}
	}
	
	private void init() {
//		initData();
		initUI();
	}	
	
	private void initUI() {
//		UIUtil.setMessage(handler, UPDATE_NOTHING);
		if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			UIUtil.setLongToast(
					SleepActivity.this, 
					UserInfo.PROMPT_CANT_SEE_SLEEP_IF_NOT_LOGIN);
		}
		else {
			if (UserInfo.getIntance().getUserData().getUserRole().equals("guardian")) {
			}
			else {
				registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
				sdChecker.checkAndAlert();
				
				if (GlobalStatus.getInstance().getBleState() == BleConnectionService.STATE_CONNECTED) {
					// We don't need to show the ble status if it has been connected.
				}
				else {
					setBleState(GlobalStatus.getInstance().getBleState());
				}
			}			
		}
	}

	private void initData() {
	}
	
	private void setTimerTask() {
		timer60s=new Timer();
		timer60s.schedule(new TimerTask() {
			@Override
			public void run() {
//				if (UserInfo.getIntance().getUserData().getUserRole().equals("guardian")) {
					new DataThread().start();
//				}
//				else {
//					updateDataGraphData();
////					updateDataGraphUI();
//					new DataThread().start();
//				}
			}
		}, 0, 1000 * 60 * 60 * 24);
	}
	
	private void cancelTimer() {
		timer60s.cancel();
	}

	/**
	 * 获取控件ID
	 */
	private void getViewID(){
		share_tw=(TextView)this.findViewById(R.id.sleep_share_textview);
		result_tw=(TextView)this.findViewById(R.id.sleep_evaluate_textview);
		toc_tw=(TextView)this.findViewById(R.id.sleep_toc_tw);
		gotosleep_tw=(TextView)this.findViewById(R.id.sleep_gotosleep_tw);
		wake_up=(TextView)this.findViewById(R.id.sleep_wakeup_tw);
		valid_sleep=(TextView)this.findViewById(R.id.sleep_valid_tw);
		respiratory_arrest_count=(TextView)this.findViewById(R.id.sleep_racount_tw);
		Tachypnoea_count=(TextView)this.findViewById(R.id.sleep_Tachypnoea_count_tw);
		
		
		wvDataGraph = (WebView) this.findViewById(R.id.sleep_datagraph_webview);
		
		bg_layout=(LinearLayout)this.findViewById(R.id.sleep_layout);
		noticete_layout=(RelativeLayout)this.findViewById(R.id.sleep_noticave_layout);
		up_tw=(TextView )this.findViewById(R.id.sleep_up_tw);
		left_tw=(TextView )this.findViewById(R.id.sleep_left_tw);
		right_tw=(TextView )this.findViewById(R.id.sleep_right_tw);
		down_tw=(TextView )this.findViewById(R.id.sleep_down_tw);
		namelogo_tw=(TextView )this.findViewById(R.id.sleep_set_textview);
		useriamge_tw=(TextView) this.findViewById(R.id.sleep_userimage_tw);
		usertext_tw=(TextView) this.findViewById(R.id.sleep_userimage_text);
		
		suggest_tw=(TextView)this.findViewById(R.id.sleep_suggest_textview);
		evaluates_tw=(TextView)this.findViewById(R.id.sleep_evaluates_textview);
		knowledge_tw=(TextView)this.findViewById(R.id.sleep_knowledge_textview);
		
		initializeUIData();
	}
	/**
	 * 初始化数据
	 */
	private void initializeUIData() {
		gotosleep_tw.setText("");
		respiratory_arrest_count.setText("");
		Tachypnoea_count.setText("");
		wake_up.setText("");
		toc_tw.setText("");
		valid_sleep.setText("");
	}
	/**
	 * 换肤
	 */
	private void changeSkin(){
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			bg_layout.setBackgroundResource(R.drawable.bg_item);
			left_tw.setVisibility(View.GONE);
			up_tw.setVisibility(View.GONE);
			right_tw.setVisibility(View.GONE);
			down_tw.setVisibility(View.GONE);
			share_tw.setBackgroundResource(R.drawable.share_click_item);
			namelogo_tw.setTextColor(getResources().getColor(R.color.white));
			result_tw.setBackgroundResource(R.drawable.lever_02_item);
			result_tw.setTextColor(getResources().getColor(R.color.blue_text));
			noticete_layout.setBackgroundResource(R.drawable.item_notice_item);
			useriamge_tw.setBackgroundResource(R.drawable.user_jin_03_item);
			usertext_tw.setTextColor(getResources().getColor(R.color.white));
			
		}
	}
	
	/**
	 * 获取控件点击事件
	 */
	private void getOnclick(){
		share_tw.setOnClickListener(listener);
	}
	/**
	 * 设置控件点击事件
	 */
	private OnClickListener listener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.sleep_share_textview:
//				boolean result = ScreenShotUtils.shotBitmap(SleepActivity.this,path_image);
//				if(result)
//				{
//					Toast.makeText(SleepActivity.this, "截图成功.", Toast.LENGTH_SHORT).show();
//					shareImage(path_image);
//				}else {
//					Toast.makeText(SleepActivity.this, "截图失败.", Toast.LENGTH_SHORT).show();
//				}
				setShareClickable(false);
				mScreenBitmap = ScreenShotUtils.takeScreenShot(SleepActivity.this);
				if (mScreenBitmap == null) {
					setShareClickable(true);
					UIUtil.setMessage(handler, SHARE_IMAGE, false);
					return;
				}
				UIUtil.setMessage(handler, SHOW_CAPTURE_SUCCESS);
				new ScreenShotThread().start();
				
				break;

			default:
				break;
			}
		}
	};
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
	/**
	 * 映射数据
	 */
	private void mapping(){
		usertext_tw.setText(UserInfo.getIntance().getUserData().getName());
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			cancelTimer();
			
			startActivity(new Intent(SleepActivity.this,MainActivity.class));
			SleepActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void updateDataGraphData() {
		getDateArray();
		getGraphData();
	}
	
	private void updateNetworkGraphData(Map<String, String> dataMap) {
		getNetworkDateArray(dataMap); 
		getNetworkGraphData(dataMap);
	}

	private void getNetworkGraphData(Map<String, String> dataMap) {
		SimpleDateFormat sdf = new SimpleDateFormat(SECOND_DATE_FORMAT);
		
		Calendar calendar = Calendar.getInstance();		
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		
		if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			for (int i = 0; i < COUNT_GRAPH_DATA; i++) {
				mNetworkGraphData[i] = 0.0f;
			}			
		}
		else {
			for (int i = 0; i < COUNT_GRAPH_DATA; i++) {
				Date date = calendar.getTime();
//				String dateStr = sdf.format(date) + ":00";
				String dateStr = sdf.format(date);
				
				String value = dataMap.get(dateStr);
				if (value != null) {
					try {
						mNetworkGraphData[i] = Float.parseFloat(value);
						System.out.println("ation data SleepActivity mNetworkGraphData:" + mNetworkGraphData[i]);
					} catch (NumberFormatException e) {
						System.out.println("action SleepActivity getNetworkGraphData NumberFormatException");
						mNetworkGraphData[i] = 0.0f;
					}
				}
				
				calendar.add(Calendar.MINUTE, 1);
			}
		}
		
		mGraphDataCount = COUNT_NETWORK_GRAPH_DATA;
	}
	
	@SuppressLint("SimpleDateFormat")
	private void getNetworkDateArray(Map<String, String> dataMap) {
		SimpleDateFormat sdf = new SimpleDateFormat(MINUTE_DATE_FORMAT);
		
		Calendar calendar = Calendar.getInstance();		
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		
		for (int i = 0; i < COUNT_DATE; i++) {
			Date date = calendar.getTime();
			String dateString = sdf.format(date);
			mDateArray[i] = dateString;
			
			calendar.add(Calendar.HOUR_OF_DAY, 4);
		}
	}
	
	private void updateDataGraphUI() {
		UIUtil.setMessage(handler, UPDATE_DATA_GRAPH);
	}

	@SuppressLint("SimpleDateFormat")
	private void getDateArray() {
		SimpleDateFormat sdf = new SimpleDateFormat(MINUTE_DATE_FORMAT);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -1);
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		
		for (int i = 0; i < COUNT_DATE; i++) {
			Date date = calendar.getTime();
			String dateString = sdf.format(date);
			mDateArray[i] = dateString;
			
			calendar.add(Calendar.HOUR_OF_DAY, 4);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void getGraphData() {
		if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			for (int i = 0; i < COUNT_GRAPH_DATA; i++) {
				mLflfhfGraphData[i] = 0.0f;
			}			
		}
		else {
		SimpleDateFormat sdf = new SimpleDateFormat(Program.MINUTE_FORMAT);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -1);
		calendar.add(Calendar.HOUR_OF_DAY, -24);		
		Date start = calendar.getTime();
		
		calendar.add(Calendar.HOUR_OF_DAY, 24);		
		Date end = calendar.getTime();
		
		Map<String, ECGResult> ecgResults = null;
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		if ((uid != null) && (uid.length() > 0)) {
//		Map<String, ECGResult> ecgResults 
//							= minuteECGResultManager.getECGResultsBetween(start, end);
			ecgResults = minuteECGResultManager.getECGResultsBetween(start, end, uid);
		} else {
			ecgResults = null;
		}
		
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		
		if (ecgResults != null) {
			for (int i = 0; i < COUNT_GRAPH_DATA; i++) {
				Date date = calendar.getTime();
				String minuteData = sdf.format(date);
				
				ECGResult result = ecgResults.get(minuteData);
				if (result != null) {
					float lf = result.LF;
					float hf = result.HF;
					float sum = lf + hf;
					
					if (Float.compare(sum, 0) == 0) {
						mLflfhfGraphData[i] = 0.0f;
					} else {
						mLflfhfGraphData[i] = lf / sum;
					}
				} else {
					mLflfhfGraphData[i] = 0.0f;
				}
						
				calendar.add(Calendar.MINUTE, 1);
			}
		} else {
			for (int i = 0; i < COUNT_GRAPH_DATA; i++) {
				mLflfhfGraphData[i] = 0.0f;
			}			
		}
		}
		
		mGraphDataCount = COUNT_GRAPH_DATA;
	}
	class DataThread extends Thread{
		private Date first_data=new Date();
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name=UserInfo.getIntance().getUserData().getUser_name();
			String userInfo = "[{username:\""+user_name+"\"}]";
			String sleep_data=Client.getSleepTips(userInfo);
			Date second_date = new Date();
			if ((second_date.getTime() - first_data.getTime()) < (1000 * 2)) {
				try {
					Thread.sleep((long) (1000 * 1.0));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("sleepactivity sleepdata:"+sleep_data);

			try {
				if(sleep_data!=null){
					if(sleep_data.equals("0")){
						UIUtil.setMessage(handler, UPDATE_NOTHING);
						return;
					}
					JSONArray jsonArray=new JSONArray(sleep_data);
					JSONObject jsonObject=jsonArray.getJSONObject(0);
					String sleepBegTime=jsonObject.getString("sleepBegTime");
					String sleepEndTime=jsonObject.getString("sleepEndTime");
					String sleepTime=jsonObject.getString("sleepTime");
					String breathStop=jsonObject.getString("breathStop");
					String breathFast=jsonObject.getString("breathFast");
					String result=jsonObject.getString("result");
					String turnOverCount=jsonObject.getString("turnOverCount");
					String resultTips=jsonObject.getString("resultTips");
					String compareTips=jsonObject.getString("compareTips");
					String content=jsonObject.getString("libraryTips");
					String status=jsonObject.getString("status");
					System.out.println("sleepactivity sleepdata item["
							+ "sleepBegTime:" + sleepBegTime + ",sleepEndTime:"
							+ sleepEndTime + ",sleepTime:" + sleepTime
							+ ",breathStop:" + breathStop + ",breathFast:"
							+ breathFast + ",result:" + result
							+ ",turnOverCount:" + turnOverCount
							+ ",resultTips:" + resultTips + ",compareTips:"
							+ compareTips + ",content:" + content +",status:" + status + "]");
					if (!UserInfo.getIntance().getUserData().getName()
							.equals(UserInfo.getIntance().getUserData().getFinal_name())) {

						if (checkBleState(status) == 2) {
							UIUtil.setMessage(handler, UPDATE_ON_CONNECT);
						} else if (checkBleState(status) == -1) {
							// UIUtil.setMessage(handler, UPDATE_OFF_LINE);
						} else {
							UIUtil.setMessage(handler, UPDATE_OFF_CONNECT);
						}
					}
					JSONArray contentArray=new JSONArray(content);
					StringBuffer sb=new StringBuffer();
					for(int i=0;i<contentArray.length();i++){
						JSONObject  contentObject=contentArray.getJSONObject(i);
						sb.append(contentObject.getString("content")+"/");
					}
					SharedPreferences.Editor editor=sp.edit();
					editor.putString("sleepBegTime", sleepBegTime);
					editor.putString("sleepEndTime", sleepEndTime);
					editor.putString("sleepTime", sleepTime);
					editor.putString("breathStop", breathStop);
					editor.putString("breathFast", breathFast);
					editor.putString("result", result);
					editor.putString("resultTips", resultTips);
					editor.putString("compareTips",compareTips);
					editor.putString("content", sb.toString());
					editor.putString("turnOverCount",turnOverCount);
					
					
					editor.commit();
					
//					if(UserInfo.getIntance().getUserData().getUserRole().equals("guardian")){
						JSONObject jDataMap = jsonObject.getJSONObject("dataMap");
						Map<String, String> dataMap = MiscUtils.parseJSONAsMap(jDataMap);						
						updateNetworkGraphData(dataMap);
						updateDataGraphUI();
//					}else{
//					}					
						UIUtil.setMessage(handler, UPDATE_DATA_SLEEP_HISTORY);
				}else{
					UIUtil.setMessage(handler, UPDATE_DATA_WRANNING);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	/**
	 * 设置随机数
	 * @param daString
	 * @return
	 */
	private String setRandom(String daString){
		if(daString.equals("")||daString==null){
			return "";
		}
		String [] dataStrings=daString.split("/");
		int index=new Random().nextInt(dataStrings.length);
		return dataStrings[index];
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataStorageService.ACTION_ALERT_SD_STATUS);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(DataStorageService.ACTION_ALERT_SD_STATUS);
		intentFilter.addAction(BleConnectionService.ACTION_RESPONSE_BLE_STATE);
		return intentFilter;
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				
				setBleState(BleConnectionService.STATE_CONNECTED);
				
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				
				setBleState(BleConnectionService.STATE_DISCONNECTED);
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (DataStorageService.ACTION_ALERT_SD_STATUS.equals(action)) {
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
	
	private void setBleState(int state) {
		mBleState = state;
		System.out.println("action SleepActivity setBleState[" + state + "]");
		UIUtil.setMessage(handler, UPDATA_BLE_STATE);
	}
	
	private void setShareClickable(boolean clickable) {
		share_tw.setClickable(clickable);
	}
	
	private class ScreenShotThread extends Thread {
		public void run() {
			if (mScreenBitmap == null) return;
			
			boolean success = ScreenShotUtils.savePic(mScreenBitmap, path_image);
			UIUtil.setMessage(handler, SHARE_IMAGE, success);
		}
	}

	private void showUploadDialog(){
		uploaDialog = new SaveDialog(SleepActivity.this, saveCallBack);
			// 设置进度条风格，风格为圆形，旋转的
		uploaDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		uploaDialog.setMessage("正在加载,请稍候...");
		uploaDialog.setIndeterminate(false);
		uploaDialog.setCancelable(false);
		uploaDialog.show();
	}
	private SaveCallBack saveCallBack=new SaveCallBack() {
		
		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			startActivity(new Intent(SleepActivity.this,MainActivity.class));
			uploaDialog.cancel();
			SleepActivity.this.finish();
		}
	};
	/**
	 * 判断被监护人的设备连接状态
	 * @param status
	 * @return
	 * 2:设备已连接
	 * -1：用户不在先
	 * 0:设备未连接
	 */
	private int checkBleState(String status){
		if("2".equals(status)){
			return 2;	
		}else if("-1".equals(status)){
			return -1;
		}else{
			return 0;
		}
		
	}
}
