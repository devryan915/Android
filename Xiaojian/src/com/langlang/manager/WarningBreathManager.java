package com.langlang.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.langlang.data.WarningBreathInfo;
import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WarningBreathManager {

	// private EcgDBOpenHelper ecgDBOpenHelper;
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;

	// public SqlManager(Context context) {
	// ecgDBOpenHelper = new EcgDBOpenHelper(context);
	// }
	public WarningBreathManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}
	
	public void increase(String minuteData, int breath) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into w_breath(minute_data, breath_num)values(?, ?)",
				new Object[] { minuteData, breath });

		db.close();
	}

	public List<WarningBreathInfo> getWarnings(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strDate = sdf.format(date);
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("w_breath", 
								 new String[] { "_id", "breath_num", "minute_data" }, 
								 "minute_data like '" + strDate + "%' order by _id asc", 
								 null,
								 null, null, null);
		
		List<WarningBreathInfo> list = new ArrayList<WarningBreathInfo>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			int breathNum = cursor.getInt(cursor.getColumnIndex("breath_num")); 
			String minuteDate = cursor.getString(cursor.getColumnIndex("minute_data")); 
			list.add(new WarningBreathInfo(id, minuteDate, breathNum));
		}
		return list;
	}
}
