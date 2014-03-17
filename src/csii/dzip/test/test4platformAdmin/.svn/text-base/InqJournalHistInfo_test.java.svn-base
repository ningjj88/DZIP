/**
 * 
 */
package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * @author chenshaoqi
 * @version 1.0.0
 * @since 2010-8-16
 */
public class InqJournalHistInfo_test extends Abstract4TestPlatformAdminToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.InqJournalHistInfo");
		map.put("SysSeqNo", Integer.parseInt("103375"));
		map.put("TranDate_Start", "20100901");
		map.put("TranDate_End", "20100902");
		map.put("ChannId", "002");
		map.put("TranStat", "0");
		map.put("Credit_Debit", "C");
		map.put("TranAMT_Start", "265");
		map.put("TranAMT_End", "268");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		InqJournalHistInfo_test testCase = new InqJournalHistInfo_test();
		testCase.test();
	}

}
