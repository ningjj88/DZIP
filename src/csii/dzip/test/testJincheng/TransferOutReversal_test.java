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
public class TransferOutReversal_test extends Abstract4TestJinChengToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0420");
		map.put("PAN", "9400276112339876545");
		map.put("PROC", "461000");
		map.put("AMT", "000100000");
		map.put("ORIG", "020009123012300912300000100675300001000000");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		TransferOutReversal_test testCase = new TransferOutReversal_test();
		testCase.test();
	}

}
