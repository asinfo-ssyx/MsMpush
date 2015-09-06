package com.asiainfo.ssyx.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * @author Administrator
 *
 */
public class StringUtil {

	private static final String DEFAULT_FILTERED_CHAR = "`~\\:;\"'<,>./";
	
	private static final String[] DEFAULT_INVALID_STR = new String[] {
			"script", "and ", "or ", "union ", "between ", "\"", "\\", "\\t",
			"insert|values", "select|from", "update|set", "delete|from",
			"drop", "where", "alter" };

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return boolean
	 * 
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() < 1);
	}

	/**
	 * 判断字符串是否为非空
	 * 
	 * @param str
	 * @return boolean
	 * 
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * Object转化为字符串
	 * 
	 * @param obj
	 * @return String
	 * 
	 */
	public static String obj2Str(Object obj) {
		return obj == null ? "" : obj.toString().trim();
	}
	
	/**
	 * Object转化为字符串，设置默认值，如果为空返回默认值
	 * @param obj		对象
	 * @param defaultValue	默认值
	 * @return
	 * String
	 *
	 */
	public static String obj2Str(Object obj, String defaultValue) {
		return obj == null ? defaultValue : obj.toString().trim();
	}

	/**
	 * 字符串转化为Integer类型
	 * 
	 * @param str
	 * @return Integer
	 * 
	 */
	public static Integer string2Integer(String str) {
		if (isNotEmpty(str)) {
			try {
				return new Integer(str.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 字符串转化为Integer类型,为空则返回默认值
	 * @param str
	 * @param defaultValue	默认值
	 * @return
	 * Integer
	 *
	 */
	public static Integer string2Integer(String str, Integer defaultValue) {
		if (isNotEmpty(str)) {
			try {
				return new Integer(str.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return defaultValue;
			}
		}
		return defaultValue;
	}

	/**
	 * 字符串转化为Long类型
	 * 
	 * @param str
	 * @return Long
	 * 
	 */
	public static Long string2Long(String str) {
		if (isNotEmpty(str)) {
			try {
				return new Long(str.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 
	 * 字符串转化为Long类型,为空则返回默认值
	 * @param str
	 * @param defaultValue	默认值
	 * @return
	 * Long
	 *
	 */
	public static Long string2Long(String str, Long defaultValue) {
		if (isNotEmpty(str)) {
			try {
				return new Long(str.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return defaultValue;
			}
		}
		return defaultValue;
	}

	/**
	 * 字符串转化为Double类型
	 * 
	 * @param str
	 * @return Double
	 * 
	 */
	public static Double stringToDouble(String str) {
		if (isNotEmpty(str)) {
			try {
				return new Double(str.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 
	 * 字符串转化为Double类型,为空则返回默认值
	 * @param str
	 * @param defaultValue	默认值
	 * @return
	 * Double
	 *
	 */
	public static Double stringToDouble(String str, Double defaultValue) {
		if (isNotEmpty(str)) {
			try {
				return new Double(str.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return defaultValue;
			}
		}
		return defaultValue;
	}

	/**
	 * 字符串转化为BigDecimal类型
	 * 
	 * @param str
	 * @return BigDecimal
	 * 
	 */
	public static BigDecimal string2BigDecimal(String str) {
		if (isNotEmpty(str)) {
			try {
				return new BigDecimal(str);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 
	 * 字符串转化为BigDecimal类型,为空则返回默认值
	 * @param str
	 * @param defaultValue	默认值
	 * @return
	 * BigDecimal
	 *
	 */
	public static BigDecimal string2BigDecimal(String str,BigDecimal defaultValue) {
		if (isNotEmpty(str)) {
			try {
				return new BigDecimal(str);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return defaultValue;
			}
		}
		return defaultValue;
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return boolean
	 * 
	 */
	public static boolean isDecimal(String str) {
		boolean res = true;
		if (isEmpty(str)) {
			return false;
		}
		try {
			new BigDecimal(str);
		} catch (NumberFormatException e) {
			res = false;
		}
		return res;
	}
	
	/**
	 * 
	 * 将字符串格式化为数字形式的字符串
	 * @param value	初始字符串
	 * @return
	 * String	数字形式的字符串
	 *
	 */
	public static String numberFormat(String value) {
		if (!isEmpty(value)) {
			NumberFormat format = NumberFormat.getInstance();
			return format.format(Double.parseDouble(value));
		}
		return null;
	}
	
	
	/**
	 * 判断是否是中文字符,判断中文的"“"、"。"、"，"号
	 * @param c	
	 * @return
	 * boolean
	 *
	 */
	// GENERAL_PUNCTUATION 判断中文的“号
	// CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
	// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
				|| (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否是中文字符串
	 * 
	 * @param str
	 * @return
	 * boolean
	 *
	 */
	public static boolean isChinese(String str) {
		if(isEmpty(str)) {
			return false;
		}
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c) == true) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 比较两个字符串是否相等
	 * @param str1
	 * @param str2
	 * @return
	 * boolean
	 *
	 */
	public static boolean stringEquals(String str1, String str2) {
		if (isEmpty(str1) && isEmpty(str2)) {
			return true;
		}
		// str1不等于null
		if (isNotEmpty(str1)) {
			return str1.equals(str2);
		}
		// str2肯定不为null
		return false;
	}

	/**
	 * 
	 * 将字符串数组转换成用逗号分割的字符串
	 * @param strArray	字符串数组
	 * @return 以逗号分隔的字符串
	 *
	 */
	public static String array2Str(Object[] strArray) {
		String str = "";
		for (int i = 0; i < strArray.length; i++) {
			str += strArray[i].toString() + ",";
		}
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}
	
	/**
	 * 将逗号分割的字符串转换成字符串数组
	 * @param str 以逗号分隔的字符串
	 * @return 字符串数组
	 */
	public static String[] str2Array(String str)
	  {
	  	 Vector vect = (Vector)getVector(str,",");
		 int num = vect.size();
		 String strArray[] = new String[num];
		 for(int i=0;i<num;i++)
		 {
		 	strArray[i] = (String)vect.elementAt(i);
		 }
		 return strArray;
	   }
	
	/**
	 * 将split指定分隔符分隔的字符，转换成set
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set commaString2Set(String commaString, String split) {
		Set s = new HashSet();
		if (isNotEmpty(commaString)) {
			String[] arr = commaString.split(split);
			for (int i = 0; i < arr.length; i++) {
				s.add(arr[i].trim());
			}
		}
		return s;
	}
	/**
	 * 将split指定分隔符分隔的字符，转换成set，默认用","分隔
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set commaString2Set(String commaString) {
		return commaString2Set(commaString,",");
	}
	/**
	 * 将'1232','234','234'或者1232,234,234类型的字符串转化为list
	 * @param str			'1232','234','234'或者1232,234,234类型的字符串,","为分隔符
	 * @param splitStr		分隔符，如","
	 * @param removeComma	是否移除单引号"'"，如果有则设置为true，没有设置为false
	 * @return
	 * List<String>
	 *
	 */
	public static List<String> string2List(String str, String splitStr,
			boolean removeComma) {
		List<String> list = new ArrayList<String>();
		int pos = str.indexOf(splitStr);
		boolean hasSplit = false;
		if(pos>=0) {
			hasSplit =true;
		}
		while (pos >= 0) {
			String obj = str.substring(0, pos);
			if (removeComma) {
				obj = obj.substring(1, obj.length() - 1);
			}
			list.add(obj);
			str = str.substring(pos + 1, str.length());
			pos = str.indexOf(splitStr);
		}
		if(hasSplit) {
			if (removeComma) {
				str = str.substring(1, str.length() - 1);
			}
			list.add(str);
		}
		return list;
	}
	/**
	 * 
	 * 判断字符串target是否在指定的字符之间 不区分大小写 比如判断"from"是否在"()"之中 对于语句：select count(*)
	 * from table where (1=1); return false 对于语句：(select count(*) from table
	 * where (1=1)); return true 对于语句：select * from table; return true
	 * 
	 * @param str
	 * @param target	
	 * @param pos		target所在字符串str的位置，如果设置错误会抛异常
	 * @param begin
	 * @param end
	 * @return
	 * @throws Exception
	 * boolean
	 *
	 */
	public static boolean contains(String str, String target, int pos, char begin,
			char end) throws Exception {
		int b = str.indexOf(begin);
		int e = str.indexOf(end);
		if ((b < 0) || (e < 0)) {
			return false;
		}

		int len = target.length();
		System.out.println(str.length() + ":" + pos + ":" + len);
		String s = str.substring(pos, pos + len);
		if (!s.equalsIgnoreCase(target)) {
			throw new Exception("string['" + target + "]location:[" + pos + "]Unspecified error");
		}

		String frontStr = str.substring(0, pos);
		String backStr = str.substring(pos + len + 1);
		// System.out.println("frontStr=["+frontStr+"],backStr=["+backStr+"]");

		int endCount = 0;
		int beginCount = 0;
		// 判断是否存在不匹对的begin字符
		boolean existBegin = false;
		for (int i = 0; i < frontStr.length(); i++) {
			char c = frontStr.charAt(i);
			if (c == begin) {
				beginCount++;
			}
			if (c == end) {
				endCount++;
			}
		}
		if ((beginCount - endCount) > 0) {
			existBegin = true;
		}

		endCount = 0;
		beginCount = 0;
		// 判断是否存在不匹对的end字符
		boolean existEnd = false;
		for (int i = 0; i < backStr.length(); i++) {
			char c = backStr.charAt(i);
			if (c == begin) {
				beginCount++;
			}
			if (c == end) {
				endCount++;
			}
		}
		if ((endCount - beginCount) > 0) {
			existEnd = true;
		}
		return existBegin && existEnd;
	}

	/**
	 * 在str中查找不在begin和end之间的target的位置 比如，在sql中找不在括号内的from关键字的位置 不区分大小写
	 * 
	 * @param str
	 * @param target
	 * @param begin
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public static int getPosNotIn(String str, String target, char begin,
			char end) throws Exception {
		String opStr = str;
		int pos = 0;// 在opStr中的位置
		int modify = 0;// str4和opStr长度差值
		boolean inIf = false;
		while (opStr.toLowerCase().indexOf(target.toLowerCase()) >= 0) {
			System.out.println("opStr=[" + opStr + "]");
			pos = opStr.toLowerCase().indexOf(target.toLowerCase());
			if (!contains(str, target, pos + modify, begin, end)) {
				inIf = true;
				break;
			}
			opStr = opStr.substring(pos + target.length());
			modify += pos + target.length();
			System.out.println("modify=" + modify);
		}

		if (!inIf) {
			return -1;
		} else {
			pos = modify + pos;
			System.out.println(str.substring(pos, pos + target.length())
					+ " found,pos=[" + pos + "],modify=[" + modify + "]");
			return pos;
		}
	}

	/**
	 * 
	 * 去除字符串间的空格
	 * @param s
	 * @return
	 * String
	 *
	 */
	public static String replaceAllBlank(String s) {
		if (isEmpty(s)) {
			return "";
		}
		return s.replaceAll("\\s", "");
	}
	
	/**
	 * 字符串替换，将 source 中的 oldString 全部换成 newString
	 * @param source 源字符串
	 * @param oldString 老的字符串
	 * @param newString 新的字符串
	 * @return 替换后的字符串
	 */
	public static String replaceAll(String source, String oldString, String newString) {
		if ( (source == null) || (source.equals("")) ) {
			return "";
		}
		StringBuffer output = new StringBuffer();

		int lengthOfSource = source.length();   // 源字符串长度
		int lengthOfOld = oldString.length();   // 老字符串长度

		int posStart = 0;   // 开始搜索位置
		int pos;            // 搜索到老字符串的位置

		while ((pos = source.indexOf(oldString, posStart)) >= 0) {
			output.append(source.substring(posStart, pos));

			output.append(newString);
			posStart = pos + lengthOfOld;
		}

		if (posStart < lengthOfSource) {
			output.append(source.substring(posStart));
		}

		return output.toString();
	}

	/**
	 * 将字符串数组转化为以逗号分隔的字符串
	 * @param s
	 * @return
	 * String	逗号分隔的字符串
	 *
	 */
	public static String arr2CommaString(String[] s) {
		if (s == null || s.length < 1) {
			return "";
		}
		String result = s[0];
		if (s.length > 1) {
			for (int i = 1; i < s.length; i++) {
				result += ("," + s[i]);
			}
		}
		return result;
	}

	/**
	 * 构造字符串，将列表中的字符串，用分隔符连成一个字符串
	 * 
	 * @param list
	 *            List 字符串（String）列表
	 * @param splitStr
	 *            String 分隔符
	 * @return String
	 */
	public static String list2StringWithSplit(List list, String splitStr) {
		if (list == null || list.size() < 1)
			return null;

		StringBuffer buf = new StringBuffer();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			buf.append(splitStr);
			buf.append((String) iter.next());
		}
		return buf.toString().substring(1);
	}

	/**
	 * 构造字符串，将列表中的字符串，用分隔符连成一个字符串 每个字符串加上单引号 查询DATABASE用
	 * 
	 * @param list  Collection
	 * @param splitStr
	 *            String
	 * @return String
	 */
	public static String list2StringWithComma(Collection list, String splitStr) {
		if (list == null || list.size() < 1)
			return null;

		StringBuffer buf = new StringBuffer();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			buf.append(splitStr);
			buf.append("'");
			buf.append((String) iter.next());
			buf.append("'");
		}

		return buf.toString().substring(1);
	}
	
	/**
	 * 删除某个字串中需要被过滤的字符 
	 * 
	 * @param src
	 * @param filterChar
	 * @return
	 */
	public static String filterStr(String src, String filterChar) {
		if (isEmpty(src))
			return "";
		src = src.trim();
		if (filterChar == null || filterChar.length() < 0) {
			filterChar = DEFAULT_FILTERED_CHAR;
		}
		int len = filterChar.length();
		for (int i = 0; i < len; i++) {
			src = src.replaceAll("\\" + String.valueOf(filterChar.charAt(i)), "");
		}
		return src;
	}

	/**
	 * 判断字串中是否包含非法字串 added by wwl 2007-6-15
	 * 
	 * @param src
	 * @param invalidStr
	 * @return
	 */
	public static boolean isContainInvalidStr(String src, String invalidStr) {
		boolean res = false;
		if (isEmpty(src)) {
			return res;
		}
		// 使用自定义非法字串进行检查
		if (invalidStr != null && invalidStr.length() > 0) {
			res = (src.indexOf(invalidStr) >= 0) ? true : false;
		}
		// 使用系统内置非法字串进行检查
		else {
			int len = DEFAULT_INVALID_STR.length;
			src = src.toLowerCase();
			String tmpInvalidStr;
			String[] tmpArr;
			boolean tmpBol;
			for (int i = 0; i < len; i++) {
				tmpInvalidStr = DEFAULT_INVALID_STR[i];
				// System.out.println(i + "." + tmpInvalidStr);
				// 多个关联非法字串
				if (tmpInvalidStr.indexOf("|") >= 0) {
					tmpBol = false;
					tmpArr = tmpInvalidStr.split("[|]");
					for (int j = 0; j < tmpArr.length; j++) {
						// System.out.println(i + "." + j + " " + tmpArr[j]);
						// 每个关联非法字串都在源字串中，才能判断整个源字串是非法的
						if (src.indexOf(tmpArr[j]) >= 0) {
							System.out.println("invalid str [" + tmpArr[j]
									+ "] exist,");
							tmpBol = true;
						}
						// 有一个关联非法字串不在源字串中，源字串就是合法的
						else {
							tmpBol = false;
							break;
						}
					}
					if (tmpBol) {
						res = true;
						break;
					}
				}
				// 单个非法字串
				else {
					if (src.indexOf(tmpInvalidStr) >= 0) {
						System.out.println("invalid str [" + tmpInvalidStr
								+ "] exist, quit");
						res = true;
						break;
					}
				}
			}
		}

		return res;
	}

	/**
	 * 将字符串中的单引号"'"改为两个单引号"''"，用于数据库操作
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	public static String convertComma2Db(String str) {
		int pos = str.indexOf("'");
		int pos1 = 0;
		while (pos != -1) {
			str = str.substring(0, pos1 + pos) + "'"
					+ str.substring(pos + pos1, str.length());
			pos1 = pos1 + pos + 2;
			pos = str.substring(pos1, str.length()).indexOf("'");
		}
		return str;
	}

	/**
	 * 把型如"12,23,34,56"的字串转换为"'12','23','34','56'"能在SQL中使用的字串
	 * 
	 * @param source
	 *            String
	 * @return String
	 */
	public static String addInvertedComma(String source) {
		if (isEmpty(source))
			return null;
		source = "'" + source.trim();
		int pos = source.indexOf(",");
		int len = source.length();
		while (pos != -1) {
			source = source.substring(0, pos) + "','"
					+ source.substring(pos + 1, len);
			len += 2;
			pos += 2;
			if (source.substring(pos).indexOf(",") != -1)
				pos = source.substring(pos).indexOf(",") + pos;
			else
				break;
		}
		source += "'";
		return source;
	}

	/**
	 * 
	 * 按起始、终止字符将字符串分段，每段中的字符串以分隔符分割，
	 * 保存到vector，再将每段得到的vector存放到结果vector返回。<br />
	 * 如字符串"[123;456][789;012]<br />
	 * 先按起始、终止字符分成[123;456]和[789;012]两段<br />
	 * 然后以";"分别分割两段字符串，得到的结果"123","456"放到一个vector对象v1，"789"，"012"放到一个vector对象v2<br />
	 * 最后将v1、v2放到最后的返回结果vector中
	 * @param str
	 * @param beginStr	起始字符串
	 * @param endStr	终止字符串
	 * @param splitStr	分段字符串中的分隔符
	 * @return
	 * Vector
	 *
	 */
	public static Vector<Vector<String>> getVectorBySegment(String str, String beginStr,
			String endStr, String splitStr) {
		Vector<Vector<String>> vect = new Vector<Vector<String>>();
		String strTemp = "";
		int bpos = str.indexOf(beginStr);
		int epos = str.indexOf(endStr);
		while (bpos >= 0 && epos > 0) {
			strTemp = str.substring(bpos + 1, epos);
			Vector<String> subvect = getVector(strTemp, splitStr);
			vect.addElement(subvect);
			str = str.substring(epos + 1, str.length());
			bpos = str.indexOf(beginStr);
			epos = str.indexOf(endStr);
		}
		return vect;
	}
	
	/**
	 * 将一个字符串按照指定的分割符进行分割
	 * 
	 * @param str
	 *            String
	 * @param splitStr
	 *            String
	 * @return Vector
	 */
	public static Vector<String> getVector(String str, String splitStr) {
		Vector<String> vect = new Vector<String>();
		int pos = str.indexOf(splitStr);
		int len = splitStr.length();
		while (pos >= 0) {
			vect.addElement(str.substring(0, pos));
			str = str.substring(pos + len, str.length());//huangc modify 20100423 pos+1改为pos+len
			pos = str.indexOf(splitStr);
		}
		vect.addElement(str.substring(0, str.length()));
		return vect;
	}
	// {0 88 { }} {88 128 {{000080 20} {000070 30}}}
	// {128 228 {{000080 40} {000070 50}}} {228 -1 {{000070 150} {000080 100}}}
//	public static Vector getVector3(String str) {
//		Vector vect = new Vector();
//		int lnum = 0;
//		int rnum = 0;
//		int bpos = 0;
//		int epos = 0;
//		boolean bFirst = true;
//		int len = str.length();
//		byte[] bstr = str.getBytes();
//		int i = 0;
//		while (i < len) {
//			if (bstr[i] == '{') {
//				lnum++;
//				if (bFirst) {
//					bpos = i;
//					bFirst = false;
//				}
//			}
//			if (bstr[i] == '}')
//				rnum++;
//			if (lnum == rnum && lnum != 0 && rnum != 0) {
//				epos = i;
//				vect = getSubVect(str.substring(bpos + 1, epos), vect);
//				lnum = 0;
//				rnum = 0;
//				bFirst = true;
//			}
//			i++;
//		}
//		return vect;
//	}

//	private static Vector getSubVect(String str, Vector vect) {
//		int bpos = 0;
//		int epos = str.indexOf(" ");
//		String strBegin = str.substring(bpos, epos);
//		// System.out.println("begin:"+strBegin);
//		str = str.substring(epos + 1);
//		epos = str.indexOf(" ");
//		String strEnd = str.substring(bpos, epos);
//		// System.out.println("end:"+strEnd);
//		str = str.substring(epos + 2, str.length() - 1);
//		// System.out.println(str);
//		if (str.length() < 1 || str.equals(" ")) {
//			Vector subvect = new Vector();
//			subvect.addElement(strBegin);
//			subvect.addElement(strEnd);
//			subvect.addElement("");
//			subvect.addElement("");
//			vect.addElement(subvect);
//		} else {
//			byte[] bstr = str.getBytes();
//			int len = str.length();
//			int i = 0;
//			String strTemp = "";
//			boolean bFirst = true;
//			bpos = 0;
//			epos = 0;
//			while (i < len) {
//				if (bstr[i] == '{')
//					bpos = i;
//				if (bstr[i] == '}')
//					epos = i;
//				// System.out.println(i+":"+bpos+" "+epos);
//				if (bpos >= 0 && epos > 0) {
//					strTemp = str.substring(bpos + 1, epos);
//					// System.out.println("temp:"+strTemp);
//					Vector subvect = new Vector();
//					subvect.addElement(strBegin);
//					subvect.addElement(strEnd);
//					int pos = strTemp.indexOf(" ");
//					subvect.addElement(strTemp.substring(0, pos));
//					strTemp = strTemp.substring(pos + 1);
//					subvect.addElement(strTemp
//							.substring(strTemp.indexOf(" ") + 1));
//					vect.addElement(subvect);
//					bpos = 0;
//					epos = 0;
//				}
//				i++;
//			}
//		}
//		return vect;
//	}


	/**
	 * 
	 * 为一个字符串的左边添加字符串strspace,以满足strlen要求修改后的长度
	 * @param str		
	 * @param strspace	左边添加的字符
	 * @param strlen	要求长度
	 * @return
	 * String
	 *
	 */
	public static String addChar4Len(String str, String strspace, int strlen) {
		if (str == null || str.length() < 1 || strspace==null || strspace.length()<1)
			return str;
		while (str.length() < strlen) {
			if(str.length() + strspace.length()>strlen) {
				return str;
			}
			str = strspace + str;
		}
		return str;
	}

	/**
	 * 将字符串的集合转换成用指定分隔符分割的字符串，addComma参数是指定字符串是否添加单引号
	 * @param collection
	 * @param separator	分隔符，为空是默认为逗号
	 * @param addComma	指定字符串是否添加单引号
	 * @return
	 * String	
	 *
	 */
	public static String list2String(Collection collection, String separator,
			boolean addComma) {
		String str = "";
		if (null == collection || collection.size() < 1) {
			return str;
		}
		if (null == separator || separator.length() < 1) {
			separator = ",";
		}
		Iterator it = collection.iterator();
		while(it.hasNext()) {
			str += (addComma ? "'" : "") + obj2Str(it.next(),"")
			+ (addComma ? "'" : "") + separator;
		}
		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
	/**
	 * 
	 * 将多个list中的数据合并,剔除重复的数据
	 * @param lists
	 * @return
	 * List
	 *
	 */
	public static List<Object> getDistinctList(List[] lists) {
		List<Object> retList = new ArrayList<Object>();
		Map<Object,Object> map = new HashMap<Object,Object>();
		for (int i = 0; i < lists.length; i++) {
			List<Object> list = (List) lists[i];
			if(list==null) {
				continue;
			}
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) != null) {
					map.put(list.get(j), list.get(j));
				}
			}
		}
		Iterator<Object> it = map.keySet().iterator();
		while (it.hasNext()) {
			retList.add(it.next());
		}
		return retList;
	}

//	public static List arry_to_list(Object[] obj, String partition,
//			boolean addComma) {
//		List retList = new ArrayList();
//		retList = str_to_list(array_to_str(obj), partition, addComma);
//		return retList;
//	}

	/**
	 * 
	 * 根据身份证号码获取性别(返回值：1－男，2－女，空为身份证号码错误)
	 * 
	 * @param iDCard
	 *            身份证号
	 * @return String 返回值：1－男，2－女，空为身份证号码错误
	 * 
	 */
	public static String getGender(String iDCard) {
		int gender = 3;
		if (iDCard.length() == 15) {
			gender = (new Integer(iDCard.substring(14, 15))).intValue() % 2;
		} else if (iDCard.length() == 18) {
			int number17 = (new Integer(iDCard.substring(16, 17))).intValue();
			gender = number17 % 2;
		}
		if (gender == 1) {
			return "1";
		} else if (gender == 0) {
			return "2";
		} else {
			return "";
		}
	}

	/**
	 * 检测是否为合法的E-MAIL格式
	 * 
	 * @param emailStr	
	 * @return boolean
	 * 
	 */
	public static boolean checkEmail(String emailStr) {
		if(isEmpty(emailStr)) {
			return false;
		}
		return emailStr.matches("[-_.a-zA-Z0-9]+@[-_a-zA-Z0-9]+.[a-zA-Z]+");
	}

	/**
	 * 检测是否是字母,数字,下划线
	 * 
	 * @param v
	 * @return boolean
	 * 
	 */
	public static boolean checkStr(String v) {
		if(isEmpty(v)) {
			return false;
		}
		return v.matches("[a-zA-Z0-9_]*");
	}
	
	/**
	 * 将字符串进行MD5加密,空字符串直接返回""
	 * @param value
	 * @return
	 * @throws Exception
	 * String	加密后的字符串
	 *
	 */
	public static String encryptMD5(String value) throws Exception {
		String result = "";
		if (isEmpty(value)) {
			return "";
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(value.getBytes());
			result = byte2hex(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}

	private static String byte2hex(byte[] bytes) {
		String result = "";
		String stmp = "";
		for (int n = 0; n < bytes.length; n++) {
			stmp = (java.lang.Integer.toHexString(bytes[n] & 0xFF));
			if (stmp.length() == 1) {
				result = result + "0" + stmp;
			} else {
				result = result + stmp;
			}
		}
		return result.toUpperCase();
	}
	
	
	public static String format4InvertedComma(String sourceString) {
		return sourceString.replaceAll("'", "''");
	}
	
	public static void main(String[] args){
		String testStr="aa;bb;cc|dd;ee;ff";
		Vector v=StringUtil.getVector(testStr, "|");
		for(int i=0;i<v.size();i++){
			System.out.println(v.get(i));
		}
		
		String strPath="file:/crmtest/product/suite400/webapp/WEB-INF/lib/aibi-waterMark-1.2.0-SNAPSHOT.jar!/META-INF/MANIFEST.MF";
		strPath=strPath.substring(5, strPath.indexOf("!"));
		System.out.println(strPath);
		strPath = strPath.substring(0, strPath.lastIndexOf("/")+1);
		System.out.println(strPath);
	}
}
