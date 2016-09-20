package com.langlang.cutils;

public class AccelCalculator {
//	private long id;
	
	public AccelCalculator() {
//		id = FunctionNative.initAccel();
	}
	
	public int setAccelInitXYZ(float accelX, float accelY, float accelZ)
	{
//		return FunctionNative.setInitXYZ(id, accelX, accelY, accelZ);
		return FunctionWrapper.getInstance().setInitXYZ(accelX, accelY, accelZ);
	}
	
	public int pushAccelData(long time, float accelX, float accelY, float accelZ) {
//		return FunctionNative.pushAccelData(id, time, accelX, accelY, accelZ);
		return FunctionWrapper.getInstance().pushAccelData(time, accelX, accelY, accelZ);
	}
	
	public int pushDeltaSteps(long deltaTime, int deltaSteps, int totalSteps) {
		return FunctionWrapper.getInstance()
							.pushDeltaSteps(deltaTime, deltaSteps, totalSteps);
	}
	
	public long setFreeFalling() {
		return FunctionWrapper.getInstance().setFreeFalling();
	}
	
	public int getSteps() {
//		return FunctionNative.getSteps(id);
		return FunctionWrapper.getInstance().getSteps();
	}
	
	public int getCalories() {
		return FunctionWrapper.getInstance().getCalories();
	}
	
	public int getGait()
	{
//		return FunctionNative.getGait(id);
		return FunctionWrapper.getInstance().getGait();
	}
	
	public int getPosture()
	{
//		return FunctionNative.getPosture(id);
		return FunctionWrapper.getInstance().getPosture();
	}
	
	public boolean isAccelCalculateDone() {
		return FunctionWrapper.getInstance().analyseAccelDone();
	}
	
	public int setHeight(short height) {
		return FunctionWrapper.getInstance().setHeight(height);
	}
	
	public int setWeight(short weight) {
		return FunctionWrapper.getInstance().setWeight(weight);
	}
	
//	public int reset() {
////		return FunctionNative.resetAccel(id);
//		return FunctionWrapper.getInstance().reset();
//	}
//	
//	public int deinit() {
////		return FunctionNative.deinitAccel(id);
//		return FunctionWrapper.getInstance().deInit();
//	}
}
