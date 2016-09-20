package com.langlang.ble;

public class FrameAnalyzer {
	private byte[] bufferedData = new byte[24];
	private int bufferedByte = 0;
	
	private static final int STATUS_START = 0;
	private static final int STATUS_FIRST_5A = 1;
	private static final int STATUS_SECOND_5A = 2;
	private static final int STATUS_12 = 3;
	
	private int status = STATUS_START;
	
	public FrameAnalyzer() {
		reset();
	}
	
	public void reset() {
		clearStatus();
	}
	
	public void consume(byte b) {
		if (status == STATUS_START) {
			if (b == 0x5A) {
				accept(b);
				status = STATUS_FIRST_5A;
			} else {
				// skip the input
			}
			return;
		}
		
		if (status == STATUS_FIRST_5A) {
			if (b == 0x5A) {
				accept(b);
				status = STATUS_SECOND_5A;
			} else {
				// input error
				clearStatus();
			}
			return;
		}
		
		if (status == STATUS_SECOND_5A) {
			if (b == 0x12) {
				accept(b);
				status = STATUS_12;
			} else {
				clearStatus();
			}			
			return;
		}
		
		if (status == STATUS_12) {
			accept(b);
			if (bufferedByte > 20) {
				clearStatus();
			}
			return;
		}
	}	
	
	private void clearStatus() {
		bufferedByte = 0;
		status = STATUS_START;
	}

	private void accept(byte b) {
		bufferedData[bufferedByte] = b;
		bufferedByte++;
	}

	public boolean canGetOneFrame() {
		return (bufferedByte == 20 
				&& bufferedData[0] == 0x5A 
				&& bufferedData[1] == 0x5A
				&& bufferedData[2] == 0x12);
	}
	
	public byte[] getOneFrame() {
		byte[] frame = new byte[20];
		for (int i = 0; i < 20; i++) {
			frame[i] = bufferedData[i];
		}
		
		clearStatus();
		return frame;
	}
}
