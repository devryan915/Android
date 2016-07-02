/**
 * @Title: SearchActivity.java
 * @Package: com.kc.ihaigo.ui.recommend
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年6月26日 上午9:31:34

 * @version V1.0

 */

package com.kc.ihaigo.ui.recommend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.SearchViewPagerAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: SearchActivity
 * @Description: 精选优惠-搜索
 * @author: ryan.wang
 * @date: 2014年6月26日 上午9:31:34
 * 
 */

public class SearchActivity extends IHaiGoActivity {

	protected static final String TAG = "SearchActivity";
	private ViewPager viewPager;
	private int currentItem = 0;
	private List<View> dots;
	private JSONArray array;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// initTitle();
		initComponets();
		getInfo();
	}

	// private void initTitle() {
	// // 定义返回功能
	// ImageView title_btn_left = (ImageView) findViewById(R.id.title_left);
	// title_btn_left.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// reBack();
	// }
	// });
	// }

	private void initComponets() {
		// TextView sortsearch_search_tv = (TextView)
		// findViewById(R.id.sortsearch_search_tv);
		// Drawable drawable = getResources().getDrawable(
		// R.drawable.sortsearch_searchimg);
		// drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
		// drawable.getIntrinsicHeight());
		// // 需要处理的文本，[smile]是需要被替代的文本
		// String maskString = "ihaigo";
		// SpannableString spannable = new SpannableString(maskString
		// + getString(R.string.sortsearch_search_hint));
		// // 要让图片替代指定的文字就要用ImageSpan
		// ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
		// // 开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
		// // 最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
		// spannable.setSpan(span, 0, maskString.length(),
		// Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		// sortsearch_search_tv.setText(spannable);
		// sortsearch_search_tv.append(getString(R.string.sortsearch_search_hint));
		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
		dots.add(findViewById(R.id.v_dot3));
		dots.add(findViewById(R.id.v_dot4));

		viewPager = (ViewPager) findViewById(R.id.search_hotsearch);
		SearchViewPagerAdapter vpaAdapter;
		try {
			vpaAdapter = new SearchViewPagerAdapter(SearchActivity.this);
			if (Constants.Debug) {
				array = new JSONArray();
				for (int i = 0; i < 5; i++) {
					JSONObject data = new JSONObject();
					data.put("name", "ipad模拟数据");
					array.put(data);
				}
			}
			vpaAdapter.setDatas(array);
			viewPager.setAdapter(vpaAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		((EditText) findViewById(R.id.sortsearch_search_tv))
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						EditText _v = (EditText) v;
						if (!hasFocus) {// 失去焦点
							_v.setHint(_v.getTag().toString());
						} else {
							String hint = _v.getHint().toString();
							_v.setTag(hint);
							_v.setHint("");
						}
					}
				});
		((EditText) findViewById(R.id.sortsearch_search_tv))
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// if (KeyEvent.KEYCODE_ENTER == event.getKeyCode()) {
						Utils.hideInputMethod(SearchActivity.this);
						Intent intent = new Intent(SearchActivity.this,
								SearchResultActivity.class);
						intent.putExtra("sortname", v.getText().toString());
						intent.putExtra(
								IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								false);
						RecommendGroupActiviy.group.startiHaiGoActivity(intent);
						// }
						return true;
					}
				});
	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.recommend.SortSearchActivity");
			showTabHost = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(
					R.drawable.rec_dot_normal);
			dots.get(position)
					.setBackgroundResource(R.drawable.rec_dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private void getInfo() {
		String url = Constants.TAG_SEARCH;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject json = new JSONObject(result);
								array = json.getJSONArray("goods");
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
				}, 0);
	}
	@Override
	public void refresh() {
		super.refresh();

	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	// reBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	// private void reBack() {
	// Intent intent = new Intent(SearchActivity.this,
	// SortSearchActivity.class);
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
	// RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	// }

}
