package com.kc.ihaigo.ui.shipping.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kc.ihaigo.R;

public class WayBillShippingAdapter extends BaseAdapter {
	private Context ctx;
	List<Map<String, String>> lists;

	// private int selectedPosition;
	//
	// public void setSelectedPosition(int selectedPosition) {
	// this.selectedPosition = selectedPosition;
	// }

	public WayBillShippingAdapter(Context ctx) {
		this.ctx = ctx;

	}

	public void setDatas(List<Map<String, String>> lists) {
		this.lists = lists;
	}

	/*
	 * <p>Title: getCount</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	@Override
	public int getCount() {
		return lists == null ? 0 : lists.size();
	}

	// public void setIength(int length) {
	// this.length = length;
	// }

	/*
	 * <p>Title: getItem</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */

	@Override
	public Object getItem(int position) {
		return position;
	}

	/*
	 * <p>Title: getItemId</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * <p>Title: getView</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @param arg1
	 * 
	 * @param arg2
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_shipping_item, null);
			holder = new ViewHolder();

			holder.transport_tite = (TextView) converView
					.findViewById(R.id.transport_tite);

			holder.transport = (TextView) converView
					.findViewById(R.id.transport);
			holder.oval = converView.findViewById(R.id.oval);

			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		if (position == 0) {
			Drawable drawable = ctx.getResources().getDrawable(
					R.drawable.new_lift);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.transport.setCompoundDrawables(drawable, null, null, null);

			holder.oval.setBackgroundResource(R.drawable.oval_shape_seleted);

		} else {
			holder.transport.setCompoundDrawables(null, null, null, null);
			holder.oval.setBackgroundResource(R.drawable.oval_shape);

		}

		holder.transport_tite.setText(lists.get(position).get("time"));
		holder.transport.setText(lists.get(position).get("location"));

		return converView;
	}

	class ViewHolder {

		/**
		 * 物流时间
		 */
		TextView transport_tite;

		/**
		 * 物流信息
		 */
		TextView transport;

		View oval;
	}

}