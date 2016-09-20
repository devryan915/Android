package com.langlang.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.langlang.data.WarningMentalInfo;
import com.langlang.db.UploadInfoDBOpenHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WarningMentalManager {

	// private EcgDBOpenHelper ecgDBOpenHelper;
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;

	// public SqlManager(Context context) {
	// ecgDBOpenHelper = new EcgDBOpenHelper(context);
	// }
	public WarningMentalManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}
	
	public void increase(String minuteData, int heart_rate) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into w_mental(minute_data, mental_num)values(?, ?)",
				new Object[] { minuteData, heart_rate });

		db.close();
	}
	
	public List<WarningMentalInfo> getWarnings(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strDate = sdf.format(date);
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("w_mental", 
								 new String[] { "_id", "mental_num", "minute_data" }, 
								 "minute_data like '" + strDate + "%' order by _id asc", 
								 null,
								 null, null, null);
		
		List<WarningMentalInfo> list = new ArrayList<WarningMentalInfo>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			int heartRate = cursor.getInt(cursor.getColumnIndex("mental_num")); 
			String minuteDate = cursor.getString(cursor.getColumnIndex("minute_data")); 
			list.add(new WarningMentalInfo(id, minuteDate, heartRate));
		}
		return list;
	}
}
