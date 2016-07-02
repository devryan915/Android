package com.broadchance.xiaojian.person.ecg;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.broadchance.xiaojian.BaseActivity;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.main.MainFragment;
import com.broadchance.xiaojian.person.PersonalHealthActivity;

public class PersonalECGActivity extends BaseActivity {

	private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	private Class fragmentClasses[] = { MainFragment.class,
			ECGCurrentFragment.class, ECGHistoryFragment.class };
	private int mTabDrawable[] = { R.drawable.tab_main, R.drawable.tab_ecg_cur,
			R.drawable.tab_ecg_his };
	private int mTabText[] = { R.string.tab_main, R.string.tab_ecg_cur,
			R.string.tab_ecg_his };

	private void initTab() {
		layoutInflater = LayoutInflater.from(this);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		int count = fragmentClasses.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(getString(mTabText[i]))
					.setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentClasses[i], null);
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.color.tab_bg);
		}
		mTabHost.getTabWidget().setDividerPadding(0);
		mTabHost.getTabWidget().setDividerDrawable(R.color.tab_bg);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				PersonalECGActivity.this.setTitle(tabId);
				if (getString(mTabText[0]).endsWith(tabId)) {
					Intent intent = new Intent(PersonalECGActivity.this,
							PersonalHealthActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
		mTabHost.setCurrentTab(1);
	}

	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mTabDrawable[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTabText[index]);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_ecg);
		initTab();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCallBack != null) {
				mCallBack.OnFragmentItemClick(keyCode);
			}
		}
		return false;
		// return super.onKeyDown(keyCode, event);
	}

}
