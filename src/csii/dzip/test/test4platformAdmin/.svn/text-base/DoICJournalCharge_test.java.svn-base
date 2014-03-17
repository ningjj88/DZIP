/**
 *
 */
package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.test.Abstract4TestPlatformAdminToHost;


/**
 * IC卡批量记账测试
 * @author xiehai
 * @version 1.0.0
 * @date 2013-9-10 下午04:17:33
 */
public class DoICJournalCharge_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.DoICJournalCharge");
		map.put(Constants.POSTDATE, "20130825");
		return map;
	}

	/**
	 * IC卡批量记账
	 */
	public static void main(String[] args) throws Exception {
		DoICJournalCharge_test testCase = new DoICJournalCharge_test();
		testCase.test();
	}

}
