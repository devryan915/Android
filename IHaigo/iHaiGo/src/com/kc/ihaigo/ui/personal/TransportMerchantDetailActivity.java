package com.kc.ihaigo.ui.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.text.ClipboardManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalEditUserInfo;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.personal.WebViewActivity;
import com.kc.ihaigo.ui.personal.adapter.TransportCommetsAdapter;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.share.QQShare;
import com.kc.ihaigo.util.share.WeiXinShare;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/***
 * 转运商详情
 * **/
public class TransportMerchantDetailActivity extends IHaiGoActivity implements
		OnClickListener {

	/**
	 * 商品ICOM
	 */
	private ImageView puragent_head;
	/**
	 * 名称
	 */
	private TextView merchant_name;
	/**
	 * 转运
	 */
	private TextView transfer;
	/**
	 * 个性签名
	 */
	private TextView pruagent_creditlevel;
	/**
	 * 收费
	 */
	private TextView puragent_feeval;
	/**
	 * 物流
	 */
	private TextView puragent_shippingval;
	/**
	 * 服务
	 */
	private TextView puragent_serviceval;
	/**
	 * 点击查看地址
	 */
	private TextView button;
	/**
	 * 手机
	 */
	private TextView phoo;
	/**
	 * 链接
	 */
	private TextView url;
	/**
	 * QQ
	 */
	private TextView pst_qq;

	/**
	 * 邮箱
	 */
	private TextView emile;
	/**
	 * 说明
	 */
	private TextView state;
	/**
	 * 介绍
	 */
	private TextView introduce;
//	/**
//	 * 活动
//	 */
//	private TextView event1;
//	/**
//	 * 活动
//	 */
//	private TextView event2;
//	/**
//	 * 活动
//	 */
//	private TextView event3;

	/**
	 * 评价
	 */
	private WrapListView userAppraise;
	/**
	 * 条数
	 */

	private TextView more_appraise;
	/**
	 * 评价BUT
	 */
	private LinearLayout lookEvalutation;
	private Dialog qqdialog;
	private Dialog phoodialog;
	private Dialog urldialog;
	private Dialog maildialog;

	/**
	 * 电话号码
	 */
	private String phoneno;
	private String QQ;
	private String Mail;
	private String urL;

	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	/**
	 * 转动ID
	 */
	private String cid;
	private TransportCommetsAdapter adapter;
	private String transId;
	public Class<IHaiGoActivity> lparentClass;
	public IHaiGoGroupActivity lparentGroupActivity;
	/**
	 * 评论总条数
	 */
	private String count;
	/**
	 * 点击查看等多收费说明
	 */
	private TextView state_intro_more;
	/**
	 * 转运公司名字
	 */
	private TextView title_middle;
	/**
	 * 收费说明的数据
	 */
	private JSONArray chargejsonArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_merchant_detail);
		initView();
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
		merchant_name = (TextView) findViewById(R.id.merchant_name);
		transfer = (TextView) findViewById(R.id.transfer);
		pruagent_creditlevel = (TextView) findViewById(R.id.pruagent_creditlevel);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		button = (TextView) findViewById(R.id.button);
		button.setOnClickListener(this);
		phoo = (TextView) findViewById(R.id.phoo);
		url = (TextView) findViewById(R.id.url);
		pst_qq = (TextView) findViewById(R.id.pst_qq);

		emile = (TextView) findViewById(R.id.emile);
		state = (TextView) findViewById(R.id.state);
		state_intro_more = (TextView) findViewById(R.id.state_intro);
		state_intro_more.setOnClickListener(this);
		state.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
		introduce = (TextView) findViewById(R.id.introduce);
