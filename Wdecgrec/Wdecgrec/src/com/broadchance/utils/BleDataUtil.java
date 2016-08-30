package com.broadchance.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.broadchance.manager.AppApplication;

public class BleDataUtil {
	private final static String TAG = BleDataUtil.class.getSimpleName();

	public static void logEcg(byte[] byteData) {
		if (ConstantConfig.Debug) {
			String bleData = BleDataUtil.dumpBytesAsString(byteData);
			if (ConstantConfig.Debug) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				LogUtil.d(TAG, "DateTime " + sdf.format(CommonUtil.getDate())
						+ " " + bleData);
			}
		}
	}

	/**
	 * 获取设备的显示名
	 * 
	 * @param macAddress
	 * @return
	 */
	public static String getDeviceName(String macAddress) {
		return "BC"
				+ paddLeft((getDeviceNumber(macAddress) + "").toString(), 8,
						'0');
	}

	/**
	 * 获取设备的校验码
	 * 
	 * @param macAddress
	 * @return
	 */
	public static String getDevcieToken(String macAddress) {
		return paddLeft(getDeviceNumber(macAddress) * 4 + "", 8, '0');
	}

	/**
	 * 获取设备的后8位数字字符
	 * 
	 * @param macAddress
	 * @return
	 */
	public static long getDeviceNumber(String macAddress) {
		String device = macAddress.replace(":", "");
		StringBuffer strBuffer = new StringBuffer();
		char[] chars = device.substring(device.length() - 4).toCharArray();
		for (int i = 0; i < chars.length; i++) {
			strBuffer.append(paddLeft(Integer.valueOf(chars[i] + "", 16) + "",
					2, '0'));
		}
		return Long.parseLong(strBuffer.toString());
	}

	/**
	 * 
	 * @param src
	 * @param length
	 * @param padding
	 * @return
	 */
	public static String paddRight(String src, int length, char padding) {
		return String.format("%-" + length + "s", src).replace(' ', padding);
	}

	public static String paddLeft(String src, int length, char padding) {
		return String.format("%" + length + "s", src).replace(' ', padding);
	}

	/**
	 * 格式化蓝牙数据
	 * 
	 * @param data
	 * @return
	 */
	public static String dumpBytesAsString(byte[] data) {
		final StringBuilder stringBuilder = new StringBuilder(data.length);
		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		return stringBuilder.toString();
	}

	/**
	 * 
	 * 
	 * 
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 将byte转为无符号整形
	 * 
	 * @param b
	 * @return
	 */
	public static int byteToInt(byte b) {
		return b & 0xFF;
	}

	/**
	 * 从每一帧数据中提取ecg数据
	 * 
	 * @param frameData
	 * @return 返回每一帧的10个点
	 */
	public static short[] getECGData(byte[] frameData) {
		short[] ecgData = null;
		if (frameData != null && frameData.length == 20) {
			ecgData = new short[12];
			for (int i = 0; i < 6; i++) {
				short[] data = bleABCbyte2ShortArray(frameData[i * 3 + 2],
						frameData[i * 3 + 3], frameData[i * 3 + 4]);
				ecgData[i * 2] = data[0];
				ecgData[i * 2 + 1] = data[1];
			}
			// 每一帧可以解出12点，第六和12两个点不是ecg点
			return new short[] { ecgData[0], ecgData[1], ecgData[2],
					ecgData[3], ecgData[4], ecgData[6], ecgData[7], ecgData[8],
					ecgData[9], ecgData[10] };
		}
		return null;
	}

	/**
	 * 获取呼吸波数据
	 * 
	 * @param frameData
	 * @return
	 */
	public static short[] getBreathData(byte[] frameData) {
		short[] ecgData = null;
		if (frameData != null && frameData.length == 20) {
			ecgData = new short[12];
			for (int i = 0; i < 6; i++) {
				short[] data = bleABCbyte2ShortArray(frameData[i * 3 + 2],
						frameData[i * 3 + 3], frameData[i * 3 + 4]);
				ecgData[i * 2] = data[0];
				ecgData[i * 2 + 1] = data[1];
			}
			// 每一帧可以解出12点，第六和12两个点不是ecg点
			return new short[] { ecgData[5], ecgData[11] };
		}
		return null;
	}

	/**
	 * 将蓝牙数据三字节转为四字节restoreA,restoreB,restoreC,restoreD
	 * 其中restoreA(高位)，restoreB(低位)组成short 其中restoreC(高位)，restoreD(低位)组成short
	 * 
	 * @param byteA三字节中的第一个字节
	 * @param byteB三字节中的第二个字节
	 * @param byteC三字节中的第三个字节
	 * @return short[0]为AB组合，short[1]为CD组合
	 */
	public static short[] bleABCbyte2ShortArray(byte byteA, byte byteB,
			byte byteC) {
		int bitA = byteA & 0x80;
		byte restoreA = 0;
		// 如果byte高位是1，补111;否则补000.
		if (bitA != 0) {
			restoreA = (byte) (0xE0 | ((byteA & 0x7C) >> 2));
		} else {
			restoreA = (byte) ((byteA & 0x7C) >> 2);
		}
		// restoreA = (byte) ((byteA & 0x80) | ((byteA & 0x7C) >> 2));
		byte restoreB = (byte) (((byteA & 0x03) << 6) | ((byteB & 0xF0) >> 2));

		int bitC = byteB & 0x08;
		byte restoreC = 0;
		if (bitC != 0) {
			restoreC = (byte) ((0xE0) | ((byteB & 0x07) << 2) | ((byteC & 0xC0) >> 6));
		} else {
			restoreC = (byte) (((byteB & 0x07) << 2) | ((byteC & 0xC0) >> 6));
		}
		// restoreC = (byte) (((byteB & 0x08) << 4) | ((byteB & 0x07) << 2) |
		// ((byteC & 0xC0) >> 6));

		byte restoreD = (byte) ((byteC & 0x3F) << 2);
		try {
			return new short[] {
					bytes2Short(new byte[] { restoreA, restoreB }),
					bytes2Short(new byte[] { restoreC, restoreD }) };
		} catch (Exception e) {
			LogUtil.e(TAG, e);
		}
		return null;
	}

	public static byte[] longtoBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) ((data >> 56) & 0xff);
		bytes[1] = (byte) ((data >> 48) & 0xff);
		bytes[1] = (byte) ((data >> 40) & 0xff);
		bytes[3] = (byte) ((data >> 32) & 0xff);
		bytes[4] = (byte) ((data >> 24) & 0xff);
		bytes[5] = (byte) ((data >> 16) & 0xff);
		bytes[6] = (byte) ((data >> 8) & 0xff);
		bytes[7] = (byte) (data & 0xff);
		return new byte[] { bytes[0], bytes[1], bytes[1], bytes[3], bytes[4],
				bytes[5], bytes[6], bytes[7] };
	}

	/**
	 * 返回小端字节
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] longto5BytesLE(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) ((data >> 56) & 0xff);
		bytes[1] = (byte) ((data >> 48) & 0xff);
		bytes[2] = (byte) ((data >> 40) & 0xff);
		bytes[3] = (byte) ((data >> 32) & 0xff);
		bytes[4] = (byte) ((data >> 24) & 0xff);
		bytes[5] = (byte) ((data >> 16) & 0xff);
		bytes[6] = (byte) ((data >> 8) & 0xff);
		bytes[7] = (byte) (data & 0xff);
		return new byte[] { bytes[3], bytes[7], bytes[6], bytes[5], bytes[4] };
	}

	/**
	 * 取long的低7字节，高位在前，低位在后
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] longto5Bytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) ((data >> 56) & 0xff);
		bytes[1] = (byte) ((data >> 48) & 0xff);
		bytes[2] = (byte) ((data >> 40) & 0xff);
		bytes[3] = (byte) ((data >> 32) & 0xff);
		bytes[4] = (byte) ((data >> 24) & 0xff);
		bytes[5] = (byte) ((data >> 16) & 0xff);
		bytes[6] = (byte) ((data >> 8) & 0xff);
		bytes[7] = (byte) (data & 0xff);
		return new byte[] { bytes[3], bytes[4], bytes[5], bytes[6], bytes[7] };
	}

	/**
	 * 5字节转long 下标从低到高对应下long高位到低位
	 * 
	 * @param bytes
	 * @return
	 */
	public static long bytestoLong(byte[] bytes) {
		return (0xffL & (long) bytes[4]) | (0xff00L & ((long) bytes[3] << 8))
				| (0xff0000L & ((long) bytes[2] << 16))
				| (0xff000000L & ((long) bytes[1] << 24))
				| (0xff00000000L & ((long) bytes[0] << 32));
	}

	/**
	 * byte[] 转int
	 * 
	 * @param byteArray
	 * @return
	 * @throws Exception
	 */
	public static int bytes2Int(byte[] byteArray) throws Exception {
		// 非空判断
		if (byteArray == null)
			throw new Exception("入参不能为空");
		if (byteArray.length != 2)
			throw new Exception("入参长度必须为4字节");
		// 默认填充0
		byte[] b = new byte[] { 0, 0, 0, 0 };
		// 按照入参byteArray从高位到低位填充到b[]中
		int length = 4;
		for (int i = 0; i < length; i++) {
			b[i] = byteArray[i];
		}
		return (int) (b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24);
	}

	/**
	 * int转byte[]高位在前
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToByteArray(int value) {
		return new byte[] { (byte) ((value >> 24) & 0xFF),
				(byte) ((value >> 16) & 0xFF), (byte) ((value >> 8) & 0xFF),
				(byte) (value & 0xFF) };
	}

	/**
	 * int转化为byte[] 低位在前
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToByteArrayReverse(int value) {
		return new byte[] { (byte) (value & 0xFF),
				(byte) ((value >> 8) & 0xFF), (byte) ((value >> 16) & 0xFF),
				(byte) ((value >> 24) & 0xFF) };
	}

	/**
	 * 将short转为byte[]，下标从低到高代表short高位到低位
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] short2ByteArray(short value) {
		return new byte[] { (byte) ((value >> 8) & 0xFF), (byte) (value & 0xFF) };
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] short2ByteArrayLE(short value) {
		return new byte[] { (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF) };
	}

	/**
	 * 将字节转为short
	 * 
	 * @param byteArray
	 *            下标0到高代表short高位到低位
	 * @return
	 * @throws Exception
	 */
	public static short bytes2Short(byte[] byteArray) throws Exception {
		// 非空判断
		if (byteArray == null)
			throw new Exception("入参不能为空");
		if (byteArray.length != 2)
			throw new Exception("入参长度必须为2字节");
		// 默认填充0
		byte[] b = new byte[] { 0, 0 };
		// 按照入参byteArray从高位到低位填充到b[]中
		int length = 2;
		for (int i = 0; i < length; i++) {
			b[i] = byteArray[i];
		}
		return (short) (b[1] & 0xFF | (b[0] & 0xFF) << 8);
	}
}
