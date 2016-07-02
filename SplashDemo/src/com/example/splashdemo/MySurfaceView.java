package com.example.splashdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.transition.Scene;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.widget.Toast;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {

	private Context context = null;

	// 用于控制SurfaceView
	private SurfaceHolder sfh = null;
	// 声明一个画笔
	private Paint paint;
	// 声明一条线程
	private Thread th = null;
	// 用于控制线程的标识符
	private boolean flag;
	// 声明一个画布
	private Canvas canvas;
	// 定义高和宽
	public static int screenW, screenH;

	/** 当前页的序号，第一页是0 */
	int currentViewNo = 0;
	//计时器的分量，比如设为30，就是同一个逻辑执行30次，设置为-1，主要是因为控制是否是第一次进入应用
	//第一次进入应用，没有需要控制计时器的分量，计时器的分量只是控制地图转动的时间
	//第一次进入应用地球不需要转动，所以oneTopCtrl的值不能大于-1，在logic函数中是这么定义的
	private int oneTopCtrl = -1;
	float oneEartRatOut = 0;	//旋转的角度
	int sign = -1;	//手势滑动方向标
	
	private Bitmap bmpEart = null;	//地图资源
	private int bmpEartX, bmpEartY ;	//地球资源的坐标
	private int ratX, ratY ;	//旋转中心点的坐标
	
	public MySurfaceView(Context context) {
		super(context);
		this.context = context;
		// ///////////SurfaceView框架/////////////////////////////
		sfh = (SurfaceHolder) this.getHolder();
		sfh.addCallback(this);
		canvas = new Canvas();
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		this.setFocusable(true);	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		screenW = this.getWidth();
		screenH = this.getHeight();
		// ///////////资源初始化////////////////////////////////
		initData();
				
		flag = true;
		th = new Thread(this);
		th.start();
	}

	/**
	 * 绘制画面
	 */
	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			if (canvas != null) {
				canvas.drawColor(context.getResources().getColor(R.color.bg));//刷新屏幕，用背景色刷新
				canvas.save();	//注意canvas.save()与canvas,restore()是成对存在的
				canvas.rotate(oneEartRatOut, ratX, ratY);	//旋转控制的角度
				canvas.drawBitmap(bmpEart, bmpEartX, bmpEartY, paint);//绘制地图
				canvas.restore();
			}
		} catch (Exception e) {

		} finally {
			if (canvas != null) {
				sfh.unlockCanvasAndPost(canvas);
			}
		}
	}

	/**
	 * 页面逻辑
	 */
	public void logic() {
		//oneTopCtrl控制旋转的时间和角度，设置为30，每次增减3*sign<-1或者1>，所以每次旋转的角度就是90度
		if (oneTopCtrl >= 0 && oneTopCtrl < 30) {
			oneTopCtrl++;
			oneEartRatOut += 3 * sign;
			if (oneEartRatOut > 360) {
				oneEartRatOut %= 360;
			}
		}
	}

	/**
	 * 触屏监听事件
	 */
	private VelocityTracker mVt = null;

	int detaX = 0; // 手指移动的x轴相对位移
	int tmpX = 0, tmpY = 0; // Move时刻坐标点

	int startX = 0, startY = 0;
	int endX = 0, endY = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				// 手指按下
				startX = (int) event.getX();
				startY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				// 添加采样点
				// mVt.addMovement(event);
				tmpX = (int) event.getX();
				tmpY = (int) event.getY();
				break;
			case MotionEvent.ACTION_UP:
				// 手指移动
				endX = (int) event.getX();
				endY = (int) event.getY();
				int tmp = (endX - startX);

				int dir = judgeDir(startX, startY, endX, endY);

				if (dir == 1) {// right
					if (currentViewNo > 0) {
						sign = 1;
						oneTopCtrl = 0;
						currentViewNo--;
					}
				} else if (dir == 2) {// left
					if (currentViewNo < 3) {
						sign = -1;
						oneTopCtrl = 0;
						currentViewNo++;
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
				// 触摸事件结束
				// 设置速度的时间单位 1代表每毫秒移动多少像素 1000代表每秒移动多少像素
				// 这个方法在获得速度之前必须要设置
				mVt.clear();// 清空
				mVt.recycle();// 回收vt对象的内存
				break;
			}
		}
		return true;
	}

	/**
	 * 手势方向判别
	 * 
	 */
	private int judgeDir(int startX, int startY, int endX, int endY) {

		if ((endX - startX) > 0 && Math.abs(endX - startX) > screenW / 3) {
			// 方向是正右方 1
			return 1;
		} else if ((endX - startX) < 0 && Math.abs(endX - startX) > screenW / 3) {
			// 方向是正左方 2
			return 2;
		}
		return 0;
	}

	@Override
	public void run() {
		while (flag) {
			long start = System.currentTimeMillis();
			myDraw();
			logic();
			long end = System.currentTimeMillis();
			try {
				if (end - start < 50) {
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 画布状态改变监听事件
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 画布被摧毁事件
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}

	/**
	 * 初始化所用资源
	 */
	private void initData() {
		//初始化地球资源
		bmpEart = (Bitmap) BitmapFactory.decodeResource(getResources(),
				R.drawable.revolve_ground);
		//初始化所有资源坐标
		initAllXY();
	}
	/**
	 * 初始化所有坐标点
	 */
	private void initAllXY() {
		bmpEartX = screenW / 2 - bmpEart.getWidth() / 2;
		bmpEartY = screenH - bmpEart.getHeight() / 4;
		
		ratX = screenW / 2;
		ratY = screenH + bmpEart.getHeight() / 4;
	}
}
