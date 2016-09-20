package com.langlang.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.langlang.global.UserInfo;

import android.os.Environment;

public class Program {
	// private Context context;
	// private StringBuffer sb;

	public final static boolean Debug=true;
	public final static String LANGLANG_PATH = "langlang";
	public final static String IMAGE = "image";
	public final static String DATA_PATH = ".data";
	public final static String DEBUG_DATA_PATH = "debug_data";
	public final static String LOG_PATH = "log";
	public final static String TEMP_PATH = "temp";
	public final static String ECG_DATA = "ECGData";
	public final static String RESP_DATA = "RespData";
	public final static String TEMP_DATA = "TEMPData";
	public final static String TUMBLE_DATA = "TumbleData";
	public final static String ACCELERATION_X_DATA = "AccelerationX";
	public final static String ACCELERATION_Y_DATA = "AccelerationY";
	public final static String ACCELERATION_Z_DATA = "AccelerationZ";
	public final static String STEP_CALORIES_DATA = "Step_Cal";
	public final static String CSV_POSTFIX = ".csv";
	public final static String DAT_POSTFIX = ".dat";
	public final static String DATX_POSTFIX = ".datx";
	public final static String DATE_FORMAT = "yyyyMMdd";
	public final static String MINUTE_FORMAT = "yyyyMMddHHmm";
	public final static String SECOND_FORMAT = "yyyyMMddHHmmss";
	public final static String TEMP_DATA_ERROR = "TEMPDataerror";
	public final static String LFHF_OUT = "LFHF_out";
	public final static String LFHF_OUT_DETAIL = "LFHF_out_detail";
	public final static String RESP_OUT = "RESP_out";
	
	public final static String ACCE_VECTOR_DATA = "Accel_Vector";
	public final static String VOLTAGE_DATA = "Voltage_Data";
	public final static String HEART_RATE_DATA = "HeartRateData";

	public final static String HTE_WARNING_DATA = "HteWarning";
	
	public final static String EVENT_LOG = "event.txt";
	public final static String STEP_COUNT_LOG = "step_count.txt";
	
	public final static int INVALID_ECG_VALUE = getInvalidEcg();
	
	/***
	 * writeFile(TEMPData,"TEMPData",temDatalist);
	 * writeFile(tumbleData,"tumbleData",tumbleDatalist);
	 * writeFile(accelerationX,"accelerationX",accelerationXlist);
	 * writeFile(accelerationY,"accelerationY",accelerationYlist);
	 * writeFile(accelerationZ,"accelerationZ",accelerationZlist);
	 ***/

	// public Program(Context context) {
	// this.context = context;
	// }

	// public Program() {
	// }

