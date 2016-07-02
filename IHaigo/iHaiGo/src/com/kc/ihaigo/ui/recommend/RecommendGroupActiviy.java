/**
 * @Title: RecommendGroupActiviy.java
 * @Package: com.kc.ihaigo.ui.recommend
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年6月26日 下午12:00:36

 * @version V1.0

 */

package com.kc.ihaigo.ui.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;

/**
 * @ClassName: RecommendGroupActiviy
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年6月26日 下午12:00:36
 * 
 */

public class RecommendGroupActiviy extends IHaiGoGroupActivity {
	// 用于管理本Group中的所有Activity
	public static RecommendGroupActiviy group;
	public String CurrenttActivitId = null;
	public Boolean DisplayTabHost = true;
	public String TAG = "RecommendGroupActiviy";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		group = this;
	}
	/**
	 * groupActivity内部跳转
	 */
	@SuppressWarnings({"deprecation", "unchecked"})
	public void startiHaiGoActivity(Intent intent) {
		Class<IHaiGoActivity> parentClass = group.getCurrentActivity() == null
				? null
				: (Class<IHaiGoActivity>) group.getCurrentActivity().getClass();
		// 要跳转的Activity
		// 把Activity转换成一个Window，然后转换成View
		Window w = group.getLocalActivityManager().startActivity(
				intent.getComponent().getClassName(), intent);
		boolean display = intent.getBooleanExtra(
				IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
		if (display) {
			IHaiGoMainActivity.tab_content.setVisibility(View.VISIBLE);
		} else {
			IHaiGoMainActivity.tab_content.setVisibility(View.GONE);
		}
		boolean refresh = intent.getBooleanExtra(
				IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
		IHaiGoActivity curActivity = (IHaiGoActivity) group
				.getCurrentActivity();
		curActivity.currentActivity = curActivity;
		curActivity.parentClass = parentClass;
		curActivity.parentGroupActivity = group;
		if (refresh) {
			curActivity.setIntent(intent);
			curActivity.refresh();
		}
		View view = w.getDecorView();
		// 设置要跳转的Activity显示为本ActivityGroup的内容
		group.setContentView(view);
	}
	/**
	 * groupActivity 由外部调用
	 */
	@SuppressWarnings({"deprecation", "unchecked"})
	public void startiHaiGoActivity(Intent intent,
			IHaiGoGroupActivity groupActivity) {
		IHaiGoMainActivity.main.setCurrentTab(IHaiGoMainActivity.REC);
		Class<IHaiGoActivity> parentClass = (Class<IHaiGoActivity>) groupActivity
				.getCurrentActivity().getClass();
		// 要跳转的Activity
		// 把Activity转换成一个Window，然后转换成View
		Window w = group.getLocalActivityManager().startActivity(
				intent.getComponent().getClassName(), intent);
		boolean display = intent.getBooleanExtra(
				IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
		if (display) {
			IHaiGoMainActivity.tab_content.setVisibility(View.VISIBLE);
		} else {
			IHaiGoMainActivity.tab_content.setVisibility(View.GONE);
		}
		boolean refresh = intent.getBooleanExtra(
				IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
		IHaiGoActivity curActivity = (IHaiGoActivity) group
				.getCurrentActivity();
		curActivity.currentActivity = curActivity;
		curActivity.parentClass = parentClass;
		curActivity.parentGroupActivity = groupActivity;
		if (refresh) {
			curActivity.setIntent(intent);
			curActivity.refresh();
		}
		View view = w.getDecorView();
		// 设置要跳转的Activity显示为本ActivityGroup的内容
		group.setContentView(view);
	}
	@Override
	protected void onResume() {
		Log.e(TAG, "onResume");
		if (CurrenttActivitId != null
				&& CurrenttActivitId.equals(this.getLocalActivityManager()
						.getCurrentId())) {
			// 要跳转的Activity
			// 把Activity转换成一个Window，然后转换成View
			Window w = group.getLocalActivityManager()
					.getActivity(CurrenttActivitId).getWindow();
			View view = w.getDecorView();
			// 设置要跳转的Activity显示为本ActivityGroup的内容
			group.setContentView(view);
			if (DisplayTabHost != null && !DisplayTabHost) {
				IHaiGoMainActivity.tab_content.setVisibility(View.GONE);
			} else {
				IHaiGoMainActivity.tab_content.setVisibility(View.VISIBLE);
			}
		} else {
			Intent intent = new Intent(this, RecommendActivity.class);
			startiHaiGoActivity(intent);
		}
		super.onResume();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.e(TAG, "onSaveInstanceState");
		CurrenttActivitId = this.getLocalActivityManager().getCurrentId();
		DisplayTabHost = IHaiGoMainActivity.tab_content.getVisibility() == View.VISIBLE
				? true
				: false;
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return this.getCurrentActivity().onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
}
