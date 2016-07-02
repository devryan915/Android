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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.TopicPublishAdapter;
import com.kc.ihaigo.ui.personal.adapter.TopicRespondAdpater;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.selfwidget.hiddenListView;
import com.kc.ihaigo.ui.topic.TopicDetailActivity;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * 我的话题
 * 
 * @author Thinkpad
 * 
 */
public class PersonalTopicActivity extends IHaiGoActivity implements
		OnClickListener, OnItemClickListener {
	/**
	 * 我的发表
	 */
	private TextView topic_publish;

	/**
	 * 我的回应
	 */
	private TextView topic_respond;
	private String FLAG = "0";
	private String FLAG_TOIPC = "0";
	private String FLAG_RESPONSE = "1";

	private boolean isFirstClick = true;
	private boolean isResponseFirstClick = true;

	private int currentStatus = VIEW_STATUS;
	private final static int VIEW_STATUS = 0;
	private final static int EDIT_STATUS = 1;
	private TopicPublishAdapter adapter;
	private TopicRespondAdpater radapter;
	private PullUpRefreshListView listview;
	private PullUpRefreshListView rlistview;

	private int curpage = 1;// 记录当前是页码
	private boolean isLoading = false;
	private boolean isLoadAll = false;// 是否加载全部数据;
	private int lastReqLength = 0;// 上一次请求到的数据长度
	private int curpages = 1;
	private boolean isResponseLoading = false;
	private boolean isResponseLoadAll = false;// 是否加载全部数据;
	private int lastResponseReqLength = 0;// 上一次请求到的数据长度
	private StringBuffer deleteGoodsId = null;
	private JSONArray json;

	private View mFooterView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_topic);
		// Constants.USER_ID = "9";
		initCooseIitle();
		initTitle();

	}

	private void initCooseIitle() {
		topic_publish = (TextView) findViewById(R.id.topic_publish);
		topic_publish.setOnClickListener(this);
		topic_publish
				.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
		topic_publish.setTextColor(this.getResources().getColor(R.color.white));

		topic_respond = (TextView) findViewById(R.id.topic_respond);
		topic_respond.setOnClickListener(this);
		topic_respond.setBackgroundResource(R.drawable.choose_item_right_shape);
		topic_respond
				.setTextColor(this.getResources().getColor(R.color.choose));
	}
	
	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		listview = (PullUpRefreshListView) findViewById(R.id.coll_listview);
		listview.setOnItemClickListener(this);
		adapter = new TopicPublishAdapter(PersonalTopicActivity.this);
		adapter.setCall(new ShopcarBackCall());
		adapter.setDatas(new ArrayList<JSONObject>());
		listview.setAdapter(adapter);

		rlistview = (PullUpRefreshListView) findViewById(R.id.r_listview);
		radapter = new TopicRespondAdpater(PersonalTopicActivity.this);
		radapter.setCall(new ShopcarBackCall());
		radapter.setDatas(new ArrayList<JSONObject>());
		rlistview.setAdapter(radapter);

		listview.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isLoading) {
					curpage++;
					// loadAgents(SORT_PRICE);
					getTopics(Constants.USER_ID, curpage, Constants.APP_DATA_LENGTH+"");
				}
			}
		});

		rlistview.setScrollBottomListener(new ScrollBottomListener() {

			@Override
			public void deal() {

				if (!isResponseLoading) {
					curpages++;
					getRespond(Constants.USER_ID, curpages, Constants.APP_DATA_LENGTH+"");
				}

			}
		});

		isFirstClick = false;
