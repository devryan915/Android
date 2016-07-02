package com.kc.ihaigo.ui.personal.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.EditDefaultAddressInfo;
import com.kc.ihaigo.ui.personal.MyMessageActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.shipping.adapter.ShippingItemAdapter;
import com.kc.ihaigo.util.Utils;

public class MyAddressAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray json;
	List<Map<String, String>> lists;

	// private int selectedPosition;
	//
	// public void setSelectedPosition(int selectedPosition) {
	// this.selectedPosition = selectedPosition;
	// }

	public MyAddressAdapter(Context ctx) {
		this.ctx = ctx;

	}

	public void setDatas(JSONArray json) {
		this.json = json;

	}

	@Override
	public int getCount() {
		return json.length();
	}

	// public void setIength(int length) {
	// this.length = length;
	// }

	@Override
	public Object getItem(int position) {
		try {
			return json.get(position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_my_address_item, null);
			holder = new ViewHolder();
			
			holder.userName = (TextView) converView.findViewById(R.id.userName);
			holder.userRegion = (TextView) converView
					.findViewById(R.id.userRegion);
			holder.infoRegion = (TextView) converView
					.findViewById(R.id.infoRegion);

			holder.seladdress = (ImageView) converView
					.findViewById(R.id.seladdress);
			holder.id = (TextView) converView.findViewById(R.id.tv_addrId);
			holder.contact_number = (TextView) converView
					.findViewById(R.id.contact_number);
			holder.postalCode = (TextView) converView
					.findViewById(R.id.postalCode);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		for (int i = 0; i <json.length(); i++) {
			try {
				// 用户id
				String userid = json.getJSONObject(position).getString("userId");
				// 收货信息id
				holder.id.setText(json.getJSONObject(position).getString("id"));
				// 联系人姓名
				holder.userName.setText(json.getJSONObject(position).getString(
						"contacts"));
				holder.userName.setTag(userid);
				// 收货区域
				holder.userRegion.setText(json.getJSONObject(position).getString(
						"userArea"));
				// 收货详细地址
				holder.infoRegion.setText(json.getJSONObject(position).getString(
						"userAddr"));
				// 状态
				String status = json.getJSONObject(position).getString(
						"status");
				holder.userRegion.setTag(status);
				if("1".equals(status)){
					holder.seladdress.setVisibility(View.GONE);
				}else if("2".equals(status)){
					holder.seladdress.setVisibility(View.VISIBLE);
				}
				// 联系人电话
				holder.contact_number.setText(json.getJSONObject(position)
						.getString("contactNumber"));
				// 邮编
				holder.postalCode.setText(json.getJSONObject(position).getString(
						"postalCode"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return converView;

	}

	public class ViewHolder {

		/**
		 * Id
		 */
		public TextView id;
		/**
		 * 用户名
		 */
		public TextView userName;
		/**
		 * 地区
		 */
		public TextView userRegion;
		/**
		 * 详细地址
		 */
		public TextView infoRegion;

		/**
		 * seladdress 选中状态
		 */
		public ImageView seladdress;

		/**
		 * 聯繫號碼
		 */
		public TextView contact_number;
		/**
		 * 邮政编码
		 */
		public TextView postalCode;
	}

}
