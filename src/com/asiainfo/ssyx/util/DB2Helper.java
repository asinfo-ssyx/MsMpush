package com.asiainfo.ssyx.util;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import org.apache.log4j.Logger;


/**   
* @Title: DBHelper
* @Copyright： Copyright(c) 2010 
* @Company: 亚信联创科技(中国)有限公司 
* @author wangbq
* @date 2011-9-29 
* @version 1.0.0  
*/
public class DB2Helper {
    private DB2ConnectionManager dbcm = DB2ConnectionManager.getInstance();
    private String[] poolsName=null;
    private int dbTime = 1000;
    private String defPoolsName=null;//默认连接池
    
    private static Logger logger = Logger.getLogger(DB2Helper.class);
    /**
     * 默认构造函数
     */
    public DB2Helper() {
        poolsName = dbcm.getPoolName();
        defPoolsName = poolsName[0];
    }
    /**
     * 设置默认连接池名称
     * @param name 默认连接池名称
     * @return　boolean 判断设置是否成功
     */
    public boolean setDefPoolName(String name){//设置默认连接池名称
        if(checkPoolName(name)){
            defPoolsName=name;
            return true;
        }
        else
            return false;
    }
    /**
     * 取得数据库访问的次数
     * @return　int 访问次数
     */
    public int getAccessNum(){
        return dbcm.getAccessNum();
    }
    /**
     * 取得指定连接池的有效连接个数
     * @param poolName 连接池名称
     * @return int 有效连接个数
     */
    public int getFreeConNum(String poolName){
        return dbcm.getFreeConNum(poolName);
    }
    /**
     * 取得默认连接池的有效连接个数
     * @return int 有效连接个数
     */
    public int getFreeConNum(){
        return dbcm.getFreeConNum(defPoolsName);
    }
    /**
     * 取得默认连接池的已使用连接个数
     * @return int 已使用连接个数
     */
    public int getUsedConNum(){
        return dbcm.getUsedConNum(defPoolsName);
    }
    /**
     * 取得指定连接池的已使用连接个数
     * @param poolName 连接池名称
     * @return int 已使用连接个数
     */
    public int getUsedConNum(String poolName){
        return dbcm.getUsedConNum(poolName);
    }
    /**
     * 取得默认连接池名称
     * @return String 连接池名称
     */
    public String getDefPoolName(){//取得默认连接池名称
        return defPoolsName;
    }
    /**
     * 取得默认连接池的一个连接
     * @return Connection 可用的连接
     */
    public Connection getConnection(){//取得默认的连接
        return getConnection(defPoolsName);
    }
    /**
     * 取得指定连接池的连接
     * @param poolName 连接池名称
     * @return Connection 可用的连接
     */
    public Connection getConnection(String poolName){//取得指定连接池的连接
        if(!checkPoolName(poolName)){
            logger.info("Thread :"+Thread.currentThread().getName()+" dbPoolName is error in getConnetion()! the errorName is "+poolName);
            poolName = defPoolsName;
        }
        Connection con = null;
        while(con==null){
            con=dbcm.getConnection(defPoolsName,dbTime);
        }
        return con;
    }
    /**
     * 释放连接到默认的连接池
     * @param con 释放的连接
     */
    public void freeConnection(Connection con){//释放连接到默认的连接池
        freeConnection(con,defPoolsName);
    }
    /**
     * 释放连接到指定的连接池
     * @param con 要释放的连接
     * @param　String poolName 连接池名称
     */
    public void freeConnection(Connection con,String poolName){
        if(!checkPoolName(poolName)){
            logger.info("Thread :"+Thread.currentThread().getName()+" dbPoolName is error in freeConnection()! the errorName is "+poolName);
            poolName = defPoolsName;
        }
        dbcm.freeConnection(poolName,con);
    }
    /**
     * 注销此连接操作对象
     */
    public void release(){
        dbcm.release();
    }
    /**
     * 插入操作使用默认连接池
     * @param sql 插入的sql语句
     * @throws Exception 发生错误时抛出异常
     */
    public void insert(String sql)throws Exception{
        change_Access(sql,defPoolsName);
    }
    /*
     * 插入操作使用指定的连接池
     * @param sql　插入的sql语句
     * @param poolName　连接池的名称
     * @exception Exception　发生错误时抛出异常
     */
    public void insert(String sql,String poolName)throws Exception{//插入操作使用指定连接池
        change_Access(sql,poolName);
    }
    /**
     * 更新操作使用默认连接池
     * @param sql　插入的sql语句
     * @throws Exception　发生错误时抛出异常
     */
    public void update(String sql)throws Exception{//更新操作使用默认连接池
        change_Access(sql,defPoolsName);
    }
    /**
     * 更新操作使用指定的连接池
     * @param sql　插入的sql语句
     * @param poolName　连接池的名称
     * @throws Exception　发生错误时抛出异常
     */
    public void update(String sql,String poolName)throws Exception{//更新操作使用指定连接池
        change_Access(sql,poolName);
    }
    /**
     * 删除操作使用默认连接池
     * @param sql　插入的sql语句
     * @throws Exception　发生错误时抛出异常
     */
    public void delete(String sql)throws Exception{//删除操作使用默认连接池
        change_Access(sql,defPoolsName);
    }
    /**
     * 删除操作使用指定的连接池
     * @param sql　删除的sql语句
     * @param poolName　连接池的名称
     * @throws Exception　发生错误时抛出异常
     */
    public void delete(String sql,String poolName)throws Exception{//删除操作使用指定连接池
        change_Access(sql,poolName);
    }

