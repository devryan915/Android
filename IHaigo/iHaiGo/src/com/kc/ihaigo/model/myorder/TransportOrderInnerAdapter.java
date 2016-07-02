package com.kc.ihaigo.model.myorder;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.magus.MagusTools;

public class TransportOrderInnerAdapter extends BaseAdapter {
	List<Items> lists;
	private Context ctx;

	public TransportOrderInnerAdapter(Context ctx, List<Items> dataInnerList) {
		this.lists = dataInnerList;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
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
					R.layout.listview_transport_order_inner_item, null);
			holder = new ViewHolder();
			holder.goods_name = (TextView) convertView
					.findViewById(R.id.goods_name);
			holder.good_image = (ImageView) convertView
					.findViewById(R.id.goods_image);
			holder.goods_color = (TextView) convertView
					.findViewById(R.id.goods_color);
			holder.goods_size = (TextView) convertView
					.findViewById(R.id.goods_size);
			holder.goods_total = (TextView) convertView
					.findViewById(R.id.goods_total);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MagusTools.setImageView(lists.get(position).icon, holder.good_image,
				R.drawable.adv_goods);
		holder.goods_name.setText(lists.get(position).name.toString());
		holder.goods_color.setText(lists.get(position).color.toString());
		holder.goods_total.setText("数量  X"
				+ lists.get(position).amount.toString());

		return convertView;
	}

	class ViewHolder {

		/**
		 * 
		 */
		TextView goods_all_total, goods_total, goods_name, goods_color,
				goods_size, all_dollars;
		ImageView good_image;
	}

}
