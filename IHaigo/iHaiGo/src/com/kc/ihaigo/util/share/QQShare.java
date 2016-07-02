/**
 * @Title: QQShare.java
 * @Package: com.kc.ihaigo.util.share
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月18日 下午5:52:36

 * @version V1.0

 */

package com.kc.ihaigo.util.share;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.login.QQLogin;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * @ClassName: QQShare
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年7月18日 下午5:52:36
 * 
 */

public class QQShare {
	public void shareToQQ(final Activity context) {

		if (!QQLogin.ready(context)) {
			QQLogin.registerQQ(context);
		}
		final Bundle params = new Bundle();
		params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TITLE,
				"qq分享");
		params.putString(
				com.tencent.connect.share.QQShare.SHARE_TO_QQ_TARGET_URL,
				"http://www.ihaigo.com");
		params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_SUMMARY,
				"我们的海淘Android马上就要上线了");
		params.putString(
				com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_URL,
				"http://himg2.huanqiu.com/attachment2010/2014/0721/20140721101938241.jpg");
		params.putString(
				com.tencent.connect.share.QQShare.SHARE_TO_QQ_APP_NAME, "海淘");
		final com.tencent.connect.share.QQShare mQQShare = new com.tencent.connect.share.QQShare(
				context, QQLogin.mTencent.getQQToken());
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mQQShare.shareToQQ(context, params, new IUiListener() {
					@Override
					public void onCancel() {
						// if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE)
						// {
						// Util.toastMessage(activity, "onCancel: ");
						// }
					}

					@Override
					public void onComplete(Object response) {
						if (Constants.Debug) {
							Toast.makeText(context, response.toString(), 1000)
									.show();
						}
					}

					@Override
					public void onError(UiError e) {
					}

				});
			}
		}).start();
	}
}
