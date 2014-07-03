package csii.dzip.action.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.CsiiStringUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.base.constant.TranName;
import csii.dzip.core.Dict;
import csii.dzip.core.InitData4Dzip;
import csii.dzip.core.TripleDES_MD5;
import csii.pe.service.comm.StringChange;

public class Init {
	private static String base64Key = "YXMXMJM0NTY3ODLMYXNKZMFZZGZHREVM"; // A3F2569DESJEIWBCJOTY45DYQWF68H1Y

	/**
	 * XML报文转换为8583报文初始化
	 * @param ctx
	 */
	public static void initXml2Iso8583(Map<String, Object> map){
		String transactionID = String.valueOf(map.get(Constants.ISO8583_TRANSACTION_ID));
		//初始化报文类型,3#,交易码
		if(TranName.ONLI_CREDIT4LOAD.equals(transactionID)
				|| TranName.ONLI_ADDITIONAL_CREDIT4LOAD.equals(transactionID)){//指定账户圈存或补登圈存
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0200);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_600000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_020060);
		}else if (TranName.ONLI_CREDIT4LOADREVERSAL.equals(transactionID)
				|| TranName.ONLI_ADDITIONAL_CREDIT4LOADREVERSAL.equals(transactionID)) {//指定账户圈存冲正或补登圈存冲正
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0420);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_600000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_042060);
		}else if (TranName.ONLI_DEBIT4LOAD.equals(transactionID)
				|| TranName.ONLI_SWITCH_ADDITIONAL_CREDIT4LOAD.equals(transactionID)) {//指定账户圈提或转待补登
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0100);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_610000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_010061);
		}else if (TranName.ONLI_DEBIT4LOADCONFIRM.equals(transactionID)
				|| TranName.ONLI_SWITCH_ADDITIONAL_CREDIT4LOAD_CONFIRM.equals(transactionID)) {//指定账户圈提确认或转待补登确认
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0200);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_610000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_020061);
		}else if (TranName.ONLI_CASHRECHARGE4CREDIT4LOAD.equals(transactionID)) {//现金充值圈存
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0200);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_630000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_020063);
		}else if (TranName.ONLI_CASHRECHARGE4CREDIT4LOADREVERSAL.equals(transactionID)) {//现金充值圈存冲正
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0420);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_630000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_042063);
		}else if (TranName.ONLI_CASHRECHARGE4CREDIT4LOADCANCEL.equals(transactionID)) {//现金充值圈存撤销
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0200);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_170000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_02001791);
		}else if (TranName.ONLI_CASHRECHARGE4CREDIT4LOADCANCELREVERSAL.equals(transactionID)) {//现金充值圈存撤销冲正
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0420);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_170000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_04201791);
		}else if (TranName.ONLI_TRANSFERCREDIT4LOAD.equals(transactionID)) {//非指定账户转账圈存
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0200);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_620000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_020062);
		}else if (TranName.ONLI_TRANSFERCREDIT4LOADREVERSAL.equals(transactionID)) {//非指定账户转账圈存冲正
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0420);
			map.put(Constants.ISO8583_PRO_CODE, Constants.PROCODE_620000);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_042062);
		}else if(TranName.ONLI_SCRIPT_NOTICE.equals(transactionID)){
			map.put(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0620);
			map.put(Constants.ISO8583_TRANSACTION_ID, Constants.TRANCD_0620);
		}
		map.put(Constants.ISO8583_MERCHANTTYPE, Constants.MERTYPCD_6010);//18# 商户类型
		map.put(Constants.ISO8583_SERCONDCODE, Constants.PE_91);//25# 服务点条件码
		map.put(Constants.ISO8583_SERCAPCODE, Constants.PE_06);//26# 服务点PIN获取码
		map.put(Constants.ISO8583_CURCODE, Constants.CURCODE_CN);//49# 币种
	}

	/**
	 * 获得返回柜面交易的TransactionID
	 * @param ctx
	 */
	public static void getResponseTransactionID(Context ctx){
		String transactionID = String.valueOf(ctx.getData(Constants.ISO8583_TRANSACTION_ID));
		if(Constants.TRANCD_021060.equals(transactionID)
				|| Constants.TRANCD_020060.equals(transactionID)
				|| Constants.TRANCD_02106001.equals(transactionID)){
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_CREDIT4LOAD);
			if(null != ctx.getData(Constants.ISO8583_ACCIDE_N1)//判断是否是补登圈存
					&& !Constants.PE_NULL.equals(ctx.getData(Constants.ISO8583_ACCIDE_N1))){
				//如果验证不通过,直接返回柜面
				ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_ADDITIONAL_CREDIT4LOAD);
			}
		}else if (Constants.TRANCD_043060.equals(transactionID)
				|| Constants.TRANCD_042060.equals(transactionID)
				|| Constants.TRANCD_04306001.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_CREDIT4LOADREVERSAL);
			if(null != ctx.getData(Constants.ISO8583_ACCIDE_N1)//判断是否是补登圈存冲正
					&& !Constants.PE_NULL.equals(ctx.getData(Constants.ISO8583_ACCIDE_N1))){
				//如果验证不通过,直接返回柜面
				ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_ADDITIONAL_CREDIT4LOADREVERSAL);
			}
		}else if (Constants.TRANCD_011061.equals(transactionID) || Constants.TRANCD_010061.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_DEBIT4LOAD);
			if(null != ctx.getData(Constants.ISO8583_ACCIDE_N1)//判断是否是转待补登圈提
					&& !Constants.PE_NULL.equals(ctx.getData(Constants.ISO8583_ACCIDE_N1))){
				//如果验证不通过,直接返回柜面
				ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_SWITCH_ADDITIONAL_CREDIT4LOAD);
			}
		}else if (Constants.TRANCD_021061.equals(transactionID)
				|| Constants.TRANCD_020061.equals(transactionID)
				|| Constants.TRANCD_02106101.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_DEBIT4LOADCONFIRM);
			if(null != ctx.getData(Constants.ISO8583_ACCIDE_N1)//判断是否是转待补登确认
					&& !Constants.PE_NULL.equals(ctx.getData(Constants.ISO8583_ACCIDE_N1))){
				//如果验证不通过,直接返回柜面
				ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_SWITCH_ADDITIONAL_CREDIT4LOAD_CONFIRM);
			}
		}else if (Constants.TRANCD_021063.equals(transactionID) || Constants.TRANCD_020063.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_CASHRECHARGE4CREDIT4LOAD);
		}else if (Constants.TRANCD_043063.equals(transactionID) || Constants.TRANCD_042063.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_CASHRECHARGE4CREDIT4LOADREVERSAL);
		}else if (Constants.TRANCD_02101791.equals(transactionID) || Constants.TRANCD_02001791.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_CASHRECHARGE4CREDIT4LOADCANCEL);
		}else if (Constants.TRANCD_04301791.equals(transactionID) || Constants.TRANCD_04201791.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_CASHRECHARGE4CREDIT4LOADCANCELREVERSAL);
		}else if (Constants.TRANCD_021062.equals(transactionID) || Constants.TRANCD_021065.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_TRANSFERCREDIT4LOAD);
		}else if (Constants.TRANCD_043065.equals(transactionID) || Constants.TRANCD_043062.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_TRANSFERCREDIT4LOADREVERSAL);
		}else if (Constants.TRANCD_0630.equals(transactionID) || Constants.TRANCD_0620.equals(transactionID)) {
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_SCRIPT_NOTICE);
		}else if(Constants.TRANCD_02106001.equals(transactionID)){
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_ADDITIONAL_CREDIT4LOAD);
		}else if(Constants.TRANCD_04306001.equals(transactionID)){
			ctx.setData(Constants.ISO8583_TRANSACTION_ID, TranName.ONLI_ADDITIONAL_CREDIT4LOADREVERSAL);
		}
	}

	/**
	 * 根据商户类型判断交易是否来自柜面
	 * @param ctx
	 * @return
	 */
	public static boolean isTransactionFromOnli(Context ctx){
		String reacode = ctx.getString(Constants.ISO8583_REACODE);//60#
		String type = reacode.substring(9, 10);
		//银联数据脚本通知应答不返回18#,只有通过60#判断交易是否是来自柜面
		return Constants.MERTYPCD_6010.equals(String.valueOf(ctx.getData(Constants.ISO8583_MERCHANTTYPE)))
				|| Constants.PE_SIX.equals(type);
	}

	/**
	 * @author chenshaoqi
	 * @since 2010.07.22
	 * @description:填充调用核心存储过程所需的报文
	 * @param ctx 传入ATM报文数据的对象
	 * @return
	 */
	public static Map<String, Object> initForXmlMsg(Context ctx) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.ISO8583_TRANSACTION_ID,ctx.getData(Constants.ISO8583_TRANSACTION_ID));
		map.put(Constants.PROC_CODE,ctx.getData(Constants.ISO8583_PRO_CODE));      			  //处理码
//		map.put(Dict.OUTPUT_THEXML, "0");                                                     //暂时不用
//		map.put(Dict.HEADER_ATM, "0");                                                        //暂时不用
//		map.put("SystemTraceNumber", "0");                                                    //暂时不用
		map.put(Constants.MSG_TYPE, ctx.getData(Constants.ISO8583_HEAD_TX_TYPE));  			  //信息类型
//		map.put(Dict.CONT_CTRL, "0");                                                         //暂时不用
		map.put(Constants.TRANSAC_PAN, ctx.getData(Constants.ISO8583_ACCTNO));     			  //账号,2#
