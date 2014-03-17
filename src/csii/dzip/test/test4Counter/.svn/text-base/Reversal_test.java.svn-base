/**
 * 
 */
package csii.dzip.test.test4Counter;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.test.Abstract4TestCounterXMLToHost;

/**
 * @author chenshaoqi
 * @version 1.0.0
 * @since 2010-8-16
 */
public class Reversal_test extends Abstract4TestCounterXMLToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put(Constants.ISO8583_TRANSACTION_ID,"019707");  
		map.put("MESG", "0420");
		map.put("PAN", "9400276112339876545");
		map.put("PROC", "010000");
		map.put("FEE", "C00000400");
		map.put("AMT", "10000");
		map.put("ORIG", "020015233611181523360000100675300001000000");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		Reversal_test testCase = new Reversal_test();
		testCase.test();
	}

}
