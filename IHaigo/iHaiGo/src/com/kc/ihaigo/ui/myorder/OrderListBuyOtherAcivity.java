package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.adapter.myorder.MyAdapter;
import com.kc.ihaigo.adapter.myorder.MyOrderAdapter;
import com.kc.ihaigo.model.myorder.Orders;
import com.kc.ihaigo.model.myorder.OrdersInfo;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @Description: 代购订单------列表
 * @author: @author: Lijie
 * @date: 2014年7月17日 下午4:09:18
 * 
 * **/
public class OrderListBuyOtherAcivity extends IHaiGoActivity implements
		OnClickListener {
	private OrdersInfo dataList = new OrdersInfo();

	private PullUpRefreshListView mPullToRefreshListView;
	private WrapListView mPullToRefreshListView_item;
	protected MyOrderAdapter mAdapter;
	protected MyAdapter myAdapter;
	protected View view;
	protected Orders TestData;
	private Button myorder_goods_remove;
	private JSONArray goodsArray;
	private String Status;
	private String Orderid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_other_order_list);
		initView();
		ininData();
		// Intent intent = new Intent(MyOrderAcivity.this,
		// TransUncompleteComDetail.class);
		// Bundle bundle = new Bundle();
		// startActivity(intent);
	}

	@Override
	protected void back() {
		// TODO Auto-generated method stub
		showTabHost = true;

		super.back();
	}

	private void ininData() {
		// TODO Auto-generated method stub
		// String url =
		// String url = "http://api.ihaigo.com/ihaigo/orders/?user=1";//
		// Constants.USER_ID
		// =9
//		String url = "http://192.168.1.4:8080/orders/?user=21";
		
		String url = "http://192.168.1.4:8080/orders/?user="+Constants.USER_ID;
		Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(result)) {
							Log.e("返回数据-----", result);
							// data = JSONUtils.fromJson(result,
							// new TypeToken<List<OrdersInfo>>() {
							// });
							// dataList = JSONUtils.fromJson(result,
							// OrdersInfo.class);

							// Log.e("result--------------",dataList.toString()+""
							// );
							try {
								JSONObject jsobj = new JSONObject(result);
								goodsArray = jsobj.getJSONArray("orders");
								for (int i = 0; i < goodsArray.length(); i++) {
									goodsArray.getJSONObject(i).getString(
											"status");
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// Log.e("data.orders-----",
							// data.get(0).orders.toString() + "");

							mAdapter = new MyOrderAdapter(
									OrderListBuyOtherAcivity.this, goodsArray,
									mPullToRefreshListView_item);

							mPullToRefreshListView.setAdapter(mAdapter);
							setListViewHeightBasedOnChildren(mPullToRefreshListView);
						}

					}

				}, 1, R.string.loading);
		mPullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {
					// 订单详情---------
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						try {
							Status = goodsArray.getJSONObject(position)
									.getString("status");
							Orderid = goodsArray.getJSONObject(position)
									.getString("id");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent = new Intent();

						if (Status.equals("1")) {

							intent.setClass(OrderListBuyOtherAcivity.this,
									SubmitedOrderDetailActivity.class);
							intent.putExtra("id", Orderid);
							startActivity(intent);
						} else if (Status.equals("2")) {
							intent.setClass(OrderListBuyOtherAcivity.this,
									PayedOrderDetailActivity.class);
							intent.putExtra("id", Orderid);
							startActivity(intent);

						}

						else if (Status.equals("3")) {
							intent.setClass(OrderListBuyOtherAcivity.this,
									SentGoodsOrderDetailActivity.class);
							intent.putExtra("id", Orderid);
							startActivity(intent);

						} else if (Status.equals("4")) {
							intent.setClass(OrderListBuyOtherAcivity.this,
									CompletedOrderDetailActivity.class);
							intent.putExtra("id", Orderid);
							startActivity(intent);
						}
					}

				});

	}
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
	private void initView() {
		// TODO Auto-generated method stub

		mPullToRefreshListView = (PullUpRefreshListView) findViewById(R.id.lv_myorder);
		mPullToRefreshListView_item = (WrapListView) findViewById(R.id.lv_myorder_inner);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
//			Intent intent = new Intent(OrderListBuyOtherAcivity.this,
//					OrderTabActivity.class);
//			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
//			startActivity(intent);

			break;

		default:
			break;
		}
	}

}
