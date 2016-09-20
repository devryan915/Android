package com.langlang.data;

import java.util.Date;

public class RRItem {
	public Date baseDate;
	public int offset;
	
	public RRItem() {}
	
	public RRItem(Date baseDate, int offset) {
		this.baseDate = baseDate;
		this.offset = offset;
	}
}
