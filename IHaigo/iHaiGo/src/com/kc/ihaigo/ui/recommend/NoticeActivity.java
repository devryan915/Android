package com.kc.ihaigo.ui.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;

/**
 * 公告类
 * 
 * @author Thinkpad
 * 
 */
public class NoticeActivity extends IHaiGoActivity implements OnClickListener {
	// 定义返回功能
	private ImageView title_btn_left;
	/**
	 * 定义BUTTON
	 */
	private TextView button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		ininTelet();
		initComponets();
	}

	private void initComponets() {

	}

	private void ininTelet() {
		title_btn_left = (ImageView) findViewById(R.id.title_left);
		title_btn_left.setOnClickListener(this);
		button = (TextView) findViewById(R.id.dredgebutton);
		button.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_left :
				reBack();

				break;
			case R.id.dredgebutton :
				Intent intent = new Intent(NoticeActivity.this,
						NoticeUserActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				RecommendGroupActiviy.group.startiHaiGoActivity(intent);

				break;

			default :
				break;
		}

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
		Intent intent = new Intent(NoticeActivity.this,
				RecommendActivity.class);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	}

}
