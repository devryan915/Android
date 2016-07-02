package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.model.myorder.Items;
import com.kc.ihaigo.model.myorder.TransportOrdeUnfinishedModel;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.JSONUtils;
import com.magus.MagusTools;
import com.tencent.mm.sdk.platformtools.Log;

public class TransportOrderUnfinishedDetailActivity extends Activity implements
		OnClickListener {
	/***
	 * @deprecated 转运订单詳情----未完成订单
	 * @author Lijie
	 * */

	private PullUpRefreshListView lv_transport_order_detail_unfished;
	private String ORDERID;
	private RelativeLayout transport_company;
	private TransportOrdeUnfinishedModel DataModel = null;
	private List<Items> Datalist = null;
	private LoadAdapter mAdapter;
	private TextView orderdetail_no;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_orde_unfinished_detail);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.transport_company).setOnClickListener(this);
		orderdetail_no = (TextView) findViewById(R.id.torderdetail_no);
		lv_transport_order_detail_unfished = (PullUpRefreshListView) findViewById(R.id.lv_transport_order_detail_unfished);
		ORDERID = this.getIntent().getStringExtra("orderId");
		orderdetail_no.setText("订单编号:" + ORDERID);
	}

	private void initData() {
		// TODO Auto-generated method stub

		String url = "http://192.168.1.4:8080/transports/" + ORDERID

		+ "/?user=9";
		Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(result)) {
							DataModel = JSONUtils.fromJson(result,
									TransportOrdeUnfinishedModel.class);
							Datalist = DataModel.goods.items;
							lv_transport_order_detail_unfished
									.setAdapter(new LoadAdapter(Datalist));
							Log.e("----------------result", result);

						}
					}
				}, 0, R.string.loading);
	}

	class LoadAdapter extends BaseAdapter {

		private List<Items> lists;

		public LoadAdapter(List<Items> datalist) {
			// TODO Auto-generated constructor stub
			this.lists = datalist;
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
				convertView = LayoutInflater
						.from(TransportOrderUnfinishedDetailActivity.this)
						.inflate(
								R.layout.listview_transport_unfinish_order_details_item,
								null);

				holder = new ViewHolder();
				holder.goodsname = (TextView) convertView
						.findViewById(R.id.goodsname);
				holder.goodsimage = (ImageView) convertView
						.findViewById(R.id.goodsimage);
				holder.puragent_quantity_val = (TextView) convertView
						.findViewById(R.id.puragent_quantity_val);
				holder.puragent_total_val = (TextView) convertView
						.findViewById(R.id.puragent_total_val);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.goodsname.setText(lists.get(position).name);
			holder.puragent_quantity_val.setText(lists.get(position).amount);
			holder.puragent_total_val.setText(lists.get(position).total);
			MagusTools.setImageView(lists.get(position).icon,
					holder.goodsimage, R.drawable.ic_launcher);

			return convertView;
		}

	}

	class ViewHolder {

		TextView goodsname,puragent_quantity_val,puragent_total_val;
		ImageView goodsimage;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();

			break;

		case R.id.transport_company:
			intent.setClass(TransportOrderUnfinishedDetailActivity.this,
					TransUncompleteComDetail.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
}
