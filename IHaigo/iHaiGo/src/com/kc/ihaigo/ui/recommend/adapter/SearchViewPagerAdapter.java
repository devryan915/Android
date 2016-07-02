package com.kc.ihaigo.ui.recommend.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.RecommendGroupActiviy;
import com.kc.ihaigo.ui.recommend.SearchResultActivity;
import com.kc.ihaigo.ui.recommend.adapter.MenucAdapter.ViewHolder;
import com.kc.ihaigo.ui.selfwidget.WrapGridView;

/**
 * 
 * @ClassName: SearchViewPagerAdapter
 * @Description: 搜索页面viewpaper适配器
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:43:30
 * 
 */
public class SearchViewPagerAdapter extends PagerAdapter {

	private Context ctx;
	private List<View> dataViews;
	private JSONArray array;

	public void setDataViews(List<View> dataViews) {
		this.dataViews = dataViews;
	}
	public void setDatas(JSONArray array) {
		this.array = array;

	}

	public SearchViewPagerAdapter(Context ctx) throws JSONException {
		this.ctx = ctx;
		MenuItemClickListener itemClick = new MenuItemClickListener();
		dataViews = new ArrayList<View>();
		for (int i = 0; i < 5; i++) {
			MenucAdapter gridViewAdapter = new MenucAdapter(ctx);
			WrapGridView gridView = (WrapGridView) LayoutInflater.from(ctx)
					.inflate(R.layout.search_hotsearch_gridview, null);
			gridViewAdapter.setDatas(array);
			gridView.setAdapter(gridViewAdapter);
			gridView.setOnItemClickListener(itemClick);
			dataViews.add(gridView);
		}
	}
	class MenuItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(ctx, SearchResultActivity.class);
			ViewHolder holder = (ViewHolder) view.getTag();
			intent.putExtra("sortname", holder.menuName.getText());
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			RecommendGroupActiviy.group.startiHaiGoActivity(intent);
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return dataViews.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		android.widget.LinearLayout.LayoutParams lParams = new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
		((ViewPager) view).addView(dataViews.get(position), lParams);
		return dataViews.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}
}