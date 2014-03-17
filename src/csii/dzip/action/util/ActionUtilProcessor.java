package csii.dzip.action.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;

import csii.base.action.util.FileUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.base.constant.SqlMaps;
import csii.dzip.core.Errors;
import csii.dzip.core.InitData4Dzip;
import csii.dzip.core.XMLPacket4ProcedureParser;

/**
 * @author chenshaoqi
 *@Description:交易公用类
 * @version 1.0.0
 * @since 2010-7-22
 */

public  class ActionUtilProcessor {
	protected final Log logger = LogFactory.getLog(getClass());
	private JdbcAccessAwareProcessor dzipdbAccess;
	private JdbcAccessAwareProcessor corebankAccess;
	private XMLPacket4ProcedureParser xmlProcedureParser;
	private DzipProcessTemplate dzipProcessTemplate;
	/**
	 * @desc atm调用核心存储过程的公共方法
	 *@author chenshq
	 *@date 2010.09.28
	 * @param ctx 传入参数
	 */
	@SuppressWarnings("unchecked")
	public void atmDoAction(final Context ctx,final Map procedureMap,final Map map){
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				List list = xmlProcedureParser.getElementNames(FileUtils.getRootElement(Constants.CORE_PROCEDURE_INTERFACE));
				String inXMLStr = xmlProcedureParser.format(map, list);// 组XML格式报文
				logger.info("本行交易报文inXML=============>" + inXMLStr);
		        /*************填充核心存储过程的参数***************/
				procedureMap.put(Constants.IN_TRAN_CODE, map.get(Constants.PROC_CODE));// 交易码
				procedureMap.put(Constants.IN_XML, inXMLStr);	 		// 传入XML格式的报文参数
				procedureMap.put(Constants.IN_XML_CNT, Constants.PE_NUM_ONE);// 报文组数
				procedureMap.put(Constants.OUT_XML,	Constants.PE_NULL); // 返回报文
				procedureMap.put(Constants.OUT_XML_CNT, Constants.PE_NUM_ZERO);
				procedureMap.put(Constants.OUT_ERROR_NBR, Constants.PE_NUM_ZERO);
				procedureMap.put(Constants.OUT_ERROR_MSG, Constants.PE_NULL);
				procedureMap.put(Constants.OUT_ORA_ERROR_MSG, Constants.PE_NULL);
				 try {
					 logger.info("==================================>Start To Call Core procedure -- atmTxn");
					 corebankAccess.getSqlMap().update("atm.atmTxn", procedureMap);// 调用核心存储过程
				} catch (Exception e) {
					try {
						throw new PeException("本行交易存储过程执行失败=====>"+e.getMessage());
					} catch (PeException e1) {
						// TODO Auto-generated catch block
						logger.error("本行交易报文inXML=============>" + inXMLStr);
						logger.error(e1.getMessage());
					}
				}
				return null;
			}
		});
	}
	/**
	 * @author chenshq
	 * @since 2010.07.22
	 * @用途：调用核心存储过程后，根据返回报文，完成响应操作。
	 * @param ctx
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public Context response(final Context ctx,final Map procedureMap)throws DataAccessException {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				/*返回报文不为空时，解析返回报文，取出信息*/
				if (procedureMap.get(Constants.OUT_XML) != null
						&&(String)procedureMap.get(Constants.OUT_XML) != "") {
					String out_XML=procedureMap.get(Constants.OUT_XML).toString();
					logger.info("本行交易存储过程返回报文OUT_XML==>" + out_XML);
					Map respMap = xmlProcedureParser.parse(out_XML);
					// 解析存储过程返回的XML格式报文
					ctx.setData(Constants.ISO8583_RESCODE, respMap.get(Constants.HOST_RESPONSE));            //获得响应码，并返回
					ctx.setData(Constants.PE_HOST_SEQ_NO, respMap.get(Constants.DWTP_RTXN_NBR));        	 //主机流水号
//					ctx.setData(Constants.ISO8583_ADDDATA, respMap.get(Constants.DWTP_TRAN_RESPONSE_DESCRIP)); //获得响应信息，并返回
					ctx.setData(Constants.ISO8583_EXPDAT, respMap.get(Constants.DWTP_EXPIRE_YEAR));           //获得卡有效期
					ctx.setData(Constants.ISO8583_SETDATE, respMap.get(Constants.DWTP_SETTLE_DATE));          //获得清算日期
					ctx.setData(Constants.PE_HOST_RESP_CD, respMap.get(Constants.HOST_RESPONSE));    		  //响应码
					if( respMap.get(Constants.DWTP_BALANCE_CURR)!=null
							&&!Constants.PE_NULL.equals(respMap.get(Constants.DWTP_BALANCE_CURR)))
						procedureMap.put(Constants.OUT_BALAMT, respMap.get(Constants.DWTP_BALANCE_CURR));      //获得帐户账面余额
					if( respMap.get(Constants.DWTP_BALANCE_AVAIL)!=null
							&&!Constants.PE_NULL.equals(respMap.get(Constants.DWTP_BALANCE_AVAIL)))
						procedureMap.put(Constants.OUT_AVAILBALAMT, respMap.get(Constants.DWTP_BALANCE_AVAIL)); //获得帐户可用余额
					procedureMap.put(Constants.OUT_ACCTTYPCD, respMap.get(Constants.DWTP_ACCT_TYPECD));         //获得帐户类型
					/*如果响应码是 00，修改交易流水的状态为成功  ；0：表示成功；*/
					if (ctx.getData(Constants.ISO8583_RESCODE).equals(Constants.PE_OK))
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);                             //成功状态
					else
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);                            //异常状态
				}
				else{
					logger.error("本行交易存储过程返回报文为空,参数信息:"+procedureMap);  //返回报文为空时
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
					ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_SYSTEM_ERROR);        //获得响应码，并返回
					ctx.setData(Constants.ISO8583_ADDDATA, "system is error!");               //填充响应信息，并返回
				}
				return null;
			}});
		return ctx;
	}
	/**
	 * @desc pos调用核心存款确认存储过程的公共方法
	 *@author chenshq
	 *@date 2010.09.28
	 * @param depComfirmMap 传入参数
	 */
	@SuppressWarnings("unchecked")
	public void depComfirm(final Map depComfirmMap){
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					// 调用核心存储过程查询时上笔交易的记录
					 logger.info("==========================>Start procedure atm.ConfirmDeposit,paramer:"+depComfirmMap);
					 corebankAccess.getSqlMap().update("atm.ConfirmDeposit",depComfirmMap);
				} catch (Exception e) {
					logger.error("存款确认存储过程执行失败=====>"+e.getMessage());
				}
				return null;
			}
		});
	}
	/**
	 * 冻结或解冻操作
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void deductPreAuth(Context ctx,JdbcAccessAwareProcessor bankdb,String type8583)throws PeException {
	    Map preAuthMap = new HashMap();
		preAuthMap = Init.initPreAuth(ctx, preAuthMap,type8583);  	//初始化
		try {
			    logger.info( "===========>excute procedure pos.preAuth,Paramer:" + preAuthMap);
				corebankAccess.getSqlMap().update("pos.preAuth", preAuthMap); // 调用取款核心存储过程。
				//输出存储过程响应的信息
				logger.info(Constants.OUT_OPTNBR + "===========>" + preAuthMap.get(Constants.OUT_OPTNBR));
				logger.info(Constants.OUT_RESPCODE + "===========>" + preAuthMap.get(Constants.OUT_RESPCODE));
				logger.info(Constants.OUT_ERROR_NBR + "===========>" + preAuthMap.get(Constants.OUT_ERROR_NBR));
				logger.info(Constants.OUT_ORA_ERROR_MSG + "===========>" + preAuthMap.get(Constants.OUT_ORA_ERROR_MSG));
				//输出完毕
				ctx.setData(Constants.PE_HOST_RESP_CD, preAuthMap.get(Constants.OUT_RESPCODE)); 		//响应码
				ctx.setData(Constants.PE_OPTNBR,preAuthMap.get(Constants.OUT_OPTNBR));      			//冻结编号
				if(!Constants.PE_OK.equals(String.valueOf(preAuthMap.get(Constants.OUT_RESPCODE)))){	//数据库操作失败
					throw new PeException("执行冻结或解冻交易失败,参数信息===>"+preAuthMap);
				}
		} catch (Exception e) {
				throw new PeException(e.getMessage());
		}
	}
	/**
	 * 扣除手续费操作
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void deductFee(Context ctx,JdbcAccessAwareProcessor bankdb,String type8583)throws PeException {
			Map feeProcedureMap = new HashMap();
			Map feeRtxnchalparam = new HashMap();
			Map feeMap = new HashMap();
			String source = ctx.getString(Constants.IN_RTXNSOURCECD);
			if(source.indexOf(Constants.ATM) != -1){//如果是ATM渠道
				feeRtxnchalparam.put(Constants.RTXNSOURCECD, Constants.ATM); 	// 交易来源
			}else if(source.indexOf(Constants.CLM) != -1){//如果是CLM渠道
				feeRtxnchalparam.put(Constants.RTXNSOURCECD, Constants.CLM); 	// 交易来源
			}
			feeRtxnchalparam.put(Constants.RTXNCATCD, Constants.RTXNCATCD_03); 		// 交易性质
			feeRtxnchalparam.put(Constants.TRANCD, Constants.TRANCD_000000); 		// 交易码
			List feeResultlist = new ArrayList();
			String fee = String.valueOf(ctx.getData(Constants.TRAN_FEE));
			try {
				feeResultlist = dzipProcessTemplate .query_OrgNbr_GlAcctTitleNbr(feeRtxnchalparam); // 取出记账顺序
				for(Iterator feeIt = feeResultlist.iterator();feeIt.hasNext();){
					feeMap = (Map) feeIt.next();
					getTranTyp(ctx, feeMap,type8583); // 根据分录顺序判断传入参数
					ctx.setData(Constants.IN_RTXNTYPCD, feeMap.get(Constants.RTXNTYPCD)); 	// 交易类型
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   		// 资金类型
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_FOUR);   				//手续费传入标志为：4
					ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1004);   	//1004: 手续费
					feeProcedureMap = Init.initDeposittxn(ctx, feeProcedureMap,type8583); 	// 填充调用核心存取款验证存储过程参
					feeProcedureMap.put(Constants.IN_BALAMT, fee);							// 扣款金额
					feeProcedureMap.put(Constants.IN_FUNDSAMT, fee);						// 扣款金额
					logger.info("====================>Start fee procedure pos.deposittxn:"+ctx.getData(Constants.PE_JOURNAL_NO));
					logger.info("====================>fee pos.deposittxn Paramer:" + feeProcedureMap);
					bankdb.getSqlMap().update("pos.deposittxn",	feeProcedureMap); 			// 调用取款核心存储过程。
					logger.info("====================>End fee procedure pos.deposittxnt:"+ctx.getData(Constants.PE_JOURNAL_NO));
					logger.info(Constants.OUT_ACCTNO + "===========>"
							+ feeProcedureMap.get(Constants.OUT_ACCTNO)); 					// 账号
					logger.info(Constants.OUT_RTXNNBR + "===========>"
							+ feeProcedureMap.get(Constants.OUT_RTXNNBR));					// 核心交易流水号
					logger.info(Constants.OUT_RESPONCD + "===========>"
							+ feeProcedureMap.get(Constants.OUT_RESPONCD));					// 核心响应码
					logger.info(Constants.OUT_ERRORNBR + "===========>"
							+ feeProcedureMap.get(Constants.OUT_ERRORNBR));					// 异常信息码
					logger.info(Constants.OUT_ERRORMSG + "===========>"
							+ feeProcedureMap.get(Constants.OUT_ERRORMSG));					// 异常信息
					logger.info(Constants.OUT_ORAERRORMSG + "===========>"
							+ feeProcedureMap.get(Constants.OUT_ORAERRORMSG));				// 数据库异常信息
					ctx.setData(Constants.PE_HOST_RESP_CD, feeProcedureMap.get(Constants.OUT_RESPONCD)); //响应码
					if (Constants.PE_OK.equals(feeProcedureMap.get(Constants.OUT_RESPONCD))
							&& Constants.PE_ZERO.equals(String.valueOf(feeProcedureMap.get(Constants.OUT_ERRORNBR)))) {
						if(!Constants.ACCT_FLAG_1.equals(String.valueOf(feeProcedureMap.get(Constants.IN_CARDACCTFLAG)))){
							ctx.setData(Constants.ACCTTYPCD, feeProcedureMap.get(Constants.OUT_ACCTTYPCD));   //帐户类型
							ctx.setData(Constants.BALAMT, feeProcedureMap.get(Constants.OUT_BALAMT));//账面余额
							ctx.setData(Constants.AVAILBALAMT, feeProcedureMap.get(Constants.OUT_AVAILBALAMT));//可用余额
						}
						ctx.setData(Constants.DEDUCT_FEE_SUCCESS, Constants.PE_Y);	//扣除交易费是否成功： Y 成功，N 失败
						feeMap.clear();
						feeProcedureMap.clear();
					} else {
						ctx.setData(Constants.DEDUCT_FEE_SUCCESS, Constants.PE_N);	//扣除交易费是否成功： Y 成功，N 失败
						logger.error("扣除手续费交易失败,参数信息:"+feeProcedureMap);
						break;
					}
				}
			} catch (PeException e) {
					 throw new PeException(e.getMessage());
			}
	}

	/**
	 * @desc 核心记账接口
	 *@author chenshq
	 *@date 2010.09.27
	 * @param ctx 传入参数
	 */
	@SuppressWarnings("unchecked")
	public void deductTranAMT(final Context ctx,final String type8583) throws PeException{
		final TransactionTemplate transactionTemplate =corebankAccess.getTransactionTemplate();
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus amtTransaction) {
					Map procedureMap = new HashMap();
					Map map = new HashMap();
					Map rtxnchalparam =  new HashMap();
					rtxnchalparam.put(Constants.RTXNSOURCECD,ctx.getData(Constants.IN_RTXNSOURCECD));   //交易来源
					rtxnchalparam.put(Constants.RTXNCATCD,ctx.getData(Constants.RTXNCATCD));			//交易性质
					rtxnchalparam.put(Constants.TRANCD,ctx.getData(Constants.TransactionId));			//交易码
					List resultlist =new ArrayList();
					String rtxnFlag=Constants.PE_Y;//标记是否执行存取款过程 ,Y:执行,N:不执行；
					try {
						if(Constants.PE_Y.equals(ctx.getData(Constants.CHECK_PIN))){//验证密码
							Map paramsMap=Init.InitCheckPin(ctx);
							logger.info("====================>Start procedure common.checkPin Paramer:" + paramsMap);
							String responcd=dzipProcessTemplate.checkPin(paramsMap);
							logger.info("====================>End procedure common.checkPin responcd:" + responcd);
							ctx.setData(Constants.PE_HOST_RESP_CD, responcd); 		//响应码
					    	if(!Constants.PE_OK.equals(responcd)){
						    	if(Constants.PE_55.equals(responcd)||Constants.PE_75.equals(responcd)){
									ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：失败
			                        ctx.setData(Constants.PE_CARD_EXPRI, Constants.PE_EXPIREYEAR);   //有效期
									rtxnFlag=Constants.PE_N;
								}else{
									throw new PeException("密码验证失败,参数信息:"+paramsMap);
								}
					    	}
							ctx.setData(Constants.CHECK_PIN, Constants.PE_N);//重新设置不需要验证密码
						}
						if(Constants.PE_Y.equals(rtxnFlag)){
							if(Constants.PE_Y.equals(ctx.getData(Constants.PE_EXECYN))){
								deductPreAuth(ctx, corebankAccess,type8583);	//执行冻结,解冻帐户功能
								ctx.setData(Constants.CHECK_IDEN,  Constants.PE_N);//不需要验证证件信息
							}
							boolean icDeduct = true;	//更新电子钱包余额标志
							resultlist = dzipProcessTemplate.query_OrgNbr_GlAcctTitleNbr(rtxnchalparam);   //取出记账分录顺序
							String num =String.valueOf(resultlist.size());//分录个数
							for(Iterator it = resultlist.iterator();it.hasNext();){
								map = (Map)it.next();
								getTranTyp(ctx, map,type8583);												//根据分录顺序判断传入参数
								ctx.setData(Constants.IN_RTXNTYPCD, map.get(Constants.RTXNTYPCD)); 			// 交易类型
								procedureMap = Init.initDeposittxn(ctx, procedureMap,type8583);				//填充调用核心存/取款验证存储过程参
								logger.info("====================>Start procedure pos.deposittxn:"+ctx.getData(Constants.PE_JOURNAL_NO));
								logger.info("====================>pos.deposittxn Paramer:" + procedureMap);
								corebankAccess.getSqlMap().update("pos.deposittxn", procedureMap); // 调用取款核心存储过程。
								logger.info("====================>End procedure pos.deposittxn:"+ctx.getData(Constants.PE_JOURNAL_NO));
								//输出存储过程响应的信息
								logger.info(Constants.OUT_ACCTNO + "===========>" + procedureMap.get(Constants.OUT_ACCTNO));  //账号
								logger.info(Constants.OUT_ACCTTYPCD + "===========>" + procedureMap.get(Constants.OUT_ACCTTYPCD));  //帐户类型和名称
								logger.info(Constants.OUT_RTXNNBR + "===========>" + procedureMap.get(Constants.OUT_RTXNNBR));//核心交易流水号
								logger.info(Constants.OUT_POSTDATE + "===========>" + procedureMap.get(Constants.OUT_POSTDATE));//账务日期
								logger.info(Constants.OUT_RESPONCD + "===========>" + procedureMap.get(Constants.OUT_RESPONCD));//核心响应码
								logger.info(Constants.OUT_ERRORNBR + "===========>" + procedureMap.get(Constants.OUT_ERRORNBR));//异常信息码
								logger.info(Constants.OUT_ERRORMSG + "===========>" + procedureMap.get(Constants.OUT_ERRORMSG));//异常信息
								logger.info(Constants.OUT_ORAERRORMSG  + "===========>" + procedureMap.get(Constants.OUT_ORAERRORMSG));//数据库异常信息
								//输出完毕
								ctx.setData(Constants.PE_HOST_RESP_CD, procedureMap.get(Constants.OUT_RESPONCD)); //响应码
							    if (Constants.PE_OK .equals(procedureMap.get(Constants.OUT_RESPONCD))
								   && Constants.PE_ZERO.equals(String.valueOf(procedureMap.get(Constants.OUT_ERRORNBR)))){
									if(!Constants.ACCT_FLAG_1.equals(String.valueOf(procedureMap.get(Constants.IN_CARDACCTFLAG)))){
										ctx.setData(Constants.ACCTTYPCD,(String)procedureMap.get(Constants.OUT_ACCTTYPCD));   //帐户类型
								        ctx.setData(Constants.ACCTNAME,(String)procedureMap.get(Constants.OUT_ACCTNAME));   //帐户名称
										ctx.setData(Constants.PE_CARD_EXPRI, procedureMap.get(Constants.OUT_EXPIREYEAR));   //有效期
										ctx.setData(Constants.BALAMT, procedureMap.get(Constants.OUT_BALAMT));//账面余额
										ctx.setData(Constants.AVAILBALAMT, procedureMap.get(Constants.OUT_AVAILBALAMT));//可用余额
									}
									if(Constants.PE_ONE.equals(map.get(Constants.TRANSEQNO))){
										ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);     		 //交易状态：成功
										ctx.setData(Constants.PE_HOST_SEQ_NO,procedureMap.get(Constants.OUT_RTXNNBR)); //主机交易流水号
										ctx.setData(Constants.PE_RLTACCTNO,procedureMap.get(Constants.OUT_ACCTNO)); //相关账号
										ctx.setData(Constants.IN_PARENTACCTNBR, procedureMap.get(Constants.OUT_ACCTNO));  	//父账号:多笔分录交易和冲正交易，必填
										ctx.setData(Constants.IN_PARENTRTXNNBR, procedureMap.get(Constants.OUT_RTXNNBR));    //父交易号:多笔分录交易和冲正交易，必填
										if(Constants.PE_THREE.equals(ctx.getData(Constants.IN_REVFLAG))){   //冲正标志:0：正常   1：冲正 	2：抹帐
											ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);			//冲正标志:0：正常   1：冲正 	2：抹帐
										}
									}
									if(num.equals(map.get(Constants.TRANSEQNO))&&Constants.PE_Y.equals(ctx.getData(Constants.DEDUCT_FEE))){
										deductFee(ctx, corebankAccess,type8583);					//交易记账成功后进行手续费的到扣除
										if (Constants.PE_Y.equals(ctx.getData(Constants.DEDUCT_FEE_SUCCESS)))
											ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); // 交易状态：成功
										else {
											icDeduct = false;
											throw new PeException("扣除手续费交易失败");
										}
									}
									map.clear();
									procedureMap.clear();
								 } else{
			                          icDeduct = false;
									  if(Constants.PE_25.equals(procedureMap.get(Constants.OUT_RESPONCD))){
										  ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：失败
				                          ctx.setData(Constants.PE_CARD_EXPRI, Constants.PE_EXPIREYEAR);   //有效期
										  break;//跳出分录
									  }else{
										throw new PeException("存取款交易失败,参数信息:"+procedureMap);
									  }
								 }
							}
							List<String> tranCdList = Arrays.asList(
									Constants.TRANCD_020060,//指定账户圈存
									Constants.TRANCD_042060,//CLM指定账户圈存冲正(他行ATM圈存冲正)
									Constants.TRANCD_020061,//CLM圈提确认(他行ATM圈提确认)
									Constants.TRANCD_020065,//他行ATM转入圈存
									Constants.TRANCD_042065,//他行ATM转入圈存冲正
									Constants.TRANCD_020063,//他行ATM现金充值圈存
									Constants.TRANCD_042063,//他行ATM现金充值圈存冲正
									Constants.TRANCD_02001791,//他行ATM现金充值圈存撤销
									Constants.TRANCD_04201791,//他行ATM现金充值圈存撤销冲正
									Constants.TRANCD_021060,//本行ATM指定账户圈存
									Constants.TRANCD_043060,//本行ATM指定账户圈冲正
									Constants.TRANCD_021063,//本行ATM现金充值圈存
									Constants.TRANCD_043063,//本行ATM现金充值圈存冲正
									Constants.TRANCD_02101791,//本行ATM现金充值圈存撤销
									Constants.TRANCD_021061,//本行ATM圈提确认
									Constants.TRANCD_021062,//本行ATM本代本转账圈存
									Constants.TRANCD_043062//本行ATM本代本转账圈存冲正
									);
							String tranCd = ctx.getString(Constants.TransactionId);//交易码
							if((!Constants.RTXNCATCD_02.equals(ctx.getData(Constants.RTXNCATCD)))//如果是本代本或本代他电子现金交易
							   && icDeduct		//如果记账成功且为电子钱包类交易，则更新电子钱包余额
							   && tranCdList.contains(tranCd)) {
								Map procMap = new HashMap();
								procMap.put(Constants.IN_MEDIUMID, ctx.getData(Constants.ISO8583_ACCTNO));
								if(tranCd.equals(Constants.TRANCD_043062)){
									procMap.put(Constants.IN_MEDIUMID, ctx.getData(Constants.PE_ACCTID2));
								}
								if(tranCd.equals(Constants.TRANCD_021062)){
									procMap.put(Constants.IN_MEDIUMID, ctx.getData(Constants.ISO8583_ACCIDE_N2));
								}
								procMap.put(Constants.IN_TRANCD, tranCd);
								procMap.put(Constants.IN_AMT, ctx.getData(Constants.PE_TRAN_AMT));
								logger.info("====================>Start procedure pos.updICEWALLETCTL:"+ctx.getData(Constants.PE_JOURNAL_NO));
								logger.info("====================>pos.updICEWALLETCTL Paramer:" + procMap);
								corebankAccess.getSqlMap().update("pos.updICEWALLETCTL", procMap); // 调用核心更新电子钱包余额存储过程
								logger.info("====================>End procedure pos.updICEWALLETCTL:"+ctx.getData(Constants.PE_JOURNAL_NO));
								//输出存储过程响应的信息
								logger.info(Constants.OUT_RESPONCD + "===========>" + procMap.get(Constants.OUT_RESPONCD));  //核心响应码
								logger.info(Constants.OUT_ERRORNBR + "===========>" + procMap.get(Constants.OUT_ERRORNBR));//异常信息码
								logger.info(Constants.OUT_ERRORMSG + "===========>" + procMap.get(Constants.OUT_ERRORMSG));//异常信息
								ctx.setData(Constants.PE_HOST_RESP_CD, procMap.get(Constants.OUT_RESPONCD)); //响应码
								if (!(Constants.PE_OK.equals(procMap.get(Constants.OUT_RESPONCD))
										&& Constants.PE_ZERO.equals(String.valueOf(procMap.get(Constants.OUT_ERRORNBR))))){
//									throw new PeException("更新电子钱包余额失败");
									throw new PeException("更新电子钱包余额失败:" + procMap.get(Constants.OUT_ERRORMSG));
								}
							}
						}
					} catch (PeException e) {
							logger.error("deductTranAMT method Error,syseqno:"+ctx.getData(Constants.PE_JOURNAL_NO)+"====>"+e.getMessage());
//							transactionTemplate.getTransactionManager().rollback(amtTransaction);
							ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);           //交易状态：失败
							ctx.setData(Constants.PE_CARD_EXPRI, Constants.PE_EXPIREYEAR);   //有效期
							List<String> tranList = Arrays.asList(Constants.TRANCD_021060, Constants.TRANCD_021063);
							if(tranList.contains(ctx.getData(Constants.TransactionId))){
								//如果交易为：指定账户圈存、现金充值圈存时，记账出现异常不返回55#给ATM
								ctx.setData(Constants.ISO8583_ICCSYSRELDATA, Constants.PE_NULL);
							}
							if(Constants.PE_NULL.equals(ctx.getData(Constants.PE_HOST_RESP_CD))||
									Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD)))
								ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_SYSTEM_ERROR);    //响应码
							if(Constants.ISO8583.equals(type8583))
								ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
							if(Constants.FIX8583.equals(type8583))
								ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
							throw new PeRuntimeException("存取款交易失败");
					}
				    return null;
				}
			});
	}

	/**
	 * 获得系统交易参数信息
	 * @return
	 */
	public String getTranParamInfo(final Context ctx,String type8583){
		String responcd =dzipProcessTemplate.getSysTranInfo(ctx,type8583); //获得系统交易参数信息
		if(!Constants.PE_OK.equals(responcd)){
			ctx.setData(Constants.PE_HOST_RESP_CD, responcd);
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易状态：失败
		}
		return responcd;
	}
	/**
	 * 预授权冻结帐户功能
	 * @param ctx
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public void preAuthDoAction(final Context ctx,final Map procedureMap){
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					logger.info( "===========>excute procedure pos.preAuth,Paramer:" + procedureMap);
					corebankAccess.getSqlMap().update("pos.preAuth", procedureMap); // 调用取款核心存储过程。
					//输出存储过程响应的信息
					logger.info(Constants.OUT_OPTNBR + "===========>" + procedureMap.get(Constants.OUT_OPTNBR));
					logger.info(Constants.OUT_TRANACCTNO + "===========>" + procedureMap.get(Constants.OUT_TRANACCTNO));
					logger.info(Constants.OUT_RESPCODE + "===========>" + procedureMap.get(Constants.OUT_RESPCODE));
					logger.info(Constants.OUT_ERROR_NBR + "===========>" + procedureMap.get(Constants.OUT_ERROR_NBR));
					logger.info(Constants.OUT_ORA_ERROR_MSG + "===========>" + procedureMap.get(Constants.OUT_ORA_ERROR_MSG));
					//输出完毕
					ctx.setData(Constants.PE_HOST_RESP_CD, procedureMap.get(Constants.OUT_RESPCODE)); 		//响应码
					ctx.setData(Constants.PE_OPTNBR,procedureMap.get(Constants.OUT_OPTNBR));      			//冻结编号
					if(Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPCODE)))){		//数据库操作成功
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);  							//交易状态：成功
						ctx.setData(Constants.PE_RLTACCTNO,procedureMap.get(Constants.OUT_TRANACCTNO));    	//相关账号
						ctx.setData(Constants.PE_CARD_EXPRI, procedureMap.get(Constants.OUT_EXPIREDATE));	//卡有效期
					}else{																					//数据库操作失败
							ctx.setData(Constants.PE_CARD_EXPRI,Constants.PE_EXPIREYEAR);					//卡有效期
							ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); 						//交易状态：失败
							logger.error("method preAuthDoAction procedure pos.preAuth Error,syseqno:"+
									ctx.getData(Constants.PE_JOURNAL_NO)+",parameters:"+procedureMap);
					}
				} catch (Exception e) {
					logger.error("预授权接口执行出错======>"+e.getMessage());
					ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_SYSTEM_ERROR); //响应码
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：失败
					ctx.setData(Constants.PE_CARD_EXPRI, Constants.PE_EXPIREYEAR);
				}
				return null;
			}});
	}

	/**
	 * 查询帐户信息功能
	 * @param ctx
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public HashMap selectAcctInfo(final Context ctx,final String type8583){
		final HashMap procedureMap = new HashMap();
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Init.initSelectAcctInfo(ctx, procedureMap,type8583);      //填充调用核心查询账户信息存储过程传入参数
				try {
					logger.info("==================================>Start To Call Core procedure ----- SelectAcctInfo");
					corebankAccess.getSqlMap().update("pos.SelectAcctInfo",	procedureMap);  // 调用核心存储过程。
					//输出存储过程响应的信息
					logger.info(Constants.OUT_RESPONCD + "===========>" + procedureMap.get(Constants.OUT_RESPONCD));//核心响应码
					logger.info(Constants.OUT_ERRORNBR + "===========>" + procedureMap.get(Constants.OUT_ERRORNBR));//异常信息码
					logger.info(Constants.OUT_ERRORMSG + "===========>" + procedureMap.get(Constants.OUT_ERRORMSG));//异常信息
					logger.info(Constants.OUT_ORAERRORMSG  + "===========>" + procedureMap.get(Constants.OUT_ORAERRORMSG));//数据库异常信息
					//输出完毕
					 if(Constants.ISO8583.equals(type8583)){
						 ctx.setData(Constants.ISO8583_RESCODE, procedureMap.get(Constants.OUT_RESPONCD));    //响应码
						 ctx.setData(Constants.ISO8583_EXPDAT, procedureMap.get(Constants.OUT_EXPIREYEAR));    //卡有效期
					 }else{
						 ctx.setData(Constants.FIX8583_RESP, procedureMap.get(Constants.OUT_RESPONCD));    //响应码
						 ctx.setData(Constants.FIX8583_EXPI, procedureMap.get(Constants.OUT_EXPIREYEAR));   //有效期
					 }
					ctx.setData(Constants.PE_HOST_RESP_CD, procedureMap.get(Constants.OUT_RESPONCD));    //响应码

					// 余额查询成功，修改交易流水的状态为成功
					if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))) {
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);    //交易状态：成功
					}else{
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);    //交易状态：失败
					}
				} catch (Exception e) {
					logger.error("procedure SelectAcctInfo error====>"+e.getMessage());
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);           //交易状态：失败
					ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_ERROR);        	//响应码：失败
					if(Constants.ISO8583.equals(type8583)){
						ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_ERROR);        	//响应码：失败
					}else{
						ctx.setData(Constants.FIX8583_RESP, Constants.PE_ERROR);    //响应码
					}
				}
				return null;
			}});
		return procedureMap;
	}

	/**
	 * @desc 批量流水核心记账接口
	 *@author xujin
	 *@date 2011.04.25
	 * @param ctx 传入参数
	 */
	@SuppressWarnings("unchecked")
	public void deductBatchTranAMT(final Map paramsMap,final Map procedureMap) throws PeException{
		final TransactionTemplate transactionTemplate =corebankAccess.getTransactionTemplate();
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus amtTransaction) {
					procedureMap.put(Constants.PE_OPTNBR,Constants.PE_ZERO);   //冻结编号
					Map journalMap = new HashMap(); // 记录交易流水的参数Map
					Map rtxnchalparam =  new HashMap();
					Map mapResult=new HashMap();
					Map mapTranTyp=new HashMap();
					List resultlist =new ArrayList();
					try {
						journalMap=getJournalBatchMap(paramsMap, journalMap);
					    Map preAuthMap = new HashMap();
						if(journalMap.get(Constants.IN_PREFLAG)!=null){//执行冻结,解冻帐户功能
							preAuthMap = Init.initBatchPreAuth(journalMap, preAuthMap);  	//初始化
							try {
								    logger.info("批量补录交易===========>start to procedure pos.batchPreAuth,paramers:"+preAuthMap);
									corebankAccess.getSqlMap().update("pos.batchPreAuth", preAuthMap); // 调用取款核心存储过程。
									//输出存储过程响应的信息
									logger.info(Constants.OUT_OPTNBR + "===========>" + preAuthMap.get(Constants.OUT_OPTNBR));
									logger.info(Constants.OUT_ERROR_NBR + "===========>" + preAuthMap.get(Constants.OUT_ERROR_NBR));
									logger.info(Constants.OUT_ORA_ERROR_MSG + "===========>" + preAuthMap.get(Constants.OUT_ORA_ERROR_MSG));
									//输出完毕
									if (Constants.PE_ZERO.equals(String.valueOf(preAuthMap.get(Constants.OUT_ERROR_NBR)))){	//数据库操作成功																			//数据库操作失败
										procedureMap.put(Constants.OUT_ERRORNBR,Constants.PE_ZERO);
										procedureMap.put(Constants.PE_OPTNBR,preAuthMap.get(Constants.OUT_OPTNBR));   //冻结编号
										procedureMap.put(Constants.PE_POST_DATE, journalMap.get(Constants.IN_EFFDATE));
										if(journalMap.get(Constants.PE_RLTSEQNO)!=null){//修改原交易状态
										   procedureMap.put(Constants.PE_JOURNAL_NO,journalMap.get(Constants.PE_RLTSEQNO));
										   procedureMap.put(Constants.PE_NEWOPTNBR,preAuthMap.get(Constants.OUT_OPTNBR));//新冻结编号
										   procedureMap.put(Constants.PE_OPTNBR,Constants.PE_ZERO); //冻结编号
										}
								    }
								    else{
								    	 if(Constants.Error_1001.equals(String.valueOf(preAuthMap.get(Constants.OUT_ERROR_NBR)))){
								    		 journalMap.put(Constants.PE_EXECYN,Constants.EXEC_Y);//该交易已经存在，不需重复记账,标记成功
								    	 }
								    	 throw new PeException("批量补录交易执行冻结或解冻交易失败,参数信息===>"+preAuthMap);
								    }
								} catch (Exception e) {
									throw new PeException(e.getMessage());
								}
						}
						if(journalMap.get(Constants.IN_PREFLAG)==null||Constants.PE_ONE.equals(journalMap.get(Constants.IN_PREFLAG))){
							procedureMap.put(Constants.OUT_ERRORNBR,null);
							rtxnchalparam.put(Constants.RTXNSOURCECD,journalMap.get(Constants.IN_RTXNSOURCECD));   //交易来源
							rtxnchalparam.put(Constants.RTXNCATCD,paramsMap.get(Constants.PE_RTXNCATCD));			//交易性质
							rtxnchalparam.put(Constants.TRANCD,paramsMap.get(Constants.PE_OUT_TRANCD));			//交易码
							resultlist = dzipProcessTemplate.query_OrgNbr_GlAcctTitleNbr(rtxnchalparam);   //取出记账顺序
							String num=String.valueOf(resultlist.size());
							for(Iterator it=resultlist.iterator();it.hasNext();){
								mapResult = (Map)it.next();
								mapTranTyp=getTranTyp(mapResult);												//根据分录顺序判断传入参数
								journalMap.put(Constants.IN_RTXNTYPCD, mapResult.get(Constants.RTXNTYPCD)); 	// 交易类型
								journalMap.put(Constants.IN_CARDACCTFLAG, mapTranTyp.get(Constants.MEDIUM_TYPE)); // 账号/卡号标志
								if(Constants.PE_ONE.equals(String.valueOf(mapTranTyp.get(Constants.MEDIUM_TYPE)))){
									journalMap.put(Constants.IN_ACCTNBR, mapTranTyp.get(Constants.IN_ACCTNBR)); // 账号
									if(Constants.RTXNCATCD_02.equals(paramsMap.get(Constants.PE_RTXNCATCD))){
										journalMap.put(Constants.IN_INITIALCARDNBR,journalMap.get(Constants.IN_ACCTNBR));
									}else{
										journalMap.put(Constants.IN_INITIALCARDNBR,null);
									}
								}
								else
								{
									if(Constants.CRDB_X.equals(paramsMap.get(Constants.PE_CRDB))&&
											Constants.PE_TWO.equals(mapResult.get(Constants.TRANSEQNO))){//ATM转账且第二笔分录
										journalMap.put(Constants.IN_ACCTNBR, paramsMap.get(Constants.PE_ACCTID2)); // 转入账号
									}
									journalMap.put(Constants.IN_INITIALCARDNBR,journalMap.get(Constants.IN_ACCTNBR));//填充卡号
								}
								logger.info("批量补录交易===============>Start procedure pos.Batch_DepWth,paramers:"+journalMap);
								corebankAccess.getSqlMap().update("pos.Batch_DepWth", journalMap); // 调用取款核心存储过程。
								logger.info("批量补录交易===================>End  pos.Batch_DepWth");
								//输出存储过程响应的信息
								logger.info(Constants.OUT_ERRORNBR + "===========>" + journalMap.get(Constants.OUT_ERRORNBR));//异常信息码
								logger.info(Constants.OUT_ERRORMSG + "===========>" + journalMap.get(Constants.OUT_ERRORMSG));//异常信息
								logger.info(Constants.OUT_ORAERRORMSG  + "===========>" + journalMap.get(Constants.OUT_ORAERRORMSG));//数据库异常信息
								logger.info(Constants.OUT_RTXNNBR  + "===========>" + journalMap.get(Constants.OUT_RTXNNBR));//核心交易号
								logger.info(Constants.OUT_ACCTNO  + "===========>" + journalMap.get(Constants.OUT_ACCTNO));//核心账号
								//输出完毕
							    if ( Constants.PE_ZERO.equals(String.valueOf(journalMap.get(Constants.OUT_ERRORNBR)))){
							    	if(Constants.PE_ONE.equals(mapResult.get(Constants.TRANSEQNO))){
								    	procedureMap.put(Constants.OUT_ERRORNBR,Constants.PE_ZERO);
										procedureMap.put(Constants.PE_HSTSEQNONEW,journalMap.get(Constants.OUT_RTXNNBR));
										procedureMap.put(Constants.PE_POST_DATE, journalMap.get(Constants.IN_EFFDATE));
									}
							    	journalMap.put(Constants.IN_ORIGTRACKNBR, null);// 防止输入原平台流水号
									journalMap.put(Constants.IN_PARENTACCTNBR, journalMap.get(Constants.OUT_ACCTNO));  	//父账号:多笔分录交易和冲正交易，必填
									journalMap.put(Constants.IN_PARENTRTXNNBR, journalMap.get(Constants.OUT_RTXNNBR));    //父交易号:多笔分录交易和冲正交易，必填
//									if(!Constants.PE_ZERO.equals(String.valueOf(paramsMap.get(Constants.PE_RLTSEQNO)))&& //修改原交易状态
//											Constants.PE_ONE.equals(mapResult.get(Constants.TRANSEQNO))){
//										procedureMap.put(Constants.PE_JOURNAL_NO,paramsMap.get(Constants.PE_RLTSEQNO));
//										procedureMap.put(Constants.PE_TRANSTATNEW,Constants.PE_ZERO);
//										if(Constants.rtxnseace_1006.equals(journalMap.get(Constants.IN_RTXNREASONCD)))
//											procedureMap.put(Constants.PE_TRANSTATNEW,Constants.PE_ONE);
//										if(Constants.rtxnseace_1005.equals(journalMap.get(Constants.IN_RTXNREASONCD)))
//											procedureMap.put(Constants.PE_TRANSTATNEW,Constants.PE_TWO);
//										if(Constants.rtxnseace_1003.equals(journalMap.get(Constants.IN_RTXNREASONCD)))
//											procedureMap.put(Constants.PE_TRANSTATNEW,Constants.PE_THREE);
//										if(Constants.rtxnseace_1002.equals(journalMap.get(Constants.IN_RTXNREASONCD)))
//											procedureMap.put(Constants.PE_TRANSTATNEW,Constants.PE_ONE);
//									}
									journalMap.put(Constants.IN_ACCTNBR,paramsMap.get(Constants.PE_ACC_NO));//填充卡号
									if(num.equals(mapResult.get(Constants.TRANSEQNO))&&Constants.BATCH_Fee_Y.equals(paramsMap.get(Constants.PE_FeeYN))){
										deductFee(journalMap,paramsMap, corebankAccess);					//交易记账成功后进行手续费的到扣除
										if (!Constants.PE_Y.equals(paramsMap.get(Constants.DEDUCT_FEE_SUCCESS)))
											throw new PeException("批量补录扣除交易费失败");
									}
									mapResult.clear();
									mapTranTyp.clear();
							    }
							    else{
							    	 if(Constants.Error_1001.equals(String.valueOf(journalMap.get(Constants.OUT_ERRORNBR)))){
							    		 journalMap.put(Constants.PE_EXECYN,Constants.EXEC_Y);//该交易已经存在，不需重复记账,标记成功
							    	 }
									throw new PeException("批量补录存取款交易失败,参数信息:"+journalMap);
							    }
							}
						}
					} catch (PeException e){
						logger.error("deductBatchTranAMT method Error====>"+e.getMessage());
//						transactionTemplate.getTransactionManager().rollback(amtTransaction);
						procedureMap.put(Constants.PE_EXECYN,journalMap.get(Constants.PE_EXECYN));
						procedureMap.put(Constants.OUT_ERRORNBR,Constants.PE_SYSTEM_ERROR);
						throw new PeRuntimeException("批量补录存取款交易失败");
					}
				return null;
				}
			});
	}

	/**
	 * 批量流水扣除手续费操作
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void deductFee(final Map journalMap,final Map paramsMap,JdbcAccessAwareProcessor bankdb)throws PeException {
			Map feeRtxnchalparam = new HashMap();
			Map feeMap = new HashMap();
			Map mapTran=new HashMap();
			feeRtxnchalparam.put(Constants.RTXNSOURCECD, "ATM"); 	// 交易来源
			feeRtxnchalparam.put(Constants.RTXNCATCD, Constants.RTXNCATCD_03); 		// 交易性质
			feeRtxnchalparam.put(Constants.TRANCD, Constants.TRANCD_000000); 		// 交易码
			List feeResultlist = new ArrayList();
			String fee = String.valueOf(paramsMap.get(Constants.TRAN_FEE));
			journalMap.put(Constants.IN_ACCTNBR,paramsMap.get(Constants.PE_ACC_NO));               //填充卡号
			try {
				feeResultlist = dzipProcessTemplate .query_OrgNbr_GlAcctTitleNbr(feeRtxnchalparam); // 取出记账顺序
				for(Iterator feeIt=feeResultlist.iterator();feeIt.hasNext();){
					feeMap = (Map) feeIt.next();
					mapTran=getTranTyp(feeMap);	 // 根据分录顺序判断传入参数
					journalMap.put(Constants.IN_CARDACCTFLAG, mapTran.get(Constants.MEDIUM_TYPE)); 			// 账号/卡号标志
					if(Constants.PE_ONE.equals(String.valueOf(mapTran.get(Constants.MEDIUM_TYPE)))){
						journalMap.put(Constants.IN_ACCTNBR, mapTran.get(Constants.IN_ACCTNBR));
						if(Constants.RTXNCATCD_02.equals(paramsMap.get(Constants.PE_RTXNCATCD))){
							journalMap.put(Constants.IN_INITIALCARDNBR,journalMap.get(Constants.IN_ACCTNBR));
						}else{
							journalMap.put(Constants.IN_INITIALCARDNBR,null);
						}
				    }
				    else
					    journalMap.put(Constants.IN_INITIALCARDNBR,journalMap.get(Constants.IN_ACCTNBR));       //填充卡号
					journalMap.put(Constants.IN_RTXNTYPCD, feeMap.get(Constants.RTXNTYPCD)); 	// 交易类型
					journalMap.put(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   		// 资金类型
					journalMap.put(Constants.IN_BALAMT, fee);							// 扣款金额
					journalMap.put(Constants.IN_FUNDSAMT, fee);						   // 扣款金额
					journalMap.put(Constants.IN_RTXNREASONCD, Constants.rtxnseace_1004);   	//1004: 手续费
					logger.info("批量补录交易==================>start to procedure Batch_DepWth,paramters:"+journalMap);
					bankdb.getSqlMap().update("pos.Batch_DepWth",journalMap); 			// 调用取款核心存储过程。
					logger.info(Constants.OUT_ACCTNO + "===========>"+ journalMap.get(Constants.OUT_ACCTNO)); 					// 账号
					logger.info(Constants.OUT_RTXNNBR + "===========>"+ journalMap.get(Constants.OUT_RTXNNBR));					// 核心交易流水号
					logger.info(Constants.OUT_ERRORNBR + "===========>"+ journalMap.get(Constants.OUT_ERRORNBR));					// 异常信息码
					logger.info(Constants.OUT_ERRORMSG + "===========>"+ journalMap.get(Constants.OUT_ERRORMSG));					// 异常信息
					logger.info(Constants.OUT_ORAERRORMSG + "===========>"+ journalMap.get(Constants.OUT_ORAERRORMSG));				// 数据库异常信息
					if ( Constants.PE_ZERO.equals(String.valueOf(journalMap.get(Constants.OUT_ERRORNBR)))) {
						journalMap.put(Constants.IN_ACCTNBR,paramsMap.get(Constants.PE_ACC_NO));//填充卡号
						paramsMap.put(Constants.DEDUCT_FEE_SUCCESS, Constants.PE_Y);		//扣除交易费是否成功： Y 成功，N 失败
						feeMap.clear();
						mapTran.clear();
					} else {
						paramsMap.put(Constants.DEDUCT_FEE_SUCCESS, Constants.PE_N);			    //扣除交易费是否成功： Y 成功，N 失败
						logger.error("批量补录交易扣除手续费交易失败,参数信息:"+journalMap);
						break;
					}
				}
			} catch (PeException e) {
				throw new PeException(e.getMessage());
			}
	}
	/**
	 * @desc 批量流水核心记账接口
	 *@author xujin
	 *@date 2011.04.25
	 * @param ctx 传入参数
	 */
	@SuppressWarnings("unchecked")
	public void deductOfflinePreAuthComp(final Map paramsMap) throws PeException{
		final TransactionTemplate transactionTemplate =corebankAccess.getTransactionTemplate();
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus amtTransaction) {
					Map rtxnchalparam =  new HashMap();
					Map mapResult=new HashMap();
					Map mapTranTyp=new HashMap();
					List resultlist =new ArrayList();
					try {
					    Map preAuthMap = new HashMap();
						if(paramsMap.get(Constants.IN_PREFLAG)!=null){//执行冻结,解冻帐户功能
							preAuthMap = Init.initBatchPreAuth(paramsMap, preAuthMap);  	//初始化
							try {
								    logger.info("补入离线预授权完成交易:先解冻===========>start to procedure pos.batchPreAuth,paramers:"+preAuthMap);
									corebankAccess.getSqlMap().update("pos.batchPreAuth", preAuthMap); // 调用取款核心存储过程。
									//输出存储过程响应的信息
									logger.info(Constants.OUT_OPTNBR + "===========>" + preAuthMap.get(Constants.OUT_OPTNBR));
									logger.info(Constants.OUT_ERROR_NBR + "===========>" + preAuthMap.get(Constants.OUT_ERROR_NBR));
									logger.info(Constants.OUT_ORA_ERROR_MSG + "===========>" + preAuthMap.get(Constants.OUT_ORA_ERROR_MSG));
									//输出完毕
									paramsMap.put(Constants.PE_HOST_RESP_CD, Constants.PE_OK); //响应码
									if (!Constants.PE_ZERO.equals(String.valueOf(preAuthMap.get(Constants.OUT_ERROR_NBR)))){	//数据库操作成功
								    	 throw new PeException("补录离线预授权完成交易解冻交易失败,参数信息===>"+preAuthMap);
									}
							} catch (Exception e) {
								    throw new PeException(e.getMessage());
							}
						}
						if(paramsMap.get(Constants.IN_PREFLAG)==null||Constants.PE_ONE.equals(paramsMap.get(Constants.IN_PREFLAG))){
							rtxnchalparam.put(Constants.RTXNSOURCECD,paramsMap.get(Constants.IN_RTXNSOURCECD));   //交易来源
							rtxnchalparam.put(Constants.RTXNCATCD,paramsMap.get(Constants.PE_RTXNCATCD));			//交易性质
							rtxnchalparam.put(Constants.TRANCD,paramsMap.get(Constants.PE_OUT_TRANCD));			//交易码
							resultlist = dzipProcessTemplate.query_OrgNbr_GlAcctTitleNbr(rtxnchalparam);   //取出记账顺序
							String num=String.valueOf(resultlist.size());
							for(Iterator it=resultlist.iterator();it.hasNext();){
								mapResult = (Map)it.next();
								mapTranTyp=getTranTyp(mapResult);												//根据分录顺序判断传入参数
								paramsMap.put(Constants.IN_RTXNTYPCD, mapResult.get(Constants.RTXNTYPCD)); 	// 交易类型
								paramsMap.put(Constants.IN_CARDACCTFLAG, mapTranTyp.get(Constants.MEDIUM_TYPE)); // 账号/卡号标志
								if(Constants.PE_ONE.equals(String.valueOf(mapTranTyp.get(Constants.MEDIUM_TYPE)))){
									paramsMap.put(Constants.IN_ACCTNBR, mapTranTyp.get(Constants.IN_ACCTNBR)); // 账号
									if(Constants.RTXNCATCD_02.equals(paramsMap.get(Constants.PE_RTXNCATCD))){
										paramsMap.put(Constants.IN_INITIALCARDNBR,paramsMap.get(Constants.IN_ACCTNBR));
									}else{
										paramsMap.put(Constants.IN_INITIALCARDNBR,null);
									}
								}
								else
								{
									paramsMap.put(Constants.IN_INITIALCARDNBR,paramsMap.get(Constants.IN_ACCTNBR));//填充卡号
								}
								logger.info("补录离线预授权完成交易===============>Start procedure pos.Batch_DepWth,paramers:"+paramsMap);
								corebankAccess.getSqlMap().update("pos.Batch_DepWth", paramsMap); // 调用取款核心存储过程。
								logger.info("补录离线预授权完成交易===================>End  pos.Batch_DepWth");
								//输出存储过程响应的信息
								logger.info(Constants.OUT_ERRORNBR + "===========>" + paramsMap.get(Constants.OUT_ERRORNBR));//异常信息码
								logger.info(Constants.OUT_ERRORMSG + "===========>" + paramsMap.get(Constants.OUT_ERRORMSG));//异常信息
								logger.info(Constants.OUT_ORAERRORMSG  + "===========>" + paramsMap.get(Constants.OUT_ORAERRORMSG));//数据库异常信息
								logger.info(Constants.OUT_RTXNNBR  + "===========>" + paramsMap.get(Constants.OUT_RTXNNBR));//核心交易号
								logger.info(Constants.OUT_ACCTNO  + "===========>" + paramsMap.get(Constants.OUT_ACCTNO));//核心账号
								//输出完毕
							    if ( Constants.PE_ZERO.equals(String.valueOf(paramsMap.get(Constants.OUT_ERRORNBR)))){
							    	paramsMap.put(Constants.PE_HOST_RESP_CD, Constants.PE_OK); //响应码
							    	if(Constants.PE_ONE.equals(mapResult.get(Constants.TRANSEQNO))){
										paramsMap.put(Constants.PE_HOST_SEQ_NO,paramsMap.get(Constants.OUT_RTXNNBR)); //主机交易流水号
										paramsMap.put(Constants.PE_RLTACCTNO,paramsMap.get(Constants.OUT_ACCTNO)); //相关账号
									}
							    	paramsMap.put(Constants.IN_ORIGTRACKNBR, null);// 防止输入原平台流水号
							    	paramsMap.put(Constants.IN_PARENTACCTNBR, paramsMap.get(Constants.OUT_ACCTNO));  	//父账号:多笔分录交易和冲正交易，必填
							    	paramsMap.put(Constants.IN_PARENTRTXNNBR, paramsMap.get(Constants.OUT_RTXNNBR));    //父交易号:多笔分录交易和冲正交易，必填
							    	paramsMap.put(Constants.IN_ACCTNBR,paramsMap.get(Constants.PE_ACC_NO));//填充卡号
									mapResult.clear();
									mapTranTyp.clear();
							    }
							    else{
									throw new PeException("补录离线预授权完成交易失败,参数信息:"+paramsMap);
							    }
							}
						}
					} catch (PeException e){
						logger.error("deductOfflinePreAuthComp method Error====>"+e.getMessage());
						paramsMap.put(Constants.PE_HOST_RESP_CD, Constants.PE_SYSTEM_ERROR); //响应码
						paramsMap.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易状态：成功
						throw new PeRuntimeException("补录离线预授权完成交易失败");
					}
				return null;
				}
			});
	}

	/**
	 * 银联数据交易流水差错扣除手续费操作
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doICJournalFee(Context ctx,JdbcAccessAwareProcessor bankdb,String type8583)throws PeException {
			Map feeRtxnchalparam = new HashMap();
			Map feeMap = new HashMap();

			feeRtxnchalparam.put(Constants.RTXNSOURCECD, Constants.CLM); 	// 交易来源
			feeRtxnchalparam.put(Constants.RTXNCATCD, Constants.RTXNCATCD_03); 		// 交易性质
			feeRtxnchalparam.put(Constants.TRANCD, Constants.TRANCD_000000); 		// 交易码
			List feeResultlist = new ArrayList();
			String fee = ctx.getString(Constants.TRAN_FEE);
			try {
				feeResultlist = dzipProcessTemplate .query_OrgNbr_GlAcctTitleNbr(feeRtxnchalparam); // 取出记账顺序
				for(Iterator feeIt = feeResultlist.iterator();feeIt.hasNext();){
					feeMap = (Map) feeIt.next();
					getTranTyp(ctx, feeMap,type8583); // 根据分录顺序判断传入参数
					ctx.setData(Constants.IN_RTXNTYPCD, feeMap.get(Constants.RTXNTYPCD)); 	// 交易类型
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   		// 资金类型
					ctx.setData(Constants.IN_RTXNREASONCD, Constants.rtxnseace_1004);   	//1004: 手续费
					Map feeProcedureMap = Init.initICJournalCharge(ctx);	//填充调用核心存/取款验证存储过程参
					feeProcedureMap.put(Constants.IN_BALAMT, fee);							// 扣款金额
					feeProcedureMap.put(Constants.IN_FUNDSAMT, fee);						// 扣款金额
					logger.info("银联数据交易流水差错扣除手续费===============>Start procedure pos.Batch_DepWth,paramers:"+feeProcedureMap);
					corebankAccess.getSqlMap().update("pos.Batch_DepWth", feeProcedureMap); // 调用取款核心存储过程。
					logger.info("银联数据交易流水差错扣除手续费===================>End  pos.Batch_DepWth");
					//输出存储过程响应的信息
					logger.info(Constants.OUT_ERRORNBR + "===========>" + feeProcedureMap.get(Constants.OUT_ERRORNBR));//异常信息码
					logger.info(Constants.OUT_ERRORMSG + "===========>" + feeProcedureMap.get(Constants.OUT_ERRORMSG));//异常信息
					logger.info(Constants.OUT_ORAERRORMSG  + "===========>" + feeProcedureMap.get(Constants.OUT_ORAERRORMSG));//数据库异常信息
					logger.info(Constants.OUT_RTXNNBR  + "===========>" + feeProcedureMap.get(Constants.OUT_RTXNNBR));//核心交易号
					logger.info(Constants.OUT_ACCTNO  + "===========>" + feeProcedureMap.get(Constants.OUT_ACCTNO));//核心账号
					//输出完毕
					if (Constants.PE_ZERO.equals(String.valueOf(feeProcedureMap.get(Constants.OUT_ERRORNBR)))) {
						ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_OK);//响应码
						ctx.setData(Constants.DEDUCT_FEE_SUCCESS, Constants.PE_Y);	//扣除交易费是否成功： Y 成功，N 失败
						feeMap.clear();
					} else {
						ctx.setData(Constants.DEDUCT_FEE_SUCCESS, Constants.PE_N);	//扣除交易费是否成功： Y 成功，N 失败
						logger.error("银联数据交易流水差错扣除手续费失败,参数信息:"+feeProcedureMap);
						break;
					}
				}
			} catch (PeException e) {
					 throw new PeException(e.getMessage());
			}
	}

	/**
	 * @desc 银联数据交易流水差错记账
	 *@author xujin
	 *@date 2013.09.04
	 * @param ctx 传入参数
	 */
	@SuppressWarnings("unchecked")
	public void doICJournalCharge(final Context ctx) throws PeException{
		final TransactionTemplate transactionTemplate =corebankAccess.getTransactionTemplate();
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus amtTransaction) {
					Map map = new HashMap();
					Map rtxnchalparam =  new HashMap();
					rtxnchalparam.put(Constants.RTXNSOURCECD,ctx.getData(Constants.IN_RTXNSOURCECD));//交易来源
					rtxnchalparam.put(Constants.RTXNCATCD,ctx.getData(Constants.RTXNCATCD));	//交易性质
					rtxnchalparam.put(Constants.TRANCD,ctx.getData(Constants.PE_OUT_TRANCD));//交易码
					List resultlist =new ArrayList();
					boolean icDeduct = true;	//更新电子钱包余额标志
					try {
						resultlist = dzipProcessTemplate.query_OrgNbr_GlAcctTitleNbr(rtxnchalparam);   //取出记账分录顺序
						String num =String.valueOf(resultlist.size());//分录个数
						for(Iterator it = resultlist.iterator();it.hasNext();){
							map = (Map)it.next();
							getTranTyp(ctx, map,Constants.ISO8583);	//根据分录顺序判断传入参数
							ctx.setData(Constants.IN_RTXNTYPCD, map.get(Constants.RTXNTYPCD)); 			// 交易类型
							Map procedureMap = Init.initICJournalCharge(ctx);	//填充调用核心存/取款验证存储过程参
							logger.info("银联数据交易流水差错记账===============>Start procedure pos.Batch_DepWth,paramers:"+procedureMap);
							corebankAccess.getSqlMap().update("pos.Batch_DepWth", procedureMap); // 调用取款核心存储过程。
							logger.info("银联数据交易流水差错记账===================>End  pos.Batch_DepWth");
							//输出存储过程响应的信息
							logger.info(Constants.OUT_ERRORNBR + "===========>" + procedureMap.get(Constants.OUT_ERRORNBR));//异常信息码
							logger.info(Constants.OUT_ERRORMSG + "===========>" + procedureMap.get(Constants.OUT_ERRORMSG));//异常信息
							logger.info(Constants.OUT_ORAERRORMSG  + "===========>" + procedureMap.get(Constants.OUT_ORAERRORMSG));//数据库异常信息
							logger.info(Constants.OUT_RTXNNBR  + "===========>" + procedureMap.get(Constants.OUT_RTXNNBR));//核心交易号
							logger.info(Constants.OUT_ACCTNO  + "===========>" + procedureMap.get(Constants.OUT_ACCTNO));//核心账号
							//输出完毕
							 if (Constants.PE_ZERO.equals(String.valueOf(procedureMap.get(Constants.OUT_ERRORNBR)))){
								ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_OK);//响应码
								if(Constants.PE_ONE.equals(map.get(Constants.TRANSEQNO))){
									ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); //交易状态：成功
									ctx.setData(Constants.PE_HOST_SEQ_NO,procedureMap.get(Constants.OUT_RTXNNBR)); //主机交易流水号
									ctx.setData(Constants.IN_PARENTACCTNBR, procedureMap.get(Constants.OUT_ACCTNO));  	//父账号:多笔分录交易和冲正交易，必填
									ctx.setData(Constants.IN_PARENTRTXNNBR, procedureMap.get(Constants.OUT_RTXNNBR));    //父交易号:多笔分录交易和冲正交易，必填
								}
								if(num.equals(map.get(Constants.TRANSEQNO))&&Constants.PE_Y.equals(ctx.getData(Constants.DEDUCT_FEE))){
									doICJournalFee(ctx, corebankAccess,Constants.ISO8583);	//交易记账成功后进行手续费的到扣除
									if (Constants.PE_Y.equals(ctx.getData(Constants.DEDUCT_FEE_SUCCESS)))
										ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); // 交易状态：成功
									else {
										icDeduct = false;
										throw new PeException("扣除手续费交易失败");
									}
								}
								map.clear();
							 }else if(Constants.Error_1001.equals(String.valueOf(procedureMap.get(Constants.OUT_ERROR_NBR)))){
								 //核心已经记账
								 ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_OK);//响应码
								 ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); //交易状态：成功
								 icDeduct = false;
								 break;
					    	 }else{
								 icDeduct = false;
								 ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_SYSTEM_ERROR);    //响应码
								 throw new PeException((String)procedureMap.get(Constants.OUT_ERRORMSG));
							 }
						}
						if(icDeduct) {	//更新电子钱包余额
							Map procMap = new HashMap();
							procMap.put(Constants.IN_MEDIUMID, ctx.getData(Constants.ISO8583_ACCTNO));
							procMap.put(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.IN_RTXNSOURCECD));
							procMap.put(Constants.IN_TRANCD, ctx.getData(Constants.PE_OUT_TRANCD));
							procMap.put(Constants.IN_AMT, ctx.getData(Constants.PE_TRAN_AMT));
							logger.info("====================>Start procedure pos.updICEWALLETCTL:"+ctx.getData(Constants.PE_JOURNAL_NO));
							logger.info("====================>pos.updICEWALLETCTL Paramer:" + procMap);
							corebankAccess.getSqlMap().update("pos.updICEWALLETCTL", procMap); // 调用核心更新电子钱包余额存储过程
							logger.info("====================>End procedure pos.updICEWALLETCTL:"+ctx.getData(Constants.PE_JOURNAL_NO));
							//输出存储过程响应的信息
							logger.info(Constants.OUT_RESPONCD + "===========>" + procMap.get(Constants.OUT_RESPONCD));  //核心响应码
							logger.info(Constants.OUT_ERRORNBR + "===========>" + procMap.get(Constants.OUT_ERRORNBR));//异常信息码
							logger.info(Constants.OUT_ERRORMSG + "===========>" + procMap.get(Constants.OUT_ERRORMSG));//异常信息
							ctx.setData(Constants.PE_HOST_RESP_CD, procMap.get(Constants.OUT_RESPONCD)); //响应码
							if (!(Constants.PE_OK.equals(procMap.get(Constants.OUT_RESPONCD))
									&& Constants.PE_ZERO.equals(String.valueOf(procMap.get(Constants.OUT_ERRORNBR))))){
//								throw new PeException("更新电子钱包余额失败");
								throw new PeException("更新电子钱包余额失败:" + procMap.get(Constants.OUT_ERRORMSG));
							}
						}
					} catch (PeException e) {
							logger.error("DoICJournalCharge method Error====>"+e.getMessage());
							ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_TWO);           //交易状态：失败
							ctx.setData(Constants.PE_HOST_RESP_CD, ctx.getData(Constants.PE_HOST_RESP_CD));    //响应码
							throw new PeRuntimeException(e.getMessage());
					}
				    return null;
				}
			});
	}

	/**
	 * @desc 查询电子钱包余额
	 *@author longt
	 *@date 2013.10.11
	 * @param ctx 传入参数
	 */
	@SuppressWarnings("unchecked")
	public void queryICEWALLETCTL(final Context ctx) throws PeException{
		try {
			Map procedureMap = new HashMap();
			procedureMap.put(Constants.IN_MEDIUMID, ctx.getData(Constants.ACCTNO));
			logger.info("银联数据交易流水差错记账===============>Start procedure pos.QueryICEWALLETCTL,paramers:"+procedureMap);
			corebankAccess.getSqlMap().update("pos.QueryICEWALLETCTL", procedureMap);
			logger.info("银联数据交易流水差错记账===================>End  pos.QueryICEWALLETCTL");
			//输出存储过程响应的信息
			logger.info(Constants.OUT_ERRORNBR + "===========>" + procedureMap.get(Constants.OUT_ERRORNBR));//异常信息码
			logger.info(Constants.OUT_ERRORMSG + "===========>" + procedureMap.get(Constants.OUT_ERRORMSG));//异常信息
			logger.info(Constants.OUT_BALAMT  + "===========>" + procedureMap.get(Constants.OUT_BALAMT));//数据库异常信息
			//输出完毕
			 if (Constants.PE_ZERO.equals(String.valueOf(procedureMap.get(Constants.OUT_ERRORNBR)))){
				 ctx.setData(Constants.BALAMT, procedureMap.get(Constants.OUT_BALAMT));
	    	 }else{
				 throw new PeException((String)procedureMap.get(Constants.OUT_ERRORMSG));
			 }
		} catch (PeException e) {
			logger.error("queryICEWALLETCTL method Error====>"+e.getMessage());
			throw new PeRuntimeException(e.getMessage());
		}
	}

	/**
	 * @author chenshq
	 * @since 2010.07.22
	 * @用途：修改流水交易状态为成功状态。
	 * @param ctx
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public void responseForReversal(final Context ctx,final Map procedureMap )throws DataAccessException {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				if (procedureMap.get(Constants.OUT_XML) != null) {
					String out_XML=procedureMap.get(Constants.OUT_XML).toString();
					logger.info("本行交易返回报文OUT_XML==>" + out_XML);
					Map respMap = xmlProcedureParser.parse(out_XML);
					// 解析存储过程返回的XML格式报文
					ctx.setData(Constants.ISO8583_RESCODE, respMap.get(Constants.HOST_RESPONSE));             //获得响应码，并返回
					ctx.setData(Constants.PE_HOST_RESP_CD, respMap.get(Constants.HOST_RESPONSE));             //获得响应码，并返回
					ctx.setData(Constants.ISO8583_ADDDATA, respMap.get(Constants.DWTP_TRAN_RESPONSE_DESCRIP));//获得响应信息，并返回
					ctx.setData(Constants.ISO8583_BALAMT, respMap.get(Constants.DWTP_BALANCE_CURR));          //获得帐户余额，并返回
					/*如果响应码是 00，修改交易流水的状态为成功  ；0：表示成功；*/
					if (Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);                              //成功状态
						try {
							String systemDate=dzipProcessTemplate.querysysdate();
							procedureMap.put(Constants.PE_LAST_UPDATE_TIME,systemDate); //最后修改时间
							dzipProcessTemplate.updJournal(procedureMap);                      //更新交易流水中的状态
							logger.info("Update the status of " + procedureMap.get(Constants.PE_JOURNAL_NO)+ " Reversal!");
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}else
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);            //异常状态
				}else{
					logger.error("============>本行交易返回报文为空,参数信息:"+procedureMap);    //返回报文为空时
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
					ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_SYSTEM_ERROR);      //获得响应码，并返回
					ctx.setData(Constants.ISO8583_ADDDATA, "system is error!");             //填充响应信息，并返回
				}
				return null;
			}});
