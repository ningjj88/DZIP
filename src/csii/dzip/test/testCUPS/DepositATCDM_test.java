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
public class DepositATCDM_test extends Abstract4TestCUPSToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0200");
//		map.put("PAN", "9400276112339876545");
		map.put("PROC", "210000");
		map.put("AMT", "001000");
//		map.put("PIN", "");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		DepositATCDM_test testCase = new DepositATCDM_test();
		testCase.test();
	}
	public void run() {
		// TODO Auto-generated method stub
		DepositATCDM_test testCase = new DepositATCDM_test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
