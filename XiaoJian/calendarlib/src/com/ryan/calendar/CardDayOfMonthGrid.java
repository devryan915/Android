package com.ryan.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ryan.calendar.CalendarPager.OnCellItemClick;
import com.ryan.calendarlib.R;

public class CardDayOfMonthGrid extends FrameLayout {

	private List<CardGridItem> days;
	private OnCellItemClick itemClick;

	public OnCellItemClick getItemClick() {
		return itemClick;
	}

	public void setItemClick(OnCellItemClick itemClick) {
		this.itemClick = itemClick;
	}

	public List<CardGridItem> getDays() {
		return days;
	}

	private int getDaySpacing(int dayOfWeek) {
		if (Calendar.SUNDAY == dayOfWeek)
			return 6;
		else
			return dayOfWeek - 2;
	}

	private int getDaySpacingEnd(int dayOfWeek) {
		return 8 - dayOfWeek;
	}

	public void updateCardGridItem(Calendar calendar) {
		for (int i = 0; i < days.size(); i++) {
			days.get(i).setVisibility(View.VISIBLE);
		}
		Integer counter = 0;
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int daySpacing = getDaySpacing(calendar.get(Calendar.DAY_OF_WEEK));
		if (daySpacing > 0) {
			Calendar prevMonth = (Calendar) calendar.clone();
			prevMonth.add(Calendar.MONTH, -1);
			prevMonth.set(Calendar.DAY_OF_MONTH,
					prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
							- daySpacing + 1);
			for (int i = 0; i < daySpacing; i++) {
				CardGridItem item = days.get(counter);
				item.setText(Integer.valueOf(prevMonth
						.get(Calendar.DAY_OF_MONTH)) + "");
				item.setTextColor(getResources().getColor(
						R.color.calendar_color));
				item.setBackgroundResource(R.drawable.days_bg);
				item.setDateTime(prevMonth.getTime());
				counter++;
				prevMonth.add(Calendar.DAY_OF_MONTH, 1);
			}
		}

		int firstDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		int lastDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
		Calendar date = (Calendar) calendar.clone();
		date.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = firstDay; i < lastDay; i++) {
			CardGridItem item = days.get(counter);
			item.setText(i + "");
			item.setTextColor(getResources().getColor(R.color.calendar_color));
			item.setBackgroundResource(R.drawable.days_bg);
			item.setDateTime(date.getTime());
			date.add(Calendar.DAY_OF_MONTH, 1);
			counter++;
		}

		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		daySpacing = getDaySpacingEnd(calendar.get(Calendar.DAY_OF_WEEK));
		if (daySpacing > 0) {
			for (int i = 0; i < daySpacing; i++) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				CardGridItem item = days.get(counter);
				item.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
				item.setTextColor(getResources().getColor(
						R.color.calendar_color));
				item.setBackgroundResource(R.drawable.days_bg);
				item.setDateTime(calendar.getTime());
				counter++;
			}
		}
		for (int i = counter; i < days.size(); i++) {
			days.get(i).setVisibility(View.INVISIBLE);
		}
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_days, this);
		days = new ArrayList<CardGridItem>();
		days.add((CardGridItem) findViewById(R.id.Day_1));
		days.add((CardGridItem) findViewById(R.id.Day_2));
		days.add((CardGridItem) findViewById(R.id.Day_3));
		days.add((CardGridItem) findViewById(R.id.Day_4));
		days.add((CardGridItem) findViewById(R.id.Day_5));
		days.add((CardGridItem) findViewById(R.id.Day_6));
		days.add((CardGridItem) findViewById(R.id.Day_7));
		days.add((CardGridItem) findViewById(R.id.Day_8));
		days.add((CardGridItem) findViewById(R.id.Day_9));
		days.add((CardGridItem) findViewById(R.id.Day_10));
		days.add((CardGridItem) findViewById(R.id.Day_11));
		days.add((CardGridItem) findViewById(R.id.Day_12));
		days.add((CardGridItem) findViewById(R.id.Day_13));
		days.add((CardGridItem) findViewById(R.id.Day_14));
		days.add((CardGridItem) findViewById(R.id.Day_15));
		days.add((CardGridItem) findViewById(R.id.Day_16));
		days.add((CardGridItem) findViewById(R.id.Day_17));
		days.add((CardGridItem) findViewById(R.id.Day_18));
		days.add((CardGridItem) findViewById(R.id.Day_19));
		days.add((CardGridItem) findViewById(R.id.Day_20));
		days.add((CardGridItem) findViewById(R.id.Day_21));
		days.add((CardGridItem) findViewById(R.id.Day_22));
		days.add((CardGridItem) findViewById(R.id.Day_23));
		days.add((CardGridItem) findViewById(R.id.Day_24));
		days.add((CardGridItem) findViewById(R.id.Day_25));
		days.add((CardGridItem) findViewById(R.id.Day_26));
		days.add((CardGridItem) findViewById(R.id.Day_27));
		days.add((CardGridItem) findViewById(R.id.Day_28));
		days.add((CardGridItem) findViewById(R.id.Day_29));
		days.add((CardGridItem) findViewById(R.id.Day_30));
		days.add((CardGridItem) findViewById(R.id.Day_31));
		days.add((CardGridItem) findViewById(R.id.Day_32));
		days.add((CardGridItem) findViewById(R.id.Day_33));
		days.add((CardGridItem) findViewById(R.id.Day_34));
		days.add((CardGridItem) findViewById(R.id.Day_35));
		days.add((CardGridItem) findViewById(R.id.Day_36));
		days.add((CardGridItem) findViewById(R.id.Day_37));
		days.add((CardGridItem) findViewById(R.id.Day_38));
		days.add((CardGridItem) findViewById(R.id.Day_39));
		days.add((CardGridItem) findViewById(R.id.Day_40));
		days.add((CardGridItem) findViewById(R.id.Day_41));
		days.add((CardGridItem) findViewById(R.id.Day_42));
		for (final CardGridItem item : days) {
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					item.setCardGridItemType(CardGridItem.DayCardGridItem);
					itemClick.onCellClick(item);
				}
			});
		}
	}

	public CardDayOfMonthGrid(Context context) {
		super(context);
		initView();
	}

	public CardDayOfMonthGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
}
