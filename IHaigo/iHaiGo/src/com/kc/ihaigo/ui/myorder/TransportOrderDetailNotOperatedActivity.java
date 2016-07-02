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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.magus.MagusTools;
import com.tencent.mm.sdk.platformtools.Log;

/***@deprecated 待操作订单详情
 * @author Lijie
 * */
public class TransportOrderDetailNotOperatedActivity extends Activity implements
		OnClickListener {
	private String OrderId;
	private String weight, depotId, goodsCounts, goodsTotal, depotInfo,
			depotName, transportIcon;
	private String fee;
	private TextView Fee_orderdetail, Weight_orderdetail, GoodsCount, PayTotal,
			StorageName,FirstTime,SendTime,FirstLocation,orderdetail_no;
	private ImageView TransportHead;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_order_detail_not_operated);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.torder_detail_btn_bottom).setOnClickListener(this);
		orderdetail_no = (TextView) findViewById(R.id.orderdetail_no);
		Weight_orderdetail = (TextView) findViewById(R.id.goods_weight);
		Fee_orderdetail = (TextView) findViewById(R.id.fee_transfer);
		StorageName = (TextView) findViewById(R.id.storage_name);
		TransportHead = (ImageView) findViewById(R.id.transport_head);
		PayTotal = (TextView) findViewById(R.id.pay_total);
		FirstTime = (TextView) findViewById(R.id.first_time);
		FirstLocation = (TextView) findViewById(R.id.first_location);
		GoodsCount = (TextView) findViewById(R.id.goods_count);
		OrderId = this.getIntent().getStringExtra("orderId");
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
										TransportOrderDetailNotOperatedActivity.this);
								JSONObject jsobj = new JSONObject(result);
								weight = jsobj.getJSONObject("info").getString(
										"weight");
								depotId = jsobj.getString("depot");
								goodsCounts = jsobj.getJSONObject("goods")
										.getString("count");
								goodsTotal = jsobj.getJSONObject("goods")
										.getString("total");
								depotName = dConfig
										.getTcompanyStorageById(depotId);
								transportIcon = dConfig
										.getTcompanyIconById("1");// 转运公司id
								fee = jsobj.getJSONObject("info").getString(
										"fee");
								JSONArray  progress=jsobj.getJSONArray("process");
								String time=progress.getJSONObject(0).getString("time");
								String location=progress.getJSONObject(0).getString("location");
								
								Weight_orderdetail
										.setText("重量：" + weight + "磅");
								Fee_orderdetail.setText("转仓费用：" + fee + "$");
								GoodsCount.setText("X" + goodsCounts);
								PayTotal.setText("$" + goodsCounts);

								StorageName.setText(depotName);
								MagusTools.setImageView(transportIcon,
										TransportHead, R.drawable.ic_launcher);
								FirstTime.setText(time);
								FirstLocation.setText(location);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Log.e("----------------result", result);

						}
					}
				},1,R.string.loading);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;
		case R.id.torder_detail_btn_bottom: // 立即发货
			intent.setClass(this, SendGoodsActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
