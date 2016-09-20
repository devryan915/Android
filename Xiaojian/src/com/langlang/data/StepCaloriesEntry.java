package com.langlang.data;

public class StepCaloriesEntry {
	public long timestamp;
	public int stepCount;
	public int calories;
	
	public StepCaloriesEntry() {}
	
	public StepCaloriesEntry(long timestamp, int stepCount, int calories) {
		this.timestamp = timestamp;
		this.stepCount = stepCount;
		this.calories = calories;
	}
}
