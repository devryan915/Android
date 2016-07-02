/**
 * @Title: PurchasingAgent.java
 * @Package: com.kc.ihaigo.ui.shopcar
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月5日 上午9:10:58

 * @version V1.0

 */

package com.kc.ihaigo.ui.shopcar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.shopcar.adapter.PurAgentAgentsAdapter;
import com.kc.ihaigo.ui.shopcar.adapter.PurAgentAgentsAdapter.ViewHolder;
import com.kc.ihaigo.ui.shopcar.adapter.PurAgentGoodsAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * @ClassName: PurchasingAgent
 * @Description: 购物车选择代购商
 * @author: ryan.wang
 * @date: 2014年7月5日 上午9:10:58
 * 
 */

public class PurchasingAgent extends IHaiGoActivity {

	private PurAgentGoodsAdapter adapter;
	private PurAgentAgentsAdapter agentAdapter;
	private int curpage = 1;// 记录当前是页码
	private boolean isLoading = false;
	private boolean isLoadAll = false;// 是否加载全部数据;
	private int lastReqLength = 0;// 上一次请求到的数据长度
	private int querytype = SORT_PRICE;
	private View queryview = null;
	private final static int SORT_DEFAULT = 0;
	private final static int SORT_PRICE = 1;
	private final static int SORT_LOGISTICS = 2;
	private final static int SORT_SERVICE = 3;
	private JSONObject billParam = new JSONObject();
	private JSONArray datasParams = null;
	private String totalquantity;
	private Class<IHaiGoActivity> lparentClass;

