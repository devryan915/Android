package com.langlang.cutils;

public class GlobalAccelCalculator {	
	private static GlobalAccelCalculator obj = new GlobalAccelCalculator();
	public static GlobalAccelCalculator getInstance() { return obj; }
	private GlobalAccelCalculator() {
		status = S_NOT_INITIAL;
	}
	
	private AccelCalculator accelCalculator = new AccelCalculator();
	
	private static final int S_NOT_INITIAL = 0;
	private static final int S_INITIALIZED = 1;
	
	public static final int RET_OK = 0;
	public static final int RET_ERROR = -1;
	
	private static int status = S_NOT_INITIAL;
	
	public int setAccelInitXYZ(float accelX, float accelY, float accelZ) {
		synchronized (accelCalculator) {
			int ret = accelCalculator.setAccelInitXYZ(accelX, accelY, accelZ);
//			if (ret == RET_OK) {
//				status = S_INITIALIZED;
//			}
			return ret;
		}
	}
	
	public int pushAccelData(long time, float accelX, float accelY, float accelZ) {
		synchronized (accelCalculator) {
//			if (status == S_NOT_INITIAL) {
//				return RET_OK;
//			}
			return accelCalculator.pushAccelData(time, accelX, accelY, accelZ);
		}
	}
	
	public int pushDeltaSteps(long deltaTime, int deltaSteps, int totalSteps) {
		synchronized (accelCalculator) {
//			if (status == S_NOT_INITIAL) {
//				return RET_OK;
//			}
			return accelCalculator
						.pushDeltaSteps(deltaTime, deltaSteps, totalSteps);
		}
	}
	
	public int getSteps() {
		synchronized (accelCalculator) {
//			if (status == S_NOT_INITIAL) {
//				return 0;
//			}
			return accelCalculator.getSteps();
		}
	}
	
	public int getCalories() {
		synchronized (accelCalculator) {
//			if (status == S_NOT_INITIAL) {
//				return 0;
//			}
			return accelCalculator.getCalories();
		}
	}
	
	public int getGait()
	{
		synchronized (accelCalculator) {
//			if (status == S_NOT_INITIAL) {
//				return 0;
//			}
			return accelCalculator.getGait();
		}
	}
	
	public int getPosture()
	{
		synchronized (accelCalculator) {
//			if (status == S_NOT_INITIAL) {
//				return RET_ERROR;
//			}
			return accelCalculator.getPosture();
		}
	}
	
	public boolean isAccelCalculateDone() {
		synchronized (accelCalculator) {
			return accelCalculator.isAccelCalculateDone();
		}
	}
	
	public long setFreeFalling() {
		synchronized (accelCalculator) {
			return accelCalculator.setFreeFalling();
		}		
	}
	
	public int setHeight(short height) {
		synchronized (accelCalculator) {
			return FunctionWrapper.getInstance().setHeight(height);
		}
	}
	
	public int setWeight(short weight) {
		synchronized (accelCalculator) {
			return FunctionWrapper.getInstance().setWeight(weight);
		}
	}
	
	public int reset() {
//		synchronized (accelCalculator) {
//			status = S_NOT_INITIAL;
//			return accelCalculator.reset();
//		}
		return 0;
	}
	
	public int deinit() {
//		synchronized (accelCalculator) {
//			status = S_NOT_INITIAL;
//			return accelCalculator.deinit();
//		}
		return 0;
	}
}
