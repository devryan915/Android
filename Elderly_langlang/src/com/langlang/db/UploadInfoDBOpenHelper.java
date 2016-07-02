package com.langlang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UploadInfoDBOpenHelper extends SQLiteOpenHelper {

	public UploadInfoDBOpenHelper(Context context) {
		super(context, "Upload.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL("create table upload_task (_id integer primary key autoincrement,time integer,data varchar(10))");
		db.execSQL("create table upload_task("
				+ "_id integer primary key autoincrement,"
				+ "file_state integer, " 
				+ "uid varchar(100) ,"
				+ "minute_data varchar(20) " 
				+ ", crt_datetime timestamp)");
		db.execSQL("create index [IX_UT_MD_UID] on [upload_task]([uid],[minute_data])");
		db.execSQL("create index [IX_UT_CD_UID] on [upload_task]([uid],[crt_datetime])");
		db.execSQL("create index [IX_UT_CD] on [upload_task]([crt_datetime])");
		
		db.execSQL("create table last_upload (_id integer primary key autoincrement," 
					+ "data varchar(50), uid varchar(100))");
		db.execSQL("create index [IX_LU_UID] on [last_upload]([uid])");		
		
		db.execSQL("create table minute_stress_level("
				+ "_id integer primary key autoincrement,"
				+ "stress_level integer, " + "minute_data varchar(20))");
		db.execSQL("create index [IX_MSL_MD] on [minute_stress_level]([minute_data])");	
		
		db.execSQL("create table minute_ecg_result("
				+ "_id integer primary key autoincrement,"
				+ "lf float, hf float, " + "minute_data varchar(20), crt_datetime timestamp, uid varchar(100))");
		db.execSQL("create index [IX_MER_MD_UID] on [minute_ecg_result]([uid],[minute_data])");
		db.execSQL("create index [IX_MER_CD_UID] on [minute_ecg_result]([uid],[crt_datetime])");
		db.execSQL("create index [IX_MER_CD] on [minute_ecg_result]([crt_datetime])");
		
		db.execSQL("create table minute_breath("
				+ "_id integer primary key autoincrement,"
				+ "breath_num integer, " + "minute_data varchar(20))");
		
		db.execSQL("create table w_heart_rate("
				+ "_id integer primary key autoincrement,"
				+ "heart_rate integer, " + "minute_data varchar(20), uid varchar(100), crt_datetime timestamp)");
		db.execSQL("create index [IX_WHR_MD_UID] on [w_heart_rate]([uid],[minute_data])");	
		db.execSQL("create index [IX_WHR_CD] on [w_heart_rate]([crt_datetime])");	
		
		db.execSQL("create table w_breath("
				+ "_id integer primary key autoincrement,"
				+ "breath_num integer, " + "minute_data varchar(20))");
		
		db.execSQL("create table w_tumble("
				+ "_id integer primary key autoincrement,"
				+ "minute_data varchar(20), uid varchar(100), crt_datetime timestamp)");
		db.execSQL("create index [IX_WT_MD_UID] on [w_tumble]([uid], [minute_data])");
		db.execSQL("create index [IX_WT_CD] on [w_tumble]([crt_datetime])");
		
//		db.execSQL("create table kll_friends("
//				+ "_id integer primary key autoincrement,"
//				+ "image integer,user_name varchar(20))");
		db.execSQL("create table sport_friends("
				+ "_id integer primary key autoincrement,"
				+ "image integer,my_name varchar(40),user_name varchar(20))");
		db.execSQL("create table w_temp("
				+ "_id integer primary key autoincrement,"
				+ "temp_data integer, " + "minute_data varchar(20))");

		db.execSQL("create table lib_table("
				+ "_id integer primary key autoincrement,"
				+ "title varchar(40), " + "url varchar(100), "
				+ "data_time integer, " + "state integer)");
		
		db.execSQL("create table my_notify(_id integer primary key autoincrement,type varchar(20),date varchar(40),count integer)");
		db.execSQL("create index [IX_MN] on [my_notify]([type])");
		
		db.execSQL("create table kll_fd("
				+ "_id integer primary key autoincrement,"
				+ "image integer,my_name varchar(40),user_name varchar(20))");
		db.execSQL("create table w_mental("
				+ "_id integer primary key autoincrement,"
				+ "mental_num integer, " + "minute_data varchar(20))");
		db.execSQL("create table w_pose("
				+ "_id integer primary key autoincrement,"
				+ "pose_num integer, " + "minute_data varchar(20))");
		db.execSQL("create table w_temps("
				+ "_id integer primary key autoincrement,"
				+ "temp_num integer, " + "minute_data varchar(20))");
		
		db.execSQL("create table weather_table("
				+ "_id integer primary key autoincrement,"
				+ "type varchar(20),date varchar(260),image integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// db.execSQL("drop table if exists ecg_data");
		db.execSQL("drop table if exists upload_task");
		db.execSQL("drop table if exists last_upload");
		db.execSQL("drop table if exists minute_stress_level");
		db.execSQL("drop table if exists minute_ecg_result");
		db.execSQL("drop table if exists minute_breath");
		db.execSQL("drop table if exists w_heart_rate");
		db.execSQL("drop table if exists w_breath");	
		db.execSQL("drop table if exists insert_friends_message");
		db.execSQL("drop table if exists w_tumble");
//		db.execSQL("drop table if exists kll_friends");
		db.execSQL("drop table if exists sport_friends");
		db.execSQL("drop table if exists w_temp");
		db.execSQL("drop table if exists lib_table");
		db.execSQL("drop table if exists my_notify");
		db.execSQL("drop table if exists kll_fd");
		db.execSQL("drop table if exists w_mental");
		db.execSQL("drop table if exists w_pose");
		db.execSQL("drop table if exists w_temps");
		db.execSQL("drop table if exists weather_table");
		onCreate(db);
	}
}
