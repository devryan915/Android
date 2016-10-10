package com.broadchance.wdecgrec.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.broadchance.entity.UserInfo;
import com.broadchance.manager.AppApplication;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FilterUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.services.BleDomainService;
import com.broadchance.wdecgrec.services.GuardService;
import com.broadchance.wdecgrec.settings.SettingsActivity;

public class ModeActivity extends BaseActivity {
	private final static String TAG = ModeActivity.class.getSimpleName();
	public static ModeActivity Instance;
	private static final int REQUEST_ENABLE_BT = 189;
	private TextView textViewHeart;
	private ScheduledExecutorService executor;
	int hearRate = 0;
	private Handler handlerTime = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// if (GuardService.Instance != null)
			// GuardService.Instance.getHeartRate(mMesg);
			if (msg.what == BleDomainService.MSG_SET_HEART) {
				String heartStr = "-";
				// hearRate = msg.getData().getInt("heart");
				hearRate = FilterUtil.Instance.getHeartRate();
				if (hearRate >= ConstantConfig.Alert_HR_Down
						&& hearRate <= ConstantConfig.Alert_HR_Up) {
					heartStr = hearRate + "";
				}
				textViewHeart.setText(heartStr + "次/分");
			}
		}
	};
	private Messenger mMesg = new Messenger(handlerTime);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_mode);
		((TextView) findViewById(R.id.tvVersion)).setText("当前版本号：V"
				+ AppApplication.curVer);
		textViewHeart = (TextView) findViewById(R.id.textViewHeart);
		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getNickName());
		Instance = this;
		startBleService();
		ConstantConfig.Debug = SettingsManager.getInstance().getFactory();
	}

	@Override
	protected void onStart() {
		// if (GuardService.Instance != null) {
		// GuardService.Instance.bindBleService();
		// }
		LogUtil.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startExecutor();
	}

	@Override
	protected void onPause() {
		super.onPause();
		endExecutor();
	}

	/**
	 * 启动ble服务
	 */
	private void startBleService() {
		Intent bleServiceintent = new Intent(ModeActivity.this,
				GuardService.class);
		startService(bleServiceintent);
	}

	private void stopBleService() {
		Intent bleServiceintent = new Intent(ModeActivity.this,
				GuardService.class);
		stopService(bleServiceintent);
	}

	// private BluetoothAdapter.LeScanCallback mLeScanCallback = new
	// BluetoothAdapter.LeScanCallback() {
	// @Override
	// public void onLeScan(final BluetoothDevice device, int rssi,
	// byte[] scanRecord) {
	// UIUserInfoLogin user = DataManager.getUserInfo();
	// if (user != null) {
	// String deviceNumber = user.getMacAddress();
	// if (deviceNumber.equals(device.getAddress())) {
	// if (ConstantConfig.Debug) {
	// UIUtil.showToast(ModeActivity.this, "扫描到指定蓝牙");
	// }
	// }
	// }
	// }
	// };
	// private static final long SCAN_PERIOD = 3000;
	// private boolean mScanning;
	// private Handler mHandler = new Handler();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager mBluetoothManager;

	/**
	 * 扫描蓝牙设备
	 * 
	 * @param enable
	 */
	// public void scanLeDevice() {
	// // BluetoothManager.
	// if (mBluetoothManager == null) {
	// mBluetoothManager = (BluetoothManager)
	// getSystemService(Context.BLUETOOTH_SERVICE);
	// if (mBluetoothManager == null) {
	// return;
	// }
	// }
	// mBluetoothAdapter = mBluetoothManager.getAdapter();
	// if (mBluetoothAdapter == null) {
	// return;
	// }
	// if (!mScanning) {
	// // Stops scanning after a pre-defined scan period.
	// mHandler.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// LogUtil.i(TAG, " stop scanLeDevice ");
	// mScanning = false;
	// mBluetoothAdapter.stopLeScan(mLeScanCallback);
	// if (ConstantConfig.Debug) {
	// UIUtil.showToast(ModeActivity.this, "扫描超时");
	// }
	// }
	// }, SCAN_PERIOD);
	// mScanning = true;
	// mBluetoothAdapter.startLeScan(mLeScanCallback);
	// } else {
	// mScanning = false;
	// mBluetoothAdapter.stopLeScan(mLeScanCallback);
	// }
	// }

	private void startExecutor() {
		executor = Executors.newScheduledThreadPool(3);
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				UIUtil.setMessage(handlerTime, 0);
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
		// scanLeDevice();
	}

	private void endExecutor() {
		if (executor != null) {
			executor.shutdown();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT) {
			// finish();
			if (resultCode == Activity.RESULT_CANCELED) {
				showToast("请开启蓝牙");
			} else {
				goEcgActivity();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		// 注销服务
		// unregisterReceiver(receiver);
		// // 结束服务，如果想让服务一直运行就注销此句
		// stopGPSService();
		// if (GuardService.Instance != null) {
		// GuardService.Instance.unBindBLeService();
		// }
		Instance = null;
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();
	}

	/**
	 * 选择穿戴模式
	 */
	public void wearMode(View view) {

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			showToast(getString(R.string.ble_not_supported));
			return;
		}
		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			showToast(getString(R.string.ble_not_supported));
			return;
		}
		// 检查蓝牙是否开启
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			goEcgActivity();
		}
	}

	public void settings(View view) {
		Intent intent = new Intent(ModeActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * 检查网络并跳转ECG页
	 */
	private void goEcgActivity() {
		UserInfo user = DataManager.getUserInfo();
		if (user.getMacAddress() != null
				&& !user.getMacAddress().trim().isEmpty()) {
			Intent intent = new Intent(ModeActivity.this, EcgActivity.class);
			// Intent intent = new Intent(ModeActivity.this,
			// DeviceScanActivity.class);
			startActivity(intent);
		} else {
			// if (user.isOverTime == 1) {
			// showToast("设备过期请重新选择设备");
			// } else {
			showToast("请绑定新设备");
			// }
			return;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
