package com.wintone.JFormEx;

public class JErrCode
{
	public static final int TW_BANK_NONE                = 0x0000;
	public static final int TW_BANK_LOAD_TEMP_FAILD	    = 0x0001;
	public static final int TW_BANK_LOAD_IMG_FAILD      = 0x0002;
	public static final int TW_BANK_LOAD_ENGINE_FAILD   = 0x0003;
	public static final int TW_BANK_DETECT_LINE_FAILD   = 0x0004;
	public static final int TW_BANK_NO_SINGLE_ROCKEY    = 0x0005;
	public static final int TW_BANK_NO_NET_ROCKEY	    = 0x0006;
	public static final int TW_BANK_NETROCKEY_ERR	    = 0x0007;
	public static final int TW_BANK_SINGLEROCKEY_ERR    = 0x0008;
	public static final int TW_BANK_ERR_NET_LOGIN       = 0x0009;
	public static final int TW_BANK_NO_ROCKEY           = 0x0010;

	public static final int TW_BANK_UNKNOW			    = 0x0011;
	public static final int TW_BANK_UNKNOW_CALL_FUN_FAIL= 0x0012;   //调用其他函数失败（别调用函数失败原因未知）
	public static final int TW_BANK_UNKNOW_CALL_ORDER   = 0x0013;   //调用次序失败（调用顺序为AB，如果A未调用或调用失败，在调用B，则发生此错误）

	public static final int TW_BANK_XML					= 0x0100;
	public static final int TW_BANK_XML_SAVE			= 0x0101;
	public static final int TW_BANK_XML_READ			= 0x0102;
	public static final int TW_BANK_XML_INTO_ELEM		= 0x0103;
	public static final int TW_BANK_XML_OUTOF_ELEM		= 0x0104;
	public static final int TW_BANK_XML_ADD_ELEM		= 0x0105;
	public static final int TW_BANK_XML_FIND_KEY_ELEM	= 0x0106;
	public static final int TW_BANK_XML_ILLEGAL_DATA    = 0x0107;

	public static final int TW_BANK_IMG_OPEN            = 0x0201;
	public static final int TW_BANK_IMG_EMPTY           = 0x0202;
	public static final int TW_BANK_LOAD_SCAN_FAILD     = 0x0203;
}
