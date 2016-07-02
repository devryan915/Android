/**
 * @Title: PuragentDetailAdapter.java
 * @Package: com.kc.ihaigo.ui.shopcar.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月7日 下午3:19:14

 * @version V1.0

 */

package com.kc.ihaigo.ui.shopcar.adapter;

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
 * @ClassName: PuragentDetailAdapter
 * @Description: 代购商详情页面用户评论列表
 * @author: ryan.wang
 * @date: 2014年7月7日 下午3:19:14
 * 
 */

public class PuragentDetailAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray datas;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	public PuragentDetailAdapter(Context ctx) {
		this.ctx = ctx;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}
	/*
	 * <p>Title: getCount</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

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
		// TODO Auto-generated method stub

		return null;
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
		// TODO Auto-generated method stub

		return 0;
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
			convertView = ((LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.listview_purdetailcomments_item, null);
			holder = new ViewHolder();
			holder.puragent_head = (ImageView) convertView
					.findViewById(R.id.puragent_head);
			holder.user_name = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.puragentdetail_comments_time = (TextView) convertView
					.findViewById(R.id.puragentdetail_comments_time);
			holder.puragent_feeval = (TextView) convertView
					.findViewById(R.id.puragent_feeval);
			holder.puragent_shippingval = (TextView) convertView
					.findViewById(R.id.puragent_shippingval);
			holder.puragent_serviceval = (TextView) convertView
					.findViewById(R.id.puragent_serviceval);
			holder.purdetail_comments_contents = (TextView) convertView
					.findViewById(R.id.purdetail_comments_contents);
			holder.user_introduce = (TextView) convertView
					.findViewById(R.id.user_introduce);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			imageLoader.displayImage(
					datas.getJSONObject(position).getString("headPortrait"),
					holder.puragent_head, options, animateFirstListener);
			holder.purdetail_comments_contents.setText(datas.getJSONObject(
					position).getString("content"));
			holder.puragentdetail_comments_time.setText(Utils.compareTime(Utils
					.getCurrentTime(),
					datas.getJSONObject(position).getLong("createTime"))
					+ "");
			holder.puragent_feeval.setText(datas.getJSONObject(position)
					.getString("charge"));
			holder.user_name.setText(datas.getJSONObject(position).getString(
					"nickName"));
			holder.puragent_serviceval.setText(datas.getJSONObject(position)
					.getString("service"));
			holder.puragent_shippingval.setText(datas.getJSONObject(position)
					.getString("logistics"));
			holder.user_introduce.setText(datas.getJSONObject(position)
					.getString("introduce"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	class ViewHolder {
		ImageView puragent_head;
		TextView user_name;
		TextView puragentdetail_comments_time;
		TextView puragent_feeval;
		TextView puragent_shippingval;
		TextView puragent_serviceval;
		TextView purdetail_comments_contents;
		TextView user_introduce;
	}
}
