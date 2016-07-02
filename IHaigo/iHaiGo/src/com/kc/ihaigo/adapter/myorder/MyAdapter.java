package com.kc.ihaigo.adapter.myorder;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.magus.MagusTools;

/***
 * 
 * */
public class MyAdapter extends BaseAdapter {

	private String goodsIcon;
	private Context ctx;
	private JSONArray data_order_item;
	private boolean isEdit = false;

	public MyAdapter(Context ctx, JSONArray jsonArray) {
		this.ctx = ctx;
		this.data_order_item = jsonArray;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data_order_item.length();
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return data_order_item.get(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parents) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.lv_order_item_inner, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.goods_name);
			holder.color = (TextView) convertView
					.findViewById(R.id.goods_color);
			holder.good_image = (ImageView) convertView
					.findViewById(R.id.goods_image);
			holder.goods_count = (TextView) convertView
					.findViewById(R.id.goods_count);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			holder.name.setText(data_order_item.getJSONObject(position)
					.getString("name"));
			holder.color.setText(data_order_item.getJSONObject(position)
					.getString("color"));
			holder.goods_count.setText("数量：X"
					+ data_order_item.getJSONObject(position).getString(
							"amount"));
			goodsIcon = data_order_item.getJSONObject(position).getString(
					"icon");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isEdit) {
			holder.orderdetail_selall_ll.setVisibility(View.VISIBLE);
		}

		// Log.e("xxxxxxxxxxxxxxxxxxxxxx", data_order_item.get(position).icon);
		MagusTools.setImageView(goodsIcon, holder.good_image,
				R.drawable.adv_goods);
		getgoodsIcon(goodsIcon);
		// imageLoader.displayImage(data_order_item.get(position).icon,
		// holder.good_image, options,
		// animateFirstListener);

		return convertView;
	}

	public String getgoodsIcon(String icon) {

		if (icon != null) {
			return icon;
		}

		return "";
	}

	class ViewHolder {

		/**
		 * 订单编号,日期,状态
		 */
		TextView orderno, order_data, order_state, myorder_btn_bottom,
				goods_count;
		TextView name, color;
		ImageView good_image;
		LinearLayout orderdetail_selall_ll;
	}

}
