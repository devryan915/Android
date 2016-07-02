package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 发表评论
 * 
 * @author zouxianbin
 * 
 */
public class PersonalPublishEvaluationActivity extends IHaiGoActivity {

	private EditText content;
	private TextView title_right;

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

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	private Class<IHaiGoActivity> lparentClass;

	private IHaiGoGroupActivity lparentGroupActivity;
	private Bundle resParams;
	private String inTAG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_evaluation);
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
		// 定义返回功能
		findViewById(R.id.title_left).setOnClickListener(this);
		// 完成
		title_right = (TextView) findViewById(R.id.title_right);
		title_right.setOnClickListener(this);
		goodsImg = (ImageView) findViewById(R.id.goodsImg);
		goodsName_tv = (TextView) findViewById(R.id.goodsName_tv);
		originalPrice_tv = (TextView) findViewById(R.id.originalPrice_tv);
		actualPrice_tv = (TextView) findViewById(R.id.actualPrice_tv);
		ram_actualPrice_tv = (TextView) findViewById(R.id.ram_actualPrice_tv);
		goodsActualPrice = (TextView) findViewById(R.id.goodsActualPrice);
		rec_supply_img = (ImageView) findViewById(R.id.rec_supply_img);
		content = (EditText) findViewById(R.id.content);

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

			// 恢复数据
			// 恢复内容
			// 来源
			imageLoader.displayImage(source, rec_supply_img, options,
					animateFirstListener);
			imageLoader.displayImage(icon, goodsImg, options,
					animateFirstListener);
			// 价格
			originalPrice_tv.setText(pri);
			actualPrice_tv.setText(price_disc);
			goodsActualPrice.setText(Ram);

			// 商品名称
			goodsName_tv.setText(name);

			ram_actualPrice_tv.setText(curRate);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:

			break;
		case R.id.title_right:
			String cont = content.getText().toString().trim();
			Utils.hideInputMethod(PersonalPublishEvaluationActivity.this);
			if (!TextUtils.isEmpty(cont)) {
				if (checkLogin()) {
					getDatas(gid, cont);
				} else {
					Intent nt = new Intent(
							PersonalPublishEvaluationActivity.this,
							PersonalLoginActivity.class);
					nt.putExtra("flag", "PersonalPublishEvaluationActivity");
					nt.putExtra("code", "");
					nt.putExtra("tag", inTAG);
					nt.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
					nt.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
					lparentGroupActivity.startiHaiGoActivity(nt);
				}

			} else {
				Toast.makeText(PersonalPublishEvaluationActivity.this,
						"请输入评价内容", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	// 发表请求
	private void getDatas(String goodId, String content) {
		String url = Constants.REC_GOODS_INSERT;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("goodId", goodId);
		map.put("content", content);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						Log.e("map", map.toString());
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject json = new JSONObject(result);
								String code = json.getString("code");
								if (!TextUtils.isEmpty(code)) {
									if ("1".equals(code)) {
										back();
									} else if ("0".equals(code)) {
										Toast.makeText(
												PersonalPublishEvaluationActivity.this,
												"失败", Toast.LENGTH_SHORT)
												.show();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	@Override
	public void refresh() {
		super.refresh();
		content.setText("");
		resParams = getIntent().getExtras();
		if (PersonalGoodsDetailsActivity.class.equals(parentClass)) {
			lparentGroupActivity = parentGroupActivity;
			lparentClass = parentClass;
			inTAG = "goods";

			// 来源
			source = resParams.getString("source");
			Ram = resParams.getString("Ram");

			// 商品Id
			gid = resParams.getString("gid");
			// 商品名称
			name = resParams.getString("name");
			// 旧美元
			pri = resParams.getString("pri");
			rid = resParams.getInt("rid");
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

			// 来源
			imageLoader.displayImage(source, rec_supply_img, options,
					animateFirstListener);
			imageLoader.displayImage(icon, goodsImg, options,
					animateFirstListener);
			// 价格
			originalPrice_tv.setText(pri);
			actualPrice_tv.setText(price_disc);
			goodsActualPrice.setText(Ram);

			// 商品名称
			goodsName_tv.setText(name);

			ram_actualPrice_tv.setText(curRate);
		} else if (PersonalLookEvaluationActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			inTAG = "look";

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

			// 来源
			imageLoader.displayImage(source, rec_supply_img, options,
					animateFirstListener);
			imageLoader.displayImage(icon, goodsImg, options,
					animateFirstListener);
			// 价格
			originalPrice_tv.setText(pri);
			actualPrice_tv.setText(price_disc);
			goodsActualPrice.setText(Ram);

			// 商品名称
			goodsName_tv.setText(name);

			ram_actualPrice_tv.setText(curRate);

		} else if (PersonalUserLogin.class.equals(parentClass)) {
			inTAG = getIntent().getStringExtra("tag");
			if (inTAG.equals("goods")) {
				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.personal.PersonalGoodsDetailsActivity");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (inTAG.equals("look")) {
				try {
					parentClass = (Class<IHaiGoActivity>) Class
							.forName("com.kc.ihaigo.ui.personal.PersonalLookEvaluationActivity");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	protected void back() {
		// TODO Auto-generated method stub
		super.back();
		parentGroupActivity = lparentGroupActivity;
		showTabHost = false;
		refreshActivity = true;
		if (PersonalUserLogin.class.equals(parentClass)) {

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
		resParams.putString("lparentClass", lparentClass + "");
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

		// 恢复内容
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

		super.onResume();
	}
}
