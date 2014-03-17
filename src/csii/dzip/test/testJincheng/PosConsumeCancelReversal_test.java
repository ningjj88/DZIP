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
public class PosConsumeCancelReversal_test extends Abstract4TestJinChengToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0420");
		map.put("PAN", "6221359010100080508");
		map.put("PROC", "200000");
		map.put("AMT", "000000000000");
		map.put("ORIG", "020014355612051435560000100675300001000000");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		PosConsumeCancelReversal_test testCase = new PosConsumeCancelReversal_test();
		testCase.test();
	}

}
