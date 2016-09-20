package com.broadchance.xiaojian;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	public final static Integer Fragment_Params_Date = 1;

	public BaseActivity getBaseActivity() {
		return (BaseActivity) super.getActivity();
	}
}