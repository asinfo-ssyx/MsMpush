package com.asiainfo.ssyx.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.asiainfo.ssyx.bean.SMArgument;
import com.asiainfo.ssyx.util.DB2Helper;
import com.asiainfo.ssyx.util.DateUtil;
import com.asiainfo.ssyx.util.LongMessageByte;

import aiismg.jcmppapi30.CMPPAPI;

public class MsmSendorExe {

	private static Logger logger = Logger.getLogger(MsmSendorExe.class);
	private static boolean flag = true;
	//地市 发送量阀值
	public static Map<String,Integer> cityParam=new HashMap<String,Integer>();
	//当天每个地市已经发送量
	public static Map<String,Integer> nowCitySendCount=new HashMap<String,Integer>();
	//活动对应的地市
	public static Map<String,String> activeCity=new HashMap<String,String>();
	//当前日期 年月日  用于跨天判断
	public static String nowDate="";
	//发送号码日志存储 用于日志插入操作
	public static List<Map<String,Object>> insertLogList=new ArrayList<Map<String,Object>>();
	//用于记录删除行id列表
	public static List<Integer> deleteMsmId=new ArrayList<Integer>();

	private CMPPAPI cmpp = null;
	public static DB2Helper dbm = new DB2Helper();
	//当天发生号码集合
	public static Map<String,Integer> nowDaySendPhoneMap=new HashMap<String,Integer>();
	//7天内发送量
	public static Map<String,Integer> sevDaySendCount=new HashMap<String,Integer>();
	//判断活动类型  S 实时，F 非实时。用于实时删除短信记录用
	public static String activeType="";
	//当前正在执行的活动id
	public static String nowRunActiveCode="";
	//设定当前活动执行状态
	public static boolean runStatue =true;

	public static int todayRnum=0; //当天因为重复未发送号码数量
	public static int sevDayRnum=0; //7天内因为重复未发送号码数量
	public static int activeNowSendNum=0; //当前活动实际发送量
	public static int cityRnum=0; //活动因为地市阀值未发生号码量
	public static int smsNum=0;//入库短信表条数
	public static String cityId="";
	public static FileWriter writer=null;
	static{
		try {
			File file=new File("/home/dxllf/MsmSendor/sendLog.txt");
			if(!file.exists()){
				file.createNewFile();
			}
			//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer=new FileWriter(file, true);
		} catch (IOException e) {
			logger.error("启动程序打开日志文件出错："+e.getMessage());
		}
	}

