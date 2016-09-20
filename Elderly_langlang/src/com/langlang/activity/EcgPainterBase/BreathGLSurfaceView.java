package com.langlang.activity.EcgPainterBase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

@SuppressLint("NewApi")
public class BreathGLSurfaceView extends GLSurfaceView implements IOpenGLPainter {
    private OpenGLBreathGrid grid = new OpenGLBreathGrid();
    
	float currX = OpenGLBreathGrid.START_X_VLINE;
	float deltaX = OpenGLBreathGrid.UNIT_SIZE / 10;
	
	float max_y = OpenGLBreathGrid.END_Y_HLINE 
							- (OpenGLBreathGrid.END_Y_HLINE - OpenGLBreathGrid.START_Y_HLINE) * 0.25f;
	float min_y = OpenGLBreathGrid.START_Y_HLINE 
							+ (OpenGLBreathGrid.END_Y_HLINE - OpenGLBreathGrid.START_Y_HLINE) * 0.25f;
	
	int pointNumber = 0;
	
	CanvasReadyCallback callback = null;
	
//	public static final int MAX_POINT = 750;
	public static final int MAX_POINT = 600;
    
	float[] vertexArray = new float[MAX_POINT * 3]; 
	
	float[] highlightPointVert = new float[1 * 3];
	int highlightPointPosition = 169;
	
	boolean normalized = false;
	float maxY ;
	float minY ;
	float prevMaxY;
	float prevMinY;
	
	public BreathGLSurfaceView(Context context) {
		super(context);
		setRenderer(new OpenGLRenderer(this));
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		initVertexArray();
//		requestRender();
	}

	public BreathGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setRenderer(new OpenGLRenderer(this));
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		initVertexArray();
		
//		requestRender();
	}
	
	public void drawBreath(float[] data, int pointCount) {
		if (pointCount == 0) return;
		
		if (!normalized && pointCount >= 100) {
			if (callback != null) {
				callback.notifyCanvasReady();
			}
			
			maxY = data[0];
			minY = data[0];		
			for (int i = 1; i < pointCount; i++) {
				if (data[i] > maxY) maxY = data[i];
				if (data[i] < minY) minY = data[i];
			}
			
			prevMaxY = maxY;
			prevMinY = minY;
			
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
			
			if ((maxY - prevMaxY > ((prevMaxY - prevMinY) * 0.25f)) 
						|| (prevMinY -  minY > ((prevMaxY - prevMinY) * 0.25f))) {
				// 重新归一化
			} else {
				maxY = prevMaxY;
				minY = prevMinY;
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
			} else {
				for (int i = 0; i < pointCount; i++) {
					vertexArray[i * 3 + 1] = min_y + (delta_y / deltaY) * (data[i] - minY);
				}
			}
			
			pointNumber = pointCount;		
			requestRender();
		}
	}
	
	public void reset() {
		pointNumber = 0;
		normalized = false;
		requestRender();
	}
	
	@Override
	public void DrawScene(GL10 gl) {
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);	

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
		
		ByteBuffer vbb 
		  = ByteBuffer.allocateDirect(vertexArray.length*4);
      vbb.order(ByteOrder.nativeOrder());
      FloatBuffer vertex = vbb.asFloatBuffer();
      vertex.put(vertexArray);
      vertex.position(0);

//      gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
//      gl.glPointSize(2f);
      gl.glLoadIdentity(); 
      gl.glTranslatef(0, 0, -4);
//      gl.glTranslatef(mPosition, 0f, 0f); 

  	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
  	
  	// 画网格
  	grid.drawGrid(gl);
  	
///<        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
      gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
//      gl.glPointSize(5f);
      gl.glLineWidth(2.0f);
//      gl.glLoadIdentity(); 
//      gl.glTranslatef(0, 0, -4);
//      gl.glTranslatef(mPosition, 0f, 0f); 
  	
  	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
//  	gl.glDrawArrays(GL10.GL_POINTS, 0, 3000);
//  	gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 3000);
//  	gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 751);
	gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointNumber);
  	
	if (normalized && pointNumber > highlightPointPosition) {
		drawHighlightPoint(gl);
	}
		
   	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
  	//<mPosition -=0.001f;
//   	mPosition -=0.004f;
		
	}

	//	private float mPosition=-0.5f;
	private void initVertexArray() {		
		currX = -OpenGLBreathGrid.START_X_VLINE;
		deltaX = OpenGLBreathGrid.UNIT_SIZE / 10;
		
//		float deltaX = 2 * currX / 750 * 3; //0.0024f;
		for (int i = 0; i < MAX_POINT; i++) {
			vertexArray[i * 3] = currX;			
			currX = currX - deltaX;
			vertexArray[i * 3 + 1] = 1;
			vertexArray[i * 3 + 2] = 0;
		}
		
		highlightPointPosition = 250;
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
	}
	
	public void setCallback(CanvasReadyCallback callback) {
		this.callback = callback;
	}
}
