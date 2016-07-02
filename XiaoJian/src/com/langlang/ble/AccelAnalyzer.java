package com.langlang.ble;

import java.util.LinkedList;
import java.util.Queue;

public class AccelAnalyzer {
	public static final int MAX_SEQ = 255;
	public static final int SEQ_RANGE = 60;
	
	private int prevXSeq;
	private int prevXVal;
	private int prevYSeq;
	private int prevYVal;
	private int prevZSeq;
	private int prevZVal;
	
	private boolean isFirstX = true;
	private boolean isFirstY = true;
	private boolean isFirstZ = true;
	
	private Queue<AccelItem> xQueue = new LinkedList<AccelItem>();
	private Queue<AccelItem> yQueue = new LinkedList<AccelItem>();
	private Queue<AccelItem> zQueue = new LinkedList<AccelItem>();
	
	private static final int S_NOT_STARTED = 0;
	private static final int S_STARTED = 1;
	
	private int status = S_NOT_STARTED;
	
	public AccelAnalyzer() {
		reset();
	}
	
	public void reset() {
		prevXSeq = 0;
		prevXVal = 0;
		prevYSeq = 0;
		prevYVal = 0;
		prevZSeq = 0;
		prevZVal = 0;
		
		isFirstX = true;
		isFirstY = true;
		isFirstZ = true;
		
		xQueue.clear();
		yQueue.clear();
		zQueue.clear();
		
		status = S_NOT_STARTED;
	}
	
	public void acceptX(int seqNo, int val) throws DataErrorException {
		if (isFirstX) {
			prevXSeq = seqNo;
			prevXVal = val;
			
			isFirstX = false;
			
			status = S_STARTED;
		} else {
			if (prevXSeq == seqNo) throw new DataErrorException("Sequence number error detected.");
			
			xQueue.add(new AccelItem(seqNo, val));
			prevXSeq = seqNo;
		}
	}

	public void acceptY(int seqNo, int val) throws DataErrorException {
		if (status == S_NOT_STARTED) return;
		
		if (isFirstY) {
			prevYSeq = seqNo;
			prevYVal = val;
			
			isFirstY = false;
		} else {
			if (prevYSeq == seqNo) throw new DataErrorException("Sequence number error detected.");			
			yQueue.add(new AccelItem(seqNo, val));
			
			prevYSeq = seqNo;
		}
	}
	
	public void acceptZ(int seqNo, int val) throws DataErrorException {
		if (status == S_NOT_STARTED) return;
		
		if (isFirstZ) {
			prevZSeq = seqNo;
			prevZVal = val;
			
			isFirstZ = false;
		} else {
			if (prevZSeq == seqNo) throw new DataErrorException("Sequence number error detected.");			
			zQueue.add(new AccelItem(seqNo, val));
			
			prevZSeq = seqNo;
		}
	}
	
	public boolean isAccelVectorReady() throws DataErrorException {
		if (xQueue.size() == 0) return false;
		if (yQueue.size() == 0) return false;
		if (zQueue.size() == 0) return false;
		
		if (xQueue.size() > 100 || yQueue.size() > 100 || zQueue.size() > 100) {
			throw new DataErrorException("Too much data");
		}
		
		int xSeq = xQueue.peek().seqNo;
		int ySeq = yQueue.peek().seqNo;
		int zSeq = zQueue.peek().seqNo;
		
		int expectYSeq = ((xSeq + 1) > 255) ? 0 : (xSeq + 1); 
		int expectZSeq = ((expectYSeq + 1) > 255) ? 0: (expectYSeq + 1);
		
		if ((ySeq == expectYSeq) && (zSeq == expectZSeq)) {
			return true;
		}
		
		boolean yMatched = false;		
		int yQueueSize = yQueue.size();
		for (int i = 0; i < yQueueSize; i++) {
			
			ySeq = yQueue.peek().seqNo;
			
			if (ySeq == expectYSeq) { 
				yMatched = true;
				break;
			} else {
				if (isInLowerRanges(xSeq, ySeq)) {
					yQueue.poll();
				} else if (isInUpperRanges(xSeq, ySeq)) {
					return false;
				}
			}
		}		
		if (!yMatched) return false;
		
		boolean zMatched = false;
		int zQueueSize = zQueue.size();
		for (int i = 0; i < zQueueSize; i++) {
			zSeq = zQueue.peek().seqNo;
			
			if (zSeq == expectZSeq) {
				return true;
			} else {
				if (isInLowerRanges(expectYSeq, zSeq)) {
					zQueue.poll();
				} else if (isInUpperRanges(expectYSeq, zSeq)) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	public int[] getAccelXYZ() throws DataErrorException {
		if (xQueue.size() == 0 || yQueue.size() == 0 || zQueue.size() == 0) {
			throw new DataErrorException("No data");
		}
		
		int[] xyz = new int[3];
		xyz[0] = xQueue.poll().val;
		xyz[1] = yQueue.poll().val;
		xyz[2] = zQueue.poll().val;
		
		return xyz;
	}
	
	boolean isInUpperRanges(int base, int seq) throws DataErrorException {
		int[] ranges = getUpperRanges(base);
		
		for (int i = 0; i < (ranges.length / 2); i++) {
			if (seq >= ranges[i * 2] && seq <= ranges[(i * 2 + 1)])
				return true;
		}
		
		return false;
	}

	boolean isInLowerRanges(int base, int seq) throws DataErrorException {
		int[] ranges = getUpperRanges(seq);

		for (int i = 0; i < (ranges.length / 2); i++) {
			if (base >= ranges[i * 2] && base <= ranges[i * 2 + 1])
				return true;
		}
		
		return false;
	}
	
	private int nextValueOf(int seq) {
		return 0;
	}

	int[] getUpperRanges(int start) throws DataErrorException {
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
}
