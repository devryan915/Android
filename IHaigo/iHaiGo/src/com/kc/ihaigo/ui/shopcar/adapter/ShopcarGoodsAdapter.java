/**
 * @Title: ShopcarGoodsAdapter.java
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: ShopcarGoodsAdapter
 * @Description: 购物车商量列表
 * @author: ryan.wang
 * @date: 2014年7月3日 下午4:28:01
 * 
 */

public class ShopcarGoodsAdapter extends BaseAdapter {
	private Context ctx;
	private boolean isCheckedTotal;
	private boolean isEdit = false;
	private android.content.DialogInterface.OnClickListener confirmClickListener;
	private BackCall call;

	public void setCall(BackCall call) {
		this.call = call;
	}

	private JSONArray datas;

	public JSONArray getDatas() {
		return datas;
	}

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public ShopcarGoodsAdapter(Context context,
			android.content.DialogInterface.OnClickListener confirmClickListener) {
		this.ctx = context;
		this.confirmClickListener = confirmClickListener;
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
	public void setCheckedTotal(boolean isCheckedTotal) {
		this.isCheckedTotal = isCheckedTotal;
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
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_shopcar_item, null);
			holder = new ViewHolder();
			holder.shopcar_goods_ll = (WrapListView) convertView
					.findViewById(R.id.shopcar_goods_ll);
			final ShopcarGoodsItemsAdapter adapter = new ShopcarGoodsItemsAdapter(
					this.ctx, confirmClickListener, call);
			holder.shopcar_goods_ll.setAdapter(adapter);
			holder.shopcar_selall_chk = (CheckBox) convertView
					.findViewById(R.id.shopcar_selall_chk);
			final View view = convertView;
			holder.shopcar_selall_chk
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							for (int i = 0; i < holder.shopcar_goods_ll
									.getCount(); i++) {
								((CheckBox) holder.shopcar_goods_ll.getChildAt(
										i)
										.findViewById(R.id.shopcar_selall_chk))
										.setChecked(isChecked);
							}
							// adapter.setCheckedTotal(isChecked);
							// adapter.notifyDataSetChanged();
						}
					});
			// holder.shopcar_goods_remove = (Button) convertView
			// .findViewById(R.id.shopcar_goods_remove);
			// holder.shopcar_goods_remove
			// .setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// ShopcarGoodsAdapter.this.notifyDataSetChanged();
			// Toast.makeText(ShopcarGoodsAdapter.this.ctx,
			// "删除成功", 1000).show();
			// }
			// });
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

			ShopcarGoodsItemsAdapter adapter = (ShopcarGoodsItemsAdapter) holder.shopcar_goods_ll
					.getAdapter();
			adapter.setDatas(data.getJSONArray("childitems"));
			adapter.setEdit(isEdit);
			adapter.notifyDataSetChanged();
			// 更新View跟新状态
			if (isEdit) {
				holder.shopcar_selall_chk.setVisibility(View.GONE);
				// holder.shopcar_goods_remove.setVisibility(View.VISIBLE);
			} else {
				holder.shopcar_selall_chk.setVisibility(View.VISIBLE);
				holder.shopcar_selall_chk.setChecked(isCheckedTotal);
				// holder.shopcar_goods_remove.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	class ViewHolder {
		WrapListView shopcar_goods_ll;
		CheckBox shopcar_selall_chk;
		// Button shopcar_goods_remove;
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
		LinearLayout ll_goods;
	}
}
