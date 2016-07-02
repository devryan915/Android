package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.RecommendActivity;
import com.kc.ihaigo.ui.recommend.RecommendGroupActiviy;
import com.kc.ihaigo.ui.recommend.SearchResultActivity;
import com.kc.ihaigo.ui.recommend.SortSearchResultActivity;
import com.kc.ihaigo.ui.recommend.adapter.GoodsDetailCommetsAdapter;
import com.kc.ihaigo.ui.selfwidget.HeadImagesView;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.ui.shopcar.ShopCarActivity;
import com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.share.QQShare;
import com.kc.ihaigo.util.share.SinaWeibo;
import com.kc.ihaigo.util.share.WeiXinShare;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 商品信息
 * 
 * @author zouxianbin
 * 
 */
public class PersonalGoodsDetailsActivity extends IHaiGoActivity {
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	/**
	 * 商品提醒
	 */
	private LinearLayout warning_lay;
	/**
	 * 商品来源
	 */
	private ImageView rec_supply_img;

	/**
	 * 旧美元
	 */
	private TextView goods_originalPrice;
	/**
	 * 实际美元
	 */
	private TextView goods_actualPrice;
	/**
	 * 实际人民币
	 */
	private TextView goodsActualPrice;
	/**
	 * 规格参数
	 */
	private TextView standardIntroduce;
	/**
	 * 商品描述
	 */
	private TextView goodsIntroduce;
	/**
	 * 评论列表
	 */
	private WrapListView userAppraise;
	/**
	 * 广告
	 */
	private HeadImagesView goodsdetail_images;
	/**
	 * 评论条数
	 */
	private TextView more_appraise;
	/**
	 * 汇率
	 */
	private TextView rateval;
	/**
	 * 预警
	 */
	private LinearLayout goods_warningBut;
	/**
	 * 入手价格
	 */
	private TextView warning_rice;
	/**
	 * 颜色
	 */
	private TextView warning_color;
	/**
	 * 大小
	 */
	private TextView warning_size;
	/**
	 * 折扣提醒
	 */
	private TextView warning_discount;
	/**
	 * 收藏数据
	 */
	private TextView collection_count;
	private ImageView collection;

	private String name;
	private String gid;
	private String curRate;
	private String Ram;
	private String price_disc;
	private String pri;
	private String source;
	private String icon;
	private String symbol;
	private int rid;
	private String codeNmae;
	private String inventory;

	private boolean flae;
	/**
	 * 收藏数量
	 */
	private int count;

	/**
	 * 标记是否已添加的
	 */
	// private String TAG_GT;

	private int c_id;
	private String c_name;
	private int z_id;
	private String z_name;
	private String r_price;
	private int r_discount;
	private int r_full;
	private GoodsDetailCommetsAdapter gdca;
	private Class<IHaiGoActivity> lparentClass;

