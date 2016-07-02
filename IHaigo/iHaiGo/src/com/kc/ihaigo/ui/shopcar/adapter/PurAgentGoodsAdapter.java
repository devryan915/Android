/**
 * @Title: PurAgentGoodsAdapter.java
 * @Package: com.kc.ihaigo.ui.shopcar.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月5日 上午10:01:59

 * @version V1.0

 */

package com.kc.ihaigo.ui.shopcar.adapter;

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
 * @ClassName: PurAgentGoodsAdapter
 * @Description: 购物车选择代购商，已买商品适配器
 * @author: ryan.wang
 * @date: 2014年7月5日 上午10:01:59
 * 
 */

public class PurAgentGoodsAdapter extends BaseAdapter {
	private int count;
	private Context ctx;
	private JSONArray datas;

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}
	public PurAgentGoodsAdapter(Context context) {
		this.ctx = context;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : (count == -1 ? datas.length() : (datas
				.length() > count ? count : datas.length()));
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	/*
	 * <p>Title: getItemId</p> <p>Description: </p>
	 * 
	 * @param position
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return 0;
	}

	/*
	 * <p>Title: getView</p> <p>Description: </p>
	 * 
	 * @param position
	 * 
	 * @param convertView
	 * 
	 * @param parent
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = ((LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.listview_puragent_goodsitem, null);
			holder = new ViewHolder();
			holder.puragent_goodslist_goddsname = (TextView) convertView
					.findViewById(R.id.puragent_goodslist_goddsname);
			holder.puragent_goodslist_goddsamount = (TextView) convertView
					.findViewById(R.id.puragent_goodslist_goddsamount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.puragent_goodslist_goddsname.setText(datas.getJSONObject(
					position).getString("name"));
			holder.puragent_goodslist_goddsamount.setText("x"
					+ datas.getJSONObject(position).getString("amount"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	class ViewHolder {
		TextView puragent_goodslist_goddsname;
		TextView puragent_goodslist_goddsamount;
	}
}
