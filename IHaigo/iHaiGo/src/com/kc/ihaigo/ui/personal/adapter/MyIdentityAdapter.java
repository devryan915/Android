package com.kc.ihaigo.ui.personal.adapter;

import java.util.List;
import java.util.Map;

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
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyIdentityAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray json;
	List<Map<String, String>> lists;
	private String head_image_url;

	// private int selectedPosition;
	//
	// public void setSelectedPosition(int selectedPosition) {
	// this.selectedPosition = selectedPosition;
	// }

	public MyIdentityAdapter(Context ctx,String head_image_url) {
		this.ctx = ctx;
		this.head_image_url = head_image_url;
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
					R.layout.listview_my_identity_item, null);
			holder = new ViewHolder();

			holder.userName = (TextView) converView.findViewById(R.id.userName);
			holder.userNameber = (TextView) converView
					.findViewById(R.id.userNameber);
			holder.personal_user_header = (ImageView) converView
					.findViewById(R.id.personal_user_header);
			holder.seladdress = (ImageView) converView
					.findViewById(R.id.seladdress);
			holder.id = (TextView) converView.findViewById(R.id.tv_cardId);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		
		try {
			String idcardImage = json.getJSONObject(position).getString(
					"idcardImage");
			String idcardImageBack = json.getJSONObject(position)
					.getString("idcardImageBack");
			String status = json.getJSONObject(position)
					.getString("status");

			holder.id.setText(json.getJSONObject(position).getString("id"));
			holder.id.setTag(idcardImage);
			holder.userName.setText(json.getJSONObject(position).getString("realName"));
			holder.userName.setTag(idcardImageBack);
			holder.userNameber.setText(json.getJSONObject(position)
					.getString("idNumber"));
			holder.userNameber.setTag(status);
			ImageLoader
			.getInstance()
			.displayImage(
					head_image_url + "",
					((ImageView) converView.findViewById(R.id.personal_user_header)));
			if("1".equals(status)){
				holder.seladdress.setVisibility(View.INVISIBLE);
			}else if("2".equals(status)){
				holder.seladdress.setVisibility(View.VISIBLE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
		 * 身份号
		 */
		public TextView userNameber;
		/**
		 * 用户头像
		 */
		public ImageView personal_user_header;
		/**
		 * seladdress 选中状态
		 */
		public ImageView seladdress;
	}

}
