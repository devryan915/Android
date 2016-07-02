/**
 * 
 */
package com.broadchance.manager;

import java.io.IOException;

import com.broadchance.utils.ConstantConfig;
import com.broadchance.wdecgrec.R;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * @author ryan.wang
 * 
 */
public class PlayerManager {
	private static PlayerManager Instance;

	public static PlayerManager getInstance() {
		if (Instance == null)
			Instance = new PlayerManager();
		return Instance;
	}

	private void play(int id) {
		try {
			if (ConstantConfig.Debug) {
				return;
			}
			final MediaPlayer player = MediaPlayer.create(
					AppApplication.Instance, R.raw.lowsignal);
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					player.release();
				}
			});
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放信号弱提示音
	 */
	public void playLowSignal() {
		if (SettingsManager.getInstance().getSettingsLowSignal()) {
			play(R.raw.lowsignal);
		}
	}

	/**
	 * 播放电量低提示音
	 */
	public void playLowPower() {
		if (SettingsManager.getInstance().getSettingsLowPower()) {
			play(R.raw.lowpower);
		}
	}

	/**
	 * 播放设备断开提示音
	 */
	public void playDevOff() {
		if (SettingsManager.getInstance().getSettingsDevOff()) {
			play(R.raw.devoff);
		}
	}

	/**
	 * 播放设备脱落提示音
	 */
	public void playDevFallOff() {
		if (SettingsManager.getInstance().getSettingsDevFallOff()) {
			play(R.raw.devfalloff);
		}
	}
}
