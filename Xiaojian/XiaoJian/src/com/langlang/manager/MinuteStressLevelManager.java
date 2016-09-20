package com.langlang.manager;

import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MinuteStressLevelManager {

	// private EcgDBOpenHelper ecgDBOpenHelper;
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;

	// public SqlManager(Context context) {
	// ecgDBOpenHelper = new EcgDBOpenHelper(context);
	// }
	public MinuteStressLevelManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}
	
	public void increase(String minuteData, int stressLevel) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into minute_stress_level(minute_data, stress_level)values(?, ?)",
				new Object[] { minuteData, stressLevel });

		db.close();
	}
	
	public Integer getNewestStressLevel() {
		db = uploadInfoDBOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("minute_stress_level", 
								 new String[] { "_id", "stress_level", "minute_data" },
								 "1 = 1 order by _id desc", 
								 null ,//new String[] {strDate},
								 null, null, null);

		if (cursor.moveToNext()) {
			int stressLevel = cursor.getInt(cursor.getColumnIndex("stress_level")); 
			return stressLevel;
		}
		cursor.close();
		return null;
	}

	public Integer getStressLevelByMinite(String minuteData) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("minute_stress_level", 
								 new String[] { "_id", "stress_level", "minute_data" },
								 "minute_data = ?", 
								 new String[] { minuteData },
								 null, null, null);
		if (cursor.moveToNext()) {
			int stressLevel = cursor.getInt(cursor.getColumnIndex("stress_level"));
			cursor.close();
			return stressLevel;
		}
		cursor.close();
		return null;
	}
}
