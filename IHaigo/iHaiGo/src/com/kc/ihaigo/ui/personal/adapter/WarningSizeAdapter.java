/**
 * @Title: RecRateAdapter.java
 * @Package: com.kc.ihaigo.ui.recommend.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-zouxianbin

 * @date 2014年7月2日 下午3:25:47

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kc.ihaigo.R;

/**
 * @ClassName:
 * @Description: 大小
 * @author: zouxianbin
 * @date:
 * 
 */

public class WarningSizeAdapter extends BaseAdapter {
	private JSONArray datas;
	private Context context;
	private int selectItem = -1;

	public WarningSizeAdapter(Context context, JSONArray datas) {
		this.context = context;
		this.datas = datas;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = ((LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.listview_wirning_dialog, null);
			holder = new ViewHolder();
			holder.topictype = (TextView) convertView
					.findViewById(R.id.topictype);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			JSONObject data = datas.getJSONObject(position);
			holder.topictype.setText(data.getString("size"));
			holder.topictype.setTag(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		TextView topictype;
	}
}
