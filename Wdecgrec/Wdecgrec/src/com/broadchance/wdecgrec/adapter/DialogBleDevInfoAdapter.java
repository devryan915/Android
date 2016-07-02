package com.broadchance.wdecgrec.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.broadchance.entity.BleDev;
import com.broadchance.entity.BleDevInfo;
import com.broadchance.wdecgrec.R;

public class DialogBleDevInfoAdapter extends BaseAdapter {

	private Context ctx;
	private List<BleDevInfo> devInfos = null;

	public DialogBleDevInfoAdapter(Context ctx, List<BleDevInfo> devs) {
		this.ctx = ctx;
		this.devInfos = devs;
	}

	@Override
	public int getCount() {
		return this.devInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return this.devInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView textViewBleInfoName;
		public TextView textViewBleInfoValue;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_bledevinfo_item, null);
			holder = new ViewHolder();
			holder.textViewBleInfoName = (TextView) convertView
					.findViewById(R.id.textViewBleInfoName);
			holder.textViewBleInfoValue = (TextView) convertView
					.findViewById(R.id.textViewBleInfoValue);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BleDevInfo devInfo = devInfos.get(position);
		holder.textViewBleInfoName.setText(devInfo.getDevInfoName());
		holder.textViewBleInfoValue.setText(devInfo.getDevInfoValue());
		return convertView;
	}
}
