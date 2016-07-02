/**
 * @Title: TransportChargeIntro.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年8月11日 下午1:37:34

 * @version V1.0

 */


package com.kc.ihaigo.ui.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;

/**
 * @ClassName: TransportChargeIntro
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年8月11日 下午1:37:34
 *
 */

public class TransportChargeIntro extends IHaiGoActivity {

	/**
	 * 收费详细
	 */
	private TextView transport_status_intro;
	private StringBuffer buff;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_charge_intro);
		init();
	}
	

	private void init() {
		transport_status_intro = (TextView) findViewById(R.id.transport_status_intro);
	}


	@Override
	public void refresh() {
		super.refresh();
		if(TransportMerchantDetailActivity.class.equals(parentClass)){
			try {
				String chargeJson = getIntent().getStringExtra("charge");
				JSONArray resData = new JSONArray(chargeJson);
				buff = new StringBuffer();
				for (int i = 0; i < resData.length(); i++) {
					String name = resData.getJSONObject(i).getString("name");
					String instruction = resData.getJSONObject(i).getString("instruction");
					buff.append(name);
					buff.append("\n\r");
					buff.append(instruction);
					buff.append("\n\r");
					
				}
				transport_status_intro.setText(buff);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
}
