package com.kc.ihaigo.adapter.myorder;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.model.myorder.Goods;
import com.kc.ihaigo.ui.selfwidget.WrapListView;

public class MyOrderAdapter extends BaseAdapter implements OnClickListener {
	private JSONArray lists;
	private Context ctx;
	private WrapListView mPullToRefreshListView_item;
	private List<Goods> data_order_item;
	private String STATE;
	private JSONArray items;
	private String OrderId;

	public MyOrderAdapter(Context ctx, JSONArray goodsArray,
			WrapListView mPullToRefreshListView_item2) {
		this.ctx = ctx;
		this.lists = goodsArray;
		this.mPullToRefreshListView_item = mPullToRefreshListView_item2;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return lists.get(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parents) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_myorder_item, null);
			holder = new ViewHolder();
			holder.orderno = (TextView) convertView.findViewById(R.id.orderno);
			holder.order_data = (TextView) convertView
					.findViewById(R.id.torder_time);
			holder.order_state = (TextView) convertView
					.findViewById(R.id.order_state);
			holder.myorder_btn_bottom = (TextView) convertView
					.findViewById(R.id.myorder_btn_bottom);
			holder.pay_number = (TextView) convertView
					.findViewById(R.id.pay_number);
			holder.goods_counts = (TextView) convertView
					.findViewById(R.id.goods_counts);
			mPullToRefreshListView_item = (WrapListView) convertView
					.findViewById(R.id.lv_myorder_inner);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			OrderId = lists.getJSONObject(position).getString("id");

			holder.orderno.setText(lists.getJSONObject(position)
					.getString("id"));
			holder.order_data.setText(lists.getJSONObject(position).getString(
					"created_at"));

			Log.e("qqqqqqqqqqq",
					lists.getJSONObject(position).getString("total").toString());

			holder.myorder_btn_bottom.setOnClickListener(this);
			STATE = lists.getJSONObject(position).getString("status");

			if (STATE.equals("1")) {
				holder.order_state.setText("已提交订单");
				holder.myorder_btn_bottom.setText("支付订单");

			} else if (STATE.equals("2")) {
				holder.order_state.setText("已支付订单");
				holder.myorder_btn_bottom.setText("提醒发货");

			} else if (STATE.equals("3")) {
				holder.order_state.setText("已发货订单");
				holder.myorder_btn_bottom.setText("查看物流");
			} else if (STATE.equals("4")) {
				holder.order_state.setText("已完成订单");
				holder.myorder_btn_bottom.setText("评价商家");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SortByState(STATE, holder.myorder_btn_bottom);
		try {
			JSONObject goods = lists.getJSONObject(position).getJSONObject(
					"goods");
			items = lists.getJSONObject(position).getJSONObject("goods")
					.getJSONArray("items");
			holder.goods_counts.setText("数量：X" + goods.getString("count"));
			holder.pay_number.setText(goods.getString("total"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mPullToRefreshListView_item.setAdapter(new MyAdapter(ctx, items));
		setListViewHeightBasedOnChildren(mPullToRefreshListView_item);
		return convertView;
	}

	/**
	 * 动态设置ListView的高度
	 * 
	 * @param listView
	 */
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

	/***
	 * 
	 * 根据订单状态---------
	 * */
	private void SortByState(String STATE, TextView myorder_btn_bottom) {
		// TODO Auto-generated method stub

		// case 1:// 已提交
		// myorder_btn_bottom
		// .setBackgroundResource(R.drawable.shopcar_paybutton_shape);
		// myorder_btn_bottom.setText("支付订单");
		//
		// break;
		// case 2:// 已付款
		// myorder_btn_bottom
		// .setBackgroundResource(R.drawable.shopcar_paybutton_shape);
		// // myorder_btn_bottom.setText("提醒发货");
		//
		// break;
		// case 3:// 已发货
		// myorder_btn_bottom.setBackgroundResource(R.drawable.account);
		// myorder_btn_bottom.setText("查看物流");
		// break;
		// case 4:// 已确认收货
		// myorder_btn_bottom.setBackgroundResource(R.drawable.account);
		// myorder_btn_bottom.setText("评价商家");
		// break;
		//
		// default:
		// break;

	}

	class ViewHolder {

		/**
		 * 订单编号,日期,状态
		 */
		TextView orderno, order_data, order_state, myorder_btn_bottom,
				pay_number;
		TextView name, color, goods_counts;
		ImageView good_image;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// AliPayUtil.payBill(ctx, new Handler(ctx.getMainLooper()), "kksk",
		// "支付订单", "静安寺科林费斯", "100");
		// AliPayUtil.payBill(SubmitedOrderDetailActivity.this, new Handler(
		// SubmitedOrderDetailActivity.this.getMainLooper()), orderno,
		// "支付订单", "静安寺科林费斯", "100");

	}
}
