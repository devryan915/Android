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

public class TopicPublishAdapter extends BaseAdapter {
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

	public TopicPublishAdapter(Context context) {
		this.ctx = context;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
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
					R.layout.listview_topic_publish, null);
			holder = new ViewHolder();
			/**
			 * 计算宽度
			 */
			convertView.findViewById(R.id.goods_item_publish).getLayoutParams().width = Utils
					.getScreenWidth(ctx);
			convertView.findViewById(R.id.test_publish).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							TopicPublishAdapter.this.call.deal(1000001,
									holder.deletegoods.getTag());
						}
					});

			holder.deletegoods = (TextView) convertView
					.findViewById(R.id.deletegoods_publish);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.experience = (TextView) convertView
					.findViewById(R.id.experience);
			holder.comments = (TextView) convertView
					.findViewById(R.id.comments);

			final HorizontalScrollView hsView = (HorizontalScrollView) convertView;
			final View view = convertView;
			// 删除
			holder.deletegoods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TopicPublishAdapter.this.call.deal(
							R.id.deletegoods_publish,
							holder.deletegoods.getTag());
					holder.deletegoods.setVisibility(View.GONE);
				}
			});
			// holder.deletegoods.measure(0, 0);
			// Handler handler = new Handler();
			// handler.post(new Runnable() {
			// @Override
			// public void run() {
			// hsView.scrollBy(holder.deletegoods.getMeasuredWidth(), 0);
			// }
			//
			// });
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 数据处理
		try {

			holder.name.setText(datas.get(position).getString("title"));
			String logItem = Utils.getCurrentTime(
					datas.get(position).getLong("createTime"), "MM-dd  HH:mm");
			holder.time.setText(logItem);
			holder.deletegoods.setTag(datas.get(position));
			/**
			 * 1、海淘攻略 2、经验交流 3、海淘晒单 4、个人转让 5、物流转运
			 */
			if (datas.get(position).getInt("type") == 1) {
				holder.experience.setText("海淘攻略");
			} else if (datas.get(position).getInt("type") == 2) {
				holder.experience.setText("经验交流");
			} else if (datas.get(position).getInt("type") == 3) {
				holder.experience.setText("海淘晒单");
			} else if (datas.get(position).getInt("type") == 4) {
				holder.experience.setText("个人转让");
			} else if (datas.get(position).getInt("type") == 5) {
				holder.experience.setText("物流转运");
			}

			holder.comments.setText(String.valueOf(datas.get(position).getInt(
					"count"))
					+ "条评论");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		/**
		 * 时间
		 */
		TextView time;
		/**
		 * 名称
		 */
		TextView name;
		/**
		 * 经验
		 */
		TextView experience;
		/**
		 * 评论
		 */
		TextView comments;
		/**
		 * 删除
		 */
		TextView deletegoods;
	}

}
