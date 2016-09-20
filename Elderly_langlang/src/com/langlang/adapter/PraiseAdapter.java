package com.langlang.adapter;

import java.util.ArrayList;

import com.langlang.elderly_langlang.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PraiseAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> arrayList;

	public PraiseAdapter(Context context,ArrayList<String> arrayList) {
		this.context = context;
		this.arrayList=arrayList;
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
