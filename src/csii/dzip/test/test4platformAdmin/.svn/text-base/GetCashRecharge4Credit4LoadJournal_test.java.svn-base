package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * 测试柜面现金充值撤销前现金充值交易流水查询
 * @author xiehai
 * @date 2014-3-20 下午02:36:26
 */
public class GetCashRecharge4Credit4LoadJournal_test extends Abstract4TestPlatformAdminToHost{

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.GetCashRecharge4Credit4LoadJournal");
		map.put(Constants.PE_POST_DATE, "20131223");
		map.put(Constants.PE_ACC_NO, "6235881000000000018");
		map.put(Constants.PE_TRAN_AMT, "10");
//		map.put(Constants.PE_PERSNBR, "");
		return map;
	}

	public static void main(String[] args) throws Exception {
		GetCashRecharge4Credit4LoadJournal_test testCase = new GetCashRecharge4Credit4LoadJournal_test();
		testCase.test();
	}

}
