package com.kc.ihaigo.ui.selfwidget;

import com.kc.ihaigo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class PullUpRefreshListView extends ListView {
	private ScrollUpListener upListener;
	private ScrollDownListener downListener;
	private ScrollBottomListener bottomListener;
	private View mFooterView;

	public void setBottomListener(ScrollBottomListener bottomListener) {
		this.bottomListener = bottomListener;
	}

	public void setDownListener(ScrollDownListener downListener) {
		this.downListener = downListener;
	}

	public void setUpListener(ScrollUpListener upListener) {
		this.upListener = upListener;
	}

	/**
	 * 
	 * @ClassName: ScrollBottomListener
	 * @Description: 当滑动到底时候处理
	 * @author: ryan.wang
	 * @date: 2014年7月13日 下午5:29:19
	 * 
	 */
	public interface ScrollBottomListener {
		void deal();
	}

	/**
	 * 
	 * @ClassName: ScrollUpListener
	 * @Description: 往上滑动事件
	 * @author: ryan.wang
	 * @date: 2014年7月13日 下午9:27:49
	 * 
	 */
	public interface ScrollUpListener {
		void deal();
	}

	/**
	 * 
	 * @ClassName: ScrollDownListener
	 * @Description: 往下滑动事件
	 * @author: ryan.wang
	 * @date: 2014年7月13日 下午9:28:22
	 * 
	 */
	public interface ScrollDownListener {
		void deal();
	}

	public PullUpRefreshListView(Context context, AttributeSet attrs) {

		super(context, attrs);
		initEvent(context);
	}

	public PullUpRefreshListView(Context context) {

		super(context);
		initEvent(context);
	}

	public PullUpRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {

		super(context, attrs, defStyle);
		initEvent(context);
	}

	private void initEvent(Context context) {
		mFooterView = View.inflate(context, R.layout.load_more_footer, null);
		addFooterView(mFooterView);
		hideFooterView();
		this.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (bottomListener != null
							&& view.getLastVisiblePosition() == (view
									.getCount() - 1)) {
						bottomListener.deal();
					}
					break;
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		this.setOnTouchListener(new OnTouchListener() {
			float oldy = -1;
			float countUp = 0;// 记录一个方向累计次数，解决滑动位置跳动问题
			float countDown = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					oldy = event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					if (oldy - event.getRawY() < 0 && downListener != null) {
						// 往下滑动
						countDown++;
						if (countDown == 2) {
							downListener.deal();
							countDown = 0;
						}
					} else if (oldy - event.getRawY() > 0 && upListener != null) {
						// 向上滑动
						countUp++;
						if (countUp == 2) {
							upListener.deal();
							countUp = 0;
						}
					}
					oldy = event.getRawY();
					break;
				}
				return false;
			}
		});
	}

	public void setScrollBottomListener(ScrollBottomListener bottomListener) {
		this.bottomListener = bottomListener;
	}


	/**
	 * Hide the load more view(footer view)
	 */
	public void hideFooterView() {
		findViewById(R.id.footer).setVisibility(View.GONE);
		mFooterView.setVisibility(View.GONE);
	    mFooterView.setPadding(0, -mFooterView.getHeight(), 0, 0);
	}

	/**
	 * Show load more view
	 */
	public void showFooterView() {
		findViewById(R.id.footer).setVisibility(View.VISIBLE);
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.setPadding(0, 0, 0, 0);
		
	}
}