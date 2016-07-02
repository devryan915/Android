package com.magus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.magus.excption.HttpServiceErrorException;
import com.magus.excption.NoNetworkActivityException;

public final class MagusCache{
	public static ExecutorService pool = Executors.newFixedThreadPool(6);
	private static CacheMap<Bitmap> imageCaches = new CacheMap<Bitmap>();
	private static CacheMap<String> dataCaches = new CacheMap<String>();
	private static String cachePath = MagusTools.getDataPath();

	public static Bitmap getBitmapFromCaches(String url) {
		if (imageCaches.containsKey(url)) {
			SoftReference<Bitmap> srf = imageCaches.get(url);
			if (srf != null && srf.get() != null) {
				return srf.get();
			}
		}
		return null;
	}
	
	public static String getStrDataFromCaches(String url) {
		if (dataCaches.containsKey(url)) {
			SoftReference<String> srf = dataCaches.get(url);
			if (srf != null && srf.get() != null) {
				return srf.get();
			}
		}
		return null;
	}

	/**
	 * 根据给定的url从缓存里获取bitmap，如果缓存里没有，则连网获取。
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String url) {
		Bitmap bm = null;
		bm = getBitmapFromCaches(url);
		if (bm == null) {
			bm = getBitmapFromSDOrIntenet(context, url, HttpConst.POST);
		}
		return bm;
	}
	/**
	 * 根据给定的url从缓存里获取数据，如果缓存里没有，则连网获取。
	 * 
	 * @param context
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws NoNetworkActivityException 
	 * @throws HttpServiceErrorException 
	 * @throws HttpServiceTimeOutException 
	 */
	public static String getData(Context context, String url) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		String str = null;
		str = getStrDataFromCaches(url);
		if (str == null) {
			str = getStrDataFromSDOrIntenet(context, url, HttpConst.POST);
		}
		return str;
	}

	public static Bitmap getBitmapFromSDOrIntenet(Context context, String url,
			String method) {
		Bitmap bm = getBitmapFromSdcard(url);
		if (bm == null) {
			bm = getBitmapFromIntenet(context, url, method);
		}
		return bm;
	}

	public static Bitmap getBitmapFromIntenet(Context context, String url,
			String method) {
		Bitmap bm = null;
		try {
			InputStream is = MagusUrlConnection.getInputStream(context,
					url, method);
			if (is != null) {
				byte[] b = MagusTools.inputStreamTobyte(is);
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = 10;
				bm = BitmapFactory.decodeByteArray(b, 0, b.length);
				imageCaches.put(url, new SoftReference<Bitmap>(bm));
				saveToSdcard(getAllPath(url, StaticFeild.bitmap_postfix), b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}
	
	public static String getStrDataFromSDOrIntenet(Context context, String url) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		return getStrDataFromIntent(context, url, HttpConst.POST);
	}
	public static String getStrDataFromSDOrIntenet(Context context, String url,String method) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		String str = getStrDataFromSdcard(url);
		if (str == null) {
			str = getStrDataFromIntent(context, url, method);
		}
		return str;
	}

	public static String getStrDataFromIntent(Context context, String url,
			String method) throws NoNetworkActivityException, SocketTimeoutException, HttpServiceErrorException, IOException {
		String str = null;
		str = MagusUrlConnection.getConnectionStr(context,url,method);
		if (str != null) {
			dataCaches.put(url, new SoftReference<String>(str));
			saveToSdcard(getAllPath(url, StaticFeild.string_postfix), str.getBytes());
		}
		return str;
	}

	private static void saveToSdcard(String fileName, byte[] b) {
		if (b == null || cachePath == null) {
			return;
		}
		MagusTools.writeFile(fileName, b);
	}

	/**
	 * update File LastModified
	 * 
	 * @param fileName
	 *            文件全路径+ 名称
	 */
	public static void upLastModified(String fileName) {
		File imgFile = new File(fileName);
		if (imgFile != null && imgFile.exists()) {
			imgFile.setLastModified(System.currentTimeMillis());// 最后修改的时间
		}
	}

	private static Bitmap getBitmapFromSdcard(String url) {
		String fileName = getAllPath(url, StaticFeild.bitmap_postfix);
		InputStream stream = MagusTools.readFile(fileName);
		try {
			if (stream != null) {
				Bitmap bm = BitmapFactory.decodeStream(stream);
				upLastModified(fileName); // 更新访问时间
				imageCaches.put(url, new SoftReference<Bitmap>(bm));
				return bm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private static String getStrDataFromSdcard(String url) {
		String fileName = getAllPath(url, StaticFeild.string_postfix);
		try {
			String str = new String(MagusTools.readFileToByte(fileName));
			if (str != null) {
				dataCaches.put(url, new SoftReference<String>(str));
				upLastModified(fileName); // 更新访问时间
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	/**
	 * 返回url在转换成文件后默认的全路径名称
	 * 
	 * @param url
	 * @param s
	 *            文件后缀名称
	 * @return
	 */
	public static String getAllPath(String url, String s) {
		url = Base64.encodeToString(url.getBytes(), Base64.DEFAULT);
		url = URLEncoder.encode(url);
		String path = cachePath + StaticFeild.CACHEDIR + url + "." + s;
		return path;
	}

	public static boolean clearCache(){
		try {
			MagusTools.clearFile(MagusTools.getDataPath()+StaticFeild.CACHEDIR);
			imageCaches.clear();
			dataCaches.clear();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void setDrawable(final String url, final ImageView image,
			int defId, final Map<String, Object> data) {
		image.setImageResource(defId);
		image.setTag(url);
		Bitmap bm = getBitmapFromCaches(url);
		if (bm != null) {
			if (data != null) {
				data.put("bitmap", bm);
			}
			image.setImageBitmap(bm);
		} else {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Activity a = (Activity) image.getContext();
						final Bitmap bm = getBitmapFromSDOrIntenet(a, url,
								HttpConst.GET);
						if (bm != null && url.equals(image.getTag())) {
							if (data != null) {
								data.put("bitmap", bm);
							}
							a.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									image.setImageBitmap(bm);
								}
							});
						}
					} catch (Exception e) {
						MagusTools.writeLog(e);
					}
				}
			});
		}
	}
}
