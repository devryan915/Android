package com.langlang.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.langlang.data.WarningTempInfo;
import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WarningTempManager {

	// private EcgDBOpenHelper ecgDBOpenHelper;
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;

	// public SqlManager(Context context) {
	// ecgDBOpenHelper = new EcgDBOpenHelper(context);
	// }
	public WarningTempManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}
	
	public void increase(String minuteData, int tempData) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into w_temp(minute_data, temp_data)values(?, ?)",
				new Object[] { minuteData, tempData });

		db.close();
	}
	
	public List<WarningTempInfo> getWarnings(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strDate = sdf.format(date);
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("w_temp", 
								 new String[] { "_id", "temp_data", "minute_data" }, 
								 "minute_data like '" + strDate + "%' order by _id asc", 
								 null,
								 null, null, null);
		
		List<WarningTempInfo> list = new ArrayList<WarningTempInfo>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			int tempData = cursor.getInt(cursor.getColumnIndex("temp_data")); 
			String minuteDate = cursor.getString(cursor.getColumnIndex("minute_data")); 
			list.add(new WarningTempInfo(id, minuteDate, tempData));
		}
		return list;
	}
}
