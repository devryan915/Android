package com.kc.ihaigo.ui.shipping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalLogisticsActivity;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.ui.shipping.adapter.WayBillShippingAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 运单详情--系统生成
 * 
 * @author zouxianbin
 * 
 */
public class WayBillDetailsActivtiy extends IHaiGoActivity {

	private WrapListView shippingItem;
	/**
	 * 订单编号
	 */
	private TextView order_Nameber;
	/**
	 * 商品名称
	 */
	private TextView goodsNameText;
	/**
	 * 时间差
	 */
	private TextView order_Tite;

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
	private RatingBar puragent_credit_rating;
	/**
	 * 代理商LOGO
	 */
	private ImageView puragent_head;
	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	/**
	 * 用户名
	 */
	private TextView userName;
	/**
	 * 用户TITLE 信息
	 */
	private TextView usre_title;
	/**
	 * 手机号
	 */
	private TextView userNameber;
	/**
	 * 地区
	 */
	private TextView region;
	/**
	 * 地址
	 */
	private TextView addressText;
	/**
	 * 备注
	 */
	private TextView note;

	/**
	 * 来源
	 */
	private ImageView source;
	/**
	 * 国内支付
	 */
	private ImageView supporthomecredit;
	/**
	 * 转运公司
	 */
	private ImageView translatecom;
	/**
	 * 商品LOGO
	 */
	private ImageView goodsImage;
	/**
	 * 商品名称
	 */
	private TextView goodsname;
	/**
	 * 原来美元价格
	 */
	private TextView oldprice;
	/**
	 * 新美元价格
	 */
	private TextView price;
	/**
	 * 颜色
	 */
	private TextView goodscolorval;
	/**
	 * 尺寸
	 */
	private TextView goodsruleval;
	/**
	 * 数量
	 */
	private TextView number;
	/**
	 * 总数量
	 */
	private TextView detail_number;
	/**
	 * 总计
	 */
	private TextView total;
	/**
	 * 折约人民币
	 */
	private TextView mom_total;
	/**
	 * 支付方式
	 */
	private TextView payType;
	/**
	 * 支持
	 */
	private ImageView priomise1;
	private ImageView priomise2;

	private Class<IHaiGoActivity> lparentClass;
	private WayBillShippingAdapter adapter;
	private List<Map<String, String>> list;
	private Map<String, String> map;
	private LinearLayout transportLayout;
	
