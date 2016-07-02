package com.kc.ihaigo.ui.personal.adapter;

import java.util.ArrayList;
import java.util.List;

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

/**
 * @ClassName: ShopcarGoodsAdapter
 * @Description: 购物车商量列表
 * @author: ryan.wang
 * @date: 2014年7月3日 下午4:28:01
 * 
 */

public class TopicRespondAdpater extends BaseAdapter {
	private Context ctx;

	private boolean isEdit = false;
	private BackCall call;
	private List<JSONObject> datas = new ArrayList<JSONObject>();

	public List<JSONObject> getDatas() {
		return datas;
	}

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public void setCall(BackCall call) {
		this.call = call;
	}

	public void setDatas(List<JSONObject> datas) {
		this.datas = datas;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public TopicRespondAdpater(Context context) {
		this.ctx = context;

		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	/*
	 * <p>Title: getCount</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	// public void setCheckedTotal(boolean isCheckedTotal) {
	// this.isCheckedTotal = isCheckedTotal;
	// }

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	/*
	 * <p>Title: getItem</p> <p>Description: </p>
	 * 
	 * @param position
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return position;
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

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutinflater = (LayoutInflater) this.ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutinflater.inflate(
					R.layout.listview_topic_respond, null);
			holder = new ViewHolder();
			/**
			 * 计算宽度
			 */
			convertView.findViewById(R.id.goods_item_topic).getLayoutParams().width = Utils
					.getScreenWidth(ctx);
			convertView.findViewById(R.id.test_topic).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							TopicRespondAdpater.this.call.deal(1000001,
									holder.deletegoods.getTag());
						}
					});

			holder.deletegoods = (TextView) convertView
					.findViewById(R.id.deletegoods);
			holder.nickName = (TextView) convertView
					.findViewById(R.id.nickName);
			holder.createTime = (TextView) convertView
					.findViewById(R.id.createTime);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.headImage = (ImageView) convertView
					.findViewById(R.id.headImage);

			final HorizontalScrollView hsView = (HorizontalScrollView) convertView;
			final View view = convertView;
			// 删除

			holder.deletegoods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TopicRespondAdpater.this.call.deal(R.id.deletegoods,
							holder.deletegoods.getTag());
					holder.deletegoods.setVisibility(View.GONE);
				}
			});
			// holder.deletegoods.measure(0, 0);
			// Handler handler = new Handler();
			// handler.post(new Runnable() {
			// @Override
			// public void run() {
			// hsView.scrollBy(-holder.deletegoods.getMeasuredWidth(), 0);
			// // hsView.scrollTo(-holder.deletegoods.getMeasuredWidth(),
			// // 0);
			// }
			//
			// });
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 数据处理
		try {
			imageLoader.displayImage(
					datas.get(position).getString("headImage"),
					holder.headImage, options, animateFirstListener);
			holder.deletegoods.setTag(datas.get(position));
			holder.nickName.setText(datas.get(position).getString("nickName"));
			String logItem = Utils.getCurrentTime(
					datas.get(position).getLong("createTime"), "MM-dd  HH:mm");
			holder.createTime.setText(logItem);
			holder.content.setText(datas.get(position).getString("content"));

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
		 * 删除了
		 */
		TextView deletegoods;

	}

}
