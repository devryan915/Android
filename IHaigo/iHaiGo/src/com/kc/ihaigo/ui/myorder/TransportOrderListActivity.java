package com.kc.ihaigo.ui.myorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.adapter.myorder.TransportOrderAdapter;
import com.kc.ihaigo.model.myorder.Items;
import com.kc.ihaigo.model.myorder.TransportOrderListModel;
import com.kc.ihaigo.model.myorder.TransportOrders;
import com.kc.ihaigo.model.myorder.Vegetable;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.JSONUtils;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @Description: 转运订单列表
 * @author: @author: Lijie
 * @date: 2014年7月28日 下午4:09:18
 * */
public class TransportOrderListActivity extends IHaiGoActivity implements
		OnClickListener {

	private PullUpRefreshListView lv_transport_order_list;
	private WrapListView lv_transport_order_inner;
	private TransportOrderAdapter mAdapter;
	private List<Vegetable> list = new ArrayList<Vegetable>();
	private int scrollState;
	JSONArray Orders;
	TransportOrderListModel transportOrderListModel;
	List<TransportOrders> DataList;
	List<Items> DataInnerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_order_list);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		lv_transport_order_list = (PullUpRefreshListView) findViewById(R.id.lv_transport_order_list);
		lv_transport_order_inner = (WrapListView) findViewById(R.id.lv_transport_order_inner);

	}

	/****
	 * 请求地址：http://{host}/transports/ 请求参数：user-用户编号 page->页数,非必须
	 * size->页面显示条数,非必须
	 * 
	 * **/
	private void initData() {
		// TODO Auto-generated method stub

//		String url = "http://192.168.1.4:8080/transports/?page=1&pagesize=10&user=9";
		String url = "http://192.168.1.4:8080/transports/?page=1&pagesize=10&user=9";
		Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(result)) {
							transportOrderListModel = JSONUtils.fromJson(
									result, TransportOrderListModel.class);
							DataList = transportOrderListModel.orders;
							mAdapter = new TransportOrderAdapter(
									TransportOrderListActivity.this, DataList,
									lv_transport_order_inner);
							lv_transport_order_list.setAdapter(mAdapter);
							lv_transport_order_list
									.setOnItemClickListener(new OnItemClickListener() {

										@SuppressWarnings("deprecation")
										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											Intent intent = new Intent();
											intent.putExtra("orderId", DataList
													.get(position).id
													.toString());
											intent.putExtra("status", DataList
													.get(position).status
													.toString());

											if (DataList.get(position).status
													.toString().equals("1")) {// 未完成
																				// 订单
												Log.e("status------", DataList
														.get(position).status
														.toString());
												intent.setClass(
														TransportOrderListActivity.this,
														TransportOrderUnfinishedDetailActivity.class);
												startActivity(intent);

											} else if (DataList.get(position).status
													.toString().equals("2")) {// 未入库
												intent.putExtra(
														"orderId",
														DataList.get(position).id
																.toString());
												intent.setClass(
														TransportOrderListActivity.this,
														TransportOrderDetailNotInStorageActivity.class);
												startActivity(intent);

											} else if (DataList.get(position).status
													.toString().equals("3")) {// 待操作订单
												intent.putExtra(
														"orderId",
														DataList.get(position).id
																.toString());
												intent.setClass(
														TransportOrderListActivity.this,
														TransportOrderDetailNotOperatedActivity.class);
												startActivity(intent);

											} else if (DataList.get(position).status
													.toString().equals("4")) {// 处理中订单
												intent.putExtra(
														"orderId",
														DataList.get(position).id
																.toString());
												intent.setClass(
														TransportOrderListActivity.this,
														InTreatmentActivity.class);
												startActivity(intent);
											} else if (DataList.get(position).status
													.toString().equals("10")) {// 已完成订单
												intent.putExtra(
														"orderId",
														DataList.get(position).id
																.toString());
												intent.setClass(
														TransportOrderListActivity.this,
														CompletedOrderDetailActivity.class);
												startActivity(intent);
											}

											else if (DataList.get(position).status
													.toString().equals("100")) {// 已评价订单
												intent.putExtra(
														"orderId",
														DataList.get(position).id
																.toString());
												intent.setClass(
														TransportOrderListActivity.this,
														EvaluatedOrderdetailActivity.class);
												startActivity(intent);
											}

										}
									});

						}

					}

				}, 1, R.string.loading);

		// for (int i = 0; i < 28; i++) {
		// Vegetable vege = new Vegetable();
		// vege.setName("2014000089898" + i);
		// list.add(vege);
		// }
		// // mAdapter = new
		// TransportOrderAdapter(TransportOrderListActivity.this,
		// // list);
		// lv_transport_order_list.setAdapter(mAdapter);

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