//		String pan = (String) ctx.getData(Constants.ISO8583_ACCTNO);
//		String key = "";
//		if(pan.length()>=8)
//			key = pan.substring(pan.length()-8,pan.length());
		if(ctx.getData(Constants.ISO8583_TRANAMT)!=null){
			BigDecimal amt = Util.getAmount(String.valueOf(ctx.getData(Constants.ISO8583_TRANAMT)));
			amt = amt.movePointLeft(2);
			map.put(Constants.TRANSAC_AMOUNT, String.valueOf(amt)); 								//交易金额 4#
		}
		else
		    map.put(Constants.TRANSAC_AMOUNT, Constants.PE_ZERO);                        	  //否则，赋值为零
		if(ctx.getData(Constants.ISO8583_TRANSFEE)!=null)
			map.put(Constants.TRANSAC_FEE_AMOUNT, ctx.getData(Constants.ISO8583_TRANSFEE));//若交易费28#不为空，取值
	    else
	    	map.put(Constants.TRANSAC_FEE_AMOUNT, Constants.PE_ZERO);                         //否则，赋值为零
//		map.put(Dict.TRANSAC_REVERSAL_AMOUNT, "0");                                           //暂时不用
		map.put(Constants.TRANSAC_SWITCH_DATE,Constants.CURRENT_YEAR.concat((String)ctx.getData(Constants.ISO8583_TRANSDTTIME))); //交易日期时间 Year+7#
		map.put(Constants.TRANSAC_LOCAL_DATE, Constants.CURRENT_YEAR.concat((String) ctx.getData(Constants.ISO8583_LOCTRANSDATE)));//交易日期Year+13#
		map.put(Constants.TRANSAC_ACQUIRERID_CODE, ctx.getData(Constants.ISO8583_ACQCODE));   //32#受理机构标识
		map.put(Constants.TRANSAC_AUDIT_TRACE, ctx.getData(Constants.ISO8583_FORWARDCODE));   //33#发送机构标识码
		map.put(Constants.TRANSAC_LOCAL_DATE_TIME, Constants.CURRENT_YEAR+ ctx.getData(Constants.ISO8583_TRANSDTTIME));//本地时间

//		map.put(Dict.TRANSAC_MANAGECD, "0");                                                      //暂时不用
//		map.put(Dict.TRANSAC_MANAGE_DATA, "0");                                                   //暂时不用
		if(ctx.getData(Constants.ISO8583_TRACK2_DATA) != null)
			map.put(Constants.TRANSAC_TRACK2_DATA, ctx.getData(Constants.ISO8583_TRACK2_DATA)); //二磁道信息，35#
		map.put(Constants.TRANSAC_RETRIEVAL_REF_NUM, ctx.getData(Constants.PE_JOURNAL_NO));     //平台流水号
		map.put(Constants.TRANSAC_TERMINALID, ctx.getData(Constants.ISO8583_CARDACCID));        //受卡机终端标识码  41#
//		map.put(Constants.TRANSAC_LOCATION_STREET_ADDRESS, ctx.getData(Constants.ISO8583_CARDACCNAME)); //受卡方名称地址

//		map.put(Dict.TRANSAC_LOCATION_CITY, "");                                                         //暂时不用
//		map.put(Dict.TRANSAC_LOCATION_STATE, "");                                                        //暂时不用
//		map.put(Dict.TRANSACISO_ALPHA_COUNTRY_CODE, "");                                                 //暂时不用
//		map.put(Dict.TRANSAC_INSTITUTION_MERCHANT_NAME, "0");                                            //暂时不用
		if(ctx.getData(Constants.ISO8583_ACCIDE_N1)!=null)
			map.put(Constants.TRANSAC_ACCNT_FROM, ctx.getData(Constants.ISO8583_ACCIDE_N1));  			//102#不为空，取值
		if(ctx.getData(Constants.ISO8583_ACCIDE_N2)!=null)
			map.put(Constants.TRANSAC_ACCNT_TO, ctx.getData(Constants.ISO8583_ACCIDE_N2));    			//103#不为空，取值

//		map.put(Dict.TRANSAC_STATEMENT_TYPE, "");                                                        //暂时不用
//		map.put(Dict.TRANSAC_RSN_CODE, "");                                                              //暂时不用
		map.put(Constants.CARD_ACCEPTORID_CODE, ctx.getData(Constants.ISO8583_CARDACCCODE));  			//受卡方标识码 42#
		map.put(Constants.TRANSAC_CARD_SEQ_NUM, Constants.PE_ONE);                                       //默认是1
//		map.put(Dict.FILE_UPDATE_CODE, "");                                                              //暂时不用
//		map.put(Dict.SIC_CODE, "");                                                                      //暂时不用
//		map.put(Dict.SICSUB_CODE, "");                                                                   //暂时不用
//		map.put(Dict.NAICS_CODE, "");                                                                    //暂时不用

		map.put(Constants.PINNEDYN,ctx.getData(Constants.CHECK_PIN));                                                     //默认是Y


		map.put(Constants.SETTLE_DATE, ctx.getData(Constants.PE_POST_DATE));                  			  //清算日期