	private IHaiGoGroupActivity lparentGroupActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waybill_details);
		initTitle();
		Image();// 显示图片初始化
		setAgentView();// 代理商VIWE初始化
		setUserView();// 用户信息
		setgoodsView();// 商品信息

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		shippingItem = (WrapListView) findViewById(R.id.shipp_wrap);
		order_Nameber = (TextView) findViewById(R.id.order_Nameber);
		goodsNameText = (TextView) findViewById(R.id.goodsName);
		order_Tite = (TextView) findViewById(R.id.order_Tite);
		payType = (TextView) findViewById(R.id.payType);
		priomise1 = (ImageView) findViewById(R.id.priomise1);
		priomise2 = (ImageView) findViewById(R.id.priomise2);
		adapter = new WayBillShippingAdapter(WayBillDetailsActivtiy.this);
		shippingItem.setAdapter(adapter);
		transportLayout = (LinearLayout) findViewById(R.id.transportLayout);
		transportLayout.setVisibility(View.GONE);

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
								//
								// // 支付方式
								// String pay_type = jsonObject
								// .getString("payType");
								// if (pay_type.equals("1")) {
								payType.setText(getString(R.string.pay_way));
								// } else {
								// payType.setText(getString(R.string.pay_unionpay));
								// }

								/**
								 * 解析物流
								 */
								getShipping(jsonObject);
								/**
								 * 用户
								 */
								getUser(jsonObject);

								/**
								 * 商品
								 */
								getGoodsInfo(jsonObject);
								/**
								 * 代理商
								 */
								getAgent(jsonObject);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				}, 0, R.string.loading);
	}

	/**
	 * 商品信息初始化
	 */
	private void setgoodsView() {
		source = (ImageView) findViewById(R.id.source);
		supporthomecredit = (ImageView) findViewById(R.id.supporthomecredit);
		translatecom = (ImageView) findViewById(R.id.translatecom);
		goodsImage = (ImageView) findViewById(R.id.goodsImage);
		oldprice = (TextView) findViewById(R.id.oldprice);
		goodsname = (TextView) findViewById(R.id.goodsname);
		price = (TextView) findViewById(R.id.price);
		goodscolorval = (TextView) findViewById(R.id.goodscolorval);
		goodsruleval = (TextView) findViewById(R.id.goodsruleval);
		number = (TextView) findViewById(R.id.number);
		number.setText("x1");
		detail_number = (TextView) findViewById(R.id.detail_number);
		total = (TextView) findViewById(R.id.total);
		mom_total = (TextView) findViewById(R.id.mom_total);
	}

	/**
	 * 商品信息
	 */
	private void getGoodsInfo(JSONObject jsonObject) {
		if (jsonObject != null) {

			try {
				JSONObject logistics = jsonObject.getJSONObject("logistics");

				JSONArray goods = logistics.getJSONArray("package");

				for (int i = 0; i < goods.length(); i++) {
					// 条目编号,
					String id = goods.getJSONObject(i).getString("id");
					// "name": "商品名称",
					String name = goods.getJSONObject(i).getString("name");
					// "商品图标",

					String icon = goods.getJSONObject(i).getString("icon");
					// ": "颜色",
					String color = goods.getJSONObject(i).getString("color");
					// 尺码
					String size = goods.getJSONObject(i).getString("size");
					// 币种编号,

					// 数量
					String amount = goods.getJSONObject(i)
							.getString("currency");
					// 总价}
					String totala = goods.getJSONObject(i).getString("total");

					DataConfig newConfig = new DataConfig(this);

					String urlString = newConfig.getSourceById(goods
							.getJSONObject(i).getString("source"));
					if (!TextUtils.isEmpty(urlString)) {
						imageLoader.displayImage(urlString, source, options,
								animateFirstListener);
					}

					if (!TextUtils.isEmpty(icon)) {
						imageLoader.displayImage(icon, goodsImage, options,
								animateFirstListener);
					}
					goodscolorval.setText(color);
					goodsruleval.setText(size);
					goodsname.setText(name);

					usre_title.setText("共计 "
							+ goods.getJSONObject(i).getString("amount")
							+ " 件商品,代购价为  ￥"
							+ goods.getJSONObject(i).getString("total"));
					// 价格
					double pri = Double.valueOf(goods.getJSONObject(i)
							.getString("price"));// 价格
					// 折扣
					double discount = Double.valueOf(goods.getJSONObject(i)
							.getLong("discount"));
					double price_disc = pri * discount;
					String symbol = newConfig.getCurrencySymbolByCode(goods
							.getJSONObject(i).getString("currency"));
					oldprice.setText(symbol + String.valueOf(pri));
					price.setText(symbol + String.valueOf(price_disc));

					double exchange = newConfig.getCurRateByCode(goods
							.getJSONObject(i).getString("currency"));
					double Ram = price_disc * exchange;
					mom_total.setText("￥" + Utils.format(Ram));
					total.setText(totala);
					detail_number.setText(amount);

				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	/**
	 * 用户信息
	 */
	private void setUserView() {
		usre_title = (TextView) findViewById(R.id.usre_title);
		userName = (TextView) findViewById(R.id.userName);
		userNameber = (TextView) findViewById(R.id.userNameber);
		region = (TextView) findViewById(R.id.region);
		addressText = (TextView) findViewById(R.id.address);
		note = (TextView) findViewById(R.id.note);
	}

	/**
	 * 用户信息
	 * 
	 * @param jsonObject
	 */
	private void getUser(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {

				JSONObject logistics = jsonObject.getJSONObject("logistics");
				String e = logistics.getString("address");
				JSONObject json = new JSONObject(e);
				// 手机号码
				String number = json.getString("contactNumber");
				String contacts = json.getString("contacts");
				String id = json.getString("id");
				String postalCode = json.getString("postalCode");
				String status = json.getString("status");
				String userAddr = json.getString("userAddr");
				String userArea = json.getString("userArea");
				region.setText(userArea);
				userName.setText(contacts);
				userNameber.setText(number);
				addressText.setText(userAddr);
				note.setText(status);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void Image() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	/**
	 * 代理商VIWE初始化
	 */
	private void setAgentView() {
		puragent_name = (TextView) findViewById(R.id.puragent_name);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_credit_rating = (RatingBar) findViewById(R.id.puragent_credit_rating);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
	}

	/**
	 * 代理商
	 */
	private void getAgent(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {
				JSONObject logistics = jsonObject.getJSONObject("logistics");
				JSONObject agent = logistics.getJSONObject("company");
				if (agent != null && agent.length() > 0) {

					String id = agent.getString("id");
					float credit = agent.getInt("credit");// 信用
					puragent_credit_rating.setRating(credit);

					String head = agent.getString("icon");// 代理商LOgo
					puragent_head.setTag(head);
					imageLoader.displayImage(head, puragent_head, options,
							animateFirstListener);

					puragent_name.setText(agent.getString("name"));
					puragent_feeval.setText(agent.getString("charge"));
					puragent_shippingval.setText(agent.getString("logistics"));
					puragent_serviceval.setText(agent.getString("service"));
					// JSONArray promise = agent.getJSONArray("promise");
					// String data = null;
					// String one = null;
					// for (int i = 0; i < promise.length(); i++) {
					// data = (String) promise.get(i).toString();
					// one = (String) promise.get(i).toString();
					//
					// }

					DataConfig dataConfig = new DataConfig(this);
					JSONArray servicesArray = dataConfig
							.getSourceServiceById(id);
					if (servicesArray != null) {
						// 设置电商支持服务
						for (int i = 0; i < servicesArray.length(); i++) {
							int service = servicesArray.getInt(i);
							if (i == 0) {
								if (service == 1) {
									supporthomecredit
											.setImageResource(R.drawable.credit_card);
								} else {
									supporthomecredit
											.setImageResource(R.drawable.credit_card_grey);
								}
							}
							if (i == 1) {
								if (service == 1) {
									translatecom
											.setImageResource(R.drawable.transshipment);
								} else {
									translatecom
											.setImageResource(R.drawable.transshipment_grey);
								}
							}

						}

						// if (data.equals("0")) {
						// priomise1
						// .setBackgroundResource(R.drawable.no_pay_go);
						// } else {
						// priomise1.setBackgroundResource(R.drawable.pay_go);
						//
						// }
						// if (one.equals("0")) {
						// priomise2
						// .setBackgroundResource(R.drawable.no_pay_godo);
						//
						// } else {
						// priomise2
						// .setBackgroundResource(R.drawable.pay_godo);
						// }

					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

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
			if (jsonObject != null) {
				bje = jsonObject.getJSONObject("logistics");
				// order_Nameber.setText(bje.getString("content"));// 订单编号
				// goodsNameText.setText(bje.getString("company"));
				Long logItemLong = Utils.getCurrentTime();// 获取当前时间
				Long itmeLongbt = null;

				if (bje != null) {

					JSONArray items = bje.getJSONArray("items");
					for (int j = 0; j < items.length(); j++) {
						itmeLongbt = items.getJSONObject(0).getLong("time");
						Long itmeLong = items.getJSONObject(j).getLong("time");
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

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void refresh() {
		super.refresh();
		String id = null;
		Bundle bun = getIntent().getExtras();
		if (ShippingActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			id = bun.getString("id");
			lparentGroupActivity=parentGroupActivity;

		} else if (PersonalLogisticsActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			id = bun.getString("id");
			lparentGroupActivity=parentGroupActivity;
		}

		if (!TextUtils.isEmpty(id)) {
			initComponets(id);
		}

	}

	@Override
	protected void back() {
		lparentClass = parentClass;

		if (PersonalLogisticsActivity.class.equals(lparentClass)) {
			showTabHost = false;
			refreshActivity = false;
		} else if (ShippingActivity.class.equals(lparentClass)) {
			showTabHost = true;
			refreshActivity = true;
		}
		super.back();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			break;

		default:
			break;
		}
	}

}
