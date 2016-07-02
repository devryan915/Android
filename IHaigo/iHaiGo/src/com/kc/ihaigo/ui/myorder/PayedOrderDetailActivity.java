package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.kc.ihaigo.R;
import com.kc.ihaigo.R.id;
import com.kc.ihaigo.R.layout;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/***
 * @deprecate代购订单-----待支付订单详情
 * @author Lijie
 * */
public class PayedOrderDetailActivity extends Activity implements
		OnClickListener {
	private String order_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payed_order_detail);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.order_detail_remind_send_goods).setOnClickListener(
				this);

	}

	private void initData() {
		// TODO Auto-generated method stub
		order_id = this.getIntent().getStringExtra("Orderid");
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
				}, 1,R.string.loading);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.order_detail_remind_send_goods: // 提醒发货操作
			String url = "http://192.168.1.4:8080/prders/{" + order_id
					+ "}/remind";
			Map<String, Object> map = new HashMap<String, Object>();
			HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
					new HttpReqCallBack() {

						@Override
						public void deal(String result) {
							Intent intent = new Intent();
							if (!TextUtils.isEmpty(result)) {
								Log.i("", "*****************收到信息" + result);
								JSONObject jsobj;
								try {
									jsobj = new JSONObject(result);

									if (jsobj.getJSONObject("code").equals(1)) {
										ToastUtil.showShort(
												PayedOrderDetailActivity.this,
												"已提醒发货");

									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {

							}
						}
					}, 1);
			break;

		default:
			break;
		}
	}

}
