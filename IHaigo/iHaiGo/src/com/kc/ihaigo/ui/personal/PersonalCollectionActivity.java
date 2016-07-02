package com.kc.ihaigo.ui.personal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.CollectionAdapter;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.shipping.ShippingActivity;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * 商品收藏
 * 
 * @author zouxianbin
 * 
 */
public class PersonalCollectionActivity extends IHaiGoActivity {
	private TextView title_right;
	private int currentStatus = VIEW_STATUS;
	private final static int VIEW_STATUS = 0;
	private final static int EDIT_STATUS = 1;
	private CollectionAdapter adapter;
	private PullUpRefreshListView listview;

	private StringBuffer deleteGoodsId = null;
	private StringBuffer deleterId = null;
	private JSONArray datas;

	private Integer curpage = 1;// 记录当前是页码
	private Boolean isLoading = false;
	private Boolean isLoadAll = false;// 是否加载全部数据;
	private Integer lastReqLength = 0;// 上一次请求到的数据长度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_collection);
		initTitle();

	}

	private void initTitle() {
		listview = (PullUpRefreshListView) findViewById(R.id.coll_listview);
		listview.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isLoading) {
					curpage++;
					getCollection();
				}

			}
		});

		adapter = new CollectionAdapter(this);
		adapter.setDatas(new ArrayList<JSONObject>());
		adapter.setCall(new ShopcarBackCall());
		listview.setAdapter(adapter);

		findViewById(R.id.title_left).setOnClickListener(this);
		title_right = (TextView) findViewById(R.id.title_right);
		title_right.setOnClickListener(this);

	}

	/**
	 * 商品列表
	 */
	private void getCollection() {
		isLoading = true;
		String url = Constants.REC_COLLECTION + "?userId=" + Constants.USER_ID
				+ "&page=" + curpage + "&pagesize=" + Constants.APP_DATA_LENGTH;

		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								datas = resData.getJSONArray("collection");
								if (datas == null || datas.length() == 0) {
									title_right.setVisibility(View.INVISIBLE);
								} else {
									title_right.setVisibility(View.VISIBLE);
								}

								List<JSONObject> json = adapter.getDatas();
								boolean isNeedRefresh = false;
								if (isLoadAll) {
									for (int i = lastReqLength; i < datas
											.length(); i++) {
										json.add(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								} else {
									for (int i = 0; i < datas.length(); i++) {
										json.add(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
									adapter.notifyDataSetChanged();
								}

								if (datas.length() < Constants.APP_DATA_LENGTH) {
									isLoadAll = true;
									// 设置成前一页，允许用户重复请求当前页以便抓取服务端最新数据
									// if (curpage > 1) {
									curpage--;
									// if (Constants.Debug) {
									// Log.d(TAG, "停留当前页" + curpage + "页");
									// }
									// }
									lastReqLength = datas.length();
								} else {
									isLoadAll = false;
								}

								isLoading = false;

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0, R.string.loading);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			break;
		case R.id.title_right:
			if (datas != null && datas.length() > 0) {
				if (currentStatus == VIEW_STATUS) {
					// 点击编辑页面切换成编辑状态
					((TextView) findViewById(R.id.title_right))
							.setText(R.string.title_save);
					adapter.setEdit(true);
					currentStatus = EDIT_STATUS;
				} else if (currentStatus == EDIT_STATUS) {
					// 保存的时候将已删除的商品提交服务端

					// 点击保存切换成查看状态
					((TextView) findViewById(R.id.title_right))
							.setText(R.string.title_edit);

					currentStatus = VIEW_STATUS;
				}
				adapter.notifyDataSetChanged();
			}

			break;

		default:
			break;
		}

	}

	public class ShopcarBackCall extends BackCall {

		@Override
		public void deal(int which, Object... obj) {
			switch (which) {
			case R.id.deletegoods:
				int id = 0;
				int rid = 0;

				JSONObject jsons = (JSONObject) obj[0];
				if (jsons != null && jsons.length() > 0) {

					JSONObject remind;
					try {
						id = jsons.getInt("id");
						remind = jsons.getJSONObject("remind");
						rid = remind.getInt("rid");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				if (id > 0) {
					deleteData(String.valueOf(id), String.valueOf(rid));
				}

				List<JSONObject> list = adapter.getDatas();
				list.remove((JSONObject) obj[0]);
				adapter.setDatas(list);
				adapter.notifyDataSetChanged();

				break;
			case 1000001:
				try {
					JSONObject json = (JSONObject) obj[0];

					if (json != null && json.length() > 0) {

						/**
						 * 编号
						 */
						String bid = json.getString("id");
						/**
						 * 商品名称
						 */

						JSONObject good = json.getJSONObject("good");

						Long gid = good.getLong("id");

						/**
						 * 商品名称
						 */
						String name = good.getString("name");
						/**
						 * 商品图片
						 */
						String icon = good.getString("icon");
						/**
						 * 来源
						 */
						String source = good.getString("source");
						/**
						 * 返回的价格
						 */
						String price = good.getString("price");
						/**
						 * 打折价格
						 */
						String discount = good.getString("discount");
						/**
						 * 币种符号
						 */
						String currency = good.getString("currency");

						/**
						 * 人民币价格
						 */
						String RMB = good.getString("RMB");

						// 取预警信息
						JSONObject remind = json.getJSONObject("remind");
						/**
						 * 预警编码
						 */
						int rId = remind.getInt("id");
						String r_price = null;
						int r_discount = 0;
						int r_full = 0;
						int c_id = 0;
						String c_name = null;
						int z_id = 0;
						String z_name = null;
						if (rId > 0) {
							r_price = remind.getString("price");
							r_discount = remind.getInt("discount");
							r_full = remind.getInt("full");
							JSONArray color = remind.getJSONArray("color");
							c_name = setBy(color, "name");
							JSONArray size = remind.getJSONArray("size");
							z_name = setBy(size, "name");

						}

						DataConfig newConfig = new DataConfig(
								PersonalCollectionActivity.this);
						/**
						 * 币种符号
						 */
						String symbol = newConfig
								.getCurrencySymbolByCode(currency);
						/**
						 * 币种名称
						 */
						String codeNmae = newConfig
								.getCurrencyNameByCode(currency);
						/**
						 * 旧美元价格
						 */
						double pric = Double.valueOf(price);// 价格
						double dis = Double.valueOf(discount);// 折扣
						/**
						 * 新美元价格
						 */
						double price_di = pric * dis;
						/**
						 * 旧美元
						 */
						String pri = Utils.format(pric);
						/**
						 * 新美元价格
						 */
						String price_disc = Utils.format(price_di);

						double exchange = newConfig.getCurRateByCode(RMB);
						/**
						 * 人民币实际价格
						 */
						String Ram = Utils.format(price_di * exchange);
						/**
						 * 比例
						 */
						String curRate = "1"
								+ codeNmae
								+ "="
								+ new BigDecimal(
										newConfig.getCurRateByCode(currency))
										.setScale(2, BigDecimal.ROUND_HALF_UP)
										.doubleValue()
								+ getText(R.string.renminbi);

						Intent intent = new Intent(
								PersonalCollectionActivity.this,
								PersonalGoodsDetailsActivity.class);
						Bundle req = new Bundle();

						req.putLong("gid", gid);
						req.putInt("rid", rId);
						req.putString("name", name);
						req.putString("icon", icon);
						req.putString("pri", symbol + pri);
						req.putString("price_disc", symbol + price_disc);
						req.putString("Ram", "￥" + Ram);
						req.putString("curRate", curRate);
						req.putString("codeNmae", codeNmae);
						req.putString("source", newConfig.getSourceById(source));

						req.putInt("c_id", c_id);
						req.putString("c_name", c_name);
						req.putInt("z_id", z_id);
						req.putString("z_name", z_name);
						req.putString("r_price", r_price);
						req.putInt("r_discount", r_discount);
						req.putInt("r_full", r_full);
						intent.putExtras(req);
						intent.putExtra(
								IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
								true);
						PersonalGroupActivity.group.startiHaiGoActivity(intent);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				break;

			case R.id.warning:

				try {
					JSONObject json = (JSONObject) obj[0];
					if (json != null && json.length() > 0) {

						/**
						 * 编号
						 */
						String bid = json.getString("id");
						JSONObject good = json.getJSONObject("good");
						/**
						 * 商品名称
						 */
						Long gid = good.getLong("id");

						/**
						 * 商品名称
						 */
						String name = good.getString("name");
						/**
						 * 商品图片
						 */
						String icon = good.getString("icon");
						/**
						 * 来源
						 */
						String source = good.getString("source");
						/**
						 * 返回的价格
						 */
						String price = good.getString("price");
						/**
						 * 打折价格
						 */
						String discount = good.getString("discount");
						/**
						 * 币种符号
						 */
						String currency = good.getString("currency");

						/**
						 * 人民币价格
						 */
						String RMB = good.getString("RMB");

						// 取预警信息
						JSONObject remind = json.getJSONObject("remind");
						/**
						 * 预警编码
						 */
						int rId = remind.getInt("id");
						String r_price = null;
						int r_discount = 0;
						int r_full = 0;
						int c_id = 0;
						String c_name = null;
						int z_id = 0;
						String z_name = null;
						if (rId > 0) {
							r_price = remind.getString("price");
							r_discount = remind.getInt("discount");
							r_full = remind.getInt("full");
							JSONArray color = remind.getJSONArray("color");
							for (int i = 0; i < color.length(); i++) {
								c_id = color.getJSONObject(i).getInt("id");
								c_name = color.getJSONObject(i).getString(
										"name");
							}
							JSONArray size = remind.getJSONArray("size");
							for (int i = 0; i < size.length(); i++) {
								z_id = size.getJSONObject(i).getInt("id");
								z_name = size.getJSONObject(i)
										.getString("name");

							}
						}

						DataConfig newConfig = new DataConfig(
								PersonalCollectionActivity.this);
						/**
						 * 币种符号
						 */
						String symbol = newConfig
								.getCurrencySymbolByCode(currency);
						/**
						 * 币种名称
						 */
						String codeNmae = newConfig
								.getCurrencyNameByCode(currency);
						/**
						 * 旧美元价格
						 */
						double pric = Double.valueOf(price);// 价格
						double dis = Double.valueOf(discount);// 折扣
						/**
						 * 新美元价格
						 */
						double price_di = pric * dis;
						/**
						 * 旧美元
						 */
						String pri = Utils.format(pric);
						/**
						 * 新美元价格
						 */
						String price_disc = Utils.format(price_di);

						double exchange = newConfig.getCurRateByCode(RMB);
						/**
						 * 人民币实际价格
						 */
						String Ram = Utils.format(price_di * exchange);

						/**
						 * 比例
						 */
						String curRate = "1"
								+ codeNmae
								+ "="
								+ new BigDecimal(
										newConfig.getCurRateByCode(currency))
										.setScale(2, BigDecimal.ROUND_HALF_UP)
										.doubleValue()
								+ getText(R.string.renminbi);
						if (rId != 0) {

							Intent intent = new Intent(
									PersonalCollectionActivity.this,
									PersonalGoodsDetailsActivity.class);
							Bundle reqs = new Bundle();

							reqs.putLong("gid", gid);
							reqs.putInt("rid", rId);
							reqs.putString("name", name);
							reqs.putString("icon", icon);
							reqs.putString("pri", symbol + pri);
							reqs.putString("price_disc", symbol + price_disc);
							reqs.putString("Ram", "￥" + Ram);
							reqs.putString("curRate", curRate);
							reqs.putString("codeNmae", codeNmae);

							reqs.putString("source",
									newConfig.getSourceById(source));

							reqs.putInt("c_id", c_id);
							reqs.putString("c_name", c_name);
							reqs.putInt("z_id", z_id);
							reqs.putString("z_name", z_name);
							reqs.putString("r_price", r_price);
							reqs.putInt("r_discount", r_discount);
							reqs.putInt("r_full", r_full);
							intent.putExtras(reqs);
							intent.putExtra(
									IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
									true);
							intent.putExtra(
									IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
									false);
							PersonalGroupActivity.group
									.startiHaiGoActivity(intent);
						} else {
							Intent intent = new Intent(
									PersonalCollectionActivity.this,
									PersonalWarningCompileActivity.class);
							Bundle reqPar = new Bundle();
							reqPar.putString("TAG_GT", "LIST");
							reqPar.putLong("gid", gid);
							reqPar.putInt("rid", rId);
							reqPar.putString("name", name);
							reqPar.putString("icon", icon);
							reqPar.putString("pri", symbol + pri);
							reqPar.putString("price_disc", symbol + price_disc);
							reqPar.putString("Ram", "￥" + Ram);
							reqPar.putString("curRate", curRate);
							reqPar.putString("codeNmae", codeNmae);

							reqPar.putString("source",
									newConfig.getSourceById(source));

							reqPar.putInt("c_id", c_id);
							reqPar.putString("c_name", c_name);
							reqPar.putInt("z_id", z_id);
							reqPar.putString("z_name", z_name);
							reqPar.putString("r_price", r_price);
							reqPar.putInt("r_discount", r_discount);
							reqPar.putInt("r_full", r_full);
							intent.putExtras(reqPar);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 删除收藏商品
	 * 
	 * @param id
	 */
	private void deleteData(String id, String rid) {
		String url = Constants.REC_DELETE_COLLECTION;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("rid", rid);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject json = new JSONObject(result);
								String code = json.getString("code");
								if (!TextUtils.isEmpty(code)) {
									if ("1".equals(code)) {
										// 删除成功
									} else {
										// 删除失败
									}

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}, 0);

	}

	@Override
	public void refresh() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
		adapter.setDatas(new ArrayList<JSONObject>());
		getCollection();

	}

	/**
	 * 拼字符串的
	 * 
	 * @param json
	 * @param string
	 * @return
	 */
	public String setBy(JSONArray json, String string) {
		String a = "";

		if (json != null || json.length() > 0 || !TextUtils.isEmpty(string)) {

			for (int i = 0; i < json.length(); i++) {

				int len = json.length() - 1;
				if (i == len) {
					try {
						a = a + json.getJSONObject(i).getString(string);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					try {
						a = a + json.getJSONObject(i).getString(string) + ";";
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}
		}
		return a;
	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
			showTabHost = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}
}
