package com.broadchance.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.util.Log;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class LogUtil {
	private final static int I = 1;
	private final static int D = 2;
	private final static int W = 3;
	private final static int E = 4;
	private final static int C = I;

	static Socket socket = null;
	static OutputStream ou;

	public static void init() {
		final LogConfigurator logConfigurator = new LogConfigurator();
		String logDir = FileUtil.ECG_DIR + "/log/";
		if (FileUtil.checkDir(logDir)) {
			logConfigurator.setFileName(logDir + "wdecgrec.log");
			// Set the root log level
			logConfigurator.setRootLevel(Level.DEBUG);
			// Set log level of a specific logger
			logConfigurator.setLevel("org.apache", Level.ERROR);
			logConfigurator.configure();
		}
		if (ConstantConfig.Debug) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						// 创建Socket
						// socket = new Socket("192.168.1.110",54321);
						socket = new Socket("192.168.1.102", 9050); // IP：10.14.114.127，端口54321
						ou = socket.getOutputStream();
						send();
					} catch (Exception e) {
					}
				}
			}).start();
		}
	}

	static LinkedBlockingQueue<String> list = new LinkedBlockingQueue<String>();

	private static void send() {
		while (true) {
			try {
				String msg = list.poll();
				int size = list.size();
				if (msg != null && msg.length() > 0) {
					msg = "\n(" + size + ")\t" + msg + "\n";
					ou.write(msg.getBytes("UTF-8"));
					ou.flush();
				}
				if (size < 20)
					Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendSocket(String tag, Object obj) {
		if (ConstantConfig.Debug) {
			list.offer(CommonUtil.getTime_D() + "\t" + tag + "\n"
					+ obj.toString());
		}
	}

	public static void d(String tag, Object obj) {
		if (D >= C && ConstantConfig.Debug) {
			sendSocket(tag, obj);
			Logger.getLogger(tag).debug(obj == null ? "" : obj.toString());
		}
	}

	public static void i(String tag, Object obj) {
		if (I >= C && ConstantConfig.Debug) {
			sendSocket(tag, obj);
			Log.i(tag, obj == null ? "" : obj.toString());
		}
	}

	public static void w(String tag, Object obj) {
		if (W >= C && ConstantConfig.Debug) {
			sendSocket(tag, obj);
			// Log.w(tag, obj == null ? "" : obj.toString());
			Logger.getLogger(tag).warn(obj == null ? "" : obj.toString());
		}
	}

	public static void e(String tag, Exception e) {
		if (E >= C && ConstantConfig.Debug) {
			sendSocket(tag, e);
			// Log.e(tag, e == null ? "" : e.toString());
			Logger.getLogger(tag).error(e);
			if (ConstantConfig.Debug) {
				e.printStackTrace();
			}
		}
	}

	public static void e(String tag, String error) {
		if (E >= C && ConstantConfig.Debug) {
			// Log.e(tag, error);
			sendSocket(tag, error);
			Logger.getLogger(tag).error(error);
		}
	}

	public static void e(String tag, Object obj, Exception e) {
		if (E >= C && ConstantConfig.Debug) {
			// Log.e(tag, obj == null ? "" : obj.toString(), e);
			sendSocket(tag, e);
			Logger.getLogger(tag).error(obj == null ? "" : obj.toString(), e);
		}
	}
}
