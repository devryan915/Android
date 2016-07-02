package com.kc.ihaigo.ui.shipping.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ChooseLogisticsAdapter extends BaseAdapter implements
		SectionIndexer {
	private List<JSONObject> list = null;
	private Context mContext;
	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public ChooseLogisticsAdapter(Context mContext) {
		this.mContext = mContext;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();

	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<JSONObject> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void setDatas(List<JSONObject> list) {
		this.list = list;
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final JSONObject mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listview_choose_item, null);
			viewHolder.name = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.logo = (ImageView) view.findViewById(R.id.logo);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		try {
			viewHolder.name.setTag(list.get(position).getString("id"));

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		try {
			if (position == getPositionForSection(section)) {
				viewHolder.tvLetter.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setText(mContent.getString("letter"));
			} else {
				viewHolder.tvLetter.setVisibility(View.GONE);
			}
			viewHolder.name.setText(this.list.get(position).getString("name"));
			imageLoader.displayImage(list.get(position).getString("icon"),
					viewHolder.logo, options, animateFirstListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView name;
		ImageView logo;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		try {
			return list.get(position).getString("letter").charAt(0);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return position;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			char firstChar = 0;
			try {
				firstChar = list.get(i).getString("letter").charAt(0);
			} catch (JSONException e) {

				e.printStackTrace();
			}
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}