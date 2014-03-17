/**
 * 
 */
package csii.dzip.test.test4platformAdmin.Audit;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * @author chenshaoqi
 * @version 1.0.0
 * @since 2010-8-16
 */
public class DownLoadFtpFile_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.DownLoadFtpFile");
		map.put("PostDate","20110110");
	    map.put("AuditType","YLQZ");


		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		DownLoadFtpFile_test testCase = new DownLoadFtpFile_test();
		testCase.test();
	}

}
