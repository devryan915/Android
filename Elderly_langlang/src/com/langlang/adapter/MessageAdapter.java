package com.langlang.adapter;

import java.util.ArrayList;
import java.util.List;

import com.langlang.data.MessageInfo;
import com.langlang.elderly_langlang.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<MessageInfo> list;

	public MessageAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		list=new ArrayList<MessageInfo>();
	}
	public void addItem(MessageInfo weatherInfo){
		list.add(weatherInfo);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		System.out.println("messageadapter text size:" + list.size());
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void clear() {
		list.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHOlder viewHOlder;
		if (convertView == null) {
			viewHOlder = new ViewHOlder();
			convertView = inflater.inflate(R.layout.item_notify, null);
			viewHOlder.imageView = (ImageView) convertView
					.findViewById(R.id.notify_item_imageview);
			viewHOlder.textView = (TextView) convertView
					.findViewById(R.id.notify_item_textview);
			convertView.setTag(viewHOlder);
		} else {
			viewHOlder = (ViewHOlder) convertView.getTag();
		}
		viewHOlder.textView.setText(list.get(position).getData());
		System.out.println("messageadapter text:"
				+ list.get(position).getData());
		viewHOlder.imageView.setBackgroundResource(list.get(position)
				.getImage());
		return convertView;
	}

	class ViewHOlder {
		ImageView imageView;
		TextView textView;
	}
}
