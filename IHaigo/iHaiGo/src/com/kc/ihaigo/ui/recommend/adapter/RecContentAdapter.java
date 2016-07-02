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

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 
 * @ClassName: RecContentAdapter
 * @Description: 精选首页 精选ListView适配器
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:44:33
 * 
 */
public class RecContentAdapter extends BaseAdapter {
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private Context ctx;
	private JSONArray datas = new JSONArray();
	// private View headView;
	// public void setHeadView(View view) {
	// this.headView = view;
	// }
	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	public RecContentAdapter(Context ctx) {
		this.ctx = ctx;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
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

	}

	@Override
	public View getView(int position, View convertViewView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertViewView == null) {
			convertViewView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_rec_item, null);
			holder = new ViewHolder();
			holder.rec_goods_img = (ImageView) convertViewView
					.findViewById(R.id.rec_goods_img);
			holder.rec_goodsname_tv = (TextView) convertViewView
					.findViewById(R.id.rec_goodsname_tv);
			holder.source = (ImageView) convertViewView
					.findViewById(R.id.source);
			holder.price = (TextView) convertViewView.findViewById(R.id.price);
			holder.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			holder.discount = (TextView) convertViewView
					.findViewById(R.id.discount);
			holder.rmbprice = (TextView) convertViewView
					.findViewById(R.id.rmbprice);
			convertViewView.setTag(holder);
		} else {
			holder = (ViewHolder) convertViewView.getTag();
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

			// 商品电商图标
			String iconUrl = dataConfig.getSourceById(datas.getJSONObject(
					position).getString("source"));
			if (!TextUtils.isEmpty(iconUrl)) {
				holder.source.setTag(iconUrl);
				imageLoader.displayImage(iconUrl, holder.source, options);
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

		return convertViewView;
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

	public JSONArray getDatas() {
		return datas;
	}
}