//		event1 = (TextView) findViewById(R.id.event1);
//		event2 = (TextView) findViewById(R.id.event2);
//		event3 = (TextView) findViewById(R.id.event3);
		more_appraise = (TextView) findViewById(R.id.more_appraise);
		lookEvalutation = (LinearLayout) findViewById(R.id.lookEvalutation);
		userAppraise = (WrapListView) findViewById(R.id.userAppraise);
		
		Image();
		// getTransport(cid);
		
		OnClick();
	}

	@Override
	public void refresh() {
		super.refresh();
		if (QualityTransportActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			transId = getIntent().getStringExtra("transId");
			getTransport();
			getTrans();
			adapter = new TransportCommetsAdapter(this);
			userAppraise.setAdapter(adapter);
		
		}
	}

	
	@Override
	protected void back() {
		parentClass = lparentClass;
		super.back();
	}

	private void OnClick() {
		emile.setOnClickListener(this);
		pst_qq.setOnClickListener(this);
		url.setOnClickListener(this);
		phoo.setOnClickListener(this);
		lookEvalutation.setOnClickListener(this);
		button.setOnClickListener(this);
	}



	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		title_middle = (TextView) findViewById(R.id.title_middle);
	}

	private void Image() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	/**
	 * 转动商详情
	 * 
	 * @param cid
	 */

	private void getTransport() {
		DataConfig da = new DataConfig(TransportMerchantDetailActivity.this);
//		String Tcompany = da.getTcompanySty(cid);
		String Tcompany = da.getTcompany();
		JSONArray com;
		try {
			JSONObject resData = new JSONObject(Tcompany);
			com = resData.getJSONArray("company");
			Log.i("geek", "转运公司"+com.toString());
			if (com != null && com.length() > 0) {
				for (int i = 0; i < com.length(); i++) {
					String id = com.getJSONObject(i).getString("id");
					String name = com.getJSONObject(i).getString("name");
					String icon = com.getJSONObject(i).getString("icon");
					String signature = com.getJSONObject(i).getString(
							"signature");
					String open = com.getJSONObject(i).getString("open");
					String charge = com.getJSONObject(i).getString("charge");
					String logis = com.getJSONObject(i).getString("logistics");
					String service = com.getJSONObject(i).getString("service");
					
					chargejsonArray = com.getJSONObject(i).getJSONArray("channels");
					Log.i("geek", "收费说明"+chargejsonArray.toString());
					String chargename = chargejsonArray.getJSONObject(0).getString("name");
					String instruction = chargejsonArray.getJSONObject(0).getString("instruction");
					String substring = instruction.substring(0, 112);
					Log.i("geek", "设置A扣"+chargejsonArray.getJSONObject(0).toString()+chargename);
					JSONArray addrjsonArray = com.getJSONObject(i).getJSONArray("addresses");
					if (!TextUtils.isEmpty(icon)) {
						imageLoader.displayImage(icon, puragent_head, options,
								animateFirstListener);
					}
					title_middle.setText(name);
					merchant_name.setText(name);
					puragent_shippingval.setText(logis);
					pruagent_creditlevel.setText(signature);
					puragent_serviceval.setText(service);
					puragent_feeval.setText(charge);
					transfer.setText(open);
					state.setText(chargename+substring);
					/**
					 * 
					 * "addresses": [ { "id": 3, "name":
					 * "美国加利福尼亚州洛杉矶(CA)XIANFENGEX仓库", "firstName": "",
					 * "lastName": "NRTC", "address": "245 South 8th Ave",
					 * "unit": "38390", "city": "La Puente/City of industry",
					 * "state": "CA", "zipCode": "91746", "tel": "626-538-8583",
					 * "remark":
					 * "友情提醒： 请大家在购物网站下单时一定记得写上姓后面的4个字母专有识别码和单元号,单元号可以写在第一行地址后面也可以写在地址第二行.(如果网站不认5位数字编号可以不用添加，但4位英文专有码要加上）"
					 * , "currency": "USD", "rate": 6.5}]
					 */

				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 用户评价
	 */
	private void getTrans() {
//		String httpUrl = Constants.TRANS_INFO + "?id=" + transId + "&page=" + 1
//				+ "&pagesize=" + Constants.APP_DATA_LENGTH;
		String httpUrl = "http://192.168.1.3:8080/transports/company/1?user=1";
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, httpUrl, null,
				new HttpReqCallBack() {
					
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject data = new JSONObject(result);
								Log.i("geek", data.toString());
								phoneno = data.getString("phone");
								urL =data.getString("website");
								Mail = data.getString("email");
								QQ = data.getString("qq");
								phoo.setText(phoneno);
								emile.setText(Mail);
								url.setText(urL);
								pst_qq.setText(QQ);
								introduce.setText(data.getString("description"));
								JSONObject jsonObject = data.getJSONObject("comments");
								count = jsonObject.getString("count");
								more_appraise.setText(count);
								JSONArray jsonArray = jsonObject.getJSONArray("items");
								if((Integer.parseInt(count))>3){
									adapter.setDatas(jsonArray);
									adapter.notifyDataSetChanged();
								}
								
//								for (int i = 0; i < jsonArray
//										.length(); i++) {
//									// 昵称
//									String nickName = jsonArray
//											.getJSONObject(i).getString(
//													"nickName");
//									// 介绍
//									String introduce = jsonArray
//											.getJSONObject(i).getString(
//													"introduce");
//									// 图像
//									String headPortrait = jsonArray
//											.getJSONObject(i).getString(
//													"headPortrait");
//									// 评价内容
//									String content = jsonArray
//											.getJSONObject(i).getString(
//													"content");
//									// 收费标准 1、1星 2、2星 3、3星 4、4星 4、5星
//									String charge = jsonArray
//											.getJSONObject(i).getString(
//													"charge");
//									// 物流速度 1、1星 2、2星 3、3星 4、4星 4、5星
//									String service = jsonArray
//											.getJSONObject(i).getString(
//													"service");
//									// 服务态度 1、1星 2、2星 3、3星 4、4星 4、5星
//									String logistic = jsonArray
//											.getJSONObject(i).getString(
//													"logistics");
//									// 服务态度 1、1星 2、2星 3、3星 4、4星 4、5星
//									String createTime = jsonArray
//											.getJSONObject(i).getString(
//													"createTime");
//
//								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		// case R.id.title_left:
		// intent.setClass(TransportMerchantDetailActivity.this,
		// QualityTransportActivity.class);
		// intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
		// PersonalGroupActivity.group.startiHaiGoActivity(intent);
		// break;
		case R.id.title_right:// 分享
			DialogUtil.showTopicShareDialog(
					TransportMerchantDetailActivity.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int which, Object... obj) {
							String titleTopic = "IHaiGo..."// 分享内容
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
										TransportMerchantDetailActivity.this,
										titleTopic
												+ "http://api.ihaigo.com/ihaigo",
										bmp);
								
								break;
							case R.id.iv_share_circle_of_friends:
								Bitmap bitmap = BitmapFactory.decodeResource(
										getResources(),
										R.drawable.share_weixin_logo);
								share.sendReqFriend(
										TransportMerchantDetailActivity.this,
										titleTopic
												+ "http://api.ihaigo.com/ihaigo",
										bitmap);
								break;
							case R.id.iv_share_qq:
								ToastUtil.showShort(
										TransportMerchantDetailActivity.this,
										"QQ分享");
								QQShare qqshare = new QQShare();
								qqshare.shareToQQ(TransportMerchantDetailActivity.this);
								break;
							case R.id.iv_share_sina:

								break;

							default:
								break;
							}
						}
					}, null);
			break;
		case R.id.url:// 打开网页
			urldialog = DialogUtil.showUrlDialog(
					TransportMerchantDetailActivity.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_url:

								String link = "";
								Intent in = new Intent(
										TransportMerchantDetailActivity.this,
										WebViewTransActivity.class);
								Bundle du = new Bundle();
								du.putString("link", link);
								in.putExtras(du);
								in.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
										false);
								in.putExtra(
										IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
										true);
								startActivity(in);

								((Dialog) obj[0]).dismiss();

								break;
							case R.id.dialog_user_Album:
								((Dialog) obj[0]).dismiss();

								break;
							default:
								break;
							}
						}
					}, urL);
			break;
		case R.id.emile:// 邮箱
			maildialog = DialogUtil.showMailDialog(
					TransportMerchantDetailActivity.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_mail:
								((Dialog) obj[0]).dismiss();

								break;
							case R.id.dialog_user_Album:
								((Dialog) obj[0]).dismiss();

								break;
							default:
								break;
							}
						}
					}, Mail);
			break;
		case R.id.pst_qq:// QQ
			DialogUtil.showQQDialog(
					TransportMerchantDetailActivity.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_qq:
								String Q = obj[1].toString();
								ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);// 把文字扔给剪贴板
								cmb.setText(Q);
								try {
									Intent intent = new Intent(Intent.ACTION_MAIN);
									ComponentName componentName = new ComponentName(
											"com.tencent.mobileqq",// 包名
											"com.tencent.mobileqq.activity.SplashActivity");// 路径
									intent.setComponent(componentName);
									startActivity(intent);
								} catch (Exception e) {

									Toast.makeText(
											TransportMerchantDetailActivity.this
													.getParent(), "手机未安装QQ",
											Toast.LENGTH_LONG).show();
								}

								((Dialog) obj[0]).dismiss();

								break;
							case R.id.dialog_user_Album:
								((Dialog) obj[0]).dismiss();

								break;
							default:
								break;
							}
						}
					}, QQ);
			break;
		case R.id.phoo:// 打开网页
			phoodialog = DialogUtil.showPhotoDialog(
					TransportMerchantDetailActivity.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_phoo:

//								Intent intent = new Intent(Intent.ACTION_CALL, Uri
//										.parse("tel:" + phoneno));
//								startActivity(intent);
								 Intent intent = new Intent();
								 intent.setAction("android.intent.action.DIAL");
								 intent.setData(Uri.parse("tel:" + phoneno));
								 startActivity(intent);
								((Dialog) obj[0]).dismiss();

								break;
							case R.id.dialog_user_Album:
								((Dialog) obj[0]).dismiss();

								break;
							default:
								break;
							}
						}
					}, phoneno);
			break;
		case R.id.lookEvalutation:
			intent.setClass(TransportMerchantDetailActivity.this,
					TransportAppraiseActivity.class);
			intent.putExtra("transId", transId);
			intent.putExtra("count", count);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			lparentClass = parentClass;
			lparentGroupActivity.startiHaiGoActivity(intent);
			break;
		case R.id.state_intro:
			intent.setClass(TransportMerchantDetailActivity.this, TransportChargeIntro.class);
			intent.putExtra("charge", chargejsonArray.toString());
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.button:
			intent.setClass(TransportMerchantDetailActivity.this, TransportAddress.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		default:
			break;
		}

	}
}
