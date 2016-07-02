package com.broadchance.xiaojian.person.sports;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.broadchance.xiaojian.BaseFragment;
import com.broadchance.xiaojian.CallBack;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.utils.CommonUtil;
import com.broadchance.xiaojian.utils.Constant;
import com.langlang.global.GlobalStatus;

public class SportsCurrentFragment extends BaseFragment {

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			sports_steps.setText(Integer.toString(new TiannmaStep().getStep()));
			DecimalFormat formator = new DecimalFormat("#.#");
			float mCalories = new TiannmaCalorie().getCalories();
			float calories = mCalories / 1000.0f;
			sports_calorie.setText(formator.format(calories));
			sports_curbreathrate.setText(Integer.toString(GlobalStatus
					.getInstance().getBreath()));
			sports_curheartrate.setText(Integer.toString(new TiannmaHeartRate()
					.getHeartRate()));
		};
	};

	class TiannmaHeartRate {
		private int[] mDisplayHte = { 0, 0, 0, 0, 0 };
		private int mCurrHte = 0;
		private int mHteCount = 0;
		private int mHeartRate = 0;

		public int getHeartRate() {
			updateHeartRateData();
			return mHeartRate;
		}

		private void updateHeartRateData() {
			int currHte = GlobalStatus.getInstance().getHeartRate();
			if (mHteCount < 4) {
				mHeartRate = currHte;
			} else {
				int averageHte = getAverageHte();
				if ((currHte > ((int) (averageHte * 1.3f)))
						|| (currHte < ((int) (averageHte * 0.7f)))) {
					System.out.println("action MainActivity currHte[" + currHte
							+ "] average hte [" + averageHte + "]");
				} else {
					mHeartRate = currHte;
				}
			}
			mDisplayHte[mCurrHte] = currHte;
			mCurrHte++;
			if (mCurrHte >= 5)
				mCurrHte = 0;

			mHteCount++;
			if (mHteCount > 5)
				mHteCount = 5;
		}

		private int getAverageHte() {
			if (mHteCount == 0)
				return 0;
			int sum = 0;
			for (int i = 0; i < mHteCount; i++) {
				sum += mDisplayHte[i];
			}
			return (int) ((sum * 1.0f) / mHteCount);
		}
	}

	class TiannmaStep {
		public int getStep() {
			return GlobalStatus.getInstance().getCurrentStep();
		}
	}

	class TiannmaCalorie {
		public int getCalories() {
			return GlobalStatus.getInstance().getCalories();
		}
	}

	private Timer timer;
	private TimerTask task;

	@Override
	public void onResume() {
		super.onResume();
		task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 200;
				handler.sendMessage(message);
			}
		};
		timer = new Timer();
		timer.schedule(task, 0, 1000);
	}

	@Override
	public void onPause() {
		super.onPause();
		timer.cancel();
		task = null;
		timer = null;
	}

	private TextView sports_steps;
	private TextView sports_calorie;
	private TextView sports_curbreathrate;
	private TextView sports_curheartrate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sportscurrent, null);
		sports_steps = (TextView) rootView.findViewById(R.id.sports_steps);
		sports_calorie = (TextView) rootView.findViewById(R.id.sports_calorie);
		sports_curbreathrate = (TextView) rootView
				.findViewById(R.id.sports_curbreathrate);
		sports_curheartrate = (TextView) rootView
				.findViewById(R.id.sports_curheartrate);
		rootView.findViewById(R.id.personalhealth_sports_setgoal)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog = CommonUtil.showSetGoalDialog(getActivity(),
								new CallBack() {

									@Override
									public void doBack(Object... params) {
										dialog.cancel();
										Toast.makeText(getActivity(),
												params[0].toString(), 1000)
												.show();
									}
								});
						dialog.show();
					}
				});
		playMusic();
		return rootView;
	}

	private List<String> musicFiles = new ArrayList<String>();
	private MediaPlayer mediaPlayer;
	private int musicListIndex = 0;
	private Dialog dialog;

	private void getMusicFiles() {
		musicFiles.clear();
		Cursor mAudioCursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,// 字段　没有字段　就是查询所有信息　相当于SQL语句中的　“
																	// * ”
				null, // 查询条件
				null, // 条件的对应?的参数
				MediaStore.Audio.AudioColumns.TITLE);// 排序方式
		// 循环输出歌曲的信息
		for (int i = 0; i < mAudioCursor.getCount(); i++) {
			mAudioCursor.moveToNext();
			// 找到歌曲标题和总时间对应的列索引
			int indexData = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);// 歌名
			String path = mAudioCursor.getString(indexData);
			musicFiles.add(path);
		}
	}

	private void play(String path) {
		mediaPlayer.reset();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		try {
			mediaPlayer.setDataSource(path);// "/storage/extSdCard/My Music"
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.start();
	}

	private void playMusic() {
		SharedPreferences sp = getActivity().getSharedPreferences(
				Constant.SETTINGS_CONFIG, getActivity().MODE_PRIVATE);
		boolean isplay = sp.getBoolean(Constant.SETTINGS_MUSIC, false);
		if (isplay) {
			mediaPlayer = new MediaPlayer();
			getMusicFiles();
			if (musicFiles.size() > 0) {
				SportsCurrentFragment.this.play(musicFiles.get(musicListIndex));
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						SportsCurrentFragment.this.play(musicFiles
								.get(++musicListIndex));
					}
				});
				mediaPlayer.setOnErrorListener(new OnErrorListener() {

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						SportsCurrentFragment.this.play(musicFiles
								.get(++musicListIndex));
						return false;
					}
				});
			}
		}
	}

	@Override
	public void onDestroyView() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
		super.onDestroyView();
	}

}