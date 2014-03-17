/**
 * 
 */
package csii.dzip.test.testCUPS;

import java.util.HashMap;
import java.util.Map;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.test.Abstract4TestCUPS8583ToHost;
import csii.test.Abstract4TestCUPSToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class WithdrawAtATM_test extends Abstract4TestCUPS8583ToHost {

	@SuppressWarnings("unchecked")
	protected Map prepare() {
		Map map=new HashMap();
		map = testMap.getMap();
		map.put(Constants.ISO8583_TRANSACTION_ID,"020001");                                  
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0200");
	    map.put(Constants.ISO8583_PRO_CODE,"010000");                                           /* 3# n6 */      
	    map.put(Constants.ISO8583_TRANAMT,"250000");                                          	/* 4# n12 */
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		WithdrawAtATM_test testCase = new WithdrawAtATM_test();
		testCase.test();
	}

}
