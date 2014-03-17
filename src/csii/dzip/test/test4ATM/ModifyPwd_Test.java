/**
 * 
 */
package csii.dzip.test.test4ATM;

import java.util.HashMap;
import java.util.Map;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.test.Abstract4Test8583ToHost;

/**
 *ATM修改密码交易测试
 * @author chenshq
 * @version 1.0.0
 * @since 2010-7-9
 */

public class ModifyPwd_Test extends Abstract4Test8583ToHost  implements Runnable  {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();
	    map.put(Constants.ISO8583_TRANSACTION_ID,"010070");                                   /* 3# n6 */
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0100");
	    map.put(Constants.ISO8583_ACCTNO,"9400279999999999998");                                 /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_PRO_CODE,"700000");                                         /* 3# n6 */
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));       /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM,"784546");                                      /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME,Util.getDateString(Constants.PE_HHMMSS));      /*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));        /*13# n4(MMDD) */
	     map.put(Constants.ISO8583_MERCHANTTYPE,"6011");                                      /*18# n4 */
	    map.put(Constants.ISO8583_SERENTRYMODE,"021");                                        /*22# n3 */
	    map.put(Constants.ISO8583_SERCONDCODE,"02");                                          /*25# n2 */
	    map.put(Constants.ISO8583_SERCAPCODE,"06");                                           /*26# n6 */
	    map.put(Constants.ISO8583_CARDACCID,"07010006");                                      /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCNAME,"_CHNGDGZSGRGBANKING_");                        /*43# ans40*/
	    map.put(Constants.ISO8583_ADDDATAPRI,"000000");                                       /*48# LLL*/
	    map.put(Constants.ISO8583_PINDATA,"222222");                                          /*52# LLL*/

		return map;
	}

	/**
	 * ATM修改密码交易测试
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ModifyPwd_Test testCase = new ModifyPwd_Test();
		testCase.test();
	}

	public void run() {
		// TODO Auto-generated method stub
		ModifyPwd_Test testCase = new ModifyPwd_Test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