	/**
	 * 每次发送前初始数量
	 */
	public static void initSendLog(){
		todayRnum=0;
		sevDayRnum=0;
		activeNowSendNum=0;
		cityRnum=0;
		smsNum=0;
		cityId="";
		nowRunActiveCode="";
		runStatue=true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new Thread(new ServerMs()).start();//启动socketServer服务线程

		CMPPAPI cmpp = new CMPPAPI();
		int nOutput = 1;

		nOutput = cmpp.InitCMPPAPI("/home/dxllf/javacmppc_r.ini");
		if (nOutput != 0)
			System.out.println("Fail to call InitCMPPAPI!");
		else {
			System.out.println("CALl SUCCESS!");
		}

		while (true) {
			initSendLog();
			logger.info("可以获取的最大内存："+Runtime.getRuntime().maxMemory()/1024/1024+"M | 当前以使用的内存 totalMemory:"+Runtime.getRuntime().totalMemory()/1024/1024+"M");
			logger.info("nowDate :"+nowDate +" 当前时间： "+DateUtil.getNowTime("yyyy-MM-dd"));
			logger.info("当前已经发送的短信条数:"+nowDaySendPhoneMap.size()+"|7天内发送的号码数量:"+sevDaySendCount.size());
			deleteUpDaySendPhone();
			if(!nowDate.equals(DateUtil.getNowTime("yyyy-MM-dd"))){//跨天 初始化配置
				nowDate=DateUtil.getNowTime("yyyy-MM-dd");
				nowDaySendPhoneMap.clear();//发送号码清空
				sevDaySendCount.clear();//清空7天发送次数
				setTodaySendPhoneNos();//启动时
				//deleteUpDaySendPhone();//删除前一天短信数据
				setNowDayCitySendCount();//启动初始化--设置当天每个地市已经发送多少
				getParam();//设置当天配置的地市发送量
				saveSendPhoneLog();//发送日志保存到历史表
				initSevDaySendCount();//查询号码7天发送次数

			}

			int nowTime = DateUtil.getNowTimeH();

			// 免打扰时间
			if (((nowTime >= 9) && (nowTime <= 11))|| ((nowTime >= 13) && (nowTime <= 20))) {
				// String content = "发送短信内容";
				activeType="F";//默认非实时
				Map<String,String> contentMap = getSendContent();
				String content="";
				String activeId="";
				if(null==contentMap){
					logger.info("当前没有短信");
					try {
						Thread.sleep(1000L);
						continue;
					} catch (InterruptedException e) {
					}
				}else{
					content=contentMap.get("content");
					activeId=contentMap.get("activeId");
				}
				nowRunActiveCode=activeId;//设置当前正在执行的活动id

				SMArgument pArgs = new SMArgument();
				pArgs.nNeedReply = Byte.parseByte("1");
				pArgs.nMsgLevel = Byte.parseByte("2");
				pArgs.sServiceID = "-swzs".getBytes();
				pArgs.nMsgFormat = Byte.parseByte("0");
				pArgs.sFeeType = "01".getBytes();
				pArgs.sFeeCode = "0010".getBytes();
				pArgs.sValidTime = "131201102300032+".getBytes();
				pArgs.sAtTime = "131201102300032+".getBytes();
				pArgs.sSrcTermID = "10658211".getBytes();

				pArgs.sMsgCon = content.getBytes();
				pArgs.msgLeng = pArgs.sMsgCon.length;
				logger.info(pArgs.sMsgCon.length);
				pArgs.sFeeTermID = "10658211".getBytes();
				pArgs.cFeeTermType = Byte.parseByte("3");
				pArgs.cDestTermType = Byte.parseByte("0");
				pArgs.sLinkID = new byte[20];

				List<byte[]> conlist = LongMessageByte.getLongByte(content);
				logger.info("create numfile start");
				String filePath = "/home/dxllf/MsmSendor/files/";
				//String filePath = "F://";
				int[] filePara = createMFile(filePath, content,activeId);
				try {
					logger.info("create file success! file count:"+filePara[0]);
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int filenum = filePara[0];
				//int allsize = filePara[1];
				logger.info("numfilenum:" + filenum);
				for (int i = 1; i <= filenum; ++i) {
					if(runStatue){
						break;
					}
					logger.info("执行第："+i+" 个文件,还有"+(filenum-i)+"个文件");
					pArgs.sDestTermIDFile = filePath + i + ".txt";
					pArgs.sMsgIDFile = filePath + i + "_return.txt";
					if (conlist.size() > 1) {
						for (byte[] msg : conlist) {
							try {
								pArgs.msgLeng = msg.length;
								pArgs.sMsgCon = msg;
								pArgs.nMsgFormat = Byte
										.parseByte("8");
								cmpp.CMPPSendBatch(
										pArgs.nNeedReply,
										pArgs.nMsgLevel,
										pArgs.sServiceID,
										pArgs.nMsgFormat,
										pArgs.sFeeType,
										pArgs.sFeeCode,
										pArgs.sValidTime,
										pArgs.sAtTime,
										pArgs.sSrcTermID,
										pArgs.sDestTermIDFile,
										pArgs.msgLeng,
										pArgs.sMsgCon,
										pArgs.sMsgIDFile, (byte) 0,
										pArgs.sFeeTermID, (byte) 0,
										(byte) 1,
										pArgs.cFeeTermType,
										pArgs.cDestTermType,
										pArgs.sLinkID);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							try {
								Thread.sleep(1000L);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}else{
						try {
							int rt = cmpp.CMPPSendBatch(pArgs.nNeedReply, pArgs.nMsgLevel,
									pArgs.sServiceID, pArgs.nMsgFormat, pArgs.sFeeType,
									pArgs.sFeeCode, pArgs.sValidTime, pArgs.sAtTime,
									pArgs.sSrcTermID, pArgs.sDestTermIDFile,
									pArgs.sMsgCon.length, pArgs.sMsgCon, pArgs.sMsgIDFile,
									(byte) 0, pArgs.sFeeTermID, (byte) 0, (byte) 0,
									pArgs.cFeeTermType, pArgs.cDestTermType, pArgs.sLinkID);

							logger.info("返回错误数据条数："+rt);

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				// 删除已发送过的phoneNo 根据id
				delSendedUserInfo(activeId,content);

				//写发送日志文件
				//if(smsNum>0){
					appendFileString("活动"+activeId+",活动地市：+"+cityId+",发送时间="+DateUtil.getNowTime("yyyy-MM-dd HH:mm:ss"));
					appendFileString("活动"+activeId+",短信表总条数="+smsNum);
					appendFileString("活动"+activeId+",当天重复数量="+todayRnum);
					appendFileString("活动"+activeId+",7天内重复数量="+sevDayRnum);
					appendFileString("活动"+activeId+",当天地市发送上线="+cityRnum+",地市配置发送量："+cityParam.get(cityId+"azscl")+",当前地市发送量："+nowCitySendCount.get(cityId+"azscl"));
					appendFileString("活动"+activeId+",实际发送数量="+activeNowSendNum);
					Map<String,Object> imap=new HashMap<String,Object>();
					imap.put("active_code", activeId);
					imap.put("city_id", cityId);
					imap.put("sms_num", smsNum);
					imap.put("today_rnum", todayRnum);
					imap.put("sevday_rnum", sevDayRnum);
					imap.put("city_rnum", cityRnum);
					imap.put("city_param", cityParam.get(cityId+"azscl"));
					imap.put("now_city_send", nowCitySendCount.get(cityId+"azscl"));
					imap.put("send_time", DateUtil.getNowTime("yyyy-MM-dd HH:mm:ss"));
					imap.put("send_num", activeNowSendNum);
					insertSendFruit(imap);
					//	}
			}

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				logger.error("1m线程休眠出错："+e.getMessage());
			}
		}
	}

	public static int insertSendFruit(Map<String,Object> map){
		Connection conn = null;
		Statement stmt = null;
		try {
			String sql = "insert into aiapp.send_sms_fruit_log(log_id," +
												  "active_code," +
												  "city_id," +
												  "sms_num," +
												  "today_rnum," +
												  "sevday_rnum," +
												  "city_rnum," +
												  "city_param," +
												  "now_city_send," +
												  "send_date,send_time,send_num) values " +
												  "(" +
												  "MY_SEQ.nextval," +
												  "'"+map.get("active_code")+"'," +
												  "'"+map.get("city_id")+"'," +
												  ""+map.get("sms_num")+"," +
												  ""+map.get("today_rnum")+"," +
												  ""+map.get("sevday_rnum")+"," +
												  ""+map.get("city_rnum")+"," +
												  ""+map.get("city_param")+"," +
												  ""+map.get("now_city_send")+",current date," +
												  "'"+map.get("send_time")+"'," +
												  ""+map.get("send_num")+"" +
												  ")";
			conn = dbm.getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					stmt.close();
				} catch (Exception e) {}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}


	/**
	 * 根据 发送内容 查询本次的发送号码群
	 * @param filePath
	 * @param content
	 * @return
	 */
	public static int[] createMFile(String filePath, String content,String activeId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int ct = 1;
		int pct = 1;
		int allct = 0;
		FileWriter fw = null;
		// 文件一次发送人数 -》修改为50，小莫已设置为300，最大350  480
		int filenum = 280;
		try {
			String sql = "select  msm_id,phone_no,city_id,active_id,send_type from UPLOAD.active_msm where active_id = '"
					+ activeId + "' and time=current date  WITH UR ";

			logger.info(sql);
			conn = dbm.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			File dirs = new File(filePath);
			if (!(dirs.exists())) {
				dirs.mkdirs();
			}
			fw = new FileWriter(filePath + ct + ".txt");
			String phoneNo="";
			String fsqd="zscl";//默认掌上冲浪
			String cityCode="";
			Map<String,Object> sendPhonelog=null;
			while (rs.next()) {
				if(runStatue){//终止当前活动判断
					deleteSendMsLog(activeId);
					return null;
				}

				try{
					fsqd=rs.getString("send_type");
					phoneNo=rs.getString("PHONE_NO").trim();

					//判断发送量
					cityCode=rs.getString("city_id").replaceFirst("s", "");
					if(!"test".equals(fsqd)){//测试号码直接跳过判断
						if(null!=fsqd&&!"".equals(fsqd)&&!"ssyx".equals(fsqd)&&!"null".equals(fsqd)){//暂时实时不判断地市
							if(cityParam.get(cityCode+fsqd)==null){
								System.out.println("当前地市渠道发送类型|"+fsqd+"|没有配置发送量 无法短信发送！！！");
								continue;
							}
							if(nowCitySendCount.get(cityCode+fsqd)==null){
								nowCitySendCount.put(cityCode+fsqd, 0);
							}
							if(cityParam.get(cityCode+fsqd)<=nowCitySendCount.get(cityCode+fsqd)){
								//System.out.println("当前地市|渠道发送量以用完 。地市渠道："+cityCode+fsqd);
								cityRnum+=1;
								continue;
							}
						}else{
							System.out.println("发送实时短信");
							activeType="S";
							deleteMsmId.add(rs.getInt("msm_id"));
						}
						if(isTodaySend(phoneNo)){//判断重复发送
							if(activeType.equals("S")){
								System.out.println("当前号码当天重复发送："+phoneNo);
							}
							continue;
						}
						if(isSevSendNum(phoneNo)){
							if(activeType.equals("S")){
								System.out.println("当前号码当7天重复发送："+phoneNo);
							}
							continue;
						}
					}else{
						logger.info("测试号码:"+phoneNo+"发送");

					}

					fw.write(phoneNo + "\n");
					++pct;
					++allct;
					if (pct == filenum) {
						pct = 1;
						++ct;
						if (fw != null) {
							logger.info("生成一个短信文件  ："+filePath + ct + ".txt");
							fw.close();
						}
						fw = new FileWriter(filePath + ct + ".txt");
					}
					sendPhonelog=new HashMap<String,Object>();
					sendPhonelog.put("cityId", cityCode);
					sendPhonelog.put("fsqd", rs.getString("send_type"));
					sendPhonelog.put("phoneNo", phoneNo);
					sendPhonelog.put("activeId", rs.getString("active_id"));
					insertLogList.add(sendPhonelog);

					if(insertLogList.size()>200){//50条执行一次
						insertSendUserMsmLog(content);
					}

					//
					if(!"test".equals(fsqd)&&null!=fsqd&&!"ssyx".equals(fsqd)){//测试号码直接跳过判断
						int i=nowCitySendCount.get(cityCode+fsqd);
						if(content.length()>70){//长度大于70算2条 长短信
							i+=2;
							activeNowSendNum+=2;
						}else{
							i++;
							activeNowSendNum++;
						}

						nowCitySendCount.put(cityCode+fsqd, i);
					}

				}catch(Exception e1){
					logger.error("循环生成短信文件出错，短信表或程序数据不全:"+e1.getMessage());
					e1.printStackTrace();
				}

			}
			cityId=cityCode;
			if(insertLogList.size()>0){//执行一次日志插入
				insertSendUserMsmLog(content);
			}
		} catch (Exception e) {
			logger.error("duan xin 生成  error:"+e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
				try {
					if (rs != null)
						rs.close();
				} catch (Exception localException3) {
				}
				try {
					pstmt.close();
				} catch (Exception localException4) {
				}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int[] intArray = new int[2];
		intArray[0] = ct;
		intArray[1] = allct;
		return intArray;
	}

	/**
	 * 短信发送手动终止时，删除发送短信日志表
	 * @param activeCode
	 */
	public static void deleteSendMsLog(String activeCode){
		Connection conn = null;
		Statement stmt = null;
		try {
			String sql="delete from aiapp.send_sms_log where active_code='"+activeCode+"' and send_date=current date ";
			conn = dbm.getConnection();
			stmt = conn.createStatement();
			int i=stmt.executeUpdate(sql);
			logger.info("删除短信日志："+i+"条数");
		} catch (SQLException e) {
			logger.error("删除短信发送日志表数据出错！");
		}finally {
			try {
				try {
					stmt.close();
				} catch (Exception localException4) {
				}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 一次取一个发送内容
	 * @return
	 */
	public static Map<String,String> getSendContent() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String,String> map=null;
		try {//PHONE_NO,msm_code,active_id,CONTENT,time 获取当天的发送内容 过期不发
			String sql = "select CONTENT,active_id from UPLOAD.active_msm " +
					"where time=current date order by send_type desc fetch first 1 rows only ";
			System.out.println(sql);
			conn = dbm.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				map=new HashMap<String,String>();
				map.put("content", rs.getString("content")) ;
				map.put("activeId", rs.getString("active_id")) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception localException3) {
				}
				try {
					pstmt.close();
				} catch (Exception localException4) {
				}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}


	/**
	 * 删除已发送过的内容用户
	 *
	 * @return
	 */
	public static String delSendedUserInfo(String sm,String content) {
		if(sm ==null || "".equals(sm.trim())){//||deleteMsmId.size()==0
			logger.info("no delete data!");
			return "";
		}
		logger.info("执行删除程序！");
		Connection conn = null;
		Statement stmt = null;

		try {
			String sql="";
			if("S".equals(activeType)){
				StringBuffer sb=new StringBuffer(" delete from UPLOAD.active_msm where msm_id in ( ");
				for (int i = 0; i < deleteMsmId.size(); i++) {
					sb.append(deleteMsmId.get(i));
					if(i<deleteMsmId.size()-1){
						sb.append(",");
					}
				}
				sb.append(")");
				sql=sb.toString();
			}else{
				sql = "delete from UPLOAD.active_msm where active_id='"+sm+"' and time=current date ";
			}

			logger.info(sql);
			conn = dbm.getConnection();
			stmt = conn.createStatement();
			int t=stmt.executeUpdate(sql);
			if(content.length()>70){//长度大于70算2条 长短信
				smsNum=t*2;
			}else{
				smsNum=t;
			}
			logger.info("删除短信表内容 条数："+t);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					stmt.close();
				} catch (Exception localException4) {
				}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		deleteMsmId.clear();
		return "";
	}

	/**
	 * 判断当天 和 7天内是否是重复发送
	 * @param phoneNo
	 * @return
	 */
	public static boolean isRepeatSend(String phoneNo){
		Connection conn = dbm.getConnection();;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		try {
//			String sql="SELECT phone_no FROM aiapp.send_sms_log where " +
//					   " send_date=current date AND " +
//					   " send_type='zscl' AND " +
//					   " phone_no='"+phoneNo+"'";
//			//System.out.println(sql);
//			pstmt = conn.prepareStatement(sql);
//			rs = pstmt.executeQuery();
//			while (rs.next()) {
//				return true;
//			}
			String sql="select phone_no from aiapp.send_sms_log where " +
					   " send_date>current date -7 days  AND " +
					   " send_type='zscl' AND " +
					   " phone_no='"+phoneNo+"' GROUP BY phone_no HAVING count(phone_no)>1";
			pstmt1 = conn.prepareStatement(sql);
			rs1 = pstmt1.executeQuery();
			while (rs1.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					//if (rs != null) rs.close();
					if (rs1!= null) rs1.close();
				} catch (Exception localException3) {}
				try {
					//if(pstmt!=null)pstmt.close();
					if(pstmt1!=null)pstmt1.close();
				} catch (Exception localException4) {
				}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * 发送短信日志操作
	 * @param phoneNo
	 * @return
	 */
	public static int insertSendUserMsmLog(String sendMs){
		Connection conn = null;
		Statement stmt = null;
		try {
			String sql = "insert into aiapp.send_sms_log(phone_no," +
												  "active_code," +
												  "city_id," +
												  "send_time," +
												  "send_date," +
												  "send_type," +
												  "send_ms) values ";
			StringBuffer sb=new StringBuffer(sql);
			for (int i = 0; i <insertLogList.size(); i++) {
				sb.append("('"+insertLogList.get(i).get("phoneNo")+"'," +
						   "'"+insertLogList.get(i).get("activeId")+"'," +
						   "'"+insertLogList.get(i).get("cityId")+"'," +
						   "current timestamp,current date ," +
						   "'"+insertLogList.get(i).get("fsqd")+"'," +
						   "'"+sendMs+"')");
				if(i<insertLogList.size()-1){
					sb.append(",");
				}
			}

			//System.out.println(sb.toString());
			conn = dbm.getConnection();
			stmt = conn.createStatement();
			stmt.execute(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					stmt.close();
				} catch (Exception localException4) {
				}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//putTodaySendPhoneNos(insertLogList);//把发送的号码放到内存中
			//putSevSendNums(insertLogList);
			//activeNowSendNum+=insertLogList.size();//记录发送条数
			insertLogList.clear();
		}
		return 1;
	}


	/**
	 * 用查询没个地市和推送渠道的发送数
	 * 获取配置参数
	 */
	public static void getParam(){
		String nowWeek=DateUtil.getTodayWeekDay(); //用来增加周配置  当前是周几

		Connection conn=dbm.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM aiapp.city_sendms_scale where send_day="+nowWeek+" and status=1 ORDER BY  city_id";
			System.out.println("获取当天地市推送配置："+sql);
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				System.out.println("city count:"+rs.getString("city_id")+rs.getString("qd_type")+"|"+rs.getInt("send_count"));
				cityParam.put(rs.getString("city_id").trim()+rs.getString("qd_type").trim(), rs.getInt("send_count"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception localException3) {
				}
				try {
					pstmt.close();
				} catch (Exception localException4) {}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 跨天或是启动时 初始化 当天每个地市发送量
	 */
	public static void setNowDayCitySendCount(){
		nowCitySendCount.clear();

		Connection conn= dbm.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT city_id,send_type ,count(1) scount FROM aiapp.send_sms_log where send_date=current date group BY  city_id,send_type";
			System.out.println(sql);
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				System.out.println("city count:"+rs.getString("city_id")+rs.getString("send_type")+"|"+rs.getInt("scount"));
				nowCitySendCount.put(rs.getString("city_id").trim()+rs.getString("send_type").trim(), rs.getInt("scount"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception localException3) {
				}
				try {
					pstmt.close();
				} catch (Exception localException4) {}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


	/**
	 * 判断当天是否发送了短信
	 * @param phoneNo
	 * @return
	 */
	public static boolean isTodaySend(String phoneNo){
		if(nowDaySendPhoneMap.containsKey(phoneNo)){
			todayRnum++;
			//System.out.println("todayRnum:"+todayRnum);
			return true;
		}
		nowDaySendPhoneMap.put(phoneNo, 1);
		return false;

//		return nowDaySendPhoneMap.containsKey(phoneNo);
	}

	/**
	 * 判断7天发送总次数是否发完
	 * @param phoneNo
	 * @return
	 */
	public static boolean isSevSendNum(String phoneNo){
		if(sevDaySendCount.containsKey(phoneNo)){
			if(sevDaySendCount.get(phoneNo)>1){
				sevDayRnum++;
				return true;
			}else{
				sevDaySendCount.put(phoneNo, sevDaySendCount.get(phoneNo)+1);
			}
		}else{
			sevDaySendCount.put(phoneNo, 1);
		}
		return false;
	}


	/**
	 * 添加当天以发送号码到map中
	 * @param phoneNos
	 */
	public static void putTodaySendPhoneNos(List<Map<String,Object>> phoneNo){
		for (Map<String, Object> map : phoneNo) {
			if(!nowDaySendPhoneMap.containsKey(map.get("phoneNo").toString())){
				nowDaySendPhoneMap.put(map.get("phoneNo").toString(), 1);
			}
		}
		System.out.println("当天已经发送的号码量："+nowDaySendPhoneMap.size());
	}


	/**
	 * 添加到内存中
	 * @param phoneNo
	 */
	public static void putSevSendNums(List<Map<String,Object>> phoneNo){
		for (Map<String, Object> map : phoneNo) {
			if(sevDaySendCount.containsKey(map.get("phoneNo").toString())){
				sevDaySendCount.put(map.get("phoneNo").toString(), sevDaySendCount.get(map.get("phoneNo").toString())+1);
			}else{
				sevDaySendCount.put(map.get("phoneNo").toString(), 1);
			}
		}
	}

	/**
	 * 启动时设置当天发送量
	 */
	public static void setTodaySendPhoneNos(){
		Connection conn= dbm.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int i=0;
		try {
			String sql = "SELECT phone_no FROM aiapp.send_sms_log where send_date=current date group BY  phone_no ";
			System.out.println("启动时初始化当天发送号码到内存中：sql语句="+sql);
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				i++;
				nowDaySendPhoneMap.put(rs.getString("phone_no"), 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception localException3) {
				}
				try {
					pstmt.close();
				} catch (Exception localException4) {}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("nowDaySendPhonesMap 号码条数 :"+i);
	}

	/**
	 * 跨天时删除前一天的数据 保证短信表发送效率
	 */
	public static void deleteUpDaySendPhone(){
		Connection conn= null;
		Statement stmt = null;
		try {
			String sql = " delete FROM UPLOAD.active_msm where current date>time ";
			System.out.println(sql);
			conn = dbm.getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					stmt.close();
				} catch (Exception localException4) {}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 短信波次判断只 需要保留7天数据，其它数据每天做一次备份，插入到大日志表
	 */
	public static void saveSendPhoneLog(){
		Connection conn= null;
		Statement stmt = null;
		try {
			String sql = " insert into  aiapp.send_sms_log_his select * from aiapp.send_sms_log where send_date<current date -7 days ";
			System.out.println("备份发送日志表："+sql);
			conn = dbm.getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
			sql=" delete from aiapp.send_sms_log where send_date<current date -7 days ";
			System.out.println("删除发送日志表数据："+sql);
			stmt.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					stmt.close();
				} catch (Exception localException4) {}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 每天设置7天发送次数
	 */
	public static void initSevDaySendCount(){
		Connection conn= dbm.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT phone_no,count(1) sendNum FROM aiapp.send_sms_log group BY phone_no ";
			System.out.println("启动时初始化 7天发送次数  到内存中：sql语句="+sql);
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				sevDaySendCount.put(rs.getString("phone_no").trim(), rs.getInt("sendNum"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				} catch (Exception localException3) {
				}
				dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
     * 文件追加内容
     * @param fileName
     * @param content
     */
    public static void appendFileString(String content) {
        try {
            writer.write(content);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
 * CREATE SEQUENCE MY_SEQ
    AS INTEGER
      START WITH 1
      INCREMENT BY 1
      MINVALUE 10000000
      MAXVALUE 69999999
      CYCLE
      NO CACHE
     ORDER;
fetch first 1 rows only
     select  MY_SEQ.nextval from sysibm.sysdummy1

     alter table UPLOAD.active_msm ADD COLUMN msm_id  INTEGER(11);
     Administrator

     dbserver

     cmc
     ailk002


CREATE INDEX msm_phone ON UPLOAD.active_msm(phone_no);
CREATE INDEX ssyx_log_phoneno ON aiapp.send_sms_log(phone_no);
create index indexname on schemaname.tablename(column1,column2....)
manage
     upload.active_msm;
z542
as1a1nf0
     /home/dxllf/MsmSendor/
     dxllf@10.105.7.254

UPLOAD.active_msm

     dxllf@10.105.7.254:/home/dxllf/MsmSendor/
	 Dxllf@123
nohup /home/dxllf/MsmSendor/jre/bin/java -Xmx2048m -jar /home/dxllf/MsmSendor/MsmPush.jar &



db2 describe indexes for table aiapp.send_sms_log show detail//查看表索引
 */

}
