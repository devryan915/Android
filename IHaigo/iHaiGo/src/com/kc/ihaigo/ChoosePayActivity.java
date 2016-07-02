package com.kc.ihaigo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @ClassName: ChoicePayWay
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月25日 下午1:49:10
 * 
 */

public class ChoosePayActivity extends Activity implements OnClickListener {

	private TextView paytype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_pay);
		initTitle();
		initpayType();
	}

	private void initpayType() {
		paytype = (TextView) findViewById(R.id.pay_type);
		findViewById(R.id.alipay).setOnClickListener(this);
		findViewById(R.id.weixinpay).setOnClickListener(this);
		findViewById(R.id.unionpay).setOnClickListener(this);
		findViewById(R.id.tenpay).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
	}

	private void initTitle() {
		// findViewById(R.id.title_left).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {

		case R.id.alipay:
			intent.putExtra("payType", paytype.getText().toString());
			setResult(2,intent);
			this.finish();
			break;
		case R.id.weixinpay:
			break;
		case R.id.unionpay:
			break;
		case R.id.tenpay:
			break;
		case R.id.title_right:
			this.finish();
			break;
		default:
			break;
		}
	}
}
