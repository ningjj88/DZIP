/*
 * @(#)PinSecurityModule.java	2007-11-10
 *
 * Copyright 2005 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package csii.dzip.action.util;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import csii.pe.service.comm.StringChange;
import com.csii.pe.common.util.Base64;
import com.csii.pe.common.util.Hex2Byte;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pe.security.EnDecryptFactory;
import com.csii.pe.service.comm.CommunicationException;
import com.union.HsmAPI.HSMException;
import com.union.HsmAPI.unionHsmAPI;

/**
 * Comment
 * <p>
 * Created on 2008-7-29 Modification history
 * <p>
 *
 * @author Larry Dai, Internet Banking System Development Group, CSII
 * @version 1.0
 * @since 1.0
 */
public class PinSecurityModule implements InitializingBean {

	private String ip;
	private int port;
	private String[] macFields;
	private RSAPublicKey pubKey;
	private String m;
	private String e;

	private RSAPublicKey devPubKey;
	private String devm;
	private String deve;
	private boolean HSM;
	private String pinkey;
	private String bankpinkey;
	private String mackey;

	private boolean productMode;

	public PinSecurityModule() {
		super();
	}

	private static byte[] pkcs1pad2(String s, int n) {
		byte[] ba = new byte[n];

		int len = s.length();

		ba[0] = (byte) (0x30 + len / 10);
		ba[1] = (byte) (0x30 + len % 10);

		int j = 2;
		int i = 0;
		while (i < len && n > 0) {
			ba[j++] = (byte) s.charAt(i++);
		}

		while (j < n) {
			ba[j++] = 1;
		}

		return ba;
	}
	//产生MAC BUFFER
	private String calculateMACBuff(Map map){
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < macFields.length; i++) {
			Object value = map.get(macFields[i]);
			if (value != null && value.toString().trim().length() > 0) {
				if(macFields[i].equals("RcvCode"))
				{
					Object value1 = map.get(macFields[0]);
					if(value1 != null && value1.toString().trim().length() > 0
						&& !(value1.equals("0800") || value1.equals("0810"))){
						continue;
					}
				}
				if(macFields[i].equals("RespCode"))
				{
					Object value1 = map.get(macFields[0]);
					if(value1 != null && value1.toString().trim().length() > 0
						&& (value1.equals("0520") || value1.equals("0530")
							||value1.equals("0522") || value1.equals("0532"))){
						continue;
					}
				}
				// 两变长处理
				if (macFields[i].equals("PrimaryAcct")
						|| macFields[i].equals("AcqCode")
						|| macFields[i].equals("FwdCode")
						|| macFields[i].equals("RcvCode")
						|| macFields[i].equals("AcctId1")
						|| macFields[i].equals("AcctId2")) {

					if (value.toString().trim().length() > 10)
						value = value.toString().trim().length() + value.toString();
					else
						value = "0" + value.toString().trim().length() + value.toString();
				}
				// 域90只取前20个字节
				if (macFields[i].equals("OrigDataElemts")) {
					if(value.toString().length() < 20) {
						throw new RuntimeException("90域 长度非法。长度：" + value.toString().length() + "，值：" + value.toString());
					} else {
						value = value.toString().trim().substring(0, 20);
					}
				}
				if (sb.length() > 0)
					sb.append(" ");
				sb.append(value);
			}
		}
		String macData = sb.toString().toUpperCase();
		//只保留字母（A-Z）,数字（0-9）,空格,逗号(,),句号(.)
		sb = new StringBuffer();
		for(int i=0;i<macData.length();i++)
		{
			if((macData.charAt(i)>='A' && macData.charAt(i)<='Z') ||
			   (macData.charAt(i)>='0' && macData.charAt(i)<='9') ||
			    macData.charAt(i)==' ' || macData.charAt(i) == ',' ||
			    macData.charAt(i) == '.')
				sb.append(macData.charAt(i));
		}
		macData = sb.toString();
		//System.out.println("macdata=" + macData);
		return macData;
	}
	public String calculateMAC(Map map,String Key)
	{
		mackey=Key.substring(0, 16);
		return calculateMAC(map);
	}
	public String calculateMAC(Map map) {

		String macData = calculateMACBuff(map);

		if (isHSM()) {
			// As unionHsmAPI is not thread-safe, create a new object for every call.
			unionHsmAPI tt = new unionHsmAPI(ip, port);

			String mac;
			try {
				System.err.println("macData:" + macData);
				mac = tt.EMBISGenerateMac("IB.coresys.zak", macData);
				return mac;

			} catch (HSMException e) {
				throw new PeRuntimeException("mac_fail", e);
			}
		} else {
			// String MacWorkKey=StringChange.HexToString("AB81FC5FC20802D8");
			// DesEncrypt wDes = new DesEncrypt(MacWorkKey);

			String MACKey = StringChange.HexToString(mackey);
			// MACKey=wDes.Decrypt(MACKey);

			DesEncrypt Des = new DesEncrypt(MACKey);
			String mac = Des.MAC(macData);
			mac = StringChange.StringToHex(mac);
			// wDes=null;
			Des = null;
			return mac;
		}
	}

	/**
	 * 异或函数，传入密码、账号和PIN格式，输出异或结果
	 * Create By CSII(Gu.Kun)
	 * 2010-9-7
	 *
	 * @param pin
	 * @param pan
	 * @param mode
	 * @return pin^pan
	 */
	private String PinXorPan(String pin, String pan, int mode) {
		String pinBlock;
		StringBuffer strValue = new StringBuffer();

		// pinBlock的处理
		int len = pin.length();
		pinBlock = len + pin;
		if (len < 10) {
			pinBlock = "0" + pinBlock;
		}
		// 不足16位右边补F
		for (; pinBlock.length() < 16;)
			pinBlock += "F";

		if (mode == 1) {
			// 无主账号
			return pinBlock.toUpperCase();
		} else {
			// 有主账号
			// panBlock的处理
			String panBlock = pan.substring(pan.length() - 13, pan.length() - 1);
			// 不足16位左边补0
			for (; panBlock.length() < 16;)
				panBlock = "0" + panBlock;

			for (int i = 0; i < pinBlock.length(); i++) {
				int num1 = Integer.parseInt(pinBlock.substring(i, i + 1), 16);
				int num2 = Integer.parseInt(panBlock.substring(i, i + 1), 16);
				String s = Integer.toHexString(num1 ^ num2);
				strValue.append(s);
			}
			return strValue.toString().toUpperCase();
		}
	}

	private String cipherXorPan(String cipher, String pan) {
		String pinBlock;
		StringBuffer strValue = new StringBuffer();
		String panBlock = pan.substring(pan.length() - 13, pan.length() - 1);
		// 不足16位左边补0
		for (; panBlock.length() < 16;)
			panBlock = "0" + panBlock;

		for (int i = 0; i < cipher.length(); i++) {
			int num1 = Integer.parseInt(cipher.substring(i, i + 1), 16);
			int num2 = Integer.parseInt(panBlock.substring(i, i + 1), 16);
			String s = Integer.toHexString(num1 ^ num2);
			strValue.append(s);
		}
		int len=Integer.parseInt(strValue.substring(0,2));
		pinBlock=strValue.substring(2,len+2);
		return pinBlock;
	}

	public String pinEncrypt(String pin, String pan, int mode, int encryptMode) throws CommunicationException {

		if (isHSM()) {
			try {
				byte[] cipherBytes = Base64.base64ToByteArray(pin);
				String cipherText = Hex2Byte.byte2Hex(cipherBytes);
				// As unionHsmAPI is not thread-safe, create a new object for every
				// call.
				unionHsmAPI tt = new unionHsmAPI(ip, port);
				String j = tt.EMBISGeneratePINFromRSAToDes("WY", "IB.coresys.zpk", cipherText.toUpperCase());

				return j;
			} catch (Exception e) {
				throw new CommunicationException("pinEncrypt_fail", e);
			}
		} else {
			String stringXor = PinXorPan(pin, pan, mode);
			String StrKey = StringChange.HexToString(pinkey);
			DesEncrypt Des = new DesEncrypt(StrKey);
			stringXor = StringChange.HexToString(stringXor);
			String StrData = Des.Encrypt(stringXor);
			return StrData;
		}
	}

	public String pinDecrypt(String cipher, String pan, int mode) throws CommunicationException {
		return cipher;
	}

	public String bankPinEncrypt(String pin, String pan) throws Exception {
			String stringXor = PinXorPan(pin, pan, 0);
			String StrKey = StringChange.HexToString(bankpinkey);
			DesEncrypt Des = new DesEncrypt(StrKey);
			stringXor = StringChange.HexToString(stringXor);
			String StrData = Des.Encrypt(stringXor);
			return StrData;
	}

	public String bankPinDecrypt(String cipher, String pan) throws Exception {
		String StrKey=StringChange.HexToString(bankpinkey);
		DesEncrypt Des = new DesEncrypt(StrKey);
		String stringXor = Des.Decrypt(cipher);
		stringXor = StringChange.StringToHex(stringXor);
		stringXor = cipherXorPan(stringXor, pan);
		return stringXor;
	}

	public String tellerPinEncrypt(String pin, String pan, int mode) throws CommunicationException {
		try {
			byte[] cipherBytes;
			if (productMode)
				cipherBytes = EnDecryptFactory.getInstance().enCrypt("RSA", pkcs1pad2(pin, (pubKey.getModulus().bitLength() + 7) >> 3),
						pubKey, "NONE/NOPADDING", null);
			else
				cipherBytes = EnDecryptFactory.getInstance().enCrypt("RSA",
						pkcs1pad2(pin, (devPubKey.getModulus().bitLength() + 7) >> 3), devPubKey, "NONE/NOPADDING", null);

			String cipherText = Hex2Byte.byte2Hex(cipherBytes);

			// As unionHsmAPI is not thread-safe, create a new object for every call.
			unionHsmAPI tt = new unionHsmAPI(ip, port);
			String j = tt.EMBISGeneratePINFromRSAToDes("WY", "IB.coresys.zpk", cipherText.toUpperCase());

			return j;
		} catch (Exception e) {
			throw new CommunicationException("pinEncrypt_fail", e);
		}
	}

	public static void main(String[] arg) throws Exception {

		PinSecurityModule module = new PinSecurityModule();
		// module.setIp("10.128.6.15");
		// module.setPort(17102);
		// module.setM("b2e103027d7f251a4d10555281091473a28322f4e5d884efbd6e1343e0f638c1fc784bbc74e3f41b748885742475e744157c02bfd683500ac4cf54f4a134a6a15480609c61785e0812146a55a43d2eb58db857f2bbdb5ed9d919290a1bedd4ff66eb21b6b4160897acf1a4ad2fe1c336ebfd9f10047dca18b289831ce86cb955");
		// module.setE("10001");
		//
		// module.afterPropertiesSet();
		// System.err.println(module.tellerPinEncrypt("abcdefee", null, 0));
		// System.err.println(module.pinEncrypt("LHsHQkPL1guVOh5vmkq5VShRpFDd2fgITPRvpkNHzl54jewxM5UldA65WbTeBU+y8UyXR1s/MaihZH40PFg0SJHB8MM4uHl8aAgFtvnjsgiUI3V9a7249CWbhxaQ1nB5p3KWi7IR5HQMHe8qSy1ewVq8k+4ROlZ+2s2Hgd2UvWw=",
		// null, 0));
//		module.setPinkey("3324BEB72D444265");
	//	module.setHSM(false);
	//	module.setBankpinkey("3324BEB72D444111");
		//module.pinEncrypt("123456", "6223880000000607", 1, 1);
		String EnStr=module.bankPinEncrypt("123456", "123456789012345678");
		EnStr=module.bankPinDecrypt("ê]lóa=?", "6221359010100018508");
		System.out.println("enStr="+EnStr);


	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMacFields(String macFields) {
		this.macFields = macFields.split(",");
	}

	public void setM(String m) {
		this.m = m;
	}

	public void setE(String e) {
		this.e = e;
	}

	public void afterPropertiesSet() throws Exception {
//		if (productMode)
//			pubKey = EnDecryptFactory.getRSAPublicKey(m, e);
//		else
//			devPubKey = EnDecryptFactory.getRSAPublicKey(devm, deve);
	}

	public void setDevm(String devm) {
		this.devm = devm;
	}

	public void setDeve(String deve) {
		this.deve = deve;
	}

	public void setProductMode(boolean productMode) {
		this.productMode = productMode;
	}

	public boolean isHSM() {
		return HSM;
	}

	public void setHSM(boolean hsm) {
		HSM = hsm;
	}

	public void setPinkey(String pinkey) {
		this.pinkey = pinkey;
	}

	public void setMackey(String mackey) {
		this.mackey = mackey;
	}

	public void setBankpinkey(String bankpinkey) {
		this.bankpinkey = bankpinkey;
	}

}
