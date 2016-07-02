package com.langlang.data;

import java.util.LinkedList;
import java.util.Queue;

public class StressLevelReport {
	private Object lock = new Object();
	private int itemCount = 0;
	
	private Queue<Integer> stress = new LinkedList<Integer>();
	private Queue<Float> stressA = new LinkedList<Float>();
	private Queue<Float> stressB1 = new LinkedList<Float>();
	private Queue<Float> stressB2 = new LinkedList<Float>();
	private Queue<Float> stressC1 = new LinkedList<Float>();
	private Queue<Float> stressC2 = new LinkedList<Float>();
	private Queue<Float> stressC3 = new LinkedList<Float>();
	private Queue<Float> stressD = new LinkedList<Float>();
	private Queue<Float> stressE1 = new LinkedList<Float>();
	private Queue<Float> stressE2 = new LinkedList<Float>();
	
	public StressLevelReport(int itemCount) {
		if (itemCount > 0) {
			this.itemCount = itemCount;
		}
	}
	
	private void reset() {
		stress.clear();
		stressA.clear();
		stressB1.clear();
		stressB2.clear();
		stressC1.clear();
		stressC2.clear();
		stressC3.clear();
		stressD.clear();
		stressE1.clear();
		stressE2.clear();
	}
	
	public void appendData(StressLevelItem item) {
		synchronized (lock) {
			if (itemCount > 0) {
				int qLen = checkQueues();
				
				if (qLen < itemCount) {					
				} else if (qLen > itemCount) {
					// Some errors must happen.
					reset();
				} else {
					stress.poll();
					stressA.poll();
					stressB1.poll();
					stressB2.poll();
					stressC1.poll();
					stressC2.poll();
					stressC3.poll();
					stressD.poll();
					stressE1.poll();
					stressE2.poll();
				}
				stress.offer(item.stress);
				stressA.offer(item.stressA);
				stressB1.offer(item.stressB1);
				stressB2.offer(item.stressB2);
				stressC1.offer(item.stressC1);
				stressC2.offer(item.stressC2);
				stressC3.offer(item.stressC3);
				stressD.offer(item.stressD);
				stressE1.offer(item.stressE1);
				stressE2.offer(item.stressE2);
			}
		}
	}
	
	public StressLevelItem[] getStressLevelReport() {
		synchronized (lock) {
		int qLen = checkQueues();
		if (qLen == 0) return null;
		
		StressLevelItem[] report = new StressLevelItem[qLen];
		for (int j = 0; j < qLen; j++) {
			report[j] = new StressLevelItem();
		}
		int i;
		
		i = 0;
		for (Integer sts: stress) {
			report[i].stress = sts;
			i++;
		}		
		
		i = 0;
		for (Float stsA: stressA) {
			report[i].stressA = stsA;
			i++;
		}
		
		i = 0;
		for (Float stsB1: stressB1) {
			report[i].stressB1 = stsB1;
			i++;
		}
		
		i = 0;
		for (Float stsB2: stressB2) {
			report[i].stressB2 = stsB2;
			i++;
		}
		
		i = 0;
		for (Float stsC1: stressC1) {
			report[i].stressC1 = stsC1;
			i++;
		}
		
		i = 0;
		for (Float stsC2: stressC2) {
			report[i].stressC2 = stsC2;
			i++;
		}
		
		i = 0;
		for (Float stsC3: stressC3) {
			report[i].stressC3 = stsC3;
			i++;
		}
		
		i = 0;
		for (Float stsD: stressD) {
			report[i].stressD = stsD;
			i++;
		}
		
		i = 0;
		for (Float stsE1: stressE1) {
			report[i].stressE1 = stsE1;
			i++;
		}
		
		i = 0;
		for (Float stsE2: stressE2) {
			report[i].stressE2 = stsE2;
			i++;
		}
		
		return report;
		}
	}

	private int checkQueues() {
		int qLen = stress.size();
		if (stressA.size() != qLen || stressB1.size() != qLen 
				|| stressB2.size() != qLen || stressC1.size() != qLen
				|| stressC2.size() != qLen || stressC3.size() != qLen
				|| stressD.size() != qLen || stressE1.size() != qLen
				|| stressE2.size() != qLen) {
			System.out.println("action StressLevelReport checkQueues error");
			reset();
			return 0;
		}

		return qLen;
	}
}
