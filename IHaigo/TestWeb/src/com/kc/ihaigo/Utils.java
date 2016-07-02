package com.kc.ihaigo;

/**
 * 通用工具类
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * @author ryan
 */

public class Utils {

	public static long getCurrentTime() {
		Date date = new Date();
		return date.getTime();
	}
	/**
	 * @Title: getCurrentTime
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param long1
	 * @param string
	 * @return CharSequence
	 * @throws
	 */

	public static String getCurrentTime(long time, String format) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}
	/**
	 * @Title: compareTime
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param maxtime
	 * @param mintime
	 * @return int
	 * @throws
	 */
	public static int compareTime(long maxtime, long mintime) {
		return (int) ((maxtime - mintime) / (3600 * 1000));
	}
	public static String readUnicodeStr(String unicodeStr) {
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < unicodeStr.length(); i++) {
			char char1 = unicodeStr.charAt(i);
			if (char1 == '\\' && isUnicode(unicodeStr, i)) {
				String cStr = unicodeStr.substring(i + 2, i + 6);
				int cInt = Integer.parseInt(cStr, 16);
				buf.append((char) cInt);
				// 跨过当前unicode码，因为还有i++，所以这里i加5，而不是6
				i = i + 5;
			} else {
				buf.append(char1);
			}
		}
		return buf.toString();
	}// 判断以index从i开始的串，是不是unicode码
	private static boolean isUnicode(String unicodeStr, int i) {
		int len = unicodeStr.length();
		int remain = len - i;
		// unicode码，反斜杠后还有5个字符 uxxxx
		if (remain < 5)
			return false;

		char flag2 = unicodeStr.charAt(i + 1);
		if (flag2 != 'u')
			return false;
		String nextFour = unicodeStr.substring(i + 2, i + 6);
		return isHexStr(nextFour);
	}

	/** hex str 1-9 a-f A-F */
	private static boolean isHexStr(String str) {
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			boolean isHex = ch >= '1' && ch <= '9' || ch >= 'a' && ch <= 'f'
					|| ch >= 'A' && ch <= 'F';
			if (!isHex)
				return false;
		}
		return true;
	}
	public static String readUnicode1(String unicodeStr) {
		StringBuilder buf = new StringBuilder();
		// 因为java转义和正则转义，所以u要这么写
		String[] cc = unicodeStr.split("\\\\u");
		for (String c : cc) {
			if (c.equals(""))
				continue;
			int cInt = Integer.parseInt(c, 16);
			char cChar = (char) cInt;
			buf.append(cChar);
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		String json = "{\n    contactNumber = 18516222701;\n    contacts = \"\\U62c9\\U62c9\";\n    id = 3;\n    postalCode = 200070;\n    status = 2;\n    userAddr = \"\\U592a\\U9633\\U5c71\\U8def\";\n    userArea = \"\\U4e0a\\U6d77 \\U4e0a\\U6d77 \\U95f8\\U5317\";\n    userId = 14;\n}";
		String t = json.toLowerCase().replace("\\\\", "\\").replace("\"", "")
				.replace(" ", "").split(";")[1].split("=")[1];
		StringBuilder sb = new StringBuilder();
		// sb.append('\\');
		// sb.append(Integer.parseInt("u", 16));
		sb.append((char) Integer.parseInt("6", 16));
		sb.append((char) (char) Integer.parseInt("2", 16));
		sb.append((char) Integer.parseInt("c", 16));
		sb.append((char) Integer.parseInt("9", 16));
		System.out.println(sb.toString());
		System.out.println(readUnicode1(t));
	}
	private static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
							case '0' :
							case '1' :
							case '2' :
							case '3' :
							case '4' :
							case '5' :
							case '6' :
							case '7' :
							case '8' :
							case '9' :
								value = (value << 4) + aChar - '0';
								break;
							case 'a' :
							case 'b' :
							case 'c' :
							case 'd' :
							case 'e' :
							case 'f' :
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A' :
							case 'B' :
							case 'C' :
							case 'D' :
							case 'E' :
							case 'F' :
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default :
								throw new IllegalArgumentException(
										"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
