package csii.dzip.test.test4platformAdmin;

import java.util.HashMap;
import java.util.Map;

import csii.pe.service.comm.StringChange;
import csii.test.Abstract4TestPlatformAdminToHost;

public class ValidateICData_test extends Abstract4TestPlatformAdminToHost {

	@Override
	protected Map prepare() {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		map.put("_TransactionCode", "platformAdmin.ValidateICData");
		map.put("CardNo", "6235881000000000034");
		map.put("SeqNo", "000");
		map.put("ICData", "9F26088B7EBEB88658F94E9F2701809F101307020103A0B000010A010000000000969B3C5D9F3704000846049F360200179505008088E8009A031310299C01609F02060000000050005F2A02015682027C009F1A0201569F03060000000000009F3303E0F0C89F34030203019F3501229F1E083038354C30544B488408A0000003330101019F0902050E9F410400000741");
		return map;
	}
	
	public static void main(String[] args) throws Exception {
		ValidateICData_test testCase = new ValidateICData_test();
		testCase.test();
	}

}
