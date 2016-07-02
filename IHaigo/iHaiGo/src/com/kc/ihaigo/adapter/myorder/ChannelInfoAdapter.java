package com.kc.ihaigo.adapter.myorder;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.R;

public class ChannelInfoAdapter extends BaseAdapter {
	private JSONArray lists = null;
	private Context mContext;
	private ListView lv_popwin_channel;

	ViewHolder holder;

	public ChannelInfoAdapter(Context mContext, JSONArray lists,
			ListView lv_popwin_channel) {
		this.mContext = mContext;
		this.lists = lists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return lists.get(position);
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

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.listview_add_channel_item, null);
			holder = new ViewHolder();
			holder.channel_name = (TextView) convertView
					.findViewById(R.id.channel_name);
			holder.select_icon = (ImageView) convertView
					.findViewById(R.id.select_icon);
			// lv_popwin_channel =findViewById(R.id.lv_popwin_channel);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.channel_name.setText(lists.getJSONObject(position)
					.getString("name"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertView;
	}

	class ViewHolder {
		TextView channel_name;
		ImageView select_icon;
		ListView lv_popwin_channel;
	}
}
