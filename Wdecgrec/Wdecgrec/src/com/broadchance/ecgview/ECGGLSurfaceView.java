package com.broadchance.ecgview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;

import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.wdecgrec.R;

public class ECGGLSurfaceView extends GLSurfaceView {
	private final static String TAG = ECGGLSurfaceView.class.getSimpleName();
	// 走纸速度5mm/mv 12.5mm/s
	public static final int ECG_MODE_LOW = 1;
	public static final int ECG_MODE_LOW_PERUNIT_POINT = (int) (FrameDataMachine.FRAME_DOTS_FREQUENCY / 12.5);
	// 默认走纸速度10mm/mv 25mm/s
	public static final int ECG_MODE_NORMAL = 2;
	public static final int ECG_MODE_NORMAL_PERUNIT_POINT = (int) (FrameDataMachine.FRAME_DOTS_FREQUENCY / 25);

	// 走纸速度20mm/mv 50mm/s
	public static final int ECG_MODE_HIGH = 3;
	public static final int ECG_MODE_HIGH_PERUNIT_POINT = (int) (FrameDataMachine.FRAME_DOTS_FREQUENCY / 50);

	public OpenGLECGGrid grid;

	float currX = 0;
	float deltaX = 0;
	/**
	 * 单位mv的ecg电压差值即1mv对应ecg有效数据(分析样本数据散落值可以得出分布率最高的相对较大电压为最大电压，分布率相对较小电压为最小电压)
	 * 中最大电压和最小电压之差 此值是通过实际设备测试得出的有效值
	 */
	private static final int BASEFACTOR = 194;
	/**
	 * 电压系数 每个ecg数据单位值对应的opengl坐标值
	 */
	float factorV = 1;

	float max_y = 0;
	float min_y = 0;

	float top_y = 0;
	float bottom_y = 0;
	float middle_y = 0;

	int pointNumber = 0;

	// public static final int MAX_POINT = 750;
	public static final int MAX_POINT = 750 * 3;

	// float[] vertexArray = new float[750 * 3];
	float[] vertexArray = new float[MAX_POINT * 3];

	float[] highlightPointVert = new float[1 * 3];
	int highlightPointPosition = 169;

	private int currTotalPointNumber = 0;

	boolean normalized = false;
	float maxY;
	float minY;

	float prevMaxY;
	float prevMinY;

	float middleY;

	int countDraw = 0;

	CanvasReadyCallback callback = null;

	private Date drawDate = null;

	private int mEcgMode = ECG_MODE_NORMAL;

	/**
	 * grid小格线颜色
	 */
	int gridLightColor = 0XFFFFFFFF;
	/**
	 * grid中格线颜色
	 */
	int gridDarkColor = 0XFFFFFFFF;
	/**
	 * grid背景色
	 */
	int gridBgColor = 0X00000000;
	/**
	 * 心电曲线颜色
	 */
	int ecgLineColor = 0XFF00FF00;
	/**
	 * 网格左右边距
	 */
	int leftRightMargin = 10;
	/**
	 * 每英寸mm
	 */
	private final static float mmPerInch = 25.4f;

	public ECGGLSurfaceView(Context context) {
		super(context);
		init(context, null);
	}

