/**
 * @Title: TopicPagerAdapter.java
 * @Package: com.kc.ihaigo.ui.topic.adpater
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月25日 上午11:30:33

 * @version V1.0

 */

package com.kc.ihaigo.ui.topic.adpater;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * @ClassName: TopicPagerAdapter
 * @Description: 话题主页适配器
 * @author: ryan.wang
 * @date: 2014年7月25日 上午11:30:33
 * 
 */

public class TopicPagerAdapter extends PagerAdapter {
	private String[] titles;
	private List<View> views;
	public List<View> getViews() {
		return views;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public void setViews(List<View> views) {
		this.views = views;
	}

	@Override
	public int getCount() {
		return titles == null ? 0 : titles.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}

}
