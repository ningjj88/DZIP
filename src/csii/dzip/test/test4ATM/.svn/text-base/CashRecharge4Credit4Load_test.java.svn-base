package csii.dzip.test.test4ATM;

import java.util.HashMap;
import java.util.Map;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.test.Abstract4Test8583ToHost;

/**
 * ATM现金充值圈存测试(本代本、本代他走的一个流程)
 * @author xiehai
 * @date 2013-12-10 上午09:33:16
 */
public class CashRecharge4Credit4Load_test extends Abstract4Test8583ToHost implements Runnable{
	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();
	    map.put(Constants.ISO8583_TRANSACTION_ID,"020063");
	    map.put(Constants.ISO8583_HEAD_TX_TYPE, "0200");
	    //6221359010100149972本    6217993300000000015他
	    map.put(Constants.ISO8583_ACCTNO,"6212339010100009642");                                /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_PRO_CODE,"630000");                                     		/* 3# n6 */
	    map.put(Constants.ISO8583_TRANAMT,"55");                                               /* 4# n12 */
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));         /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM, Util.getDateString("HHmmss"));                   /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME,Util.getDateString(Constants.PE_HHMMSS));        /*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));          /*13# n4(MMDD) */
	    map.put(Constants.ISO8583_MERCHANTTYPE,"6011");                                         /*18# n4 */
	    map.put(Constants.ISO8583_SERENTRYMODE, "052");                                          /*22# n3 */
	    map.put(Constants.ISO8583_CARDSEQNBR, "001");     										//23#
	    map.put(Constants.ISO8583_SERCONDCODE, "91");                                          /*25# n2 */
	    map.put(Constants.ISO8583_SERCAPCODE, "06");                                           /*26# n6 */
	    map.put(Constants.ISO8583_ACQCODE,"05576760");                                       /*32# n..11 LLVAR*/
	    map.put(Constants.ISO8583_FORWARDCODE,"05576760");                                   /*33# n..11 LLVAR*/
	    map.put(Constants.ISO8583_RTVREFNUM,"055767011671");                                    /*37# n12*/
	    map.put(Constants.ISO8583_CARDACCID,"01010009");                                          /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCCODE,"000129000000000");                               /*42# ans15*/
	    map.put(Constants.ISO8583_CARDACCNAME,"_CHNGDGZSGRGBANKING_成都test");                 /*43# ans40*/
	    map.put(Constants.ISO8583_CURCODE,"156");                                               /*49# an3*/
	    map.put(Constants.ISO8583_PINDATA,"111111");                                          /*52# LLL*/
	    map.put(Constants.ISO8583_ICCSYSRELDATA, Util.HexToString("9F26080B61E36C79357C5A9F2701809F100807010199A0B806019F3704000000009F360201C2950500001800009A031312109C01609F02060000000000005F2A02015682027D009F1A0201569F03060000000000009F3303E0F0F09F34036003029F3501119F1E0832303033313233318405FFFFFFFFFF9F090220069F4104000000019F74064543433030319F631030313032303030308030303030303030"));
	    map.put(Constants.ISO8583_REACODE,"000005101000000000000002300");                                        /*60# LLL*/
	    map.put(Constants.ISO8583_RCVCODE, "00010000"); //100#
	    map.put(Constants.ISO8583_MESAUTHCD, "EEEEEEEE");   //128#
	    return map;
	}

	public static void main(String[] args) throws Exception {
		CashRecharge4Credit4Load_test testCase = new CashRecharge4Credit4Load_test();
		testCase.test();
	}

	public void run() {
		CashRecharge4Credit4Load_test testCase = new CashRecharge4Credit4Load_test();
		try {
			testCase.test();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
