package com.langlang.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventLogger {
	public static void logEvent(String event) {
		String logPath = Program.getSDLanglangLogPath();
		File logDir = new File(logPath);
		
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		
		String eventFilePath = logPath + File.separator + Program.EVENT_LOG;
		File eventFile = new File(eventFilePath);
		
		if (!eventFile.exists()) {
			try {
				eventFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			if (eventFile.length() > 1024 * 256) {
				eventFile.delete();
				
				try {
					eventFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}			
		}
		
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String eventStr = sdf.format(now) + "\t" + event + "\n";
		
		Program.writerFile(eventFilePath, eventStr);
	}
	public static void logStep(String stepInfo) {
		String logPath = Program.getSDLanglangLogPath();
		File logDir = new File(logPath);
		
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		
		String eventFilePath = logPath + File.separator + Program.STEP_COUNT_LOG;
		File eventFile = new File(eventFilePath);
		
		if (!eventFile.exists()) {
			try {
				eventFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			if (eventFile.length() > 1024 * 1024 * 5) {
				eventFile.delete();
				
				try {
					eventFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}			
		}
		
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String eventStr = sdf.format(now) + "\t" + stepInfo + "\n";
		
		Program.writerFile(eventFilePath, eventStr);
	}
}
