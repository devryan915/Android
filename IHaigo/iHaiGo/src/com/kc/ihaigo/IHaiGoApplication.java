/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.kc.ihaigo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kc.ihaigo.ui.personal.bean.Users;
import com.kc.ihaigo.util.ACache;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.login.QQLogin;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * 
 * @ClassName: IHaiGoApplication
 * @Description: 应用程序，初始化全局事件
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:45:41
 * 
 */
public class IHaiGoApplication extends Application {
	private final String TAG = "IHaiGoApplication";
	private Users users;

	@Override
	public void onCreate() {
		super.onCreate();
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		initImageLoader(getApplicationContext());
		// 初始化接口数据配置参数
		initDataConfig();
		// 清理缓存
		// Utils.clearCache(getApplicationContext());
		// 注册qq
		QQLogin.registerQQ(this);
		Intent service = new Intent(Constants.MESSAGE_ACTION);
		startService(service);
	}

	public void initDataConfig() {
		String url = Constants.REC_CONFIG + "config";
		// String url = "http://192.168.1.4:8080/app/config";
		if (Constants.Debug) {
			Log.d(TAG, "正在请求配置信息");
		}
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						try {
							// 从缓存读取接口配置信息
							final DataConfig dConfig = new DataConfig(
									getApplicationContext());
							if (TextUtils.isEmpty(result)) {
								if (dConfig.getAdsUpdateTime() == 0)
									Log.e(TAG, "无有效数据配置参数");
								return;
							}
							// 从服务端获取接口配置信息
							JSONObject confJsonObject = new JSONObject(result);
							final long adslut = confJsonObject.getLong("ads");
							final long currencylut = confJsonObject
									.getLong("currency");
							final long sourcelut = confJsonObject
									.getLong("source");
							final long categorylut = confJsonObject
									.getLong("category");
							final long levellut = confJsonObject
									.getLong("level");
							final long lcompanylut = confJsonObject
									.getLong("lcompany");
							final long tcompanylut = confJsonObject
									.getLong("tcompany");
							if (Constants.Debug) {
								Log.d(TAG, "已获取配置信息：" + "广告更新时间：" + adslut
										+ ";货币更新时间：" + currencylut + ";电商更新时间："
										+ sourcelut + ";目录更新时间：" + categorylut
										+ ";等级更新时间：" + levellut + ";物流公司更新时间："
										+ lcompanylut + "转运公司更新时间"
										+ tcompanylut);
							}
							// 检查广告页面是否需要更新
							if (adslut > dConfig.getAdsUpdateTime()) {
								String url = Constants.REC_CONFIG + "ads";
								if (Constants.Debug) {
									Log.d(TAG, "正在请求广告信息:" + url);
								}
								HttpAsyncTask.fetchData(HttpAsyncTask.GET, url,
										null, new HttpReqCallBack() {
											@Override
											public void deal(String result) {
												if (Constants.Debug) {
													Log.d(TAG, "已获取广告信息："
															+ result);
												}
												if (!TextUtils.isEmpty(result)) {
													dConfig.setAds(result);
													dConfig.setAdsUpdateTime(adslut);
												}
											}
										}, 0);
							}
							if (currencylut > dConfig.getCurrencyUpdateTime()) {
								String url = Constants.REC_CONFIG + "currency";
								if (Constants.Debug) {
									Log.d(TAG, "正在请求货币信息:" + url);
								}
								HttpAsyncTask.fetchData(HttpAsyncTask.GET, url,
										null, new HttpReqCallBack() {
											@Override
											public void deal(String result) {
												if (Constants.Debug) {
													Log.d(TAG, "已获取货币信息："
															+ result);
												}
												if (!TextUtils.isEmpty(result)) {
													dConfig.setCurrency(result);
													dConfig.setCurrencyUpdateTime(currencylut);
												}
											}
										}, 0);
							}
							if (sourcelut > dConfig.getSourceUpdateTime()) {
								String url = Constants.REC_CONFIG + "source";
								if (Constants.Debug) {
									Log.d(TAG, "正在请求电商信息:" + url);
								}
								HttpAsyncTask.fetchData(HttpAsyncTask.GET, url,
										null, new HttpReqCallBack() {
											@Override
											public void deal(String result) {
												if (Constants.Debug) {
													Log.d(TAG, "已获取电商信息："
															+ result);
												}
												if (!TextUtils.isEmpty(result)) {
													dConfig.setSource(result);
													dConfig.setSourceUpdateTime(sourcelut);
												}
											}
										}, 0);
							}
							if (categorylut > dConfig.getCategoryUpdateTime()) {
								String url = Constants.REC_CONFIG + "category";
								if (Constants.Debug) {
									Log.d(TAG, "正在请求分类信息:" + url);
								}
								HttpAsyncTask.fetchData(HttpAsyncTask.GET, url,
										null, new HttpReqCallBack() {
											@Override
											public void deal(String result) {
												if (Constants.Debug) {
													Log.d(TAG, "已获取目录信息："
															+ result);
												}
												if (!TextUtils.isEmpty(result)) {
													dConfig.setCategory(result);
													dConfig.setCategoryUpdateTime(categorylut);
												}
											}
										}, 0);
							}
							if (levellut > dConfig.getLevelUpdateTime()) {
								String url = Constants.REC_CONFIG + "level";
								if (Constants.Debug) {
									Log.d(TAG, "正在请求等级信息:" + url);
								}
								HttpAsyncTask.fetchData(HttpAsyncTask.GET, url,
										null, new HttpReqCallBack() {
											@Override
											public void deal(String result) {
												if (Constants.Debug) {
													Log.d(TAG, "已获取等级信息："
															+ result);
												}
												if (!TextUtils.isEmpty(result)) {
													dConfig.setLevel(result);
													dConfig.setLevelUpdateTime(levellut);
												}
											}
										}, 0);
							}
							if (lcompanylut > dConfig.getLcompanyUpdateTime()) {
								// String url = Constants.REC_CONFIG +
								// "logistics";
								// String url =
								// "http://192.168.1.4:8080/app/logistics";
								String url = Constants.SHI_APP;
								if (Constants.Debug) {
									Log.e(TAG, "正在请求物流信息:" + url);
								}
								HttpAsyncTask.fetchData(HttpAsyncTask.GET, url,
										null, new HttpReqCallBack() {
											@Override
											public void deal(String result) {
												if (Constants.Debug) {
													Log.d(TAG, "已获取物流信息："
															+ result);
												}
												if (!TextUtils.isEmpty(result)) {
													dConfig.setLcompany(result);
													dConfig.setLcompanyUpdateTime(lcompanylut);
												}
											}
										}, 0);
							}
							if (tcompanylut > dConfig.getTcompanyUpdateTime()) {
								// String url =
								// "http://192.168.1.4:8080/app/transport";
								String url = Constants.TRANS_APP;
								if (Constants.Debug) {
									Log.e(TAG, "正在请求转运公司信息:" + url);
								}
								HttpAsyncTask.fetchData(HttpAsyncTask.GET, url,
										null, new HttpReqCallBack() {
											@Override
											public void deal(String result) {
												if (Constants.Debug) {
													Log.d(TAG, "已获取转运公司信息："
															+ result);
												}
												if (!TextUtils.isEmpty(result)) {
													dConfig.setTcompany(result);
													dConfig.setTcompanyUpdateTime(tcompanylut);

												}
											}
										}, 0);
							}

						} catch (JSONException e) {
							e.printStackTrace();
							Log.e(TAG, e.toString());
						}
					}
				}, 0);

	}

	@SuppressWarnings("deprecation")
	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.memoryCacheExtraOptions(480, 800)
				// max width, max height，即保存的每个缓存文件的最大长宽
				// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
				// null)// Can slow ImageLoader, use it carefully (Better don't
				// use
				// it)/设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(5)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100)
				// 缓存的文件数量
				.discCache(
						new UnlimitedDiscCache(ACache.get(this)
								.getCacheManager().getCacheDir()))
				// 自定义缓存路径,将缓存归于AcachedDir下，方便管理缓存大小
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout
																				// (5
																				// s),
																				// readTimeout
																				// (30
																				// s)超时时间
																				// .writeDebugLogs()
																				// //
																				// Remove
																				// for
																				// release
																				// app
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}
	/**
	 * getter method
	 * 
	 * @return the users
	 */

	public Users getUsers() {
		return users;
	}

	/**
	 * setter method
	 * 
	 * @param users
	 *            the users to set
	 */
	public void setUsers(Users users) {
		this.users = users;
	}

}