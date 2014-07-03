package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * 柜面IC卡待补登金额查询
 * @author xiehai
 * @date 2014-5-5 下午03:03:55
 */
public class AdditionalAmtQuery_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.AdditionalAmtQuery");
		map.put("Card_No","6212331000000999438");
		return map;
	}

	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		AdditionalAmtQuery_test testCase = new AdditionalAmtQuery_test();
		testCase.test();
	}

}
