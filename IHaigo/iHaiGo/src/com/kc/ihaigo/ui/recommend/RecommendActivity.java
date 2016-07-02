package com.kc.ihaigo.ui.recommend;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGoodsDetailsActivity;
import com.kc.ihaigo.ui.recommend.adapter.RecContentAdapter;
import com.kc.ihaigo.ui.recommend.adapter.RecContentAdapter.ViewHolder;
import com.kc.ihaigo.ui.recommend.adapter.RecRateAdapter;
import com.kc.ihaigo.ui.selfwidget.HeadImagesView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollDownListener;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollUpListener;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * 
 * @ClassName: RecommendActivity
 * @Description: 精选首页
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:44:15
 * 
 */
@SuppressLint("HandlerLeak")
public class RecommendActivity extends IHaiGoActivity
		implements
			OnItemClickListener,
			OnClickListener {
	private PullUpRefreshListView rec_ll_content;
	private RecContentAdapter rcadapter;
	// private Handler handlerRate;
	private HeadImagesView headList;
	private final String TAG = "RecommendActivity";
	private int curpage = 1;// 记录当前是页码
	private boolean isLoading = false;
	private boolean isLoadAll = false;// 是否加载全部数据;
	private int lastReqLength = 0;// 上一次请求到的数据长度
	private LinearLayout rec_rate_ll;
	private TextView rec_rate_tvval;
	private JSONArray rates;
	private int currentmessagepage = 0;
	private boolean mesgloading = false;
	private JSONArray msgDatas = new JSONArray();
	private TextView msgBox;

	// private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		initTitle();
		initComponents();
		getAds();
		getGoods();
		getExchangeRate();
	}

	/**
	 * @Title: getExchangeRate
	 * @user: ryan.wang
	 * @Description: 获取汇率
	 * @throws
	 */

	private void getExchangeRate() {
		String url = Constants.REC_RATE;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							StringBuffer rate = new StringBuffer();
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(result);
								rates = jsonObject.getJSONArray("rates");
								for (int i = 0; i < rates.length(); i++) {
									JSONObject rateJsonObject = rates
											.getJSONObject(i);
									// double rateRate = rateJsonObject
									// .getDouble("rate") * 0.01;
									//
									// rate.append(rateJsonObject.get("name")
									// + "  1:"
									// + new BigDecimal(rateRate)
									// .setScale(
									// 4,
									// BigDecimal.ROUND_HALF_UP)
									// .doubleValue() + " ");
									rate.append(rateJsonObject
											.getString("name")
											+ " "
											+ rateJsonObject.getString("code")
											+ " "
											+ "100:"
											+ rateJsonObject.getString("rate")
											+ "    ");
								}
								rec_rate_tvval.setText(rate);
								// 写入永久性缓存中
								DataConfig dConfig = new DataConfig(
										RecommendActivity.this);
								dConfig.setRate(result);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	private void initTitle() {
		findViewById(R.id.title_right).setOnClickListener(this);
		TextView title_tv_middle = (TextView) findViewById(R.id.title_middle);
		title_tv_middle.setText(R.string.title_activity_recommend);
	}

	private void initComponents() {
		View headView = getLayoutInflater().inflate(R.layout.activity_rec_head,
				null);
		headView.findViewById(R.id.rec_msg_ly).setOnClickListener(this);
		msgBox = (TextView) headView.findViewById(R.id.msgBox);
		rec_rate_ll = (LinearLayout) findViewById(R.id.rec_rate_ll);
		rec_rate_ll.setOnClickListener(this);
		headList = (HeadImagesView) headView.findViewById(R.id.headLive);
		headList.setBackCall(new BackCall() {

			@Override
			public void deal(int which, Object... obj) {
				Intent intent = new Intent(RecommendActivity.this,
						AdvActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				RecommendGroupActiviy.group.startiHaiGoActivity(intent);
			}
		});
		rec_ll_content = (PullUpRefreshListView) findViewById(R.id.rec_content_ll);
		rec_rate_tvval = (TextView) findViewById(R.id.rec_rate_tvval);
		rcadapter = new RecContentAdapter(RecommendActivity.this);
		rcadapter.setDatas(new JSONArray());
		rec_ll_content.addHeaderView(headView);
		rec_ll_content.setAdapter(rcadapter);
		rec_ll_content.setOnItemClickListener(this);
		rec_ll_content.setScrollBottomListener(new ScrollBottomListener() {
			@Override
			public void deal() {
				if (!isLoading) {
					curpage++;
					getGoods();
				}
			}
		});
		rec_ll_content.setUpListener(new ScrollUpListener() {
			@Override
			public void deal() {
				if (IHaiGoMainActivity.tab_content.getVisibility() != View.GONE
						|| rec_rate_ll.getVisibility() != View.GONE) {
					IHaiGoMainActivity.tab_content.measure(0, 0);
					rec_ll_content.getLayoutParams().height = Utils
							.getScreenHeight(RecommendActivity.this)
							- IHaiGoMainActivity.tab_content
									.getMeasuredHeight() - 20;
					IHaiGoMainActivity.tab_content.setVisibility(View.GONE);
					rec_rate_ll.setVisibility(View.GONE);
				}
			}
		});
		rec_ll_content.setDownListener(new ScrollDownListener() {
			@Override
			public void deal() {
				if (IHaiGoMainActivity.tab_content.getVisibility() != View.VISIBLE
						|| rec_rate_ll.getVisibility() != View.VISIBLE) {
					IHaiGoMainActivity.tab_content.setVisibility(View.VISIBLE);
					rec_rate_ll.setVisibility(View.VISIBLE);
				}
			}
		});
		// dialog = DialogUtil.showProgressDialog(
		// RecommendActivity.this.getParent(), null, "我是进度dialog");
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			int curMsg = 0;

			@Override
			public void run() {
				if (curMsg == msgDatas.length()) {
					curMsg = 0;
					getRecMsg();
				}
				try {
					if (msgDatas.length() > 0) {
						msgBox.setText(msgDatas.getJSONObject(curMsg++)
								.getString("title"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					handler.postDelayed(this, 3000);
				}
			}
		};
		handler.post(runnable);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long itemId) {
		Intent intent = new Intent(RecommendActivity.this,
				PersonalGoodsDetailsActivity.class);
		Bundle reqParams = new Bundle();
		RecContentAdapter.ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null)
			return;
		String code = holder.price.getTag() == null
				? ""
				: (String) holder.price.getTag();
		DataConfig dcConfig = new DataConfig(RecommendActivity.this);
		String symbol = dcConfig.getCurrencySymbolByCode(code);
		String curRate = "1"
				+ dcConfig.getCurrencyNameByCode(code)
				+ "="
				+ new BigDecimal(dcConfig.getCurRateByCode(code)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue()
				+ getText(R.string.renminbi);
		String codeNmae = dcConfig.getCurrencyNameByCode(code);
		// 商品ID
		reqParams.putLong("gid", itemId);
		// 商品LOGO
		reqParams.putString("icon", holder.rec_goods_img.getTag() + "");
		// 商品名称
		reqParams.putString("name", holder.rec_goodsname_tv.getText() + "");

		// 旧美元
		reqParams.putString("pri", holder.price.getText() + "");
		// 新美元
		reqParams.putString("price_disc", holder.discount.getText() + "");
		// 人民币
		reqParams.putString("Ram", holder.rmbprice.getText() + "");
		// reqParams.putString("inventory", inventory);

		// 汇率
		reqParams.putString("curRate", curRate);
		//
		reqParams.putString("codeNmae", codeNmae);
		// 来源
		reqParams.putString("source", holder.source.getTag() + "");

		// 币种
		reqParams.putString("symbol", symbol);
		intent.putExtras(reqParams);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	}
	/**
	 * 广告
	 */

	private void getAds() {
		final DataConfig dConfig = new DataConfig(getApplicationContext());
		String result = dConfig.getAds();
		if (TextUtils.isEmpty(result)) {
			// 如果缓存没有数据则通过网络获取
			String url = Constants.REC_CONFIG + "ads";
			HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
					new HttpReqCallBack() {
						@Override
						public void deal(String res) {
							if (Constants.Debug) {
								Log.d(TAG, "已获取广告信息：" + res);
							}
							if (!TextUtils.isEmpty(res)) {
								loadAdsData(res);
							}
						}
					}, 0);
		} else {
			// 如果缓存存在数据则直接更新
			loadAdsData(result);
		}

	}

	public void loadAdsData(String result) {
		if (!TextUtils.isEmpty(result)) {
			JSONObject jsobject;
			try {
				jsobject = new JSONObject(result);
				JSONArray ads = jsobject.getJSONArray("ads");
				headList.setAvdImages(ads);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 公告
	 */

	private void getRecMsg() {
		if (!mesgloading) {
			mesgloading = true;
			String url = Constants.REC_GOODS_FINDREMINDT + "getNotice?page="
					+ ++currentmessagepage + "&pagesize="
					+ Constants.APP_DATA_LENGTH;
			HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
					new HttpReqCallBack() {
						@Override
						public void deal(String result) {
							if (!TextUtils.isEmpty(result)) {
								try {
									JSONObject resData = new JSONObject(result);
									msgDatas = resData.getJSONArray("notice");
									if (msgDatas.length() < 1) {
										currentmessagepage = 0;
									}
								} catch (JSONException e) {
									currentmessagepage = 0;
								}
							} else {
								--currentmessagepage;
							}
							mesgloading = true;
						}
					});
		}
	}

	/**
	 * 商品翻页
	 */
	private void getGoods() {
		isLoading = true;
		rec_ll_content.showFooterView();
		String url = Constants.REC_GOODS + "?page=" + curpage + "&size="
				+ Constants.APP_DATA_LENGTH;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						try {

							if (!TextUtils.isEmpty(result)) {
								JSONObject jsonbject = new JSONObject(result);
								JSONArray datas = jsonbject
										.getJSONArray("goods");
								JSONArray oldDatas = rcadapter.getDatas();
								if (Constants.Debug) {
									Log.d(TAG, "请求到" + datas.length()
											+ "条,总长度：" + oldDatas.length());
								}
								boolean isNeedRefresh = false;

								if (isLoadAll) {
									for (int i = lastReqLength; i < datas
											.length(); i++) {
										oldDatas.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								} else {
									for (int i = 0; i < datas.length(); i++) {
										oldDatas.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								}

								if (isNeedRefresh) {
									rcadapter.notifyDataSetChanged();
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
									ToastUtil.showShort(RecommendActivity.this,
											"没有更多的数据");
								} else {
									isLoadAll = false;
								}
								isLoading = false;
								rec_ll_content.hideFooterView();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, 5);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_right :
				// 转到分类搜索
				Intent title_right = new Intent(RecommendActivity.this,
						SortSearchActivity.class);
				title_right.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
						false);
				RecommendGroupActiviy.group.startiHaiGoActivity(title_right);
				break;
			case R.id.rec_msg_ly :// 公告
				Intent rec_msg_ly = new Intent(RecommendActivity.this,
						NoticeActivity.class);
				RecommendGroupActiviy.group.startiHaiGoActivity(rec_msg_ly);
				break;
			case R.id.rec_rate_ll :
				DialogUtil.showRateDialog(RecommendActivity.this.getParent(),
						null, null, new RecRateAdapter(RecommendActivity.this,
								rates));
				break;
			default :
				break;
		}

	}

	@Override
	protected void back() {
		exitApp();
	}

}
