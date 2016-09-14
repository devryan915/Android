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
import com.broadchance.entity.Contact;
import com.broadchance.wdecgrec.R;

public class DialogChooseContactAdapter extends BaseAdapter {

	private Context ctx;
	private List<Contact> contacts = null;

	public DialogChooseContactAdapter(Context ctx, List<Contact> contacts) {
		this.ctx = ctx;
		this.contacts = contacts;
	}

	@Override
	public int getCount() {
		return this.contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return this.contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView textViewBleName;
		public View viewSel;
		public Contact contact;
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
		Contact contact = contacts.get(position);
		holder.textViewBleName.setText(contact.getPhoneNo());
		holder.contact = contact;
		return convertView;
	}
}
