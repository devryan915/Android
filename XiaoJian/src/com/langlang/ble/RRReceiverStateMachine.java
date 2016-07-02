package com.langlang.ble;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.langlang.data.RRItem;
import com.langlang.utils.Program;

public class RRReceiverStateMachine {
	private static final int S_IDLE = 0;
	private static final int S_BASE_TIME = 1;
	private static final int S_OFFSET = 2;
	private static final int S_COMPLETED = 3;
	
	private static final int FRAME_TYPE_UNKNOWN = -1;
	private static final int FRAME_TYPE_NONE = 0;
	private static final int FRAME_TYPE_BASE_TIME = 1; 
	private static final int FRAME_TYPE_OFFSET = 2;
	private static final int FRAME_TYPE_COMPLETED = 3;
	
	private static final int TIME_OUT = 1000 * 60 * 1;

	private int mStatus = S_IDLE;
	private byte mLastBaseTimeSeqNO = 0x00;
	private Date mLastRRDate = null;
	private Date mBaseDate = null;	
	private List<RRItem> mRRList = new ArrayList<RRItem>();
	
	private Date mStartDate = null;
	private Date mEndDate = null;
	
	private static final int MAX_SEQ = 255;
	private static final int SEQ_RANGE = 20;
	
	private int mLastOffset = 0;
	
	public RRReceiverStateMachine() {
		reset();
	}
	
	public void reset() {
		mStatus = S_IDLE;
		mLastBaseTimeSeqNO = 0x00;
		mLastRRDate = null;
		mBaseDate = null;
		mRRList.clear();
		
		mStartDate = null;
		mEndDate = null;
	}
	
	public void consume(byte[] data) {
		if (mStatus == S_IDLE) {
			if (getFrameType(data) == FRAME_TYPE_BASE_TIME) {			
				mLastRRDate = new Date();
				mBaseDate = getBaseDate(data);  //new Date(); // Get base date from BLE data
				mStartDate = mBaseDate;
				mLastBaseTimeSeqNO = getSeqNo(data);
				mStatus = S_BASE_TIME;
			}
			
			return;
		}
		
		if (mStatus == S_BASE_TIME) {
			if (getFrameType(data) == FRAME_TYPE_NONE) {			
				// check time out
				Date now = new Date();
				if (now.getTime() - mLastRRDate.getTime() >= TIME_OUT) {
					mEndDate = mBaseDate;
					mStatus = S_COMPLETED;
				}
				
				return;
			}

			if (getFrameType(data) == FRAME_TYPE_OFFSET) {
				mLastRRDate = new Date();
				
				byte seqNo = getSeqNo(data);
				int base = Program.convertByteToUnsignedInt(mLastBaseTimeSeqNO);
				int seq = Program.convertByteToUnsignedInt(seqNo);
				
				try {
					if (isInUpperRanges(base, seq)) {
						accept(getOffsets(data));
					}
				} catch (DataErrorException e) {
					e.printStackTrace();
					System.out.println("action RRReceiverStateMachine DataErrorException happened.");
				}
				return;
			}
		
			if (getFrameType(data) == FRAME_TYPE_COMPLETED) {
				mEndDate = mBaseDate;
				mStatus = S_COMPLETED;
				
				return;
			}

			if (getFrameType(data) == FRAME_TYPE_BASE_TIME) {			
				mLastRRDate = new Date();
				mBaseDate = getBaseDate(data);  //new Date(); // Get base date from BLE data
				mLastBaseTimeSeqNO = getSeqNo(data);
				mStatus = S_BASE_TIME;
				
				return;
			}			
			
			return;
		}
		
		if (mStatus == S_OFFSET) {
			if (getFrameType(data) == FRAME_TYPE_NONE) {			
				// check time out
				Date now = new Date();
				if (now.getTime() - mLastRRDate.getTime() >= TIME_OUT) {
					mEndDate = getEndDateByBase(mBaseDate, mLastOffset);
					mStatus = S_COMPLETED;
				}
				
				return;
			}
			
			if (getFrameType(data) == FRAME_TYPE_OFFSET) {			
				mLastRRDate = new Date();
				
				byte seqNo = getSeqNo(data);
				int base = Program.convertByteToUnsignedInt(mLastBaseTimeSeqNO);
				int seq = Program.convertByteToUnsignedInt(seqNo);
				
				try {
					if (isInUpperRanges(base, seq)) {
						accept(getOffsets(data));
					}
				} catch (DataErrorException e) {
					e.printStackTrace();
					System.out.println("action RRReceiverStateMachine DataErrorException happened.");
				}
				return;
			}
			
			// very important. Be care that some data frames can lost.
			if (getFrameType(data) == FRAME_TYPE_BASE_TIME) {			
				mLastRRDate = new Date();
				mBaseDate = getBaseDate(data);  //new Date(); // Get base date from BLE data
				mLastBaseTimeSeqNO = getSeqNo(data);
				mStatus = S_BASE_TIME;
			}
			
			if (getFrameType(data) == FRAME_TYPE_COMPLETED) {
				mEndDate = getEndDateByBase(mBaseDate, mLastOffset);
				mStatus = S_COMPLETED;
				return;
			}
		
			return;
		}
	}

