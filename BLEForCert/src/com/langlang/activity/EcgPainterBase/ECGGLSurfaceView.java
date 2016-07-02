package com.langlang.activity.EcgPainterBase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

@SuppressLint("NewApi")
public class ECGGLSurfaceView extends GLSurfaceView implements IOpenGLPainter {
	// 默认走纸速度25mm/s
	public static final int ECG_MODE_DEFAULT = 0;
	public static final int ECG_MODE_DEFAULT_TOTAL_POINT = (250 * (75 / 25));
	
	// 走纸速度10mm/s
	public static final int ECG_MODE_0 = 1;
	public static final int ECG_MODE_0_TOTAL_POINT =  25 * 75; //(250 * (75 / 10));
	// 走纸速度20mm/s
	public static final int ECG_MODE_1 = 2;
	public static final int ECG_MODE_1_TOTAL_POINT =  938; //(250 * (75 / 20));
	
	private OpenGLECGGrid grid = new OpenGLECGGrid();
    
	float currX = OpenGLECGGrid.START_X_VLINE;
	float deltaX = OpenGLECGGrid.UNIT_SIZE / 10;
	
	float max_y = OpenGLECGGrid.END_Y_HLINE 
							- (OpenGLECGGrid.END_Y_HLINE - OpenGLECGGrid.START_Y_HLINE) * 0.25f;
	float min_y = OpenGLECGGrid.START_Y_HLINE 
							+ (OpenGLECGGrid.END_Y_HLINE - OpenGLECGGrid.START_Y_HLINE) * 0.25f;
	
	
	float top_y = OpenGLECGGrid.END_Y_HLINE;
	float bottom_y = OpenGLECGGrid.START_Y_HLINE;
	float middle_y = OpenGLECGGrid.START_Y_HLINE 
					+ (OpenGLECGGrid.END_Y_HLINE - OpenGLECGGrid.START_Y_HLINE) * 0.5f;
	
	int pointNumber = 0;
	
//	public static final int MAX_POINT = 750;
	public static final int MAX_POINT = 750 * 3;
    
//	float[] vertexArray = new float[750 * 3]; 
	float[] vertexArray = new float[MAX_POINT * 3]; 
	
	float[] highlightPointVert = new float[1 * 3];
	int highlightPointPosition = 169;
	
	private int currTotalPointNumber = 0;
	
	boolean normalized = false;
	float maxY ;
	float minY ;
	
	float prevMaxY;
	float prevMinY;
	
	float middleY;
	//每格小格子画17个ECG数据,一共画50个格子
	float perECGSize = (top_y - bottom_y) / (17 * 50);
	
	int countDraw = 0;
	
	CanvasReadyCallback callback = null;
	
	private Date drawDate = null;
	
	private int mEcgMode = ECG_MODE_DEFAULT;
	
	public ECGGLSurfaceView(Context context) {
		super(context);
		setRenderer(new OpenGLRenderer(this));
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		initVertexArray();
	}

