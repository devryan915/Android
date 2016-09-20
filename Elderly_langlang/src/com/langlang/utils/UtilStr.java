package com.langlang.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * 
 * 常用:cutString\equalStr\isEmpty\getTime\dealObject
 */
public class UtilStr  {

    /**
     * 将字符串用md5加密
     * 
     * @param content
     *            需要加密的字符串
     * @return 加密后的字符串
     */
    public static String getEncryptPassword(String content) {
        return DigestUtils.md5Hex(content.trim());
    }

    /**
     * 判断列表是否有数据
     * 
     * @param list
     *            需要判断的列表
     * @return 如果为空或null返回true，反之false
     */
	public static boolean isEmpty(List<?> list) {
        return (list == null || list.isEmpty());
    }

    /**
     * 判断字符串是否为空
     * 
     * @param value
     *            要判断的字符串
     * @return 如果为""或null返回true，反之false
     */
    public static boolean isEmpty(String value) {
        return (value == null || value.trim().equals("")) ? true : false;
    }

    /**
     * 判断对象是否为空
     * 
     * @param value
     *            要判断的对象
     * @return 如果为null或对象的值为""返回true，反之false
     */
    public static boolean isEmpty(Object value) {
        return (value == null || trim(value).equals("")) ? true : false;
    }

    /**
     * 判断val字符串是否是以字符串c结尾
     * 
     * @param val
     *            目标字符串
     * @param c
     *            要搜索的字符串
     * @return 如果是返回true，反之false
     */
    public static boolean isEndWith(String val, String c) {
        if (isEmpty(val) || isEmpty(c)) {
            return false;
        }
        val = val.trim();
        String subVal = val.substring(val.trim().length() - c.length());
        return subVal.equalsIgnoreCase(c);
    }



    /**
     * 将对象转化为String
     * 
     * @param obj
     *            要转化的对象
     * @return 转化后的字符串
     */
    public static final String dealObject(Object obj) {
        return isEmpty(obj) ? "" : obj.toString().trim();
    }
    
    /**
     * 处理字符串，若字符串为空或null，则转换为空
     * 
     * @param str
     *            要处理的字符串
     * @return 处理后的结果
     */
    public static final String dealString(String str) {
        return isEmpty(str) ? "" : str.trim();
    }
    
    public static final String dealStringNoTrim(Object obj){
        return isEmpty(obj) ? "" : obj.toString();
    }
    
    
	/**
	 * 将Double转换成精确到小数点后两位的字符串
	 * 
	 * @param d 需要转换的Double类型数据
	 * @return 转换后的String类型结果
	 */
	public static String doublePoint(Double d) {
		if (d == null) {
			return "0.00";
		}
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(d.doubleValue());
	}

    /**
     * 获得字符串Double类型数据
     * 
     * @param str
     *            要计算的字符串
     * @return 计算后的Double类型数据
     * 
     */
    public static final Double dealAmt(String str) {
        if (UtilStr.isEmpty(str)) {
            return new Double(0);
        }
        return new Double(str);
    }

    /**
     * 处理字符串为空的情况
     * 
     * @param str
     *            要处理的字符串
     * @return 处理后的结果
     */
    public static final String dealNull(String str) {
        if (isEmpty(str)) {
            return "";
        } else if ("null".equalsIgnoreCase(str.trim())) {
            return "";
        }
        return str.trim();
    }
    
    /**
     * 处理字符串，若字符串为空或null，则返回默认值defaultString
     * 
     * @param str
     *            要处理的字符串
     * @param defaultString
     *            默认值
     * @return 处理结果字符串
     */
    public static final String dealString(String str, String defaultString) {
        return isEmpty(str) ? defaultString : str;
    }

    /**
     * 
     * 处理区号，如果首字符是0，则去除
     * 
     * @param zoneNo
     *            要处理的区号
     * @return 处理后的结果
     */
    public static final String dealZoneNo(String zoneNo) {
        if (isEmpty(zoneNo)) {
            return zoneNo;
        }
        zoneNo = trim(zoneNo);
        char firstWord = zoneNo.charAt(0);
        if (firstWord == '0' && zoneNo.length() > 0) {
            zoneNo = zoneNo.substring(1);
        }
        return zoneNo;
    }
    

