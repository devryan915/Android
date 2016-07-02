package com.broadchance.wdecgrec.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class GridItemLayout extends RelativeLayout {
	public GridItemLayout(Context context) {
		super(context);
	}

	public GridItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GridItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, heightMeasureSpec));
		// Children are just made to fill our space.
		int childWidthSize = getMeasuredWidth();
		// 高度和宽度一样
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize,
				MeasureSpec.EXACTLY);
		heightMeasureSpec = widthMeasureSpec;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
