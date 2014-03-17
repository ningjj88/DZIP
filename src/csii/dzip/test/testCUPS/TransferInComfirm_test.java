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
public class TransferInComfirm_test extends Abstract4TestCUPSToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0220");
		map.put("PAN", "9400276112339876545");
		map.put("PROC", "471000");
		map.put("AMT", "0100000");
		map.put("ORIG", "020020430612292043060000100675300001000000");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		TransferInComfirm_test testCase = new TransferInComfirm_test();
		testCase.test();
	}

}
