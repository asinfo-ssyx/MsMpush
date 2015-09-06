package com.asiainfo.ssyx.test;

import java.util.List;

import com.asiainfo.ssyx.util.LongMessageByte;

public class LongMessageByteTest {

	public static void main(String[] args) throws Exception{
/*		
		 System.out.println(System.getProperty("file.encoding"));
		 
		String s = "精彩动漫潮流资讯、趣味动漫。漫友互动，快来下载和动漫客户端吧s";
//		System.out.println(s.getBytes("utf-8").length);
//		System.out.println(s.getBytes().length);
//		System.out.println(s.getBytes("gbk").length);
//		System.out.println(s.getBytes("iso-8859-1").length);
//		System.out.println(new String(s.getBytes("utf-8"), "gbk"));
//		System.out.println(new String(s.getBytes("gbk")));
		String ss = new String(s.getBytes("gbk"), "gbk");
		System.out.println(ss);
		System.out.println(ss.getBytes("gbk").length);
		
		System.out.println("==================分割线============================");
		String sss = new String(new String(s.getBytes("UTF-8"), "utf-8").getBytes("GBK"),"GBK");
		byte[] b = sss.getBytes("GBK");
		System.out.println(sss);
		System.out.println(b);
		System.out.println(b.length);
		System.out.println(new String(b, "GBK"));*/
//		System.out.println(new String(s.getBytes("gbk"), "utf-8"));
//		System.out.println(new String(s.getBytes("utf-8"), "utf-8"));
		//System.out.println(new String("精彩动漫潮流资讯、趣味动漫。漫友互动，快来下载和动漫客户端吧s".getBytes("iso-8859-1")));
//System.out.println("精彩动漫潮流资讯、趣味动漫。漫友互动，快来下载和动漫客户端吧".getBytes().length);
		String s2 = new String("发送短信TJ到10086开通咪咕特级会员（6元/月）8月期间登客户端免费领100M流量，分享再获30Mhttp://t.cn/RLYcEpB");
		//utf-8:一个中文3个字节一个英文一个字节，gbk：一个中文2个字节，一个英文1个字节，ucs2:一个中文或英文都是2个字节
		List<byte[]> list =LongMessageByte.getLongByte(s2);
		System.out.println(list.size());
		for(int i=0; i<list.size();i++){
			System.out.println(new String(list.get(i),"utf-8"));
		}
	}

}
