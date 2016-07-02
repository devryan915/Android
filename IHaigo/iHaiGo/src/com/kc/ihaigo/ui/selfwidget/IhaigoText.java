package com.kc.ihaigo.ui.selfwidget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 
 * @ClassName: IhaigoText
 * @Description: 解决跑马灯
 * @author: ryan.wang
 * @date: 2014年8月5日 上午11:55:53
 * 
 */
public class IhaigoText extends TextView {
	public IhaigoText(Context con) {
		super(con);
	}

	public IhaigoText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public IhaigoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public boolean isFocused() {
		return true;
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
	}
}