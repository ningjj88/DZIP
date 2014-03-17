/**
 * 
 */
package csii.dzip.test.testCUPS;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.test.Abstract4TestCUPS8583ToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class PosConsume_test extends Abstract4TestCUPS8583ToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put(Constants.ISO8583_TRANSACTION_ID,"020000");                                  
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0200");
	    map.put(Constants.ISO8583_PRO_CODE,"010000");                                           /* 3# n6 */      
	    map.put(Constants.ISO8583_TRANAMT,"1000");                                          	/* 4# n12 */
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		PosConsume_test testCase = new PosConsume_test();
		testCase.test();
//		BasicDataSource aa=new BasicDataSource();
//		System.out.print(aa.getMaxActive());
	}

}
