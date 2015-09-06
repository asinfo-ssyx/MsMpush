package com.asiainfo.ssyx.bean;
public final class SMArgument{
	public byte  nNeedReply;
	public byte  nMsgLevel;
	public byte[] sServiceID = new byte[10];
	public byte nMsgFormat;
	public byte[] sFeeType = new byte[2];
	public byte[] sFeeCode = new byte[6];
	public byte[] sValidTime = new byte[17];
	public byte[] sAtTime = new byte[17];
	public byte[] sSrcTermID = new byte[21];
	public byte[] sDestTermID = new byte[32];
	public byte[] sFeeTermID = new byte[32];
	public byte   cFeeTermType;
	public byte   cDestTermType;
	public byte[] sLinkID    = new byte[20];
	public String sDestTermIDFile;
	public String sMsgFile;
	public String sMsgIDFile;
	public byte[] sMsgCon;
	public int msgLeng;
}
