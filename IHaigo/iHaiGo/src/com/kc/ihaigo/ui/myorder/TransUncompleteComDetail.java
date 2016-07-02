/**
 * @Title: TransUncompleteComDetail.java
 * @Package: com.kc.ihaigo.ui.myorder
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年8月6日 上午10:46:11

 * @version V1.0

 */

package com.kc.ihaigo.ui.myorder;

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
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.GoodsDetailCommetsAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 
 * @ClassName: TransUncompleteComDetail
 * @Description: 转运公司地址页面
 * @author: ryan.wang
 * @date: 2014年8月6日 下午3:10:29
 * 
 */

public class TransUncompleteComDetail extends Activity
		implements
			OnClickListener {
	private int transcompanyId = 1;
	private int used;
	private JSONArray addresses;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_uncomplete_tcomdetail);
		initView();
		initData();
	}

	/**
	 * @Title: initView
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initView() {
		findViewById(R.id.addForecast).setOnClickListener(this);
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.transport_company_showaddress).setOnClickListener(
				this);
	}

	/**
	 * @Title: initData
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */
	private void initData() {
		String url = Constants.REC_TRANSPORTS + "company/" + transcompanyId
				+ "?user=" + Constants.USER_ID;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								used = data.getInt("used");
								String phone = data.getString("phone");
								String website = data.getString("website");
								String qq = data.getString("qq");
								String email = data.getString("email");
								String description = data
										.getString("description");
								JSONObject comments = data
										.getJSONObject("comments");
								String commentscount = comments
										.getString("count");
								JSONArray commentsitems = comments
										.getJSONArray("items");
								((TextView) findViewById(R.id.transport_transtimes))
										.setText(getText(
												R.string.transport_transtimes)
												.toString().replace("$",
														used + ""));
								((TextView) findViewById(R.id.transport_company_phone))
										.setText(phone);
								((TextView) findViewById(R.id.transport_company_website))
										.setText(website);
								((TextView) findViewById(R.id.transport_company_qq))
										.setText(qq);
								((TextView) findViewById(R.id.transport_company_email))
										.setText(email);
								((TextView) findViewById(R.id.transport_company_service))
										.setText(description);
								((TextView) findViewById(R.id.more_appraise))
										.setText(commentscount);
								GoodsDetailCommetsAdapter gdca = new GoodsDetailCommetsAdapter(
										TransUncompleteComDetail.this);
								gdca.setDatas(commentsitems);
								((ListView) findViewById(R.id.transport_evaluation))
										.setAdapter(gdca);

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
				}, 5, R.string.loading);
		DataConfig config = new DataConfig(TransUncompleteComDetail.this);
		String transCom = config.getTcompanySty(transcompanyId + "");
		if (!TextUtils.isEmpty(transCom)) {
			try {
				JSONObject com = new JSONObject(transCom);
				String iconurl = com.getString("icon");
				String name = com.getString("name");
				String signature = com.getString("signature");
				String charge = com.getString("charge");
				String logistics = com.getString("logistics");
				String service = com.getString("service");
				addresses = com.getJSONArray("addresses");
				JSONArray channels = com.getJSONArray("channels");
				ImageLoader.getInstance().displayImage(iconurl,
						(ImageView) findViewById(R.id.puragent_head));
				((TextView) findViewById(R.id.user_name)).setText(name);
				((TextView) findViewById(R.id.user_introduce))
						.setText(signature);
				((TextView) findViewById(R.id.puragent_feeval)).setText(charge);
				((TextView) findViewById(R.id.puragent_shippingval))
						.setText(logistics);
				((TextView) findViewById(R.id.puragent_serviceval))
						.setText(service);
				StringBuffer introdue = new StringBuffer();
				for (int i = 0; i < channels.length(); i++) {
					JSONObject channel = channels.getJSONObject(i);
					introdue.append(channel.getString("name") + "\r\n"
							+ channel.getString("instruction") + "\r\n\r\n");
				}
				((TextView) findViewById(R.id.charge_introduce))
						.setText(introdue);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.addForecast :  //添加预报
				Intent forecast = new Intent(TransUncompleteComDetail.this,
						ForecastAddActivity.class);
				startActivity(forecast);
				break;
			case R.id.transport_company_showaddress :
				if (addresses != null) {
					Intent address = new Intent(TransUncompleteComDetail.this,
							TransportComAddress.class);
					Bundle reqParams = new Bundle();
					reqParams.putInt("transcompanyId", transcompanyId);
					reqParams.putInt("used", used);
					reqParams.putString("address", addresses.toString());
					address.putExtras(reqParams);
					startActivity(address);
				}
				break;
			case R.id.title_left:
			this.finish();
			break;
			default :
				break;
		}
	}
}
