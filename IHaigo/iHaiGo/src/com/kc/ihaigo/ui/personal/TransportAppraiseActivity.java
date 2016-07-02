package com.kc.ihaigo.ui.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.TransCommentAdpater;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class TransportAppraiseActivity extends IHaiGoActivity implements OnItemClickListener{
	
	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private PullUpRefreshListView trans_listview;//全部评价
	private TransCommentAdpater adapter;
	private ImageView puragent_head;
	private TextView comments_num;
	private TextView charge_star_num;
	private TextView trans_star_num;
	private TextView service_star_num;
	private RatingBar charge_ratingbar_one;
	private RatingBar trans_ratingbar_one;
	private RatingBar service_ratingbar_one;
	private String transId;
	private String count;
	private String charge;
	private String logis;
	private String service;
	private int curpage = 1;// 记录当前是页码
	private boolean isLoading = false;
	private boolean isLoadAll = false;// 是否加载全部数据;
	private int lastReqLength = 0;// 上一次请求到的数据长度
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_app);
		init();
	}
	private void Image() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	private void initVar() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
	}
	/**
	 * @Title: init
	 * @user: helen.yang
	 * @Description: TODO
	 * void
	 * @throws
	 */

	private void init() {
		
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
		comments_num = (TextView) findViewById(R.id.comments_num);
		charge_star_num = (TextView) findViewById(R.id.charge_star_num);
		trans_star_num = (TextView) findViewById(R.id.trans_star_num);
		service_star_num = (TextView) findViewById(R.id.service_star_num);
		charge_ratingbar_one = (RatingBar) findViewById(R.id.charge_ratingbar_one);
		trans_ratingbar_one = (RatingBar) findViewById(R.id.trans_ratingbar_one);
		service_ratingbar_one = (RatingBar) findViewById(R.id.service_ratingbar_one);
		
		trans_listview = (PullUpRefreshListView) findViewById(R.id.trans_listview);
		trans_listview.setOnItemClickListener(this);
		adapter = new TransCommentAdpater(TransportAppraiseActivity.this);
		trans_listview.setAdapter(adapter);
		trans_listview.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isLoading) {
					curpage++;
					getTrans(transId,String.valueOf(curpage));
				}
			}
		});
		
	}
	
	
	@Override
	public void refresh() {
		super.refresh();
		if(TransportMerchantDetailActivity.class.equals(parentClass)){
			transId = getIntent().getStringExtra("transId");
			count = getIntent().getStringExtra("count");
			comments_num.setText(count+"人评价");
			getTransport(transId);
			initVar();
			getTrans(transId,String.valueOf(curpage));
			charge_ratingbar_one.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					
				}
			});
			trans_ratingbar_one.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					
				}
			});
			service_ratingbar_one.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					
				}
			});
		}
	}
	/**
	 * 转动商详情
	 * 
	 * @param cid
	 */

	private void getTransport(String cid) {
		DataConfig da = new DataConfig(TransportAppraiseActivity.this);
		String Tcompany = da.getTcompanySty(cid);
		Log.i("geek", ":::::;:::::::"+Tcompany.toString());
		JSONObject com;
		try {
			com = new JSONObject(Tcompany);
			Log.i("geek", ":::::;:::::::"+com.toString());
			if (com != null && com.length() > 0) {
				for (int i = 0; i < com.length(); i++) {
					String id = com.getString("id");
					String name = com.getString("name");
					String icon = com.getString("icon");
					String signature = com.getString(
							"signature");
					String open = com.getString("open");
					charge = com.getString("charge");
					logis = com.getString("logistics");
					service = com.getString("service");
					
					charge_star_num.setText(charge);
					trans_star_num.setText(logis);
					service_star_num.setText(service);
					charge_ratingbar_one.setRating(Float.parseFloat(charge));
					trans_ratingbar_one.setRating(Float.parseFloat(logis));
					service_ratingbar_one.setRating(Float.parseFloat(service));
					if (!TextUtils.isEmpty(icon)) {
//						imageLoader.displayImage(icon, puragent_head, options,
//								animateFirstListener);
					}
				

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 用户评价
	 */
	private void getTrans(String transId,String page) {
		String httpUrl = Constants.TRANS_INFO + "?id=" + transId + "&page=" + page
				+ "&pagesize=" + Constants.APP_DATA_LENGTH;
		isLoading = true;
		trans_listview.showFooterView();
////		String httpUrl = Constants.TRANS_COMMENT_INFO+"?company=" + transId+"&user="+1+ "&page=" + 
////				curpage+ "&pagesize=" + Constants.APP_DATA_LENGTH;
//		String httpUrl = Constants.TRANS_COMMENT_INFO +"?user="+transId;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, httpUrl, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								Log.i("geek", ";;;;;;;;;;"+data.toString());
//								JSONObject jsonObject = data.getJSONObject("evaluationTransport");
								JSONArray jsonArray = data.getJSONArray("evaluationTransport");
								JSONArray oldDatas = adapter.getDatas();
								boolean isNeedRefresh = false;
								if (isLoadAll) {
									for (int i = lastReqLength; i < jsonArray
											.length(); i++) {
										oldDatas.put(jsonArray.getJSONObject(i));
										isNeedRefresh = true;
									}
									ToastUtil.showShort(TransportAppraiseActivity.this, "已没有更多的数据！");
								} else {
									for (int i = 0; i < jsonArray.length(); i++) {
										oldDatas.put(jsonArray.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
									adapter.notifyDataSetChanged();
								}
								if (jsonArray.length() < Constants.APP_DATA_LENGTH) {
									isLoadAll = true;
									// 设置成前一页，允许用户重复请求当前页以便抓取服务端最新数据
									// if (curpage > 1) {
									curpage--;
									// if (Constants.Debug) {
									// Log.d(TAG, "停留当前页" + curpage + "页");
									// }
									// }
									lastReqLength = jsonArray.length();
//									ToastUtil.showShort(PersonalTopicActivity.this, "已没有更多的数据！");
								} else {
									isLoading = false;
									trans_listview.hideFooterView();
								}
								
								isLoading = false;
								trans_listview.hideFooterView();
//								adapter.setDatas(jsonArray);
//								adapter.notifyDataSetChanged();

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		
	}
}
