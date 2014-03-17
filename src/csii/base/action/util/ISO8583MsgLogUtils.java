package csii.base.action.util;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.core.Context;

import csii.base.constant.Constants;

public class ISO8583MsgLogUtils {
	private static Log log=LogFactory.getLog(ISO8583MsgLogUtils.class);
	private static boolean isNotNullString(Object o){
		if(o!=null&&!"".equals(o))
			return true;
		return false;
	}
	/**
	 * 记录日志,默认Log
	 * @param context
	 * @param baseInfo
	 */
	public static void logFormat(Context context,String baseInfo) {
		StringBuilder sb=new StringBuilder("#################################[ 始-"+baseInfo+" ]#################################\r\n");
		sb.append("域号\t域值\t域名称\r\n");
		for(Map.Entry<String, String> en:mapFormat.entrySet()){
			if(isNotNullString(context.getData(en.getKey())))
				sb.append(en.getValue().replace("%", (String)context.getData(en.getKey()))+"\r\n");
		}
		sb.append("#################################[ 终-").append(baseInfo).append(" ]#################################\r\n");
		log.info(sb);
	}

	/**
	 * 记录日志,指定Log
	 * @param context
	 * @param baseInfo
	 * @param inlog
	 */
	public static void logFormat(Context context,String baseInfo,Log inlog) {
		StringBuilder sb=new StringBuilder("\r\n#################################[ 始-"+baseInfo+" ]#################################\r\n");
		sb.append("域号\t域值\t域名称\r\n");
		for(Map.Entry<String, String> en:mapFormat.entrySet()){
			if(isNotNullString(context.getData(en.getKey()))) {
				if(en.getKey().equals("AddiData") && context.getData("HeadTxType").equals("0810") && context.getData("NetwkCode").equals("101")) {
					if(((String)context.getData(en.getKey())).substring(0, 2).equals("NK") || ((String)context.getData(en.getKey())).substring(0, 2).equals("NP"))
						sb.append(en.getValue().replace("%", ((String)context.getData(en.getKey())).substring(0, 2) +
								Util.StringToHex(((String)context.getData(en.getKey())).substring(2)))+"\r\n");
				} else if(en.getKey().equals("MsgSecCode") || en.getKey().equals("PinData"))
					sb.append(en.getValue().replace("%", Util.StringToHex((String)context.getData(en.getKey())))+"\r\n");
				else
					sb.append(en.getValue().replace("%", (String)context.getData(en.getKey()))+"\r\n");
			}
		}
		sb.append("#################################[ 终-").append(baseInfo).append(" ]#################################\r\n");
		inlog.info(sb);
	}

	/**
	 * 记录日志,指定Log
	 * @param context
	 * @param baseInfo
	 * @param inlog
	 */
	public static void logFormatByBitSet(Context context,String baseInfo,Log inlog) {
		try{
			StringBuilder sb=new StringBuilder("\r\n#################################[ 始-"+baseInfo+" ]#################################\r\n");
			BitSet bs=(BitSet)context.getData("BITMAP");
			sb.append("域号\t域值\t域名称\r\n");
			sb.append(mapFormat.get("HeadTxType").replace("%",context.getData("HeadTxType").toString())+"\r\n");
			for(int i=0;i<128;i++){
				if(bs.get(i)&&fields.get(i+1)!=null){
					String s=fields.get(i+1);
					sb.append(mapFormat.get(s).replace("%", (String)context.getData(s))+"\r\n");
				}
			}
			sb.append("#################################[ 终-").append(baseInfo).append(" ]#################################\r\n");
			inlog.info(sb);
		}catch(Exception e){
			log.error("日志记录异常："+e.getCause());
		}
	}

/**
 * 从Map中取值记录日志.
 * @param context
 * @param baseInfo
 * @param inlog
 */
	public static void logFormat(Map map,String baseInfo,Log inlog) {
		StringBuilder sb=new StringBuilder("\r\n#################################[ 始-"+baseInfo+" ]#################################\r\n");
		sb.append("域号\t域值\t域名称\r\n");
		for(Map.Entry<String, String> en:mapFormat.entrySet()){
			if(isNotNullString(map.get(en.getKey())))
				if(en.getKey().equals("AddiData") && map.get("HeadTxType").equals("0800") && map.get("NetwkCode").equals("101")){
					if(((String)map.get(en.getKey())).substring(0, 2).equals("NK") || ((String)map.get(en.getKey())).equals("NP")){
						sb.append(en.getValue().replace("%", ((String)map.get(en.getKey())).substring(0, 2) +
								Util.StringToHex(((String)map.get(en.getKey())).substring(2)))+"\r\n");
					}
				}else if(en.getKey().equals("MsgSecCode") || en.getKey().equals("PinData") || en.getKey().equals("ICCSYSRELDATA") || en.getKey().equals("MAC")){
					sb.append(en.getValue().replace("%", Util.StringToHex((String)map.get(en.getKey())))+"\r\n");
				}else{
					sb.append(en.getValue().replace("%", (String)map.get(en.getKey()))+"\r\n");
				}
		}
		sb.append("#################################[ 终-").append(baseInfo).append(" ]#################################\r\n");
		inlog.info(sb);
	}

