/**
 * 
 */
package csii.dzip.test.testCUPS;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestCUPSToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class QueryDetail_test extends Abstract4TestCUPSToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0200");
		map.put("PAN", "9400276112339876545");
		map.put("PROC", "390000");
		map.put("AMT", "000000000000");
		map.put("POSC", "00");
//		map.put("POSC", "00");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		QueryDetail_test testCase = new QueryDetail_test();
		testCase.test();
	}

}
