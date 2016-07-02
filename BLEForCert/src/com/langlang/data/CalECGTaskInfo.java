package com.langlang.data;

public class CalECGTaskInfo {
	public int id;
	public String minuteData;
	public int status;
	
	public static final int CAL_STATE_NEW = 0;
	public static final int CAL_STATE_SUCCESS = 1;
	public static final int CAL_STATE_ERROR = 2;
	
	public CalECGTaskInfo(int id, String minuteData, int status) {
		this.id = id;
		this.minuteData = minuteData;
		this.status = status;
	}	
}