//		getTopics(Constants.USER_ID, curpage, Constants.APP_DATA_LENGTH+"");
		// adapter.notifyDataSetChanged();
	}

	private void initVar() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
		curpages = 1;
		isResponseLoading = false;
		isResponseLoadAll = false;// 是否加载全部数据;
		lastResponseReqLength = 0;// 上一次请求到的数据长度
	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		showTabHost = true;
		super.back();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.topic_publish:
			FLAG = FLAG_TOIPC;
			listview.setVisibility(View.VISIBLE);
			rlistview.setVisibility(View.GONE);
			if (isFirstClick) {
				// List<JSONObject> newDatas = new ArrayList<JSONObject>();
				// adapter.setDatas(newDatas);
				// initVar();
				getTopics(Constants.USER_ID, curpage, Constants.APP_DATA_LENGTH+"");
				// listview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				isFirstClick = false;
			}
			topic_publish
					.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
			topic_publish.setTextColor(this.getResources().getColor(
					R.color.white));

			topic_respond
					.setBackgroundResource(R.drawable.choose_item_right_shape);
			topic_respond.setTextColor(this.getResources().getColor(
					R.color.choose));

			break;
		case R.id.topic_respond:
			listview.setVisibility(View.GONE);
			rlistview.setVisibility(View.VISIBLE);
			FLAG = FLAG_RESPONSE;
			if (isResponseFirstClick) {

				// List<JSONObject> newDatas = new ArrayList<JSONObject>();
				// radapter.setDatas(newDatas);
				// initVar();
				getRespond(Constants.USER_ID, curpages, Constants.APP_DATA_LENGTH+"");
				radapter.notifyDataSetChanged();
				isResponseFirstClick=false;
			}
			topic_publish
					.setBackgroundResource(R.drawable.choose_item_lift_shape);
			topic_publish.setTextColor(this.getResources().getColor(
					R.color.choose));

			topic_respond
					.setBackgroundResource(R.drawable.choose_item_right_selected_shape);
			topic_respond.setTextColor(this.getResources().getColor(
					R.color.white));

			break;

		case R.id.title_left:
			intent.setClass(PersonalTopicActivity.this, PersonalActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		default:
			break;
		}

	}
	
	
	/**
	 * 我的回应列表
	 * 
	 * @param id
	 * @param page
	 * @param pagesize
	 */

	public void getRespond(String id, int pages, String pagesize) {
		isResponseLoading = true;
		rlistview.showFooterView();
		String url = Constants.TOPICS_URL + "respond" + "?uid=" + id + "&page="
				+ pages + "&pagesize=" + pagesize;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							Log.i("geek", "**********************嘻嘻getRespond"
									+ result);
							try {
								JSONObject resData = new JSONObject(result);
								json = resData.getJSONArray("topic");
								List<JSONObject> oldDatas = radapter.getDatas();
								boolean isNeedRefresh = false;
								if (isResponseLoadAll) {
									for (int i = lastResponseReqLength; i < json
											.length(); i++) {
										oldDatas.add(json.getJSONObject(i));
										isNeedRefresh = true;
									}
									ToastUtil.showShort(PersonalTopicActivity.this, "已没有更多的数据！");
								} else {
									for (int i = 0; i < json.length(); i++) {
										oldDatas.add(json.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
									radapter.notifyDataSetChanged();
								}
								if (json.length() < Constants.APP_DATA_LENGTH) {
									isResponseLoadAll = true;
									// 设置成前一页，允许用户重复请求当前页以便抓取服务端最新数据
									// if (curpage > 1) {
									curpages--;
									// if (Constants.Debug) {
									// Log.d(TAG, "停留当前页" + curpage + "页");
									// }
									// }
									lastResponseReqLength = json.length();
//									ToastUtil.showShort(PersonalTopicActivity.this, "已没有更多的数据！");
								} else {
									isResponseLoading = false;
									rlistview.hideFooterView();
								}

								// if (json != null) {
								// List<JSONObject> datas = new
								// ArrayList<JSONObject>();
								// for (int i = 0; i < json.length(); i++) {
								// datas.add(json.getJSONObject(i));
								// }
								// }
								isResponseLoading = false;
								rlistview.hideFooterView();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i("geek", "**********************哈哈getRespond"
									+ result);
						}
					}
				}, 0);
	}

	/**
	 * 我的话题列表
	 * 
	 * @param id
	 * @param page
	 * @param pagesize
	 */

	public void getTopics(String id, int page, String pagesize) {
		isLoading = true;
		listview.showFooterView();
		String url = Constants.TOPICS_URL + "myTopic" + "?uid=" + id + "&page="
				+ page + "&pagesize=" + pagesize;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							Log.i("geek", "**********************嘻嘻" + result);
							try {
								JSONObject resData = new JSONObject(result);
								json = resData.getJSONArray("topic");
								List<JSONObject> oldDatas = adapter.getDatas();
								boolean isNeedRefresh = false;
								if (isLoadAll) {
									for (int i = lastReqLength; i < json
											.length(); i++) {
										oldDatas.add(json.getJSONObject(i));
										isNeedRefresh = true;
									}
									ToastUtil.showShort(PersonalTopicActivity.this, "已没有更多的数据！");
								} else {
									for (int i = 0; i < json.length(); i++) {
										oldDatas.add(json.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
									adapter.setDatas(oldDatas);
									adapter.notifyDataSetChanged();
								}
								if (json.length() < Constants.APP_DATA_LENGTH) {
									isLoadAll = true;
									// 设置成前一页，允许用户重复请求当前页以便抓取服务端最新数据
									// if (curpage > 1) {
									curpage--;
									// if (Constants.Debug) {
									// Log.d(TAG, "停留当前页" + curpage + "页");
									// }
									// }
									lastReqLength = json.length();
//									ToastUtil.showShort(PersonalTopicActivity.this, "已没有更多的数据！");
								} else {
									isLoading = false;
									listview.hideFooterView();
								}
								isLoading = false;
								listview.hideFooterView();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i("geek", "**********************哈哈" + result);
						}
					}
				}, 1);
	}
	
	/**
	 * 删除话题
	 * 
	 * @param id
	 */
	private void deleteTopic(String id) {
		String url = Constants.TOPIC_URL + id + "/updateTopic";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", Constants.USER_ID);
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
										ToastUtil.showShort(
												PersonalTopicActivity.this,
												"删除话题成功");
									} else {
										// 删除失败
										ToastUtil.showShort(
												PersonalTopicActivity.this,
												"删除话题失败");
									}

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}, 1);

	}

	/**
	 * 删除我的回应
	 * 
	 * @param id
	 */
	private void deleteResponse(String eid) {
		String url = Constants.TOPIC_URL + eid + "/update";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", Constants.USER_ID);
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
										ToastUtil.showShort(
												PersonalTopicActivity.this,
												"删除回应成功");
									} else {
										// 删除失败
										ToastUtil.showShort(
												PersonalTopicActivity.this,
												"删除回应失败");
									}

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}, 1);

	}

	public class ShopcarBackCall extends BackCall {

		private int id;
		private int eid;

		@Override
		public void deal(int which, Object... obj) {
			switch (which) {
			case R.id.deletegoods_publish:
				id = 0;
				if (deleteGoodsId == null) {
					deleteGoodsId = new StringBuffer();
					JSONObject json = (JSONObject) obj[0];
					try {
						id = json.getInt("id");
						deleteGoodsId.append(id);
						// 弹框提示
						DialogUtil.showDeleteTopicDialog(
								PersonalTopicActivity.this.getParent(),
								new BackCall() {

									@Override
									public void deal(int which, Object... obj) {
										switch (which) {
										case R.id.exit_oks:
											((Dialog) obj[0]).dismiss();
											deleteTopic(String.valueOf(id));
											break;

										default:
											break;
										}
									}
								}, null);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					deleteGoodsId.append(id);
				}

				List<JSONObject> list = adapter.getDatas();
				list.remove((JSONObject) obj[0]);
				adapter.setDatas(list);
				adapter.notifyDataSetChanged();

				break;
			case R.id.deletegoods:
				eid = 0;
				if (deleteGoodsId == null) {
					deleteGoodsId = new StringBuffer();
					JSONObject jsonObject = (JSONObject) obj[0];
					try {
						eid = jsonObject.getInt("eid");
						deleteGoodsId.append(eid);
						// 弹框提示
						DialogUtil.showDeleteRespondDialog(
								PersonalTopicActivity.this.getParent(),
								new BackCall() {

									@Override
									public void deal(int which, Object... obj) {
										switch (which) {
										case R.id.exit_oks:
											((Dialog) obj[0]).dismiss();
											deleteResponse(String.valueOf(eid));
											break;

										default:
											break;
										}
									}
								}, null);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					deleteGoodsId.append(eid);
				}
				List<JSONObject> lists = radapter.getDatas();
				lists.remove((JSONObject) obj[0]);
				radapter.setDatas(lists);
				radapter.notifyDataSetChanged();

				break;
			case 1000001:
				try {
					JSONObject jsons = (JSONObject) obj[0];
					if (FLAG_RESPONSE.equals(FLAG)) {
						eid = jsons.getInt("eid");
						id = jsons.getInt("id");
						ToastUtil.showShort(PersonalTopicActivity.this,
								"哈哈呼呼呼呼" + eid);
					} else if (FLAG_TOIPC.equals(FLAG)) {
						id = jsons.getInt("id");
						ToastUtil.showShort(PersonalTopicActivity.this,
								"哈哈呼呼呼呼" + id);
					}
					Intent intent = new Intent();
					intent.setClass(PersonalTopicActivity.this,
							TopicDetailActivity.class);
					intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
							false);
					intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
							true);
					if (FLAG_RESPONSE.equals(FLAG)) {
						intent.putExtra("eid", String.valueOf(eid));
						intent.putExtra("id", String.valueOf(id));
						intent.putExtra("which", "PersonalTopicActivity");
					} else if (FLAG_TOIPC.equals(FLAG)) {
						intent.putExtra("id", String.valueOf(id));
						intent.putExtra("which", "PersonalTopicActivity");
					}
					TopicGroupActivity.group.startiHaiGoActivity(intent,PersonalGroupActivity.group);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		List<JSONObject> newDatas = new ArrayList<JSONObject>();
		if (TopicDetailActivity.class.equals(parentClass)) {
			if(FLAG_RESPONSE.equals(FLAG)){
//				radapter.setDatas(newDatas);
				radapter.notifyDataSetChanged();
				getRespond(Constants.USER_ID, curpage, Constants.APP_DATA_LENGTH+"");
//				isResponseFirstClick = false;
				isFirstClick = false;
			}else if(FLAG_TOIPC.equals(FLAG)){
//				adapter.setDatas(newDatas);
				adapter.notifyDataSetChanged();
				getTopics(Constants.USER_ID, curpage, Constants.APP_DATA_LENGTH+"");
//				isFirstClick = false;
				isResponseFirstClick = false;
			}
			
			
		}else if(PersonalActivity.class.equals(parentClass)){
			if(FLAG_RESPONSE.equals(FLAG)){
				radapter.notifyDataSetChanged();
				getRespond(Constants.USER_ID, curpage, Constants.APP_DATA_LENGTH+"");
			}else if(FLAG_TOIPC.equals(FLAG)){
				adapter.setDatas(newDatas);
				adapter.notifyDataSetChanged();
				getTopics(Constants.USER_ID, curpage, Constants.APP_DATA_LENGTH+"");
			}
		}
		initVar();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ToastUtil.showShort(PersonalTopicActivity.this, "哈哈" + position);
	}

}
