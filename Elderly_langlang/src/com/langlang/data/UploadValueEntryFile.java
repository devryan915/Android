package com.langlang.data;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.langlang.global.UserInfo;
import com.langlang.utils.Program;

public class UploadValueEntryFile extends UploadFile {
	private final static int BACKWARD_TIME = 1000 * 60;
	
	protected List<ValueEntry> dataList;
		
	public UploadValueEntryFile(String uid, 
								String dataType, 
								String minuteData, List<ValueEntry> src) {
		this.uid = uid;
		this.dataType = dataType;
		this.minuteData = minuteData;
		
		// deep copy from the source data
		if (src == null || src.size() <= 0) {
			dataList = null;
		}
		else {
			dataList = new ArrayList<ValueEntry>(src.size());
			
			for (int i = 0; i < src.size(); i++) {
				dataList.add(src.get(i));
			}
		}
	}
	
	@Override
	public byte[] getBytes() {
		if (bytesData == null) {
			return convertListToBytes();
		}
		else {
			return bytesData;
		}
	}
	
	@Override
	public void saveToFile() {
		if (bytesData == null) { return; }
		
		String savePath = Program.getSDDatXPathByDataType(dataType, minuteData, uid);
		FileOutputStream fos = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			fos = new FileOutputStream(savePath);
			bufferedOutputStream = new BufferedOutputStream(fos);
			bufferedOutputStream.write(bytesData);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (bufferedOutputStream != null) {
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private byte[] convertListToBytes() {
		if (dataList == null) { return null; }
		
		byte[] data = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
			DataOutputStream out = new DataOutputStream(bufferedOutputStream);
		
			if (isWarningType(dataType)) {
				if (out != null) {
					int warningUp = 0;
					int warningDw = 0;
					
					if (dataType.equals(Program.HTE_WARNING_DATA)) {
						warningUp = UserInfo.getIntance().getUserData().getLimit_heart_up();
						warningDw = UserInfo.getIntance().getUserData().getLimit_heart_dw();
					}
					else if (dataType.equals(Program.TUMBLE_DATA)) {
						warningUp = 1;
						warningDw = 1;
					}
					
					Iterator<ValueEntry> it = dataList.iterator();
					while (it.hasNext()) {
						ValueEntry entry = it.next();
						out.writeLong(entry.timestamp);
						out.writeInt(entry.value);
						out.writeInt(warningDw);
						out.writeInt(warningUp);
					}
				}
				
				data = bos.toByteArray();
				bos.close();
			
				return data;
			}
		
			if (out != null) {
				if (!isWarningType(dataType)) {
					out.writeInt(Integer.MAX_VALUE);
					out.writeInt(Integer.MAX_VALUE);
					SimpleDateFormat sdf = new SimpleDateFormat(Program.SECOND_FORMAT);
					Date dateTime = sdf.parse(minuteData + "00");
					out.writeLong(dateTime.getTime());
				}
				
				Iterator<ValueEntry> it = dataList.iterator();
				while (it.hasNext()) {
					ValueEntry entry = it.next();
					out.writeInt(entry.value);
				}
				
				data = bos.toByteArray();
			}
		}
		catch (Exception e) {
			data = null;
		}
		finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					data = null;
					e.printStackTrace();
				}		
			}
		}	
		
		return data;
	}
	
	private boolean isWarningType(String dataType) {
		if (dataType == Program.TUMBLE_DATA) {
			return true;	
		}
		if (dataType == Program.HTE_WARNING_DATA) {
			return true;
		}
		
		return false;
	}
}
