package com.langlang.cutils;

public class FunctionNative {
	static {
		/*
		System.loadLibrary("MyCUtils");
		*/
		System.loadLibrary("LangLangCUtils");
	}
	
	public static native int getVersion();	
	public static native long init(char mode);
	public static native int setGender(long id, char gender);
	public static native int setAge(long id, short age);
	public static native int setHeight(long id, short height);
	public static native int setWeight(long id, short weight);
	public static native long setSensorPosition(long id, char position);
	public static native int setInFilesPath(long id, String inFilesPath);
	public static native int setOFiles(long id, String outFile, String outDetailFile, String outFilteredFile);
	public static native int setECGOFiles(long id, String outFile, String outDetailFile, 
												String outFilteredFile, String outAngleFile);
	public static native int pushECGData(long id, long time, float voltage);
	public static native int getECGHeartRate(long id);
	public static native int getHeartRateRealtime(long id);
	public static native int getStress(long id);
	public static native float getStressA(long id);
	public static native float getStressB1(long id);
	public static native float getStressB2(long id);
	public static native float getStressC1(long id);
	public static native float getStressC2(long id);
	public static native float getStressC3(long id);
	public static native float getStressD(long id);
	public static native float getStressE1(long id);
	public static native float getStressE2(long id);
	
//	public static native int  calculateECG(String inFile,
//			char gender, short age,
//			String outFile, String outFileDetail,
//			String outFilteredFile, String outAngleFile);
//	public static native long initECG();	
//	public static native int deInitECG(long id);
//	public static native int calculateResp(
//			String inFile, 
//			String outFile, 
//			String outFileDetail, 
//			String outFilteredFile, 
//			String outAngleFile);
//	public static native int initResp();
	
	public static native int setRespOFiles(long id, String outFile,
			String outDetailFile, String outFilteredFile,
			String outAngleFile);
	public static native int pushRespData(long id, long time, float voltage);
	public static native int getRespRate(long id);
//	public static native int deInitResp(long id);
	
	
//	public static native long initAccel();
	public static native int setInitXYZ(long id, float accel_x, float accel_y, float accel_z);
	public static native long setFreeFalling(long id);	
	public static native int pushAccelData(long id, long time,
									  float accelX, float accelY, float accelZ);
	public static native int getSteps(long id);
	public static native int getGait(long id);
	public static native int getPosture(long id);
	public static native int pushDeltaSteps(long id, long deltaTime, int deltaSteps, int totalSteps);
	
	public static native int getCalories(long id);
	
	public static native int getLF(long id);
	public static native int getHF(long id);
	
	public static native int reset(long id);
	public static native int deInit(long id);
	
//	public static native int resetAccel(long id);
//	public static native int deinitAccel(long id);
	public static native int analyseECGDone(long id);
	public static native int analyseRespDone(long id);
	public static native int analyseAccelDone(long id);
}
