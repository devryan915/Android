/**
 * @Title: SortSearchActivity.java
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.MenucAdapter;
import com.kc.ihaigo.ui.recommend.adapter.MenucAdapter.ViewHolder;
import com.kc.ihaigo.ui.recommend.adapter.MenufAdapter;
import com.kc.ihaigo.util.DataConfig;

/**
 * @ClassName: SortSearchActivity
 * @Description: 精选优惠-分类搜索
 * @author: ryan.wang
 * @date: 2014年6月26日 上午9:31:34
 * 
 */

public class SortSearchActivity extends IHaiGoActivity {

	private ListView sortsearch_menuf_ll;
	private GridView sortsearch_menuc_gv;
	private MenufAdapter adapter;
	private MenucAdapter adapterc;
	private LinearLayout sortview_hot_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sort_search);
		// initTitle();
		initComponets();
		loadCategory();
		loadHotkeys();
	}

	/**
	 * @Title: loadHotkeys
	 * @user: ryan.wang
	 * @Description: 初始化热门搜索
	 * @throws
	 */

	private void loadHotkeys() {
		// String url = Constants.REC_SEARCH_HOTS;
		// HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
		// new HttpReqCallBack() {
		//
		// @Override
		// public void deal(String result) {
		// try {
		// JSONObject object = new JSONObject(result);
		// JSONArray hots = new JSONArray();
		for (int i = 0; i < 4; i++) {
			TextView tv = (TextView) getLayoutInflater().inflate(
					R.layout.sortsearch_hotsearch_textview, null);
			tv.setText("蛋白粉");
			sortview_hot_layout.addView(tv);
		}
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// });
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.recommend.RecommendActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}
	/**
	 * @Title: loadCategory
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void loadCategory() {
		DataConfig dConfig = new DataConfig(SortSearchActivity.this);
		String category = dConfig.getCategory();
		if (!TextUtils.isEmpty(category)) {
			try {
				JSONObject dataJsonObject = new JSONObject(category);
				JSONArray categoryArray = dataJsonObject
						.getJSONArray("category");
				MenuItemClickListener itemClick = new MenuItemClickListener();
				itemClick.setFlag(0);
				sortsearch_menuf_ll = (ListView) findViewById(R.id.sortsearch_menuf_ll);
				adapter = new MenufAdapter(SortSearchActivity.this);
				adapter.setDatas(categoryArray);
				sortsearch_menuf_ll.setAdapter(adapter);
				sortsearch_menuf_ll.setOnItemClickListener(itemClick);
				itemClick = new MenuItemClickListener();
				itemClick.setFlag(1);
				sortsearch_menuc_gv = (GridView) findViewById(R.id.sortsearch_menuc_gv);
				adapterc = new MenucAdapter(SortSearchActivity.this);
				sortsearch_menuc_gv.setAdapter(adapterc);
				sortsearch_menuc_gv.setOnItemClickListener(itemClick);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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
		TextView sortsearch_search_tv = (TextView) findViewById(R.id.sortsearch_search_tv);
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
		sortsearch_search_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SortSearchActivity.this,
						SearchActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				RecommendGroupActiviy.group.startiHaiGoActivity(intent);
			}
		});

		// 热门搜索
		sortview_hot_layout = (LinearLayout) findViewById(R.id.sortview_hot_layout);
	}

	class MenuItemClickListener implements OnItemClickListener {
		private int flag;

		public void setFlag(int flag) {
			this.flag = flag;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (flag == 0) {
				View selView = (View) parent.getTag();
				TextView tv;
				ImageView iv;
				tv = (TextView) view.findViewById(R.id.sortsearch_menufname_tv);
				iv = (ImageView) view.findViewById(R.id.sortsearch_menusel_iv);
				tv.setTextColor(getResources().getColor(R.color.menufnamesel));
				iv.setBackgroundColor(getResources().getColor(
						R.color.menufimagesel));
				if (tv.getTag() != null) {
					adapterc.setDatas((JSONArray) tv.getTag());
				} else {
					adapterc.setDatas(null);
				}
				adapterc.notifyDataSetChanged();
				if (selView != null && selView != view) {
					tv = (TextView) selView
							.findViewById(R.id.sortsearch_menufname_tv);
					iv = (ImageView) selView
							.findViewById(R.id.sortsearch_menusel_iv);
					tv.setTextColor(getResources().getColor(R.color.menufname));
					iv.setBackgroundColor(getResources().getColor(
							R.color.transparent));
				}
				parent.setTag(view);
			} else if (flag == 1) {
				Intent intent = new Intent(SortSearchActivity.this,
						SortSearchResultActivity.class);
				ViewHolder holder = (ViewHolder) view.getTag();
				intent.putExtra("sortname", holder.menuName.getText());
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				RecommendGroupActiviy.group.startiHaiGoActivity(intent);
			}
		}
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
	// Intent intent = new Intent(SortSearchActivity.this,
	// RecommendActivity.class);
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
	// RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	// }

}
