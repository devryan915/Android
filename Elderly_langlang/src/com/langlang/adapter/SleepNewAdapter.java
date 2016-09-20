package com.langlang.adapter;

import java.util.ArrayList;
import java.util.List;

import com.langlang.data.SleepInfo;
import com.langlang.elderly_langlang.R;
import com.tencent.open.cgireport.c;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SleepNewAdapter extends BaseAdapter {
	private List<SleepInfo> list;
	private LayoutInflater layoutInflater;

	public SleepNewAdapter(Context context) {
		layoutInflater = LayoutInflater.from(context);
		list = new ArrayList<SleepInfo>();
	}

	public void addListItem(SleepInfo sleepInfo) {
		list.add(sleepInfo);
	}

	public void clear() {
		list.clear();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHodler v;
		if (convertView == null) {
			v = new ViewHodler();
			convertView = layoutInflater.inflate(R.layout.item_sleepitem, null);
			v.start_time = (TextView) convertView
					.findViewById(R.id.sleepitem_start_time_tw);
			v.maintain_time = (TextView) convertView
					.findViewById(R.id.sleepitem_maintain_time_tw);
			v.sleep_state = (TextView) convertView
					.findViewById(R.id.sleepitem_state_tw);
			convertView.setTag(v);
		} else {
			v = (ViewHodler) convertView.getTag();
		}
		v.start_time.setText(list.get(position).getStart_time());
		v.maintain_time.setText(list.get(position).getMaintain_time());
		v.sleep_state.setText(list.get(position).getSleep_state());
		
		return convertView;
	}

	class ViewHodler {
		TextView start_time;// 开始时间
		TextView maintain_time;// 维持时间
		TextView sleep_state;// 睡眠状态
	}
}
