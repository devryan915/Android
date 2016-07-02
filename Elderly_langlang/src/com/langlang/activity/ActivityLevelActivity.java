package com.langlang.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.langlang.adapter.PoseAdapter;
import com.langlang.ble.BleConnectionNotifiaction;
import com.langlang.data.Knowledge;
import com.langlang.dialog.PraiseDialog;
import com.langlang.dialog.SaveDialog;
import com.langlang.dialog.SaveDialog.SaveCallBack;
import com.langlang.elderly_langlang.R;
import com.langlang.global.GlobalStatus;
import com.langlang.global.Client;
import com.langlang.global.UserInfo;
import com.langlang.service.BleConnectionService;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
import com.langlang.utils.HttpTools;
import com.langlang.utils.Program;
import com.langlang.utils.SDChecker;
import com.langlang.utils.ScreenShotUtils;
import com.langlang.utils.UIUtil;
import com.tencent.plus.m;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityLevelActivity extends BaseActivity {

	private final int KNOWLEDGE = 0;
	private final static int UPDATE_ON_CONNECT = 112;
	private final static int UPDATE_OFF_CONNECT = 111;
	private final static int UPDATE_NOT_MESSAGE = 113;
	private final static int UPDATE_DATE_WRANNING = 114;
	private final static int UPDATE_OFF_LINE = 1113;
	private final int UPDATE_ON_LINE_NO_DATA=1115;
	private final static int UPDATE_STEP = 1;
	private final static int UPDATE_CALORIES = 2;
	private final static int UPDATE_POSTURE_AND_GAIT = 3;
	private final static int UPDATE_POSTURE_TUMBLE = 4;
	private final static int UPDATE_STAND_STILL = 5;
	private final static int SEND_PRAISE_ALA = 6;
	private final static int UPDATE_EXAMINE_ALA = 7;
	private final int SEND_ALARM_WRAING = 8;
	private final int UPDATE_NULL = 9;
	
	private final static int ALERT_SD_STATUS = 30;
	
	private final static int UPDATA_BLE_STATE=40;
	
	private final int SHARE_IMAGE = 50; 
	private final int SHOW_CAPTURE_SUCCESS = 51;

	private final static int POSTURE_STAND = 0;
	private final static int POSTURE_SIT = 1;
	private final static int POSTURE_WALK = 2;
	private final static int POSTURE_RUN = 3;
	private final static int POSTURE_LAY= 23;
	private final static int POSTURE_TUMBLE = 10000;

	private final static String TUMBLE_FLAG = "TUMBLE_FLAG";
	private final static int TUMBLE_FLAG_ON = 1;
	private final static int TUMBLE_FLAG_OFF = 0;
	private final static String TUMBLE_TIME = "TUMBLE_TIME";
	
	private final int UPDATA_NETWORK_DATA = 10;

//	private TextView baffle_1;
//	private TextView baffle_2;
//	private TextView baffle_3;
//	private TextView baffle_4;
//	private TextView baffle_5;
//	private TextView baffle_6;

	private RelativeLayout bg_layout;
	private RelativeLayout left_layout;
	private RelativeLayout right_layout;
	private RelativeLayout noticave_layout;
	private TextView tree_one;
	private TextView tree_two;
	private TextView tree_three;
	private TextView kll_image;
	private TextView step_image;
	private TextView spot_image;
	private TextView center_image;
	private TextView pose_tw;
	
	
	private TextView hint_tw;
//	private Button name_textView;
	private TextView share_textView;
	private RelativeLayout sport_textView;
//	private ListView listView;
//	private PoseAdapter adapter;
	private TextView useriamge_tw;
	private TextView usertext_tw;
	private TextView txtStepCount;
	private TextView txtCalories;
	private int mStepCount = 0;
	private int mCalories = 0;
	private String mStatus;

	private int mPostureData = POSTURE_STAND;

	private SharedPreferences sp;
	private SharedPreferences app_skin;
	private String result;
	private String compare;
	private String knowledge;
	private String mNetwork_compare;
	private String mCalorie_string ;
	private TextView knowledge_tw;
	private TextView suggest_tw;
	private TextView evaluate_tw;

	private Timer timer = new Timer();
	private Timer timer1m;

	private SharedPreferences spTumbleFlag;

	private ImageView pose_image;
	private AnimationDrawable animationDrawable;
	private String path_image=Program.getSDLangLangImagePath() + "/pose_image.png";
	private PraiseDialog praiseDialog;

	private TextView txtStandStill;
	private long mStandStillTime = 0;
	private String nameString = UserInfo.getIntance().getUserData()
			.getUser_name();// 监护对象的用户名
	private String my_name = UserInfo.getIntance().getUserData().getMy_name();// 自己的用户名
	
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
		setContentView(R.layout.acticity_activity_level);
		app_skin=getSharedPreferences("app_skin",MODE_PRIVATE);

		sp = this.getSharedPreferences("actknowledge", MODE_PRIVATE);
		spTumbleFlag = this.getSharedPreferences(TUMBLE_FLAG, MODE_PRIVATE);

		getviewID();
		changeSkin();
		getOnclick();
		mapping();
		new knowledgeThread().start();

		pose_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getTumbleFlagForSP() == TUMBLE_FLAG_ON) {
					showDialog();
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (UserInfo.getIntance().getUserData().getUserRole().equals("guardian")) {
			showUploadDialog();
			setNetworkTime();
			hint_tw.setText("提醒Ta动一动");
		} else {
			hint_tw.setText("谁点了我？");
			setTimerTask();
			init();
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			
			sdChecker.checkAndAlert();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
			cancelNetworkTimer();
		} else {
			unregisterReceiver(mGattUpdateReceiver);
			cancelTimer();
		}
	}

	private void init() {
		initData();
		initUI();
		
		if (GlobalStatus.getInstance().getBleState() == BleConnectionService.STATE_CONNECTED) {
			// We don't need to show the ble status if it has been connected.
		}
		else {
			setBleState(GlobalStatus.getInstance().getBleState());
		}
	}

	private void initData() {
		mStepCount = GlobalStatus.getInstance().getCurrentStep();
		mCalories = GlobalStatus.getInstance().getCalories();

		if (getTumbleFlagForSP() == TUMBLE_FLAG_ON) {
			mPostureData = POSTURE_TUMBLE;
			showPrompt();
		} else {
			int posture = GlobalStatus.getInstance().getPosture();
			int gait = GlobalStatus.getInstance().getGait();

			updatePostureData(posture, gait);
		}

		mStandStillTime = (GlobalStatus.getInstance().getStandStillTime() / 1000) / 60;
	}

	private void initUI() {
		updateStepCountUI();
		updateCaloriesUI();
		updatePostureAndGaitUI();
		updateStandStillUI();
	}

	/**
	 * 获取控件Id
	 */
	private void getviewID() {
		hint_tw = (TextView) this.findViewById(R.id.act_hint_tw);
		pose_image = (ImageView) this.findViewById(R.id.act_pose_textveiws);
//		pose_image.setBackgroundResource(R.anim.anim_run);
//		animationDrawable = (AnimationDrawable) pose_image.getBackground();
//		animationDrawable.start();

//		baffle_1 = (TextView) this.findViewById(R.id.act_baffle_one);
//		baffle_2 = (TextView) this.findViewById(R.id.act_baffle_two);
//		baffle_3 = (TextView) this.findViewById(R.id.act_baffle_three);
//		baffle_4 = (TextView) this.findViewById(R.id.act_baffle_four);
//		baffle_5 = (TextView) this.findViewById(R.id.act_baffle_five);
//		baffle_6 = (TextView) this.findViewById(R.id.act_baffle_six);
		share_textView = (TextView) this.findViewById(R.id.act_share_textview);
		sport_textView = (RelativeLayout) this
				.findViewById(R.id.act_act_textviews);
//		listView = (ListView) this.findViewById(R.id.act_listview);
//		adapter = new PoseAdapter(ActivityLevelActivity.this);
//		listView.setAdapter(adapter);

		txtStepCount = (TextView) this
				.findViewById(R.id.act_stepcount_textivew);
		txtCalories = (TextView) this.findViewById(R.id.act_kllcount_textivew);

		txtStandStill = (TextView) this.findViewById(R.id.act_static_tw);
		
		
		bg_layout=(RelativeLayout) this.findViewById(R.id.act_layout);
		
		left_layout=(RelativeLayout) this.findViewById(R.id.act_kll_textview);
		right_layout=(RelativeLayout) this.findViewById(R.id.act_step_textview);
		noticave_layout=(RelativeLayout) this.findViewById(R.id.act_notive_layout);
		tree_one=(TextView) this.findViewById(R.id.act_tree_one);
		
		tree_two=(TextView) this.findViewById(R.id.act_tree_two);
		tree_three=(TextView) this.findViewById(R.id.act_tree_three);
		kll_image=(TextView) this.findViewById(R.id.act_kllcount_image);
		step_image=(TextView) this.findViewById(R.id.act_stepcount_image);
		spot_image=(TextView) this.findViewById(R.id.act_hint_image);
		center_image=(TextView) this.findViewById(R.id.act_logo_textview);
		pose_tw=(TextView) this.findViewById(R.id.act_set_textview);
		
		
		useriamge_tw=(TextView) this.findViewById(R.id.act_userimage_tw);
		usertext_tw=(TextView) this.findViewById(R.id.act_userimage_text);
		
		suggest_tw=(TextView)this.findViewById(R.id.act_suggest_textview);
		evaluate_tw=(TextView)this.findViewById(R.id.act_evaluate_textview);
		knowledge_tw=(TextView)this.findViewById(R.id.act_knowledge_textview);
	}

	/**
	 * 换肤
	 */
	private void changeSkin(){
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
			bg_layout.setBackgroundResource(R.drawable.bg_item);
			tree_one.setVisibility(View.GONE);
			tree_two.setVisibility(View.GONE);
			tree_three.setVisibility(View.GONE);
			kll_image.setBackgroundResource(R.drawable.zitai_07_item);
			step_image.setBackgroundResource(R.drawable.zitai_09_item);
			spot_image.setBackgroundResource(R.drawable.moveclick_item);
			center_image.setBackgroundResource(R.drawable.zitailogo_16_item);
			share_textView.setBackgroundResource(R.drawable.share_click_item);
			pose_tw.setTextColor(getResources().getColor(R.color.white));
			sport_textView.setBackgroundResource(R.drawable.posesport_click_item);
			left_layout.setBackgroundResource(R.drawable.zitaimian_10_item);
			right_layout.setBackgroundResource(R.drawable.zitaimian_11_item);
			noticave_layout.setBackgroundResource(R.drawable.item_notice_item);
			useriamge_tw.setBackgroundResource(R.drawable.user_jin_03_item);
			usertext_tw.setTextColor(getResources().getColor(R.color.white));
			
		}
	}
	/**
	 * 获取控件的点击事件
	 */
	private void getOnclick() {
//		baffle_1.setOnClickListener(listener);
//		baffle_2.setOnClickListener(listener);
//		baffle_3.setOnClickListener(listener);
//		baffle_4.setOnClickListener(listener);
//		baffle_5.setOnClickListener(listener);
//		baffle_6.setOnClickListener(listener);

		sport_textView.setOnClickListener(listener);
		share_textView.setOnClickListener(listener);

	}

	/**
	 * 设置控件的点击事件
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.act_act_textviews:
				if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
					UIUtil.setToast(
									ActivityLevelActivity.this,
									UserInfo.PROMPT_NOT_LOGIN
									);
					return;
				}
				if (!nameString.equals(my_name)) {
					new sendThread().start();
				} else {
					new examineThread().start();
				}

				break;
			case R.id.act_share_textview:
//				boolean result = ScreenShotUtils.shotBitmap(ActivityLevelActivity.this,path_image);
//				if(result)
//				{
//					Toast.makeText(ActivityLevelActivity.this, "截图成功.", Toast.LENGTH_SHORT).show();
//					shareImage(path_image);
//				}else {
//					Toast.makeText(ActivityLevelActivity.this, "截图失败.", Toast.LENGTH_SHORT).show();
//				}
				setShareClickable(false);
				mScreenBitmap = ScreenShotUtils.takeScreenShot(ActivityLevelActivity.this);
				if (mScreenBitmap == null) {
					setShareClickable(true);
					UIUtil.setMessage(handler, SHARE_IMAGE, false);
					return;
				}
				UIUtil.setMessage(handler, SHOW_CAPTURE_SUCCESS);
				new ScreenShotThread().start();
			default:
				break;
			}
		}
	};

	/**
	 * 映射数据
	 */
	private void mapping() {
		usertext_tw.setText(UserInfo.getIntance().getUserData().getName());
	}

	class knowledgeThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			SharedPreferences.Editor editor = sp.edit();

			String param = "[{type:\"2\"}]";
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
			if (msg.what == UPDATE_STEP) {
				txtStepCount.setText(Integer.toString(mStepCount));
			}
			else if (msg.what == UPDATE_CALORIES) {
				DecimalFormat formator = new DecimalFormat("#.#");
				float calories = mCalories / 1000.0f;
				txtCalories.setText(formator.format(calories));
			}
			else if (msg.what == UPDATE_POSTURE_AND_GAIT) {
				updatePostureUI();
			}
			else if (msg.what == KNOWLEDGE) {
				Knowledge knowledgeees = (Knowledge) msg.obj;
//				adapter.clear();
//				adapter.addListItem(knowledgeees.result);
//				adapter.addListItem(knowledgeees.compare);
//				adapter.addListItem(knowledgeees.knowledge);
//				adapter.notifyDataSetChanged();
				evaluate_tw.setText(knowledgeees.compare);
				suggest_tw.setText(knowledgeees.result);
				knowledge_tw.setText(knowledgeees.knowledge);
			}
			else if (msg.what == UPDATE_POSTURE_TUMBLE) {
				updatePostureUI();
			}
			else if (msg.what == UPDATE_STAND_STILL) {
				txtStandStill.setText(mStandStillTime + " 分钟");
			}
			else if (msg.what == UPDATA_NETWORK_DATA) {
				dialogCancel();
				txtStepCount.setText(Integer.toString(mStepCount));
				txtCalories.setText(mCalorie_string);
				txtStandStill.setText(mStandStillTime + " 分钟");
				updatePostureDataservice(mPostureData);
				updatePostureUI();
			}
			else if (msg.what == SEND_PRAISE_ALA) {
				String send_result = msg.obj.toString();
				if (send_result.equals("1")) {
					UIUtil.setToast(ActivityLevelActivity.this, "发送成功");
				} else {
					UIUtil.setToast(ActivityLevelActivity.this, "发送失败");
				}
			}
			else if (msg.what == UPDATE_EXAMINE_ALA) {

				showInteract(activity_List);

			}
			else if (msg.what == SEND_ALARM_WRAING) {
				UIUtil.setToast(ActivityLevelActivity.this, "网络异常");
			}
			else if (msg.what == UPDATE_NULL) {
				UIUtil.setToast(ActivityLevelActivity.this, "列表为空");
			}
			else if (msg.what == ALERT_SD_STATUS) {
				sdAlert.checkAndAlert();
			}
			else if (msg.what == UPDATA_BLE_STATE) {
				mBleConnectionNotifiaction.show(mBleState);
			}
			else if (msg.what == SHOW_CAPTURE_SUCCESS) {
				UIUtil.setToast(ActivityLevelActivity.this, "截图成功");
			}
			else if (msg.what == UPDATE_ON_CONNECT) {
				dialogCancel();
//				UIUtil.setToast(ActivityLevelActivity.this, "设备已连接");
			}
			else if (msg.what == UPDATE_OFF_LINE) {
				dialogCancel();
				UIUtil.setToast(ActivityLevelActivity.this,  UserInfo.getIntance().getUserData().getName()+"不在线");
			}
			else if (msg.what == UPDATE_ON_LINE_NO_DATA) {
				dialogCancel();
				UIUtil.setToast(ActivityLevelActivity.this, "没有最新数据");
			}
			else if (msg.what == UPDATE_OFF_CONNECT) {
				dialogCancel();
				UIUtil.setToast(ActivityLevelActivity.this, UserInfo.getIntance().getUserData().getName()+"的设备未连接");
			}
			else if (msg.what == SHARE_IMAGE) {
				boolean success = (Boolean) msg.obj;
				if (success) {
					shareImage(path_image);
				}
				else {
					UIUtil.setToast(ActivityLevelActivity.this, "无法截图");
				}				
				setShareClickable(true);
			}
			else if (msg.what == UPDATE_NOT_MESSAGE) {
				UIUtil.setToast(ActivityLevelActivity.this, "服务器访问失败");
				dialogCancel();
			}
			else if(msg.what==UPDATE_DATE_WRANNING){
				UIUtil.setToast(ActivityLevelActivity.this, "获取姿态数据时，网络异常");
				dialogCancel();
			}
		}
	};

	private void showInteract(ArrayList<String> arrayList) {
		praiseDialog = new PraiseDialog(ActivityLevelActivity.this, arrayList);
		praiseDialog.show();
		praiseDialog.setCancelable(false);
	}

	private void updateKnowledge(String result, String compare, String knowledge) {
		UIUtil.setMessage(handler, KNOWLEDGE, new Knowledge(result, compare,
				knowledge));
	}

	/**
	 * 当天结果
	 * 
	 * @return
	 */
	private String getResult() {
		if (UserInfo.getIntance().getUserData().getUserRole()
				.equals("guardian")) {
			result = "累计" + mStepCount + "步，消耗" + mCalorie_string + "kcal,适当的运动有助于身体健康";
		} else {
			DecimalFormat formator = new DecimalFormat("#.#");
			float calories = mCalories / 1000.0f;
			String caloriesStr = formator.format(calories);
			
			if (mStepCount < 3001) {
				result = "累计" + mStepCount + "步，消耗" + caloriesStr
						+ "千卡,多多动一动哦！";
			} else if (mStepCount > 3000 && mStepCount < 5001) {
				result = "累计" + mStepCount + "步，消耗" + caloriesStr + "千卡,继续加油！";
			} else if (mStepCount > 5000 && mStepCount < 10001) {
				result = "累计" + mStepCount + "步，消耗" + caloriesStr + "千卡，真棒！";
			} else if (mStepCount > 10000) {
				result = "累计 ＞10000步，消耗" + caloriesStr + "千卡,注意休息！";
			}
		}
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
			if (mStepCount < 3001) {
				compare = "请继续加油哦！";
			} else if (mStepCount > 3000 && mStepCount < 8001) {
				compare = "离目标不远啦，真棒！";
			} else {
				compare = "完成目标，注意适当休息！（健康人目标为8000步/日）";
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

	private void setTimerTask() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				getResult();
				getcompare();
				getKnowledge();
				updateKnowledge(result, compare, knowledge);
			}
		}, 500, 1000 * 10);

	}

	private void setNetworkTime() {
		timer1m = new Timer();
		timer1m.schedule(new TimerTask() {
			@Override
			public void run() {
				new updataDataThread().start();

			}
		}, 500, 1000 * 60);
	}

	private void cancelNetworkTimer() {
		timer1m.cancel();
	}

	private void cancelTimer() {
		timer.cancel();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(ActivityLevelActivity.this,
					MainActivity.class));
			ActivityLevelActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);

	}

	private void updateStepCountUI() {
		UIUtil.setMessage(handler, UPDATE_STEP);
	}

	private void updateCaloriesUI() {
		UIUtil.setMessage(handler, UPDATE_CALORIES);
	}

	private void updatePostureAndGaitUI() {
		UIUtil.setMessage(handler, UPDATE_POSTURE_AND_GAIT);
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
		intentFilter
				.addAction(DataStorageService.ACTION_STAND_STILL_DATA_AVAILABLE);
		intentFilter.addAction(DataStorageService.ACTION_ALERT_SD_STATUS);
		intentFilter.addAction(BleConnectionService.ACTION_RESPONSE_BLE_STATE);
		return intentFilter;
	}

	private void updateTumbleUI() {
		UIUtil.setMessage(handler, UPDATE_POSTURE_TUMBLE);
	}

	private void updatePostureUI() {
		System.out.println("皮肤："+app_skin.getString("skin","defaul"));
		if("skin_one".equals(app_skin.getString("skin","defaul"))){
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
				pose_image.setBackgroundResource(R.anim.anim_walk_item);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_RUN) {
				pose_image.setBackgroundResource(R.anim.anim_run_item);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_TUMBLE) {
				pose_image.setBackgroundResource(R.anim.anim_tumble_item);
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
				pose_image.setBackgroundResource(R.anim.anim_walk);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_RUN) {
				pose_image.setBackgroundResource(R.anim.anim_run);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
			else if (mPostureData == POSTURE_TUMBLE) {
				pose_image.setBackgroundResource(R.anim.anim_tumble);
				animationDrawable = (AnimationDrawable) pose_image.getBackground();
				animationDrawable.start();
			}
		}
		
	}

	private void updateStandStillUI() {
		UIUtil.setMessage(handler, UPDATE_STAND_STILL);
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
	/**
	 * 从服务器获取的值判断姿态
	 * @param posture
	 */
	private void updatePostureDataservice(int posture) {
		if (posture == 0) {
			
			mPostureData = POSTURE_STAND;
		
		} else if (posture == 2 || posture == 3) {

			mPostureData = POSTURE_WALK;
			
		} else if (posture == 4 || posture == 5) {

			mPostureData = POSTURE_RUN;
			
		} else if (posture == 20) {
			
			mPostureData = POSTURE_STAND;
		
		} else if (posture == 21) {

			mPostureData = POSTURE_SIT;
			
		}else if (posture >= 23&&posture <=27) {
			
			mPostureData = POSTURE_LAY;
		
		}
		else {
		
			mPostureData = POSTURE_STAND;
		
		}

		
	}
	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("确认");
		builder.setMessage("是否清除跌倒标识?")
				.setCancelable(false)
				.setPositiveButton("清除标识",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								clearTumbleFlag();
								mPostureData = POSTURE_STAND;
								updateTumbleUI();
							}
						})
				.setNegativeButton("不清除标识",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();
	}

	private void showPrompt() {
		if (getTumbleFlagForSP() == TUMBLE_FLAG_ON) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("姿态已经被设置为[跌倒],是否现在清除该标识?(您也可以在界面上点击[跌倒]图标清除该标识)")
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
					.setNegativeButton("我知道了,以后再清除",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
		}
	}

	private void clearTumbleFlag() {
		SharedPreferences.Editor editor = spTumbleFlag.edit();
		editor.putInt(TUMBLE_FLAG, TUMBLE_FLAG_OFF);
		editor.putLong(TUMBLE_TIME, 0);
		editor.commit();
	}

	private void setTumbleFlag() {
		SharedPreferences.Editor editor = spTumbleFlag.edit();
		editor.putInt(TUMBLE_FLAG, TUMBLE_FLAG_ON);
		Date now = new Date();
		editor.putLong(TUMBLE_TIME, now.getTime());
		editor.commit();
	}

	private int getTumbleFlagForSP() {
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

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

				setBleState(BleConnectionService.STATE_CONNECTED);
				
				mStepCount = GlobalStatus.getInstance().getLastStepCount();
				updateStepCountUI();
				
				mCalories = GlobalStatus.getInstance().getLastCalories();
				updateCaloriesUI();
								
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				
				setBleState(BleConnectionService.STATE_DISCONNECTED);
				resetPosture();
				resetStepCountAndCalories();
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (DataStorageService.ACTION_UPDATE_STEP_AND_CALORIES
					.equals(action)) {
//				mStepCount = intent.getIntExtra(DataStorageService.STEP_COUNT,
//						0);
//				mCalories = intent.getIntExtra(DataStorageService.CALORIES, 0);
				mStepCount = GlobalStatus.getInstance().getCurrentStep();
				mCalories = GlobalStatus.getInstance().getCalories();
				if (mCalories == 0) {
					System.out.println("action ActivityLevelActivity mCalories is zero");
				}
				updateStepCountUI();
				updateCaloriesUI();
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
			} else if (DataStorageService.ACTION_STAND_STILL_DATA_AVAILABLE
					.equals(action)) {
				long standStillTime = intent.getLongExtra(
						DataStorageService.STAND_STILL_TIME, 0);
				mStandStillTime = (standStillTime / 1000) / 60;
				updateStandStillUI();
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
				String user_data = Client.getStepTips(userInfo);
				
				
				Date second_date = new Date();
				if ((second_date.getTime() - first_data.getTime()) < (1000 * 2)) {
					try {
						Thread.sleep((long) (1000 * 1.0));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				System.out.println("actact user_data:" + user_data);
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
				mPostureData = StringToInt(jsonObject.getString("pose"));
				mCalorie_string = jsonObject.getString("calories");
				mStepCount = StringToInt(jsonObject.getString("stepCount"));
				mStandStillTime = StringTOLong(jsonObject
						.getString("restTimes"));
				mNetwork_compare = jsonObject.getString("compareTips");
				mStatus=jsonObject.getString("status");
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
				updateKnowledge(result, compare, knowledge);

				System.out.println("my data mPostureData:" + mPostureData
						+ " mCalories:" + mCalorie_string + " mStepCount:"
						+ mStepCount + " mStandStillTime:" + mStandStillTime + " mStatus:" + mStatus);
				UIUtil.setMessage(handler, UPDATA_NETWORK_DATA);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 点赞时发送的线程
	 * 
	 * @author Administrator
	 * 
	 */
	class sendThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String userInfo = "[{username:\"" + my_name + "\",acceptObj:\""
					+ nameString + "\",type:\"4\"}]";
			String sleep_data = Client.sendRequest(userInfo);
			if (sleep_data == null) {
				return;
			}
			UIUtil.setMessage(handler, SEND_PRAISE_ALA, sleep_data);
		}
	}

	private ArrayList<String> activity_List = new ArrayList<String>();

	/**
	 * 点赞时查看的线程
	 * 
	 * @author Administrator
	 * 
	 */
	class examineThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name = UserInfo.getIntance().getUserData()
					.getUser_name();
			String userInfo = "[{userName:\"" + user_name + "\",type:\"4\"}]";
			String sleep_data = Client.getNewMessage(userInfo);
			if (sleep_data == null) {
				if (HttpTools.isNetworkAvailable(ActivityLevelActivity.this) == false) {
					UIUtil.setMessage(handler, SEND_ALARM_WRAING);
				} else {
					UIUtil.setMessage(handler, UPDATE_NULL);
				}
				return;
			}

			try {
				activity_List.clear();
				JSONArray jsonArray = new JSONArray(sleep_data);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String send_user = jsonObject.getString("name");
					String send_time = jsonObject.getString("mCreateDate");
					String send_type = jsonObject.getString("type");
					String type_result = null;
					String final_result;

					if (send_type.equals("4")) {

						type_result = "提醒我动一动";
						final_result = send_user + "在" + send_time
								+ type_result;
						activity_List.add(final_result);

					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UIUtil.setMessage(handler, UPDATE_EXAMINE_ALA);
		}
	}

	private int StringToInt(String daString) {
		if (daString == null || daString.equals("")) {
			return 0;
		}
		return Integer.valueOf(daString);
	}

	private long StringTOLong(String daString) {
		if (daString == null || daString.equals("")) {
			return 0;
		}
		return Long.parseLong(daString);
	}
	
	private void setBleState(int state) {
		mBleState = state;
		UIUtil.setMessage(handler, UPDATA_BLE_STATE);
	}
	
	private void setShareClickable(boolean clickable) {
		share_textView.setClickable(clickable);
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

	
	private void resetPosture() {
		mPostureData = POSTURE_STAND;
		updatePostureAndGaitUI();
	}
	
	private void resetStepCountAndCalories() {
		mStepCount = 0;
		updateStepCountUI();
		
		mCalories = 0;
		updateCaloriesUI();
	}
private SaveCallBack saveCallBack=new SaveCallBack() {
		
		@Override
		public void Cancel() {
			// TODO Auto-generated method stub
			startActivity(new Intent(ActivityLevelActivity.this,MainActivity.class));
			uploaDialog.cancel();
			ActivityLevelActivity.this.finish();
		}
	};
	private void showUploadDialog(){
		uploaDialog = new SaveDialog(ActivityLevelActivity.this, saveCallBack);
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
