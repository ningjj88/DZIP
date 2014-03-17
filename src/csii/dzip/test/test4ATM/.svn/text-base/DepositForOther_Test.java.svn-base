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
 *存款交易的测试类
 * @author chenshq
 * @version 1.0.0
 * @since 2010-7-9
 */
public class DepositForOther_Test extends Abstract4Test8583ToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();
		 
		map.put(Constants.ISO8583_TRANSACTION_ID,"020021");                                 /* 3# n6 */
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0200");
	    map.put(Constants.ISO8583_ACCTNO,"8888880000438323");                               /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_PRO_CODE,"210000");                                       /* 3# n6 */       
	    map.put(Constants.ISO8583_TRANAMT,"10000");                                          	/* 4# n12 */
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));     /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM,Util.getDateString("HHmmss"));                 /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME,"113509");   								 /*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));      /*13# n4(MMDD) */
	    map.put(Constants.ISO8583_SETDATE,"0820");                                          /*15# s4 */
	    map.put(Constants.ISO8583_MERCHANTTYPE,"6011");                                     /*18# n4 */
	    map.put(Constants.ISO8583_SERENTRYMODE,"021");                                      /*22# n3 */
	    map.put(Constants.ISO8583_SERCONDCODE,"00");                                        /*25# n2 */
	    map.put(Constants.ISO8583_ACQCODE,"00042210");                                      /*32# n..11 LLVAR*/
	    map.put(Constants.ISO8583_FORWARDCODE,"00012210");                                  /*33# n..11 LLVAR*/
	    map.put(Constants.ISO8583_TRACK2_DATA,"9400276112339876545=20100630010101");           /*35# z..37(LLVAR)*/
	    map.put(Constants.ISO8583_TRACK3_DATA,"94386160000080034=1561560000000000000003976999236000002070000000000000000000000=000000000000=0000000345");            /*36# z..37(LLVAR)*/
	    map.put(Constants.ISO8583_RTVREFNUM,"002440818168");                                /*37# n12*/
	    map.put(Constants.ISO8583_CARDACCID,"002001");                                      /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCCODE,"000129000000000");                           /*42# ans15*/
	    map.put(Constants.ISO8583_CARDACCNAME,"_CHNGDGZSGRGBANKING_");                      /*43# ans40*/
	    map.put(Constants.ISO8583_CURCODE,"156");                                           /*49# an3*/
	    map.put(Constants.ISO8583_REACODE,"1234567890");                                    /*60# LLL*/
		return map;
	}

	/**
	 * ATM存款交易测试
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		DepositForOther_Test testCase = new DepositForOther_Test();
		testCase.test();
	}

	public void run() {
		// TODO Auto-generated method stub
		DepositForOther_Test testCase = new DepositForOther_Test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
