package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * IC卡柜面会计科目查询
 * @author xiehai
 * @date 2014-6-17 下午02:26:55
 */
public class QueryIcChargeGlacct_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.QueryIcChargeGlacct");
		return map;
	}

	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		QueryIcChargeGlacct_test testCase = new QueryIcChargeGlacct_test();
		testCase.test();
	}

}
