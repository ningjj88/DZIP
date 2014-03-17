/**
 * 
 */
package csii.dzip.test.testCUPS.testPreauthorization;

import java.util.HashMap;
import java.util.Map;

import csii.dzip.test.testCUPS.testMap;
import csii.test.Abstract4TestCUPSToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class PreAuthCancel_test extends Abstract4TestCUPSToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0100");
//		map.put("PAN", "9400276112339876545");
		map.put("PROC", "200000");
		map.put("AMT", "00050000");
		map.put("POSC", "06");
		map.put("AUTH", "115108");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		PreAuthCancel_test testCase = new PreAuthCancel_test();
		testCase.test();
	}
	public void run() {
		// TODO Auto-generated method stub
		PreAuthCancel_test testCase = new PreAuthCancel_test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
