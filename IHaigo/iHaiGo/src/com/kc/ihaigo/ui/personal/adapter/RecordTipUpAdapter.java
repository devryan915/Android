package com.kc.ihaigo.ui.personal.adapter;

import org.json.JSONArray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.RecordConsusmptionAdapter.ViewHolder;

public class RecordTipUpAdapter extends BaseAdapter {

	private String[] datas;
	private Context ctx;
	private JSONArray json;

	public RecordTipUpAdapter(Context ctx) {
		this.ctx = ctx;
	}


	public void setDatas(String[] datas) {
		this.datas = datas;
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
					com.kc.ihaigo.R.layout.listview_record_tipup, null);
			holder = new ViewHolder();
			holder.recordName = (TextView) converView
					.findViewById(R.id.recordName);
			holder.recordLogo = (ImageView) converView
					.findViewById(R.id.recordLogo);
			holder.record_titm = (TextView) converView
					.findViewById(R.id.record_titm);
			holder.way = (TextView) converView.findViewById(R.id.way);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		
//		if(json.length() == 0){
//			holder.billing_fl.setVisibility(View.GONE);
//			holder.billing_pay.setVisibility(View.VISIBLE);
//			holder.billing_pay_hint.setText(ctx.getResources().getString(R.string.billing_pay_record));
//			holder.billing_empty_hint.setImageResource(R.drawable.empty_bg);
//		}
		return converView;
	}

	class ViewHolder {
		ImageView recordLogo;
		TextView recordName;
		TextView record_titm;
		TextView way;
	}
}
