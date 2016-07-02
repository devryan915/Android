/**
 * @Title: PersonalNickName.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月15日 下午1:26:47

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * @ClassName: PersonalNickName
 * @Description: 编辑昵称修改
 * @author: helen.yang
 * @date: 2014年7月15日 下午1:26:47
 * 
 */

public class PersonalNickName extends IHaiGoActivity implements OnClickListener {

	private String TAG = "PersonalNickName";
	private int code;
	private EditText nickName;
	private String editnickName;
	private String introduce;
	private String sex;
	private String head_image_url;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_nickname);
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
	private void initComponents() {
		
	}

	@Override
	public void refresh() {
		super.refresh();
		String which = getIntent().getStringExtra("which");
		if("PersonalEditUserInfo".equals(which)){
			name = getIntent().getStringExtra("nickName");
			nickName.setText(name);
			introduce = getIntent().getStringExtra("introduce");
			sex = getIntent().getStringExtra("sex");
			head_image_url = getIntent().getStringExtra("headPortnextrait");
		}
	}

	/**
	 * 
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		nickName = (EditText) findViewById(R.id.et_enter_nickname);
		nickName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
	}

	
	
	@Override
	protected void back() {
		resParams = new Bundle();
		resParams.putString("nickName", name);
		resParams.putString("introduce", introduce);
		resParams.putString("headPortrait", head_image_url);
		resParams.putString("sex", sex);
		super.back();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(PersonalNickName.this,
						PersonalEditUserInfo.class);
				intent.putExtra("nickName", name);
				intent.putExtra("introduce", introduce);
				intent.putExtra("headPortnextrait", head_image_url);
				intent.putExtra("which", "PersonalEditUserInfo");
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;

			case R.id.title_right :
				editnickName = nickName.getText().toString().trim();
//				boolean checkHanzi = CheckUtil.checkHanzi(editnickName);
//				if(checkHanzi){
//					nickName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
//					if("".equals(editnickName)){
//						ToastUtil.showLocation(PersonalNickName.this, "昵称不能为空！");
//					}else{
//						updateUser();
//					}
//				}else{
//					nickName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
//					if("".equals(editnickName)){
//						ToastUtil.showLocation(PersonalNickName.this, "昵称不能为空！");
//					}else{
//						updateUser();
//					}
//				}
				updateUser();
				break;
			default :
				break;
		}

	}

	/**
	 * 
	 * @Title: updateUser
	 * @user: helen.yang
	 * @Description: 修改用户信息——昵称 void
	 * @throws
	 */
	private void updateUser() {
		final String url = Constants.UPDATEUSER_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		Log.i("geek", " +++++++++++++++++????????????" + Constants.USER_ID);
		map.put("userId", Constants.USER_ID);
		map.put("nickName", editnickName);
		map.put("sex", sex);
		map.put("introduce", introduce);
		map.put("headPortnextrait", head_image_url);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject obj = new JSONObject(result);
								code = obj.getInt("code");
								if (code == 1) {
									name = editnickName;
									intent.setClass(PersonalNickName.this,
											PersonalEditUserInfo.class);
									intent.putExtra("nickName", editnickName);
									intent.putExtra("introduce", introduce);
									intent.putExtra("sex", sex);
									intent.putExtra("headPortnextrait", head_image_url);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if (code == 0) {
									Toast.makeText(PersonalNickName.this,
											"修改信息失败", 0).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.i(TAG, "*****************收到信息" + result);
						}
					}
				}, 1);
	}

}
