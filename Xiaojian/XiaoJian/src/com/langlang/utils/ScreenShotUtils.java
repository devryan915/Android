package com.langlang.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;

/**
 * 进行截屏工具类
 */
public class ScreenShotUtils {
	/**
	 * 进行截取屏幕
	 * 
	 * @param pActivity
	 * @return bitmap
	 */
	public static Bitmap takeScreenShot(Activity pActivity) {
		Bitmap bitmap = null;
		View view = pActivity.getWindow().getDecorView();
		// 设置是否可以进行绘图缓存
		view.setDrawingCacheEnabled(true);
		// 如果绘图缓存无法，强制构建绘图缓存
		view.buildDrawingCache();
		// 返回这个缓存视图
		bitmap = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		// 测量屏幕宽和高
		view.getWindowVisibleDisplayFrame(frame);
		int stautsHeight = frame.top;
		Log.d("jiangqq", "状态栏的高度为:" + stautsHeight);

		int width = pActivity.getWindowManager().getDefaultDisplay().getWidth();
		int height = pActivity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 根据坐标点和需要的宽和高创建bitmap
		bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height
				- stautsHeight);
		return bitmap;
	}
	
	public static Bitmap takeScreenshot(Activity pActivity) {
		Bitmap bitmap = null;
		View view = pActivity.getWindow().getDecorView();
		// 设置是否可以进行绘图缓存
		view.setDrawingCacheEnabled(true);
		// 如果绘图缓存无法，强制构建绘图缓存
		view.buildDrawingCache();
		// 返回这个缓存视图
		bitmap = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		// 测量屏幕宽和高
		view.getWindowVisibleDisplayFrame(frame);
		int stautsHeight = frame.top;
		Log.d("jiangqq", "状态栏的高度为:" + stautsHeight);

		int width = pActivity.getWindowManager().getDefaultDisplay().getWidth();
		int height = pActivity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 根据坐标点和需要的宽和高创建bitmap
		bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height
				- stautsHeight);
		return bitmap;
	}

	/**
	 * 保存图片到sdcard中
	 * 
	 * @param pBitmap
	 */
	public static boolean savePic(Bitmap pBitmap, String strName) {
		FileOutputStream fos = null;
		if (pBitmap == null) {
			return false;
		}
		try {
			fos = new FileOutputStream(strName);
			if (null != fos) {
				pBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				return true;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		
		pBitmap.recycle();
		pBitmap = null;
		
		return false;
	}

	/**
	 * 截图
	 * 
	 * @param pActivity
	 * @return 截图并且保存sdcard成功返回true，否则返回false
	 */
	public static boolean shotBitmap(Activity pActivity, String image_part) {

		// return ScreenShotUtils.savePic(takeScreenShot(pActivity),
		// "sdcard/"+System.currentTimeMillis()+".png");
		return ScreenShotUtils.savePic(takeScreenShot(pActivity), image_part);
	}

	public static Bitmap SavePixels(int x, int y, int w, int h, GL10 gl) {
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
		Bitmap sb = Bitmap.createBitmap(bt, w, h,Config.RGB_565);
		return sb;
	}

	public static void SavePNG(int x, int y, int w, int h, String fileName,
			GL10 gl) {
		Bitmap bmp = SavePixels(x, y, w, h, gl);
		try {
			FileOutputStream fos = new FileOutputStream(Program.getSDLangLangImagePath()
					+ fileName); // android123提示大家，如何2.2或更高的系统sdcard路径为/mnt/sdcard/
			bmp.compress(CompressFormat.PNG, 100, fos); // 保存为png格式，质量100%
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	// 以下工具函数用于将View保存为bitmap
	public static Bitmap saveViewToBitmap(View view, int width, int height, Config config) {
		Bitmap bmp = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bmp);
		view.draw(canvas);
		
	    canvas.save(Canvas.ALL_SAVE_FLAG);  
	    canvas.restore();
		
		return bmp;
	}
	
	public static Bitmap saveViewToBitmap(View view, int width, int height) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		view.draw(canvas);
		
	    canvas.save(Canvas.ALL_SAVE_FLAG);  
	    canvas.restore();
		
		return bmp;
	}
	
	public static Bitmap saveViewToBitmap(View view) {
		Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		view.draw(canvas);
		
	    canvas.save(Canvas.ALL_SAVE_FLAG);  
	    canvas.restore();
		
		return bmp;
	}
}
