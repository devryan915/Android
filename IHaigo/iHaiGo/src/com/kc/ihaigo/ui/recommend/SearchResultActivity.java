/**
 * @Title: SearchResultActivity.java
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

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGoodsDetailsActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.personal.PersonalJoinShopCartActivity;
import com.kc.ihaigo.ui.recommend.adapter.SearchResAdapter;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: SearchResultActivity
 * @Description: 精选优惠-分类搜索
 * @author: ryan.wang
 * @date: 2014年6月26日 上午9:31:34
 * 
 */

public class SearchResultActivity extends IHaiGoActivity
		implements
			OnItemClickListener {
	private PullUpRefreshListView res_ll_content;
	private LinearLayout search_hotsearch_hotkeys;
	private SearchResAdapter rcadapter;
	private int curpage = 1;// 记录当前是页码
	private boolean isLoading = false;
	private boolean isLoadAll = false;// 是否加载全部数据;
	private String searchKey = "";
	private int lastReqLength;
	private final int SORT_HOT = 1;
	private final int SORT_DISCOUNT = 2;
	private final int SORT_PRICE = 3;
	private int category = SORT_HOT;
	private ImageView ssr__indicator_shape_hot;
	private ImageView ssr__indicator_shape_discount;
	private ImageView ssr__indicator_shape_price;
	private EditText sortsearch_search_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		initComponets();
		loadHotkeys();
	}

	/**
	 * @Title: loadHotkeys
	 * @user: ryan.wang
	 * @Description: TODO void
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
			View v = getLayoutInflater().inflate(
					R.layout.search_hotsearch_textview, null);
			TextView tv = (TextView) (v.findViewById(R.id.hotkey));
			tv.setText("蛋白粉");
			search_hotsearch_hotkeys.addView(v);
		}
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// });

	}

	/**
	 * @Title: loadData
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void loadData() {
		isLoading = true;
		// String url = Constants.REC_SEARCH + "?category=0&criteria=searchKey";
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

	private void initComponets() {
		res_ll_content = (PullUpRefreshListView) findViewById(R.id.res_content_ll);
		rcadapter = new SearchResAdapter(SearchResultActivity.this);
		rcadapter.setCall(new BackCall() {
			@Override
			public void deal(int which, Object... obj) {
				Intent join_shoppingBut = new Intent(SearchResultActivity.this,
						PersonalJoinShopCartActivity.class);
				Bundle join = new Bundle();
				join.putString("gid", obj[0].toString());
				join.putString("price", obj[1].toString());
				join.putString("discount", obj[2].toString());
				join.putString("rmbprice", obj[3].toString());
				join.putString("curRate", obj[4].toString());
				join.putString("sourceurl", obj[5].toString());
				join.putString("goodsurl", obj[6].toString());
				join.putString("goodsname", obj[7].toString());
				join_shoppingBut.putExtras(join);
				join_shoppingBut.putExtra(
						IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				join_shoppingBut.putExtra(
						IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				RecommendGroupActiviy.group
						.startiHaiGoActivity(join_shoppingBut);
			}
		});
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
		res_ll_content.setOnItemClickListener(this);
		search_hotsearch_hotkeys = (LinearLayout) findViewById(R.id.search_hotsearch_hotkeys);
		findViewById(R.id.queryhot).setOnClickListener(this);
		findViewById(R.id.discount).setOnClickListener(this);
		findViewById(R.id.price).setOnClickListener(this);
		ssr__indicator_shape_hot = (ImageView) findViewById(R.id.ssr__indicator_shape_hot);
		ssr__indicator_shape_discount = (ImageView) findViewById(R.id.ssr__indicator_shape_discount);
		ssr__indicator_shape_price = (ImageView) findViewById(R.id.ssr__indicator_shape_price);
		sortsearch_search_tv = ((EditText) findViewById(R.id.sortsearch_search_tv));
		sortsearch_search_tv
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
		sortsearch_search_tv
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						searchKey = v.getText().toString();
						Utils.hideInputMethod(SearchResultActivity.this);
						initQueryParams();
						loadData();
						return true;
					}
				});
	}

	@Override
	public void refresh() {
		searchKey = (String) getIntent().getCharSequenceExtra("sortname");
		sortsearch_search_tv.setText(searchKey);
		rcadapter.setDatas(new JSONArray());
		loadData();
	}

	private void initQueryParams() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
		rcadapter.setDatas(new JSONArray());
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	// reBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	//
	// private void reBack() {
	// Intent intent = new Intent(SearchResultActivity.this,
	// RecommendActivity.class);
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
	// RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	// }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.queryhot :
				ssr__indicator_shape_hot.setVisibility(View.VISIBLE);
				ssr__indicator_shape_discount.setVisibility(View.GONE);
				ssr__indicator_shape_price.setVisibility(View.GONE);
				category = SORT_HOT;
				break;
			case R.id.discount :
				ssr__indicator_shape_hot.setVisibility(View.GONE);
				ssr__indicator_shape_discount.setVisibility(View.VISIBLE);
				ssr__indicator_shape_price.setVisibility(View.GONE);
				category = SORT_DISCOUNT;
				break;
			case R.id.price :
				ssr__indicator_shape_hot.setVisibility(View.GONE);
				ssr__indicator_shape_discount.setVisibility(View.GONE);
				ssr__indicator_shape_price.setVisibility(View.VISIBLE);
				category = SORT_PRICE;
				break;
			default :
				break;
		}
		initQueryParams();
		loadData();
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.recommend.SearchActivity");
			showTabHost = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long itemId) {
		Intent intent = new Intent(SearchResultActivity.this,
				PersonalGoodsDetailsActivity.class);
		Bundle reqParams = new Bundle();
		SearchResAdapter.ViewHolder holder = (SearchResAdapter.ViewHolder) view
				.getTag();
		String code = (String) holder.price.getTag();
		DataConfig dcConfig = new DataConfig(SearchResultActivity.this);
		String symbol = dcConfig.getCurrencySymbolByCode(code);
		String curRate = "1"
				+ dcConfig.getCurrencyNameByCode(code)
				+ "="
				+ new BigDecimal(dcConfig.getCurRateByCode(code)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue()
				+ getText(R.string.renminbi);
		String codeNmae = dcConfig.getCurrencyNameByCode(code);
		// 商品ID
		reqParams.putLong("gid", itemId);
		// 商品LOGO
		reqParams.putString("icon", holder.rec_goods_img.getTag() + "");
		// 商品名称
		reqParams.putString("name", holder.rec_goodsname_tv.getText() + "");

		// 旧美元
		reqParams.putString("pri", holder.price.getText() + "");
		// 新美元
		reqParams.putString("price_disc", holder.discount.getText() + "");
		// 人民币
		reqParams.putString("Ram", holder.rmbprice.getText() + "");

		// 汇率
		reqParams.putString("curRate", curRate);
		//
		reqParams.putString("codeNmae", codeNmae);
		// 来源
		reqParams.putString("source", holder.source.getTag() + "");

		// 币种
		reqParams.putString("symbol", symbol);
		intent.putExtras(reqParams);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		PersonalGroupActivity.group.startiHaiGoActivity(intent,RecommendGroupActiviy.group);
	}
}
