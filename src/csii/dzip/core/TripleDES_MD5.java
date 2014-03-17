/**
 * 
 */
package csii.dzip.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2011-2-28
 */
public class TripleDES_MD5 {
	
	private static final String MD5 = "MD5"; // 加密算法
	private static final String UTF8_CHARSET = "UTF-8"; // UTF-8字符集
	private static final String DEFAULT_CHARSET = "ISO-8859-1"; // ISO-8859-1字符集
	private static final String Algorithm = "DESede";// 加密算法的名称
	private static final String DESEDE_ECB_NOPADDING = "DESede/ECB/NoPadding"; // 采用NoPadding的ECB模式的tripleDES
	
	public static final int KEYTYPE_DEFAULT = 0; // 通过参数一个24位字符串（由字母（大小写均可）和数字组成）
	public static final int KEYTYPE_HEXKEY = 1; // 通过参数传入一个48位的16进制字符串（由字母（大小写均可）和数字组成）
	public static final int KEYTYPE_RANDOM_HEXKEY = 2; // 程序随机生成一个48位的16进制字符串
	public static final int KEYTYPE_BASE64KEY = 3; // 通过参数传入一个32位的base64编码格式的字符串（由字母（大小写均可）和数字组成）
	
	private static String base64Key = "YXMXMJM0NTY3ODLMYXNKZMFZZGZHREVM"; // A3F2569DESJEIWBCJOTY45DYQWF68H1Y
	private static String hexKey = "6173173093343536373832cc61734a64c15964664744454c"; // 037176e7af4311224421604224e4d8e390d841617af07d58
	private static String key = "as123456789fasdfasdfaDEf"; // qv????C\"D!`B$??????????Aaz??}X
	private static Cipher cipher;// 密码器
	private static SecretKey deskey;// 密钥
	
	private static TripleDES_MD5 instance = null;
	
	/**
	 * 私有的默认构造函数
	 * 
	 * @throws IOException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 */
	private TripleDES_MD5(String key, int keyType) throws InvalidKeyException, InvalidKeySpecException, IOException {
		initKey(key, keyType);
	}
	
	/**
	 * 默认采用 key 为程序自带的 base64Key,keyType为 3
	 * 
	 * @return
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public static synchronized TripleDES_MD5 getInstance() throws InvalidKeyException, InvalidKeySpecException, IOException {
		return getInstance(base64Key, 3);
	} 
	
	/**
	 * 获得TripleDES_MD5单例
	 * 
	 * @param key 
	 * @param keyType 
	 * @return
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public static synchronized TripleDES_MD5 getInstance(String key, int keyType) throws InvalidKeyException, InvalidKeySpecException, IOException {
		if (instance == null) {
			instance = new TripleDES_MD5(key, keyType);
		}
		return instance;
	} 
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		// key 为 24位字符
		System.out.println("*************key 为 24位字符*****************");
		TripleDES_MD5.getInstance(key, TripleDES_MD5.KEYTYPE_DEFAULT);
		TripleDES_MD5.getCipherTextOf3Des_MD5("100000252686252686");
		System.out.println();
		// key 为 48位十六进制字符
		System.out.println("**********key 为 48位十六进制字符*************");
		instance = null;
		TripleDES_MD5.getInstance(hexKey, TripleDES_MD5.KEYTYPE_HEXKEY);
		TripleDES_MD5.getCipherTextOf3Des_MD5("100000252686252686");
		System.out.println();
		// key 为 32位base64编码格式字符
		System.out.println("*******key 为 32位base64编码格式字符**********");
		instance = null;
		TripleDES_MD5.getInstance(base64Key, TripleDES_MD5.KEYTYPE_BASE64KEY);
		TripleDES_MD5.getCipherTextOf3Des_MD5("100000252686252686");
		System.out.println();
	}
	
	/**
	 * 获得经由3DES+MD5加密后的密文
	 * 
	 * @param data
	 * @return
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String getCipherTextOf3Des_MD5(String data) throws InvalidKeyException, InvalidKeySpecException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException{
		String cipherOfTripleDES = createEncryptor(padding(data)); // 3DES加密
		String cipherOfMD5 = hash(cipherOfTripleDES);
		return cipherOfMD5;
	}
	
	/**
	 * Base64格式解码
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static final byte[] dBase64(String data) throws IOException {
		BASE64Decoder dec = new BASE64Decoder();
		byte[] dnParm = dec.decodeBuffer(data);
		return dnParm;
	}

	/**
	 * Base64格式编码
	 * 
	 * @param data
	 * @return
	 */
	public static final String eBase64(byte[] data) {
		BASE64Encoder encoder = new BASE64Encoder();
		String base64 = encoder.encode(data);
		return base64;
	}

