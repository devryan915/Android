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

public class CalendarWeek extends FrameLayout implements OnClickListener {

	private CardGridItem week_monday;
	private CardGridItem week_tuesday;
	private CardGridItem week_wednesday;
	private CardGridItem week_thursday;
	private CardGridItem week_friday;
	private CardGridItem week_saturday;
	private CardGridItem week_sunday;

	private CardGridItem selectedDayView;
	private TextView label_date;
	private Calendar curDate = Calendar.getInstance();

	public Calendar getCurDate() {
		return curDate;
	}

	public void setCurDate(Calendar curDate) {
		this.curDate = (Calendar) curDate.clone();
		setDateLabel(CalendarPager.Label_Init);
	}

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

	public CalendarWeek(Context context) {
		super(context);
		intView();
	}

	public CalendarWeek(Context context, AttributeSet attrs) {
		super(context, attrs);
		intView();
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.CalendarPagerAttrs);
		typedArray.recycle();
	}

	private void intView() {
		LayoutInflater.from(getContext()).inflate(R.layout.calendar_week, this);
		label_date = (TextView) findViewById(R.id.label_date);
		findViewById(R.id.nav_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDateLabel(CalendarPager.Label_Minus);
			}
		});
		findViewById(R.id.nav_right).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDateLabel(CalendarPager.Label_Add);
			}
		});
		week_monday = (CardGridItem) findViewById(R.id.week_monday);
		week_monday.setOnClickListener(this);
		week_tuesday = (CardGridItem) findViewById(R.id.week_tuesday);
		week_tuesday.setOnClickListener(this);
		week_wednesday = (CardGridItem) findViewById(R.id.week_wednesday);
		week_wednesday.setOnClickListener(this);
		week_thursday = (CardGridItem) findViewById(R.id.week_thursday);
		week_thursday.setOnClickListener(this);
		week_friday = (CardGridItem) findViewById(R.id.week_friday);
		week_friday.setOnClickListener(this);
		week_saturday = (CardGridItem) findViewById(R.id.week_saturday);
		week_saturday.setOnClickListener(this);
		week_sunday = (CardGridItem) findViewById(R.id.week_sunday);
		week_sunday.setOnClickListener(this);
		setDateLabel(CalendarPager.Label_Init);
	}

	private void updateWeek() {
		Calendar cal = (Calendar) curDate.clone();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		week_monday.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		week_monday.setTextColor(getResources()
				.getColor(R.color.calendar_color));
		week_monday.setBackgroundResource(R.drawable.days_bg);
		week_monday.setDateTime(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		week_tuesday.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		week_tuesday.setTextColor(getResources().getColor(
				R.color.calendar_color));
		week_tuesday.setBackgroundResource(R.drawable.days_bg);
		week_tuesday.setDateTime(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		week_wednesday.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		week_wednesday.setTextColor(getResources().getColor(
				R.color.calendar_color));
		week_wednesday.setBackgroundResource(R.drawable.days_bg);
		week_wednesday.setDateTime(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		week_thursday.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		week_thursday.setTextColor(getResources().getColor(
				R.color.calendar_color));
		week_thursday.setBackgroundResource(R.drawable.days_bg);
		week_thursday.setDateTime(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		week_friday.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		week_friday.setTextColor(getResources()
				.getColor(R.color.calendar_color));
		week_friday.setBackgroundResource(R.drawable.days_bg);
		week_friday.setDateTime(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		week_saturday.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		week_saturday.setTextColor(getResources().getColor(
				R.color.calendar_color));
		week_saturday.setBackgroundResource(R.drawable.days_bg);
		week_saturday.setDateTime(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		week_sunday.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		week_sunday.setTextColor(getResources()
				.getColor(R.color.calendar_color));
		week_sunday.setBackgroundResource(R.drawable.days_bg);
		week_sunday.setDateTime(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		int dayofweek = curDate.get(Calendar.DAY_OF_WEEK);
		switch (dayofweek) {
		case Calendar.MONDAY:
			onClick(week_monday);
			break;
		case Calendar.TUESDAY:
			onClick(week_tuesday);
			break;
		case Calendar.WEDNESDAY:
			onClick(week_wednesday);
			break;
		case Calendar.THURSDAY:
			onClick(week_thursday);
			break;
		case Calendar.FRIDAY:
			onClick(week_friday);
			break;
		case Calendar.SATURDAY:
			onClick(week_saturday);
			break;
		case Calendar.SUNDAY:
			onClick(week_sunday);
			break;

		default:
			break;
		}
	}

	private void setDateLabel(int type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月第w周");
		if (type == CalendarPager.Label_Init) {
			label_date.setText(sdf.format(curDate.getTime()));
			updateWeek();
		} else if (type == CalendarPager.Label_Add) {
			curDate.add(Calendar.WEEK_OF_YEAR, 1);
			label_date.setText(sdf.format(curDate.getTime()));
			updateWeek();
		} else if (type == CalendarPager.Label_Minus) {
			curDate.add(Calendar.WEEK_OF_YEAR, -1);
			label_date.setText(sdf.format(curDate.getTime()));
			updateWeek();
		}
	}

	@Override
	public void onClick(View v) {
		if (v instanceof CardGridItem) {
			CardGridItem gridItem = (CardGridItem) v;
			if (selectedDayView != null) {
				selectedDayView.setTextColor(getResources().getColor(
						R.color.calendar_color));
				selectedDayView.setBackgroundResource(R.drawable.days_bg);
			}
			gridItem.setTextColor(getResources()
					.getColor(android.R.color.white));
			gridItem.setBackgroundResource(R.drawable.days_bgsel);
			selectedDayView = gridItem;
			onCellItemClick.onCellClick(gridItem);
		}
	}

}
