package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.base.constant.Constants;
import csii.dzip.core.Dict;
import csii.test.Abstract4TestPlatformAdminToHost;

/**
 * IC卡销户登记测试
 * @author xiehai
 * @date 2014-2-21 下午03:18:08
 */
public class CloseAccountRegistration_test extends Abstract4TestPlatformAdminToHost {

	@Override
	@SuppressWarnings("unchecked")
	protected Map prepare() {
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.CloseAccountRegistration");
		map.put(Dict.CARD_NO, "6235881000000000018");
		map.put(Dict.TRXTYPE, Constants.IC_CLOSEACCOUNT_REGISTRATION);
		map.put(Dict.ACTCODE, "0");
		map.put(Dict.LGNTYPE, "1");
		map.put(Dict.SOURCE, Dict.SOURCE_BP);
		map.put(Dict.TRACENO, "444444");
		map.put(Dict.LGNINSTCODE, "05576751");
		map.put(Dict.LGNMERCHCODE, "123456");
		map.put(Dict.LGNUSERCODE, "654321");
		map.put(Dict.RTNWAY, "02");
		map.put(Dict.NAMEFLAG, "0");
		map.put(Dict.ID_TYPE, "0");
		map.put(Dict.ID_CODE, "513022197808226065");
		map.put(Dict.PINFLAG, "1");
		map.put(Dict.PIN, "111111");
		map.put(Dict.RTNFLG, "1");
		return map;
	}

	public static void main(String[] args) throws Exception {
		CloseAccountRegistration_test testCase = new CloseAccountRegistration_test();
		testCase.test();
	}

}
