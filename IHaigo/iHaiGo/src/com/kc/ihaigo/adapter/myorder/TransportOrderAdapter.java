package com.kc.ihaigo.adapter.myorder;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.model.myorder.TransportOrderInnerAdapter;
import com.kc.ihaigo.model.myorder.TransportOrders;
import com.kc.ihaigo.ui.myorder.TransportOrderDetailNotInStorageActivity;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.util.DataConfig;
import com.magus.MagusTools;

/***
 * @deprecated 转运订单Adapter
 * @author Lijie
 * */
public class TransportOrderAdapter extends BaseAdapter {
	private TransportOrderInnerAdapter GoodsAdapter = null;
	List<TransportOrders> lists;
	private Context ctx;
	private String TcompanyIcon;
	private String STATUS;
	private String OrderId;
	private String LogisticsId;

	public TransportOrderAdapter(Context ctx, List<TransportOrders> dataList,
			WrapListView lv_transport_order_inner) {
		this.lists = dataList;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
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
					R.layout.listview_transport_order_list_item, null);
			holder = new ViewHolder();

			holder.lv_transport_orders_inner = (WrapListView) convertView
					.findViewById(R.id.lv_transport_order_inner);
			holder.torder_content = (TextView) convertView
					.findViewById(R.id.torder_content);
			holder.orderno = (TextView) convertView.findViewById(R.id.orderno);
			holder.torder_time = (TextView) convertView
					.findViewById(R.id.torder_date);
			holder.torder_info = (RelativeLayout) convertView
					.findViewById(R.id.torder_info);
			holder.transport_info = (LinearLayout) convertView
					.findViewById(R.id.transport_info);
			holder.goods_total = (LinearLayout) convertView
					.findViewById(R.id.goods_total);
			holder.select_service = (LinearLayout) convertView
					.findViewById(R.id.select_service);
			holder.waybill_info_ll = (LinearLayout) convertView
					.findViewById(R.id.waybill_info_ll);
			holder.torder_storage = (LinearLayout) convertView
					.findViewById(R.id.torder_storage);
			holder.order_state = (TextView) convertView
					.findViewById(R.id.order_state);
			holder.torder_btn_bottom = (Button) convertView
					.findViewById(R.id.torder_btn_bottom);
			holder.goods_all_total = (TextView) convertView
					.findViewById(R.id.goods_all_total);
			holder.all_dollars = (TextView) convertView
					.findViewById(R.id.all_dollars);
			holder.goods_weight = (TextView) convertView
					.findViewById(R.id.goods_weight);
			holder.fee_transfer = (TextView) convertView
					.findViewById(R.id.fee_transfer);
			holder.torder_remark = (TextView) convertView
					.findViewById(R.id.torder_remark);
			holder.service_content = (TextView) convertView
					.findViewById(R.id.service_content);
			holder.waybill_no = (TextView) convertView
					.findViewById(R.id.waybill_no);
			holder.good_image = (ImageView) convertView
					.findViewById(R.id.goods_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		OrderId = lists.get(position).id;
//		LogisticsId = lists.get(position-1).id;
		
		holder.orderno.setText(lists.get(position).id);
		holder.torder_time.setText(lists.get(position).time);
		// 获取转运公司信息
		DataConfig data = new DataConfig(ctx);
		TcompanyIcon = data.getTcompanyIconById(lists.get(position).transport);
		// Log.e("icon----------", TcompanyIcon);
		STATUS = lists.get(position).status.toString();
		if (lists.get(position).status.equals("2")) {
			holder.order_state.setText("未入库订单");
			holder.torder_storage.setVisibility(View.INVISIBLE); // 已入库3天
			holder.torder_remark.setVisibility(View.VISIBLE);
			holder.torder_remark.setText(lists.get(position).title.remark);
			holder.waybill_info_ll.setVisibility(View.VISIBLE);
			holder.select_service.setVisibility(View.GONE);
			holder.transport_info.setVisibility(View.GONE); // 重量相关信息
			holder.waybill_no
					.setText("运单号：" + lists.get(position).info.waybill);
			MagusTools.setImageView(TcompanyIcon, holder.good_image,
					R.drawable.ic_launcher);
			// holder.goods_weight.setText(lists.get(position).info.weight
			// + "磅");
			holder.fee_transfer.setText(lists.get(position).info.waybill);

			holder.torder_btn_bottom.setText("查看物流");//即未发货详情页
			holder.torder_btn_bottom.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Intent intent = new Intent();
//					intent.putExtra("orderId", LogisticsId);
//					intent.setClass(ctx,
//							TransportOrderDetailNotInStorageActivity.class);
//					ctx.startActivity(intent);

				}
			});
		} else if (STATUS.equals("3")) {
			holder.torder_storage.setVisibility(View.VISIBLE);
			holder.transport_info.setVisibility(View.VISIBLE);
			holder.goods_weight.setText("重量:" + lists.get(position).info.weight
					+ "磅");
			holder.fee_transfer.setText("转仓费:$" + lists.get(position).info.fee);
			holder.torder_btn_bottom.setText("发货操作");
			holder.order_state.setText("待操作订单");
			holder.waybill_info_ll.setVisibility(View.GONE);
			holder.torder_btn_bottom.setText("发货操作");
		} else if (STATUS.equals("4")) {
			holder.select_service.setVisibility(View.VISIBLE);
			holder.transport_info.setVisibility(View.GONE);
			holder.goods_total.setVisibility(View.GONE);// 数量。。折合人民币约，，
			holder.torder_storage.setVisibility(View.INVISIBLE);
			holder.all_dollars
					.setText("服务：" + lists.get(position).info.service);
			holder.order_state.setText("处理中订单");
			holder.torder_btn_bottom.setVisibility(View.INVISIBLE);
			holder.torder_info.setVisibility(View.VISIBLE);
			holder.lv_transport_orders_inner.setAdapter(null);
			holder.torder_content.setCompoundDrawables(ctx.getResources()
					.getDrawable(R.drawable.goods_lift_logo), null, null, null);
			holder.waybill_info_ll.setVisibility(View.GONE);
			// holder.torder_content.setText(lists.get(position).title.Tpackage
			// .get(0).toString().trim());
			// MagusTools.setImageView(url, image, defId);
		} else if (STATUS.equals("5")) {
			holder.order_state.setText("待支付订单");
			holder.torder_btn_bottom.setText("支付订单");
			holder.waybill_info_ll.setVisibility(View.GONE);
		} else if (STATUS.equals("6")) {
			holder.order_state.setText("已支付订单");
			holder.torder_btn_bottom.setVisibility(View.INVISIBLE);
			holder.waybill_info_ll.setVisibility(View.GONE);
		} else if (STATUS.equals("7")) {
			holder.order_state.setText("已发货订单");
			holder.torder_btn_bottom.setText("查看物流");
			holder.waybill_info_ll.setVisibility(View.GONE);
		} else if (STATUS.equals("10")) {
			holder.order_state.setText("已完成订单");
			holder.torder_btn_bottom.setText("评价");
			holder.waybill_info_ll.setVisibility(View.GONE);

		} else if (STATUS.equals("100")) {
			holder.order_state.setText("已评价订单");
			holder.waybill_info_ll.setVisibility(View.GONE);
			holder.torder_btn_bottom.setVisibility(View.GONE);

		}
		if (lists.get(position).goods != null && STATUS.equals("1")) {
			holder.torder_info.setVisibility(View.GONE);
			holder.goods_all_total.setText("数量   x"
					+ lists.get(position).goods.count);
			holder.all_dollars.setText("总计 " + lists.get(position).goods.total);
			GoodsAdapter = new TransportOrderInnerAdapter(ctx,
					lists.get(position).goods.getItems());

			holder.order_state.setText("未完成订单");
			holder.torder_btn_bottom.setVisibility(View.GONE);
			holder.transport_info.setVisibility(View.GONE);
			holder.select_service.setVisibility(View.GONE);
			holder.goods_total.setVisibility(View.VISIBLE);
			holder.waybill_info_ll.setVisibility(View.GONE);
			holder.lv_transport_orders_inner.setAdapter(GoodsAdapter);
			setListViewHeightBasedOnChildren(	holder.lv_transport_orders_inner);

		} else {
			if (lists.get(position).title.content != null) {
				holder.lv_transport_orders_inner.setAdapter(null);
				holder.goods_total.setVisibility(View.GONE);
				holder.torder_info.setVisibility(View.VISIBLE);
				holder.torder_btn_bottom.setVisibility(View.VISIBLE);
				holder.torder_content.setText(lists.get(position).title.content
						.toString().trim());
			}

		}

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
	class ViewHolder {

		/**
		 * 
		 */
		WrapListView lv_transport_orders_inner;
		TextView orderno, torder_time, torder_content, divide_line,
				order_state, goods_all_total, all_dollars, fee_transfer,
				goods_weight, torder_remark, service_content, waybill_no;
		ImageView good_image;
		RelativeLayout torder_info;
		LinearLayout transport_info, goods_total, torder_storage,
				select_service, waybill_info_ll;
		Button torder_btn_bottom;
	}
}
