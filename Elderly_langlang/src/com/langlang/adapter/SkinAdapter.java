package com.langlang.adapter;

import com.langlang.elderly_langlang.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SkinAdapter extends BaseAdapter {
	private SharedPreferences skin_sp;
	
	private String skin_name[] = { "金镶玉", "太空版" };
	private int skin_image[] = { R.drawable.skin_one, R.drawable.skin_two };
	private LayoutInflater inflater;
	private Context context;

	public SkinAdapter(Context context) {
		this.context = context;
		skin_sp=context.getSharedPreferences("skin_index", context.MODE_PRIVATE);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return skin_name.length;
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
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_skin, null);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.skin_item_image);
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.skin_item_tw);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.imageView.setBackgroundResource(skin_image[position]);
		viewHolder.textView.setText(skin_name[position]);
		if(skin_sp.getInt("skin_index", 0)==position){
			
			viewHolder.textView.setTextColor(Color.GREEN);
		}
		if(skin_sp.getInt("skin_index", 0)!=position){
			viewHolder.textView.setTextColor(Color.WHITE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView textView;
	}
}
