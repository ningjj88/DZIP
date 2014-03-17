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
public class PreAuthAdd_test extends Abstract4TestCUPSToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0100");
//		map.put("PAN", "9400276112339876545");
		map.put("PROC", "030000");
		map.put("AMT", "00050000");
		map.put("POSC", "60");
//		map.put("PIN", "");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		PreAuthAdd_test testCase = new PreAuthAdd_test();
		testCase.test();
	}
	public void run() {
		// TODO Auto-generated method stub
		PreAuthAdd_test testCase = new PreAuthAdd_test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
