package com.broadchance.wdecgrec.adapter;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.broadchance.entity.BleDev;
import com.broadchance.wdecgrec.R;

public class DialogBleAdapter extends BaseAdapter {

	private Context ctx;
	private List<BleDev> devs = null;

	public void addDevice(BleDev device) {
		// if (!devs.contains(device)) {
		// devs.add(device);
		// }
		for (BleDev dev : devs) {
			if (dev.getMacAddress().equals(device.getMacAddress())) {
				return;
			}
		}
		devs.add(device);
	}

	public DialogBleAdapter(Context ctx) {
		this.ctx = ctx;
		this.devs = new ArrayList<BleDev>();
	}

	@Override
	public int getCount() {
		return this.devs.size();
	}

	@Override
	public Object getItem(int position) {
		return this.devs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView textViewBleName;
		public View viewSel;
		public BleDev dev;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_chooseble_item, null);
			holder = new ViewHolder();
			holder.textViewBleName = (TextView) convertView
					.findViewById(R.id.textViewBleName);
			holder.viewSel = convertView.findViewById(R.id.viewSelSkin);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BleDev dev = devs.get(position);
		holder.textViewBleName.setText(dev.getMacAddress());
		holder.dev = dev;
		return convertView;
	}
}
