package com.asiainfo.ssyx.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.PropertyConfigurator;

/**
 * @Title: ToolsUtil
 * @Copyright： Copyright(c) 2010
 * @Company: 亚信联创科技(中国)有限公司
 * @author wangbq
 * @date 2011-9-28
 * @version 1.0.0
 */
public class ToolsUtil {
	
    /**
	 * 取得当前时间
	 * 
	 * @return
	 */
	public static String getNowTime() {
		Calendar cn = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = sdf.format(cn.getTime());
		return nowTime;
	}
	
    /**
	 * 根据format取得当前日时
	 * 
	 * @return
	 */
	public static String getNowTime(String format) {
		Calendar cn = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String nowTime = sdf.format(cn.getTime());
		return nowTime;
	}

	/**
	 * 主要把从数据库中取出的字符转换为程序中可认的中文字符
	 * @param change  需要转换的数据库字符
	 * @return String　转换后的程序中的中文字符
	 * @throws UnsupportedEncodingException　不能转换时抛出的异常
	 */
	public synchronized static String fromDatabase(String change)
			throws UnsupportedEncodingException {
		return new String(change.getBytes("iso-8859-1"), "gb2312");
	}

	/**
	 * 主要把程序中可认的中文字符转换为存入数据库中的字符
	 * 
	 * @param change
	 *            　需要转换的程序中的中文字符
	 * @return　　　　　转换后的数据库字符
	 * @throws UnsupportedEncodingException
	 *             　不能转换时抛出的异常
	 */
	public synchronized static String toDatabase(String change)
			throws UnsupportedEncodingException {
		return new String(change.getBytes("gb2312"), "iso-8859-1");
	}
	
	public  static String utf8Togbk(String change){
		
		String newStr = null;
		try {
			newStr = new String(change.getBytes("utf-8"), "gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newStr;
	}
	
	public static void log4jInti(){
		PropertyConfigurator.configure("./config/log4j.properties");  
	}
	
	/**
	 * 获取上个月对应的月份
	 * @return
	 */
	public static String getLastMonth() {
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyyMM");
		Date date = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);

		String year = yearSdf.format(cal.getTime());
		return year;
	}
	
	
}
