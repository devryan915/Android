package com.langlang.utils;

import android.os.Environment;
import android.os.StatFs;

public class SDCardUtils {	
	// 以M为单位
	public static long getAvaiableSpace() {
		long availableSpare = -1;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long blocks = statFs.getAvailableBlocks();
			availableSpare = (blocks * blockSize) / (1024 * 1024);
		}
		return availableSpare;
	}
	
	public static boolean isAvailableSpace(long sizeMb) {
		boolean ishasSpace = false;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			long availabe = getAvaiableSpace();
			if (availabe <= 0) {
				return false;
			}
			else {
				if (availabe >= sizeMb) {
					ishasSpace = true;
				}
				else {
					ishasSpace = false;
				}
			}
		}
		return ishasSpace;
	}
}
