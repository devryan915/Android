package com.langlang.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.langlang.data.SleepEvent;
import com.langlang.data.SleepGraphData;
import com.langlang.data.SleepQualityRect;
import com.langlang.data.SleepState;
import com.langlang.elderly_langlang.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class SleepGraphView extends View {
	// LF/HF曲线颜色
	private static final int clrLfHf = Color.rgb(97, 170, 200);	
//	private static final int clrLfHf = Color.rgb(50, 205, 153);	//#32CD99
	
	// 睡眠质量颜色
	private static final int clrAwake = Color.rgb(239, 152, 29);
//	private static final int clrLight = Color.rgb(186, 225, 243);
	private static final int clrLight = Color.rgb(51, 176, 233);//#33b0e9
	private static final int clrDeep = Color.rgb(189, 220, 124);
	
	private static final int GRAPH_HEIGHT = 360;
	private static final int TIME_HEIGHT = 26;
	private static final int RESP_HEIGHT = 17;
	private static final int BLANK_HEIGHT = 24;
	
	private static final int[] RESP_COLORS = {
		Color.rgb(0, 0, 0),
		Color.rgb(0x0D, 0x33, 0x0), //#0D3300
		Color.rgb(0x0D, 0x33, 0x0), //#0D3300
		Color.rgb(0x0D, 0x33, 0x0), //#0D3300
		Color.rgb(0x0D, 0x33, 0x0), //#0D3300
		Color.rgb(0x0D, 0x33, 0x0), //#0D3300
		Color.rgb(0x0D, 0x33, 0x0), //#0D3300
		Color.rgb(0x07, 0x45, 0x05),//#074505
		Color.rgb(0x00, 0x5F, 0x0D),//#005F0D
		Color.rgb(0x00, 0x8B, 0x18),//#008B18
		Color.rgb(0x26, 0x9E, 0x30),//#269E30
		Color.rgb(0x61, 0xB4, 0x19),//#61B419
		Color.rgb(0x9A, 0xC6, 0x1B),//#9AC61B
		Color.rgb(0xE1, 0xF6, 0xBA),//#E1F6BA
		Color.rgb(0xF0, 0xFF, 0xE5),//#F0FFE5
		//----------------------------
		//Color.rgb(0xFC, 0xFF, 0xFD),//#FCFFFD //15
		//----------------------------
//		Color.rgb(0xFF, 0xFF, 0xFF),//#FFFFFF //16
		//----------------------------
		Color.rgb(0xFF, 0xF8, 0xF9),//#FFF8F9 //17
		//----------------------------
		Color.rgb(0xFF, 0xED, 0xE8),//#FFEDE8
		Color.rgb(0xFF, 0xE1, 0xD3),//#FFE1D3
		Color.rgb(0xFF, 0xC7, 0xA4),//#FFC7A4
		Color.rgb(0xEF, 0x70, 0x15),//#EF7015
		Color.rgb(0xE8, 0x3F, 0x0A),//#E83F0A
		Color.rgb(0xDD, 0x12, 0x0A),//#DD120A
		Color.rgb(0xBD, 0x00, 0x09),//#BD0009
		Color.rgb(0x87, 0x00, 0x05),//#870005
		Color.rgb(0x6B, 0x00, 0x03),//#6B0003
		Color.rgb(0x4D, 0x00, 0x01),//#4D0001
		Color.rgb(0x4D, 0x00, 0x01),//#4D0001
		Color.rgb(0x4D, 0x00, 0x01),//#4D0001
		Color.rgb(0x4D, 0x00, 0x01),//#4D0001
		Color.rgb(0x4D, 0x00, 0x01),//#4D0001
		Color.rgb(0x4D, 0x00, 0x01),//#4D0001
	};
	
	private Paint mPaint = null;
	private SleepGraphData mGraphData = null;
	
	//获得控件自身大小
	private int mHeight;
	private int mWidth;
	
	//睡眠和睡眠质量	
	private float mOrigX;
	private float mOrigY;
	
	//呼吸波的坐标原点
	private float mRespOrigX;
	private float mRespOrigY;
	
	//入睡和睡醒时刻
	private float mEventOrigX;
	private float mEventOrigY;
	
	//LF/HF数据对应的几何数据
	private float[] mLfHfData = new float[SleepGraphData.LFHF_DATA_COUNT * 2];
	private float mDeltaYOfLfHf;
	private float mDeltaLfHf;
	private float mTopOfLfHf;
	
	//Sleep Quality数据对应的几何数据
	private float[] mSleepLevels = new float[SleepGraphData.SLEEP_QUALITY_COUNT];
	
	private List<SleepQualityRect> mSleepQualityRects 
								 = new ArrayList<SleepQualityRect>();
	private float mSleepLevelNone;
	private float mSleepLevelAwake;
	private float mSleepLevelAwakyHeight;
	private float mSleepLevelLight;
	private float mSleepLevelLightHeight;
	private float mSleepLevelDeep;
	private float mSleepLevelDeepHeight;
	private float mDeltaSleepQuality;
	
	private Date mStartDate;
	private Date mEndDate;
	
	//呼吸波对应的几何数据
	private int[] mRespData = new int[SleepGraphData.RESP_DATA_COUNT];
	private float mDeltaResp;
	
	private Bitmap mFallAsleepBmp = null;
	private Bitmap mRollOverBmp = null;
	private Bitmap mWakeupBmp = null;
	
	public SleepGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);

