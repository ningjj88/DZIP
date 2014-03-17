package csii.dzip.action.util;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.dzip.action.util.secapipackage.SecApi;
/**
 * 用于ATMP传入密码的加密解密功能
 * @author xujin
 * @version 1.0.0
 * @since 2011-05-06
 */
public class DecryptSecApi implements InitializingBean{

	/**
	 * @param args
	 */
	protected final Log log = LogFactory.getLog(getClass());
	private int bankpindex;
	private byte piklen;
	private int keylen;
	private int pwdlen;
	private byte pinforemat;
	private String bankpinkey;
	private String pinkey;
	private String mackey;
	private SecApi  secApi;
	public DecryptSecApi(){
		super();
	}
	
	/**
	 * 用于ATMP传入密码的解密功能
     * @param Pin 密文
	 * @param Pan 卡号
	 * @return 明文
	 * @throws PeException 
	 */
	public String HSMAPIDecryptPIN(String Pin,String Pan) throws PeException 
	{
		this.secApi.getHsmConf();
		byte[] mac = Util.StringToHex(Pin).getBytes();
		byte[] strPan = Pan.getBytes();
		byte[] cryptPin = new byte[16];
		byte[] iPinLen = new byte[1];
		int ret=0;
		try
		{
			ret=secApi.HSMAPIDecryptPIN(keylen, pinkey.getBytes(), pinforemat, mac, strPan, iPinLen, cryptPin);
		}
		catch (IOException e)
        {
			log.error("DecryptSecApi=============>调用加密机解密出错");
        }
		if(ret==0)
		    return  new String(cryptPin).substring(0, 6);
		else
			return null;
	}
	
	/**
	 * 用于ATMP传入密码的加密功能
     * @param Pin 明文
	 * @param Pan 卡号
	 * @return 密文
	 */
	public String HSMAPIEncryptPIN(String Pin,String Pan) throws PeException
	{
		
		this.secApi.getHsmConf();
		byte[] mac = Pin.getBytes();
		byte[] strPan = Pan.getBytes();
		byte[] cryptPin = new byte[16];
		int ret=0;
		try
		{
			ret=secApi.HSMAPIEncryptPIN(keylen, pinkey.getBytes(), pinforemat,pwdlen, mac, strPan, cryptPin);
		}
		catch (IOException e)
        {
			log.error("DecryptSecApi=============>调用加密机加密出错");
        }
		if(ret==0)
		    return new String(cryptPin);
		else
			return null;
	}
	
	/**
	 * 用于ATMP传入密码的带索引解密功能
     * @param Pin 密文
	 * @param Pan 卡号
	 * @return 明文
	 */
	public String sec_decryptPINBlock(String Pin,String Pan)throws PeException
	{
		this.secApi.getHsmConf();
		byte[] mac = Util.StringToHex(Pin).getBytes();
		byte[] strPan = Pan.getBytes();
		byte[] cryptPin = new byte[16];
		int ret=0;
		try
		{
			ret=secApi.sec_decryptPINBlock(bankpindex, piklen, bankpinkey.getBytes(), mac, strPan, cryptPin);
		}
		catch (IOException e)
        {
            e.printStackTrace();
            throw new PeException("系统错误");
        }
		if(ret==0)
	    	return  new String(cryptPin).substring(0, 6);
		else
			return null;
	}
	
	/**
	 * 用于ATMP传入密码的带索引加密功能
     * @param Pin 明文
	 * @param Pan 卡号
	 * @return 密文
	 */
	public String sec_encrypPINBlock(String Pin,String Pan) throws PeException
	{
		//SecApi secApi = new SecApi();
		this.secApi.getHsmConf();
		byte[] mac = Pin.getBytes();
		byte[] strPan = Pan.getBytes();
		byte[] cryptPin = new byte[16];
		int ret=0;
		try
		{
			ret=secApi.sec_encrypPINBlock(bankpindex, piklen, bankpinkey.getBytes(), mac,pinforemat, strPan, cryptPin);
		}
		catch (IOException e)
        {
            e.printStackTrace();
            throw new PeException("系统错误");
        }
		if(ret==0)
			return  new String(cryptPin);
		else
			return null;
	}

	public void afterPropertiesSet() throws Exception {
		
	}
	public void setBankpindex(int bankpindex) {
		this.bankpindex = bankpindex;
	}
	public int getBankpindex() {
		return this.bankpindex ;
	}
	public void setPiklen(byte piklen) {
		this.piklen = piklen;
	}
	public byte getPiklen() {
		return this.piklen ;
	}
	public void setKeylen(int keylen) {
		this.keylen = keylen;
	}
	public int getKeylen() {
		return this.keylen ;
	}
	
	public void setPwdlen(int pwdlen) {
		this.pwdlen = pwdlen;
	}
	public int getPwdlen() {
		return this.pwdlen ;
	}
	public void setPinforemat(byte pinforemat) {
		this.pinforemat = pinforemat;
	}
	public byte getPinforemat() {
		return this.pinforemat ;
	}
	public void setMackey(String mackey) {
		this.mackey = mackey;
	}
	public String getMackey() {
		return this.mackey ;
	}
	public void setBankpinkey(String bankpinkey) {
		this.bankpinkey = bankpinkey;
	}
	public String getBankpinkey() {
		return this.bankpinkey ;
	}
	public void setPinkey(String pinkey) {
		this.pinkey = pinkey;
	}
	public String getPinkey() {
		return this.pinkey ;
	}
	public void setSecApi(SecApi bankpinkey) {
		this.secApi = bankpinkey;
	}
	public SecApi getSecApi() {
		return this.secApi ;
	}
}
