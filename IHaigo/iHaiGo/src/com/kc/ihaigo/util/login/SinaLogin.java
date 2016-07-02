/**
 * @Title: SinaLogin.java
 * @Package: com.kc.ihaigo.util.login
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月18日 下午5:53:17

 * @version V1.0

 */

package com.kc.ihaigo.util.login;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.util.AccessTokenKeeper;
import com.kc.ihaigo.util.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * @ClassName: SinaLogin
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年7月18日 下午5:53:17
 * 
 */

public class SinaLogin {
	private static final String TAG = "SinaLogin";

	public static SsoHandler loginSina(final Activity context,
			final BackCall call) {
		/** 微博 Web 授权类，提供登陆等功能 */
		WeiboAuth mWeiboAuth;

		/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
		SsoHandler mSsoHandler;
		/**
		 * 微博 OpenAPI 回调接口。
		 */
		// 创建授权认证信息
		// 创建微博实例
		mWeiboAuth = new WeiboAuth(context, Constants.APP_KEY_SINA_LOGIN,
				Constants.APP_SINA_LOGIN_REDIRECT_URL,
				Constants.APP_SINA_LOGIN_SCOPE);
		mSsoHandler = new SsoHandler(context, mWeiboAuth);
		/**
		 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
		 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
		 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
		 * SharedPreferences 中。
		 */
		mSsoHandler.authorize(new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values) {
				// 从 Bundle 中解析 Token
				/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
				Oauth2AccessToken accessToken = Oauth2AccessToken
						.parseAccessToken(values);
				if (accessToken != null && accessToken.isSessionValid()) {
					// 保存 Token 到 SharedPreferences
					String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(new java.util.Date(accessToken
									.getExpiresTime()));
					AccessTokenKeeper.writeAccessToken(context, accessToken);
					UsersAPI mUsersAPI = new UsersAPI(accessToken);
					long uid = Long.parseLong(accessToken.getUid());
					mUsersAPI.show(uid, new RequestListener() {
						@Override
						public void onComplete(String response) {
							if (!TextUtils.isEmpty(response)) {
								LogUtil.i(TAG, response);
								call.deal(0, response);
							}
						}

						@Override
						public void onWeiboException(WeiboException e) {
							LogUtil.e(TAG, e.getMessage());
						}
					});
				} else {
					// 以下几种情况，您会收到 Code：
					// 1. 当您未在平台上注册的应用程序的包名与签名时；
					// 2. 当您注册的应用程序包名与签名不正确时；
					// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
					String code = values.getString("code");
					if (Constants.Debug) {
						Log.d(TAG, code);
					}
				}
			}

			@Override
			public void onCancel() {
				if (Constants.Debug) {
					Log.d(TAG, "新浪微博登录授权取消操作");
				}
			}

			@Override
			public void onWeiboException(WeiboException e) {
				if (Constants.Debug) {
					Log.d(TAG, "新浪微博登录发生异常：" + e.getMessage());
				}
			}
		});
		return mSsoHandler;
	}
}