	private static Map<String, String> mapFormat=new LinkedHashMap<String, String>();
	private static Map<Integer, String> fields=new LinkedHashMap<Integer, String>();
	static{
  	mapFormat.put(Constants.ISO8583_HEAD_TX_TYPE,"HeadTxType [%]\t消息类型");
  	mapFormat.put(Constants.ISO8583_ACCTNO,"域2\t[%]\t主账号");
  	mapFormat.put(Constants.ISO8583_PRO_CODE,"域3\t[%]\t交易码");
  	mapFormat.put(Constants.ISO8583_TRANAMT,"域4\t[%]\t交易金额");
  	mapFormat.put(Constants.ISO8583_SETAMT,"域5\t[%]\t清算金额");
  	mapFormat.put(Constants.ISO8583_CHDAMT,"域6\t[%]\t持卡人扣账金额");
  	mapFormat.put(Constants.ISO8583_TRANSDTTIME,"域7\t[%]\t传输日期");
  	mapFormat.put(Constants.ISO8583_SYSTRACENUM,"域11\t[%]\t跟踪号");
  	mapFormat.put(Constants.ISO8583_LOCTRANSTIME,"域12\t[%]\t交易时间");
  	mapFormat.put(Constants.ISO8583_LOCTRANSDATE,"域13\t[%]\t交易日期");
  	mapFormat.put(Constants.ISO8583_EXPDAT,"域14\t[%]\t卡有效期");
  	mapFormat.put(Constants.ISO8583_SETDATE,"域15\t[%]\t清算日期");
  	mapFormat.put(Constants.ISO8583_MERCHANTTYPE,"域18\t[%]\t商户类型");
  	mapFormat.put(Constants.ISO8583_COUNTRYCD,"域19\t[%]\t国家代码");
  	mapFormat.put(Constants.ISO8583_SERENTRYMODE,"域22\t[%]\t服务点输入方式码");
  	mapFormat.put(Constants.ISO8583_CARDSEQNBR,"域23\t[%]\t卡序列号");
  	mapFormat.put(Constants.ISO8583_SERCONDCODE,"域25\t[%]\t服务点条件码");
  	mapFormat.put(Constants.ISO8583_SERCAPCODE,"域26\t[%]\t服务点PIN获取码");
  	mapFormat.put(Constants.ISO8583_TRANSFEE,"域28\t[%]\t交易费");
  	mapFormat.put(Constants.ISO8583_ACQCODE,"域32\t[%]\t受理机构标识码");
  	mapFormat.put(Constants.ISO8583_FORWARDCODE,"域33\t[%]\t发送机构标识码");
  	mapFormat.put(Constants.ISO8583_TRACK2_DATA,"域35\t[%]\t2磁道数据");
  	mapFormat.put(Constants.ISO8583_TRACK3_DATA,"域36\t[%]\t3磁道数据");
  	mapFormat.put(Constants.ISO8583_RTVREFNUM,"域37\t[%]\t检索参考号");
  	mapFormat.put(Constants.ISO8583_AUTRESCOD,"域38\t[%]\t授权码");
  	mapFormat.put(Constants.ISO8583_RESCODE,"域39\t[%]\t应答码");
  	mapFormat.put(Constants.ISO8583_CARDACCID,"域41\t[%]\t受卡方终端标识");
  	mapFormat.put(Constants.ISO8583_CARDACCCODE,"域42\t[%]\t受卡方标识码");
  	mapFormat.put(Constants.ISO8583_CARDACCNAME,"域43\t[%]\t受卡方名称地址");
  	mapFormat.put(Constants.ISO8583_ADDDATA,"域44\t[%]\t附加应答码");
  	mapFormat.put(Constants.ISO8583_ADDDATAPRI,"域48\t[%]\t附加应数据");
  	mapFormat.put(Constants.ISO8583_CURCODE,"域49\t[%]\t交易货币代码");
  	mapFormat.put(Constants.ISO8583_CURCODE_SETTLE,"域50\t[%]\t清算货币代码");
  	mapFormat.put(Constants.ISO8583_PINDATA,"域52\t[%]\t个人标识码数据");
  	mapFormat.put(Constants.ISO8583_SECURCONTRINFO,"域53\t[%]\t安全控制信息");
  	mapFormat.put(Constants.ISO8583_BALAMT,"域54\t[%]\t实际余额");
  	mapFormat.put(Constants.ISO8583_ICCSYSRELDATA,"域55\t[%]\t基于PBOC借贷记标准的IC卡数据域");
  	mapFormat.put(Constants.ISO8583_ADD_DATA_PRI,"域57\t[%]\t附加交易信息");
  	mapFormat.put(Constants.ISO8583_IC_PBOC_DATA_RESVD, "域58\t[%]基于PBOC电子钱包/存折IC卡标准的交易数据");
  	mapFormat.put(Constants.ISO8583_DETAILDATA,"域59\t[%]\t明细查询数据");
  	mapFormat.put(Constants.ISO8583_REACODE,"域60\t[%]\t自定义域");
  	mapFormat.put(Constants.ISO8583_IDENNUMBE,"域61\t[%]\t持卡人身份认证信息");
  	mapFormat.put(Constants.ISO8583_SWITCH_DATA,"域62\t[%]\t交换中心数据");
  	mapFormat.put(Constants.ISO8583_FIN_NET_DATE,"域63\t[%]\t#金融网路数据");
  	mapFormat.put(Constants.ISO8583_SETTLE_CODE,"域66\t[%]\t清算代码");
  	mapFormat.put(Constants.ISO8583_NET_MAN_INF_CODE,"域70\t[%]\t网络管理信息码");
  	mapFormat.put(Constants.ISO8583_CREDITS_NUM,"域74\t[%]\t贷记交易笔数");
  	mapFormat.put(Constants.ISO8583_CREDITS_REV_NUM,"域75\t[%]\t冲正贷记笔数");
  	mapFormat.put(Constants.ISO8583_DEBITS_NUM,"域76\t[%]\t借记交易笔数");
  	mapFormat.put(Constants.ISO8583_DEBITS_REV_NUM,"域77\t[%]\t冲正借记笔数");
  	mapFormat.put(Constants.ISO8583_TRANSFER_NUM,"域78\t[%]\t转帐笔数");
  	mapFormat.put(Constants.ISO8583_TRANSFER_REV_NUM,"域79\t[%]\t冲正转帐笔数");
  	mapFormat.put(Constants.ISO8583_INQUERIES_NUM,"域80\t[%]\t查询笔数");
  	mapFormat.put(Constants.ISO8583_AUTHOR_NUM,"域81\t[%]\t授权笔数");
  	mapFormat.put(Constants.ISO8583_FEE_AMOUNT_CREDITS,"域82\t[%]\t贷记服务费金额");
  	mapFormat.put(Constants.ISO8583_FEE_AMOUNT_DEBITS,"域84\t[%]\t借记服务费金额");
  	mapFormat.put(Constants.ISO8583_CREDITS_AMOUNT,"域86\t[%]\t贷记交易金额");
  	mapFormat.put(Constants.ISO8583_CREDITS_REV_AMOUNT,"域87\t[%]\t冲正贷记金额");
  	mapFormat.put(Constants.ISO8583_DEBITS_AMOUNT,"域88\t[%]\t借记交易金额");
  	mapFormat.put(Constants.ISO8583_DEBITS_REV_AMOUNT,"域89\t[%]\t冲正借记金额");
  	mapFormat.put(Constants.ISO8583_ORGDATA,"域90\t[%]\t原始数据元");
  	mapFormat.put(Constants.ISO8583_AMOUNT_NET_SETTLE,"域97\t[%]\t净清算额");
  	mapFormat.put(Constants.ISO8583_SET_INST_IDCODE,"域99\t[%]\t清算机构代码");
  	mapFormat.put(Constants.ISO8583_RCVCODE,"域100\t[%]\t接收机构标识码");
  	mapFormat.put(Constants.ISO8583_ACCIDE_N1,"域102\t[%]\t账户标识1");
  	mapFormat.put(Constants.ISO8583_ACCIDE_N2,"域103\t[%]\t账户标识2");
  	mapFormat.put(Constants.ISO8583_NANSCRESERVE,"域121\t[%]\tCUPS保留");
  	mapFormat.put(Constants.ISO8583_ACQRESERVE,"域122\t[%]\t受理方保留");
  	mapFormat.put(Constants.ISO8583_ISURESERVE,"域123\t[%]\t发卡方保留");
  	mapFormat.put(Constants.ISO8583_MESAUTHCD,"域128\t[%]\tMac");

  	fields.put(2,  Constants.ISO8583_ACCTNO            );
  	fields.put(3,  Constants.ISO8583_PRO_CODE          );
  	fields.put(4,  Constants.ISO8583_TRANAMT           );
  	fields.put(5,  Constants.ISO8583_SETAMT            );
  	fields.put(6,  Constants.ISO8583_CHDAMT            );
  	fields.put(7,  Constants.ISO8583_TRANSDTTIME       );
  	fields.put(11, Constants.ISO8583_SYSTRACENUM       );
  	fields.put(12, Constants.ISO8583_LOCTRANSTIME      );
  	fields.put(13, Constants.ISO8583_LOCTRANSDATE      );
  	fields.put(14, Constants.ISO8583_EXPDAT            );
  	fields.put(15, Constants.ISO8583_SETDATE           );
  	fields.put(18, Constants.ISO8583_MERCHANTTYPE      );
  	fields.put(19, Constants.ISO8583_COUNTRYCD         );
  	fields.put(22, Constants.ISO8583_SERENTRYMODE      );
  	fields.put(23, Constants.ISO8583_CARDSEQNBR        );
  	fields.put(25, Constants.ISO8583_SERCONDCODE       );
  	fields.put(26, Constants.ISO8583_SERCAPCODE        );
  	fields.put(28, Constants.ISO8583_TRANSFEE          );
  	fields.put(32, Constants.ISO8583_ACQCODE           );
  	fields.put(33, Constants.ISO8583_FORWARDCODE       );
  	fields.put(35, Constants.ISO8583_TRACK2_DATA       );
  	fields.put(36, Constants.ISO8583_TRACK3_DATA       );
  	fields.put(37, Constants.ISO8583_RTVREFNUM         );
  	fields.put(38, Constants.ISO8583_AUTRESCOD         );
  	fields.put(39, Constants.ISO8583_RESCODE           );
  	fields.put(41, Constants.ISO8583_CARDACCID         );
  	fields.put(42, Constants.ISO8583_CARDACCCODE       );
  	fields.put(43, Constants.ISO8583_CARDACCNAME       );
  	fields.put(44, Constants.ISO8583_ADDDATA           );
  	fields.put(48, Constants.ISO8583_ADDDATAPRI        );
  	fields.put(49, Constants.ISO8583_CURCODE           );
  	fields.put(50, Constants.ISO8583_CURCODE_SETTLE    );
  	fields.put(52, Constants.ISO8583_PINDATA           );
  	fields.put(53, Constants.ISO8583_SECURCONTRINFO    );
  	fields.put(54, Constants.ISO8583_BALAMT            );
  	fields.put(55, Constants.ISO8583_ICCSYSRELDATA     );
  	fields.put(57, Constants.ISO8583_ADD_DATA_PRI      );
  	fields.put(58, Constants.ISO8583_IC_PBOC_DATA_RESVD );
  	fields.put(59, Constants.ISO8583_DETAILDATA        );
  	fields.put(60, Constants.ISO8583_REACODE           );
  	fields.put(61, Constants.ISO8583_IDENNUMBE         );
  	fields.put(62, Constants.ISO8583_SWITCH_DATA       );
  	fields.put(63, Constants.ISO8583_FIN_NET_DATE      );
  	fields.put(66, Constants.ISO8583_SETTLE_CODE       );
  	fields.put(70, Constants.ISO8583_NET_MAN_INF_CODE  );
  	fields.put(74, Constants.ISO8583_CREDITS_NUM       );
  	fields.put(75, Constants.ISO8583_CREDITS_REV_NUM   );
  	fields.put(76, Constants.ISO8583_DEBITS_NUM        );
  	fields.put(77, Constants.ISO8583_DEBITS_REV_NUM    );
  	fields.put(78, Constants.ISO8583_TRANSFER_NUM      );
  	fields.put(79, Constants.ISO8583_TRANSFER_REV_NUM  );
  	fields.put(80, Constants.ISO8583_INQUERIES_NUM     );
  	fields.put(81, Constants.ISO8583_AUTHOR_NUM        );
  	fields.put(82, Constants.ISO8583_FEE_AMOUNT_CREDITS);
  	fields.put(84, Constants.ISO8583_FEE_AMOUNT_DEBITS );
  	fields.put(86, Constants.ISO8583_CREDITS_AMOUNT    );
  	fields.put(87, Constants.ISO8583_CREDITS_REV_AMOUNT);
  	fields.put(88, Constants.ISO8583_DEBITS_AMOUNT     );
  	fields.put(89, Constants.ISO8583_DEBITS_REV_AMOUNT );
  	fields.put(90, Constants.ISO8583_ORGDATA           );
  	fields.put(97, Constants.ISO8583_AMOUNT_NET_SETTLE );
  	fields.put(99, Constants.ISO8583_SET_INST_IDCODE   );
  	fields.put(100,Constants.ISO8583_RCVCODE           );
  	fields.put(102,Constants.ISO8583_ACCIDE_N1         );
  	fields.put(103,Constants.ISO8583_ACCIDE_N2         );
  	fields.put(121,Constants.ISO8583_NANSCRESERVE      );
  	fields.put(122,Constants.ISO8583_ACQRESERVE        );
  	fields.put(123,Constants.ISO8583_ISURESERVE        );
  	fields.put(128,Constants.ISO8583_MESAUTHCD         );
	}

}
