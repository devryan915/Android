package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.SearchResultActivity;
import com.kc.ihaigo.ui.recommend.adapter.JoinShopCartAdapter;
import com.kc.ihaigo.ui.recommend.adapter.JoinShopCartAdapter.ViewHolder;
import com.kc.ihaigo.ui.shopcar.ShopCarActivity;
import com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalJoinShopCartActivity extends IHaiGoActivity implements
		OnItemClickListener {
	protected static final String TAG_T = "JoinShopCartActivity";
	private GridView choose;
	private GridView choose_size;
	private JoinShopCartAdapter coloradapter;
	private JoinShopCartAdapter adapter;
	private View colorselectedView;// 记录当前选中的view
	private View ruleselectedView;
	private TextView buyCount;
	private TextView choose_ok;
	private Long selcorlorId;
	private Long selruleId;
	private TextView inventory;
	private int amount = 0;// 库存数量

	private Class<IHaiGoActivity> lparentClass;
	private String goodsurl;
	private String sourceurl;
	private String goodsname;
	private String price;
	private String discount;
	private String curRate;
	private String rmbprice;
	private String goodId;
	private IHaiGoGroupActivity lparentGroupActivity;
	private LinearLayout lin_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_shipping);
		initTitle();
		ininComponets();
	}

	@Override
	public void refresh() {
		super.refresh();
		// 清空数据
		coloradapter.setDatas(new JSONArray());
		adapter.setDatas(new JSONArray());

		Bundle resParams = getIntent().getExtras();
		if (PersonalGoodsDetailsActivity.class.equals(parentClass)
				|| SearchResultActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			lparentGroupActivity = parentGroupActivity;

			resParams = getIntent().getExtras();
			goodId = resParams.getString("gid");
			price = resParams.getString("price");
			discount = resParams.getString("discount");
			rmbprice = resParams.getString("rmbprice");
			curRate = resParams.getString("curRate");
			sourceurl = resParams.getString("sourceurl");
			goodsurl = resParams.getString("goodsurl");
			goodsname = resParams.getString("goodsname");

		}

		ImageLoader.getInstance().displayImage(goodsurl,
				((ImageView) findViewById(R.id.goodsImg)));
		ImageLoader.getInstance().displayImage(sourceurl,
				((ImageView) findViewById(R.id.merchants)));
		((TextView) findViewById(R.id.goodsName_tv)).setText(goodsname);
		((TextView) findViewById(R.id.originalPrice_tv)).setText(price);
		((TextView) findViewById(R.id.actualPrice_tv)).setText(discount);
		((TextView) findViewById(R.id.ram_actualPrice_tv)).setText(curRate);
		((TextView) findViewById(R.id.goodsActualPrice)).setText(rmbprice);

		if (!TextUtils.isEmpty(goodId)) {

			String url = Constants.REC_STOCKS + goodId + "/stocks";
			HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
					new HttpReqCallBack() {
						@Override
						public void deal(String result) {
							try {
								if (TextUtils.isEmpty(result)) {
									lin_info.setVisibility(View.INVISIBLE);
									return;
								} else {
									lin_info.setVisibility(View.VISIBLE);
									JSONObject datasObject = new JSONObject(
											result);
									JSONArray datas = datasObject
											.getJSONArray("stocks");
									coloradapter.setDatas(datas);
									choose.setAdapter(coloradapter);
									if (datas != null & datas.length() > 0) {
										choose.performItemClick(coloradapter
												.getView(0, null, null), 0,
												coloradapter.getItemId(0));
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
		}

	}

	private void ininComponets() {
		adapter = new JoinShopCartAdapter(PersonalJoinShopCartActivity.this);
		coloradapter = new JoinShopCartAdapter(
				PersonalJoinShopCartActivity.this);
		choose = (GridView) findViewById(R.id.choose);
		choose.setOnItemClickListener(this);
		choose_size = (GridView) findViewById(R.id.choose_size);
		choose_size.setAdapter(adapter);
		choose_size.setOnItemClickListener(this);
		buyCount = (TextView) findViewById(R.id.buyCount);
		choose_ok = (TextView) findViewById(R.id.choose_ok);
		choose_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!TextUtils.isEmpty(goodId)
						&& !TextUtils.isEmpty(selcorlorId + "")
						&& !TextUtils.isEmpty(selruleId + "")
						&& !TextUtils.isEmpty(buyCount.getText() + "")) {
					addShopCart();
				}

			}
		});
		findViewById(R.id.minusgoods).setOnClickListener(this);
		findViewById(R.id.addgoods).setOnClickListener(this);
	}

	private boolean checkGoods() {
		if (!TextUtils.isEmpty(buyCount.getText())) {
			String count = (String) buyCount.getText();
			if (Integer.parseInt(count) > amount) {
				Toast.makeText(PersonalJoinShopCartActivity.this,
						R.string.shopcar_goods_nostocks, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
			if (selcorlorId == null || selruleId == null) {
				return false;
			}
		}
		return true;
	}

	private void addShopCart() {
		if (!checkGoods())
			return;
		String url = Constants.REC_CARTS + Constants.USER_ID;
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("good", goodId);
		reqParams.put("color", selcorlorId + "");
		reqParams.put("size", selruleId + "");
		reqParams.put("amount", buyCount.getText() + "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (Constants.Debug) {
							Log.d(TAG_T, result + "");
						}
						try {
							if (!TextUtils.isEmpty(result)) {
								JSONObject resObject = new JSONObject(result);
								String code = resObject.getString("code");
								if ("1".equals(code)) {
									Toast.makeText(
											PersonalJoinShopCartActivity.this,
											R.string.shopcar_goods_addshopcar,
											1000).show();
									Intent intent = new Intent(
											PersonalJoinShopCartActivity.this,
											PersonalActivity.class);

									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											true);
									// 先回到首页在转到购物车页面
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
									// 显示新加入购物车数量
									((TextView) IHaiGoMainActivity.main
											.findViewById(R.id.shopcar_new_goods))
											.setText(1+"");
									((TextView) IHaiGoMainActivity.main
											.findViewById(R.id.shopcar_new_goods))
											.setVisibility(View.VISIBLE);

									IHaiGoMainActivity.main
											.setCurrentTab(IHaiGoMainActivity.SHOPCART);
									Intent shopcart = new Intent(
											PersonalJoinShopCartActivity.this,
											ShopCarActivity.class);
									shopcart.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											true);
									shopcart.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									ShopCarGroupActiviy.group
											.startiHaiGoActivity(shopcart);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		inventory = (TextView) findViewById(R.id.inventory);
		lin_info = (LinearLayout) findViewById(R.id.lin_info);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long arg3) {
		TextView choose = (TextView) view.findViewById(R.id.choose);
		choose.setBackgroundResource(R.drawable.selected);
		if (view.getTag() != null) {
			Object obj = ((ViewHolder) view.getTag()).choose.getTag();
			if (obj instanceof JSONArray) {
				if (colorselectedView != null && colorselectedView != choose) {
					colorselectedView.setBackgroundResource(R.drawable.uncheck);
				}
				colorselectedView = choose;
				// 选择颜色则更新尺寸
				adapter.setDatas((JSONArray) obj);
				adapter.notifyDataSetChanged();
				selcorlorId = adapterView.getItemIdAtPosition(position);
			} else {
				if (ruleselectedView != null && ruleselectedView != choose) {
					ruleselectedView.setBackgroundResource(R.drawable.uncheck);
				}
				ruleselectedView = choose;
				selruleId = adapterView.getItemIdAtPosition(position);
				if (choose.getTag() != null) {
					amount = (Integer) choose.getTag();

				}
				inventory.setVisibility(View.GONE);
				inventory.setText(this.getResources().getString(
						R.string.inventory_tv)
						+ obj + this.getResources().getString(R.string.second));
				// Toast.makeText(PersonalJoinShopCartActivity.this, "" + obj,
				// 1000).show();
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.minusgoods:
			String count = (String) buyCount.getText();
			if (Integer.parseInt(count) > 1) {
				buyCount.setText((Integer.parseInt(count) - 1) + "");
			}

			break;
		case R.id.addgoods:
			String coun = (String) buyCount.getText();
			buyCount.setText((Integer.parseInt(coun) + 1) + "");

			break;
		case R.id.title_left:
			break;
		default:
			break;
		}

	}

	@Override
	protected void back() {
		lparentGroupActivity = parentGroupActivity;
		showTabHost = false;
		refreshActivity = true;
		super.back();
	}

}
