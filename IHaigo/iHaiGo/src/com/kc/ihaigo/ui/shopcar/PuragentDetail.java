/**
 * @Title: PuragentDetail.java
 * @Package: com.kc.ihaigo.ui.shopcar
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月7日 下午3:09:57

 * @version V1.0

 */

package com.kc.ihaigo.ui.shopcar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.ui.shopcar.adapter.PuragentDetailAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: PuragentDetail
 * @Description: 代购商详情
 * @author: ryan.wang
 * @date: 2014年7月7日 下午3:09:57
 * 
 */

public class PuragentDetail extends IHaiGoActivity {
	private WrapListView purdetail_comments;
	private RatingBar puragent_credit_rating;
	private TextView puragent_name;
	private ImageView puragent_head;
	private TextView purdetail_service;
	private TextView puragent_feeval;
	private TextView puragent_shippingval;
	private TextView puragent_serviceval;
	private TextView puragentdetail_fixedprice_val;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private JSONObject billParam;
	private JSONArray datasParams = null;
	private PuragentDetailAdapter adapter;
	protected Integer reback;
	protected Integer cancel;
	private String image;
	private String name;
	private String totalquantity;
	private String totalrmb;
	private float credit;
	private String price;
	private String feeval;
	private String shippingval;
	private String serviceval;
	private Class<IHaiGoActivity> lparentClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_puragent_detail);
		initTitle();
		initComponents();
	}

	@Override
	public void refresh() {
		Bundle resParams;
		if (PurchasingAgent.class.equals(parentClass)) {
			lparentClass = parentClass;
			resParams = getIntent().getExtras();
			image = resParams.getString("image");
			name = resParams.getString("name");
			totalquantity = resParams.getString("totalquantity");
			totalrmb = resParams.getString("totalrmb");
			credit = resParams.getFloat("credit");
			price = resParams.getString("price");
			feeval = resParams.getString("feeval");
			shippingval = resParams.getString("shippingval");
			serviceval = resParams.getString("serviceval");
			try {
				billParam = new JSONObject(resParams.getString("billParam"));
				datasParams = new JSONArray(resParams.getString("datasParams"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			imageLoader.displayImage(image, puragent_head, options,
					animateFirstListener);
			puragent_name.setText(name);
			puragent_credit_rating.setRating(credit);
			puragentdetail_fixedprice_val.setText(price);
			puragent_feeval.setText(feeval);
			puragent_shippingval.setText(shippingval);
			puragent_serviceval.setText(serviceval);
			long id = resParams.getLong("id", -1);
			String url = Constants.SHOPCAR_AGENT + "findAgents?id=" + id;
			HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
					new HttpReqCallBack() {
						@Override
						public void deal(String result) {
							if (!TextUtils.isEmpty(result)) {
								try {
									JSONObject resData = new JSONObject(result);
									((TextView) findViewById(R.id.introduce))
											.setText(resData
													.getString("introduce"));
									((TextView) findViewById(R.id.statement))
											.setText(resData
													.getString("statement"));
									JSONArray promiseData = resData
											.getJSONArray("promise");
									reback = (Integer) promiseData.get(0);
									cancel = (Integer) promiseData.get(1);
									if (reback == 1) {
										((ImageView) findViewById(R.id.puragentdetail_supportreback))
												.setBackgroundResource(R.drawable.supportreback);
									} else {

									}
									if (cancel == 1) {
										((ImageView) findViewById(R.id.puragentdetail_supportcancelorder))
												.setBackgroundResource(R.drawable.supportcancelorder);
									} else {

									}
									// count
									JSONArray commentsData = resData
											.getJSONArray("comments");
									adapter.setDatas(commentsData);
									adapter.notifyDataSetChanged();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					});
		}
	}

	/**
	 * @Title:
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initComponents() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
		purdetail_comments = (WrapListView) findViewById(R.id.purdetail_comments);
		adapter = new PuragentDetailAdapter(PuragentDetail.this);
		purdetail_comments.setAdapter(adapter);
		puragent_credit_rating = (RatingBar) findViewById(R.id.puragent_credit_rating);
		puragent_credit_rating.setRating(3.8f);
		purdetail_service = (TextView) findViewById(R.id.purdetail_service);
		purdetail_service.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PuragentDetail.this,
						ShopcarConfirmBillActivity.class);
				Bundle reqParams = new Bundle();
				reqParams.putString("datasParams", datasParams.toString());
				reqParams.putString("billParam", billParam.toString());
				reqParams.putString("image", image);
				reqParams.putString("name", name);
				reqParams.putFloat("credit", credit);
				reqParams.putString("price", price);
				reqParams.putString("feeval", feeval);
				reqParams.putString("shippingval", shippingval);
				reqParams.putString("serviceval", serviceval);
				reqParams.putInt("reback", reback);
				reqParams.putInt("cancel", cancel);
				reqParams.putString("totalquantity", totalquantity);
				reqParams.putString("totalrmb", totalrmb);
				intent.putExtras(reqParams);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
				ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
			}
		});
		puragent_name = (TextView) findViewById(R.id.puragent_name);
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		puragentdetail_fixedprice_val = (TextView) findViewById(R.id.puragentdetail_fixedprice_val);
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
				.setText(R.string.title_activity_puragentdetail);
		// 设置编辑事件
		// findViewById(R.id.title_left).setOnClickListener(new
		// OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// reBack();
		// }
		// });
	}

	/*
	 * <p>Title: dispatchKeyEvent</p> <p>Description: </p>
	 * 
	 * @param event
	 * 
	 * @return
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */

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
	// if (TAG.equals("ShopCarActivity")) {
	// Intent intent = new Intent(PuragentDetail.this,
	// PurchasingAgent.class);
	// intent.putExtra("TAG", "ShopCarActivity");
	// intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
	// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
	// ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
	// }
	//
	// }
}
