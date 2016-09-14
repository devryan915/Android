/**
 * 
 */
package com.broadchance.manager;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FileUtil;
import com.broadchance.utils.SDCardUtils;

/**
 * @author ryan.wang
 * 
 */
public class SettingsManager {
	private static SettingsManager Instance;

	public static SettingsManager getInstance() {
		if (Instance == null)
			Instance = new SettingsManager();
		return Instance;
	}

	/**
	 * 离线存储第一档占空间大小200(MB)
	 */
	public final static int OFFDATA_SIZE_LOW = 200;
	/**
	 * 离线存储第二档占空间大小500(MB)
	 */
	public final static int OFFDATA_SIZE_NORMAL = 500;
	/**
	 * 离线存储第三档占空间大小1024(MB)
	 */
	public final static int OFFDATA_SIZE_HIGH = 1024;
	/**
	 * 关闭存储
	 */
	public final static int OFFDATA_SIZE_OFF = -1;

	public String getServerURL() {
		return PreferencesManager.getInstance().getString(
				ConstantConfig.PREFERENCES_SERVERURL,
				ConstantConfig.SERVER_URL_DEF);
	}

	public void setServerURL(String url) {
		ConstantConfig.SERVER_URL = url;
		PreferencesManager.getInstance().putString(
				ConstantConfig.PREFERENCES_SERVERURL, url);
	}

	public void setDpiConfigX(float value) {
		PreferencesManager.getInstance().putFloat(
				ConstantConfig.PREFERENCES_XDPI, value);
	}

	public float getDpiConfigX() {
		return PreferencesManager.getInstance().getFloat(
				ConstantConfig.PREFERENCES_XDPI);
	}

	public void setDpiConfigY(float value) {
		PreferencesManager.getInstance().putFloat(
				ConstantConfig.PREFERENCES_YDPI, value);
	}

	public float getDpiConfigY() {
		return PreferencesManager.getInstance().getFloat(
				ConstantConfig.PREFERENCES_YDPI);
	}

	public void setGPSLon(String lon) {
		PreferencesManager.getInstance().putString(
				ConstantConfig.PREFERENCES_GPSLON, lon);
	}

	public String getGPSLon() {
		return PreferencesManager.getInstance().getString(
				ConstantConfig.PREFERENCES_GPSLON);
	}

	public void setGPSLat(String lon) {
		PreferencesManager.getInstance().putString(
				ConstantConfig.PREFERENCES_GPSLAT, lon);
	}

	public String getGPSLat() {
		return PreferencesManager.getInstance().getString(
				ConstantConfig.PREFERENCES_GPSLAT);
	}

	/**
	 * 设置-选项设置-离线数据，设定存储的大小(MB)
	 * 
	 * @param dataCapacity
	 *            设定存储大小MB
	 */
	public void setSettignsOffData(int dataCapacity) {
		if (DataManager.getUserInfo() == null)
			return;
		PreferencesManager.getInstance().putInt(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_OFFDATA, dataCapacity);
	}

	public int getSettingsOffData() {
		if (DataManager.getUserInfo() == null)
			return -1;
		int capacity = PreferencesManager.getInstance().getInt(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_OFFDATA, -1);
		if (capacity == -1) {
			// 如果未设定，默认给最大值
			setSettignsOffData(OFFDATA_SIZE_HIGH);
			capacity = OFFDATA_SIZE_HIGH;
		}
		long avlSpace = SDCardUtils.getAvailableSpace();
		long usedSpace = SDCardUtils.getUsedSpace(FileUtil.ECG_DIR);
		avlSpace = avlSpace + usedSpace;
		// 检查容量是否超出可用容量，如果超出
		// 15天预计预留容量为1G。当容量不足1G时自动更换为7天，当容量不足500MB时自动更换为3天，当容量不足200MB时自动关闭该功能
		if (capacity > avlSpace) {
			switch (capacity) {
			case OFFDATA_SIZE_LOW:
				// 不满200关闭
				setSettignsOffData(OFFDATA_SIZE_OFF);
				break;
			case OFFDATA_SIZE_NORMAL:
				// 不满500但>=200降为三天
				if (avlSpace >= OFFDATA_SIZE_LOW) {
					setSettignsOffData(OFFDATA_SIZE_LOW);
				} else {
					// 不足200关闭
					setSettignsOffData(OFFDATA_SIZE_OFF);
				}
				break;
			case OFFDATA_SIZE_HIGH:
				// 不满1024但>=500降为7天
				if (avlSpace >= OFFDATA_SIZE_NORMAL) {
					setSettignsOffData(OFFDATA_SIZE_NORMAL);
				} else if (avlSpace >= OFFDATA_SIZE_LOW) {
					// 不满500但>=200降为三天
					setSettignsOffData(OFFDATA_SIZE_LOW);
				} else {
					// 不足200关闭
					setSettignsOffData(OFFDATA_SIZE_OFF);
				}
				break;
			default:
				break;
			}
			capacity = PreferencesManager.getInstance().getInt(
					DataManager.getUserInfo().getUserID()
							+ ConstantConfig.PREFERENCES_OFFDATA);
		}
		return capacity;
	}

