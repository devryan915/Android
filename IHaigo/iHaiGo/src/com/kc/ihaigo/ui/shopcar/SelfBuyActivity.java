package com.kc.ihaigo.ui.shopcar;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.kc.ihaigo.R;
/***
 * 自购
 * ***/
public class SelfBuyActivity extends Activity implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_self_buy);
		
		findViewById(R.id.title_left).setOnClickListener(this);
		
		
		
		 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;

		default:
			break;
		}
	}
	

}
