/**
 * @Title: TopicActivity.java
 * @Package: com.kc.ihaigo.ui.topic
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月25日 上午11:02:18

 * @version V1.0

 */

package com.kc.ihaigo.ui.topic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.selfwidget.SyncHorizontalScrollView;
import com.kc.ihaigo.ui.topic.adpater.TopicContentAdapter;
import com.kc.ihaigo.ui.topic.adpater.TopicPagerAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: TopicActivity
 * @Description: 话题主页
 * @author: ryan.wang
 * @date: 2014年7月25日 上午11:02:18
 * 
 */

public class TopicActivity extends IHaiGoActivity implements OnClickListener {
	private final String TAG = "TopicActivity";
	private RelativeLayout rl_nav;
	private SyncHorizontalScrollView mHsv;
	private RadioGroup rg_nav_content;
	private ImageView iv_nav_indicator;
	private ImageView iv_nav_left;
	private ImageView iv_nav_right;
	private ViewPager topic_mainview;
	private int indicatorWidth;
	private LayoutInflater mInflater;
	private TopicPagerAdapter mAdapter;
	private int currentIndicatorLeft = 0;

	public static final String TOPIC_TYPE = "type";
	public static final int GONGLUE = 1;
	public static final int JIAOLIU = 2;
	public static final int SHAIDAN = 3;
	public static final int ZHUANRANG = 4;
	public static final int ZHUANYUN = 5;

