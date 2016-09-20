package com.langlang.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class MiscUtils {
	public static boolean isServiceRunning(Context context, String packageName){
	     ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	     List<RunningServiceInfo> list = am.getRunningServices(30);
	     for(RunningServiceInfo info : list){
	         if(info.service.getClassName().equals(packageName)){
	        	 return true;
	         }
	    }
	    return false;
	}
	
	public static String parseArrayAsString(float[] arrayData) {
		if (arrayData.length <= 0) 
			return "";
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arrayData.length; i++) {
			if (i == 0) {				
			} else {
				sb.append(",");
			}
			sb.append(Float.toString(arrayData[i]));
		}
		
		return sb.toString();
	}
	
	public static String parseArrayAsString(String[] arrayData) {
		if (arrayData.length <= 0) 
			return "";
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arrayData.length; i++) {
			if (i == 0) {				
			} else {
				sb.append(",");
			}
			sb.append(arrayData[i]);
		}
		
		return sb.toString();
	}
	
	public static Map<String, String> parseJSONAsMap(JSONObject jObj) throws JSONException {
		Map<String, String> map = new HashMap<String, String>();
		
		Iterator<String> it = jObj.keys();
		
		List<String> list = new ArrayList<String>();		
		while (it.hasNext()) {
			String key = it.next().toString();
			System.out.println(key);
			list.add(key);
		}
		
		Iterator<String> itForMap = list.iterator();
		while (itForMap.hasNext()) {
			String key = itForMap.next();
			map.put(key, (String) jObj.get(key));
		}
		
		return map;
	}
	
	public static String dumpBytesAsString(byte[] data) {
		final StringBuilder stringBuilder = new StringBuilder(data.length);
		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		
		return stringBuilder.toString();
	}
}
