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
public class ReversaForOther_Test extends Abstract4Test8583ToHost implements Runnable {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();
		map.put(Constants.ISO8583_TRANSACTION_ID,"042001");
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0420");
	    map.put(Constants.ISO8583_ACCTNO,"6217993300000000015");                                   /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_PRO_CODE,"010000");                                           /* 3# n6 */
	    map.put(Constants.ISO8583_TRANAMT,"2000000");                                          	/* 4# n12 */
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));         /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM, Util.getDateString("mmss")+"01");                   /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME,"162515");        								/*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));          /*13# n4(MMDD) */
	    map.put(Constants.ISO8583_SETDATE,"0915");          									/*15# n4(MMDD) */
	    map.put(Constants.ISO8583_MERCHANTTYPE,"6011");                                         /*18# n4 */
	    map.put(Constants.ISO8583_SERENTRYMODE,"021");                                          /*22# n3 */
	    map.put(Constants.ISO8583_SERCONDCODE,"02");                                            /*25# n2 */
	    map.put(Constants.ISO8583_ACQCODE,"05576750");                                       	/*32# n..11 LLVAR*/
	    map.put(Constants.ISO8583_FORWARDCODE,"05576750");                                   	/*33# n..11 LLVAR*/
	    map.put(Constants.ISO8583_RTVREFNUM,"002440818168");                                    /*37# n12*/
	    map.put(Constants.ISO8583_AUTRESCOD,"000000");                                   		/*38# n6*/
	    map.put(Constants.ISO8583_CARDACCID,"10010016");                                        /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCCODE,"000129000000000");                               /*42# ans15*/
	    map.put(Constants.ISO8583_CARDACCNAME,"_CHNGDGZSGRGBANKING自动柜员机");                  /*43# ans40*/
	    map.put(Constants.ISO8583_CURCODE,"156");                                               /*49# an3*/
	    map.put(Constants.ISO8583_REACODE,"027000002000100000000000021000");                          		/*60# n16*/
	    map.put(Constants.ISO8583_ORGDATA,"020025150903181625150000557675000005576750");        /*90# LLL Original Message Type+11#+7#+32#(右对齐，前补0)+33#(右对齐，前补0)*/
	    map.put(Constants.ISO8583_RCVCODE,"00001000");                           				/*100# n16*/
	    map.put(Constants.ISO8583_NANSCRESERVE,"01C002100000000000000000000000");          	    /*121# n16*/
	    map.put(Constants.ISO8583_MESAUTHCD,"EEEEEEEE");                          				/*128# n16*/
	  		return map;
	}

	/**
	 * ATM存款交易测试
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ReversaForOther_Test testCase = new ReversaForOther_Test();
		testCase.test();
	}

	public void run() {
		// TODO Auto-generated method stub
		ReversaForOther_Test testCase = new ReversaForOther_Test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
