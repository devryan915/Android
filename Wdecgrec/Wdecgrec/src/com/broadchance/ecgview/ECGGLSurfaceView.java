package com.broadchance.ecgview;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import javax.microedition.khronos.opengles.GL10;

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
import com.broadchance.utils.LogUtil;
import com.broadchance.wdecgrec.R;

public class ECGGLSurfaceView extends GLSurfaceView {
	private final static String TAG = ECGGLSurfaceView.class.getSimpleName();

	public enum EcgType {
		Range// 电压幅频
		, Speed// 速度
	}

	public enum EcgLevel {
		Level1, Level2, Level3, Level4
	}

	// // 走纸速度5mm/mv 12.5mm/s
	// public static final int ECG_MODE_LOW = 1;
	// /**
	// * 每个格子多少个点
	// */
	// public static final int ECG_MODE_LOW_PERUNIT_POINT = (int)
	// (FrameDataMachine.FRAME_DOTS_FREQUENCY / 12.5);
	// // 默认走纸速度10mm/mv 25mm/s
	// public static final int ECG_MODE_NORMAL = 2;
	// /**
	// * 每个格子多少个点
	// */
	// public static final int ECG_MODE_NORMAL_PERUNIT_POINT = (int)
	// (FrameDataMachine.FRAME_DOTS_FREQUENCY / 25);
	//
	// // 走纸速度20mm/mv 50mm/s
	// public static final int ECG_MODE_HIGH = 3;
	// /**
	// * 每个格子多少个点
	// */
	// public static final int ECG_MODE_HIGH_PERUNIT_POINT = (int)
	// (FrameDataMachine.FRAME_DOTS_FREQUENCY / 50);

	public OpenGLECGGrid grid;

	float currX = 0;
	float deltaX = 0;
	/**
	 * 单位mv的ecg电压差值即1mv对应ecg有效数据(分析样本数据散落值可以得出分布率最高的相对较大电压为最大电压，分布率相对较小电压为最小电压)
	 * 中最大电压和最小电压之差 此值是通过实际设备测试得出的有效值 按照10mm/mV定的标 200 / 0.65f
	 */
	public static Float BASEFACTOR = 156f;
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
	// public static final int MAX_POINT = 750 * 3;

	// float[] vertexArray = new float[750 * 3];
	float[] vertexArray = null;

	// float[] highlightPointVert = new float[BLACK_LINE_POINTS * 3];
	// int highlightPointPosition = 169;

	/**
	 * 当前所有点数
	 */
	private int currTotalPointNumber = 0;
	/**
	 * 当前更新点数的下标
	 */
	// private AtomicInteger curPointIndex = new AtomicInteger(0);

	boolean normalized = false;
	float maxY;
	float minY;

	float prevMaxY;
	float prevMinY;

	float middleY;

	// int countDraw = 0;

	// CanvasReadyCallback callback = null;

	// private Date drawDate = null;

	// private int mEcgMode = ECG_MODE_NORMAL;
	// private final static int BLACK_LINE_POINTS = 20;

	/**
	 * grid小格线颜色
	 */
	public static int gridLightColor = 0XFFFFFFFF;
	/**
	 * grid中格线颜色
	 */
	public static int gridDarkColor = 0XFFFFFFFF;
	/**
	 * grid背景色
	 */
	int gridBgColor = 0X00000000;
	/**
	 * 心电曲线颜色
	 */
	public static int ecgLineColor = 0XFF00FF00;
	/**
	 * 网格左右边距
	 */
	int leftRightMargin = 10;
	/**
	 * 每英寸mm
	 */
	private final static float mmPerInch = 25.4f;
	/**
	 * 缓存的点，用于计算平均差值
	 */
	private LinkedBlockingQueue<Integer> vertextQueue = new LinkedBlockingQueue<Integer>();
	/**
	 * 
	 */
	private Integer[] queueArray;

	/**
	 * 必须保留足够大的点数，保证在这个范围内能够得到稳定的平均差值
	 */
	// private static int MAXVERTEXT = 125 * 3;

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

	// public int getMode() {
	// return mEcgMode;
	// }

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

	// public void setCallback(CanvasReadyCallback callback) {
	// this.callback = callback;
	// }

