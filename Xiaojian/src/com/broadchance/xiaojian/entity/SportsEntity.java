package com.broadchance.xiaojian.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.broadchance.xiaojian.service.BleDataDomainService;
import com.broadchance.xiaojian.utils.DBHelper;

public class SportsEntity {
	private String BleDatas;

	public String getBleDatas() {
		return BleDatas;
	}

	public void setBleDatas(String bleDatas) {
		BleDatas = bleDatas;
	}

	public String getDataTime() {
		return DataTime;
	}

	public void setDataTime(String dataTime) {
		DataTime = dataTime;
	}

	private String DataTime;

	private String DataID;

	public String getDataID() {
		return DataID;
	}

	public void setDataID(String dataID) {
		DataID = dataID;
	}

	private String MobileNo;
	private String DeviceNo;

	public String getMobileNo() {
		return MobileNo;
	}

	public void setMobileNo(String mobileNo) {
		MobileNo = mobileNo;
	}

	public String getDeviceNo() {
		return DeviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		DeviceNo = deviceNo;
	}
	public Queue<String> getOutofDateDataIDs(Context context) {
		SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
		String sql = "SELECT DataID from ble_sports_data  WHERE Isupload = 0 and DataTime<= ?";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		String[] selectionArgs = new String[] { new SimpleDateFormat(
				"yyyy-MM-dd 23:59:59").format(calendar.getTime()) };
		Queue<String> queue = new LinkedBlockingQueue<String>();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		while (cursor.moveToNext()) {
			queue.offer(cursor.getString(cursor.getColumnIndex("DataID")));
		}
		cursor.close();
		return queue;
	}
	public JSONArray getUploadData(Context context) {
		SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

		String sql = "SELECT * from ble_sports_data  WHERE Isupload = ? and DataTime<= ? limit 2000 order by DataTime ";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		String[] selectionArgs = new String[] { "0",
				new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(calendar.getTime()) };

		JSONArray entities = new JSONArray();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		while (cursor.moveToNext()) {
			try {
				JSONObject entity = new JSONObject();
				entity.put("BleDatas",
						cursor.getString(cursor.getColumnIndex("BleDatas")));
				entity.put("DataID",
						cursor.getString(cursor.getColumnIndex("DataID")));
				entity.put("DataTime",
						cursor.getString(cursor.getColumnIndex("DataTime")));
				entity.put("DeviceNo",
						cursor.getString(cursor.getColumnIndex("DeviceNo")));
				entity.put("MobileNo",
						cursor.getString(cursor.getColumnIndex("MobileNo")));
				entity.put("DataType", BleDataDomainService.DataType_Sports);
				entities.put(entity);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		return entities;
	}

	public boolean addData(Context context) {
		if (BleDatas != null && DataTime != null) {
			SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("BleDatas", BleDatas);
			values.put("DataID", DataID);
			values.put("DataTime", DataTime);
			values.put("DeviceNo", DeviceNo);
			values.put("MobileNo", MobileNo);
			return db.insert("ble_sports_data", null, values) != -1 ? true
					: false;
		}
		return false;
	}

	public boolean updateUploadDataStatus(Context context) {
		if (DataID != null) {
			SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("Isupload", 1);
			return db.update("ble_sports_data", values, "DataID = ?",
					new String[] { DataID }) != -1 ? true : false;
		}
		return false;
	}

	public boolean deleteData(Context context) {
		if (DataID != null) {
			SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
			return db.delete("ble_sports_data", "DataID = ?",
					new String[] { DataID }) != -1 ? true : false;
		}
		return false;
	}
}
