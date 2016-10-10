package com.broadchance.utils;

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
	}

	public static void d(String tag, Object obj) {
		if (D >= C && ConstantConfig.Debug) {
			// Log.d(tag, obj == null ? "" : obj.toString());
			Logger.getLogger(tag).debug(obj == null ? "" : obj.toString());
		}
	}

	public static void i(String tag, Object obj) {
		if (I >= C && ConstantConfig.Debug) {
			Log.i(tag, obj == null ? "" : obj.toString());
		}
	}

	public static void w(String tag, Object obj) {
		if (W >= C && ConstantConfig.Debug) {
			// Log.w(tag, obj == null ? "" : obj.toString());
			Logger.getLogger(tag).warn(obj == null ? "" : obj.toString());
		}
	}

	public static void e(String tag, Exception e) {
		if (E >= C && ConstantConfig.Debug) {
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
			Logger.getLogger(tag).error(error);
		}
	}

	public static void e(String tag, Object obj, Exception e) {
		if (E >= C && ConstantConfig.Debug) {
			// Log.e(tag, obj == null ? "" : obj.toString(), e);
			Logger.getLogger(tag).error(obj == null ? "" : obj.toString(), e);
		}
	}
}
