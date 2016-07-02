package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.tencent.mm.sdk.platformtools.Log;

/***
 * @deprecated 转运订单------处理中订单详情
 * @author Lijie
 ****/
public class InTreatmentActivity extends Activity implements OnClickListener {
	private String OrderId;
	private JSONArray Packages;
	private ListView lv_package_content;
	private PackageAdapter mAdapter;
	private RelativeLayout agent_info_rl;
	private TextView selected_service, reported,orderdetail_no;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_treatment);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		OrderId = this.getIntent().getStringExtra("orderId");
		findViewById(R.id.title_left).setOnClickListener(this);
		lv_package_content = (ListView) findViewById(R.id.lv_package_content);
		findViewById(R.id.agent_info_rl).setOnClickListener(this);
		orderdetail_no = (TextView) findViewById(R.id.orderdetail_no);
		selected_service = (TextView) findViewById(R.id.selected_service);
		reported = (TextView) findViewById(R.id.reported);
		orderdetail_no.setText("订单编号："+OrderId);

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
							try {
								DataConfig dConfig = new DataConfig(
										InTreatmentActivity.this);
								JSONObject jsobj = new JSONObject(result);
								jsobj.getJSONObject("info")
										.getString("service");
								Packages = jsobj.getJSONObject("info")
										.getJSONArray("packages");
								mAdapter = new PackageAdapter(Packages);
								lv_package_content.setAdapter(mAdapter);
								setListViewHeightBasedOnChildren(lv_package_content);
								// for (int i = 0; i < packages.length(); i++) {
								// packages.getJSONObject(i).getString("content");
								// }

								selected_service.setText(jsobj.getJSONObject(
										"info").getString("service"));
								reported.setText("$"
										+ jsobj.getJSONObject("info")
												.getString("reported"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Log.e("----------------result", result);

						}
					}
				}, 1, R.string.loading);
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

	class PackageAdapter extends BaseAdapter {
		ViewHolder holder;
		private JSONArray packages;

		public PackageAdapter(JSONArray packag) {

			this.packages = packag;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return packages.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return packages.get(position);
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

			if (convertView == null) {
				convertView = LayoutInflater.from(InTreatmentActivity.this)
						.inflate(R.layout.listview_package_item, null);
				holder = new ViewHolder();
				holder.package_content = (TextView) convertView
						.findViewById(R.id.package_content);
				holder.channel = (TextView) convertView
						.findViewById(R.id.channel);
				holder.package_content = (TextView) convertView
						.findViewById(R.id.package_content);
				holder.identity = (TextView) convertView
						.findViewById(R.id.identity);
				holder.address = (TextView) convertView
						.findViewById(R.id.address);
				holder.remark = (TextView) convertView
						.findViewById(R.id.remark);
				holder.package_count = (TextView) convertView
						.findViewById(R.id.package_count);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				packages.getJSONObject(position).getString("content");
				packages.getJSONObject(position).getString("channel");
				packages.getJSONObject(position).getString("identity");
				packages.getJSONObject(position).getString("address");
				packages.getJSONObject(position).getString("remark");
				packages.getJSONObject(position).getString("tariff");

				holder.package_content.setText(packages.getJSONObject(position)
						.getString("content"));
				holder.channel.setText(packages.getJSONObject(position)
						.getString("channel"));
				holder.identity.setText(packages.getJSONObject(position)
						.getString("identity"));
				holder.address.setText(packages.getJSONObject(position)
						.getString("address"));
				holder.remark.setText(packages.getJSONObject(position)
						.getString("remark"));
				holder.package_count.setText("包裹" + (position + 1) + "");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}

		class ViewHolder {
			TextView package_content;
			TextView channel;
			TextView identity;
			TextView address;
			TextView remark;
			TextView tariff;
			TextView package_count;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();

			break;
		case R.id.agent_info_rl:   // 转运公司详情
			intent.setClass(InTreatmentActivity.this, TransUncompleteComDetail.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
