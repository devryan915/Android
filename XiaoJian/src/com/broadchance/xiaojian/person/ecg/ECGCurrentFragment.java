package com.broadchance.xiaojian.person.ecg;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.broadchance.xiaojian.BaseFragment;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.service.BleDataParserService;
import com.broadchance.xiaojian.service.BluetoothLeService;
import com.langlang.activity.EcgPainterBase.ECGGLSurfaceView;
import com.langlang.dialog.ECGProgressDialog;
import com.langlang.global.GlobalStatus;
import com.langlang.global.UserInfo;
import com.langlang.manager.WarningHteManager;
import com.langlang.utils.Filter;
import com.langlang.utils.Program;
import com.langlang.utils.ScreenShotUtils;
import com.langlang.utils.UIUtil;

public class ECGCurrentFragment extends BaseFragment {
	private static final String ACTION_ALARM = "com.langlang.activity.HeartRateActivity.ACTION_ALARM";
	private ECGGLSurfaceView mSurfaceView;
	private ECGProgressDialog progressDialog = null;
	private Bitmap mEcgBmp = null;
	private Bitmap mScreenBmp = null;
	private boolean isECGCanvasInitialized = false;
	private volatile boolean mCapture = false;
	private Object lockCapture = new Object();
	private Object lockBmps = new Object();
	private TextView left_tw;
	private TextView up_tw;
	private TextView right_tw;
	private TextView down_tw;
	private int ecg_x;
	private int ecg_y;
	private Bitmap mLeftBitmap = null;
	private int left_x;
	private int left_y;
	private Bitmap mRightBitmap = null;
	private int right_x;
	private int right_y;
	private Bitmap mUpBitmap = null;
	private int up_x;
	private int up_y;
	private Bitmap mDownBitmap = null;
	private int down_x;
	private int down_y;
	private boolean stopPainting = false;
	public static final int UPDATE_CANVAS = 1;
	public static final int SHOW_PROGRESS = 3;
	public static final int HIDE_PROGRESS = 4;
	public static final int QUIT_ACTIVITY = 5;
	// private final static int UPDATE_MENTAL_STRESS_DETAIL = 6;
	private final static int SHOW_CAPTURE_SUCCESS = 51;
	private final static int SHOW_IS_PAITING_STOPPED = 52;
	private final static int SHARE_IMAGE = 50;
	private Queue<Integer> queue = new LinkedList<Integer>();
	private int pointNumber = 0;
	private static final int MAX_POINT = 750;
	private static final String TAG = ECGCurrentFragment.class.getSimpleName();
	private float[] ECGData = new float[MAX_POINT];
	private ECGProgressDialog.OnCancelCallback onCancelCallback = new ECGProgressDialog.OnCancelCallback() {
		@Override
		public void onCancel() {
			UIUtil.setMessage(handler, QUIT_ACTIVITY);
		}
	};
	private Filter filter = new Filter();
	private String path_image = Program.getSDLangLangImagePath()
			+ "/heart_image.png";

