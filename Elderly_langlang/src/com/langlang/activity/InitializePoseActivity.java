package com.langlang.activity;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import com.langlang.cutils.GlobalAccelCalculator;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.UserInfo;
import com.langlang.service.BleConnectionService;
import com.langlang.service.DataStorageService;
import com.langlang.utils.UIUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class InitializePoseActivity extends BaseActivity {
	private final int UPLOAD_XYZ=1; 
	private final int RESET_DATA=0;
	private final int IS_WARNING=2;

	private String mClient_data;
	
	private TextView initialize;
	private Queue<Integer>xQueue=new LinkedList<Integer>();
	private Queue<Integer>yQueue=new LinkedList<Integer>();
	private Queue<Integer>zQueue=new LinkedList<Integer>();
	
	private int x_sum=0;
	private int y_sum=0;
	private int z_sum=0;
//	private NewStepCounter newStepCounter;
	private SharedPreferences sharedPreferences;
	private String mName;
	
	private Object lockData = new Object();
	
//	private StepCounter stepCounter;
	@SuppressLint("HandlerLeak") 
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what==RESET_DATA){
				if(mClient_data.equals("1")){
					Toast.makeText(InitializePoseActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(InitializePoseActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
				}
			}
			if (msg.what == IS_WARNING) {
				UIUtil.setToast(InitializePoseActivity.this, "网络异常");
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialize_pose);
//		newStepCounter=new NewStepCounter();
//		stepCounter=StepCounter.getInstance();
		mappingData();
		initialize = (TextView) this.findViewById(R.id.initialize_sure_button);
		initialize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(InitializePoseActivity.this, "x_sum:"+x_sum/5+"\ny_sum:"+y_sum/5+"\nz_sum:"+z_sum/5, Toast.LENGTH_SHORT).show();
//				int xyz=stepCounter.setAccelInitXYZ(x_sum/5, y_sum/5, z_sum/5);
				int xyz = GlobalAccelCalculator.getInstance().setAccelInitXYZ(x_sum/5, y_sum/5, z_sum/5);
				if (UserInfo.getIntance().getUserData().getLogin_mode() != 1) {
					synchronized (lockData) {					
						if (x_sum == 0 && y_sum == 0 && z_sum == 0) {
							Toast.makeText(InitializePoseActivity.this, "读取初始化数据失败，\n请检查设备后再初始化姿态",
									Toast.LENGTH_LONG).show();
						}
						else
						{
							new uploadXYZ(x_sum, y_sum, z_sum).start();
						}
					}
				}
				System.out.println("action initialize xyz:" + xyz);
			}
		});
		
//		sendSetEcgPolicyIntent(BleConnectionService.POLICY_ECG_ONLY,
//									BleConnectionService.POLICY_SUB_UNKNOWN);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(broadcastReceiver, makeIntentFilter());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
//		sendSetEcgPolicyIntent(BleConnectionService.POLICY_MIXED,
//				   BleConnectionService.POLICY_SUB_POLICY_MIXED_0);	
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(android.content.Context context,
				android.content.Intent intent) {
			String action = intent.getAction();
			if (DataStorageService.ACTION_ACCELERATED_X.equals(action)) {
				synchronized (lockData) {
//				sendMessage(MY_ACCELERATED_X, intent.getIntExtra(
//						DataStorageService.ACTION_ACCELERATED_X_DATA, 0));
				xQueue.offer(intent.getIntExtra(
						DataStorageService.ACTION_ACCELERATED_X_DATA, 0));
				if(xQueue.size()>5){
					xQueue.poll();
				}
				if (xQueue.size() > 4) {
					x_sum=0;
					for (Integer i : xQueue) {
						x_sum = x_sum + i;
						System.out.println("my queue x:" + i);
					}
					System.out.println("my queue x_sum:" + x_sum);
				}
				}
			} else if (DataStorageService.ACTION_ACCELERATED_Y.equals(action)) {
				synchronized (lockData) {
//				sendMessage(MY_ACCELERATED_Y, intent.getIntExtra(
//						DataStorageService.ACTION_ACCELERATED_Y_DATA, 0));
				
				yQueue.offer(intent.getIntExtra(
						DataStorageService.ACTION_ACCELERATED_Y_DATA, 0));
				if(yQueue.size()>5){
					yQueue.poll();
				}
				if (yQueue.size() > 4) {
					y_sum=0;
					for (Integer i : yQueue) {
						y_sum = y_sum + i;
						System.out.println("my queue y:" + i);
					}
					
					System.out.println("my queue y_sum:" + y_sum);
				}
				}

			} else if (DataStorageService.ACTION_ACCELERATED_Z.equals(action)) {
				synchronized (lockData) {
//				sendMessage(MY_ACCELERATED_Z, intent.getIntExtra(
//						DataStorageService.ACTION_ACCELERATED_Z_DATA, 0));
				
				
				zQueue.offer(intent.getIntExtra(
						DataStorageService.ACTION_ACCELERATED_Z_DATA, 0));
				if(zQueue.size()>5){
					zQueue.poll();
				}
				if (zQueue.size() > 4) {
					z_sum=0;
					for (Integer i : zQueue) {
						z_sum = z_sum + i;
						System.out.println("my queue z:" + i);
					}
					System.out.println("my queue z_sum:" + z_sum);
				}
				}
			}
		};
	};

	private IntentFilter makeIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataStorageService.ACTION_ACCELERATED_X);
		intentFilter.addAction(DataStorageService.ACTION_ACCELERATED_Y);
		intentFilter.addAction(DataStorageService.ACTION_ACCELERATED_Z);
		return intentFilter;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(InitializePoseActivity.this,
					SetActivity.class));
			InitializePoseActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	class uploadXYZ extends Thread{
		private int uploadXSum;
		private int uploadYSum;
		private int uploadZsum;
		
		public uploadXYZ(int xSum, int ySum, int zSum) {
			uploadXSum = xSum;
			uploadYSum = ySum;
			uploadZsum = zSum;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_info="[{userName:\"" + mName + "\",accX:\"" + uploadXSum/5
					+ "\",accY:\"" + uploadYSum/5 + "\",accZ:\"" + uploadZsum/5 + "\"}]";
//			sendMessage(UPLOAD_XYZ, Client.uploadXYZ(user_info));
			
			mClient_data=Client.uploadXYZ(user_info);
			if(mClient_data==null||"".equals(mClient_data)){
				UIUtil.setMessage(handler, IS_WARNING);
			}
			else{
				UIUtil.setMessage(handler, RESET_DATA);
			}
			
		}
	}
	private void mappingData(){
		mName=UserInfo.getIntance().getUserData().getMy_name();
	}
	
	private void sendSetEcgPolicyIntent(int policy, int subPolicy) {
		final Intent intent = new Intent(BleConnectionService.ACTION_SET_ECG_MODE);
		intent.putExtra("POLICY", policy);
		intent.putExtra("SUB_POLICY", subPolicy);
		intent.putExtra("FROM", BleConnectionService.POLICY_CTRL_FROM_CLIENT);
		sendBroadcast(intent);
	}
	
}
