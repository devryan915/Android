package com.langlang.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.langlang.data.WarningHteInfo;
import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WarningHteManager {

	// private EcgDBOpenHelper ecgDBOpenHelper;
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;

	// public SqlManager(Context context) {
	// ecgDBOpenHelper = new EcgDBOpenHelper(context);
	// }
	public WarningHteManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}
	
	public void increase(String minuteData, int heart_rate) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into w_heart_rate(minute_data, heart_rate)values(?, ?)",
				new Object[] { minuteData, heart_rate });

		db.close();
	}
	
	public void increase(String minuteData, int heart_rate, String uid) {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dtStr = sdf.format(now);
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into w_heart_rate(minute_data, heart_rate, uid, crt_datetime) values(?, ?, ?, datetime('" 
				+ dtStr + "'))",
				new Object[] { minuteData, heart_rate, uid });

		db.close();
	}
	
	public List<WarningHteInfo> getWarnings(Date date, String uid) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strDate = sdf.format(date);
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("w_heart_rate", 
								 new String[] { "_id", "heart_rate", "minute_data" }, 
								 "minute_data like '" + strDate + "%' and uid=? order by _id desc", 
								 new String[] { uid },
								 null, null, null);
		
		List<WarningHteInfo> list = new ArrayList<WarningHteInfo>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			int heartRate = cursor.getInt(cursor.getColumnIndex("heart_rate")); 
			String minuteDate = cursor.getString(cursor.getColumnIndex("minute_data")); 
			list.add(new WarningHteInfo(id, minuteDate, heartRate));
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	public int getWarningCount(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strDate = sdf.format(date);
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		int count = 0;

		Cursor cursor = db.query("w_heart_rate", 
								 new String[] { "count(1) as count_warning" }, 
								 "minute_data like '" + strDate + "%' order by _id asc", 
								 null,
								 null, null, null);		
		if (cursor.moveToNext()) {
			count = cursor.getInt(cursor.getColumnIndex("count_warning")); 
		}
		
		if (cursor != null) {
			cursor.close();
		}
		return count;
	}
	
	public int getWarningCount(Date date, String uid) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strDate = sdf.format(date);
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		int count = 0;

		Cursor cursor = db.query("w_heart_rate", 
								 new String[] { "count(1) as count_warning" }, 
								 "minute_data like '" + strDate + "%' and uid=? order by _id asc", 
								 new String[] { uid },
								 null, null, null);		
		if (cursor.moveToNext()) {
			count = cursor.getInt(cursor.getColumnIndex("count_warning")); 
		}
		
		if (cursor != null) {
			cursor.close();
		}
		return count;
	}
	
	public void clearDataBeforeNDays(Date date, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dtStr = sdf.format(date);
		
		int daysAgo = -days;
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL("delete from w_heart_rate where crt_datetime < datetime('"
				 + dtStr + "', '" + Integer.toString(daysAgo)+ " day')");
		db.close();
	}
}
