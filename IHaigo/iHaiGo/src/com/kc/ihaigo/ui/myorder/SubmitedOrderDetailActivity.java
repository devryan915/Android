package com.kc.ihaigo.ui.myorder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.ChoosePayActivity;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.pay.AliPayUtil;
import com.magus.MagusTools;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @Description: 代购订单-----已提交订单详情
 * @author: @author: Lijie
 * @date: 2014年7月17日 下午4:40:18
 * 
 **/
public class SubmitedOrderDetailActivity extends IHaiGoActivity implements
		OnClickListener {
	private ListView lv_submit_order_detail;
	private TextView orderdetail_no, pay_number;
	private String orderno;
	private int order_status;
	private String order_id;
	private LinearLayout order_detail_logistics_info;
	private TextView goods_name, goods_color, goods_size, pay_total,
			order_detail_puragent_name, puragent_feeval, puragent_shippingval,
			puragent_serviceval, order_detail_time, transport_location;
	private ImageView goods_image, agent_head;
	private TextView remark_info;
	private View view_remove;
	private String color;
	private String name;
	private String icon;
	private String size;
	private JSONObject jsonObject;
	private JSONArray goods;
	private String paytotal;
	private JSONObject agent;
	private JSONArray logistic;
	private JSONArray goodsItems;
	private OrderDetailBuyOtherAdapter myAdapter;
	private TextView payType, puragent_quantity_val, agent_count,
			buy_other_total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_order_detail);
		initView();
		ininData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.left_title).setOnClickListener(this);
		findViewById(R.id.order_detail_pay).setOnClickListener(this);
		orderdetail_no = (TextView) findViewById(R.id.orderdetail_no);
		order_detail_logistics_info = (LinearLayout) findViewById(R.id.order_detail_logistics_info);
		findViewById(R.id.merchant_info).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		goods_image = (ImageView) findViewById(R.id.goodsimage);
		pay_number = (TextView) findViewById(R.id.pay_number);
		goods_name = (TextView) findViewById(R.id.goods_name);
		goods_color = (TextView) findViewById(R.id.goods_color);
		goods_size = (TextView) findViewById(R.id.goods_size);
		pay_total = (TextView) findViewById(R.id.pay_total);
		agent_head = (ImageView) findViewById(R.id.agent_head);
		order_detail_puragent_name = (TextView) findViewById(R.id.order_detail_puragent_name);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		order_detail_time = (TextView) findViewById(R.id.order_detail_time);
		transport_location = (TextView) findViewById(R.id.transport_location);
		lv_submit_order_detail = (ListView) findViewById(R.id.lv_submit_order_detail);
		payType = (TextView) findViewById(R.id.payType);
		remark_info = (TextView) findViewById(R.id.remark_info);
		puragent_quantity_val = (TextView) findViewById(R.id.puragent_quantity_val);
		agent_count = (TextView) findViewById(R.id.agent_count);
		buy_other_total = (TextView) findViewById(R.id.buy_other_total);
		payType.setOnClickListener(this);
		order_id = this.getIntent().getStringExtra("id").toString().trim();
		Log.e("iiiiiiiiiiiiiiiiiidddddddddddddd", order_id);
		// order_status =
		// Integer.parseInt(this.getIntent().getStringExtra("status").trim());
		switch (order_status) {
		case 1: // 已提交

			break;
		case 2: // 已付款

			break;
		case 3: // 已发货
			order_detail_logistics_info.setVisibility(View.VISIBLE);
			break;
		case 4: // 已确认收货

			break;

		default:
			break;
		}

	}

	private void ininData() {
		// TODO Auto-generated method stub
		order_id = this.getIntent().getStringExtra("id").trim();
		Log.e("======================", order_id);
		// String url = "http://api.ihaigo.com/ihaigo/orders/" + order_id
		// + "/show?user=1";

		// String url =
		// "http://192.168.1.4:8080/orders/0000000000149/show?user=1";
		String url = "http://192.168.1.4:8080/orders/" + order_id
				+ "/show?user="+Constants.USER_ID; //21
		Log.e("url------------", url);
		Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						// TODO Auto-generated method stub

						if (!TextUtils.isEmpty(result)) {
							try {
								jsonObject = new JSONObject(result);
								orderno = jsonObject.get("id").toString()
										.trim();

								remark_info.setText(jsonObject
										.getString("remark"));
								// status = jsonObject.get("status").toString()
								// .trim();
								orderdetail_no.setText("订单编号 ：" + orderno);

								// goods = jsonObject.getJSONArray("goods");

								goodsItems = jsonObject.getJSONObject("goods")
										.getJSONArray("items");
								puragent_quantity_val.setText("x"
										+ jsonObject.getJSONObject("goods")
												.getString("count"));
								pay_total.setText(jsonObject.getJSONObject(
										"goods").getString("total"));
								Log.e("aaaaaaaaaaaaaaa", goodsItems.toString());
								myAdapter = new OrderDetailBuyOtherAdapter(
										goodsItems);
								lv_submit_order_detail.setAdapter(myAdapter);

								/***
								 * 产品信息
								 * */
								// for (int i = 0; i < goods.length(); i++) {
								// icon = goods.getJSONObject(i).getString(
								// "icon");
								// color = goods.getJSONObject(i).getString(
								// "color");
								// name = goods.getJSONObject(i).getString(
								// "name");
								// size = goods.getJSONObject(i).getString(
								// "size");
								// paytotal = goods.getJSONObject(i)
								// .getString("total").trim();
								// Log.e("xxxxxxxxxxxx", goods + "" + icon
								// + pay_total);
								// setData();
								//
								// }

								/****
								 * 代购商信息
								 * **/
								agent = jsonObject.getJSONObject("agent");

								agent.getString("headPortrait");
								agent.getString("price");
								agent.getString("logistics");
								agent.getString("service");
								agent.getString("introduce");
								agent.getString("statement");

								puragent_serviceval.setText(agent.getString(
										"service").toString());
								puragent_shippingval.setText(agent.get(
										"logistics").toString());
								puragent_feeval.setText(agent.getString(
										"charge").toString());
								order_detail_puragent_name.setText(agent
										.getString("agentsName").toString());
								MagusTools.setImageView(
										agent.getString("headPortrait")
												.toString(), agent_head,
										R.drawable.puragent);
								buy_other_total.setText("￥"
										+ agent.get("price").toString());
								agent_count.setText(jsonObject.getJSONObject(
										"goods").getString("count"));
//								pay_number.setText("￥"
//										+ agent.get("price").toString());
								 //支付金额。保留小数点后一位
										 Float  f= Float.parseFloat(agent.getString("price").toString());
										 BigDecimal   b   =   new   BigDecimal(f);  
										  float   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();  
								 Math.round( f*10000/10000.0);
								 pay_number.setText("￥"+f1+"");
								 Log.e("--------",  f1+"");

								/***
								 * 物流信息
								 * */
								for (int j = 0; j < jsonObject.getJSONArray(
										"logistic").length(); j++) {
									logistic = jsonObject
											.getJSONArray("logistic")
											.getJSONObject(j)
											.getJSONArray("items");
									Log.e("logstic",
											jsonObject.getJSONArray("logistic")
													.getJSONObject(j)
													.getJSONArray("items")
													.toString());
									for (int i = 0; i < logistic.length(); i++) {

										logistic.getJSONObject(i).get(
												"location");
										order_detail_time.setText(logistic
												.getJSONObject(i).get("time")
												.toString());
										transport_location.setText(logistic
												.getJSONObject(i)
												.getString("location")
												.toString());
										Log.e("", logistic.getJSONObject(i)
												.get("time").toString());

									}

								}
								/***
								 * 用户相关信息----------------待续
								 **/

								Log.e("address--------",
										jsonObject.getString("address")
												.toString());
								Log.e("agent--------", jsonObject
										.getJSONObject("agent").toString());

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}

					private void setData() {
						// TODO Auto-generated method stub
						MagusTools.setImageView(icon, goods_image,
								R.drawable.adv_goods);
						goods_name.setText(name);
						goods_color.setText("颜色：" + color);
						pay_total.setText("$" + paytotal);
						goods_size.setText("尺寸：" + size);
					}

				}, 1, R.string.loading);

	}

	class OrderDetailBuyOtherAdapter extends BaseAdapter {
		private JSONArray goodsItemArray;
		private String goodsIcon;
		private String itemId;
		ViewHolder holder;
		private boolean isEdit = false;

		public OrderDetailBuyOtherAdapter(JSONArray goodsItemArray) {
			this.goodsItemArray = goodsItemArray;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return goodsItemArray.length();
		}

		public void setEdit(boolean isEdit) {
			this.isEdit = isEdit;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return goodsItemArray.get(position);
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parents) {
			// TODO Auto-generated method stub

			if (convertView == null) {
				convertView = LayoutInflater.from(
						SubmitedOrderDetailActivity.this).inflate(
						R.layout.listview_orderdetail_buyother_item, null);
				holder = new ViewHolder();
				holder.goods_name = (TextView) convertView
						.findViewById(R.id.goodsname);
				holder.goodsimage = (ImageView) convertView
						.findViewById(R.id.goodsimage);
				holder.orderdetail_goodsitem_remove_ll = (LinearLayout) convertView
						.findViewById(R.id.shopcar_selall_ll);
				holder.goods_remove = (Button) convertView
						.findViewById(R.id.shopcar_goods_remove);
				holder.deletegoods = (TextView) convertView
						.findViewById(R.id.deletegoods);
				holder.goodscolorval = (TextView) convertView
						.findViewById(R.id.goodscolorval);
				holder.goodsruleval = (TextView) convertView
						.findViewById(R.id.goodsruleval);
				holder.oldprice = (TextView) convertView
						.findViewById(R.id.oldprice);
				holder.price = (TextView) convertView.findViewById(R.id.price);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				holder.goods_name.setText(goodsItemArray
						.getJSONObject(position).getString("name"));
				goodsIcon = goodsItemArray.getJSONObject(position).getString(
						"icon");
				itemId = goodsItemArray.getJSONObject(position).getString("id");
				MagusTools.setImageView(goodsIcon, holder.goodsimage,
						R.drawable.ic_launcher);
				holder.goodscolorval.setText(goodsItemArray.getJSONObject(
						position).getString("color"));
				holder.goodsruleval.setText(goodsItemArray.getJSONObject(
						position).getString("size"));
				holder.oldprice.setText(goodsItemArray.getJSONObject(position)
						.getString("price"));
				holder.price.setText(goodsItemArray.getJSONObject(position)
						.getString("discount"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final HorizontalScrollView hsView = (HorizontalScrollView) convertView;
			// 删除item
			if (isEdit) {
				holder.orderdetail_goodsitem_remove_ll
						.setVisibility(View.VISIBLE);
				Log.e("vvvvvvvvvvvvvvvvvvvv", "delete------1");
				holder.goods_remove.setVisibility(View.VISIBLE);
				holder.goods_remove.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// holder.goods_remove.setVisibility(View.INVISIBLE);
						OrderDetailBuyOtherAdapter.this.notifyDataSetChanged();
						if (holder.deletegoods.getVisibility() == View.GONE) {
							holder.deletegoods.setVisibility(View.VISIBLE);
							Log.e("vvvvvvvvvvvvvvvvvvvv", "delete------2");

						}
						holder.deletegoods.setVisibility(View.VISIBLE);
						holder.deletegoods.measure(0, 0);
						Handler handler = new Handler();
						handler.post(new Runnable() {
							@Override
							public void run() {
								Log.e("vvvvvvvvvvvvvvvvvvvv", "delete------3");
								hsView.scrollBy(
										holder.deletegoods.getMeasuredWidth(),
										0);
							}
						});

					}
				});

				// holder.goods_remove.setOnClickListener(new OnClickListener()
				// {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// holder.deletegoods.setVisibility(View.VISIBLE);
				// holder.deletegoods
				// .setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// final String url = "http://192.168.1.4:8080/orders/"
				// + orderno;
				// final Map<String, Object> map = new HashMap<String,
				// Object>();
				// map.put("user", "9");
				// map.put("items", itemId);
				// HttpAsyncTask.fetchData(
				// HttpAsyncTask.DELETE, url, map,
				// new HttpReqCallBack() {
				//
				// @Override
				// public void deal(
				// String result) {
				// if (!TextUtils
				// .isEmpty(result)) {
				// // Log.i(
				// // "+++++++++++++++++++收到信息"
				// // + result);
				// try {
				// JSONObject json = new JSONObject(
				// result);
				// Log.e("11111111111111111",
				// result);
				// myAdapter
				// .notifyDataSetChanged();
				//
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				//
				// } else {
				// }
				// }
				// }, 1);
				// }
				// });
				// }
				// });
			}
			return convertView;
		}

		class ViewHolder {
			TextView goods_name, deletegoods, goodsdelete, goodscolorval,
					goodsruleval, oldprice, price;
			ImageView goodsimage;
			ListView lv_popwin_channel;
			LinearLayout orderdetail_goodsitem_remove_ll;
			Button goods_remove;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 2) {

			payType.setText(data.getStringExtra("payType"));
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.left_title:
			this.finish();
			// intent.setClass(this, OrderTabActivity.class);
			// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			// PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.payType:

			intent.setClass(SubmitedOrderDetailActivity.this,
					ChoosePayActivity.class);
			startActivityForResult(intent, 1);

			break;
		case R.id.order_detail_pay:// 支付
			AliPayUtil.payBill(SubmitedOrderDetailActivity.this, new Handler(
					SubmitedOrderDetailActivity.this.getMainLooper()), orderno,
					"支付订单", "静安寺科林费斯", "100");
			// Intent intents = new Intent(SubmitedOrderDetailActivity.this,
			// ChoicePayWay.class);
			// Bundle bundle = new Bundle();
			// intents.putExtras(bundle);
			// intents.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			// intents.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			// startiHaiGoActivity(intents);
			break;
		case R.id.merchant_info: // 商家详情
			intent.setClass(this, MerchanantInfoActivity.class);
			startActivity(intent);

			break;
		case R.id.title_right:
			DialogUtil.showEditOrderDialog(SubmitedOrderDetailActivity.this,
					new BackCall() {
						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_edit_order:
								// myAdapter.setEdit(true);
								// myAdapter.notifyDataSetChanged();
								// ((Dialog) obj[0]).dismiss();

								//
								// JSONArray items;
								// items =
								// goodsArray.getJSONObject(i).getJSONObject("goods")
								// .getJSONArray("items");
								// myAdapter=new
								// MyAdapter(SubmitedOrderDetailActivity.this,items);
								// myAdapter.setEdit(true);

								break;
							case R.id.dialog_cancel_order:
								((Dialog) obj[0]).dismiss();
								// Intent intent = new Intent(
								// OrderDetailActivity.this,
								// PurchasingAgent.class);
								// intent.putExtra("key",
								// "fromShopCarActivity");
								// intent.putExtra(
								// IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
								// true);
								// intent.putExtra(
								// IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								// true);
								// ShopCarGroupActiviy.group
								// .startiHaiGoActivity(intent);
								break;
							default:
								break;
							}
						}
					}, null);
			break;

		default:
			break;
		}

	}

}
