package com.langlang.cutils;

public class FunctionWrapper {
	public static final int BOOL_TRUE = 1;
	
	public static int  calculateECG(String inFile,
			char gender,
			short age,
			String outFile, String outFileDetail,
			String outFilteredFile, String outAngleFile)
	{
		return 0;
//		return FunctionNative.calculateECG(inFile, 
//										   gender, 
//										   age, 
//										   outFile, 
//				outFileDetail, outFilteredFile, outAngleFile);
	}
	
	public static int calculateResp(
											String inFile, 
											String outFile, 
											String outFileDetail, 
											String outFilteredFile,  
											String outAngleFile)
	{
		return 0;
//		return FunctionNative.calculateResp(inFile, outFile, 
//											outFileDetail, outFilteredFile, outAngleFile);
	}
	
	private static FunctionWrapper obj = new FunctionWrapper();
	public static FunctionWrapper getInstance() {
		return obj;
	}
	private long id;
	
	private FunctionWrapper() {
		id = FunctionNative.init((char) 2);
		if (id == 0) {
			throw new RuntimeException("Got a zero when calling init.");
		}
	}
	
	public int getVersion() {
		return FunctionNative.getVersion();
	}
	
	public int setGender(char gender) {
//		gender = (char) 1;
		return FunctionNative.setGender(id, gender);
	}
	
	public int setAge(short age) {
//		age = 30;
		return FunctionNative.setAge(id, age);	
	}
	
	public int setHeight(short height) {
		return FunctionNative.setHeight(id, height);
	}
	
	public int setWeight(short weight) {
		return FunctionNative.setWeight(id, weight);
	}
	
	public long setSensorPosition(char position) {
		return FunctionNative.setSensorPosition(id, position);
	}
	
	public int setInFilesPath(String inFilesPath) {
		return FunctionNative.setInFilesPath(id, inFilesPath);
	}
	
	public int setOFiles(String outFile, String outDetailFile, String outFilteredFile) {
		return FunctionNative.setOFiles(id, outFile, outDetailFile, outFilteredFile);
	}
	
	public int setECGOFiles(String outFile, String outDetailFile, 
										String outFilteredFile, String outAngleFile) {
		return FunctionNative.setECGOFiles(id, outFile, outDetailFile, outFilteredFile, outAngleFile);
	}
	
	public int pushECGData(long time, float voltage) {
		return FunctionNative.pushECGData(id, time, voltage);			
	}
	
	public int pushDeltaSteps(long deltaTime, int deltaSteps, int totalSteps) {
		return FunctionNative.pushDeltaSteps(id, deltaTime, deltaSteps, totalSteps);
	}
	
	public int getECGHeartRate() {
		return FunctionNative.getECGHeartRate(id);
	}

	public int getHeartRateRealtime() {
		return FunctionNative.getHeartRateRealtime(id);
	}
	
	public int getStress() {
		return FunctionNative.getStress(id);
	}
	
	public float getStressA() {
		return FunctionNative.getStressA(id);
	}
	
	public float getStressB1() {
		return FunctionNative.getStressB1(id);
	}
	
	public float getStressB2() {
		return FunctionNative.getStressB2(id);
	}
	
	public float getStressC1() {
		return FunctionNative.getStressC1(id);
	}
	
	public float getStressC2() {
		float stressC2 = FunctionNative.getStressC2(id);
//		return FunctionNative.getStressC2(id);
		System.out.println("action FunctionWrapper getStressC2=[" + stressC2 + "]");
		
		return stressC2;
	}
	
	public float getStressC3() {
		return FunctionNative.getStressC3(id);
	}
	
	public float getStressD() {
		return FunctionNative.getStressD(id);
	}
	
	public float getStressE1() {
		return FunctionNative.getStressE1(id);
	}
	
	public float getStressE2() {
		return FunctionNative.getStressE2(id);
	}

	public int setRespOFiles(String outFile,
			String outDetailFile, String outFilteredFile,
			String outAngleFile) {
		return FunctionNative.setRespOFiles(id, outFile, outDetailFile, outFilteredFile, outAngleFile);
	}
	
	public int pushRespData(long time, float voltage) {
		return FunctionNative.pushRespData(id, time, voltage);
	}
	
	public int getRespRate() {
		return FunctionNative.getRespRate(id);
	}
	
	public int setInitXYZ(float accel_x, float accel_y, float accel_z) {
		return FunctionNative.setInitXYZ(id, accel_x, accel_y, accel_z);
	}
	
	public long setFreeFalling() {
		return FunctionNative.setFreeFalling(id);
	}
	
	public int pushAccelData(long time,
			  float accelX, float accelY, float accelZ) {
		return FunctionNative.pushAccelData(id, time, accelX, accelY, accelZ);
	}
	
	public int getSteps() {
		return FunctionNative.getSteps(id);
	}
	
	public int getGait() {
		return FunctionNative.getGait(id);
	}
	
	public int getPosture() {
		return FunctionNative.getPosture(id);
	}
	
	public int getCalories() {
		return FunctionNative.getCalories(id);
	}
	
	public int getLF() {
		return FunctionNative.getLF(id);
	}
	
	public int getHF() {
		return FunctionNative.getHF(id);		
	}
	
	public int reset() {
		return FunctionNative.reset(id);
	}
	
	public int deInit() {
		return FunctionNative.deInit(id);
	}
	
//	public int analyseDone() {
//		return FunctionNative.analyseDone(id);
//	}
	
	public boolean analyseECGDone() {
		return (FunctionNative.analyseECGDone(id) == BOOL_TRUE);			
	}
	
	public int analyseRespDone() {
		return FunctionNative.analyseRespDone(id);
	}
	
	public boolean analyseAccelDone() {
		return (FunctionNative.analyseAccelDone(id) == BOOL_TRUE);
	}
}
