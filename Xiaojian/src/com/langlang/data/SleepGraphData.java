package com.langlang.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SleepGraphData {
	public static int LFHF_DATA_COUNT = 24 * 60;
	public static int SLEEP_QUALITY_COUNT = 24;
	public static int RESP_DATA_COUNT = 24 * 4;
	
	public static int SECONDS_PER_DAY = 24 * 60 * 60;
	
	public float[] lfhfData;
	public int[] sleepQualities;
	public int[] resps;
	
	public List<SleepState> sleepStateList;
	public List<SleepEvent> sleepEventList;
	
	public boolean hasData = false;
	
	public SleepGraphData() {
		lfhfData = new float[LFHF_DATA_COUNT];
		sleepQualities = new int[SLEEP_QUALITY_COUNT];
		resps = new int[RESP_DATA_COUNT];
		
		sleepStateList = new ArrayList<SleepState>();
		sleepEventList = new ArrayList<SleepEvent>();
		
//		init(); //用于调试
	}
	
	public void update() {
		hasData = true;
	}
	
	private void init() {
		for (int i = 0; i < LFHF_DATA_COUNT; i++) {
			lfhfData[i] = 1.0f;
		}
		for (int i = 0; i < SLEEP_QUALITY_COUNT; i++) {
			sleepQualities[i] = i % 3 + 1;
		}
		for (int i = 0; i < RESP_DATA_COUNT; i++) {
			resps[i] = i % 20;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		Date fallAsleep = new Date();
		Date rollOver = new Date();
		Date rollOver2 = new Date();
		Date wakeUp = new Date();
		
		try {
			date = sdf.parse("09/29/2014 09:14:20");
			fallAsleep = sdf.parse("09/29/2014 23:14:20");
			rollOver = sdf.parse("09/30/2014 04:14:20");
			rollOver2 = sdf.parse("09/30/2014 05:14:20");
			wakeUp = sdf.parse("09/30/2014 06:14:20");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < 12; i++) {
			Date curr = new Date();
			curr.setTime(date.getTime() + 1000 * 60 * 60 * i);
			SleepState sleepState = new SleepState(curr, 1000 * 60 * 30, i % 3);
			sleepStateList.add(sleepState);
		}
		
		SleepEvent fallAsleepEvent = new SleepEvent();
		fallAsleepEvent.when = fallAsleep;
		fallAsleepEvent.event = SleepEvent.FALL_ASLEEP; 
		sleepEventList.add(fallAsleepEvent);
	
		SleepEvent rollOverEvent = new SleepEvent();
		rollOverEvent.when = rollOver;
		rollOverEvent.event = SleepEvent.ROLL_OVER; 
		sleepEventList.add(rollOverEvent);
		
		SleepEvent rollOver2Event = new SleepEvent();
		rollOver2Event.when = rollOver2;
		rollOver2Event.event = SleepEvent.ROLL_OVER; 
		sleepEventList.add(rollOver2Event);
		
		SleepEvent wakeUpEvent = new SleepEvent();
		wakeUpEvent.when = wakeUp;
		wakeUpEvent.event = SleepEvent.WAKEUP; 
		sleepEventList.add(wakeUpEvent);
		
		hasData = true;
	}
}
