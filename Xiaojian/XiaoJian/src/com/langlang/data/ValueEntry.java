package com.langlang.data;

public class ValueEntry {
	public long timestamp;
	public int value;
	public String ssString;
	
	public ValueEntry(long timestamp, int value) {
		this.timestamp = timestamp;
		this.value = value;
	}
	public ValueEntry(long timestamp, String ssString) {
		this.timestamp = timestamp;
		this.ssString = ssString;
	}
}
