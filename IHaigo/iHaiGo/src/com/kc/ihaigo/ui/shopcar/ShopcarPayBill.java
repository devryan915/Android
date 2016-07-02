/**
 * @Title: ShopcarPayBill.java
 * @Package: com.kc.ihaigo.ui.shopcar
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月9日 下午3:54:22

 * @version V1.0

 */

package com.kc.ihaigo.ui.shopcar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.ChoicePayWay;
import com.kc.ihaigo.ui.shopcar.adapter.PurAgentGoodsAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.pay.AliPayUtil;
import com.kc.ihaigo.util.pay.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: ShopcarPayBill
 * @Description: 购物车支付订单
 * @author: ryan.wang
 * @date: 2014年7月9日 下午3:54:22
 * 
 */

public class ShopcarPayBill extends IHaiGoActivity {
	private ListView puragent_goodslist;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private RatingBar puragent_credit_rating;
	private TextView puragent_name;
	private ImageView puragent_head;
	private TextView puragent_feeval;
	private TextView puragent_shippingval;
	private TextView puragent_serviceval;
	private TextView puragentdetail_fixedprice_val;
	private TextView shopcar_confrimbill_paybill;
	private TextView shopcar_confirmbill_buytypeval;
	private PurAgentGoodsAdapter adapter;
	private String orderId = " ";
	private Class<IHaiGoActivity> lparentClass;
	private StringBuffer subjects = new StringBuffer();// 购买的商品名称
	private StringBuffer body = new StringBuffer();// 购买的商品详情
	private View puragent_goodslist_more;
	private TextView shopcar_paybill_payamountval;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopcar_paybill);
		initTitle();
		initComponents();
	}
	@Override
	public void refresh() {
		// 初始化已购商品
		Bundle bundle;
		if (ShopcarConfirmBillActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			bundle = getIntent().getExtras();
			String goods = bundle.getString("datasParams");
			try {
				if (!TextUtils.isEmpty(goods)) {
					JSONArray datasParams = new JSONArray(goods);
					JSONArray goodsDatas = new JSONArray();
					StringBuffer itemIds = null;
					for (int i = 0; i < datasParams.length(); i++) {
						JSONArray childItems = datasParams.getJSONObject(i)
								.getJSONArray("childitems");
						for (int j = 0; j < childItems.length(); j++) {
							JSONObject data = childItems.getJSONObject(j);
							JSONObject goodsData = new JSONObject();
							goodsData.put("name", data.getString("name"));
							subjects.append(data.getString("name") + "x"
									+ data.getString("amount") + ",");
							goodsData.put("amount", data.getString("amount"));
							goodsDatas.put(goodsData);
							if (itemIds == null) {
								itemIds = new StringBuffer(data.getString("id"));
							} else {
								itemIds.append("," + data.getString("id"));
							}
						}
					}
					if (goodsDatas.length() > 3) {
						puragent_goodslist_more.setVisibility(View.VISIBLE);
					} else {
						puragent_goodslist_more.setVisibility(View.GONE);
					}
					adapter.setDatas(goodsDatas);
					adapter.notifyDataSetChanged();
					// 初始化收货地址
					JSONObject addressParam = new JSONObject(
							bundle.getString("addressParam"));
					((TextView) findViewById(R.id.shopcar_paybill_username))
							.setText(addressParam.getString("contacts"));
					((TextView) findViewById(R.id.shopcar_paybill_userno))
							.setText(addressParam.getString("contactNumber"));
					((TextView) findViewById(R.id.shopcar_paybill_address))
							.setText(addressParam.getString("userArea"));
					((TextView) findViewById(R.id.shopcar_paybill_addressdetail))
							.setText(addressParam.getString("userAddr"));
					body.append(addressParam.getString("contacts")
							+ addressParam.getString("contactNumber")
							+ addressParam.getString("userArea")
							+ addressParam.getString("userAddr"));
					JSONObject billParam = new JSONObject(
							bundle.getString("billParam"));
					shopcar_confirmbill_buytypeval.setText(billParam
							.getString("paytypename"));
					shopcar_confirmbill_buytypeval.setTag(billParam
							.getInt("paytypeid"));
					orderId = billParam.getString("orderId");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			imageLoader.displayImage(bundle.getString("image"), puragent_head,
					options, animateFirstListener);
			puragent_name.setText(bundle.getString("name"));
			puragent_credit_rating.setRating(bundle.getFloat("credit", 0f));
			((TextView) findViewById(R.id.puragent_quantity_val)).setText("x"
					+ bundle.getString("totalquantity"));
			((TextView) findViewById(R.id.puragent_quantity_valnum))
					.setText(bundle.getString("totalquantity"));
			((TextView) findViewById(R.id.puragent_discounted_val)).setText("￥"
					+ bundle.getString("totalrmb"));
			puragentdetail_fixedprice_val.setText(bundle.getString("price"));
			shopcar_paybill_payamountval.setText(bundle.getString("price")
					.substring(0, 1)
					+ Utils.format(Double.parseDouble(bundle.getString("price")
							.substring(1))));
			puragent_feeval.setText(bundle.getString("feeval"));
			puragent_shippingval.setText(bundle.getString("shippingval"));
			puragent_serviceval.setText(bundle.getString("serviceval"));
			int reback = bundle.getInt("reback", -1);
			int cancel = bundle.getInt("cancel", -1);
			if (reback == 1) {
				((ImageView) findViewById(R.id.puragentdetail_supportreback))
						.setBackgroundResource(R.drawable.supportreback);
			} else if (reback == 0) {
				((ImageView) findViewById(R.id.puragentdetail_supportreback))
						.setBackgroundResource(R.drawable.supportreback);
			}
			if (cancel == 1) {
				((ImageView) findViewById(R.id.puragentdetail_supportcancelorder))
						.setBackgroundResource(R.drawable.supportcancelorder);
			} else if (cancel == 0) {
				((ImageView) findViewById(R.id.puragentdetail_supportcancelorder))
						.setBackgroundResource(R.drawable.supportcancelorder);
			}
			((TextView) findViewById(R.id.shopcar_paybill_shopcar_paybill_descval))
					.setText(bundle.getString("bak"));
		}
	}
	/**
	 * @Title: initComponents
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initComponents() {
		puragent_goodslist = (ListView) findViewById(R.id.puragent_goodslist);
		adapter = new PurAgentGoodsAdapter(ShopcarPayBill.this);
		adapter.setCount(3);
		puragent_goodslist.setAdapter(adapter);
		findViewById(R.id.puragent_goodslist_more).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						v.setVisibility(View.GONE);
						adapter.setCount(-1);
						adapter.notifyDataSetChanged();
					}
				});
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
		puragent_credit_rating = (RatingBar) findViewById(R.id.puragent_credit_rating);
		puragent_credit_rating.setRating(3.8f);
		puragent_name = (TextView) findViewById(R.id.puragent_name);
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		puragentdetail_fixedprice_val = (TextView) findViewById(R.id.puragentdetail_fixedprice_val);
		shopcar_confrimbill_paybill = (TextView) findViewById(R.id.shopcar_confrimbill_paybill);
		shopcar_confrimbill_paybill.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				payBill();
			}
		});
		shopcar_confirmbill_buytypeval = ((TextView) findViewById(R.id.shopcar_confirmbill_buytypeval));
		puragent_goodslist_more = findViewById(R.id.puragent_goodslist_more);
		shopcar_paybill_payamountval = (TextView) findViewById(R.id.shopcar_paybill_payamountval);
	}

	/**
	 * 
	 * @Title: payBill
	 * @user: ryan.wang
	 * @Description: 购物车这边暂时无法支付
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private void payBill() {
		int payType = (Integer) shopcar_confirmbill_buytypeval.getTag();
		switch (payType) {
			case ChoicePayWay.Alipay :
				AliPayUtil.payBill(ShopcarPayBill.this.getParent(),
						new Handler(ShopcarPayBill.this.getParent()
								.getMainLooper()) {
							@Override
							public void handleMessage(Message msg) {
								Result result = new Result((String) msg.obj);
								switch (msg.what) {
									case AliPayUtil.RQF_PAY :
										if (9000 == result.getResultCode()) {
											try {
												lparentClass = (Class<IHaiGoActivity>) Class
														.forName("com.kc.ihaigo.ui.shopcar.ShopCarActivity");
												back();
											} catch (ClassNotFoundException e) {
												e.printStackTrace();
											}
										}
										break;
									default :
										break;
								}
								if (Constants.Debug) {
									Toast.makeText(ShopcarPayBill.this,
											msg.obj.toString(),
											Toast.LENGTH_SHORT).show();
								}
							}

						}, orderId, subjects.substring(0, subjects.toString()
								.length() - 1), body.toString(), "0.01");
				// shopcar_paybill_payamountval.getText().toString().substring(1)
				break;
			default :
				break;
		}
	}
	@Override
	protected void back() {
		parentClass = lparentClass;
		super.back();
	}
	/**
	 * @Title: initTitle
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initTitle() {
		// 设置标题
		((TextView) findViewById(R.id.title_middle))
				.setText(R.string.title_activity_paybill);
		// findViewById(R.id.title_left).setOnClickListener(new
		// OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// reBack();
		// }
		// });
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	// reBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	// protected void reBack() {
	//
	// if (TAG.equals("PuragentDetail")) {
	// Intent intent = new Intent(ShopcarPayBill.this,
	// ShopcarConfirmBillActivity.class);
	// intent.putExtra("TAG", "PuragentDetail");
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
	// ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
	// } else if (TAG.equals("Personal")) {
	// Intent intent = new Intent(ShopcarPayBill.this,
	// ShopcarConfirmBillActivity.class);
	// intent.putExtra("TAG", "Personal");
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
	// PersonalGroupActivity.group.startiHaiGoActivity(intent);
	// }
	//
	// }
}
