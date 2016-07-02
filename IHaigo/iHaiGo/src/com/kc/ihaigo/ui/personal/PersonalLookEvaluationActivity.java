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
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.CommetsAdapter;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 查看全部评价
 * 
 * @author Thinkpad
 * 
 */
public class PersonalLookEvaluationActivity extends IHaiGoActivity {
	private PullUpRefreshListView listView;
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
	private ImageView rec_supply_img;

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

	private Integer curpage = 1;// 记录当前是页码
	private Boolean isLoading = false;
	private Boolean isLoadAll = false;// 是否加载全部数据;
	private Integer lastReqLength = 0;// 上一次请求到的数据长度
	private CommetsAdapter gdca;
	private Class<IHaiGoActivity> lparentClass;
	private IHaiGoGroupActivity lparentGroupActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look_evaluation);
		initComponets();
		initTitle();
		setImage();

	}

	private void initTitle() {
		// 定义返回功能
		findViewById(R.id.title_left).setOnClickListener(this);
		// 我要评价
		findViewById(R.id.evaluationBut).setOnClickListener(this);

	}

	private void setImage() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	private void initComponets() {

		listView = (PullUpRefreshListView) findViewById(R.id.listView);
		goodsImg = (ImageView) findViewById(R.id.goodsImg);
		rec_supply_img = (ImageView) findViewById(R.id.rec_supply_img);
		goodsName_tv = (TextView) findViewById(R.id.goodsName_tv);
		originalPrice_tv = (TextView) findViewById(R.id.originalPrice_tv);
		actualPrice_tv = (TextView) findViewById(R.id.actualPrice_tv);
		goodsActualPrice = (TextView) findViewById(R.id.goodsActualPrice);
		ram_actualPrice_tv = (TextView) findViewById(R.id.ram_actualPrice_tv);

		listView.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isLoading) {
					curpage++;
					getDatas(gid);
				}

			}
		});

		gdca = new CommetsAdapter(PersonalLookEvaluationActivity.this);
		gdca.setDatas(new JSONArray());
		listView.setAdapter(gdca);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			break;
		case R.id.evaluationBut:// 我要评价

			Intent intent = new Intent(PersonalLookEvaluationActivity.this,
					PersonalPublishEvaluationActivity.class);
			Bundle evalus = new Bundle();
			evalus.putString("gid", gid);
			evalus.putString("name", name);
			evalus.putString("icon", icon);
			evalus.putString("pri", pri);
			evalus.putString("price_disc", price_disc);
			evalus.putString("Ram", Ram);
			evalus.putString("curRate", curRate);
			evalus.putString("codeNmae", codeNmae);
			evalus.putString("source", source);
			intent.putExtras(evalus);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			lparentGroupActivity.startiHaiGoActivity(intent);

			break;

		default:
			break;
		}

	}

	@Override
	public void refresh() {
		super.refresh();

		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
		gdca.setDatas(new JSONArray());
		Bundle resParams;
		if (PersonalGoodsDetailsActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			resParams = getIntent().getExtras();
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

		}

		// 来源
		imageLoader.displayImage(source, rec_supply_img, options,
				animateFirstListener);
		imageLoader.displayImage(icon, goodsImg, options, animateFirstListener);
		// 价格
		originalPrice_tv.setText(pri);
		actualPrice_tv.setText(price_disc);
		goodsActualPrice.setText(Ram);

		// 商品名称
		goodsName_tv.setText(name);

		ram_actualPrice_tv.setText(curRate);

		if (!TextUtils.isEmpty(gid)) {
			getDatas(gid);
		}

	}

	private void getDatas(String goodId) {
		isLoading = true;
		String url = Constants.REC_GOODS_EVALUATION;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("goodId", goodId);
		map.put("page", curpage);
		map.put("pagesize", Constants.APP_DATA_LENGTH);
		Log.e("lg", map.toString());
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {

								JSONObject jsonbject = new JSONObject(result);
								JSONArray datas = jsonbject
										.getJSONArray("evaluationGood");

								JSONArray data = gdca.getDatas();
								boolean isNeedRefresh = false;
								if (isLoadAll) {
									for (int i = lastReqLength; i < datas
											.length(); i++) {
										data.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								} else {
									for (int i = 0; i < datas.length(); i++) {
										data.put(datas.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
									gdca.notifyDataSetChanged();
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

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	@Override
	protected void back() {
		parentClass = lparentClass;
		super.back();
	}

}
