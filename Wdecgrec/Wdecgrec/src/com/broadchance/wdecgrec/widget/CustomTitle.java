/**
 * 
 */
package com.broadchance.wdecgrec.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.PlayerManager;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.services.BleDataParserService;
import com.broadchance.wdecgrec.services.BleDomainService;
import com.broadchance.wdecgrec.services.BluetoothLeService;

/**
 * @author ryan.wang
 * 
 */
public class CustomTitle extends RelativeLayout {
	Context context;
	private ImageView imageViewSignal;
	private ImageView imageViewPower;
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BluetoothLeService.ACTION_GATT_RSSICHANGED)) {
				if (BleDataParserService.rssiValue > BleDataParserService.SIGNAL_MAX) {
					imageViewSignal
							.setImageResource(R.drawable.common_blesingnal_3);
				} else if (BleDataParserService.rssiValue > BleDataParserService.SIGNAL_MIN
						&& BleDataParserService.rssiValue <= BleDataParserService.SIGNAL_MAX) {
					imageViewSignal
							.setImageResource(R.drawable.common_blesingnal_2);
				} else {
					imageViewSignal
							.setImageResource(R.drawable.common_blesingnal_1);
				}
			} else if (action
					.equals(BluetoothLeService.ACTION_GATT_POWERCHANGED)) {
				float power = intent.getFloatExtra(
						BluetoothLeService.EXTRA_DATA, 0);
				UIUtil.showToast(context, "蓝牙电量 power:" + power);
				if (power > BleDataParserService.POWER_MAX) {
					imageViewPower
							.setImageResource(R.drawable.common_blepower_3);
				} else if (power > BleDataParserService.POWER_MIN
						&& power <= BleDataParserService.POWER_MAX) {
					imageViewPower
							.setImageResource(R.drawable.common_blepower_2);
				} else {
					imageViewPower
							.setImageResource(R.drawable.common_blepower_1);
				}
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
		imageViewPower = (ImageView) view.findViewById(R.id.imageViewPower);
		if (BleDataParserService.rssiValue > BleDataParserService.SIGNAL_MAX) {
			imageViewSignal.setImageResource(R.drawable.common_blesingnal_3);
		} else if (BleDataParserService.rssiValue > BleDataParserService.SIGNAL_MIN
				&& BleDataParserService.rssiValue <= BleDataParserService.SIGNAL_MAX) {
			imageViewSignal.setImageResource(R.drawable.common_blesingnal_2);
		} else if (BleDataParserService.rssiValue <= BleDataParserService.SIGNAL_MIN) {
			imageViewSignal.setImageResource(R.drawable.common_blesingnal_1);
		}
		float power = FrameDataMachine.getInstance().getPower();
		if (power > BleDataParserService.POWER_MAX) {
			imageViewPower.setImageResource(R.drawable.common_blepower_3);
		} else if (power > BleDataParserService.POWER_MIN
				&& power <= BleDataParserService.POWER_MAX) {
			imageViewPower.setImageResource(R.drawable.common_blepower_2);
		} else {
			imageViewPower.setImageResource(R.drawable.common_blepower_1);
		}
	}

	private IntentFilter makeIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSICHANGED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_POWERCHANGED);
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
