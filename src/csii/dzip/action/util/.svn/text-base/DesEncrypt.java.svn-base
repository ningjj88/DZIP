package csii.dzip.action.util;

import java.io.UnsupportedEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import csii.pe.service.comm.StringChange;

public class DesEncrypt {
	Cipher ecipher;

	Cipher dcipher;
	DesEncrypt(String strkey) {
		try {
			// 从原始密匙数据创建DESKeySpec对象
			KeySpec dks=null;
			String Algorithm=null;
			String CipherMode=null;

			if(strkey.length() == 8)
			{
				Algorithm="DES";
				CipherMode="DES/ECB/NoPadding";
				dks = new DESKeySpec(strkey.getBytes("ISO8859-1"));
			}else if(strkey.length() == 16)
			{
				Algorithm="DESede";
				CipherMode="DESede/ECB/NoPadding";
				dks = new SecretKeySpec(strkey.getBytes("ISO8859-1"),Algorithm);
			}else if(strkey.length() == 24)
			{
				Algorithm="DESede";
				CipherMode="DESede/ECB/NoPadding";
				dks = new DESedeKeySpec(strkey.getBytes("ISO8859-1"));
			}else
			{
				System.out.println("Key length not 8 byte and not 16 byte and not 24 byte");
				return;
			}
			//PBEWithHMacAndDES
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			// 一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);

			SecretKey key = keyFactory.generateSecret(dks);

			ecipher = Cipher.getInstance(CipherMode);
			dcipher = Cipher.getInstance(CipherMode);

			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);

			/*
			 * IV模式 byte[] initVector = new byte[] { 0x0, 0x0, 0x00, 0x00, 0x00,
			 * 0x00, 0x00, 0x00 }; AlgorithmParameterSpec algParamSpec = new
			 * IvParameterSpec(initVector); ecipher =
			 * Cipher.getInstance("DES/CBC/PKCS5Padding"); dcipher =
			 * Cipher.getInstance("DES/CBC/PKCS5Padding");
			 * ecipher.init(Cipher.ENCRYPT_MODE, key,algParamSpec);
			 * dcipher.init(Cipher.DECRYPT_MODE, key,algParamSpec); *
			 */

		} catch (javax.crypto.NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (java.security.InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public String Encrypt(String str) {
		try {
			byte[] str1=ecipher.doFinal(str.getBytes("ISO8859-1"));
			return new String(str1,"ISO8859-1");

		} catch (javax.crypto.BadPaddingException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String Decrypt(String dec) {
		try {
			byte[] dec1 = dec.getBytes("ISO8859-1");
			return new String(dcipher.doFinal(dec1),"ISO8859-1");

		} catch (javax.crypto.BadPaddingException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public String MAC(String macStr) {

		StringBuffer sb = new StringBuffer();
		sb.append(macStr);

		int len=8-(sb.length() % 8);
		if(len != 8 && len != 0)
		{
			for(int i=0;i< len;i++)
				sb.append('\0');
		}

		macStr =  sb.toString().toUpperCase();
		//System.out.println(macStr);
		int Count=macStr.length()/8;
		String tmpStr1=null;
		String tmpStr2=null;

		for(int i=0;i<Count;i++)
		{
			if(i == 0)
			{
				tmpStr1=macStr.substring(0, 8);
			}else
			{
				tmpStr1=macStr.substring(i*8, (i+1)*8);
				tmpStr1=StringChange.StringXor(tmpStr1,tmpStr2);
			}
			tmpStr2=Decrypt(tmpStr1);
		}
		return tmpStr2;
	}
	/* b2iu是一个把byte按照不考虑正负号的原则的＂升位＂程序，因为java没有unsigned运算 */
	private static long b2iu(byte b) {

		return b < 0 ? b & 0x7F + 128 : b;

	}

	public static String StrToHex(byte[] data) {
		String Hex = "";
		long H;
		char[] DigitNormal = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		for (int i = 0; i < data.length; i++) {
			H = b2iu(data[i]);
			Hex = Hex + DigitNormal[(int) (H >>> 4 & 0xF)];
			Hex = Hex + DigitNormal[(int) (H & 0xF)];
		}
		return Hex;

	}

	public static byte[] HexToStr(String Hex) {
		int H1, H2;
		byte[] data = new byte[Hex.length() / 2];
		for (int i = 0; i < Hex.length(); i += 2) {
			H1 = Hex.charAt(i);
			if (H1 >= '0' && H1 <= '9') {
				H1 = H1 - '0';
			} else if (H1 >= 'A' && H1 <= 'F') {
				H1 = H1 - 'A' + 10;
			} else {
				H1 = 0;
			}
			H2 = Hex.charAt(i + 1);
			if (H2 >= '0' && H2 <= '9') {
				H2 = H2 - '0';
			} else if (H2 >= 'A' && H2 <= 'F') {
				H2 = H2 - 'A' + 10;
			} else {
				H2 = 0;
			}
			data[i / 2] = (byte) ((H1 * 16 + H2) & 0xFF);
		}
		return data;
	}

	public static void main(String args[]) {
		String Str;
		String data;
		String dataHex="0123456789ABCDE7";
		String StrHex="1AFEAD0492191ACD";

		Str=StringChange.HexToString(StrHex);
		//Str="11111111";
		DesEncrypt Des = new DesEncrypt(Str);

		data=StringChange.HexToString(dataHex);
		data="0210 166223880000000607 304000 0908160818 538594 6011 02 0804053910 0804053910 A0 12344321 1234567812345";

//		data = Des.Encrypt(data);
//		System.out.println("Encrypt Data="+data+" Len="+data.length());
//		dataHex=StringChange.StringToHex(data);
//		System.out.println("Encrypt DataHex="+dataHex);
//		data = Des.Decrypt(data);
//		System.out.println("Decrypt Data="+data);
//		dataHex=StringChange.StringToHex(data);
//		System.out.println("Decrypt DataHex="+dataHex);

		data = Des.MAC(data);
		System.out.println("MAC Data="+data);
		dataHex=StringChange.StringToHex(data);
		System.out.println("Encrypt DataHex="+dataHex);

	}

}

