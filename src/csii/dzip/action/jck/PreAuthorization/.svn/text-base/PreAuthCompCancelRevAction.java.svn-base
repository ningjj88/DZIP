
package  csii.dzip.action.jck.PreAuthorization;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description：POS预授权完成撤销冲正
 *@author :chenshq
 *@date：2010-08-16
 */
public class PreAuthCompCancelRevAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		Map paramMap  = new HashMap();
		Map procedureMap = new HashMap();
		String oriMsgType =((String) ctx.getData(Constants.FIX8583_ORIG)).substring(0,4);
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+"POS"); //交易来源
		ctx.setData(Constants.CHECK_TRACK2, Constants.PE_N);                //成商不需要验证二磁
		ctx.setData(Constants.CHECK_PIN, Constants.PE_N);                //成商不需要验证密码
//		System.err.println("oriMsgType=====================>"+ oriMsgType);
//		System.err.println("0200".equals(oriMsgType));
		int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证冲正是否已经发生
		if(count>0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_26); //响应码
			ctx.setData(Constants.FIX8583_RESP, Constants.PE_OK); //响应码
			ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);   	//卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);					// 修改流水交易状态;
		}else{
		    if(Constants.MSG_TYPE_0200.equals(oriMsgType)){
		    	logger.info("JCK PreAuthCompCancelRevAction ==================>Start");
				Map resultMap =utilProcessor.getOriSysTraNum(ctx);//获得预授权完成撤销流水号
				logger.info("JCK预授权完成撤销冲正上笔交易信息(resultMap)==================>"+resultMap);
				if(!Constants.PE_NINE.equals(String.valueOf(resultMap.get(Constants.TRANSTAT)))){
					String rltSeqNo = String.valueOf(resultMap.get(Constants.PE_RLTSEQNO));//获得预授权完成流水号
					Map sqlMap = utilProcessor.getOptNbr(rltSeqNo);		//获得预授权完成流水信息
					logger.info("JCK预授权完成撤销冲正上笔的上笔交易信息(sqlMap)==================>"+sqlMap);
					String authrid=String.valueOf(sqlMap.get(Constants.PE_RLTSEQNO));//获得预授权流水号
					sqlMap.clear();
					sqlMap = utilProcessor.getOptNbr(authrid);		//获得帐户冻结编号和状态
					logger.info("JCK预授权完成撤销冲正上笔的上笔的上笔交易信息(sqlMap)==================>"+sqlMap);
					String in_optNbr = String.valueOf(sqlMap.get(Constants.PE_OPTNBR));
		//			String tranStat = (String)sqlMap.get(Constants.PE_TRANS_STAT);
					ctx.setData(Constants.IN_HOLDFALG, Constants.UNFREEZE); 						//冻结解冻标识:0：冻结 1：解冻
					ctx.setData(Constants.IN_OPTNBR,in_optNbr);  		    						//in_holdFlag标识判断是否必输项 0：为空	1：必输
					ctx.setData(Constants.PE_PRE_AMT,sqlMap.get(Constants.PE_TRAN_AMT));    		//预授权金额
					ctx.setData(Constants.PE_EXECYN, Constants.PE_Y);                       //执行冻结帐户功能

					ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+"POS");
					ctx.setData(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_2); 	// 介质号：2 卡号
					ctx.setData(Constants.ATMYN, Constants.PE_ZERO); 					//是否ATM    ATM ：1，POS：0  
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     //资金类型
      				ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO));	// 原交易主机流水号
					ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO)); // 原平台流水号
					ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_EC);			//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO)	;				//冲正标志:0：正常   1：冲正  2：抹帐
					utilProcessor.deductTranAMT(ctx,Constants.FIX8583);					//执行数据库操作
					ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
					if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
						/*更新预授权交易的状态*/
						sqlMap.clear();
						sqlMap.put(Constants.PE_TRANS_STAT, Constants.PE_TWO);  	    //交易状态:2 已完成
						sqlMap.put(Constants.PE_JOURNAL_NO,authrid);  	//交易状态
						sqlMap.put(Constants.PE_OPTNBR,Constants.PE_ZERO);  	
						updateJoural.execute(sqlMap);  //更新预授权交易中冻结编号
						/*更新预授权完成交易的状态*/
						sqlMap.clear();
						sqlMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);  	    //交易状态:0 成功
						sqlMap.put(Constants.PE_JOURNAL_NO,rltSeqNo);  	//交易状态
						updateJoural.execute(sqlMap);  //更新预授权交易中冻结编号
						/*更新预授权完成撤销和预授权完成撤销冲正两个交易的状态*/
						procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE);   //交易状态: 已冲正
						procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
						procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
						ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
						ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);				//抹去冻结编号
					}
					if(Constants.PE_25.equals(ctx.getData(Constants.FIX8583_RESP))){
						procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE );   //交易状态: 已冲正
						procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
						procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
						ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
						ctx.setData(Constants.FIX8583_RESP,Constants.PE_OK);           	//响应码
						ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR); //卡有效期
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);        //交易状态：成功
					}
                    updateJoural.execute(ctx,procedureMap);					// 修改流水交易状态;
				}else{
					ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_26);   //响应码
					ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
					ctx.setData(Constants.FIX8583_RESP,Constants.PE_OK);           	//响应码
                    ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);    //卡有效期
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);        //交易状态：成功
				}
				updateJoural.execute(ctx,procedureMap);					// 修改流水交易状态;
			}else{//预授权撤销冲正
				logger.info("JCK PreAuthCancelRevAction ==================>Start");
				ctx.setData(Constants.PE_CRDB,Constants.PE_NULL);
				Map resultMap =utilProcessor.getOriSysTraNum(ctx);//获取原交易流水号
				logger.info("JCK预授权撤销冲正上笔交易信息(resultMap)==================>"+resultMap);
				String authrid = String.valueOf(resultMap.get(Constants.PE_RLTSEQNO));//获得预授权
				Map sqlMap = utilProcessor.getOptNbr(authrid);		//获得帐户冻结编号和状态
				logger.info("JCK预授权撤销冲正上笔的上笔交易信息(sqlMap)==================>"+sqlMap);
				String in_optNbr = String.valueOf(sqlMap.get(Constants.PE_OPTNBR));
				String tranStat =String.valueOf(sqlMap.get(Constants.PE_TRANS_STAT));
				if(!Constants.PE_ZERO.equals(tranStat)){
					ctx.setData(Constants.IN_HOLDFALG, Constants.FREEZE); 			//冻结解冻标识:0：冻结 1：解冻
					ctx.setData(Constants.IN_OPTNBR,in_optNbr);  					//in_holdFlag标识判断是否必输项 0：为空	1：必输
					ctx.setData(Constants.PE_PRE_AMT,sqlMap.get(Constants.PE_TRAN_AMT));    //预授权金额
					paramMap = Init.initPreAuth(ctx, paramMap,Constants.FIX8583);  			//初始化
					utilProcessor.preAuthDoAction(ctx, paramMap);    				//执行预授权
					ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
					ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
					if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
						sqlMap.clear();
						sqlMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);  	//交易状态
						sqlMap.put(Constants.PE_JOURNAL_NO,authrid);  	//交易状态
						sqlMap.put(Constants.PE_OPTNBR,ctx.getData(Constants.PE_OPTNBR));  	//交易状态
						updateJoural.execute(sqlMap);  //更新预授权交易中冻结编号
						procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE);   //交易状态: 已冲正
						procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
						procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
						ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);				//抹去冻结编号
					}
					updateJoural.execute(ctx,procedureMap);						// 修改流水交易状态;
				}else{
					ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);   //响应码
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);  //交易状态：成功
					ctx.setData(Constants.FIX8583_RESP,Constants.PE_OK);      //响应码
					ctx.setData(Constants.FIX8583_EXPI,Constants.PE_EXPIREYEAR);//卡有效期
					ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);				//抹去冻结编号
					updateJoural.execute(ctx);						// 修改流水交易状态;
				}
			}
		}
		
	}

	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}


	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}

	}
