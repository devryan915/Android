package com.langlang.activity;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.langlang.elderly_langlang.R;
import com.langlang.global.GlobalStatus;
import com.langlang.service.BluetoothLeService;
import com.langlang.service.DataStorageService;
import com.langlang.utils.UIUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class DeviceActivity extends BaseActivity {
	private final int UPDATA_VOLTAGE = 0;
	private final int UPDATA_POSTURE = 1;
	private final int UPDATE_TEMPERATURE = 2;
	
	private final int RSSI=1;
	private float voltage;
	private Timer timer = new Timer();
	
	private TextView rssi_tw;
	private TextView voltage_tw;
	private TextView posture_tw;
	private TextView gait_tw;
	private TextView temperature_tw;
	
	private int mPosture;
	private int mGait;
	
	private float mTemperature = 0.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		getViewId();
		setTimer();
	}

	private void getViewId() {
		rssi_tw = (TextView) this.findViewById(R.id.devvice_rssi_tw);
		voltage_tw = (TextView) this.findViewById(R.id.device_voltage_tw);
		posture_tw = (TextView) this.findViewById(R.id.devvice_posture_tw);
		gait_tw = (TextView) this.findViewById(R.id.devvice_gait_tw);
		temperature_tw=(TextView)this.findViewById(R.id.devvice_temperature_tw);
	}

	private void setTimer() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				voltage = GlobalStatus.getInstance().getVoltage();
				UIUtil.setMessage(handler, UPDATA_VOLTAGE);
				
//				mPosture = GlobalStatus.getInstance().getPosture();
//				mGait = GlobalStatus.getInstance().getGait();
//				UIUtil.setMessage(handler, UPDATA_POSTURE);
				
//				mTemperature = GlobalStatus.getInstance().getTemp();
//				UIUtil.setMessage(handler, UPDATE_TEMPERATURE);
			}
		}, 0, 1000 * 1);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPDATA_VOLTAGE) {
				voltage_tw.setText("" + voltage);
			} 
			
			if(msg.what==RSSI)
			{
				if (msg.obj != null) {
					int getRssi = Integer.valueOf(msg.obj.toString());
					rssi_tw.setText("" + getRssi);
				}
			}
			
			if (msg.what == UPDATA_POSTURE) {
				posture_tw.setText(Integer.toString(mPosture));
				gait_tw.setText(Integer.toString(mGait));
			}
			
//			if (msg.what == UPDATE_TEMPERATURE) {
//				DecimalFormat formator = new DecimalFormat("#.#");
//				float temperature = mTemperature;
//				temperature_tw.setText(formator.format(temperature));
//			}
		};
	};

	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			
			 if (BluetoothLeService.ACTION_GATT_RSSI.equals(action)) {
				Message message = Message.obtain();
				message.what = RSSI;
				message.obj = intent.getIntExtra("rssi", 0);
				handler.sendMessage(message);
			}
			else if (DataStorageService.ACTION_POSTURE_DATA_AVAILABLE
						.equals(action)) {
					int[] postureData = intent
							.getIntArrayExtra(DataStorageService.POSTURE_DATA);
					if (postureData != null) {
						mPosture = postureData[0];
						mGait = postureData[1];
						
						UIUtil.setMessage(handler, UPDATA_POSTURE);
					}
			}
		}
	};
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		intentFilter.addAction(DataStorageService.ACTION_POSTURE_DATA_AVAILABLE);
		return intentFilter;
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(DeviceActivity.this, ConfiguratorActivity.class));
			DeviceActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
