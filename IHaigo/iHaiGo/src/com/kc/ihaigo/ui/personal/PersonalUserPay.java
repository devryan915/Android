/**
 * @Title: PersonalUSerPay.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月13日 下午5:56:10

 * @version V1.0

 */


package com.kc.ihaigo.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;

/**
 * @ClassName: PersonalUSerPay
 * @Description: 进入用户充值页面
 * @author: helen.yang
 * @date: 2014年7月13日 下午5:56:10
 *
 */

public class PersonalUserPay extends IHaiGoActivity implements OnClickListener{

	private String TAG = "PersonalUserPay";
	private TextView choice_pay_way;
	private EditText enter_pay_sum;
	private EditText enter_trade_psd;
	private TextView at_once_pay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_user_pay);
		initTitle();
		initComponents();
	}
	

	/***
	 * 
	  * @Title: initComponents
	  * @user: helen.yang
	  * @Description: TODO void
	  * @throws
	 */
	private void initComponents(){
		choice_pay_way = (TextView) findViewById(R.id.choice_pay_way);
		choice_pay_way.setOnClickListener(this);
		//充值金额
		enter_pay_sum = (EditText) findViewById(R.id.enter_pay_sum);
		enter_pay_sum.setOnClickListener(this);
		//输入交易密码
		enter_trade_psd = (EditText) findViewById(R.id.enter_trade_psd);
		enter_trade_psd.setOnClickListener(this);
		//立即充值
		at_once_pay = (TextView) findViewById(R.id.at_once_pay);
	}
	
	/**
	 * 
	  * @Title: initTitle
	  * @user: helen.yang
	  * @Description: TODO void
	  * @throws
	 */
	private void initTitle(){
		findViewById(R.id.title_left).setOnClickListener(this);
		
	}

	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(
					PersonalUserPay.this,
					PersonalActivity.class);
			intent.putExtra(
					IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
					false);
			intent.putExtra(
					IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
					true);
			PersonalGroupActivity.group
					.startiHaiGoActivity(intent);
			break;
		case R.id.choice_pay_way:
			intent.setClass(
					PersonalUserPay.this,
					ChoicePayWay.class);
			intent.putExtra(
					IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
					false);
			intent.putExtra(
					IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
					false);
			PersonalGroupActivity.group
					.startiHaiGoActivity(intent);
			break;
		case R.id.at_once_pay:
			break;
		default:
			break;
		}
	}
}
