package com.broadchance.xiaojian.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.broadchance.xiaojian.BaseActivity;
import com.broadchance.xiaojian.BaseFragment;
import com.broadchance.xiaojian.R;

public class MainFragment extends BaseFragment {

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		BaseActivity mainActivity = (BaseActivity) getActivity();
		if (mainActivity.getClass() != MainActivity.class)
			return null;
		View rootView = inflater.inflate(R.layout.fragment_main, null);
		rootView.findViewById(R.id.main_healthcall).setOnClickListener(
				mainActivity);
		rootView.findViewById(R.id.main_personalhealth).setOnClickListener(
				mainActivity);
		rootView.findViewById(R.id.main_lovecare).setOnClickListener(
				mainActivity);
		rootView.findViewById(R.id.main_cloudservice).setOnClickListener(
				mainActivity);
		rootView.findViewById(R.id.main_settings).setOnClickListener(
				mainActivity);
		return rootView;
	}

}
