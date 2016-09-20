package com.langlang.ble;


public class FrameStateMachine {
	byte[] buffer = new byte[60];
	
	public static final int S_NOT_STARTED = 0;
	public static final int S_STARTED = 1000;
	public static final int S_FRAME_READY = 2000;
	
	public static final int S_5A = 1;
	public static final int S_5A_2ND = 2;
	
	public static final int S_INNER_NOT_5A = 1001;
	public static final int S_INNER_5A = 1002;
	public static final int S_INNER_5A_2ND = 1003;
	
	private int status = S_NOT_STARTED;
	private int curr = 0;
	private boolean isFrameReady = false;
	
	public static final int END_UNKNOWN = -1;
	public static final int END_TO_STARTED = 0;
	public static final int END_TO_NOT_STARTED = 1;
	
	private int endStatus = END_UNKNOWN;
	
	public FrameStateMachine() {
		reset();
	}
	
	public void reset() {
		setStatus(S_NOT_STARTED);
		curr = 0;
		endStatus = END_UNKNOWN;
	}
	
	public void consume(byte b) {
		if (status == S_NOT_STARTED) {
			if (b == 0x5A) {
				setStatus(S_5A);
			} else {
				// do not launch the state machine.
			}
			
			return;
		}
		
		if (status == S_5A) {
			if (b == 0x5A) {
				setStatus(S_5A_2ND);
			} else {
				setStatus(S_NOT_STARTED);
			}
			
			return;
		}
		
		if (status == S_5A_2ND) {
			if (b == 0x12) {
				setStatus(S_STARTED);
			} else if (b == 0x5A) {
				// stay in status S_5A_2ND
			} else {
				setStatus(S_NOT_STARTED);
			}
			
			return;
		}
		
		if (status == S_STARTED) {
			accept(b);
			if (isFrameSize()) {
				setEndStatus(END_TO_NOT_STARTED);
				return;
			}
			
			if (b == 0x5A) {
				setStatus(S_INNER_5A);
			}
			else {
				setStatus(S_INNER_NOT_5A);
			}
			
			return;
		}
		
		if (status == S_INNER_NOT_5A) {
			accept(b);			
			if (isFrameSize()) {
				setEndStatus(END_TO_NOT_STARTED);
				return;
			}
			
			if (b == 0x5A) {
				setStatus(S_INNER_5A);
			}
			
			return;
		}
		
		if (status == S_INNER_5A) {
			accept(b);			
			if (isFrameSize()) {
				setEndStatus(END_TO_NOT_STARTED);
				return;
			}
			
			if (b == 0x5A) {
				setStatus(S_INNER_5A_2ND);
			} else {
				setStatus(S_INNER_NOT_5A);
			}
			
			return;
		}
		
		if (status == S_INNER_5A_2ND) {
			if (b == 0x12) {
				curr = curr - 2;
				setEndStatus(END_TO_STARTED);
				return;
				
			} else {
				accept(b);
				if (isFrameSize()) {
					setEndStatus(END_TO_NOT_STARTED);
					return;
				}
				
				if (b == 0x5A) {
					// do not move
				} else {
					setStatus(S_INNER_NOT_5A);
				}
			}
			
			return;
		}
	}
	
	private void setStatus(int newStatus) {
		status = newStatus;
		
		if (status == S_NOT_STARTED) {
			onNotStarted();
		}
		
		if (status == S_STARTED) {
			onStarted();
		}
	}
	
	public byte[] getFrame() {
		byte[] frame;
		
		if (!isFrameReady()) {
			return null;
		} else {			
			frame = new byte[curr];
			for (int i = 0; i < curr; i++) {
				frame[i] = buffer[i];
			}
			
			if (endStatus == END_TO_STARTED) {
				setStatus(S_STARTED);
			}
			if (endStatus == END_TO_NOT_STARTED) {
				setStatus(S_NOT_STARTED);
			}
		}
		
		return frame;
	}
	
	public boolean isFrameReady() {
		return isFrameReady;
	}
	
	private void setFrameReady() {
		isFrameReady = true;
	}
	
	private void onStarted() {
		curr = 0;
		isFrameReady = false;		
	}
	
	private void onNotStarted() {
		curr = 0;
		isFrameReady = false;
	}
	
	private void accept(byte b) {
		buffer[curr] = b;
		curr++;
	}
	
	private void setEndStatus(int status) {
		endStatus = status;
		setFrameReady();
	}
	
	private boolean isFrameSize() {
		return (curr > 16);
	}
} 
