package com.ryan.calendar;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerDayAdapter extends PagerAdapter {
	private CardDayOfMonthGrid[] months;

	public CardDayOfMonthGrid[] getMonths() {
		return months;
	}

	public void setMonths(CardDayOfMonthGrid[] months) {
		this.months = months;
	}

	@Override
	public int getCount() {
		return months.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(months[position]);
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(months[position]);
		return months[position];
	}

}
