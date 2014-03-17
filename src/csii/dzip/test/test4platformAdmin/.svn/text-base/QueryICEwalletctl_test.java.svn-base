/**
 *
 */
package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.test.Abstract4TestPlatformAdminToHost;


/**
 * IC卡圈存圈提差错记账测试
 * @author xiehai
 * @version 1.0.0
 * @date 2013-9-10 下午04:17:33
 */
public class QueryICEwalletctl_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.QueryICEwalletctl");
		map.put("AcctNo", "6235881000000000000");
		return map;
	}

	/**
	 * 电子钱包余额查询
	 */
	public static void main(String[] args) throws Exception {
		QueryICEwalletctl_test testCase = new QueryICEwalletctl_test();
		testCase.test();
	}

}
