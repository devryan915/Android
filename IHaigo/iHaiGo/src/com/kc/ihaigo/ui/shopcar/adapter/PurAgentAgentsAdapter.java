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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: PurAgentGoodsAdapter
 * @Description: 购物车选择代购商，已买商品适配器
 * @author: ryan.wang
 * @date: 2014年7月5日 上午10:01:59
 * 
 */

public class PurAgentAgentsAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray datas = new JSONArray();
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	public PurAgentAgentsAdapter(Context context) {
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
		return null;
	}

	@Override
	public long getItemId(int position) {
		try {
			return datas.getJSONObject(position).getLong("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = ((LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.listview_puragentsortlist_item, null);
			holder = new ViewHolder();
			holder.puragent_credit_rating = (RatingBar) convertView
					.findViewById(R.id.puragent_credit_rating);
			holder.puragent_head = (ImageView) convertView
					.findViewById(R.id.puragent_head);
			holder.puragent_name = (TextView) convertView
					.findViewById(R.id.puragent_name);
			holder.puragent_priceval = (TextView) convertView
					.findViewById(R.id.puragent_priceval);
			holder.puragent_feeval = (TextView) convertView
					.findViewById(R.id.puragent_feeval);
			holder.puragent_shippingval = (TextView) convertView
					.findViewById(R.id.puragent_shippingval);
			holder.puragent_serviceval = (TextView) convertView
					.findViewById(R.id.puragent_serviceval);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			String imageUrl = datas.getJSONObject(position).getString(
					"headPortrait");
			holder.puragent_head.setTag(imageUrl);
			imageLoader.displayImage(imageUrl, holder.puragent_head, options,
					animateFirstListener);
			holder.puragent_name.setText(datas.getJSONObject(position)
					.getString("agentsName"));
			holder.puragent_priceval.setText("￥"
					+ datas.getJSONObject(position).getString("price"));
			holder.puragent_credit_rating.setRating(Float.parseFloat(datas
					.getJSONObject(position).getString("credit")));
			holder.puragent_feeval.setText(datas.getJSONObject(position)
					.getString("charge"));
			holder.puragent_shippingval.setText(datas.getJSONObject(position)
					.getString("logistics"));
			holder.puragent_serviceval.setText(datas.getJSONObject(position)
					.getString("service"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	public class ViewHolder {
		public RatingBar puragent_credit_rating;
		public ImageView puragent_head;
		public TextView puragent_name;
		public TextView puragent_priceval;
		public TextView puragent_feeval;
		public TextView puragent_shippingval;
		public TextView puragent_serviceval;
	}
	/**
	 * @Title: setDatas
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param allGoods
	 *            void
	 * @throws
	 */

	public void setDatas(JSONArray allDatas) {
		this.datas = allDatas;
	}

	public JSONArray getDatas() {
		return datas;
	}
}
