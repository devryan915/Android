package com.broadchance.entity;

import java.util.Date;

import org.json.JSONObject;

import android.content.Intent;

import com.broadchance.manager.DataManager;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.utils.BleDataUtil;
import com.broadchance.utils.CommonUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.alert.AlertMachine;
import com.broadchance.wdecgrec.alert.AlertType;

/**
 * ble一帧的数据
 * 
 * @author ryan.wang
 * 
 */
public class FrameData {
	private final static String TAG = FrameData.class.getSimpleName();
	private byte[] frameData;
	public final static byte[] BLANK_FRAME = new byte[] { 0x0, 0x0,
			(byte) 0x80, 0x8, 0x0, (byte) 0x80, 0x8, 0x0, (byte) 0x80, 0x8,
			0x0, (byte) 0x80, 0x8, 0x0, (byte) 0x80, 0x8, 0x0, (byte) 0x80,
			0x8, 0x0 };
	private FrameType frameType = null;
	private FrameStatus frameStatus = FrameStatus.NORMAL;
	private int seq;
	private boolean isParsedData = false;
	// private boolean isFallOff=false;
	//
	// public boolean isFallOff() {
	// return isFallOff;
	// }

	/**
	 * 电量帧的电压值
	 */
	private float power;
	/**
	 * 体表温度
	 */
	private float temperature = 0;
	/**
	 * 蓝牙收到的时间
	 */
	public long date;

	public float getTemperature() {
		return temperature;
	}

	public float getPower() {
		return power;
	}

	public byte[] getFrameData() {
		return frameData;
	}

	public void setFrameData(byte[] frameData) {
		this.frameData = frameData;
	}

	/**
	 * 数据帧在flash中的序号
	 * 
	 * @throws Exception
	 */
	// private int flashPosition;
	public FrameData(byte[] frameData, Long date) throws Exception {
		if (frameData == null)
			throw new Exception("FrameData data不能为空");
		if (frameData.length != 20)
			throw new Exception("FrameData data数据长度非法：" + frameData.length);
		if (date == null)
			throw new Exception("FrameData date不能为空");
		this.frameData = frameData;
		this.date = date;
		// parseData();
	}

	public FrameType getFrameType() throws Exception {
		if (isParsedData) {
			return frameType;
		} else {
			throw new Exception("请先调用parseData");
		}
	}

	public int getSeq() throws Exception {
		if (isParsedData) {
			return seq;
		} else {
			throw new Exception("请先调用parseData");
		}
	}

	public short[] getFramePoints() throws Exception {
		if (isParsedData) {
			if (frameStatus == FrameStatus.NORMAL) {
				return BleDataUtil.getECGData(frameData);
			} else {
				return new short[] { (short) 0x8000, (short) 0x8000,
						(short) 0x8000, (short) 0x8000, (short) 0x8000,
						(short) 0x8000, (short) 0x8000, (short) 0x8000,
						(short) 0x8000, (short) 0x8000 };
			}
		} else {
			throw new Exception("请先调用parseData");
		}
	}

	public short[] getBreathPoints() throws Exception {
		if (isParsedData) {
			return BleDataUtil.getBreathData(frameData);
		} else {
			throw new Exception("请先调用parseData");
		}
	}

	/**
	 * 将数据置0，保留帧类型
	 */
	// private void resetData() {
	// for (int i = 2; i < frameData.length; i++) {
	// frameData[i] = BLANK_FRAME[i];
	// }
	// }

	public FrameStatus getFrameStatus() {
		return frameStatus;
	}

	public void setFrameStatus(FrameStatus frameStatus) {
		this.frameStatus = frameStatus;
	}

	/**
	 * 上一次MII通道是否发生脱落
	 */
	// private boolean lastMIIOff = false;

	public void parseData() {
		try {
			// int frameHead = BleDataUtil.byteToInt(frameData[0]);
			// 获取帧类型
			String frameTypeHex = String.format("%02X ", frameData[0])
					.toUpperCase().trim();
			String action = "";
			// Intent intent = null;
			boolean isECG = true;
			if (frameTypeHex.startsWith("8")) {// 第一通道帧类型0x8x
				frameType = FrameType.MII;
				action = FrameDataMachine.ACTION_ECGMII_DATAOFF_AVAILABLE;
				if (frameTypeHex.endsWith("0")) {
					// 取消脱落
					if (AlertMachine.getInstance().canSendAlert(
							AlertType.A00001, 0)) {
						AlertMachine.getInstance()
								.cancelAlert(AlertType.A00001);
					}
					// UIUtil.sendBroadcast(new Intent(
					// FrameDataMachine.ACTION_ECGMII_DATAON_AVAILABLE));
				} else {
					// 如果电极脱落则取消过速过缓和停博预警
					AlertMachine.getInstance().cancelAlert(AlertType.B00001);
					AlertMachine.getInstance().cancelAlert(AlertType.B00002);
					AlertMachine.getInstance().cancelAlert(AlertType.B00003);
					if (AlertMachine.getInstance().canSendAlert(
							AlertType.A00001, 1)) {
						// 发生脱落
						// UIUserInfoLogin user = DataManager.getUserInfo();
						if (!DataManager.isLogin())
							return;
						JSONObject alertObj = new JSONObject();
						alertObj.put("id", AlertType.A00001.getValue());
						alertObj.put("state", 1);
						alertObj.put("time", CommonUtil.getTime_B());
						JSONObject value = new JSONObject();
						value.put("bledevice", DataManager.getUserInfo()
								.getMacAddress());
						value.put("ch", "all");
						alertObj.put("value", value);
						AlertMachine.getInstance().sendAlert(AlertType.A00001,
								alertObj);
					}
				}
			} else if (frameTypeHex.startsWith("9")) {// 第二通道帧类型0x9x
				frameType = FrameType.MV1;
				action = FrameDataMachine.ACTION_ECGMV1_DATAOFF_AVAILABLE;
			} else if (frameTypeHex.startsWith("A")) {// 第三通道帧类型0x10x
				frameType = FrameType.MV5;
				action = FrameDataMachine.ACTION_ECGMV5_DATAOFF_AVAILABLE;
			} else {// 错帧或者非ECG帧
				if (frameTypeHex.startsWith("F")) {
					// 电量信号，读取17字节
					power = BleDataUtil.byteToInt(frameData[17]) / 50f;
					temperature = BleDataUtil.byteToInt(frameData[16]) / 5.0f;
					frameType = FrameType.POWER;
				}
				isECG = false;
			}
			if (isECG) {
				seq = BleDataUtil.byteToInt(frameData[1]);
				if (!frameTypeHex.endsWith("0")) {// 电极脱落/数据发生错误置不为0
					// 取消置零
					// resetData();
					frameStatus = FrameStatus.WRONG;
					UIUtil.sendBroadcast(new Intent(action));
				}
			}

		} catch (Exception e) {
			LogUtil.e(TAG, e);
		} finally {
			isParsedData = true;
		}
	}
}