package com.kc.ihaigo.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.R.string;
import com.kc.ihaigo.ui.shipping.AddShippingActivity;
import com.kc.ihaigo.ui.shipping.ShippingActivity;
import com.kc.ihaigo.ui.shipping.ShippingGroupActiviy;

/**
 * 个人中心--我的物流----全部订单
 * 
 * @author zonxianbin
 * 
 */
public class AllWayBillActivity extends IHaiGoActivity implements
		OnClickListener {
	private RelativeLayout indent_all;
	private TextView indent_all_tv;
	private ImageView indent_all_bg;
	private RelativeLayout one_month;
	private TextView one_month_tv;
	private ImageView one_month_bg;
	private RelativeLayout three_month;
	private TextView three_month_tv;
	private ImageView three_month_bg;
	private RelativeLayout six_month;
	private TextView six_month_tv;
	private ImageView six_month_bg;

	private String TAG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_way_bill);
		initTitle();
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		indent_all = (RelativeLayout) findViewById(R.id.indent_all);
		indent_all_tv = (TextView) findViewById(R.id.indent_all_tv);
		indent_all_bg = (ImageView) findViewById(R.id.indent_all_bg);
		one_month = (RelativeLayout) findViewById(R.id.one_month);
		one_month_tv = (TextView) findViewById(R.id.one_month_tv);
		one_month_bg = (ImageView) findViewById(R.id.one_month_bg);
		three_month = (RelativeLayout) findViewById(R.id.three_month);
		three_month_tv = (TextView) findViewById(R.id.three_month_tv);
		three_month_bg = (ImageView) findViewById(R.id.three_month_bg);
		six_month = (RelativeLayout) findViewById(R.id.six_month);
		six_month_tv = (TextView) findViewById(R.id.six_month_tv);
		six_month_bg = (ImageView) findViewById(R.id.six_month_bg);
		indent_all.setOnClickListener(this);
		one_month.setOnClickListener(this);
		three_month.setOnClickListener(this);
		six_month.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.indent_all:

			setBackground(indent_all_tv.getText().toString().trim());
			Intent indent = new Intent(AllWayBillActivity.this,
					PersonalLogisticsActivity.class);
			Bundle bun = new Bundle();
			bun.putString("String", indent_all_tv.getText().toString().trim());
			bun.putString("type", "0");
			indent.putExtras(bun);
			indent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			indent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(indent);
			break;
		case R.id.one_month:

			setBackground(one_month_tv.getText().toString().trim());
			Intent one = new Intent(AllWayBillActivity.this,
					PersonalLogisticsActivity.class);
			Bundle bune = new Bundle();
			bune.putString("String", one_month_tv.getText().toString().trim());
			bune.putString("type", "0");
			one.putExtras(bune);
			one.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			one.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(one);
			break;
		case R.id.three_month:

			setBackground(three_month_tv.getText().toString().trim());
			Intent three = new Intent(AllWayBillActivity.this,
					PersonalLogisticsActivity.class);
			Bundle buner = new Bundle();
			buner.putString("String", three_month_tv.getText().toString()
					.trim());
			buner.putString("type", "0");
			three.putExtras(buner);
			three.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			three.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(three);
			break;
		case R.id.six_month:

			setBackground(six_month_tv.getText().toString().trim());
			Intent six = new Intent(AllWayBillActivity.this,
					PersonalLogisticsActivity.class);
			Bundle bunersix = new Bundle();
			bunersix.putString("String", six_month_tv.getText().toString()
					.trim());
			bunersix.putString("type", "0");
			six.putExtras(bunersix);
			six.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			six.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(six);
			break;

		default:
			break;
		}
	}

	private void setBackground(String string) {
		String TAG = string;
		String indentString = this.getString(R.string.personal_indent_all);
		String oneString = this.getString(R.string.personal_one_month);
		String threeString = this.getString(R.string.personal_three_month);
		String sixString = this.getString(R.string.personal_six_month);
		if (TAG.equals(indentString)) {
			indent_all_bg.setBackgroundResource(R.drawable.shopcarchked);
			one_month_bg.setBackgroundResource(R.drawable.more);
			three_month_bg.setBackgroundResource(R.drawable.more);
			six_month_bg.setBackgroundResource(R.drawable.more);
		} else if (TAG.equals(oneString)) {
			indent_all_bg.setBackgroundResource(R.drawable.more);
			one_month_bg.setBackgroundResource(R.drawable.shopcarchked);
			three_month_bg.setBackgroundResource(R.drawable.more);
			six_month_bg.setBackgroundResource(R.drawable.more);

		} else if (TAG.equals(threeString)) {
			indent_all_bg.setBackgroundResource(R.drawable.more);
			one_month_bg.setBackgroundResource(R.drawable.more);
			three_month_bg.setBackgroundResource(R.drawable.shopcarchked);
			six_month_bg.setBackgroundResource(R.drawable.more);
		} else if (TAG.equals(sixString)) {
			indent_all_bg.setBackgroundResource(R.drawable.more);
			one_month_bg.setBackgroundResource(R.drawable.more);
			three_month_bg.setBackgroundResource(R.drawable.more);
			six_month_bg.setBackgroundResource(R.drawable.shopcarchked);
		}

	}

	@Override
	public void refresh() {
		Bundle resParams = getIntent().getExtras();
		String string = resParams.getString("key");
		TAG = resParams.getString("TAG");
		setBackground(string);

	}
	@Override
	protected void back() {
		// TODO Auto-generated method stub
		super.back();
		showTabHost=false;
		refreshActivity=true;
	}

}