	public boolean isCompleted() {
		return (mStatus == S_COMPLETED);
	}
	
	public List<RRItem> getRRList() {
		return mRRList;
	}
	
	public Date getStartDate() {
		return mStartDate;
	}
	
	public Date getEndDate() {
		return mEndDate;
	}
	
	private int getFrameType(byte[] data) {
		int dataFrameType = FRAME_TYPE_UNKNOWN;
		int intValueOfByte = Program.byteToInt(data[3]);
		
		if (intValueOfByte == 146) {// Frame 92
			dataFrameType = FRAME_TYPE_BASE_TIME;
		}
		else if (intValueOfByte == 147) {// Frame 93
			dataFrameType = FRAME_TYPE_OFFSET;
		}
		else if (intValueOfByte == 148) {// Frame 94
			dataFrameType = FRAME_TYPE_COMPLETED;
		}
		else {
			dataFrameType = FRAME_TYPE_NONE;
		}
		
		return dataFrameType;
	}
	
	private byte getSeqNo(byte[] data) {
		return data[4];
	}
	
	
	private Date getBaseDate(byte[] data) {
		Calendar calendar = Calendar.getInstance();
		
		int year = Program.convertByteToUnsignedInt(data[5]) * 256 
							+ Program.convertByteToUnsignedInt(data[6]);
		int month = Program.convertByteToUnsignedInt(data[7]) - 1;
		int day = Program.convertByteToUnsignedInt(data[8]);
		
		int hour = Program.convertByteToUnsignedInt(data[9]);
		int minute = Program.convertByteToUnsignedInt(data[10]);
		int second = Program.convertByteToUnsignedInt(data[11]);
		
		int millSecond = Program.convertByteToUnsignedInt(data[12]) * 256
							+ Program.convertByteToUnsignedInt(data[13]);
		
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millSecond);
		
		return calendar.getTime();
	}
	
	private boolean isInUpperRanges(int base, int seq) throws DataErrorException {
		int[] ranges = getUpperRanges(base);
		
		for (int i = 0; i < (ranges.length / 2); i++) {
			if (seq >= ranges[i * 2] && seq <= ranges[(i * 2 + 1)])
				return true;
		}
		
		return false;
	}

	private int[] getUpperRanges(int start) throws DataErrorException {
		if (start >= 0 && start <= (MAX_SEQ - SEQ_RANGE)) {
			int[] ranges = new int[2];
			ranges[0] = start + 1;
			ranges[1] = start + SEQ_RANGE;
			
			return ranges;
			
		} else if (start > (MAX_SEQ - SEQ_RANGE) && start <= MAX_SEQ) {
			int[] ranges = new int[4];
			ranges[0] = (start == MAX_SEQ) ? 0 : start + 1;
			ranges[1] = MAX_SEQ;
			ranges[2] = 0;
			ranges[3] = SEQ_RANGE - (MAX_SEQ - start);
			
			return ranges;
		} else {
			throw new DataErrorException("Out of range");
		}
	}
	
	private int[] getOffsets(byte[] data) {
		int[] offsets = new int[7];
		offsets[0] = Program.convertByteToUnsignedInt(data[5]) * 256 + Program.convertByteToUnsignedInt(data[6]);
		offsets[1] = Program.convertByteToUnsignedInt(data[7]) * 256 + Program.convertByteToUnsignedInt(data[8]);
		offsets[2] = Program.convertByteToUnsignedInt(data[9]) * 256 + Program.convertByteToUnsignedInt(data[10]);
		offsets[3] = Program.convertByteToUnsignedInt(data[11]) * 256 + Program.convertByteToUnsignedInt(data[12]);
		offsets[4] = Program.convertByteToUnsignedInt(data[13]) * 256 + Program.convertByteToUnsignedInt(data[14]);
		offsets[5] = Program.convertByteToUnsignedInt(data[15]) * 256 + Program.convertByteToUnsignedInt(data[16]);
		offsets[6] = Program.convertByteToUnsignedInt(data[17]) * 256 + Program.convertByteToUnsignedInt(data[18]);
		return offsets;
	}
	
	private void accept(int[] offsets) {
		for (int i = 0; i < offsets.length; i++)
			mRRList.add(new RRItem(mBaseDate, offsets[i]));
		
		int lastIndex = offsets.length - 1;
		mLastOffset = offsets[lastIndex];
	}
	
	private Date getEndDateByBase(Date baseDate, int offset) {
		Date date = new Date();
		date.setTime(baseDate.getTime() + offset * 8);
		return date;
	}
}
