/**
 * @Title: AdvDetailsAdapter.java
 * @Package: com.kc.ihaigo.ui.recommend.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:涓婃捣鍧ゅ垱淇℃伅鎶�湳鏈夐檺鍏徃
 * 

 * @author Comsys-ryan.wang

 * @date 2014骞�鏈�鏃�涓婂崍11:55:49

 * @version V1.0

 */

package com.kc.ihaigo.ui.recommend.adapter;

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
 * @ClassName: AdvDetailsAdapter
 * @Description: TODO
 * @author: zouxianbin
 * @date: 2014年7月1日 上午11:19:10
 * 
 */

public class AdvDetailsAdapter extends BaseAdapter {
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private Context ctx;
	private String[] datas;

	public void setDatas(String[] datas) {
		this.datas = datas;
	}

	public AdvDetailsAdapter(Context ctx) {
		this.ctx = ctx;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	public class ViewHolder {
		/**
		 * 标题
		 */
		TextView advListItem;
		/**
		 * 内容图片
		 */
		ImageView advContent;
	}

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_adv_item, null);
			holder = new ViewHolder();
			holder.advListItem = (TextView) converView
					.findViewById(R.id.advList_item);
			holder.advContent = (ImageView) converView
					.findViewById(R.id.adv_content);

			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		return converView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.length;
	}

	// @Override
	// public int getCount() {
	// return datas == null ? 0 : datas.length();
	// }

}
