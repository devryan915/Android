package com.kc.ihaigo.ui.recommend.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.RecommendActivity;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @ClassName: ViewPagerAdapter
 * @Description: 精选首页广告位viewpaper适配器
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:43:30
 * 
 */
public class ViewPagerAdapter extends PagerAdapter {

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context ctx;
	private ArrayList<ImageView> imageViews;
	private JSONArray ads;
	private BackCall call;
	public void setBackCall(BackCall call) {
		this.call = call;
	}

	public void setImages(final JSONArray ads) {
		this.ads = ads;
		imageViews = new ArrayList<ImageView>();
		for (int i = 0; i < ads.length(); i++) {
			final int position = i;
			ImageView imageView = new ImageView(this.ctx);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						call.deal(0, ads.getJSONObject(position));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			imageViews.add(imageView);
		}
	}

	public ViewPagerAdapter(final Context ctx) {
		this.ctx = ctx;
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions(R.drawable.empty_large_width);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return ads.length();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		android.widget.LinearLayout.LayoutParams lParams = new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
		try {
			String dataString = ads.get(position).toString();
			if (Utils.isJsonObject(dataString)) {
				if (ctx instanceof RecommendActivity) {
					JSONObject jsonObject = new JSONObject(dataString);
					imageLoader.displayImage(jsonObject.getString("image"),
							imageViews.get(position), options);
				}
			} else {
				imageLoader.displayImage(dataString, imageViews.get(position),
						options);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		((ViewPager) view).addView(imageViews.get(position), lParams);
		return imageViews.get(position);
	}
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}
}