	/**
	 * 获取文件存放的路劲发
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		} else {
			System.out.println("SDCard不可用,请检查SDCard是否插好！");
			return null;
		}
		return sdDir.toString();
	}

	/**
	 * 读取SD卡中文本文件
	 * 
	 * @param fileName
	 *            文件的名称
	 */
	public static String readSDFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		File file = new File(getSDPath() + "/" + fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) != -1) {
				sb.append((char) c);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 追加文件：使用FileWriter
	 * 
	 * @param fileName
	 *            文件名称
	 * @param content
	 *            文件内容
	 */
	public static void writerFile(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将字符串转换成数组
	 * 
	 * @param data
	 */
	// public StringBuffer transformArray(String data) {
	// System.out.println("xsads" + data.length());
	// System.out.println("xxxxxxxxxw" + data);
	// if (data.length() % 4 == 0) {
	// int[] bleData = new int[data.length() / 4];
	// sb = new StringBuffer();
	// for (int i = 0; i < bleData.length; i++) {
	// bleData[i] = Integer.parseInt(data.substring(0, 4), 16);
	// if (bleData.length != i) {
	// data = data.substring(4);
	// }
	// System.out.println("bleData[" + i + "]=" + bleData[i]);
	// }
	// for (int j = 0; j < bleData.length; j++) {
	// if (j != bleData.length - 1) {
	// sb.append(bleData[j] + ",");
	// } else {
	// sb.append(bleData[j]);
	// }
	// }
	// }
	// return sb;
	// }

	public static int byteToInt(byte b) {
		int rVal;
		if ((b & 0x80) > 0) {
			rVal = 128 + (b & 0x7f);
		} else {
			rVal = b & 0x7f;
		}
		return rVal;
	}

	/**
	 * 心电图数据
	 * @param data
	 * @return
	 */
	public static int[] getECGValues(byte[] data) {
//		int[] ECGData = new int[5];
//		ECGData[0] = bytesToInt(data[5], data[6]);
//		ECGData[1] = bytesToInt(data[7], data[8]);
//		ECGData[2] = bytesToInt(data[9], data[10]);
//		ECGData[3] = bytesToInt(data[13], data[14]);
//		ECGData[4] = bytesToInt(data[15], data[16]);
//		System.out.println("action program ECGData[0]，0：" + ECGData[0] + "，1："
//				+ ECGData[1] + "，2：" + ECGData[2] + "，3：" + ECGData[3] + "，4："
//				+ ECGData[4]);
//		// ECGData[0] = (int) (Integer.parseInt("", 16));
//
//		System.out.println("action getECGValues end2");
//
//		return ECGData;
		int[] ECGData = new int[5];
		
//		if ((data[19] == (byte)0xC0) || (data[19] == (byte)0xC4) ) {
		if (isValidEcg(data)) {
		ECGData[0] = bytesToInt(data[5], data[6]);
		ECGData[1] = bytesToInt(data[7], data[8]);
		ECGData[2] = bytesToInt(data[9], data[10]);
		ECGData[3] = bytesToInt(data[11], data[12]);
		ECGData[4] = bytesToInt(data[13], data[14]);
		}
		else {
//			ECGData[0] = bytesToInt((byte)0x7F, (byte)0xFF);
//			ECGData[1] = bytesToInt((byte)0x7F, (byte)0xFF);
//			ECGData[2] = bytesToInt((byte)0x7F, (byte)0xFF);
//			ECGData[3] = bytesToInt((byte)0x7F, (byte)0xFF);
//			ECGData[4] = bytesToInt((byte)0x7F, (byte)0xFF);			
			ECGData[0] = INVALID_ECG_VALUE;
			ECGData[1] = INVALID_ECG_VALUE;
			ECGData[2] = INVALID_ECG_VALUE;
			ECGData[3] = INVALID_ECG_VALUE;
			ECGData[4] = INVALID_ECG_VALUE;			
		}

		return ECGData;
	}

	/**
	 * 温度
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getTemp(byte[] data) {
		int temp[] = new int[1];
//		temp[0] = bytesToInt(data[18], data[19]);
		
		// 
//		temp[0] = bytesToInt(data[17], data[18]);
		temp[0] = convertByteToUnsignedInt(data[17]);
		return temp;
	}

	/**
	 * 电压
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getVoltageData(byte[] data) {
		int voltage[] = new int[1];
//		temp[0] = bytesToInt(data[18], data[19]);
		
		// 
//		temp[0] = bytesToInt(data[17], data[18]);
		voltage[0] = convertByteToUnsignedInt(data[18]);
		return voltage;
	}	
	
	public static String[] getTemps(byte[] data) {
		String temp[] = new String[1];
//		temp[0] = bytesToInt(data[18], data[19]);
		temp[0] = bytesToInts(data[17], data[18]);
		return temp;
	}

	/**
	 * 跌倒
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getTumbleData(byte[] data) {
		int tumble[] = new int[1];
//		tumble[0] = byteToInt(data[17]);
		tumble[0] = bytesToInt(data[17], data[18]);
		return tumble;
	}
	/**
	 * 跌倒(jx)
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getTumbleDatas(byte[] data) {
		byte tumble[] = new byte[2];
		tumble[0] =data[17];
		tumble[1] =data[18];
	
		return tumble;
	}

	/**
	 * 加速度X
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getAccelerationXData(byte[] data) {
//		int[] accelerationX = new int[2];
		int[] accelerationX = new int[1];
//		accelerationX[0] = bytesToInt(data[5], data[6]);
//		accelerationX[1] = bytesToInt(data[11], data[12]);

//		accelerationX[0] = NumberConvertor.bytesToInt(data[5], data[6]);
//		accelerationX[1] = NumberConvertor.bytesToInt(data[11], data[12]);
		
		accelerationX[0] = NumberConvertor.bytesToInt(data[17], data[18]);
		
		return accelerationX;
	}

	/**
	 * 加速度Y
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getAccelerationYData(byte[] data) {
//		int[] accelerationY = new int[2];
		int[] accelerationY = new int[1];
//		accelerationY[0] = bytesToInt(data[7], data[8]);
//		accelerationY[1] = bytesToInt(data[13], data[14]);
		
//		accelerationY[0] = NumberConvertor.bytesToInt(data[7], data[8]);
//		accelerationY[1] = NumberConvertor.bytesToInt(data[13], data[14]);

		accelerationY[0] = NumberConvertor.bytesToInt(data[17], data[18]);
		
		return accelerationY;
	}

	/**
	 * 加速度Z
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getAccelerationZData(byte[] data) {
//		int[] accelerationZ = new int[2];
		int[] accelerationZ = new int[1];
//		accelerationZ[0] = bytesToInt(data[9], data[10]);
//		accelerationZ[1] = bytesToInt(data[15], data[16]);
		
//		accelerationZ[0] = NumberConvertor.bytesToInt(data[9], data[10]);
//		accelerationZ[1] = NumberConvertor.bytesToInt(data[15], data[16]);
		
		accelerationZ[0] = NumberConvertor.bytesToInt(data[17], data[18]);
		
		return accelerationZ;
	}

	/**
	 * 呼吸波数据
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getResp(byte[] data) {
//		int[] Resp = new int[2];
//		/*
//		Resp[0] = (int) data[11] * 256 + (int) data[12];
//		Resp[1] = (int) data[17] * 256 + (int) data[18];
//		*/
//		Resp[0] = bytesToInt(data[11], data[12]);
//		Resp[1] = bytesToInt(data[17], data[18]);
//		return Resp;
//		int[] Resp = new int[2];
		int[] Resp = new int[1];
		/*
		Resp[0] = (int) data[11] * 256 + (int) data[12];
		Resp[1] = (int) data[17] * 256 + (int) data[18];
		*/
//		if ((data[19] == (byte)0xC0) || (data[19] == (byte)0xC4)) {
		if (isValidEcg(data)) {
		Resp[0] = bytesToInt(data[11], data[12]);
		}
		else {
//			Resp[0] = bytesToInt((byte)0x7F, (byte)0xFF);
			Resp[0] = INVALID_ECG_VALUE;
		}
		return Resp;
	}
	
	public static int[] getStepCountData(byte[] data) {
		int[] stepCount = new int[1];
		stepCount[0] = bytesToInt(data[17], data[18]);
		return stepCount;
	}

	public static int bytesToInt(byte a, byte b) {
		int intValue;

		intValue = (byteToInt(a) << 8) + byteToInt(b);
//		System.out.println("action bytesToInt [" + a + "," + b + ","
//				+ byteToInt(a) + ", " + byteToInt(b) + ","
//				+ (byteToInt(a) << 8) + "," + intValue);

		if ((intValue & 0x8000) > 0) {
			int maskedValue = intValue & 0x7fff;
			intValue = -(~maskedValue);
		} else {
		}

		return intValue;
	}
	public static String bytesToInts(byte a, byte b) {
		byte []dat=new byte[2];
		dat[0]=a;
		dat[1]=b;
		StringBuilder stringBuilder = new StringBuilder(dat.length);
		for (byte byteChar : dat)
			stringBuilder.append(String.format("%02X ", byteChar));
		String intValue;
		
		
	
		
		intValue = stringBuilder.toString();
//		System.out.println("action bytesToInt [" + a + "," + b + ","
//				+ byteToInt(a) + ", " + byteToInt(b) + ","
//				+ (byteToInt(a) << 8) + "," + intValue);
//
//		if ((intValue & 0x8000) > 0) {
//			int maskedValue = intValue & 0x7fff;
//			intValue = -(~maskedValue);
//		} else {
//		}

		return intValue;
	}
	public static String getSDLangLangPath() {
		return getSDPath() + File.separator + LANGLANG_PATH;
	}
	public static String getSDLangLangImagePath() {
		return getSDLangLangPath() + File.separator + IMAGE;
	}

	public static String getSDLangLangDataPath() {
		if (UserInfo.isInDebugMode()) {
			return getSDLangLangPath() + File.separator + DEBUG_DATA_PATH;
		} else {
			return getSDLangLangPath() + File.separator + DATA_PATH;
		}
	}
	public static String getSDLangLangTempPath() {
		return getSDLangLangPath() + File.separator + TEMP_PATH;
	}
	/**
	 * 
	 * @param dataType
	 *            文件类型
	 * @param minute
	 *            : 'yyyyMMddHHmm'文件格式
	 * @return
	 */
	public static String getSDPathByDataType(String dataType, String minute) {
		return getDataPathByMinute(minute) + File.separator + dataType + "_"
				+ minute + CSV_POSTFIX;
	}
	
	public static String getSDPathByDataType(String dataType, String minute, String uid) {
		return getDataPathByMinute(minute, uid) + File.separator + dataType + "_"
				+ minute + CSV_POSTFIX;
	}
	
	public static String getSDDataPathByDataType(String dataType, String minute) {
		return getDataPathByMinute(minute) + File.separator + dataType + "_"
				+ minute + DAT_POSTFIX;
	}
	
	public static String getSDDatXPathByDataType(String dataType, String minute) {
		return getDataPathByMinute(minute) + File.separator + dataType + "_"
				+ minute + DATX_POSTFIX;
	}
	
	public static String getSDDataPathByDataType(String dataType, String minute, String uid) {
		return getDataPathByMinute(minute, uid) + File.separator + dataType + "_"
				+ minute + DAT_POSTFIX;
	}
	
	public static String getSDDatXPathByDataType(String dataType, String minute, String uid) {
		return getDataPathByMinute(minute, uid) + File.separator + dataType + "_"
				+ minute + DATX_POSTFIX;
	}
	
	public static String getSDPathByTempType(String dataType, String minute) {
		return getTempPathByMinute(minute) + File.separator + dataType + "_"
				+ minute + CSV_POSTFIX;
	}

	public static void createSDLangLangDir() {
		File dir = new File(getSDLangLangPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	public static void createSDLangLangImageDir() {
		File dir = new File(getSDLangLangImagePath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void createSDLangLangDataDir() {
		File dir = new File(getSDLangLangDataPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public static String getDataPathByMinute(String minute) {
		String dateString = minute.substring(0, 8);
		return getSDLangLangDataPath() + File.separator + dateString
				+ File.separator + minute;
	}
	
	public static String getDataPathByMinute(String minute, String uid) {
		String dateString = minute.substring(0, 8);
		return getSDLangLangDataPath() + File.separator + uid 
						+ File.separator + dateString
						+ File.separator + minute;
	}
	
	public static String getTempPathByMinute(String minute) {
		String dateString = minute.substring(0, 8);
		return getSDLangLangTempPath() + File.separator + dateString
				+ File.separator + minute;
	}

	/**
	 * 根据currMinute创建文件夹
	 * 
	 * @param currMinute当前分钟数对应的文件夹
	 */
	public static void createDataDirForCurrMinute(String currMinute) {
		System.out.println("action createDataDirForCurrMinute [" + currMinute
				+ "]");
		String dateString = currMinute.substring(0, 8);
		String dirPath = getSDLangLangDataPath() + File.separator + dateString;

		System.out.println("action createDataDirForCurrMinute [" + dirPath
				+ "]");

		File dateDir = new File(dirPath);
		if (!dateDir.exists()) {

			System.out
					.println("action createDataDirForCurrMinute !dateDir.exists()");

			dateDir.mkdirs();

			String minutePath = getDataPathByMinute(currMinute);
			File minuteDir = new File(minutePath);
			minuteDir.mkdirs();
		} else {
			System.out
					.println("action createDataDirForCurrMinute dateDir.exists()");

			String minutePath = getDataPathByMinute(currMinute);
			File minuteDir = new File(minutePath);

			System.out.println("action createDataDirForCurrMinute minutePath["
					+ minutePath + "]");
			if (!minuteDir.exists()) {
				System.out
						.println("action createDataDirForCurrMinute !minuteDir.exists()");
				minuteDir.mkdirs();
			}
		}
	}
	
	public static void createDataDirForCurrMinute(String currMinute, String uid) {
		System.out.println("action createDataDirForCurrMinute [" + currMinute 
				+ "] UID[" + uid + "]");
		String dateString = currMinute.substring(0, 8);
		
		String dirPath 
					= getSDLangLangDataPath() 
							+ File.separator + uid + File.separator
							+ dateString;

		System.out.println("action createDataDirForCurrMinute [" + dirPath
				+ "]");

		File dateDir = new File(dirPath);
		if (!dateDir.exists()) {

			System.out
					.println("action createDataDirForCurrMinute !dateDir.exists()");

			dateDir.mkdirs();

			String minutePath = getDataPathByMinute(currMinute, uid);
			File minuteDir = new File(minutePath);
			minuteDir.mkdirs();
		} else {
			System.out
					.println("action createDataDirForCurrMinute dateDir.exists()");

			String minutePath = getDataPathByMinute(currMinute, uid);
			File minuteDir = new File(minutePath);

			System.out.println("action createDataDirForCurrMinute minutePath["
					+ minutePath + "]");
			if (!minuteDir.exists()) {
				System.out
						.println("action createDataDirForCurrMinute !minuteDir.exists()");
				minuteDir.mkdirs();
			}
		}
	}
	
	/**
	 * 根据currMinute创建文件夹
	 * 
	 * @param currMinute当前分钟数对应的文件夹
	 */
	public static void createTempDirForCurrMinute(String currMinute) {
		String dateString = currMinute.substring(0, 8);
		String dirPath = getSDLangLangTempPath() + File.separator + dateString;

		File dateDir = new File(dirPath);
		if (!dateDir.exists()) {
			dateDir.mkdirs();

			String minutePath = getTempPathByMinute(currMinute);
			File minuteDir = new File(minutePath);
			minuteDir.mkdirs();
		} else {
			String minutePath = getTempPathByMinute(currMinute);
			File minuteDir = new File(minutePath);

			if (!minuteDir.exists()) {
				minuteDir.mkdirs();
			}
		}
	}
	
	public static void createTempDirForCurrMinute(String currMinute, String uid) {
		String dateString = currMinute.substring(0, 8);
		String dirPath = getSDLangLangTempPath() 
								+ File.separator + uid + File.separator 
								+ dateString;

		File dateDir = new File(dirPath);
		if (!dateDir.exists()) {
			dateDir.mkdirs();

			String minutePath = getTempPathByMinute(currMinute);
			File minuteDir = new File(minutePath);
			minuteDir.mkdirs();
		} else {
			String minutePath = getTempPathByMinute(currMinute);
			File minuteDir = new File(minutePath);

			if (!minuteDir.exists()) {
				minuteDir.mkdirs();
			}
		}
	}
	
/**
 * 获取上一分钟所对应的文件
 * @param dataType 文件类型
 * @return
 */
	public static File getLastMinuteFile(String dataType) {
		String filePath = getSDPathByDataType(dataType,
				DateUtil.getLastMinute());// 获取对应的文件路径
		File file = new File(filePath);

		if (!file.exists()) {
			return null;
		} else {
			return file;
		}
	}
	/**
	 * 生成文件路径
	 * @return
	 */
	public static String generatePathForTemp(String currMinute){
		String filepathString="";
		String dateString = currMinute.substring(0, 8);
		String dirPath = getSDLangLangTempPath() + File.separator + dateString;
		File file=new File(dirPath);
		if(!file.exists()){
			file.mkdirs();
			String minutePath = getTempPathByMinute(currMinute);
			File minuteDir = new File(minutePath);
			minuteDir.mkdirs();
			filepathString=minutePath;
		}else{
			String minutePath = getTempPathByMinute(currMinute);
			File minuteDir = new File(minutePath);
			if (!minuteDir.exists()) {
				minuteDir.mkdirs();
			}
			filepathString=minutePath;
		}
		return filepathString;
	}

	public static void createSDLangLangTempDir() {
		File dir = new File(getSDLangLangTempPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public static String getLastLineOf(String path) {
		int lineCount = 0;
		String lastLine = "";
		BufferedReader br = null;
		
		boolean hasException = false;
		
		try {
			br = new BufferedReader(new FileReader(path));			
			String data = br.readLine();
		
			while (data != null) {		
				lineCount++;
				lastLine = data;
				data = br.readLine();
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			hasException = true;
		} catch (IOException e) {
			e.printStackTrace();
			hasException = true;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {					
					e.printStackTrace();
					return null;
				}
			}
		}
		
		if (hasException == true) {
			return null;
		}
		
		if (lineCount == 0) {
			return null;
		}
		else {
			return lastLine;
		}
	}
	
	public static String getFirstLineOf(String path) {
		int lineCount = 0;
		String lastLine = "";
		BufferedReader br = null;
		
		boolean hasException = false;
		
		try {
			br = new BufferedReader(new FileReader(path));			
			String data = br.readLine();
			
			if (data != null) {
				lastLine = data;
			} else {
				lineCount = 0;
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			hasException = true;
		} catch (IOException e) {
			e.printStackTrace();
			hasException = true;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {					
					e.printStackTrace();
					return null;
				}
			}
		}
		
		if (hasException == true) {
			return null;
		}
		
		if (lineCount == 0) {
			return null;
		}
		else {
			return lastLine;
		}
	}
	
	public static void logToSDCard(String content, String path) {
		if ((content == null) || (path == null)) {
			return;
		}
		if (getSDPath() != null) {
			writerFile(getSDPath() + File.separator + path, content);
		}
	}	
	
	public static int convertByteToUnsignedInt(byte b) {
		int tmp = 0x00000000;
		tmp |= (0xff & b);
		return tmp;
	}

	public static int[] getPrevStepCounts(byte[] data) {
		int[] stepCounts = new int[5];
		stepCounts[0] = NumberConvertor.bytesToInt(data[5], data[6]);
		stepCounts[1] = NumberConvertor.bytesToInt(data[7], data[8]);
		stepCounts[2] = NumberConvertor.bytesToInt(data[9], data[10]);
		stepCounts[3] = NumberConvertor.bytesToInt(data[11], data[12]);
		stepCounts[4] = NumberConvertor.bytesToInt(data[13], data[14]);
		
		return stepCounts;
	}

	public static String getMinuteDataByDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(MINUTE_FORMAT);
		Date now = new Date();
		return sdf.format(now);
	}
	
	public static String getSecondDataByDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(SECOND_FORMAT);
		Date now = new Date();
		return sdf.format(now);
	}

	public static int getFirstBitOf(byte b) {
		int tmp = 0x00000000;
		tmp |= (0x80 & b);
		if (tmp > 0) {
			return 1;
		} 
		return 0;
	}
	
	public static String getSDLanglangLogPath() {
		return getSDLangLangPath() + File.separator + LOG_PATH;
	}
	
	public static boolean isValidEcg(byte[] data) {
		if (data.length != 20) return false;
		
		return (data[19] == (byte)0xC0) || (data[19] == (byte)0xC4);  
	}
	
	public static boolean isEcgC7(byte[] data) {
		if (data.length != 20) return false;
		
		if ((data[19] == (byte)0xC0) || (data[19] == (byte)0xC4)) {
			return false;
		}
		else {
			return true;
		}		
//		return (data[19] == (byte)0xC7);
	}
	
	public static int getInvalidEcg() {
		return bytesToInt((byte)0x7F, (byte)0xFF);
	}

	// Get Heart Rate From Frame69
	public static int[] getHeartRate(byte[] data) {
		int[] heartRates = new int[1];
		heartRates[0] = bytesToInt(data[5], data[6]);
		return heartRates;
	}

	// Get Step Count From Frame69
	public static int[] getStepCountDataByNewFrame(byte[] data) {
		int[] stepCounts = new int[1];
		stepCounts[0] = bytesToInt(data[7], data[8]);
		return stepCounts;
	}

	// Get Temp Data From Frame69
	public static int[] getTempByNewFrame(byte[] data) {
		int[] temp = new int[1];
		temp[0] = convertByteToUnsignedInt(data[17]);
		return temp;
	}

	// Get Voltage Data From Frame69
	public static int[] getVoltageDataByNewFrame(byte[] data) {
		int[] voltageData = new int[1];
		voltageData[0] = convertByteToUnsignedInt(data[18]);
		return voltageData;
	}

	// Get Tumble Data From Frame69
	public static byte[] getTumbleDataByNewFrame(byte[] data) {
		byte tumble[] = new byte[1];
		tumble[0] =data[16];
	
		return tumble;
	}
}