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
public class PosPurchaseReturn_test extends Abstract4TestJinChengToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0220");
		map.put("PAN", "6221359010100080508");
		map.put("PROC", "200000");
		map.put("AMT", "001000");
		map.put("ORIG", "020014373712051437370000100675300001000000");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		PosPurchaseReturn_test testCase = new PosPurchaseReturn_test();
		testCase.test();
//		BasicDataSource aa=new BasicDataSource();
//		System.out.print(aa.getMaxActive());
	}

}
