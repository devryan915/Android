package com.langlang.data;

import java.util.Date;

public class SleepEvent {
	public final static int FALL_ASLEEP = 0;
	public final static int ROLL_OVER = 1;
	public final static int WAKEUP = 2;
	
	public Date when;
	public int event;
}
