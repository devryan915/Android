package com.ryan.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ryan.calendarlib.R;

public class CalendarPager extends FrameLayout {

	private CardDayOfMonthGrid[] monthView = new CardDayOfMonthGrid[12];

	private CardGridItem selectedMonthView;
	private CardGridItem selectedDayView;
	private CardMonthOfYearGrid calendar_monthofyear;
	private LinearLayout calendar_daysofmonth;

	private ViewPager calendar_viewpager;
	private ViewPagerDayAdapter viewPagerDayAdapter;

	private TextView label_date;

	private Calendar calDate = Calendar.getInstance();

	public Calendar getCalDate() {
		return calDate;
	}

	public void setCalDate(Calendar calDate) {
		this.calDate = calDate;
	}

	public static final int MonthOfYear = 1;
	public static final int DaysOfMonth = 2;

	public int curPage = MonthOfYear;

	private int curMonthPage = -1;

	public static final int Label_Init = 1;
	public static final int Label_Add = 2;
	public static final int Label_Minus = 3;

	private OnCellItemClick onCellItemClick;
	private OnCellItemClick innerOnCellItemClick = new OnCellItemClick() {

		@Override
		public void onCellClick(CardGridItem cellItem) {
			Toast.makeText(
					getContext(),
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cellItem
							.getDateTime()), 10).show();
			calDate.setTime(cellItem.getDateTime());
			if (cellItem.getCardGridItemType() == CardGridItem.MonthCardGridItem) {
				if (selectedMonthView != null) {
					selectedMonthView.setTextColor(getResources().getColor(
							R.color.calendar_color));
					selectedMonthView
							.setBackgroundResource(R.drawable.month_bg);
				}
				cellItem.setTextColor(getResources().getColor(
						android.R.color.white));
				cellItem.setBackgroundResource(R.drawable.month_bgsel);
				selectedMonthView = cellItem;
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						showDaysOfMonth();
					}
				});
			} else if (cellItem.getCardGridItemType() == CardGridItem.DayCardGridItem) {
				if (selectedDayView != null) {
					selectedDayView.setTextColor(getResources().getColor(
							R.color.calendar_color));
					selectedDayView.setBackgroundResource(R.drawable.days_bg);
				}
				cellItem.setTextColor(getResources().getColor(
						android.R.color.white));
				cellItem.setBackgroundResource(R.drawable.days_bgsel);
				selectedDayView = cellItem;
				if (onCellItemClick != null) {
					onCellItemClick.onCellClick(cellItem);
				}
			}
		}
	};

	public OnCellItemClick getOnCellItemClick() {
		return onCellItemClick;
	}

	public void setOnCellItemClick(OnCellItemClick onCellItemClick) {
		this.onCellItemClick = onCellItemClick;
	}

	public interface OnCellItemClick {
		void onCellClick(CardGridItem cellItem);
	}

	public CalendarPager(Context context) {
		super(context);
	}

	private void initDays() {
		Context context = getContext();
		Calendar cal = (Calendar) calDate.clone();
		CardDayOfMonthGrid month;
		for (int i = 0; i < 12; i++) {
			if (monthView[i] != null) {
				month = monthView[i];
			} else {
				month = new CardDayOfMonthGrid(context);
				month.setItemClick(innerOnCellItemClick);
				monthView[i] = month;
			}
			cal.set(Calendar.MONTH, i);
			month.updateCardGridItem(cal);
		}
	}

	public CalendarPager(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.CalendarPagerAttrs);
		LayoutInflater.from(context).inflate(R.layout.calendar_calendar, this);
		calendar_monthofyear = (CardMonthOfYearGrid) findViewById(R.id.calendar_monthofyear);
		calendar_daysofmonth = (LinearLayout) findViewById(R.id.calendar_daysofmonth);
		label_date = (TextView) findViewById(R.id.label_date);
		findViewById(R.id.nav_left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setDateLabel(Label_Minus);
			}
		});

		findViewById(R.id.nav_right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setDateLabel(Label_Add);
			}
		});
		setDateLabel(Label_Init);
		// initDays();
		calendar_monthofyear.setItemClick(innerOnCellItemClick);
		calendar_viewpager = (ViewPager) findViewById(R.id.calendar_viewpager);
		viewPagerDayAdapter = new ViewPagerDayAdapter();
		viewPagerDayAdapter.setMonths(monthView);
		calendar_viewpager.setAdapter(viewPagerDayAdapter);
		calendar_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			float offset = -1;

			@Override
			public void onPageSelected(int pageselected) {
				if (curMonthPage < pageselected) {
					setDateLabel(Label_Add);
				} else if (curMonthPage > pageselected) {
					setDateLabel(Label_Minus);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (curMonthPage == 0 || curMonthPage == 11) {
					if (offset == arg1) {
						showMonthOfYear();
					} else {
						offset = arg1;
					}
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							offset = -1;
						}
					}, 500);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		typedArray.recycle();
	}

	private void updateMonth() {
		calendar_monthofyear.updateCardGridItem((Calendar) calDate.clone());
	}

	private void updateDays() {
		curMonthPage = calDate.get(Calendar.MONTH);
		calendar_viewpager.setCurrentItem(curMonthPage, true);
	}

	private void setDateLabel(int type) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		if (type == Label_Init) {
			if (curPage == MonthOfYear) {
				sdf.applyPattern("yyyy年");
				label_date.setText(sdf.format(calDate.getTime()));
				updateMonth();
			} else if (curPage == DaysOfMonth) {
				sdf.applyPattern("yyyy年MM月");
				label_date.setText(sdf.format(calDate.getTime()));
				updateDays();
			}
		} else if (type == Label_Add) {
			if (curPage == MonthOfYear) {
				calDate.add(Calendar.YEAR, 1);
				sdf.applyPattern("yyyy年");
				label_date.setText(sdf.format(calDate.getTime()));
				updateMonth();
			} else if (curPage == DaysOfMonth) {
				// 如果是最大月份，跳转到年月
				if (curMonthPage == 11) {
					showMonthOfYear();
					return;
				}
				calDate.add(Calendar.MONTH, 1);
				sdf.applyPattern("yyyy年MM月");
				label_date.setText(sdf.format(calDate.getTime()));
				updateDays();
			}
		} else if (type == Label_Minus) {
			if (curPage == MonthOfYear) {
				calDate.add(Calendar.YEAR, -1);
				sdf.applyPattern("yyyy年");
				label_date.setText(sdf.format(calDate.getTime()));
				updateMonth();
			} else if (curPage == DaysOfMonth) {
				// 如果是最小月份，跳转到年月
				if (curMonthPage == 0) {
					showMonthOfYear();
					return;
				}
				calDate.add(Calendar.MONTH, -1);
				sdf.applyPattern("yyyy年MM月");
				label_date.setText(sdf.format(calDate.getTime()));
				updateDays();
			}
		}
	}

	private void showMonthOfYear() {
		calendar_monthofyear.setVisibility(View.VISIBLE);
		calendar_daysofmonth.setVisibility(View.GONE);
		curPage = MonthOfYear;
		setDateLabel(Label_Init);
	}

	private void showDaysOfMonth() {
		calendar_monthofyear.setVisibility(View.GONE);
		calendar_daysofmonth.setVisibility(View.VISIBLE);
		curPage = DaysOfMonth;
		initDays();
		setDateLabel(Label_Init);
	}
}
