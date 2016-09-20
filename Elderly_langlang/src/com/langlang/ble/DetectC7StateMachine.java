package com.langlang.ble;

public class DetectC7StateMachine {
	
	private static final int S_UNKNOWN = -1;
	private static final int S_START = 0;
	private static final int S_C7_0 = 1;
	private static final int S_C7_1 = 2;
	private static final int S_NONE_C7 = 3;
	
	private int mCountSkipped = 0;	
	private static final int COUNT_SKIP = 25;
	private int mSkip = 0;
	
	private int status = S_UNKNOWN;
	
	public DetectC7StateMachine() {
		mSkip = COUNT_SKIP;
		reset();
	}
	
	public DetectC7StateMachine(int skip) {
		mSkip = skip;
		reset();
	}
	
	public void reset() {
		mCountSkipped = 0;
		status = S_START;
	}	
	
	public void consume(boolean isC7) {
		if (status == S_START) {
			if (isC7) {
				transTo(S_C7_0);
			} else {
				transTo(S_NONE_C7);
			}
			return;
		}
		
		if (status == S_C7_0) {
			if (isC7) {
				transTo(S_C7_1);
			} else {
				transTo(S_NONE_C7);
			}
			return;
		}
		
		if (status == S_C7_1) {
			mCountSkipped++;
			
			if (mCountSkipped > mSkip) {
				transTo(S_START);
			}
			return;
		}		
		
		if (status == S_NONE_C7) {
			if (isC7) {
				transTo(S_C7_0);
			}
			return;
		}
	};
	
	public boolean isInMaskStatus() {
		if (status == S_NONE_C7) return false;
		if (status == S_START) return false;
		
		return true;
	}
	
	private void transTo(int newStatus) {
		status = newStatus;
		
		if (status == S_START) {
			mCountSkipped = 0; 
		}
	}
}
