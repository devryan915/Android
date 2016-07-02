/**
 * @Title: PersonalLoginPasd.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月11日 下午2:02:44

 * @version V1.0

 */


package com.kc.ihaigo.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;

/**
 * @ClassName: PersonalLoginPasd
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月11日 下午2:02:44
 *
 */

public class PersonalLoginPasd extends IHaiGoActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.personal_userlogin_pasd);
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
		switch (v.getId()) {
		case R.id.title_left:
			Intent intent = new Intent(
					PersonalLoginPasd.this,
					PersonalLoginActivity.class);
			intent.putExtra(
					IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
					false);
			intent.putExtra(
					IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
					false);
			PersonalGroupActivity.group
					.startiHaiGoActivity(intent);
			break;
		default:
			break;
		}
	}
}
