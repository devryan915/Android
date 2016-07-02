/**
 * @Title: ChoicePayWay.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月25日 下午1:49:10

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;

/**
 * @ClassName: ChoicePayWay
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月25日 下午1:49:10
 * 
 */

public class ChoicePayWay extends IHaiGoActivity implements OnClickListener {
	public static final int Alipay = 1;
	public static final int Weixinpay = 2;
	public static final int UnionPay = 3;
	public static final int TencentPay = 4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_choice_pay_way);
		initTitle();
		initpayType();
	}
	@SuppressWarnings({"unchecked", "unused"})
	@Override
	public void refresh() {
		Bundle bundle = getIntent().getExtras();

	}
	/**
	 * @Title: initpayType
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */
	private void initpayType() {
		findViewById(R.id.alipay).setOnClickListener(this);
		findViewById(R.id.weixinpay).setOnClickListener(this);
		findViewById(R.id.unionpay).setOnClickListener(this);
		findViewById(R.id.tenpay).setOnClickListener(this);
	}

	private void initTitle() {
		// findViewById(R.id.title_left).setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		resParams = new Bundle();
		switch (v.getId()) {
			case R.id.alipay :
				resParams.putInt("paytypeid", Alipay);
				resParams.putString("paytypename",
						getResources().getString(R.string.blipay_way));
				break;
			case R.id.weixinpay :
				resParams.putInt("paytypeid", Weixinpay);
				resParams.putString("paytypename",
						getResources().getString(R.string.wechat_way));
				break;
			case R.id.unionpay :
				resParams.putInt("paytypeid", UnionPay);
				resParams.putString("paytypename",
						getResources().getString(R.string.unionpay_way));
				break;
			case R.id.tenpay :
				resParams.putInt("paytypeid", TencentPay);
				resParams.putString("paytypename",
						getResources().getString(R.string.tenpay_way));
				break;
			default :
				break;
		}
		back();
	}
}
