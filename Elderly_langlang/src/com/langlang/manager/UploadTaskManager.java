package com.langlang.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.langlang.data.UploadTaskInfo;
import com.langlang.db.UploadInfoDBOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UploadTaskManager {

	// private EcgDBOpenHelper ecgDBOpenHelper;
	private UploadInfoDBOpenHelper uploadInfoDBOpenHelper;
	private SQLiteDatabase db;

	// public SqlManager(Context context) {
	// ecgDBOpenHelper = new EcgDBOpenHelper(context);
	// }
	public UploadTaskManager(Context context) {
		uploadInfoDBOpenHelper = new UploadInfoDBOpenHelper(context);
	}

	/**
	 * 数据库添加
	 * 
	 * @param upload_time
	 *            上传时间
	 * @param file_name
	 *            文件名称
	 * @param file_state
	 *            文件状态
	 * @param file_path
	 *            文件路径
	 */
	// public void increase(String upload_time, String file_name, int
	// file_state,
	// String file_path) {
	public void increase(String minuteData) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		// db.execSQL(
		// "insert into upload_task(upload_time,file_name,file_state,file_path)values(?,?,?,?)",
		// new Object[] { upload_time, file_name, file_state, file_path });
		System.out.println("action UploadTaskManager increase minuteData[" + minuteData + "]");
		
		if (minuteData.length() == 12) {
			String year = minuteData.substring(0, 4);
			String month = minuteData.substring(4, 6);
			String day = minuteData.substring(6, 8);
			String hour = minuteData.substring(8, 10);
			String minute = minuteData.substring(10, 12);
			
			String dt = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";
			System.out.println("action UploadTaskManager increase dt[" + dt + "]");
			
			String cmd = "insert into upload_task(minute_data, file_state, crt_datetime)values(?, 0, datetime('";
			cmd += dt;
			cmd += "'))";
			
			System.out.println("action UploadTaskManager increase cmd[" + cmd + "]");
			
			db.execSQL(cmd, new Object[] { minuteData });
		} else {
			System.out.println("action UploadTaskManager increase minuteData format error.");
		}

		db.close();
		System.out.println("action UploadTaskManager increase completed.");
	}
	
	public void increase(String minuteData, String uid) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		
		if (minuteData.length() == 12) {
			String year = minuteData.substring(0, 4);
			String month = minuteData.substring(4, 6);
			String day = minuteData.substring(6, 8);
			String hour = minuteData.substring(8, 10);
			String minute = minuteData.substring(10, 12);
			
			String dt = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";
			System.out.println("action UploadTaskManager increase dt[" + dt + "]");
			
			String cmd = "insert into upload_task(minute_data, file_state, uid, crt_datetime)values(?, 0, ?, datetime('";
			cmd += dt;
			cmd += "'))";
			
			System.out.println("action UploadTaskManager increase cmd[" + cmd + "]");
			
			db.execSQL(cmd, new Object[] { minuteData, uid });
		} else {
			System.out.println("action UploadTaskManager increase minuteData format error.");
		}

		db.close();
		System.out.println("action UploadTaskManager increase completed.");
	}

	/**
	 * 根据文件的Id修改数据库的文件状态
	 * 
	 * @param id
	 *            文件id号
	 * @param file_state
	 *            文件状态
	 */
	public void update(int id, int file_state) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL("update upload_task set file_state=? where _id=?",
				new Object[] { file_state, id });
		db.close();
	}

	/**
	 * 根据文件的id删除数据库数据
	 * 
	 * @param id
	 *            文件id号
	 */
	public void delete(int id) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		// < db.execSQL("delete from ecg_info where _id=?", new Object[] { id
		// });
		db.execSQL("delete from upload_task where _id=?", new Object[] { id });
		db.close();
	}

	public void deleteByMinuteData(String minuteData) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		// < db.execSQL("delete from ecg_info where _id=?", new Object[] { id
		// });
		db.execSQL("delete from upload_task where minute_data=?", new Object[] { minuteData });
		db.close();
	}
	
	public void deleteByMinuteData(String minuteData, String uid) {
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		// < db.execSQL("delete from ecg_info where _id=?", new Object[] { id
		// });
		db.execSQL("delete from upload_task where minute_data=? and uid=?", 
								new Object[] { minuteData, uid });
		db.close();
	}
	
	/**
	 * 查询数据库
	 * 
	 * @param file_state
	 *            文件状态
	 */
	public String query(String file_state) {
		String nameString = null;
		db = uploadInfoDBOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("upload_task", new String[] { "file_state",
				"minute_data" }, "file_state=?", new String[] { file_state },
				null, null, null);

		while (cursor.moveToNext()) {
			nameString = cursor.getString(cursor.getColumnIndex("minute_data"));
			String fstate = cursor.getString(cursor
					.getColumnIndex("file_state"));
			System.out.println("action query  " + nameString + "::" + fstate);
		}
		return nameString;
	}

	/**
	 * 取一个需要上传的任务
	 * 
	 * @return
	 */
