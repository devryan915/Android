package com.kc.ihaigo.ui.topic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.EditDefaultCardInfo;
import com.kc.ihaigo.ui.personal.MyMessageActivity;
import com.kc.ihaigo.ui.personal.PersonalActivity;
import com.kc.ihaigo.ui.personal.PersonalConfirmSettingPassword;
import com.kc.ihaigo.ui.personal.PersonalFirstLogin;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.personal.PersonalLoginActivity;
import com.kc.ihaigo.ui.personal.PersonalTopicActivity;
import com.kc.ihaigo.ui.personal.PersonalTopicActivity.ShopcarBackCall;
import com.kc.ihaigo.ui.personal.PersonalUserLogin;
import com.kc.ihaigo.ui.personal.adapter.MyIdentityAdapter;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView.ScrollBottomListener;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.ui.topic.adpater.DetailResponseAdapter;
import com.kc.ihaigo.ui.topic.adpater.DetailResponseAdapter.ViewHolder;
import com.kc.ihaigo.ui.topic.adpater.DetailTopicImageAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.share.QQShare;
import com.kc.ihaigo.util.share.WeiXinShare;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.platformtools.Log;

/***
 * 话题详情
 * ******/
public class TopicDetailActivity extends IHaiGoActivity {

	private EditText myresponse;
	private String tid;
	private JSONArray json;
	private TextView detail_topic_title;
	private ImageView detail_user_image;
	private TextView detail_user_nickName;
	private TextView detail_user_introduce;
	private TextView detail_topic_createtime;
	private TextView detail_page_turning;
	private TextView detail_topic_content;
	private ImageView detail_topic_content_image;
	private PullUpRefreshListView topic_detail_content_ll;
	private View headView;
	private DetailResponseAdapter adapter;

	private int mCurrentPageNum;// 当前页
	private int mTotalPageNum;// 总页数
	private int count;// 从服务器返回的评论，回复的总条数

	private int curpage = 1;// 记录当前是页码
	private int transit_page = 1;
	private int loadNum = Constants.APP_DATA_LENGTH;
	private boolean isLoading = false;
	private boolean isLoadAll = false;// 是否加载全部数据;
	private int lastReqLength = 0;// 上一次请求到的数据长度

	private RelativeLayout page_turn_ll;// 翻页的整体布局文件
	private SeekBar seekbar_page;// 翻页时显示的seekbar
	private TextView end_page;// 最后页码
	// private String id;// 话题编码

