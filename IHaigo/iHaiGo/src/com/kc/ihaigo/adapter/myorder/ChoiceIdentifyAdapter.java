package com.kc.ihaigo.adapter.myorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kc.ihaigo.R;

public class ChoiceIdentifyAdapter extends BaseAdapter {
	private JSONArray datas;

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	private Context context;

	public ChoiceIdentifyAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}

	@Override
	public Object getItem(int position) {
		return position;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = ((LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.listview_dialogaddress_item, null);
			holder = new ViewHolder();
			holder.address = (TextView) convertView.findViewById(R.id.address);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			JSONObject data = datas.getJSONObject(position);
			String name = data.getString("contacts");
			String contactNumber = data.getString("contactNumber");
			String userArea = data.getString("userArea");
			String userAddr = data.getString("userAddr");
			holder.address.setText(name + contactNumber + userArea + userAddr);
			holder.address.setTag(data.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		TextView address;
	}
}
