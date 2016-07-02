package com.broadchance.wdecgrec.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.login.WelcomeActivity;

/**
 * 
 * @ClassName: ViewPagerAdapter
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年7月31日 下午5:14:16
 * 
 */
public class WelcomePagerAdapter extends PagerAdapter {

	private Context ctx;
	private ArrayList<ImageView> imageViews;

	public WelcomePagerAdapter(final WelcomeActivity ctx) {
		this.ctx = ctx;
		// imageViews = new ArrayList<ImageView>();
		// ImageView imageView = new ImageView(this.ctx);
		// imageView.setScaleType(ScaleType.CENTER_CROP);
		// imageView.setBackgroundDrawable(ctx.getResources().getDrawable(
		// R.drawable.welcome1));
		// imageViews.add(imageView);
		// imageView = new ImageView(this.ctx);
		// imageView.setScaleType(ScaleType.CENTER_CROP);
		// imageView.setBackgroundDrawable(ctx.getResources().getDrawable(
		// R.drawable.welcome2));
		// imageViews.add(imageView);
		// imageView = new ImageView(this.ctx);
		// imageView.setScaleType(ScaleType.CENTER_CROP);
		// imageView.setBackgroundDrawable(ctx.getResources().getDrawable(
		// R.drawable.welcome3));
		// imageViews.add(imageView);
		// imageView = new ImageView(this.ctx);
		// imageView.setScaleType(ScaleType.CENTER_CROP);
		// imageView.setBackgroundDrawable(ctx.getResources().getDrawable(
		// R.drawable.welcome4));
		// imageViews.add(imageView);
		// imageView = new ImageView(this.ctx);
		// imageView.setScaleType(ScaleType.CENTER_CROP);
		// imageView.setBackgroundDrawable(ctx.getResources().getDrawable(
		// R.drawable.welcome5));
		// imageViews.add(imageView);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		android.widget.LinearLayout.LayoutParams lParams = new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
		// switch (position) {
		// case 0 :
		// imageViews.get(position).setBackgroundDrawable(
		// ctx.getResources().getDrawable(R.drawable.welcome1));
		// break;
		// case 1 :
		// imageViews.get(position).setBackgroundDrawable(
		// ctx.getResources().getDrawable(R.drawable.welcome2));
		// break;
		//
		// case 2 :
		// imageViews.get(position).setBackgroundDrawable(
		// ctx.getResources().getDrawable(R.drawable.welcome3));
		// break;
		//
		// case 3 :
		// imageViews.get(position).setBackgroundDrawable(
		// ctx.getResources().getDrawable(R.drawable.welcome4));
		// break;
		//
		// case 4 :
		// imageViews.get(position).setBackgroundDrawable(
		// ctx.getResources().getDrawable(R.drawable.welcome5));
		// break;
		// default :
		// break;
		// }
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