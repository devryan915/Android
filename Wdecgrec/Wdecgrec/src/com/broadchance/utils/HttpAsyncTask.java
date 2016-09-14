package com.broadchance.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.broadchance.entity.serverentity.BaseResponse;
import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;

/**
 * @ClassName: HttpAsyncTask
 * @Description: 异步请求Http,参数为可变长参数，预定义，第一个参数为请求方式，第二个为请求url，第三个为请求参数，第四个为回调函数
 * @author: ryan.wang
 * @date: 2014年7月10日 下午3:57:44
 * 
 */

public class HttpAsyncTask<T extends BaseResponse<?>> extends
		AsyncTask<Object, Integer, StringResponse> {

	private static final String TAG = HttpAsyncTask.class.getSimpleName();
	Handler mHandler;
	// 是否加载完毕
	boolean mIsLoaded = false;
	// 是否超时
	boolean mIsTimeOut = false;
	// 请求是否报错
	boolean mIsError = false;
	Context context;
	String mErrorMsg = "";
	// 请求地址
	String mUrl;
	Map<String, Object> mReqParams;
	HttpReqCallBack<T> mCallBack;
	Dialog progressDialog = null;
	private int loadDataTime = 0;

	/**
	 * @param <T>
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
	public static <T extends BaseResponse<?>> void fetchData(String url,
			Map<String, Object> reqParams, HttpReqCallBack<T> backCall) {
		if (url == null || reqParams == null)
			return;
		HttpAsyncTask<T> task = new HttpAsyncTask<T>(url, reqParams, backCall);
		task.execute();
	}

	public HttpAsyncTask(String url, Map<String, Object> reqParams,
			HttpReqCallBack<T> callBack) {
		this.mUrl = url;
		this.mReqParams = reqParams;
		this.mCallBack = callBack;
		if (this.mCallBack != null) {
			this.context = this.mCallBack.getReqActivity();
		}
		mIsLoaded = false;
		mIsError = false;
		mIsTimeOut = false;
		mHandler = new Handler();
		loadDataTime = 0;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadDataTime++;
				if (loadDataTime <= ConstantConfig.TIME_DELAY) {
					// 超过2s显示loading
					if (loadDataTime == 1 && context != null
							&& progressDialog == null && !mIsLoaded) {
						if ((mCallBack != null && mCallBack.isShowLoading())
								|| mCallBack == null) {
							try {
								progressDialog = UIUtil.showLoadingDialog(
										context, "");
								TextView dialogContent = ((TextView) progressDialog
										.findViewById(R.id.content));
								dialogContent.setText("数据加载中...");
								progressDialog.show();
							} catch (Exception exp) {
								LogUtil.e(TAG, exp);
							}
						}
					}
					mHandler.postDelayed(this, 1000);
				} else {
					closeLoading();
					if (!mIsLoaded) {
						mIsTimeOut = true;
						if (ConstantConfig.Debug) {
							LogUtil.d(TAG, "请求超时: Url:" + mUrl);
						}
						if (mCallBack != null) {
							// 请求超时
							mCallBack.doError("请求超时: Url:" + mUrl);
						}
					}
				}

			}
		}, 1000);
	}

	private void closeLoading() {
		if (progressDialog != null) {
			progressDialog.cancel();
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/*
	 * (non-Javadoc) 此方法在非UI线程中处理
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected StringResponse doInBackground(Object... params) {

		if (this.mReqParams != null && this.mReqParams.size() > 0) {
			try {
				return HttpUtil.postData(mUrl, mReqParams);
				// if (response.isOk()) {
				// return response.getData();
				// } else {
				// mIsError = true;
				// mErrorMsg = response.getData();
				// }
			} catch (Exception e) {
				mIsError = true;
				mErrorMsg = e.toString();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(StringResponse response) {
		closeLoading();
		mIsLoaded = true;
		// 访问未超时，且数据不为空认为数据请求成功
		String result = response.isOk() ? response.getData() : "";
		boolean hasData = result != null && !result.isEmpty();
		if (!mIsError && !mIsTimeOut) {
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, result);
			}
			T entityData = null;
			try {
				if (mCallBack != null) {
					// 此处通过CallBack传入的泛型类型
					Class cz = mCallBack.getClass();
					// mCallBack的父类是接口
					// Type type = ((ParameterizedType)
					// cz.getGenericInterfaces()[0])
					// .getActualTypeArguments()[0];
					// mCallBack的父类是非接口类
					// TypeReference传入的T必须继承带泛型的非抽象类或接口
					// entityData = JSON.parseObject(result,
					// new TypeReference<T>() {
					// });
					Type type = ((ParameterizedType) cz.getGenericSuperclass())
							.getActualTypeArguments()[0];
					if (hasData || response.getCode().equals("400")
							|| response.getCode().equals("401")) {
						if (hasData) {
							entityData = JSON.parseObject(result, type);
							if (entityData.getCode() == null) {
								entityData.setCode(entityData.OK);
							}
						} else {
							hasData = true;
							entityData = (T) ((Class) type).newInstance();
							entityData.setCode(entityData.FAILED);
							entityData.setMessage(response.getData());
						}
						mCallBack.doSuccess(entityData);
					} else {
						mIsError = true;
						mErrorMsg = response.getData();
						LogUtil.e(TAG, mErrorMsg);
					}
				}
			} catch (Exception e) {
				mIsError = true;
				mErrorMsg = "反序列化失败：\r\n" + e.toString();
				LogUtil.e(TAG, e);
			}
		}
		// 请求失败
		if (mIsError) {
			if (mCallBack != null) {
				mCallBack.doError(mErrorMsg);
			}
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, mErrorMsg);
			}
		} else if (!hasData && !mIsTimeOut) {// 请求无数据返回
			mErrorMsg = "no data";
			if (mCallBack != null) {
				mCallBack.doError(mErrorMsg);
			}
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, mErrorMsg);
			}
		}
		super.onPostExecute(response);
	}
}
