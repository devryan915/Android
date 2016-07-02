package com.broadchance.xiaojian.person.ecg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.broadchance.xiaojian.BaseFragment;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.BaseActivity.OnFragmentCallBack;
import com.ryan.calendar.CalendarPager;
import com.ryan.calendar.CalendarPager.OnCellItemClick;
import com.ryan.calendar.CardGridItem;

public class ECGHistoryFragment extends BaseFragment {

	private CalendarPager mCalendarCard;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_ecghistory, null);
		mCalendarCard = (CalendarPager) rootView
				.findViewById(R.id.calendarCard);
		mCalendarCard.setOnCellItemClick(new OnCellItemClick() {
			@Override
			public void onCellClick(CardGridItem item) {
				mCalendarCard.setVisibility(View.GONE);
				ECGHistoryDateFragment currentFragment = new ECGHistoryDateFragment();
				Bundle bundle = new Bundle();
				bundle.putLong(BaseFragment.Fragment_Params_Date.toString(),
						item.getDateTime().getTime());
				currentFragment.setArguments(bundle);
				getBaseActivity().getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.selecgFragmentContent, currentFragment)
						.commit();
			}
		});
		getBaseActivity().mCallBack = new OnFragmentCallBack() {
			// 重写回退
			@Override
			public void OnFragmentItemClick(int id) {
				mCalendarCard.setVisibility(View.VISIBLE);
			}
		};
		return rootView;
	}
}