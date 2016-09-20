package com.broadchance.xiaojian;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.broadchance.xiaojian.main.LoginActivity;
import com.langlang.global.UserInfo;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class LaunchActivity extends BaseActivity {

	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;

	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 1000;
	protected static final String TAG = null;
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		NoTitle = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		mHandler = new Handler();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			// Use this check to determine whether BLE is supported on the
			// device.
			// Then you can
			// selectively disable BLE-related features.
			if (!getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				Toast.makeText(this, "不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
				finish();
			}

			// Initializes a Bluetooth adapter. For API level 18 and above, get
			// a
			// reference to
			// BluetoothAdapter through BluetoothManager.
			final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();

			// Checks if Bluetooth is supported on the device.
			if (mBluetoothAdapter == null) {
				Toast.makeText(this, "无蓝牙设备", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

				@Override
				public void onLeScan(final BluetoothDevice device, int rssi,
						byte[] scanRecord) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// String user_data =
							// "[{\"accountCode\":75,\"address\":\"\",\"age\":25,\"alarmCount\":\"\",\"alertStepList\":[{\"accountCode\":2,\"alertType\":1,\"createBy\":\"\",\"createDate\":null,\"disName\":\"心率\",\"enName\":\"heartRate\",\"floorLimit\":50,\"id\":0,\"sendAccountCode\":\"\",\"sendType\":0,\"status\":1,\"updateBy\":\"\",\"updateDate\":null,\"upperLimit\":180},{\"accountCode\":2,\"alertType\":2,\"createBy\":\"\",\"createDate\":null,\"disName\":\"精神压力\",\"enName\":\"press\",\"floorLimit\":0,\"id\":0,\"sendAccountCode\":\"\",\"sendType\":0,\"status\":0,\"updateBy\":\"\",\"updateDate\":null,\"upperLimit\":0},{\"accountCode\":2,\"alertType\":4,\"createBy\":\"\",\"createDate\":null,\"disName\":\"呼吸\",\"enName\":\"breath\",\"floorLimit\":0,\"id\":0,\"sendAccountCode\":\"\",\"sendType\":0,\"status\":0,\"updateBy\":\"\",\"updateDate\":null,\"upperLimit\":0},{\"accountCode\":2,\"alertType\":5,\"createBy\":\"\",\"createDate\":null,\"disName\":\"跌倒\",\"enName\":\"fall\",\"floorLimit\":0,\"id\":0,\"sendAccountCode\":\"\",\"sendType\":0,\"status\":1,\"updateBy\":\"\",\"updateDate\":null,\"upperLimit\":0},{\"accountCode\":2,\"alertType\":6,\"createBy\":\"\",\"createDate\":null,\"disName\":\"紧急呼救\",\"enName\":\"sos\",\"floorLimit\":0,\"id\":0,\"sendAccountCode\":\"\",\"sendType\":0,\"status\":1,\"updateBy\":\"\",\"updateDate\":null,\"upperLimit\":0}],\"applogin\":\"\",\"beginDate\":\"\",\"birthday\":null,\"breatheLimit\":\"\",\"city\":\"\",\"clinic\":\"\",\"createBy\":\"\",\"createDate\":null,\"currentCourse\":\"\",\"currentCourseBeginDate\":null,\"currentCourseEndDate\":null,\"departments\":\"\",\"doctor\":\"\",\"doctorPosition\":\"\",\"email\":\"\",\"emergencyAddress\":\"\",\"emergencyContact\":\"\",\"emergencyTel\":\"\",\"enName\":\"\",\"enabled\":0,\"endDate\":\"\",\"equipmentCode\":\"\",\"equipmentNumber\":\""
							// + device.getAddress().replace(":", "")
							// +
							// "\",\"equipmentStatus\":\"\",\"equipmentType\":\"\",\"expirationDate\":null,\"faultDate\":null,\"faultType\":\"\",\"guardian\":\"\",\"heart\":0,\"heartLimit\":\"\",\"height\":\"165\",\"hospital\":\"\",\"hospitalAddress\":\"\",\"loseEfficacyUser\":\"\",\"macTmpFloor\":\"\",\"macTmpUpper\":\"\",\"mbirthDay\":\"1989-10-07\",\"menuList\":[],\"mobile\":\"15987458569\",\"name\":\"高林明4\",\"newVersion\":\"7\",\"onlineUser\":\"\",\"organ\":\"\",\"pageSize\":0,\"param\":\"\",\"password\":\"\",\"path\":\"\",\"photo\":\"\",\"physicalAddress\":\"\",\"physicalContacts\":\"\",\"physicalName\":\"\",\"postCode\":\"\",\"provinces\":[{\"capital\":\"北京\",\"code\":0,\"id\":1,\"province\":\"北京市\"},{\"capital\":\"天津\",\"code\":0,\"id\":2,\"province\":\"天津市\"},{\"capital\":\"上海\",\"code\":0,\"id\":3,\"province\":\"上海市\"},{\"capital\":\"重庆\",\"code\":0,\"id\":4,\"province\":\"重庆市\"},{\"capital\":\"石家庄\",\"code\":0,\"id\":5,\"province\":\"河北省\"},{\"capital\":\"太原\",\"code\":0,\"id\":6,\"province\":\"陕西省\"},{\"capital\":\"沈阳\",\"code\":0,\"id\":7,\"province\":\"辽宁省\"},{\"capital\":\"长春\",\"code\":0,\"id\":8,\"province\":\"吉林省\"},{\"capital\":\"哈尔滨\",\"code\":0,\"id\":9,\"province\":\"黑龙江省\"},{\"capital\":\"南京\",\"code\":0,\"id\":10,\"province\":\"江苏省\"},{\"capital\":\"杭州\",\"code\":0,\"id\":11,\"province\":\"浙江省\"},{\"capital\":\"合肥\",\"code\":0,\"id\":12,\"province\":\"安徽省\"},{\"capital\":\"福州\",\"code\":0,\"id\":13,\"province\":\"福建省\"},{\"capital\":\"南昌\",\"code\":0,\"id\":14,\"province\":\"江西省\"},{\"capital\":\"济南\",\"code\":0,\"id\":15,\"province\":\"山东省\"},{\"capital\":\"郑州\",\"code\":0,\"id\":16,\"province\":\"河南省\"},{\"capital\":\"武汉\",\"code\":0,\"id\":17,\"province\":\"湖北省\"},{\"capital\":\"长沙\",\"code\":0,\"id\":18,\"province\":\"湖南省\"},{\"capital\":\"广州\",\"code\":0,\"id\":19,\"province\":\"广东省\"},{\"capital\":\"海口\",\"code\":0,\"id\":20,\"province\":\"海南省\"},{\"capital\":\"成都\",\"code\":0,\"id\":21,\"province\":\"四川省\"},{\"capital\":\"贵阳\",\"code\":0,\"id\":22,\"province\":\"贵州省\"},{\"capital\":\"昆明\",\"code\":0,\"id\":23,\"province\":\"云南省\"},{\"capital\":\"西安\",\"code\":0,\"id\":24,\"province\":\"陕西省\"},{\"capital\":\"兰州\",\"code\":0,\"id\":25,\"province\":\"甘肃省\"},{\"capital\":\"西宁\",\"code\":0,\"id\":26,\"province\":\"青海省\"},{\"capital\":\"台北\",\"code\":0,\"id\":27,\"province\":\"台湾省\"},{\"capital\":\"拉萨\",\"code\":0,\"id\":28,\"province\":\"西藏自治区\"},{\"capital\":\"南宁\",\"code\":0,\"id\":29,\"province\":\"广西壮族自治区\"},{\"capital\":\"呼和浩特\",\"code\":0,\"id\":30,\"province\":\"内蒙古自治区\"},{\"capital\":\"银川\",\"code\":0,\"id\":31,\"province\":\"宁夏回族自治区\"},{\"capital\":\"乌鲁木齐\",\"code\":0,\"id\":32,\"province\":\"新疆维吾尔自治区\"},{\"capital\":\"香港\",\"code\":0,\"id\":33,\"province\":\"香港\"},{\"capital\":\"澳门\",\"code\":0,\"id\":34,\"province\":\"澳门\"}],\"pubMail\":\"\",\"pubPhone1\":\"\",\"pubPhone2\":\"\",\"pubWeChat\":\"\",\"pubWebAddress\":\"\",\"registerUser\":\"\",\"relation\":\"\",\"relationShip\":\"\",\"remark\":\"\",\"roleId\":0,\"roleList\":[],\"roleName\":\"user\",\"roomNumber\":\"\",\"sensorPosition\":\"1\",\"sex\":1,\"startPage\":0,\"status\":0,\"stepstTarget\":\"\",\"subscribeCourse\":\"\",\"subscribeCourseBeginDate\":null,\"subscribeCourseEndDate\":null,\"telephoneNo\":\"\",\"tutelageList\":[],\"updateBy\":\"\",\"updateDate\":null,\"uploadDate\":null,\"urlList\":[],\"userName\":\"gaolinming4\",\"userType\":\"\",\"uuid\":\"\",\"validUser\":\"\",\"versionMark\":\"0\",\"weChat\":\"\",\"weight\":\"55\"}]";
							// UserInfo.getIntance().setUserData(user_data);
							UserInfo.getIntance().getUserData()
									.setDevice_number(device.getAddress());
							UserInfo.getIntance().getUserData()
									.setLogin_mode(1);
							Toast.makeText(LaunchActivity.this,
									"发现设备:" + device.getAddress(), 1000).show();
							toMainAcitity();
						}
					});
				}
			};

		} else {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					toMainAcitity();
				}
			}, SCAN_PERIOD);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (mBluetoothAdapter != null) {
			if (!mBluetoothAdapter.isEnabled()) {
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}
			}
			// Initializes list.
			scanLeDevice(true);
		}
	}

	private void toMainAcitity() {
		Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mBluetoothAdapter != null) {
			scanLeDevice(false);
		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					// 10s扫描不到跳转主页
					toMainAcitity();
				}
			}, SCAN_PERIOD);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

}
