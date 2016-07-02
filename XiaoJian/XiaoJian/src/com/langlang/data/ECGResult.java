package com.langlang.data;

public class ECGResult {
	public int stress;
	public float LF;
	public float HF;
	
	public ECGResult(){}

	public ECGResult(int stress, float LF, float HF) {
		this.stress = stress;
		this.LF = LF;
		this.HF = HF;
	}
}
