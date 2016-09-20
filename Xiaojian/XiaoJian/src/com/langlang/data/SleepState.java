package com.langlang.data;

import java.util.Date;

public class SleepState {
	public Date startDate;
	public int duaration;
	public int state;
	
	public SleepState(Date date, int duaration, int state) {
		this.startDate = date;
		this.duaration = duaration;
		this.state = state;
	}

	@Override
	public String toString() {
		return "SleepState [startDate=" + startDate + ", duaration="
				+ duaration + ", state=" + state + "]";
	}
}
