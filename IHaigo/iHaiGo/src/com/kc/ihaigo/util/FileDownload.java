package com.kc.ihaigo.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kc.ihaigo.IHaiGoMainActivity;

public class FileDownload extends Thread {
	public static void downLoadFile(final String urlStr, final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int blockSize;
				int threadNum = 5;
				FileDownload[] fds = new FileDownload[threadNum];
				try {
					URL url = new URL(urlStr);
					URLConnection conn = url.openConnection();
					// 获取下载文件的总大小
					int fileSize = conn.getContentLength();
					// 计算每个线程要下载的数据量
					blockSize = fileSize / threadNum;
					String filename = urlStr.substring(urlStr.lastIndexOf("/"));
					int suffix = filename.lastIndexOf(".");
					// 默认下载文件为*.APK
					filename = suffix < 0
							? Utils.getCurrentTime() + ".apk"
							: Utils.getCurrentTime()
									+ filename.substring(suffix);
					File file = new File(ACache.get(IHaiGoMainActivity.main)
							.getCacheManager().getCacheDir()
							+ "/download/");
					if (!file.exists()) {
						file.mkdirs();
					}
					file = new File(file, filename);
					for (int i = 0; i < threadNum; i++) {
						// 启动线程，分别下载自己需要下载的部分
						if (i < threadNum - 1) {
							FileDownload fdt = new FileDownload(url, file, i
									* blockSize, (i + 1) * blockSize - 1);
							fdt.setName("Thread" + i);
							fdt.start();
							fds[i] = fdt;
						} else {
							// 解决整除后百分比计算误差
							// 最后一个线程下载剩下所有部分
							FileDownload fdt = new FileDownload(url, file, i
									* blockSize, fileSize);
							fdt.setName("Thread" + i);
							fdt.start();
							fds[i] = fdt;
						}
					}
					boolean finished = false;
					while (!finished) {
						// 先把整除的余数搞定
						int downloadedSize = 0;
						finished = true;
						for (int i = 0; i < fds.length; i++) {
							downloadedSize += fds[i].getDownloadSize();
							if (!fds[i].isFinished()) {
								finished = false;
							}
						}
						// 通知handler去更新视图组件
						Message message = handler.obtainMessage();
						message.obj = fileSize + ":" + downloadedSize + ":"
								+ file.getAbsolutePath();
						handler.sendMessage(message);
						// 休息1秒后再读取下载进度
						Thread.sleep(1000);
					}
				} catch (Exception e) {
				}
			}
		}).start();

	}
	private static final int BUFFER_SIZE = 1024;
	private URL url;
	private File file;
	private int startPosition;
	private int endPosition;
	private int curPosition;
	// 用于标识当前线程是否下载完成
	private boolean finished = false;
	private int downloadSize = 0;
	private FileDownload(URL url, File file, int startPosition, int endPosition) {
		this.url = url;
		this.file = file;
		this.startPosition = startPosition;
		this.curPosition = startPosition;
		this.endPosition = endPosition;
	}
	@Override
	public void run() {
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		byte[] buf = new byte[BUFFER_SIZE];
		URLConnection con = null;
		try {
			con = url.openConnection();
			con.setAllowUserInteraction(true);
			// 设置当前线程下载的起点，终点
			con.setRequestProperty("Range", "bytes=" + startPosition + "-"
					+ endPosition);
			// 使用java中的RandomAccessFile 对文件进行随机读写操作
			fos = new RandomAccessFile(file, "rw");
			// 设置开始写文件的位置
			fos.seek(startPosition);
			bis = new BufferedInputStream(con.getInputStream());
			// 开始循环以流的形式读写文件
			while (curPosition < endPosition) {
				int len = bis.read(buf, 0, BUFFER_SIZE);
				if (len == -1) {
					break;
				}
				fos.write(buf, 0, len);
				curPosition = curPosition + len;
				if (curPosition > endPosition) {
					downloadSize += len - (curPosition - endPosition) + 1;
				} else {
					downloadSize += len;
				}
			}
			// 下载完成设为true
			this.finished = true;
			bis.close();
			fos.close();
		} catch (IOException e) {
			Log.d(getName() + " Error:", e.getMessage());
		}
	}

	public boolean isFinished() {
		return finished;
	}

	public int getDownloadSize() {
		return downloadSize;
	}
}