package com.langlang.adapter;

import java.util.ArrayList;

import com.langlang.elderly_langlang.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HeartWAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<String> arrayList;

	public HeartWAdapter(Context context) {
		this.context = context;
		arrayList=new ArrayList<String>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addItem(String item) {
		arrayList.add(item);
	}
	public void clear(){
		arrayList.clear();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView textView = new TextView(context);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTextColor(context.getResources().getColor(R.color.golden));
		textView.setTextSize(20);
		textView.setText(arrayList.get(position));
		return textView;
	}
}
