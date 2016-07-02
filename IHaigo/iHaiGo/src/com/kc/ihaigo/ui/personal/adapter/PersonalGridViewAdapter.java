/**
 * @Title: PersonalGridViewAdapter.java
 * @Package: com.kc.ihaigo.ui.personal.adapter
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月10日 上午11:37:53

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal.adapter;

import com.kc.ihaigo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: PersonalGridViewAdapter
 * @Description: 个人中心主页中 gridView的数据
 * @author: helen.yang
 * @date: 2014年7月10日 上午11:37:53
 * 
 */

public class PersonalGridViewAdapter extends BaseAdapter {
	private Context content;
	private int[] gv_image;
	private int[] gv_title;
	/**
	 * 创建一个新的实例 PersonalGridViewAdapter.
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */

	public PersonalGridViewAdapter(Context ctx,int[] image,int[] title) {
		this.content = ctx;
		this.gv_image = image;
		this.gv_title = title;
	}

	/*
	 * <p>Title: getCount</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	@Override
	public int getCount() {
		return gv_image.length;
	}

	/*
	 * <p>Title: getItem</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */

	@Override
	public Object getItem(int position) {
		return gv_image[position];
	}

	/*
	 * <p>Title: getItemId</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * <p>Title: getView</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @param arg1
	 * 
	 * @param arg2
	 * 
	 * @return
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		final ViewHolder holder;
		if (converView == null) {
			converView = LayoutInflater.from(this.content).inflate(
					com.kc.ihaigo.R.layout.gridview_personal_item, null);
			holder = new ViewHolder();
			holder.personal_item = (ImageView) converView.findViewById(R.id.personal_iv_img);
			holder.personal_title = (TextView) converView.findViewById(R.id.personal_tv_title);
			
			converView.setTag(holder);
		}else{
			holder = (ViewHolder) converView.getTag();
		}
		
		holder.personal_item.setImageResource(gv_image[position]);
		holder.personal_title.setText(gv_title[position]);
		
		return converView;
	}

	
}

	class ViewHolder {
		/**
		 * 选项图标
		 */
		ImageView personal_item;
		/**
		 * 标题
		 */
		TextView personal_title;
	} 