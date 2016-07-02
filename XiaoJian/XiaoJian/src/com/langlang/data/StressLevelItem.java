package com.langlang.data;

public class StressLevelItem {
	public int stress;
	public float stressA;
	public float stressB1;
	public float stressB2;
	public float stressC1;
	public float stressC2;
	public float stressC3;
	public float stressD;
	public float stressE1;
	public float stressE2;
	
	@Override
	public String toString() {
		return 	"[" + stress + ","+ stressA + "," + stressB1 + "," + stressB2 + "," + stressC1
				+ stressC2 + "," + stressC3 + "," + stressD + "," + stressE1
				+ stressE2 + "]";
	}
}
