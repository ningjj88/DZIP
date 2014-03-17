/**
 * 
 */
package csii.dzip.test.testJincheng;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestJinChengToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class ReversalAtATM_test extends Abstract4TestJinChengToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0420");
		map.put("PAN", "9400276112339876545");
		map.put("PROC", "010000");
		map.put("AMT", "00200000");
		map.put("ORIG", "020009151001070915100000100675300001000000");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		ReversalAtATM_test testCase = new ReversalAtATM_test();
		testCase.test();
	}

}
