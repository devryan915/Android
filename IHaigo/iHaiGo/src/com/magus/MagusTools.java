package com.magus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.magus.bean.ContactBean;

public class MagusTools {
	private static Properties ppt = new Properties();
	public static Location location;

	/**
	 * 将输入流转换为字符串
	 * 
	 * @param is
	 *            要转换的输入流
	 * @return 转换之后的字符串
	 * @throws IOException
	 *             如果要转化的输入流为空将抛出异常
	 */
	public static String inputStreamToString(InputStream is) throws IOException {
		if (is == null) {
			throw new IOException("不能将空的输入流转换为字符串");
		}
		byte[] data = inputStreamTobyte(is);
		return data == null ? null : new String(data);
	}

	/**
	 * 将给定的HashMap里的数据存入到Intent中
	 * 
	 * @param map
	 *            给定的map集合
	 * @param intent
	 *            要存入的intent
	 * @return 包含了给定hashmap的key，value的Intent。
	 */
	public static Intent mapToIntent(HashMap<String, Object> map, Intent intent) {
		for (Entry<String, Object> entry : map.entrySet()) {
			intent.putExtra(entry.getKey(), (String) entry.getValue());
		}
		return intent;
	}

	/**
	 * 将给定的JavaBean的集合，转化成存放HashMap的集合
	 * javabean里面的每个字段的字段名为map里的key，值为map里的value。
	 * 
	 * 一般在把bean的集合转化成 listView的adaper数据时使用。
	 * 
	 * @param <T>
	 * @param beanList
	 *            要转化的bean的集合
	 * @return 转换好之后的集合
	 */
	@SuppressWarnings("rawtypes")
	public static <T> List<HashMap<String, Object>> beanListToListmap(List<T> beanList) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if (beanList == null || beanList.size() == 0) {
			return list;
		}
		Class beanclass = beanList.get(0).getClass();
		HashMap<String, Method> methodNames = new HashMap<String, Method>();
		Method[] methods = beanclass.getDeclaredMethods();
		for (int j = 0; j < methods.length; j++) {
			String methodname = methods[j].getName();
			if (methodname.contains("get") && methods[j].getParameterTypes().length == 0) {
				methodNames.put(tofirstLowerCase(methodname.substring(3, methodname.length())), methods[j]);
			}
		}
		for (T bean : beanList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (Entry<String, Method> entry : methodNames.entrySet()) {
				try {
					map.put(entry.getKey(), entry.getValue().invoke(bean, new Object[] {}));
				} catch (Exception e) {
					MagusTools.writeLog(e);
				}
			}
			list.add(map);
		}