	public ECGGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.openglecgview);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			switch (typedArray.getIndex(i)) {
			case R.styleable.openglecgview_gridlightcolor:
				gridLightColor = typedArray.getColor(
						R.styleable.openglecgview_gridlightcolor,
						gridLightColor);
				break;
			case R.styleable.openglecgview_griddarkcolor:
				gridDarkColor = typedArray.getColor(
						R.styleable.openglecgview_griddarkcolor, gridDarkColor);
				break;
			case R.styleable.openglecgview_gridbgcolor:
				gridBgColor = typedArray.getColor(
						R.styleable.openglecgview_gridbgcolor, gridBgColor);
				break;
			case R.styleable.openglecgview_ecglinecolor:
				ecgLineColor = typedArray.getColor(
						R.styleable.openglecgview_ecglinecolor, ecgLineColor);
				break;
			case R.styleable.openglecgview_leftrightmargin:
				leftRightMargin = typedArray.getDimensionPixelSize(
						R.styleable.openglecgview_leftrightmargin,
						leftRightMargin);
				break;
			default:
				break;
			}
		}
		typedArray.recycle();
	}

	private void init(Context context, AttributeSet attrs) {
		setRenderer(new OpenGLECGRenderer(this));
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		// 使用透明
		// getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}

	/**
	 * 
	 * @param activity
	 * @param gridVNum
	 *            ecg竖向格子数
	 */
	public void initView(Activity activity, int gridVNum) {
		float ratio = 1f;
		float xDPI = SettingsManager.getInstance().getDpiConfigX();
		float yDPI = SettingsManager.getInstance().getDpiConfigY();
		// DisplayMetrics dm = new DisplayMetrics();
		// DisplayMetrics resdm = new DisplayMetrics();
		DisplayMetrics outMetrics = new DisplayMetrics();
		// activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams lp = getLayoutParams();
		// dm.densityDpi / 25.4f计算出每mm多少像素pix/mm，
		// int glViewpix = (int) (dm.densityDpi / 25.4f);
		// *1.1f是由于50 *
		// glViewpix等于50个标准心电高度，对应OpenGLECGGrid.CANVAS_HEIGHT的网格高度,而ECGRenderer中设置gl.glOrthof(-ratio,
		// ratio, -1.1f, 1.1f, 1, -1);
		// 设置的窗口大小为2.2f，所以此处需要乘以1.1f
		// lp.height = 50 * glViewpix* 1.1f;
		// lp.width = 75 * glViewpix* 1.1f;
		//

		// Point point = new Point();
		// activity.getWindowManager().getDefaultDisplay().getRealSize(point);
		// double x = Math.pow(point.x / dm.xdpi, 2);
		// double y = Math.pow(point.y / dm.ydpi, 2);
		// double screenInches = Math.sqrt(x + y);
		activity.getWindowManager().getDefaultDisplay()
				.getRealMetrics(outMetrics);
		xDPI = xDPI <= 0 ? outMetrics.xdpi : xDPI;
		yDPI = yDPI <= 0 ? outMetrics.ydpi : yDPI;

		// xDPI = 221.99764f;
		// yDPI = 221.99968f;

		// resdm = getResources().getDisplayMetrics();
		// double avgDPI = Math.sqrt(Math.pow(outMetrics.xdpi, 2)
		// + Math.pow(outMetrics.ydpi, 2));
		double glpixpermmY = yDPI / mmPerInch;
		// Toast.makeText(
		// activity,
		// "set xdpi:" + dm.xdpi + " ydpi" + dm.ydpi + " "
		// + outMetrics.xdpi + "x" + outMetrics.ydpi + " avgDPI"
		// + avgDPI + " glpixpermm" + glpixpermm,
		// Toast.LENGTH_LONG).show();
		float viewportRatio = OpenGLECGRenderer.VIEWPORT_HALFHEIGHT
				/ OpenGLECGGrid.CANVAS_HALFHEIGHT;
		/**
		 * 将像素值和物理尺寸对应起来 由于opengl视窗是填充窗口
		 * 在renderer中设置了正交并按照高度适配，因此也将opengl坐标系和像素值及物理尺寸对应起来了
		 */
		lp.height = (int) (gridVNum * glpixpermmY * viewportRatio);
		int viewWidth = 0;
		// lp.width = (int) (75 * glpixpermm * 1.1f);
		// lp.height = 480;
		// lp.width = 270;
		setLayoutParams(lp);
		ratio = xDPI / yDPI;
		double glpixpermmX = glpixpermmY / ratio;
		int gridHNum = 75;
		// 根据屏幕宽度实际像素点计算宽可以画出几个点
		viewWidth = outMetrics.widthPixels;
		// 如果使用getWidth必须保证InitView在页面计算之后调用才可以获取到值
		// viewWidth = getWidth();
		// viewWidth = View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED);
		// viewWidth = getMeasuredWidth();
		gridHNum = (int) (Math
				.ceil((viewWidth - leftRightMargin) / glpixpermmX));
		// 保证一个中格(5小格)
		gridHNum = (gridHNum / 5) * 5;
		grid = new OpenGLECGGrid(ratio, gridVNum, gridHNum);
		initParams(gridVNum);
		initVertexArray();
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		requestRender();
	}

	public int getMode() {
		return mEcgMode;
	}

	void initParams(int gridVNum) {
		currX = grid.HLINE_STARTX;
		max_y = grid.VLINE_ENDY - (grid.VLINE_ENDY - grid.VLINE_STARTY) * 0.25f;
		min_y = grid.VLINE_STARTY + (grid.VLINE_ENDY - grid.VLINE_STARTY)
				* 0.25f;

		top_y = grid.VLINE_ENDY;
		bottom_y = grid.VLINE_STARTY;
		middle_y = grid.VLINE_STARTY + (grid.VLINE_ENDY - grid.VLINE_STARTY)
				* 0.5f;
		// perECGSize = (top_y - bottom_y) / (17 * gridVNum);
	}

	public void setCallback(CanvasReadyCallback callback) {
		this.callback = callback;
	}

	public void drawECG(Integer[] data, int pointCount) {
		if (pointCount == 0)
			return;

		// normalized = true;

		// 用于存放data数组中实际的最大值和最小值
		float maxDataValue = 0.0f;
		float minDataValue = 0.0f;

		// 用于存放最大的几何坐标值和最小的几何坐标值
		float max = 0.0f;
		float min = 0.0f;

		// yGridValue用于表示每个单元格之间对应的实际电压差
		// 每个单位的ECG数值对应的电压差为0.006V
		float yGridValue = 0.0f;
		int maxIdx = pointCount - 1;
		int maxIIdx = pointCount - 2;
		if (!normalized && pointCount >= 85) {
			if (callback != null) {
				callback.notifyCanvasReady();
			}

			maxY = data[maxIdx];
			minY = data[maxIdx];
			for (int i = maxIIdx; i > -1; i--) {
				if (data[i] > maxY)
					maxY = data[i];
				if (data[i] < minY)
					minY = data[i];
			}

			maxDataValue = maxY;
			minDataValue = minY;

			prevMaxY = maxY;
			prevMinY = minY;

			middleY = minY + (maxY - minY) * 0.5f;

			normalized = true;
		} else if (normalized) {
			// 检查是否需要重新归一化
			maxY = data[maxIdx];
			minY = data[maxIdx];
			for (int i = maxIIdx; i > -1; i--) {
				if (data[i] > maxY)
					maxY = data[i];
				if (data[i] < minY)
					minY = data[i];
			}

			// 保存data数组中的最大值和最小值
			maxDataValue = maxY;
			minDataValue = minY;

			if ((maxY - prevMaxY > ((prevMaxY - prevMinY) * 0.25f))
					|| (prevMinY - minY > ((prevMaxY - prevMinY) * 0.25f))) {
				// 重新归一化
				prevMaxY = maxY;
				prevMinY = minY;

				middleY = minY + (maxY - minY) * 0.5f;
			} else if (((prevMaxY - maxY) > ((prevMaxY - prevMinY) * 0.25f))
					|| ((minY - prevMinY) > ((prevMaxY - prevMinY) * 0.25f))) {
				// 重新归一化
				prevMaxY = maxY;
				prevMinY = minY;
				middleY = minY + (maxY - minY) * 0.5f;
			} else {
				// maxY = prevMaxY;
				// minY = prevMinY;
			}
		}

		if (normalized) {
			// 归一化
			float delta_y = max_y - min_y;
			float deltaY = maxY - minY;
			// if (ConstantConfig.Debug) {
			// LogUtil.d(TAG, "maxY " + maxY + " minY " + minY + " deltaY "
			// + deltaY);
			// }
			if (Float.compare(deltaY, 0) == 0) {
				for (int i = 0; i < pointCount; i++) {
					vertexArray[i * 3 + 1] = min_y + 0.5f * delta_y;
				}
				yGridValue = 0.0f;
			} else {
				// for (int i = 0; i < pointCount; i++) {
				// vertexArray[i * 3 + 1] = middle_y + (data[i] - middleY)
				// * perECGSize;
				// }

				for (int i = 0; i < pointCount; i++) {
					vertexArray[i * 3 + 1] = middle_y
							+ (data[maxIdx - i] - middleY) * factorV;
				}

				// for (int i = 0; i < pointCount; i++) {
				// vertexArray[i * 3 + 1] = (min_y + (data[i] - minY))
				// * deltaY * factorV;
				// }
				//
				// max = min_y + (delta_y / deltaY) * (maxDataValue - minY);
				// min = min_y + (delta_y / deltaY) * (minDataValue - minY);
				//
				// if (Float.compare(max - min, 0) == 0) {
				// yGridValue = 0.0f;
				// } else {
				// yGridValue = (maxDataValue - minDataValue) * 0.006f
				// / ((max - min) / grid.getxUnitCellSize());
				// }
			}
			pointNumber = pointCount;

			if (callback != null && callback.stopPainting()) {
				callback.onPaintingStopped(yGridValue);
			} else {
				requestRender();
			}
		}
	}

	public int getCurrTotalPointNumber() {
		return currTotalPointNumber;
	}

	public void setCurrTotalPointNumber(int currTotalPointNumber) {
		this.currTotalPointNumber = currTotalPointNumber;
	}

	public void reset() {
		pointNumber = 0;
		normalized = false;
		requestRender();
	}

	public void DrawScene(GL10 gl) {
		if (grid == null)
			return;
		if (countDraw == 0) {
			drawDate = new Date();
		} else {
			Date now = new Date();
			long ts = now.getTime() - drawDate.getTime();
			System.out.println("action DrawScene calculate date[" + ts + "]");
			if (ts > 100) {
				System.out.println("action DrawScene calculate too long date["
						+ ts + "]");
			}
			drawDate = now;
		}
		countDraw++;
		System.out.println("action DrawScene start[" + countDraw + "]");
		float[] bgColor = CommonUtil.colorToRGB(gridBgColor);
		gl.glClearColor(bgColor[1], bgColor[2], bgColor[3], bgColor[0]);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		FloatBuffer vertex = CommonUtil.floatBufferUtil(vertexArray);
		gl.glTranslatef(0, 0, 0);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// 画网格
		grid.drawGrid(gl, gridLightColor, gridDarkColor);

		float[] ecglineColor = CommonUtil.colorToRGB(ecgLineColor);
		gl.glColor4f(ecglineColor[1], ecglineColor[2], ecglineColor[3],
				ecglineColor[0]);
		gl.glLineWidth(2.0f);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointNumber);

		if (normalized && pointNumber > highlightPointPosition) {
			drawHighlightPoint(gl);
		}

		if (callback != null && callback.getCapture()) {
			System.out.println("action ECGGLSurfaceView width, height:"
					+ this.getWidth() + "," + this.getHeight());
			Bitmap bitmap = savePixels(0, 0, this.getWidth(), this.getHeight(),
					gl);
			callback.onCaptured(bitmap);
			bitmap = null;
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	public static Bitmap savePixels(int x, int y, int w, int h, GL10 gl) {
		int b[] = new int[w * h];
		int bt[] = new int[w * h];
		IntBuffer ib = IntBuffer.wrap(b);
		ib.position(0);
		gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int pix = b[i * w + j];
				int pb = (pix >> 16) & 0xff;
				int pr = (pix << 16) & 0x00ff0000;
				int pix1 = (pix & 0xff00ff00) | pr | pb;
				bt[(h - i - 1) * w + j] = pix1;
			}
		}
		// Bitmap sb=Bitmap.createBitmap(bt, w, h, true);
		// Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.RGB_565);
		Bitmap sb = Bitmap.createBitmap(bt, w, h, Config.ARGB_8888);

		return sb;
	}

	// private float mPosition=-0.5f;
	private void initVertexArray() {
		currX = grid.HLINE_ENDX;
		getDeltaX();

		for (int i = 0; i < MAX_POINT; i++) {
			vertexArray[i * 3] = currX;
			currX = currX - deltaX;

			vertexArray[i * 3 + 2] = 0;
		}

		if (currTotalPointNumber != 0) {
			highlightPointPosition = currTotalPointNumber / 2;
		} else {
			highlightPointPosition = 350;
		}

		highlightPointVert[0] = vertexArray[highlightPointPosition * 3];
		highlightPointVert[2] = vertexArray[highlightPointPosition * 3 + 2];

		pointNumber = 0;
		normalized = false;
	}

	private void drawHighlightPoint(GL10 gl) {
		highlightPointVert[1] = vertexArray[highlightPointPosition * 3 + 1];

		ByteBuffer vertHPointBuffer = ByteBuffer
				.allocateDirect(highlightPointVert.length * 4);
		vertHPointBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer vertexHPointer = vertHPointBuffer.asFloatBuffer();
		vertexHPointer.put(highlightPointVert);
		vertexHPointer.position(0);

		// gl.glColor4f(60.0f / 256, 75.0f/ 256, 113.0f /256, 1.0f);
		// gl.glColor4f(0.5f, 1.5f, 1.5f, 1.0f);
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
		if (ECG_MODE_NORMAL == mode || ECG_MODE_LOW == mode
				|| ECG_MODE_HIGH == mode) {
			mEcgMode = mode;
			initVertexArray();
		}
	}

	private void getDeltaX() {
		// grid.getyUnitCellSize()代表1mm(标准值1mm=0.1mv)对应opengl坐标值，
		// grid.getyUnitCellSize()*10代表1mv映射到opengl坐标中的值
		float defaultFactor = (grid.getyUnitCellSize() * 10) / BASEFACTOR;
		if (ECG_MODE_LOW == mEcgMode) {
			// 走纸速度12.5mm/s
			deltaX = grid.getxUnitCellSize() / ECG_MODE_LOW_PERUNIT_POINT;
			currTotalPointNumber = grid.getGRID_NUM_H()
					* ECG_MODE_LOW_PERUNIT_POINT;
			factorV = defaultFactor * 0.5f;
		} else if (ECG_MODE_HIGH == mEcgMode) {
			// 走纸速度50mm/s
			deltaX = grid.getxUnitCellSize() / ECG_MODE_HIGH_PERUNIT_POINT;
			currTotalPointNumber = grid.getGRID_NUM_H()
					* ECG_MODE_HIGH_PERUNIT_POINT;
			factorV = defaultFactor * 2;
		} else {
			// 默认走纸速度25mm/s
			deltaX = grid.getxUnitCellSize() / ECG_MODE_NORMAL_PERUNIT_POINT;
			currTotalPointNumber = grid.getGRID_NUM_H()
					* ECG_MODE_NORMAL_PERUNIT_POINT;
			factorV = defaultFactor;
		}
	}
}
