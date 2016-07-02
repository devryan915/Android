package com.kc.ihaigo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 程序的主页面，向右滑动可出现菜单。
 * 
 * @author wanghao
 * 
 */
public abstract class IHaiGoGroupActivity extends BaseActivityGroup {

	public void startiHaiGoActivity(Intent intent) {

	}

	ViewGroup container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lak_root);
		// 注解的方式将用到的控件初始化
		// ActivityHelper.findViewById(this);
		// 这里不能使用ActivityHelper.findViewById(this)来实现View的初始化，因为子类通过调用super.onCreateView(...)来调用的该代码，所以不能找到

		initViewRef();

		addViewToRoot(container);

	}

	private void initViewRef() {
		container = (ViewGroup) this.findViewById(R.id.root_content);
	}

	public void startActivityEasy(Class<? extends Activity> activityClazz) {
		Intent intent = new Intent(IHaiGoGroupActivity.this, activityClazz);
		startActivity(intent);
	}

	public void changeActivity(Class<? extends Activity> activityClazz) {
		container.removeAllViews();
		Intent intent = new Intent(IHaiGoGroupActivity.this, activityClazz);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		View targetView = getLocalActivityManager().startActivity(
				activityClazz.getSimpleName(), intent).getDecorView();
		container.addView(targetView);
	}

	/**
	 * @author xiaokdeou 点击RadioGroup实现view切换
	 * @param activityClazz
	 * @param container
	 */
	public void changeActivity(Class<? extends Activity> activityClazz,
			LinearLayout container) {
		container.removeAllViews();
		Intent intent = new Intent(IHaiGoGroupActivity.this, activityClazz);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		View targetView = getLocalActivityManager().startActivity(
				activityClazz.getSimpleName(), intent).getDecorView();
		container.addView(targetView);
	}

	/**
	 * @author xiaokdeou 点击RadioGroup实现view切换
	 * @param activityClazz
	 * @param container
	 */
	public void changeActivity(Class<? extends Activity> activityClazz,
			LinearLayout container, Object obj) {
		container.removeAllViews();
		Intent intent = new Intent(IHaiGoGroupActivity.this, activityClazz);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.putExtra("itemname", (String) obj);
		View targetView = getLocalActivityManager().startActivity(
				activityClazz.getSimpleName(), intent).getDecorView();
		container.addView(targetView);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return this.getCurrentActivity().onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
}
