package com.langlang.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends Activity{
	public static double ScreanW, ScreanH;// 屏幕宽高d
	public static int ble_state=0; 
	public static boolean mGaurdian_role = true;
	public static String m_version_base;
	public static int m_version_code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setFullScrean();
		super.onCreate(savedInstanceState);
		setScreanWH();
		getVersionName();
	}
	/**
	 * 获取版本号
	 */
	private void getVersionName(){
		  // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        String version_name;
        int version_code;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
			 version_name = packInfo.versionName;
			 version_code= packInfo.versionCode;
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			version_name=null;
			version_code=0;
			e.printStackTrace();
		}
		m_version_base=version_name;
		m_version_code=version_code;
		System.out.println("版本号："+m_version_base+":"+m_version_code);
	}
	/**
	 * 设置全屏
	 */
	public void setFullScrean() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	/**
	 * 获取屏幕宽高
	 */
	public void setScreanWH() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		ScreanW = metrics.widthPixels;
		ScreanH = metrics.heightPixels;
	}
}
