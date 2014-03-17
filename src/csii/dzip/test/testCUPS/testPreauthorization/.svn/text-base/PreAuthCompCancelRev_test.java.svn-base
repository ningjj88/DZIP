/**
 * 
 */
package csii.dzip.test.testCUPS.testPreauthorization;

import java.util.HashMap;
import java.util.Map;

import csii.dzip.test.testCUPS.testMap;
import csii.test.Abstract4TestCUPS8583ToHost;
import csii.test.Abstract4TestCUPSToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class PreAuthCompCancelRev_test extends Abstract4TestCUPS8583ToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0420");
//		map.put("PAN", "9400276112339876545");
		map.put("PROC", "200000");
		map.put("AMT", "00050000");
		map.put("POSC", "06");
		map.put("AUTH", "461");
		map.put("ORIG", "020011195501221119550000100675300001000000");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		PreAuthCompCancelRev_test testCase = new PreAuthCompCancelRev_test();
		testCase.test();
	}
	public void run() {
		// TODO Auto-generated method stub
		PreAuthCompCancelRev_test testCase = new PreAuthCompCancelRev_test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
