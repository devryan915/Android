package com.example.testopengl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity implements OnGestureListener {

	MyGLSurfaceView glSurfaceView;
	// 定义旋转角度
	public float anglex = 0f;
	public float angley = 0f;
	static final float ROTATE_FACTOR = 60;
	// 定义手势检测器实例
	GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		glSurfaceView = (MyGLSurfaceView) findViewById(R.id.myGLSurfaceView1);
		// 创建手势检测器
		detector = new GestureDetector(this, this);
		initSurfaceView();

	}

	// 设置ECG控件尺寸
	private int mPixelsPerMm = 0;

	private void getPixelsPerMm() {
		DisplayMetrics dm = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int widthPixels = dm.widthPixels;
		int heightPixels = dm.heightPixels;
		float density = dm.density;
		// int pixelsPerInchCalcu = (int) density * 160;
		float pixelsPerInchCalcu = density * 160;
		float pixelsPerInch = dm.densityDpi;

		// float scaledDensity = dm.scaledDensity;

		double diagonalPixels = Math.sqrt(Math.pow(widthPixels, 2)
				+ Math.pow(heightPixels, 2));
		// double screenSize
		// = diagonalPixels * 1.0 / (160 * density);
		double screenSize = diagonalPixels * 1.0 / (pixelsPerInchCalcu);
		System.out.println(screenSize);
		mPixelsPerMm = 10 * pixPerCm(dm.densityDpi);
	}

	private int pixPerCm(double pixelsPerInch) {
		return (int) (pixelsPerInch / 2.54f * 0.93f);
	}

	private void initSurfaceView() {

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();
		sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
		sb.append("\nLine1Number = " + tm.getLine1Number());
		sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\nNetworkType = " + tm.getNetworkType());
		sb.append("\nPhoneType = " + tm.getPhoneType());
		sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
		sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
		sb.append("\nSimState = " + tm.getSimState());
		sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		//
		sb.append("\n BRAND = " + Build.BRAND);
		sb.append("\n DEVICE = " + Build.DEVICE);
		sb.append("\n PRODUCT = " + Build.PRODUCT);
		sb.append("\n MODEL = " + Build.MODEL);
		Log.e("info", sb.toString());

		DisplayMetrics dm = new DisplayMetrics();
		DisplayMetrics resdm = new DisplayMetrics();
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams lp = (LayoutParams) glSurfaceView.getLayoutParams();
		// dm.densityDpi / 25.4f计算出每mm多少像素pix/mm，
		// int glViewpix = (int) (dm.densityDpi / 25.4f);
		// *1.1f是由于50 *
		// glViewpix等于50个标准心电高度，对应OpenGLECGGrid.CANVAS_HEIGHT的网格高度,而ECGRenderer中设置gl.glOrthof(-ratio,
		// ratio, -1.1f, 1.1f, 1, -1);
		// 设置的窗口大小为2.2f，所以此处需要乘以1.1f
		// lp.height = 50 * glViewpix* 1.1f;
		// lp.width = 75 * glViewpix* 1.1f;
		//

		Point point = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(point);
		double x = Math.pow(point.x / dm.xdpi, 2);
		double y = Math.pow(point.y / dm.ydpi, 2);
		double screenInches = Math.sqrt(x + y);
		getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
		resdm = getResources().getDisplayMetrics();
		double avgDPI = Math.sqrt(Math.pow(outMetrics.xdpi, 2)
				+ Math.pow(outMetrics.ydpi, 2));
		double glpixpermm = outMetrics.ydpi / 25.4f;
		Toast.makeText(
				MainActivity.this,
				"set xdpi:" + dm.xdpi + " ydpi" + dm.ydpi + " "
						+ outMetrics.xdpi + "x" + outMetrics.ydpi + " avgDPI"
						+ avgDPI + " glpixpermm" + glpixpermm,
				Toast.LENGTH_LONG).show();
		lp.height = (int) (50 * glpixpermm * 1.1f);
		lp.width = (int) (75 * glpixpermm * 1.1f);
		// lp.height = 480;
		// lp.width = 270;
		glSurfaceView.setLayoutParams(lp);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		// 将该Activity上的触碰事件交给GestureDetector处理
		return detector.onTouchEvent(me);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		setAnglexAndAndley(distanceX, distanceY);

		Log.d("chen", "onScroll(),distanceX=" + distanceX + ",distanceY="
				+ distanceY);
		return true;
	}

	private void setAnglexAndAndley(float velocityX, float velocityY) {
		velocityX = velocityX > 2000 ? 2000 : velocityX;
		velocityX = velocityX < -2000 ? -2000 : velocityX;
		velocityY = velocityY > 2000 ? 2000 : velocityY;
		velocityY = velocityY < -2000 ? -2000 : velocityY;

		// 根据横向上的速度计算沿Y轴旋转的角度
		angley -= velocityX * ROTATE_FACTOR / 2000;
		// 根据纵向上的速度计算沿X轴旋转的角度
		anglex -= velocityY * ROTATE_FACTOR / 2000;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
}
