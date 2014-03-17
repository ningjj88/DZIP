package csii.dzip.test.test4ATM;

import java.util.HashMap;
import java.util.Map;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.test.Abstract4Test8583ToHost;

/**
 * ATM指定账户圈存冲正测试(本代本、本代他走的一个流程)
 * @author xiehai
 * @date 2013-12-12 上午10:58:26
 */
public class Credit4LoadReversal_test extends Abstract4Test8583ToHost {
	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();
	    map.put(Constants.ISO8583_TRANSACTION_ID,"042060");
	    map.put(Constants.ISO8583_HEAD_TX_TYPE, "0420");
	    //6221359010100149972本    6217993300000000015他
	    map.put(Constants.ISO8583_ACCTNO,"6221359010100149972");                                /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_PRO_CODE,"600000");                                     		/* 3# n6 */
	    map.put(Constants.ISO8583_TRANAMT,"100");                                               /* 4# n12 */
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));         /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM, Util.getDateString("HHmmss"));                   /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME, "161128");        /*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));          /*13# n4(MMDD) */
	    map.put(Constants.ISO8583_MERCHANTTYPE,"6011");                                         /*18# n4 */
	    map.put(Constants.ISO8583_SERENTRYMODE, "051");                                          /*22# n3 */
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
	    map.put(Constants.ISO8583_ICCSYSRELDATA, Util.HexToString("9F100807010199A0B806019F360201C2950500001800009F1E083230303331323331DF31050000000000"));
	    map.put(Constants.ISO8583_REACODE,"000005101000000000000002300");                                        /*60# LLL*/
	    map.put(Constants.ISO8583_ORGDATA, "020016112812131611280000557675000005576750");//消息类型(4)+系统跟踪号(6)+交易日期时间(10)+32#+33#(11不足补零)
	    map.put(Constants.ISO8583_RCVCODE, "00010000"); //100#
	    map.put(Constants.ISO8583_MESAUTHCD, "EEEEEEEE");   //128#
	    return map;
	}
	public static void main(String[] args) throws Exception {
		Credit4LoadReversal_test testCase = new Credit4LoadReversal_test();
		testCase.test();
	}

	public void run() {
		Credit4LoadReversal_test testCase = new Credit4LoadReversal_test();
		try {
			testCase.test();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
