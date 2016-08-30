package com.broadchance.ecgview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.broadchance.utils.CommonUtil;

public class OpenGLECGGrid {
	/**
	 * 转化为opengl使用的小端字节序(LittleEdian) 行线坐标数据
	 */
	private FloatBuffer vertexBuffer_H;
	/**
	 * 纵线坐标数据
	 */
	private FloatBuffer vertexBuffer_V;
	/**
	 * PPI宽高比(由于Android设备单个像素的宽高比不一定等于1， 所以PPI分为ydpi和xdpi，此处以高为准进行适配)
	 */
	private float xyDPIRatio = 0;
	/**
	 * y轴一个格子大小
	 */
	private float yUnitCellSize;
	/**
	 * x轴一个格子大小(在正交投影下，x轴的格子大小和y轴大小应该一样， 但由于设备屏幕差异导致单个像素的宽高比未必是1，因此为了保证显示
	 * 出的物理尺寸x轴和y轴保持一致，此处需要用 设备像素比来调整)
	 */
	private float xUnitCellSize;
	/**
	 * 缩放比例，根据需要缩放整个坐标系，默认值为1f， 根据OpenGLRenderer 中gl.glOrthof(-ratio, ratio,
	 * -1.1f, 1.1f, 0f, -5f)窗口高度设置 和CANVAS_HEIGHT =
	 * 2f,再根据DisplayMetrics的ydpi和xdpi 设置gl.glViewport(0, 0, width,
	 * height);视窗分辨率，可以保证单网格物理尺寸为1mm
	 */
	private float SCALE = 1f;
	/**
	 * x轴上的格子数(由于是根据y轴进行设配此处只要保证将屏幕width撑满即可， 比如20可以将屏幕撑满，大于20的任何值均可)
	 */
	private int GRID_NUM_H = 75;
	/**
	 * y轴上的格子数
	 */
	private int GRID_NUM_V = 20;
	/**
	 * 定义中心点到top的距离
	 */
	public final static float CANVAS_HALFHEIGHT = 20f;
	/**
	 * 按照高度适配，正交投影视窗对应的opengl坐标窗口大小设置为bottom=-1.1f，top=1.1f
	 * 之所以窗口的高度设置为2.2f(top-bottom)，画grid高度设置2f，是为了网格留个边距(margin)
	 */
	private final static float CANVAS_HEIGHT = CANVAS_HALFHEIGHT * 2;
	/**
	 * 计算每个格子的大小
	 */
	private float UNIT_SIZE = 0;
	/**
	 * 行线起始点,沿坐标轴方向，起始点为负轴
	 */
	public float HLINE_STARTX = 0;
	/**
	 * 行线的结束点,沿坐标轴方向，结束点为正轴
	 */
	public float HLINE_ENDX = 0;
	/**
	 * 纵线的起始点,沿坐标轴方向，起始点为负轴
	 */
	public float VLINE_STARTY = 0;
	/**
	 * 纵线的结束点,沿坐标轴方向，结束点为正轴
	 */
	public float VLINE_ENDY = 0;// 1.50f
	/**
	 * 行线的所有坐标数据
	 */
	private float[] verticeHLines;
	/**
	 * 纵线的所有坐标数据
	 */
	private float[] verticeVLines;

	float[] gridLightColor;
	float[] gridDarkColor;

	public float getyUnitCellSize() {
		return yUnitCellSize;
	}
	
	public float getxUnitCellSize() {
		return xUnitCellSize;
	}

	public int getGRID_NUM_H() {
		return GRID_NUM_H;
	}

	public void setGRID_NUM_H(int gRID_NUM_H) {
		GRID_NUM_H = gRID_NUM_H;
	}

	public int getGRID_NUM_V() {
		return GRID_NUM_V;
	}

	public void setGRID_NUM_V(int gRID_NUM_V) {
		GRID_NUM_V = gRID_NUM_V;
	}

	public OpenGLECGGrid(float ratio, int gridVNum, int gridHNum) {
		xyDPIRatio = ratio;
		GRID_NUM_V = gridVNum;
		GRID_NUM_H = gridHNum;

		UNIT_SIZE = (CANVAS_HEIGHT / GRID_NUM_V) * SCALE;
		xUnitCellSize = UNIT_SIZE / xyDPIRatio;
		yUnitCellSize = UNIT_SIZE;

		VLINE_STARTY = (-GRID_NUM_V / 2) * yUnitCellSize;
		VLINE_ENDY = VLINE_STARTY + GRID_NUM_V * yUnitCellSize;

		HLINE_STARTX = -(GRID_NUM_H / 2) * xUnitCellSize;
		HLINE_ENDX = HLINE_STARTX + GRID_NUM_H * xUnitCellSize;
		initParams();
		initBuffer();
	}

