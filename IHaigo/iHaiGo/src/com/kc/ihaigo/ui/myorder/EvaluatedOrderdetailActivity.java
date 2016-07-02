package com.kc.ihaigo.ui.myorder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kc.ihaigo.R;

/***
 * @author Lijie
 * @deprecated 转运订单---已评价订单
 * 
 * */
public class EvaluatedOrderdetailActivity extends Activity implements
		OnClickListener {
	private TextView orderdetail_no;
	private String OrderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluated_orderdetail);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		OrderId=this.getIntent().getStringExtra("orderId");
		findViewById(R.id.left_title).setOnClickListener(this);
		orderdetail_no=(TextView) findViewById(R.id.orderdetail_no);
		
		orderdetail_no.setText("订单编号："+OrderId);
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_title:
			this.finish();
			break;

		default:
			break;
		}

	}

}
