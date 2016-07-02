/**
 * @Title: ShopcarCfGoodsItemsAdapter.java
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: ShopcarCfGoodsItemsAdapter
 * @Description: 购物车确认订单商量列表
 * @author: ryan.wang
 * @date: 2014年7月3日 下午4:28:01
 * 
 */

public class ShopcarCfGoodsItemsAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray datas;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	public ShopcarCfGoodsItemsAdapter(Context context) {
		this.ctx = context;
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

	@Override
	public Object getItem(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_shopcar_confirmbill_goods_item, null);
			holder = new ViewHolder();
			holder.goodsimage = (ImageView) convertView
					.findViewById(R.id.goodsimage);
			holder.goodsname = (TextView) convertView
					.findViewById(R.id.goodsname);
			holder.oldprice = (TextView) convertView
					.findViewById(R.id.oldprice);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.goodscolorval = (TextView) convertView
					.findViewById(R.id.goodscolorval);
			holder.goodsruleval = (TextView) convertView
					.findViewById(R.id.goodsruleval);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}// 商品图片
		String goodsImage;
		try {
			DataConfig dataConfig = new DataConfig(ctx);
			goodsImage = datas.getJSONObject(position).getString("icon");
			imageLoader.displayImage(goodsImage, holder.goodsimage, options,
					animateFirstListener);
			// 商品名称
			holder.goodsname.setText(datas.getJSONObject(position)
					.getString("name").toString());
			// 根据货币编号获取货币符号
			String code = datas.getJSONObject(position).getString("currency");
			String symbol = dataConfig.getCurrencySymbolByCode(code);
			// 原价
			double price = datas.getJSONObject(position).getDouble("price");
			holder.oldprice.setText(symbol + price);
			holder.oldprice.setTag(code);
			// 折后价
			double discount = datas.getJSONObject(position).getDouble(
					"discount");
			holder.price.setText(symbol + discount);
			holder.price.setTag(discount);
			holder.goodscolorval.setText(datas.getJSONObject(position)
					.getString("color").toString());
			holder.goodsruleval.setText(datas.getJSONObject(position)
					.getString("size").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	class ViewHolder {
		/**
		 * 商品图片
		 */
		ImageView goodsimage;
		/**
		 * 商品名称
		 */
		TextView goodsname;
		/**
		 * 原价
		 */
		TextView oldprice;
		/**
		 * 现价
		 */
		TextView price;
		/**
		 * 颜色
		 */
		TextView goodscolorval;
		/**
		 * 尺寸
		 */
		TextView goodsruleval;
	}
}
