package com.langlang.cutils;

public class RespCalculator {
//	private long id;
	
	public RespCalculator() {
//		id = FunctionNative.initResp();
	}
	
	public int pushRespData(long time, float voltage) {
//		return FunctionNative.pushRespData(id, time, voltage);
		return FunctionWrapper.getInstance().pushRespData(time, voltage);
	}
	
	public int getRespRate() {
//		return FunctionNative.getRespRate(id);
		return FunctionWrapper.getInstance().getRespRate();
	}

	public int deInitResp() {
//		return FunctionNative.deInitResp(id);
		return 0;
	}
}
