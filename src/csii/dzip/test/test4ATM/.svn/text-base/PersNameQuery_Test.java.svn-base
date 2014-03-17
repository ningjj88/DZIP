
package csii.dzip.test.test4ATM;

import java.util.HashMap;
import java.util.Map;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.test.Abstract4Test8583ToHost;

/**
 * @author xujin
 * @version 1.0.0
 * @since 2011-12-27
 */
public class PersNameQuery_Test extends Abstract4Test8583ToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0200");
		 // 8888880000438323 外行锦程卡
		 // 7777770000438323 外行其他卡
		 // 9999990000438323本行卡
		map.put(Constants.ISO8583_TRANSACTION_ID,"020041");                                     
	    map.put(Constants.ISO8583_ACCTNO,"6221359010100538232");                                /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_PRO_CODE,"410000");                                           /* 3# n6 */                                                          
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));         /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM,"784546");                                        /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME,Util.getDateString(Constants.PE_HHMMSS));        /*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));          /*13# n4(MMDD) */
	    map.put(Constants.ISO8583_SERENTRYMODE,"021");                                          /*22# n3 */
	    map.put(Constants.ISO8583_SERCAPCODE,"06");                                             /*26# n2 */
	    map.put(Constants.ISO8583_CARDACCID,"10010016");                                          /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCNAME,"_CHNGDGZSGRGBANKING自动柜员机");                  /*43# ans40*/
	    map.put(Constants.ISO8583_CURCODE,"156");                                               /*49# an3*/	
	    return map;
	}

	/**
	 * ATM查询交易测试
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		PersNameQuery_Test testCase = new PersNameQuery_Test();
		testCase.test();
	}

	public void run() {
		// TODO Auto-generated method stub
		PersNameQuery_Test testCase = new PersNameQuery_Test();
		try {
//			System.out.println("QueryTran_Test");
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
