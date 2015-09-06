package com.asiainfo.ssyx.common;

import com.asiainfo.ssyx.bean.SMArgument;

import aiismg.jcmppapi30.CMPPAPI;


public class MsmSendor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CMPPAPI cmpp = new CMPPAPI();
	    int nOutput = 1;

	    nOutput = cmpp.InitCMPPAPI("/home/dxllf/javacmppc_r.ini");
	    if (nOutput != 0)
	      System.out.println("Fail to call InitCMPPAPI!");
	    else {
	      System.out.println("CALl SUCCESS!");
	    }
	    
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
	    pArgs.sDestTermIDFile = "/home/dxllf/test.txt";

	    pArgs.sMsgCon = "一二三四五六七八九十".getBytes();
	    pArgs.msgLeng = pArgs.sMsgCon.length;
	    System.out.println(pArgs.sMsgCon.length);
	    
	    pArgs.sDestTermIDFile = "/home/dxllf/test.txt";
	    pArgs.sMsgIDFile = "/home/dxllf/return.txt";
	    pArgs.sFeeTermID = "10658211".getBytes();
	    pArgs.cFeeTermType = Byte.parseByte("3");
	    pArgs.cDestTermType = Byte.parseByte("0");
	    pArgs.sLinkID = new byte[20];
	    try
	    {
	        int rt = cmpp.CMPPSendBatch(
	          pArgs.nNeedReply, pArgs.nMsgLevel, 
	          pArgs.sServiceID, pArgs.nMsgFormat, pArgs.sFeeType, 
	          pArgs.sFeeCode, pArgs.sValidTime, pArgs.sAtTime, 
	          pArgs.sSrcTermID, pArgs.sDestTermIDFile, 
	          pArgs.sMsgCon.length, 
	          pArgs.sMsgCon, pArgs.sMsgIDFile, (byte) 0,
				pArgs.sFeeTermID, (byte) 0,
				(byte) 0, pArgs.cFeeTermType,
				pArgs.cDestTermType, pArgs.sLinkID);

	        System.out.println(rt);

	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	}

}
