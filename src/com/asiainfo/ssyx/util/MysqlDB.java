package com.asiainfo.ssyx.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MysqlDB {
	private static Map<String,String> dbMap =new HashMap<String,String>();
	static {
		Properties prop = new Properties();
        InputStream in = null;
        try {
        	in = new FileInputStream("./config/mysql.properties");
            prop.load(in);
            dbMap.put("url", prop.getProperty("Url").trim());
            dbMap.put("driver", prop.getProperty("Driver").trim());
            dbMap.put("user", prop.getProperty("User").trim());
            dbMap.put("password", prop.getProperty("PassWord").trim());
            System.out.println(dbMap.toString());
        } catch (Exception e) {
        	System.out.println("加载mysql 配置文件出错"+e.getMessage());
        }
	}

	public static Connection getMysqlConnection(){
		Connection conn=null;
		try {
			System.out.println("driver:"+dbMap.get("driver"));
			System.out.println("url:"+dbMap.get("url"));
			System.out.println("user:"+dbMap.get("user"));
			System.out.println("password:"+dbMap.get("password"));
			Class.forName(dbMap.get("driver")).newInstance();
			conn = DriverManager.getConnection(dbMap.get("url"),dbMap.get("user"),dbMap.get("password"));
		} catch (Exception e) {
			System.out.println("获取mysql连接出错："+e.getMessage());
		}
		return conn;
	}
}