//		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
//		setData(new SleepGraphData());
	}

	public void setData(SleepGraphData graphData) {
		mGraphData = graphData;
//		calculate();
	}
	
	private void calculate() {
		mSleepQualityRects.clear();
		
		mHeight = this.getHeight();
		mWidth = this.getWidth();
		
		// 计算原点坐标
		mOrigX = 0.0f;
		mOrigY = mHeight * 1.0f * GRAPH_HEIGHT / (GRAPH_HEIGHT + TIME_HEIGHT + RESP_HEIGHT + BLANK_HEIGHT);
		
		// 睡眠曲线
        mDeltaLfHf = mWidth * 1.0f / SleepGraphData.LFHF_DATA_COUNT;
        mTopOfLfHf = mOrigY * 0.25f;

        float currX = mOrigX;
        for (int i = 0; i < SleepGraphData.LFHF_DATA_COUNT; i++) {
        	mLfHfData[i * 2] = currX;
        	mLfHfData[i * 2 + 1] 
        				= mOrigY + (mTopOfLfHf - mOrigY) * mGraphData.lfhfData[i];
        	currX += mDeltaLfHf;
        }

        // 睡眠质量
		mDeltaSleepQuality = mWidth * 1.0f / SleepGraphData.SECONDS_PER_DAY;
		mSleepLevelNone = 0;
		
		mSleepLevelAwakyHeight = (mTopOfLfHf - mOrigY) * 0.05f;
		mSleepLevelAwake = mOrigY + mSleepLevelAwakyHeight;
		
		mSleepLevelLightHeight = (mTopOfLfHf - mOrigY) * 0.35f;
		mSleepLevelLight = mOrigY + mSleepLevelLightHeight;
		
		mSleepLevelDeepHeight = (mTopOfLfHf - mOrigY) * 0.7f;		
		mSleepLevelDeep = mOrigY + mSleepLevelDeepHeight;
        
//		for (int i = 0; i < SleepGraphData.SLEEP_QUALITY_COUNT; i++) {
//			if (mGraphData.sleepQualities[i] == 0) {
//				mSleepLevels[i] = mSleepLevelNone;
//			}
//			else if (mGraphData.sleepQualities[i] == 1) {
//				mSleepLevels[i] = mSleepLevelAwake;
//			}
//			else if (mGraphData.sleepQualities[i] == 2) {
//				mSleepLevels[i] = mSleepLevelLight;
//			}
//			else if (mGraphData.sleepQualities[i] == 3) {
//				mSleepLevels[i] = mSleepLevelDeep;
//			}
//		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		mStartDate = calendar.getTime();		
		calendar.add(Calendar.DATE, 1);
		
		mEndDate = calendar.getTime();
		for (SleepState state: mGraphData.sleepStateList) {
			
			long time = state.startDate.getTime();
			if (time < mStartDate.getTime() || time > mEndDate.getTime()) {
				continue;
			}
			
			SleepQualityRect sleepQualityRect = new SleepQualityRect();
			sleepQualityRect.left 
					= (time - mStartDate.getTime()) * 1.0f * mWidth / 1000 / SleepGraphData.SECONDS_PER_DAY;
			
			System.out.println("action SleepGraphView sleepQualityRect.left " + sleepQualityRect.left);
			
			if (state.state == 0) {
				sleepQualityRect.top = mSleepLevelNone;
				sleepQualityRect.color = clrAwake;
			}
			else if (state.state == 1) {
				sleepQualityRect.top = mSleepLevelAwake;
				sleepQualityRect.color = clrAwake;
			}
			else if (state.state == 2) {
				sleepQualityRect.top = mSleepLevelLight;
				sleepQualityRect.color = clrLight;
			}
			else if (state.state == 3) {
				sleepQualityRect.top = mSleepLevelDeep;
				sleepQualityRect.color = clrDeep;
			}
			
//			sleepQualityRect.top = mSleepLevelDeep;
//			sleepQualityRect.color = clrDeep;
			
			System.out.println("action SleepGraphView clrDeep " + clrDeep);
			
			System.out.println("action SleepGraphView sleepQualityRect.duaration " + state.duaration);
			
			sleepQualityRect.right = sleepQualityRect.left + state.duaration * 1.0f * mWidth / 1 / SleepGraphData.SECONDS_PER_DAY;
			sleepQualityRect.bottom = mOrigY;
			
			mSleepQualityRects.add(sleepQualityRect);
			
			System.out.println("action SleepGraphView sleepQualityRect " + sleepQualityRect);
		}
		
		// 呼吸波
		mRespOrigX = mOrigX;
		mRespOrigY = mOrigY + TIME_HEIGHT + RESP_HEIGHT + 3;
		
		mDeltaResp = mWidth * 1.0f / SleepGraphData.RESP_DATA_COUNT;
		
		// 入睡和睡醒
		mEventOrigX = mOrigX;
		mEventOrigY = mOrigY + TIME_HEIGHT + RESP_HEIGHT + 3;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mGraphData == null) { return; }
		if (!mGraphData.hasData) { return; }
		
		calculate();
		
		drawSleepQuality(canvas);
		drawLfHf(canvas);
		drawResp(canvas);
		drawSleepEvent(canvas);
	}

	private void drawLfHf(Canvas canvas) {
		if (mPaint == null) { return; }
		if (mGraphData == null) { return; }
		if (!mGraphData.hasData) { return; }
		
		mPaint.reset();
		mPaint.setColor(clrLfHf);
		mPaint.setStrokeWidth(1);
		mPaint.setAlpha(75);
        
        for (int i = 0; i < SleepGraphData.LFHF_DATA_COUNT - 1; i++) {
        	canvas.drawLine(mLfHfData[i * 2], mLfHfData[i * 2 + 1], 
        					mLfHfData[(i + 1) * 2], mLfHfData[(i + 1) * 2 + 1], mPaint);
        }
	}
	
	private void drawSleepQuality(Canvas canvas) {
		if (mPaint == null) { return; }
		if (mGraphData == null) { return; }
		if (!mGraphData.hasData) { return; }
		
		float currX = mOrigX;		
		
		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
//		for (int i = 0; i < SleepGraphData.SLEEP_QUALITY_COUNT; i++) {
//			
////			if (mSleepLevels[i] == mSleepLevelAwake) {
////				mPaint.setColor(clrAwake);// 设置灰色  
////		        canvas.drawRect(currX, 
////		        				mSleepLevelAwake, 
////		        				mDeltaSleepQuality, 
////		        				mSleepLevelAwakyHeight, mPaint);
////			}
////			else {
////				
////			}
////			
////			float height;
////			for (int i = 0; i < SleepGraphData.SLEEP_QUALITY_COUNT; i++) {
//				float height = 0.0f;
//				int color = clrAwake;
//				if (mGraphData.sleepQualities[i] == 0) {
//					height = mSleepLevelNone;
//				}
//				else if (mGraphData.sleepQualities[i] == 1) {
//					height = mSleepLevelAwakyHeight;
//					color = clrAwake;
//				}
//				else if (mGraphData.sleepQualities[i] == 2) {
//					height = mSleepLevelLightHeight;
//					color = clrLight;
//				}
//				else if (mGraphData.sleepQualities[i] == 3) {
//					height = mSleepLevelDeepHeight;
//					color = clrDeep;
//				}
//				mPaint.setColor(color);  
//		        canvas.drawRect(currX, 
//		        				mOrigY + height, //  mSleepLevels[i], 
//		        				currX + mDeltaSleepQuality, 
//		        				mOrigY, mPaint);				
////			}
//			
//			currX += mDeltaSleepQuality;
//		}
		
		for (SleepQualityRect rect: mSleepQualityRects) {
			System.out.println("action SleepGraphView 2222 " + rect);
			
			mPaint.setColor(rect.color);
			
	        canvas.drawRect(rect.left, rect.top, 
	        				rect.right, rect.bottom, mPaint);				
		}
	}
	
	private void drawResp(Canvas canvas) {
		if (mPaint == null) { return; }
		if (mGraphData == null) { return; }
		if (!mGraphData.hasData) { return; }

		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
		
		float currX = mRespOrigX;
		for (int i = 0; i < SleepGraphData.RESP_DATA_COUNT; i++) {
			if (mGraphData.resps[i] == 0) {
			} else {
			
			int color = RESP_COLORS[0];
			if (mGraphData.resps[i] >= RESP_COLORS.length) {
				color = RESP_COLORS[RESP_COLORS.length - 1];
			}
			else {
				color = RESP_COLORS[mGraphData.resps[i]];
			}
			mPaint.setColor(color);
			
	        canvas.drawRect(currX, 
    				mRespOrigY - RESP_HEIGHT, 
    				currX + mDeltaResp, 
    				mRespOrigY, mPaint);
//}
			}

	        currX += mDeltaResp;
		}
	}
	
	private void drawSleepEvent(Canvas canvas) {
		if (mPaint == null) { return; }
		if (mGraphData == null) { return; }
		if (!mGraphData.hasData) { return; }
		
		mFallAsleepBmp 
		= ((BitmapDrawable) getResources().getDrawable(R.drawable.fallasleep))
				.getBitmap();
		mRollOverBmp
		= ((BitmapDrawable) getResources().getDrawable(R.drawable.rollover))
				.getBitmap();
		mWakeupBmp
		= ((BitmapDrawable) getResources().getDrawable(R.drawable.wakeup))
				.getBitmap();
		
		mPaint.reset();
		
		Bitmap bmp = null;
		
		for (SleepEvent sleepEvent: mGraphData.sleepEventList) {
			if ( sleepEvent.event == SleepEvent.FALL_ASLEEP
			  || sleepEvent.event == SleepEvent.ROLL_OVER
			  || sleepEvent.event == SleepEvent.WAKEUP) {
				if (sleepEvent.event == SleepEvent.FALL_ASLEEP) {
					bmp = mFallAsleepBmp;
				}
				if (sleepEvent.event == SleepEvent.ROLL_OVER) {
					bmp = mRollOverBmp;
				}
				if (sleepEvent.event == SleepEvent.WAKEUP) {
					bmp = mWakeupBmp;
				}
			}
			else {
				continue;
			}
			
			System.out.println("action SleepGraphView event when:" 
						+ sleepEvent.event + "," 
						+ sleepEvent.when + "," + mStartDate);
			
			float offset = 0.0f;
			if (sleepEvent.when.getTime() - mEndDate.getTime() > 0) {
			}
			else {
				if (mEndDate.getTime() - sleepEvent.when.getTime() <= 30 * 60 * 1000) {
					System.out.println("action SleepGraphView 222222222211111");
					offset 
					= (sleepEvent.when.getTime() - mStartDate.getTime() - 35 * 60 * 1000) 
						* 1.0f * mWidth / 1000 / SleepGraphData.SECONDS_PER_DAY;
					
					canvas.drawBitmap(bmp, 
							  mOrigX + offset, 
							  mEventOrigY, mPaint);
				}
				else {
					System.out.println("action SleepGraphView 2222222222222222");
					offset 
					= (sleepEvent.when.getTime() - mStartDate.getTime() - 20 * 60 * 1000) 
						* 1.0f * mWidth / 1000 / SleepGraphData.SECONDS_PER_DAY;
					
					canvas.drawBitmap(bmp, 
							  mOrigX + offset, 
							  mEventOrigY, mPaint);
				}
			}
		}
	}
}
