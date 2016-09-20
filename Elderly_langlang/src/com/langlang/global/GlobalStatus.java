package com.langlang.global;

import com.langlang.data.StressLevelItem;
import com.langlang.data.StressLevelReport;
import com.langlang.service.BleConnectionService;

public class GlobalStatus {
	public static final int MAX_ITEM_IN_STRESS_REPORT = 6;
	public static final int COLUMN_OF_STRESS_REPORT = 5;
	
	private static GlobalStatus obj = new GlobalStatus();
	private GlobalStatus() {
		report = new StressLevelReport(MAX_ITEM_IN_STRESS_REPORT);
	}
	public static GlobalStatus getInstance() {
		return obj;
	}
	
	private int heartRate = 0;
	private int stressLevel = 0;
	private int breath = 0;
	private byte currentStatus = 0x00;
	private int temperature = 0;
	private int currentStep = 0;
//	private float colaries = 0.0f;
	private int calories = 0;
	private float voltage = 0.0f;	
	private String temperatures = "";
	private StressLevelReport report;
	private float temp=0.0f;
	private int posture;
	private int gait;
	private long standStillTime = 0;
	private int rssi = 0;

	private int lastStepCount = 0;
	private int lastCalories = 0;
	
	private Object lockECGMode = new Object();
	private int currentECGMode = 0;
	
	private int bleState = BleConnectionService.STATE_DISCONNECTED;
	
	public int getHeartRate() {
		return heartRate;
	}
	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}
	public int getStressLevel() {
		return stressLevel;
	}
	public void setStressLevel(int stressLevel) {
		this.stressLevel = stressLevel;
	}
	public int getBreath() {
		return breath;
	}
	public void setBreath(int breath) {
		this.breath = breath;
	}
	public byte getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(byte currentStatus) {
		this.currentStatus = currentStatus;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public int getCurrentStep() {
		return currentStep;
	}
	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	public int getCalories() {
		return calories;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}

	public String getTemperatures() {
		return temperatures;
	}
	public void setTemperatures(String temperatures) {
		this.temperatures = temperatures;
	}

	public float getTemp() {
		return temp;
	}
	public void setTemp(float temp) {
		this.temp = temp;
	}

	public float getVoltage() {
		return voltage;
	}
	public void setVoltage(float voltage) {
		this.voltage = voltage;
	}
	
	public void appendStressLevelItem(StressLevelItem item) {
		report.appendData(item);
	}	
	public StressLevelItem[] getStressLevelReport() {
		return report.getStressLevelReport();
	}	
	
	public int getPosture() {
		return posture;
	}
	public void setPosture(int posture) {
		this.posture = posture;
	}
	public int getGait() {
		return gait;
	}
	public void setGait(int gait) {
		this.gait = gait;
	}	
	public long getStandStillTime() {
		return standStillTime;
	}
	public void setStandStillTime(long standStillTime) {
		this.standStillTime = standStillTime;
	}
	
	public void reset() {
		this.voltage = 0;
		setBleState(BleConnectionService.STATE_DISCONNECTED);
		setRssi(0);
		setCurrentStep(0);
		setCalories(0);
		setGait(0);
		setPosture(0);
		setStressLevel(0);
	}
	
	public void setBleState(int newState) {
		this.bleState = newState;
	}
	
	public int getBleState() {
		return this.bleState;
	}
	
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	
	public int getRssi() {
		return this.rssi;
	}
	
	public int getLastStepCount() {
		return lastStepCount;
	}
	public void setLastStepCount(int lastStepCount) {
		this.lastStepCount = lastStepCount;
	}
	public int getLastCalories() {
		return lastCalories;
	}
	public void setLastCalories(int lastCalories) {
		this.lastCalories = lastCalories;
	}
	
	public int getCurrentECGMode() {
		synchronized (lockECGMode) {
			return currentECGMode;
		}
	}
	public void setCurrentECGMode(int currentECGMode) {
		synchronized (lockECGMode) {
			this.currentECGMode = currentECGMode;
		}
	}
}
