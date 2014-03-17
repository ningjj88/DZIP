/*
 * 
 */
package csii.dzip.action.util.ftp;
/**
 * ftp客戶端服务实现提供类
 * 
 * @author shengzt
 */
public class CFtpClient {

	public CFtpClient() {
	}

	public static IFtpClient getFtp4jClient() {
		return new CFtpClientUsing();
	}
}
