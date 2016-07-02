package com.broadchance.wdecgrec.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.broadchance.entity.BleDev;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.UploadFileResponse;
import com.broadchance.entity.UploadWay;
import com.broadchance.entity.serverentity.ResponseCode;
import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.entity.serverentity.UIDeviceResponse;
import com.broadchance.entity.serverentity.UIPixelResponse;
import com.broadchance.entity.serverentity.UpLoadData;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FileUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.NetUtil;
import com.broadchance.utils.SSXLXService;
import com.broadchance.utils.UIUtil;
import com.broadchance.utils.ZipUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.adapter.DialogBleAdapter;
import com.broadchance.wdecgrec.login.LoginActivity;
import com.broadchance.wdecgrec.services.BluetoothLeService;
import com.broadchance.wdecgrec.settings.SettingsActivity;

public class ModeActivity extends BaseActivity {
	private final static String TAG = ModeActivity.class.getSimpleName();
	/**
	 * UI
	 */
	private Button buttonWearMode;
	private Button buttonFamilyMode;
	private Button buttonSportsMode;
	private Button buttonMonitorMode;
	private Button buttonLogout;
	private Button buttonSettings;

	private Button buttonSportsModeTip;
	private Button buttonMonitorModeTip;

	private TextView modeWelcome;

	DialogBleAdapter.ViewHolder holderSel;

	/**
	 * BLE
	 */
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	/**
	 * ble设备列表设配器
	 */
	DialogBleAdapter adapter;
	/**
	 * 蓝牙设备列表对话框
	 */
	Dialog bleDialog;

	private static final int REQUEST_ENABLE_BT = 189;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 30000;
	/**
	 * 蓝牙扫描对话框
	 */
	Dialog bleScanDialog;
	/**
	 * 绑定新设备对话框
	 */
	Dialog bindDevDialog;
	/**
	 * 用新设备替换原设备对话框
	 */
	Dialog replaceDevDialog;
	/**
	 * 确认使用新设备替换原设备对话框
	 */
	Dialog reconfirmDialog;
	/**
	 * 退出确认对话框
	 */
	Dialog logoutDialog;
	/**
	 * 输入的设备密码
	 */
	private EditText editTextDevPwd;
	/**
	 * Net请求
	 */
	private final static int REQUEST_NET_CODE = 190;

	// private void sendECGData(IntBuffer buffer, Integer value, String action)
	// {
	// if ((!buffer.hasRemaining() || value == null) && buffer.position() > 0) {
	// Intent intent = new Intent();
	// intent.setAction(action);
	// int[] des = new int[buffer.position()];
	// System.arraycopy(buffer.array(), 0, des, 0, des.length);
	// buffer.position(0);
	// intent.putExtra(BluetoothLeService.EXTRA_DATA, des);
	// value = 9;
	// }
	// }

