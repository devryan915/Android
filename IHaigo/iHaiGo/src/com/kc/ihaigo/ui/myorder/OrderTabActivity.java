package com.kc.ihaigo.ui.myorder;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;

/***
 * @Description: 我的订单选项卡
 * @author: @author: Lijie
 * @date: 2014年7月23日 下午1:40:23
 * 
 * **/
public class OrderTabActivity extends IHaiGoActivity implements
		OnClickListener, OnCheckedChangeListener {

	private LinearLayout myorder_content_container;
	private RadioButton other_buy_orderlist, transport_orderlist;
	private PopupWindow myChoicePopupWindow;
	private RadioGroup order_select;
	private TextView all_order, title_right, a_month, three_month, six_month;
	private ImageView title_left;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_tab);
		initView();

	}

	@Override
	protected void back() {
		// TODO Auto-generated method stub
		showTabHost = true;

		super.back();
	}

	private void initView() {
		// TODO Auto-generated method stub
		title_left = (ImageView) findViewById(R.id.title_left);
		title_right = (TextView) findViewById(R.id.title_right);
		order_select = (RadioGroup) findViewById(R.id.order_select);
		myorder_content_container = (LinearLayout) findViewById(R.id.content_container);
		other_buy_orderlist = (RadioButton) findViewById(R.id.other_buy_orderlist);
		transport_orderlist = (RadioButton) findViewById(R.id.transport_orderlist);
		all_order = (TextView) findViewById(R.id.all_order);

		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		all_order.setOnClickListener(this);
		order_select.setOnCheckedChangeListener(this);
		if (flag = true) {
			other_buy_orderlist.setChecked(true);
			flag = false;
		} else {
			transport_orderlist.setChecked(true);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.all_order:
			showPopupWindows(all_order);

			break;
		case R.id.title_left:
			Intent intent = new Intent(OrderTabActivity.this,
					PersonalActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.title_right:
			Intent intents = new Intent(OrderTabActivity.this,
					ForecastAddActivity.class);
			startActivity(intents);
			break;
		case R.id.a_month:
			myChoicePopupWindow.dismiss();
			all_order.setText(a_month.getText().toString().trim());
			break;
		case R.id.three_month:
			myChoicePopupWindow.dismiss();
			all_order.setText(three_month.getText().toString().trim());
			break;
		case R.id.six_month:
			myChoicePopupWindow.dismiss();
			all_order.setText(six_month.getText().toString().trim());
			break;

		default:
			break;
		}

	}

	public void showPopupWindows(View parentView) {
		if (myChoicePopupWindow == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);

			View view = layoutInflater.inflate(R.layout.myorder_popwin, null);
			a_month = (TextView) view.findViewById(R.id.a_month);
			three_month = (TextView) view.findViewById(R.id.three_month);
			six_month = (TextView) view.findViewById(R.id.six_month);

			a_month.setOnClickListener(this);// popwindow---item点击事件
			three_month.setOnClickListener(this);
			six_month.setOnClickListener(this);
			// 创建一个PopuWidow对象
			myChoicePopupWindow = new PopupWindow(view,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			// all_order=(TextView) view.findViewById(R.id.all_order);
			// all_order.setOnClickListener(this);
		}
		myChoicePopupWindow.setFocusable(true);
		myChoicePopupWindow.setTouchable(true);
		myChoicePopupWindow.setOutsideTouchable(true);
		myChoicePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		WindowManager wm = getWindowManager();
		int screenWidth = wm.getDefaultDisplay().getWidth();
		myChoicePopupWindow.showAsDropDown(parentView);

		myChoicePopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				myChoicePopupWindow.dismiss();

			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup radio, int arg1) {
		// TODO Auto-generated method stub
		switch (radio.getCheckedRadioButtonId()) {
		case R.id.other_buy_orderlist:
			other_buy_orderlist
					.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
			other_buy_orderlist.setTextColor(OrderTabActivity.this
					.getResources().getColor(R.color.white));

			transport_orderlist
					.setBackgroundResource(R.drawable.choose_item_right_shape);
			transport_orderlist.setTextColor(OrderTabActivity.this
					.getResources().getColor(R.color.choose));
			title_right.setVisibility(View.INVISIBLE);
			changeActivity(OrderListBuyOtherAcivity.class,
					myorder_content_container);

			break;
		case R.id.transport_orderlist:
			other_buy_orderlist
					.setBackgroundResource(R.drawable.choose_item_lift_shape);
			other_buy_orderlist.setTextColor(this.getResources().getColor(
					R.color.choose));

			transport_orderlist
					.setBackgroundResource(R.drawable.choose_item_right_selected_shape);
			transport_orderlist.setTextColor(this.getResources().getColor(
					R.color.white));
			title_right.setVisibility(View.VISIBLE);
			title_right.setOnClickListener(this);
			changeActivity(TransportOrderListActivity.class,
					myorder_content_container);
			break;

		default:
			break;
		}

	}

}
