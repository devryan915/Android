/**
 * @Title: AdvDetailsActivity.java
 * @Package: com.kc.ihaigo.ui.recommend
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月1日 上午11:19:10

 * @version V1.0

 */

package com.kc.ihaigo.ui.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.AdvDetailsAdapter;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;

/**
 * 广告详情
 * 
 * @ClassName: AdvDetailsActivity
 * @Description: TODO
 * @author: zouxianbin
 * @date: 2014年7月1日 上午11:19:10
 * 
 */

public class AdvDetailsActivity extends IHaiGoActivity
		implements
			OnItemClickListener {

	/*
	 * <p>Title: onCreate</p> <p>Description: </p>
	 * 
	 * @param savedInstanceState
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	private PullUpRefreshListView adv_details_list;
	private AdvDetailsAdapter aAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adv_details);
		initComponets();
	}

	private void initComponets() {
		// 定义返回功能
		ImageView title_btn_left = (ImageView) findViewById(R.id.title_left);
		title_btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reBack();
			}
		});

		adv_details_list = (PullUpRefreshListView) findViewById(R.id.adv_details_ll);
		aAdapter = new AdvDetailsAdapter(AdvDetailsActivity.this);
		aAdapter.setDatas(new String[]{"", "", ""});
		adv_details_list.setAdapter(aAdapter);

		adv_details_list.setOnItemClickListener(this);
	}

	private void loadData(final int what) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(AdvDetailsActivity.this,
				AdvDetailsInfoActivity.class);
		RecommendGroupActiviy.group.startiHaiGoActivity(intent);

	}
	@Override
	public void refresh() {
		super.refresh();
		

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			reBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void reBack() {
		Intent intent = new Intent(AdvDetailsActivity.this,
				RecommendActivity.class);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	}

}
