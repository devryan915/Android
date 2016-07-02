package com.kc.ihaigo.ui.recommend.adapter;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 
 * @ClassName: SearchResAdapter
 * @Description: 搜索结果页ListView适配器
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:44:33
 * 
 */
public class SearchResAdapter extends BaseAdapter {
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private Context ctx;
	private JSONArray datas = new JSONArray();
	private BackCall call;

	public void setCall(BackCall call) {
		this.call = call;
	}
	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}
	public JSONArray getDatas() {
		return datas;
	}
	public SearchResAdapter(Context ctx) {
		this.ctx = ctx;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	public class ViewHolder {
		/**
		 * 商品图标
		 */
		public ImageView rec_goods_img;
		/**
		 * 商品名称
		 */
		public TextView rec_goodsname_tv;

		/**
		 * 来源
		 */
		public ImageView source;
		/**
		 * 优惠价格
		 */
		public TextView discount;
		/**
		 * 价格
		 */
		public TextView price;
		// 折后价
		public TextView rmbprice;
		public TextView shoppingBtn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_search_item, null);
			holder = new ViewHolder();
			holder.rec_goods_img = (ImageView) convertView
					.findViewById(R.id.rec_goods_img);
			holder.rec_goodsname_tv = (TextView) convertView
					.findViewById(R.id.rec_goodsname_tv);

			holder.source = (ImageView) convertView.findViewById(R.id.source);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

			holder.discount = (TextView) convertView
					.findViewById(R.id.discount);
			holder.rmbprice = (TextView) convertView
					.findViewById(R.id.rmbprice);
			holder.shoppingBtn = (TextView) convertView
					.findViewById(R.id.shoppingBtn);
			holder.shoppingBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String code = (String) holder.price.getTag();
					DataConfig dcConfig = new DataConfig(ctx);
					call.deal(
							0,
							new String[]{
									holder.rec_goodsname_tv.getTag().toString(),
									holder.price.getText().toString(),
									holder.discount.getText().toString(),
									holder.rmbprice.getText().toString(),
									"1"
											+ dcConfig
													.getCurrencyNameByCode(code)
											+ "="
											+ new BigDecimal(dcConfig
													.getCurRateByCode(code))
													.setScale(
															2,
															BigDecimal.ROUND_HALF_UP)
													.doubleValue()
											+ ctx.getText(R.string.renminbi),
									holder.source.getTag().toString(),
									holder.rec_goods_img.getTag().toString(),
									holder.rec_goodsname_tv.getText()
											.toString()});
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			DataConfig dataConfig = new DataConfig(ctx);
			// 商品图片
			String goodsImage = datas.getJSONObject(position).getString("icon");
			holder.rec_goods_img.setTag(goodsImage);
			imageLoader.displayImage(goodsImage, holder.rec_goods_img, options,
					animateFirstListener);

			// 商品名称
			holder.rec_goodsname_tv.setText(datas.getJSONObject(position)
					.getString("name").toString());
			holder.rec_goodsname_tv.setTag(datas.getJSONObject(position)
					.getLong("id"));
			// 商品电商图标
			String iconUrl = dataConfig.getSourceById(datas.getJSONObject(
					position).getString("source"));
			if (!TextUtils.isEmpty(iconUrl)) {
				holder.source.setTag(iconUrl);
				imageLoader.displayImage(iconUrl, holder.source, options,
						animateFirstListener);
			}
			// 根据货币编号获取货币符号
			String code = datas.getJSONObject(position).getString("currency");
			String symbol = dataConfig.getCurrencySymbolByCode(code);
			// 原价
			double price = datas.getJSONObject(position).getDouble("price");
			holder.price.setText(symbol + price);
			holder.price.setTag(code);
			// 折后价
			double discount = datas.getJSONObject(position).getDouble(
					"discount");
			holder.discount.setText(symbol + discount);
			holder.rmbprice.setText("￥"
					+ new BigDecimal(discount
							* dataConfig.getCurRateByCode(code)).setScale(2,
							BigDecimal.ROUND_HALF_UP).doubleValue() + " ");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	@Override
	public long getItemId(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}
}