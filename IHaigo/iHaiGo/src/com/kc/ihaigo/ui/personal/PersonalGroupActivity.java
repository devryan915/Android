/**
 * @Title: PersonalGroupActivity.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月10日 上午9:28:13

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.util.Constants;

/**
 * @ClassName: PersonalGroupActivity
 * @Description: 个人中心主入口
 * @author: helen.yang
 * @date: 2014年7月10日 上午9:28:13
 * 
 */

public class PersonalGroupActivity extends IHaiGoGroupActivity {

	// 用于管理本Group中的所有Activity
	public static PersonalGroupActivity group;
	public String CurrenttActivitId = null;
	public Boolean DisplayTabHost = true;
	public String TAG = "PersonalGroupActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		group = this;
	}

	protected boolean checkLogin() {
		return TextUtils.isEmpty(Constants.USER_ID) ? false : true;
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
		IHaiGoMainActivity.main.setCurrentTab(IHaiGoMainActivity.PERSONAL);
		// 先回到首页在转到购物车页面
		// PersonalGroupActivity.group.startiHaiGoActivity(intent);
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
		if (CurrenttActivitId != null) {
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
			// 要跳转的Activity
			Intent intent = new Intent(this, PersonalActivity.class);
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

	/*
	 * <p>Title: onActivityResult</p> <p>Description: </p>
	 * 
	 * @param requestCode
	 * 
	 * @param resultCode
	 * 
	 * @param data
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		((IHaiGoActivity) this.getCurrentActivity()).dealDatas(requestCode,
				resultCode, data);
	}

}
