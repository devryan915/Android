/**
 * @Title: QQLogin.java
 * @Package: com.kc.ihaigo.util.login
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月18日 下午5:00:03

 * @version V1.0

 */

package com.kc.ihaigo.util.login;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.util.Constants;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * @ClassName: QQLogin
 * @Description: 用于qq第三方登录
 * @author: ryan.wang
 * @date: 2014年7月18日 下午5:00:03
 * 
 */

public class QQLogin {

	public static Tencent mTencent;
	public class BaseUIListener implements IUiListener {
		private Context mContext;
		private String mScope;
		private boolean mIsCaneled;
		private static final int ON_COMPLETE = 0;
		private static final int ON_ERROR = 1;
		private static final int ON_CANCEL = 2;
		private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case ON_COMPLETE :
						JSONObject response = (JSONObject) msg.obj;
						break;
					case ON_ERROR :
						break;
					case ON_CANCEL :
						break;
				}
			}
		};

		public BaseUIListener(Context mContext) {
			super();
			this.mContext = mContext;
		}

		public BaseUIListener(Context mContext, String mScope) {
			super();
			this.mContext = mContext;
			this.mScope = mScope;
		}

		public void cancel() {
			mIsCaneled = true;
		}

		@Override
		public void onComplete(Object response) {
			if (mIsCaneled)
				return;
			Message msg = mHandler.obtainMessage();
			msg.what = ON_COMPLETE;
			msg.obj = response;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onError(UiError e) {
			if (mIsCaneled)
				return;
			Message msg = mHandler.obtainMessage();
			msg.what = ON_ERROR;
			msg.obj = e;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onCancel() {
			if (mIsCaneled)
				return;
			Message msg = mHandler.obtainMessage();
			msg.what = ON_CANCEL;
			mHandler.sendMessage(msg);
		}

		public Context getmContext() {
			return mContext;
		}

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

	}
	public static void registerQQ(Context context) {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(Constants.APP_ID_QQ_LOGIN,
					context);
		}
	}
	public static boolean ready(Context context) {
		if (mTencent == null) {
			return false;
		}
		boolean ready = mTencent.isSessionValid()
				&& mTencent.getQQToken().getOpenId() != null;
		// if (!ready)
		// Toast.makeText(context, "login and get openId first, please!",
		// Toast.LENGTH_SHORT).show();
		return ready;
	}
	/**
	 * 
	 * @Title: loginqq
	 * @user: ryan.wang
	 * @Description: 通过qq登录
	 * 
	 * @param context
	 * @return String
	 * @throws
	 */
	public static void loginQQ(final Activity context, final BackCall backCall) {
		registerQQ(context);
		// qq登录后的用户唯一标识
		final String openId = mTencent.getOpenId();
		if (mTencent == null)
			return;
		if (!mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onCancel() {
				}

				@Override
				public void onError(UiError arg0) {
				}

				@Override
				public void onComplete(Object obj) {
					backCall.deal(0, obj);
				}
			};
			mTencent.login(context, "all", listener);
		} else {
			mTencent.logout(context);
		}
	}
	public static String getUserInfo(Activity context, final BackCall backCall) {
		if (ready(context)) {
			UserInfo mInfo = new UserInfo(context, mTencent.getQQToken());
			// get_simple_userinfo
			mInfo.getUserInfo(new IUiListener() {

				@Override
				public void onError(UiError arg0) {

				}

				@Override
				public void onComplete(Object response) {
					backCall.deal(0, response);
				}

				@Override
				public void onCancel() {

				}
			});
		}
		return null;
	}

}
