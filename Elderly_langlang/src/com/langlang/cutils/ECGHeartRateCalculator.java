package com.langlang.cutils;

import com.langlang.data.StressLevelItem;

public class ECGHeartRateCalculator {
	private Object lock = new Object();
//	private long id;
	
	public ECGHeartRateCalculator() {
//		System.out.println("action ECGHeartRateCalculator start call initECG.");
//		id = FunctionNative.initECG();
//		System.out.println("action ECGHeartRateCalculator id=" + id);
	}
	
	public int setECGOFiles(String outFile,
			  String outDetailFile, String outFilteredFile,
			  String outAngleFile)
	{
		synchronized (lock) {
//		return FunctionNative.setECGOFiles(id, outFile, outDetailFile, outFilteredFile, outAngleFile);
		return FunctionWrapper.getInstance().setECGOFiles(outFile, outDetailFile, outFilteredFile, outAngleFile);
		}
	}
	
	public int pushECGData(long time, float voltage) {
		synchronized (lock) {
//			return FunctionNative.pushECGData(id, time, voltage);
			return FunctionWrapper.getInstance().pushECGData(time, voltage);
		}
	}
	
	public int setGender(char gender) {
		synchronized (lock) {
//		return FunctionNative.setGender(id, gender);
		return FunctionWrapper.getInstance().setGender(gender);
		}
	}
	
	public int setAge(short age) {
		synchronized (lock) {
//		return FunctionNative.setAge(id, age);
		return FunctionWrapper.getInstance().setAge(age);
		}
	}
	
	public int getECGHeartRate() {
		synchronized (lock) {
//		System.out.println("action getECGHeartRate id2=" + id);
//		return FunctionNative.getECGHeartRate(id);
		return FunctionWrapper.getInstance().getECGHeartRate();
		}
	}
	
	public int getHeartRateRealtime() {
		synchronized (lock) {
//		return FunctionNative.getHeartRateRealtime(id);
		return FunctionWrapper.getInstance().getHeartRateRealtime();
		}
	}
	
//	public int deInit() {
////		return FunctionNative.deInitECG(id);
//		return FunctionWrapper.getInstance().deInit();
//	}
	
	public int getStress() {
//		return FunctionNative.getStress(id);
		return FunctionWrapper.getInstance().getStress();
	}
	
	public float getStressA() {
//		return FunctionNative.getStressA(id);
		return FunctionWrapper.getInstance().getStressA();
	}
	
	public float getStressB1() {
//		return FunctionNative.getStressB1(id);
		return FunctionWrapper.getInstance().getStressB1();
	}
	
	public float getStressB2() {
//		return FunctionNative.getStressB2(id);
		return FunctionWrapper.getInstance().getStressB2();
	}
	
	public float getStressC1() {
//		return FunctionNative.getStressC1(id);
		return FunctionWrapper.getInstance().getStressC1();
	}
	
	public float getStressC2() {
//		return FunctionNative.getStressC2(id);
		return FunctionWrapper.getInstance().getStressC2();
	}
	
	public float getStressC3() {
//		return FunctionNative.getStressC3(id);
		return FunctionWrapper.getInstance().getStressC3();
	}
	
	public float getStressD() {
//		return FunctionNative.getStressD(id);
		return FunctionWrapper.getInstance().getStressD();
	}
	
	public float getStressE1() {
//		return FunctionNative.getStressE1(id);
		return FunctionWrapper.getInstance().getStressE1();
	}
	
	public float getStressE2() {
//		return FunctionNative.getStressE2(id);
		return FunctionWrapper.getInstance().getStressE2();
	}
	
	public float getLf() {
//		return 0.3f;
		return FunctionWrapper.getInstance().getLF();
	}
	
	public float getHf() {
//		return 0.7f;
		return FunctionWrapper.getInstance().getHF();
	}
	
	public boolean isECGCalculateDone() {
		synchronized (lock) {
			return FunctionWrapper.getInstance().analyseECGDone();
		}
	}
	
	public StressLevelItem getStressLevelItem() {
		synchronized (lock) {
			StressLevelItem item = new StressLevelItem();
			item.stress = FunctionWrapper.getInstance().getStress();
			item.stressA = FunctionWrapper.getInstance().getStressA();
			item.stressB1 = FunctionWrapper.getInstance().getStressB1();
			item.stressB2 = FunctionWrapper.getInstance().getStressB2();
			item.stressC1 = FunctionWrapper.getInstance().getStressC1();
			item.stressC2 = FunctionWrapper.getInstance().getStressC2();
			item.stressC3 = FunctionWrapper.getInstance().getStressC3();
			item.stressD = FunctionWrapper.getInstance().getStressD();
			item.stressE1 = FunctionWrapper.getInstance().getStressE1();
			item.stressE2 = FunctionWrapper.getInstance().getStressE2();
			
			return item;
		}
	}
}
