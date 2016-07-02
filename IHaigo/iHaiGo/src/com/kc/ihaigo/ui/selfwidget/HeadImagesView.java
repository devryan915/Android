/**
 * @Title: HeadImagesView.java
 * @Package: com.kc.ihaigo.ui.selfwidget
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月1日 下午1:42:17

 * @version V1.0

 */

package com.kc.ihaigo.ui.selfwidget;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.recommend.adapter.ViewPagerAdapter;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: HeadImagesView
 * @Description: 自定义精选首页，广告首页，产品详情页面多个图片展示
 * @author: ryan.wang
 * @date: 2014年7月1日 下午1:42:17
 * 
 */

public class HeadImagesView extends FrameLayout {
	private View progress_indicator;
	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;
	private ViewPager viewPager;
	private JSONArray ads;
	private Context context;
	private BackCall call;
	public void setBackCall(BackCall call) {
		this.call = call;
	}

	public void setAvdImages(JSONArray ads) {
		if (ads != null && ads.length() > 0) {
			this.ads = ads;
			final int length = Utils.getScreenWidth(context) / ads.length();// 定义平均长度
			progress_indicator.getLayoutParams().width = length;
			ViewPagerAdapter vpaAdapter = new ViewPagerAdapter(context);
			vpaAdapter.setImages(ads);
			vpaAdapter.setBackCall(call);
			viewPager.setAdapter(vpaAdapter);
			viewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					TranslateAnimation animation = new TranslateAnimation(
							currentItem * length, arg0 * length, 0, 0);
					animation.setFillAfter(true);
					animation.setDuration(100);
					progress_indicator.startAnimation(animation);
					currentItem = arg0;
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});
			measure(0, 0);
		}
	}

	private Handler handlerViewPaper;
	private TextView googsName;

	public HeadImagesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.HeadImagesView);
		LayoutInflater.from(context).inflate(R.layout.common_images_head, this);
		LinearLayout indicatorbg = (LinearLayout) findViewById(R.id.indicator);
		progress_indicator = findViewById(R.id.progress_indicator);

		viewPager = (ViewPager) findViewById(R.id.headimages);

		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			switch (typedArray.getIndex(i)) {
				case R.styleable.HeadImagesView_indicatorheight :
					int indicatorheight = typedArray.getDimensionPixelSize(
							R.styleable.HeadImagesView_indicatorheight, 2);
					indicatorbg.getLayoutParams().height = indicatorheight;
					break;
				case R.styleable.HeadImagesView_indicatorbgcolor :
					int bgcolor = typedArray.getColor(
							R.styleable.HeadImagesView_indicatorbgcolor,
							R.color.progressbg);
					indicatorbg.setBackgroundColor(bgcolor);
					break;
				case R.styleable.HeadImagesView_indicatordrawable :
					Drawable indicatorcolor = typedArray
							.getDrawable(R.styleable.HeadImagesView_indicatordrawable);
					progress_indicator.setBackgroundDrawable(indicatorcolor);
					break;
				case R.styleable.HeadImagesView_autoplay :
					boolean autoplay = typedArray.getBoolean(
							R.styleable.HeadImagesView_autoplay, false);
					if (autoplay) {
						scheduledExecutorService = Executors
								.newSingleThreadScheduledExecutor();
						scheduledExecutorService.scheduleAtFixedRate(
								new ScrollTask(), 1, 2, TimeUnit.SECONDS);
						handlerViewPaper = new Handler() {
							public void handleMessage(android.os.Message msg) {
								viewPager.setCurrentItem(currentItem);
							};
						};
					}
					break;
				case R.styleable.HeadImagesView_displayfooter :
					boolean displayfooter = typedArray.getBoolean(
							R.styleable.HeadImagesView_displayfooter, false);
					if (displayfooter) {
						googsName = (TextView) findViewById(R.id.common_goodsname_tv);
						googsName.setVisibility(View.VISIBLE);
					}
					break;
				default :
					break;
			}
		}
		typedArray.recycle();

	}

	@Override
	public void draw(Canvas canvas) {

		super.draw(canvas);
	}

	private class ScrollTask implements Runnable {
		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % ads.length();
				handlerViewPaper.obtainMessage().sendToTarget();
			}
		}
	}

	/**
	 * @Title: setGoodsName
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param charSequenceExtra
	 *            void
	 * @throws
	 */

	public void setGoodsName(CharSequence charSequenceExtra) {
		googsName.setText(charSequenceExtra);
	}
}
