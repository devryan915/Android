/**
 * @Title: SortSearchResultActivity.java
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGoodsDetailsActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.recommend.adapter.RecContentAdapter;
import com.kc.ihaigo.ui.recommend.adapter.RecContentAdapter.ViewHolder;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * @ClassName: SortSearchResultActivity
 * @Description: 精选优惠-分类搜索结果页
 * @author: ryan.wang
 * @date: 2014年6月26日 上午9:31:34
 * 
 */

public class SortSearchResultActivity extends IHaiGoActivity implements
		OnItemClickListener {
	private PullUpRefreshListView res_ll_content;
	private RecContentAdapter rcadapter;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sortsearch_results);
		// initTitle();
		initComponets();
	}

	@Override
	public void refresh() {
		super.refresh();
		searchKey = (String) getIntent().getCharSequenceExtra("sortname");
		initQueryParams();
		loadData(searchKey);
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

	private void loadData(String searchKey) {
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
	// 定义返回功能
	// ImageView title_btn_left = (ImageView) findViewById(R.id.title_left);
	// title_btn_left.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// reBack();
	// }
	// });
	// }

	@SuppressWarnings("unchecked")
	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.recommend.SortSearchActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}

	private void initComponets() {
		res_ll_content = (PullUpRefreshListView) findViewById(R.id.res_content_ll);
		rcadapter = new RecContentAdapter(SortSearchResultActivity.this);
		res_ll_content.setAdapter(rcadapter);
		res_ll_content.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {
				if (!isLoading) {
					curpage++;
					loadData(searchKey);
				}
			}
		});
		res_ll_content.setOnItemClickListener(this);
		findViewById(R.id.queryhot).setOnClickListener(this);
		findViewById(R.id.discount).setOnClickListener(this);
		findViewById(R.id.price).setOnClickListener(this);
		ssr__indicator_shape_hot = (ImageView) findViewById(R.id.ssr__indicator_shape_hot);
		ssr__indicator_shape_discount = (ImageView) findViewById(R.id.ssr__indicator_shape_discount);
		ssr__indicator_shape_price = (ImageView) findViewById(R.id.ssr__indicator_shape_price);
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
	// Intent intent = new Intent(SortSearchResultActivity.this,
	// SortSearchActivity.class);
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
	// RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.queryhot:
			ssr__indicator_shape_hot.setVisibility(View.VISIBLE);
			ssr__indicator_shape_discount.setVisibility(View.GONE);
			ssr__indicator_shape_price.setVisibility(View.GONE);
			category = SORT_HOT;
			break;
		case R.id.discount:
			ssr__indicator_shape_hot.setVisibility(View.GONE);
			ssr__indicator_shape_discount.setVisibility(View.VISIBLE);
			ssr__indicator_shape_price.setVisibility(View.GONE);
			category = SORT_DISCOUNT;
			break;
		case R.id.price:
			ssr__indicator_shape_hot.setVisibility(View.GONE);
			ssr__indicator_shape_discount.setVisibility(View.GONE);
			ssr__indicator_shape_price.setVisibility(View.VISIBLE);
			category = SORT_PRICE;
			break;
		default:
			break;
		}
		initQueryParams();
		loadData(searchKey);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long itemId) {
		Intent intent = new Intent(SortSearchResultActivity.this,
				PersonalGoodsDetailsActivity.class);
		Bundle reqParams = new Bundle();
		RecContentAdapter.ViewHolder holder = (ViewHolder) view.getTag();
		String code = (String) holder.price.getTag();
		DataConfig dcConfig = new DataConfig(SortSearchResultActivity.this);
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
		PersonalGroupActivity.group.startiHaiGoActivity(intent,
				RecommendGroupActiviy.group);
	}

}
