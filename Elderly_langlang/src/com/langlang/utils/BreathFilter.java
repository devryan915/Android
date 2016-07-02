package com.langlang.utils;

public class BreathFilter {
	private int[] data ;
	private int total;
	private int curr;
	
	/***
	private static int[] B = {
		      0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		      0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		      0,    0,    0,    1,    1,    1,    1,    1,    1,    1,    1,    1,
		      1,    1,    1,    2,    2,    2,    2,    2,    2,    2,    2,    2,
		      3,    3,    3,    3,    3,    3,    3,    3,    3,    3,    4,    4,
		      4,    4,    4,    4,    4,    4,    4,    4,    4,    4,    4,    4,
		      5,    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,
		      5,    5,    5,    5,    5,    4,    4,    4,    4,    4,    4,    4,
		      4,    4,    4,    4,    4,    4,    4,    3,    3,    3,    3,    3,
		      3,    3,    3,    3,    3,    2,    2,    2,    2,    2,    2,    2,
		      2,    2,    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
		      1,    1,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		      0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		      0,    0,    0,    0,    0
		};
	***/
	
	private static int[] B = {	
		 0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	      0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	      0,    0,    0,    0,    0,    1,    1,    1,    1,    1,    1,    1,
	      1,    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
	      2,    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,
	      2,    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,
	      2,    2,    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
	      3,    3,    3,    2,    2,    2,    2,    2,    2,    2,    2,    2,
	      2,    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,
	      2,    2,    2,    2,    2,    1,    1,    1,    1,    1,    1,    1,
	      1,    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
	      0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	      0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	      0,    0,    0,    0,    0
	};
	
	/*
	private static int[] B = {1,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0,   0,   0,    0,    0,   0,   0,   0,
		0,   0,    0,    0,   0
	};
	*/
	
	public BreathFilter() {
		data = new int[1024];
		curr = 0;
		total = 0;
	}
			
	public synchronized void reset() {
		curr = 0;
		total = 0;
	}
	
	public boolean canGetOne() {
		return (total > 160);
	}
	
	public synchronized void addData(int value) throws Exception {
		if (total > 1024) {
			throw new Exception("Filter error, too much data.");
		}
		
		data[curr] = value;
		curr++;
		
		total++;
	}
	
	public synchronized int getOne() throws Exception {
		if (total <= 160) {
			throw new Exception("Filter error, no data available.");
		}
		
		int sum;
		sum = 0;
		for (int i = 0; i < 161; i++) {
			sum += (data[i] * B[i]);
		}
		
		for (int i = 0; i < total - 1; i++) {
			data[i] = data[i + 1];
		}
		
		curr--;
		total--;
		
		return sum;
	}
}