	public ECGGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setRenderer(new OpenGLRenderer(this));
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		initVertexArray();
	}
	
	public void setCallback(CanvasReadyCallback callback) {
		this.callback = callback;
	}
	
	public void drawECG(float[] data, int pointCount) {
		if (pointCount == 0) return;
		
//		normalized = true;
		
		// 用于存放data数组中实际的最大值和最小值
		float maxDataValue = 0.0f;
		float minDataValue = 0.0f;
		
		// 用于存放最大的几何坐标值和最小的几何坐标值
		float max = 0.0f;
		float min = 0.0f;
		
		// yGridValue用于表示每个单元格之间对应的实际电压差
		// 每个单位的ECG数值对应的电压差为0.006V
		float yGridValue = 0.0f;
		
		if (!normalized && pointCount >= 85) {
			if (callback != null) {
				callback.notifyCanvasReady();
			}
			
			maxY = data[0];
			minY = data[0];		
			for (int i = 1; i < pointCount; i++) {
				if (data[i] > maxY) maxY = data[i];
				if (data[i] < minY) minY = data[i];
			}
			
			maxDataValue = maxY;
			minDataValue = minY;
			
			prevMaxY = maxY;
			prevMinY = minY;
			
			middleY = minY + (maxY - minY) * 0.5f;
			
			normalized = true;
		}
		else if (normalized) {
			// 检查是否需要重新归一化
			maxY = data[0];
			minY = data[0];		
			for (int i = 1; i < pointCount; i++) {
				if (data[i] > maxY) maxY = data[i];
				if (data[i] < minY) minY = data[i];
			}
			
			// 保存data数组中的最大值和最小值
			maxDataValue = maxY;
			minDataValue = minY;
			
			if ((maxY - prevMaxY > ((prevMaxY - prevMinY) * 0.25f)) 
						|| (prevMinY -  minY > ((prevMaxY - prevMinY) * 0.25f))) {
				// 重新归一化
				prevMaxY = maxY;
				prevMinY = minY;
				
				middleY = minY + (maxY - minY) * 0.5f;
			} 
			else if (((prevMaxY - maxY) > ((prevMaxY - prevMinY) * 0.25f)) 
						|| ((minY - prevMinY) > ((prevMaxY - prevMinY) * 0.25f))) {
				// 重新归一化
				prevMaxY = maxY;
				prevMinY = minY;
				
				middleY = minY + (maxY - minY) * 0.5f;
			}
			else {
//				maxY = prevMaxY;
//				minY = prevMinY;
			}			
		}
		
		if (normalized) {
			// 归一化
			float delta_y = max_y - min_y;
			float deltaY = maxY - minY;
			
			if (Float.compare(deltaY, 0) == 0) {
				for (int i = 0; i < pointCount; i++) {
					vertexArray[i * 3 + 1] = min_y + 0.5f * delta_y;
				}
				yGridValue = 0.0f;
			} else {
				for (int i = 0; i < pointCount; i++) {
					vertexArray[i * 3 + 1] = middle_y + (data[i] - middleY) * perECGSize;
				}
				
//				for (int i = 0; i < pointCount; i++) {
//					vertexArray[i * 3 + 1] = min_y + (delta_y / deltaY) * (data[i] - minY);
//				}
				
//				max = min_y + (delta_y / deltaY) * (maxDataValue - minY);
//				min = min_y + (delta_y / deltaY) * (minDataValue - minY);
//				
//				if (Float.compare(max - min, 0) == 0) {
//					yGridValue = 0.0f;
//				}
//				else {
//					yGridValue = (maxDataValue - minDataValue) * 0.006f / ((max - min) / OpenGLECGGrid.UNIT_SIZE);
//				}
			}
			pointNumber = pointCount;
//			System.out.println("action ECGGLSurfaceView deltaY:" + deltaY);
			
			if (callback != null && callback.stopPainting()) {
//				callback.onPaintingStopped(deltaY * 0.006f / 25);
				callback.onPaintingStopped(yGridValue);
			} else {
				requestRender();
			}
		}
	}
	
	public void reset() {
		pointNumber = 0;
		normalized = false;
		requestRender();
	}
	
	@Override
	public void DrawScene(GL10 gl) {
		if (countDraw == 0) {
			drawDate = new Date();
		} else {
			Date now = new Date();
			long ts = now.getTime() - drawDate.getTime();			
			System.out.println("action DrawScene calculate date[" + ts + "]");
			if (ts > 100) {
				System.out.println("action DrawScene calculate too long date[" + ts + "]");
			}
			drawDate = now;
		}
		
		countDraw++;
		System.out.println("action DrawScene start[" + countDraw + "]");
		
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);	

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
		
		ByteBuffer vbb 
					= ByteBuffer.allocateDirect(vertexArray.length*4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer vertex = vbb.asFloatBuffer();
		vertex.put(vertexArray);
		vertex.position(0);

		gl.glLoadIdentity(); 
		gl.glTranslatef(0, 0, -4);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
  	
	  	// 画网格
	  	grid.drawGrid(gl);
  	
	  	gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
	  	gl.glLineWidth(2.0f);
  	
	  	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
	  	gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointNumber);
  	
		if (normalized && pointNumber > highlightPointPosition) {
			drawHighlightPoint(gl);
		}
		
	   	if (callback.getCapture()) {
	   		System.out.println("action ECGGLSurfaceView width, height:" 
	   						+ this.getWidth() + "," + this.getHeight());
	   		Bitmap bitmap = savePixels(0, 0, this.getWidth(), this.getHeight(), gl);
	   		callback.onCaptured(bitmap);
	   		bitmap = null;
	   	}
   	
	   	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	public static Bitmap savePixels(int x, int y, int w, int h, GL10 gl)
	{ 
		int b[] = new int[w * h];
		int bt[] = new int[w * h];
		IntBuffer ib=IntBuffer.wrap(b);
		ib.position(0);
		gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
		for (int i = 0; i < h; i++)
		{
			for (int j = 0; j < w; j++)
			{
				int pix = b[i * w + j];
				int pb = (pix >> 16) & 0xff;
				int pr = (pix << 16) & 0x00ff0000;
				int pix1 = (pix & 0xff00ff00) | pr | pb;
				bt[(h - i - 1) * w + j]= pix1;
			}
		}
	//	Bitmap sb=Bitmap.createBitmap(bt, w, h, true);
//		Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.RGB_565);
		Bitmap sb = Bitmap.createBitmap(bt, w, h, Config.ARGB_8888);
		
		return sb;
	}

	//	private float mPosition=-0.5f;
	private void initVertexArray() {		
		currX = -OpenGLECGGrid.START_X_VLINE;
//		deltaX = OpenGLECGGrid.UNIT_SIZE / 10;
		getDeltaX();
		
//		float deltaX = 2 * currX / 750 * 3; //0.0024f;
		for (int i = 0; i < MAX_POINT; i++) {
			vertexArray[i * 3] = currX;			
			currX = currX - deltaX;
			
			vertexArray[i * 3 + 2] = 0;
		}
		
		if (currTotalPointNumber != 0) {
			highlightPointPosition = currTotalPointNumber / 2;
		}
		else {
			highlightPointPosition =  350;
		}
		
		highlightPointVert[0] = vertexArray[highlightPointPosition * 3];
		highlightPointVert[2] = vertexArray[highlightPointPosition * 3 + 2];		
		
		pointNumber = 0;
		normalized = false;
	}

	private void drawHighlightPoint(GL10 gl) {
		highlightPointVert[1] = vertexArray[highlightPointPosition * 3 + 1];

        ByteBuffer vertHPointBuffer = ByteBuffer.allocateDirect(highlightPointVert.length * 4);  
        vertHPointBuffer.order(ByteOrder.nativeOrder());  
		FloatBuffer vertexHPointer = vertHPointBuffer.asFloatBuffer();  
		vertexHPointer.put(highlightPointVert);  
		vertexHPointer.position(0);
		
//		gl.glColor4f(60.0f / 256, 75.0f/ 256, 113.0f /256, 1.0f);
//		gl.glColor4f(0.5f, 1.5f, 1.5f, 1.0f);
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		gl.glPointSize(9f);
		
	  	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexHPointer);
		gl.glDrawArrays(GL10.GL_POINTS, 0, 1);
	}
	
	public interface CanvasReadyCallback {
		public void notifyCanvasReady();
		public boolean getCapture();
		public void onCaptured(Bitmap bitmap);
		public boolean stopPainting();
		public void onPaintingStopped(float yGridValue);
	}
	
	public void redrawForCapture() {
		requestRender();
	}
	
	public void setEcgMode(int mode) {
		if (ECG_MODE_DEFAULT == mode || ECG_MODE_0 == mode 
									 || ECG_MODE_1 == mode) {
			mEcgMode = mode;
			initVertexArray();
		}
	}
	
	private void getDeltaX() {
		float total = (OpenGLECGGrid.UNIT_SIZE / 10) * ECG_MODE_DEFAULT_TOTAL_POINT;
		if (ECG_MODE_0 == mEcgMode) {
			// 走纸速度10mm/s
			deltaX = total / ECG_MODE_0_TOTAL_POINT;
			currTotalPointNumber = ECG_MODE_0_TOTAL_POINT;
			
		}
		else if (ECG_MODE_1 == mEcgMode) {
			// 走纸速度20mm/s
			deltaX = total / ECG_MODE_1_TOTAL_POINT;
			currTotalPointNumber = ECG_MODE_1_TOTAL_POINT;
		}
		else {
			// 默认走纸速度25mm/s
			deltaX = total / ECG_MODE_DEFAULT_TOTAL_POINT;
			currTotalPointNumber = ECG_MODE_DEFAULT_TOTAL_POINT;
		}
	}
}
