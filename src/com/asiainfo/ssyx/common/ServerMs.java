package com.asiainfo.ssyx.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMs implements Runnable{

	public static void main(String[] args) {
		 new Thread(new ServerMs()).start();
	}
	@Override
	public void run() {
		try{
			ServerSocket server=null;
			try{
				server=new ServerSocket(19254);
				System.out.println("start server :"+server);
				//创建一个ServerSocket在端口4700监听客户请求
			}catch(Exception e) {
				System.out.println("can not listen to:"+e);
			}

			try{
				while (true) {
		            Socket socket = server.accept();
		            new Thread(new ExecuteRequest(socket)).start();
		        }
				//使用accept()阻塞等待客户请求，有客户请求到来则产生一个Socket对象，并继续执行
			}catch(Exception e) {
				System.out.println("Error."+e);
			}
		}catch (Exception x){
			System.out.println("Error,:"+x);
		}
	}

}
