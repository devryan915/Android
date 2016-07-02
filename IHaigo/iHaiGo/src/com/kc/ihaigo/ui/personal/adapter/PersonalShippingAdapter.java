package com.kc.ihaigo.ui.personal.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.ui.shipping.WayBillDetailsActivtiy;
import com.kc.ihaigo.ui.shipping.WayBillInfoActivity;
import com.kc.ihaigo.ui.shipping.WayTransDetails;
import com.kc.ihaigo.ui.shipping.adapter.ShippingItemAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

public class PersonalShippingAdapter extends BaseAdapter {

	private Context ctx;

	private JSONArray json = new JSONArray();
	private boolean item = true;

	private Long notime;

	private Map<String, String> map;

	private ShippingItemAdapter iadapter;
	private List<Map<String, String>> lists;

	/**
	 * 系统生成 代购
	 */
	private final static String PYPE_AUTO = "1";
	/**
	 * 2.手动 manual
	 * 
	 * @param ctx
	 */
	private final static String PYPE_MANUAL = "2";
	/**
	 * 3.转运transshipment
	 * 
	 * @param ctx
	 */
	private final static String PYPE_TRANS = "3";
	/**
	 * 4.转运包裹时shipment
	 * 
	 * @param ctx
	 */
	private final static String PYPE_SHIPMENT = "4";

	public PersonalShippingAdapter(Context ctx) {
		this.ctx = ctx;

	}

	public void setDatas(JSONArray json) {
		this.json = json;

	}

	public JSONArray getDatas() {
		return json;
	}

