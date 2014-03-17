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
public class ImportFtpFile_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.ImportFtpFile");
		map.put("PostDate","20110110");
	    map.put("AuditType","YLQZ");


		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		ImportFtpFile_test testCase = new ImportFtpFile_test();
		testCase.test();
	}

}
