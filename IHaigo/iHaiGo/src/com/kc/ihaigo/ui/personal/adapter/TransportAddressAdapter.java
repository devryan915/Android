/**
 * @Title: TransportAddressAdapter.java
 * @Package: com.kc.ihaigo.ui.personal.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年8月11日 下午4:23:44

 * @version V1.0

 */


package com.kc.ihaigo.ui.personal.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.TransCommentAdpater.ViewHolder;
import com.kc.ihaigo.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: TransportAddressAdapter
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年8月11日 下午4:23:44
 *
 */

public class TransportAddressAdapter extends BaseAdapter {

	private Context ctx;
	private JSONArray datas;
	/**
	 * 创建一个新的实例 TransportAddressAdapter. 
	 * <p>Title: </p>
	 * <p>Description: </p>
	 */

	public TransportAddressAdapter(Context ctx) {
		this.ctx = ctx;
	}
	
	
	/**
	 * getter method
	 * @return the datas
	 */
	
	public JSONArray getDatas() {
		return datas;
	}


	/**
	 * setter method
	 * @param datas the datas to set
	 */
	
	public void setDatas(JSONArray datas) {
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
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_trans_addr_item, null);
			holder = new ViewHolder();
			holder.warehouse_address = (TextView) convertView.findViewById(R.id.warehouse_address);
			holder.warehouse_city = (TextView) convertView.findViewById(R.id.warehouse_city);
			holder.warehouse_firstName = (TextView) convertView.findViewById(R.id.warehouse_firstName);
			holder.warehouse_LastName = (TextView) convertView.findViewById(R.id.warehouse_LastName);
			holder.warehouse_name = (TextView) convertView.findViewById(R.id.warehouse_name);
			holder.warehouse_state = (TextView) convertView.findViewById(R.id.warehouse_state);
			holder.warehouse_tel = (TextView) convertView.findViewById(R.id.warehouse_tel);
			holder.warehouse_unit = (TextView) convertView.findViewById(R.id.warehouse_unit);
			holder.warehouse_zip_code = (TextView) convertView.findViewById(R.id.warehouse_zip_code);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 数据处理
		try {
			holder.warehouse_address.setText(datas.getJSONObject(position).getString("address"));
			holder.warehouse_city.setText(datas.getJSONObject(position).getString("city"));
			holder.warehouse_firstName.setText(datas.getJSONObject(position).getString("firstName"));
			holder.warehouse_LastName.setText(datas.getJSONObject(position).getString("lastName"));
			holder.warehouse_name.setText(datas.getJSONObject(position).getString("name"));
			holder.warehouse_state.setText(datas.getJSONObject(position).getString("state"));
			holder.warehouse_tel.setText(datas.getJSONObject(position).getString("tel"));
			holder.warehouse_unit.setText(datas.getJSONObject(position).getString("unit"));
			holder.warehouse_zip_code.setText(datas.getJSONObject(position).getString("zipCode"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder{
		/**
		 * 仓库名
		 */
		TextView warehouse_name;
		/**
		 * 名
		 */
		TextView warehouse_firstName;
		/**
		 * 姓
		 */
		TextView warehouse_LastName;
		/**
		 * 地址
		 */
		TextView warehouse_address;
		/**
		 * 单元
		 */
		TextView warehouse_unit;
		/**
		 * 城市
		 */
		TextView warehouse_city;
		/**
		 * 州
		 */
		TextView warehouse_state;
		/**
		 * 邮编
		 */
		TextView warehouse_zip_code;
		/**
		 * 电话
		 */
		TextView warehouse_tel;
	}
}