	private String TAG;
	private String totalrmb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchasing_agent);
		// initTitle();
		initComponents();

	}

	/**
	 * @Title: loadAgents
	 * @user: ryan.wang
	 * @Description: 获取代购商
	 * @throws
	 */
	private void loadAgents(int sorttype) {
		isLoading = true;
		String url = Constants.SHOPCAR_AGENT + "getAgentsList?total="
				+ totalrmb + "&sort=" + sorttype + "&page=" + curpage
				+ "&pagesize=" + Constants.APP_DATA_LENGTH;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								JSONArray datas = resData
										.getJSONArray("agents");
								JSONArray oldDatas = agentAdapter.getDatas();
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
									agentAdapter.notifyDataSetChanged();
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
							} catch (NotFoundException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
				}, 10);
	}

	/**
	 * @Title: initComponents
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initComponents() {
		ListView puragent_goodslist = (ListView) findViewById(R.id.puragent_goodslist);
		adapter = new PurAgentGoodsAdapter(PurchasingAgent.this);
		adapter.setCount(3);
		puragent_goodslist.setAdapter(adapter);
		PullUpRefreshListView puragent_sort_list = (PullUpRefreshListView) findViewById(R.id.puragent_sort_list);
		puragent_sort_list.setScrollBottomListener(new ScrollBottomListener() {
			@Override
			public void deal() {
				if (!isLoading) {
					curpage++;
					loadAgents(SORT_PRICE);
				}
			}
		});
		agentAdapter = new PurAgentAgentsAdapter(PurchasingAgent.this);
		puragent_sort_list.setAdapter(agentAdapter);
		findViewById(R.id.puragent_goodslist_more).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						v.setVisibility(View.GONE);
						adapter.setCount(-1);
						adapter.notifyDataSetChanged();
					}
				});
		puragent_sort_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				Intent intent = new Intent(PurchasingAgent.this,
						PuragentDetail.class);
				Bundle reqParams = new Bundle();
				reqParams.putLong("id", id);
				try {
					billParam.put("agent", id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				reqParams.putString("image", holder.puragent_head.getTag()
						.toString());
				reqParams.putString("name", holder.puragent_name.getText()
						.toString());
				reqParams.putFloat("credit",
						holder.puragent_credit_rating.getRating());
				reqParams.putString("price", holder.puragent_priceval.getText()
						.toString());
				reqParams.putString("feeval", holder.puragent_feeval.getText()
						.toString());
				reqParams.putString("shippingval", holder.puragent_shippingval
						.getText().toString());
				reqParams.putString("serviceval", holder.puragent_serviceval
						.getText().toString());
				reqParams.putString("totalquantity", totalquantity);
				reqParams.putString("totalrmb", totalrmb);
				reqParams.putString("billParam", billParam.toString());
				reqParams.putString("datasParams", datasParams.toString());
				intent.putExtras(reqParams);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
				ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
			}
		});
		findViewById(R.id.puragent_sort_default).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (queryview != null) {
							((TextView) queryview).setCompoundDrawables(null,
									null, null, null);
							((TextView) queryview).setTextColor(getResources()
									.getColor(R.color.purchasesortnormal));
						}
						Drawable drawable = getResources().getDrawable(
								R.drawable.puragent_agent_indicator);
						// / 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
								drawable.getMinimumHeight());
						((TextView) v).setCompoundDrawables(null, null, null,
								drawable);
						((TextView) v).setTextColor(getResources().getColor(
								R.color.purchasesortsel));
						queryview = v;
						initVar();
						querytype = SORT_PRICE;
						loadAgents(SORT_PRICE);
					}
				});
		findViewById(R.id.puragent_sort_price).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (queryview != null) {
							((TextView) queryview).setCompoundDrawables(null,
									null, null, null);
							((TextView) queryview).setTextColor(getResources()
									.getColor(R.color.purchasesortnormal));
						}
						Drawable drawable = getResources().getDrawable(
								R.drawable.puragent_agent_indicator);
						// / 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
								drawable.getMinimumHeight());
						((TextView) v).setCompoundDrawables(null, null, null,
								drawable);
						((TextView) v).setTextColor(getResources().getColor(
								R.color.purchasesortsel));
						queryview = v;
						initVar();
						querytype = SORT_PRICE;
						loadAgents(SORT_PRICE);
					}
				});
		findViewById(R.id.puragent_sort_speed).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (queryview != null) {
							((TextView) queryview).setCompoundDrawables(null,
									null, null, null);
							((TextView) queryview).setTextColor(getResources()
									.getColor(R.color.purchasesortnormal));
						}
						Drawable drawable = getResources().getDrawable(
								R.drawable.puragent_agent_indicator);
						// / 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
								drawable.getMinimumHeight());
						((TextView) v).setCompoundDrawables(null, null, null,
								drawable);
						((TextView) v).setTextColor(getResources().getColor(
								R.color.purchasesortsel));
						queryview = v;
						initVar();
						querytype = SORT_LOGISTICS;
						loadAgents(SORT_LOGISTICS);
					}
				});
		findViewById(R.id.puragent_sort_service).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (queryview != null) {
							((TextView) queryview).setCompoundDrawables(null,
									null, null, null);
							((TextView) queryview).setTextColor(getResources()
									.getColor(R.color.purchasesortnormal));
						}
						Drawable drawable = getResources().getDrawable(
								R.drawable.puragent_agent_indicator);
						// / 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
								drawable.getMinimumHeight());
						((TextView) v).setCompoundDrawables(null, null, null,
								drawable);
						((TextView) v).setTextColor(getResources().getColor(
								R.color.purchasesortsel));
						queryview = v;
						initVar();
						querytype = SORT_SERVICE;
						loadAgents(SORT_SERVICE);
					}
				});
		queryview = findViewById(R.id.puragent_sort_default);
	}

	private void initVar() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
		agentAdapter.setDatas(new JSONArray());
	}

	@Override
	public void refresh() {
		adapter.setDatas(new JSONArray());
		adapter.notifyDataSetChanged();
		findViewById(R.id.puragent_goodslist_more).setVisibility(View.GONE);
		Bundle bundle;
		if (ShopCarActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			bundle = getIntent().getExtras();
			totalquantity = bundle.getString("totalquantity");
			totalrmb = bundle.getString("totalrmb");
			((TextView) findViewById(R.id.puragent_quantity_val))
					.setText(totalquantity);
			((TextView) findViewById(R.id.puragent_discounted_val)).setText("￥"
					+ totalrmb);
			// 初始化已购商品
			String goods = (String) bundle.getString("datas");
			if (!TextUtils.isEmpty(goods)) {
				try {
					datasParams = new JSONArray(goods);
					JSONArray goodsDatas = new JSONArray();
					StringBuffer itemIds = null;
					for (int i = 0; i < datasParams.length(); i++) {
						JSONArray childItems = datasParams.getJSONObject(i)
								.getJSONArray("childitems");
						for (int j = 0; j < childItems.length(); j++) {
							JSONObject data = childItems.getJSONObject(j);
							JSONObject goodsData = new JSONObject();
							goodsData.put("name", data.getString("name"));
							goodsData.put("amount", data.getString("amount"));
							goodsDatas.put(goodsData);
							if (itemIds == null) {
								itemIds = new StringBuffer(data.getString("id"));
							} else {
								itemIds.append("," + data.getString("id"));
							}
						}
					}
					billParam.put("itemids", itemIds);
					if (goodsDatas.length() < 4) {
						findViewById(R.id.puragent_goodslist_more)
								.setVisibility(View.GONE);
					} else {
						findViewById(R.id.puragent_goodslist_more)
								.setVisibility(View.VISIBLE);
					}
					adapter.setDatas(goodsDatas);
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
		initVar();
		loadAgents(SORT_PRICE);
	}
	/*
	 * <p>Title: back</p> <p>Description: </p>
	 * 
	 * @see com.kc.ihaigo.IHaiGoActivity#back()
	 */

	@Override
	protected void back() {
		parentClass = lparentClass;
		showTabHost = true;
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

	// protected void reBack() {
	// if (TAG.equals("ShopCarActivity")) {
	// Intent intent = new Intent(PurchasingAgent.this,
	// ShopCarActivity.class);
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
	// ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
	// }
	//
	// }

	/**
	 * @Title: initTitle
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	// private void initTitle() {
	// }
}
