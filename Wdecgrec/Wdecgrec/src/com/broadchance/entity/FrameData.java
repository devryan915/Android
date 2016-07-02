package com.broadchance.entity;

import java.util.Date;

import com.broadchance.manager.FrameDataMachine;
import com.broadchance.utils.BleDataUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;

public class FrameData {
	private final static String TAG = FrameData.class.getSimpleName();
	private byte[] frameData;
	private FrameType frameType = null;
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
	public Date date;

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
	public FrameData(byte[] frameData, Date date) throws Exception {
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
			return BleDataUtil.getECGData(frameData);
		} else {
			throw new Exception("请先调用parseData");
		}
	}

	/**
	 * 将数据置0，保留帧类型
	 */
	private void resetData() {
		for (int i = 2; i < frameData.length; i++) {
			frameData[i] = 0;
		}
	}

	public void parseData() {
		try {
			// int frameHead = BleDataUtil.byteToInt(frameData[0]);
			// 获取帧类型
			String frameTypeHex = String.format("%02X ", frameData[0])
					.toUpperCase().trim();
			String action = "";
			boolean isECG = true;
			if (frameTypeHex.startsWith("8")) {// 第一通道帧类型0x8x
				frameType = FrameType.MII;
				action = FrameDataMachine.ACTION_ECGMII_DATAOFF_AVAILABLE;
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
				if (!frameTypeHex.endsWith("0")) {// 电极脱落/数据发生错误置为0
					resetData();
					UIUtil.sendBroadcast(action);
				}
			}

		} catch (Exception e) {
			LogUtil.e(TAG, e);
		} finally {
			isParsedData = true;
		}
	}
}