	@Override
	public int getCount() {
		return json == null ? 0 : json.length();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * type =2手动运单（到时候再改） type =1系统运单 orderId 订单编号 logisticsTime 运单时间 agent 代购商
	 * name 快递公司 content 内容 remarks 备注 createTime 创建时间 locations途径地点 time 时间
	 * location 途径地点 id 运单编码 waybillNumber 运单号 pwaybillNumber 父运单号
	 * 
	 * 
	 * @param arg0
	 * 
	 * @param arg1
	 * 
	 * @param arg2
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */

	@Override
	public View getView(final int position, View converView, ViewGroup arg2) {
		final ViewHolder holder;
		final int posi = position;
		String type = null;
		String content = null;
		final String id = null;

		if (converView == null) {

			converView = LayoutInflater.from(this.ctx).inflate(
					com.kc.ihaigo.R.layout.listview_shipping, null);
			holder = new ViewHolder();
			holder.orderTite = (TextView) converView
					.findViewById(R.id.orderTite);
			holder.way = (TextView) converView.findViewById(R.id.way);
			holder.order_name = (TextView) converView
					.findViewById(R.id.order_name);
			holder.order_Nameber = (TextView) converView
					.findViewById(R.id.order_Nameber);
			holder.order_addName = (TextView) converView
					.findViewById(R.id.order_addName);
			holder.order_addName.setVisibility(View.GONE);
			holder.moreBtn = (ImageView) converView.findViewById(R.id.moreBtn);
			holder.order_Tite = (TextView) converView
					.findViewById(R.id.order_Tite);
			holder.goodsName = (TextView) converView
					.findViewById(R.id.goodsName);
			holder.transportLayout = (LinearLayout) converView
					.findViewById(R.id.transportLayout);

			holder.shippingItem = (WrapListView) converView
					.findViewById(R.id.shippingItem);

			holder.way_bill = (LinearLayout) converView
					.findViewById(R.id.way_bill);

			holder.way_bill.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						setWayBill(
								json.getJSONObject(position).getString("type"),
								json.getJSONObject(position).getString("id"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			// 点击事件--更多物流信息
			holder.transportLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (item == false) {// 显示单条
						item = true;
						if (lists != null && lists.size() > 0) {
							iadapter.setIength(1);
							iadapter.notifyDataSetChanged();
						}

					} else {// 显示多条
						item = false;
						setOneTime(holder, posi, item);
						if (lists != null && lists.size() > 0) {
							iadapter.setIength(lists.size());
							iadapter.notifyDataSetChanged();
						}

						// allShipping(holder, posi, id);

					}

				}
			});

			converView.setTag(holder);
		} else {

			holder = (ViewHolder) converView.getTag();
		}
		setOneTime(holder, position, item);

		try {

			Long logLong = json.getJSONObject(position).getLong("createTime");

			String logItem = Utils.getCurrentTime(logLong, "MM-dd  HH:mm");

			holder.orderTite.setText(logItem);

			type = json.getJSONObject(position).getString("type");// 类型
			holder.way_bill.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						setWayBill(
								json.getJSONObject(position).getString("type"),
								json.getJSONObject(position).getString("id"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			content = json.getJSONObject(position).getString("content");// 订单编号

			if (type.equals(PYPE_AUTO)) {// 1
				holder.way.setVisibility(View.VISIBLE);
				holder.order_Nameber.setVisibility(View.VISIBLE);
				holder.order_addName.setVisibility(View.GONE);// 自动生成的备住
				holder.order_Nameber.setText(json.getJSONObject(position)
						.getString("content"));// 订单编号
				holder.order_name.setText("订单编号");
				// holder.order_Nameber.setTag(id);

				holder.goodsName.setText(json.getJSONObject(position)
						.getString("company"));

			} else if (type.equals(PYPE_MANUAL)) {// 2
				holder.way.setVisibility(View.GONE);
				holder.order_Nameber.setVisibility(View.INVISIBLE);

				holder.order_name.setText(json.getJSONObject(position)
						.getString("content"));
				if (!TextUtils.isEmpty(json.getJSONObject(position).getString(
						"remark"))) {
					holder.order_addName.setVisibility(View.VISIBLE);
					holder.order_addName.setText(json.getJSONObject(position)
							.getString("remark"));// 备注
				} else {
					holder.order_addName.setVisibility(View.GONE);
				}

				// 根据返回的ID去取物流信息 物流公司id
				DataConfig dataConfig = new DataConfig(ctx);
				String name = dataConfig.getLcompanyName(json.getJSONObject(
						position).getString("company"));
				holder.goodsName.setText(name);

			} else if (type.equals(PYPE_TRANS)) {// 3

				holder.way.setVisibility(View.VISIBLE);
				holder.order_Nameber.setVisibility(View.INVISIBLE);

				holder.order_name.setText(json.getJSONObject(position)
						.getString("content"));
				if (!TextUtils.isEmpty(json.getJSONObject(position).getString(
						"remark"))) {
					holder.order_addName.setVisibility(View.VISIBLE);
					holder.order_addName.setText(json.getJSONObject(position)
							.getString("remark"));
				} else {
					holder.order_addName.setVisibility(View.GONE);
				}

				// 根据返回的ID去 转运名称 转运公司id
				DataConfig dataConfig = new DataConfig(ctx);
				String Tcompany = dataConfig.getTcompanySty(json.getJSONObject(
						position).getString("company"));
				JSONArray com = new JSONArray(Tcompany);
				if (com != null && com.length() > 0) {
					for (int i = 0; i < com.length(); i++) {
						holder.goodsName.setText(com.getJSONObject(i)
								.getString("name"));
					}
				}

			} else if (type.equals(PYPE_SHIPMENT)) {// 4
				holder.way.setVisibility(View.VISIBLE);
				holder.order_Nameber.setVisibility(View.INVISIBLE);

				holder.order_name.setText(json.getJSONObject(position)
						.getString("content"));
				if (!TextUtils.isEmpty(json.getJSONObject(position).getString(
						"remark"))) {
					holder.order_addName.setVisibility(View.VISIBLE);
					holder.order_addName.setText(json.getJSONObject(position)
							.getString("remark"));
				} else {
					holder.order_addName.setVisibility(View.GONE);
				}

				// 根据返回的ID去 转运名称 转运公司id
				DataConfig dataConfig = new DataConfig(ctx);
				String Tcompany = dataConfig.getTcompanySty(json.getJSONObject(
						position).getString("company"));
				JSONArray com = new JSONArray(Tcompany);
				if (com != null && com.length() > 0) {
					for (int i = 0; i < com.length(); i++) {
						holder.goodsName.setText(com.getJSONObject(i)
								.getString("name"));
					}
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return converView;
	}

	private void setWayBill(final String type, final String id) {

		if (type.equals(PYPE_AUTO)) {
			Intent intent = new Intent(ctx, WayBillDetailsActivtiy.class);
			Bundle bun = new Bundle();
			bun.putString("id", id);
			intent.putExtras(bun);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);

			PersonalGroupActivity.group.startiHaiGoActivity(intent);

		} else if (type.equals(PYPE_MANUAL)) {

			Intent intent = new Intent(ctx, WayBillInfoActivity.class);
			Bundle bun = new Bundle();
			bun.putString("id", id);
			intent.putExtras(bun);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);

		} else if (type.equals(PYPE_TRANS)) {
			// 3.转运
			Intent intent = new Intent(ctx, WayTransDetails.class);
			Bundle bun = new Bundle();
			bun.putString("id", id);
			intent.putExtras(bun);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);

		} else if (type.equals(PYPE_SHIPMENT)) {
			// 4.装运包裹，id： 转运公司Id
			Intent intent = new Intent(ctx, WayTransDetails.class);
			Bundle bun = new Bundle();
			bun.putString("id", id);
			intent.putExtras(bun);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);

		}
	}

	private void allShipping(final ViewHolder holder, int position, String id) {
		/**
		 * 物流详情请求
		 */
		final String url = Constants.REC_LOGISTICS_DTAT + id;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {

								String timeString = null;
								String locString = null;
								List<Map<String, String>> list = new ArrayList<Map<String, String>>();

								JSONObject jsonObject = new JSONObject(result);
								JSONArray json = jsonObject
										.getJSONArray("logistics");
								// JSONArray items = json.optJSONArray("items");
								if (json != null) {
									for (int k = 0; k < json.length(); k++) {
										JSONObject jsObject = json
												.getJSONObject(k);
										JSONArray items = jsObject
												.getJSONArray("items");
										for (int i = 0; i < items.length(); i++) {
											locString = items.getJSONObject(i)
													.getString("location");
											Long time = items.getJSONObject(i)
													.getLong("time");
											timeString = Utils.getCurrentTime(
													time, "yyyy-MM-dd  HH:mm");
											map = new HashMap<String, String>();
											map.put("time", timeString);
											map.put("location", locString);
											list.add(map);
										}

									}
									ShippingItemAdapter adapter = new ShippingItemAdapter(
											ctx, list);
									holder.shippingItem.setAdapter(adapter);
									adapter.notifyDataSetChanged();

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}

				}, 0);

	}

