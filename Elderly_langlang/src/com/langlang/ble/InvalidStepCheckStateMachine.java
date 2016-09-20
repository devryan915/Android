package com.langlang.ble;

public class InvalidStepCheckStateMachine {
	public static final int S_UNKNOWN = -1;
	public static final int S_START = 0;
	public static final int S_FIRST_INVALID = 1;
	public static final int S_INVALID = 2;
	public static final int S_FIRST_VALID = 3;
	public static final int S_VALID = 4;
	
	private int state = S_UNKNOWN;
	
	private int subtraction = 0;
	private int prevStep = 0;
	
	private int lastInvalidStep = 0;
	
	public InvalidStepCheckStateMachine() {
		reset();
	}
	
	public void reset() {
		state = S_START;
		subtraction = 0;
		prevStep = 0;
		lastInvalidStep = 0;
	}
	
	public void consume(boolean isValid, int step) {
		if (!isValid) {
			lastInvalidStep = step;
		}
		
		if (state == S_START) {
			if (isValid) {
				transTo(S_VALID, step);
			}
			else {
				transTo(S_FIRST_INVALID, step);
			}
			return;
		}
		
		if (state == S_FIRST_INVALID) {
			if (isValid) {
				transTo(S_FIRST_VALID, step);
			}
			else {
				transTo(S_INVALID, step);
			}
			return;
		}
		
		if (state == S_INVALID) {
			if (isValid) {
				transTo(S_FIRST_VALID, step);
			}
			else {
				// do nothing, still stay in S_INVALID state
			}
			return;
		}

		if (state == S_FIRST_VALID) {
			if (isValid) {
				transTo(S_VALID, step);
			}
			else {
				transTo(S_FIRST_INVALID, step);
			}
			return;
		}

		if (state == S_VALID) {
			if (isValid) {
				transTo(S_VALID, step);
			}
			else {
				transTo(S_FIRST_INVALID, step);
			}
			return;
		}		
	}
	
	public int getCurrentState() {
		return state;
	}
	
	public int getSubtractionStep() {
		return subtraction;
	}
	
	private void transTo(int newState, int step) {
		state = newState;
		
		if (state == S_FIRST_INVALID) {
			prevStep = step;
		}
		
		if (state == S_FIRST_VALID) {
			subtraction = step - prevStep;
		}
	}
	
	public int getSubInvalidStep() {
		return lastInvalidStep - prevStep;
	}
}
