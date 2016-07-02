/**
 * @Title: MenufAdapter.java
 * @Package: com.kc.ihaigo.ui.recommend
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年6月26日 下午3:46:01

 * @version V1.0

 */

package com.kc.ihaigo.ui.recommend.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.R;

/**
 * @ClassName: MenufAdapter
 * @Description: 分类搜索左边菜单ListView适配器
 * @author: ryan.wang
 * @date: 2014年6月26日 下午3:46:01
 * 
 */

public class MenufAdapter extends BaseAdapter {

	private JSONArray datas;
	private Context ctx;

	public MenufAdapter(Context ctx) {
		this.ctx = ctx;
	}

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder;
		// if (converView == null) {
		converView = LayoutInflater.from(this.ctx).inflate(
				com.kc.ihaigo.R.layout.listview_sortsearch_item, null);
		holder = new ViewHolder();
		holder.menuName = (TextView) converView
				.findViewById(R.id.sortsearch_menufname_tv);
		holder.imageLine = (ImageView) converView
				.findViewById(R.id.sortsearch_menusel_iv);
		// } else {
		// holder = (ViewHolder) converView.getTag();
		// }
		try {
			converView.setTag(datas.getJSONObject(position).getString("id"));
			holder.menuName.setText(datas.getJSONObject(position).getString(
					"name"));
			holder.menuName.setTag(datas.getJSONObject(position).getJSONArray(
					"items"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return converView;
	}

	class ViewHolder {
		TextView menuName;
		ImageView imageLine;
	}
}
