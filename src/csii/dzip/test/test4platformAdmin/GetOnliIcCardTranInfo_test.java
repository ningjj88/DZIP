/**
 *
 */
package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestPlatformAdminToHost;


/**
 * 柜面发起电子现金交易前需要获得数据<BR>
 * 测试
 * @author xiehai
 * @date 2014-2-26 上午10:48:32
 */
public class GetOnliIcCardTranInfo_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.GetOnliIcCardTranInfo");
		return map;
	}

	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		GetOnliIcCardTranInfo_test testCase = new GetOnliIcCardTranInfo_test();
		testCase.test();
	}

}
