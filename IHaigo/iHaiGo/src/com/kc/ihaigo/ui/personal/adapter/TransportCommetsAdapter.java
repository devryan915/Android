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

package com.kc.ihaigo.ui.personal.adapter;

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

public class TransportCommetsAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray datas;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public TransportCommetsAdapter(Context ctx) {
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
					R.layout.transp_style, null);
			holder = new ViewHolder();
			holder.headImage = (ImageView) convertView.findViewById(R.id.headImage);
			holder.nickName = (TextView) convertView.findViewById(R.id.nickName);
			holder.charge = (TextView) convertView.findViewById(R.id.puragent_charge);
			holder.trans = (TextView) convertView.findViewById(R.id.puragent_trans);
			holder.service = (TextView) convertView.findViewById(R.id.puragent_service);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.createTime = (TextView) convertView.findViewById(R.id.createTime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			imageLoader.displayImage(
					datas.getJSONObject(position).getString("headPortrait"),
					holder.headImage, options, animateFirstListener);
			holder.nickName.setText(datas.getJSONObject(position).getString("nickName"));
			String logItem = Utils.getCurrentTime(
					datas.getJSONObject(position).getLong("createTime"), "MM-dd  HH:mm");
			holder.createTime.setText(logItem);
			holder.content.setText(datas.getJSONObject(position).getString("content"));
			// 收费标准 1、1星 2、2星 3、3星 4、4星 4、5星
			holder.charge.setText(datas.getJSONObject(position).getString(
					"charge"));
			// 物流速度 1、1星 2、2星 3、3星 4、4星 4、5星
			holder.service.setText(datas.getJSONObject(position).getString(
					"service")) ;
			// 服务态度 1、1星 2、2星 3、3星 4、4星 4、5星
			holder.trans.setText(datas.getJSONObject(position).getString(
					"logistics")) ;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder {
		/**
		 * LOGO
		 */
		ImageView headImage;
		/**
		 * 姓名
		 */
		TextView nickName;
		/**
		 * 时间
		 */
		TextView createTime;
		/**
		 * 内容
		 */
		TextView content;
		/**
		 * 收费
		 */
		TextView charge;
		/**
		 * 物流
		 */
		TextView trans;
		/**
		 * 服务
		 */
		TextView service;
	}
}
