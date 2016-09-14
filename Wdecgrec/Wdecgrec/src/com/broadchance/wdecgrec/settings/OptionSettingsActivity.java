package com.broadchance.wdecgrec.settings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.broadchance.entity.BleDevInfo;
import com.broadchance.entity.SettingsOffData;
import com.broadchance.entity.UploadFile;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.FileUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.SDCardUtils;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.adapter.DialogBleDevInfoAdapter;
import com.broadchance.wdecgrec.adapter.DialogOffDataAdapter;
import com.broadchance.wdecgrec.services.BleDataParserService;
import com.broadchance.wdecgrec.services.BluetoothLeService;

public class OptionSettingsActivity extends BaseActivity implements
		OnCheckedChangeListener {
	private static final String TAG = OptionSettingsActivity.class
			.getSimpleName();
	private View viewShowDevinfo;
	private View viewShowOffData;
	private TextView textViewUploadNetType;
	private CheckBox checkBoxUpload;
	private CheckBox checkBoxLowSignal;
	private CheckBox checkBoxLowPower;
	private CheckBox checkBoxDevOff;
	private CheckBox checkBoxDevFallOff;
	private CheckBox checkBoxGPS;
	private Button buttonSave;
	DialogOffDataAdapter.ViewHolder holderOffDataSel;
	private Dialog dialogOffData = null;
	private Dialog dialogDevInfo = null;
	DialogOffDataAdapter adapterOffData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option_settings);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(this);
		findViewById(R.id.buttonTitleBack).setOnClickListener(this);
		viewShowDevinfo = findViewById(R.id.viewShowDevinfo);
		viewShowDevinfo.setOnClickListener(this);
		viewShowOffData = findViewById(R.id.viewShowOffData);
		viewShowOffData.setOnClickListener(this);
		// 上传网络类型
		textViewUploadNetType = (TextView) findViewById(R.id.textViewUploadNetType);
		checkBoxUpload = (CheckBox) findViewById(R.id.checkBoxUpload);
		boolean uploadNetType = SettingsManager.getInstance()
				.getSettingsNetType();
		checkBoxUpload.setChecked(uploadNetType);
		checkBoxUpload.setOnCheckedChangeListener(this);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1970);
		List<UploadFile> uploadFiles = DataManager.getUploadFile(cal.getTime(),
				-1);
		int count = uploadFiles != null ? uploadFiles.size() : 0;
		textViewUploadNetType.setText((uploadNetType ? "所有网络" : "WIFI") + "("
				+ count + ")");
		// 设备信号弱
		checkBoxLowSignal = (CheckBox) findViewById(R.id.checkBoxLowSignal);
		boolean lowSignal = SettingsManager.getInstance()
				.getSettingsLowSignal();
		checkBoxLowSignal.setChecked(lowSignal);
		checkBoxLowSignal.setOnCheckedChangeListener(this);
		// 设备低电量
		checkBoxLowPower = (CheckBox) findViewById(R.id.checkBoxLowPower);
		boolean lowPower = SettingsManager.getInstance().getSettingsLowPower();
		checkBoxLowPower.setChecked(lowPower);
		checkBoxLowPower.setOnCheckedChangeListener(this);
		// 设备断开提示
		checkBoxDevOff = (CheckBox) findViewById(R.id.checkBoxDevOff);
		boolean devOff = SettingsManager.getInstance().getSettingsDevOff();
		checkBoxDevOff.setChecked(devOff);
		checkBoxDevOff.setOnCheckedChangeListener(this);
		// 设备脱落提示
		checkBoxDevFallOff = (CheckBox) findViewById(R.id.checkBoxDevFallOff);
		boolean devFallOff = SettingsManager.getInstance()
				.getSettingsDevFallOff();
		checkBoxDevFallOff.setChecked(devFallOff);
		checkBoxDevFallOff.setOnCheckedChangeListener(this);
		// 是否启用GPS
		checkBoxGPS = (CheckBox) findViewById(R.id.checkBoxGPS);
		boolean gps = SettingsManager.getInstance().getSettingsGPS();
		checkBoxGPS.setChecked(gps);
		checkBoxGPS.setOnCheckedChangeListener(this);
		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getLoginName());
	}

	private void showBleDevInfo(List<BleDevInfo> devs) {
		DecimalFormat df = new DecimalFormat(".##");
		List<BleDevInfo> devInfos = new ArrayList<BleDevInfo>();
		BleDevInfo devInfo = new BleDevInfo();
		devInfo.setDevInfoName("电压");
		Float power = FrameDataMachine.getInstance().getPower();
		if (power != null) {
			devInfo.setDevInfoValue(df.format(power));
		} else {
			devInfo.setDevInfoValue("0.0");
		}
		devInfos.add(devInfo);
		devInfo = new BleDevInfo();
		devInfo.setDevInfoName("强度");
		Integer rssi = BluetoothLeService.rssiValue;
		if (rssi != null) {
			devInfo.setDevInfoValue(rssi + "");
		} else {
			devInfo.setDevInfoValue("0.0");
		}
		devInfos.add(devInfo);
		devInfo = new BleDevInfo();
		devInfo.setDevInfoName("温度");
		devInfo.setDevInfoValue(df.format(FrameDataMachine.getInstance()
				.getTemperature()));
		devInfos.add(devInfo);
		LayoutInflater inflater = (LayoutInflater) OptionSettingsActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_settingsdevinfo, null);
		TextView title = (TextView) layout.findViewById(R.id.dialog_title);
		title.setText(R.string.dialog_title_devinfo);
		ListView listView = (ListView) layout.findViewById(R.id.listView);
		DialogBleDevInfoAdapter adapter = new DialogBleDevInfoAdapter(
				OptionSettingsActivity.this, devInfos);
		listView.setAdapter(adapter);
		dialogDevInfo = UIUtil.buildDialog(OptionSettingsActivity.this, layout);
		Button buttonSave = (Button) layout.findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogDevInfo != null) {
					dialogDevInfo.cancel();
					dialogDevInfo.dismiss();
					dialogDevInfo = null;
				}
			}
		});
		dialogDevInfo.show();
	}

	private void showOffData(List<BleDevInfo> devs) {
		List<SettingsOffData> datas = new ArrayList<SettingsOffData>();
		long capacity = SettingsManager.getInstance().getSettingsOffData();

		SettingsOffData data = null;
		data = new SettingsOffData();
		data.setCapacity(SettingsManager.OFFDATA_SIZE_LOW);
		data.setDataTime("3天");
		data.setSelect(SettingsManager.OFFDATA_SIZE_LOW == capacity);
		datas.add(data);

		data = new SettingsOffData();
		data.setCapacity(SettingsManager.OFFDATA_SIZE_NORMAL);
		data.setDataTime("7天");
		data.setSelect(SettingsManager.OFFDATA_SIZE_NORMAL == capacity);
		datas.add(data);

		data = new SettingsOffData();
		data.setCapacity(SettingsManager.OFFDATA_SIZE_HIGH);
		data.setDataTime("15天");
		data.setSelect(SettingsManager.OFFDATA_SIZE_HIGH == capacity);
		datas.add(data);
		data = new SettingsOffData();
		data.setCapacity(SettingsManager.OFFDATA_SIZE_OFF);
		data.setDataTime("关闭");
		data.setSelect(SettingsManager.OFFDATA_SIZE_OFF == capacity);
		datas.add(data);
		LayoutInflater inflater = (LayoutInflater) OptionSettingsActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_settings_offdata, null);
		// TextView title = (TextView) layout.findViewById(R.id.dialog_title);
		// title.setText(R.string.dialog_title_offdata);
		ListView listView = (ListView) layout.findViewById(R.id.listView);

		adapterOffData = new DialogOffDataAdapter(OptionSettingsActivity.this,
				datas);
		listView.setAdapter(adapterOffData);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DialogOffDataAdapter.ViewHolder holder = (com.broadchance.wdecgrec.adapter.DialogOffDataAdapter.ViewHolder) view
						.getTag();
				holderOffDataSel = holder;
				adapterOffData.setSelectData(holderOffDataSel.data
						.getCapacity());
				adapterOffData.notifyDataSetChanged();
			}
		});
		dialogOffData = UIUtil.buildDialog(OptionSettingsActivity.this, layout);
		Button buttonSave = (Button) layout.findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (holderOffDataSel != null) {
					try {
						int capacity = holderOffDataSel.data.getCapacity();
						long avlSpace = SDCardUtils.getAvailableSpace();
						long usedSpace = SDCardUtils
								.getUsedSpace(FileUtil.ECG_DIR);
						avlSpace = avlSpace + usedSpace;
						// 检查容量是否超出可用容量，如果超出
						// 15天预计预留容量为1G。当容量不足1G时自动更换为7天，当容量不足500MB时自动更换为3天，当容量不足200MB时自动关闭该功能
						if (capacity > avlSpace) {
							showToast("剩余空间不足");
							return;
						}
						SettingsManager.getInstance().setSettignsOffData(
								capacity);
						showToast("保存成功");
						if (dialogOffData != null) {
							dialogOffData.cancel();
							dialogOffData.dismiss();
							dialogOffData = null;
						}
					} catch (Exception e) {
						LogUtil.e(TAG, e);
					}

				} else {
				}
			}
		});
		dialogOffData.show();
	}

	private void saveData() {
		try {
			SettingsManager.getInstance().setSettingsNetType(
					checkBoxUpload.isChecked());
			SettingsManager.getInstance().setSetttingsLowSignal(
					checkBoxLowSignal.isChecked());
			SettingsManager.getInstance().setSettingsLowPower(
					checkBoxLowPower.isChecked());
			SettingsManager.getInstance().setSettingsDevOff(
					checkBoxDevOff.isChecked());
			SettingsManager.getInstance().setSettingsDevFallOff(
					checkBoxDevFallOff.isChecked());
			SettingsManager.getInstance().setSettingsGPS(
					checkBoxGPS.isChecked());
			showToast("保存成功");
			returnSettingsAcitivity();
		} catch (Exception e) {
			LogUtil.e(TAG, e);
			showToast("保存失败");
		}
	}

	private void returnSettingsAcitivity() {
		Intent myIntent = new Intent();
		myIntent = new Intent(OptionSettingsActivity.this,
				SettingsActivity.class);
		startActivity(myIntent);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.viewShowDevinfo:
			showBleDevInfo(null);
			break;
		case R.id.viewShowOffData:
			showOffData(null);
			break;
		case R.id.buttonTitleBack:
			returnSettingsAcitivity();
			break;
		case R.id.buttonSave:
			saveData();
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged
	 * (android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkBoxUpload:
			// putPreferencesBoolean( DataManager.getUserInfo().getUserID()
			// + ConstantConfig.PREFERENCES_UPLOADNETTYPE, isChecked);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 1970);
			List<UploadFile> uploadFiles = DataManager.getUploadFile(
					cal.getTime(), -1);
			int count = uploadFiles != null ? uploadFiles.size() : 0;
			textViewUploadNetType.setText((isChecked ? "所有网络" : "WIFI") + "("
					+ count + ")");
			break;
		case R.id.checkBoxLowSignal:
			// putPreferencesBoolean( DataManager.getUserInfo().getUserID()
			// + ConstantConfig.PREFERENCES_LOWSINGNAL, isChecked);
			break;
		case R.id.checkBoxLowPower:
			// putPreferencesBoolean( DataManager.getUserInfo().getUserID()
			// + ConstantConfig.PREFERENCES_LOWPOWER, isChecked);
			break;
		case R.id.checkBoxDevOff:
			// putPreferencesBoolean( DataManager.getUserInfo().getUserID()
			// + ConstantConfig.PREFERENCES_DEVOFF, isChecked);
			break;
		case R.id.checkBoxDevFallOff:
			// putPreferencesBoolean( DataManager.getUserInfo().getUserID()
			// + ConstantConfig.PREFERENCES_DEVFALLOFF, isChecked);
			break;
		case R.id.checkBoxGPS:
			// putPreferencesBoolean( DataManager.getUserInfo().getUserID()
			// + ConstantConfig.PREFERENCES_GPS, isChecked);
			break;
		default:
			break;
		}
	}
}
