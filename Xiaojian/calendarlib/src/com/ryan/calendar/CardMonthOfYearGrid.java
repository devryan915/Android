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

public class CardMonthOfYearGrid extends FrameLayout {
	private List<CardGridItem> months;
	private OnCellItemClick itemClick;

	public OnCellItemClick getItemClick() {
		return itemClick;
	}

	public void setItemClick(OnCellItemClick itemClick) {
		this.itemClick = itemClick;
	}

	public List<CardGridItem> getMonths() {
		return months;
	}

	public void updateCardGridItem(Calendar calendar) {
		for (int i = 0; i < months.size(); i++) {
			CardGridItem item = months.get(i);
			item.setText(i + 1 + "");
			item.setTextSize(getContext().getResources().getDimension(
					R.dimen.month_font));
			item.setTextColor(getContext().getResources().getColor(
					R.color.calendar_color));
			item.setBackgroundResource(R.drawable.month_bg);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, i);
			item.setDateTime(calendar.getTime());
		}
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_month, this);
		months = new ArrayList<CardGridItem>();
		months.add((CardGridItem) findViewById(R.id.Month_1));
		months.add((CardGridItem) findViewById(R.id.Month_2));
		months.add((CardGridItem) findViewById(R.id.Month_3));
		months.add((CardGridItem) findViewById(R.id.Month_4));
		months.add((CardGridItem) findViewById(R.id.Month_5));
		months.add((CardGridItem) findViewById(R.id.Month_6));
		months.add((CardGridItem) findViewById(R.id.Month_7));
		months.add((CardGridItem) findViewById(R.id.Month_8));
		months.add((CardGridItem) findViewById(R.id.Month_9));
		months.add((CardGridItem) findViewById(R.id.Month_10));
		months.add((CardGridItem) findViewById(R.id.Month_11));
		months.add((CardGridItem) findViewById(R.id.Month_12));
		for (int i = 0; i < months.size(); i++) {
			CardGridItem item = months.get(i);

			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CardGridItem gridItem = (CardGridItem) v;
					gridItem.setCardGridItemType(CardGridItem.MonthCardGridItem);
					itemClick.onCellClick(gridItem);
				}
			});
		}
	}

	public CardMonthOfYearGrid(Context context) {
		super(context);
		initView();
	}

	public CardMonthOfYearGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

}
