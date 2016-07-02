package com.broadchance.xiaojian.utils;

import java.io.File;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBHelper extends SQLiteOpenHelper {
	public final static int DB_VERSION = 1;
	private final static String DB_ROOT_DIR = "xiaojian";
	private final static String DB_NAME = "xiaojian";

	private static String getDBName() {
		String dbName = DB_NAME;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			dbName = path + "/" + DB_ROOT_DIR;
			File file = new File(dbName);
			if (!file.exists() && !file.mkdirs()) {
				dbName = DB_NAME;
			} else {
				dbName = dbName + "/" + DB_NAME;
			}
		}
		return dbName;
	}

	public DBHelper(Context context) {
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
		db.execSQL("CREATE TABLE ble_ecg_data (DataID TEXT PRIMARY KEY , MobileNo TEXT, DeviceNo TEXT, BleDatas TEXT,IsError INTEGER DEFAULT (0), DataTime TEXT, Isupload INTEGER DEFAULT (0));");
		db.execSQL("CREATE TABLE ble_sleep_data (DataID TEXT PRIMARY KEY , MobileNo TEXT, DeviceNo TEXT, BleDatas TEXT, DataTime TEXT, Isupload INTEGER DEFAULT (0));");
		db.execSQL("CREATE TABLE ble_sports_data (DataID TEXT PRIMARY KEY , MobileNo TEXT, DeviceNo TEXT, BleDatas TEXT, DataTime TEXT, Isupload INTEGER DEFAULT (0));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
