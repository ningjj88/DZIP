package csii.dzip.core;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encrypt4Des3 {

	private static final String Algorithm = "DESede"; 
//	private static final String Transformation_ECB = "desede/ECB/PKCS5Padding";
	private static final String Transformation_ECB = "DESede";
	private static final String Transformation_CBC = "DESede/CBC/PKCS5Padding";
	private static final String MD5 = "MD5";
	
    /**
     * ECB加密,不要IV
     * @param key 密钥
     * @param data 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeECB(byte[] key, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(Algorithm);
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(Transformation_ECB);
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }
    /**
     * ECB解密,不要IV
     * @param key 密钥
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] des3DecodeECB(byte[] key, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(Algorithm);
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(Transformation_ECB);
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }
    /**
     * CBC加密
     * @param key 密钥
     * @param keyiv IV
     * @param data 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(Algorithm);
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance(Transformation_CBC);
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);

        return bOut;
    }

    /**
     * CBC解密
     * @param key 密钥
     * @param keyiv IV
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {

        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(Algorithm);
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(Transformation_CBC);
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }
    
	/**
	 * 
	 * 获得MD5加密密码的方法
	 */

	public static String getMD5ofStr(String origString) {
		String origMD5 = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance(MD5);
			byte[] result = md5.digest(origString.getBytes());
			origMD5 = byteToHex(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return origMD5;
	}
	/**
	 * 字节数组转化为16进制的String
	 * @param abyte0
	 * @return
	 */
	public static String byteToHex(byte abyte0[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < abyte0.length; i++)
        {
            String s = Integer.toHexString(abyte0[i] & 0xff);
            if(s.length() != 2)
                stringbuffer.append('0').append(s);
            else
                stringbuffer.append(s);
        }
        return stringbuffer.toString().toUpperCase();
    }
	
	/**
	 * 获得加密的keydata
	 * @return keydata
	 */
	public static String getKeyData(){
		return null;
	}
	
	public static void main(String[] args)  throws Exception {
		String key = "A3F2569DESJEIWBCJOTY45DYQWF68H1Y";
//        byte[] key=new BASE64Decoder().decodeBuffer("30303030303030303030303030303030");
        byte[] keyData=key.getBytes();
        byte[] keyiv = { 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[] data="a".getBytes("UTF-8");
        System.out.println("ECB加密解密");
        byte[] str3 = des3EncodeECB(keyData,data );
        byte[] str4 = des3DecodeECB(keyData, str3);
        System.out.println(new BASE64Encoder().encode(str3));
        System.out.println(getMD5ofStr(new String(str3)));
        System.out.println(new String(str4, "UTF-8"));
        System.out.println();
        System.out.println("CBC加密解密");
        byte[] str5 = des3EncodeCBC(keyData, keyiv, data);
        byte[] str6 = des3DecodeCBC(keyData, keyiv, str5);
        System.out.println(new BASE64Encoder().encode(str5));
        System.out.println(byteToHex(str5));
        System.out.println(new String(str6, "UTF-8"));
	}
}
