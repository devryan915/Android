/**
 * @Title: WrapGridView.java
 * @Package: com.kc.ihaigo.ui.selfwidget
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月12日 下午3:49:25

 * @version V1.0

 */

package com.kc.ihaigo.ui.selfwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @ClassName: WrapGridView
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年7月12日 下午3:49:25
 * 
 */

public class WrapGridView extends GridView {
	public WrapGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapGridView(Context context) {
		super(context);
	}

	public WrapGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
