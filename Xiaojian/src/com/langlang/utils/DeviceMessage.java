package com.langlang.utils;

import com.langlang.data.DeviceData;

public class DeviceMessage {
	private static DeviceMessage deviceMessage=null;
	private DeviceData deviceData;
	private DeviceMessage(){
		deviceData=new DeviceData();
	}
	public  static DeviceMessage getInstance(){
		if(deviceMessage==null){
			deviceMessage=new DeviceMessage();
		}
		return deviceMessage;
	}
	
	public DeviceData getDeviceData(){
		return deviceData;
	}
}
