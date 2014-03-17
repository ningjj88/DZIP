/**
 * 
 */
package csii.dzip.test.testJincheng;

import java.util.HashMap;
import java.util.Map;

import csii.test.Abstract4TestJinChengToHost;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class DepositATCDM_test extends Abstract4TestJinChengToHost implements Runnable {
	private String strPan=null;
	public DepositATCDM_test(String strPan)
	{
		this.strPan=strPan;
	}
	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map = new HashMap();
		map = testMap.getMap();
		map.put("MESG", "0200");
		map.put("PAN", strPan);
		map.put("PROC", "210000");
		map.put("AMT", "00200000");
//		map.put("PIN", "");
		return map;
	}
	
	/**
	 * 柜面查询交易测试
	 */
	public static void main(String[] args) throws Exception {
		DepositATCDM_test testCase = new DepositATCDM_test("6221359010100080508");
		testCase.test();
	}
	public void run() {
		// TODO Auto-generated method stub
	//	DepositATCDM_test testCase = new DepositATCDM_test();
		try {
		//	testCase.test();
			Map map = prepare();
			// map.put("_TransactionTimestamp", Util.getCurrentTimestamp());
			this.send(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
