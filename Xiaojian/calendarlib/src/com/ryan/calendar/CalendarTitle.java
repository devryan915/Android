package com.ryan.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ryan.calendar.CalendarPager.OnCellItemClick;
import com.ryan.calendarlib.R;

public class CalendarTitle extends FrameLayout {

	private TextView label_date;
	private Calendar curDate = Calendar.getInstance();
	private OnCellItemClick onCellItemClick = new OnCellItemClick() {

		@Override
		public void onCellClick(CardGridItem cellItem) {
			Toast.makeText(
					getContext(),
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cellItem
							.getDateTime()), 10).show();
		}
	};

	public OnCellItemClick getOnCellItemClick() {
		return onCellItemClick;
	}

	public void setOnCellItemClick(OnCellItemClick onCellItemClick) {
		this.onCellItemClick = onCellItemClick;
	}

	public Calendar getCurDate() {
		return curDate;
	}

	public void setCurDate(Calendar curDate) {
		this.curDate = (Calendar) curDate.clone();
		setDateLabel(CalendarPager.Label_Init);
	}

	public CalendarTitle(Context context) {
		super(context);
		intView();
	}

	public CalendarTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		intView();
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.CalendarPagerAttrs);
		typedArray.recycle();
	}

	private void intView() {
		LayoutInflater.from(getContext())
				.inflate(R.layout.calendar_title, this);
		label_date = (TextView) findViewById(R.id.label_date);
		findViewById(R.id.nav_left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setDateLabel(CalendarPager.Label_Minus);
				if (onCellItemClick != null) {
					CardGridItem cellItem = new CardGridItem(getContext());
					cellItem.setDateTime(curDate.getTime());
					onCellItemClick.onCellClick(cellItem);
				}
			}
		});

		findViewById(R.id.nav_right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setDateLabel(CalendarPager.Label_Add);
				if (onCellItemClick != null) {
					CardGridItem cellItem = new CardGridItem(getContext());
					cellItem.setDateTime(curDate.getTime());
					onCellItemClick.onCellClick(cellItem);
				}

			}
		});
		setDateLabel(CalendarPager.Label_Init);
	}

	private void setDateLabel(int type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		if (type == CalendarPager.Label_Init) {
			label_date.setText(sdf.format(curDate.getTime()));
		} else if (type == CalendarPager.Label_Add) {
			curDate.add(Calendar.MONTH, 1);
			label_date.setText(sdf.format(curDate.getTime()));
		} else if (type == CalendarPager.Label_Minus) {
			curDate.add(Calendar.MONTH, -1);
			label_date.setText(sdf.format(curDate.getTime()));
		}
	}
}
