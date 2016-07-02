/**
 * @Title: MenucAdapter.java
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
import android.widget.TextView;

import com.kc.ihaigo.R;

/**
 * @ClassName: MenucAdapter
 * @Description: 分类搜索左边菜单GridView适配器
 * @author: ryan.wang
 * @date: 2014年6月26日 下午3:46:01
 * 
 */

public class MenucAdapter extends BaseAdapter {

	private JSONArray datas;
	private Context ctx;

	// private int selectedPosition;
	//
	// public void setSelectedPosition(int selectedPosition) {
	// this.selectedPosition = selectedPosition;
	// }

	public MenucAdapter(Context ctx) {
		this.ctx = ctx;
	}

	/*
	 * <p>Title: getCount</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.length();
	}

	/*
	 * <p>Title: getItem</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	/*
	 * <p>Title: getItemId</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return position;
	}

	/*
	 * <p>Title: getView</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @param arg1
	 * 
	 * @param arg2
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					com.kc.ihaigo.R.layout.gridview_sortsearch_item, null);
			holder = new ViewHolder();
			holder.menuName = (TextView) converView
					.findViewById(R.id.sortsearch_menucname_tv);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		try {
			holder.menuName.setText(datas.getJSONObject(position).getString(
					"name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return converView;
	}

	public class ViewHolder {
		public TextView menuName;
	}
}
