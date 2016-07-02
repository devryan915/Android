/**
 * 
 */
package com.broadchance.wdecgrec.test;

import com.broadchance.utils.LogUtil;

/**
 * @author ryan.wang
 * 
 */
public class Test {
	private static final String TAG = Test.class.getSimpleName();

	public static void testBit() {
		// 测试bit位
		byte b = (byte) -255;
		int bit1 = (b & 0x80) >> 7;
		int bit2 = (b & 0x40) >> 6;
		int bit3 = (b & 0x20) >> 5;
		int bit4 = (b & 0x10) >> 4;
		int bit5 = (b & 0x08) >> 3;
		int bit6 = (b & 0x04) >> 2;
		int bit7 = (b & 0x02) >> 1;
		int bit8 = (b & 0x01) >> 0;
		StringBuffer bitStr = new StringBuffer();
		bitStr.append(bit1);
		bitStr.append(bit2);
		bitStr.append(bit3);
		bitStr.append(bit4);
		bitStr.append(bit5);
		bitStr.append(bit6);
		bitStr.append(bit7);
		bitStr.append(bit8);
		LogUtil.d(TAG, bitStr);
	}
}
