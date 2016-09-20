package com.langlang.adapter;

import java.util.ArrayList;

import com.langlang.elderly_langlang.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HeartAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<String>list;

	public HeartAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		list=new ArrayList<String>();
	}
	public  void addListItem(String item){
		list.add(item);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
	public void clear(){
		list.clear();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHodler viewHodler;
		if (convertView == null) {
			viewHodler = new ViewHodler();
			convertView = inflater.inflate(R.layout.item_pose, null);
			viewHodler.textView = (TextView) convertView
					.findViewById(R.id.pose_item_textview);
			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHodler) convertView.getTag();
		}
		viewHodler.textView.setText(list.get(position));
		return convertView;
	}

	class ViewHodler {
		TextView textView;
	}
}
