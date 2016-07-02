package com.kc.ihaigo.adapter.myorder;

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
import com.magus.MagusTools;

/***
 * 优质转运Adapter
 * */
public class QualityTransportAdapter extends BaseAdapter {
	// List<Vegetable> lists;
	JSONArray lists;
	private Context ctx;

	public QualityTransportAdapter(Context ctx, JSONArray datas) {
		this.lists = datas;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists == null ? 0 : lists.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return null;
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
					R.layout.listview_merchant_transport_item, null);
			holder = new ViewHolder();
			holder.transport_icon = (ImageView) convertView
					.findViewById(R.id.transport_icon);
			holder.merchant_name = (TextView) convertView
					.findViewById(R.id.merchant_name);
			holder.transport_signature = (TextView) convertView
					.findViewById(R.id.transport_signature);
			holder.transport_charge = (TextView) convertView
					.findViewById(R.id.transport_charge);
			holder.transport_shippingval = (TextView) convertView
					.findViewById(R.id.transport_shippingval);
			holder.transport_serviceval = (TextView) convertView
					.findViewById(R.id.transport_serviceval);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// for (int i = 0; i < lists.length(); i++) {
		try {
			lists.getJSONObject(position).getString("icon");
			holder.merchant_name.setText(lists.getJSONObject(position)
					.getString("name"));
			holder.transport_signature.setText(lists.getJSONObject(position)
					.getString("signature"));
			holder.transport_charge.setText(lists.getJSONObject(position)
					.getString("charge"));
			holder.transport_shippingval.setText(lists.getJSONObject(position)
					.getString("logistics"));
			holder.transport_serviceval.setText(lists.getJSONObject(position)
					.getString("service"));
			MagusTools.setImageView(
					lists.getJSONObject(position).getString("icon"),
					holder.transport_icon, R.drawable.ic_launcher);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// }

		// holder.merchant_name.setText(lists.get(position).getName());

		return convertView;
	}

	class ViewHolder {

		/**
		 * 
		 */
		TextView merchant_name, transport_signature, transport_charge,
				transport_shippingval, transport_serviceval;
		ImageView transport_icon;
	}
}
