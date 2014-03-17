package csii.dzip.test.test4ATM;

import java.util.HashMap;
import java.util.Map;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.test.Abstract4Test8583ToHost;

/**
 * 转账圈存冲正测试
 * @author xiehai
 * @date 2013-11-8 上午09:52:11
 */
public class Test_Transfer4CreditForLoadReversal extends Abstract4Test8583ToHost implements Runnable{

	@Override
	protected Map<String, Object> prepare() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.ISO8583_TRANSACTION_ID, "042062");
		map.put(Constants.ISO8583_HEAD_TX_TYPE, "0420");
		map.put(Constants.ISO8583_ACCTNO, "6217993300000000015");
		map.put(Constants.ISO8583_PRO_CODE, "620000");
		map.put(Constants.ISO8583_TRANAMT,"100000");
		map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));     /* 7# n10(MMDDhhmmss)*/
		map.put(Constants.ISO8583_SYSTRACENUM, Util.getDateString("HHmmss"));                /*11# n6 */
		map.put(Constants.ISO8583_LOCTRANSTIME, "163522");    /*12# n6(hhmmss) */
		map.put(Constants.ISO8583_LOCTRANSDATE, Util.getDateString(Constants.PE_MMDD));      /*13# n4(MMDD) */
		map.put(Constants.ISO8583_EXPDAT, "0000");
		map.put(Constants.ISO8583_MERCHANTTYPE, "6011");                                      /*18# n4 */
		map.put(Constants.ISO8583_SERENTRYMODE, "051");    //若卡1为磁条卡021                                    /*22# n3 */
		map.put(Constants.ISO8583_CARDSEQNBR, "001");     //若卡1为磁条卡则不需要23#
	    map.put(Constants.ISO8583_SERCONDCODE, "91");                                          /*25# n2 */
	    map.put(Constants.ISO8583_SERCAPCODE, "06");                                           /*26# n6 */
	    map.put(Constants.ISO8583_ACQCODE, "00005576750");                                       	/*32# n..11 LLVAR*/
	    map.put(Constants.ISO8583_FORWARDCODE, "00005576750");                                   	/*33# n..11 LLVAR*/
	    //若卡1磁条卡需35#和36#
	    //map.put(Constants.ISO8583_TRACK2_DATA, "6217993300000000015=49122205180600000");            /*35# z..37(LLVAR)*/
	    //map.put(Constants.ISO8583_TRACK3_DATA, "996217993300000000015=1561560000000000000003000000114000049121=000000000000=000000000000=000000051806000");            /*36# z..37(LLVAR)*/
	    map.put(Constants.ISO8583_RTVREFNUM, "055767011533");                                    /*37# n12*/
	    map.put(Constants.ISO8583_CARDACCID, "01010009");                                          /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCCODE, "123456789012345");                               /*42# ans15*/
	    map.put(Constants.ISO8583_CARDACCNAME, "CHN29000CHINA UNIONPAY SIMULATOR");                  /*43# ans40*/
	    map.put(Constants.ISO8583_ADDDATAPRI, "AA");
	    map.put(Constants.ISO8583_CURCODE, "156");                                               /*49# an3*/
	    map.put(Constants.ISO8583_PINDATA, "111111");                  						    /*52# b64*/
	    map.put(Constants.ISO8583_SECURCONTRINFO, "2600000000000000");                           /*53# n16*/
	    map.put(Constants.ISO8583_ICCSYSRELDATA, Util.HexToString("9F100807010199A0B806019F360201C2950500001800009F1E083230303331323331DF31050000000000"));
	    map.put(Constants.ISO8583_REACODE, "000005101000000000000002300"); //卡1为磁条卡000002000100000000000021000						//60#
	    map.put(Constants.ISO8583_ORGDATA, "020016352211081635220000557675000005576750");
	    map.put(Constants.ISO8583_SECURCONTRINFO, "05576750");
	    map.put(Constants.ISO8583_ACCIDE_N1, "6217993300000000015");                            /*102# LLL*/
	    map.put(Constants.ISO8583_ACCIDE_N2, "6221359010100149972");                            /*103# LLL*/
	    map.put(Constants.ISO8583_MESAUTHCD, "EEEEEEEE");
		return map;
	}

	@Override
	public void run() {
		ComfirmDepositForOther_Test testCase = new ComfirmDepositForOther_Test();
		try {
			testCase.test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception{
		Test_Transfer4CreditForLoadReversal testCase = new Test_Transfer4CreditForLoadReversal();
		testCase.test();
//		System.out.println(("9F2608A894BA53BF2A0B3C9F2701809F100807010199A0B806019F3704000000009F360201C2950500001800009A031311079C01609F02060000000000005F2A02015682027D009F1A0201569F03060000000000009F3303E0F0F09F34036003029F3501119F1E0832303033313233318405FFFFFFFFFF9F090220069F4104000000019F74064543433030319F631030313032303030308030303030303030"));
	}
}