	private void normalizePoints(Integer[] data) {
		int pointCount = data.length;
		for (int i = 0; i < pointCount; i++) {
			vertextQueue.offer(data[i]);
		}
		int length = vertextQueue.size() - currTotalPointNumber * 1;
		for (int i = 0; i < length; i++) {
			vertextQueue.poll();
		}
		queueArray = new Integer[0];
		queueArray = vertextQueue.toArray(queueArray);
		float premaxY = queueArray[0];
		float preminY = queueArray[0];
		for (int i = 0; i < queueArray.length; i++) {
			if (queueArray[i] > premaxY)
				premaxY = queueArray[i];
			if (queueArray[i] < preminY)
				preminY = queueArray[i];
		}
		float postmaxY = queueArray[0];
		float postminY = queueArray[0];
		if (queueArray.length > length && length > 0) {
			postmaxY = queueArray[length];
			postminY = queueArray[length];
			for (int i = length; i < queueArray.length; i++) {
				if (queueArray[i] > postmaxY)
					postmaxY = queueArray[i];
				if (queueArray[i] < postminY)
					postminY = queueArray[i];
			}
		}
		// maxY = premaxY * 0.7f + postmaxY * 0.3f;
		// minY = preminY * 0.7f + postminY * 0.3f;
		maxY = Math.max(premaxY, postmaxY);
		minY = Math.max(preminY, postminY);
		// System.out.println(minY + (maxY - minY) * 0.5f);
	}

	private Object objectDrawLock = new Object();

	// private AtomicBoolean atomicBooleanDrawLock = new AtomicBoolean(false);
	// private int chkCount = 0;
	public void clearDraw() {
		pointNumber = 0;
	}

	public void drawECG(Integer[] data) {
		synchronized (objectDrawLock) {
			try {
				// long useTime = System.currentTimeMillis();
				int pointCount = data.length;
				if (pointCount == 0) {

					return;
				}
				// normalized = true;

				// 用于存放data数组中实际的最大值和最小值
				// float maxDataValue = 0.0f;
				// float minDataValue = 0.0f;

				// 用于存放最大的几何坐标值和最小的几何坐标值
				// float max = 0.0f;
				// float min = 0.0f;

				// yGridValue用于表示每个单元格之间对应的实际电压差
				// 每个单位的ECG数值对应的电压差为0.006V
				// float yGridValue = 0.0f;
				// int maxIdx = 0;
				// int maxIIdx = 1;
				if (!normalized) {
					// if (callback != null) {
					// callback.notifyCanvasReady();
					// }

					maxY = data[0];
					minY = data[0];

					// maxDataValue = maxY;
					// minDataValue = minY;
					normalizePoints(data);
					prevMaxY = maxY;
					prevMinY = minY;

					middleY = minY + (maxY - minY) * 0.5f;
					normalized = true;
				} else if (normalized) {
					// 检查是否需要重新归一化
					maxY = data[0];
					minY = data[0];
					normalizePoints(data);
					int dealtMaxY = Math.abs((int) (prevMaxY - maxY));
					int dealtMinY = Math.abs((int) (prevMinY - minY));
					if (dealtMaxY > Math.abs((int) (prevMaxY * 0.25f))
							|| dealtMinY > Math.abs((int) (prevMinY * 0.25f))) {
						prevMaxY = maxY;
						prevMinY = minY;
					}
					middleY = prevMinY + (prevMaxY - prevMinY) * 0.5f;
				}

				if (normalized) {
					// for (int i = 0; i < pointCount; i++) {
					// int curPoint = curPointIndex.get();
					// vertexArray[curPoint * 3 + 1] = middle_y
					// + (data[i] - middleY) * factorV;
					// // 限定边界，最小不超过下限，最大不超过上限
					// vertexArray[curPoint * 3 + 1] = Math.min(
					// vertexArray[curPoint * 3 + 1], grid.VLINE_ENDY);
					// vertexArray[curPoint * 3 + 1] = Math.max(
					// vertexArray[curPoint * 3 + 1],
					// grid.VLINE_STARTY);
					// curPointIndex.getAndIncrement();
					// curPointIndex.set(curPointIndex.get()
					// % currTotalPointNumber);
					// }
					for (int i = currTotalPointNumber - 1, j = queueArray.length - 1; i >= 0
							&& j >= 0; i--, j--) {
						// 将原来的数据往前
						vertexArray[i * 3 + 1] = middle_y
								+ (queueArray[j] - middleY) * factorV;
						// 限定边界，最小不超过下限，最大不超过上限
						vertexArray[i * 3 + 1] = Math.min(
								vertexArray[i * 3 + 1], grid.VLINE_ENDY);
						vertexArray[i * 3 + 1] = Math.max(
								vertexArray[i * 3 + 1], grid.VLINE_STARTY);
					}
					// LogUtil.d(TAG, "drawEcg data：" + "prevMaxY " + prevMaxY
					// + " prevMinY" + prevMinY + "  当前中线：" + middleY
					// + " currTotalPointNumber/currTotalPointNumber："
					// + currTotalPointNumber + "/" + currTotalPointNumber);
				}
				if (pointNumber < currTotalPointNumber) {
					pointNumber += pointCount;
					pointNumber = Math.min(pointNumber, currTotalPointNumber);
				}
				requestRender();
			} catch (Exception e) {
				LogUtil.e(TAG, e);
			} finally {
				// atomicBooleanDrawLock.set(false);
			}
		}
	}

