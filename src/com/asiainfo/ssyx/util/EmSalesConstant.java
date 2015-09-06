package com.asiainfo.ssyx.util;
/**
 * @Title: MsmConstant
 * @Copyright： Copyright(c) 2010
 * @Company: 亚信联创科技(中国)有限公司
 * @author wangbq
 * @date 2011-9-29
 * @version 1.0.0
 */
public final class EmSalesConstant {
	
	public final static String SEPARATOR=System.getProperty("file.separator");
	public final static String INITPATH = "."+SEPARATOR+"config"+SEPARATOR;
	
	public final static String HDBCONFIG = "./config/hsqldb.properties";
	public final static String DB2CONFIG = "./config/db2.properties";
	public final static String SCCONFIG = "./config/sc.properties";
	
	/**
	 * 未处理
	 */
	public final static String EXCUTEWAIT ="1";
	
	/**
	 * 月报文件已生成成功
	 */
	public final static String EXCUTESUCESS ="2";
	
	/**
	 * 文件生成失败（无数据，无表，字段错误等情况）
	 */
	public final static String EXCUTEERROR ="3";
	
	/**
	 * 文件生成中
	 */
	public final static String EXCUTEING ="4";
	
	/**
	 * 需重刷处理
	 */
	public final static String EXCUTEREFLASH ="5";
	
	

	public final static String ISSTART = "isStart";
	public final static String ISRUNING= "isRuning";

	/**
	 * 取数据大小，即每次从数据库中取数据条数
	 */
	public final static int GETROWNUM = 10;
	/**
	 * 消息队列阈值，即当消息队列数据条数超出，则停止从数据库中取数据
	 */
	public final static int QUEUEMAXNUM = 500;

	/**
	 * 允许连续发送短信失败最大值
	 */
	public final static int MAXERRORCOUNT = 5;

	/**
	 * 系统启动、停止发送短信标志位，Q为退出系统，S为暂停发送，E为继续发送
	 */
	public final static String STFLAG = "E";

	/**
	 * 短信过期时间
	 */
	public final static int MTTIMEOUT = 600;

	/**
	 * 发送线程休息时间基值
	 */
	public final static int MTSLEEPTIME = 10;
	/**
	 * 取得消息线程休息时间基值
	 */
	public final static int GETSLEEPTIME = 1000;

	/**
	 * 发送日志开关
	 */
	public final static String SENDLOGFLAG = "0";
	
	/**
	 * 发送成功标志
	 */
	public final static String SENDSUCCESS = "0";
	
	/**
	 * 发送错误标志
	 */
	public final static String SENDERROR = "-1";
	
	/**
	 * 发送不在白名单标志
	 */
	public final static String SENDFAIL = "1";

}
