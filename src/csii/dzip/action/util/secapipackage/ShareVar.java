package csii.dzip.action.util.secapipackage;

/**
 * @author zhaozheng
 *
 */
public class ShareVar
{
    static final int MAXLEN = 4000 * 4;		/* modified by xu.guoyou on 15 June 2009 */
    static final int MAXTIMEOUT = 20 * 60;

    static final int NODEIDSIZE = 15;
    static final int APPIDSIZE = 5;
    static final int KEYTYPESIZE = 5;
    static final int KEYINDEXSIZE = 3;
    static final int KEYLENSIZE = 3;
    static final int KEYSIZE = 49;
    static final int KEYCHECKSIZE = 16;
    static final int TVVSIZE = 4;
    
    static final int DATABLOCKSIZE = 1024;		/* added by xu.guoyou on 15 June 2009 */

    //public ShareVar  m_ShareVar = null;

    static final int TOKENSIZE = 1 + NODEIDSIZE + 1 + APPIDSIZE + 1 + KEYTYPESIZE + 1 + KEYINDEXSIZE + 1 + KEYLENSIZE + 1 + KEYSIZE + 1 + KEYCHECKSIZE + 1 + 4 + TVVSIZE;

    /*public ShareVar()
    {
       m_ShareVar = new ShareVar();
    }*/

    static class WinTimeStatus
    {
        static final byte DISABLE = 0;
        static final byte ENABLE = 1;
    }

    static class KeyOptFlag
    {
        static final byte KEYREAD = 1;
        static final byte KEYWRITE = 2;
    }

    static class KeySaveFlag
    {
        static final byte HSMSAVE = 1;
        static final byte CSMPSAVE = 2;
    }
    static class KeyType
    {
        static final byte ZMKTYPE = '2';
        static final byte ZAKTYPE = '3';
        static final byte ZPKTYPE = '4';
        static final byte CVKTYPE = '5';
        static final byte PVKTYPE = '6';
        static final byte VIPKTYPE = '7';
        static final byte DPMAKTYPE = '8';
        static final byte WWKTYPE = '9';
        static final byte NULLTYPE = 0x00;
    }

    static class  KeyTypeStr
    {
        static final String ZMKTYPESTR = "ZMK";
        static final String ZPKTYPESTR = "PIK";
        static final String ZAKTYPESTR = "MAK";
        static final String CVKTYPESTR = "CVK";
        static final String PVKTYPESTR = "PVK";
        static final String VIPKTYPESTR = "VIPK";
        static final String DPMAKTYPESTR = "DPMAK";
        static final String WWKTYPESTR = "WWK";
        static final String NULLTYPESTR = "NULL";
    }

    static class KeyTypeCode
    {
        static final String ZMKTYPECode = "000";
        static final String ZPKTYPECode = "001";
        static final String ZAKTYPECode = "008";
        static final String CVKTYPECode = "402";
        static final String PVKTYPECode = "002";
        static final String VIPKTYPECode = "006";
    }



    static class KeyStatus
    {
        static final byte KEYNORMAL = '1';
        static final byte KEYWINDOW = '2';
        static final byte KEYUPDATE = '3';
    }

    static class KeyLen
    {
        static final byte KEYLEN64 = '1';
        static final byte KEYLEN128 = '2';
        static final byte KEYLEN192 = '3';
    }


    static final String sFile = "hsmapi.conf";
    static final String accNo = "0000000000000000000";
    static final String PIN = "FFFFFFF";
    static final String MAX_PIN_LEN_STR = "12";

    static final String FileName = "HsmApiLog.txt";

    static class artflag
    {
/*        static final byte ANSI99 = '0';
        static final byte ANSI919 = '1';*/
    	static final byte ANSI99 = '1';		/* modified by xu.guoyou on 16 June 2009 */
    	static final byte ANSI919 = '2';
        static final byte CRYPT_CBC = '1';
        static final byte CRYPT_ECB = '0';
    }

    static class PinFmt
    {
        static final String  ANSI98PAN = "01";
        static final String  DocutelATM ="02";
        static final String  IBMATM = "03";
        static final String  PlusNetwork = "04";
        static final String  ISO9564 = "05";
        static final String  ANSI98NoPAN = "06";
    }

    static class HsmConf
    {
		static String ip;
		static String bip;
		static String clientip;
		static String logpath;
		static String logfilename;
		static int port;
		static int timeout;
		static int headlen;
		static int taillen;
		static int errlv;
        
        
    }

    static final String FilePath = HsmConf.logpath;
    static class TradeType
    {
        static final int TRADE_ENCRYPT_DATA =  0x01;
        static final int TRADE_DECRYPT_DATA =  0x02;
        static final int TRADE_GEN_PIK =    0x11;
        static final int TRADE_GEN_MAK =    0x12;
        static final int TRADE_GEN_PIN =    0x21;
        static final int TRADE_ENCRYPT_PIN =   0x22;
        static final int TRADE_CONVERT_PIN =   0x23;
        static final int TRADE_VERIFY_PIN =   0x24;
        static final int TRADE_DECRYPT_PIN =   0x25;
        static final int TRADE_GEN_MAC =    0x31;
        static final int TRADE_VERIFY_MAC =   0x32;
        static final int TRADE_IMPORT_PIK =   0x41;
        static final int TRADE_IMPORT_MAK =   0x42;
        static final int TRADE_RESET_PIK =   0x43;
        static final int TRADE_RESET_MAK =   0x44;
        static final int TRADE_IMPORT_VIPK =   0x45;
        static final int TRADE_GEN_PVK_PAIR =  0x51;
        static final int TRADE_GEN_PVV =    0x52;
        static final int TRADE_VERIFY_PVV =   0x53;
        static final int TRADE_GEN_PVK =    0x61;
        static final int TRADE_GEN_IBM_OFFSET =  0x62;
        static final int TRADE_GEN_IBM_PIN =   0x63;
        static final int TRADE_VERIFY_IBM_PIN =  0x64;
        static final int TRADE_GEN_CVK_PAIR =  0x71;
        static final int TRADE_GEN_CVV =    0x72;
        static final int TRADE_VERIFY_CVV =   0x73;
        static final int TRADE_EXPORT_PIK =   0x81;
        static final int TRADE_EXPORT_MAK =   0x82;
        static final int TRADE_GEN_ARPC =   0x85;
        static final int TRADE_VERIFY_ARQC =   0x86;
        
    }




}

/*
class KeyToken {
  byte keyoptflag;
  byte[] nodeid  = new byte[NODEIDSIZE + 1];
  byte[] appid  = new byte[APPIDSIZE + 1];
  byte[] keytype  = new byte[KEYTYPESIZE + 1];
  byte[] index  = new byte[KEYINDEXSIZE + 1];
  byte[] keylen  = new byte[KEYLENSIZE + 1];
  byte[] key  = new byte[KEYSIZE + 1];
  byte[] keycheck  = new byte[KEYCHECKSIZE + 1];
  byte saveflag;
  byte keystatus;
  byte special;
  byte tradetype;
  ByteBuffer tvv = ByteBuffer.allocate(TVVSIZE);
 }
*/