    private void change_Access(String sql,String poolName)throws Exception{//非查询操作使用指定连接池
        Connection con = null;
        Statement stmt = null;
        try {
            con = getConnection(poolName);
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception ex) {
            }
            freeConnection(con,poolName);
        }
    }
    /**
     * 查询一行一列，使用默认连接池
     * @param sql　插入的sql语句
     * @return String 返回查询的值（没有为null,反之为一字符串）
     * @throws Exception　发生错误时抛出异常
     */
    public String select(String sql) throws Exception{//查询一行一列，使用默认连接池
        Vector<Serializable> v = select(sql,1,1,defPoolsName);
        if(v==null) return null;
        return (String)v.get(0);
    }
    /**
     * 查询一行一列，使用指定的连接池
     * @param sql　插入的sql语句
     * @param poolName　连接池名称
     * @return String 返回查询的值（没有为null,反之为一字符串）
     * @throws Exception　发生错误时抛出异常
     */
    public String select(String sql,String poolName) throws Exception{//查询一行一列，使用指定连接池
        Vector<?> v =select(sql,1,1,poolName);
        if(v==null) return null;
        return (String)v.get(0);
    }
    /**
     * 查询一行多列，使用默认连接池（lineNum为列数）
     * @param sql　插入的sql语句
     * @param lineNum　列数
     * @return Vector 返回查询的值（没有为null）
     * @throws Exception　发生错误时抛出异常
     */
    public Vector<?> select(String sql,int lineNum) throws Exception{//查询一行多列，使用默认连接池（lineNum　为列数）
        return select(sql,lineNum,1,defPoolsName);
    }
    /**
     * 查询一行多列，使用指定的连接池（lineNum为列数）
     * @param sql　插入的sql语句
     * @param lineNum　列数
     * @param poolName　名称连接池
     * @return Vector 返回查询的值（没有为null）
     * @throws Exception　发生错误时抛出异常
     */
    public Vector<?> select(String sql,int lineNum,String poolName) throws Exception{//查询一行多列，使用指定连接池（lineNum　为列数）
        return select(sql,lineNum,1,poolName);
    }
    /**
     * 查询rowNum行lineNum列，使用默认连接池
     * @param sql　插入的sql语句
     * @param lineNum　列数
     * @param rowNum　行数
     * @return Vector 返回查询的值（没有为null）
     * @throws Exception　发生错误时抛出异常
     */
    public Vector<?> select(String sql,int lineNum,int rowNum) throws Exception{//查询rowNum行lineNum列，使用默认连接池（lineNum　为列数,rowNum为行数）
        return select(sql,lineNum,rowNum,defPoolsName);
    }
    /**
     * 查询rowNum行lineNum列，使用指定连接池
     * @param sql　插入的sql语句
     * @param lineNum　列数
     */
    public Vector<Serializable> select(String sql,int lineNum,int rowNum,String poolName) throws Exception{
        Vector<Serializable> rows = new Vector<Serializable>();//行
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection(poolName);
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if(rowNum==1){//一行时使用rows来存放返回的数据
                if(rs.next()){
                    for(int i=0;i<lineNum;i++){
                        String value = rs.getString(i+1);
                        if(value!=null) rows.addElement(ToolsUtil.fromDatabase(value));
                        else rows.addElement(null);
                    }
                }else{
                    return null;
                }
            }else{
                while(rs.next()){
                    Vector<String> line = new Vector<String>();
                    for(int i=0;i<lineNum;i++){
                        String value = rs.getString(i+1);
                        if(value!=null) line.addElement(ToolsUtil.fromDatabase(value));
                        else line.addElement(null);
                    }
                    rows.addElement(line);
                    if((--rowNum)==0) break;
                }
            }
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            try {
                rs.close();
                stmt.close();
            }
            catch (Exception ex) {
            }
            freeConnection(con,poolName);
        }
        if(rows.size()==0) return null;
        return rows;
    }
    private boolean checkPoolName(String name){//检测输入的连接池名字是否正确
        boolean flag =false;
        int size = poolsName.length;
        for(int i=0;i<size;i++){
            if(poolsName[i].equals(name)){
                flag=true;
                break;
            }
        }
        return flag;
    }

}