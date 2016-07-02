package com.kc.ihaigo.ui.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;

public class NoticeUserActivity extends IHaiGoActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_user);
		findViewById(R.id.title_left);
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
		Intent intent = new Intent(NoticeUserActivity.this,
				NoticeActivity.class);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		RecommendGroupActiviy.group.startiHaiGoActivity(intent);
	}
	@Override
	public void onClick(View v) {
	switch (v.getId()) {
	case R.id.title_left:
		reBack();
		
		break;

	default:
		break;
	}
	}

}
