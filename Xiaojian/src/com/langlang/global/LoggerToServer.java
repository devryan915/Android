package com.langlang.global;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerToServer {
	public static void log(String msg) {
		String username = UserInfo.getIntance().getUserData().getMy_name();
		String version = UserInfo.getIntance().getUserData().getVersion();
		if (version == null || version.length() <= 0) {
			version = "未知版本";
		}
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LogToServerThread logToServerThread = new LogToServerThread();
		logToServerThread.addLog(username, "0:0:0:0", version, sdf.format(now), msg);
		logToServerThread.start();	
	}
}
