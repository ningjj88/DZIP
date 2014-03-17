/**
 * 
 */
package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * @author chenshaoqi
 * @version 1.0.0
 * @since 2010-8-16
 */
public class UpdChannelTran_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.UpdChannelTran");
		map.put("ChannId", "123456");
		map.put("TranCd", "6546");
		map.put("TranDesc", "this is a test");
		map.put("TranStat", "1");
		

		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		UpdChannelTran_test testCase = new UpdChannelTran_test();
		testCase.test();
	}

}