//		map.put(Constants.TRANSAC_PIN, ctx.getData(Constants.ISO8583_PINDATA));                           //密码

		if(ctx.getData(Constants.IN_PIN)!=null
				&&!Constants.PE_NULL.equals((String)ctx.getData(Constants.IN_PIN))){			//密码域不为空时
			 map.put(Constants.TRANSAC_PIN,ctx.getData(Constants.IN_PIN)); 					//明文密码
			 String encryptPwd = generateEntryPwd(ctx,(String)ctx.getData(Constants.IN_PIN));  //调用3DES+MD5算法生成密文
			 map.put(Constants.TRANSAC_PIN,encryptPwd); 												//密文密码
		}
		map.put(Constants.PE_REQ_CHANN, ctx.getData(Constants.PE_REQ_CHANN));                            //交易渠道
		map.put(Constants.TRACK2YN, ctx.getData(Constants.CHECK_TRACK2));                                                     //默认是Y
		return map;
	}

	/**
	 * @author shengzt
	 * @since 2010.08.23
	 * @description:填充调用核心查询账户信息存储过程传入参数
	 * @param ctx 传入数据对象
	 * @return
	 */
	public static Map<String, Object> initSelectAcctInfo(Context ctx, HashMap<String, Object> procedureMap,String type8583) {
		int cashBoxNbr = Integer.valueOf((String)ctx.getData(Constants.IN_CASHBOXNBR));	//钱箱号
		procedureMap.put(Constants.IN_ACCTNO, ctx.getData(Constants.PE_ACC_NO));        //账号
		procedureMap.put(Constants.IN_CARDACCTFLAG, Constants.CARD_ACCT_FLAG_2);        //账号/介质号标志
		procedureMap.put(Constants.IN_MEDIAACCTSEQNO, null);                            //介质下挂帐户序号
		procedureMap.put(Constants.IN_CHKMAGFLAG, null);                                //备用
		procedureMap.put(Constants.IN_POSTDATE,ctx.getData(Constants.PE_POST_DATE));    //帐务日期
		procedureMap.put(Constants.IN_TRACK2, ctx.getData(Constants.IN_TRACK2));        //二磁
		procedureMap.put(Constants.IN_TRACK3, null);                                    //备用
		String pin = (String)ctx.getData(Constants.IN_PIN);
		String encryptPwd = generateEntryPwd(ctx,pin); 									//调用3DES+MD5算法生成密文
		procedureMap.put(Constants.IN_PIN,encryptPwd);  								//密码
		procedureMap.put(Constants.IN_RTXNSOURCECD,
				(Constants.PE_NULL.equals(ctx.getData(Constants.IN_RTXNREASONCD))
				 || null == ctx.getData(Constants.IN_RTXNSOURCECD)) ? Constants.YATM : ctx.getData(Constants.IN_RTXNSOURCECD));//交易的来源
		procedureMap.put(Constants.IN_TRLLERNBR,ctx.getData(Constants.IN_ORIGPERSNBR)); //柜员号
		procedureMap.put(Constants.IN_CASHBOXNBR, cashBoxNbr);       					//钱箱号
		procedureMap.put(Constants.IN_RTXNTYPCD, ctx.getData(Constants.IN_RTXNTYPCD));//交易类型
		procedureMap.put(Constants.IN_TRACKYN, ctx.getData(Constants.CHECK_TRACK2));  //是否校验二磁
		procedureMap.put(Constants.IN_PINYN, ctx.getData(Constants.CHECK_PIN));     //是否校验密码
		procedureMap.put(Constants.IN_TRANAMT, ctx.getData(Constants.PE_TRAN_AMT)); //交易金额，参数获得
		procedureMap.put(Constants.IN_IDENINFO,null);                               //持卡人证件
		procedureMap.put(Constants.IN_EXPIREDATE,null);                             //卡有效期
		//传入交易号(消息类型和交易处理码)和业务编码 2013-07-24 xiehai
		procedureMap.put(Constants.IN_TRAN_CD, ctx.getData(Constants.TransactionId));//交易号
		procedureMap.put(Constants.IN_ASSBUSTYP, ctx.getData(Constants.IN_ASSBUSTYP));//关联业务编码
		if(Constants.PE_Y.equals(ctx.getData(Constants.CHECK_IDEN))){
			procedureMap.put(Constants.IN_IDENINFO,ctx.getData(Constants.PE_FIELD_61));//持卡人证件
			String idenInfo=null;
			if(String.valueOf(ctx.getData(Constants.PE_FIELD_61)).indexOf(Constants.PE_CUP)==0){
				 idenInfo=fill(String.valueOf(ctx.getData(Constants.PE_FIELD_61)),2,Constants.LEFT,Constants.PE_SPACE);
				 idenInfo=fill(idenInfo,22,Constants.LEFT,Constants.PE_ZERO);
				 if(Constants.ISO8583.equals(type8583))
					 ctx.setData(Constants.ISO8583_IDENNUMBE,idenInfo);
				 else
					 ctx.setData(Constants.FIX8583_IDEN,idenInfo);

			}
			if(Constants.ISO8583.equals(type8583))
				procedureMap.put(Constants.IN_EXPIREDATE,ctx.getData(Constants.ISO8583_EXPDAT));	//卡有效期
			else
			    procedureMap.put(Constants.IN_EXPIREDATE,ctx.getData(Constants.FIX8583_EXPI));	//卡有效期
		}
		return procedureMap;
	}
	/**
	 * @author xujin
	 * @desc 柜面和互联网的现金转账类交易域61中的身份证件验证没有AM用法，
	 *       需要转换成AM以方便在数据库中判断
	 * @date 2010.08.31
	 */
	public static void formatIdenAM(Context ctx){
		String reacode0910=ctx.getString(Constants.IN_CHANNELTYPCD);
		if((Constants.PE_06.equals(reacode0910)||Constants.PE_07.equals(reacode0910))
				&&ctx.getData(Constants.PE_FIELD_61)!=null){
			StringBuffer identify = new StringBuffer((String)ctx.getData(Constants.PE_FIELD_61));
			String idenFlag=identify.substring(0, 2);
			if(Constants.PE_01.equals(idenFlag)){//01:身份证件
				int NM=identify.indexOf(Constants.PE_NM);//持卡人姓名
				if(NM!=-1)
					identify.insert(NM,"AM0010000000000000");
				else{
					identify.insert(identify.length(),"CUPAM0010000000000000");
					ctx.setData(Constants.CHECK_IDEN,  Constants.PE_Y);
				}
			}
			ctx.setData(Constants.PE_FIELD_61,identify.toString());
		}
	}
	/**
	 * @author xujin
	 * @desc   转出交易把域61的NM用法标记的持卡人姓名2替换持卡人姓名1传到存储过程判断。
	 * @date    2012.03.24
	 */
	public static void initForIdenNM(Context ctx){
		if(ctx.getData(Constants.PE_FIELD_61)!=null){
			String identify=(String)ctx.getData(Constants.PE_FIELD_61);
			int NM=identify.indexOf(Constants.PE_NM);
			if(NM!=-1){
				 String strNM=identify.substring(NM+2);
				 String str=identify.substring(0, NM+2);
				 for(int i=0;i<strNM.length();i++){
					 if(strNM.substring(i,i+1).equals(Constants.PE_SPACE)){
						 String strName2=strNM.substring(i).trim();
						 ctx.setData(Constants.PE_FIELD_61,str+strName2);
						 break;
					 }
				 }
			}
		}
	}
	/**
	 * @author xujin
	 * @desc   格式化F57的CI用法:持卡人身份信息
	 * @date    2012.10.24
	 */
	@SuppressWarnings("unchecked")
	public static String formatF57ForCI(HashMap procedureMap){
		String addDataPri="CI";
		String acctName=(String)procedureMap.get(Constants.OUT_ACCTNAME);
		addDataPri=addDataPri+CsiiStringUtils.formatString(acctName, 20);
		String idType=(String)procedureMap.get(Constants.OUT_IDTYPE);//证件类型
		String idNO=(String)procedureMap.get(Constants.OUT_IDNO);//证件编码
		if(Constants.PE_ZERO.equals(idType)||Constants.PE_SEVEN.equals(idType)){
			addDataPri=addDataPri+Constants.PE_01;//身份证
		}else if(Constants.PE_TWO.equals(idType)){
			addDataPri=addDataPri+Constants.PE_03;//护照
		}else if(Constants.PE_THREE.equals(idType)){
			addDataPri=addDataPri+Constants.PE_02;//军官证
		}else if(Constants.PE_FOUR.equals(idType)){
			addDataPri=addDataPri+Constants.PE_07;//士兵证
		}else if(Constants.PE_SIX.equals(idType)){
			addDataPri=addDataPri+Constants.PE_05;//台胞证
		}else if(Constants.PE_NINE.equals(idType)){
			addDataPri=addDataPri+Constants.PE_06;//警官证
		}else{
			addDataPri=addDataPri+Constants.PE_99;//其它证件
		}
		return addDataPri=addDataPri+CsiiStringUtils.formatString(idNO, 20);
	}

	/**
	 * @author chenshaoqi
	 * @desc   在调用对deposittxn过程之前，对传入参数进行初始化。
	 * @date    2010.08.31
	 */
	@SuppressWarnings("unchecked")
	public static Map initDeposittxn(Context ctx, Map procedureMap,String type8583){
		StringBuffer trdt = new StringBuffer(Util.getDateString(Constants.PE_YYYY));			//年份
		if(Constants.ISO8583.equals(type8583))
			trdt.append(ctx.getData(Constants.ISO8583_TRANSDTTIME));						    //传输日期和时间
		if(Constants.FIX8583.equals(type8583))
			trdt.append(ctx.getData(Constants.FIX8583_TRDT));						    		//传输日期和时间
		int orgNbr = Integer.valueOf((String) ctx.getData(Constants.IN_ORGNBR));				//机构号
		int cashBoxNbr = Integer.valueOf((String)ctx.getData(Constants.IN_CASHBOXNBR));			//钱箱号
		String postDate = Util.getFormatString((String) ctx.getData(Constants.PE_POST_DATE), Constants.PE_YYMD);
		Date EFFDATE = Util.getDate(postDate,Constants.PE_YYMD);								//日期
		/***********************************************************/
		procedureMap.put(Constants.IN_ORGNBR, orgNbr);  										//机构号，数据库配置参数
		procedureMap.put(Constants.IN_CARDACCTFLAG,ctx.getData(Constants.MEDIUM_TYPE));    		//账号/介质号/卡号标志 :0：存折介质号   1：账号  2：卡号  3: 支票
		if(Constants.PE_NULL.equals(ctx.getData(Constants.IN_MEDIAACCTSEQNO))
				|| ctx.getData(Constants.IN_MEDIAACCTSEQNO)==null)
			procedureMap.put(Constants.IN_MEDIAACCTSEQNO, Constants.PE_NUM_ONE);                //介质下挂帐户序号; 从1开始，默认1
		else
			procedureMap.put(Constants.IN_MEDIAACCTSEQNO, ctx.getData(Constants.IN_MEDIAACCTSEQNO));  //介质下挂帐户序号; 从1开始，默认1
		procedureMap.put(Constants.IN_FUNDTYPARRAYSIZE, Constants.PE_NUM_ZERO);                 //资金类型数组长度
		procedureMap.put(Constants.IN_FUNDTYPCD, ctx.getData(Constants.IN_FUNDTYPCD));			//资金类型
		if(Constants.TRS_FUND_TYP_EL.equals(ctx.getData(Constants.IN_FUNDTYPCD)))
			procedureMap.put(Constants.IN_FUNDTYPDTLCD, Constants.FUND_TYP_DTL_INTER);    		//资金类型明细
		if(Constants.TRS_FUND_TYP_CASH.equals(ctx.getData(Constants.IN_FUNDTYPCD)))
			procedureMap.put(Constants.IN_FUNDTYPDTLCD, Constants.FUND_TYP_DTL_ALL);    		//资金类型明细

		procedureMap.put(Constants.IN_CLEARCATCD, Constants.CLEARCATCD_IMED);                  	//清算类别
		procedureMap.put(Constants.IN_NBROFITEMS, Constants.PE_ONE);                     		//资金类型顺序号
		procedureMap.put(Constants.IN_FUNDSAMT, ctx.getData(Constants.PE_TRAN_AMT));			//资金金额，参数获得
		procedureMap.put(Constants.IN_BALCATARRAYSIZE, Constants.PE_NUM_ZERO);                  //余额类型数组长度
		procedureMap.put(Constants.IN_BALCATCD, Constants.BALCATCD_NOTE);                       //余额大类,固定为"NOTE":本金
		procedureMap.put(Constants.IN_BALTYPCD,Constants.BALTYPCD_BAL);                         //余额类型 固定为"BAL":
		procedureMap.put(Constants.IN_BALAMT, ctx.getData(Constants.PE_TRAN_AMT));  			//交易金额，参数获得
		procedureMap.put(Constants.IN_ACCTNBR,ctx.getData(Constants.IN_ACCTNBR));  			    //卡号，参数获得
		if(Constants.TRS_TYPE_GLR.equals(ctx.getData(Constants.IN_RTXNTYPCD))
				||Constants.TRS_TYPE_DEP.equals(ctx.getData(Constants.IN_RTXNTYPCD))
				||Constants.TRS_TYPE_DDEP.equals(ctx.getData(Constants.IN_RTXNTYPCD)))
			procedureMap.put(Constants.IN_DEPFLAG, Constants.TRS_FLAG_DEP);						//出入账标识:0:出  1:入
		else{
			if(Constants.TRS_TYPE_GLD.equals(ctx.getData(Constants.IN_RTXNTYPCD))
					||Constants.TRS_TYPE_WTH.equals(ctx.getData(Constants.IN_RTXNTYPCD))
					||Constants.TRS_TYPE_DWTH.equals(ctx.getData(Constants.IN_RTXNTYPCD)))
				procedureMap.put(Constants.IN_DEPFLAG, Constants.TRS_FLAG_WTH);					//出入账标识:0:出  1:入
		}
		procedureMap.put(Constants.IN_PARENTACCTNBR, ctx.getData(Constants.IN_PARENTACCTNBR));  //父账号:多笔分录交易和冲正交易，必填
		procedureMap.put(Constants.IN_PARENTRTXNNBR, ctx.getData(Constants.IN_PARENTRTXNNBR));  //父交易号:多笔分录交易和冲正交易，必填
		procedureMap.put(Constants.IN_PARENTRTXNTYPCD, Constants.PE_NULL);                      //父交易代码:多笔分录交易和冲正交易，必填
		procedureMap.put(Constants.IN_CASHBOXNBR, cashBoxNbr);       							//钱箱号， 数据库配置参数
		procedureMap.put(Constants.IN_RTXNTYPCD, ctx.getData(Constants.IN_RTXNTYPCD));          //交易码判断
		procedureMap.put(Constants.IN_RTXNNBR,  ctx.getData(Constants.IN_RTXNNBR));				//交易号 :冲正交易，必填
		procedureMap.put(Constants.IN_INITIALCARDNBR, ctx.getData(Constants.IN_INITIALCARDNBR));//介质号/卡号
		procedureMap.put(Constants.IN_TRACENBR,ctx.getData(Constants.PE_JOURNAL_NO));  			//交易流水号
		procedureMap.put(Constants.IN_EFFDATE, EFFDATE);                            			//有效日期
		procedureMap.put(Constants.IN_ORIGPERSNBR, ctx.getData(Constants.IN_ORIGPERSNBR));      //交易柜员号，数据库配置参数
		procedureMap.put(Constants.IN_ORIGNTWKNODENBR, ctx.getData(Constants.IN_ORIGNTWKNODENBR)); //交易站点
		procedureMap.put(Constants.IN_RTXNSTATCD, ctx.getData(Constants.IN_RTXNSTATCD));        //交易状态,正常、冲正：'C'	抹帐:'EC'
		procedureMap.put(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.IN_RTXNSOURCECD)); 	//交易的来源
		procedureMap.put(Constants.IN_POSTYN, Constants.PE_Y);                                 	//过帐标志:'Y'
		procedureMap.put(Constants.IN_COMMITYN,  Constants.PE_N);                               //COMMIT标志:前端控制事物时为'N'	Y：commit	N：Not Commit
		if(Constants.CARD_ACCT_FLAG_2_STR.equals(String.valueOf(procedureMap.get(Constants.IN_CARDACCTFLAG)))){
			String pin =(String) ctx.getString(Constants.IN_PIN);
			String encryptPwd = generateEntryPwd(ctx,pin);//调用3DES+MD5算法生成密文
			procedureMap.put(Constants.IN_PIN,encryptPwd);//密码
		}
		procedureMap.put(Constants.IN_TRANDATETIME,trdt.toString());  							//交易传输日期
		procedureMap.put(Constants.IN_LOCALDATETIME,trdt.toString());  							//本地日期
		if(Constants.ISO8583.equals(type8583))
			procedureMap.put(Constants.IN_ACQINSTID,ctx.getData(Constants.ISO8583_ACQCODE));  	//受理机构号
		if(Constants.FIX8583.equals(type8583))
			procedureMap.put(Constants.IN_ACQINSTID,ctx.getData(Constants.FIX8583_ACQU));  		//受理机构号

		if(Constants.ISO8583.equals(type8583))
			procedureMap.put(Constants.IN_FWDORGCD,ctx.getData(Constants.ISO8583_FORWARDCODE));	//发送机构号
		if(Constants.FIX8583.equals(type8583))
			procedureMap.put(Constants.IN_FWDORGCD,ctx.getData(Constants.FIX8583_FORW));  		//发送机构号

		procedureMap.put(Constants.IN_ORIGTRACKNBR,ctx.getData(Constants.IN_ORIGTRACKNBR));		//原平台流水号
		procedureMap.put(Constants.IN_ORIGPOSTDATE,ctx.getData(Constants.IN_ORIGPOSTDATE));		//原始交易日期
		if(Constants.CARD_ACCT_FLAG_2_STR.equals(String.valueOf(procedureMap.get(Constants.IN_CARDACCTFLAG)))){
			if(Constants.ISO8583.equals(type8583))
				procedureMap.put(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));//二磁信息
			if(Constants.FIX8583.equals(type8583))
				procedureMap.put(Constants.IN_TRACK2,ctx.getData(Constants.FIX8583_TRACK2));  	//二磁信息
		}
		procedureMap.put(Constants.IN_ONLINEYN, Constants.PE_Y);                       			//是否联机标志
		procedureMap.put(Constants.IN_AVAILMETHCD,ctx.getData(Constants.IN_AVAILMETHCD));   	//可用方式:'GMT'：柜面通  ，'HVPS':大额，'BEPS':小额	，'LCH':同城，'TIPS':财税，'YLQZ':银联前置，'JCK':锦城卡前置，'ATMP':ATMP
		procedureMap.put(Constants.IN_GENTOTAL, Constants.PE_ZERO);                             //
		procedureMap.put(Constants.IN_REGDYN,  Constants.PE_N);                                 //REGDYN：
		procedureMap.put(Constants.IN_IS_VOUCHER,  Constants.PE_Y);                            	//是否凭证交易Y/N
		procedureMap.put(Constants.IN_DEBUGYN,  Constants.PE_N);                                //DEBUGYN:Y：Debug  N：Not Debug
		procedureMap.put(Constants.IN_CASHANALYSISCD, null);                        			//现金分析号,现金交易时必输
		procedureMap.put(Constants.IN_VOUHTYPE, null);                              			//凭证种类，凭证交易时必输
		procedureMap.put(Constants.IN_VOUHNO, null);                                			//凭证号，凭证交易时必输
		procedureMap.put(Constants.IN_GENRTXNARRAYSIZE, null);                     				//子交易数组长度
		procedureMap.put(Constants.IN_GENRTXNTYPCD, null);                         				//子交易代码
		procedureMap.put(Constants.IN_GENRTXNAMT, null);                           				//子交易金额
		procedureMap.put(Constants.IN_GENRTXNPROCESSYN, null);                     				//子交易处理标志
		procedureMap.put(Constants.IN_GENTOTALF,null);                            				//子交易累计标志
		procedureMap.put(Constants.IN_REVFLAG, ctx.getData(Constants.IN_REVFLAG));              //冲正标志:0：正常   1：冲正 	2：抹帐
		procedureMap.put(Constants.IN_TRACKYN, ctx.getData(Constants.CHECK_TRACK2));  //是否校验二磁
		procedureMap.put(Constants.IN_PINYN, ctx.getData(Constants.CHECK_PIN));     //是否校验密码
		procedureMap.put(Constants.IN_TXNSEQNBR,null);                             //交易序号
		procedureMap.put(Constants.IN_IDENINFO,null);                               //持卡人证件
		procedureMap.put(Constants.IN_EXPIREDATE,null);                             //卡有效期
		if(Constants.PE_Y.equals(ctx.getData(Constants.CHECK_IDEN))){
			procedureMap.put(Constants.IN_IDENINFO,ctx.getData(Constants.PE_FIELD_61));//持卡人证件
			String idenInfo=null;
			if(String.valueOf(ctx.getData(Constants.PE_FIELD_61)).indexOf(Constants.PE_CUP)==0){
				 idenInfo=fill(String.valueOf(ctx.getData(Constants.PE_FIELD_61)),2,Constants.LEFT,Constants.PE_SPACE);
				 idenInfo=fill(idenInfo,22,Constants.LEFT,Constants.PE_ZERO);
				 if(Constants.ISO8583.equals(type8583))
					 ctx.setData(Constants.ISO8583_IDENNUMBE,idenInfo);
				 else
					 ctx.setData(Constants.FIX8583_IDEN,idenInfo);

			}
			if(Constants.ISO8583.equals(type8583))
				procedureMap.put(Constants.IN_EXPIREDATE,ctx.getData(Constants.ISO8583_EXPDAT));	//卡有效期
			else
			    procedureMap.put(Constants.IN_EXPIREDATE,ctx.getData(Constants.FIX8583_EXPI));	//卡有效期
		}
		return procedureMap;
	}

	/**
	 * @author xujin
	 * @desc   备用函数：多次调用deposittxn时，生成组合的参数集
	 * @date    2011.02.21
	 */
	@SuppressWarnings("unchecked")
	public static Map getCombinationMap(Map param1,Map param2,Map param3,Map param4){
		Iterator it= param1.entrySet().iterator();
		Map procedureMap= new HashMap();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			StringBuffer value = new StringBuffer();
			String key = entry.getKey().toString();
			if(entry.getValue()!=null)
				value.append(entry.getValue());
			else
				value.append("");
			value.append(";");

			if(param2!=null){
				if(param2.get(key)!=null)
					value.append(param2.get(key));
				else
					value.append("");
				value.append(";");
			}
			if(param3!=null){
				if(param3.get(key)!=null)
					value.append(param3.get(key));
				else
					value.append("");
				value.append(";");
			}
			if(param4!=null){
				if(param4.get(key)!=null)
					value.append(param4.get(key));
				else
					value.append("");
				value.append(";");
			}
			procedureMap.put(key, value);
		}
		return procedureMap;
	}

	/**
	 * 验证密码初始化参数
	 * @param ctx 传入数据对象
	 */
	@SuppressWarnings("unchecked")
	public static Map InitCheckPin(Context ctx) {
		String encryptPwd =generateEntryPwd(ctx,String.valueOf(ctx.getData(Constants.IN_PIN))); 	//调用3DES+MD5算法生成密文
		Map paramMap = new HashMap();
		paramMap.put(Constants.IN_PAN, ctx.getData(Constants.PE_ACC_NO));   //交易账号
		paramMap.put(Constants.IN_RTXNSOURCE,ctx.getData(Constants.IN_RTXNSOURCECD));//交易的来源
		paramMap.put(Constants.IN_PIN, encryptPwd);   //交易密码
		int persNbr = Integer.valueOf((String)ctx.getData(Constants.IN_ORIGPERSNBR));//柜员号
		paramMap.put(Constants.IN_TRLLERNBR, persNbr);
		int cashBoxNbr = Integer.valueOf((String)ctx.getData(Constants.IN_CASHBOXNBR));	//钱箱号
		paramMap.put(Constants.IN_CASHBOXNBR,cashBoxNbr);
        return paramMap;
	}

	/**
	 * @author xujin
	 * @since 2012.05.31
	 * @description:预授权交易冻结金额为预授权金额的115%
	 * @param preAuthAmt 预授权金额
	 * @return
	 */
	public static BigDecimal initHoldAmt(String preAuthAmt){
		BigDecimal preAmt = Util.getAmount(preAuthAmt);//预授权金额
		BigDecimal preAuthProp=InitData4Dzip.getPreAuthCompProp();
		return preAmt.multiply(preAuthProp).setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @author xujin
	 * @since 2012.05.31
	 * @description:预授权交易冻结金额为预授权金额的115%
	 * @param preAuthAmt 预授权金额
	 * @param ctx 传入数据对象
	 * @return
	 */
	public static void initHoldAmt(String preAuthAmt,Context ctx){
		ctx.setData(Constants.PE_PRE_AMT,initHoldAmt(preAuthAmt));    //预授权金额
	}

	/**
	 * @author xujin
	 * @since 2012.05.31
	 * @description:预授权交易时记账金额不能超出115%并获得预授权交易冻结金额
	 * @param preAuthAmt 预授权金额
	 * @param ctx 传入数据对象
	 * @return
	 */
	public static void initPreAuthTranAmtAndHoldAmt(String preAuthAmt,Context ctx){
		BigDecimal preAmt=initHoldAmt(preAuthAmt);//预授权金额
		BigDecimal tranAmt = Util.getAmount(String.valueOf(ctx.getData(Constants.PE_TRAN_AMT)));//记账金额
		if(tranAmt.compareTo(preAmt) > 0){  //如果记账金额大于预授权金额的1.15倍就按预授权金额的1.15倍记账
			ctx.setData(Constants.PE_TRAN_AMT,preAmt);
		}
		ctx.setData(Constants.PE_PRE_AMT,preAmt);    //预授权金额
	}

	/**
	 * @author xujin
	 * @since 2011.01.11
	 * @description:预授权接口
	 * @param ctx 传入数据对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map initPreAuth(Context ctx, Map  procedureMap,String type8583) {
		procedureMap.put(Constants.IN_HOLDFALG, ctx.getData(Constants.IN_HOLDFALG));  		//冻结解冻标识
		procedureMap.put(Constants.IN_MEDIUMID, ctx.getData(Constants.PE_ACC_NO));  	//介质号
		procedureMap.put(Constants.IN_EFFDATETIME,ctx.getData(Constants.PE_POST_DATE));     //冻结交易发生日期
		if(Constants.UNFREEZE.equals(ctx.getData(Constants.IN_HOLDFALG))){
			BigDecimal optnbr = new BigDecimal((String)ctx.getData(Constants.IN_OPTNBR));
			procedureMap.put(Constants.IN_OPTNBR, optnbr);                		      		//冻结编号
		}
		BigDecimal  amt = new BigDecimal(ctx.getString(Constants.PE_PRE_AMT));
		procedureMap.put(Constants.IN_AMT, amt);		   									// 冻结金额
		Date date = Util.getDate((String)ctx.getData(Constants.PE_POST_DATE), "yyyyMMdd") ;
		procedureMap.put(Constants.IN_STRDATE, date);
		if(Constants.PE_ONE.equals(ctx.getData(Constants.IN_HOLDFALG)))
			procedureMap.put(Constants.IN_MATDATE,date);
		else{
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, InitData4Dzip.getMaxHoldDays());
			Date matdate = c.getTime();
			procedureMap.put(Constants.IN_MATDATE,matdate);
		}                              		//冻结到期日期
		BigDecimal  branchNbr = new BigDecimal((String) ctx.getData(Constants.IN_ORGNBR));
		procedureMap.put(Constants.IN_BRANCHNBR, branchNbr);                           		//机构号
		BigDecimal  tellerNo = new BigDecimal((String) ctx.getData(Constants.IN_ORIGPERSNBR));
		procedureMap.put(Constants.IN_TELLERNO, tellerNo);                             		//柜员号
		procedureMap.put(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.IN_RTXNSOURCECD));//交易来源
		if(Constants.ISO8583.equals(type8583))
			procedureMap.put(Constants.IN_TRACK, ctx.getData(Constants.ISO8583_TRACK2_DATA));
		else
		    procedureMap.put(Constants.IN_TRACK, ctx.getData(Constants.FIX8583_TRACK2));   //磁道
		String pin =ctx.getString(Constants.IN_PIN);
		String encryptPwd = generateEntryPwd(ctx,pin); 									    //调用3DES+MD5算法生成密文
		procedureMap.put(Constants.IN_PIN,encryptPwd);  									//密码
		procedureMap.put(Constants.IN_PREFLAG, ctx.getData(Constants.IN_PREFLAG));     		//预授权标记
		procedureMap.put(Constants.IN_TRACKYN, ctx.getData(Constants.CHECK_TRACK2));        //是否校验二磁
		procedureMap.put(Constants.IN_PINYN, ctx.getData(Constants.CHECK_PIN));     		//是否校验密码
		procedureMap.put(Constants.IN_TRACENBR,ctx.getData(Constants.PE_JOURNAL_NO));
		procedureMap.put(Constants.IN_IDENINFO,null);                               //持卡人证件
		procedureMap.put(Constants.IN_EXPIREDATE,null);                             //卡有效期
		if(Constants.PE_Y.equals(ctx.getData(Constants.CHECK_IDEN))){
			procedureMap.put(Constants.IN_IDENINFO,ctx.getData(Constants.PE_FIELD_61));//持卡人证件
			String idenInfo=null;
			if(String.valueOf(ctx.getData(Constants.PE_FIELD_61)).indexOf(Constants.PE_CUP)==0){
				 idenInfo=fill(String.valueOf(ctx.getData(Constants.PE_FIELD_61)),2,Constants.LEFT,Constants.PE_SPACE);
				 idenInfo=fill(idenInfo,22,Constants.LEFT,Constants.PE_ZERO);
				 if(Constants.ISO8583.equals(type8583))
					 ctx.setData(Constants.ISO8583_IDENNUMBE,idenInfo);
				 else
					 ctx.setData(Constants.FIX8583_IDEN,idenInfo);

			}
			if(Constants.ISO8583.equals(type8583))
				procedureMap.put(Constants.IN_EXPIREDATE,ctx.getData(Constants.ISO8583_EXPDAT));	//卡有效期
			else
			    procedureMap.put(Constants.IN_EXPIREDATE,ctx.getData(Constants.FIX8583_EXPI));	//卡有效期
		}
		return procedureMap;
	}
	/**
	 * @author xujin
	 * @since 2011.08.07
	 * @description:预授权接口
	 * @param paramsMap 传入数据对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map initBatchPreAuth(Map paramsMap, Map  procedureMap) {
		procedureMap.put(Constants.IN_HOLDFALG, paramsMap.get(Constants.IN_HOLDFALG)); //冻结解冻标识
		procedureMap.put(Constants.IN_MEDIUMID, paramsMap.get(Constants.IN_ACCTNBR));  //介质号
		procedureMap.put(Constants.IN_EFFDATETIME,paramsMap.get(Constants.IN_EFFDATE));//冻结交易发生日期
		if(Constants.UNFREEZE.equals(paramsMap.get(Constants.IN_HOLDFALG))){
			BigDecimal optnbr = new BigDecimal(String.valueOf(paramsMap.get(Constants.IN_OPTNBR)));
			procedureMap.put(Constants.IN_OPTNBR, optnbr);                		      		//冻结编号
		}
		BigDecimal  amt = Util.getAmount(String.valueOf(paramsMap.get(Constants.PE_PRE_AMT)));
		procedureMap.put(Constants.IN_AMT, amt);		   									// 冻结金额
		Date date = Util.getDate((String)paramsMap.get(Constants.IN_EFFDATE), "yyyyMMdd") ;
		procedureMap.put(Constants.IN_STRDATE, date);
		if(Constants.PE_ONE.equals(paramsMap.get(Constants.IN_HOLDFALG)))
			procedureMap.put(Constants.IN_MATDATE,date);
		else{
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, InitData4Dzip.getMaxHoldDays());
			Date matdate = c.getTime();
			procedureMap.put(Constants.IN_MATDATE,matdate);
		}
		BigDecimal  branchNbr = new BigDecimal((String) paramsMap.get(Constants.IN_ORGNBR));
		procedureMap.put(Constants.IN_BRANCHNBR, branchNbr);                           		//机构号
		BigDecimal  tellerNo = new BigDecimal((String)paramsMap.get(Constants.IN_ORIGPERSNBR));
		procedureMap.put(Constants.IN_TELLERNO, tellerNo);                             		//柜员号
		procedureMap.put(Constants.IN_RTXNSOURCECD,paramsMap.get(Constants.IN_RTXNSOURCECD));//交易来源
		procedureMap.put(Constants.IN_TRACENBR,paramsMap.get(Constants.IN_TRACENBR));
		procedureMap.put(Constants.IN_ORIGPOSTDATE,paramsMap.get(Constants.IN_ORIGPOSTDATE));
		return procedureMap;
	}
	/**
	 * @author shengzt
	 * @desc   在调用InqJournalInfo过程之前，对传入参数进行初始化。
	 * @date    2010.09.03
	 */
	@SuppressWarnings("unchecked")
	public static Map initInqJournalInfo(Context ctx, Map procedureMap)	{
		procedureMap.put(Constants.IN_CASHBOXNBR, ctx.getData(Constants.CASHBOXNBR));     	    //钱箱号
		procedureMap.put(Constants.IN_RTXNCATCD, ctx.getData(Constants.RTXNCATCD));     	    //业务代理
		procedureMap.put(Constants.IN_SYSSEQNO, (ctx.getData(Constants.PE_SYSSEQNO)==null||Constants.PE_NULL.equals(ctx.getData(Constants.PE_SYSSEQNO)))?null:ctx.getData(Constants.PE_SYSSEQNO));   //平台流水号
		procedureMap.put(Constants.IN_TRANDATE, ctx.getData(Constants.PE_TRANDATE));          	//交易日期
		procedureMap.put(Constants.IN_CHANNID, ctx.getData(Constants.CHANNID));               	//渠道号
		procedureMap.put(Constants.IN_TRANSTAT, ctx.getData(Constants.TRANSTAT));             	//交易状态
		procedureMap.put(Constants.IN_CRDB, ctx.getData(Constants.PE_CREDIT_DEBIT));        	//交易类型
		procedureMap.put(Constants.IN_TRANAMTSTART, ctx.getData(Constants.PE_TRANAMT_START));  	//交易起始金额
		procedureMap.put(Constants.IN_TRANAMTEND, ctx.getData(Constants.PE_TRANAMT_END));     	//交易结束金额
		procedureMap.put(Constants.IN_TRANCD, ctx.getData(Constants.TRANCD));     	//交易码
		return procedureMap;
	}

	/**
	 * @author shengzt
	 * @desc   在调用InqJournalHistInfo过程之前，对传入参数进行初始化。
	 * @date   2010.09.03
	 */
	@SuppressWarnings("unchecked")
	public static Map initInqJournalHistInfo(Context ctx, Map procedureMap)	{
		procedureMap.put(Constants.IN_CASHBOXNBR, ctx.getData(Constants.CASHBOXNBR));     	    //钱箱号
		procedureMap.put(Constants.IN_RTXNCATCD, ctx.getData(Constants.RTXNCATCD));     	    //业务代理
		procedureMap.put(Constants.IN_SYSSEQNO, (ctx.getData(Constants.PE_SYSSEQNO)==null||Constants.PE_NULL.equals(ctx.getData(Constants.PE_SYSSEQNO)))?null:ctx.getData(Constants.PE_SYSSEQNO));   //平台流水号
		procedureMap.put(Constants.IN_TRANDATESTART, ctx.getData(Constants.PE_TRANDATE_START)); //交易开始日期
		procedureMap.put(Constants.IN_TRANDATEEND, ctx.getData(Constants.PE_TRANDATE_END));     //交易结束日期
		procedureMap.put(Constants.IN_CHANNID, ctx.getData(Constants.CHANNID));              	//渠道号
		procedureMap.put(Constants.IN_TRANSTAT, ctx.getData(Constants.TRANSTAT));            	//交易状态
		procedureMap.put(Constants.IN_CRDB, ctx.getData(Constants.PE_CREDIT_DEBIT));            //交易类型
		procedureMap.put(Constants.IN_TRANAMTSTART, ctx.getData(Constants.PE_TRANAMT_START));   //交易起始金额
		procedureMap.put(Constants.IN_TRANAMTEND, ctx.getData(Constants.PE_TRANAMT_END));       //交易结束金额
		return procedureMap;
	}

	/**
	 * @author xujin
	 * @desc   在调用QueryJournalBatch过程之前，对传入参数进行初始化。
	 * @date    2011.04.03
	 */
	@SuppressWarnings("unchecked")
	public static Map initQueryJournalBatch(Context ctx, Map procedureMap)	{
		procedureMap.put(Constants.IN_RTXNCATCD,ctx.getData(Constants.RTXNCATCD));              //业务性质
		procedureMap.put(Constants.IN_CRDB, ctx.getData(Constants.PE_CREDIT_DEBIT));        	//交易类型
		if(Constants.ALLSTAT.equals((String)ctx.getData(Constants.TRANSTAT)))
			procedureMap.put(Constants.IN_TRANSTAT,null);
		else
		    procedureMap.put(Constants.IN_TRANSTAT, ctx.getData(Constants.TRANSTAT));             	//交易状态
		procedureMap.put(Constants.IN_CHANNID, ctx.getData(Constants.CHANNID));               	//渠道号
		return procedureMap;
	}
	/**
	 * @author xujin
	 * @desc   对通存通兑流水传入参数进行初始化。
	 * @date    2011.04.03
	 */
	@SuppressWarnings("unchecked")
	public static Map initDepAndWthJournal(Context ctx, Map procedureMap)	{
		procedureMap.put(Constants.IN_RTXNCATCD,ctx.getData(Constants.RTXNCATCD));        //业务性质
		procedureMap.put(Constants.IN_POSTDATE, ctx.getData(Constants.POSTDATE));         //记账日期
		procedureMap.put(Constants.IN_TRANSTAT, Constants.PE_ZERO);             	      //交易状态
		return procedureMap;
	}
	/**
	 * @author xujin
	 * @desc   对修改密码传入参数进行初始化。
	 * @date    2011.04.03
	 */
	@SuppressWarnings("unchecked")
	public static Map initModifyPwd4ATM(Context ctx, Map procedureMap)	{
		/*填充核心存储过程的参数*/
		procedureMap.put(Constants.IN_PAN, ctx.getData(Constants.ISO8583_ACCTNO));          //传入卡号
		procedureMap.put(Constants.IN_Track2, ctx.getData(Constants.ISO8583_TRACK2_DATA));  //传入二磁
		procedureMap.put(Constants.IN_CHGORRESET, Constants.PE_ONE);         				//更改或者重置。1：更改；2：重置
		procedureMap.put(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.IN_RTXNSOURCECD));// 填充交易来源;
		String encryptPwd=null;
		if(ctx.getData(Constants.IN_PIN)!=null
				&&!Constants.PE_NULL.equals((String)ctx.getData(Constants.IN_PIN))){			//密码域不为空时
			 encryptPwd =generateEntryPwd(ctx,(String)ctx.getData(Constants.IN_PIN));  //调用3DES+MD5算法生成密文
			 procedureMap.put(Constants.IN_OLDPASSWORD, encryptPwd); //旧密码
		}
		if(ctx.getData(Constants.IN_NEWPASSWORD)!=null
				&&!Constants.PE_NULL.equals((String)ctx.getData(Constants.IN_NEWPASSWORD))){			//密码域不为空时
			 encryptPwd =generateEntryPwd(ctx,(String)ctx.getData(Constants.IN_NEWPASSWORD));  //调用3DES+MD5算法生成密文
			 procedureMap.put(Constants.IN_NEWPASSWORD, encryptPwd); //新密码
		}
		return procedureMap;
	}

	/**
	 * 通过F33后四位是0344判断为境外交易
	 * @return
	 */
	public static void initOutSideFlag(final Context ctx){
		ctx.setData(Constants.PE_OUTSIDEFLAG,Constants.PE_ZERO);//境内
		String regionCd=(String)ctx.getData(Constants.PE_FOW_ORG_CD);
		regionCd=regionCd.substring(regionCd.length()-4, regionCd.length());
		if(Constants.FWDORGCD_0344.equals(regionCd))
			ctx.setData(Constants.PE_OUTSIDEFLAG,Constants.PE_ONE);//境外
	}

	/**
	 * @author xujin
	 * 对F22初始化验证密码标识和磁道标识
	 * 对F48初始化关联业务类型标识(AS+AO用法)
	 * 对F60初始化终端类型,电子商务标识,交易发起方式,交易介质
	 * 对F61初始化身份证件标识
	 * @param ctx
	 * @throws PeException
	 */
	public static void initFieldsValue(final Context ctx){
		ctx.setData(Constants.CHECK_IDEN,  Constants.PE_N);//校验身份证件标识赋值N(N:不验证;Y验证)
		ctx.setData(Constants.CHECK_TRACK2,  Constants.PE_N);//校验二磁标识赋值N(N:不验证;Y验证)
		ctx.setData(Constants.CHECK_PIN,  Constants.PE_N);//校验密码标识赋值N(N:不验证;Y验证)
		//关联业务类型00(AS+AO用法<01:01:有卡自助消费;02:02:无卡自助消费;
		//03:03:互联网消费;04:04:订购;05:05:柜面无卡存款;06:06:自助存款;
		//07:07:自助转账;08::08互联网转账;09:09:代收;10:10:代付;11:11:预付卡;
		//13:13:自主识别无卡自助账户验证;14:14:自主识别无卡自助委托类;>)
		ctx.setData(Constants.IN_ASSBUSTYP,  Constants.PE_OK);
		ctx.setData(Constants.IN_CHANNELTYPCD,  Constants.PE_OK);//终端类型
		ctx.setData(Constants.CHECK_ECI,  Constants.PE_NULL);//电子商务标识
		ctx.setData(Constants.IN_TRANLAUNCHWAYCD,  Constants.PE_ZERO);//交易发起方式
		ctx.setData(Constants.IN_TRANMEDIUMCD, Constants.PE_ONE);//交易介质,默认磁卡
		ctx.setData(Constants.PE_AM,Constants.PE_MINUSONE);
		ctx.setData(Constants.CHECK_AM,Constants.PE_N);
		if(ctx.getData(Constants.ISO8583_REACODE)!=null){//第60域
			 String reacode = (String)ctx.getData(Constants.ISO8583_REACODE);
			 if(reacode.length()>10)
				 ctx.setData(Constants.IN_CHANNELTYPCD,reacode.substring(8,10));//第60域终端类型位置9-10
			 else
				 ctx.setData(Constants.IN_CHANNELTYPCD,Constants.PE_OK);
			 if(reacode.length()>14)
				 ctx.setData(Constants.CHECK_ECI,reacode.substring(12,14));//第60域位置电子商务类型13-14
			 if(reacode.length()>23)
				 ctx.setData(Constants.IN_TRANLAUNCHWAYCD, reacode.substring(22,23));//第60域位置交易发起方式23
			 if(reacode.length()>24)
				 ctx.setData(Constants.IN_TRANMEDIUMCD, reacode.substring(23,24));//第60域位置交易介质24
		}

		String serviceCode = (String)ctx.getData(Constants.ISO8583_SERENTRYMODE);  //第22域
		if(serviceCode!=null&&!Constants.PE_NULL.equals(serviceCode)){       //服务点输入方式
			 String srvc12=serviceCode.substring(0, 2);

			 if((Constants.PE_02.equals(srvc12) || Constants.PE_90.equals(srvc12))
					 //22#05不验证2磁
					 //|| Constants.PE_05.equals(srvc12))//添加IC卡二磁验证
					 &&ctx.getData(Constants.ISO8583_TRACK2_DATA)!= null)//02表示磁条输入
			 	 ctx.setData(Constants.CHECK_TRACK2, Constants.PE_Y); //如果22域中标识是磁条输入，则可以校验二磁
			 if(Constants.PE_ONE.equals(serviceCode.substring(2, 3))
					 &&ctx.getData(Constants.ISO8583_PINDATA)!= null)
				 ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);//如果22域中标识是密码必填，则可以校验密码
		}
		//不验证密码设置密码域为null,这样防止加解密码
		if(Constants.PE_N.equals(ctx.getString(Constants.CHECK_PIN)))
			ctx.setData(Constants.ISO8583_PINDATA,null);

		String addDataPri=String.valueOf(ctx.getData(Constants.ISO8583_ADDDATAPRI));//第48域
		if(addDataPri!=null&&addDataPri.indexOf(Constants.PE_AS)!=-1&&addDataPri.indexOf(Constants.PE_AO)!=-1){//关联业务类型AO
			int flag=addDataPri.indexOf(Constants.PE_AO);
			ctx.setData(Constants.IN_ASSBUSTYP,addDataPri.substring(flag+5,flag+7));
		}

		String field61="";
		int AM=-1;
		if(ctx.getData(Constants.ISO8583_IDENNUMBE)!=null){//第61域AM用法
			 field61=String.valueOf(ctx.getData(Constants.ISO8583_IDENNUMBE));
			 AM=field61.indexOf(Constants.PE_AM);
			 ctx.setData(Constants.PE_AM,String.valueOf(AM));
			 if(!Constants.PE_MINUSONE.equals(String.valueOf(AM))){
				 if(field61.substring(AM+2, AM+18).indexOf(Constants.PE_ONE)!=-1){
					 ctx.setData(Constants.CHECK_IDEN,Constants.PE_Y);
				     ctx.setData(Constants.CHECK_AM,Constants.PE_Y);
				 }
			 }
			 if(field61.indexOf(Constants.PE_NM)!=-1)
				 ctx.setData(Constants.CHECK_IDEN,Constants.PE_Y);
		}
	}

	@SuppressWarnings("unchecked")
	public static  Map initOfflinePreAuthComp(Map<String, Object>  paramsMap){
		Map journalMap = new HashMap(); // 记录交易流水的参数Map
		journalMap.put(Constants.IN_OPTNBR,String.valueOf(paramsMap.get(Constants.PE_OPTNBR)));
		journalMap.put(Constants.IN_RTXNREASONCD,Constants.rtxnseace_1005);
		journalMap.put(Constants.IN_PREFLAG,Constants.PE_ONE);//即冻结或解冻又记账
		journalMap.put(Constants.IN_HOLDFALG,Constants.UNFREEZE);//解冻
		System.out.println("===================>"+paramsMap.get(Constants.PE_PRE_AMT));
		journalMap.put(Constants.PE_PRE_AMT, paramsMap.get(Constants.PE_PRE_AMT));//金额
		journalMap.put(Constants.PE_RLTSEQNO, paramsMap.get(Constants.PE_RLTSEQNO));
		journalMap.put(Constants.PE_RTXNCATCD, Constants.PE_03);    //填充渠道
		journalMap.put(Constants.PE_OUT_TRANCD, Constants.TRANCD_02200006);

		journalMap.put(Constants.IN_ORIGPOSTDATE, paramsMap.get(Constants.PE_POST_DATE));   //填充原来交易日期
		journalMap.put(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);                       //填充交易状态：C
		journalMap.put(Constants.IN_ORIGTRACKNBR, null);
		journalMap.put(Constants.IN_PARENTACCTNBR, null);       	                            //填充父账号
		journalMap.put(Constants.IN_PARENTRTXNNBR, null);       	                            //填充父交易号
		journalMap.put(Constants.IN_PARENTRTXNTYPCD,null);
		journalMap.put(Constants.IN_INITIALCARDNBR,null);
		journalMap.put(Constants.IN_TRACENBR,paramsMap.get(Constants.PE_JOURNAL_NO));           //填充平台流水号
		journalMap.put(Constants.IN_POSTYN,Constants.PE_Y);
		journalMap.put(Constants.IN_COMMITYN,Constants.PE_N);
		journalMap.put(Constants.IN_DEBUGYN,Constants.PE_N);
		journalMap.put(Constants.IN_CASHANALYSISCD,null);
	    journalMap.put(Constants.IN_RTXNREASONCD,Constants.PE_NULL);
		journalMap.put(Constants.IN_ACCTNBR, paramsMap.get(Constants.PE_ACC_NO));       	     //填充账号
		journalMap.put(Constants.IN_NBROFITEMS, Constants.PE_ONE);       	                     //填充资金类型顺序号
		journalMap.put(Constants.IN_CLEARCATCD, Constants.CLEARCATCD_IMED);      //填充清算类别
		journalMap.put(Constants.IN_BALCATCD, Constants.BALCATCD_NOTE);  //余额大类
		journalMap.put(Constants.IN_BALTYPCD, Constants.BALTYPCD_BAL);    //余额类型
		journalMap.put(Constants.IN_BALCATARRAYSIZE,Constants.PE_NUM_ZERO);                      //余额类型数组长度
		journalMap.put(Constants.IN_FUNDTYPARRAYSIZE,Constants.PE_NUM_ZERO);                      //资金类型数组长度
		journalMap.put(Constants.IN_FUNDSAMT,paramsMap.get(Constants.PE_TRAN_AMT));              //填充资金金额
		journalMap.put(Constants.IN_BALAMT,paramsMap.get(Constants.PE_TRAN_AMT));                //填充本金发生额
		journalMap.put(Constants.PE_FeeYN,Constants.BATCH_Fee_N);//标记不是手续费交易
		journalMap.put(Constants.IN_FUNDTYPCD,Constants.TRS_FUND_TYP_EL);
		journalMap.put(Constants.IN_FUNDTYPDTLCD,Constants.FUND_TYP_DTL_INTER);
		journalMap.put(Constants.IN_RTXNSOURCECD,"YPOS");
		journalMap.put(Constants.IN_ORGNBR, InitData4Dzip.getYLOrgNbr()); //机构号数据库配置参数
		journalMap.put(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr()); // 钱箱号,数据库配置参数
		journalMap.put(Constants.IN_ORIGPERSNBR, InitData4Dzip.getYLPersNbr()); // 柜员号，数据库配置参数
		journalMap.put(Constants.IN_ORIGNTWKNODENBR, InitData4Dzip.getJCNtwkNodeNbr()); // 交易站点号，数据库配置参数
		journalMap.put(Constants.IN_EFFDATE, paramsMap.get(Constants.IN_EFFDATE));     		//填充帐务日期
		return journalMap;
	}

	@SuppressWarnings("unchecked")
	public static  Map initICJournalCharge(Context ctx){
		Map journalMap = new HashMap();
		journalMap.put(Constants.IN_RTXNREASONCD,ctx.getString(Constants.IN_RTXNREASONCD));
		journalMap.put(Constants.IN_RTXNTYPCD, ctx.getString(Constants.IN_RTXNTYPCD)); 	// 交易类型
		journalMap.put(Constants.IN_CARDACCTFLAG, ctx.getString(Constants.MEDIUM_TYPE)); // 账号/卡号标志
		journalMap.put(Constants.IN_RTXNSTATCD,ctx.getString(Constants.IN_RTXNSTATCD));  //填充交易状态
		journalMap.put(Constants.IN_ORIGPOSTDATE, ctx.getString(Constants.PE_POST_DATE));   //填充原来交易日期
		journalMap.put(Constants.IN_ORIGTRACKNBR, ctx.getString(Constants.IN_ORIGTRACKNBR));
		journalMap.put(Constants.IN_PARENTACCTNBR, ctx.getString(Constants.IN_PARENTACCTNBR));       	                            //填充父账号
		journalMap.put(Constants.IN_PARENTRTXNNBR, ctx.getString(Constants.IN_PARENTRTXNNBR));       	                            //填充父交易号
		journalMap.put(Constants.IN_PARENTRTXNTYPCD,null);
		journalMap.put(Constants.IN_INITIALCARDNBR,ctx.getString(Constants.IN_INITIALCARDNBR));
		journalMap.put(Constants.IN_TRACENBR,ctx.getString(Constants.PE_JOURNAL_NO));           //填充平台流水号
		journalMap.put(Constants.IN_POSTYN,Constants.PE_Y);
		journalMap.put(Constants.IN_COMMITYN,Constants.PE_N);
		journalMap.put(Constants.IN_DEBUGYN,Constants.PE_N);
		journalMap.put(Constants.IN_CASHANALYSISCD,null);
	    journalMap.put(Constants.IN_RTXNREASONCD,Constants.PE_NULL);
		journalMap.put(Constants.IN_ACCTNBR, ctx.getString(Constants.IN_ACCTNBR));       	     //填充账号
		journalMap.put(Constants.IN_NBROFITEMS, Constants.PE_ONE);       	                     //填充资金类型顺序号
		journalMap.put(Constants.IN_CLEARCATCD, Constants.CLEARCATCD_IMED);      //填充清算类别
		journalMap.put(Constants.IN_BALCATCD, Constants.BALCATCD_NOTE);  //余额大类
		journalMap.put(Constants.IN_BALTYPCD, Constants.BALTYPCD_BAL);    //余额类型
		journalMap.put(Constants.IN_BALCATARRAYSIZE,Constants.PE_NUM_ZERO);                      //余额类型数组长度
		journalMap.put(Constants.IN_FUNDTYPARRAYSIZE,Constants.PE_NUM_ZERO);                      //资金类型数组长度
		journalMap.put(Constants.IN_FUNDSAMT,ctx.getString(Constants.PE_TRAN_AMT));              //填充资金金额
		journalMap.put(Constants.IN_BALAMT,ctx.getString(Constants.PE_TRAN_AMT));                //填充本金发生额
		journalMap.put(Constants.IN_FUNDTYPCD,Constants.TRS_FUND_TYP_EL);
		journalMap.put(Constants.IN_FUNDTYPDTLCD,Constants.FUND_TYP_DTL_INTER);
		journalMap.put(Constants.IN_RTXNSOURCECD,ctx.getString(Constants.IN_RTXNSOURCECD));
		journalMap.put(Constants.IN_ORGNBR, InitData4Dzip.getYLOrgNbr()); //机构号数据库配置参数
		journalMap.put(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr()); // 钱箱号,数据库配置参数
		journalMap.put(Constants.IN_ORIGPERSNBR, InitData4Dzip.getYLPersNbr()); // 柜员号，数据库配置参数
		journalMap.put(Constants.IN_ORIGNTWKNODENBR, InitData4Dzip.getJCNtwkNodeNbr()); // 交易站点号，数据库配置参数
		journalMap.put(Constants.IN_EFFDATE, ctx.getString(Constants.IN_EFFDATE));     		//填充帐务日期
		return journalMap;
	}

	/**
	 * 柜面IC卡差错记账存储过程参数初始化
	 * @param ctx
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static  Map initICErrorHandle(Context ctx){
		Map journalMap = new HashMap();
		journalMap.put(Constants.IN_RTXNREASONCD, ctx.getString(Constants.IN_RTXNREASONCD));
		journalMap.put(Constants.IN_RTXNTYPCD, ctx.getString(Constants.IN_RTXNTYPCD)); 	// 交易类型
		journalMap.put(Constants.IN_CARDACCTFLAG, ctx.getString(Constants.MEDIUM_TYPE)); // 账号/卡号标志
		journalMap.put(Constants.IN_RTXNSTATCD, ctx.getString(Constants.IN_RTXNSTATCD));  //填充交易状态
		journalMap.put(Constants.IN_ORIGPOSTDATE, ctx.getString(Constants.PE_POST_DATE));   //填充原来交易日期
		journalMap.put(Constants.IN_ORIGTRACKNBR, ctx.getString(Constants.IN_ORIGTRACKNBR));
		journalMap.put(Constants.IN_PARENTACCTNBR, ctx.getString(Constants.IN_PARENTACCTNBR));       	                            //填充父账号
		journalMap.put(Constants.IN_PARENTRTXNNBR, ctx.getString(Constants.IN_PARENTRTXNNBR));       	                            //填充父交易号
		journalMap.put(Constants.IN_PARENTRTXNTYPCD, null);
		journalMap.put(Constants.IN_INITIALCARDNBR, ctx.getString(Constants.IN_INITIALCARDNBR));
		journalMap.put(Constants.IN_TRACENBR, ctx.getString(Constants.PE_JOURNAL_NO));           //填充平台流水号
		journalMap.put(Constants.IN_POSTYN, Constants.PE_Y);
		journalMap.put(Constants.IN_COMMITYN, Constants.PE_N);
		journalMap.put(Constants.IN_DEBUGYN, Constants.PE_N);
		journalMap.put(Constants.IN_CASHANALYSISCD,null);
	    journalMap.put(Constants.IN_RTXNREASONCD, Constants.PE_NULL);
		journalMap.put(Constants.IN_ACCTNBR, ctx.getString(Constants.IN_ACCTNBR));       	     //填充账号
		journalMap.put(Constants.IN_NBROFITEMS, Constants.PE_ONE);       	                     //填充资金类型顺序号
		journalMap.put(Constants.IN_CLEARCATCD, Constants.CLEARCATCD_IMED);      //填充清算类别
		journalMap.put(Constants.IN_BALCATCD, Constants.BALCATCD_NOTE);  //余额大类
		journalMap.put(Constants.IN_BALTYPCD, Constants.BALTYPCD_BAL);    //余额类型
		journalMap.put(Constants.IN_BALCATARRAYSIZE, Constants.PE_NUM_ZERO);                      //余额类型数组长度
		journalMap.put(Constants.IN_FUNDTYPARRAYSIZE, Constants.PE_NUM_ZERO);                      //资金类型数组长度
		journalMap.put(Constants.IN_FUNDSAMT, ctx.getString(Constants.PE_TRAN_AMT));              //填充资金金额
		journalMap.put(Constants.IN_BALAMT, ctx.getString(Constants.PE_TRAN_AMT));                //填充本金发生额
		journalMap.put(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);
		journalMap.put(Constants.IN_FUNDTYPDTLCD, Constants.FUND_TYP_DTL_INTER);
		journalMap.put(Constants.IN_RTXNSOURCECD, ctx.getString(Constants.IN_RTXNSOURCECD));
		journalMap.put(Constants.IN_ORGNBR, InitData4Dzip.getYLOrgNbr()); //机构号数据库配置参数
		journalMap.put(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr()); // 钱箱号,数据库配置参数
		journalMap.put(Constants.IN_ORIGPERSNBR, InitData4Dzip.getYLPersNbr()); // 柜员号，数据库配置参数
		journalMap.put(Constants.IN_ORIGNTWKNODENBR, InitData4Dzip.getJCNtwkNodeNbr()); // 交易站点号，数据库配置参数
		journalMap.put(Constants.IN_EFFDATE, ctx.getString(Constants.IN_EFFDATE));     		//填充帐务日期
		return journalMap;
	}

	/**
	 * 调用3DES+MD5算法生成密文
	 * @param ctx
	 */
	private static String generateEntryPwd(Context ctx,String pin) {
		String encryptPwd =null;
		if(Constants.PE_Y.equals(ctx.getString(Constants.CHECK_PIN))){
		    String taxrptForPersNbr = (String)ctx.getData(Constants.TAXRPTFORPERSNBR);   //获得客户号
		    String encrypt = taxrptForPersNbr+pin;										 //12位客户号+6位明文密码
			try {
				TripleDES_MD5.getInstance(base64Key, TripleDES_MD5.KEYTYPE_BASE64KEY);
				encryptPwd = TripleDES_MD5.getCipherTextOf3Des_MD5(encrypt);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return encryptPwd;//返回密文密码
	}

	/**
	 *  填充字符串，达到指定的长度
	 * @param str  需要被填充的字符串
	 * @param length  填充长度
	 * @param direction  左或右填充
	 * @param content    填充的内容
	 * @return   填充后的字符串
	 */
	public static String fill(String str, int length, String direction,String content) {
		for (int i = 0; i < length; i++) {
			if (direction == Constants.LEFT)    //左边补齐
				str = content + str;
			else								//右边补齐
				str = str + content;
		}
		return str;
	}

	/**
	 * 通过二磁获取IC卡的卡有效期,并设置14#
	 * @param ctx
	 */
	public static void setIcCardExpDate(Context ctx){
		//通过二磁信息获得卡有效期
		String track2 = ctx.getString(Constants.ISO8583_TRACK2_DATA);//二磁信息
		if(track2 != null && !"".equals(track2)){
			int indexOfEquals = track2.indexOf('=');//'='的位置索引
			if(indexOfEquals != -1){
				ctx.setData(Constants.ISO8583_EXPDAT, track2.substring(indexOfEquals+1, indexOfEquals+4+1));
			}else{
				ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);//初始卡有效期
			}
		}
	}

	/**
	 * 解密柜面发来报文中的密码
	 * @param timeStamp PE时间戳
	 * @param pin 柜面通过时间戳加密的密码
	 * @return
	 */
	public static String getPinFromOnli(String timeStamp, String pin){
		int len = timeStamp.length();
		String pinKey = timeStamp.substring(len-8, len);//取时间戳的后8位
		DesEncrypt des = new DesEncrypt(pinKey);
		return des.Decrypt(StringChange.HexToString(pin)).trim();//柜面传来的8位密码=6位真实密码+2个空格
	}

	/**
	 * 初始化PE.INSERTORUPDATEICCLOSIONGPAY参数
	 * @param ctx
	 */
	public static Map<String, Object> init4InsertOrUpdateIcclosingpay(Context ctx){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Dict.IN_CARD_NO, ctx.getData(Dict.CARD_NO));
		map.put(Dict.IN_TRXTYPE, ctx.getData(Dict.TRXTYPE));
		map.put(Dict.IN_ACTCODE, ctx.getData(Dict.ACTCODE));
		map.put(Dict.IN_LGNTYPE, ctx.getData(Dict.LGNTYPE));
		map.put(Dict.IN_SOURCE, Dict.SOURCE_BP);//固定交易来源为柜面
		map.put(Dict.IN_LGNINSTCODE, ctx.getData(Dict.LGNINSTCODE));
		map.put(Dict.IN_TRACENO, ctx.getData(Dict.TRACENO));
		map.put(Dict.IN_LGNMERCHCODE, ctx.getData(Dict.LGNMERCHCODE));
		map.put(Dict.IN_LGNUSRECODE, ctx.getData(Dict.LGNUSERCODE));
		map.put(Dict.IN_RTNWAY, ctx.getData(Dict.RTNWAY));
		map.put(Dict.IN_NAMEFLAG, ctx.getData(Dict.NAMEFLAG));
		map.put(Dict.IN_ID_TYPE, ctx.getData(Dict.ID_TYPE));
		map.put(Dict.IN_ID_CODE, ctx.getData(Dict.ID_CODE));
		map.put(Dict.IN_PINFLAG, ctx.getData(Dict.PINFLAG));
		map.put(Dict.IN_RTNFLG, ctx.getData(Dict.RTNFLG));

		return  map;
	}
}
