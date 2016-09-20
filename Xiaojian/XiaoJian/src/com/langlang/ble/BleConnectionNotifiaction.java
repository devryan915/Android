package com.langlang.ble;

import android.content.Context;

import com.broadchance.xiaojian.service.BleConnectionService;
import com.langlang.utils.UIUtil;

public class BleConnectionNotifiaction {
	private Context context;
	
	private static final String NT_CONNECTED = "设备已连接";
	private static final String NT_CONNECTTING = "正在尝试连接设备...";
	private static final String NT_DISCONNECTED = "设备未连接";
	private static final String NT_SCANNING = "正在搜索设备";
	private static final String NT_UNKNOWN = "未知设备状态";	
	
	public BleConnectionNotifiaction(Context context) {
		this.context = context;
	}
	
	public void show(int state) {
		if (state == BleConnectionService.STATE_CONNECTED) {
			UIUtil.setToast(context, NT_CONNECTED);
		}
		else if (state == BleConnectionService.STATE_DISCONNECTED) {
			UIUtil.setToast(context, NT_DISCONNECTED);
		}
		else if (state == BleConnectionService.STATE_CONNECTING) {
			UIUtil.setToast(context, NT_CONNECTTING);
		}
		else if (state == BleConnectionService.STATE_SCANNING) {
			UIUtil.setToast(context, NT_SCANNING);
		}
		else if (state == BleConnectionService.STATE_UNKOWN) {
			UIUtil.setToast(context, NT_UNKNOWN);
		}
		else {
			// do nothing
		}
	}
}
