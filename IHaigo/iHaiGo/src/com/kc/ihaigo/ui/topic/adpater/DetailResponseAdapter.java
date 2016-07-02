/**
 * @Title: DetailResponseAdapter.java
 * @Package: com.kc.ihaigo.ui.topic.adpater
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月29日 上午9:16:58

 * @version V1.0

 */


package com.kc.ihaigo.ui.topic.adpater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.personal.adapter.TopicRespondAdpater;
import com.kc.ihaigo.ui.topic.TopicGroupActivity;
import com.kc.ihaigo.ui.topic.TopicResponse;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @ClassName: DetailResponseAdapter
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月28日 下午3:39:34
 * 
 */

public class DetailResponseAdapter extends BaseAdapter {

	private Context ctx;
	private JSONArray datas = new JSONArray();
	private BackCall call;

	public DetailResponseAdapter(Context ctx) {
		this.ctx = ctx;
	}
	
	public void setCall(BackCall call) {
		this.call = call;
	}

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	
	/**
	 * getter method
	 * @return the datas
	 */
	
	public JSONArray getDatas() {
		return datas;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}

	/*
	 * <p>Title: getItem</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder ;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_detail_topic_response_item, null);
			holder = new ViewHolder();
			holder.detail_user_headImage = (ImageView) convertView
					.findViewById(R.id.detail_user_image);
			holder.detail_user_nickName = (TextView) convertView
					.findViewById(R.id.detail_nickName);
			holder.detail_user_introduce = (TextView) convertView
					.findViewById(R.id.detail_introduce);
			holder.createtime = (TextView) convertView
					.findViewById(R.id.detail_topic_createtime);
			holder.comment_content = (TextView) convertView
					.findViewById(R.id.detail_topic_content);
			holder.response_content = (TextView) convertView
					.findViewById(R.id.detail_response_last_content);
			holder.btn_response = (ImageButton) convertView
					.findViewById(R.id.btn_topic_response);
			holder.btn_response.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DetailResponseAdapter.this.call.deal(R.id.btn_topic_response,
							holder.btn_response.getTag());
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 数据处理
		try {
			JSONObject jsonObject = datas.getJSONObject(position);
			if(jsonObject !=null){
				Log.i("topic", "listview hahah;;;;;;;;"+jsonObject.toString());
				String url = jsonObject.getString("headImage");
				ImageLoader
				.getInstance()
				.displayImage(
						url + "",
						holder.detail_user_headImage);
				final String id = jsonObject.getString("id");
				final String nickName= jsonObject.getString("nickName");
				holder.detail_user_nickName.setText(nickName);
				holder.detail_user_nickName.setTag(id);
				String type = jsonObject.getString("type");
				holder.detail_user_introduce.setText(jsonObject.getString("introduce"));
				holder.detail_user_introduce.setTag(type);
				final int pid = jsonObject.getInt("pid");
				if(pid>0){
					holder.response_content.setVisibility(View.VISIBLE);
					holder.response_content.setText(jsonObject.getString("econtent"));
				}else{
					holder.response_content.setVisibility(View.GONE);
				}
				holder.comment_content.setText(jsonObject.getString("content"));
				holder.comment_content.setTag(String.valueOf(pid));
				String createtime =Utils.getCurrentTime(jsonObject.getLong("createTime"), "MM-dd  HH:mm");
				holder.createtime.setText(createtime);
				holder.btn_response.setTag(datas.get(position));
//				holder.btn_response.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						ToastUtil.showShort(ctx, "点击回复"+position);
//						Intent intent = new Intent();
//						intent.setClass(ctx, TopicResponse.class);
////						intent.putExtra("tid", tid);
//						intent.putExtra("type", "2");
//						intent.putExtra("pid", String.valueOf(id));
//						intent.putExtra("nickName", nickName);
//						intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
//						intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
//						PersonalGroupActivity.group.startiHaiGoActivity(intent);
//					}
//				});
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public class ViewHolder {
		/**
		 * 用户头像
		 */
		public ImageView detail_user_headImage;
		/**
		 * 用户昵称
		 */
		public TextView detail_user_nickName;

		/**
		 * 用户一句话介绍
		 */
		public TextView detail_user_introduce;
		/**
		 * 生成时间
		 */
		public TextView createtime;
		/**
		 * 回复按钮
		 */
		public ImageButton btn_response;
		/**
		 * 评论内容
		 */
		public TextView comment_content;
		/**
		 * 回复内容
		 */
		public TextView response_content;

	}

}

