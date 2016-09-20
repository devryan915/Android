package com.langlang.manager;

import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LastUploadManager {
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;
	
	public LastUploadManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}

	public void increase(String lastUpload) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into last_upload(_id , data)values(?,?)",
				new Object[] {1, lastUpload });
		db.close();
//		System.out.println("action LastUploadManager increase completed.");
	}
	
	public void increase(String lastUpload, String uid) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into last_upload(data, uid) values(?,?)",
				new Object[] {lastUpload, uid});
		db.close();
	}

	public void update(String lastUpload) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL("update last_upload set data=? where _id=?",
				new Object[] { lastUpload, 1 });
		db.close();
//		System.out.println("action LastUploadManager update completed.");
	}
	
	public void update(String lastUpload, String uid) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL("update last_upload set data=? where uid=?",
				new Object[] { lastUpload, uid });
		db.close();
	}

	public String query() {		
		String lastUpdate = null;
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		
		Cursor cursor = db.query("last_upload", 
								new String[] { "data" }, 
								"_id=?",
								new String[] { "1" }, 
								null, 
								null, 
								null);
//		while (cursor.moveToNext()) {
		if (cursor.moveToNext()) {
			lastUpdate = cursor.getString(cursor
					.getColumnIndex("data"));			
		}
		
		cursor.close();
		return lastUpdate;
	}
	
	public String queryByUser(String uid) {		
		String lastUpdate = null;
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		
		Cursor cursor = db.query("last_upload", 
								new String[] { "data" }, 
								"uid=?",
								new String[] { uid }, 
								null, 
								null, 
								null);
//		while (cursor.moveToNext()) {
		if (cursor.moveToNext()) {
			lastUpdate = cursor.getString(cursor
					.getColumnIndex("data"));			
		}
		
		cursor.close();
		return lastUpdate;
	}
}
