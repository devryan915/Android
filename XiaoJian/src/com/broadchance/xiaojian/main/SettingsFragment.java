package com.broadchance.xiaojian.main;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.utils.Constant;

public class SettingsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings_content,
				null);
		SharedPreferences sp = getActivity().getSharedPreferences(
				Constant.SETTINGS_CONFIG, getActivity().MODE_PRIVATE);
		CheckBox setmusic = ((CheckBox) rootView
				.findViewById(R.id.settings_chkmusic));
		setmusic.setChecked(sp.getBoolean(Constant.SETTINGS_MUSIC, false));
		setmusic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences sp = getActivity().getSharedPreferences(
						Constant.SETTINGS_CONFIG, getActivity().MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putBoolean(Constant.SETTINGS_MUSIC, isChecked);
				editor.commit();
			}
		});

		return rootView;
	}
}
