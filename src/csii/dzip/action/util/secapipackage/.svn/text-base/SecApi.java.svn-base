/*
 * 日期  2011-05-11
 *
 */
package csii.dzip.action.util.secapipackage;

import java.io.IOException;

/**
 * @author xujin
 *
 * 加密接口
 * 
 */
public class SecApi
{
	private SecApiConf secApiConf;
	private static SecSocket ss = null;
	byte[] outbuf = new byte[ShareVar.MAXLEN];
	
	
    public SecApi()
    {
    	
    }
//
//    public SecApi(String sFile)
//    {
//        if (ShareVar.HsmConf.port == 0)
//        {
//            new GetConf(sFile);
//        }
//    }
    
    public void getHsmConf(){
	      if (ShareVar.HsmConf.port == 0)
	      {
	      	this.secApiConf.getHsmConf();
	      }
    }

    /**
     * input para
     * @param nodeapp
     * @param nodeid
     * @param keytype
     * @param keylen
     * output para
     * @param elmkey
     * @param ezmkey
     * @param keycheck
     * @return
     * @throws Exception
     * 不存数据库
     */

    private int hsmLink(byte[] secbufin, int len, byte[] secbufout) 
    {


			byte[] tmp1 = new byte[ShareVar.MAXLEN];
			byte[] tmp2 = new byte[ShareVar.MAXLEN];
			hsmlog t = new hsmlog();
			String HsmLinkErr = "HsmLinkErrCode:  ";
			
			for (int i = 0; i < ShareVar.HsmConf.headlen; i++) {
				secbufin[2 + i] = 'A';
			}
			for (int i = 0; i < ShareVar.HsmConf.taillen; i++) {
				secbufin[len + i] = 'B';
			}

			len += ShareVar.HsmConf.taillen;

			// len = 2 + datalen 
		/*	secbufin[0] = (byte) (((len - 2) >> 8) & 0xff);
			secbufin[1] = (byte) ((len - 2) & 0xff);
*/
			SecSocket ss = new SecSocket();
			try {
				ss.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			int nRet = 0;
			try {
				nRet = ss.write(secbufin, 0, len);
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (nRet == -1) {
				try {
					t.writeFileString(1,ShareVar.FilePath,ShareVar.FileName,HsmLinkErr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					t.writeFileString(1, ShareVar.FilePath, ShareVar.FileName, "API_ERR_SOCKET_SEND");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return -SecErr.ErrCode.API_ERR_SOCKET_SEND;
			}
			//nRet = ss.read(outbuf, 0, outbuf.length);
			//ss.close();
			
			int i = 0,trylen = 0;
			
			try {
				nRet = ss.read(outbuf, 0, outbuf.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(nRet == 1460){
				trylen += nRet;
				System.arraycopy(outbuf, 0, tmp1, 0, 1460);
				try {
					i = ss.read(outbuf, 0, outbuf.length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.arraycopy(outbuf, 0, tmp2, 0, i);
				nRet += i;
				System.arraycopy(tmp1, 0, secbufout, 0, 1460);
				System.arraycopy(tmp2, 0, secbufout, 1460, nRet -1460 );
			}
			else{
				System.arraycopy(outbuf, 0, secbufout, 0, nRet);
			}
				
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (nRet == -1) {
				try {
					t.writeFileString(1,ShareVar.FilePath,ShareVar.FileName,HsmLinkErr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					t.writeFileString(1, ShareVar.FilePath, ShareVar.FileName, "API_ERR_SOCKET_RECV");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return -SecErr.ErrCode.API_ERR_SOCKET_RECV;
			}
			
//			DataDump.hexPrint("receive data", secbufout, nRet);
			return nRet;
   }

    /**
     * @param digit
     * @param minlen
     * @param maxlen
     * @return
     */
    private int CheckAscii(byte[]digit, int minlen, int maxlen)
    {
        int i = 0, len = 0;

        if (digit == null)
            return SecErr.ErrCode.API_ERR_BUF_NULL;

        if (minlen == maxlen)
            len = minlen;
        else
            len = digit.length;

        if (minlen == 0 && maxlen == 0 && len >= 4096)
            return SecErr.ErrCode.API_ERR_BUF_OVERFLOW;
        if (minlen > len || maxlen < len)
            return SecErr.ErrCode.API_ERR_MAX_LEN;

        for (i = 0; i < len; i++)
            if (!((digit[i] <= '9' && digit[i] >= '0') || (digit[i] <= 'Z' && digit[i] >= 'A')))
                return SecErr.ErrCode.API_ERR_NOT_ASCII;

        return 0;
    }

    /**
    * @param digit
    * @param minlen
    * @param maxlen
    * @return
    */

    private int CheckDigit(byte[] digit, int minlen, int maxlen)
    {
        int i = 0, len = 0;

        if (digit == null)
            return SecErr.ErrCode.API_ERR_BUF_NULL;

        if (minlen == maxlen)
            len = minlen;
        else
            len = digit.length;

        if (minlen == 0 && maxlen == 0 && len >= 0x10000)
            return SecErr.ErrCode.API_ERR_BUF_OVERFLOW;
        if (minlen > len || maxlen < len)
            return SecErr.ErrCode.API_ERR_MAX_LEN;

        for (i = 0; i < len; i++)
            if (digit[i] > '9' || digit[i] < '0')
                return SecErr.ErrCode.API_ERR_NOT_DIGIT;

        return 0;
    }


   
    
     /*
      * para input
      * @param iValue
      * para output
      * @param bOutByte
      * @return
      * 将整数转换为4字节长度的字节数组
      */
     public void int2DecByte(int iValue, byte[] bOutByte)
     {
    	 String sResult = "";
    	 String sTemp = "";
    	 String sValue = Integer.toString(iValue);
    	 
    	 for (int i = 0; i < (4 - sValue.length()); i++)
    	 {
    		 sTemp += "0";
    	 }
    	 
    	 sResult = sTemp.concat(sValue);
    	     	 
    	 System.arraycopy(sResult.getBytes(), 0, bOutByte, 0, 4);  
     }
     
     /*
      * para input
      * @param iValue
      * para output
      * @param bOutByte
      * @return
      *将整数转换为4字节长度的十六进制字节数组
      */     
     public void int2HexByte(int iValue, byte[] bOutByte)
     {
    	 String sResult = "";
    	 String sTemp = "";
    	 String sValue = Integer.toHexString(iValue).toUpperCase();
    	 
    	 for (int i = 0; i < (4 - sValue.length()); i++)
    	 {
    		 sTemp += "0";
    	 }
    	 
    	 sResult = sTemp.concat(sValue);
    	     	 
    	 System.arraycopy(sResult.getBytes(), 0, bOutByte, 0, 4);    	
     }
     
     /* end of xu.guoyou */
     

    /**
    * @param digit
    * @param minlen
    * @param maxlen
    * @return
    */

    private int CheckHexAscii(byte[] digit, int minlen, int maxlen)
    {
        int i = 0, len = 0;

        if (digit == null)
            return  SecErr.ErrCode.API_ERR_BUF_NULL;

        if (minlen == maxlen)
            len = minlen;
        else
            len = digit.length;

        if (minlen == 0 && maxlen == 0 && len >= 4096)
            return SecErr.ErrCode.API_ERR_BUF_OVERFLOW;
        if (minlen > len || maxlen < len)
            return SecErr.ErrCode.API_ERR_MAX_LEN;

        for (i = 0; i < len; i++)
            if (!((digit[i] <= '9' && digit[i] >= '0') || (digit[i] <= 'F' && digit[i] >= 'A')))
                return SecErr.ErrCode.API_ERR_NOT_HEX_ASCII;

        return 0;
    }

    /***************************************************************************
     * 函数功能: 计算MAC
     * 输入参数:
     *   BMKIndex:  区域主密钥(BMK)索引, 值为: 0 ~ 999
     *   MAKLen:    MAK长度,值为:
     *              0x01 -- 表示8字节(单倍长)密钥
     *              0x02 -- 表示16字节(双倍长)密钥
     *              0x03 -- 表示24字节(三倍长)密钥
     *   MACAlgo:   MAC算法标志, 值为:
     *              0x01: ANSI9.9 
     *              0x02: ANSI9.19 (注意: 当MAC算法为ANSI9.19时, MAK长度必须是16或24字节, 即双倍长或三倍长密钥)
     *              0x03: XOR
     *   MAKUnderBMK: MAK密文(BMK加密), 长度为MAKLen, 为扩展后的16进制ASCII码,其实际长度为MAKLen的2倍,如:
     *                MAKey: "0123456789ABCDEF", 则MAKLen == 8; 而其实际长度为16(ASCII字符)bytes
     *   MACDataLen: 计算MAC的数据长度
     *   MACData:    计算MAC的数据
     * 
     * 输出参数:
     *   outMAC: 计算后的MAC(16进制ASCII码)
     **************************************************************************/
    
    public int sec_genMAC(/* input parameter */
            int BMKIndex, 
            byte MAKLen, 
            byte MACAlgo, 
            byte[] MAKUnderBMK, 
            int MACDataLen, 
            byte[] MACData,
           
            /* output parameter */
            byte[] outMAC) throws IOException
    
    {
    	byte[] secbufin = new byte[ShareVar.MAXLEN];
        byte[] secbufout = new byte[ShareVar.MAXLEN];  
    	int wlen, retval, offset;    
    	
    	int maklen = MAKLen << 3;
    	
    	offset = 0;
    	
    	/*组织加密机请求报文*/   
    	secbufin[offset] = 0x04;    
    	secbufin[++offset] = 0x10;    
    	secbufin[++offset] = 0x01;   /* 传输主密钥类型: 0x01 -- 为区域主密钥; 0x02 -- 为终端主密钥; 0x00 -- 本地主密钥 */
    	secbufin[++offset] =(byte)((BMKIndex >> 8) & 0xff);
    	secbufin[++offset] = (byte)(BMKIndex & 0xff);
    	secbufin[++offset] = (byte)maklen;
    	secbufin[++offset] = MACAlgo;
    	
    	offset += 1;
    	
    	byte[] tmp = new byte[MAKUnderBMK.length/2];
    	
    	retval = DataConvert.str2Hex(MAKUnderBMK, MAKUnderBMK.length, tmp);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	System.arraycopy(tmp, 0, secbufin, offset, MAKUnderBMK.length/2);
    	
    	offset += MAKUnderBMK.length/2;
    	
    	byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    	
    	System.arraycopy(iv, 0, secbufin, offset, 8);
    	
    	offset += 8;
    	

    	secbufin[offset] = (byte)((MACDataLen >> 8) & 0xFF);        
    	secbufin[offset+1] = (byte)(MACDataLen & 0xFF);
    	
    	offset += 2;
    	
    	
    	
    	System.arraycopy(MACData, 0, secbufin, offset, MACDataLen);
    	
    	offset += MACDataLen;
    
    //	DataDump.hexPrint("secbufin", secbufin, offset);

        int outlen = hsmLink(secbufin, offset, secbufout);
        if (outlen == -1)
        {
            return -1;
        }

    //    DataDump.hexPrint("secbufout", secbufout, outlen);
        
        if (secbufout[0] == 'A')
        {
        	byte[] tmpMac = new byte[16];
        	byte[] a = new byte[8];
        	
        	System.arraycopy(secbufout, 1, a, 0, 8);
        	retval = DataConvert.hex2Str(a, 8, tmpMac);
            if (retval != 0)
            {
                return SecErr.ErrCode.API_ERR_STR_HEX;
            }
        	System.arraycopy(tmpMac, 0, outMAC, 0, 16);
        }
        else
        {
        	return secbufout[1];
        }
        return 0;
    }

    
    /***************************************************************************
     * 函数功能: 验证MAC
     * 输入参数:
     *   BMKIndex:  区域主密钥(BMK)索引, 值为: 0 ~ 999
     *   MAKLen:    MAK长度,值为:
     *              0x01 -- 表示8字节(单倍长)密钥
     *              0x02 -- 表示16字节(双倍长)密钥
     *              0x03 -- 表示24字节(三倍长)密钥
     *   MACAlgo:   MAC算法标志, 值为:
     *              0x01: ANSI9.9 
     *              0x02: ANSI9.19 (注意: 当MAC算法为ANSI9.19时, MAK长度必须是16或24字节, 即双倍长或三倍长密钥)
     *              0x03: XOR
     *   MAKUnderBMK: MAK密文(BMK加密), 长度为MAKLen, 为扩展后的16进制ASCII码,其实际长度为MAKLen的2倍,如:
     *                MAKey: "0123456789ABCDEF", 则MAKLen == 8; 而其实际长度为16(ASCII字符)bytes
     *   inMAC:     输入的待校验的MAC(长度为4字节)
     *   MACDataLen: 计算MAC的数据长度
     *   MACData:    计算MAC的数据
     * 
     * 输出参数: 无
     **************************************************************************/
    
    public int sec_verifyMAC(/* input parameter */
            int BMKIndex, 
            byte MAKLen, 
            byte MACAlgo, 
            byte[] MAKUnderBMK, 
            byte[] inMAC,
            int MACDataLen, 
            byte[] MACData
           
            ) throws IOException
    
    {
    	byte[] secbufin = new byte[ShareVar.MAXLEN];
        byte[] secbufout = new byte[ShareVar.MAXLEN];  
    	int wlen, retval, offset;    
    	
    	int maklen = MAKLen << 3;
    	
    	offset = 0;
    	
    	/*组织加密机请求报文*/
    	secbufin[offset] = 0x04;    
    	secbufin[++offset] = 0x11;    
    	secbufin[++offset] = 0x01;   /* 传输主密钥类型: 0x01 -- 为区域主密钥; 0x02 -- 为终端主密钥; 0x00 -- 本地主密钥 */
    	secbufin[++offset] =(byte)((BMKIndex >> 8) & 0xff);
    	secbufin[++offset] = (byte)(BMKIndex & 0xff);
    	secbufin[++offset] = (byte)maklen;
    	secbufin[++offset] = MACAlgo;
    	
    	offset += 1;
    	
    	byte[] tmp = new byte[MAKUnderBMK.length/2];
    	
    	retval = DataConvert.str2Hex(MAKUnderBMK, MAKUnderBMK.length, tmp);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	System.arraycopy(tmp, 0, secbufin, offset, MAKUnderBMK.length/2);
    	
    	offset += MAKUnderBMK.length/2;
    	
    	byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    	
    	System.arraycopy(iv, 0, secbufin, offset, 8);
    	
    	offset += 8;
    	
    	byte[] mac = new byte[4];
    	
    	retval = DataConvert.str2Hex(inMAC, inMAC.length, mac);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	
    	System.arraycopy(mac, 0, secbufin, offset, 4);
    	
    	offset += 4;
    	
    	secbufin[offset] = (byte)((MACDataLen >> 8) & 0xFF);        
    	secbufin[offset+1] = (byte)(MACDataLen & 0xFF);
    	
    	offset += 2;
    	
    	
    	
    	System.arraycopy(MACData, 0, secbufin, offset, MACDataLen);
    	
    	offset += MACDataLen;
    
    //	DataDump.hexPrint("secbufin", secbufin, offset);

        int outlen = hsmLink(secbufin, offset, secbufout);
        if (outlen == -1)
        {
            return -1;
        }

    //    DataDump.hexPrint("secbufout", secbufout, outlen);
        
        if (secbufout[0] == 'A')
        {
        	return 0;
        }
        else
        {
        	return secbufout[1];
        }
       
    }
    
    /***************************************************************************
     * 函数功能: 加密明文PIN
     * 输入参数:
     *   BMKIndex:  区域主密钥(BMK)索引, 值为: 0 ~ 999
     *   PIKLen:    PIK长度,值为:
     *              0x01 -- 表示8字节(单倍长)密钥
     *              0x02 -- 表示16字节(双倍长)密钥
     *              0x03 -- 表示24字节(三倍长)密钥
     *   PIKUnderBMK: PIK密文(BMK加密), 长度为PIKLen, 为扩展后的16进制ASCII码,其实际长度为PIKLen的2倍,如:
     *                PIKey: "0123456789ABCDEF", 则PIKLen == 8; 而其实际长度为16(ASCII字符)bytes
     *   clearPIN:  明文PIN, 长度为4 ~ 12的ASCII码
     *   PINFormat:   加密后生成的PINBlock格式, 值为:
     *                0x01 -- ANXI9.8带主帐号(ISO9564-1-0)
     *                0x02 -- Docutel ATM, 1位PIN长度 + n位PIN + 用户自定义数据
     *                0x03 -- Diebold and IBM ATM, n位PIN + 'F'
     *                0x04 -- Plus Network, 与格式1差别在于取主帐号最左12位
     *                0x05 -- ISO9564-1-1格式: 1NP..PR...R
     *                0x06 -- ANXIX9.8不带主帐号
     *   PAN:        用户主帐号(12 ~ 19位ASCII)
     * 
     * 输出参数:
     *   outPINBlock: 用PIK加密后的PINBlock密文
     **************************************************************************/
    public int sec_encrypPINBlock(/* input parameter */
            int BMKIndex, 
            byte PIKLen, 
            byte[] PIKUnderBMK, 
            byte[] clearPIN,
            byte PINFormat, 
            byte[] PAN,
           
            /* output parameter */
            byte[] outPINBlock) throws IOException
    
    {
    	byte[] secbufin = new byte[ShareVar.MAXLEN];
        byte[] secbufout = new byte[ShareVar.MAXLEN];  
    	int wlen, retval, offset;    
    	
    	int piklen = PIKLen << 3;
    	
    	offset = 0;
    	
    	/*组织加密机请求报文*/
    	secbufin[offset] = 0x04;    
    	secbufin[++offset] = 0x05;    
    	secbufin[++offset] =(byte)((BMKIndex >> 8) & 0xff);
    	secbufin[++offset] = (byte)(BMKIndex & 0xff);
    	
    	secbufin[++offset] = (byte)(PIKLen-1);
    	secbufin[++offset] = (byte)piklen;
    	
    	offset += 1;
    	
    	byte[] tmp = new byte[PIKUnderBMK.length/2];
    	
    	retval = DataConvert.str2Hex(PIKUnderBMK, PIKUnderBMK.length, tmp);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	System.arraycopy(tmp, 0, secbufin, offset, PIKUnderBMK.length/2);
    	
    	offset += PIKUnderBMK.length/2;
    	
    	byte[] pinblock = {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
    	
    	pinblock[0] =  (byte)(clearPIN.length);
    	
    	byte[] plainPin = new byte[8];
    	retval = DataConvert.str2Hex(clearPIN, clearPIN.length, plainPin);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	
    	System.arraycopy(plainPin, 0, pinblock, 1, clearPIN.length/2);
    	
    	int panlen = PAN.length;
    	int i;
    	byte[] PANBlock = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    	switch (PINFormat)
    	{
	    	case 0x01:
	    		if (12 == panlen)
	    		{
	    			PANBlock[2] = (byte)((0 << (byte)4) | (byte)(PAN[10] - '0'));
	    		}
	    		else
	    		{
	    			PANBlock[2] = (byte)(((PAN[panlen - 1 - 12] - '0') << 4) | (PAN[panlen - 1 - 12 + 1] - '0'));
	    		}
	
	    		for (i = 1; i < 6; i++)
	    		{
	    			PANBlock[2 + i] = (byte)(((PAN[panlen - 1 - 12 + i*2] - '0') << 4) | (PAN[panlen - 1 - 12 + i*2 + 1] - '0'));
	    		}
	
	    		for (i = 0; i < 8; i++)
	    		{
	    			pinblock[i] ^= PANBlock[i];
	    		}		
	    		break;
	    	case 0x02:
	    		break;
	    	case 0x03:
	    		break;
	    	case 0x04:
	    		break;
	    	case 0x05:
	    		break;
	    	case 0x06:
	    		break;
	    	default:
	    		break;
    	}
    	    		
    	
    	System.arraycopy(pinblock, 0, secbufin, offset, 8);
    	
    	offset += 8;
    	
   // 	DataDump.hexPrint("secbufin", secbufin, offset);

        int outlen = hsmLink(secbufin, offset, secbufout);
        if (outlen == -1)
        {
            return -1;
        }

     //   DataDump.hexPrint("secbufout", secbufout, outlen);
        
        if (secbufout[0] == 'A')
        {
        	byte[] tmpMac = new byte[16];
        	byte[] a = new byte[8];
        	
        	System.arraycopy(secbufout, 1, a, 0, 8);
        	retval = DataConvert.hex2Str(a, 8, tmpMac);
            if (retval != 0)
            {
                return SecErr.ErrCode.API_ERR_STR_HEX;
            }
        	System.arraycopy(tmpMac, 0, outPINBlock, 0, 16);
        }
        else
        {
        	return secbufout[1];
        }
        return 0;
    }

    /***************************************************************************
     * 函数功能: 解密密文PINBlock
     * 输入参数:
     *   BMKIndex:  区域主密钥(BMK)索引, 值为: 0 ~ 999
     *   PIKLen:    PIK长度,值为:
     *              0x01 -- 表示8字节(单倍长)密钥
     *              0x02 -- 表示16字节(双倍长)密钥
     *              0x03 -- 表示24字节(三倍长)密钥
     *   PIKUnderBMK: PIK密文(BMK加密), 长度为PIKLen, 为扩展后的16进制ASCII码,其实际长度为PIKLen的2倍,如:
     *                PIKey: "0123456789ABCDEF", 则PIKLen == 8; 而其实际长度为16(ASCII字符)bytes
     *   cipherPINBlock:  密文PINBlock(注意: 其格式应符合ANSI9.8)
     *   PAN:        用户主帐号(12 ~ 19位ASCII)
     * 
     * 输出参数:
     *   clearPIN: 解密后的PIN明文, 如: "123456"
     **************************************************************************/
    public int sec_decryptPINBlock(/* input parameter */
            int BMKIndex, 
            byte PIKLen, 
            byte[] PIKUnderBMK, 
            byte[] cipherPINBlock,
            byte[] PAN,
           
            /* output parameter */
            byte[] clearPIN) throws IOException
    
    {
    	byte[] secbufin = new byte[ShareVar.MAXLEN];
        byte[] secbufout = new byte[ShareVar.MAXLEN];  
    	int wlen, retval, offset;    
    	
    	int piklen = PIKLen << 3;
    	
    	offset = 0;
    	
    	/*组织加密机请求报文*/ 
    	secbufin[offset] = 0x04;    
    	secbufin[offset + 1] = 0x22;    
    	secbufin[offset + 2] =(byte)((BMKIndex >> 8) & 0xff);
    	secbufin[offset + 3] = (byte)(BMKIndex & 0xff);
    	
    	offset += 4;
    	
    	byte[] plainPin = new byte[8];
    	retval = DataConvert.str2Hex(cipherPINBlock, cipherPINBlock.length, plainPin);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	
    	System.arraycopy(plainPin, 0, secbufin, offset, 8);
    	
    	offset += 8;
    	
    	int panlen = PAN.length;
    	int i;
    	byte[] PANBlock = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    	
    	byte[] a = new byte[8];
	    if (12 == panlen)
	    {
	    	retval = DataConvert.str2Hex(PAN, 12, a);
	    	if (retval != 0)
	        {
	            return SecErr.ErrCode.API_ERR_STR_HEX;
	        }
	    	System.arraycopy(a, 0, PANBlock, 2, 6);
	    	
	    }
	    else
	    {
	    	
	    	byte[] b = new byte[12];
	    	
	    	System.arraycopy(PAN, (panlen - 1 - 12), b, 0, 12);
	    	
	    	retval = DataConvert.str2Hex(b, 12, a);
	    	if (retval != 0)
	        {
	            return SecErr.ErrCode.API_ERR_STR_HEX;
	        }
	    	System.arraycopy(a, 0, PANBlock, 2, 6);
	    	
	   	}
	
	    System.arraycopy(PANBlock, 0, secbufin, offset, 8);
	    
    	offset += 8;
    	
    	secbufin[offset++] = (byte)piklen;
    	
    	byte[] tmp = new byte[PIKUnderBMK.length/2];
    	
    	retval = DataConvert.str2Hex(PIKUnderBMK, PIKUnderBMK.length, tmp);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	System.arraycopy(tmp, 0, secbufin, offset, PIKUnderBMK.length/2);
    	
    	offset += PIKUnderBMK.length/2;
    	
    	
    //	DataDump.hexPrint("secbufin", secbufin, offset);

        int outlen = hsmLink(secbufin, offset, secbufout);
        if (outlen == -1)
        {
            return -1;
        }

    //    DataDump.hexPrint("secbufout", secbufout, outlen);
        
        if (secbufout[0] == 'A')
        {
        	byte[] tmpPIN = new byte[16];
        	byte[] d = new byte[16];
        	      	
        	System.arraycopy(secbufout, 1, d, 0, 8);
        	retval = DataConvert.hex2Str(d, 8, tmpPIN);
            if (retval != 0)
            {
                return SecErr.ErrCode.API_ERR_STR_HEX;
            }
            
            int pinLen = tmpPIN[1] - 0x30;
            
            
        	System.arraycopy(tmpPIN, 2, clearPIN, 0, pinLen);
        }
        else
        {
        	return secbufout[1];
        }
        return 0;
    }
    
    /***************************************************************************
     * 函数功能: 解密密文PINBlock
     **************************************************************************/
    public int HSMAPIDecryptPIN(/* input parameter */
    		int iKeyLen,
            byte[] caPIK, 
            int iFormat,
            byte[] caPINBlock,
            byte[] caPAN,
           
            /* output parameter */
            byte[] iPINLen,
            byte[] clearPIN) throws IOException
    
    {
    	byte[] secbufin = new byte[ShareVar.MAXLEN];
        byte[] secbufout = new byte[ShareVar.MAXLEN];  
    	int wlen, retval, offset;    
    	
    	offset = 0;
    	
    	/*组织加密机请求报文*/
    	secbufin[offset] = (byte)0xD1;    
    	secbufin[offset + 1] = 0x26;  
    	secbufin[offset + 2] =(byte)(iKeyLen);
    	
    	offset += 3;
    	
    	if (caPIK.length != (byte)(iKeyLen) * 2)
    	{
    		return -1;
    		
    	}
    	byte[] tmp = new byte[caPIK.length/2];
    	
    	retval = DataConvert.str2Hex(caPIK, caPIK.length, tmp);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	System.arraycopy(tmp, 0, secbufin, offset, caPIK.length/2);
    	
    	offset += caPIK.length/2;
    	
    	secbufin[offset++] = (byte)iFormat;   
    	
    	byte[] plainPin = new byte[8];
    	retval = DataConvert.str2Hex(caPINBlock, caPINBlock.length, plainPin);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	
    	System.arraycopy(plainPin, 0, secbufin, offset, 8);
    	
    	offset += 8;
    	
    	int panlen = caPAN.length;
    	int i;
    	
    	switch(iFormat)
    	{
    		case 1:
    		case 4:
    			if (caPAN == null)
    			{
    				return -1;
    			}
    			System.arraycopy(caPAN, 0, secbufin, offset, caPAN.length);
    			offset += caPAN.length;
    			break;
    		case 2:
    			if (caPAN == null)
    			{
    				return -1;
    			}
    			System.arraycopy(caPAN, 0, secbufin, offset, 12);
    			offset += 12;
    			break;
    	
    	}
	
    //	DataDump.hexPrint("secbufin", secbufin, offset);

        int outlen = hsmLink(secbufin, offset, secbufout);
        if (outlen == -1)
        {
            return -1;
        }

    //    DataDump.hexPrint("secbufout", secbufout, outlen);
        
        if (secbufout[0] == 'A')
        {
        	
        	System.arraycopy(secbufout, 1, iPINLen, 0, 1);
        	
        	System.arraycopy(secbufout, 2, clearPIN, 0, secbufout[1]);
        	
        }
        else
        {
        	return secbufout[1];
        }
        return 0;
    }
    
    
    /***************************************************************************
     * 函数功能: 加密PINBlock
     **************************************************************************/
    public int HSMAPIEncryptPIN(/* input parameter */
    		int iKeyLen,
            byte[] caPIK, 
            int iFormat,
            int iPINLen,
            byte[] caPINBlock,
            byte[] caPAN,
           
            /* output parameter */
            byte[] clearPIN) throws IOException
    
    {
    	byte[] secbufin = new byte[ShareVar.MAXLEN];
        byte[] secbufout = new byte[ShareVar.MAXLEN];  
    	int wlen, retval, offset;    
    	
    	offset = 0;
    	
    	/*组织加密机请求报文*/  
    	secbufin[offset] = (byte)0xD1;    
    	secbufin[offset + 1] = 0x22;  
    	secbufin[offset + 2] =(byte)(iKeyLen);
    	
    	offset += 3;
    	
    	if (caPIK.length != (byte)(iKeyLen) * 2)
    	{
    		return -1;
    		
    	}
    	byte[] tmp = new byte[caPIK.length/2];
    	
    	retval = DataConvert.str2Hex(caPIK, caPIK.length, tmp);
    	if (retval != 0)
        {
            return SecErr.ErrCode.API_ERR_STR_HEX;
        }
    	System.arraycopy(tmp, 0, secbufin, offset, caPIK.length/2);
    	
    	offset += caPIK.length/2;
    	
    	secbufin[offset++] = (byte)iFormat;   
    	secbufin[offset++] = (byte)iPINLen;   
    	
    	System.arraycopy(caPINBlock, 0, secbufin, offset, iPINLen);
    	
    	offset += iPINLen;
    	
    	int panlen = caPAN.length;
    	int i;
    	
    	switch(iFormat)
    	{
    		case 1:
    		case 4:
    			if (caPAN == null)
    			{
    				return -1;
    			}
    			System.arraycopy(caPAN, 0, secbufin, offset, caPAN.length);
    			offset += caPAN.length;
    			break;
    		case 2:
    			if (caPAN == null)
    			{
    				return -1;
    			}
    			System.arraycopy(caPAN, 0, secbufin, offset, 12);
    			offset += 12;
    			break;
    	
    	}
	
    //	DataDump.hexPrint("secbufin", secbufin, offset);

        int outlen = hsmLink(secbufin, offset, secbufout);
        if (outlen == -1)
        {
            return -1;
        }

   //     DataDump.hexPrint("secbufout", secbufout, outlen);
        
        if (secbufout[0] == 'A')
        {
        	byte[] tmpPIN = new byte[16];
        	byte[] d = new byte[16];
        	      	
        	System.arraycopy(secbufout, 1, d, 0, 8);
        	retval = DataConvert.hex2Str(d, 8, tmpPIN);
            if (retval != 0)
            {
                return SecErr.ErrCode.API_ERR_STR_HEX;
            }
           
            
        	System.arraycopy(tmpPIN, 0, clearPIN, 0, 16);
        }
        else
        {
        	return secbufout[1];
        }
        return 0;
    }
    
    public SecApiConf getSecApiConf() {
		return secApiConf;
	}

	public void setSecApiConf(SecApiConf secApiConf) {
		this.secApiConf = secApiConf;
	}
}






