package csii.dzip.core;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

/**
 * 
 */

/**
 * @author Zhang yongqing
 * 
 */
public class JNative4CapDes {
	
	static String passwd = "111111";
	static String work_key = "94002710";
	static String cardno = "6212339010100000013";

	static {
		System.setProperty("jnative.debug", "true");
		System.setProperty("jnative.loadNative", "/usr/lib/libJNativeCpp.so");
	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) throws Exception, NativeException, IllegalAccessException {
//		JNative4CapDes jnative4CapDes = new JNative4CapDes();
//		String reslut = jnative4CapDes.getCap_enc_pin(work_key, passwd, cardno);
//		String pwd = jnative4CapDes.getCap_dis_pin(work_key, reslut, cardno);
//		String reslut2 = jnative4CapDes.getCap_enc_pin(work_key, pwd, cardno);
//		String pwd2 = jnative4CapDes.getCap_dis_pin(work_key, reslut2, cardno);
//	}


	/**
	   * 加密
	   * 
	 * @param work_key 密码加密密钥(联名行编号)
	 * @param passwd 密码明文
	 * @param cardno 卡号
	 * @return result 密码密文
	 * @throws NativeException
	 * @throws IllegalAccessException
	 */
	public static String getCap_enc_pin(String work_key, String passwd, String cardno) throws NativeException, IllegalAccessException {

		Pointer result = new Pointer(MemoryBlockFactory.createMemoryBlock(16));

		JNative jnative_enc = new JNative("cd_cap_des.so", "cap_enc_pin");

		jnative_enc.setParameter(0, work_key);
		jnative_enc.setParameter(1, passwd);
		jnative_enc.setParameter(2, cardno);
		jnative_enc.setParameter(3, result);
		jnative_enc.invoke();
		return result.getAsString();
	}
	
	
	/**
	   * 解密
	   * 
	 * @param work_key 密码解密密钥(联名行编号)
	 * @param result 密码密文
	 * @param cardno 卡号
	 * @return passwd 密码明文
	 * @throws NativeException
	 * @throws IllegalAccessException
	 */
	public static String getCap_dis_pin(String work_key, String result, String cardno) throws NativeException, IllegalAccessException {
		
		Pointer pwd = new Pointer(MemoryBlockFactory.createMemoryBlock(6));

		JNative jnative_dis = new JNative("cd_cap_des.so", "cap_dis_pin");
		jnative_dis.setParameter(0, work_key);
		jnative_dis.setParameter(1, result);
		jnative_dis.setParameter(2, cardno);
		jnative_dis.setParameter(3, pwd);
		jnative_dis.invoke();
	
		return pwd.getAsString();
	}

}
