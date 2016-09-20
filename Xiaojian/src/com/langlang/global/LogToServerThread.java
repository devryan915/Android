package com.langlang.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LogToServerThread extends Thread {
	private Map<String, String> map = new HashMap<String, String>();
	
	public LogToServerThread() {
		map.clear();
	}
	public void addParam(String name, String value) {
		map.put(name, value);
	}
	
	public void addLog(String username, String ip, String exception, String time, String remark) {
		map.put("username", (username == null) ? "" : username);
		map.put("ip", (ip == null) ? "" : ip);
		map.put("exception", (exception == null) ? "" : exception);
		map.put("time", (time == null) ? "" : time);
		map.put("remark", (remark == null) ? "" : remark);
	}
	
	@Override
	public void run() {
		if (map.size() <= 0) return;
		
		Set<Entry<String, String>> entrySet = map.entrySet();
		
		boolean isFirstItem = true;
		String request = "";
		
		for (Entry<String, String> entry: entrySet) {
			if (isFirstItem) {
				isFirstItem = false;
			} else {
				request += ",";
			}
			
			request += entry.getKey();
			request += ":\"";
			request += entry.getValue();
			request += "\"";
		}
		
		String log = "[{" + request + "}]";
		String result = Client.logToServer(log);
		System.out.println("LogToServerThread result:"+result);
	}
}
