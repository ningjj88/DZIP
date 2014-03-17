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
 * ATM转账交易测试
 *
 * @author chenshq
 * @version 1.0.0
 * @since 2010-7-9
 */
public class Transfer_Test extends Abstract4Test8583ToHost  implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();

	    map.put(Constants.ISO8583_TRANSACTION_ID,"020040");
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0200");
	    map.put(Constants.ISO8583_ACCTNO,"123456");                               /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_PRO_CODE,"400000");                                            /* 3# n6 */
	    map.put(Constants.ISO8583_TRANAMT,"20000000");                                           /* 4# n12 */
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));     /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM,"784546");                                    /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME,Util.getDateString(Constants.PE_HHMMSS));    /*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));      /*13# n4(MMDD) */
	    map.put(Constants.ISO8583_SETDATE,"0820");                                          /*15# s4 */
	    map.put(Constants.ISO8583_MERCHANTTYPE,"6011");                                     /*18# n4 */
	    map.put(Constants.ISO8583_SERENTRYMODE,"050");                                      /*22# n3 */
	    map.put(Constants.ISO8583_SERCONDCODE,"02");                                        /*25# n2 */
	    map.put(Constants.ISO8583_TRANSFEE,"000");                                          /*28# n3 */
	    map.put(Constants.ISO8583_ACQCODE,"00000042210");                                   /*32# n..11 LLVAR*/
	    map.put(Constants.ISO8583_FORWARDCODE,"00000012210");                               /*33# n..11 LLVAR*/
	    map.put(Constants.ISO8583_TRACK2_DATA,"6221359010100538232=000020142207951=");           /*35# z..37(LLVAR)*/
	    map.put(Constants.ISO8583_RTVREFNUM,"002440818168");                                /*37# n12*/
	    map.put(Constants.ISO8583_CARDACCID,"10010016");                                      /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCCODE,"000129000000000");                           /*42# ans15*/
	    map.put(Constants.ISO8583_CARDACCNAME,"_CHNGDGZSGRGBANKING_");                      /*43# ans40*/
	    map.put(Constants.ISO8583_CURCODE,"156");                                           /*49# an3*/
	    map.put(Constants.ISO8583_PINDATA,"111111");              							/*52# b64*/
	    map.put(Constants.ISO8583_SECURCONTRINFO,"2000000000000000");                       /*53# n16*/
	    map.put(Constants.ISO8583_REACODE,"1234567890");                                    /*60# LLL*/
	    map.put(Constants.ISO8583_ACCIDE_N1,"123456");                            /*102# LLL*/
	    map.put(Constants.ISO8583_ACCIDE_N2,"6221359010100080086");                            /*103# LLL*/
		return map;
	}

	/**
	 * ATM转账交易测试
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Transfer_Test testCase = new Transfer_Test();
		testCase.test();
	}

	public void run() {
		// TODO Auto-generated method stub
		Transfer_Test testCase = new Transfer_Test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
