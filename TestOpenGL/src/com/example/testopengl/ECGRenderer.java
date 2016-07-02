package com.example.testopengl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;
import android.widget.Toast;

public class ECGRenderer implements Renderer {
	private final static String TAG = ECGRenderer.class.getSimpleName();
	float[] vertex = new float[] { -0.9f, 0, 0, 0.9f, 0, 0, 0, 0.5f, 0, 0,
			-0.5f, 0 };
	FloatBuffer vertexBuffer;
	private MainActivity context;
	OpenGLECGGrid grid;
	float ratio = 0;

	public ECGRenderer(MainActivity context) {

		this.context = context;
		grid = new OpenGLECGGrid();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		// 关闭抗抖动
		gl.glDisable(GL10.GL_DITHER);
		// 设置系统对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		// 清楚深度缓存
		gl.glClearDepthf(1.0f);
		// 用黑色清屏
		gl.glClearColor(0, 0, 0, 1);
		// 设置阴影平滑模式
		gl.glShadeModel(GL10.GL_SMOOTH);
		// 启用深度测试，让O喷GLES跟踪每个物体的在Z轴上的深度，避免后面的物体遮挡前面的物体
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// 设置深度测试的类型
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// 启用2D纹理贴图
		// gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "Viewport " + width + "x" + height);
		final int widthL = width;
		final int heightL = height;
		// width *= 0.5f;
		// height *=0.5f;
		this.context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, "Viewport " + widthL + "x" + heightL,
						Toast.LENGTH_LONG).show();
			}
		});

		// 计算透视视窗的宽度、高度比
		ratio = (float) width / (float) height;
		// 设置3D视窗的大小及位置，前两个是视窗的位置，后面两个是视窗的宽度
		gl.glViewport(0, 0, width, height);
		// GL_PROJECTION将当前矩阵模式设为投影矩阵，透视图，越远看起来越小
		// GL_MODELVIEW模型视图，任何新的变换都会影响矩阵中的所有物体
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 初始化单位矩阵
		gl.glLoadIdentity();

		// grid.initData(ratio);
		grid.initData(1);
		// vertex = new float[] { -1f / ratio, 0, 0, 1f / ratio, 0, 0, 0, 1f, 0,
		// 0, -1f, 0 };
		vertex = new float[] { -0.5f, 0.5f, 1.2f, 0.5f, 0.5f, 1.2f, -0.5f,
				-0.2f, 10.2f, 0.5f, -0.2f, 10.2f, 0, 0.5f, 0.8f, 0, -0.5f, 0.8f };
		vertexBuffer = MyUtil.floatBufferUtil(vertex);
		// 调用此方法设置透视视窗的空间大小。
		// gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		// Calculate the aspect ratio of the window
		// GLU.gluPerspective(gl, 45.0f, ratio, 0.1f, 100.0f);
		// 选择正交,窗口高度两个世界坐标,行坐标尺寸跟纵坐标保持一致
		// gl.glOrthof(-ratio, ratio, 1f, 1f, 1, -1);
		// 由于opengl才用右手左边而移动设备才用左手坐标所以此处裁剪的z方向取方向值
		gl.glOrthof(-ratio, ratio, -1.1f, 1.1f, 0f, -5f);
		// GLU.gluOrtho2D(gl, -1f, 1f, -1f, 1f);
		// Select the modelview matrix
		// gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		// gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// 清除屏幕缓存和深度缓存
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// 把绘图中心移入屏幕
		// gl.glTranslatef(0f, 0.0f, -4f);
		gl.glTranslatef(0f, 0.0f, 0f);
		// 启用顶点座标数据
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glLineWidth(2.5f);
		gl.glColor4f(1.0f, 0.0f, .0f, 1.0f);
		// 设置三角形顶点
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawArrays(GL10.GL_LINES, 0, 6);

		grid.drawGrid(gl);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}
}