	private PullUpRefreshListView topicContent;
	private Integer curpage = 1;// 记录当前是页码
	private Boolean isLoading = false;
	private Boolean isLoadAll = false;// 是否加载全部数据;
	private Integer lastReqLength = 0;// 上一次请求到的数据长度
	private TopicContentAdapter contentAdapter;
	private int topictype = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		initcomponets();
	}

	@Override
	public void refresh() {
		int id = rg_nav_content.getCheckedRadioButtonId();
		Bundle params = getIntent().getExtras();
		if (params != null) {
			String topicType = params.getString("TOPIC_TYPE");
			if (!TextUtils.isEmpty(topicType)) {
				int type = Integer.parseInt(topicType) - 1;
				if (type == id) {
					rg_nav_content.check(type);
					if (rg_nav_content.getChildAt(type) != null) {
						TranslateAnimation animation = new TranslateAnimation(
								currentIndicatorLeft,
								((RadioButton) rg_nav_content.getChildAt(type))
										.getLeft(), 0f, 0f);
						animation.setInterpolator(new LinearInterpolator());
						animation.setDuration(100);
						animation.setFillAfter(true);
						// 执行位移动画
						iv_nav_indicator.startAnimation(animation);
						topic_mainview.setCurrentItem(type); // ViewPager
																// 跟随一起
																// 切换

						// 记录当前 下标的距最左侧的 距离
						currentIndicatorLeft = ((RadioButton) rg_nav_content
								.getChildAt(type)).getLeft();
						mHsv.smoothScrollTo(
								(type > 1 ? ((RadioButton) rg_nav_content
										.getChildAt(type)).getLeft() : 0)
										- ((RadioButton) rg_nav_content
												.getChildAt(2)).getLeft(), 0);
					}
					topicContent = (PullUpRefreshListView) mAdapter.getViews()
							.get(type);
					contentAdapter = new TopicContentAdapter(TopicActivity.this);
					topicContent.setAdapter(contentAdapter);
					topicContent
							.setScrollBottomListener(new ScrollBottomListener() {
								@Override
								public void deal() {
									if (!isLoading) {
										curpage++;
										loadTopics();
									}
								}
							});
					topicContent
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									Intent intent = new Intent(
											TopicActivity.this,
											TopicDetailActivity.class);
									intent.putExtra("id", id + "");
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									TopicGroupActivity.group
											.startiHaiGoActivity(intent);
								}
							});
					initVar();
					loadTopics();
				} else {
					rg_nav_content.check(type);
				}
			}
		}
	}
	private void initcomponets() {
		rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
		mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
		rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
		iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
		iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);
		topic_mainview = (ViewPager) findViewById(R.id.topic_mainview);
		indicatorWidth = Utils.getScreenWidth(TopicActivity.this) / 4;
		LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
		cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
		iv_nav_indicator.setLayoutParams(cursor_Params);
		mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);
		// 获取布局填充器
		mInflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		// 另一种方式获取
		// LayoutInflater mInflater = LayoutInflater.from(this);
		String[] tabTitle = getResources().getStringArray(R.array.topic_titles);
		rg_nav_content.removeAllViews();
		for (int i = 0; i < tabTitle.length; i++) {
			RadioButton rb = (RadioButton) mInflater.inflate(
					R.layout.nav_radiogroup_item, null);
			rb.setId(i);
			rb.setText(tabTitle[i]);
			rb.setLayoutParams(new LayoutParams(indicatorWidth,
					LayoutParams.MATCH_PARENT));
			rg_nav_content.addView(rb);
		}
		// topic_mainview.setOffscreenPageLimit(0);
		mAdapter = new TopicPagerAdapter();
		mAdapter.setTitles(tabTitle);
		List<View> views = new ArrayList<View>();
		PullUpRefreshListView content = (PullUpRefreshListView) mInflater
				.inflate(R.layout.activity_topic_content, null);
		views.add(content);
		content = (PullUpRefreshListView) mInflater.inflate(
				R.layout.activity_topic_content, null);
		views.add(content);
		content = (PullUpRefreshListView) mInflater.inflate(
				R.layout.activity_topic_content, null);
		views.add(content);
		content = (PullUpRefreshListView) mInflater.inflate(
				R.layout.activity_topic_content, null);
		views.add(content);
		content = (PullUpRefreshListView) mInflater.inflate(
				R.layout.activity_topic_content, null);
		views.add(content);
		mAdapter.setViews(views);
		topic_mainview.setAdapter(mAdapter);
		topic_mainview.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (rg_nav_content != null
						&& rg_nav_content.getChildCount() > position) {
					((RadioButton) rg_nav_content.getChildAt(position))
							.performClick();
					switch (position) {
						case 0 :
							topictype = GONGLUE;
							break;
						case 1 :
							topictype = JIAOLIU;
							break;
						case 2 :
							topictype = SHAIDAN;
							break;
						case 3 :
							topictype = ZHUANRANG;
							break;
						case 4 :
							topictype = ZHUANYUN;
							break;

						default :
							break;
					}
					topicContent = (PullUpRefreshListView) mAdapter.getViews()
							.get(position);
					contentAdapter = new TopicContentAdapter(TopicActivity.this);
					topicContent.setAdapter(contentAdapter);
					topicContent
							.setScrollBottomListener(new ScrollBottomListener() {
								@Override
								public void deal() {
									if (!isLoading) {
										curpage++;
										loadTopics();
									}
								}
							});
					topicContent
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									Intent intent = new Intent(
											TopicActivity.this,
											TopicDetailActivity.class);
									intent.putExtra("id", id + "");
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									TopicGroupActivity.group
											.startiHaiGoActivity(intent);
								}
							});
					initVar();
					loadTopics();
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		rg_nav_content
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (rg_nav_content.getChildAt(checkedId) != null) {
							TranslateAnimation animation = new TranslateAnimation(
									currentIndicatorLeft,
									((RadioButton) rg_nav_content
											.getChildAt(checkedId)).getLeft(),
									0f, 0f);
							animation.setInterpolator(new LinearInterpolator());
							animation.setDuration(100);
							animation.setFillAfter(true);
							// 执行位移动画
							iv_nav_indicator.startAnimation(animation);
							topic_mainview.setCurrentItem(checkedId); // ViewPager
																		// 跟随一起
																		// 切换

							// 记录当前 下标的距最左侧的 距离
							currentIndicatorLeft = ((RadioButton) rg_nav_content
									.getChildAt(checkedId)).getLeft();
							mHsv.smoothScrollTo(
									(checkedId > 1
											? ((RadioButton) rg_nav_content
													.getChildAt(checkedId))
													.getLeft() : 0)
											- ((RadioButton) rg_nav_content
													.getChildAt(2)).getLeft(),
									0);
							// ((TopicContentFragment)
							// mAdapter.getItem(checkedId))
							// .loadTopics();
						}
					}
				});
		rg_nav_content.check(GONGLUE - 1);
		findViewById(R.id.title_right).setOnClickListener(this);
	}

	private void initVar() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
	}

	/**
	 * 
	 * @Title: loadTopics
	 * @user: ryan.wang
	 * @Description:
	 * 
	 * @param type
	 * @param page
	 *            void
	 * @throws
	 */
	public void loadTopics() {
		isLoading = true;
		String url = Constants.TOPICS_URL + "?type=" + topictype + "&page="
				+ curpage + "&pagesize=" + Constants.APP_DATA_LENGTH;
		if (Constants.Debug) {
			Log.d(TAG, "请求第" + curpage + "页");
		}
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								JSONArray datas = resData.getJSONArray("topic");

								if (datas != null && datas.length() > 0) {

									JSONArray oldDatas = contentAdapter
											.getDatas();
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
										contentAdapter.notifyDataSetChanged();
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
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.title_right :
				intent = new Intent(TopicActivity.this,
						TopicPublishActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				TopicGroupActivity.group.startiHaiGoActivity(intent);
				break;

			default :
				break;
		}
	}
}
