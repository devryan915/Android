package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/***
 * @deprecated 转运订单------已完成 订单详情
 * @author Lijie
 * */
public class CompletedOrderDetailActivity extends Activity implements
		OnClickListener {
	private String order_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_completed_order_detail);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.order_detail_remind_send_goods).setOnClickListener(this);
		findViewById(R.id.title_left).setOnClickListener(this);

	}

	private void initData() {
		// TODO Auto-generated method stub
		order_id = this.getIntent().getStringExtra("orderid");
		String url = "http://api.ihaigo.com/ihaigo/orders/" + order_id
				+ "/show?user=1";
		Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i("", "*****************收到信息" + result);

						} else {

						}
					}
				}, 1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.order_detail_remind_send_goods: // 评价商家
			intent.setClass(CompletedOrderDetailActivity.this,
					EvaluateMerchantActivity.class);
			break;
		case R.id.title_left: // 评价商家
			this.finish();
			
			break;
		default:
			break;
		}
	}

}
