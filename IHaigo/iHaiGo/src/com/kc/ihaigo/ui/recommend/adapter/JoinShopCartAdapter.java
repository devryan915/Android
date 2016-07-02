package com.kc.ihaigo.ui.recommend.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kc.ihaigo.R;

public class JoinShopCartAdapter extends BaseAdapter {

	private Context ctx;
	private JSONArray datas;

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	public JoinShopCartAdapter(Context ctx) {
		this.ctx = ctx;

	}

	public class ViewHolder {

		/**
		 * 选择着色
		 */
		public TextView choose;

	}

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					R.layout.gridview_add_shipping_choose, null);
			holder = new ViewHolder();
			holder.choose = (TextView) converView.findViewById(R.id.choose);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		try {
			if (!datas.getJSONObject(position).isNull("color")) {
				holder.choose.setText(datas.getJSONObject(position).getString(
						"color"));
				holder.choose.setTag(datas.getJSONObject(position)
						.getJSONArray("items"));
			} else {
				holder.choose.setText(datas.getJSONObject(position).getString(
						"size"));
				// 改尺寸数量库存情况
				holder.choose.setTag(datas.getJSONObject(position).getInt(
						"amount"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return converView;
	}
	@Override
	public long getItemId(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
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