package csii.dzip.core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import csii.base.action.util.Util;
/**
 * 用于ATMP传入密码的解密功能
 * @author chenshq
 * @version 1.0.0
 * @since 2011-3-1
 */
public class Decrypt4Des {
	private byte[] desCrypto(byte[] datasource, String password) {
		try {

			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密匙初始化Cipher对象
			// SecureRandom random = new SecureRandom();
			// cipher.init(Cipher.ENCRYPT_MODE, securekey,random);
			cipher.init(Cipher.ENCRYPT_MODE, securekey);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] decrypt(byte[] src, String password) throws Exception {
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		// 用密匙初始化Cipher对象
		// SecureRandom random = new SecureRandom();
		// cipher.init(Cipher.ENCRYPT_MODE, securekey,random);
		cipher.init(Cipher.DECRYPT_MODE, securekey);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}

	public static String byteToHex(byte abyte0[]) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < abyte0.length; i++) {
			String s = Integer.toHexString(abyte0[i] & 0xff);
			if (s.length() != 2)
				stringbuffer.append('0').append(s);
			else
				stringbuffer.append(s);
		}
		return stringbuffer.toString().toUpperCase();
	}

	public static String strToHex(String str) {
		StringBuffer strbuf = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String s = Integer.toHexString(str.charAt(i));
			if (s.length() != 2)
				strbuf.append('0').append(s);
			else
				strbuf.append(s);
		}
		return strbuf.toString();
	}

	public static String HexToString(String strValue) {
		int intCounts = strValue.length() / 2;
		String strReturn = "";
		String strHex = "";
		int intHex = 0;
		byte byteData[] = new byte[intCounts];
		try {
			for (int intI = 0; intI < intCounts; intI++) {
				strHex = strValue.substring(0, 2);
				strValue = strValue.substring(2);
				intHex = Integer.parseInt(strHex, 16);
				if (intHex > 128)
					intHex = intHex - 256;
				byteData[intI] = (byte) intHex;
			}
			strReturn = new String(byteData, "ISO8859-1");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strReturn;
	}

	private static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 | _b1);
		return ret;
	}

	/**
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] tmp = src.getBytes();
		int length = src.getBytes().length / 2;
		byte[] ret = new byte[length];
		// System.out.println("tmp.length()：" +tmp.length);
		for (int i = 0; i < (tmp.length / 2); ++i) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 
	 * @param key
	 *            密钥
	 * @param pwd
	 *            密文
	 * @return 明文
	 */
	public static String decryptPWD(String key, String pwd) {
		String password = null;
		byte[] decryptPWD=null;
		Decrypt4Des des = new Decrypt4Des();
		// 直接将如上内容解密
		try {
			decryptPWD=HexString2Bytes(pwd);
		} catch (Exception e1) {
			decryptPWD=HexString2Bytes(Util.StringToHex(pwd));
		}
		byte[] decryResult;
		try {
			decryResult = des.decrypt(decryptPWD, key);
			password = new String(decryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return password.trim();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 待加密内容
		// String str = "56A7F898297EF7FD";
		String str = "111111  ";
		// 密码，长度要是8的倍数
		String password = "39876545";
		Decrypt4Des des = new Decrypt4Des();
		byte[] result = des.desCrypto(str.getBytes(), password);
		// System.out.println("加密后内容为："+byteToHex(result));
		// 直接将如上内容解密
		try {
			// byte[] decryResult = des.decrypt(HexString2Bytes(str), password);
			byte[] decryResult = des.decrypt(result, password);
			System.out.println("解密后内容为：" + new String(decryResult));
			System.out.println("解密后内容为：" + byteToHex(decryResult));
			System.out.println(password.substring(password.length()-2,password.length()));
			// System.out.println("解密后内容为："+ HexToString("81E43F1B1CE2CA10"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
}