	/**
	 * 设置-选项设置-GPS定位提示音
	 * 
	 * @param value
	 */
	public void setSettingsGPS(boolean value) {
		if (DataManager.getUserInfo() == null)
			return;
		PreferencesManager.getInstance().putBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_GPS, value);
	}

	public boolean getSettingsGPS() {
		if (DataManager.getUserInfo() == null)
			return true;
		return PreferencesManager.getInstance().getBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_GPS, true);
	}

	/**
	 * 设置-选项设置-设备脱落提示音
	 * 
	 * @param value
	 */
	public void setSettingsDevFallOff(boolean value) {
		if (DataManager.getUserInfo() == null)
			return;
		PreferencesManager.getInstance().putBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_DEVFALLOFF, value);
	}

	public boolean getSettingsDevFallOff() {
		if (DataManager.getUserInfo() == null)
			return true;
		return PreferencesManager.getInstance().getBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_DEVFALLOFF, true);
	}

	/**
	 * 设置-选项设置-设备断开提示音
	 * 
	 * @param value
	 */
	public void setSettingsDevOff(boolean value) {
		if (DataManager.getUserInfo() == null)
			return;
		PreferencesManager.getInstance().putBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_DEVOFF, value);
	}

	public boolean getSettingsDevOff() {
		if (DataManager.getUserInfo() == null)
			return true;
		return PreferencesManager.getInstance().getBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_DEVOFF, true);
	}

	/**
	 * 设置-选项设置-设备电量低提示音
	 * 
	 * @param value
	 */
	public void setSettingsLowPower(boolean value) {
		if (DataManager.getUserInfo() == null)
			return;
		PreferencesManager.getInstance().putBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_LOWPOWER, value);
	}

	public boolean getSettingsLowPower() {
		if (DataManager.getUserInfo() == null)
			return true;
		return PreferencesManager.getInstance().getBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_LOWPOWER, true);
	}

	/**
	 * 设置-选项设置-设备信号弱提示音
	 * 
	 * @param value
	 */
	public void setSetttingsLowSignal(boolean value) {
		if (DataManager.getUserInfo() == null)
			return;
		PreferencesManager.getInstance().putBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_LOWSINGNAL, value);
	}

	public boolean getSettingsLowSignal() {
		if (DataManager.getUserInfo() == null)
			return true;
		return PreferencesManager.getInstance().getBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_LOWSINGNAL, true);
	}

	/**
	 * 设置-选项设置-数据文件上传
	 * 
	 * @param value
	 */
	public void setSettingsNetType(boolean value) {
		if (DataManager.getUserInfo() == null)
			return;
		PreferencesManager.getInstance().putBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_UPLOADNETTYPE, value);
	}

	public boolean getSettingsNetType() {
		// 默认选中
		if (DataManager.getUserInfo() == null) {
			return true;
		}
		return PreferencesManager.getInstance().getBoolean(
				DataManager.getUserInfo().getUserID()
						+ ConstantConfig.PREFERENCES_UPLOADNETTYPE, true);
	}

	/**
	 * 读取本地存储的设备号信息
	 * 
	 * @return
	 */
	// public String getDeviceNumber() {
	// UIUserInfoLogin user = DataManager.getUserInfo();
	// if (user != null) {
	// String deviceNumber = PreferencesManager.getInstance()
	// .getString(
	// user.getUserID()
	// + ConstantConfig.PREFERENCES_DEVIVCENUMBER);
	// return deviceNumber;
	// }
	// return "";
	// }

	/**
	 * 保存设备号信息
	 * 
	 * @param mac
	 */
	// public void setDeviceNumber(String mac) {
	// UIUserInfoLogin user = DataManager.getUserInfo();
	// if (user != null) {
	// PreferencesManager.getInstance()
	// .putString(
	// user.getUserID()
	// + ConstantConfig.PREFERENCES_DEVIVCENUMBER,
	// mac);
	// }
	// }

	/**
	 * 获取用户名
	 * 
	 * @return
	 */
	// public String getUseName() {
	// String deviceNumber = PreferencesManager.getInstance().getString(
	// ConstantConfig.PREFERENCES_USERNAME);
	// return deviceNumber;
	// }
}
