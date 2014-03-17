/**
 *
 */
package csii.dzip.test.test4Onli;

import java.util.HashMap;
import java.util.Map;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.test.Abstract4TestOnliToHost;

/**
 * ATM非指定账户转账圈存冲正
 * @author xiehai
 * @date 2013-12-19 下午01:03:32
 */
public class Transfer4Credit4LoadReversal_Test extends Abstract4TestOnliToHost {

	@SuppressWarnings("unchecked")
	@Override
	protected Map prepare() {
		Map map=new HashMap();
	    map.put("_TransactionCode", "onli.TransferCredit4LoadReversal");
	    map.put(Constants.ISO8583_ACCTNO,"6221359010100149972");                               /* 2#  n..19 LLVAR */
	    map.put(Constants.ISO8583_TRANAMT,"300");                                           /* 4# n12 */
	    map.put(Constants.ISO8583_TRANSDTTIME, Util.getDateString(Constants.PE_MDHMS));     /* 7# n10(MMDDhhmmss)*/
	    map.put(Constants.ISO8583_SYSTRACENUM, "000007");                                    /*11# n6 */
	    map.put(Constants.ISO8583_LOCTRANSTIME,Util.getDateString(Constants.PE_HHMMSS));    /*12# n6(hhmmss) */
	    map.put(Constants.ISO8583_LOCTRANSDATE,Util.getDateString(Constants.PE_MMDD));      /*13# n4(MMDD) */
	    map.put(Constants.ISO8583_SERENTRYMODE,"051");                                      /*22# n3 */
	    map.put(Constants.ISO8583_CARDSEQNBR, "001");     										//23#
	  //  map.put(Constants.ISO8583_TRACK2_DATA,"6221359010100538232=000020142207951=");           /*35# z..37(LLVAR)*/
	    map.put(Constants.ISO8583_CARDACCID,"01010009");                                          /*41# ans8*/
	    map.put(Constants.ISO8583_CARDACCCODE,"000129000000000");                               /*42# ans15*/
	    map.put(Constants.ISO8583_CARDACCNAME,"_CHNGDGZSGRGBANKING_成都test");                 /*43# ans40*/
	    map.put(Constants.ISO8583_PINDATA,"111111");                                          /*52# LLL*/
	    map.put(Constants.ISO8583_ADDDATAPRI, "1234132PB051");									//48#
	    map.put(Constants.ISO8583_CURCODE,"156");                                           /*49# an3*/
	    map.put(Constants.ISO8583_PINDATA,"111111");              							/*52# b64*/
	    map.put(Constants.ISO8583_SECURCONTRINFO,"2000000000000000");                       /*53# n16*/
	    map.put(Constants.ISO8583_ICCSYSRELDATA, "9F26080B61E36C79357C5A9F2701809F100807010199A0B806019F3704000000009F360201C2950500001800009A031312109C01609F02060000000000005F2A02015682027D009F1A0201569F03060000000000009F3303E0F0F09F34036003029F3501119F1E0832303033313233318405FFFFFFFFFF9F090220069F4104000000019F74064543433030319F631030313032303030308030303030303030");
	    map.put(Constants.ISO8583_REACODE,"000005100600000000000002300");                                    /*60# LLL*/
	    map.put(Constants.ISO8583_ORGDATA, "020000000701151316500000557675000005576750");//消息类型(4)+系统跟踪号(6)+交易日期时间(10)+32#+33#(11不足补零)
	    map.put(Constants.ISO8583_ACCIDE_N1,"6221359010100149972");                            /*102# LLL*/
	    map.put(Constants.ISO8583_ACCIDE_N2,"6212339010100009642");                            /*103# LLL*/

	    return map;
	}

	/**
	 * ATM转账交易测试
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Transfer4Credit4LoadReversal_Test testCase = new Transfer4Credit4LoadReversal_Test();
		testCase.test();
	}

	public void run() {
		// TODO Auto-generated method stub
		Transfer4Credit4LoadReversal_Test testCase = new Transfer4Credit4LoadReversal_Test();
		try {
			testCase.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