//		updateJournal(ctx);// 修改交易流水状态
	}

	/**
	 * @author chenshaoqi
	 * @用途：处理改密操作后的报文返回
	 * @param ctx
	 * @param procedureMap
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public Context ModPwdProcesser(final Context ctx,final Map procedureMap)throws DataAccessException{
		String msgTyp = String.valueOf(ctx.getData(Constants.ISO8583_HEAD_TX_TYPE)); //消息类型
		String repMsgTyp = getResMsgTyp(msgTyp);
		ctx.setData(Constants.ISO8583_HEAD_TX_TYPE, repMsgTyp);       //填充返回消息类型0110
		ctx.setData(Constants.ISO8583_RESCODE, procedureMap.get(Constants.OUT_RESPONCODE));  //响应码
		ctx.setData(Constants.PE_HOST_RESP_CD, procedureMap.get(Constants.OUT_RESPONCODE));  //响应码
		Map map=new HashMap();
		if (Constants.PE_OK.equals(procedureMap.get(Constants.OUT_RESPONCODE))) { //修改交易流水状态为成功
			logger.info("===================>Modify passsword success!");
			map.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);                    //成功 状态
			ctx.setData(Constants.ISO8583_ADDDATA, "Modify passsword success!");
		}else{                                                                          //返回错误信息时
			logger.error("Modify passsword faild,paramers:"+procedureMap);
			map.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);                       //失败状态
			ctx.setData(Constants.ISO8583_ADDDATA, "Modify passsword faild!");
		}
		try {
			if(Constants.PE_59.equals(ctx.getData(Constants.PE_HOST_RESP_CD)))//如果是欺诈就调用欺诈存储过程记账欺诈次数并返回响应码
			{
				dzipProcessTemplate.updFraudInfo(ctx);
			}
			map.put(Constants.PE_JOURNAL_NO, ctx.getData(Constants.PE_JOURNAL_NO)); //交易流水号
			map.put(Constants.PE_HOST_RESP_CD,ctx.getData(Constants.PE_HOST_RESP_CD)); //主机响应码
			dzipProcessTemplate.updJournal(map);
			convertRespCD(ctx);
			logger.info("Update the status of " + ctx.getData(Constants.PE_JOURNAL_NO)+ " successful!");
		} catch (Exception e) {
			logger.error("ModPwdProcesser updJournal error============>"+e.getMessage());    //返回报文为空时
		}
		return ctx;
	}

	/**
	 * @author chenshaoqi
	 * @since 2010.08.09
	 * @dispcription 8583传入的处理码、消息类型与核心存储过程的不一致， 用于二者之间的转换
	 * @param outProdCD 外部处理码
	 * @param outMsgTyp 外部消息类型
	 * @param pramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getInprocdAndMsgTyp(final Map paramMap){
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Map sqlMap = new HashMap();
				sqlMap.put(Constants.PROC_CODE, paramMap.get(Constants.ISO8583_TRANSACTION_ID)); // sqlmap填入外部处理码
				sqlMap.put(Constants.MSG_TYPE, paramMap.get(Constants.MSG_TYPE));   // 外部消息类型
				sqlMap.put(Constants.PE_REQ_CHANN, paramMap.get(Constants.PE_REQ_CHANN));// 请求渠道
				sqlMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.queryInprocd", sqlMap); // 调用相关的SQL
				String inprocd = String.valueOf(sqlMap.get(Constants.IN_PRO_CD)); // 取出处理码
				String inmsgtyp = String.valueOf(sqlMap.get(Constants.IN_MSG_TYP)); // 取出消息类型
				String returnMsgtyp = String.valueOf(sqlMap.get(Constants.RESP_MSG_TYP)); // 取出返回消息类型
				paramMap.put(Constants.PROC_CODE, inprocd); // 处理码
				paramMap.put(Constants.MSG_TYPE, inmsgtyp); // 信息类型
				paramMap.put(Constants.RESP_MSG_TYP, returnMsgtyp);// 返回信息类型
				return null;
			}
		});
		return paramMap;
	}
	/**
	 * @author chenshaoqi
	 * @since 2010.09.30
	 * @dispcription 查询pos交易的返回消息类型
	 * @return 返回消息类型
	 */
	@SuppressWarnings("unchecked")
	public String getResMsgTyp(String msgTyp){
		Map resultMap = new HashMap();
		String resMsgTyp;
		resultMap = (Map) dzipdbAccess.getSqlMap().queryForObject("pos.queryResMsgTyp", msgTyp); // 调用相关的SQL
		if(  resultMap != null && resultMap.get(Constants.RESP_MSG_TYP) !=null){
			resMsgTyp = (String)resultMap.get(Constants.RESP_MSG_TYP);// 返回信息类型
		}else{
			resMsgTyp = Constants.PE_NULL;
		}
		return resMsgTyp;
	}

	/**
	 * @author chenshaoqi
	 * @since 2010.09.30
	 * @dispcription 查询pos交易的返回消息类型
	 * @return 返回消息类型
	 */
	@SuppressWarnings("unchecked")
	public void convertRespCD(Context ctx){
		Map paramMap = new HashMap();
		if(ctx.getData(Constants.ISO8583_RESCODE)!=null){
			paramMap.put(Constants.RESPCD, ctx.getData(Constants.ISO8583_RESCODE));
			paramMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getResp", paramMap); // 调用相关的SQL
			ctx.setData(Constants.ISO8583_RESCODE, paramMap.get(Constants.CHANNRESPCD));
			ctx.setData(Constants.ISO8583_ADDDATA, paramMap.get(Constants.RESPNAME));
		}
		if(ctx.getData(Constants.FIX8583_RESP)!=null){
			paramMap.put(Constants.RESPCD, ctx.getData(Constants.FIX8583_RESP));
			paramMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getResp", paramMap); // 调用相关的SQL
			ctx.setData(Constants.FIX8583_RESP, paramMap.get(Constants.CHANNRESPCD));
			ctx.setData(Constants.FIX8583_ADDI, paramMap.get(Constants.RESPNAME));
			ctx.setData(Constants.ISO8583_ADDDATA, paramMap.get(Constants.RESPNAME));
		}
	}
	/**
	 * @desc 根据平台交易流水查询核心交易流水
	 * @param origSysTraNumber 平台交易流水
	 * @return  核心交易流水
	 */
	@SuppressWarnings("unchecked")
	public String getRtxnNbr(final String origSysTraNumber) {
		Map sqlMap = new HashMap();
		String rtxnNbr = null;
		try {
			sqlMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getRtxnNbr", origSysTraNumber); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("getRtxnNbr method error,syseqno:"+origSysTraNumber+",message:"+e.getMessage());
		}
		if(sqlMap !=null)
			rtxnNbr = String.valueOf(sqlMap.get(Constants.OUT_RTXNNBR)); // 取出核心交易流水号
		return rtxnNbr;
	}
	/**
	 * @desc 根据系统跟踪号交易流水查询平台交易流水
	 * @param origSysTraNumber 平台交易流水
	 * @return  平台交易流水
	 */
	@SuppressWarnings("unchecked")
	public Map getJournalNO( Map sqlMap ) {
		Map map= new HashMap();
		try {
			map = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getJournalNO", sqlMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("getJournalNO method error,sqlMap:"+sqlMap+",message:"+e.getMessage());
		}
		return map;
	}

	/**
	 * 通过转入圈存冲正获取转账圈存冲正交易信息
	 * @param sqlMap 转入圈存冲正交易信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getJournalInfoByTransferInReversal(Map sqlMap){
		Map map = new HashMap();
		try {
			map  = (Map) dzipdbAccess.getSqlMap().queryForObject(SqlMaps.COMMON_GETJOUNAL_BY_TRANSINREVER, sqlMap);
		} catch (Exception e) {
			logger.error("通过转入圈存冲正获取转账圈存冲正流水出错!" + e.getMessage());
		}

		return map;
	}

	/**
	 * @desc 根据平台交易流水号查询平台交易流水信息
	 * @param  journalNO平台交易流水号
	 * @return  平台交易流水信息
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public Map getJournalInfo(String journalNO) throws PeException {
		Map sqlMap = new HashMap();
		try {
			sqlMap =(Map)dzipdbAccess.getSqlMap().queryForObject("common.getJournalInfo", journalNO); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("getJournalInfo method error,journalNO:"+journalNO+",message:"+e.getMessage());
		}
		if(sqlMap==null||sqlMap.isEmpty())
				throw new PeException("没有找到原始交易,前置交易号"+journalNO);
        return sqlMap;
	}

	/**
	 * @desc 根据原始数据获得上笔数据
	 * @param  ctx
	 * @return  count是否存在
	 */
	@SuppressWarnings("unchecked")
	public String  verifyOrigDate(final Context ctx) {
		String count="0";
		String channelId = String.valueOf(ctx.getData(Constants.PE_REQ_CHANN));
		String origDataElement = null;
		if(ctx.getData(Constants.ISO8583_ORGDATA)!=null)
			origDataElement = String.valueOf(ctx.getData(Constants.ISO8583_ORGDATA));// 获得90#上笔交易的信息
		else
			origDataElement = String.valueOf(ctx.getData(Constants.FIX8583_ORIG));  // 获得90#上笔交易的信息
		String origSysTraNumber = origDataElement.substring(4, 10); 			    // 原交易系统跟踪号
		String origTranDateTime = origDataElement.substring(10,20); 			    // 原交易系统跟踪号
		String origAcquInstituIdCd = origDataElement.substring(23, 31);             // 原交易受理机构标识码
		String origiForwInstituIdCd = origDataElement.substring(34, 42);            // 原交易发送机构标识码
		Map sqlMap=new HashMap();
		sqlMap.put(Constants.PE_CHANNELID, channelId);
		sqlMap.put(Constants.PE_ORIGSYSTRANUMBER, origSysTraNumber);
		sqlMap.put(Constants.PE_ORIGTRANDATETIME, origTranDateTime);
		sqlMap.put(Constants.PE_ORIGACQUINSTITUIDCD, origAcquInstituIdCd);
		sqlMap.put(Constants.PE_ORIGIFORWINSTITUIDCD, origiForwInstituIdCd);
		if(ctx.getData(Constants.ISO8583_ACCTNO)!=null)
			sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
		else
			sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.FIX8583_PAN));
		sqlMap.put(Constants.PE_TRANS_STAT,Constants.PE_ZERO);
		String strSQL="common.verifyOrigDate";
		if(ctx.getData(Constants.IN_MEDIAACCTSEQNO)!=null&&
				Constants.rtxnseace_1003.equals(ctx.getData(Constants.IN_MEDIAACCTSEQNO))){//pos消费退货
			strSQL="common.verifyNoPosRet";
			sqlMap.put(Constants.PE_OUT_TRANCD, ctx.getData(Constants.TransactionId));
		}
		try {
			count =(String) dzipdbAccess.getSqlMap().queryForObject(strSQL, sqlMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("verifyOrigDate method error,sqlMap:"+sqlMap+",message:"+e.getMessage());
		}
        return count;
	}
	/**
	 * @desc 获得POS消费退货总金额
	 * @param  ctx
	 * @return  strSumAMT是否存在
	 */
	@SuppressWarnings("unchecked")
	public String  getOrigPosRetSumAMT(final Context ctx) {
		String strSumAMT="0";
		String channelId = String.valueOf(ctx.getData(Constants.PE_REQ_CHANN));
		String origDataElement = null;
		if(ctx.getData(Constants.ISO8583_ORGDATA)!=null)
			origDataElement = String.valueOf(ctx.getData(Constants.ISO8583_ORGDATA));// 获得90#上笔交易的信息
		else
			origDataElement = String.valueOf(ctx.getData(Constants.FIX8583_ORIG));  // 获得90#上笔交易的信息
		String origSysTraNumber = origDataElement.substring(4, 10); 			    // 原交易系统跟踪号
		String origTranDateTime = origDataElement.substring(10,20); 			    // 原交易系统跟踪号
		String origAcquInstituIdCd = origDataElement.substring(23, 31);             // 原交易受理机构标识码
		String origiForwInstituIdCd = origDataElement.substring(34, 42);            // 原交易发送机构标识码
		Map sqlMap=new HashMap();
		sqlMap.put(Constants.PE_CHANNELID, channelId);
		sqlMap.put(Constants.PE_ORIGSYSTRANUMBER, origSysTraNumber);
		sqlMap.put(Constants.PE_ORIGTRANDATETIME, origTranDateTime);
		sqlMap.put(Constants.PE_ORIGACQUINSTITUIDCD, origAcquInstituIdCd);
		sqlMap.put(Constants.PE_ORIGIFORWINSTITUIDCD, origiForwInstituIdCd);
		if(ctx.getData(Constants.ISO8583_ACCTNO)!=null)
			sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
		else
			sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.FIX8583_PAN));
		sqlMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);
		try {
			strSumAMT =(String) dzipdbAccess.getSqlMap().queryForObject("common.getOrigPosRetSumAMT", sqlMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("getOrigPosRetSumAMT method error,sqlMap:"+sqlMap+",message:"+e.getMessage());
		}
        return strSumAMT;
	}
	/**
	 * @desc 根据原始数据获得上笔数据
	 * @param  sqlMap
	 * @return  count是否存在
	 */
	@SuppressWarnings("unchecked")
	public String  verifyOrigDate(Map sqlMap) {
		String count="0";
		sqlMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);
		try {
			count =(String) dzipdbAccess.getSqlMap().queryForObject("common.verifyOrigDate", sqlMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("verifyOrigDate method error,sqlMap:"+sqlMap+",message:"+e.getMessage());
		}
        return count;
	}


	/**
	 * @desc 根据批量交易流水号查询批量交易流水信息是否执行
	 * @param  journalNO平台交易流水号
	 * @return  执行标记
	 */
	public String  verifyJournalBatch(String journalNO) {
		String count="0";
		try {
			count =(String) dzipdbAccess.getSqlMap().queryForObject("common.verifyJournalBatch", journalNO); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("verifyJournalBatch method error,journalNO:"+journalNO+",message:"+e.getMessage());
		}
        return count;
	}

	/**
	 * 传入渠道类型，业务性质，交易种类查询机构号和科目号
	 * 然后根据机构号和科目号获取对应的内部账号
	 * @param paramMap
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public String getDirectPostAcctNbr(Map paramMap) throws SQLException{
		//根据机构号和科目号查询内部账号
		String ownAcctSubNbr = dzipProcessTemplate.query_directPostAcctNbr(paramMap);// 查询参数 ：内部账账号
		return ownAcctSubNbr;
	}

	/**获取原交易流水号
	 * @param ctx
	 * @return
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public Map getOriSysTraNum(final Context ctx) throws PeException {
		String channelId = String.valueOf(ctx.getData(Constants.PE_REQ_CHANN));
		String origDataElement = null;
		if(ctx.getData(Constants.ISO8583_ORGDATA)!=null)
			origDataElement = String.valueOf(ctx.getData(Constants.ISO8583_ORGDATA));// 获得90#上笔交易的信息
		else
			origDataElement = String.valueOf(ctx.getData(Constants.FIX8583_ORIG));  // 获得90#上笔交易的信息
		String origSysTraNumber = origDataElement.substring(4, 10); 			    // 原交易系统跟踪号
		String origTranDateTime = origDataElement.substring(10,20); 			    // 原交易系统跟踪号
		String origAcquInstituIdCd = origDataElement.substring(23, 31);             // 原交易受理机构标识码
		String origiForwInstituIdCd = origDataElement.substring(34, 42);            // 原交易发送机构标识码
		Map sqlMap=new HashMap();
		sqlMap.put(Constants.PE_CHANNELID, channelId);
		sqlMap.put(Constants.PE_ORIGSYSTRANUMBER, origSysTraNumber);
		sqlMap.put(Constants.PE_ORIGTRANDATETIME, origTranDateTime);
		sqlMap.put(Constants.PE_ORIGACQUINSTITUIDCD, origAcquInstituIdCd);
		sqlMap.put(Constants.PE_ORIGIFORWINSTITUIDCD, origiForwInstituIdCd);
		if(ctx.getData(Constants.ISO8583_ACCTNO)!=null)
			sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
		else
			sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.FIX8583_PAN));
		Map resultMap = getJournalNO(sqlMap);
		if(resultMap !=null && resultMap.get(Constants.JOURNAL_NO)!=null){
			origSysTraNumber = String.valueOf(resultMap.get(Constants.JOURNAL_NO)); // 取出核心交易流水号
			ctx.setData(Constants.PE_ORIGSTAT, resultMap.get(Constants.TRANSTAT));// 取出交易状态
		}
		else
			origSysTraNumber = null;
		if(origSysTraNumber==null){
			if(ctx.getData(Constants.ISO8583_ORGDATA)!=null)
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_25);
			else
				ctx.setData(Constants.FIX8583_RESP, Constants.PE_25);
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_25); // 主机响应码
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
			logger.error("getOriSysTraNum method error======>无法找到原始交易平台流水号,sqlMap:"+sqlMap);
			throw new PeException("无法找到原始交易平台流水号");
		}
		return resultMap;
	}
	/**
	 * 根据数据库获得交易类型确定交易的参数
	 * @param ctx
	 * @param map
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public void getTranTyp(final Context ctx, Map map,String type8583) throws PeException {
		if(map.get(Constants.ORGNBR)==null || map.get(Constants.GLACCTTITLENBR)==null ){  //若无机构号和科目号则记客户帐
			ctx.setData(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_2); 	  //介质号：2 卡号
			if(Constants.ISO8583.equals(type8583))
				ctx.setData(Constants.IN_ACCTNBR, ctx.getData(Constants.ISO8583_ACCTNO));//用户卡
			if(Constants.FIX8583.equals(type8583))
				ctx.setData(Constants.IN_ACCTNBR, ctx.getData(Constants.FIX8583_PAN));//用户卡号
			ctx.setData(Constants.IN_INITIALCARDNBR, ctx.getData(Constants.IN_ACCTNBR));//用户卡号
			if(Constants.PE_TWO.equals((String)map.get(Constants.TRANSEQNO))//获得转账交易的转入卡号
					&&Constants.TRANCD_020040.equals(ctx.getString(Constants.TransactionId)))
			{
				ctx.setData(Constants.CHECK_TRACK2, Constants.PE_N);                //不需要验证2磁
				ctx.setData(Constants.IN_ACCTNBR, ctx.getData(Constants.ISO8583_ACCIDE_N2));//转入卡号
				ctx.setData(Constants.IN_INITIALCARDNBR, ctx.getData(Constants.IN_ACCTNBR));//转入卡号
			}
		}
		else {
			ctx.setData(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_1);//介质号： 1  帐号
			String directPostAcctNbr = dzipProcessTemplate.query_directPostAcctNbr(map);
			if (directPostAcctNbr != null) {  //记内部帐之前获取内部帐户
				ctx.setData(Constants.IN_ACCTNBR, directPostAcctNbr);         // 内部帐号
				if(Constants.RTXNCATCD_02.equals(ctx.getData(Constants.RTXNCATCD))){//本带他传卡号
					if(Constants.ISO8583.equals(type8583))
						ctx.setData(Constants.IN_INITIALCARDNBR, ctx.getData(Constants.ISO8583_ACCTNO));//用户卡
					if(Constants.FIX8583.equals(type8583))
						ctx.setData(Constants.IN_INITIALCARDNBR, ctx.getData(Constants.FIX8583_PAN));//用户卡号
				}else{
					ctx.setData(Constants.IN_INITIALCARDNBR,Constants.PE_NULL);  //空值
				}
			} else{
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_SYSTEM_ERROR );    //响应码
				throw new PeException("找不到内帐户,参数:"+map);
			}
		}
	}
	/**
	 * 根据数据库获得交易类型确定交易的参数
	 * @param ctx
	 * @param map
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public Map getTranTyp(Map map) throws PeException {
		Map mapRes=new HashMap();
		if(map.get(Constants.ORGNBR)==null || map.get(Constants.GLACCTTITLENBR)==null ){  //若无机构号和科目号则记客户帐
			mapRes.put(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_2); 	  //介质号：2 卡号
		}
		else {
			mapRes.put(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_1);//介质号： 1  帐号
			String directPostAcctNbr = dzipProcessTemplate.query_directPostAcctNbr(map);
			if (directPostAcctNbr != null) {  //记内部帐之前获取内部帐户
				mapRes.put(Constants.IN_ACCTNBR, directPostAcctNbr);         // 内部帐号
			} else{
				throw new PeException("找不到内帐户,参数:"+map);
			}
		}
		return mapRes;
	}
	/**
	 * @desc 根据平台交易流水查询90域元数据
	 * @param authrid 平台交易流水
	 * @return  预授权冻结编号
	 */
	@SuppressWarnings("unchecked")
	public Map getOrgData(Object peJournalNO) {
		Map sqlMap = new HashMap();
		try {
			sqlMap.put(Constants.PE_JOURNAL_NO, peJournalNO);
			sqlMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getOrgData",	sqlMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("getOrgData method error,journalNO:"+peJournalNO+",message:"+e.getMessage());
		}
		if (sqlMap != null)
			return sqlMap;
		else
			return null;
	}

	/**
	 * @desc 根据平台交易流水查询预授权冻结编号
	 * @param authrid 平台交易流水
	 * @return  预授权冻结编号
	 */
	@SuppressWarnings("unchecked")
	public Map getOptNbr(String authrid) {
		Map sqlMap = new HashMap();
		try {
			sqlMap = (Map) dzipdbAccess.getSqlMap().queryForObject("pos.getOptNbr",	authrid); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("getOptNbr method error,journalNO:"+authrid+",message:"+e.getMessage());
		}
		if (sqlMap != null)
			return sqlMap;
		else
			return null;
	}

	/**
	 * 初始化参数，调用计算手续费接口
	 * @param ctx
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public void  calcTranFee(final Context ctx) throws PeException{
		Map sqlMap = new HashMap();
		if(ctx.getData(Constants.ISO8583_ACQCODE)!=null)
			sqlMap.put(Constants.IN_ACQINSTID,ctx.getData(Constants.ISO8583_ACQCODE));
		else
			sqlMap.put(Constants.IN_ACQINSTID, ctx.getData(Constants.FIX8583_ACQU));
		if(ctx.getData(Constants.ISO8583_MERCHANTTYPE)!=null)
			sqlMap.put(Constants.IN_MERTYPCD,(String)ctx.getData(Constants.ISO8583_MERCHANTTYPE));
		else
			sqlMap.put(Constants.IN_MERTYPCD,(String)ctx.getData(Constants.FIX8583_MERC));

		//如果是同城交易 将地区码传入
		if(ctx.getData(Constants.IS_CITYWIDE_TRAN) != null
		   && ctx.getData(Constants.IS_CITYWIDE_TRAN).equals(Constants.PE_NUM_ONE)){//如果是同城交易
			sqlMap.put(Constants.IN_ACQINSTID, ctx.getData(Constants.AREAREGIONCD));
		}
		sqlMap.put(Constants.IN_TRANAMT, String.valueOf(ctx.getData(Constants.PE_TRAN_AMT)));
		sqlMap.put(Constants.IN_FARMERFLAG, String.valueOf(ctx.getData(Constants.PE_REACODE)));
		sqlMap.put(Constants.IN_RTXNCATCD, ctx.getData(Constants.RTXNCATCD));
		sqlMap.put(Constants.IN_POSTDATE, ctx.getData(Constants.PE_POST_DATE));
		sqlMap.put(Constants.IN_TRANCD, ctx.getData(Constants.TransactionId));
		sqlMap.put(Constants.IN_OUTSIDEFLAG, ctx.getData(Constants.PE_OUTSIDEFLAG));
		sqlMap.put(Constants.IN_ACCTNO, ctx.getData(Constants.PE_ACC_NO));
		calcTranFee(sqlMap);
		if(sqlMap!=null&&Constants.PE_ZERO.equals(String.valueOf(sqlMap.get(Constants.OUT_ERROR_NBR)))){
			ctx.setData(Constants.TRAN_FEE, sqlMap.get(Constants.OUT_FEEAMT));	//普通卡的手续费
			ctx.setData(Constants.DEDUCT_FEE, sqlMap.get(Constants.OUT_FEEYN));	//是否需要扣除交易费： Y 需要，N 不需要
		}else
		    throw new PeException("用计算手续费接口出错,参数:"+sqlMap);
	}
	/**
	 * 计算手续费
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public Map  calcTranFee(Map paramMap){
		try {
			dzipdbAccess.getSqlMap().update("common.calcTranFee",paramMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		if (paramMap != null)
			return paramMap;
		else
			return null;
	}

	/**
	 * 初始化参数，调用验证金额限额函数
	 * @param ctx
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public void  valTranLimit(final Context ctx) throws PeException{
		Map sqlMap = new HashMap();
		if(ctx.getData(Constants.ISO8583_MERCHANTTYPE)!=null)
			sqlMap.put(Constants.IN_MERTYPCD,(String)ctx.getData(Constants.ISO8583_MERCHANTTYPE));
		else
			sqlMap.put(Constants.IN_MERTYPCD,(String)ctx.getData(Constants.FIX8583_MERC));
		if(ctx.getData(Constants.ISO8583_COUNTRYCD)!=null)
			sqlMap.put(Constants.IN_FWDORGCD, ctx.getData(Constants.ISO8583_COUNTRYCD));
		else
			sqlMap.put(Constants.IN_FWDORGCD, ctx.getData(Constants.FIX8583_COUNTRYCD));
		sqlMap.put(Constants.IN_TRANAMT, String.valueOf(ctx.getData(Constants.PE_TRAN_AMT)));
		sqlMap.put(Constants.IN_RTXNCATCD, ctx.getData(Constants.RTXNCATCD));

		sqlMap.put(Constants.IN_POSTDATE, ctx.getData(Constants.PE_POST_DATE));
		sqlMap.put(Constants.IN_TRANCD, ctx.getData(Constants.TransactionId));
		sqlMap.put(Constants.IN_OUTSIDEFLAG, ctx.getData(Constants.PE_OUTSIDEFLAG));
		sqlMap.put(Constants.IN_CHANNELTYPCD, ctx.getData(Constants.IN_CHANNELTYPCD));
		sqlMap.put(Constants.IN_TRANLAUNCHWAYCD, ctx.getData(Constants.IN_TRANLAUNCHWAYCD));
		sqlMap.put(Constants.IN_BUSITYP, ctx.getData(Constants.IN_BUSITYP));
		sqlMap.put(Constants.IN_ACCTNO, ctx.getData(Constants.PE_ACC_NO));
		sqlMap.put(Constants.IN_TRANFEE, ctx.getData(Constants.TRAN_FEE));
		valTranLimit(sqlMap);
		if(sqlMap!=null&&Constants.PE_ZERO.equals(String.valueOf(sqlMap.get(Constants.OUT_ERROR_NBR)))){
		    ctx.setData(Constants.PE_HOST_RESP_CD, sqlMap.get(Constants.OUT_RESPONCD));
		    if(!Constants.PE_OK.equals(ctx.getString(Constants.PE_HOST_RESP_CD))){
			    ctx.setData(Constants.ISO8583_RESCODE, sqlMap.get(Constants.OUT_RESPONCD));
			    throw new PeException("验证不通过,响应吗为:"+ctx.getString(Constants.PE_HOST_RESP_CD));
		    }
		}else
		    throw new PeException("调用验证金额限额接口出错,参数:"+sqlMap);
	}
	/**
	 * 验证金额限额
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public Map  valTranLimit(Map paramMap){
		try {
			dzipdbAccess.getSqlMap().update("common.valTranLimit",paramMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		if (paramMap != null)
			return paramMap;
		else
			return null;
	}
	/**
	 * @desc 根据原始数据查询平台交易流水
	 * @param origSysTraNumber 平台交易流水
	 * @return  平台交易流水
	 */
	@SuppressWarnings("unchecked")
	public Map getBatchJournalNO( Map sqlMap ) {
		Map map= new HashMap();
		try {
			map = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getBatchJournalNO", sqlMap); // 调用相关的SQL
		} catch (DataAccessException e) {
			logger.error("getBatchJournalNO method error,sqlMap:"+sqlMap+",message:"+e.getMessage());
		}
		return map;
	}

	/**
	 * @author xujin
	 * @since 2011.4.25
	 * @param paramsMap   传入参数
	 * @param postDate    传入参数
	 * @param peJournalNO 传入参数
	 * @param journalMap  传入参数
	 */
	@SuppressWarnings({"unchecked" })
	public Map getJournalBatchMap(final Map paramsMap, Map journalMap) throws PeException{
		try {
			String outtrancd=String.valueOf(paramsMap.get(Constants.PE_OUT_TRANCD));
			if(Constants.PE_06.equals(outtrancd.substring(outtrancd.length()-2, outtrancd.length()))||  //预授权类交易
					Constants.PE_60.equals(outtrancd.substring(outtrancd.length()-2, outtrancd.length()))){
				Map map=new HashMap();
				journalMap.put(Constants.IN_HOLDFALG,Constants.FREEZE);//冻结
				journalMap.put(Constants.PE_PRE_AMT,Init.initHoldAmt(String.valueOf(paramsMap.get(Constants.PE_TRAN_AMT))));
				journalMap.put(Constants.IN_OPTNBR,Constants.PE_ZERO);
				if(outtrancd.equals(Constants.TRANCD_01000306)||outtrancd.equals(Constants.TRANCD_04200306)
						||outtrancd.equals(Constants.TRANCD_01002006)||outtrancd.equals(Constants.TRANCD_04200360)
						||outtrancd.equals(Constants.TRANCD_01000360)){
					journalMap.put(Constants.IN_PREFLAG,Constants.PE_ZERO);//只冻结或解冻不记账
					if(!outtrancd.equals(Constants.TRANCD_01000306)&&!outtrancd.equals(Constants.TRANCD_01000360)){
						String journalNO=String.valueOf(paramsMap.get(Constants.PE_RLTSEQNO));
						map=getJournalInfo(journalNO);
						journalMap.put(Constants.IN_OPTNBR,String.valueOf(map.get(Constants.PE_OPTNBR)));
						journalMap.put(Constants.IN_HOLDFALG,Constants.UNFREEZE);//解冻
						journalMap.put(Constants.PE_RLTSEQNO, journalNO);
					}
				}else{
					if(outtrancd.equals(Constants.TRANCD_02000006)||outtrancd.equals(Constants.TRANCD_02200006)){//预授权完成,预授权结算通知(离线)
						journalMap.put(Constants.IN_RTXNREASONCD,Constants.rtxnseace_1005);
					}
					journalMap.put(Constants.IN_PREFLAG,Constants.PE_ONE);//即冻结或解冻又记账
					journalMap.put(Constants.IN_HOLDFALG,Constants.UNFREEZE);//解冻
					String journalNO=String.valueOf(paramsMap.get(Constants.PE_RLTSEQNO));
					journalMap.put(Constants.PE_RLTSEQNO, journalNO);
					map=getJournalInfo(journalNO);
					journalMap.put(Constants.IN_OPTNBR,String.valueOf(map.get(Constants.PE_OPTNBR)));
					if(outtrancd.equals(Constants.TRANCD_04200006)||        //预授权完成冲正
							outtrancd.equals(Constants.TRANCD_02002006)){//预授权完成撤销
						journalMap.put(Constants.IN_HOLDFALG,Constants.FREEZE);//冻结
						journalNO=String.valueOf(map.get(Constants.PE_RLTSEQNO));
						journalMap.put(Constants.PE_RLTSEQNO, journalNO);
						map.clear();
						map=getJournalInfo(journalNO);
						journalMap.put(Constants.PE_PRE_AMT,Init.initHoldAmt(String.valueOf(map.get(Constants.PE_TRAN_AMT))));
						if(outtrancd.equals(Constants.TRANCD_02002006)){
						journalMap.put(Constants.IN_RTXNREASONCD,Constants.rtxnseace_1006);
						}
					}
					if(outtrancd.equals(Constants.TRANCD_04202006)){//预授权[完成]撤销冲正
						journalMap.put(Constants.IN_PREFLAG,Constants.PE_ZERO);//只冻结或解冻不记账
						journalMap.put(Constants.IN_HOLDFALG,Constants.FREEZE);//冻结
						journalNO=String.valueOf(map.get(Constants.PE_RLTSEQNO));
						journalMap.put(Constants.PE_RLTSEQNO, journalNO);
						map.clear();
						map=getJournalInfo(journalNO);
						journalMap.put(Constants.PE_PRE_AMT,Init.initHoldAmt(String.valueOf(map.get(Constants.PE_TRAN_AMT))));
						if(Constants.MSG_TYPE_0200.equals(paramsMap.get(Constants.PE_MSG_TYP))){//预授权完成撤销冲正
							journalMap.put(Constants.IN_PREFLAG,Constants.PE_ONE);//即冻结或解冻又记账
							journalMap.put(Constants.IN_HOLDFALG,Constants.UNFREEZE);//解冻
							journalNO=String.valueOf(map.get(Constants.PE_RLTSEQNO));
							journalMap.put(Constants.PE_RLTSEQNO, journalNO);
							map.clear();
							map=getJournalInfo(journalNO);
							journalMap.put(Constants.IN_OPTNBR,String.valueOf(map.get(Constants.PE_OPTNBR)));
						}
					}
				}
			}
			journalMap.put(Constants.IN_ORIGPOSTDATE, paramsMap.get(Constants.PE_POST_DATE));   //填充原来交易日期
			journalMap.put(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);                                            //填充交易状态：C
			journalMap.put(Constants.IN_ORIGTRACKNBR, null);
			journalMap.put(Constants.IN_PARENTACCTNBR, null);       	                            //填充父账号
			journalMap.put(Constants.IN_PARENTRTXNNBR, null);       	                            //填充父交易号
			journalMap.put(Constants.IN_PARENTRTXNTYPCD,null);
			journalMap.put(Constants.IN_INITIALCARDNBR,null);
			journalMap.put(Constants.IN_TXNSEQNBR,null);                             //交易序号
			journalMap.put(Constants.IN_TRACENBR,paramsMap.get(Constants.PE_JOURNAL_NO));           //填充平台流水号
			journalMap.put(Constants.IN_POSTYN,Constants.PE_Y);
			journalMap.put(Constants.IN_COMMITYN,Constants.PE_N);
			journalMap.put(Constants.IN_DEBUGYN,Constants.PE_N);
			journalMap.put(Constants.IN_CASHANALYSISCD,null);
		    journalMap.put(Constants.IN_RTXNREASONCD,Constants.PE_NULL);
			journalMap.put(Constants.IN_ACCTNBR, paramsMap.get(Constants.PE_ACC_NO));       	     //填充账号
			journalMap.put(Constants.IN_NBROFITEMS, Constants.PE_ONE);       	                     //填充资金类型顺序号
			journalMap.put(Constants.IN_CLEARCATCD, "IMED");       	                                 //填充清算类别
			journalMap.put(Constants.IN_BALCATCD, "NOTE");                                           //余额大类
			journalMap.put(Constants.IN_BALTYPCD, "BAL");                                            //余额类型
			journalMap.put(Constants.IN_BALCATARRAYSIZE,Constants.PE_NUM_ZERO);                      //余额类型数组长度
			journalMap.put(Constants.IN_FUNDTYPARRAYSIZE,Constants.PE_NUM_ZERO);                      //资金类型数组长度
			journalMap.put(Constants.IN_FUNDSAMT,paramsMap.get(Constants.PE_TRAN_AMT));              //填充资金金额
			journalMap.put(Constants.IN_BALAMT,paramsMap.get(Constants.PE_TRAN_AMT));                //填充本金发生额
			if(journalMap.get(Constants.PE_PRE_AMT)!=null){
				BigDecimal preAmt=Util.getAmount(String.valueOf(journalMap.get(Constants.PE_PRE_AMT)));
				BigDecimal tranAmt =Util.getAmount(String.valueOf(paramsMap.get(Constants.PE_TRAN_AMT)));//记账金额
				//if(tranAmt >preAmt){  //如果记账金额大于预授权金额的1.15倍就按预授权金额的1.15倍记账
				if(tranAmt.compareTo(preAmt) > 0){  //如果记账金额大于预授权金额的1.15倍就按预授权金额的1.15倍记账
					journalMap.put(Constants.IN_FUNDSAMT,preAmt);              //填充资金金额
					journalMap.put(Constants.IN_BALAMT,tranAmt);                //填充本金发生额
				}
			}
			paramsMap.put(Constants.PE_FeeYN,Constants.BATCH_Fee_N);//标记不是手续费交易
			if(paramsMap.get(Constants.TRAN_FEE)!=null&&Constants.PE_ZERO.equals(paramsMap.get(Constants.TRAN_FEE))){
				paramsMap.put(Constants.PE_FeeYN,Constants.BATCH_Fee_Y);//标记是手续费交易
			}
			String rtxnsouceFix=null;
			if(Constants.PE_ATMP.equals(paramsMap.get(Constants.PE_REQ_CHANN))){
				if(Constants.CRDB_X.equals(paramsMap.get(Constants.PE_CRDB))){
					journalMap.put(Constants.IN_FUNDTYPCD,Constants.TRS_FUND_TYP_EL);
					journalMap.put(Constants.IN_FUNDTYPDTLCD,Constants.FUND_TYP_DTL_INTER);
				}else{
					journalMap.put(Constants.IN_FUNDTYPCD,Constants.TRS_FUND_TYP_CASH);
					journalMap.put(Constants.IN_FUNDTYPDTLCD,Constants.FUND_TYP_DTL_ALL);
				}
				journalMap.put(Constants.IN_RTXNSOURCECD,"ATM");
				if(Constants.RTXNCATCD_02.equals(paramsMap.get(Constants.PE_RTXNCATCD))){
						if(Constants.PE_JINCHENG.equals(dzipProcessTemplate.getChannID()))
							rtxnsouceFix="J";
						else
							rtxnsouceFix="Y";
						if(Constants.MERTYPCD_6011.equals(paramsMap.get(Constants.PE_MER_TYPE))){
							journalMap.put(Constants.IN_RTXNSOURCECD,rtxnsouceFix+"ATM");
						}else{
							journalMap.put(Constants.IN_RTXNSOURCECD,rtxnsouceFix+"POS");
						}
						String trancd=String.valueOf(paramsMap.get(Constants.PE_OUT_TRANCD));
						Map map=new HashMap();
						map.put(Constants.ISO8583_TRANSACTION_ID,trancd); // map填入外部处理码
						map.put(Constants.MSG_TYPE,trancd.substring(0, 4));   // 外部消息类型
						map.put(Constants.PE_REQ_CHANN,paramsMap.get(Constants.PE_REQ_CHANN));// 请求渠道
						map=getInprocdAndMsgTyp(map);            //获得返回消息类型和内部处理码

						trancd=String.valueOf(map.get(Constants.RESP_MSG_TYP))+trancd.substring(4, 6);
						paramsMap.put(Constants.PE_OUT_TRANCD,trancd);
				}
			}else{
				journalMap.put(Constants.IN_FUNDTYPCD,Constants.TRS_FUND_TYP_EL);
				journalMap.put(Constants.IN_FUNDTYPDTLCD,Constants.FUND_TYP_DTL_INTER);
				if(Constants.PE_JINCHENG.equals(paramsMap.get(Constants.PE_REQ_CHANN)))
					rtxnsouceFix="J";
				else
					rtxnsouceFix="Y";
				if(Constants.MERTYPCD_6011.equals(paramsMap.get(Constants.PE_MER_TYPE))){
					journalMap.put(Constants.IN_RTXNSOURCECD,rtxnsouceFix+"ATM");
				}else{
					journalMap.put(Constants.IN_RTXNSOURCECD,rtxnsouceFix+"POS");
				}
				if(Constants.TRANCD_020020.equals(outtrancd)) //POS消费撤销
					journalMap.put(Constants.IN_RTXNREASONCD,Constants.rtxnseace_1002);
				if(Constants.TRANCD_022020.equals(outtrancd)) //POS退货
					journalMap.put(Constants.IN_RTXNREASONCD,Constants.rtxnseace_1003);
			}
			Map map=dzipProcessTemplate.getHostStatPostDate();//获得系统日期和系统状态
			int errornbr=Integer.valueOf(String.valueOf(map.get(Constants.OUT_ERROR_NBR)));
			if(errornbr==0){
				if(Constants.PE_TWO.equals(map.get(Constants.OUT_SYSTEMSTAT))){
					journalMap.put(Constants.PE_EXECYN,Constants.EXEC_N);
					throw new PeException("系统已经离线，不可发生交易");
				}
				journalMap.put(Constants.IN_EFFDATE, map.get(Constants.OUT_POSTDATE));     		//填充帐务日期
			}
			else{
				journalMap.put(Constants.PE_EXECYN,Constants.EXEC_Y);
				throw new PeException("获得系统日期和系统状态错误");
			}
			if(Constants.PE_ZERO.equals(paramsMap.get(Constants.PE_RTXNCATCD))||
					Constants.RTXNCATCD_02.equals(paramsMap.get(Constants.PE_RTXNCATCD))){
				Map mapNode=dzipProcessTemplate.getNtwkNodeInfo(String.valueOf(paramsMap.get(Constants.PE_TRM_CD)),
			    		String.valueOf(map.get(Constants.OUT_POSTDATE)));
				if(Constants.PE_OK.equals(mapNode.get(Constants.OUT_RESPONCD))){
					journalMap.put(Constants.IN_ORGNBR, mapNode.get(Constants.OUT_ORGNBR)); //机构号数据库配置参数
					journalMap.put(Constants.IN_CASHBOXNBR, mapNode.get(Constants.OUT_CASHBOXNBR)); // 钱箱号,数据库配置参数
					journalMap.put(Constants.IN_ORIGPERSNBR,  mapNode.get(Constants.OUT_PERSNBR)); // 柜员号，数据库配置参数
					journalMap.put(Constants.IN_ORIGNTWKNODENBR, mapNode.get(Constants.OUT_NTWKNODENBR)); // 交易站点号，数据库配置参数
				}
				else{
					journalMap.put(Constants.PE_EXECYN,Constants.EXEC_Y);
					throw new PeException("无效终端号==>"+paramsMap.get(Constants.PE_TRM_CD));
				}
			}else{
				journalMap.put(Constants.IN_ORGNBR, InitData4Dzip.getYLOrgNbr()); //机构号数据库配置参数
				journalMap.put(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr()); // 钱箱号,数据库配置参数
				journalMap.put(Constants.IN_ORIGPERSNBR, InitData4Dzip.getYLPersNbr()); // 柜员号，数据库配置参数
				journalMap.put(Constants.IN_ORIGNTWKNODENBR, InitData4Dzip.getJCNtwkNodeNbr()); // 交易站点号，数据库配置参数
			}
			if(Constants.TRANCD_0420.equals(outtrancd.substring(0,4))||          //冲正类交易
					Constants.TRANCD_022047.equals(outtrancd)||                  //确认类交易
					Constants.TRANCD_022021.equals(outtrancd)||
					Constants.TRANCD_022029.equals(outtrancd)){
			    Map sqlMap=new HashMap();
			    sqlMap.put(Constants.PE_CHANNELID, paramsMap.get(Constants.PE_REQ_CHANN));
			    sqlMap.put(Constants.PE_ORIGSYSTRANUMBER, paramsMap.get(Constants.PE_ORIGTRACENUM));
			    sqlMap.put(Constants.PE_ORIGTRANDATETIME, paramsMap.get(Constants.PE_ORIGDATETIME));
			    sqlMap.put(Constants.PE_ORIGACQUINSTITUIDCD, paramsMap.get(Constants.PE_ORIGREQORGCD));
			    sqlMap.put(Constants.PE_ORIGIFORWINSTITUIDCD, paramsMap.get(Constants.PE_ORIGFWDORGCD));
			    sqlMap.put(Constants.PE_PAN, paramsMap.get(Constants.PE_ACC_NO));
			    Map resultMap = getBatchJournalNO(sqlMap);//获得原始平台流水
			    if(resultMap !=null && resultMap.get(Constants.JOURNAL_NO)!=null){
					if(Constants.TRANCD_0420.equals(outtrancd.substring(0,4))){
				        journalMap.put(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_EC);               //冲正 交易状态为EC
				        journalMap.put(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));
					}
				    if(Constants.TRANCD_022047.equals(outtrancd)||
							Constants.TRANCD_022021.equals(outtrancd)||Constants.TRANCD_022029.equals(outtrancd)){
				    	if(resultMap !=null && resultMap.get(Constants.JOURNAL_NO)!=null){
							Map ComfirmMap = new HashMap();
							ComfirmMap.put(Constants.IN_PREFLAG, Constants.PE_ZERO);
							ComfirmMap.put(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);
							ComfirmMap.put(Constants.IN_TRACENBR,String.valueOf(resultMap.get(Constants.JOURNAL_NO)));
							ComfirmMap.put(Constants.IN_EFFDATE,String.valueOf(resultMap.get(Constants.POSTDATE)));
							/* 验证交易是否成功*/
							ComfirmBatchRtxn(ComfirmMap);
							// 如果交易成功，直接返回成功信息。
							if (Constants.PE_ZERO.equals(ComfirmMap.get(Constants.OUT_DEPOSITSUCCYN))){	// 是否已经记账标记 0:记账成功 1：记账失败
								journalMap.put(Constants.PE_EXECYN,Constants.EXEC_Y);
								throw new PeException("已经记账成功 ，不需重复记账");
							}
				    	}
				    }
				}else{
				    journalMap.put(Constants.PE_EXECYN,Constants.EXEC_Y);
				    throw new PeException("无法找到原始交易");
				}
			}
		} catch (PeException e) {
			 throw new PeException(e.getMessage());
		}
		return journalMap;
	}

	/**
	 *获得备份流水信息
	 * @author xujin
	 * @param listRecord
	 * @return int ErrorNbr
	 */
	@SuppressWarnings("unchecked")
    public List GetBackupJournal(Map paramMap) throws PeException{
		List listRecord = new ArrayList();
		logger.info("GetBackupJournal ==================>Start!" );
		try {
		    dzipdbAccess.getSqlMap().queryForObject("common.GetBackupJournal", paramMap);
			if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
				listRecord= (List) paramMap.get(Constants.OUT_REFCURSOR);// 获得结果集
			} else{
				throw new PeException(Errors.DATEBASE_ERROR);
			}
		} catch (Exception e) {
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		logger.info("GetBackupJournal ==================>End!" );
		return listRecord;       //返回结果集
	}
	/**
	 * @desc 调用存储过程确定是否需要补录的公共方法
	 *@author xujin
	 *@date 2011.08.07
	 * @param ComfirmMap 传入参数
	 */
	@SuppressWarnings("unchecked")
	public Map ComfirmBatchRtxn(final Map ComfirmMap){
	try {
			// 调用核心存储过程查询时上笔交易的记录
			 corebankAccess.getSqlMap().update("pos.ComfirmBatchRtxn",ComfirmMap);
		} catch (Exception e) {
			logger.error("pos.ComfirmBatchRtxn====>"+e.getMessage());
		}
		return ComfirmMap;
	}
	/**
	 * @desc 根据批量日期时间获得临时记帐最大日期时间
	 * @param  strMaxDateTime
	 * @return  resultMap
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public Map  GetBatchMaxDateTime(String strMaxDateTime) throws PeException {
		Map paramMap=new HashMap();
		Map resultMap=new HashMap();
		try {
			paramMap.put(Constants.IN_TRANDATETIME,strMaxDateTime);
			List listRe =GetBackupJournal(paramMap);
			for(Iterator iterator = listRe.iterator();iterator.hasNext();){
				Map map = (Map) iterator.next();
				Map ComfirmMap = new HashMap();
				ComfirmMap.put(Constants.IN_PREFLAG, Constants.PE_ZERO);
				ComfirmMap.put(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);
				ComfirmMap.put(Constants.IN_RTXNCATCD,String.valueOf(map.get(Constants.PE_RTXNCATCD)));
				ComfirmMap.put(Constants.IN_TRACENBR,String.valueOf(map.get(Constants.PE_SYSSEQNO)));
				ComfirmMap.put(Constants.IN_EFFDATE,String.valueOf(map.get(Constants.PE_POST_DATE)));
				String outTranCd=String.valueOf(map.get(Constants.PE_OUT_TRANCD));
				if(Constants.TRANCD_0420.equals(outTranCd.substring(0, 4))){
					ComfirmMap.put(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_EC);
					ComfirmMap.put(Constants.IN_TRACENBR,String.valueOf(map.get(Constants.PE_RLTSEQNO)));
				}
				if(outTranCd.equals(Constants.TRANCD_01000306)||outTranCd.equals(Constants.TRANCD_04200306)
						||outTranCd.equals(Constants.TRANCD_01002006)||outTranCd.equals(Constants.TRANCD_04200360)
						||outTranCd.equals(Constants.TRANCD_01000360)){
					ComfirmMap.put(Constants.IN_PREFLAG, Constants.PE_ONE);
					if(outTranCd.equals(Constants.TRANCD_04200306)||outTranCd.equals(Constants.TRANCD_04200360))
						ComfirmMap.put(Constants.IN_TRACENBR,String.valueOf(map.get(Constants.PE_SYSSEQNO)));
				}
				if(outTranCd.equals(Constants.TRANCD_04202006)
						&&Constants.TRANCD_0420.equals(map.get(Constants.PE_MSG_TYP))){
					ComfirmMap.put(Constants.IN_PREFLAG, Constants.PE_ONE);
					ComfirmMap.put(Constants.IN_TRACENBR,String.valueOf(map.get(Constants.PE_SYSSEQNO)));
				}

				/* 验证交易是否成功*/
				ComfirmBatchRtxn(ComfirmMap);
				// 如果交易成功，直接返回成功信息。
				if (Constants.PE_ONE.equals(ComfirmMap.get(Constants.OUT_DEPOSITSUCCYN))){	// 是否已经记账标记 0:记账成功 1：记账失败
					resultMap.put(Constants.PE_LAST_UPDATE_TIME, map.get(Constants.PE_LAST_UPDATE_TIME));
					resultMap.put(Constants.PE_SYSSEQNO, map.get(Constants.PE_SYSSEQNO));
					break;
				}
			}
		} catch (DataAccessException e) {
			logger.error("GetBatchMaxDateTime====>"+e.getMessage());
			throw new PeException(e.getMessage());
		}
        return resultMap;
	}

	public List<Map<String, Object>> getOfflinePreAuthComp(){
	    return dzipdbAccess.getSqlMap().queryForList("common.getOfflinePreAuthComp");
	}

	/**
	 * 通过流水标志获得银联数据记账流水
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getICJournalByChargeFlag(Map paramMap){
	    return dzipdbAccess.getSqlMap().queryForList("common.getICJournalByChargeFlag",paramMap);
	}

	/**
	 * IC卡单笔记账交易流水查询
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSingleJournal(Map paramMap){
		return (Map)dzipdbAccess.getSqlMap().queryForObject("common.getSingleICJournal", paramMap);
	}

	/**
	 * 获得IC卡电子钱包的余额
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getIcCardBal(Map<String, Object> paramMap){
		try {
			corebankAccess.getSqlMap().update(SqlMaps.ATM_ICCARD_BAL, paramMap);
			logger.info(Constants.OUT_BALAMT + "==========>" + paramMap.get(Constants.OUT_BALAMT));
			logger.info(Constants.OUT_ERRORMSG + "==========>" + paramMap.get(Constants.OUT_ERRORMSG));
			logger.info(Constants.OUT_ERRORNBR + "==========>" + paramMap.get(Constants.OUT_ERRORNBR));
		} catch (Exception e) {
			logger.error("查询电子钱包余额出错!" + e.getMessage());
		}
		return paramMap;
	}

	/**
	 * 通过转账圈存流水号找到转入圈存交易流水号
	 * @param rltseqno 转账圈存流水号
	 * @param outtrancd 交易码
	 * @return 转入圈存流水号及22#
	 * @throws PeException
	 */
	public Map<String, Object> getSysseqByRltseqno(String rltseqno, String outtrancd)throws PeException{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.PE_RLTSEQNO, rltseqno);
		paramMap.put(Constants.PE_OUT_TRANCD, outtrancd);
		try {
			map = (Map) dzipdbAccess.getSqlMap().queryForObject(SqlMaps.COMMON_GET_SYSSEQNO_BY_RLTSEQNO, paramMap);
		} catch (Exception e) {
			logger.error("通过转账圈存流水号找到转入圈存交易流水号出错!" + e.getMessage());
		}
		if(Constants.PE_NUM_ZERO == map.size()){
			throw new PeException("通过转账圈存流水号,未找到转入圈存流水号!");
		}

		return map;
	}

	/**
	 * @return the dzipdbAccess
	 */
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	/**
	 * @param dzipdbAccess the dzipdbAccess to set
	 */
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
	/**
	 * @return the corebankAccess
	 */
	public JdbcAccessAwareProcessor getCorebankAccess() {
		return corebankAccess;
	}
	/**
	 * @param corebankAccess the corebankAccess to set
	 */
	public void setCorebankAccess(JdbcAccessAwareProcessor corebankAccess) {
		this.corebankAccess = corebankAccess;
	}
	/**
	 * @return the xmlProcedureParser
	 */
	public XMLPacket4ProcedureParser getXmlProcedureParser() {
		return xmlProcedureParser;
	}
	/**
	 * @param xmlProcedureParser the xmlProcedureParser to set
	 */
	public void setXmlProcedureParser(XMLPacket4ProcedureParser xmlProcedureParser) {
		this.xmlProcedureParser = xmlProcedureParser;
	}
	/**
	 * @return the dzipProcessTemplate
	 */
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	/**
	 * @param dzipProcessTemplate the dzipProcessTemplate to set
	 */
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}

}

