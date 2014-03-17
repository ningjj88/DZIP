/**
 *
 */
package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.dzip.core.InitData4Dzip;
import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * IC卡单笔记账测试
 * @author xiehai
 * @version 1.0.0
 * @date 2013-9-11 下午03:23:11
 */
public class DoSingleICJournalCharge_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.DoSingleICJournalCharge");
		map.put(Constants.IC_ACQ_INST_CODE, "05576750");
		map.put(Constants.IC_TRACE_NO, "033011");
		map.put(Constants.IC_TRANS_DATE, "0903");
		return map;
	}

	/**
	 * IC卡单笔记账
	 */
	public static void main(String[] args) throws Exception {
		DoSingleICJournalCharge_test testCase = new DoSingleICJournalCharge_test();
		testCase.test();
	}

}
