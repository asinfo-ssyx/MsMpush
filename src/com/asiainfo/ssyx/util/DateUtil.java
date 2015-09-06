package com.asiainfo.ssyx.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtil {

	public static String dateToStr(Date date,String format) {
		SimpleDateFormat dateformat1=new SimpleDateFormat(format);
		return dateformat1.format(date);
	}

	public static Date strToDate(String str,String format) {
		SimpleDateFormat dateformat1=new SimpleDateFormat(format);
		try {
			return dateformat1.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getNowTime(String format) {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(now);
	}

	public static String getDayofThisWeek(String format,int weekday) {
		SimpleDateFormat dateformat1=new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return dateformat1.format(cal.getTime());
	}
	public static int getNowTimeH() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		return Integer.valueOf(sdf.format(now));
	}

	/**
	 * 根据时间字符串 判断是星期几
	 * @param date
	 * @return
	 */
	public static String getWeekDay(String date){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat sdw = new SimpleDateFormat("E");
	    Date d = null;
	    try {
	    d = sd.parse(date);
	    } catch (ParseException e) {
	    e.printStackTrace();
	    }
	    return sdw.format(d);
	}

	public static String getTodayWeekDay(){
		String weekString = "";
	    //final String dayNames[] = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
		final String dayNames[] = {"7","1","2","3","4","5","6"};
	    Calendar calendar = Calendar.getInstance();
	    Date date = new Date();
	    calendar.setTime(date);
	    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	    weekString = dayNames[dayOfWeek - 1];
	    return weekString;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		Map<String,Integer> map=new HashMap<String,Integer>();
		String s="节流量包来啦在2015年28天法定假日，每天只需元即可享受省内流量最低66天，省内接、打中国移动电话话费全免（v网除外），发送JRB1到10086马上办理，查询流量请下载掌上冲浪客户端http://wap.scmcc.com.cn/sjcl";
		System.out.println(s.length());
		byte[] messageUCS2;

		messageUCS2 = s.getBytes("UnicodeBigUnmarked");
		int messageUCS2Len = messageUCS2.length;// 长短信长度
		System.out.println(messageUCS2Len);
		System.out.println(66042*2+73392);

		while(true){
			System.out.println("while");
			for (int i = 0; i < 100; i++) {
				System.out.println("for:"+i);
				if(i==10){
					break;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}
