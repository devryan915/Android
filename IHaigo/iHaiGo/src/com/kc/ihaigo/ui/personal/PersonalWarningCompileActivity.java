package com.kc.ihaigo.ui.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.WarningColorAdapater;
import com.kc.ihaigo.ui.personal.adapter.WarningSizeAdapter;
import com.kc.ihaigo.util.ChartUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 编辑预警信息
 * 
 * @author zouxianbin
 * 
 */
public class PersonalWarningCompileActivity extends IHaiGoActivity implements
		OnClickListener {
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	/**
	 * 商品LOGO
	 */
	private ImageView goodsImg;
	/**
	 * 商品名称
	 */

	private TextView goodsName_tv;
	/**
	 * 旧美元
	 */

	private TextView originalPrice_tv;
	/**
	 * 实际美元
	 */

	private TextView actualPrice_tv;
	/**
	 * 人民币
	 */

	private TextView goodsActualPrice;

	/**
	 * 1美元=人民币
	 */

	private TextView ram_actualPrice_tv;
	/**
	 * 来源
	 */
	private ImageView merchants;
	/**
	 * listview
	 */
	private ListView listView;
	private JSONArray stocks;
	private JSONArray items;
	/**
	 * 颜色
	 */
	private WarningColorAdapater adapter;
	/**
	 * 大小
	 */
	private WarningSizeAdapter sizeadapter;
	private String color;
	/*
	 * 详情颜色ID
	 */
	private String colorId;
	/**
	 * 详情颜色Nmae
	 */
	private String colorName;
	/**
	 * 大小Id
	 */
	private String sizeId;
	/**
	 * 大小名称
	 */
	private String sizeName;

	private TextView colorText;
	private TextView sizeText;
	private EditText edit_price;
	private CheckBox checkBox_fullInt;
	private CheckBox checkBox_discountInt;
	private int chec_fullInt;
	private int chec_discountInt;
	private LinearLayout ll_price_graphic;
	/**
	 * 删除BUTTO
	 */
	private TextView button;
	/**
	 * 从不同的模块中转标识
	 */
	private String TAG;
	/**
	 * 从列表还是详情标识
	 */

	private String TAG_GT = "GT";
	/**
	 * 从商品详情过来的添加
	 */

	private String ADD;
	/**
	 * 删除BUTTON布局
	 */
	private LinearLayout detele_button;
	/**
	 * 市场均价
	 */
	private TextView averageprice;
	/**
	 * 最高价
	 */
	private TextView highestprice;
	/**
	 * 最低价
	 */
	private TextView hislowestprice;

	/**
	 * 标题
	 */
	private TextView title_middle;
	/**
	 * 添加 100000000.修改标记位 100000001,
	 */
	private int TAG_IN;
	private final static int TAG_ADD = 100000000;
	private final static int TAG_AMEND = 100000001;

	/**
	 * 返回的价格
	 */
	private String priceStr;

	/**
	 * 返回来的颜色名称
	 */
	private String colorStr;
	/**
	 * 返回的大小
	 */
	private String sizeStr;

	private String name;
	private String gid;
	private String curRate;
	private String Ram;
	private String price_disc;
	private String pri;
	private String source;
	private String icon;
	private String symbol;
	private String codeNmae;

	private int rid;
	private int c_id;
	private String c_name;
	private int z_id;
	private String z_name;
	private String r_price;
	private int r_discount;
	private int r_full;
	private Class<IHaiGoActivity> lparentClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_warning_compile);
		initComponets();
		setImage();

	}

	/**
	 * 显示网络图片
	 */
	private void setImage() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	private void initComponets() {
		title_middle = (TextView) findViewById(R.id.title_middle);

		goodsImg = (ImageView) findViewById(R.id.goodsImg);
		merchants = (ImageView) findViewById(R.id.merchants);
		goodsName_tv = (TextView) findViewById(R.id.goodsName_tv);
		originalPrice_tv = (TextView) findViewById(R.id.originalPrice_tv);
		actualPrice_tv = (TextView) findViewById(R.id.actualPrice_tv);
		goodsActualPrice = (TextView) findViewById(R.id.goodsActualPrice);
		ram_actualPrice_tv = (TextView) findViewById(R.id.ram_actualPrice_tv);
		findViewById(R.id.sizeLayout).setOnClickListener(this);
		findViewById(R.id.colorLayout).setOnClickListener(this);
		colorText = (TextView) findViewById(R.id.colorText);
		sizeText = (TextView) findViewById(R.id.sizeText);
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		edit_price = (EditText) findViewById(R.id.edit_price);
		checkBox_discountInt = (CheckBox) findViewById(R.id.checkBox_discountInt);
		checkBox_discountInt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (checkBox_discountInt.isChecked()) {
					chec_discountInt = 0;
				} else {
					chec_discountInt = 1;
				}
				return false;
			}
		});

		checkBox_fullInt = (CheckBox) findViewById(R.id.checkBox_fullInt);
		checkBox_fullInt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (checkBox_fullInt.isChecked()) {
					chec_fullInt = 0;
				} else {
					chec_fullInt = 1;

				}
				return false;
			}
		});
		detele_button = (LinearLayout) findViewById(R.id.detele_button);
		button = (TextView) findViewById(R.id.button);
		button.setOnClickListener(this);

		averageprice = (TextView) findViewById(R.id.averageprice);
		highestprice = (TextView) findViewById(R.id.highestprice);
		hislowestprice = (TextView) findViewById(R.id.hislowestprice);

		// 坐标图
		ll_price_graphic = (LinearLayout) findViewById(R.id.ll_price_graphic);

	}

	/**
	 * 设置商品预警(商品历史价格)
	 */

	private void findRemindPrice(final String gid) {

		String url = Constants.REC_GOODS_HISTORICAL + gid
				+ "/getGoodHistorical" + "?uid=" + Constants.USER_ID + "&page="
				+ Constants.REC_GOODS_HISTORICAL_PASE + "&pagesize="
				+ Constants.APP_DATA_LENGTH;

		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);

								// 坐标
								// 最高最低平均价
								JSONArray goodpice = data
										.getJSONArray("goodpice");
								for (int i = 0; i < goodpice.length(); i++) {
									hislowestprice.setText(goodpice
											.getJSONObject(i).getString("min"));
									highestprice.setText(goodpice
											.getJSONObject(i).getString("max"));
									averageprice.setText(goodpice
											.getJSONObject(i).getString("avg"));

								}

								// 历史价格表
								JSONArray goodlist = data
										.getJSONArray("goodlist");
								if (goodlist != null && goodlist.length() != 0) {

									List<Double> list_price = new ArrayList<Double>();
									List<String> list_itme = new ArrayList<String>();

									for (int i = 0; i < goodlist.length(); i++) {

										list_price.add(goodlist
												.getJSONObject(i).getDouble(
														"price"));

										// 时间
										Long timeString = goodlist
												.getJSONObject(i).getLong(
														"createTime");
										// 时间转换
										String tim = Utils.getCurrentTime(
												timeString, "MM.dd");
										list_itme.add(tim);

									}
									ll_price_graphic.addView(ChartUtil
											.drawHistoryPrice(
													PersonalWarningCompileActivity.this,
													list_price, list_itme));
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});

	}

	/**
	 * 颜色库存
	 * 
	 * @param id
	 */
	private void getStocks(String gid) {
		String url = Constants.REC_GOODS_STOCKS + 1 + "/stocks";
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								// dialog
								stocks = data.getJSONArray("stocks");

								adapter = new WarningColorAdapater(
										PersonalWarningCompileActivity.this,
										stocks);

								for (int i = 0; i < stocks.length(); i++) {
									items = stocks.getJSONObject(i)
											.getJSONArray("items");
									sizeadapter = new WarningSizeAdapter(
											PersonalWarningCompileActivity.this,
											items);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});

	}

	/**
	 * 添加完成
	 */
	private void setInfo(String gid, String price, String color, String size,
			int discount, int full) {
		String url = Constants.REC_GOODS_FINDREMINDT + gid + "/add";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", Constants.USER_ID);
		map.put("price", price);
		map.put("color", color);
		map.put("size", size);
		map.put("discount", discount);
		map.put("full", full);
		Log.e("11111", map.toString());
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {

							try {

								JSONObject data = new JSONObject(result);
								String code = data.getString("code");
								if ("0".equals(code)) {// 失败
									Toast.makeText(
											PersonalWarningCompileActivity.this,
											"添加失败", Toast.LENGTH_LONG).show();

								} else if ("1".equals(code)) {// 成功

									back();

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	/**
	 * 修改商品预警信息
	 */
	private void findInfo(int rid, String price, String color, String size,
			int discount, int full) {
		String url = Constants.REC_GOODS_FINDREMINDT + rid + "/updateRemind";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("price", price);
		map.put("color", color);
		map.put("size", size);
		map.put("discount", discount);
		map.put("full", full);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								String code = data.getString("code");
								if ("0".equals(code)) {// 失败
									Toast.makeText(
											PersonalWarningCompileActivity.this,
											"修改失败", Toast.LENGTH_LONG).show();

								} else if ("1".equals(code)) {// 成功
									Intent intent = new Intent(
											PersonalWarningCompileActivity.this,
											PersonalCollectionActivity.class);
									intent.putExtra("TAG", TAG);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	/**
	 * 删除商品预警信息
	 */
	private void findDelete(int rid) {
		String url = Constants.REC_GOODS_FINDREMINDT + rid;
		HttpAsyncTask.fetchData(HttpAsyncTask.DELETE, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								String code = data.getString("code");
								if ("0".equals(code)) {
									Toast.makeText(
											PersonalWarningCompileActivity.this,
											"删除失败", Toast.LENGTH_LONG).show();

								} else if ("1".equals(code)) {
									back();

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});

	}

	public String setBy(JSONArray json, String string) {
		String a = "";
		for (int i = 0; i < json.length(); i++) {

			int len = json.length() - 1;
			if (i == len) {
				try {
					a = a + json.getJSONObject(i).getString(string);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					a = a + json.getJSONObject(i).getString(string) + ";";
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return a;
	}

	/**
	 * 添加
	 */
	private void add() {
		String price = edit_price.getText().toString().trim();
		String coloString = colorText.getText().toString().trim();
		String sizeString = sizeText.getText().toString().trim();
		if (TextUtils.isEmpty(price)) {
			Toast.makeText(PersonalWarningCompileActivity.this, "请输入价格",
					Toast.LENGTH_LONG).show();
		} else if (TextUtils.isEmpty(coloString)) {
			Toast.makeText(PersonalWarningCompileActivity.this, "请选择颜色",
					Toast.LENGTH_LONG).show();

		} else if (TextUtils.isEmpty(sizeString)) {
			Toast.makeText(PersonalWarningCompileActivity.this, "请选择尺码",
					Toast.LENGTH_LONG).show();
		} else if (!TextUtils.isEmpty(gid) && !gid.equals("-1")) {
			setInfo(gid, price, String.valueOf(colorText.getTag()),
					String.valueOf(sizeText.getTag()), chec_discountInt,
					chec_fullInt);
		}
	}

	/**
	 * 修改
	 */
	private void set() {

		String price = edit_price.getText().toString().trim();
		String coloString = colorText.getText().toString().trim();
		String sizeString = sizeText.getText().toString().trim();
		if (rid > 0) {

			if (!price.equals(priceStr) || (!coloString.equals(colorStr))
					|| (!sizeString.equals(sizeStr))
					|| (r_full != chec_fullInt)
					|| (r_discount != chec_discountInt)) {

				if (!TextUtils.isEmpty(gid) && !gid.equals("-1")) {
					findInfo(rid, price, String.valueOf(colorText.getTag()),
							String.valueOf(sizeText.getTag()), chec_fullInt,
							chec_discountInt);
				}

			} else {
				Toast.makeText(PersonalWarningCompileActivity.this, "请填写修改内容",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.title_left:
			break;
		case R.id.button:// 删除
			if (rid > 0) {
				findDelete(rid);
			}

			break;
		case R.id.title_right:// 完成提交
			if (TAG_GT.equals("LIST")) {// 列表过来的
				add();
			} else if (TAG_GT.equals("NO")) {// 滑添加过来的
				add();
			} else if (TAG_GT.equals("YES"))
				set();
			break;

		case R.id.sizeLayout:// 选择大小
			DialogUtil.showRulesDialog(
					PersonalWarningCompileActivity.this.getParent(),
					new BackCall() {
						@Override
						public void deal(int which, Object... obj) {
							((Dialog) obj[0]).dismiss();
							try {
								switch (which) {
								case R.id.complete:
									List<JSONObject> selDatas = (List<JSONObject>) obj[1];
									StringBuffer ids = new StringBuffer();
									StringBuffer names = new StringBuffer();
									for (int i = 0; i < selDatas.size(); i++) {
										JSONObject data = selDatas.get(i);
										ids.append(data.getString("id") + ";");
										names.append(data.getString("size")
												+ ";");
									}
									sizeText.setText(names.toString()
											.subSequence(0, names.length() - 1));
									sizeText.setTag(ids.toString().subSequence(
											0, ids.length() - 1));
									break;
								default:
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, null, sizeadapter);

			break;

		case R.id.colorLayout:// 选择颜色
			DialogUtil.showRulesDialog(
					PersonalWarningCompileActivity.this.getParent(),
					new BackCall() {
						@Override
						public void deal(int which, Object... obj) {
							((Dialog) obj[0]).dismiss();
							try {
								switch (which) {
								case R.id.complete:
									List<JSONObject> selDatas = (List<JSONObject>) obj[1];
									StringBuffer ids = new StringBuffer();
									StringBuffer names = new StringBuffer();
									for (int i = 0; i < selDatas.size(); i++) {
										JSONObject data = selDatas.get(i);
										ids.append(data.getString("id") + ";");
										names.append(data.getString("color")
												+ ";");
									}
									colorText.setText(names.toString()
											.subSequence(0, names.length() - 1));

									colorText.setTag(ids.toString()
											.subSequence(0, ids.length() - 1));
									break;
								default:
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, null, adapter);

			break;

		default:
			break;
		}

	}

	@Override
	public void refresh() {
		super.refresh();
		Bundle resParams = getIntent().getExtras();
		if (PersonalCollectionActivity.class.equals(parentClass)) {
			lparentClass = parentClass;

			TAG_GT = resParams.getString("TAG_GT");
			// 来源
			source = resParams.getString("source");
			Ram = resParams.getString("Ram");

			// 商品Id
			gid = String.valueOf(resParams.getLong("gid"));
			// 商品名称
			name = resParams.getString("name");
			// 旧美元
			pri = resParams.getString("pri");
			// 新美元
			price_disc = resParams.getString("price_disc");
			// 汇率
			curRate = resParams.getString("curRate");
			// 商品LOGO
			icon = resParams.getString("icon");
			/**
			 * 货币名称
			 */
			codeNmae = resParams.getString("codeNmae");
			// 预警
			rid = resParams.getInt("rid");
			c_id = resParams.getInt("c_id");
			z_id = resParams.getInt("z_id");
			c_name = resParams.getString("c_name");
			z_name = resParams.getString("z_name");
			r_price = resParams.getString("r_price");
			r_discount = resParams.getInt("r_discount");
			r_full = resParams.getInt("r_full");

		} else if (PersonalGoodsDetailsActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			TAG_GT = resParams.getString("TAG_GT");
			// 来源
			source = resParams.getString("source");
			Ram = resParams.getString("Ram");

			// 商品Id
			gid = resParams.getString("gid");

			// 商品名称
			name = resParams.getString("name");
			// 旧美元
			pri = resParams.getString("pri");
			// 新美元
			price_disc = resParams.getString("price_disc");
			// 汇率
			curRate = resParams.getString("curRate");
			// 商品LOGO
			icon = resParams.getString("icon");
			/**
			 * 货币名称
			 */
			codeNmae = resParams.getString("codeNmae");
			// 预警
			rid = resParams.getInt("rid");
			c_id = resParams.getInt("c_id");
			z_id = resParams.getInt("z_id");
			c_name = resParams.getString("c_name");
			z_name = resParams.getString("z_name");
			r_price = resParams.getString("r_price");
			r_discount = resParams.getInt("r_discount");
			r_full = resParams.getInt("r_full");

		}

		/**
		 * 旧价格
		 */

		originalPrice_tv.setText(pri);
		/**
		 * 新价格
		 */
		actualPrice_tv.setText(price_disc);
		/**
		 * 人民币价格
		 */

		goodsActualPrice.setText(Ram);

		/**
		 * 商品名称
		 */
		goodsName_tv.setText(name);
		/**
		 * 比例
		 */
		ram_actualPrice_tv.setText(curRate);

		// 商品LOGO
		imageLoader.displayImage(icon, goodsImg, options, animateFirstListener);
		// 来源
		imageLoader.displayImage(source, merchants, options,
				animateFirstListener);
		edit_price.setText(r_price);
		colorText.setText(c_name);
		sizeText.setText(z_name);

		if (r_discount == 1) {
			checkBox_discountInt.setChecked(true);
		} else {
			checkBox_discountInt.setChecked(false);
		}
		if (r_full == 1) {
			checkBox_fullInt.setChecked(true);
		} else {
			checkBox_fullInt.setChecked(false);
		}
		chec_fullInt = r_full;
		chec_discountInt = r_discount;

		// 取颜色存在
		if (!TextUtils.isEmpty(gid) && !gid.equals("-1")) {
			getStocks(gid);
			findRemindPrice(gid);
		}

		/**
		 * 从列表是否增加预警和详情时预警标识
		 */
		if (TAG_GT.equals("LIST")) {// 列表
			detele_button.setVisibility(View.GONE);

		} else if (TAG_GT.equals("NO")) {
			detele_button.setVisibility(View.GONE);

		} else if (TAG_GT.equals("YES")) {
			detele_button.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void back() {
		lparentClass = parentClass;
		if (PersonalGoodsDetailsActivity.class.equals(lparentClass)) {
			showTabHost = false;
		} else {
			showTabHost = true;
		}

		super.back();
	}

}
