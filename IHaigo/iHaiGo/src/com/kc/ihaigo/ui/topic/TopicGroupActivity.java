/**
 * @Title: TopicGroupActivity.java
 * @Package: com.kc.ihaigo.ui.topic
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月25日 上午10:52:03

 * @version V1.0

 */

package com.kc.ihaigo.ui.topic;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;

/**
 * @ClassName: TopicGroupActivity
 * @Description: 话题入口
 * @author: ryan.wang
 * @date: 2014年7月25日 上午10:52:03
 * 
 */

public class TopicGroupActivity extends IHaiGoGroupActivity {
	// 用于管理本Group中的所有Activity
	public static TopicGroupActivity group;
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
		IHaiGoMainActivity.main.setCurrentTab(IHaiGoMainActivity.TOPIC);
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

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
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
			Intent intent = new Intent(this, TopicActivity.class);
			startiHaiGoActivity(intent);
		}
		super.onResume();
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		CurrenttActivitId = this.getLocalActivityManager().getCurrentId();
		DisplayTabHost = IHaiGoMainActivity.tab_content.getVisibility() == View.VISIBLE
				? true
				: false;
		super.onSaveInstanceState(outState);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return this.getCurrentActivity().onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (this.getCurrentActivity() == null)
			return;
		((IHaiGoActivity) this.getCurrentActivity()).dealDatas(requestCode,
				resultCode, data);
	}
}
