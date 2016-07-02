/**
 * @Title: GoodsDetailCommetsAdapter.java
 * @Package: com.kc.ihaigo.ui.recommend.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月12日 上午10:39:57

 * @version V1.0

 */

package com.kc.ihaigo.ui.recommend.adapter;

import org.json.JSONArray;
import org.json.JSONException;

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
 * @ClassName: GoodsDetailCommetsAdapter
 * @Description: 商品详情用户评论
 * @author: ryan.wang
 * @date: 2014年7月12日 上午10:39:57
 * 
 */

public class GoodsDetailCommetsAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray datas;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public GoodsDetailCommetsAdapter(Context ctx) {
		this.ctx = ctx;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}

	/*
	 * <p>Title: getItem</p> <p>Description: </p>
	 * 
	 * @param position
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */

	@Override
	public Object getItem(int position) {

		return position;
	}

	/*
	 * <p>Title: getItemId</p> <p>Description: </p>
	 * 
	 * @param position
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return position;
	}

	/*
	 * <p>Title: getView</p> <p>Description: </p>
	 * 
	 * @param position
	 * 
	 * @param convertView
	 * 
	 * @param parent
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.user_style, null);
			holder = new ViewHolder();
			holder.merchant_logo = (ImageView) convertView
					.findViewById(R.id.merchant_logo);
			holder.merchant_name = (TextView) convertView
					.findViewById(R.id.merchant_name);
			holder.merchant_slogan = (TextView) convertView
					.findViewById(R.id.merchant_slogan);
			holder.merchant_content = (TextView) convertView
					.findViewById(R.id.merchant_content);
			holder.publish_time = (TextView) convertView
					.findViewById(R.id.publish_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			imageLoader.displayImage(
					datas.getJSONObject(position).getString("headPortrait"),
					holder.merchant_logo, options, animateFirstListener);
			holder.merchant_name.setText(datas.getJSONObject(position)
					.getString("nickName"));
			holder.merchant_slogan.setText(datas.getJSONObject(position)
					.getString("introduce"));
			holder.merchant_content.setText(datas.getJSONObject(position)
					.getString("content"));
			holder.publish_time.setText(Utils.getCurrentTime(datas
					.getJSONObject(position).getLong("createTime"),
					"MM-dd HH:mm"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder {
		ImageView merchant_logo;
		TextView merchant_name;
		TextView merchant_slogan;
		TextView merchant_content;
		TextView publish_time;
	}
}