//	public UploadTaskInfo fetchOneUploadTask() {
//		try {
//			db = uploadInfoDBOpenHelper.getWritableDatabase();
//
//			UploadTaskInfo uploadTaskInfo = null;
////			Cursor cursor = db.query("upload_task", new String[] { "_id", "file_state", "minute_data" }, "file_state=? order by _id asc", new String[] { "0" },
////					null, null, null);
//			
//			Date now = new Date();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String dtStr = sdf.format(now);
//			
//			System.out.println("action UploadTaskManager dtStr[" + dtStr + "]");
//			
//			String whereStmt = "file_state=? and crt_datetime < datetime('";
//			whereStmt += dtStr;
//			whereStmt += "', '-10 minute') order by _id desc";
//			
//			System.out.println("action UploadTaskManager whereStmt[" + whereStmt + "]");
//			
//			Cursor cursor = db.query(
//						"upload_task", 
//						new String[] { "_id", "file_state", "minute_data" }, 
////						"file_state=? and crt_datetime < datetime('now', '-10 minute') order by _id desc",
//						whereStmt,
//						new String[] { "0" },
//					null, null, null);
//
//			if (cursor.moveToNext()) {
//				String minuteData = cursor.getString(cursor
//						.getColumnIndex("minute_data"));
//
//				uploadTaskInfo = new UploadTaskInfo(cursor.getInt(cursor
//						.getColumnIndex("_id")), cursor.getString(cursor
//						.getColumnIndex("minute_data")));
//				return uploadTaskInfo;
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return null;
//	}
	
	public UploadTaskInfo fetchOneUploadTaskByUser(String uid) {
		try {
			db = uploadInfoDBOpenHelper.getWritableDatabase();

			UploadTaskInfo uploadTaskInfo = null;
			
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dtStr = sdf.format(now);
			
			System.out.println("action UploadTaskManager dtStr[" + dtStr + "]");
			
			String whereStmt = "file_state=? and uid=? and crt_datetime < datetime('";
			whereStmt += dtStr;
			whereStmt += "', '-10 minute') order by _id desc limit 1";
			
			System.out.println("action UploadTaskManager whereStmt[" + whereStmt + "]");
			
			Cursor cursor = db.query(
						"upload_task", 
						new String[] { "_id", "file_state", "minute_data, uid" }, 
//						"file_state=? and crt_datetime < datetime('now', '-10 minute') order by _id desc",
						whereStmt,
						new String[] { "0", uid },
					null, null, null);

			if (cursor.moveToNext()) {
				int taskId = cursor.getInt(cursor.getColumnIndex("_id"));
				String minuteData = cursor.getString(cursor.getColumnIndex("minute_data"));
				String userid = cursor.getString(cursor.getColumnIndex("uid"));
				
				uploadTaskInfo = new UploadTaskInfo(taskId, minuteData, userid);

				return uploadTaskInfo;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void clearDataBeforeNDays(Date date, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dtStr = sdf.format(date);
		
		int daysAgo = -days;
		
		db = uploadInfoDBOpenHelper.getWritableDatabase();
		db.execSQL("delete from upload_task where crt_datetime < datetime('"
				 + dtStr + "', '" + Integer.toString(daysAgo)+ " day')");
		db.close();
	}
}
