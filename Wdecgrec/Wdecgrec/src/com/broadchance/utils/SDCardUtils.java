package com.broadchance.utils;

import java.io.File;
import java.util.List;

import android.os.Environment;
import android.os.StatFs;

public class SDCardUtils {
	// 以M为单位
	public static long getAvailableSpace() {
		String path = Environment.getExternalStorageDirectory().getPath();
		long availableSpare = -1;
		if (isSDCardAvailable()) {
			StatFs statFs = new StatFs(path);
			// long blockSize = statFs.getBlockSize();
			// long blocks = statFs.getAvailableBlocks();
			// availableSpare = (blocks * blockSize) / (1024 * 1024);
			availableSpare = statFs.getAvailableBytes() / (1024 * 1024);
		}
		return availableSpare;
	}

	/**
	 * 获取目录占用空间大小
	 * 
	 * @param path
	 * @return 以M为单位
	 */
	public static long getUsedSpace(String path) {
		long usedSpace = -1;
		if (isSDCardAvailable()) {
			File file = new File(path);
			usedSpace = getFileSize(0, file) / (1024 * 1024);
		}
		return usedSpace;
	}

	private static long getFileSize(long size, File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				size = getFileSize(size, f);
			}

		} else {
			size += file.length();
		}
		return size;
	}

	public static boolean isSDCardAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean isAvailableSpace(long sizeMb) {
		boolean ishasSpace = false;
		if (isSDCardAvailable()) {
			long availabe = getAvailableSpace();
			if (availabe <= 0) {
				return false;
			} else {
				if (availabe >= sizeMb) {
					ishasSpace = true;
				} else {
					ishasSpace = false;
				}
			}
		}
		return ishasSpace;
	}
}
