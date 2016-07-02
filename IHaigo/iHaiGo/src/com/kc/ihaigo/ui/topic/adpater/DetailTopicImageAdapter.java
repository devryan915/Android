/**
 * @Title: DetailTopicImageAdapter.java
 * @Package: com.kc.ihaigo.ui.topic.adpater
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月30日 上午10:06:17

 * @version V1.0

 */


package com.kc.ihaigo.ui.topic.adpater;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.fluent.Content;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.topic.TopicDetailActivity;
import com.kc.ihaigo.ui.topic.TopicResponse;
import com.kc.ihaigo.ui.topic.adpater.DetailResponseAdapter.ViewHolder;
import com.kc.ihaigo.util.ToastUtil;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.platformtools.Log;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: DetailTopicImageAdapter
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月30日 上午10:06:17
 *
 */

public class DetailTopicImageAdapter extends BaseAdapter {
	private Context ctx;
	private JSONArray datas ;

	/**
	 * 创建一个新的实例 DetailTopicImageAdapter. 
	 * <p>Title: </p>
	 * <p>Description: </p>
	 */

	public DetailTopicImageAdapter(Context ctx) {
		this.ctx = ctx;
	}
	
	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

	
	/**
	 * getter method
	 * @return the datas
	 */
	
	public JSONArray getDatas() {
		return datas;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.detail_image_listview_item, null);
			holder = new ViewHolder();
			holder.detail_image = (ImageView) convertView
					.findViewById(R.id.detail_topic_content_image);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 数据处理

		try {
			for (int i = 0; i < datas.length(); i++) {
				String imageurl = datas.getString(i);
				if(imageurl !=null){
					ImageLoader
					.getInstance()
					.displayImage(
							imageurl + "",
							holder.detail_image);
				}
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public class ViewHolder{
		public ImageView detail_image;
	}
}
