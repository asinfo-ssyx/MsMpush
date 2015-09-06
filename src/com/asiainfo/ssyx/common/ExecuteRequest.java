package com.asiainfo.ssyx.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box.Filler;

import org.apache.log4j.Logger;

public class ExecuteRequest implements Runnable{
	public static String activeCo="yangsy";

	private Socket socket;
	public ExecuteRequest(Socket s){
		this.socket=s;
	}

	private static Logger logger = Logger.getLogger(ExecuteRequest.class);


	@Override
	public void run() {
		ObjectInputStream is = null;
        ObjectOutputStream os = null;
		try {
			is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            os = new ObjectOutputStream(socket.getOutputStream());
            Map<String,String> map=null;
            Map<String,String> sendmap=null;
			while(true){
	            Object obj = is.readObject();
	            map = (Map<String,String>)obj;
	            sendmap=new HashMap<String,String>();
	            sendmap.put("active_201501151722491", "n");
	            sendmap.put("active_201501151744391", "a");
	            sendmap.put("active_201501191123571", "a");
	            sendmap.put("active_201501191141102", "a");
	            //System.out.println("Client Ms :"+map);
	            if(map.get("shutStatue")!=null){
	            	System.out.println(map);
	            	System.out.println("shut down");
	            	sendmap.remove(map.get("activeCode"));
	            	//shutActiveSendMs(map);
	            }
//	            getSendMsActive(sendmap);
//	            if(!MsmSendorExe.nowRunActiveCode.equals("")){
//	            	sendmap.put(MsmSendorExe.nowRunActiveCode, "n");
//	            }
	            os.writeObject(sendmap);
	            os.flush();
	            Thread.sleep(1000L);
			}
		} catch (Exception e) {
			logger.error("获取输入流出错:"+e);
		} finally{
			try{
				is.close();
				os.close();
				socket.close();
			}catch(Exception e){

			}
		}
	}

	public boolean shutActiveSendMs(Map<String,String> map){
		String activeCode=map.get("activeCode");
		if(activeCode.equals(MsmSendorExe.nowRunActiveCode)){//如果为当前正在执行活动设置运行状态停止
			MsmSendorExe.runStatue=false;
			return true;
		}else{//如果不等删除短信表数据
			Connection conn = null;
			Statement stmt = null;
			try {
				String sql="delete from UPLOAD.active_msm where active_id='"+activeCode+"' and time=current date ";
				conn = MsmSendorExe.dbm.getConnection();
				stmt = conn.createStatement();
				int i=stmt.executeUpdate(sql);
				logger.info("手动停止-----删除短信日志："+i+"条数");
				return true;
			} catch (SQLException e) {
				logger.error("手动停止-----删除短信发送日志表数据出错！");
			}finally {
				try {
					try {
						stmt.close();
					} catch (Exception localException4) {}
					MsmSendorExe.dbm.freeConnection(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public void getSendMsActive(Map<String,String> map){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select active_id from UPLOAD.active_msm where time=current date group by active_id ";
			System.out.println(sql);
			conn = MsmSendorExe.dbm.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				map.put(rs.getString("active_id"), "a");//待发送
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				try{
					if (rs != null) rs.close();
				} catch (Exception localException3) {}
				try {
					pstmt.close();
				} catch (Exception localException4) {}
				MsmSendorExe.dbm.freeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

	}
}
