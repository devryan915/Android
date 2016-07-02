package com.kc.ihaigo.ui.shopcar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGoodsDetailsActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.recommend.RecommendActivity;
import com.kc.ihaigo.ui.recommend.RecommendGroupActiviy;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.shopcar.adapter.ShopcarGoodsAdapter;
import com.kc.ihaigo.ui.shopcar.adapter.ShopcarGoodsItemsAdapter.ViewHolder;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: ShopCarActivity
 * @Description: 购物车主页
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:09:18
 * 
 */
public class ShopCarActivity extends IHaiGoActivity {
	private PullUpRefreshListView shopcar_goods;
	private CheckBox shopcar_selall_chk;
	private View shopcar_bottoom_pay;
	private ShopcarGoodsAdapter adapter;
	private int currentStatus = VIEW_STATUS;
	private final static int VIEW_STATUS = 0;
	private final static int EDIT_STATUS = 1;
	private final static String TAG = "ShopCarActivity";
	private StringBuffer deleteGoodsId = null;
	private TextView shopcar_bottom_counts;
	private TextView shopcar_bottom_discountedprice;
	private TextView addshopcartgoods;
	private BackCall call;
	private List<String> selectGoodIds = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_car);
		initTitle();
		initComponents();
		loadData();
	}

	/**
	 * @Title: loadData
	 * @user: ryan.wang
	 * @Description:
	 * @throws
	 */

	private void loadData() {
		String url = Constants.REC_CARTS + Constants.USER_ID;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						try {
							if (TextUtils.isEmpty(result))
								return;
							JSONArray datas = new JSONArray();// 按照电商分类后的数据datas
							JSONObject data;// 某一个电商的数据信息，包括source和childitems
							JSONObject resData = new JSONObject(result);
							// 对数据进行电商分类处理
							JSONArray temp = resData.getJSONArray("items");
							if (temp.length() > 0) {
								findViewById(R.id.empty).setVisibility(
										View.GONE);
								findViewById(R.id.have).setVisibility(
										View.VISIBLE);
							}
							addshopcartgoods.setText(temp.length() + "");
							double totalRMB = 0;
							int totalcount = 0;// 总数量
							String currency = "";
							DataConfig dataConfig = new DataConfig(
									ShopCarActivity.this);
							for (int i = 0; i < temp.length(); i++) {
								int source = temp.getJSONObject(i).getInt(
										"source");
								totalcount += temp.getJSONObject(i).getInt(
										"amount");
								currency = temp.getJSONObject(i).getString(
										"currency");
								totalRMB += temp.getJSONObject(i).getDouble(
										"discount")
										* dataConfig.getCurRateByCode(currency)
										* temp.getJSONObject(i)
												.getInt("amount");
								JSONArray childItems = null;
								// 寻找此电商分类是否有存在，如果存在则将当前商品加入到电商分类中
								for (int j = 0; j < datas.length(); j++) {
									if (source == datas.getJSONObject(j)
											.getInt("source")) {
										childItems = datas.getJSONObject(j)
												.getJSONArray("childitems");
										childItems.put(temp.getJSONObject(i));
									}
								}
								// 如果不存在当前商品的电商分类，则新建一个电商分类
								if (childItems == null) {
									data = new JSONObject();
									data.put("source", source);
									childItems = new JSONArray();
									childItems.put(temp.getJSONObject(i));
									data.put("childitems", childItems);
									datas.put(data);
								}
							}
							adapter.setDatas(datas);
							adapter.setCall(call);
							adapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, 0);
	}

	/**
	 * 
	 * @Title: addFavorite
	 * @user: ryan.wang
	 * @Description: 加入收藏 void
	 * @throws
	 */
	private void addFavorite(String goodsId) {
		// String url = Constants.PER_USERCENTER + "insertCollection";
		String url = Constants.PER_USERCENTER + "favorites";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		// reqParams.put("goodId", goodsId);
		reqParams.put("userId", Constants.USER_ID);
		reqParams.put("itemIds", goodsId);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								if ("1".equals(resData.getString("code"))) {
									Toast.makeText(ShopCarActivity.this,
											R.string.shopcar_goods_addfavorite,
											Toast.LENGTH_SHORT).show();
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
		exitApp();
	}

	@Override
	public void refresh() {
		addshopcartgoods.setText("0");
		shopcar_selall_chk.setChecked(false);
		shopcar_bottom_counts.setText("0");
		shopcar_bottom_discountedprice.setText("￥0.00");
		shopcar_bottom_discountedprice.setTag(0d);
		adapter = new ShopcarGoodsAdapter(ShopCarActivity.this,
				new GoodsDealListener());
		shopcar_goods.setAdapter(adapter);
		loadData();
	}

	/**
	 * @Title: initComponents
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initComponents() {
		shopcar_goods = (PullUpRefreshListView) findViewById(R.id.shopcar_goods);
		adapter = new ShopcarGoodsAdapter(ShopCarActivity.this,
				new GoodsDealListener());
		shopcar_goods.setAdapter(adapter);
		shopcar_selall_chk = (CheckBox) findViewById(R.id.shopcar_selall_chk);
		shopcar_selall_chk
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						adapter.setCheckedTotal(isChecked);
						adapter.notifyDataSetChanged();
					}
				});
		shopcar_goods.setScrollBottomListener(new ScrollBottomListener() {
			@Override
			public void deal() {
				loadData();
			}
		});
		shopcar_bottoom_pay = findViewById(R.id.shopcar_bottoom_pay);
		shopcar_bottoom_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selcount = Integer.parseInt((String) shopcar_bottom_counts
						.getText());
				if (selcount < 1) {
					Toast.makeText(ShopCarActivity.this,
							R.string.shopcar_goods_nogoods, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				DialogUtil.showPayDialog(ShopCarActivity.this.getParent(),
						new BackCall() {
							@Override
							public void deal(int whichButton, Object... obj) {
								switch (whichButton) {
								case R.id.dialog_pay_selfbuy:
									Toast.makeText(ShopCarActivity.this, "自购",
											1000).show();
									((Dialog) obj[0]).dismiss();
 
									String url = "http://192.168.1.4:8080/transports/";
									Log.e("url------------", url);
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("user",  Constants.USER_ID);
									map.put("items", selectGoodIds.toString());

									HttpAsyncTask.fetchData(HttpAsyncTask.GET,
											url, map, new HttpReqCallBack() {

												@Override
												public void deal(String result) {
													// TODO Auto-generated
													// method stub

													if (!TextUtils
															.isEmpty(result)) {
														if (result.trim().equals("1")) {
															Intent intents = new Intent(
																	ShopCarActivity.this,
																	SelfBuyActivity.class);
															startActivity(intents);

														}
													}

												}

											}, 1, R.string.loading);

									break;
								case R.id.dialog_pay_otherbuy:
									// 代购
									((Dialog) obj[0]).dismiss();
									Intent intent = new Intent(
											ShopCarActivity.this,
											PurchasingAgent.class);
									Bundle reqParams = new Bundle();
									reqParams.putString(
											"datas",
											deleteGoods(adapter.getDatas(),
													selectGoodIds).toString());
									reqParams.putString("totalquantity",
											shopcar_bottom_counts.getText()
													.toString());
									reqParams.putString("totalrmb",
											shopcar_bottom_discountedprice
													.getTag().toString()
													.toString());
									intent.putExtras(reqParams);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									ShopCarGroupActiviy.group
											.startiHaiGoActivity(intent);
									break;
								default:
									break;
								}
							}
						}, null);
			}
		});
		shopcar_bottom_counts = (TextView) findViewById(R.id.shopcar_bottom_counts);
		shopcar_bottom_discountedprice = (TextView) findViewById(R.id.shopcar_bottom_discountedprice);
		shopcar_bottom_discountedprice.setTag(0d);
		addshopcartgoods = ((TextView) findViewById(R.id.addshopcartgoods));
		call = new ShopcarBackCall();
		findViewById(R.id.empty).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.empty:
			Intent intent = new Intent(ShopCarActivity.this,
					RecommendActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			// 先回到首页在转到购物车页面
			RecommendGroupActiviy.group.startiHaiGoActivity(intent);
			IHaiGoMainActivity.main.setCurrentTab(IHaiGoMainActivity.REC);
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	/**
	 * @Title: initTitle
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */

	private void initTitle() {
		// 设置标题
		// ((TextView) findViewById(R.id.title_middle))
		// .setText(R.string.tab_shopcar);
		// 设置编辑事件
		findViewById(R.id.title_right).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentStatus == VIEW_STATUS) {
							// 点击编辑页面切换成编辑状态
							((TextView) findViewById(R.id.title_right))
									.setText(R.string.title_save);
							adapter.setEdit(true);
							currentStatus = EDIT_STATUS;
						} else if (currentStatus == EDIT_STATUS) {
							// 保存的时候将已删除的商品提交服务端
							if (deleteGoodsId != null
									&& deleteGoodsId.length() > 0) {
								deleteData(deleteGoodsId.toString());
							}
							// 点击保存切换成查看状态
							((TextView) findViewById(R.id.title_right))
									.setText(R.string.title_edit);
							adapter.setEdit(false);
							currentStatus = VIEW_STATUS;
						}
						adapter.notifyDataSetChanged();
					}
				});
	}

	/**
	 * 执行保存操作
	 * 
	 * @Title: saveData
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */
	private void updateData(String itemId, String amount) {
		String url = Constants.REC_CARTS + Constants.USER_ID + "/update";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("item", itemId);
		reqParams.put("amount", amount);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								if ("1".equals(resData.getString("code"))) {
									if (Constants.Debug) {
										// Toast.makeText(ShopCarActivity.this,
										// "修改成功", Toast.LENGTH_SHORT)
										// .show();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	private void deleteData(String goodsIds) {
		String url = Constants.REC_CARTS + Constants.USER_ID + "/delete";
		// url = "http://192.168.1.4:8080/cart/14/delete";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("items", goodsIds);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (result != null) {
							try {
								JSONObject data = new JSONObject(result);
								if ("1".equals(data.get("code"))) {
									Toast.makeText(
											ShopCarActivity.this,
											R.string.shopcar_goods_deletesuccess,
											Toast.LENGTH_SHORT).show();
								}
								loadData();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	/**
	 * 
	 * @ClassName: AddFavoriteListener
	 * @Description: 已确认加入购物车事件
	 * @author: ryan.wang
	 * @date: 2014年7月4日 下午2:08:41
	 * 
	 */
	public class GoodsDealListener implements
			android.content.DialogInterface.OnClickListener {
		private Object[] params;

		public void setParams(Object[] params) {
			this.params = params;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (params != null && params.length > 0) {
				addFavorite(params[0].toString());
				call.deal(R.id.movefavorite, params[0].toString());
			}
		}
	}

	private JSONArray deleteGoods(JSONArray datas, Object deleteGoodsId) {
		// 删除数据集
		JSONArray newDatas = new JSONArray();
		for (int i = 0; i < datas.length(); i++) {
			try {
				JSONObject newData = new JSONObject();
				JSONObject data = datas.getJSONObject(i);
				JSONArray newChildItems = new JSONArray();
				JSONArray childitems = data.getJSONArray("childitems");
				// 删除商品
				for (int j = 0; j < childitems.length(); j++) {
					JSONObject childItem = childitems.getJSONObject(j);
					if (deleteGoodsId instanceof String) {
						if (!deleteGoodsId.toString().equals(
								childItem.getString("id"))) {
							newChildItems.put(childItem);
						}
					} else if (deleteGoodsId instanceof List) {
						if (((ArrayList<String>) deleteGoodsId)
								.contains(childItem.getString("id"))) {
							newChildItems.put(childItem);
						}
					}
				}
				if (newChildItems.length() > 0) {
					newData.put("source", data.getInt("source"));
					newData.put("childitems", newChildItems);
					newDatas.put(newData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return newDatas;
	}

	public class ShopcarBackCall extends BackCall {

		@Override
		public void deal(int which, Object... obj) {
			ViewHolder holder = null;
			int count = 0;
			int total = 0;
			String deleteGoodId = "";
			JSONArray datas = null;
			JSONArray newDatas = null;
			switch (which) {
			case R.id.movefavorite:
				datas = adapter.getDatas();
				// 移至收藏，并从购物车删除
				// if (deleteGoodsId == null) {
				// deleteGoodsId = new StringBuffer();
				// deleteGoodsId.append(obj[0].toString());
				// } else {
				// deleteGoodsId.append("," + obj[0].toString());
				// }
				newDatas = deleteGoods(datas, obj[0].toString());
				adapter.setDatas(newDatas);
				adapter.notifyDataSetChanged();
				break;
			case R.id.deletegoods:
				// 删除商品
				// 是否可以再适配器删除的时候，通过设置数据标志位来实现
				datas = adapter.getDatas();
				if (deleteGoodsId == null) {
					deleteGoodsId = new StringBuffer();
					deleteGoodsId.append(obj[0].toString());
				} else {
					deleteGoodsId.append("," + obj[0].toString());
				}
				// 删除数据集
				newDatas = deleteGoods(datas, obj[0].toString());
				adapter.setDatas(newDatas);
				adapter.notifyDataSetChanged();
				break;
			case R.id.minusgoods:
				holder = (ViewHolder) obj[0];
				total = Integer.parseInt((String) shopcar_bottom_counts
						.getText());
				count = Integer.parseInt((String) holder.buyCount.getText()) - 1;
				int tocount = total - 1;
				if (count < 1) {
					return;
				}
				holder.buyCount.setText(count + "");
				if (holder.shopcar_selall_chk.isChecked()) {
					shopcar_bottom_counts.setText(tocount + "");
					if (tocount == 0) {
						shopcar_bottom_discountedprice.setText("￥0.00");
						shopcar_bottom_discountedprice.setTag(0d);
					} else {
						DataConfig dataConfig = new DataConfig(
								ShopCarActivity.this);
						String code = (String) holder.oldprice.getTag();
						double totalRmb = (Double) shopcar_bottom_discountedprice
								.getTag();
						totalRmb -= (Double) holder.price.getTag()
								* dataConfig.getCurRateByCode(code);
						shopcar_bottom_discountedprice.setText("￥"
								+ new BigDecimal(totalRmb).setScale(2,
										BigDecimal.ROUND_HALF_UP).doubleValue()
								+ "");
						shopcar_bottom_discountedprice.setTag(new BigDecimal(
								totalRmb).setScale(2, BigDecimal.ROUND_HALF_UP)
								.doubleValue());
					}
				}
				updateData(holder.deletegoods.getTag().toString(),
						holder.buyCount.getText().toString());
				break;
			case R.id.addgoods:
				holder = (ViewHolder) obj[0];
				count = Integer.parseInt((String) holder.buyCount.getText());
				total = Integer.parseInt((String) shopcar_bottom_counts
						.getText());
				holder.buyCount.setText((count + 1) + "");
				if (holder.shopcar_selall_chk.isChecked()) {
					shopcar_bottom_counts.setText((total + 1) + "");
					DataConfig dataConfig = new DataConfig(ShopCarActivity.this);
					String code = (String) holder.oldprice.getTag();
					double totalRmb = (Double) shopcar_bottom_discountedprice
							.getTag();
					totalRmb += (Double) holder.price.getTag()
							* dataConfig.getCurRateByCode(code);
					shopcar_bottom_discountedprice.setText("￥"
							+ new BigDecimal(totalRmb).setScale(2,
									BigDecimal.ROUND_HALF_UP).doubleValue()
							+ "");
					shopcar_bottom_discountedprice.setTag(new BigDecimal(
							totalRmb).setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue());
				}
				updateData(holder.deletegoods.getTag().toString(),
						holder.buyCount.getText().toString());
				break;
			case R.id.shopcar_selall_chk:
				boolean isChecked = (Boolean) obj[0];
				holder = (ViewHolder) obj[1];
				deleteGoodId = holder.deletegoods.getTag().toString();
				if (isChecked) {
					count = Integer
							.parseInt((String) holder.buyCount.getText());
					total = Integer.parseInt((String) shopcar_bottom_counts
							.getText());
					shopcar_bottom_counts.setText((total + count) + "");
					DataConfig dataConfig = new DataConfig(ShopCarActivity.this);
					String code = (String) holder.oldprice.getTag();
					double totalRmb = (Double) shopcar_bottom_discountedprice
							.getTag();
					totalRmb += ((Double) holder.price.getTag())
							* dataConfig.getCurRateByCode(code) * count;
					shopcar_bottom_discountedprice.setText("￥"
							+ new BigDecimal(totalRmb).setScale(2,
									BigDecimal.ROUND_HALF_UP).doubleValue()
							+ "");
					shopcar_bottom_discountedprice.setTag(new BigDecimal(
							totalRmb).setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue());
					selectGoodIds.add(deleteGoodId);
				} else {
					count = Integer
							.parseInt((String) holder.buyCount.getText());
					total = Integer.parseInt((String) shopcar_bottom_counts
							.getText());
					shopcar_bottom_counts.setText((total - count) + "");
					if ((total - count) == 0) {
						shopcar_bottom_discountedprice.setText("￥0.00");
						shopcar_bottom_discountedprice.setTag(0d);
					} else {
						DataConfig dataConfig = new DataConfig(
								ShopCarActivity.this);
						String code = (String) holder.oldprice.getTag();
						double totalRmb = (Double) shopcar_bottom_discountedprice
								.getTag();
						totalRmb -= ((Double) holder.price.getTag())
								* dataConfig.getCurRateByCode(code) * count;
						shopcar_bottom_discountedprice.setText("￥"
								+ new BigDecimal(totalRmb).setScale(2,
										BigDecimal.ROUND_HALF_UP).doubleValue()
								+ "");
						shopcar_bottom_discountedprice.setTag(new BigDecimal(
								totalRmb).setScale(2, BigDecimal.ROUND_HALF_UP)
								.doubleValue());
					}
					selectGoodIds.remove(deleteGoodId);
				}
				break;
			case R.id.ll_goods:
				deleteGoodId = obj[0].toString();
				try {
					for (int i = 0; i < adapter.getDatas().length(); i++) {
						JSONObject data = adapter.getDatas().getJSONObject(i);
						JSONArray childItems = data.getJSONArray("childitems");
						for (int j = 0; j < childItems.length(); j++) {
							JSONObject childData = childItems.getJSONObject(j);
							if (deleteGoodId.equals(childData.getString("id"))) {
								Intent intent = new Intent(
										ShopCarActivity.this,
										PersonalGoodsDetailsActivity.class);
								Bundle reqParams = new Bundle();

								String code = childData.getString("currency");
								DataConfig dcConfig = new DataConfig(
										ShopCarActivity.this);
								String symbol = dcConfig
										.getCurrencySymbolByCode(code);
								String curRate = "1"
										+ dcConfig.getCurrencyNameByCode(code)
										+ "="
										+ Utils.format(dcConfig
												.getCurRateByCode(code))
										+ getText(R.string.renminbi);
								String codeNmae = dcConfig
										.getCurrencyNameByCode(code);

								// 商品ID
								reqParams.putString("gid", deleteGoodId);
								// 商品名称
								reqParams.putString("name",
										childData.getString("name"));
								// 商品LOGO
								reqParams.putString("icon", dcConfig
										.getSourceById(childData
												.getString("icon")));
								// 价格
								reqParams.putString("pri",
										symbol + childData.getDouble("price"));
								reqParams.putString("price_disc", symbol
										+ childData.getDouble("discount"));
								reqParams
										.putString(
												"Ram",
												"￥"
														+ Utils.format(childData
																.getDouble("discount")
																* dcConfig
																		.getCurRateByCode(code)));

								// 汇率
								reqParams.putString("curRate", curRate);
								// 来源
								reqParams.putString("source", dcConfig
										.getSourceById(childData
												.getString("source")));
								reqParams.putString("codeNmae", codeNmae);
								// 币种
								reqParams.putString("symbol", symbol);
								intent.putExtras(reqParams);
								intent.putExtra(
										IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
										true);
								intent.putExtra(
										IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
										true);
								ShopCarGroupActiviy.group
										.startiHaiGoActivity(intent);
								PersonalGroupActivity.group
										.startiHaiGoActivity(intent,
												ShopCarGroupActiviy.group);
								break;
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}

}
