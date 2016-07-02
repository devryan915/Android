package com.kc.ihaigo.ui.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kc.ihaigo.R;

public class SettingsTimeAdapter extends BaseAdapter {
	private Context ctx;
	private String[] timeStrings;
	public SettingsTimeAdapter(Context context) {
		this.ctx = context;
		timeStrings = context.getResources().getStringArray(
				R.array.settings_informtime);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public long getItemId(int position) {

		return position % 25;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_settings_time, null);
			holder = new ViewHolder();
			holder.settings_time = ((TextView) convertView
					.findViewById(R.id.settings_time));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.settings_time.setText(timeStrings[position % 25] + "");
		return convertView;
	}
	class ViewHolder {
		TextView settings_time;
	}

	@Override
	public Object getItem(int position) {

		return timeStrings[position % 25];
	}

}
