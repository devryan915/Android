package com.kc.ihaigo.ui.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.AdvDetailsInfoAdapter;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;

/**
 * 广告商品列表
 * 
 * @author zouxianbin
 * 
 */
public class AdvDetailsInfoActivity extends IHaiGoActivity {
	private PullUpRefreshListView listView;

	private GridView goodsInfo;
	private AdvDetailsInfoAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adv_details_info);
		ininTelet();
		initComponets();
	}

	private void initComponets() {

		goodsInfo = (GridView) findViewById(R.id.goodsInfo);
		adapter = new AdvDetailsInfoAdapter(AdvDetailsInfoActivity.this);
		adapter.setDatas(new String[] { "", "", "", "", "", "", "" });
		goodsInfo.setAdapter(adapter);

	}

	private void ininTelet() {
		// 定义返回功能
		ImageView title_btn_left = (ImageView) findViewById(R.id.title_left);
		title_btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reBack();

			}
		});
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
		Intent intent = new Intent(AdvDetailsInfoActivity.this,
				AdvDetailsActivity.class);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	}
}
