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
public class Query_test extends Abstract4TestCounterXMLToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put(Constants.ISO8583_TRANSACTION_ID,"019704");  
		map.put("MESG", "0200");
		map.put("PAN", "9400276112339876545");
		map.put("PROC", "300000");
		map.put("FEE", "C00000400");

		map.put("_TrmCode", "6210");
		map.put("_TrmSeqDate", "20100629");
		map.put("_HostDate", "20100629");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		Query_test testCase = new Query_test();
		testCase.test();
	}

}