	private boolean isOpen = true;
	private WrapListView image_list;// 图片的listView
	private DetailTopicImageAdapter imgAdapter;
	private TextView current_page;// 当前页控件
	private Class<IHaiGoActivity> lparentClass;
	private View mFooterView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_detail);
		initTitle();
		initComponents();
	}

	@Override
	public void refresh() {
		super.refresh();
		if (PersonalTopicActivity.class.equals(parentClass)
				|| TopicActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			tid = getIntent().getStringExtra("id");
			JSONArray newDatas = new JSONArray();
			adapter.setDatas(newDatas);
			adapter.notifyDataSetChanged();
			initVar();
			page_turn_ll.setVisibility(View.GONE);
			isOpen = true;
			detail(tid, String.valueOf(curpage));

		} else if (TopicResponse.class.equals(parentClass)) {
			if (PersonalTopicActivity.class.equals(lparentClass)) {
				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.personal.PersonalTopicActivity");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else if (TopicActivity.class.equals(parentClass)) {
				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.personal.TopicActivity");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			tid = getIntent().getStringExtra("id");
			JSONArray newDatas = new JSONArray();
			adapter.setDatas(newDatas);
			adapter.notifyDataSetChanged();
			initVar();
			page_turn_ll.setVisibility(View.GONE);
			isOpen = true;
			detail(tid, String.valueOf(curpage));
		}
		// else if (PersonalUserLogin.class.equals(parentClass)
		// || PersonalFirstLogin.class.equals(parentClass)
		// || PersonalConfirmSettingPassword.class.equals(parentClass)) {
		// try {
		// parentClass = (Class<IHaiGoActivity>) Class
		// .forName("com.kc.ihaigo.ui.personal.TopicActivity");
		// lparentClass = parentClass;
		// } catch (ClassNotFoundException e) {
		// e.printStackTrace();
		// }
		// tid = getIntent().getStringExtra("topicid");
		// JSONArray newDatas = new JSONArray();
		// adapter.setDatas(newDatas);
		// adapter.notifyDataSetChanged();
		// initVar();
		// page_turn_ll.setVisibility(View.GONE);
		// isOpen = true;
		// detail(tid, String.valueOf(curpage));
		// }

		myresponse.requestFocus();// 获取焦点
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		myresponse = (EditText) findViewById(R.id.detail_myresponse);
		myresponse.requestFocus();// 获取焦点
		myresponse.setOnClickListener(this);
		detail_page_turning = (TextView) findViewById(R.id.detail_page_turning);
		detail_page_turning.setOnClickListener(this);
		page_turn_ll = (RelativeLayout) findViewById(R.id.page_turn_ll);
		seekbar_page = (SeekBar) findViewById(R.id.seekbar_page);
		// seekbar_page.setMax(100);
		end_page = (TextView) findViewById(R.id.end_page);
		current_page = (TextView) findViewById(R.id.current_page);
		page_turn_ll.setVisibility(View.GONE);
	}

	/**
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */

	private void initComponents() {
		topic_detail_content_ll = (PullUpRefreshListView) findViewById(R.id.topic_detail_content_ll);
		headView = getLayoutInflater().inflate(
				R.layout.topic_detail_listview_header, null);
		topic_detail_content_ll.addHeaderView(headView);
		
		detail_topic_title = (TextView) headView
				.findViewById(R.id.detail_topic_title);
		detail_user_image = (ImageView) headView
				.findViewById(R.id.detail_user_image);
		detail_user_nickName = (TextView) headView
				.findViewById(R.id.detail_user_nickName);
		detail_user_introduce = (TextView) headView
				.findViewById(R.id.detail_user_introduce);
		detail_topic_createtime = (TextView) headView
				.findViewById(R.id.detail_topic_createtime);
		detail_topic_content = (TextView) headView
				.findViewById(R.id.detail_topic_content);
		WrapListView image_list = (WrapListView) headView
				.findViewById(R.id.image_list);
		imgAdapter = new DetailTopicImageAdapter(TopicDetailActivity.this);
		image_list.setAdapter(imgAdapter);

		adapter = new DetailResponseAdapter(TopicDetailActivity.this);
		adapter.setCall(new TopicBackCall());
		topic_detail_content_ll.setAdapter(adapter);
		topic_detail_content_ll
				.setScrollBottomListener(new ScrollBottomListener() {
					@Override
					public void deal() {
						if (!isLoading) {
							curpage++;
							if (!TextUtils.isEmpty(tid)) {
								detail(tid, String.valueOf(curpage));
							}

						}
					}
				});
		initVar();

		seekbar_page
				.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

	}

	private void initVar() {
		curpage = 1;// 记录当前是页码
		isLoading = false;
		isLoadAll = false;// 是否加载全部数据;
		lastReqLength = 0;// 上一次请求到的数据长度
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void back() {
		if (lparentClass == null) {
			try {
				lparentClass = (Class<IHaiGoActivity>) Class
						.forName("com.kc.ihaigo.ui.topic.TopicActivity");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			super.back();
		} else {
			parentClass = lparentClass;
			if (PersonalTopicActivity.class.equals(parentClass)) {

			} else if (TopicActivity.class.equals(parentClass)) {
				showTabHost = true;
			}
			super.back();
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {

		case R.id.detail_myresponse:

			ToastUtil.showShort(TopicDetailActivity.this, "点击啦！回复啦！");
			intent.setClass(TopicDetailActivity.this, TopicResponse.class);
			intent.putExtra("id", tid);
			intent.putExtra("type", "1");
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			// if (TopicActivity.class.equals(parentClass)) {
			// intent.putExtra("flag", "TopicActivity");
			// TopicGroupActivity.group.startiHaiGoActivity(intent);
			// Log.i("geek", parentClass.getName());
			// }else if(PersonalTopicActivity.class.equals(parentClass)){
			// intent.putExtra("flag", "PersonalTopicActivity");
			//
			// }
			TopicGroupActivity.group.startiHaiGoActivity(intent);

			break;
		case R.id.detail_page_turning:
			// ToastUtil.showShort(TopicDetailActivity.this, "点击啦！翻页啦！");
			//
			if (isOpen & (mTotalPageNum > 0)) {
				page_turn_ll.setVisibility(View.VISIBLE);
				isOpen = false;
			}
			// else if(mTotalPageNum <0 & isOpen){
			// ToastUtil.showShort(TopicDetailActivity.this, "当前页已是全部的数据");
			// page_turn_ll.setVisibility(View.GONE);
			// seekbar_page.setProgress(0);
			// isOpen = false;
			// }
			else if (!isOpen) {
				page_turn_ll.setVisibility(View.GONE);
				seekbar_page.setProgress(0);
				isOpen = true;
			} else {
				ToastUtil.showShort(TopicDetailActivity.this, "当前页已是全部的数据");
				page_turn_ll.setVisibility(View.GONE);
				seekbar_page.setProgress(0);
				isOpen = false;
			}
			break;
		case R.id.title_right:
			DialogUtil.showTopicShareDialog(
					TopicDetailActivity.this.getParent(), new BackCall() {

						@Override
						public void deal(int which, Object... obj) {
							String titleTopic = detail_topic_title.getText()
									.toString();
							WeiXinShare share = new WeiXinShare();
							switch (which) {
							case R.id.iv_share_weixin:
								// share.shareToWeiXinText(TopicDetailActivity.this,"IHaigo",titleTopic+"http://api.ihaigo.com/ihaigo");
								// share.shareToWeiXinImg(TopicDetailActivity.this);
								// share.shareToWeiXin(TopicDetailActivity.this);
								Bitmap bmp = BitmapFactory.decodeResource(
										getResources(),
										R.drawable.share_weixin_logo);
								share.sendReq(
										TopicDetailActivity.this,
										titleTopic
												+ "http://api.ihaigo.com/ihaigo",
										bmp);
								break;
							case R.id.iv_share_circle_of_friends:
								Bitmap bitmap = BitmapFactory.decodeResource(
										getResources(),
										R.drawable.share_weixin_logo);
								share.sendReqFriend(
										TopicDetailActivity.this,
										titleTopic
												+ "http://api.ihaigo.com/ihaigo",
										bitmap);
								break;
							case R.id.iv_share_qq:
								ToastUtil.showShort(TopicDetailActivity.this,
										"QQ分享");
								QQShare qqshare = new QQShare();
								qqshare.shareToQQ(TopicDetailActivity.this);
								break;
							case R.id.iv_share_sina:

								break;

							default:
								break;
							}
						}
					}, null);
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: detail
	 * @user: helen.yang
	 * @Description: 显示话题详情列表
	 * 
	 * @param id
	 *            void
	 * @throws
	 */
	private void detail(String id, String page) {
		isLoading = true;
		final String url = Constants.TOPIC_URL + id + "/show" + "?page="
				+ curpage + "&pagesize=" + Constants.APP_DATA_LENGTH;
		final Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							Log.i("geek",
									"**********************嘻嘻getRespondDetail"
											+ result);
							try {
								JSONObject resData = new JSONObject(result);
								JSONObject jsonObj = resData
										.getJSONObject("topic");
								
								if (jsonObj !=null && jsonObj.length()>0) {
									
								
								
								
								Log.i("topic", ":::::::::::::::西西topic"
										+ jsonObj.toString());
								detail_topic_title.setText(jsonObj
										.getString("title"));
								detail_user_nickName.setText(jsonObj
										.getString("nickName"));
								detail_user_introduce.setText(jsonObj
										.getString("introduce"));
								detail_topic_content.setText(jsonObj
										.getString("content"));
								String createtime = Utils.getCurrentTime(
										jsonObj.getLong("createTime"),
										"MM-dd  HH:mm");
								detail_topic_createtime.setText(createtime);
								String url = jsonObj.getString("headImage");
								ImageLoader
										.getInstance()
										.displayImage(
												url,
												((ImageView) headView
														.findViewById(R.id.detail_user_image)));

								JSONArray imagejsonArray = jsonObj
										.getJSONArray("image");
								imgAdapter.setDatas(imagejsonArray);
								imgAdapter.notifyDataSetChanged();
								Log.i("image",
										"image::::::::"
												+ imagejsonArray.length());

								/**
								 * 针对话题的评论、回复
								 */
								json = resData.getJSONArray("evaluationTopic");
								count = resData.getInt("count");
								if (count % loadNum == 0) {
									mTotalPageNum = count / loadNum;
								} else if (count % loadNum != 0) {
									mTotalPageNum = (count / loadNum) + 1;
								}
								end_page.setText(String.valueOf(mTotalPageNum));
								// adapter.setDatas(json);
								// adapter.notifyDataSetChanged();
								Log.i("topic",
										":::::::::::::::西西evaluationTopic"
												+ json.length());
								JSONArray oldDatas = adapter.getDatas();
								boolean isNeedRefresh = false;
								if (isLoadAll) {
									for (int i = lastReqLength; i < json
											.length(); i++) {
										oldDatas.put(json.getJSONObject(i));
										isNeedRefresh = true;
									}
								} else {
									for (int i = 0; i < json.length(); i++) {
										oldDatas.put(json.getJSONObject(i));
										isNeedRefresh = true;
									}
								}
								if (isNeedRefresh) {
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
								} else {
									isLoadAll = false;
								}
								isLoading = false;
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i("geek",
									"**********************哈哈getRespondDetail"
											+ result);
						}
					}
				}, 1);

	}

	/**
	 * @Title: getList
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */

	public void getListData(String id, String page) {
		final String url = Constants.TOPIC_URL + id + "/getList" + "?page="
				+ curpage + "&pagesize=" + Constants.APP_DATA_LENGTH;
		final Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							Log.i("geek", "**********************嘻嘻分页查询的评论回复数据"
									+ result);
							try {
								JSONObject resData = new JSONObject(result);
								JSONArray datas = resData
										.getJSONArray("evaluationTopic");
								Log.i("topic", ":::::::::::::::西西分页查询的评论回复数据"
										+ datas.toString() + datas.length());
								adapter.setDatas(datas);
								adapter.notifyDataSetChanged();

							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i("geek",
									"**********************哈哈getRespondDetail"
											+ result);
						}
					}
				}, 1);

	}

	/**
	 * 
	 * @ClassName: MyOnSeekBarChangeListener
	 * @Description: seekbar的事件处理
	 * @author: helen.yang
	 * @date: 2014年7月29日 下午6:49:13
	 * 
	 */
	class MyOnSeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			mCurrentPageNum = seekBar.getProgress()
					/ (seekBar.getMax() / mTotalPageNum) + 1;
			if (mCurrentPageNum > mTotalPageNum) {
				mCurrentPageNum = mTotalPageNum;
			}
			current_page.setText(String.valueOf(mCurrentPageNum) + "/"
					+ String.valueOf(mTotalPageNum));
			// ToastUtil.showShort(
			// TopicDetailActivity.this,
			// String.valueOf(mCurrentPageNum) + "/"
			// + String.valueOf(mTotalPageNum));

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			current_page.setVisibility(View.VISIBLE);
			// if (count % loadNum == 0) {
			// mTotalPageNum = count / loadNum;
			// } else if (count % loadNum != 0) {
			// mTotalPageNum = (count / loadNum) + 1;
			// }
			// end_page.setText(String.valueOf(mTotalPageNum));
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			current_page.setVisibility(View.GONE);
			getListData(tid, String.valueOf(mCurrentPageNum));
			// end_page.setText("");
			ToastUtil.showLocation(TopicDetailActivity.this, "加载成功");
		}

	}

	public class TopicBackCall extends BackCall {

		@Override
		public void deal(int which, Object... obj) {
			switch (which) {
			case R.id.btn_topic_response:
				ToastUtil.showShort(TopicDetailActivity.this, "点击话题回复");
				try {
					JSONObject jsonObj = (JSONObject) obj[0];
					Log.i("response", jsonObj.toString());

					Intent intent = new Intent();
					intent.setClass(TopicDetailActivity.this,
							TopicResponse.class);
					intent.putExtra("tid", tid);
					intent.putExtra("type", "2");
					intent.putExtra("pid", jsonObj.getString("id"));
					intent.putExtra("nickName", jsonObj.getString("nickName"));
					intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
							true);
					intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
							false);
					TopicGroupActivity.group.startiHaiGoActivity(intent);
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
