package com.broadchance.wdecgrec;

import thoth.holter.ecg_010.manager.PreferencesManager;
import thoth.holter.ecg_010.manager.ResponseCodeMsgManager;
import thoth.holter.ecg_010.manager.SkinManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.SSXLXService;
import com.broadchance.utils.UIUtil;

public class BaseActivity extends Activity implements View.OnClickListener {

	// protected boolean NoTitle = false;
	protected Resources localRes;
	protected SSXLXService serverService;
	protected ClientGameService clientService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// if (NoTitle) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// } else {
		// getActionBar().setDisplayShowCustomEnabled(true);
		// getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// getActionBar().setCustomView(R.layout.custom_title_main);
		// View titleView = getActionBar().getCustomView();
		// }
		super.onCreate(savedInstanceState);
		localRes = SkinManager.getInstance().getLocalResources();
		serverService = SSXLXService.getInstance();
		clientService = ClientGameService.getInstance();
	}

	protected String getSkinString(int resNameId) {
		return SkinManager.getInstance().getString(resNameId);
	}

	protected float getSkinDimen(int resNameId) {
		return SkinManager.getInstance().getDimen(resNameId);
	}

	protected Drawable getSkinDrawable(int resNameId) {
		return SkinManager.getInstance().getDrawable(resNameId);
	}

	protected int getSkinColor(int resNameId) {
		return SkinManager.getInstance().getColor(resNameId);
	}

	protected SharedPreferences getSharedPreferences() {
		return PreferencesManager.getInstance().getSharedPreferences();
	}

	protected String getPreferencesString(String key) {
		return PreferencesManager.getInstance().getString(key);
	}

	protected boolean getPreferencesBoolean(String key) {
		return PreferencesManager.getInstance().getBoolean(key);
	}

	protected long getPreferencesLong(String key) {
		return PreferencesManager.getInstance().getLong(key);
	}

	protected void putPreferencesBoolean(String key, boolean value) {
		PreferencesManager.getInstance().putBoolean(key, value);
	}

	protected void putPreferencesString(String key, String value) {
		PreferencesManager.getInstance().putString(key, value);
	}

	protected void putPreferencesLong(String key, long value) {
		PreferencesManager.getInstance().putLong(key, value);
	}

	/**
	 * 显示悬浮框提示
	 * 
	 * @param content
	 */
	protected void showToast(String content) {
		UIUtil.showToast(BaseActivity.this, content);
	}

	/**
	 * 显示服务端返回的消息提示
	 * 
	 * @param code
	 */
	// protected void showResponseMsg(String code) {
	// UIUtil.showToast(BaseActivity.this, ResponseCodeMsgManager
	// .getInstance().getMsg(code));
	// }

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {

	}

}