	/**
	 * 把为byte数组 转化为16进制的字符串
	 * 
	 * @param data
	 * @return
	 */
	private static final String byteToHex(byte data[]) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String s = Integer.toHexString(data[i] & 0xff);
			if (s.length() != 2)
				stringbuffer.append('0').append(s);
			else
				stringbuffer.append(s);
		}
		return new String(stringbuffer.toString().toUpperCase());
	}

	/**
	 * 指定的数据根据提供的密钥进行加密
	 * 
	 * @param data
	 * @return
	 * @throws InvalidKeyException 
	 * @throws UnsupportedEncodingException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	private static String createEncryptor(String data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] raw = cipher.doFinal(data.getBytes(UTF8_CHARSET));
		return eBase64(raw);
	}
 
	/**
	 * 对 Byte 数组进行解密
	 * 
	 * @param data 要解密的数据
	 * @return 返回加密后的 String
	 */
	private String createDecryptor(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] cipherByte = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			cipherByte = cipher.doFinal(data);
		} catch (java.security.InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (javax.crypto.BadPaddingException ex) {
			ex.printStackTrace();
		} catch (javax.crypto.IllegalBlockSizeException ex) {
			ex.printStackTrace();
		}
		return (new String(cipherByte, UTF8_CHARSET));
	}

	/**
	 * 16进制转换为字符串
	 * 
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static final String hexToString(String data) throws UnsupportedEncodingException {
		int intCounts = data.length() / 2;
		String strReturn = "";
		String strHex = ""; 
		int intHex = 0;
		byte byteData[] = new byte[intCounts];
		for (int intI = 0; intI < intCounts; intI++) {
			strHex = data.substring(0, 2);
			data = data.substring(2);
			intHex = Integer.parseInt(strHex, 16);
			if (intHex > 128)
				intHex = intHex - 256;
			byteData[intI] = (byte) intHex;
		}
		strReturn = new String(byteData, DEFAULT_CHARSET);
		return strReturn;
	}

	/**
	 * 字符串转换为16进制
	 * 
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static final String stringToHex(String data) throws UnsupportedEncodingException {
		byte byteData[] = null;
		int intHex = 0;
		String strHex = ""; 
		String strReturn = "";
		byteData = data.getBytes(DEFAULT_CHARSET);
		for (int intI = 0; intI < byteData.length; intI++) {
			intHex = (int) byteData[intI];
			if (intHex < 0)
				intHex += 256;
			if (intHex < 16)
				strHex += "0" + Integer.toHexString(intHex);
			else
				strHex += Integer.toHexString(intHex);
		}
		strReturn = strHex;
		return strReturn;
	}

	/**
	 * 将字符串的key转化为dBase64数组
	 * 
	 * @param key
	 * @param keyType
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	private void initKey(String key, int keyType) throws IOException, InvalidKeyException, InvalidKeySpecException {
		byte[] dKey = null;
		String localBase64Key = null;
		switch(keyType){
			case 0: // 通过参数一个24位字符串（由字母（大小写均可）和数字组成）
				localBase64Key = eBase64((keyHandler(key)).getBytes(DEFAULT_CHARSET));
				break;
			case 1: // 通过参数传入一个48位的16进制字符串（由字母（大小写均可）和数字组成）
				localBase64Key = eBase64((hexKeyHandler(key)).getBytes(DEFAULT_CHARSET));
				break;
			case 2: // 程序随机生成一个48位的16进制字符串
				localBase64Key = eBase64((hexKeyHandler(random4HexKey())).getBytes(DEFAULT_CHARSET));
				break;
			case 3: // 通过参数传入一个32位的base64编码格式的字符串（由字母（大小写均可）和数字组成）
			default:
				localBase64Key = key;
				break;
		}
		if(localBase64Key == null){
			localBase64Key = base64Key;
		}
		dKey = dBase64(localBase64Key.toUpperCase());
		try {
			deskey = new SecretKeySpec(dKey, Algorithm);
			cipher = Cipher.getInstance(DESEDE_ECB_NOPADDING);
		} catch (NoSuchPaddingException ex) {
			
		} catch (NoSuchAlgorithmException ex) {
			
		}
	}
	
	/**
	 * 处理key为24个字符的方法
	 * 
	 * @param key 一个24位字符串（由字母（大小写均可）和数字组成）
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static final String keyHandler(String key) throws UnsupportedEncodingException{
		return hexKeyHandler(stringToHex(key));
	} 
	
	/**
	 * 处理key为48个16进制字符的方法
	 *  
	 * @param key  一个48位的16进制字符串
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static final String hexKeyHandler(String key) throws UnsupportedEncodingException{
		return hexToString(key);
	} 
	
	/**
	 * 使用MD5加密算法对数据加密
	 * 
	 * @param data 待加密的数据
	 * @return 经过MD5加密后的密文
	 */
	public static String hash(String data) {
		String cipherText = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance(MD5);
			byte[] result = md5.digest(data.getBytes());
			cipherText = byteToHex(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	/**
	 * 填充规则(不足8个字符，差N位就填充N位N)
	 * 
	 * @param data 待填充的数据
	 * @return 按填充规则填充后的数据
	 */
	private static String padding(String data) {
		int length = data.length();
		int i = length % 8;
		if (i == 0) {
			return data;
		}
		int k = 8 - i;
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < k; j++) {
			sb.append(k);
		}
		//System.out.println("填充后的数据明文: " + data + sb.toString());
		return data + sb.toString();
	}
	
	/**
	 * 随机产生的48位16进制key
	 * 
	 * @return
	 */
	private String random4HexKey() {
		Random random = new Random();
		String s = "0";
		for (int i = 0; i < 6; i++) {
			s = s + Integer.toHexString(random.nextInt());
		}
		return s;
	}
	
}