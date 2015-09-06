package com.asiainfo.ssyx.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ClientMs implements Runnable{
	public static void main(String[] args) throws Exception {
		boolean b=true;
		System.out.println(b+"");
//		Map<String,String> map=new HashMap<String,String>();
//		map.put("active", "client");
//		ClientMs t1=new ClientMs();
//		new Thread(t1).start();
//
//		Thread.sleep(20000L);
//		System.out.println("11111111111111");
//		Map<String,String> sendMap=new HashMap<String,String>();
//		sendMap.put("shutStatue", "Y");
//		sendMap.put("activeCode", "yangshiyu");
//		t1.sendMsT0Server(sendMap);
	}

	public void sendMsT0Server(Map<String,String> map){
		try {
			os.writeObject(map);
			os.flush();
		} catch (IOException e) {
			System.out.println("发送信息到server出错："+e.getMessage());
		}

	}

	Socket socket = null;
    ObjectOutputStream os = null;
    ObjectInputStream is = null;

	@Override
	public void run() {
		try {
			socket = new Socket("localhost", 19254);
			os = new ObjectOutputStream(socket.getOutputStream());
	        is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
	        Map<String,String> map=null;
	        Map<String,String> sendMap=null;
	        int i=1;
			while(true){
				sendMap=new HashMap<String,String>();
				sendMap.put("a", ++i+"");
				sendMsT0Server(sendMap);
				Object obj = is.readObject();
	            map = (Map<String,String>)obj;
				System.out.println(map);
				if(map!=null){
					System.out.println("now run active:"+map.get("nowRunActive"));
				}
				Thread.sleep(1000L);
			}
		}catch (Exception e) {
			System.out.println("线程客户端出错:"+e.getMessage());
		}finally{
			try{
				is.close();
				os.close();
				socket.close();
			}catch(Exception e){
				System.out.println("客户端关闭socket 出错："+e.getMessage());
			}
		}
	}
}
