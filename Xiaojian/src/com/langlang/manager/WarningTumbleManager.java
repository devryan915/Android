package com.langlang.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WarningTumbleManager {
	// private EcgDBOpenHelper ecgDBOpenHelper;
		private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
		private SQLiteDatabase db;

		// public SqlManager(Context context) {
		// ecgDBOpenHelper = new EcgDBOpenHelper(context);
		// }
		public WarningTumbleManager(Context context) {
			uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
		}
		
		public void increase(String minuteData) {
			db = uploadInfoDBOpenHelper.getWritableDatabase();
			db.execSQL(
					"insert into w_tumble(minute_data)values(?)",
					new Object[] { minuteData });

			db.close();
		}
		
		public void increase(String minuteData, String uid) {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dtStr = sdf.format(now);
			
			db = uploadInfoDBOpenHelper.getWritableDatabase();
			db.execSQL(
					"insert into w_tumble(minute_data, uid, crt_datetime) values(?, ?, datetime('"
					+ dtStr
					+ "'))",
					new Object[] { minuteData, uid });

			db.close();
		}
		
//		@SuppressLint("SimpleDateFormat")
//		public List<WarningTumbleInfo> getWarnings(Date date) {
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			String strDate = sdf.format(date);
//			db = uploadInfoDBOpenHelper.getWritableDatabase();
//			Cursor cursor = db.query("w_tumble", 
//									 new String[] { "_id", "minute_data" }, 
//									 "minute_data like '" + strDate + "%' order by _id asc", 
//									 null,
//									 null, null, null);
//			
//			List<WarningTumbleInfo> list = new ArrayList<WarningTumbleInfo>();
//			while (cursor.moveToNext()) {
//				int id = cursor.getInt(cursor.getColumnIndex("_id"));
//				String minuteDate = cursor.getString(cursor.getColumnIndex("minute_data")); 
//				list.add(new WarningTumbleInfo(id, minuteDate));
//			}
//			return list;
//		}
		
		public int getWarningCount(Date date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String strDate = sdf.format(date);
			
			db = uploadInfoDBOpenHelper.getWritableDatabase();
			int count = 0;

			Cursor cursor = db.query("w_tumble", 
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

			Cursor cursor = db.query("w_tumble", 
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
			db.execSQL("delete from w_tumble where crt_datetime < datetime('"
					 + dtStr + "', '" + Integer.toString(daysAgo)+ " day')");
			db.close();
		}
}
