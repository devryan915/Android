package com.kc.ihaigo.ui.personal.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class TransCommentAdpater extends BaseAdapter {
	private Context ctx;

	private boolean isEdit = false;
	private JSONArray datas = new JSONArray();


	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

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

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public TransCommentAdpater(Context context) {
		this.ctx = context;

		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_all_user_trans_comment_item, null);
			holder = new ViewHolder();
			
			holder.headImage = (ImageView) convertView.findViewById(R.id.headImage);
			holder.nickName = (TextView) convertView.findViewById(R.id.nickName);
			holder.charge = (TextView) convertView.findViewById(R.id.puragent_charge);
			holder.trans = (TextView) convertView.findViewById(R.id.puragent_trans);
			holder.service = (TextView) convertView.findViewById(R.id.puragent_service);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.createTime = (TextView) convertView.findViewById(R.id.createTime);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 数据处理
		try {
//			datas.getJSONObject(position);
			imageLoader.displayImage(
					datas.getJSONObject(position).getString("headPortrait"),
					holder.headImage, options, animateFirstListener);
			holder.nickName.setText(datas.getJSONObject(position).getString("nickName"));
			String logItem = Utils.getCurrentTime(
					datas.getJSONObject(position).getLong("createTime"), "MM-dd  HH:mm");
			holder.createTime.setText(logItem);
			holder.content.setText(datas.getJSONObject(position).getString("content"));
			// 收费标准 1、1星 2、2星 3、3星 4、4星 4、5星
			holder.charge.setText(datas.getJSONObject(position).getString(
					"charge"));
			// 物流速度 1、1星 2、2星 3、3星 4、4星 4、5星
			holder.service.setText(datas.getJSONObject(position).getString(
					"service")) ;
			// 服务态度 1、1星 2、2星 3、3星 4、4星 4、5星
			holder.trans.setText(datas.getJSONObject(position).getString(
					"logistics")) ;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	class ViewHolder {
		/**
		 * LOGO
		 */
		ImageView headImage;
		/**
		 * 姓名
		 */
		TextView nickName;
		/**
		 * 时间
		 */
		TextView createTime;
		/**
		 * 内容
		 */
		TextView content;
		/**
		 * 收费
		 */
		TextView charge;
		/**
		 * 物流
		 */
		TextView trans;
		/**
		 * 服务
		 */
		TextView service;
	}

}
