/**
 * @Title: HttpAsyncTask.java
 * @Package: com.kc.ihaigo.ui.selfwidget
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月10日 下午3:57:44

 * @version V1.0

 */

package com.kc.ihaigo.util;

import java.util.Map;

import android.app.Dialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @ClassName: HttpAsyncTask
 * @Description: 异步请求Http,参数为可变长参数，预定义，第一个参数为请求方式，第二个为请求url，第三个为请求参数，第四个为回调函数
 * @author: ryan.wang
 * @date: 2014年7月10日 下午3:57:44
 * 
 */

public class HttpAsyncTask extends AsyncTask<Object, Integer, String> {
	public interface HttpReqCallBack {
		void deal(String result);
		// void doSuccess(String result);
		// void doError(String result);
	}
	public static final int POST = 0;
	public static final int GET = 1;
	public static final int DELETE = 2;
	public static final int UPLOAD_IMAGE = 3;
	public static final int DOWNLOAD = 4;
	private HttpReqCallBack callBack;
	private static final String TAG = "HttpAsyncTask";
	private String showContent = "";
	private static Dialog progressDialog;
	private static TextView dialogContent;

	/**
	 * 
	 * @Title: fetchData
	 * @user: ryan.wang
	 * @Description: 异步获取http数据方法
	 * 
	 * @param type
	 *            HttpAsyncTask.POST HttpAsyncTask.GET HttpAsyncTask.DELETE
	 * @param url
	 *            请求url
	 * @param reqParams
	 *            如果是post方法需要将参数传递过来
	 * @param callBack
	 *            回调方法用来处理返回结果
	 * @param objects
	 *            数组参数，预留参数非必须参数。
	 * 
	 *            数组 第一个参数为整型定义数据缓存时间，如没有则使用系统默认缓存时间 数组;
	 *            第二参数定义为阻塞窗口需要显示的提示性信息resId,如果使用此参数则认为需要使用阻塞;
	 * @throws
	 */
	public static void fetchData(int type, String url,
			Map<String, Object> reqParams, HttpReqCallBack callBack,
			Object... objects) {
		if (Constants.Debug) {
			Log.d(TAG, "正在异步请求数据:" + url);
		}
		new HttpAsyncTask().execute(type, url, reqParams, callBack, objects);
		try {
			if (objects != null && objects.length == 2) {
				int resId = (Integer) objects[1];
				if (IHaiGoMainActivity.main != null) {
					if (progressDialog == null) {
						progressDialog = DialogUtil.showLoadingDialog(
								IHaiGoMainActivity.main, "");
						dialogContent = ((TextView) progressDialog
								.findViewById(R.id.content));
					}
					dialogContent.setText(resId);
					progressDialog.show();
				}
			}
		} catch (Exception e) {

		}
	}
	@Override
	protected String doInBackground(Object... params) {
		if (params != null && params.length > 0) {
			int reqtype = (Integer) params[0];
			switch (reqtype) {
				case GET :
					return getHttpData(params);
				case POST :
					return getHttpData(params);
				case DELETE :
					return getHttpData(params);
				case UPLOAD_IMAGE :
					return getHttpData(params);
			}

		}
		return null;
	}
	/**
	 * 
	 * @Title: getHttpData
	 * @user: ryan.wang
	 * @Description: Http(get post delete,upload)请求返回数据
	 * 
	 * @param params
	 * @return String
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private String getHttpData(Object... params) {
		// 必要四个参数，如果不符则是非法调用
		if (params.length < 4)
			return null;
		String result = "";
		int reqtype = (Integer) params[0];
		String url = (String) params[1];
		Map<String, Object> reparams = (Map<String, Object>) params[2];
		callBack = (HttpReqCallBack) params[3];
		// 只有设置有效缓存时间才取缓存中的数据并缓存数据，默认不缓存数据
		if (params.length > 4 && params[4] != null
				&& ((Object[]) params[4]).length > 0
				&& Integer.parseInt(((Object[]) params[4])[0] + "") > 0) {
			// 默认先取缓存
			ACache cache = ACache.get(IHaiGoMainActivity.main);
			result = cache.getAsString(url);
			if (!TextUtils.isEmpty(result)) {
				if (Constants.Debug) {
					Log.d(TAG, "已从缓存中获取数据");
				}
				return result;
			}
		}
		if (Constants.Debug) {
			Log.d(TAG, "正在请求服务端数据：" + url);
		}
		switch (reqtype) {
			case GET :
				result = HttpUtil.getData(url);
				if (Constants.Debug) {
					Log.d(TAG, "已通过get方法获取到数据：" + result);
				}
				break;
			case POST :
				result = HttpUtil.postData(url, reparams);
				if (Constants.Debug) {
					Log.d(TAG, "已通过post方法获取到数据：" + result);
				}
				break;
			case DELETE :
				result = HttpUtil.deleteData(url);
				if (Constants.Debug) {
					Log.d(TAG, "已通过delete方法获取到数据：" + result);
				}
			case UPLOAD_IMAGE :
				result = HttpUtil.uploadImage(url, reparams);
				if (Constants.Debug) {
					Log.d(TAG, "已通过UPLOAD_IMAGE方法上传数据并返回结果：" + result);
				}
				break;
			default :
				break;
		}

		// 只有缓存有效才进行数据缓存
		if (params.length > 4 && params[4] != null
				&& ((Object[]) params[4]).length > 0
				&& Integer.parseInt(((Object[]) params[4])[0] + "") > 0) {
			// 如果有指定有效时间则使用传入的缓存有效时间
			Object[] objects = (Object[]) params[4];
			if (objects.length > 0) {
				ACache cache = ACache.get(IHaiGoMainActivity.main);
				cache.put(url, result, Integer.parseInt(objects[0] + ""));
				if (Constants.Debug) {
					Log.d(TAG, "设置数据缓存时间：" + objects[0]);
				}
			}
		}
		return result;
	}
	@Override
	protected void onPostExecute(String result) {
		callBack.deal(result);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		super.onPostExecute(result);
	}

}