	private void setOneTime(ViewHolder holder, int position, boolean item) {
		lists = new ArrayList<Map<String, String>>();
		try {
			JSONArray items = json.getJSONObject(position)
					.getJSONArray("items");
			String timeString = null;
			String locString = null;
			Long notime = null;
			if (items != null && items.length() > 0) {
				for (int i = 0; i < items.length(); i++) {
					Long time = items.getJSONObject(i).getLong("time");
					timeString = Utils
							.getCurrentTime(time, "yyyy-MM-dd  HH:mm");
					locString = items.getJSONObject(i).getString("location");
					map = new HashMap<String, String>();
					map.put("time", timeString);
					map.put("location", locString);
					lists.add(map);
					notime = items.getJSONObject(0).getLong("time");
				}

				Long logItemLong = Utils.getCurrentTime();

				String itemString = Utils.compareTime(logItemLong, notime);

				holder.order_Tite.setText(itemString);

				iadapter = new ShippingItemAdapter(ctx, lists);
				iadapter.setIength(1);
				holder.shippingItem.setAdapter(iadapter);
				iadapter.notifyDataSetChanged();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class ViewHolder {
		/**
		 * 生成时间
		 */
		TextView orderTite;
		/**
		 * 生成方式
		 */
		TextView way;
		/**
		 * 订单编号名字
		 */
		TextView order_name;
		/**
		 * 订单编号
		 */
		TextView order_Nameber;
		/**
		 * 自动生成的备住
		 */
		TextView order_addName;
		/**
		 * 物流详情
		 */
		TextView order_Tite;
		/**
		 * 商品名字
		 */
		TextView goodsName;
		/**
		 * 更多详情
		 */
		ImageView moreBtn;
		/**
		 * shippingItem 订单物流信息
		 */
		WrapListView shippingItem;
		/**
		 * 物流信息显示更多
		 */
		LinearLayout transportLayout;
		/**
		 * 运单详情
		 */
		LinearLayout way_bill;
	}
}
