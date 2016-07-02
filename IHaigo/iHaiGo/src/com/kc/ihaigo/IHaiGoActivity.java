/**
 * @Title: IHaiGoActivity.java
 * @Package: com.kc.ihaigo
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月10日 上午11:24:22

 * @version V1.0

 */

package com.kc.ihaigo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.recommend.RecommendGroupActiviy;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.util.Constants;

/**
 * @ClassName: IHaiGoActivity
 * @Description: 基类Activity
 * @author: ryan.wang
 * @date: 2014年7月10日 上午11:24:22
 * 
 */

public class IHaiGoActivity extends IHaiGoGroupActivity
		implements
			OnClickListener {
	/**
	 * 调用者class,使用class方便比较Component不好比较
	 */
	public Class<IHaiGoActivity> parentClass;
	/**
	 * 调用者groupActivity
	 */
	public IHaiGoGroupActivity parentGroupActivity;
	/**
	 * 当前Activity
	 */
	public IHaiGoActivity currentActivity;
	/**
	 * 页面是否显示Tabhost,默认不显示
	 */
	protected boolean showTabHost = false;
	/**
	 * 页面是否需要刷新，默认刷新
	 */
	protected boolean refreshActivity = true;
	/**
	 * 如果需要刷新，可能使用的参数集合
	 */
	protected Bundle resParams;

	/**
	 * 
	 * @Title: refresh
	 * @user: ryan.wang
	 * @Description: 接收调用者参数刷新页面数据 void
	 * @throws
	 */
	public void refresh() {

	}

	/**
	 * 
	 * @Title: dealDatas
	 * @user: ryan.wang
	 * @Description: 可用于处理Activity回调类方法，比如Activity
	 *               onActivityResult方法，由于是protected在groupActivity中无法下传到子Activity中处理可调用才此方法
	 * 
	 * @param datas
	 *            void
	 * @throws
	 */
	public void dealDatas(Object... datas) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 初始化回退事件
		View backView = findViewById(R.id.title_left);
		if (backView != null) {
			backView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					back();
				}
			});
		}
	}

	/**
	 * 
	 * @Title: back
	 * @user: ryan.wang
	 * @Description: 返回上级页面 void
	 * @throws
	 */
	protected void back() {
		if (parentClass == null || currentActivity == null)
			return;
		if (!parentGroupActivity.equals(currentActivity.getParent())) {
			if (parentGroupActivity instanceof RecommendGroupActiviy) {
				IHaiGoMainActivity.main.setCurrentTab(IHaiGoMainActivity.REC);
			} else if (parentGroupActivity instanceof TopicGroupActivity) {
				IHaiGoMainActivity.main.setCurrentTab(IHaiGoMainActivity.TOPIC);
			} else if (parentGroupActivity instanceof ShippingGroupActiviy) {
				IHaiGoMainActivity.main
						.setCurrentTab(IHaiGoMainActivity.SHIPPING);
			} else if (parentGroupActivity instanceof ShopCarGroupActiviy) {
				IHaiGoMainActivity.main
						.setCurrentTab(IHaiGoMainActivity.SHOPCART);
			} else if (parentGroupActivity instanceof PersonalGroupActivity) {
				IHaiGoMainActivity.main
						.setCurrentTab(IHaiGoMainActivity.PERSONAL);
			}
		}
		Intent intent = new Intent(currentActivity, parentClass);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, showTabHost);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
				refreshActivity);
		// 检查是否需要传参数
		if (resParams != null) {
			intent.putExtras(resParams);
		}
		
		parentGroupActivity.startiHaiGoActivity(intent);
	}
	public void exitApp() {
		IHaiGoMainActivity.main.finish();;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * @Title: checkLogin
	 * @user: ryan.wang
	 * @Description: 检查是否登录成功
	 * 
	 * @return boolean
	 * @throws
	 */
	protected boolean checkLogin() {
		return TextUtils.isEmpty(Constants.USER_ID) ? false : true;
	}

	/**
	 * View控件的点击事件
	 */
	@Override
	public void onClick(View v) {

	}

	/**
	 * 
	 * @Title: refreshLoginStatus
	 * @user: ryan.wang
	 * @Description: 刷新登录状态 logon true登录,false登出
	 * @param logon
	 *            void
	 * @throws
	 */
	public void refreshLoginStatus(boolean logon) {

	}
	/**
	 * 
	 * @Title: refreshNetStatus
	 * @user: ryan.wang
	 * @Description: 根据网络状态的变化刷新数据
	 * 
	 * @param linked
	 *            void
	 * @throws
	 */
	public void refreshNetStatus(boolean linked) {

	}

}
