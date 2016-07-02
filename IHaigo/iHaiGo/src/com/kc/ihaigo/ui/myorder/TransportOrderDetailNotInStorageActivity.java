package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.model.myorder.TranslportOrderDetailModel;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.magus.MagusTools;
import com.tencent.mm.sdk.platformtools.Log;

/***
 * @deprecated 转运订单详情----未入库
 * @author Lijie
 * */
public class TransportOrderDetailNotInStorageActivity extends Activity
		implements OnClickListener {
	private String OrderId, goodsName, goodsIcon;
	private View agent_info;
	private View torder_details_logistics_info;
	private TextView FirstTime, FirstLocation, GoodsCount, PayDollars,
			StorageName,orderdetail_no;
	private TranslportOrderDetailModel translportOrderDetailModel;
	private ListView lv_orderdetail_goods;
	private JSONArray goodsItems;// goos内容

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_order_detail);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		orderdetail_no = (TextView) findViewById(R.id.orderdetail_no);
		FirstTime = (TextView) findViewById(R.id.first_time);
		FirstLocation = (TextView) findViewById(R.id.first_location);
		GoodsCount = (TextView) findViewById(R.id.goods_count);
		PayDollars = (TextView) findViewById(R.id.pay_dollar);
		StorageName = (TextView) findViewById(R.id.storage_name);
		lv_orderdetail_goods = (ListView) findViewById(R.id.lv_orderdetail_goods);
		OrderId = this.getIntent().getStringExtra("orderId");
		orderdetail_no.setText("订单编号："+OrderId);
		//
	}

	private void initData() {
		// TODO Auto-generated method stub
		String url = "http://192.168.1.4:8080/transports/" + OrderId
				+ "/?user=9";
		Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(result)) {
							// translportOrderDetailModel = JSONUtils.fromJson(
							// result, TranslportOrderDetailModel.class);
							try {
								JSONObject jsobj = new JSONObject(result);
								String count = jsobj.getJSONObject("goods")
										.getString("count");
								String total = jsobj.getJSONObject("goods")
										.getString("total");
								// 购买产品数量和应付钱数
								GoodsCount.setText("X" + count);
								PayDollars.setText("$" + total);
								// 仓库信息
								String depot = jsobj.getString("depot");
								DataConfig dConfig = new DataConfig(
										TransportOrderDetailNotInStorageActivity.this);
								String depotName = dConfig
										.getTcompanyStorageById(depot);
								StorageName.setText(depotName);

								JSONArray progress = jsobj
										.getJSONArray("process");
								goodsItems = jsobj.getJSONObject("goods")
										.getJSONArray("items");
								String time = progress.getJSONObject(0)
										.getString("time");
								String location = progress.getJSONObject(0)
										.getString("location");

								FirstTime.setText(time);
								FirstLocation.setText(location);
								// for (int i = 0; i < progress.length(); i++) {
								// progress.getJSONObject(i).getString("time");
								// progress.getJSONObject(i).getString("location");
								//
								//
								//
								// }
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							lv_orderdetail_goods.setAdapter(new mAdapter());
							Log.e("----------------result", result);

						}
					}
				}, 1, R.string.loading);
	}

	class mAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return goodsItems.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return goodsItems.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(
						TransportOrderDetailNotInStorageActivity.this).inflate(
						R.layout.listview_not_in_orderdetail_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.goodsname);
				holder.goods_image = (ImageView) convertView
						.findViewById(R.id.goodsimage);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				goodsName = goodsItems.getJSONObject(position)
						.getString("name");
				goodsIcon = goodsItems.getJSONObject(position)
						.getString("icon");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			holder.name.setText(goodsName);
			MagusTools.setImageView(goodsIcon, holder.goods_image,
					R.drawable.ic_launcher);

			return convertView;
		}

		class ViewHolder {

			/**
			 * 订单编号,日期,状态
			 */
			TextView orderno, order_data, order_state, myorder_btn_bottom;
			TextView name, color;
			ImageView goods_image;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;
		case R.id.title_right:
			DialogUtil.showCancelForecast(
					TransportOrderDetailNotInStorageActivity.this,
					new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							// TODO Auto-generated method stub
							switch (whichButton) {
							case R.id.dialog_cancle:
								((Dialog) obj[0]).dismiss();

								break;
							case R.id.choose_ok:
								((Dialog) obj[0]).dismiss();

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