	public int getCurrTotalPointNumber() {
		return currTotalPointNumber;
	}

	public void setCurrTotalPointNumber(int currTotalPointNumber) {
		this.currTotalPointNumber = currTotalPointNumber;
	}

	// public void reset() {
	// pointNumber = 0;
	// normalized = false;
	// requestRender();
	// }

	public void DrawScene(GL10 gl) {
		if (grid == null || vertexArray == null)
			return;
		float[] bgColor = CommonUtil.colorToRGB(gridBgColor);
		gl.glClearColor(bgColor[1], bgColor[2], bgColor[3], bgColor[0]);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		FloatBuffer vertex = CommonUtil.floatBufferUtil(vertexArray);
		gl.glTranslatef(0, 0, 0);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// 画网格
		grid.drawGrid(gl, gridLightColor, gridDarkColor);
		if (pointNumber > 0) {
			float[] ecglineColor = CommonUtil.colorToRGB(ecgLineColor);
			gl.glColor4f(ecglineColor[1], ecglineColor[2], ecglineColor[3],
					ecglineColor[0]);
			gl.glLineWidth(2.0f);

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
			// int curPoint = curPointIndex.get();
			gl.glDrawArrays(GL10.GL_LINE_STRIP,
					Math.max(currTotalPointNumber - pointNumber, 0),
					pointNumber);
			// int nStartPosition = curPoint + BLACK_LINE_POINTS;
			// int nPoints = currTotalPointNumber - nStartPosition;
			// if (nPoints > 0 && pointNumber > nStartPosition) {
			// gl.glDrawArrays(GL10.GL_LINE_STRIP, nStartPosition, nPoints);
			// }
			// if (normalized && pointNumber == currTotalPointNumber) {
			// drawHighlightPoint(gl);
			// }

			// if (callback != null && callback.getCapture()) {
			// System.out.println("action ECGGLSurfaceView width, height:"
			// + this.getWidth() + "," + this.getHeight());
			// Bitmap bitmap = savePixels(0, 0, this.getWidth(),
			// this.getHeight(), gl);
			// callback.onCaptured(bitmap);
			// bitmap = null;
			// }
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
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
		currX = grid.HLINE_STARTX;

		for (int i = 0; i < currTotalPointNumber; i++) {
			vertexArray[i * 3] = currX;
			currX = currX + deltaX;
			vertexArray[i * 3 + 2] = 0;
		}

		// if (currTotalPointNumber != 0) {
		// highlightPointPosition = currTotalPointNumber / 2;
		// } else {
		// highlightPointPosition = 350;
		// }
		// currX = grid.HLINE_STARTX;
		// for (int i = 0; i < BLACK_LINE_POINTS; i++) {
		// highlightPointVert[i * 3] = currX;
		// currX = currX + deltaX;
		//
		// highlightPointVert[i * 3 + 2] = 0;
		// }
		// highlightPointVert[0] = vertexArray[highlightPointPosition * 3];
		// highlightPointVert[2] = vertexArray[highlightPointPosition * 3 + 2];

		// pointNumber = 0;
		normalized = false;
	}

	// private void drawHighlightPoint(GL10 gl) {
	// // highlightPointVert[1] = vertexArray[highlightPointPosition * 3 + 1];
	//
	// ByteBuffer vertHPointBuffer = ByteBuffer
	// .allocateDirect(highlightPointVert.length * 4);
	// vertHPointBuffer.order(ByteOrder.nativeOrder());
	// FloatBuffer vertexHPointer = vertHPointBuffer.asFloatBuffer();
	// vertexHPointer.put(highlightPointVert);
	// vertexHPointer.position(0);
	//
	// // gl.glColor4f(60.0f / 256, 75.0f/ 256, 113.0f /256, 1.0f);
	// // gl.glColor4f(0.5f, 1.5f, 1.5f, 1.0f);
	// gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
	// gl.glPointSize(9f);
	//
	// gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexHPointer);
	// gl.glDrawArrays(GL10.GL_POINTS, 0, BLACK_LINE_POINTS);
	// }

	// public interface CanvasReadyCallback {
	// public void notifyCanvasReady();
	//
	// public boolean getCapture();
	//
	// public void onCaptured(Bitmap bitmap);
	//
	// public boolean stopPainting();
	//
	// public void onPaintingStopped(float yGridValue);
	// }
	//
	// public void redrawForCapture() {
	// requestRender();
	// }

	public void setEcgMode(EcgType type, EcgLevel level) {
		if (type == EcgType.Range) {
			float defaultFactor = (grid.getyUnitCellSize()) / BASEFACTOR;
			if (level == EcgLevel.Level1) {
				// 0.1mV/mm
				factorV = defaultFactor / 0.1f;
			} else if (level == EcgLevel.Level2) {
				// 0.2mV/mm
				factorV = defaultFactor / 0.2f;
			} else if (level == EcgLevel.Level3) {
				// mV/mm
				factorV = defaultFactor / 0.5f;
			} else if (level == EcgLevel.Level4) {
				// mV/mm
				factorV = defaultFactor / 1.0f;
			}
		} else {
			float speed = 0;
			if (level == EcgLevel.Level1) {
				// 10mm/s
				speed = (int) (FrameDataMachine.FRAME_DOTS_FREQUENCY / 10.0);
				deltaX = grid.getxUnitCellSize() / speed;
				currTotalPointNumber = (int) (grid.getGRID_NUM_H() * speed);
			} else if (level == EcgLevel.Level2) {
				// 20mm/s
				speed = (int) (FrameDataMachine.FRAME_DOTS_FREQUENCY / 20.0);
				deltaX = grid.getxUnitCellSize() / speed;
				currTotalPointNumber = (int) (grid.getGRID_NUM_H() * speed);
			} else if (level == EcgLevel.Level3) {
				// 20mm/s
				speed = (int) (FrameDataMachine.FRAME_DOTS_FREQUENCY / 25.0);
				deltaX = grid.getxUnitCellSize() / speed;
				currTotalPointNumber = (int) (grid.getGRID_NUM_H() * speed);
			}
			vertexArray = new float[currTotalPointNumber * 3];
			initVertexArray();
			pointNumber = 0;
		}
	}

	// private void getDeltaX() {
	// // grid.getyUnitCellSize()代表1mm(标准值1mm=0.1mv)对应opengl坐标值，
	// // grid.getyUnitCellSize()*10代表1mv映射到opengl坐标中的值
	// float defaultFactor = (grid.getyUnitCellSize() * 10) / BASEFACTOR;
	// if (ECG_MODE_LOW == mEcgMode) {
	// // 走纸速度12.5mm/s
	// deltaX = grid.getxUnitCellSize() / ECG_MODE_LOW_PERUNIT_POINT;
	// currTotalPointNumber = grid.getGRID_NUM_H()
	// * ECG_MODE_LOW_PERUNIT_POINT;
	// factorV = defaultFactor * 0.5f;
	// } else if (ECG_MODE_HIGH == mEcgMode) {
	// // 走纸速度50mm/s
	// deltaX = grid.getxUnitCellSize() / ECG_MODE_HIGH_PERUNIT_POINT;
	// currTotalPointNumber = grid.getGRID_NUM_H()
	// * ECG_MODE_HIGH_PERUNIT_POINT;
	// factorV = defaultFactor * 2;
	// } else {
	// // 默认走纸速度25mm/s
	// deltaX = grid.getxUnitCellSize() / ECG_MODE_NORMAL_PERUNIT_POINT;
	// currTotalPointNumber = grid.getGRID_NUM_H()
	// * ECG_MODE_NORMAL_PERUNIT_POINT;
	// factorV = defaultFactor;
	// }
	// vertexArray = new float[currTotalPointNumber * 3];
	// }
}
