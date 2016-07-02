package com.kc.ihaigo.ui.shipping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalLoginActivity;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.shipping.adapter.ShippingAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * 
 * @ClassName: ShippingActivity
 * @Description: 物流主页
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:10:03
 * 
 */
public class ShippingActivity extends IHaiGoActivity {
	private PullUpRefreshListView shipping_list;
	private ShippingAdapter adapter;
	private RelativeLayout empty;
	private LinearLayout have;
	private LinearLayout shipp;
	private Integer curpage = 1;// 记录当前是页码
	private Boolean isLoading = false;
	private Boolean isLoadAll = false;// 是否加载全部数据;
	private Integer lastReqLength = 0;// 上一次请求到的数据长度

	private StringBuffer deleteId;
	private JSONObject bject;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shipping);
		empty = (RelativeLayout) findViewById(R.id.empty);
		have = (LinearLayout) findViewById(R.id.have);
		have.setVisibility(View.GONE);
		mHandler = new Handler();

		initTitle();
		initComponets();
		getLogisticsList();

	}

	private void initTitle() {

		findViewById(R.id.title_right).setOnClickListener(this);

	}

	private void initComponets() {
		shipping_list = (PullUpRefreshListView) findViewById(R.id.shipping_list);
		adapter = new ShippingAdapter(this);
		adapter.setCall(new ShopcarBackCall());
		adapter.setDatas(new ArrayList<JSONObject>());
		shipping_list.setAdapter(adapter);

		shipping_list.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isLoading && !isLoadAll) {
					curpage++;
					getLogisticsList();
				}

			}
		});

	}

	/**
	 * 隐藏
	 * 
	 * @param contentId
	 */
	private void setWay(String id) {
		String url = Constants.REC_SHIPPING_HID + id;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", Constants.USER_ID);
		map.put("hide", "2");// 是否隐藏 （当需要隐藏是，传值为2，否则为1或者不传）
		map.put("content", "");// 包裹内容（当不是隐藏时，该字段必输）
		map.put("remark", "");// 备注（当不是隐藏时，该字段必输）

		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {

						try {

							if (!TextUtils.isEmpty(result)) {
								JSONObject jsonObject = new JSONObject(result);
								int code = jsonObject.getInt("code");
								if (code == 1) {
									List<JSONObject> list = adapter.getData();
									list.remove(bject);
									adapter.setDatas(list);
									adapter.notifyDataSetChanged();

								} else {
									adapter.notifyDataSetChanged();
								}
							} else {
								Toast.makeText(ShippingActivity.this,
										getString(R.string.connection_time),
										Toast.LENGTH_LONG).toString();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, 0, R.string.loading);

	}

	public class ShopcarBackCall extends BackCall {

		@Override
		public void deal(int which, Object... obj) {
			switch (which) {
			case R.id.button:
				String contentId = null;
				try {
					bject = (JSONObject) obj[0];
					contentId = bject.getString("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (!TextUtils.isEmpty(contentId)) {
					setWay(contentId);
				}

				break;

			default:
				break;
			}
		}
	}

	/**
	 * 物流列表
	 */
	private void getLogisticsList() {
		isLoading = true;
		final String url = Constants.REC_LOGISTICS + "?userId="
				+ Constants.USER_ID + "&page=" + curpage + "&pagesize="
				+ Constants.APP_DATA_LENGTH;
		
			HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
					new HttpReqCallBack() {
						@Override
						public void deal(String result) {
							try {
								if (!TextUtils.isEmpty(result)) {
									have.setVisibility(View.VISIBLE);
									empty.setVisibility(View.GONE);
									JSONObject resData = new JSONObject(result);
									JSONArray datas = resData
											.getJSONArray("logistics");
									if (datas != null && datas.length() > 0) {

										List<JSONObject> json = adapter
												.getData();
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
											// Log.d(TAG, "停留当前页" + curpage +
											// "页");
											// }
											// }
											lastReqLength = datas.length();
										} else {
											isLoadAll = false;
										}

										isLoading = false;

									}

								} else {
									have.setVisibility(View.GONE);
									empty.setVisibility(View.VISIBLE);
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}, 0, R.string.loading);
		

	}

	@Override
	public void refresh() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;
		adapter.setDatas(new ArrayList<JSONObject>());

		getLogisticsList();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right:// 添加
			Intent intent = new Intent(ShippingActivity.this,
					AddShippingActivity.class);
			Bundle bun = new Bundle();
			intent.putExtras(bun);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			ShippingGroupActiviy.group.startiHaiGoActivity(intent);

			break;

		default:
			break;
		}

	}

	@Override
	protected void back() {
		exitApp();
	}
}
