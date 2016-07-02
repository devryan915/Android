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

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.shopcar.ShopCarActivity;
import com.kc.ihaigo.ui.shopcar.ShopCarGroupActiviy;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: ShopcarGoodsAdapter
 * @Description: 购物车商量列表
 * @author: ryan.wang
 * @date: 2014年7月3日 下午4:28:01
 */

public class ShopcarGoodsItemsAdapter extends BaseAdapter {
	private Context ctx;
	// private boolean isCheckedTotal;
	private boolean isEdit = false;
	private JSONArray datas;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private BackCall call;
	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}
	private android.content.DialogInterface.OnClickListener confirmClickListener;

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
	// public void setCheckedTotal(boolean isCheckedTotal) {
	// this.isCheckedTotal = isCheckedTotal;
	// }

	public ShopcarGoodsItemsAdapter(
			Context context,
			android.content.DialogInterface.OnClickListener confirmClickListener,
			BackCall call) {
		this.ctx = context;
		this.confirmClickListener = confirmClickListener;
		this.call = call;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}

	@Override
	public Object getItem(int position) {
		return null;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_shopcargoods_item, null);
			convertView.findViewById(R.id.goodsitem).getLayoutParams().width = Utils
					.getScreenWidth(ctx);
			final TextView tView = (TextView) ((ShopCarActivity) this.ctx)
					.findViewById(R.id.shopcar_bottom_counts);
			final TextView disRMB = (TextView) ((ShopCarActivity) this.ctx)
					.findViewById(R.id.shopcar_bottom_discountedprice);
			holder = new ViewHolder();
			holder.shopcar_selall_chk = (CheckBox) convertView
					.findViewById(R.id.shopcar_selall_chk);
			holder.shopcar_selall_chk
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							ShopcarGoodsItemsAdapter.this.call.deal(
									R.id.shopcar_selall_chk, isChecked, holder);
							// if (isChecked) {
							// String count = (String) holder.buyCount
							// .getText();
							// String total = (String) tView.getText();
							// tView.setText((Integer.parseInt(total) + Integer
							// .parseInt(count)) + "");
							// DataConfig dataConfig = new DataConfig(ctx);
							// String code = (String) holder.oldprice.getTag();
							// double totalRmb = (Double) disRMB.getTag();
							// totalRmb += ((Double) holder.price.getTag())
							// * dataConfig.getCurRateByCode(code)
							// * Integer.parseInt(count);
							// disRMB.setText("￥"
							// + new BigDecimal(totalRmb).setScale(2,
							// BigDecimal.ROUND_HALF_UP)
							// .doubleValue() + "");
							// disRMB.setTag(new BigDecimal(totalRmb)
							// .setScale(2, BigDecimal.ROUND_HALF_UP)
							// .doubleValue());
							// } else {
							// String count = (String) holder.buyCount
							// .getText();
							// String total = (String) tView.getText();
							// tView.setText((Integer.parseInt(total) - Integer
							// .parseInt(count)) + "");
							// if ((Integer.parseInt(total) - Integer
							// .parseInt(count)) == 0) {
							// disRMB.setText("￥0.00");
							// disRMB.setTag(0d);
							// } else {
							// DataConfig dataConfig = new DataConfig(ctx);
							// String code = (String) holder.oldprice
							// .getTag();
							// double totalRmb = (Double) disRMB.getTag();
							// totalRmb -= ((Double) holder.price.getTag())
							// * dataConfig.getCurRateByCode(code)
							// * Integer.parseInt(count);
							// disRMB.setText("￥"
							// + new BigDecimal(totalRmb)
							// .setScale(
							// 2,
							// BigDecimal.ROUND_HALF_UP)
							// .doubleValue() + "");
							// disRMB.setTag(new BigDecimal(totalRmb)
							// .setScale(2,
							// BigDecimal.ROUND_HALF_UP)
							// .doubleValue());
							// }
							// }
						}
					});
			holder.buyCount = (TextView) convertView
					.findViewById(R.id.buyCount);
			holder.minusgoods = (ImageView) convertView
					.findViewById(R.id.minusgoods);
			holder.minusgoods.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// String total = (String) tView.getText();
					// int count = Integer.parseInt((String) holder.buyCount
					// .getText()) - 1;
					// int tocount = Integer.parseInt(total) - 1;
					// if (tocount < 1 || count < 1) {
					// return;
					// }
					// holder.buyCount.setText(count + "");
					// if (holder.shopcar_selall_chk.isChecked()) {
					// tView.setText(tocount + "");
					// if (tocount == 0) {
					// disRMB.setText("￥0.00");
					// disRMB.setTag(0d);
					// } else {
					// DataConfig dataConfig = new DataConfig(ctx);
					// String code = (String) holder.oldprice.getTag();
					// double totalRmb = (Double) disRMB.getTag();
					// totalRmb -= (Double) holder.price.getTag()
					// * dataConfig.getCurRateByCode(code);
					// disRMB.setText("￥"
					// + new BigDecimal(totalRmb).setScale(2,
					// BigDecimal.ROUND_HALF_UP)
					// .doubleValue() + "");
					// disRMB.setTag(new BigDecimal(totalRmb).setScale(2,
					// BigDecimal.ROUND_HALF_UP).doubleValue());
					// }
					// }
					ShopcarGoodsItemsAdapter.this.call.deal(R.id.minusgoods,
							holder);
				}
			});
			holder.addgoods = (ImageView) convertView
					.findViewById(R.id.addgoods);
			holder.addgoods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// String count = (String) holder.buyCount.getText();
					// String total = (String) tView.getText();
					// holder.buyCount.setText((Integer.parseInt(count) + 1) +
					// "");
					// if (holder.shopcar_selall_chk.isChecked()) {
					// tView.setText((Integer.parseInt(total) + 1) + "");
					// DataConfig dataConfig = new DataConfig(ctx);
					// String code = (String) holder.oldprice.getTag();
					// double totalRmb = (Double) disRMB.getTag();
					// totalRmb += (Double) holder.price.getTag()
					// * dataConfig.getCurRateByCode(code);
					// disRMB.setText("￥"
					// + new BigDecimal(totalRmb).setScale(2,
					// BigDecimal.ROUND_HALF_UP).doubleValue()
					// + "");
					// disRMB.setTag(new BigDecimal(totalRmb).setScale(2,
					// BigDecimal.ROUND_HALF_UP).doubleValue());
					// }
					ShopcarGoodsItemsAdapter.this.call.deal(R.id.addgoods,
							holder);
				}
			});
			holder.goodecountselector = (LinearLayout) convertView
					.findViewById(R.id.goodecountselector);
			holder.shopcar_goods_remove = (Button) convertView
					.findViewById(R.id.shopcar_goods_remove);
			holder.deletegoods = (TextView) convertView
					.findViewById(R.id.deletegoods);
			final HorizontalScrollView hsView = (HorizontalScrollView) convertView;
			final View view = convertView;
			holder.deletegoods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ShopcarGoodsItemsAdapter.this.call.deal(R.id.deletegoods,
							holder.deletegoods.getTag());
				}
			});
			holder.shopcar_goods_remove
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ShopcarGoodsItemsAdapter.this
									.notifyDataSetChanged();
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
			holder.movefavorite = (Button) convertView
					.findViewById(R.id.movefavorite);
			holder.movefavorite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((com.kc.ihaigo.ui.shopcar.ShopCarActivity.GoodsDealListener) confirmClickListener)
							.setParams(new Object[]{holder.deletegoods.getTag()});
					DialogUtil.showAddShopcarDialog(ShopCarGroupActiviy.group,
							confirmClickListener);
				}
			});
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
			// 替代listview onitemclick事件
			holder.ll_goods = (LinearLayout) convertView
					.findViewById(R.id.ll_goods);
			holder.ll_goods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					call.deal(R.id.ll_goods, holder.deletegoods.getTag());
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 商品图片
		String goodsImage;
		try {
			holder.deletegoods.setTag(datas.getJSONObject(position).getLong(
					"id"));
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
			holder.buyCount.setText(datas.getJSONObject(position)
					.getString("amount").toString());
			if (isEdit) {
				holder.shopcar_selall_chk.setVisibility(View.GONE);
				holder.goodecountselector.setVisibility(View.GONE);
				holder.shopcar_goods_remove.setVisibility(View.VISIBLE);
				holder.movefavorite.setVisibility(View.VISIBLE);
			} else {
				holder.deletegoods.setVisibility(View.GONE);
				holder.shopcar_selall_chk.setVisibility(View.VISIBLE);
				// holder.shopcar_selall_chk.setChecked(isCheckedTotal);
				holder.goodecountselector.setVisibility(View.VISIBLE);
				holder.shopcar_goods_remove.setVisibility(View.GONE);
				holder.movefavorite.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	public class ViewHolder {
		public LinearLayout ll_goods;
		public Button shopcar_goods_remove;
		public CheckBox shopcar_selall_chk;
		ImageView minusgoods;
		ImageView addgoods;
		LinearLayout goodecountselector;
		public TextView deletegoods;
		Button movefavorite;
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
		public TextView oldprice;
		/**
		 * 现价
		 */
		public TextView price;
		/**
		 * 颜色
		 */
		TextView goodscolorval;
		/**
		 * 尺寸
		 */
		TextView goodsruleval;
		/**
		 * 数量
		 */
		public TextView buyCount;
	}
}
