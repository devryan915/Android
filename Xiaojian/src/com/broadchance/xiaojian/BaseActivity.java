package com.broadchance.xiaojian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity implements
		OnClickListener {

	protected boolean NoTitle = false;
	protected boolean ShowLeftTitle = false;
	protected boolean ShowRightTitle = false;
	protected TextView mleftTitle;
	protected TextView mMiddleTitle;
	protected TextView mrightTitle;

	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		mMiddleTitle.setText(titleId);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		mMiddleTitle.setText(title);
	}

	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (NoTitle) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setDisplayOptions(
					ActionBar.DISPLAY_SHOW_CUSTOM);
			getSupportActionBar().setCustomView(R.layout.custom_title_main);
			View titleView = getSupportActionBar().getCustomView();
			mleftTitle = (TextView) titleView.findViewById(R.id.title_left);
			mMiddleTitle = (TextView) titleView.findViewById(R.id.title_middle);
			mrightTitle = (TextView) titleView.findViewById(R.id.title_right);
			if (ShowLeftTitle) {
				mleftTitle.setVisibility(View.VISIBLE);
			} else {
				mleftTitle.setVisibility(View.GONE);
			}
			if (ShowRightTitle) {
				mrightTitle.setVisibility(View.VISIBLE);
			} else {
				mrightTitle.setVisibility(View.GONE);
			}
		}
		super.onCreate(savedInstanceState);

		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeEnabled(true);
		sm.setFadeDegree(0.8f);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setMode(SlidingMenu.LEFT);
		sm.setSlidingEnabled(false);
		setBehindContentView(android.R.layout.list_content);
	}

	public OnFragmentCallBack mCallBack;

	public interface OnFragmentCallBack {
		public void OnFragmentItemClick(int id);
	}

	@Override
	public void onClick(View v) {

	}
}
