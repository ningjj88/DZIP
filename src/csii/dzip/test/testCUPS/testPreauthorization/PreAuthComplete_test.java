/**
 * 
 */
package csii.dzip.test.testCUPS.testPreauthorization;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.dzip.test.testCUPS.testMap;
import csii.test.Abstract4TestCUPS8583ToHost;
import csii.test.Abstract4TestCUPSToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class PreAuthComplete_test extends Abstract4TestCUPS8583ToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("HeadTxType", "0220");
		map.put("_TransactionId", "02200006");
		
//		map.put("PAN", "9400276112339876545");
		map.put("PROCODE", "000000");
		map.put("TRANAMT", "00020000");
		map.put("SERCONDCODE", "06");
		map.put("AUTRESCOD", "100272");
		map.put(Constants.ISO8583_MERCHANTTYPE,"7011");                                         /*18# n4 */
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		PreAuthComplete_test testCase = new PreAuthComplete_test();
		testCase.test();
	}
	public void run() {
		// TODO Auto-generated method stub
		PreAuthComplete_test testCase = new PreAuthComplete_test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
