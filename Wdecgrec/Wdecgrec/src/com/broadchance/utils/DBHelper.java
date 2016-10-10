package com.broadchance.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.broadchance.manager.AppApplication;

public class DBHelper extends SQLiteOpenHelper {
	private final static int DB_VERSION = 1;
	private final static String DB_ROOT_DIR = "wdecgrec";
	private final static String DB_NAME = "appdata";
	private static DBHelper Instance = null;

	// public final static String TBL_USER = "user";
	public final static String TBL_UPLOAD = "upload";
	public final static String TBL_ALERT = "alert";

	public synchronized static DBHelper getInstance() {
		// if (Instance == null)
		Instance = new DBHelper(AppApplication.Instance);
		return Instance;
	}

	private static String getDBName() {
		String dbName = DB_NAME;
		// if (ConstantConfig.Debug) {
		// if (Environment.getExternalStorageState().equals(
		// Environment.MEDIA_MOUNTED)) {
		// String path = Environment.getExternalStorageDirectory()
		// .getAbsolutePath();
		// dbName = path + "/" + DB_ROOT_DIR;
		// File file = new File(dbName);
		// if (!file.exists() && !file.mkdirs()) {
		// dbName = DB_NAME;
		// } else {
		// dbName = dbName + "/" + DB_NAME;
		// }
		// }
		// }
		return dbName;
	}

	private DBHelper(Context context) {
		this(context, getDBName(), DB_VERSION);
	}

	private DBHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	private DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// status 状态0非当前用户，1当前用户
		// db.execSQL("CREATE TABLE "
		// + TBL_USER
		// +
		// " (user_name TEXT PRIMARY KEY , pwd TEXT, nick_name TEXT, userid TEXT,token TEXT,status integer,macaddress TEXT,certkey text);");

		/**
		 * file_name 文件名如:201601021525234(yyyyMMddHHmmssSSSZ) path 文件的绝对路径
		 * status 0数据未做处理1正在上传2上传成功3上传失败 uploadtimes上传重试次数 filetype 1为补传文件
		 * datetime数据存入时间yyyyMMddHHmmssSSSZa
		 */
		db.execSQL("CREATE TABLE "
				+ TBL_UPLOAD
				+ " (file_name TEXT PRIMARY KEY ,user_id TEXT,path TEXT,  status integer,uploadtimes integer,data_begintime TEXT,data_endtime TEXT,creation_date TEXT,upload_date TEXT, filetype integer,bpath TEXT,hrs TEXT);");
		/**
		 * 预警信息表 _id 自定id userid 用户id orderno 业务单号 id 报警类型A00001... state 1触发0取消
		 * time 触发时间yyyyMMdd HH:mm:ss.SSS value 报警内容JSON数据,creattime yyyyMMdd
		 * HH:mm:ss.SSS
		 */
		db.execSQL("CREATE TABLE "
				+ TBL_ALERT
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,userid TEXT,orderno TEXT,id TEXT,state integer,time TEXT,value TEXT,creattime TEXT);");

	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	public boolean tabbleIsExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// if (tabbleIsExist(TBL_USER))
		// db.execSQL("DROP TABLE " + TBL_USER + ";");
		if (tabbleIsExist(TBL_UPLOAD))
			db.execSQL("DROP TABLE " + TBL_UPLOAD + ";");
		if (tabbleIsExist(TBL_ALERT))
			db.execSQL("DROP TABLE " + TBL_ALERT + ";");
		onCreate(db);
	}

}
