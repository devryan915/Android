/**
 * 
 */

package com.kc.ihaigo.ui.recommend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.RecContentAdapter;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * 
 * @ClassName: AdvActivity
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年8月1日 上午10:28:38
 * 
 */

public class AdvActivity extends IHaiGoActivity {
	private PullUpRefreshListView res_ll_content;
	private RecContentAdapter rcadapter;
	private int curpage = 1;// 记录当前是页码
	private boolean isLoading = false;
	private boolean isLoadAll = false;// 是否加载全部数据;
	private int lastReqLength;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adv);
		// initTitle();
		initComponets();
	}
	@Override
	public void refresh() {
		initQueryParams();
		loadData();
	}
	private void initQueryParams() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
		rcadapter.setDatas(new JSONArray());
	}
	/**
	 * @Title: loadData
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void loadData() {
		isLoading = true;
		// String url = Constants.REC_SEARCH + "?category=0&criteria=奶粉辅食";
		String url = Constants.REC_GOODS + "?page=" + curpage + "&size="
				+ Constants.APP_DATA_LENGTH;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						try {
							if (!TextUtils.isEmpty(result)) {
								JSONObject jsonbject = new JSONObject(result);
								JSONArray datas = jsonbject
										.getJSONArray("goods");
								JSONArray oldDatas = rcadapter.getDatas();
								boolean isNeedRefresh = false;
								if (isLoadAll) {
									for (int i = lastReqLength; i < datas
											.length(); i++) {
										oldDatas.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								} else {
									for (int i = 0; i < datas.length(); i++) {
										oldDatas.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
									rcadapter.notifyDataSetChanged();
								}
								if (datas.length() < Constants.APP_DATA_LENGTH) {
									isLoadAll = true;
									// 设置成前一页，允许用户重复请求当前页以便抓取服务端最新数据
									// if (curpage > 1) {
									curpage--;
									// if (Constants.Debug) {
									// Log.d(TAG, "停留当前页" + curpage + "页");
									// }
									// }
									lastReqLength = datas.length();
								} else {
									isLoadAll = false;
								}
								isLoading = false;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

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
		res_ll_content = (PullUpRefreshListView) findViewById(R.id.res_content_ll);
		rcadapter = new RecContentAdapter(AdvActivity.this);
		res_ll_content.setAdapter(rcadapter);
		res_ll_content.setScrollBottomListener(new ScrollBottomListener() {
			@Override
			public void deal() {
				if (!isLoading) {
					curpage++;
					loadData();
				}
			}
		});
	}
	/*
	 * <p>Title: back</p> <p>Description: </p>
	 * 
	 * @see com.kc.ihaigo.IHaiGoActivity#back()
	 */

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
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	// reBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	// private void reBack() {
	// Intent intent = new Intent(AdvActivity.this, SortSearchActivity.class);
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
	// RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	// }

	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.queryhot :
	// ssr__indicator_shape_hot.setVisibility(View.VISIBLE);
	// ssr__indicator_shape_discount.setVisibility(View.GONE);
	// ssr__indicator_shape_price.setVisibility(View.GONE);
	// category = SORT_HOT;
	// break;
	// case R.id.discount :
	// ssr__indicator_shape_hot.setVisibility(View.GONE);
	// ssr__indicator_shape_discount.setVisibility(View.VISIBLE);
	// ssr__indicator_shape_price.setVisibility(View.GONE);
	// category = SORT_DISCOUNT;
	// break;
	// case R.id.price :
	// ssr__indicator_shape_hot.setVisibility(View.GONE);
	// ssr__indicator_shape_discount.setVisibility(View.GONE);
	// ssr__indicator_shape_price.setVisibility(View.VISIBLE);
	// category = SORT_PRICE;
	// break;
	// default :
	// break;
	// }
	// initQueryParams();
	// loadData();
	// }

}
