package com.kc.ihaigo.ui.shipping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.myorder.TransportOrderListActivity;
import com.kc.ihaigo.ui.personal.PersonalLogisticsActivity;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.ui.shipping.adapter.WayBillShippingAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class WayTransDetails extends IHaiGoActivity {
	private WrapListView shippingItem;
	/**
	 * 订单编号
	 */
	private TextView order_Nameber;
	/**
	 * 商品名称
	 */
	private TextView goodsNameText;
	private TextView order_Tite;

	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	private Class<IHaiGoActivity> lparentClass;
	private WayBillShippingAdapter adapter;
	private List<Map<String, String>> list;
	private Map<String, String> map;

	/**
	 * 代理商LOGO
	 */
	private ImageView puragent_head;
	/**
	 * 仓库地址
	 */
	private TextView info_dinaet;
	/**
	 * 代理商 --名称
	 */
	private TextView puragent_name;
	/**
	 * 代理商 --收费
	 */
	private TextView puragent_feeval;
	/**
	 * 代理商 --物流
	 */
	private TextView puragent_shippingval;
	/**
	 * 代理商 --服务
	 */
	private TextView puragent_serviceval;
	/**
	 * 代理商--信用
	 */
	private TextView pruagent_creditlevel;
	/**
	 * 次数
	 */
	private TextView open_tv;

	private TextView info_parcel;// 包裹内容
	private TextView info_channel;// 渠道
	private TextView info_status;// 身份证
	private TextView info_address;// 收货地
	private TextView info_leave;// 留言

	private RelativeLayout rela_channel;
	private RelativeLayout rela_address;
	private RelativeLayout rela_status;

	private String id;
	private IHaiGoGroupActivity lparentGroupActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_way_trans);
		initTitle();
		Image();// 显示图片初始化

	}

	private void Image() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	private void initTitle() {
		
		
		
		findViewById(R.id.title_left).setOnClickListener(this);
		shippingItem = (WrapListView) findViewById(R.id.shipp_wrap);
		order_Nameber = (TextView) findViewById(R.id.order_Nameber);
		goodsNameText = (TextView) findViewById(R.id.goodsName);
		adapter = new WayBillShippingAdapter(WayTransDetails.this);
		shippingItem.setAdapter(adapter);

		order_Tite = (TextView) findViewById(R.id.order_Tite);

		info_dinaet = (TextView) findViewById(R.id.info_dinaet);
		puragent_name = (TextView) findViewById(R.id.puragent_name);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		pruagent_creditlevel = (TextView) findViewById(R.id.pruagent_creditlevel);
		info_parcel = (TextView) findViewById(R.id.info_parcel);
		info_channel = (TextView) findViewById(R.id.info_channel);
		info_status = (TextView) findViewById(R.id.info_status);
		info_address = (TextView) findViewById(R.id.info_address);
		info_leave = (TextView) findViewById(R.id.info_leave);

		rela_channel = (RelativeLayout) findViewById(R.id.rela_channel);
		rela_address = (RelativeLayout) findViewById(R.id.rela_address);
		rela_status = (RelativeLayout) findViewById(R.id.rela_status);
		open_tv = (TextView) findViewById(R.id.open);
		if(this.getIntent().getStringExtra("questCode")!=null){
			id=this.getIntent().getStringExtra("id");
			initComponets(id);
		}
	}

	private void initComponets(String id) {

		String url = Constants.REC_SHIPPING_DATA + id + "?user="
				+ Constants.USER_ID;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {

						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject jsonObject = new JSONObject(result);

								/**
								 * 解析物流
								 */
								getShipping(jsonObject);

								JSONObject logistics = jsonObject
										.getJSONObject("logistics");

								// 商品
								int type = logistics.getInt("type");
								if (type == 3) {

									JSONArray packa = logistics
											.getJSONArray("package");
									for (int i = 0; i < packa.length(); i++) {
										info_parcel.setText(packa
												.getJSONObject(i).getString(
														"content"));
										info_leave.setText(packa.getJSONObject(
												i).getString("remark"));

									}
									rela_channel.setVisibility(View.GONE);
									rela_address.setVisibility(View.GONE);
									rela_status.setVisibility(View.GONE);

								} else if (type == 4) {

									JSONArray packa = jsonObject
											.getJSONArray("package");
									for (int i = 0; i < packa.length(); i++) {
										info_parcel.setText(packa
												.getJSONObject(i).getString(
														"content"));
										info_leave.setText(packa.getJSONObject(
												i).getString("remark"));
										rela_channel.setVisibility(View.GONE);

										info_status.setText(packa
												.getJSONObject(i).getString(
														"indentity"));
										info_address.setText(packa
												.getJSONObject(i).getString(
														"address"));

									}

								}

								// 根据返回的ID去 转运名称 转运公司id
								DataConfig dataConfig = new DataConfig(
										WayTransDetails.this);
								String Tcompany = dataConfig
										.getTcompanySty(logistics
												.getString("company"));
								JSONArray com = new JSONArray(Tcompany);
								if (com != null && com.length() > 0) {
									for (int i = 0; i < com.length(); i++) {
										String id = com.getJSONObject(i)
												.getString("id");
										String name = com.getJSONObject(i)
												.getString("name");
										String icon = com.getJSONObject(i)
												.getString("icon");
										String signature = com.getJSONObject(i)
												.getString("signature");
										String open = com.getJSONObject(i)
												.getString("open");
										String charge = com.getJSONObject(i)
												.getString("charge");
										String logis = com.getJSONObject(i)
												.getString("logistics");
										String service = com.getJSONObject(i)
												.getString("service");

										if (!TextUtils.isEmpty(icon)) {
											imageLoader.displayImage(icon,
													puragent_head, options,
													animateFirstListener);
										}
										puragent_name.setText(name);
										puragent_shippingval.setText(logis);
										pruagent_creditlevel.setText(signature);
										puragent_serviceval.setText(service);
										puragent_feeval.setText(charge);
										open_tv.setText(open);

										JSONArray addresses = com
												.getJSONObject(i).getJSONArray(
														"addresses");
										String addName;
										for (int j = 0; j < addresses.length(); j++) {
											addName = addresses
													.getJSONObject(0)
													.getString("name");
											info_dinaet.setText(addName);

										}

										/**
										 * 
										 * "addresses": [ { "id": 3, "name":
										 * "美国加利福尼亚州洛杉矶(CA)XIANFENGEX仓库",
										 * "firstName": "", "lastName": "NRTC",
										 * "address": "245 South 8th Ave",
										 * "unit": "38390", "city":
										 * "La Puente/City of industry",
										 * "state": "CA", "zipCode": "91746",
										 * "tel": "626-538-8583", "remark":
										 * "友情提醒： 请大家在购物网站下单时一定记得写上姓后面的4个字母专有识别码和单元号,单元号可以写在第一行地址后面也可以写在地址第二行.(如果网站不认5位数字编号可以不用添加，但4位英文专有码要加上）"
										 * , "currency": "USD", "rate": 6.5}]
										 */

									}
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							Toast.makeText(WayTransDetails.this,
									getString(R.string.connection_time),
									Toast.LENGTH_LONG).show();
						}

					}
				}, 0, R.string.loading);
	}

	/**
	 * 解析物流
	 */
	private void getShipping(JSONObject jsonObject) {
		list = new ArrayList<Map<String, String>>();

		/**
		 * 解析物流
		 */
		JSONObject bje;
		try {
			if (jsonObject != null && jsonObject.length() > 0) {
				bje = jsonObject.getJSONObject("logistics");
				if (bje != null && bje.length() > 0) {

					order_Nameber.setText(bje.getString("content"));// 订单编号

					DataConfig dataConfig = new DataConfig(WayTransDetails.this);
					String name = dataConfig.getLcompanyName(bje
							.getString("company"));
					goodsNameText.setText(name);

					Long logItemLong = Utils.getCurrentTime();// 获取当前时间
					Long itmeLongbt = null;

					JSONArray items = bje.getJSONArray("items");
					if (items != null && items.length() > 0) {

						for (int j = 0; j < items.length(); j++) {
							itmeLongbt = items.getJSONObject(0).getLong("time");// 获取第一个
							Long itmeLong = items.getJSONObject(j).getLong(
									"time");
							String location = items.getJSONObject(j).getString(
									"location");
							String timeString = Utils.getCurrentTime(itmeLong,
									"yyyy-MM-dd  HH:mm");
							map = new HashMap<String, String>();
							map.put("time", timeString);
							map.put("location", location);
							list.add(map);
						}

						String itemString = Utils.compareTime(logItemLong,
								itmeLongbt);// 时间差
						order_Tite.setText(itemString);
						adapter.setDatas(list);
						adapter.notifyDataSetChanged();

					}
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void refresh() {
		super.refresh();
		Bundle bun = getIntent().getExtras();
		if (ShippingActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			id = bun.getString("id");
			initComponets(id);
		} else if (PersonalLogisticsActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			id = bun.getString("id");
			initComponets(id);
		}
	}

}
