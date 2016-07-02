/**
 * @Title: RecRateAdapter.java
 * @Package: com.kc.ihaigo.ui.recommend.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月2日 下午3:25:47

 * @version V1.0

 */

package com.kc.ihaigo.ui.recommend.adapter;

import java.math.BigDecimal;

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
 * @ClassName: RecRateAdapter
 * @Description: 显示最新汇率
 * @author: ryan.wang
 * @date: 2014年7月2日 下午3:25:47
 * 
 */

public class RecRateAdapter extends BaseAdapter {
	private JSONArray datas;
	private Context context;
	public RecRateAdapter(Context context, JSONArray datas) {
		this.context = context;
		this.datas = datas;
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

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(
					R.layout.listview_recrate_item, null);
			holder.currencyName = (TextView) convertView
					.findViewById(R.id.currencyName);
			holder.currencyRate = (TextView) convertView
					.findViewById(R.id.currencyRate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			JSONObject data = datas.getJSONObject(position);
			holder.currencyName.setText(data.getString("name") + "("
					+ data.getString("code") + ")");
			double rateRate = data.getDouble("rate") * 0.01;
			holder.currencyRate.setText("1:"
					+ new BigDecimal(rateRate).setScale(4,
							BigDecimal.ROUND_HALF_UP).doubleValue());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	class ViewHolder {
		TextView currencyName;
		TextView currencyRate;
	}
}
