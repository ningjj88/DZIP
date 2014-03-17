package csii.dzip.action.util;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import csii.dzip.core.Encrypt4Des3;


/**
 * 用于验证用户输入的密码是否和数据库中的密码是否相同
 * @author chenshq
 * @version 1.0.0
 * @since 2010-12-13
 */
public class PassWordValidate {
	private JdbcAccessAwareProcessor corebankdb;
	private Encrypt4Des3 encrypt;
	
/**
 * 密码验证
 * @return 验证结果
 */
	public boolean validate(String passWord){
		return false;
	}

	public Encrypt4Des3 getEncrypt() {
		return encrypt;
	}
	public void setEncrypt(Encrypt4Des3 encrypt) {
		this.encrypt = encrypt;
	}
	public JdbcAccessAwareProcessor getCorebankdb() {
		return corebankdb;
	}
 void setCorebankdb(JdbcAccessAwareProcessor corebankdb) {
		this.corebankdb = corebankdb;
	}

}
