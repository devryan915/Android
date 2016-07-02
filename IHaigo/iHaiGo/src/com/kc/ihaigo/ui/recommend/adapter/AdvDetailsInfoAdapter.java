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

public class AdvDetailsInfoAdapter extends BaseAdapter {

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private Context ctx;
	private String[] datas;

	public void setDatas(String[] datas) {
		this.datas = datas;
	}

	public AdvDetailsInfoAdapter(Context ctx) {
		this.ctx = ctx;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	public class ViewHolder {

		/**
		 * 内容图片
		 */
		ImageView contentImg;
		/**
		 * 商品名称
		 */
		TextView goodsName;
		/**
		 * 原来价格
		 */
		TextView originalPrice;
		/**
		 * 实际价格
		 */
		TextView actualPrice;
		/**
		 * 人民币价格
		 */
		TextView ram_actualPrice;
		/**
		 * 加入购物车
		 */
		ImageView shopping_but;
	}

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_adv_info_item, null);
			holder = new ViewHolder();
			holder.contentImg = (ImageView) converView
					.findViewById(R.id.content_img);
			holder.goodsName = (TextView) converView
					.findViewById(R.id.goodsName);
			holder.originalPrice = (TextView) converView
					.findViewById(R.id.adv_originalPrice);
			holder.originalPrice = (TextView) converView
					.findViewById(R.id.adv_originalPrice);
			holder.ram_actualPrice = (TextView) converView
					.findViewById(R.id.ram_actualPrice);
			holder.shopping_but = (ImageView) converView
					.findViewById(R.id.shopping_but);

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
