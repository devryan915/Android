package com.kc.ihaigo.util;

/**
 * 通用工具类
 */
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 
 * @author ryan
 */

public class Utils {

	/**
	 * 清除系统缓存
	 * 
	 * @param ctx
	 */
	public static long clearCache() {
		long clearsize = ACache.get(IHaiGoMainActivity.main).getCacheManager()
				.getCacheSize();
		ACache.get(IHaiGoMainActivity.main).clear();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
		return clearsize;

	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 
	 * @Title: getDefaultImageOptions
	 * @user: ryan.wang
	 * @Description:
	 * 
	 * @return DisplayImageOptions
	 * @throws
	 */
	public static DisplayImageOptions getDefaultImageOptions(Integer... resId) {
		return new DisplayImageOptions.Builder()
				.showImageForEmptyUri(
						(resId != null && resId.length > 0)
								? resId[0]
								: R.drawable.empty_large).cacheInMemory(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.NONE)
				.considerExifParams(true).build();
	}

	public static SimpleImageLoadingListener getDefaultAnimateListener() {
		return new SimpleImageLoadingListener() {
			final List<String> displayedImages = Collections
					.synchronizedList(new LinkedList<String>());

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				if (loadedImage != null) {
					ImageView imageView = (ImageView) view;
					boolean firstDisplay = !displayedImages.contains(imageUri);
					if (firstDisplay) {
						FadeInBitmapDisplayer.animate(imageView, 500);
						displayedImages.add(imageUri);
					}
				}
			}
		};
	}

	/**
	 * @Title: getCurrentTime
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param long1
	 * @param string
	 * @return CharSequence
	 * @throws
	 */

	public static String getCurrentTime(long time, String format) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	/**
	 * @Title: compareTime
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param maxtime
	 * @param mintime
	 * @return 两个时间的小时差
	 * @throws
	 */
	public static String compareTime(long maxtime, long mintime) {

		long a = (maxtime - mintime);
		// 秒
		int s = (int) (a / 1000);
		// 分
		int min = s / 60;
		// 时
		int h = min / 60;
		if (min < 1 && min > 0) {
			return String.valueOf("刚刚");
		} else if (h < 1 && h > 0) {
			return String.valueOf(min + "分钟前");
		} else if (1 <= h && h < 24) {
			return String.valueOf(h + "小时前");
		} else if (h >= 24) {

			Date date = new Date(mintime);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm",
					Locale.getDefault());
			String currentTime = sdf.format(date);
			return currentTime;
		}
		return String.valueOf("刚刚");

	}

	/**
	 * 
	 * @Title: readUnicode1
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param unicodeStr
	 *            如 \u62c9
	 * @return String
	 * @throws
	 */
	public static String readUnicode(String unicodeStr) {
		StringBuilder buf = new StringBuilder();
		// 因为java转义和正则转义，所以u要这么写
		String[] cc = unicodeStr.split("\\\\u");
		for (String c : cc) {
			if (c.equals(""))
				continue;
			int cInt = Integer.parseInt(c, 16);
			char cChar = (char) cInt;
			buf.append(cChar);
		}
		return buf.toString();
	}

	/**
	 * @Title:
	 * @user: zouxianbin
	 * @Description: TODO
	 * 
	 * 
	 * @return double 保留两位小数
	 * @throws
	 */
	public static String format(double amount) {
		BigDecimal money = new BigDecimal(amount);
		return money.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "";
	}

	/**
	 * 
	 * @Title: getScreenWidth
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param context
	 *            传入Activity
	 * @return int
	 * @throws
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		return mDisplayMetrics.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		return mDisplayMetrics.heightPixels;
	}

	/**
	 * 
	 * @Title: dip2px
	 * @user: ryan.wang
	 * @Description: 密度转分辨率
	 * 
	 * @param dipValue
	 * @return int
	 * @throws
	 */
	public static int dip2px(Activity context, float dipValue) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		return (int) (dipValue * mDisplayMetrics.scaledDensity);
	}

	/**
	 * 
	 * @Title: hideInputMethod
	 * @user: ryan.wang
	 * @Description: 隐藏输入法
	 * 
	 * @param context
	 *            void
	 * @throws
	 */
	public static void hideInputMethod(Activity context) {
		try {
			// 隐藏输入法
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(context.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @Title: isJsonObject
	 * @user: ryan.wang
	 * @Description: 是否是jsonobject
	 * 
	 * @param json
	 * @return boolean
	 * @throws
	 */
	public static boolean isJsonObject(String json) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
		}
		return jsonObject == null ? false : true;
	}

	/**
	 * 
	 * @Title: installApk
	 * @user: ryan.wang
	 * @Description: 安装APK
	 * 
	 * @param context
	 * @param cachePath
	 *            void
	 * @throws
	 */
	public static void installApk(Activity context, String apkPath) {
		// try {
		// //修改文件权限
		// String command = "chmod 777 " + apkPath;
		// Runtime runtime = Runtime.getRuntime();
		// Process localProcess = runtime.exec(command);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apkPath),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 
	 * @Title: compareVersion
	 * @user: ryan.wang
	 * @Description: 是否有新版本，true有新版本
	 * 
	 * @param context
	 * @param newVersion
	 * @return boolean
	 * @throws
	 */
	public static boolean compareVersion(Context context, String newVersion) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo apkInfo = manager.getPackageInfo("com.kc.ihaigo",
					PackageManager.GET_META_DATA);
			String version = apkInfo.versionName;
			if (version.compareTo(newVersion) < 0)
				return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		return false;
	}
	/**
	 * 
	 * @Title: getAppVersion
	 * @user: ryan.wang
	 * @Description: 获取App版本号
	 * 
	 * @return String
	 * @throws
	 */
	public static String getAppVersion() {
		PackageManager manager = IHaiGoMainActivity.main.getPackageManager();
		PackageInfo apkInfo;
		String version = "V1.0.0";
		try {
			apkInfo = manager.getPackageInfo("com.kc.ihaigo",
					PackageManager.GET_META_DATA);
			version = "V" + apkInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
}
