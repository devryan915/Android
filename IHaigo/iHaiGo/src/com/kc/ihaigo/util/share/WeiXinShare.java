package com.kc.ihaigo.util.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.CheckBox;
import android.widget.Toast;

import com.kc.ihaigo.util.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

public class WeiXinShare {
	private static final int THUMB_SIZE = 150;
	private static final String SDCARD_ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	private CheckBox isTimelineCb;

	public void shareToWeiXin(Activity context) {
		// 调用api接口发送数据到微信
		IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.APP_ID_WEIXIN);
		api.registerApp(Constants.APP_ID_WEIXIN);
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = "testcontent";

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = "testtitle";

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;

		api.sendReq(req);
	}

	public void shareToWeiXinText(Activity context, String title,
			String msgContent) {
		// 调用api接口发送数据到微信
		IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.APP_ID_WEIXIN);
		api.registerApp(Constants.APP_ID_WEIXIN);
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = msgContent;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = title;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;

		api.sendReq(req);
	}

	/**
	 * 发送图片和文字
	 * 
	 * @param context
	 * @param text
	 * @param bmp
	 * @param type 分享到盆友圈：SendMessageToWX.Req.WXSceneTimeline 分享到盆友：SendMessageToWX.Req.WXSceneSession
	 */
	public void sendReq(Context context, String text, Bitmap bmp) {
		// IWXAPI api = WXAPIFactory.createWXAPI(context, ShareConstant.APP_ID,
		// true);
		IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.APP_ID_WEIXIN);
		api.registerApp(Constants.APP_ID_WEIXIN);
		if (api.openWXApp()) {
			String url = Environment.getExternalStorageDirectory() + "/liangPic"+"/1406180922503.png";// 分享的好友点击信息会跳转到这个地址去
			WXWebpageObject localWXWebpageObject = new WXWebpageObject();
			localWXWebpageObject.webpageUrl = url;
			WXMediaMessage localWXMediaMessage = new WXMediaMessage(
					localWXWebpageObject);
			
			localWXMediaMessage.description = text;

			// 设置消息的缩略图
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
					THUMB_SIZE, true);
			bmp.recycle();
			localWXMediaMessage.thumbData = Util.bmpToByteArray(thumbBmp, true);

			// localWXMediaMessage.thumbData = getBitmapBytes(bmp, false);
			SendMessageToWX.Req localReq = new SendMessageToWX.Req();
			localReq.transaction = System.currentTimeMillis() + "";
			localReq.message = localWXMediaMessage;
			// localReq.scene = SendMessageToWX.Req.WXSceneTimeline;// 分享到朋友圈
			 localReq.scene = SendMessageToWX.Req.WXSceneSession;// 分享给好友
//			localReq.scene = type;

			api.sendReq(localReq);
		} else {
			Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 发送图片和文字
	 * 
	 * @param context
	 * @param text
	 * @param bmp
	 * @param type 分享到盆友圈：SendMessageToWX.Req.WXSceneTimeline 分享到盆友：SendMessageToWX.Req.WXSceneSession
	 */
	public void sendReqFriend(Context context, String text, Bitmap bmp) {
		// IWXAPI api = WXAPIFactory.createWXAPI(context, ShareConstant.APP_ID,
		// true);
		IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.APP_ID_WEIXIN);
		api.registerApp(Constants.APP_ID_WEIXIN);
		if (api.openWXApp()) {
			String url = Environment.getExternalStorageDirectory() + "/liangPic"+"/1406180922503.png";// 分享的好友点击信息会跳转到这个地址去
			WXWebpageObject localWXWebpageObject = new WXWebpageObject();
			localWXWebpageObject.webpageUrl = url;
			WXMediaMessage localWXMediaMessage = new WXMediaMessage(
					localWXWebpageObject);
			
			localWXMediaMessage.description = text;

			// 设置消息的缩略图
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
					THUMB_SIZE, true);
			bmp.recycle();
			localWXMediaMessage.thumbData = Util.bmpToByteArray(thumbBmp, true);

			// localWXMediaMessage.thumbData = getBitmapBytes(bmp, false);
			SendMessageToWX.Req localReq = new SendMessageToWX.Req();
			localReq.transaction = System.currentTimeMillis() + "";
			localReq.message = localWXMediaMessage;
			 localReq.scene = SendMessageToWX.Req.WXSceneTimeline;// 分享到朋友圈
//			 localReq.scene = SendMessageToWX.Req.WXSceneSession;// 分享给好友
//			localReq.scene = type;

			api.sendReq(localReq);
		} else {
			Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show();
		}
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}
}
