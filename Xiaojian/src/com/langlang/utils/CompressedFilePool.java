package com.langlang.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import com.langlang.global.UserInfo;

public class CompressedFilePool {
	Map<String, DataOutputStream> mPool = new HashMap<String, DataOutputStream>();
	
	private String mMinuteData = null;
	
	public DataOutputStream getOutputStream(String type) {
		return mPool.get(type);
	}
	
	public void reset() {
		flushCompressFiles();
		mPool.clear();
	}	
	
	public DataOutputStream createDataOutputStream(String type, String currentMinute) {
		DataOutputStream dataOutputStream = null;
		
		try {
//			String dataPath = Program.getSDDataPathByDataType(type, currentMinute);
			String dataPath = getDataPath(type, currentMinute);
			
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(dataPath));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
			dataOutputStream = new DataOutputStream(bufferedOutputStream);
		}
		catch (Exception ex) {
			System.out.println("action CompressedFilePool createDataOutputStream[" 
									+ type + "][" + currentMinute + "]");
			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dataOutputStream = null;
			}
		}
		
		if (dataOutputStream != null) {
			mPool.put(type, dataOutputStream);
		}
		
		return dataOutputStream;
	}
	
	public DataOutputStream createDataOutputStream(String type, String currentMinute, String uid) {
		DataOutputStream dataOutputStream = null;
		
		try {
//			String dataPath = Program.getSDDataPathByDataType(type, currentMinute, uid);
			String dataPath = getDataPath(type, currentMinute, uid);
			
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(dataPath));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
			dataOutputStream = new DataOutputStream(bufferedOutputStream);
		}
		catch (Exception ex) {
			System.out.println("action CompressedFilePool createDataOutputStream[" 
									+ type + "][" + currentMinute + "]");
			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dataOutputStream = null;
			}
		}
		
		if (dataOutputStream != null) {
			mPool.put(type, dataOutputStream);
		}
		
		return dataOutputStream;
	}
	
	public void flushCompressFiles() {
		Set<String> keySet = mPool.keySet();
		
		for (String key : keySet) {
			OutputStream outputStream = null;
			outputStream = mPool.get(key);
			if (outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				mPool.remove(outputStream);
			}		
		}
		
		mMinuteData = null;
	}
	
	public void openCompressFiles(String currentMinute) {
		mMinuteData = currentMinute;
		
		createDataOutputStream(Program.ECG_DATA, currentMinute);
		createDataOutputStream(Program.RESP_DATA, currentMinute);
		createDataOutputStream(Program.TUMBLE_DATA, currentMinute);
		createDataOutputStream(Program.ACCELERATION_X_DATA, currentMinute);
		createDataOutputStream(Program.ACCELERATION_Y_DATA, currentMinute);
		createDataOutputStream(Program.ACCELERATION_Z_DATA, currentMinute);
		createDataOutputStream(Program.ACCE_VECTOR_DATA, currentMinute);
		createDataOutputStream(Program.STEP_CALORIES_DATA, currentMinute);
		createDataOutputStream(Program.TEMP_DATA, currentMinute);
		createDataOutputStream(Program.VOLTAGE_DATA, currentMinute);
		createDataOutputStream(Program.HTE_WARNING_DATA, currentMinute);
		createDataOutputStream(Program.HEART_RATE_DATA, currentMinute);
	}
	
	public void openCompressFiles(String currentMinute, String uid) {
		createDataOutputStream(Program.ECG_DATA, currentMinute, uid);
		createDataOutputStream(Program.RESP_DATA, currentMinute, uid);
		createDataOutputStream(Program.TUMBLE_DATA, currentMinute, uid);
		createDataOutputStream(Program.ACCELERATION_X_DATA, currentMinute, uid);
		createDataOutputStream(Program.ACCELERATION_Y_DATA, currentMinute, uid);
		createDataOutputStream(Program.ACCELERATION_Z_DATA, currentMinute, uid);
		createDataOutputStream(Program.ACCE_VECTOR_DATA, currentMinute, uid);
		createDataOutputStream(Program.STEP_CALORIES_DATA, currentMinute, uid);
		createDataOutputStream(Program.TEMP_DATA, currentMinute, uid);
		createDataOutputStream(Program.VOLTAGE_DATA, currentMinute, uid);
		createDataOutputStream(Program.HTE_WARNING_DATA, currentMinute, uid);
		createDataOutputStream(Program.HEART_RATE_DATA, currentMinute, uid);
	}
	
	private String getDataPath(String type, String currentMinute) {
		String dataPath;
		if (UserInfo.CMPR_DATX == UserInfo.dataCompressType()) {
			dataPath = Program.getSDDatXPathByDataType(type, currentMinute);
		}
		else {
			dataPath = Program.getSDDataPathByDataType(type, currentMinute);
		}
		return dataPath;
	}
	
	private String getDataPath(String type, String currentMinute, String uid) {
		String dataPath;
		if (UserInfo.CMPR_DATX == UserInfo.dataCompressType()) {
			dataPath = Program.getSDDatXPathByDataType(type, currentMinute, uid);
		}
		else {
			dataPath = Program.getSDDataPathByDataType(type, currentMinute, uid);
		}
		
		return dataPath;
	}
	
	public String getMinuteData() {
		return mMinuteData;
	}
}
