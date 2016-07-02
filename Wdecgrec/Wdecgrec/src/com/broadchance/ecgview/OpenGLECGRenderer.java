package com.broadchance.ecgview;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

import com.broadchance.utils.CommonUtil;

public class OpenGLECGRenderer implements Renderer {
	private final static String TAG = OpenGLECGRenderer.class.getSimpleName();
	/**
	 * 定义正交投影中心点到top的距离
	 */
	public final static float VIEWPORT_HALFHEIGHT = 22f;
	private final ECGGLSurfaceView surfaceView;
	float[] vertex = new float[] { -0.9f, 0, 0, 0.9f, 0, 0, 0, 0.5f, 0, 0,
			-0.5f, 0 };
	FloatBuffer vertexBuffer;

	public OpenGLECGRenderer(ECGGLSurfaceView surfaceView) {
		this.surfaceView = surfaceView;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);

		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (this.surfaceView != null) {
			this.surfaceView.DrawScene(gl);
			// return;
		}
		// // 清除屏幕缓存和深度缓存
		// gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// // 把绘图中心移入屏幕
		// // gl.glTranslatef(0f, 0.0f, -4f);
		// gl.glTranslatef(0f, 0.0f, 0f);
		// // 启用顶点座标数据
		// gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//
		// gl.glLineWidth(2.5f);
		// gl.glColor4f(1.0f, 0.0f, .0f, 1.0f);
		// // 设置三角形顶点
		// gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		// gl.glDrawArrays(GL10.GL_LINES, 0, vertex.length / 3);
		//
		// gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float ratio = (float) width / (float) height;
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Calculate the aspect ratio of the window
		// GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
		// 100.0f);
		// 由于opengl才用右手左边而移动设备才用左手坐标所以此处裁剪的z方向取方向值

		// 使用正交投影，以高度为基准，宽度尺寸保持和高度同比例缩放，保证行坐标和纵坐标显示尺寸相同
		gl.glOrthof(-ratio * VIEWPORT_HALFHEIGHT, ratio * VIEWPORT_HALFHEIGHT,
				-VIEWPORT_HALFHEIGHT, VIEWPORT_HALFHEIGHT, 0f, -5f);
		vertex = new float[] { -1, 1, 1, 1, 1, 1, -1, -1, 1, 1, -1, 1, -1, 1,
				1, -1, -1, 1, 1, 1, 1, 1, -1, 1 };
		vertexBuffer = CommonUtil.floatBufferUtil(vertex);
		// Select the modelview matrix
		// gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		// gl.glLoadIdentity();
		// LogUtil.d(TAG, width + " x " + height);
	}

}