    /**
     * 将一个字串去除多余的空格字符
     * 
     * @param str
     *            原字串
     * @param defaultString
     *            当str为null值时的默认返回值
     * @return 处理结果字符串
     */
    public static final String trim(String str, String defaultString) {
        if (str == null) {
            return defaultString;
        }
        return str.trim();
    }

    
    /**
     * 将一个字串去除多余的空格字符
     * 
     * @param str
     *            原字串
     * @return 如果原字串为null则返回null，否则返回去除多余空格后的字串
     */
    public static final String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    /**
     * 删除obj数据的前后空格和回车
     * 
     * @param obj
     *            要计算的对象
     * @return 计算后的结果
     */
    public static final Object trim(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return ((String) obj).trim();
        } else {
            return obj;
        }
    }
    
    /**
     * 将一个字串的多余空格去除，并转换为小写字符
     * 
     * @param str
     *            原字串
     * @return 如果原字串为null则返回null，否则返回转换后的字串
     */
    public static final String trimAndLowcase(String str) {

        return toLowcase(trim(str));
    }

    /**
     * 将一个字串的多余空格去除，并转换为小写字符
     * 
     * @param str
     *            原字串
     * @param defaultString
     *            当str为null值时的默认返回值
     * @return 转换为小写字符
     */
    public static final String trimAndLowcase(String str, String defaultString) {
        return toLowcase(trim(str), defaultString);
    }

    
    /**
     * 将一个字串转化为大写字符
     * 
     * @param str
     *            原字串
     * @return 如果原字串为null则返回null，否则返回转换后的字串
     */
    public static final String toLowcase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * 将一个字串转化为大写字符
     * 
     * @param str
     *            原字串
     * @param defaultString
     *            当str为null值时的默认返回值
     * @return 将指定字符串转化为大写字符
     */
    public static final String toLowcase(String str, String defaultString) {
        if (str == null) {
            return defaultString;
        }
        return str.toLowerCase();
    }
    
    
    /**
     * 将字符串val扩展到len长度，如果len大于val字符串的长度，用字符串c填充<br>fill("a",10,"2")  结果为 a222222222
     * 
     * @param val
     *            要扩展的字符串
     * @param len
     *            要扩展到的长度
     * @param c
     *            填充字符串
     * @return 扩展后的字符串
     */
    public static String fill(String val, int len, String c) {
        String result = "";
        if (UtilStr.isEmpty(val)) {
            for (int i = 0; i < len; i++) {
                result += c;
            }
            return result;
        } else {
            for (int j = 0; j < len - getByteLen(val); j++) {
                result += c;
            }
        }
        return val + result;

    }
    
    

    /**
     * 获得字符串的子串
     * 
     * @param str
     *            要处理的字符串
     * @param start
     *            开始位置
     * @param count
     *            截取的字符数
     * @return 截取后的结果
     */
    public static final String subString(String str, int start, int count) {
        str = dealString(str);
        if (start < 0 || count <= 0) {
            return str;
        }

        if (str.length() <= start) {
            return "";
        } else if (str.length() <= start + count) {
            return str.substring(start, str.length());
        } else {
            return str.substring(start, start + count) + "...";
        }
    }

    


    /**
     * 将一个字串转换成规定长度的字串，不足以空格补齐
     * 
     * @param str
     *            原字串
     * @return 如果原字串为null则返回null，否则返回去除多余空格后的字串
     */
    public static final String toSizeString(String str, int size) {
        StringBuffer sb = new StringBuffer("");
        if (str != null) {
            sb.append(trim(str));
        }
        int len = sb == null ? 0 : sb.length();
        if (len >= size) {
            sb.delete(size, len);
        } else {
            for (int i = 0; i < size - len; i++) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * 处理Integer数据，如果Integer长度大于size则删除从size位置开始的后几位，反之将obj扩展到size位，前面用"0"补充
     * 
     * @param obj
     *            要处理的obj
     * @param size
     *            指定的位数
     * @return 计算后的结果
     */
    public static final String toSizeString(Integer obj, int size) {
        StringBuffer sb = new StringBuffer("");
        if (obj != null) {
            sb.append("" + obj.intValue());
        }
        int len = sb.length();
        if (len >= size) {
            sb.delete(size, len);
        } else {
            for (int i = 0; i < size - len; i++) {
                sb.insert(0, "0");
            }
        }
        return sb.toString();
    }

    /**
     * 处理Short数据，如果Short长度大于size则删除从size位置开始的后几位，反之将obj扩展到size位，前面用"0"补充
     * 
     * @param obj
     *            要处理的obj
     * @param size
     *            指定的位数
     * @return 计算后的结果
     */
    public static final String toSizeString(Short obj, int size) {
        StringBuffer sb = new StringBuffer("");
        if (obj != null) {
            sb.append("" + obj.intValue());
        }
        int len = sb.length();
        if (len >= size) {
            sb.delete(size, len);
        } else {
            for (int i = 0; i < size - len; i++) {
                sb.insert(0, "0");
            }
        }
        return sb.toString();
    }

    /**
     * 处理Double数据，如果Double长度大于size则删除从size位置开始的后几位，反之将obj扩展到size位，前面用"0"补充
     * 
     * @param obj
     *            要处理的obj
     * @param size
     *            指定的位数
     * @return 计算后的结果
     */
    public static final String toSizeString(Double obj, int size) {
        StringBuffer sb = new StringBuffer("");
        if (obj != null) {
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            sb.append(nf.format(obj).substring(1));
        }
        int len = sb.length();
        if (len >= size) {
            sb.delete(size, len);
        } else {
            for (int i = 0; i < size - len; i++) {
                sb.insert(0, "0");
            }
        }
        return sb.toString();
    }

   
    
    
    /**
     * 从电话号码中截取区号，此区号不包括零
     * 
     * @param phone
     *            要处理的电话号码
     * @return 处理后的结果
     */
    public static final String getZoneNo(String phone) {
        String zoneNo = "";
        if (isEmpty(phone)) {
            return zoneNo;
        }
        phone = trim(phone);
        if (phone.length() < 9) {
            return zoneNo;
        }
        // 将首位如果是0，则去掉，调用dealZoneNo(String zoneNo)方法
        phone = dealZoneNo(phone);
        // 判断号码是否为手机号码，如果是则返回空
        String numTwo = phone.substring(0, 2);
        if ("13".equalsIgnoreCase(numTwo)) {
            return zoneNo;
        }

        char c = phone.charAt(0);

        if (c == '1' || c == '2') {
            zoneNo = phone.substring(0, 2);
        } else {
            zoneNo = phone.substring(0, 3);
        }
        // zoneNo = dealZoneNo(phone.substring(0, phone.length()-8));
        return zoneNo;
    }


    /**
     * 将一字符转换为布尔形
     * 
     * @param b
     *            输入的字符串
     * @return 处理后的布尔类型数据
     */
    public static boolean toBoolean(String b) {
        boolean t = false;
        if (b.toLowerCase().equals("true") || b.equals("1") || b.toLowerCase().equals("yes")
                || b.toLowerCase().endsWith("y")) {
            t = true;
        }
        return t;
    }
    
    /**
     * 将字符串转换为整形
     * 
     * @param str
     *            输入的字符串
     * @param i
     *            默认值
     * @return 如果str可以转换成int数据则返回转换后的结果，反之返回i
     */
    public static int toInt(String str, int i) {
        int result = 0;
        try {
            result = java.lang.Integer.parseInt(str);
        } catch (Exception ex) {
            result = i;
        }
        return result;
    }

    /**
     * 将字符串转换为整形
     * 
     * @param str
     *            输入的字符串
     * @return 处理后的整形数据
     */
    public static int toInt(String str) {
        return toInt(str, 0);
    }

   

    /**
     * 计算String类型数据，返回计算后的日期格式
     * 
     * @param sdate
     *            要计算的数据
     * @param delimate
     *            指定sdate使用的分隔符
     * @return 如果delimate为空或null，返回null，反之返回计算后的Calendar结果
     */
    public static Calendar toCalendar(String sdate, String delimate) {
        Calendar returnValue = null;

        if (sdate == null || sdate.trim().length() == 0 || delimate == null || delimate.trim().length() < 1) {
            return returnValue;
        }

        java.util.StringTokenizer st = new java.util.StringTokenizer(sdate, delimate);
        try {
            int y = 0, m = 0, d = 0;
            if (st.hasMoreTokens()) {
                y = new Integer(st.nextToken()).intValue();
            }
            if (st.hasMoreTokens()) {
                m = new Integer(st.nextToken()).intValue();
            }
            if (st.hasMoreTokens()) {
                d = new Integer(st.nextToken()).intValue();
            }
            returnValue = Calendar.getInstance();
            returnValue.set(y, m - 1, d, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;

    }
    
    
    /**
     * 计算字符串中特定字符或字符串的个数
     * 
     * @param str
     *            需要计数的字符或字符串，如'?'
     * @return 个数
     */
    public static int countStr(String src, String str) {
        if (src == null || str == null) {
            return 0;
        }
        int i = 0;
        int pos = 0;
        while ((pos = src.indexOf(str, pos)) != -1) {
            i++;
            pos = pos + str.length();
        }
        return i;
    }

    

    /**
     * 分割字串
     * 
     * @param source
     *            原始字符
     * @param delim
     *            分割符
     * @return 字符串数组,且去掉多余空格
     */
    public static String[] split(String source, String delim) {
        int i = 0;
        int l = 0;
        if (source == null || delim == null) {
            return new String[0];
        }
        if (source.equals("") || delim.equals("")) {
            return new String[0];
        }

        String douDelim = delim + delim;
        String repDelim = delim + " " + delim;
        source.replaceAll(douDelim, repDelim);

        StringTokenizer st = new StringTokenizer(source, delim);
        l = st.countTokens();
        String[] s = new String[l];
        while (st.hasMoreTokens()) {
            s[i++] = st.nextToken().trim();
        }
        return s;
    }

    /**
     * 处理String类型的数组，返回Integer类型数组
     * 
     * @param str
     *            String类型的数组
     * @return 如果str为空或为null，返回长度为0的Integer类型数组
     */
    public static Integer[] parse(String[] str) {
        if (str == null || str.length == 0) {
            return new Integer[0];
        }
        Integer[] vals = new Integer[str.length];
        for (int i = 0; i < str.length; i++) {
            vals[i] = Integer.valueOf(str[i]);
        }

        return vals;
    }

    /**
     * 处理String类型的数组，返回List，其中存储str数组中的所有元素
     * 
     * @param str
     *            String类型的数组
     * @return 如果str为空或为null，返回长度为0的List对象
     */
    public static List<String> parseStrToStrList(String[] str) {
        List<String> list = new ArrayList<String>();
        if (str == null || str.length == 0) {
            return list;
        }
        for (int i = 0; i < str.length; i++) {
            if (!isEmpty(str[i])) {
                list.add(str[i].trim());
            }
        }

        return list;
    }

    /**
     * 处理String类型的数组，将str中的每个对象转换成int型，放置到List中，返回
     * 
     * @param str
     *            String类型的数组
     * @return 如果str为空或为null，返回长度为0的List对象
     */
    public static List<Integer> parseStrToInt(String[] str) {
        List<Integer> list = new ArrayList<Integer>();
        if (str == null || str.length == 0) {
            return list;
        }
        for (int i = 0; i < str.length; i++) {
            if (!isEmpty(str[i])) {
                list.add(new Integer(str[i].trim()));
            }
        }

        return list;
    }

    /**
     * 分割字串
     * 
     * @param source
     *            原始字符
     * @return 字符串数组,且去掉多余空格
     */
    public static List<String> split(String source) {
        return splitToList(source, " ");
    }

    /**
     * 将source字符串，用delim字符拆分，将结果放置到List列表中
     * 
     * @param source
     *            要拆分的字符串
     * @param delim
     *            指定的拆分字符
     * @return 如果source为空，返回长度为0的List
     */
    public static List<String> splitToList(String source, String delim) {
        List<String> list = new ArrayList<String>();
        if (isEmpty(source)) {
            return list;
        }

        StringTokenizer st = new StringTokenizer(source, delim);
        while (st.hasMoreTokens()) {
            String val = st.nextToken().trim();
            if (!isEmpty(val)) {
                list.add(val);
            }
        }
        return list;
    }

    /**
     * 将source字符串，用delim字符拆分，并将拆分出每个字符串转换成Integer型，放置到List列表中
     * 
     * @param source
     *            要拆分的字符串
     * @param delim
     *            指定的拆分字符
     * @return 如果source为空，返回长度为0的List
     */
    public static List<Integer> splitToIntList(String source, String delim) {
        List<Integer> list = new ArrayList<Integer>();
        if (isEmpty(source)) {
            return list;
        }

        StringTokenizer st = new StringTokenizer(source, delim);
        while (st.hasMoreTokens()) {
            String val = st.nextToken().trim();
            if (!isEmpty(val)) {
                list.add(Integer.valueOf(val));
            }
        }
        return list;
    }

    /**
     * 将content中的preStr字符串用postStr字符串替换
     * 
     * @param content
     *            要处理的字符串
     * @param preStr
     *            要替换的字符串
     * @param postStr
     *            替换后的字符
     * @return 如果content为空或者preStr为空或者postStr为空，返回content
     */
    public static String replace(String content, String preStr, String postStr) {
        if (isEmpty(content) || isEmpty(preStr) || postStr == null) {
            return content;
        }
        StringBuffer sb = new StringBuffer(content);
        int pos = -1;
        while ((pos = sb.indexOf(preStr)) >= 0) {
            sb.replace(pos, pos + preStr.length(), postStr);
        }

        return sb.toString();
    }


    /**
     * 将数组以delim分割,组成字符串,支持整形数字，字符串数组
     * 
     * @param source
     *            对象数组
     * @param delim
     *            分割符
     * @return 字符
     */
    public static String combo(Object[] source, String delim) {
        if (source == null || source.length < 1) {
            return "";
        }
        if (delim == null || delim.trim().equals("")) {
            delim = ",";
        }
        String tmp = "";
        for (int i = 0; i < source.length; i++) {
            if (!isEmpty(source[i])) {
                tmp += String.valueOf(source[i]) + delim;
            }
        }
        if (tmp.endsWith(delim)) {
            tmp = tmp.substring(0, tmp.length() - delim.length());
        }

        return tmp;
    }

    /**
     * 从字符串中提取出大字字符
     * 
     * @param source
     *            要处理的字符串
     * @return 如果source为null返回""
     */
    public static String getUpperCase(String source) {
        if (source == null) {
            return "";
        }
        String tmp = "";
        for (int i = 0; i < source.length(); i++) {
            if ((source.charAt(i) >= 'A' && source.charAt(i) <= 'Z')
                    || (source.charAt(i) >= '0' && source.charAt(i) <= '9')) {
                tmp += source.charAt(i);
            }
        }
        return tmp;
    }

    /**
     * 处理电话号码，删除电话号码前部的所有0
     * 
     * @param phone
     *            要处理的电话号码
     * @return 如果phone为空，返回null
     */
    public static String dealPhone(String phone) {
        if (isEmpty(phone)) {
            return null;
        } else {
            phone = phone.trim();
            boolean findNonezero = false;
            String dest = "";
            int length = phone.length();
            for (int i = 0; i < length; i++) {
                char c = phone.charAt(i);
                if (c == '0') {
                    if (findNonezero) {
                        dest += c;
                    }
                } else {
                    if (c == '(') {
                        continue;
                    }
                    if (c == '*') {
                        break;
                    }
                    findNonezero = true;
                    if (c >= '0' && c <= '9') {
                        dest += c;
                    }
                }
            }

            phone = dest;
        }
        return phone;
    }


    /**
     * 处理字符串pres，将pres中的字符c用，字符rs替换
     * 
     * @param pres
     *            要处理的字符串
     * @param c
     *            搜索替换的字符
     * @return 如果pres为空，返回""
     */
    public static String replaceString(String pres, String c, String rs) {
        if (isEmpty(pres)) {
            return "";
        }

        StringBuffer sb = new StringBuffer(pres);
        int pos = -1;
        while ((pos = pres.indexOf(c)) >= 0) {
            sb.replace(pos, pos + 1, rs);
        }
        return sb.toString();
    }

    /**
     * 切断字符串val，删除从开始位置到count位置的字符串
     * 
     * @param val
     *            源字符串
     * @param count
     *            要切断的最后位置
     * @return 如果val为空返回去掉前后空格和回车的val，如果count大于val的长度返回""
     */
    public static String cutString(String val, int count) {
        if (isEmpty(val)) {
            return trim(val);
        }
        if (val.length() <= count) {
            return "";
        }
        return val.substring(0, val.length() - count);
    }

    /**
     * 获取从str字符串的开始位置到最后一个div所在位置字符串
     * 
     * @param str
     *            所要处理的字符串
     * @param div
     *            要搜索的字符
     * @return 从str字符串的开始位置到最后一个div所在位置字符串
     */
    public static String getLastDivBeforeName(String str, String div) {
        int lastDot = str.lastIndexOf(div);
        if (lastDot == -1) {
            return "";
        } else {
            return str.substring(0, lastDot);
        }
    }

    /**
     * 取最后div后面的字符
     * 
     * @param str
     *            目标字符串
     * @param div
     *            要搜索的字符串
     * @return 最后div后面的字符
     */
    public static String getLastDivAfterName(String str, String div) {
        int lastDot = str.lastIndexOf(div);
        if (lastDot == -1) {
            return "";
        } else {
            return str.substring(lastDot + 1, str.length());
        }
    }

    /**
     * 获取str中以div拆分的前部字符串
     * 
     * @param str
     *            要搜索的字符串
     * @param div
     *            拆分字符
     * @param count
     * @return 如果str为空返回""
     */
    public static String getDivBeforeName(String str, String div, int count) {
        if (isEmpty(str)) {
            return "";
        }
        int i = 0, pos = -1;
        while ((pos = str.indexOf(div, pos + 1)) >= 0) {
            i++;
            if (i > count) {
                str = str.substring(0, pos);
                break;
            }
        }
        return str;
    }

    /**
     * 组织key, value配对
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @return 如果value为null返回key + 换行回车值，反之返回key=value + 换行回车
     */
    public static String getKeyValue(String key, String value) {
        return key + (value == null ? "" : ("=" + value)) + "\r\n";
    }

    /**
     * 判断两个字串在去掉多余空格之后是否相等
     * 
     * @param str1
     *            字符串1
     * @param str2
     *            字符串2
     * @return 如果str1和str2都为null返回true，如果str1和str2有一个null返回false
     */
    public static boolean equalStr(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.trim().equals(str2.trim());
    }

    /**
     * 判断两个对象是否相等，该对象必须有equals方法
     * 
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @return 如果obj1和obj2都为null返回true，如果obj1和obj2有一个null返回false
     */
    public static boolean equalObject(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    /**
     * 返回字符串字节数组的长度
     * @param s 字符串
     * @return int 长度值
     */
    private static int getByteLen(String s) {
        if (s == null) {
            return 0;
        }
        return s.getBytes().length;
    }
    
    
    /**
     * 将HTML字符串中的特殊字符使用转义字符串代替
     * 
     * @param string
     *            HTML字符串
     * @return 如果string为空返回""
     */
    public static String escapeHTMLString(String string) {
        return escapeHTMLString(string, "");
    }

    /**
     * 将HTML字符串中的特殊字符使用转义字符串代替,如果输入字符串为空,则返回displayThisIfNull
     * 
     * @param string
     *            HTML字符串
     * @param displayThisIfNull
     *            string为空要显示的字符串
     * @return 如果string为空返回displayThisIfNull
     */
    public static String escapeHTMLString(String string, String displayThisIfNull) {
        if (string == null || string.equals("")) {
            return displayThisIfNull;
        }
        String str = string;
        int index;
        for (index = str.indexOf("&", 0); index != -1; index = str.indexOf("&", index + 5)) {
            str = str.substring(0, index) + "&amp;" + str.substring(index + 1);
        }

        for (index = str.indexOf("<", 0); index != -1; index = str.indexOf("<", index + 4)) {
            str = str.substring(0, index) + "&lt;" + str.substring(index + 1);
        }
        for (index = str.indexOf(">", 0); index != -1; index = str.indexOf(">", index + 4)) {
            str = str.substring(0, index) + "&gt;" + str.substring(index + 1);
        }
        // for (index = str.indexOf("\n", 0); index != -1; index =
        // str.indexOf("\n", index + 4))
        // {
        // str = str.substring(0, index) + "<BR>" + str.substring(index + 1);
        // }
        return str;
    }

    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////字符串     /////////////////////////
    
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// /////////////////////////
    /**
     * 返回排序后(根据语言进行排序)的集合
     * @param list  要排序的集合 list不支持int，integer,long等数字型.如list.add(1)不可以,list.add("1")可以
     * @param locale Locale对象 <br>&nbsp;&nbsp;&nbsp;&nbsp;java.util.Locale locale=new  Locale("en", "US"); 或request.getLocale()
     * 
     * @return
     */
//    public static Collection<T> getSortedCollection(List<T> list,Locale locale) {
//        if (isEmpty(list)) {
//            return list;
//        }
//        
//        TreeSet<T> ts = new TreeSet<T>(Collator.getInstance(locale));        
//        ts.addAll(list);
//        return ts;
//    }
    
    
    /**
     * 将字符串以指定分隔符 分隔 以字符串数组形式返回
     * @param source 字符串
     * @param spliter 分隔符
     * @return String[]
     */
    public static String[] extractStr(String source, String spliter) {
    	if(null == source || source.equals("")){
    		return new String[0];
    	}
    	ArrayList<String> result = new ArrayList<String>();
    	String[] args = source.split(spliter);
    	for(String item : args){
    		if(null != item && item.trim().equals("") == false){
    			result.add(item);
    		}
    	}
    	String[] arr_result = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
        	arr_result[i] = result.get(i);
		}
        return arr_result;
    }
    
    /**
     * 将一个字串拆分成相应的值对并保存在一个Map中， 如"a=1,b=2, c=3"
     * 
     * @param source
     *            待拆分字串
     * @param delim1
     *            第一分隔符
     * @param delim2
     *            第二分隔符
     * @return 返回一个Map对象，它不为null;其健值为小写、首尾没有多余空格的Strig对象
     */
    public static Map<String, String> split(String source, String delim1, String delim2, boolean toLowcase) {
		Map<String, String> map = new HashMap<String, String>();
        String[] sv = split(source, delim1);
        for (int i = 0; i < sv.length; i++) {
            String[] tmp = split(sv[i], delim2);
            if (toLowcase) {
                map.put(tmp[0].trim().toLowerCase(), tmp.length < 2 ? "" : tmp[1].trim().toLowerCase());
            } else {
                map.put(tmp[0].trim(), tmp.length < 2 ? "" : tmp[1].trim());
            }
        }
        return map;
    }
    
    
    /**
     * 仅获得日期中的时间
     * 
     * @param date
     *            输入的日期
     * @return 日期中的时间
     */
    public static String getTime(Date date) {
        if (date == null) {
            return "00:00";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        String time = (hour < 10 ? "0" + hour : "" + hour) + ":" + (min < 10 ? "0" + min : "" + min);
        return time;
    }
    
    /**
     * 验证字符串是否是Ingeger数据类型
     * 
     * @param value
     *            需验证的字符串
     * @return 如果是返回true，反之返回false
     */
    public static boolean checkIsNumber(String value) {
        boolean isNumber = true;
        try {
            new Integer(value);
        } catch (Exception e) {
            isNumber = false;
        }
        return isNumber;
    }
    public static String convert(String s){
        String result;
        byte[] temp ;
        try{
        	temp = s.getBytes("iso-8859-1");
            result =  new String(temp,"utf-8");
        }catch(Exception e){
        	return null;
        }
        return result;
    }
    
    public static void main(String args[]){

    	System.out.println(getEncryptPassword("12345"));
    }
}