		return list;
	}

	/**
	 * 将给定的json字符串转换成 HashMap ,注意返回的HashMap 的Value 的值只可能是 String 和
	 * ArrayList<HashMap|integer|String> 类型
	 * 解析给定的json对象时（包括其包含的对象）如果不是数组，则直接以key，value的形式放入map中 如果是JSONArray 则已
	 * key，Arraylist<Map|integer|String> 的形式放入map中,ArrayList里面的map数据形式同上所述。
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static HashMap<String, Object> getJsonDataToMap(String jsonStr) {
		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			MagusTools.writeLog(e);
		}
		return getJsonDataToMap(json);
	}

	public static HashMap<String, Object> getJsonDataToMap(JSONObject json) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (json != null) {
			putJsonToMap(json, map);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private static void putJsonToMap(JSONObject json, HashMap<String, Object> map) {
		try {
			if (json != null) {
				Iterator<String> keys = json.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					Object object = json.opt(key);
					if (object instanceof JSONObject) {
						putJsonToMap((JSONObject) object, map);
					} else if (object instanceof JSONArray) {
						ArrayList<Object> list = new ArrayList<Object>();
						JSONArray array = (JSONArray) object;
						for (int i = 0; i < array.length(); i++) {
							Object subobj = array.get(i);
							if (subobj instanceof JSONObject) {
								list.add(getJsonDataToMap((JSONObject) subobj));
							} else {
								list.add(subobj);
							}
						}
						map.put(key, list);
					} else {
						int i = 0;
						String repeat = key;
						while (map.containsKey(repeat)) {
							i++;
							repeat = key + i;
						}
						map.put(repeat, object == null ? "" : object.toString());
					}
				}
			}
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
	}

	/**
	 * 把json字符串转化成javaBean
	 * 
	 * @param <T>
	 * @param jsonStr
	 *            json字符串
	 * @param beanclass
	 *            要转化的javabean的class
	 * @return 封装好数据之后的javabean
	 */
	public static <T> T getJsonDataToBean(String jsonStr, Class<T> beanclass) {
		if (jsonStr == null) {
			return null;
		}

		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			MagusTools.writeLog(e);
		}
		return getJsonObjectToBean(json, beanclass);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T getJsonObjectToBean(JSONObject obj, Class<T> beanclass) {
		HashMap<String, Method> methodNames = new HashMap<String, Method>();
		T bb = null;
		try {
			bb = beanclass.newInstance();
			Method[] methods = beanclass.getDeclaredMethods();
			for (int j = 0; j < methods.length; j++) {
				String methodname = methods[j].getName();
				if (methodname.contains("set")) {
					methodNames.put(tofirstLowerCase(methodname.substring(3, methodname.length())), methods[j]);
				}
			}

			for (Entry<String, Method> entry : methodNames.entrySet()) {
				if ((obj.has(entry.getKey()) || obj.has(tofirstUpperCase(entry.getKey())))) {
					String fildname = entry.getKey();
					Object obje = obj.opt(entry.getKey());
					if (obje == null) {
						obje = obj.opt(tofirstUpperCase(entry.getKey()));
						fildname = tofirstUpperCase(entry.getKey());
					}

					if (obje == null || obje == JSONObject.NULL) {
						continue;
					}
					if (obje instanceof JSONArray) {
						JSONArray arr = (JSONArray) obje;
						Field field = beanclass.getDeclaredField(fildname);
						ParameterizedType ptype = (ParameterizedType) field.getGenericType();
						Type[] type = ptype.getActualTypeArguments();
						Class subbeanclass = (Class) type[0];
						ArrayList sublist = new ArrayList();
						for (int i = 0; i < arr.length(); i++) {
							Object subobj = arr.get(i);
							if (subobj instanceof JSONObject) {
								Object subbean = getJsonObjectToBean((JSONObject) subobj, subbeanclass);
								sublist.add(subbean);
							} else {
								sublist.add(subobj);
							}
						}
						entry.getValue().invoke(bb, sublist);
					} else if (obje instanceof JSONObject) {
						Field field = beanclass.getDeclaredField(fildname);
						Type type = field.getGenericType();
						Class subbeanclass = (Class) type;
						Object subobj = getJsonObjectToBean((JSONObject) obje, subbeanclass);
						entry.getValue().invoke(bb, subobj);
					} else {
						try {
							entry.getValue().invoke(bb, obje.toString());
						} catch (Exception e) {
							MagusTools.writeLog(e);
						}
					}
				}
			}
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
		return bb;
	}

	/**
	 * 将字符串的首字符转化成小写
	 * 
	 * @param str
	 *            要转化的字符串
	 * @return 返回首字符变小写之后的字符串
	 */
	public static String tofirstLowerCase(String str) {
		if (str != null && str.length() > 0) {
			return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
		} else {
			return str;
		}
	}

	/**
	 * 将字符串的首字符转化成大写
	 * 
	 * @param str
	 *            要转化的字符串
	 * @return 返回首字符变大写之后的字符串
	 */
	public static String tofirstUpperCase(String str) {

		if (str != null && str.length() > 0) {
			return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
		} else if (str != null && str.length() == 0) {
			return str.toUpperCase();
		} else {
			return str;
		}

	}

	/**
	 * 替换string中的占位符
	 * 
	 * @param source
	 * @param param
	 * @return 替换之后的字符串
	 */
	public static String formatString(String source, Object... param) {
		if (source != null && param != null) {
			return String.format(source, param);
		}
		return source;
	}

	public static boolean inputStreamToOutput(InputStream is, OutputStream os) {
		try {
			if (is != null && os != null) {
				byte[] b = new byte[1024 * 3];
				int i = 0;
				while ((i = is.read(b)) != -1) {
					os.write(b, 0, i);
				}

				return true;
			}
		} catch (IOException e) {
			MagusTools.writeLog(e);
		} finally {
			try {
				os.flush();
				is.close();
				os.close();
			} catch (IOException e) {
				MagusTools.writeLog(e);
			}
		}
		return false;
	}

	/**
	 * 将输入流转化成字节数组
	 * 
	 * @param is
	 *            要转化的输入流
	 * @return 转化之后的字节数组
	 */
	public static byte[] inputStreamTobyte(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		boolean b = inputStreamToOutput(is, bos);
		return b ? bos.toByteArray() : null;
	}

	/**
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int hpx2otherpx(float pxValue) {
		return (int) ((pxValue / 1.5) * StaticFeild.density);

	}

	public static int dip2px(float dipValue) {

		return (int) (dipValue * StaticFeild.density);
	}

	public static int px2dip(float pxValue) {

		return (int) (pxValue / StaticFeild.density);
	}

	/**
	 * 得到一个popwindow
	 * 
	 * @param activity
	 *            所在activity
	 * @param layout
	 *            要生成popwindow的布局
	 * @param animid
	 *            pop的动画文件 使用系统默认的传-1
	 * @return 根据参数生成的popwindow
	 */
	public static PopupWindow getPopupWindow(Activity activity, int layout, int animid) {
		PopupWindow mPop = new PopupWindow(activity.getLayoutInflater().inflate(layout, null), LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		if (animid != -1) {
			mPop.setAnimationStyle(animid);
		}
		mPop.setBackgroundDrawable(new BitmapDrawable());
		return mPop;
	}

	/**
	 * 得到一个popwindow
	 * 
	 * @param activity
	 *            所在activity
	 * @param layout
	 *            要生成popwindow的布局
	 * @param animid
	 *            pop的动画文件 默认传-1.
	 * @param width
	 *            pop的宽度
	 * @param height
	 *            pop的高度
	 * @return 根据参数生成的popwindow
	 */
	public static PopupWindow getPopupWindow(Activity activity, int layout, int animid, int width, int height) {
		PopupWindow mPop = new PopupWindow(activity.getLayoutInflater().inflate(layout, null), width, height, true);
		if (animid != -1) {
			mPop.setAnimationStyle(animid);
		}
		mPop.setBackgroundDrawable(new BitmapDrawable());
		return mPop;
	}

	/**
	 * 
	 * @param context
	 * @param defId
	 *            ImageView要显示的默认图片，没有传0.
	 * @param url
	 * @param image
	 */
	public static void setImageView(String url, ImageView image, int defId) {
		MagusCache.setDrawable(url, image, defId, null);
	}

	public static void setImageView(String url, ImageView image, int defId, Map<String, Object> data) {
		MagusCache.setDrawable(url, image, defId, data);
	}

	/**
	 * 获取地理位置
	 * 
	 * @param context
	 * @return
	 */
	public static Location getLacation(Activity context) {
		if (location == null) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
			if (manager != null) {
				String provider = manager.getBestProvider(criteria, true);
				if (provider == null) {
					provider = LocationManager.GPS_PROVIDER;
				}
				location = manager.getLastKnownLocation(provider);
				if (location == null) {
					provider = LocationManager.NETWORK_PROVIDER;
					location = manager.getLastKnownLocation(provider);
				}
				manager.requestLocationUpdates(provider, 1000, 20, new LocationListener() {
					public void onLocationChanged(Location location) {
						MagusTools.location = location;
					}

					public void onProviderDisabled(String provider) {
					}

					public void onProviderEnabled(String provider) {
					}

					public void onStatusChanged(String provider, int status, Bundle extras) {
					}
				});
			}
		}
		return location;
	}

	// /**
	// * 需加入 android:sharedUserId="android.uid.system" 设置, 未调通,有些问题,不能用
	// *
	// */
	// public static void openGPS(Context context){
	// ContentResolver resolver = context.getContentResolver();
	// Settings.Secure.setLocationProviderEnabled(resolver,
	// LocationManager.GPS_PROVIDER,true);
	// Settings.Secure.setLocationProviderEnabled(resolver,
	// LocationManager.NETWORK_PROVIDER,true);
	// }

	/**
	 * 获取手机上的联系人 ContactBean 对象里可以通过getName()和getPhoneNo()获取联系人姓名和电话。
	 * 
	 * @param activity
	 * @return
	 */
	public static ArrayList<ContactBean> getContact(Activity activity) {
		ArrayList<ContactBean> list = new ArrayList<ContactBean>();
		try {
			// 从本机中取号
			// 得到ContentResolver对象
			ContentResolver cr = activity.getContentResolver();
			// 取得电话本中开始一项的光标
			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			while (cursor.moveToNext()) {
				// 取得联系人名字
				int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
				String contactName = cursor.getString(nameFieldColumnIndex).trim();
				if (contactName == null)
					break;
				// 取得联系人ID
				int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor phone = cr
						.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { Integer.toString(id) }, null);// 再类ContactsContract.CommonDataKinds.Phone中根据查询相应id联系人的所有电话；

				// 取得电话号码(可能存在多个号码)
				while (phone.moveToNext()) {
					String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

					ContactBean bean = new ContactBean();
					bean.setName(contactName);
					bean.setPhoneNo(getNumber(strPhoneNumber.trim()));
					if (!list.contains(bean)) {
						list.add(bean);
					}
				}
				phone.close();
			}
			cursor.close();

			// 读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
			// String [] simurls = new
			// String[]{"content://icc/adn","content://sim/adn"};
			// for(int j=0;j<simurls.length;j++){
			// Intent intent = new Intent();
			// intent.setData(Uri.parse(simurls[j]));
			// Uri uri = intent.getData();
			// Cursor mCursor = getContentResolver().query(uri, null, null,
			// null, null);
			// if (mCursor != null) {
			// while (mCursor.moveToNext()) {
			// // 取得联系人名字
			// int nameFieldColumnIndex = mCursor.getColumnIndex("name");
			// String contactName = mCursor.getString(nameFieldColumnIndex);
			// if(contactName == null) break;
			// // 取得电话号码
			// int numberFieldColumnIndex = mCursor
			// .getColumnIndex("number");
			// String userNumber = mCursor.getString(numberFieldColumnIndex);
			// String pinyinf = PinyinUtil.toPinyin(this,
			// contactName).substring(0, 1).toUpperCase();
			// String allPinyin ="";
			// for(int i=0;i<contactName.length();i++){
			// String s = PinyinUtil.toPinyin(this, contactName.charAt(i));
			// if(s != null && s.length()>0){
			// allPinyin = allPinyin + s.substring(0, 1).toLowerCase();
			// }
			// }
			// ContactBean bean = new ContactBean();
			// bean.setName(contactName);
			// bean.setPhoneNo(getNumber(userNumber.trim()));
			// bean.setPinyin(allPinyin);
			//
			// if(!allContact.contains(bean)){
			// allContact.add(bean);
			// }
			// if(contactData.get(pinyinf) != null){
			// if(!contactData.get(pinyinf).contains(bean)){
			// contactData.get(pinyinf).add(bean);
			// Collections.sort(contactData.get(pinyinf));
			// }
			// }else{
			// ArrayList<ContactBean> list = new ArrayList<ContactBean>();
			// list.add(bean);
			// contactData.put(pinyinf, list);
			// }
			// }
			// mCursor.close();
			// }
			// }
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
		return list;
	}

	// 还原11位手机号 包括去除“-”
	public static String getNumber(String num2) {
		String num;
		if (num2 != null) {
			num = num2.replaceAll("-", "");
			if (num.startsWith("+86")) {
				num = num.substring(3);
			} else if (num.startsWith("86")) {
				num = num.substring(2);
			} else if (num.startsWith("17951")) {
				num = num.substring(5);
			}
		} else {
			num = "";
		}
		return num;
	}

	// public static String getExceptionString(Throwable e) {
	// String s = "";
	// if (e != null) {
	// if (StaticFild.haslog) {
	// s = s + e.getLocalizedMessage() + "\n";
	// }
	// StackTraceElement[] steArr = e.getStackTrace();
	// if (steArr != null) {
	// for (int i = 0; i < steArr.length; i++) {
	// s = s + steArr[i].toString() + "\n";
	// }
	// } else {
	// s = e.getMessage();
	// }
	// }
	// return s;
	// }

	/**
	 * 已过时，改用MagusDialog类
	 * 显示提示框，只有标题和一个确定按钮，并且点击确定后关闭当前Activity，默认title为“提示”，按钮文字为“确定”
	 * 
	 * @param text
	 *            提示消息
	 */
	@Deprecated
	public static AlertDialog showCloseAlert(final Context ctx, String text) {
		return new AlertDialog.Builder(ctx).setTitle("提示").setMessage(text).setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ctx instanceof Activity) {
					dialog.dismiss();
					((Activity) ctx).finish();
				}
			}
		}).setCancelable(false).show();
	}

	/**
	 * 已过时，改用MagusDialog类 显示提示框，可设置标题和文字，点确定后关闭当前Activity
	 */
	@Deprecated
	public static AlertDialog showCloseAlert(final Context ctx, String title, String text) {
		return new AlertDialog.Builder(ctx).setTitle(title).setMessage(text).setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ctx instanceof Activity) {
					dialog.dismiss();
					((Activity) ctx).finish();
				}
			}
		}).setCancelable(false).show();
	}

	/**
	 * 已过时，改用MagusDialog类 显示提示框,点确定不做任何操作，只是单纯的消息提醒。
	 */
	@Deprecated
	public static AlertDialog showAlert(Context ctx, String text) {
		return new AlertDialog.Builder(ctx).setTitle("提示").setMessage(text).setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setCancelable(false).show();
	}

	/**
	 * 已过时，改用MagusDialog类
	 * 
	 * @param ctx
	 * @param title
	 * @param text
	 * @return
	 */
	@Deprecated
	public static AlertDialog showAlert(Context ctx, String title, String text) {
		return new AlertDialog.Builder(ctx).setTitle(title).setMessage(text).setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setCancelable(false).show();
	}

	/**
	 * 已过时，改用MagusDialog类
	 * 
	 * @param ctx
	 * @param title
	 * @param text
	 * @param yes
	 * @param no
	 * @return
	 */
	@Deprecated
	public static AlertDialog showAlert(Context ctx, String title, String text, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no) {
		AlertDialog dialog = new AlertDialog.Builder(ctx).setTitle(title).setMessage(text).setPositiveButton("确认", yes).setNegativeButton("取消", no).setCancelable(false).show();
		return dialog;
	}

	/**
	 * 将异常信息写入sd卡根目录下的 ”产品名“+”_log.txt"中
	 * 主要用于测试，产品正式上线应该把StaticFild.haslog设为false。
	 */
	public static void writeLog(final Throwable e) {
		try {
			Log.w(StaticFeild.TAG, e);
			if (haslog() && hasSDcard()) {
				MagusCache.pool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							File file = new File(StaticFeild.logFile);
							if (!file.exists() && !file.getParentFile().exists()) {
								file.getParentFile().mkdirs();
							}
							FileOutputStream fos = new FileOutputStream(file, true);
							PrintStream ps = new PrintStream(fos);
							e.printStackTrace(ps);
							fos.close();
							ps.close();
						} catch (Exception e1) {
							Log.w(StaticFeild.TAG, e1);
						}
					}
				});
			}
		} catch (Exception e1) {
			Log.w(StaticFeild.TAG, e1);
		}
	}

	/**
	 * 将请求连接或一些信息写入sd卡根目录下的 ”产品名“+”_log.txt"中
	 * 主要用于测试，产品正式上线应该把StaticFild.haslog设为false。
	 */
	public static void writeLog(final String str) {
		if (haslog()) {
			Log.e(StaticFeild.TAG, str);
		}
		if (haslog() && hasSDcard()) {
			MagusCache.pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						File file = new File(StaticFeild.logFile);
						if (!file.exists() && !file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						FileWriter fw = new FileWriter(file, true);
						SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
						fw.write((format.format(new Date(System.currentTimeMillis())) + "\n" + str + "\n"));
						fw.flush();
						fw.close();
					} catch (Exception e1) {
						Log.w(StaticFeild.TAG, e1);
					}
				}
			});
		}
	}

	/**
	 * 往文件里写内容,方法完成后不会关闭输入流，应在调用方法后自行处理。
	 * 
	 * @param is
	 *            要写的内容
	 * @param fileName
	 *            要写入的文件（全路径+文件名）
	 * @return 是否写入成功
	 */
	public static boolean writeFile(String fileName, InputStream is) {
		FileOutputStream fos = null;
		try {
			File saveFile = new File(fileName);
			if (!saveFile.exists() && saveFile.getParentFile() != null && !saveFile.getParentFile().exists()) {
				saveFile.getParentFile().mkdirs();
			}
			saveFile = new File(fileName);
			fos = new FileOutputStream(saveFile);
			return inputStreamToOutput(is, fos);
		} catch (Exception e1) {
			MagusTools.writeLog(e1);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {

			}
		}
		return false;

	}

	/**
	 * 往SD卡里写内容。
	 * 
	 * @param bytes
	 *            要写的内容
	 * @param fileName
	 *            要写入的文件（全路径+文件名）
	 * @return 是否写入成功
	 */
	public static boolean writeFile(String fileName, byte[] bytes) {
		FileOutputStream fos = null;
		try {
			File saveFile = new File(fileName);
			if (!saveFile.exists() && saveFile.getParentFile() != null && !saveFile.getParentFile().exists()) {
				saveFile.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(saveFile);
			fos.write(bytes);
			if (fos != null) {
				fos.flush();
			}
			return true;
		} catch (Exception e1) {
			writeLog(e1);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {

			}
		}
		return false;
	}

	/**
	 * 从给定的文件名读取文件，
	 * 
	 * @param fileName
	 *            要读取的文件名路径： 全路径 + 文件名
	 * @return 给定文件的输入流。 如果sd卡状态不正确或文件不存在，则返回null；
	 */
	public static FileInputStream readFile(String fileName) {
		if (fileName == null) {
			return null;
		}
		FileInputStream fis = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return null;
			}
			fis = new FileInputStream(file);
			return fis;
		} catch (Exception e1) {
			writeLog(e1);
		}
		return null;
	}

	/**
	 * 从给定的文件名读取文件，
	 * 
	 * @param fileName
	 *            要读取的文件名路径： 全路径 + 文件名
	 * @return 给定文件的输入流。 如果sd卡状态不正确或文件不存在，则返回null；
	 */
	public static byte[] readFileToByte(String fileName) {
		FileInputStream fis = readFile(fileName);
		return inputStreamTobyte(fis);
	}

	/**
	 * 根据所给的key获取 magus.properties里的配置信息
	 * 
	 * @param key
	 *            配置文件里对应的key
	 * @return 对应的值，如果没有对应的key返回""
	 */
	public static String getProperty(String key) {
		try {
			if (ppt == null) {
				ppt = new Properties();
				// File file = new
				// File("file:///android_asset/magus.properties");
				ppt.load(MagusTools.class.getResourceAsStream("/res/raw/properties"));
			}
			return ppt.getProperty(key);
		} catch (IOException e) {
			MagusTools.writeLog("从magus.properties配置文件中读取" + key + "属性失败,请检查该文件或属性是否已配置。");
			MagusTools.writeLog(e);
		}
		return "";
	}

	private static void loadProperty() {
		try {
			ppt = new Properties();
			ppt.load(MagusTools.class.getResourceAsStream("/res/raw/properties"));
		} catch (Exception e) {
			MagusTools.writeLog("raw目录下没有property文件");
		}
	}

	private static void setProperty(HashMap<String, String> propertyMap) {
		if (propertyMap == null || propertyMap.size() == 0) {
			return;
		}
		if (ppt == null) {
			ppt = new Properties();
		}
		ppt.putAll(propertyMap);
	}

	public static void setProperty(String key, String value) {
		if (key == null) {
			return;
		}
		if (ppt == null) {
			ppt = new Properties();
		}
		ppt.put(key, value);
	}

	/**
	 * 根据所给的key先从项目默认的SharedPreferences文件里获取所给key的配置信息，
	 * 如果SharedPreferences不存在该项配置的值，则再从magus.properties里获取配置信息
	 * 
	 * @param key
	 *            配置文件里对应的key
	 * @context 上下文对象
	 * @return 对应的值，如果没有对应的key返回""
	 */
	public static String getProperty(String key, Context context) {
		try {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String value = sp.getString(key, null);
			if (value != null) {
				return value;
			}
			return getProperty(key);
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
		return "";
	}

	/**
	 * 判断是现网还是qa环境
	 * 
	 * @return
	 */
	public static boolean isQA() {
		String qa = getProperty("qa");
		boolean b = false;
		try {
			b = Boolean.parseBoolean(qa);
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
		return b;
	}

	/**
	 * 判断是否要写调试及异常日志
	 * 
	 * @return
	 */
	public static boolean haslog() {
		String log = getProperty("log");
		boolean b = false;
		try {
			b = Boolean.parseBoolean(log);
		} catch (Exception e) {
			MagusTools.writeLog("从magus.properties配置文件中读取log属性失败,请检查该属性的配置。");
			MagusTools.writeLog(e);
		}
		return b;
	}

	/**
	 * 判断是否支持google地图包
	 * 
	 * @return
	 */
	public static boolean hasGoogleMap() {
		try {
			Class.forName("com.google.android.maps.MapActivity");
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 清除文件或缓存
	 * 
	 * @param dir
	 *            要清除的目录或文件
	 * @return 清除的文件个数
	 */
	public static int clearFile(File file) {
		int deletedFiles = 0;
		try {
			if (file == null || !file.exists()) {
				return deletedFiles;
			}

			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearFile(child);
					}

					if (child.delete()) {
						deletedFiles++;
					}
				}
			} else {
				if (file.delete()) {
					deletedFiles++;
				}
			}
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
		return deletedFiles;
	}

	/**
	 * 清除文件或缓存
	 * 
	 * @param path
	 *            要清除的目录或文件的路径
	 * @return 清除的文件个数
	 */
	public static int clearFile(String path) {
		int deletedFiles = 0;
		if (path == null) {
			return deletedFiles;
		}
		File file = new File(path);
		if (!file.exists()) {
			return deletedFiles;
		}
		deletedFiles = clearFile(file);
		return deletedFiles;
	}

	/**
	 * 初始化一些数据，在程序启动时必须调用
	 * 
	 * @param propertyMap
	 *            配置文件里的配置信息，当程序不使用默认的raw/property文件配置，而是使用项目代码设置时传此参数。
	 */
	public static void initData(Context context, HashMap<String, String> propertyMap) {
		try {
			if (propertyMap != null && propertyMap.size() > 0) {
				setProperty(propertyMap);
			} else {
				loadProperty();
			}

			StaticFeild.packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(StaticFeild.packageName, 0);
			StaticFeild.ver = info.versionName;

			initUsefulDeviceInfo(context);

			// createTestFloatView(context);

			MagusCache.pool.execute(new Runnable() {
				@Override
				public void run() {
					removeExpiredCache();
					if (haslog() && hasSDcard()) {
						File file = new File(StaticFeild.logFile);
						clearFile(file);
					}
				}
			});
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
	}

	// private static void createTestFloatView(Context context) {
	// Toast toast = Toast.makeText(context, "测试数据", Toast.LENGTH_LONG);
	// // toast.setGravity(Gravity.LEFT, 0, 0);
	// toast.show();
	//
	// }

	// public class WindowManageDemoActivity extends Activity {
	//
	// private WindowManager mWindowManager;
	// private WindowManager.LayoutParams param;
	// private FloatView mLayout;
	// /** Called when the activity is first created. */
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.main);
	//
	// showView();
	// }
	//
	// @Override
	// public void onDestroy(){
	// super.onDestroy();
	// //在程序退出(Activity销毁）时销毁悬浮窗口
	// mWindowManager.removeView(mLayout);
	// }
	// }

	private static void createTestFloatView(Context context) {
		// Toast toast = Toast.makeText(context, "测试数据", Toast.LENGTH_LONG);
		// // toast.setGravity(Gravity.LEFT, 0, 0);
		// toast.show();

		showView(context);
	}

	public static void showView(final Context context) {
		// final FloatView mLayout = new
		// FloatView(context.getApplicationContext());
		final ImageView mLayout = new ImageView(context);
//		mLayout.setBackgroundResource(R.drawable.com_xiaofeizhe_06);
		// 获取WindowManager
		final WindowManager mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		final WindowManager.LayoutParams param = new WindowManager.LayoutParams();
		// param = ((MyApplication) getApplication()).getMywmParams();

		param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 系统提示类型,重要
		param.format = 1;
		param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
		param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

		// param.alpha = 1.0f;

		param.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		param.x = 200;
		param.y = 200;

		// 设置悬浮窗口长宽数据
		param.width = WindowManager.LayoutParams.WRAP_CONTENT;
		param.height = WindowManager.LayoutParams.WRAP_CONTENT;

		// 显示myFloatView图像
		mWindowManager.addView(mLayout, param);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Activity activity = (Activity) context;
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mWindowManager.addView(mLayout, param);
					}
				});
			}
		}, new Date(System.currentTimeMillis() + 3000), 2000);

	}

	// /**
	// * 创建测试模式提示悬浮窗
	// */
	// private static void createTestFloatView(Context context) {
	// final Button btn_floatView = new Button(context);
	// btn_floatView.setText("悬浮窗");
	//
	// final WindowManager wm = (WindowManager)
	// context.getSystemService(Context.WINDOW_SERVICE);
	// final WindowManager.LayoutParams params = new
	// WindowManager.LayoutParams();
	//
	// // 设置window type
	// params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	// /*
	// * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
	// * 即拉下通知栏不可见
	// */
	//
	// params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
	//
	// // 设置Window flag
	// params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
	// WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	// /*
	// * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
	// * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
	// * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
	// */
	//
	// // 设置悬浮窗的长得宽
	// params.width = 100;
	// params.height = 100;
	//
	// // 设置悬浮窗的Touch监听
	// btn_floatView.setOnTouchListener(new OnTouchListener() {
	// int lastX, lastY;
	// int paramX, paramY;
	//
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// lastX = (int) event.getRawX();
	// lastY = (int) event.getRawY();
	// paramX = params.x;
	// paramY = params.y;
	// break;
	// case MotionEvent.ACTION_MOVE:
	// int dx = (int) event.getRawX() - lastX;
	// int dy = (int) event.getRawY() - lastY;
	// params.x = paramX + dx;
	// params.y = paramY + dy;
	// // 更新悬浮窗位置
	// wm.updateViewLayout(btn_floatView, params);
	// break;
	// }
	// return true;
	// }
	// });
	//
	// wm.addView(btn_floatView, params);
	// // isAdded = true;
	// }

	/**
	 * 初始化常用的手机设备信息
	 * 
	 * @param context
	 * @param info
	 */
	private static void initUsefulDeviceInfo(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		StaticFeild.width = dm.widthPixels;
		StaticFeild.height = dm.heightPixels;
		StaticFeild.density = dm.density;
		StaticFeild.densityDip = dm.densityDpi;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		StaticFeild.deviceId = tm.getDeviceId();
	}

	/**
	 * 初始化一些数据，在程序启动时调用
	 */
	public static void initData(Context context) {
		initData(context, null);
	}

	public static boolean hasSDcard() {
		boolean b = false;
		try {
			b = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			MagusTools.writeLog(e);
		}
		return b;
	}

	/**
	 * 根据给定的类型名和字段名，返回R文件中的字段的值
	 * 
	 * @param typeName
	 *            属于哪个类别的属性 （id,layout,drawable,string,color,attr......）
	 * @param fieldName
	 *            字段名
	 * @return 字段的值
	 * @throws Exception
	 */
	public static int getFieldValue(String typeName, String fieldName) {
		int i = -1;
		try {
			Class<?> clazz = Class.forName(StaticFeild.packageName + ".R$" + typeName);
			i = clazz.getField(fieldName).getInt(null);
		} catch (Exception e) {
			MagusTools.writeLog(e);
			MagusTools.writeLog("没有找到" + StaticFeild.packageName + ".R$" + typeName + "类型资源 " + fieldName + "请copy相应文件到对应的目录.");
		}
		return i;
	}

	public static int getId(String fieldName) {
		return getFieldValue("id", fieldName);
	}

	public static int getLayout(String fieldName) {
		return getFieldValue("layout", fieldName);
	}

	public static int getDrawable(String fieldName) {
		return getFieldValue("drawable", fieldName);
	}

	public static int[] getStyleable(String fieldName) throws ClassNotFoundException {
		try {
			Class<?> clazz = Class.forName(StaticFeild.packageName + ".R$styleable");
			return (int[]) clazz.getField(fieldName).get(null);
		} catch (Exception e) {
			throw new ClassNotFoundException("没有找到自定义属性 " + fieldName + "请copy相应文件到对应的目录.");
		}
	}

	public static StateListDrawable getStateDrawable(Drawable defDrawable, Drawable pressedDrawable) {
		StateListDrawable bg = null;
		if (pressedDrawable != null && defDrawable != null) {
			bg = new StateListDrawable();
			bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_pressed }, pressedDrawable);
			bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, pressedDrawable);
			bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_selected }, pressedDrawable);
			bg.addState(new int[] { android.R.attr.state_enabled }, defDrawable);
		}
		return bg;
	}

	// public static void setOnclickListener(View.OnClickListener listener,
	// View...views ){
	// if(listener!=null){
	// int len = views.length;
	// for(int i=0;i<len;i++){
	// View view = views[i];
	// if(view != null){
	// view.setOnClickListener(listener);
	// }
	// }
	// }
	// }

	/**
	 * 将drawable转化成Bitmap
	 * 
	 * @param drawable
	 *            要转化的drawable
	 * @return 转化后的bitmap。
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	public static String getDataPath() {
		String path = null;
		File file = Environment.getExternalStorageDirectory();
		if (file != null && file.exists()) {
			path = file.getPath();
		} else {
			file = Environment.getDataDirectory();
			if (file != null && file.exists()) {
				path = file.getPath();
			}
		}

		if (path != null) {
			path = path + "/" + getProperty("product");
		}

		return path;
	}

	public static boolean removeExpiredCache() {
		try {
			File file = new File(getDataPath(), StaticFeild.CACHEDIR);
			if (file.isDirectory()) {
				SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date(System.currentTimeMillis());
				File subFile[] = file.listFiles();
				int currentTime = Integer.valueOf(dataFormat.format(date));
				int expiredDay = 15;
				try {
					expiredDay = Integer.parseInt(MagusTools.getProperty("expired"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < subFile.length; i++) {
					int subFileLastModified = Integer.valueOf(dataFormat.format(new Date(subFile[i].lastModified())).toString());
					int day = currentTime - subFileLastModified;
					if (day > expiredDay) { // 如果大于设置日期或者负值
						subFile[i].delete();
					}
				}
			}
			return true;
		} catch (Exception e) {
			MagusTools.writeLog(e);
			return false;
		}
	}

	/**
	 * 根据Activity类名获得匹配的layout资源文件名称
	 * note：Activity的命名必须符合类的命名规范，即每个单词首字母大写。Activity可以以
	 * “Activity”结尾。资源文件每个单词小写，单词之间用下划线隔开。 example：
	 * Activity的命名为TestUseActivity.class或TestUse.class 资源文件必须命名为test_use.xml
	 * 
	 * @return 对应的资源文件的名称
	 */
	public static String getMatcherResourceName(Activity activity) {
		String resourceName = null;
		String activityName = activity.getClass().getSimpleName();
		if (activityName.contains("Activity")) {
			resourceName = activityName.substring(0, activityName.indexOf("Activity"));
		} else {
			resourceName = activityName;
		}
		resourceName = tofirstLowerCase(resourceName);
		Pattern p = Pattern.compile("\\p{Upper}");
		Matcher m = p.matcher(resourceName);
		while (m.find()) {
			String s = m.group();
			resourceName = resourceName.replace(s, "_" + s.toLowerCase());
		}

		return resourceName;
	}

	/**
	 * 根据Activity类名获得匹配的layout资源文件名称
	 * note：Activity的命名必须符合类的命名规范，即每个单词首字母大写。Activity可以以
	 * “Activity”结尾。资源文件每个单词小写，单词之间用下划线隔开。 example：
	 * Activity的命名为TestUseActivity.class或TestUse.class 资源文件必须命名为test_use.xml
	 * 
	 * @return 对应的资源文件的名称
	 */
	public static String getMatcherResourceName(Fragment fragment) {
		String resourceName = null;
		String fragmentName = fragment.getClass().getSimpleName();
		// if (fragmentName.contains("Activity")) {
		// resourceName = fragmentName.substring(0,
		// fragmentName.indexOf("Activity"));
		// } else {
		// resourceName = fragmentName;
		// }
		resourceName = fragmentName;
		resourceName = tofirstLowerCase(resourceName);
		Pattern p = Pattern.compile("\\p{Upper}");
		Matcher m = p.matcher(resourceName);
		while (m.find()) {
			String s = m.group();
			resourceName = resourceName.replace(s, "_" + s.toLowerCase());
		}

		return resourceName;
	}
}
