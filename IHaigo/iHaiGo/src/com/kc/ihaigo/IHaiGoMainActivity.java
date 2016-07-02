package com.kc.ihaigo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.recommend.RecommendGroupActiviy;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;
import com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;

/**
 * 
 * @ClassName: IHaiGoMainActivity
 * @Description: 程序主入口，Tab主页面
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:45:15
 * 
 */
@SuppressWarnings("deprecation")
public class IHaiGoMainActivity extends TabActivity
		implements
			OnCheckedChangeListener {
	public static IHaiGoMainActivity main;
	private TabHost mHost;
	public RadioGroup radioderGroup;
	public static View tab_content;
	public static final String FLAG_DISPLAYTABHOST = "1";
	public static final String FLAG_REFRESHACTIVITY = "2";
	/**
	 * 精选首页
	 */
	public static final int REC = 1;
	/**
	 * 话题
	 */
	public static final int TOPIC = 2;
	/**
	 * 购物车
	 */
	public static final int SHOPCART = 3;
	/**
	 * 物流
	 */
	public static final int SHIPPING = 4;
	/**
	 * 个人中心
	 */
	public static final int PERSONAL = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ihai_go_main);
		tab_content = findViewById(R.id.tab_content);
		radioderGroup = (RadioGroup) findViewById(R.id.main_radio);
		radioderGroup.setOnCheckedChangeListener(this);
		Intent recIntent = new Intent(this, RecommendGroupActiviy.class);
		Intent topicIntent = new Intent(this, TopicGroupActivity.class);
		Intent shopcarIntent = new Intent(this, ShopCarGroupActiviy.class);
		Intent shippingIntent = new Intent(this, ShippingGroupActiviy.class);
		Intent personIntent = new Intent(this, PersonalGroupActivity.class);
		mHost = getTabHost();
		TabSpec rec = mHost.newTabSpec(REC + "").setIndicator(REC + "")
				.setContent(recIntent);

		TabSpec topic = mHost.newTabSpec(TOPIC + "").setIndicator(TOPIC + "")
				.setContent(topicIntent);
		TabSpec shopcart = mHost.newTabSpec(SHOPCART + "")
				.setIndicator(SHOPCART + "").setContent(shopcarIntent);
		TabSpec shipping = mHost.newTabSpec(SHIPPING + "")
				.setIndicator(SHIPPING + "").setContent(shippingIntent);
		TabSpec personal = mHost.newTabSpec(PERSONAL + "")
				.setIndicator(PERSONAL + "").setContent(personIntent);

		mHost.addTab(rec);
		mHost.addTab(topic);
		mHost.addTab(shopcart);
		mHost.addTab(shipping);
		mHost.addTab(personal);
		mHost.setCurrentTab(1);
		mHost.setCurrentTab(2);
		mHost.setCurrentTab(3);
		mHost.setCurrentTab(4);
		mHost.setCurrentTab(0);
		main = this;
	}

	public void setCurrentTab(int tab) {
		switch (tab) {
			case REC :
				mHost.setCurrentTab(0);
				radioderGroup.check(R.id.tab_rd_rec);
				break;
			case TOPIC :
				mHost.setCurrentTab(1);
				radioderGroup.check(R.id.tab_rd_topic);
				break;
			case SHOPCART :
				mHost.setCurrentTab(2);
				radioderGroup.check(R.id.tab_rd_shopcar);
				break;
			case SHIPPING :
				mHost.setCurrentTab(3);
				radioderGroup.check(R.id.tab_rd_shipping);
				break;
			case PERSONAL :
				mHost.setCurrentTab(4);
				radioderGroup.check(R.id.tab_rd_personal);
				break;
			default :
				break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
		switch (checkedId) {
			case R.id.tab_rd_rec :
				mHost.setCurrentTabByTag(REC + "");
				break;
			case R.id.tab_rd_topic :
				mHost.setCurrentTabByTag(TOPIC + "");
				break;
			case R.id.tab_rd_shopcar :
				mHost.setCurrentTabByTag(SHOPCART + "");
				break;
			case R.id.tab_rd_shipping :
				mHost.setCurrentTabByTag(SHIPPING + "");
				break;
			case R.id.tab_rd_personal :
				mHost.setCurrentTabByTag(PERSONAL + "");
				break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		((IHaiGoActivity) this.getCurrentActivity()).dealDatas(requestCode,
				resultCode, data);
	}
}
