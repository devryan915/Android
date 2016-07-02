package com.broadchance.xiaojian.person.ecg;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.broadchance.xiaojian.BaseFragment;
import com.broadchance.xiaojian.CallBack;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.service.BleDataDomainService;
import com.broadchance.xiaojian.utils.CommonUtil;
import com.broadchance.xiaojian.utils.HttpAsyncTask;
import com.broadchance.xiaojian.utils.HttpAsyncTask.HttpReqCallBack;
import com.langlang.activity.EcgPainterBase.ECGGLSurfaceView;
import com.langlang.dialog.ECGProgressDialog;
import com.langlang.global.UserInfo;
import com.langlang.utils.Program;
import com.langlang.utils.ScreenShotUtils;
import com.ryan.calendar.CalendarWeek;

public class ECGHistoryDateFragment extends BaseFragment {
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
	private final static int UPDATE_MENTAL_STRESS_DETAIL = 6;
	private final static int SHOW_CAPTURE_SUCCESS = 51;
	private final static int SHOW_IS_PAITING_STOPPED = 52;
	private final static int SHARE_IMAGE = 50;
	private Queue<Integer> queue = new LinkedList<Integer>();
	private int pointNumber = 0;
	private static final int MAX_POINT = 750;
	private static final String TAG = ECGHistoryDateFragment.class
			.getSimpleName();
	private float[] ECGData = new float[MAX_POINT];
	private String path_image = Program.getSDLangLangImagePath()
			+ "/heart_image.png";

	// private TextView tv_heartRate;
	// private TextView tv_hteWarning;
	private Dialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.fragment_ecghistorydate, null);
		// tv_heartRate = (TextView) rootView.findViewById(R.id.tv_heartRate);
		// tv_hteWarning = (TextView) rootView.findViewById(R.id.tv_hteWarning);
		mSurfaceView = (ECGGLSurfaceView) rootView
				.findViewById(R.id.hte_GLSurfaceView_ecg);
		mSurfaceView.setCallback(new ECGGLSurfaceView.CanvasReadyCallback() {
			@Override
			public void notifyCanvasReady() {
				isECGCanvasInitialized = true;
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
				} else {
					stopPainting = true;
				}
			}
		});
		left_tw = (TextView) rootView.findViewById(R.id.hte_left_tw);
		up_tw = (TextView) rootView.findViewById(R.id.hte_up_tw);
		right_tw = (TextView) rootView.findViewById(R.id.hte_right_tw);
		down_tw = (TextView) rootView.findViewById(R.id.hte_down_tw);
		Bundle bundle = getArguments();
		if (bundle != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(bundle
					.getLong(BaseFragment.Fragment_Params_Date.toString()));
			((CalendarWeek) rootView.findViewById(R.id.calendarWeek))
					.setCurDate(cal);
			HashMap<String, Object> propertys = new HashMap<String, Object>();
			propertys.put("deviceno", UserInfo.getIntance().getUserData()
					.getDevice_number());
			propertys.put("datatype", BleDataDomainService.DataType_ECG);
			propertys.put("datatime",
					new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
							.format(bundle
									.getLong(BaseFragment.Fragment_Params_Date
											.toString())));
			HttpAsyncTask.fetchData("QueryData", propertys,
					new HttpReqCallBack() {
						@Override
						public void deal(SoapObject result) {
							if (result != null) {
								// Toast.makeText(getActivity(),
								// result.toString(), 1000).show();
								String ecgString = result
										.getPropertyAsString(0);
								final JSONArray ecgDatas;
								try {
									ecgDatas = new JSONArray(ecgString);
									for (int i = 0; i < ecgDatas.length(); i++) {
										final String data = ecgDatas
												.getJSONObject(i).getString(
														"data");
										new Handler().postDelayed(
												new Runnable() {
													@Override
													public void run() {
														updateCanvas(CommonUtil
																.String2IntArray(data));
													}
												}, 400 * i);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}, new Object[] { getActivity(), "正在查询数据" });
		}
		rootView.findViewById(R.id.personalhealth_ecg_periodplayback)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog = CommonUtil.showPeriodDialog(getActivity(),
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
		return rootView;
	}

	private void updateCanvas(int[] queueData) {
		if (queueData == null)
			return;
		float[] fVal = new float[10];
		if ((queueData != null) && (queueData.length >= 10)) {
			for (int i = 0; i < 10; i++) {
				fVal[i] = ((queueData[i] - 32000) * 6) / 1000f;
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
	}

	@Override
	public void onResume() {
		super.onResume();
		mSurfaceView.reset();
		isECGCanvasInitialized = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e(TAG, "unregisterReceiver");
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
		new MergeBitmapThread().start();
	}

	class MergeBitmapThread extends Thread {
		public void run() {
			boolean success = mergeBitmap();
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

}