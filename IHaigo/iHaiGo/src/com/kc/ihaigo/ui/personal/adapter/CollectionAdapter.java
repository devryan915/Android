package com.kc.ihaigo.ui.personal.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
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
 * @ClassName: ShopcarGoodsAdapter
 * @Description: 购物车商量列表
 * @author: ryan.wang
 * @date: 2014年7月3日 下午4:28:01
 * 
 */

public class CollectionAdapter extends BaseAdapter {
	private Context ctx;

	private boolean isEdit = false;
	private BackCall call;
	private List<JSONObject> datas = new ArrayList<JSONObject>();

	public List<JSONObject> getDatas() {
		return datas;
	}

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public void setCall(BackCall call) {
		this.call = call;
	}

	public void setDatas(List<JSONObject> datas) {
		this.datas = datas;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public CollectionAdapter(Context context) {
		this.ctx = context;

		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();

	}

	/*
	 * <p>Title: getCount</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	// public void setCheckedTotal(boolean isCheckedTotal) {
	// this.isCheckedTotal = isCheckedTotal;
	// }

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
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
		// TODO Auto-generated method stub

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {

			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_personal_collection_item, null);
			holder = new ViewHolder();
			/**
			 * 计算宽度
			 */
			convertView.findViewById(R.id.goods_item).getLayoutParams().width = Utils
					.getScreenWidth(ctx);
			convertView.findViewById(R.id.test).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							CollectionAdapter.this.call.deal(1000001,
									holder.deletegoods.getTag());
						}
					});

			holder.shopcar_goods_remove = (ImageView) convertView
					.findViewById(R.id.shopcar_goods_remove);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.goodsfrom = (ImageView) convertView
					.findViewById(R.id.goodsfrom);
			holder.goodsname = (TextView) convertView
					.findViewById(R.id.goodsname);
			holder.oldprice = (TextView) convertView
					.findViewById(R.id.oldprice);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.price_discount = (TextView) convertView
					.findViewById(R.id.price_discount);
			holder.warning = (ImageView) convertView.findViewById(R.id.warning);
			holder.warning.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CollectionAdapter.this.call.deal(R.id.warning,
							holder.warning.getTag());

				}
			});

			holder.deletegoods = (TextView) convertView
					.findViewById(R.id.deletegoods);
			final HorizontalScrollView hsView = (HorizontalScrollView) convertView;
			final View view = convertView;
			// 删除

			holder.deletegoods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CollectionAdapter.this.call.deal(R.id.deletegoods,
							holder.deletegoods.getTag());
					holder.deletegoods.setVisibility(View.GONE);
				}
			});

			// 调出删除了
			holder.shopcar_goods_remove
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (holder.deletegoods.getVisibility() == View.GONE) {
								holder.deletegoods.setVisibility(View.VISIBLE);

							}
							holder.deletegoods.measure(0, 0);
							Handler handler = new Handler();
							handler.post(new Runnable() {
								@Override
								public void run() {
									hsView.scrollBy(holder.deletegoods
											.getMeasuredWidth(), 0);
								}
							});

						}
					});
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 数据处理
		try {
			/**
			 * 主键ID
			 */
			String id = datas.get(position).getString("id");
			JSONObject good = datas.get(position).getJSONObject("good");
			String goodsid = good.getString("id");

			String name = good.getString("name");
			String icon = good.getString("icon");
			String source = good.getString("source");
			String price = good.getString("price");
			String discount = good.getString("discount");

			String code = good.getString("currency");
			String RMB = good.getString("RMB");

			JSONObject remind = datas.get(position).getJSONObject("remind");
			int rid = remind.getInt("id");

			holder.deletegoods.setTag(datas.get(position));
			holder.warning.setTag(datas.get(position));

			if (!TextUtils.isEmpty(icon)) {
				imageLoader.displayImage(icon, holder.image, options,
						animateFirstListener);
			}

			DataConfig newConfig = new DataConfig(ctx);
			String urlString = newConfig.getSourceById(source);
			if (!TextUtils.isEmpty(urlString)) {
				imageLoader.displayImage(urlString, holder.goodsfrom, options);
			}
			holder.goodsname.setText(name);

//			double pri = Double.valueOf(price);// 价格
//			double dis = Double.valueOf(discount);//
//			double price_disc = pri * dis;
			String symbol = newConfig.getCurrencySymbolByCode(code);

			holder.oldprice.setText(symbol + price);
			holder.price.setText(symbol + discount);
//
//			double exchange = newConfig.getCurRateByCode(RMB);
//			double Ram = price_disc * exchange;

			holder.price_discount.setText("￥" + RMB);

			// 判断是否有预警

			if (rid > 0) {
				holder.warning.setBackgroundResource(R.drawable.warning);
			} else {
				holder.warning.setBackgroundResource(R.drawable.warning_no);
			}

			// 显示隐藏
			if (isEdit) {
				holder.shopcar_goods_remove.setVisibility(View.VISIBLE);
			} else {

				holder.shopcar_goods_remove.setVisibility(View.GONE);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	class ViewHolder {
		/**
		 * 调出删除BUTTON
		 */

		ImageView shopcar_goods_remove;
		/**
		 * 商品信息
		 */
		ImageView image;
		/**
		 * 来源
		 */
		ImageView goodsfrom;
		/**
		 * 商品名称
		 */
		TextView goodsname;
		/**
		 * 原来 价格
		 */
		TextView oldprice;
		/**
		 * 实际价格
		 */
		TextView price;
		/**
		 * 人民币价
		 */
		TextView price_discount;
		/**
		 * 预警
		 */
		ImageView warning;
		/**
		 * 删除BUTTON
		 */
		TextView deletegoods;
	}

}
