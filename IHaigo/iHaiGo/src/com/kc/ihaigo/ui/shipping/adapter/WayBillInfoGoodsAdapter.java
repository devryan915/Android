package com.kc.ihaigo.ui.shipping.adapter;

import java.util.List;
import java.util.Map;

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

public class WayBillInfoGoodsAdapter extends BaseAdapter {
	private Context ctx;
	List<Map<String, String>> lists;
	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	// private int selectedPosition;
	//
	// public void setSelectedPosition(int selectedPosition) {
	// this.selectedPosition = selectedPosition;
	// }

	public WayBillInfoGoodsAdapter(Context ctx, List<Map<String, String>> lists) {
		this.ctx = ctx;
		this.lists = lists;
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
		return lists.size();
	}

	// public void setIength(int length) {
	// this.length = length;
	// }

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

	/*
	 * <p>Title: getItemId</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * <p>Title: getView</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @param arg1
	 * 
	 * @param arg2
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_goods_number, null);
			holder = new ViewHolder();

			holder.goods_name = (TextView) converView
					.findViewById(R.id.goods_name);

			holder.goods_weight = (TextView) converView
					.findViewById(R.id.goods_weight);
			holder.goods_Logo = (ImageView) converView
					.findViewById(R.id.goods_Logo);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		holder.goods_name.setText(lists.get(position).get("goods_name"));
		holder.goods_weight.setText(lists.get(position).get("goods_weight"));
		/*
		 * imageLoader.displayImage(headPortrait, puragent_head, options,
		 * animateFirstListener);
		 */

		return converView;
	}

	class ViewHolder {

		/**
		 * 商品
		 */
		TextView goods_name;

		/**
		 * 重量
		 */
		TextView goods_weight;

		ImageView goods_Logo;
	}

}