	private LinearLayout lookEvalutation;
	private IHaiGoGroupActivity lparentGroupActivity;
	private LinearLayout user_appraise;
	private String link;// 直接购买的地址
	private String inTAG;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_goods_details);
		initComponets();
		setImage();

		if (savedInstanceState != null) {

			// 得到保存的数据
			name = savedInstanceState.getString("name");
			gid = savedInstanceState.getString("name");
			curRate = savedInstanceState.getString("curRate");
			Ram = savedInstanceState.getString("Ram");
			price_disc = savedInstanceState.getString("pri");
			pri = savedInstanceState.getString("name");
			source = savedInstanceState.getString("source");
			icon = savedInstanceState.getString("icon");
			symbol = savedInstanceState.getString("symbol");
			codeNmae = savedInstanceState.getString("codeNmae");
			rid = savedInstanceState.getInt("rid");
			c_id = savedInstanceState.getInt("c_id");
			z_id = savedInstanceState.getInt("z_id");

			z_id = savedInstanceState.getInt("z_id");
			r_full = savedInstanceState.getInt("r_full");
			c_name = savedInstanceState.getString("c_name");
			z_name = savedInstanceState.getString("z_name");
			r_price = savedInstanceState.getString("r_price");
			inTAG = savedInstanceState.getString("inTAG");

			// 恢复数据
			// 恢复内容
			// 恢复内容
			rateval.setText(curRate);
			if (!TextUtils.isEmpty(source)) {
				imageLoader.displayImage(source, rec_supply_img, options,
						animateFirstListener);
			}
			goods_originalPrice.setText(pri);
			goods_actualPrice.setText(price_disc);

			goods_originalPrice.setText(pri);

			goods_actualPrice.setText(price_disc);
			goodsActualPrice.setText(Ram);
			goodsdetail_images.setGoodsName(name);
			if (rid > 0) {
				warning_lay.setVisibility(View.VISIBLE);
				warning_rice.setText(r_price + codeNmae);
				warning_color.setText(c_name);
				warning_size.setText(z_name);
				if (r_discount == 0) {
					warning_discount.setText("关");
				} else {
					warning_discount.setText("开");
				}
			} else {
				warning_lay.setVisibility(View.GONE);
			}

			if (!TextUtils.isEmpty(gid) && !gid.equals("-1")) {
				if (checkLogin()) {
					getCount(gid);
				}
				getDatas(gid);

			}
		}
		dialog = DialogUtil.showfavorite(
				PersonalGoodsDetailsActivity.this.getParent(), new BackCall() {

					@Override
					public void deal(int whichButton, Object... obj) {
						switch (whichButton) {
						case R.id.choose_ok:// 确定
							((Dialog) obj[0]).dismiss();
							break;
						case R.id.warning:// 设置预警
							Intent intent = new Intent(
									PersonalGoodsDetailsActivity.this,
									PersonalWarningCompileActivity.class);
							Bundle req = new Bundle();
							req.putString("TAG_GT", "NO");// 都没有添加的
							req.putString("name", name);
							req.putString("gid", gid);
							req.putInt("rid", rid);
							req.putString("curRate", curRate);
							req.putString("Ram", Ram);
							req.putString("price_disc", price_disc);
							req.putString("pri", pri);
							req.putString("source", source);
							intent.putExtras(req);
							intent.putExtra(
									IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
									true);
							intent.putExtra(
									IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
									false);

							if (RecommendActivity.class.equals(parentClass)
									|| SortSearchResultActivity.class
											.equals(parentClass)
									|| SearchResultActivity.class
											.equals(parentClass)) {
								lparentGroupActivity
										.startiHaiGoActivity(intent);

							} else if (PersonalCollectionActivity.class
									.equals(parentClass)) {
								lparentGroupActivity
										.startiHaiGoActivity(intent);

							} else if (ShopCarActivity.class
									.equals(parentClass)) {
								lparentGroupActivity
										.startiHaiGoActivity(intent);

							}

							((Dialog) obj[0]).dismiss();

							break;
						default:
							break;
						}

					}
				}, null);

	}

	private void initComponets() {
		warning_lay = (LinearLayout) findViewById(R.id.warning_lay);
		rec_supply_img = (ImageView) findViewById(R.id.rec_supply_img);
		standardIntroduce = (TextView) findViewById(R.id.standardIntroduce);
		goodsIntroduce = (TextView) findViewById(R.id.goodsIntroduce);
		userAppraise = (WrapListView) findViewById(R.id.userAppraise);
		goodsdetail_images = (HeadImagesView) findViewById(R.id.goodsdetail_images);
		more_appraise = (TextView) findViewById(R.id.more_appraise);
		goods_originalPrice = (TextView) findViewById(R.id.goods_originalPrice);
		goods_actualPrice = (TextView) findViewById(R.id.goods_actualPrice);
		goodsActualPrice = (TextView) findViewById(R.id.goodsActualPrice);
		rateval = (TextView) findViewById(R.id.rateval);
		lookEvalutation = (LinearLayout) findViewById(R.id.lookEvalutation);
		lookEvalutation.setOnClickListener(this);
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.join_shoppingBut).setOnClickListener(this);
		findViewById(R.id.buyBut).setOnClickListener(this);
		collection = (ImageView) findViewById(R.id.collection);
		collection.setOnClickListener(this);
		collection_count = (TextView) findViewById(R.id.collection_count);
		findViewById(R.id.title_right).setOnClickListener(this);
		goods_warningBut = (LinearLayout) findViewById(R.id.goods_warningBut);
		goods_warningBut.setOnClickListener(this);

		warning_rice = (TextView) findViewById(R.id.warning_rice);
		warning_color = (TextView) findViewById(R.id.warning_color);
		warning_size = (TextView) findViewById(R.id.warning_size);
		warning_discount = (TextView) findViewById(R.id.warning_discount);

		gdca = new GoodsDetailCommetsAdapter(PersonalGoodsDetailsActivity.this);
		userAppraise.setAdapter(gdca);

		user_appraise = (LinearLayout) findViewById(R.id.user_appraise);
		user_appraise.setOnClickListener(this);
	}

	private void setImage() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
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
		return a;
	}

	/**
	 * 收藏商品数量，以及用户是否收藏
	 */

	private void getCount(String gid) {
		String url = Constants.GET_COUNT + "?userId=" + Constants.USER_ID
				+ "&goodId=" + gid;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								count = data.getInt("count");
								collection_count.setText(String.valueOf(count)
										+ getString(R.string.count_add));
								int code = data.getInt("code");
								if (code == 0) {
									flae = false;

									collection
											.setBackgroundResource(R.drawable.collection_no);

								} else {
									flae = true;
									collection
											.setBackgroundResource(R.drawable.collection);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	/**
	 * 取消收藏
	 */

	private void cancelCollection() {
		String url = Constants.GET_COLLECTION;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("goodId", gid);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								int code = data.getInt("code");
								if (code == 1) {// 成功
									int coun = count - 1;
									if (coun < 0) {
										collection_count.setText("0");
									} else {
										String countStr = String
												.valueOf(count - 1);
										collection_count
												.setText(countStr
														+ getString(R.string.count_add));
									}

									collection
											.setBackgroundResource(R.drawable.collection_no);
								} else if (code == 0) {
									String countStr = String.valueOf(count);
									collection_count.setText(countStr
											+ getString(R.string.count_add));
									collection
											.setBackgroundResource(R.drawable.collection);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							// Toast.makeText(PersonalGoodsDetailsActivity.this,
							// result, Toast.LENGTH_LONG).show();
						}
					}
				}, 0);
	}

	/**
	 * 添加收藏
	 */

	private void insertCollection() {
		String url = Constants.GET_IN_COLLECTION;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("goodId", gid);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								int code = data.getInt("code");
								if (code == 1) {// 成功
									dialog.show();
									collection
											.setBackgroundResource(R.drawable.collection);
									int counts = count;
									String countStr = String.valueOf(counts);
									collection_count.setText(countStr
											+ getString(R.string.count_add));

								} else if (code == 0) {
									collection
											.setBackgroundResource(R.drawable.collection_no);
									String countStr = String.valueOf(count);
									collection_count.setText(countStr
											+ getString(R.string.count_add));
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							// Toast.makeText(PersonalGoodsDetailsActivity.this,
							// result, Toast.LENGTH_LONG).show();
						}
					}
				}, 0);
	}

	/**
	 * 商品详情--评论 规格 ,
	 * 
	 * @param goodId
	 */
	private void getDatas(String gId) {
		String url = Constants.REC_GOODS_DATAS + gId;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);

								// 直接购买地址
								link = data.getString("link");
								// 取到图片
								JSONArray images = data.getJSONArray("images");
								goodsdetail_images.setAvdImages(images);
								// 取商品描述
								goodsIntroduce.setText(data.getString("desc"));
								// 规格
								standardIntroduce.setText(data
										.getString("spec"));

								int count = data.getInt("count");
								if (count > 3) {// 只有评论超过3条时才会出现。
									lookEvalutation.setVisibility(View.VISIBLE);
									// 初始化评论
									more_appraise.setText(data
											.getString("count"));// 条数
								}

								// 评论列表
								JSONArray comments = data
										.getJSONArray("comments");
								gdca.setDatas(comments);
								gdca.notifyDataSetChanged();

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				},0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_appraise:// 评论

			Intent user = new Intent(PersonalGoodsDetailsActivity.this,
					PersonalPublishEvaluationActivity.class);
			Bundle bun = new Bundle();
			bun.putString("gid", gid);
			bun.putInt("rid", rid);
			bun.putString("name", name);
			bun.putString("icon", icon);
			bun.putString("pri", pri);
			bun.putString("price_disc", price_disc);
			bun.putString("Ram", Ram);
			bun.putString("curRate", curRate);
			bun.putString("codeNmae", codeNmae);
			bun.putString("source", source);
			user.putExtras(bun);
			user.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			user.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			lparentGroupActivity.startiHaiGoActivity(user);

			// if (RecommendActivity.class.equals(parentClass)
			// || SortSearchResultActivity.class.equals(parentClass)
			// || SearchResultActivity.class.equals(parentClass)) {
			// lparentGroupActivity.startiHaiGoActivity(user);
			//
			// } else if (PersonalCollectionActivity.class.equals(parentClass))
			// {
			// lparentGroupActivity.startiHaiGoActivity(user);
			//
			// } else if (ShopCarActivity.class.equals(parentClass)) {
			// lparentGroupActivity.startiHaiGoActivity(user);
			//
			// }

			break;

		case R.id.lookEvalutation:// 查看全部评论
			Intent intent = new Intent(PersonalGoodsDetailsActivity.this,
					PersonalLookEvaluationActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("gid", gid);
			bundle.putInt("rid", rid);
			bundle.putString("name", name);
			bundle.putString("icon", icon);
			bundle.putString("pri", pri);
			bundle.putString("price_disc", price_disc);
			bundle.putString("Ram", Ram);
			bundle.putString("curRate", curRate);
			bundle.putString("codeNmae", codeNmae);
			bundle.putString("source", source);
			intent.putExtras(bundle);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			lparentGroupActivity.startiHaiGoActivity(intent);
			// if (RecommendActivity.class.equals(parentClass)
			// || SortSearchResultActivity.class.equals(parentClass)
			// || SearchResultActivity.class.equals(parentClass)) {
			// RecommendGroupActiviy.group.startiHaiGoActivity(intent);
			//
			// } else if (PersonalCollectionActivity.class.equals(parentClass))
			// {
			// PersonalGroupActivity.group.startiHaiGoActivity(intent);
			//
			// } else if (ShopCarActivity.class.equals(parentClass)) {
			// ShopCarGroupActiviy.group.startiHaiGoActivity(intent);
			//
			// }

			break;

		case R.id.goods_warningBut:// 已添加编辑预警
			Intent tent = new Intent(PersonalGoodsDetailsActivity.this,
					PersonalWarningCompileActivity.class);
			Bundle reqtent = new Bundle();
			reqtent.putString("gid", gid);
			reqtent.putInt("rid", rid);
			reqtent.putString("name", name);
			reqtent.putString("icon", icon);
			reqtent.putString("pri", pri);
			reqtent.putString("price_disc", price_disc);
			reqtent.putString("Ram", Ram);
			reqtent.putString("curRate", curRate);
			reqtent.putString("codeNmae", codeNmae);
			reqtent.putString("source", source);
			reqtent.putInt("c_id", c_id);
			reqtent.putString("c_name", c_name);
			reqtent.putInt("z_id", z_id);
			reqtent.putString("z_name", z_name);
			reqtent.putString("r_price", r_price);
			reqtent.putInt("r_discount", r_discount);
			reqtent.putInt("r_full", r_full);
			tent.putExtras(reqtent);
			tent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			tent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			lparentGroupActivity.startiHaiGoActivity(tent);
			// if (RecommendActivity.class.equals(parentClass)
			// || SortSearchResultActivity.class.equals(parentClass)
			// || SearchResultActivity.class.equals(parentClass)) {
			// RecommendGroupActiviy.group.startiHaiGoActivity(tent);
			//
			// } else if (PersonalCollectionActivity.class.equals(parentClass))
			// {
			// PersonalGroupActivity.group.startiHaiGoActivity(tent);
			//
			// } else if (ShopCarActivity.class.equals(parentClass)) {
			// ShopCarGroupActiviy.group.startiHaiGoActivity(tent);
			//
			// }

			break;
		case R.id.title_left:
			break;
		case R.id.title_right:
			DialogUtil.showTopicShareDialog(parentGroupActivity,
					new BackCall() {
						@Override
						public void deal(int which, Object... obj) {
							switch (which) {
							case R.id.iv_share_weixin:
								ToastUtil.showShort(currentActivity, "微信分享");
								WeiXinShare share = new WeiXinShare();
								share.shareToWeiXin(parentGroupActivity);
								break;
							case R.id.iv_share_circle_of_friends:

								break;
							case R.id.iv_share_qq:
								ToastUtil.showShort(currentActivity, "QQ分享");
								QQShare qqshare = new QQShare();
								qqshare.shareToQQ(parentGroupActivity);
								break;
							case R.id.iv_share_sina:
								SinaWeibo weibo = new SinaWeibo();
								weibo.shareToSina(parentGroupActivity);
								break;
							default:
								break;
							}
						}
					}, null);
			break;
		case R.id.join_shoppingBut:// 加入购物车

			if (checkLogin()) {

				Intent join_shoppingBut = new Intent(
						PersonalGoodsDetailsActivity.this,
						PersonalJoinShopCartActivity.class);
				Bundle join = new Bundle();

				join.putString("gid", gid);
				join.putString("price", pri);
				join.putString("discount", price_disc);
				join.putString("rmbprice", Ram);
				join.putString("curRate", curRate);
				join.putString("sourceurl", source);
				join.putString("goodsurl", icon);
				join.putString("goodsname", name);
				join_shoppingBut.putExtras(join);
				join_shoppingBut.putExtra(
						IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				join_shoppingBut.putExtra(
						IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				lparentGroupActivity.startiHaiGoActivity(join_shoppingBut);

			} else {

				Intent nt = new Intent(PersonalGoodsDetailsActivity.this,
						PersonalLoginActivity.class);
				nt.putExtra("flag", "PersonalGoodsDetailsActivity");
				nt.putExtra("code", "");
				nt.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				nt.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				lparentGroupActivity.startiHaiGoActivity(nt);

			}

			break;
		case R.id.buyBut:// 立即购买
			// if (TextUtils.isEmpty(link)) {
			Intent intn = new Intent(PersonalGoodsDetailsActivity.this,
					WebViewActivity.class);
			Bundle bule = new Bundle();
			bule.putString("link", link);
			intn.putExtras(bule);
			intn.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			intn.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			lparentGroupActivity.startiHaiGoActivity(intn);

			break;
		case R.id.collection:// 收藏

			if (checkLogin()) {
				if (flae == false) {
					insertCollection();

					// collection.setBackgroundResource(R.drawable.collection);
					flae = true;
				} else if (flae == true) {
					cancelCollection();
					// collection.setBackgroundResource(R.drawable.collection_no);
					flae = false;
				}

			} else {

				Intent nt = new Intent(PersonalGoodsDetailsActivity.this,
						PersonalLoginActivity.class);
				nt.putExtra("flag", "PersonalGoodsDetailsActivity");
				nt.putExtra("code", "");
				nt.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				nt.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				lparentGroupActivity.startiHaiGoActivity(nt);

			}

			break;
		default:
			break;
		}

	}

	/**
	 * 在该方法中保存状态数据
	 */
	@Override
	protected void onSaveInstanceState(Bundle resParams) {
		resParams = new Bundle();
		resParams.putString("gid", gid);
		resParams.putInt("rid", rid);
		resParams.putString("name", name);
		resParams.putString("icon", icon);
		resParams.putString("pri", pri);
		resParams.putString("price_disc", price_disc);
		resParams.putString("Ram", Ram);
		resParams.putString("curRate", curRate);
		resParams.putString("codeNmae", codeNmae);
		resParams.putString("source", source);

		resParams.putInt("c_id", c_id);
		resParams.putString("c_name", c_name);
		resParams.putInt("z_id", z_id);
		resParams.putString("z_name", z_name);
		resParams.putString("r_price", r_price);
		resParams.putInt("r_discount", r_discount);
		resParams.putInt("r_full", r_full);
		resParams.putString("inTAG", inTAG);

		super.onSaveInstanceState(resParams);

	}

	/**
	 * 在该方法中保存状态数据
	 */
	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences sp = this.getSharedPreferences("save",
				Context.MODE_PRIVATE);
		// 保存输入框的内容
		sp.edit().putString("name", name).commit();
		sp.edit().putString("gid", gid).commit();
		sp.edit().putString("curRate", curRate).commit();
		sp.edit().putString("Ram", Ram).commit();
		sp.edit().putString("price_disc", price_disc).commit();
		sp.edit().putString("pri", pri).commit();
		sp.edit().putString("source", source).commit();
		sp.edit().putString("icon", icon).commit();
		sp.edit().putString("symbol", symbol).commit();
		sp.edit().putString("codeNmae", codeNmae).commit();
		sp.edit().putInt("rid", rid).commit();
		sp.edit().putInt("c_id", c_id).commit();
		sp.edit().putInt("z_id", z_id).commit();
		sp.edit().putInt("r_full", r_full).commit();
		sp.edit().putInt("r_discount", r_discount).commit();
		sp.edit().putString("c_name", c_name).commit();
		sp.edit().putString("z_name", z_name).commit();
		sp.edit().putString("r_price", r_price).commit();
		sp.edit().putString("inTAG", inTAG).commit();

	}

	/**
	 * 在该方法中恢复状态数据
	 */
	@Override
	protected void onResume() {

		// 得到保存的内容
		name = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("name", null);
		gid = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("gid", null);
		curRate = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("curRate", null);
		Ram = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("Ram", null);
		price_disc = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("price_disc", null);
		pri = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("pri", null);
		source = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("source", null);
		icon = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("icon", null);
		symbol = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("symbol", null);
		codeNmae = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("codeNmae", null);
		rid = this.getSharedPreferences("save", Context.MODE_PRIVATE).getInt(
				"rid", 0);
		c_id = this.getSharedPreferences("save", Context.MODE_PRIVATE).getInt(
				"rid", 0);
		z_id = this.getSharedPreferences("save", Context.MODE_PRIVATE).getInt(
				"rid", 0);
		c_name = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("c_name", null);
		z_name = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("z_name", null);
		r_price = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("r_price", null);
		r_discount = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getInt("r_discount", 0);
		r_full = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getInt("r_full", 0);
		inTAG = this.getSharedPreferences("save", Context.MODE_PRIVATE)
				.getString("inTAG", null);

		// 恢复内容
		rateval.setText(curRate);
		if (!TextUtils.isEmpty(source)) {
			imageLoader.displayImage(source, rec_supply_img, options,
					animateFirstListener);
		}
		goods_originalPrice.setText(pri);
		goods_actualPrice.setText(price_disc);

		goods_originalPrice.setText(pri);

		goods_actualPrice.setText(price_disc);
		goodsActualPrice.setText(Ram);
		goodsdetail_images.setGoodsName(name);
		if (rid > 0) {
			warning_lay.setVisibility(View.VISIBLE);
			warning_rice.setText(r_price + codeNmae);
			warning_color.setText(c_name);
			warning_size.setText(z_name);
			if (r_discount == 0) {
				warning_discount.setText("关");
			} else {
				warning_discount.setText("开");
			}
		} else {
			warning_lay.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(gid) && !gid.equals("-1")) {
			if (checkLogin()) {
				getCount(gid);
			}
			getDatas(gid);

		}

		super.onResume();
	}

	@Override
	public void refresh() {
		super.refresh();
		Bundle resParams = getIntent().getExtras();
		if (PersonalCollectionActivity.class.equals(parentClass)) {
			inTAG = "1";
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			// 来源
			source = resParams.getString("source");
			Ram = resParams.getString("Ram");
			// 商品Id
			gid = String.valueOf(resParams.getLong("gid", -1l));
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
			inventory = resParams.getString("inventory");
			// 预警
			rid = resParams.getInt("rid");
			c_id = resParams.getInt("c_id");
			z_id = resParams.getInt("z_id");
			c_name = resParams.getString("c_name");
			z_name = resParams.getString("z_name");
			r_price = resParams.getString("r_price");
			r_discount = resParams.getInt("r_discount");
			r_full = resParams.getInt("r_full");
		} else

		if (RecommendActivity.class.equals(parentClass)) {
			inTAG = "2";
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			// 商品Id
			gid = String.valueOf(resParams.getLong("gid", -1l));
			// 商品LOGO
			icon = resParams.getString("icon");
			// 商品名称
			name = resParams.getString("name");

			// 旧美元
			pri = resParams.getString("pri");
			// 新美元
			price_disc = resParams.getString("price_disc");
			Ram = resParams.getString("Ram");
			// 汇率
			curRate = resParams.getString("curRate");
			/**
			 * 货币名称
			 */
			codeNmae = resParams.getString("codeNmae");
			// 来源
			source = resParams.getString("source");

			// 币种
			symbol = resParams.getString("symbol");

		} else if (SortSearchResultActivity.class.equals(parentClass)) {
			inTAG = "3";
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			// 商品Id
			gid = String.valueOf(resParams.getLong("gid", -1l));
			// 商品LOGO
			icon = resParams.getString("icon");
			// 商品名称
			name = resParams.getString("name");

			// 旧美元
			pri = resParams.getString("pri");
			// 新美元
			price_disc = resParams.getString("price_disc");
			Ram = resParams.getString("Ram");
			// 汇率
			curRate = resParams.getString("curRate");
			/**
			 * 货币名称
			 */
			codeNmae = resParams.getString("codeNmae");
			// 来源
			source = resParams.getString("source");

			// 币种
			symbol = resParams.getString("symbol");
		} else if (SearchResultActivity.class.equals(parentClass)) {
			inTAG = "4";
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			// 商品Id
			gid = String.valueOf(resParams.getLong("gid", -1l));
			// 商品LOGO
			icon = resParams.getString("icon");
			// 商品名称
			name = resParams.getString("name");

			// 旧美元
			pri = resParams.getString("pri");
			// 新美元
			price_disc = resParams.getString("price_disc");
			Ram = resParams.getString("Ram");
			// 汇率
			curRate = resParams.getString("curRate");
			/**
			 * 货币名称
			 */
			codeNmae = resParams.getString("codeNmae");
			// 来源
			source = resParams.getString("source");

			// 币种
			symbol = resParams.getString("symbol");
		} else

		if (ShopCarGroupActiviy.class.equals(parentClass)) {
			inTAG = "5";
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			// 商品Id
			gid = String.valueOf(resParams.getLong("gid", -1l));
			// 商品LOGO
			icon = resParams.getString("icon");
			// 商品名称
			name = resParams.getString("name");

			// 旧美元
			pri = resParams.getString("pri");
			// 新美元
			price_disc = resParams.getString("price_disc");
			Ram = resParams.getString("Ram");
			// 汇率
			curRate = resParams.getString("curRate");
			/**
			 * 货币名称
			 */
			codeNmae = resParams.getString("codeNmae");
			// 来源
			source = resParams.getString("source");

			// 币种
			symbol = resParams.getString("symbol");

		}

		else if (PersonalLookEvaluationActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			if (inTAG.equals("1")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.personal.PersonalCollectionActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (inTAG.equals("2")) {
				if (lparentClass != null) {

					try {

						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.RecommendActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("3")) {
				if (lparentClass != null) {

					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SortSearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("4")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} else

		if (inTAG.equals("5")) {
			if (lparentClass != null) {
				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if (WebViewActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			if (inTAG.equals("1")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.personal.PersonalCollectionActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (inTAG.equals("2")) {
				if (lparentClass != null) {

					try {

						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.RecommendActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("3")) {
				if (lparentClass != null) {

					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SortSearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("4")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("5")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} else if (PersonalJoinShopCartActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			if (inTAG.equals("1")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.personal.PersonalCollectionActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (inTAG.equals("2")) {
				if (lparentClass != null) {

					try {

						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.RecommendActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("3")) {
				if (lparentClass != null) {

					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SortSearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("4")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("5")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

		else if (PersonalWarningCompileActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			if (inTAG.equals("1")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.personal.PersonalCollectionActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (inTAG.equals("2")) {
				if (lparentClass != null) {

					try {

						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.RecommendActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("3")) {
				if (lparentClass != null) {

					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SortSearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("4")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("5")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

		else if (PersonalUserLogin.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;

			if (inTAG.equals("1")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.personal.PersonalCollectionActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (inTAG.equals("2")) {
				if (lparentClass != null) {

					try {

						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.RecommendActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("3")) {
				if (lparentClass != null) {

					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SortSearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("4")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.recommend.SearchResultActivity");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else

			if (inTAG.equals("5")) {
				if (lparentClass != null) {
					try {
						parentClass = (Class<IHaiGoActivity>) Class
								.forName("com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

		rateval.setText(curRate);
		if (!TextUtils.isEmpty(source)) {
			imageLoader.displayImage(source, rec_supply_img, options,
					animateFirstListener);
		}
		goods_originalPrice.setText(pri);
		goods_actualPrice.setText(price_disc);

		goods_originalPrice.setText(pri);

		goods_actualPrice.setText(price_disc);
		goodsActualPrice.setText(Ram);
		goodsdetail_images.setGoodsName(name);
		if (rid > 0) {
			warning_lay.setVisibility(View.VISIBLE);
			warning_rice.setText(r_price + codeNmae);
			warning_color.setText(c_name);
			warning_size.setText(z_name);
			if (r_discount == 0) {
				warning_discount.setText("关");
			} else {
				warning_discount.setText("开");
			}
		} else {
			warning_lay.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(gid) && !gid.equals("-1")) {
			if (checkLogin()) {
				getCount(gid);
			}
			getDatas(gid);

		}

	}

	@Override
	protected void back() {

		lparentClass = parentClass;

		if (inTAG.equals("1")) {
			if (lparentClass == null) {
				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.personal.PersonalCollectionActivity");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			showTabHost = true;
			refreshActivity = true;
			resParams = new Bundle();
		} else if (inTAG.equals("2")) {
			if (lparentClass == null) {

				try {

					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.recommend.RecommendActivity");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			showTabHost = true;
			refreshActivity = true;
			resParams = new Bundle();

		} else

		if (inTAG.equals("3")) {
			if (lparentClass == null) {

				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.recommend.SortSearchResultActivity");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			showTabHost = true;
			refreshActivity = true;
			resParams = new Bundle();

		} else

		if (inTAG.equals("4")) {
			if (lparentClass == null) {
				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.recommend.SearchResultActivity");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			showTabHost = true;
			refreshActivity = true;
			resParams = new Bundle();

		} else

		if (inTAG.equals("5")) {
			if (lparentClass == null) {

				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			showTabHost = true;
			refreshActivity = true;
			resParams = new Bundle();

		}

		super.back();
	}

}
