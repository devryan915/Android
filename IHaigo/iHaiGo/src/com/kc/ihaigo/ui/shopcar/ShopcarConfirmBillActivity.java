package com.kc.ihaigo.ui.shopcar;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.AddressActivity;
import com.kc.ihaigo.ui.personal.ChoicePayWay;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.ui.shopcar.adapter.ShopcarCfAdapter;
import com.kc.ihaigo.ui.shopcar.adapter.ShopcarChoiceAddressAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: ShopcarConfirmBillActivity
 * @Description: 购物车确认订单页面
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:09:18
 * 
 */
public class ShopcarConfirmBillActivity extends IHaiGoActivity {
	private WrapListView shopcar_goods;
	private TextView shopcar_confirmbill_addressval;
	private TextView shopcar_confrimbill_commitbill;
	private JSONObject billParam;
	private JSONArray datasParams = null;
	private ShopcarCfAdapter adapter;
	private ShopcarChoiceAddressAdapter addressAdapter;
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
	private JSONObject addressParam;
	private int reback;
	private int cancel;
	private String TAG;
	private String image;
	private String name;
	private float credit;
	private String price;
	private String feeval;
	private String shippingval;
	private String serviceval;
	private String totalquantity;
	private String totalrmb;
	private Class<IHaiGoActivity> lparentClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopcar_confirmbill);
		initTitle();
		initComponents();
	}

	private void loadAddress() {
		String url = Constants.PER_USERCENTER + "getUserAddress";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("userId", Constants.USER_ID);
		reqParams.put("page", 1 + "");
		reqParams.put("pagesize", Integer.MAX_VALUE + "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								addressAdapter.setDatas(resData
										.getJSONArray("userAddress"));
								addressAdapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);

	}

	@Override
	public void refresh() {
		try {
			loadAddress();
			Bundle reqParams;
			if (PuragentDetail.class.equals(parentClass)) {
				lparentClass = parentClass;
				reqParams = getIntent().getExtras();
				image = reqParams.getString("image");
				name = reqParams.getString("name");
				credit = reqParams.getFloat("credit", 0f);
				price = reqParams.getString("price");
				feeval = reqParams.getString("feeval");
				shippingval = reqParams.getString("shippingval");
				serviceval = reqParams.getString("serviceval");
				totalquantity = reqParams.getString("totalquantity");
				totalrmb = reqParams.getString("totalrmb");
				billParam = new JSONObject(reqParams.getString("billParam"));
				datasParams = new JSONArray(reqParams.getString("datasParams"));
				adapter.setDatas(datasParams);
				adapter.notifyDataSetChanged();
				imageLoader.displayImage(image, puragent_head, options,
						animateFirstListener);
				puragent_name.setText(name);
				puragent_credit_rating.setRating(credit);
				((TextView) findViewById(R.id.puragent_quantity_val))
						.setText("x" + totalquantity);
				((TextView) findViewById(R.id.puragent_quantity_valnum))
						.setText(totalquantity);
				puragentdetail_fixedprice_val.setText(price);
				puragent_feeval.setText(feeval);
				puragent_shippingval.setText(shippingval);
				puragent_serviceval.setText(serviceval);
				reback = reqParams.getInt("reback", -1);
				cancel = reqParams.getInt("cancel", -1);
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
			} else if (AddressActivity.class.equals(parentClass)) {
				loadAddress();
			} else if (ChoicePayWay.class.equals(parentClass)) {
				reqParams = getIntent().getExtras();
				((TextView) findViewById(R.id.shopcar_confirmbill_buytypeval))
						.setText(reqParams.getString("paytypename"));
				billParam.put("paytypeid", reqParams.getInt("paytypeid", -1));
				billParam
						.put("paytypename", reqParams.getString("paytypename"));
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @Title: initComponents
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initComponents() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
		shopcar_goods = (WrapListView) findViewById(R.id.shopcar_goods);
		adapter = new ShopcarCfAdapter(ShopcarConfirmBillActivity.this);
		shopcar_goods.setAdapter(adapter);
		shopcar_confirmbill_addressval = (TextView) findViewById(R.id.shopcar_confirmbill_addressval);
		shopcar_confrimbill_commitbill = (TextView) findViewById(R.id.shopcar_confrimbill_commitbill);
		addressAdapter = new ShopcarChoiceAddressAdapter(
				ShopcarConfirmBillActivity.this);
		shopcar_confirmbill_addressval
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						/**
						 * 说明 DialogUtil.showChoiceAddress是收货地址的弹框
						 * ShopcarChoiceAddressAdapter 收货地址数据适配
						 */
						Toast.makeText(ShopcarConfirmBillActivity.this,
								"选择收货地址", 0).show();
						DialogUtil.showChoiceAddress(
								ShopcarConfirmBillActivity.this.getParent(),
								new BackCall() {

									@Override
									public void deal(int whichButton,
											Object... obj) {
										((Dialog) obj[0]).dismiss();
										try {
											switch (whichButton) {
												case R.id.addr_complete :
													try {
														addressParam = new JSONObject(
																obj[1].toString());
														billParam
																.put("address",
																		addressParam
																				.getString("id"));
														shopcar_confirmbill_addressval
																.setText(obj[2]
																		+ "");
													} catch (JSONException e) {
														e.printStackTrace();
													}
													break;
												case R.id.add_new_addr :
													Intent intent = new Intent(
															ShopcarConfirmBillActivity.this,
															AddressActivity.class);
													intent.putExtra(
															IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
															false);
													intent.putExtra(
															IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
															true);
													ShopCarGroupActiviy.group
															.startiHaiGoActivity(intent);
													break;
												default :
													break;
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}, null, addressAdapter).show();;
					}
				});
		shopcar_confrimbill_commitbill
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						commitBill();
					}
				});
		puragent_credit_rating = (RatingBar) findViewById(R.id.puragent_credit_rating);
		puragent_credit_rating.setRating(3.8f);
		puragent_name = (TextView) findViewById(R.id.puragent_name);
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		puragentdetail_fixedprice_val = (TextView) findViewById(R.id.puragentdetail_fixedprice_val);

		findViewById(R.id.choice_pay_way).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								ShopcarConfirmBillActivity.this,
								ChoicePayWay.class);
						Bundle bundle = new Bundle();
						intent.putExtras(bundle);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								false);
						intent.putExtra(
								IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
						ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
					}
				});
	}

	private void commitBill() {
		String url = Constants.REC_ORDERS;
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("user", Constants.USER_ID);
		try {
			reqParams.put("items", billParam.getString("itemids"));
			reqParams.put("agent", billParam.getString("agent"));
			reqParams.put("address", billParam.getString("address"));
			// billParam.getString("payType")
			reqParams.put("payType", billParam.getString("paytypeid"));
			reqParams
					.put("bak",
							((EditText) findViewById(R.id.shopcar_confirmbill_commentsval))
									.getText().toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								if ("1".equals(resData.getString("code"))) {
									if (true) {
										Intent intent = new Intent(
												ShopcarConfirmBillActivity.this,
												ShopcarPayBill.class);
										Bundle reqParams = new Bundle();
										billParam.put("orderId",
												resData.getString("orderId"));
										reqParams.putString("billParam",
												billParam.toString());
										reqParams.putString("image", image);
										reqParams.putString("name", name);
										reqParams.putFloat("credit", credit);
										reqParams.putString("price", price);
										reqParams.putString("feeval", feeval);
										reqParams.putString("shippingval",
												shippingval);
										reqParams.putString("serviceval",
												serviceval);
										reqParams.putInt("reback", reback);
										reqParams.putInt("cancel", cancel);
										reqParams.putString("totalquantity",
												totalquantity);
										reqParams.putString("totalrmb",
												totalrmb);
										reqParams.putString("datasParams",
												datasParams.toString());
										reqParams
												.putString(
														"bak",
														((EditText) findViewById(R.id.shopcar_confirmbill_commentsval))
																.getText()
																.toString());
										reqParams.putString("addressParam",
												addressParam.toString());
										intent.putExtras(reqParams);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										ShopCarGroupActiviy.group
												.startiHaiGoActivity(intent);

									} else if (TAG.equals("Personal")) {
										Intent intent = new Intent(
												ShopcarConfirmBillActivity.this,
												ShopcarPayBill.class);
										intent.putExtra("TAG", "Personal");
										billParam.put("orderId",
												resData.getString("orderId"));
										intent.putExtra("billParam",
												billParam.toString());
										intent.putExtra("image", getIntent()
												.getStringExtra("image"));
										intent.putExtra("name", getIntent()
												.getStringExtra("name"));
										intent.putExtra("credit", getIntent()
												.getStringExtra("credit"));
										intent.putExtra("price", getIntent()
												.getStringExtra("price"));
										intent.putExtra("feeval", getIntent()
												.getStringExtra("feeval"));
										intent.putExtra(
												"shippingval",
												getIntent().getStringExtra(
														"shippingval"));
										intent.putExtra(
												"serviceval",
												getIntent().getStringExtra(
														"serviceval"));
										intent.putExtra("reback", reback);
										intent.putExtra("cancel", cancel);
										intent.putExtra(
												"totalquantity",
												getIntent()
														.getCharSequenceExtra(
																"totalquantity"));
										intent.putExtra(
												"totalrmb",
												getIntent()
														.getCharSequenceExtra(
																"totalrmb"));
										intent.putExtra("datasParams",
												datasParams.toString());
										intent.putExtra(
												"bak",
												((EditText) findViewById(R.id.shopcar_confirmbill_commentsval))
														.getText());
										intent.putExtra("addressParam",
												addressParam.toString());
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										PersonalGroupActivity.group
												.startiHaiGoActivity(intent);

									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	@Override
	protected void back() {
		parentClass = lparentClass;
		super.back();
	};

	/**
	 * @Title: initTitle
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {
		// 设置标题
		((TextView) findViewById(R.id.title_middle))
				.setText(R.string.title_activity_confirmbill);
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
	// Intent intent = new Intent(ShopcarConfirmBillActivity.this,
	// PuragentDetail.class);
	// intent.putExtra("TAG", "PuragentDetail");
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
	// ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
	// } else if (TAG.equals("Personal")) {
	// Intent intent = new Intent(ShopcarConfirmBillActivity.this,
	// AddressActivity.class);
	// intent.putExtra("TAG", "Personal");
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
	// PersonalGroupActivity.group.startiHaiGoActivity(intent);
	// }
	//
	// }

}
