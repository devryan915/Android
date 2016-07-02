package com.kc.ihaigo.ui.personal.adapter;

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
import com.kc.ihaigo.util.Utils;

public class RecordConsusmptionAdapter extends BaseAdapter {

	private String[] datas;
	private Context ctx;
	
	private JSONArray json;
	
	private String type;
	private String pay;
	private String payType;
	private String content;
	private String amount;
//	private long createTime;
	private String createTime;

	// private int selectedPosition;
	//
	// public void setSelectedPosition(int selectedPosition) {
	// this.selectedPosition = selectedPosition;
	// }

	public RecordConsusmptionAdapter(Context ctx) {
		this.ctx = ctx;
	}


	public void setDatas(JSONArray json) {
		this.json = json;
	}

	@Override
	public int getCount() {
		return json.length();
	}


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
		ViewHolder holder;
		if (converView == null) {
			converView = LayoutInflater.from(this.ctx).inflate(
					com.kc.ihaigo.R.layout.listview_record_consumption, null);
			holder = new ViewHolder();
			holder.recordName = (TextView) converView
					.findViewById(R.id.recordName);
			holder.recordLogo = (ImageView) converView
					.findViewById(R.id.recordLogo);
			holder.record_time = (TextView) converView
					.findViewById(R.id.record_time);
			holder.way = (TextView) converView.findViewById(R.id.way);
			holder.amount = (TextView) converView.findViewById(R.id.amount);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		
//		holder.recordName.setText(datas[position]);
		
		try {
			type = json.getJSONObject(position).getString("type");
			pay = json.getJSONObject(position).getString("pay");
			payType = json.getJSONObject(position).getString("payType");
			content = json.getJSONObject(position).getString("content");
			amount = json.getJSONObject(position).getString("amount");
//			createTime = json.getJSONObject(position).getLong("createTime");
			createTime = json.getJSONObject(position).getString("createTime");
			if("1".equals(payType)){
				holder.recordName.setText("支付订单");
			}
			
			String time = Utils.getCurrentTime(Long.parseLong(createTime), "yyyy-MM-dd  HH:mm");
			holder.record_time.setText(time);
			holder.way.setText(content);
			String cutamount = "-￥"+amount;
			holder.amount.setText(cutamount);
			if("1".equals(pay)){
				holder.recordLogo.setImageResource(R.drawable.transfer_company);
			}else if("2".equals(pay)){
				holder.recordLogo.setImageResource(R.drawable.tariff);
			}else if("3".equals(pay)){
				holder.recordLogo.setImageResource(R.drawable.hotboom_merchant);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return converView;
	}

	class ViewHolder {
		
		ImageView recordLogo;
		TextView recordName;
		TextView record_time;
		TextView way;
		TextView amount;
	}
}
