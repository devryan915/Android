package com.ryan.calendar;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CardGridItem extends TextView {
	public final static int MonthCardGridItem = 1;
	public final static int DayCardGridItem = 2;
	private int CardGridItemType = MonthCardGridItem;

	public int getCardGridItemType() {
		return CardGridItemType;
	}

	public void setCardGridItemType(int cardGridItemType) {
		CardGridItemType = cardGridItemType;
	}

	private Date dateTime;
	private boolean enable = true;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public CardGridItem(Context context) {
		super(context);
	}

	public CardGridItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
