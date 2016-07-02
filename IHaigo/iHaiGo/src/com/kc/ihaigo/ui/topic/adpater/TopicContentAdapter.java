/**
 * @Title: TopicContentAdapter.java
 * @Package: com.kc.ihaigo.ui.topic.adpater
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月25日 下午2:14:37

 * @version V1.0

 */

package com.kc.ihaigo.ui.topic.adpater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: TopicContentAdapter
 * @Description: 话题listview适配器
 * @author: ryan.wang
 * @date: 2014年7月25日 下午2:14:37
 * 
 */

public class TopicContentAdapter extends BaseAdapter {
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private Context context;
	private JSONArray datas = new JSONArray();
	public TopicContentAdapter(Context context) {
		this.context = context;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}
	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	public JSONArray getDatas() {
		return datas;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}
	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflayout = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflayout.inflate(R.layout.listview_topic_item, null);
			holder = new ViewHolder();
			holder.topic_topictitle = (TextView) convertView
					.findViewById(R.id.topic_topictitle);
			holder.topic_image = (ImageView) convertView
					.findViewById(R.id.topic_image);
			holder.topic_content = (TextView) convertView
					.findViewById(R.id.topic_content);
			holder.topic_comments_num = (TextView) convertView
					.findViewById(R.id.topic_comments_num);
			holder.topic_comments_num = (TextView) convertView
					.findViewById(R.id.topic_comments_num);
			holder.topic_comments_publisher = (TextView) convertView
					.findViewById(R.id.topic_comments_publisher);
			holder.topic_comments_pubtime = (TextView) convertView
					.findViewById(R.id.topic_comments_pubtime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			JSONObject data = datas.getJSONObject(position);
			holder.topic_topictitle.setText(data.getString("title"));
			JSONArray urls = data.getJSONArray("image");
			if (urls.length() > 0) {
				holder.topic_image.setVisibility(View.VISIBLE);
				imageLoader.displayImage(urls.getString(0), holder.topic_image,
						options, animateFirstListener);
				holder.topic_image.setTag(true);
			} else {
				holder.topic_image.setVisibility(View.GONE);
			}
			holder.topic_content.setText(data.getString("content"));
			holder.topic_comments_num.setText(data.getString("count"));
			holder.topic_comments_publisher.setText(data.getString("nickName"));
//			holder.topic_comments_pubtime.setText(Utils.compareTime(
//					Utils.getCurrentTime(), data.getLong("createTime"))
//					+ context.getResources().getString(R.string.topic_pubtime));
			holder.topic_comments_pubtime.setText(Utils.compareTime(
					Utils.getCurrentTime(), data.getLong("createTime")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	class ViewHolder {
		TextView topic_topictitle;
		ImageView topic_image;
		TextView topic_content;
		TextView topic_comments_num;
		TextView topic_comments_publisher;
		TextView topic_comments_pubtime;
	}
}
