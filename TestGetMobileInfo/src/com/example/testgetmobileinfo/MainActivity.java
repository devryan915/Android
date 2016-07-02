package com.example.testgetmobileinfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView listView;
	private EditText editTextX;
	private EditText editTextY;
	private TextView textViewValue;
	private Button buttonGetDpi;

	private int widthPixels;
	private int heightPixels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editTextX = (EditText) findViewById(R.id.editTextX);
		editTextY = (EditText) findViewById(R.id.editTextY);

		textViewValue = (TextView) findViewById(R.id.textViewValue);
		buttonGetDpi = (Button) findViewById(R.id.buttonGetDpi);
		buttonGetDpi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float x = Float.parseFloat(editTextX.getText().toString());
				float y = Float.parseFloat(editTextY.getText().toString());
				textViewValue.setText("BRAND:" + Build.BRAND + "\nMODEL:"
						+ Build.MODEL + "\nxDpi:" + widthPixels / (x / 25.4f)
						+ "\nyDpi:" + heightPixels / (y / 25.4f));
			}
		});

		listView = (ListView) findViewById(R.id.listView);
		List<DeviceInfo> devInfos = new ArrayList<MainActivity.DeviceInfo>();
		DeviceInfo devInfo = null;
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
		widthPixels = outMetrics.widthPixels;
		heightPixels = outMetrics.heightPixels;

		devInfo = new DeviceInfo();
		devInfo.name = "BRAND";
		devInfo.value = Build.BRAND + "";
		devInfos.add(devInfo);

		devInfo = new DeviceInfo();
		devInfo.name = "MODEL";
		devInfo.value = Build.MODEL + "";
		devInfos.add(devInfo);

		devInfo = new DeviceInfo();
		devInfo.name = "xdpi";
		devInfo.value = outMetrics.xdpi + "";

		devInfos.add(devInfo);
		devInfo = new DeviceInfo();
		devInfo.name = "ydpi";
		devInfo.value = outMetrics.ydpi + "";
		devInfos.add(devInfo);

		devInfo = new DeviceInfo();
		devInfo.name = "分辨率";
		devInfo.value = outMetrics.widthPixels + "x" + outMetrics.heightPixels;
		devInfos.add(devInfo);

		devInfo = new DeviceInfo();
		devInfo.name = "densityDpi";
		devInfo.value = outMetrics.densityDpi + "";
		devInfos.add(devInfo);

		devInfo = new DeviceInfo();
		devInfo.name = "DEVICE";
		devInfo.value = Build.DEVICE + "";
		devInfos.add(devInfo);

		devInfo = new DeviceInfo();
		devInfo.name = "PRODUCT";
		devInfo.value = Build.PRODUCT + "";
		devInfos.add(devInfo);

		StringBuilder sb = new StringBuilder();
		sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
		sb.append("\nLine1Number = " + tm.getLine1Number());
		sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\nNetworkType = " + tm.getNetworkType());
		sb.append("\nPhoneType = " + tm.getPhoneType());
		sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
		sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
		sb.append("\nSimState = " + tm.getSimState());
		sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		//
		sb.append("\n BRAND = " + Build.BRAND);
		sb.append("\n DEVICE = " + Build.DEVICE);
		sb.append("\n PRODUCT = " + Build.PRODUCT);
		sb.append("\n MODEL = " + Build.MODEL);
		Log.i("info", sb.toString());

		DeviceAdapter adapter = new DeviceAdapter(MainActivity.this, devInfos);
		listView.setAdapter(adapter);
	}

	class DeviceInfo {
		public String name;
		public String value;
	}

	class DeviceAdapter extends BaseAdapter {

		private Context ctx;
		private List<DeviceInfo> devInfos = null;

		public DeviceAdapter(Context ctx, List<DeviceInfo> devInfos) {
			this.ctx = ctx;
			this.devInfos = devInfos;
		}

		@Override
		public int getCount() {
			return this.devInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return this.devInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {
			public TextView textViewName;
			public TextView textViewValue;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(this.ctx).inflate(
						R.layout.listview_devinfo_item, null);
				holder = new ViewHolder();
				holder.textViewName = (TextView) convertView
						.findViewById(R.id.textViewName);
				holder.textViewValue = (TextView) convertView
						.findViewById(R.id.textViewValue);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textViewName.setText(devInfos.get(position).name);
			holder.textViewValue.setText(devInfos.get(position).value);
			return convertView;
		}
	}
}