	// private TextView tv_heartRate;
	// private TextView tv_hteWarning;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// ECG
			// if
			// (BleDataParserService.ACTION_ECG_DATA_AVAILABLE.equals(msg.obj))
			// {
			// // 心电
			// tv_heartRate.setText(Integer.toString(new TiannmaHeartRate()
			// .getHeartRate()));
			// // 获取心电报警次数
			// tv_hteWarning.setText(Integer.toString(new TiannmaHteWarning()
			// .getHteWarning()));
			// }
			if (msg.what == UPDATE_CANVAS) {
				float[] fVal = new float[10];
				int[] queueData = (int[]) msg.obj;

				if ((queueData != null) && (queueData.length >= 10)) {
					for (int i = 0; i < 10; i++) {
						fVal[i] = ((queueData[i] - 32000) * 6) / 1000f; // for
																		// ECG
					}
					if (pointNumber <= 740) {
						if (pointNumber == 0) {
							System.out
									.println("action EcgPainterActivity pointNumber is 0.");
						} else {
							for (int i = pointNumber - 1; i >= 0; i--) {
								ECGData[(i + 10)] = ECGData[i];
							}
						}
					} else {
						for (int i = pointNumber - 10 - 1; i >= 0; i--) {
							ECGData[(i + 10)] = ECGData[i];
						}
					}

					for (int i = 0; i < 10; i++) {
						ECGData[(9 - i)] = fVal[i];
					}

					pointNumber += 10;
					if (pointNumber >= 750)
						pointNumber = 750;

					mSurfaceView.drawECG(ECGData, pointNumber);
				}
			} else if (msg.what == SHOW_PROGRESS) {
				if (progressDialog == null) {
					progressDialog = new ECGProgressDialog(getActivity(),
							onCancelCallback);
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("正在初始化,请稍候...");
					progressDialog.setIndeterminate(false);
					progressDialog.setCancelable(false);

					progressDialog.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							if (!isECGCanvasInitialized) {

							}
						}
					});

					progressDialog.show();
				} else {
					progressDialog.show();
				}
			} else if (msg.what == HIDE_PROGRESS) {
				if (progressDialog != null) {
					progressDialog.cancel();
				}
			} else if (msg.what == SHOW_CAPTURE_SUCCESS) {
				UIUtil.setLongToast(getActivity(), "截图成功");
			} else if (msg.what == SHARE_IMAGE) {
				boolean success = (Boolean) msg.obj;
				if (success) {
					UIUtil.setToast(getActivity(), path_image);
				} else {
					UIUtil.setToast(getActivity(), "无法截图");
				}
			}
			// else if (msg.what == UPDATE_MENTAL_STRESS_DETAIL) {
			// }
		};
	};
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				Toast.makeText(getActivity(), "设备已连接", 1000).show();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				Toast.makeText(getActivity(), "设备已断开", 1000).show();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				Toast.makeText(getActivity(), "找到服务", 1000).show();
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// byte[] data = intent
				// .getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				// final StringBuilder stringBuilder = new StringBuilder(
				// data.length);
				// for (byte byteChar : data)
				// stringBuilder.append(String.format("%02X ", byteChar));
				// Toast.makeText(getActivity(),
				// "获取到数据" + stringBuilder.toString(), 100).show();
			} else if (ACTION_ALARM.equals(action)) {
				int[] queueData = new int[10];
				boolean hasData = false;

				synchronized (queue) {
					if (queue.size() >= 10) {
						for (int i = 0; i < 10; i++) {
							queueData[i] = queue.poll();
						}
						hasData = true;
					}
				}
				if (hasData) {
					UIUtil.setMessage(handler, UPDATE_CANVAS, queueData);
				}
			} else if (BleDataParserService.ACTION_ECG_DATA_AVAILABLE
					.equals(action)) {
				synchronized (queue) {
					int[] ecg = intent.getIntArrayExtra("ECGData");

					if (ecg != null) {
						for (int i = 0; i < ecg.length; i++) {
							try {
								filter.addData(((ecg[i] - 28000)));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < ecg.length; i++) {
							if (filter.canGetOne()) {
								try {
									queue.offer(filter.getOne());
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								break;
							}
						}
					}
				}
				Message msg = Message.obtain();
				msg.obj = BleDataParserService.ACTION_ECG_DATA_AVAILABLE;
				handler.sendMessage(msg);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_ecgcurrent, null);
		// tv_heartRate = (TextView) rootView.findViewById(R.id.tv_heartRate);
		// tv_hteWarning = (TextView) rootView.findViewById(R.id.tv_hteWarning);
		mSurfaceView = (ECGGLSurfaceView) rootView
				.findViewById(R.id.hte_GLSurfaceView_ecg);
		mSurfaceView.setCallback(new ECGGLSurfaceView.CanvasReadyCallback() {
			@Override
			public void notifyCanvasReady() {
				isECGCanvasInitialized = true;
				UIUtil.setMessage(handler, HIDE_PROGRESS);
			}

			@Override
			public boolean getCapture() {
				synchronized (lockCapture) {
					return mCapture;
				}
			}

			@Override
			public void onCaptured(Bitmap bitmap) {
				setCapture(false);
				onECGCaptured(bitmap);
			}

			@Override
			public boolean stopPainting() {
				return stopPainting;
			}
		});
		mSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (stopPainting) {
					stopPainting = false;
					UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED, false);
				} else {
					stopPainting = true;
					UIUtil.setMessage(handler, SHOW_IS_PAITING_STOPPED, true);
				}
			}
		});
		left_tw = (TextView) rootView.findViewById(R.id.hte_left_tw);
		up_tw = (TextView) rootView.findViewById(R.id.hte_up_tw);
		right_tw = (TextView) rootView.findViewById(R.id.hte_right_tw);
		down_tw = (TextView) rootView.findViewById(R.id.hte_down_tw);

		// Timer timer = new Timer();
		// timer.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// UIUtil.setMessage(handler, UPDATE_MENTAL_STRESS_DETAIL);
		// }
		// }, 0, 1000 * 60);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mGattUpdateReceiver,
				makeGattUpdateIntentFilter());
		Log.e(TAG, "registerReceiver");
		filter.reset();
		pointNumber = 0;
		queue.clear();
		mSurfaceView.reset();
		isECGCanvasInitialized = false;
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM);
		AlarmManager am = (AlarmManager) getActivity().getSystemService(
				getActivity().ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent,
				0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.setRepeating(AlarmManager.RTC, triggerAtTime + 200, 40, pi);
	}

	@Override
	public void onPause() {
		super.onPause();
		Intent intent = new Intent();
		intent.setAction(ACTION_ALARM);
		AlarmManager am = (AlarmManager) getActivity().getSystemService(
				getActivity().ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent,
				0);
		Calendar c = Calendar.getInstance();
		long triggerAtTime = c.getTimeInMillis();
		am.cancel(pi);
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		Log.e(TAG, "unregisterReceiver");
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(ACTION_ALARM);
		intentFilter.addAction(BleDataParserService.ACTION_ECG_DATA_AVAILABLE);
		intentFilter.addAction(BleDataParserService.ACTION_RESP_DATA_AVAILABLE);
		intentFilter
				.addAction(BleDataParserService.ACTION_UPDATE_STEP_AND_CALORIES);
		intentFilter
				.addAction(BleDataParserService.ACTION_CURRENT_STATE_CHANGE);
		intentFilter.addAction(BleDataParserService.ACTION_TEMP);
		intentFilter
				.addAction(BleDataParserService.ACTION_UPDATE_ECG_HEART_RATE);
		intentFilter.addAction(BleDataParserService.ACTION_TUMBLE_HAPPENED);
		intentFilter.addAction(BleDataParserService.ACTION_ALERT_SD_STATUS);
		return intentFilter;
	}

	private void setCapture(boolean capture) {
		synchronized (lockCapture) {
			mCapture = capture;
			if (capture) {
				int[] location = new int[2];

				mSurfaceView.getLocationOnScreen(location);
				ecg_x = location[0];
				ecg_y = location[1];

				left_tw.getLocationOnScreen(location);
				left_x = location[0];
				left_y = location[1];

				right_tw.getLocationOnScreen(location);
				right_x = location[0];
				right_y = location[1];

				up_tw.getLocationOnScreen(location);
				up_x = location[0];
				up_y = location[1];

				down_tw.getLocationOnScreen(location);
				down_x = location[0];
				down_y = location[1];

				System.out.println("action HeartRateActivity setCapture22 "
						+ ecg_x + "," + ecg_y);

				// mSurfaceView.drawECG(ECGData, pointNumber);
				mSurfaceView.redrawForCapture();
			}
		}
	}

	private void onECGCaptured(Bitmap bitmap) {
		synchronized (lockBmps) {
			mEcgBmp = bitmap;
			mLeftBitmap = ScreenShotUtils.saveViewToBitmap(left_tw);
			mRightBitmap = ScreenShotUtils.saveViewToBitmap(right_tw);
			mUpBitmap = ScreenShotUtils.saveViewToBitmap(up_tw);
			mDownBitmap = ScreenShotUtils.saveViewToBitmap(down_tw);
		}
		UIUtil.setMessage(handler, SHOW_CAPTURE_SUCCESS);
		new MergeBitmapThread().start();
	}

	class MergeBitmapThread extends Thread {
		public void run() {
			boolean success = mergeBitmap();
			UIUtil.setMessage(handler, SHARE_IMAGE, success);
		}
	}

	public synchronized boolean mergeBitmap() {
		synchronized (lockBmps) {
			if (mScreenBmp == null || mEcgBmp == null) {
				return false;
			}
			if (mLeftBitmap == null || mRightBitmap == null
					|| mUpBitmap == null || mDownBitmap == null) {
				return false;
			}

			int bgWidth = mScreenBmp.getWidth();
			int bgHeight = mScreenBmp.getHeight();

			Bitmap newBitmap = Bitmap.createBitmap(bgWidth, bgHeight,
					Config.ARGB_8888);

			Canvas canvas = new Canvas(newBitmap);

			canvas.drawBitmap(mScreenBmp, 0, 0, null);
			mScreenBmp.recycle();
			mScreenBmp = null;

			canvas.drawBitmap(mEcgBmp, ecg_x, ecg_y, null);
			mEcgBmp.recycle();
			mEcgBmp = null;
			canvas.drawBitmap(mLeftBitmap, left_x, left_y, null);
			mLeftBitmap.recycle();
			mLeftBitmap = null;

			canvas.drawBitmap(mRightBitmap, right_x, right_y, null);
			mRightBitmap.recycle();
			mRightBitmap = null;

			canvas.drawBitmap(mUpBitmap, up_x, up_y, null);
			mUpBitmap.recycle();
			mUpBitmap = null;

			canvas.drawBitmap(mDownBitmap, down_x, down_y, null);
			mDownBitmap.recycle();
			mDownBitmap = null;

			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();

			boolean success = ScreenShotUtils.savePic(newBitmap, path_image);

			if (success) {
				// UIUtil.setMessage(handler, SHOW_CAPTURE_SUCCESS);
			}

			if (newBitmap != null) {
				newBitmap.recycle();
				newBitmap = null;
			}

			return success;
		}
	}

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

	class TiannmaHteWarning {
		private WarningHteManager warningHteManager = new WarningHteManager(
				getActivity());
		private int mHteWarning = 0;

		private void updateHteWarningData() {
			String uid = UserInfo.getIntance().getUserData().getMy_name();
			if ((uid != null) && (uid.length() > 0)) {
				mHteWarning = warningHteManager
						.getWarningCount(new Date(), uid);
			} else {
				mHteWarning = 0;
			}
		}

		public int getHteWarning() {
			updateHteWarningData();
			return mHteWarning;
		}
	}
}