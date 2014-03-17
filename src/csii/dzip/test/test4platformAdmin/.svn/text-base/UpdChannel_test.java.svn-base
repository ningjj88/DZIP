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
public class UpdChannel_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.UpdChannel");
		map.put("ChannId", "005");
		map.put("ChannName", "银联前置");
		map.put("ChannStat", "0");

		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		UpdChannel_test testCase = new UpdChannel_test();
		testCase.test();
	}

}
