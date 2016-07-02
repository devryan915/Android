package com.langlang.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.langlang.data.ECGResult;
import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MinuteECGResultManager {

	// private EcgDBOpenHelper ecgDBOpenHelper;
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;

	public MinuteECGResultManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}
	
	public void increase(String minuteData, ECGResult ecgResult) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		System.out.println("action MinuteECGResultManager increase minuteData[" + minuteData + "]");
		
		if (minuteData.length() == 12) {
			String year = minuteData.substring(0, 4);
			String month = minuteData.substring(4, 6);
			String day = minuteData.substring(6, 8);
			String hour = minuteData.substring(8, 10);
			String minute = minuteData.substring(10, 12);
			
			String dt = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";
			System.out.println("action MinuteECGResultManager increase dt[" + dt + "]");
			
			String cmd = "insert into minute_ecg_result(minute_data, crt_datetime, lf , hf) values(?, datetime('";
			cmd += dt;
			cmd += "'), ?, ?)";
			
			System.out.println("action MinuteECGResultManager increase cmd[" + cmd + "]");
			
			db.execSQL(cmd, new Object[] { minuteData, ecgResult.LF, ecgResult.HF });
		} else {
			System.out.println("action MinuteECGResultManager increase minuteData format error.");
		}

		db.close();
		System.out.println("action MinuteECGResultManager increase completed.");
	}
	
	public void increase(String minuteData, ECGResult ecgResult, String uid) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		
		if (minuteData.length() == 12) {
			String year = minuteData.substring(0, 4);
			String month = minuteData.substring(4, 6);
			String day = minuteData.substring(6, 8);
			String hour = minuteData.substring(8, 10);
			String minute = minuteData.substring(10, 12);
			
			String dt = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";
			
			String cmd = "insert into minute_ecg_result(minute_data, crt_datetime, lf , hf, uid) values(?, datetime('";
			cmd += dt;
			cmd += "'), ?, ?, ?)";
			
			System.out.println("action MinuteECGResultManager increase cmd[" + cmd + "]");
			
			db.execSQL(cmd, new Object[] { minuteData, ecgResult.LF, ecgResult.HF, uid });
		} else {
			System.out.println("action MinuteECGResultManager increase minuteData format error.");
		}

		db.close();
	}
	
	public Map<String, ECGResult> getECGResultsBetween(Date start, Date end) {
		Cursor cursor = null;
		Map<String, ECGResult> map = null;
		
		try {
			db = uploadInfoDBOpenHelper.getWritableDatabase();
			
			map = new HashMap<String, ECGResult>();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startDate = sdf.format(start);
			String endDate = sdf.format(end);
			
			System.out.println("action MinuteECGResultManager dtStr[" 
							+ startDate + "," + endDate + "]");
			
			String whereStmt = "crt_datetime < datetime('";
			whereStmt += endDate;
			whereStmt += "') and crt_datetime > datetime('";
			whereStmt += startDate;
			whereStmt += "')";
			
			System.out.println("action MinuteECGResultManager whereStmt[" + whereStmt + "]");
			
			cursor = db.query(
						"minute_ecg_result", 
						new String[] { "_id", "minute_data", "lf", "hf" }, 
						whereStmt,
						null,
						null, null, null);

			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("minute_data")),
						new ECGResult(0,
									  cursor.getFloat(cursor.getColumnIndex("lf")),
									  cursor.getFloat(cursor.getColumnIndex("hf"))
									 )
						);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("action MinuteECGResultManager getECGResultsBetween exception ex[" + ex.toString() + "]");
			map = null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return map;
	}
	
	public Map<String, ECGResult> getECGResultsBetween(Date start, Date end, String uid) {
		Cursor cursor = null;
		Map<String, ECGResult> map = null;
		
		try {
			db = uploadInfoDBOpenHelper.getWritableDatabase();
			
			map = new HashMap<String, ECGResult>();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startDate = sdf.format(start);
			String endDate = sdf.format(end);
			
			System.out.println("action MinuteECGResultManager dtStr[" 
							+ startDate + "," + endDate + "]");
			
			String whereStmt = "crt_datetime < datetime('";
			whereStmt += endDate;
			whereStmt += "') and crt_datetime > datetime('";
			whereStmt += startDate;
			whereStmt += "')";
			whereStmt += " and uid=?";
			
			System.out.println("action MinuteECGResultManager whereStmt[" + whereStmt + "]");
			
			cursor = db.query(
						"minute_ecg_result", 
						new String[] { "_id", "minute_data", "lf", "hf" }, 
						whereStmt,
						new String[] { uid },
						null, null, null);

			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("minute_data")),
						new ECGResult(0,
									  cursor.getFloat(cursor.getColumnIndex("lf")),
									  cursor.getFloat(cursor.getColumnIndex("hf"))
									 )
						);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("action MinuteECGResultManager getECGResultsBetween exception ex[" + ex.toString() + "]");
			map = null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return map;
	}
	
	public void clearDataBeforeNDays(Date date, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dtStr = sdf.format(date);
		
		int daysAgo = -days;
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL("delete from minute_ecg_result where crt_datetime < datetime('"
				 + dtStr + "', '" + Integer.toString(daysAgo)+ " day')");
		db.close();
	}
}