	private void initParams() {

		verticeVLines = new float[(GRID_NUM_H + 1) * 6];
		verticeHLines = new float[(GRID_NUM_V + 1) * 6];

		float xPosition = HLINE_STARTX;
		for (int i = 0; i < GRID_NUM_H + 1; i++) {
			verticeVLines[i * 6] = xPosition;
			verticeVLines[i * 6 + 1] = VLINE_ENDY;
			verticeVLines[i * 6 + 2] = 0;
			verticeVLines[i * 6 + 3] = xPosition;
			verticeVLines[i * 6 + 4] = VLINE_STARTY;
			verticeVLines[i * 6 + 5] = 0;
			xPosition += xUnitCellSize;
		}

		float yPosition = VLINE_STARTY;

		for (int i = 0; i < GRID_NUM_V + 1; i++) {
			verticeHLines[i * 6] = HLINE_STARTX;
			verticeHLines[i * 6 + 1] = yPosition;
			verticeHLines[i * 6 + 2] = 0;
			verticeHLines[i * 6 + 3] = HLINE_ENDX;
			verticeHLines[i * 6 + 4] = yPosition;
			verticeHLines[i * 6 + 5] = 0;

			yPosition += yUnitCellSize;
		}

	}

	private void initBuffer() {
		ByteBuffer vertexByteBuffer_H = ByteBuffer
				.allocateDirect(verticeHLines.length * 4);
		vertexByteBuffer_H.order(ByteOrder.nativeOrder());
		vertexBuffer_H = vertexByteBuffer_H.asFloatBuffer();
		vertexBuffer_H.put(verticeHLines);
		vertexBuffer_H.position(0);

		ByteBuffer vertexByteBuffer_V = ByteBuffer
				.allocateDirect(verticeVLines.length * 4);
		vertexByteBuffer_V.order(ByteOrder.nativeOrder());
		vertexBuffer_V = vertexByteBuffer_V.asFloatBuffer();
		vertexBuffer_V.put(verticeVLines);
		vertexBuffer_V.position(0);
	}

	public void drawGrid(GL10 gl, int gridLightColor, int gridDarkColor) {
		this.gridLightColor = CommonUtil.colorToRGB(gridLightColor);
		this.gridDarkColor = CommonUtil.colorToRGB(gridDarkColor);
		drawHLines(gl);
		drawVLines(gl);
	}

	private void drawHLines(GL10 gl) {
		// 指向数组数据
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer_H);
		gl.glLineWidth(1.0f);

		byte[] line = new byte[2];
		for (int i = 0; i < GRID_NUM_V + 1; i++) {
			line[0] = (byte) (i * 2);
			line[1] = (byte) (i * 2 + 1);
			if (i % 5 == 0) {
				// float[] bgColor = CommonUtil.colorToRGB(this.gridDarkColor);
				gl.glColor4f(this.gridDarkColor[1], this.gridDarkColor[2],
						this.gridDarkColor[3], this.gridDarkColor[0]);
				gl.glLineWidth(2.0f);
			} else {
				// float[] bgColor = CommonUtil.colorToRGB(this.gridLightColor);
				gl.glColor4f(this.gridLightColor[1], this.gridLightColor[2],
						this.gridLightColor[3], this.gridLightColor[0]);
				gl.glLineWidth(1.0f);
			}
			gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
					ByteBuffer.wrap(line));
		}
	}

	private void drawVLines(GL10 gl) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer_V);
		gl.glLineWidth(1.0f);

		byte[] line = new byte[2];
		for (int i = 0; i < GRID_NUM_H + 1; i++) {
			line[0] = (byte) (i * 2);
			line[1] = (byte) (i * 2 + 1);

			if (i % 5 == 0) {
				// float[] bgColor = CommonUtil.colorToRGB(this.gridDarkColor);
				gl.glColor4f(this.gridDarkColor[1], this.gridDarkColor[2],
						this.gridDarkColor[3], this.gridDarkColor[0]);
				gl.glLineWidth(2.0f);
			} else {
				// float[] bgColor = CommonUtil.colorToRGB(this.gridLightColor);
				gl.glColor4f(this.gridLightColor[1], this.gridLightColor[2],
						this.gridLightColor[3], this.gridLightColor[0]);
				gl.glLineWidth(1.0f);
			}
			gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
					ByteBuffer.wrap(line));
		}
	}
}
