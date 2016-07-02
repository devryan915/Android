/**
 * @Title: IhaigoWelcome.java
 * @Package: com.kc.ihaigo.ui.welcome
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月31日 下午5:07:18

 * @version V1.0

 */

package com.kc.ihaigo.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.welcome.adapter.WelcomePagerAdapter;

/**
 * @ClassName: IhaigoWelcome
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年7月31日 下午5:07:18
 * 
 */

public class IhaigoWelcome extends IHaiGoActivity {
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ihaigo_welcome);
		viewPager = (ViewPager) findViewById(R.id.welcomeViews);
		viewPager.setAdapter(new WelcomePagerAdapter(IhaigoWelcome.this,
				new BackCall() {
					@SuppressWarnings("deprecation")
					@Override
					public void deal(int which, Object... obj) {
						if (parentClass != null) {
							back();
							PersonalGroupActivity.group
									.getLocalActivityManager()
									.destroyActivity(
											"com.kc.ihaigo.ui.welcome.IhaigoWelcome",
											true);
						} else {
							Intent intent = new Intent(IhaigoWelcome.this,
									IHaiGoMainActivity.class);
							startActivity(intent);
							IhaigoWelcome.this.finish();
						}
					}
				}));
	}
	@Override
	public void refresh() {

	}

}
