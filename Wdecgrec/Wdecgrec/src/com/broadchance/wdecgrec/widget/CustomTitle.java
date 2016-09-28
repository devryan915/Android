/**
 * 
 */
package com.broadchance.wdecgrec.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Messenger;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.broadchance.manager.FrameDataMachine;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertType;
import com.broadchance.wdecgrec.services.BleDataParserService;
import com.broadchance.wdecgrec.services.BleDomainService;
import com.broadchance.wdecgrec.services.BluetoothLeService;
import com.broadchance.wdecgrec.services.GuardService;

/**
 * @author ryan.wang
 * 
 */
public class CustomTitle extends RelativeLayout {
	Context context;
	private ImageView imageViewSignal;
	private TextView textViewPower;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == BleDomainService.MSG_GET_POWER) {
				setPower();
			}
		}
	};
	private Messenger mMesg = new Messenger(handler);

	private void setPower() {
		// Float power = msg.getData().getFloat("power");
		Float power = FrameDataMachine.getInstance().getPower();
		if (power != null) {
			// UIUtil.showToast(context, "蓝牙电量 power:" + power);
			if (power > AlertMachine.getInstance()
					.getAlertConfig(AlertType.A00005).getFloatValueRaise()) {
				textViewPower.setText("高");
			} else if (power > AlertMachine.getInstance()
					.getAlertConfig(AlertType.A00005).getFloatValueRaise()
					&& power <= AlertMachine.getInstance()
							.getAlertConfig(AlertType.A00005)
							.getFloatValueClear()) {
				textViewPower.setText("中");
			} else {
				textViewPower.setText("低");
			}
		} else {
			textViewPower.setText("-");
		}
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(GuardService.ACTION_GATT_RSSICHANGED)) {
				// int rssi = intent.getIntExtra(BluetoothLeService.EXTRA_DATA,
				// 0);
				int rssi = BluetoothLeService.rssiValue;
				// if (ConstantConfig.Debug) {
				// UIUtil.showToast(context, "蓝牙信号量:" + rssi);
				// }
				if (rssi > BleDataParserService.SIGNAL_MAX) {
					imageViewSignal
							.setImageResource(R.drawable.common_blesingnal_3);
				} else if (rssi > BleDataParserService.SIGNAL_MIN
						&& rssi <= BleDataParserService.SIGNAL_MAX) {
					imageViewSignal
							.setImageResource(R.drawable.common_blesingnal_2);
				} else {
					imageViewSignal
							.setImageResource(R.drawable.common_blesingnal_1);
				}
			} else if (action.equals(GuardService.ACTION_GATT_POWERCHANGED)) {
				// float power = intent.getFloatExtra(
				// BluetoothLeService.EXTRA_DATA, 0);
				// if (GuardService.Instance != null) {
				// GuardService.Instance.getPower(mMesg);
				// }
				setPower();
			} else if (action
					.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
				textViewPower.setText("-");
				imageViewSignal
						.setImageResource(R.drawable.common_blesingnal_0);
			}
		}
	};

	public CustomTitle(Context context) {
		super(context);
		init(context, null);
	}

	public CustomTitle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public CustomTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		this.context = context;
		View view = LayoutInflater.from(context).inflate(
				R.layout.common_title_status, this);
		imageViewSignal = (ImageView) view.findViewById(R.id.imageViewSignal);
		textViewPower = (TextView) view.findViewById(R.id.textViewPower);
		Integer rssi = BluetoothLeService.rssiValue;
		if (rssi == null) {
			imageViewSignal.setImageResource(R.drawable.common_blesingnal_0);
		} else {
			if (rssi > BleDataParserService.SIGNAL_MAX) {
				imageViewSignal
						.setImageResource(R.drawable.common_blesingnal_3);
			} else if (rssi > BleDataParserService.SIGNAL_MIN
					&& rssi <= BleDataParserService.SIGNAL_MAX) {
				imageViewSignal
						.setImageResource(R.drawable.common_blesingnal_2);
			} else if (rssi <= BleDataParserService.SIGNAL_MIN) {
				imageViewSignal
						.setImageResource(R.drawable.common_blesingnal_1);
			}
		}
		// if (GuardService.Instance != null) {
		// GuardService.Instance.getPower(mMesg);
		// }
		setPower();
		// Float power = FrameDataMachine.getInstance().getPower();
		// if (power == null) {
		// textViewPower.setText("-");
		// } else {
		// if (power > AlertMachine.getInstance()
		// .getAlertConfig(AlertType.A00005).getFloatValueClear()) {
		// textViewPower.setText("高");
		// } else if (power > AlertMachine.getInstance()
		// .getAlertConfig(AlertType.A00005).getFloatValueRaise()
		// && power <= AlertMachine.getInstance()
		// .getAlertConfig(AlertType.A00005)
		// .getFloatValueClear()) {
		// textViewPower.setText("中");
		// } else {
		// textViewPower.setText("低");
		// }
		// }

	}

	private IntentFilter makeIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GuardService.ACTION_GATT_RSSICHANGED);
		intentFilter.addAction(GuardService.ACTION_GATT_POWERCHANGED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		return intentFilter;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		context.registerReceiver(receiver, makeIntentFilter());
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		context.unregisterReceiver(receiver);
	}

}