	/**
		 * 
		 */
	void test() {
		try {
			String dir = FileUtil.getEcgDir();
			File ecgFile = new File(dir);
			final File[] files = ecgFile.listFiles();
			// new ClientGameService().uploadRealTimeFile(files[1],
			// new HttpReqCallBack<UploadFileResponse>() {
			//
			// @Override
			// public Activity getReqActivity() {
			// return null;
			// }
			//
			// @Override
			// public void doSuccess(UploadFileResponse result) {
			// if (result.isOk()) {
			// showToast("实时上传成功");
			// }
			// }
			//
			// @Override
			// public void doError(String result) {
			// if (ConstantConfig.Debug) {
			// showToast(result);
			// }
			// }
			// });
			UIUserInfoLogin user = DataManager.getUserInfo();
			if (user == null) {
				LogUtil.d(TAG, "用户数据不存在");
				return;
			}
			String zipPath = FileUtil.ECG_BATCH_UPLOADDIR + "pkg.zip";
			List<File> fileArray = new ArrayList<File>();
			fileArray.add(files[1]);
			fileArray.add(files[2]);
			fileArray.add(files[3]);
			ZipUtil.zipFiles(fileArray, FileUtil.ECG_BATCH_UPLOADDIR
					+ "pkg.zip");
			File zipFile = new File(zipPath);
			if (zipFile.exists()) {
				String devID = "74:DA:EA:9F:93:11";
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss.SSS");
				List<UpLoadData> upLoadDatas = new ArrayList<UpLoadData>();
				UpLoadData data = new UpLoadData();

				data.setBeginDT(sdf.format(new Date()));
				data.setEndDT(sdf.format(new Date()));
				data.setUserID(user.getUserID());
				data.setDeviceID(devID);
				data.setFileName(files[1].getName());
				upLoadDatas.add(data);
				data = new UpLoadData();
				data.setBeginDT(sdf.format(new Date()));
				data.setEndDT(sdf.format(new Date()));
				data.setUserID(user.getUserID());
				data.setDeviceID(devID);
				data.setFileName(files[2].getName());
				upLoadDatas.add(data);
				data = new UpLoadData();
				data.setBeginDT(sdf.format(new Date()));
				data.setEndDT(sdf.format(new Date()));
				data.setUserID(user.getUserID());
				data.setDeviceID(devID);
				data.setFileName(files[3].getName());
				upLoadDatas.add(data);
				String desDataJson = JSON.toJSONString(upLoadDatas);
				/**
				 * 批量
				 */
				new ClientGameService().uploadFile(zipFile, desDataJson,
						UploadWay.Batch,
						new HttpReqCallBack<UploadFileResponse>() {

							@Override
							public Activity getReqActivity() {
								return null;
							}

							@Override
							public void doSuccess(UploadFileResponse result) {
								if (result.isOk()) {
									showToast("批量上传成功");
								}
							}

							@Override
							public void doError(String result) {
								if (ConstantConfig.Debug) {
									showToast(result);
								}
							}
						});
			}
			// String zipPath = FileUtil.ECGDIR + "pkg.zip";
			// ZipUtil.zipFiles(new File[] { files[1] }, FileUtil.ECGDIR
			// + "pkg.zip");
			// File zipFile = new File(zipPath);
			// if (zipFile.exists()) {
			// String userID = "09e95aba62f24df78c80bccf3043530b";
			// UpLoadData data = new UpLoadData();
			// data.setUserID(userID);
			// data.setDeviceID("74:DA:EA:9F:93:11");
			// StringBuffer stringBuffer = new StringBuffer();
			// FileInputStream fis = new FileInputStream(zipFile);
			// byte[] buffer = new byte[1024];
			// int readLength = -1;
			// while ((readLength = fis.read(buffer)) != -1) {
			// stringBuffer.append(new String(buffer, 0, readLength,
			// Charset.forName("UTF-8")));
			// }
			// data.setData(stringBuffer.toString());
			// data.setData("fsdfds是对方发生地方");
			// List<UpLoadData> upLoadDatas = new ArrayList<UpLoadData>();
			// upLoadDatas.add(data);
			// String zipData = JSON.toJSONString(upLoadDatas);
			// zipData = ZipUtil.compress(zipData);
			// serverService.AddRemote_Data(userID, 0, zipData,
			// new HttpReqCallBack<StringResponse>() {
			// @Override
			// public Activity getReqActivity() {
			// return ModeActivity.this;
			// }
			//
			// @Override
			// public void doSuccess(StringResponse result) {
			// if (result.isOk()) {
			// showToast("GetUserBase请求成功！");
			// } else {
			// showResponseMsg(result.Code);
			// }
			// }
			//
			// @Override
			// public void doError(String result) {
			// showToast(result);
			// }
			// });
			// }
			// int pow = (int) Math.pow(2, 6);
			// System.out.println(pow);
			// 测试随机读取
			// SimpleDateFormat sdf = new
			// SimpleDateFormat(ConstantConfig.DATA_DATE_FORMAT);
			// File ecgFile = new File(FileUtil.ECGDIR + sdf.format(new Date())
			// + ConstantConfig.ECGDATA_SUFFIX);
			// ecgFile = new File(FileUtil.ECGDIR + "test"
			// + ConstantConfig.ECGDATA_SUFFIX);
			// boolean canWrite = false;
			// if (FileUtil.getInstance().checkDir()) {
			// if (!ecgFile.exists()) {
			// canWrite = ecgFile.createNewFile();
			// } else {
			// canWrite = true;
			// }
			// if (canWrite) {
			// RandomAccessFile raf = new RandomAccessFile(ecgFile, "rw");
			// FileChannel fc = raf.getChannel();
			// MappedByteBuffer mbb = fc.map(
			// FileChannel.MapMode.READ_WRITE, 0, 10);
			// mbb.put((byte) 1);
			// mbb.put(9, (byte) 10);
			// mbb = fc.map(FileChannel.MapMode.READ_WRITE, 9, 10);
			// mbb.put(0, (byte) 11);
			// mbb.put(9, (byte) 20);
			// raf.close();
			// }
			// FileInputStream fis = new FileInputStream(ecgFile);
			// FileChannel fc = fis.getChannel();
			// ByteBuffer byteBuffer = ByteBuffer.allocate(20);
			// fc.read(byteBuffer);
			// fis.close();
			// }

			/*
			 * // 测试 // Integer value = null; // IntBuffer miiBuffer =
			 * IntBuffer.allocate(20); // miiBuffer.put(1, 1); //
			 * miiBuffer.put(2); // miiBuffer.put(3); // miiBuffer.put(4); // //
			 * sendECGData(miiBuffer, value, "ss"); // Intent intent = new
			 * Intent(); // int[] des = new int[miiBuffer.position()]; // int[]
			 * dis = new int[miiBuffer.position()]; //
			 * System.arraycopy(miiBuffer.array(), 0, des, 0, des.length); //
			 * miiBuffer.position(0); // miiBuffer.get(dis); //
			 * miiBuffer.put(5); // miiBuffer.put(6); // intent.putExtra("test",
			 * des); // int[] array = intent.getIntArrayExtra("test"); //
			 * array[2] = 9; // System.out.println(array);
			 */
			// 测试bit位
			// byte b = (byte) -255;
			// int bit1 = (b & 0x80) >> 7;
			// int bit2 = (b & 0x40) >> 6;
			// int bit3 = (b & 0x20) >> 5;
			// int bit4 = (b & 0x10) >> 4;
			// int bit5 = (b & 0x08) >> 3;
			// int bit6 = (b & 0x04) >> 2;
			// int bit7 = (b & 0x02) >> 1;
			// int bit8 = (b & 0x01) >> 0;
			// StringBuffer bitStr = new StringBuffer();
			// bitStr.append(bit1);
			// bitStr.append(bit2);
			// bitStr.append(bit3);
			// bitStr.append(bit4);
			// bitStr.append(bit5);
			// bitStr.append(bit6);
			// bitStr.append(bit7);
			// bitStr.append(bit8);
			// LogUtil.e(TAG, bitStr);
			/*
			 * 测试设备绑定替换修改密码 String userID = "09e95aba62f24df78c80bccf3043530b";
			 * String iDCode = "74:DA:EA:9F:93:20"; String password = "111111";
			 * String oldpassword = "123456";
			 * serverService.ChangeDeviceUserID(userID, iDCode, password,
			 * oldpassword, new HttpReqCallBack<StringResponse>() {
			 * 
			 * @Override public Activity getReqActivity() { return
			 * LoginActivity.this; }
			 * 
			 * @Override public void doSuccess(StringResponse result) { if
			 * (result.isIsOK()) { showToast("提交成功！"); } else {
			 * showResponseMsg(result.Code); } }
			 * 
			 * @Override public void doError(String result) { if
			 * (ConstantConfig.Debug) { showToast(result); } } });
			 */
			/*
			 * String userID = "09e95aba62f24df78c80bccf3043530b";
			 * serverService.GetUserBase(userID, new
			 * HttpReqCallBack<UIUserInfoBaseResponse>() {
			 * 
			 * @Override public Activity getReqActivity() { return
			 * LoginActivity.this; }
			 * 
			 * @Override public void doSuccess(UIUserInfoBaseResponse result) {
			 * if (result.isIsOK()) { showToast("GetUserBase请求成功！"); } else {
			 * showResponseMsg(result.Code); } }
			 * 
			 * @Override public void doError(String result) { if
			 * (ConstantConfig.Debug) { showToast(result); } } });
			 */
		} catch (Exception e) {
			LogUtil.e(TAG, e);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode);
		buttonWearMode = (Button) findViewById(R.id.buttonWearMode);
		buttonWearMode.setOnClickListener(this);
		buttonFamilyMode = (Button) findViewById(R.id.buttonFamilyMode);
		buttonFamilyMode.setOnClickListener(this);
		buttonSportsMode = (Button) findViewById(R.id.buttonSportsMode);
		buttonSportsMode.setOnClickListener(this);
		buttonMonitorMode = (Button) findViewById(R.id.buttonMonitorMode);
		buttonMonitorMode.setOnClickListener(this);
		buttonSportsModeTip = (Button) findViewById(R.id.buttonSportsModeTip);
		buttonSportsModeTip.setOnClickListener(this);
		buttonMonitorModeTip = (Button) findViewById(R.id.buttonMonitorModeTip);
		buttonMonitorModeTip.setOnClickListener(this);
		buttonLogout = (Button) findViewById(R.id.buttonLogout);
		buttonLogout.setOnClickListener(this);
		buttonSettings = (Button) findViewById(R.id.buttonSettings);
		buttonSettings.setOnClickListener(this);
		modeWelcome = (TextView) findViewById(R.id.modeWelcome);
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user != null) {
			modeWelcome.setText(user.getLoginName());
		}
		initLocation();
		initDPIConfig();
		// Button buttonTest = (Button) findViewById(R.id.buttonTest);
		// if (ConstantConfig.Debug) {
		// buttonTest.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// test();
		// }
		// });
		// } else {
		// buttonTest.setVisibility(View.GONE);
		// }
	}

	private void initDPIConfig() {
		boolean needRefresh = SettingsManager.getInstance().getDpiConfigX() <= 0
				|| SettingsManager.getInstance().getDpiConfigY() <= 0;
		needRefresh = true;
		if (needRefresh) {
			String brand = Build.BRAND;
			String model = Build.MODEL;
			serverService.GetStaticPixel(brand, model,
					new HttpReqCallBack<UIPixelResponse>() {

						@Override
						public Activity getReqActivity() {
							return null;
						}

						@Override
						public void doSuccess(UIPixelResponse result) {
							if (result.isOk()) {
								try {
									float value;
									value = Float.parseFloat(result.getData()
											.getX());
									SettingsManager.getInstance()
											.setDpiConfigX(value);
									value = Float.parseFloat(result.getData()
											.getY());
									SettingsManager.getInstance()
											.setDpiConfigY(value);
								} catch (Exception e) {
									if (ConstantConfig.Debug) {
										LogUtil.d(TAG, e);
									}
								}

							}
						}

						@Override
						public void doError(String result) {

						}
					});
		}
	}

	private void initLocation() {
		UIUserInfoLogin user = DataManager.getUserInfo();
		final String lon = SettingsManager.getInstance().getGPSLon();
		final String lat = SettingsManager.getInstance().getGPSLat();
		if (user != null && lon != null && !lon.trim().isEmpty() && lat != null
				&& !lat.trim().isEmpty()) {
			SSXLXService.getInstance().InserGPS(user.getUserID(), lon, lat,
					new HttpReqCallBack<StringResponse>() {
						@Override
						public boolean isShowLoading() {
							return false;
						}

						@Override
						public Activity getReqActivity() {
							return null;
						}

						@Override
						public void doSuccess(StringResponse result) {
							if (ConstantConfig.Debug) {
								if (result.isOk()) {
									LogUtil.i(TAG, "定位数据提交成功：\n经度：" + lon
											+ "\n纬度：" + lat);
								}
							}
						}

						@Override
						public void doError(String result) {
							if (ConstantConfig.Debug) {
								LogUtil.d(TAG, result);
							}
						}
					});
		}
	}

	// 获取广播数据
	// private class GPSReceiver extends BroadcastReceiver {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// Bundle bundle = intent.getExtras();
	// final String lon = bundle.getString("lon");
	// final String lat = bundle.getString("lat");
	// if (ConstantConfig.Debug) {
	// showToast("目前经纬度\n经度：" + lon + "\n纬度：" + lat);
	// }
	// if (lon != null && !lon.isEmpty() && lat != null && !lat.isEmpty()) {
	// UIUserInfoLogin user = DataManager.getUserInfo();
	// if (user == null) {
	// LogUtil.d(TAG, "用户数据不存在");
	// return;
	// }
	// // 定位数据暂存
	// SharedPreferences sp = getSharedPreferences();
	// Editor editor = sp.edit();
	// editor.putString("lon", lon);
	// editor.putString("lat", lat);
	// editor.commit();
	// stopGPSService();
	// serverService.InserGPS(user.getUserID(), lon, lat,
	// new HttpReqCallBack<StringResponse>() {
	//
	// @Override
	// public boolean isShowLoading() {
	// return false;
	// }
	//
	// @Override
	// public Activity getReqActivity() {
	//
	// return ModeActivity.this;
	// }
	//
	// @Override
	// public void doSuccess(StringResponse result) {
	// if (ConstantConfig.Debug) {
	// if (result.isOk()) {
	// LogUtil.i(TAG, "定位数据提交成功：\n经度：" + lon
	// + "\n纬度：" + lat);
	// }
	// }
	// }
	//
	// @Override
	// public void doError(String result) {
	// if (ConstantConfig.Debug) {
	// showToast(result);
	// }
	// }
	// });
	// }
	//
	// }
	// }

	/**
	 * 停止定位服务
	 */
	// private void stopGPSService() {
	// try {
	// stopService(new Intent(ModeActivity.this, GpsService.class));
	// } catch (Exception e) {
	// }
	// }

	/**
	 * 检查网络是否可用
	 * 
	 * @return
	 */
	private boolean checkNetAvailable() {
		boolean isNetAvailable = NetUtil.isNetAvailable();
		if (!isNetAvailable) {
			showToast("网络不可用，请开启网络");
			Intent intent = null;

			// 判断手机系统的版本 即API大于10 就是3.0或以上版本
			if (android.os.Build.VERSION.SDK_INT > 10) {
				intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
			} else {
				intent = new Intent();
				ComponentName component = new ComponentName(
						"com.android.settings",
						"com.android.settings.WirelessSettings");
				intent.setComponent(component);
				intent.setAction("android.intent.action.VIEW");
			}
			startActivityForResult(intent, REQUEST_NET_CODE);
		}
		return isNetAvailable;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT) {
			// finish();
			if (resultCode == Activity.RESULT_CANCELED) {
				showToast("请开启蓝牙");
			} else {
				showChooseBledev();
				scanLeDevice(true);
			}
			return;
		} else if (requestCode == REQUEST_NET_CODE
				&& resultCode == Activity.RESULT_CANCELED) {
			showToast("请设置网络");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		// 注销服务
		// unregisterReceiver(receiver);
		// // 结束服务，如果想让服务一直运行就注销此句
		// stopGPSService();
		super.onDestroy();
	}

	/**
	 * 选择穿戴模式
	 */
	private void wearMode() {
		mHandler = new Handler();
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			showToast(getString(R.string.ble_not_supported));
			return;
		}
		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
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
			showChooseBledev();
			scanLeDevice(true);
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			System.out.println("");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					BleDev dev = new BleDev();
					dev.setMacAddress(device.getAddress());
					adapter.addDevice(dev);
					adapter.notifyDataSetChanged();
					if (bleScanDialog != null) {
						bleScanDialog.cancel();
						bleScanDialog.dismiss();
					}
				}
			});
		}
	};

	/**
	 * 扫描蓝牙设备
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			bleScanDialog = UIUtil.showLoadingDialog(ModeActivity.this, "");
			// bleScanDialog.setCanceledOnTouchOutside(false);
			TextView dialogContent = ((TextView) bleScanDialog
					.findViewById(R.id.content));
			dialogContent.setText("正在扫描蓝牙...");
			bleScanDialog.show();
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					bleScanDialog.cancel();
					bleScanDialog.dismiss();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private void closeAllDialog() {
		if (bleDialog != null) {
			bleDialog.cancel();
			bleDialog.dismiss();
		}
		if (bleDialog != null) {
			bleDialog.cancel();
			bleDialog.dismiss();
		}
		if (bleDialog != null) {
			bleDialog.cancel();
			bleDialog.dismiss();
		}
	}

	/**
	 * 显示蓝牙设备列表
	 */
	private void showChooseBledev() {
		holderSel = null;
		LayoutInflater inflater = (LayoutInflater) ModeActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_choosebledev, null);
		ListView listViewChangeSkin = (ListView) layout
				.findViewById(R.id.listViewBle);
		adapter = new DialogBleAdapter(ModeActivity.this);
		listViewChangeSkin.setAdapter(adapter);
		listViewChangeSkin.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DialogBleAdapter.ViewHolder holder = (DialogBleAdapter.ViewHolder) view
						.getTag();
				if (holderSel != null)
					holderSel.viewSel
							.setBackgroundResource(R.drawable.changeskin_nor);
				holderSel = holder;
				holderSel.viewSel
						.setBackgroundResource(R.drawable.changeskin_sel);
			}
		});
		Button btnConectBLe = (Button) layout.findViewById(R.id.buttonSave);
		btnConectBLe.setOnClickListener(this);
		Button buttonReScan = (Button) layout.findViewById(R.id.buttonReScan);
		buttonReScan.setOnClickListener(this);
		bleDialog = UIUtil.buildDialog(ModeActivity.this, layout);
		bleDialog.show();
	}

	private void settings() {
		closeBleDialog();
		Intent intent = new Intent(ModeActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * 检查设备状态 设备是否可用，是否是新设备 检查帐号的设备列表 是否有设备，存在的设备列表中是否包括当前选择的设备
	 */
	private void checkBleDev() {
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user == null) {
			LogUtil.d(TAG, "用户数据不存在");
			return;
		}
		serverService.CheckUserDevice(user.getUserID(),
				holderSel.dev.getMacAddress(),
				new HttpReqCallBack<UIDeviceResponse>() {

					@Override
					public Activity getReqActivity() {
						return ModeActivity.this;
					}

					@Override
					public void doSuccess(UIDeviceResponse result) {
						if (result.isOk()) {
							// 检查有效期还剩一个月时，提示设备有效期至XX年XX月XX日
							goEcgActivity();
						} else {
							// 绑定新设备
							if (ResponseCode.DeviceNotEnable
									.equals(result.Code)) {
								// 绑定新设备
								LayoutInflater inflater = (LayoutInflater) ModeActivity.this
										.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								LinearLayout layout = (LinearLayout) inflater
										.inflate(R.layout.dialog_devicebind,
												null);
								editTextDevPwd = (EditText) layout
										.findViewById(R.id.editTextDevPwd);
								Button buttonReject = (Button) layout
										.findViewById(R.id.buttonReject);
								buttonReject
										.setOnClickListener(ModeActivity.this);
								Button buttonAllowed = (Button) layout
										.findViewById(R.id.buttonAllowed);
								buttonAllowed
										.setOnClickListener(ModeActivity.this);
								bindDevDialog = UIUtil.buildDialog(
										ModeActivity.this, layout);
								bindDevDialog.show();
							} else if (ResponseCode.DeviceChange
									.equals(result.Code)) {
								// 新设备替换原设备
								LayoutInflater inflater = (LayoutInflater) ModeActivity.this
										.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								LinearLayout layout = (LinearLayout) inflater
										.inflate(R.layout.dialog_replacedevice,
												null);
								editTextDevPwd = (EditText) layout
										.findViewById(R.id.editTextDevPwd);
								Button buttonCancel = (Button) layout
										.findViewById(R.id.buttonCancel);
								buttonCancel
										.setOnClickListener(ModeActivity.this);
								Button buttonReplace = (Button) layout
										.findViewById(R.id.buttonReplace);
								buttonReplace
										.setOnClickListener(ModeActivity.this);
								replaceDevDialog = UIUtil.buildDialog(
										ModeActivity.this, layout);
								replaceDevDialog.show();
							} else {
								showToast(result.getMessage());
								// showResponseMsg(result.Code);
							}
						}
					}

					@Override
					public void doError(String result) {
						if (ConstantConfig.Debug) {
							showToast(result);
						}
					}
				});
	}

	/**
	 * 关闭设备列表
	 */
	private void closeBleDialog() {
		if (bleDialog != null) {
			bleDialog.cancel();
			bleDialog.dismiss();
		}
		if (bleScanDialog != null) {
			bleScanDialog.cancel();
			bleScanDialog.dismiss();
		}
		if (bindDevDialog != null) {
			bindDevDialog.cancel();
			bindDevDialog.dismiss();
		}
		if (replaceDevDialog != null) {
			replaceDevDialog.cancel();
			replaceDevDialog.dismiss();
		}
		if (reconfirmDialog != null) {
			reconfirmDialog.cancel();
			reconfirmDialog.dismiss();
		}
		if (logoutDialog != null) {
			logoutDialog.cancel();
			logoutDialog.dismiss();
		}
	}

	/**
	 * 关闭绑定设备对话框
	 */
	private void closeBindDevDialog() {
		if (bindDevDialog != null) {
			bindDevDialog.cancel();
			bindDevDialog.dismiss();
		}
	}

	/**
	 * 关闭替换对话框
	 */
	private void closeReplaceDevDialog() {
		if (replaceDevDialog != null) {
			replaceDevDialog.cancel();
			replaceDevDialog.dismiss();
		}
	}

	/**
	 * 关闭确认对话框
	 */
	private void closeReconfirmDialog() {
		if (reconfirmDialog != null) {
			reconfirmDialog.cancel();
			reconfirmDialog.dismiss();
		}
	}

	/**
	 * 确定选择了蓝牙
	 */
	private void selectBleDev() {
		if (holderSel != null) {
			scanLeDevice(false);
			checkBleDev();
		} else {
			showToast("穿戴模式，必须选择蓝牙");
		}
	}

	/**
	 * 家属模式
	 */
	private void familyMode() {
		// if (!ConstantConfig.Debug) {
		// showToast("敬请期待");
		// if (true)
		// return;
		// }
		closeBleDialog();
		if (checkNetAvailable()) {
			Intent intent = new Intent(ModeActivity.this, EcgActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 显示功能未开启提示
	 * 
	 * @param view
	 */
	private void showViewTip(final View view) {
		view.setVisibility(View.VISIBLE);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				view.setVisibility(View.INVISIBLE);
			}
		}, 1000);
	}

	/**
	 * 退出登录
	 */
	private void logOut() {
		logoutDialog = UIUtil.buildTipDialog(ModeActivity.this,
				getString(R.string.dialog_title_logout),
				getString(R.string.dialog_tips_contentlogout),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (logoutDialog != null) {
							logoutDialog.cancel();
							logoutDialog.dismiss();
						}
						Intent intent = new Intent(ModeActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (logoutDialog != null) {
							logoutDialog.cancel();
							logoutDialog.dismiss();
						}
					}
				}, getString(R.string.dialog_button_confirm),
				getString(R.string.dialog_button_reject));
		logoutDialog.show();
	}

	/**
	 * 检查网络并跳转ECG页
	 */
	private void goEcgActivity() {
		closeBleDialog();
		SettingsManager.getInstance().setDeviceNumber(
				holderSel.dev.getMacAddress());
		if (BluetoothLeService.getInstance() != null) {
			BluetoothLeService.getInstance().connect();
		}
		if (checkNetAvailable()) {
			Intent intent = new Intent(ModeActivity.this, EcgActivity.class);
			startActivity(intent);
		}
	}

	private String checkDevPwd() {
		String devPwd = editTextDevPwd.getText().toString();
		if (devPwd.isEmpty()) {
			return null;
		}
		return devPwd;
	}

	/**
	 * 绑定设备，提交绑定设备
	 */
	private void bindNewDevice() {
		String devPwd = checkDevPwd();
		if (devPwd == null) {
			showToast("请输入设备密码！");
			return;
		}
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user == null) {
			LogUtil.d(TAG, "用户数据不存在");
			return;
		}
		String userPwd = DataManager.getUserPwd();
		String macAddress = holderSel.dev.getMacAddress();
		serverService.ChangeDeviceUserID(user.getUserID(), macAddress, userPwd,
				devPwd, new HttpReqCallBack<StringResponse>() {

					@Override
					public Activity getReqActivity() {
						return ModeActivity.this;
					}

					@Override
					public void doSuccess(StringResponse result) {
						if (result.isOk()) {
							showToast("绑定成功！");
							closeBleDialog();
							closeBindDevDialog();
							goEcgActivity();
						} else {
							showToast(result.getMessage());
							// showResponseMsg(result.Code);
						}
					}

					@Override
					public void doError(String result) {
						if (ConstantConfig.Debug) {
							showToast(result);
						}
					}
				});
	}

	/**
	 * 替换设备
	 */
	private void replaceOldDevice() {
		// 确认替换原设备
		reconfirmDialog = UIUtil.buildTipDialog(ModeActivity.this,
				getString(R.string.dialog_title_tips),
				getString(R.string.dialog_cofirmreplacedevice),
				ModeActivity.this, ModeActivity.this,
				getString(R.string.dialog_button_confirm),
				getString(R.string.dialog_button_cancel));
		reconfirmDialog.show();
	}

	/**
	 * 确认替换设备，提交服务端
	 */
	private void reConfirmReplaceDialog() {
		String devPwd = checkDevPwd();
		if (devPwd == null) {
			showToast("请输入设备密码！");
			return;
		}
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user == null) {
			LogUtil.d(TAG, "用户数据不存在");
			return;
		}
		String userPwd = DataManager.getUserPwd();
		String macAddress = holderSel.dev.getMacAddress();
		serverService.ChangeDeviceUserID(user.getUserID(), macAddress, userPwd,
				devPwd, new HttpReqCallBack<StringResponse>() {

					@Override
					public Activity getReqActivity() {
						return ModeActivity.this;
					}

					@Override
					public void doSuccess(StringResponse result) {
						if (result.isOk()) {
							showToast("替换成功！");
							closeBleDialog();
							closeReplaceDevDialog();
							closeReconfirmDialog();
							goEcgActivity();
						} else {
							showToast(result.getMessage());
							// showResponseMsg(result.Code);
						}
					}

					@Override
					public void doError(String result) {
						if (ConstantConfig.Debug) {
							showToast(result);
						}
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			logOut();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonWearMode:
			wearMode();
			break;
		case R.id.buttonFamilyMode:
			familyMode();
			break;
		case R.id.buttonSportsMode:
			showViewTip(buttonSportsModeTip);
			break;
		case R.id.buttonMonitorMode:
			showViewTip(buttonMonitorModeTip);
			break;
		case R.id.buttonLogout:
			logOut();
			break;
		case R.id.buttonReScan:
			scanLeDevice(true);
			break;
		case R.id.buttonSettings:
			settings();
			break;
		case R.id.buttonSave:
			selectBleDev();
			break;
		case R.id.buttonReject:
			closeBindDevDialog();
			break;
		case R.id.buttonAllowed:
			bindNewDevice();
			break;
		case R.id.buttonCancel:
			closeReplaceDevDialog();
			break;
		case R.id.buttonReplace:
			replaceOldDevice();
			break;
		case R.id.buttonRecCancel:
			closeReplaceDevDialog();
			closeReconfirmDialog();
			break;
		case R.id.buttonRecConfirm:
			reConfirmReplaceDialog();
			break;
		default:
			break;
		}
	}
}
