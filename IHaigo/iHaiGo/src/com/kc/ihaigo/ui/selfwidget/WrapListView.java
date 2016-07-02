package com.kc.ihaigo.ui.selfwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class WrapListView extends ListView {

	public WrapListView(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	public WrapListView(Context context) {

		super(context);

	}

	public WrapListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

	}
	
	
	

	

	

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	/*
	 * <p>Title: onDraw</p> <p>Description: </p>
	 * 
	 * @param canvas
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}