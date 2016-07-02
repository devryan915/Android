/**
 * @Title: ShopcarCfAdapter.java
 * @Package: com.kc.ihaigo.ui.shopcar.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月3日 下午4:28:01

 * @version V1.0

 */

package com.kc.ihaigo.ui.shopcar.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: ShopcarCfAdapter
 * @Description: 购物车模块确认订单
 * @author: ryan.wang
 * @date: 2014年7月3日 下午4:28:01
 * 
 */

public class ShopcarCfAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray datas;
	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	public ShopcarCfAdapter(Context context) {
		this.ctx = context;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
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
		return 0;
	}

	public class ViewHolder {
		WrapListView shopcar_goods_ll;
		ImageView source;
		/**
		 * 支持国内信用卡
		 */
		ImageView supportinnerlandcredit;
		/**
		 * 是否转运公司
		 */
		ImageView translatecom;
		/**
		 * 是否支持直邮
		 */
		ImageView supportredirectorpost;
		/**
		 * 是否支持paypal
		 */
		ImageView paypal;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_shopcar_confirmbill_item, null);
			holder = new ViewHolder();
			holder.shopcar_goods_ll = (WrapListView) convertView
					.findViewById(R.id.shopcar_goods_ll);
			final ShopcarCfGoodsItemsAdapter adapter = new ShopcarCfGoodsItemsAdapter(
					this.ctx);
			holder.shopcar_goods_ll.setAdapter(adapter);
			holder.source = (ImageView) convertView.findViewById(R.id.source);
			holder.supportinnerlandcredit = (ImageView) convertView
					.findViewById(R.id.supportinnerlandcredit);
			holder.translatecom = (ImageView) convertView
					.findViewById(R.id.translatecom);
			holder.supportredirectorpost = (ImageView) convertView
					.findViewById(R.id.supportredirectorpost);
			holder.paypal = (ImageView) convertView.findViewById(R.id.paypal);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			JSONObject data = datas.getJSONObject(position);
			int source = data.getInt("source");
			DataConfig dataConfig = new DataConfig(ctx);
			JSONArray servicesArray = dataConfig.getSourceServiceById(source
					+ "");
			// 商品电商图标
			String iconUrl = dataConfig.getSourceById(datas.getJSONObject(
					position).getString("source"));
			if (!TextUtils.isEmpty(iconUrl)) {
				holder.source.setTag(iconUrl);
				imageLoader.displayImage(iconUrl, holder.source, options,
						animateFirstListener);
			}
			if (servicesArray != null) {
				// 设置电商支持服务
				for (int i = 0; i < servicesArray.length(); i++) {
					int service = servicesArray.getInt(i);
					if (i == 0) {
						if (service == 1) {
							holder.supportinnerlandcredit
									.setImageResource(R.drawable.credit_card);
						} else {
							holder.supportinnerlandcredit
									.setImageResource(R.drawable.credit_card_grey);
						}
					}
					if (i == 1) {
						if (service == 1) {
							holder.translatecom
									.setImageResource(R.drawable.transshipment);
						} else {
							holder.translatecom
									.setImageResource(R.drawable.transshipment_grey);
						}
					}
					if (i == 2) {
						if (service == 1) {
							holder.supportredirectorpost
									.setImageResource(R.drawable.direct_mail);
						} else {
							holder.supportredirectorpost
									.setImageResource(R.drawable.direct_mail_grey);
						}
					}
					if (i == 3) {
						if (service == 1) {
							holder.paypal.setImageResource(R.drawable.paypal);
						} else {
							holder.paypal
									.setImageResource(R.drawable.paypal_grey);
						}
					}
				}
			}
			// 更新子listview编辑状态
			ShopcarCfGoodsItemsAdapter adapter = (ShopcarCfGoodsItemsAdapter) holder.shopcar_goods_ll
					.getAdapter();
			adapter.setDatas(data.getJSONArray("childitems"));
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

}
