package com.kc.ihaigo.adapter.myorder;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.kc.ihaigo.R;
import com.kc.ihaigo.model.myorder.Vegetable;

@SuppressLint("WrongViewCast")
public class AddServicesAdapter extends BaseAdapter {

	private List<Vegetable> lists = null;
	private Context mContext;

	public AddServicesAdapter(Context mContext, List lists) {
		this.mContext = mContext;
		this.lists = lists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return list.length();
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		// try {
		// return list.get(position);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.add_service_dialog, null);
			holder = new ViewHolder();
			holder.divide_box = (CheckBox) convertView
					.findViewById(R.id.divide_box);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.divide_box.setText(lists.get(position).getName());
		holder.divide_box.setCompoundDrawables(mContext.getResources()
				.getDrawable(R.drawable.dialog_addaddress), null, null, null);
		// try {
		// holder.goods_name.setText(list.getJSONObject(position).getString(
		// "name"));
		// holder.goods_weight.setText(list.getJSONObject(position).getString(
		// "weight"));
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return convertView;
	}

}

class ViewHolder {
	CheckBox divide_box, goods_weight;
}