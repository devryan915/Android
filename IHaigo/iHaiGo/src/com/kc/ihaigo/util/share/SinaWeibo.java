/**
 * @Title: SinaWeibo.java
 * @Package: com.kc.ihaigo.util.share
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月18日 下午5:54:20

 * @version V1.0

 */

package com.kc.ihaigo.util.share;

import android.app.Activity;
import android.util.Log;

import com.kc.ihaigo.util.Constants;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

/**
 * @ClassName: SinaWeibo
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2014年7月18日 下午5:54:20
 * 
 */

public class SinaWeibo implements IWeiboHandler.Response {
	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;
	private final static String TAG = "SinaWeibo";

	public void shareToSina(Activity context) {
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context,
				Constants.APP_KEY_SINA_LOGIN);

		// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
		// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
		// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
		mWeiboShareAPI.registerApp();

		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!mWeiboShareAPI.isWeiboAppInstalled()) {
			mWeiboShareAPI
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							if (Constants.Debug) {
								Log.d(TAG, "onCancel");
							}
						}
					});
		}
		sendMessage(true, false, false, false, false, false);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。
	 * 
	 * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
	 */
	private void sendMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo,
			boolean hasVoice) {

		if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
			int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
			if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
				sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic,
						hasVideo, hasVoice);
			} else {
				sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic,
						hasVideo/* , hasVoice */);
			}
		} else {
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 * @param hasVoice
	 *            分享的内容是否有声音
	 */
	private void sendMultiMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo,
			boolean hasVoice) {

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if (hasText) {
			weiboMessage.textObject = getTextObj();
		}

		if (hasImage) {
			weiboMessage.imageObject = getImageObj();
		}

		// 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
		if (hasWebpage) {
			weiboMessage.mediaObject = getWebpageObj();
		}
		if (hasMusic) {
			weiboMessage.mediaObject = getMusicObj();
		}
		if (hasVideo) {
			weiboMessage.mediaObject = getVideoObj();
		}
		if (hasVoice) {
			weiboMessage.mediaObject = getVoiceObj();
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()}
	 * < 10351 时，只支持分享单条消息，即 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 */
	private void sendSingleMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo/*
																 * , boolean
																 * hasVoice
																 */) {

		// 1. 初始化微博的分享消息
		// 用户可以分享文本、图片、网页、音乐、视频中的一种
		WeiboMessage weiboMessage = new WeiboMessage();
		if (hasText) {
			weiboMessage.mediaObject = getTextObj();
		}
		if (hasImage) {
			weiboMessage.mediaObject = getImageObj();
		}
		if (hasWebpage) {
			weiboMessage.mediaObject = getWebpageObj();
		}
		if (hasMusic) {
			weiboMessage.mediaObject = getMusicObj();
		}
		if (hasVideo) {
			weiboMessage.mediaObject = getVideoObj();
		}
		/*
		 * if (hasVoice) { weiboMessage.mediaObject = getVoiceObj(); }
		 */

		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = "来自ihaigo的分享";
		return textObject;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();
		// BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView
		// .getDrawable();
		// imageObject.setImageObject(bitmapDrawable.getBitmap());
		return imageObject;
	}

	/**
	 * 创建多媒体（网页）消息对象。
	 * 
	 * @return 多媒体（网页）消息对象。
	 */
	private WebpageObject getWebpageObj() {
		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Utility.generateGUID();
		// mediaObject.title = mShareWebPageView.getTitle();
		// mediaObject.description = mShareWebPageView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// mediaObject.setThumbImage(mShareWebPageView.getThumbBitmap());
		// mediaObject.actionUrl = mShareWebPageView.getShareUrl();
		// mediaObject.defaultText = "Webpage 默认文案";
		return mediaObject;
	}

	/**
	 * 创建多媒体（音乐）消息对象。
	 * 
	 * @return 多媒体（音乐）消息对象。
	 */
	private MusicObject getMusicObj() {
		// 创建媒体消息
		MusicObject musicObject = new MusicObject();
		musicObject.identify = Utility.generateGUID();
		// musicObject.title = mShareMusicView.getTitle();
		// musicObject.description = mShareMusicView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// musicObject.setThumbImage(mShareMusicView.getThumbBitmap());
		// musicObject.actionUrl = mShareMusicView.getShareUrl();
		// musicObject.dataUrl = "www.weibo.com";
		// musicObject.dataHdUrl = "www.weibo.com";
		// musicObject.duration = 10;
		// musicObject.defaultText = "Music 默认文案";
		return musicObject;
	}

	/**
	 * 创建多媒体（视频）消息对象。
	 * 
	 * @return 多媒体（视频）消息对象。
	 */
	private VideoObject getVideoObj() {
		// 创建媒体消息
		VideoObject videoObject = new VideoObject();
		videoObject.identify = Utility.generateGUID();
		// videoObject.title = mShareVideoView.getTitle();
		// videoObject.description = mShareVideoView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// videoObject.setThumbImage(mShareVideoView.getThumbBitmap());
		// videoObject.actionUrl = mShareVideoView.getShareUrl();
		videoObject.dataUrl = "www.weibo.com";
		videoObject.dataHdUrl = "www.weibo.com";
		videoObject.duration = 10;
		videoObject.defaultText = "Vedio 默认文案";
		return videoObject;
	}

	/**
	 * 创建多媒体（音频）消息对象。
	 * 
	 * @return 多媒体（音乐）消息对象。
	 */
	private VoiceObject getVoiceObj() {
		// 创建媒体消息
		VoiceObject voiceObject = new VoiceObject();
		voiceObject.identify = Utility.generateGUID();
		// voiceObject.title = mShareVoiceView.getTitle();
		// voiceObject.description = mShareVoiceView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// voiceObject.setThumbImage(mShareVoiceView.getThumbBitmap());
		// voiceObject.actionUrl = mShareVoiceView.getShareUrl();
		voiceObject.dataUrl = "www.weibo.com";
		voiceObject.dataHdUrl = "www.weibo.com";
		voiceObject.duration = 10;
		voiceObject.defaultText = "Voice 默认文案";
		return voiceObject;
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
			case WBConstants.ErrorCode.ERR_OK :
				Log.d(TAG, "ERR_OK");
				break;
			case WBConstants.ErrorCode.ERR_CANCEL :
				Log.d(TAG, "ERR_CANCEL");
				break;
			case WBConstants.ErrorCode.ERR_FAIL :
				Log.d(TAG, "ERR_FAIL");
				break;
		}
	}
}