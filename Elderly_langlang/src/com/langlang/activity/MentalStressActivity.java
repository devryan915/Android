package com.langlang.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.langlang.adapter.StressAdapter;
import com.langlang.ble.BleConnectionNotifiaction;
import com.langlang.data.Knowledge;
import com.langlang.data.StressLevelItem;
import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.service.BleConnectionService;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MentalStressActivity extends BaseActivity {
	private final int KNOWLEDGE = 0;
	private final static int UPDATE_MENTAL_STRESS = 1;
	private final static int UPDATE_MENTAL_STRESS_DETAIL = 2;
	private final static int UPDATE_ON_CONNECT = 112;
	private final static int UPDATE_OFF_CONNECT = 111;
	private final static int UPDATE_NOT_MESSAGE = 113;
	private final static int UPDATE_OFF_LINE = 1113;
	private final int UPDATE_ON_LINE_NO_DATA=1115;
	private final static int UPDATE_DATE_WRANNING = 114;
	private final int UPDATA_NETWORK_DATA =3;
	
	private final static int ALERT_SD_STATUS = 30;
	
	private final static int UPDATA_BLE_STATE=40;
	
	private final int SHARE_IMAGE = 50; 
	private final int SHOW_CAPTURE_SUCCESS = 51;
	
	private final int NOTIFY_INVALID_ECG = 60;
	
	private SharedPreferences sp;
	private SharedPreferences app_skin;
	private TextView a1, b1, c1, d1, e1, a2, b2, c2, d2, e2, a3, b3, c3, d3,
			e3;
	private TextView a4, b4, c4, d4, e4, a5, b5, c5, d5, e5, a6, b6, c6, d6,
			e6;
	private LinearLayout bg_layout;
	private TextView namelogo_tw;
	private RelativeLayout notivate_layout;
//	private ListView listView;
//	private StressAdapter adapter;
	private TextView knowledge_tw;
	private TextView suggest_tw;
	private TextView evaluates_tw;
	private TextView useriamge_tw;
	private TextView usertext_tw;
	private TextView share_tw;
	private TextView evaluate_tw;// 当前压力值评价
	private TextView evaluate_progress;// 压力显示图
	private Timer timer = new Timer();
	private Timer timer1m = new Timer();
	private String result;
	private String compare;
	private String knowledge;
	private String mNetwork_result;
	private String mNetwork_compare;
	private String mStatus;
	private int mMentalevel = -1;
	private String mMental_compare;
	private ArrayList<String>mCompare_list=new ArrayList<String>();
	
	private String path_image=Program.getSDLangLangImagePath() + "/mental_image.png";
	
	private StressLevelItem[] mStressLevelItems = null;
	
	SDChecker sdChecker = new SDChecker(this, SDChecker.SPACE_M_0);
	SDChecker sdAlert = new SDChecker(this);
	
	private int mBleState = BleConnectionService.STATE_DISCONNECTED;
	BleConnectionNotifiaction mBleConnectionNotifiaction
									= new BleConnectionNotifiaction(this);

	private Bitmap mScreenBitmap = null;
	private SaveDialog uploaDialog=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mental_stress);
		
		app_skin=getSharedPreferences("app_skin",MODE_PRIVATE);
		
		
		
		sp = this.getSharedPreferences("mentalknowledge", MODE_PRIVATE);
		getViewId();
		checkImage();
		changeSkin();
		new knowledgeThree().start();
		getOnclick();
		mapping();
		
	}
	/**
	 * 根据皮肤判断图片资源
	 */
	private void checkImage(){
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			evaluate_progress.setBackgroundResource(R.drawable.low_09_item);
		}
	}
	/**
	 * 获取控件Id
	 */
	private void getViewId() {
		a1 = (TextView) this.findViewById(R.id.mts_a1_tw);
		b1 = (TextView) this.findViewById(R.id.mts_b1_tw);
		c1 = (TextView) this.findViewById(R.id.mts_c1_tw);
		d1 = (TextView) this.findViewById(R.id.mts_d1_tw);
		e1 = (TextView) this.findViewById(R.id.mts_e1_tw);

		a2 = (TextView) this.findViewById(R.id.mts_a2_tw);
		b2 = (TextView) this.findViewById(R.id.mts_b2_tw);
		c2 = (TextView) this.findViewById(R.id.mts_c2_tw);
		d2 = (TextView) this.findViewById(R.id.mts_d2_tw);
		e2 = (TextView) this.findViewById(R.id.mts_e2_tw);

		a3 = (TextView) this.findViewById(R.id.mts_a3_tw);
		b3 = (TextView) this.findViewById(R.id.mts_b3_tw);
		c3 = (TextView) this.findViewById(R.id.mts_c3_tw);
		d3 = (TextView) this.findViewById(R.id.mts_d3_tw);
		e3 = (TextView) this.findViewById(R.id.mts_e3_tw);

		a4 = (TextView) this.findViewById(R.id.mts_a4_tw);
		b4 = (TextView) this.findViewById(R.id.mts_b4_tw);
		c4 = (TextView) this.findViewById(R.id.mts_c4_tw);
		d4 = (TextView) this.findViewById(R.id.mts_d4_tw);
		e4 = (TextView) this.findViewById(R.id.mts_e4_tw);

		a5 = (TextView) this.findViewById(R.id.mts_a5_tw);
		b5 = (TextView) this.findViewById(R.id.mts_b5_tw);
		c5 = (TextView) this.findViewById(R.id.mts_c5_tw);
		d5 = (TextView) this.findViewById(R.id.mts_d5_tw);
		e5 = (TextView) this.findViewById(R.id.mts_e5_tw);

		a6 = (TextView) this.findViewById(R.id.mts_a6_tw);
		b6 = (TextView) this.findViewById(R.id.mts_b6_tw);
		c6 = (TextView) this.findViewById(R.id.mts_c6_tw);
		d6 = (TextView) this.findViewById(R.id.mts_d6_tw);
		e6 = (TextView) this.findViewById(R.id.mts_e6_tw);

		evaluate_tw = (TextView) this.findViewById(R.id.mts_evaluate_textview);
		evaluate_progress = (TextView) this
				.findViewById(R.id.mts_evaluate_progress);
		share_tw = (TextView) this.findViewById(R.id.mts_share_textview);
//		listView = (ListView) this.findViewById(R.id.mts_listview);
//		adapter = new StressAdapter(MentalStressActivity.this);
//		listView.setAdapter(adapter);
		
		bg_layout=(LinearLayout)this.findViewById(R.id.mst_layout);
		notivate_layout=(RelativeLayout)this.findViewById(R.id.mts_noticate_layout);
		namelogo_tw=(TextView)this.findViewById(R.id.mts_set_textview);
		useriamge_tw=(TextView) this.findViewById(R.id.mts_userimage_tw);
		usertext_tw=(TextView) this.findViewById(R.id.mts_userimage_text);
		suggest_tw=(TextView)this.findViewById(R.id.mts_suggest_textview);
		evaluates_tw=(TextView)this.findViewById(R.id.mts_evaluates_textview);
		knowledge_tw=(TextView)this.findViewById(R.id.mts_knowledge_textview);
	}

	/**
	 * 换肤
	 */
	private void changeSkin(){
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			share_tw.setBackgroundResource(R.drawable.share_click_item);
			bg_layout.setBackgroundResource(R.drawable.bg_item);
			namelogo_tw.setTextColor(getResources().getColor(R.color.white));
			evaluate_tw.setBackgroundResource(R.drawable.lever_02_item);
			evaluate_tw.setTextColor(getResources().getColor(R.color.blue_text));
			notivate_layout.setBackgroundResource(R.drawable.item_notice_item);
			useriamge_tw.setBackgroundResource(R.drawable.user_jin_03_item);
			usertext_tw.setTextColor(getResources().getColor(R.color.white));
		}
	}
	/**
	 * 获取控件点击事件
	 */
	private void getOnclick() {
		share_tw.setOnClickListener(listener);
	}

	/**
	 * 设置控件点击事件
	 */
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.mts_share_textview:
//				boolean result = ScreenShotUtils.shotBitmap(MentalStressActivity.this,path_image);
//				if(result)
//				{
//					Toast.makeText(MentalStressActivity.this, "截图成功.", Toast.LENGTH_SHORT).show();
//					shareImage(path_image);
//				}else {
//					Toast.makeText(MentalStressActivity.this, "截图失败.", Toast.LENGTH_SHORT).show();
//				}
				setShareClickable(false);
				mScreenBitmap = ScreenShotUtils.takeScreenShot(MentalStressActivity.this);
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
	private void mapping() {
		usertext_tw.setText(UserInfo.getIntance().getUserData().getName());
	}

	class knowledgeThree extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			SharedPreferences.Editor editor = sp.edit();
			String param = "[{type:\"4\"}]";
			StringBuffer sb = new StringBuffer();
			try {
				String knowladgeResult = Client.getTips(param);
				System.out.println("mentalstressactivity knowladgeResult:"
						+ knowladgeResult);
				if (knowladgeResult != null) {
					JSONArray jsonArray = new JSONArray(knowladgeResult);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						sb.append(jsonObject.getString("content") + "/");
					}
					if (sb.length() > 0 || !sb.toString().equals("")) {
						editor.putString("kndata", sb.toString());
						editor.commit();
					}
				}
				System.out.println("my sb:" + sb.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == KNOWLEDGE) {
				updateProgress();
				Knowledge knowledgeees = (Knowledge) msg.obj;
				evaluates_tw.setText(knowledgeees.compare);
				suggest_tw.setText(knowledgeees.result);
				knowledge_tw.setText(knowledgeees.knowledge);
			}

			else if (msg.what == UPDATE_MENTAL_STRESS) {
				updateMentalStress();
			}

			else if (msg.what == UPDATE_MENTAL_STRESS_DETAIL) {
				updateMentalStressDetail();
			}
			else if(msg.what==UPDATA_NETWORK_DATA){
				dialogCancel();
				updateMentalStress();
				 showNetworkStressLevelsInUI();
			}
			else if (msg.what == ALERT_SD_STATUS) {
				sdAlert.checkAndAlert();
			}
			else if (msg.what == UPDATA_BLE_STATE) {
				mBleConnectionNotifiaction.show(mBleState);
			}
			else if (msg.what == SHOW_CAPTURE_SUCCESS) {
				UIUtil.setToast(MentalStressActivity.this, "截图成功");
			}
			else if (msg.what == UPDATE_ON_CONNECT) {
				dialogCancel();
//				UIUtil.setToast(MentalStressActivity.this, "设备已连接");
			}
			else if (msg.what == UPDATE_OFF_LINE) {
				dialogCancel();
				UIUtil.setToast(MentalStressActivity.this, UserInfo.getIntance().getUserData().getName()+"不在线");
			}
			else if (msg.what == UPDATE_OFF_CONNECT) {
				dialogCancel();
				UIUtil.setToast(MentalStressActivity.this,UserInfo.getIntance().getUserData().getName()+"的设备未连接");
			}
			else if (msg.what == UPDATE_ON_LINE_NO_DATA) {
				dialogCancel();
				UIUtil.setToast(MentalStressActivity.this, "没有最新数据");
			}
			else if (msg.what == SHARE_IMAGE) {
				boolean success = (Boolean) msg.obj;
				if (success) {
					shareImage(path_image);
				}
				else {
					UIUtil.setToast(MentalStressActivity.this, "无法截图");
				}
				
				setShareClickable(true);
			}
			else if (msg.what == NOTIFY_INVALID_ECG) {
				UIUtil.setToast(MentalStressActivity.this, "设备处于无效状态,请检查");
			}
			else if (msg.what == UPDATE_NOT_MESSAGE) {
				UIUtil.setToast(MentalStressActivity.this, "服务器访问失败");
				dialogCancel();
			}
			else if(msg.what==UPDATE_DATE_WRANNING){
				dialogCancel();
				UIUtil.setToast(MentalStressActivity.this, "获取压力值数据时，网络异常");
			}
		}
	};

	private void updateKnowledge(String result, String compare, String knowledge) {
		UIUtil.setMessage(handler, KNOWLEDGE, new Knowledge(result, compare,
				knowledge));
	}

	/**
	 * 更新进度条状态
	 */
	private void updateProgress() {
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			if (mMentalevel == 1) {

				evaluate_progress.setBackgroundResource(R.drawable.low_09_item);
			} else if (mMentalevel == 2) {

				evaluate_progress.setBackgroundResource(R.drawable.namal_09_item);
			}
			 else if (mMentalevel == 3) {

					evaluate_progress.setBackgroundResource(R.drawable.high_09_item);
				}
			else {
//				evaluate_progress.setBackgroundResource(R.drawable.high_09_item);
			}
		}else{
			if (mMentalevel == 1) {

				evaluate_progress.setBackgroundResource(R.drawable.low_09);
			} else if (mMentalevel == 2) {

				evaluate_progress.setBackgroundResource(R.drawable.namal_09);
			} else if (mMentalevel == 3) {

				evaluate_progress.setBackgroundResource(R.drawable.high_09);
			} else {
//				evaluate_progress.setBackgroundResource(R.drawable.high_09);
			}
		}
		
	}

	/**
	 * 当天结果
	 * 
	 * @return
	 */
	private String getResult() {
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")){
			result=mNetwork_result;
		}else{
		if (mMentalevel == 2) {

			result = "心态平和，状态不错！";
		} else if (mMentalevel == 1) {

			result = "有点压力，慢慢调节哦！";
		}
		else if (mMentalevel == 3) {

			result = "注意调整心态哦！";
		}else {
			result = "";
		}}
		return result;
	}

	/**
	 * 历史比较
	 * 
	 * @return
	 */
	private String getcompare() {
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
			compare = mNetwork_compare;
		} else {

			if (mMentalevel == 2) {
				compare = "注意保持平稳的心态，避免波动哦！";
			} else if (mMentalevel == 1) {
				compare = "压力的调整得到了有效改善，真棒！";
			} else if (mMentalevel == 3) {
				compare = "慢慢来，循序渐进调节心态！";
			}else {
				compare = "";
			}
		}
		return compare;
	}

	/**
	 * 小知识
	 * 
	 * @return
	 */
	private String getKnowledge() {
		String knowledge_data = sp.getString("kndata", "");
		String[] kle = knowledge_data.split("/");
		int index = new Random().nextInt(kle.length);
		knowledge = kle[index];
		return knowledge;
	}

	private void updateMentalStressData() {
		int stressLevel = GlobalStatus.getInstance().getStressLevel();

		if ((stressLevel > 0) && (stressLevel < 50)) {
			mMentalevel = 1;
		} else if ((stressLevel >= 50) && (stressLevel < 80)) {
			mMentalevel = 2;
		} else if ((stressLevel >= 80) && (stressLevel < 101)) {
			mMentalevel = 3;
		} else {
			mMentalevel = -1;
		}
	}

	private void updateMentalStressDetailData() {
		mStressLevelItems = GlobalStatus.getInstance().getStressLevelReport();
	}

	private void updateMentalStressUI() {
		UIUtil.setMessage(handler, UPDATE_MENTAL_STRESS);
	}

	private void updateMentalStressDetailUI() {
		UIUtil.setMessage(handler, UPDATE_MENTAL_STRESS_DETAIL);
	}

	private void updateMentalStress() {
		if (mMentalevel == 1) {
			evaluate_tw.setText("优");
		} else if (mMentalevel == 2) {
			evaluate_tw.setText("良");
		} else if (mMentalevel == 3) {
			evaluate_tw.setText("中");
		} else {
			evaluate_tw.setText("");
		}
	}

	private void updateMentalStressDetail() {
		String[][] stressLevels = initStressLevels();

		if (mStressLevelItems != null) {
			for (int i = 0; i < mStressLevelItems.length; i++) {
//				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][0] = Float
//						.toString(mStressLevelItems[i].stressA);
//
//				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][1] = Float
//						.toString((mStressLevelItems[i].stressB1 + mStressLevelItems[i].stressB2) / 2);
//
//				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][2] = Float
//						.toString((mStressLevelItems[i].stressC1
//								+ mStressLevelItems[i].stressC2 + mStressLevelItems[i].stressC3) / 3);
//
//				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][3] = Float
//						.toString(mStressLevelItems[i].stressD);
//
//				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][4] = Float
//						.toString((mStressLevelItems[i].stressE1 + mStressLevelItems[i].stressE2) / 2);
				
				DecimalFormat Formator = new DecimalFormat("#.#");
				
				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][0] 
									= Formator.format(mStressLevelItems[i].stressA);

				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][1] 
									= Formator.format( 
										(mStressLevelItems[i].stressB1 + mStressLevelItems[i].stressB2) / 2);

				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][2] 
									= Formator.format(
									 (mStressLevelItems[i].stressC1
									+ mStressLevelItems[i].stressC2 
									+ mStressLevelItems[i].stressC3) / 3);

				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][3] 
									= Formator.format(mStressLevelItems[i].stressD);

				stressLevels[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT - 1 - i][4] 
									= Formator.format(
									  (mStressLevelItems[i].stressE1 + mStressLevelItems[i].stressE2) / 2);

				showStressLevelsInUI(stressLevels);
			}
		}
	};

	private void showStressLevelsInUI(String[][] stressLevels) {
//		a1.setText(stressLevels[0][0]);
//		b1.setText(stressLevels[0][1]);
//		c1.setText(stressLevels[0][2]);
//		d1.setText(stressLevels[0][3]);
//		e1.setText(stressLevels[0][4]);
//
//		a2.setText(stressLevels[1][0]);
//		b2.setText(stressLevels[1][1]);
//		c2.setText(stressLevels[1][2]);
//		d2.setText(stressLevels[1][3]);
//		e2.setText(stressLevels[1][4]);
//
//		a3.setText(stressLevels[2][0]);
//		b3.setText(stressLevels[2][1]);
//		c3.setText(stressLevels[2][2]);
//		d3.setText(stressLevels[2][3]);
//		e3.setText(stressLevels[2][4]);
//
//		a4.setText(stressLevels[3][0]);
//		b4.setText(stressLevels[3][1]);
//		c4.setText(stressLevels[3][2]);
//		d4.setText(stressLevels[3][3]);
//		e4.setText(stressLevels[3][4]);
//
//		a5.setText(stressLevels[4][0]);
//		b5.setText(stressLevels[4][1]);
//		c5.setText(stressLevels[4][2]);
//		d5.setText(stressLevels[4][3]);
//		e5.setText(stressLevels[4][4]);
//
//		a6.setText(stressLevels[5][0]);
//		b6.setText(stressLevels[5][1]);
//		c6.setText(stressLevels[5][2]);
//		d6.setText(stressLevels[5][3]);
//		e6.setText(stressLevels[5][4]);
		
		a1.setText(stressLevels[5][0]);
		b1.setText(stressLevels[5][1]);
		c1.setText(stressLevels[5][2]);
		d1.setText(stressLevels[5][3]);
		e1.setText(stressLevels[5][4]);

		a2.setText(stressLevels[4][0]);
		b2.setText(stressLevels[4][1]);
		c2.setText(stressLevels[4][2]);
		d2.setText(stressLevels[4][3]);
		e2.setText(stressLevels[4][4]);

		a3.setText(stressLevels[3][0]);
		b3.setText(stressLevels[3][1]);
		c3.setText(stressLevels[3][2]);
		d3.setText(stressLevels[3][3]);
		e3.setText(stressLevels[3][4]);

		a4.setText(stressLevels[2][0]);
		b4.setText(stressLevels[2][1]);
		c4.setText(stressLevels[2][2]);
		d4.setText(stressLevels[2][3]);
		e4.setText(stressLevels[2][4]);

		a5.setText(stressLevels[1][0]);
		b5.setText(stressLevels[1][1]);
		c5.setText(stressLevels[1][2]);
		d5.setText(stressLevels[1][3]);
		e5.setText(stressLevels[1][4]);

		a6.setText(stressLevels[0][0]);
		b6.setText(stressLevels[0][1]);
		c6.setText(stressLevels[0][2]);
		d6.setText(stressLevels[0][3]);
		e6.setText(stressLevels[0][4]);
	}

	private void showNetworkStressLevelsInUI() {
		String[] stressData = new String[30];
		for (int i = 0; i < stressData.length; i++) {
			stressData[i] = "-";
		}
		
		DecimalFormat formator = new DecimalFormat("#.#");
		for (int i = 0; (i < mCompare_list.size()) && (i < stressData.length); i++) {
			String item = mCompare_list.get(i);
			Double doubleData;
			try {
				doubleData = Double.parseDouble(item);
			} catch (Exception e) {
				doubleData = 0.0;
			}
			stressData[i] = formator.format(doubleData);
		}

		a1.setText(stressData[0]);
		b1.setText(stressData[1]);
		c1.setText(stressData[2]);
		d1.setText(stressData[3]);
		e1.setText(stressData[4]);

		a2.setText(stressData[5]);
		b2.setText(stressData[6]);
		c2.setText(stressData[7]);
		d2.setText(stressData[8]);
		e2.setText(stressData[9]);

		a3.setText(stressData[10]);
		b3.setText(stressData[11]);
		c3.setText(stressData[12]);
		d3.setText(stressData[13]);
		e3.setText(stressData[14]);

		a4.setText(stressData[15]);
		b4.setText(stressData[16]);
		c4.setText(stressData[17]);
		d4.setText(stressData[18]);
		e4.setText(stressData[19]);

		a5.setText(stressData[20]);
		b5.setText(stressData[21]);
		c5.setText(stressData[22]);
		d5.setText(stressData[23]);
		e5.setText(stressData[24]);

		a6.setText(stressData[25]);
		b6.setText(stressData[26]);
		c6.setText(stressData[27]);
		d6.setText(stressData[28]);
		e6.setText(stressData[29]);
	}
	private String[][] initStressLevels() {
		String[][] stressLevels = new String[GlobalStatus.MAX_ITEM_IN_STRESS_REPORT][GlobalStatus.COLUMN_OF_STRESS_REPORT];

		for (int i = 0; i < GlobalStatus.MAX_ITEM_IN_STRESS_REPORT; i++) {
			for (int j = 0; j < GlobalStatus.COLUMN_OF_STRESS_REPORT; j++) {
				stressLevels[i][j] = "-";
			}
		}

		return stressLevels;
	}

	private void setTimerTask() {
		timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				

				updateMentalStressData();
				updateMentalStressDetailData();
				
				getResult();
				getcompare();
				getKnowledge();
				updateKnowledge(result, compare, knowledge);

				updateMentalStressUI();
				updateMentalStressDetailUI();
			}
		}, 0, 1000 * 15);

	}

	private void setNetworkTimer() {
timer1m=new Timer();
		timer1m.schedule(new TimerTask() {
			@Override
			public void run() {
				new updataDataThread().start();
			}
		}, 0, 1000 * 60);

	}

	private void cancelTimer() {
		timer.cancel();
	}
	private void cancelNetworkTimer() {
		timer1m.cancel();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
//			UIUtil.setMessage(handler, UPDATA_NETWORK_DATA);
			setNetworkTimer();
			showUploadDialog();
		} else {
			setTimerTask();
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			
			if (GlobalStatus.getInstance().getBleState() == BleConnectionService.STATE_CONNECTED) {
				// We don't need to show the ble status if it has been connected.
			}
			else {
				setBleState(GlobalStatus.getInstance().getBleState());
			}
			
			sdChecker.checkAndAlert();
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
			cancelNetworkTimer();
		} else {
			unregisterReceiver(mGattUpdateReceiver);
			
			cancelTimer();
		}

	}

	class updataDataThread extends Thread {
		private Date first_data=new Date();
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
	
			String user_name = UserInfo.getIntance().getUserData()
					.getUser_name();
			System.out.println("actact:" + user_name);
			String userInfo = "[{username:\"" + user_name + "\"}]";

			try {
				String user_data = Client.getPressureTips(userInfo);
				Date second_date = new Date();
				if ((second_date.getTime() - first_data.getTime()) < (1000 * 2)) {
					try {
						Thread.sleep((long) (1000 * 1.0));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("mentalStressActivity data :" + user_data);
				if (user_data == null || "".equals(user_data)) {
					UIUtil.setMessage(handler, UPDATE_DATE_WRANNING);
					return;
				}
				if("0".equals(user_data)){
					UIUtil.setMessage(handler, UPDATE_NOT_MESSAGE);
					return;
				}
				if("-1".equals(user_data)){
					UIUtil.setMessage(handler, UPDATE_OFF_LINE);
					return;
				}
				JSONArray jsonArray = new JSONArray(user_data);
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				mMentalevel = StringToInt(jsonObject.getString("result"));
				mNetwork_result = jsonObject.getString("resultTips");
				mNetwork_compare = jsonObject.getString("compareTips");
				mMental_compare=jsonObject.getString("graphDataList");
				mStatus=jsonObject.getString("status");
				
				JSONArray compareArray = new JSONArray(mMental_compare);
				
				mCompare_list.clear();
				for(int i=0;i<compareArray.length();i++)
				{
					JSONObject compareObject = compareArray.getJSONObject(i);
					mCompare_list.add(compareObject.getString("stressa"));
					mCompare_list.add(compareObject.getString("stressb"));
					mCompare_list.add(compareObject.getString("stressc"));
					mCompare_list.add(compareObject.getString("stressd"));
					mCompare_list.add(compareObject.getString("stresse"));
					
				}
					
				
				getResult();
				getcompare();
				getKnowledge();
				updateKnowledge(result, compare, knowledge);
				if(checkBleState(mStatus)==2){
					UIUtil.setMessage(handler, UPDATE_ON_CONNECT);
				}else if(checkBleState(mStatus)==-2){
					UIUtil.setMessage(handler, UPDATE_ON_LINE_NO_DATA);
				}
				else{
					UIUtil.setMessage(handler, UPDATE_OFF_CONNECT);
				}
				UIUtil.setMessage(handler, UPDATA_NETWORK_DATA);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private int StringToInt(String daString) {
		if (daString == null || daString.equals("")) {
			return 0;
		}
		Pattern pattern = Pattern.compile("[0-9]*"); 
	   if(pattern.matcher(daString).matches()){
		   return Integer.valueOf(daString);
	   }  
		
		return 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			startActivity(new Intent(MentalStressActivity.this,
					MainActivity.class));
			MentalStressActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();		
		intentFilter.addAction(DataStorageService.ACTION_ALERT_SD_STATUS);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
//		intentFilter
//				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
//		intentFilter.addAction(DataStorageService.ACTION_ECG_DATA_AVAILABLE);
		intentFilter.addAction(BleConnectionService.ACTION_RESPONSE_BLE_STATE);
		intentFilter.addAction(DataStorageService.ACTION_NOTIFY_INVALID_ECG);
		
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
			else if (DataStorageService.ACTION_NOTIFY_INVALID_ECG.equals(action)) {
				UIUtil.setMessage(handler, NOTIFY_INVALID_ECG);
			}
		}
	};
	
	private void setBleState(int state) {
		mBleState = state;
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

private SaveCallBack saveCallBack=new SaveCallBack() {
		
		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			startActivity(new Intent(MentalStressActivity.this,MainActivity.class));
			uploaDialog.cancel();
			MentalStressActivity.this.finish();
		}
	};
	private void showUploadDialog(){
		uploaDialog = new SaveDialog(MentalStressActivity.this, saveCallBack);
			// 设置进度条风格，风格为圆形，旋转的
		uploaDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		uploaDialog.setMessage("正在加载,请稍候...");
		uploaDialog.setIndeterminate(false);
		uploaDialog.setCancelable(false);
		uploaDialog.show();
	}
	private void dialogCancel(){
		if(uploaDialog!=null){
			uploaDialog.cancel();
			uploaDialog.dismiss();
		}
	}
}
