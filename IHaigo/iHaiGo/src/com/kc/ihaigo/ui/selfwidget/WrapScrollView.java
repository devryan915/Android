/**
 * @Title: WrapScrollView.java
 * @Package: com.kc.ihaigo.ui.selfwidget
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月11日 下午4:23:35

 * @version V1.0

 */

package com.kc.ihaigo.ui.selfwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @ClassName: WrapScrollView
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年7月11日 下午4:23:35
 * 
 */

public class WrapScrollView extends ScrollView {
	private ScrollViewListener scrollViewListener = null;
	public interface ScrollViewListener {
		void onScrollChanged(WrapScrollView scrollView, int x, int y, int oldx,
				int oldy);

	}
	public WrapScrollView(Context context) {
		super(context);
	}

	public WrapScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WrapScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}
	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}
}
