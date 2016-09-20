package com.langlang.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * 带声音提示的Toast自定义 Toast控件
 * 
 * @author http://weibo.com/lixiaodaoaaa http://t.qq.com/lixiaodaoaaa
 * @version 0.1
 * @created 2013-4-23
 */
public class MyToast extends Toast {
	private MediaPlayer mPlayer;
	private boolean isSound;

	public MyToast(Context context) {
		this(context, false, 0);
	}

	// isSound 表示是否播放音乐;
	public MyToast(Context context, boolean isSound, int music) {
		super(context);

		this.isSound = isSound;

		mPlayer = MediaPlayer.create(context, music);
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();// 释放资源。让资源得到释放;;
			}
		});
	}

	@Override
	public void show() {
		super.show();
		if (isSound) {
			mPlayer.start();
		}
	}

	/**
	 * 设置是否播放声音
	 */
	public void setIsSound(boolean isSound) {
		this.isSound = isSound;
	}

	/**
	 * 获取控件实例
	 * 
	 * @param context
	 * @param text
	 *            提示消息
	 * @param isSound
	 *            是否播放声音
	 * @return
	 */
	public static MyToast show(Context context, CharSequence text,
			boolean isSound, int duration, int music) {
		MyToast result = new MyToast(context, isSound, music);
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// View v = inflate.inflate(R.layout.item_toast, null);
		// // v.setMinimumWidth(dm.widthPixels);// 设置控件最小宽度为手机屏幕宽度
		// TextView tv = (TextView)
		// v.findViewById(R.id.item_new_data_toast_message);
		// tv.setText(text);
		// result.setView(v);
		result.setDuration(duration);// 设置 显示多长时间;
		result.setGravity(Gravity.BOTTOM, 0, (int) (dm.density * 85));
		return result;
	}

}
