/**
 * @Title: TopicResponse.java
 * @Package: com.kc.ihaigo.ui.topic
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月27日 下午1:58:34

 * @version V1.0

 */

package com.kc.ihaigo.ui.topic;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalConfirmSettingPassword;
import com.kc.ihaigo.ui.personal.PersonalFirstLogin;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.personal.PersonalLoginActivity;
import com.kc.ihaigo.ui.personal.PersonalTopicActivity;
import com.kc.ihaigo.ui.personal.PersonalUserLogin;
import com.kc.ihaigo.ui.topic.adpater.DetailResponseAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @ClassName: TopicResponse
 * @Description: 话题回复，评论页面
 * @author: helen.yang
 * @date: 2014年7月27日 下午1:58:34
 * 
 */

public class TopicResponse extends IHaiGoActivity implements OnClickListener {

	private EditText et_response_content;
	private String response_content;
	private String tid;
	private String type;
	private String pid;
	private TextView topic_response_who;
	private TextView title_middle;

	private Class<IHaiGoActivity> lparentClass;
	private String flag;
	private IHaiGoGroupActivity lparentGroupActivity;
	private String nickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_response);
		initTitle();
	}

	/**
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		title_middle = (TextView) findViewById(R.id.title_middle);
		et_response_content = (EditText) findViewById(R.id.response_content);
		et_response_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
		topic_response_who = (TextView) findViewById(R.id.topic_response_who);
	}

	@Override
	public void refresh() {
		super.refresh();
		if (TopicDetailActivity.class.equals(parentClass)) {
			lparentGroupActivity  = parentGroupActivity;
			flag = getIntent().getStringExtra("flag");
			lparentClass = parentClass;
			type = getIntent().getStringExtra("type");
			if ("2".equals(type)) {
				topic_response_who.setVisibility(View.VISIBLE);
				tid = getIntent().getStringExtra("tid");
				pid = getIntent().getStringExtra("pid");
				nickName = getIntent().getStringExtra("nickName");
				title_middle.setText("回复");
				topic_response_who.setText("@" + nickName);
				et_response_content.setText("");
				et_response_content
						.setHint(R.string.enter_comment_content_hint);
			} else if ("1".equals(type)) {
				tid = getIntent().getStringExtra("id");
				title_middle.setText("评论");
				et_response_content.setPadding(25, 15, 10, 10);
				et_response_content.setText("");
				et_response_content
						.setHint(R.string.enter_comment_content_hint);
			}
		}else if (PersonalUserLogin.class.equals(parentClass)
				|| PersonalFirstLogin.class.equals(parentClass)
				|| PersonalConfirmSettingPassword.class.equals(parentClass)) {
			tid = getIntent().getStringExtra("topicid");
			type = getIntent().getStringExtra("type");
			if ("2".equals(type)) {
				topic_response_who.setVisibility(View.VISIBLE);
				pid = getIntent().getStringExtra("pid");
				nickName = getIntent().getStringExtra("nickName");
				title_middle.setText("回复");
				topic_response_who.setText("@" + nickName);
				et_response_content.setText("");
				et_response_content
						.setHint(R.string.enter_comment_content_hint);
			} else if ("1".equals(type)) {
				title_middle.setText("评论");
				et_response_content.setPadding(25, 15, 10, 10);
				et_response_content.setText("");
				et_response_content
						.setHint(R.string.enter_comment_content_hint);
			}
		}

	}

	@Override
	protected void back() {
		
		if (lparentClass == null) {
			try {
				parentClass = (Class<IHaiGoActivity>) Class
						.forName("com.kc.ihaigo.ui.topic.TopicDetailActivity");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			super.back();
		}else{
			parentClass = lparentClass;
			super.back();
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
//		case R.id.title_left:
//			intent.setClass(TopicResponse.this, TopicDetailActivity.class);
//			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
//			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
//			PersonalGroupActivity.group.startiHaiGoActivity(intent);
//			break;

		case R.id.title_right:
			if(checkLogin()){
				response_content = et_response_content.getText().toString();
				add(tid);
			}else{
				jumpLogin();
			}
			
			break;
		default:
			break;
		}
	}

	private void add(final String tid) {

		final String add_url = Constants.TOPIC_URL + tid + "/add";
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("tid", "");// 话题
		map.put("uid", Constants.USER_ID);// 用户id
		map.put("type", type);// 1评论2回复
		map.put("content", response_content);
		if ("2".equals(type)) {
			map.put("pid", pid);// 详情接口返回的id
		}
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, add_url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject json = new JSONObject(result);
								String code = json.getString("code");
								Log.i("geek", "hahah+" + tid + response_content);
								if (!TextUtils.isEmpty(code)) {
									if ("1".equals(code)) {
										// 添加回应成功
										ToastUtil.showShort(TopicResponse.this,
												"添加回应成功");
										Log.i("geek", ";;;;;;;;"+parentClass);
										Intent intent = new Intent();
										intent.setClass(TopicResponse.this,
												TopicDetailActivity.class);
										intent.putExtra("id", tid);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
												true);
										intent.putExtra(
												IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
												false);
										TopicGroupActivity.group.startiHaiGoActivity(intent);
										
									} else {
										// 添加回应失败
										ToastUtil.showShort(TopicResponse.this,
												"添加回应失败");
									}

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}, 0);
	}
	
	public void jumpLogin() {
		Intent intent = new Intent();
		intent.setClass(TopicResponse.this, PersonalLoginActivity.class);
		intent.putExtra("code", "");
		intent.putExtra("topicid", tid);
		intent.putExtra("type", type);
		if("2".equals(type)){
			intent.putExtra("pid", pid);
			intent.putExtra("nickName", nickName);
		}
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		TopicGroupActivity.group.startiHaiGoActivity(intent);
	}
}
