/**
 * @Title: PersonalLogisticsActivity.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月10日 下午4:17:34

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.CollectionAdapter;
import com.kc.ihaigo.ui.personal.adapter.PersonalShippingAdapter;
import com.kc.ihaigo.ui.personal.adapter.PersonalShippingAdapters;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * 
 * 个人中心--我的物流
 * 
 * @ClassName: PersonalLogisticsActivity
 * @Description: TODO
 * @author: zouxianbin
 * @date: 2014年7月10日 下午4:17:34
 * 
 */

public class PersonalLogisticsActivity extends IHaiGoActivity implements
		OnClickListener {
	/**
	 * 国内物流
	 */
	private TextView domestic;

	/**
	 * 海外物流
	 */
	private TextView overseas;
	/**
	 * 全部订单
	 */
	private TextView all_way_bill;
	private PullUpRefreshListView shipping_list;
	private PullUpRefreshListView ship_list;

	private PersonalShippingAdapter adapter;
	private PersonalShippingAdapters adapters;

	private String tagString;
	private String domestic_TAG = "1";
	private String overseas_TAG = "1";

	/**
	 * 请求数据时,是否检索请求. 默认 0 全部 1、一个月2、三个月3、六个月
	 */
	private String type = "0";

	private RelativeLayout empty;
	private LinearLayout have;

	private Integer curpage = 1;// 记录当前是页码
	private Boolean isLoading = false;
	private Boolean isLoadAll = false;// 是否加载全部数据;
	private Integer lastReqLength = 0;// 上一次请求到的数据长度

	private Integer curpages = 1;// 记录当前是页码
	private Boolean isLoadings = false;
	private Boolean isLoadAlls = false;// 是否加载全部数据;
	private Integer lastReqLengths = 0;// 上一次请求到的数据长度

	private String TAG_IN = "domestic";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiity_personal_logistics);
		empty = (RelativeLayout) findViewById(R.id.empty);
		have = (LinearLayout) findViewById(R.id.have);
		have.setVisibility(View.GONE);

		initTitle();
		initCooseIitle();
		initComponets();

	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */

	private void initCooseIitle() {
		domestic = (TextView) findViewById(R.id.domestic);
		domestic.setOnClickListener(this);
		domestic.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
		domestic.setTextColor(this.getResources().getColor(R.color.white));

		overseas = (TextView) findViewById(R.id.overseas);
		overseas.setOnClickListener(this);
		overseas.setBackgroundResource(R.drawable.choose_item_right_shape);
		overseas.setTextColor(this.getResources().getColor(R.color.choose));
	}

	/**
	 * 
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		all_way_bill = (TextView) findViewById(R.id.all_way_bill);
		all_way_bill.setOnClickListener(this);

	}

	private void initComponets() {
		shipping_list = (PullUpRefreshListView) findViewById(R.id.shipping_list);
		shipping_list.setVisibility(View.VISIBLE);

		adapter = new PersonalShippingAdapter(this);
		adapter.setDatas(new JSONArray());
		shipping_list.setAdapter(adapter);

		shipping_list.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isLoading) {
					curpage++;
					getLogisticsSystem(type);
				}

			}
		});

		ship_list = (PullUpRefreshListView) findViewById(R.id.ship_list);
		ship_list.setVisibility(View.GONE);
		adapters = new PersonalShippingAdapters(this);
		adapters.setDatas(new JSONArray());
		ship_list.setAdapter(adapters);
		ship_list.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isLoadings) {
					curpages++;
					getLogisticsManual(type);
				}

			}
		});
		getLogisticsSystem(type);

	}

	private void getLogisticsSystem(String type) {
		isLoading = true;

		/**
		 * 系统生成物流列表
		 */

		final String url = Constants.REC_SYSTEM + "userId=" + Constants.USER_ID
				+ "&page=" + curpage + "&pagesize=" + Constants.APP_DATA_LENGTH
				+ "&type=" + type;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {

						try {

							if (!TextUtils.isEmpty(result)) {
								have.setVisibility(View.VISIBLE);
								empty.setVisibility(View.GONE);
								JSONObject jsonObject = new JSONObject(result);
								JSONArray datas = jsonObject
										.getJSONArray("logistics");
								JSONArray json = adapter.getDatas();

								boolean isNeedRefresh = false;
								if (isLoadAll) {
									for (int i = lastReqLength; i < datas
											.length(); i++) {
										json.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								} else {
									for (int i = 0; i < datas.length(); i++) {
										json.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
									adapter.notifyDataSetChanged();
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

							} else {
								have.setVisibility(View.GONE);
								empty.setVisibility(View.VISIBLE);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, 0, R.string.loading);

	}

	private void getLogisticsManual(String type) {

		/**
		 * 手动生成物流列表
		 */

		final String url = Constants.REC_MANUAL + "userId=" + Constants.USER_ID
				+ "&page=" + curpages + "&pagesize="
				+ Constants.APP_DATA_LENGTH + "&type=" + type;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {

						try {

							if (!TextUtils.isEmpty(result)) {
								JSONObject jsonObject = new JSONObject(result);
								JSONArray datas = jsonObject
										.getJSONArray("logistics");

								JSONArray json = adapters.getDatas();

								boolean isNeedRefreshs = false;
								if (isLoadAlls) {
									for (int i = lastReqLengths; i < datas
											.length(); i++) {
										json.put(datas.getJSONObject(i));
										isNeedRefreshs = true;
									}
								} else {
									for (int i = 0; i < datas.length(); i++) {
										json.put(datas.getJSONObject(i));
										isNeedRefreshs = true;
									}
								}
								if (isNeedRefreshs) {
									adapters.notifyDataSetChanged();
								}

								if (datas.length() < Constants.APP_DATA_LENGTH) {
									isLoadAlls = true;
									// 设置成前一页，允许用户重复请求当前页以便抓取服务端最新数据
									// if (curpage > 1) {
									curpages--;
									// if (Constants.Debug) {
									// Log.d(TAG, "停留当前页" + curpage + "页");
									// }
									// }
									lastReqLengths = datas.length();
								} else {
									isLoadAlls = false;
								}

								isLoadings = false;
							} else {
								have.setVisibility(View.GONE);
								empty.setVisibility(View.VISIBLE);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, 0, R.string.loading);

	}

	/*
	 * <p>Title: onClick</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.all_way_bill:
			String string = all_way_bill.getText().toString().trim();
			Intent inte = new Intent(PersonalLogisticsActivity.this,
					AllWayBillActivity.class);

			Bundle bun = new Bundle();
			bun.putString("key", string);
			bun.putString("TAG", TAG_IN);
			inte.putExtras(bun);
			inte.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			inte.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(inte);
			break;
		case R.id.domestic:
			TAG_IN = "domestic";
			domestic_TAG = "2";
			if (domestic_TAG.equals("1")) {
				// 不为空再执行
				getLogisticsSystem(type);

			}
			adapter.notifyDataSetChanged();
			domestic.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
			domestic.setTextColor(this.getResources().getColor(R.color.white));

			overseas.setBackgroundResource(R.drawable.choose_item_right_shape);
			overseas.setTextColor(this.getResources().getColor(R.color.choose));
			shipping_list.setVisibility(View.VISIBLE);
			ship_list.setVisibility(View.GONE);
			break;

		case R.id.overseas:
			TAG_IN = "overseas";
			if (overseas_TAG.equals("1")) {
				getLogisticsManual(type);
				overseas_TAG = "2";

			}
			adapters.notifyDataSetChanged();
			ship_list.setVisibility(View.VISIBLE);
			shipping_list.setVisibility(View.GONE);
			domestic.setBackgroundResource(R.drawable.choose_item_lift_shape);
			domestic.setTextColor(this.getResources().getColor(R.color.choose));

			overseas.setBackgroundResource(R.drawable.choose_item_right_selected_shape);
			overseas.setTextColor(this.getResources().getColor(R.color.white));

			break;

		default:
			break;
		}

	}

	@Override
	public void refresh() {
		super.refresh();
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度

		curpages = 1;// 记录当前是页码
		isLoadings = false;
		isLoadAlls = false;// 是否加载全部数据;
		lastReqLengths = 0;// 上一次请求到的数据长度

		Bundle resParams = getIntent().getExtras();

		tagString = resParams.getString("String");
		all_way_bill.setText(tagString);
		type = resParams.getString("type");

	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
			showTabHost = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}

}
