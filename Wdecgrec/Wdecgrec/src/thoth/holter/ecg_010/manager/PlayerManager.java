/**
 * 
 */
package thoth.holter.ecg_010.manager;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import thoth.holter.ecg_010.R;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * @author ryan.wang
 * 
 */
public class PlayerManager {
	private final static String TAG = PlayerManager.class.getSimpleName();
	private static PlayerManager Instance;
	private AtomicBoolean isPlaying = new AtomicBoolean(false);

	private int playCount = 0;
	private int curPlayCount = 0;
	private int curPlayResId = 0;
	private OnCompletionListener listener;
	private ConcurrentLinkedQueue<Integer> palyList = new ConcurrentLinkedQueue<Integer>();

	public static PlayerManager getInstance() {
		if (Instance == null)
			Instance = new PlayerManager();
		return Instance;
	}

	private void _play(int id, final OnCompletionListener listener) {
		if (ConstantConfig.Debug) {
			String curCount = (curPlayCount + 1) + "/" + playCount;
			switch (id) {
			case R.raw.lowsignal:
				LogUtil.d(TAG, "play信号低" + curCount);
				break;
			case R.raw.lowpower:
				LogUtil.d(TAG, "play电量低" + curCount);
				break;
			case R.raw.devoff:
				LogUtil.d(TAG, "play设备断开低" + curCount);
				break;
			case R.raw.devfalloff:
				LogUtil.d(TAG, "play设备脱落低" + curCount);
				break;
			default:
				break;
			}
		}
		MediaPlayer player = MediaPlayer.create(AppApplication.Instance, id);
		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				curPlayCount++;
				if (listener != null) {
					listener.onCompletion(mp);
				}
			}
		});
	}

	private void play() {
		if (palyList.size() <= 0)
			return;
		if (isPlaying.compareAndSet(false, true)) {
			try {
				playCount = 0;
				curPlayCount = 0;
				curPlayResId = palyList.poll();
				switch (curPlayResId) {
				case R.raw.lowsignal:
					playCount = 1;
					break;
				case R.raw.lowpower:
					playCount = 2;
					break;
				case R.raw.devoff:
					playCount = 3;
					break;
				case R.raw.devfalloff:
					playCount = 4;
					break;
				default:
					break;
				}
				listener = new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.release();
						if (curPlayCount < playCount) {
							_play(curPlayResId, listener);
						} else {
							try {
								// 每个提示音播放完等待3s
								Thread.sleep(3000);
								isPlaying.set(false);
								play();
							} catch (InterruptedException e) {
								e.printStackTrace();
								isPlaying.set(false);
								play();
							}
						}
					}
				};
				_play(curPlayResId, listener);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				isPlaying.set(false);
				play();
				LogUtil.e(TAG, e);
			}
		}
	}

	/**
	 * 播放信号弱提示音
	 */
	public void playLowSignal() {
		if (SettingsManager.getInstance().getSettingsLowSignal()) {
			palyList.offer(R.raw.lowsignal);
			play();
		}
	}

	/**
	 * 播放电量低提示音
	 */
	public void playLowPower() {
		if (SettingsManager.getInstance().getSettingsLowPower()) {
			palyList.offer(R.raw.lowpower);
			play();
		}
	}

	/**
	 * 播放设备断开提示音
	 */
	public void playDevOff() {
		if (SettingsManager.getInstance().getSettingsDevOff()) {
			palyList.offer(R.raw.devoff);
			play();
		}
	}

	/**
	 * 播放设备脱落提示音
	 */
	public void playDevFallOff() {
		if (SettingsManager.getInstance().getSettingsDevFallOff()) {
			palyList.offer(R.raw.devfalloff);
			play();
		}
	}
}
