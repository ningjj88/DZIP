
package  csii.dzip.action.jck.PreAuthorization;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description：POS预授权结算通知
 *@author :xujin
 *@date：2011-05-20
 */
public class PreAuthSettInformAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK PreAuthSettInformAction ==================>Start");
		Map procedureMap  = new HashMap();
		String authrid = ctx.getData(Constants.FIX8583_AUTH).toString();//获得预授权
		Map sqlMap = utilProcessor.getOptNbr(authrid);		//获得帐户冻结编号和状态
		logger.info("JCK预授权结算通知上笔交易信息(sqlMap)==================>"+sqlMap);
		ctx.setData(Constants.PE_RLTSEQNO,authrid);           //原交易流水号
		if(sqlMap!=null){
			String in_optNbr = (String)sqlMap.get(Constants.PE_OPTNBR);		//预授权冻结编号
			String tranStat = (String)sqlMap.get(Constants.PE_TRANS_STAT);  //预授权状态
			BigDecimal preAmt = Util.getAmount(String.valueOf(sqlMap.get(Constants.PE_TRAN_AMT)));//预授权金额
			BigDecimal tranAmt = Util.getAmount(String.valueOf(ctx.getData(Constants.PE_TRAN_AMT)));////预授权完成金额
			//if(tranAmt <=((preAmt*0.15)+preAmt) ){  //预授权完成的金额小于预授权金额的1.15倍
			if(tranAmt.compareTo(preAmt.multiply(new BigDecimal("0.15")).add(preAmt)) <=0){  //预授权完成的金额小于预授权金额的1.15倍
				if(Constants.PE_ZERO.equals(tranStat)){              //0：表示预授权成功
					ctx.setData(Constants.PE_RLTSEQNO,authrid);           			//原交易流水号
					ctx.setData(Constants.IN_HOLDFALG, Constants.UNFREEZE); 		//冻结解冻标识:0：冻结 1：解冻
					ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS); 	//交易来源
					ctx.setData(Constants.IN_OPTNBR,in_optNbr);  		    //in_holdFlag标识判断是否必输项 0：为空	1：必输
					ctx.setData(Constants.PE_PRE_AMT,sqlMap.get(Constants.PE_TRAN_AMT));    //预授权金额
					ctx.setData(Constants.PE_EXECYN, Constants.PE_Y);                       //执行冻结帐户功能
					
					ctx.setData(Constants.IN_DEPFLAG, Constants.TRS_FLAG_WTH); 					// 出入账标识 0：出账  1：入账
					ctx.setData(Constants.ATMYN, Constants.PE_ZERO); 							//是否ATM  ATM ：1，POS：0
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     		// 资金类型
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);				    //交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);						//冲正标志:0：正常   1：冲正 	2：抹帐
					ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1005);  //1005：预授权完成
					utilProcessor.deductTranAMT(ctx,Constants.FIX8583);					//执行数据库操作
					ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
					if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){  //客户帐扣除成功
						procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_TWO );   //交易状态， 2：完成
						procedureMap.put(Constants.PE_JOURNAL_NO, authrid);    //原交易流水号
						procedureMap.put(Constants.PE_OPTNBR,Constants.PE_ZERO); 
//							procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
						ctx.setData(Constants.PE_RLTSEQNO,authrid);           	//原交易流水号
						ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);
					}
					updateJoural.execute(ctx,procedureMap); 												// 修改流水交易状态
				}else{//预授权状态已不是成功状态
					switch(Integer.valueOf(tranStat)){
					case 1:						//1:表示预授权已经撤销
						logger.info("==================>JCK预授权结算通知上笔交易已经撤销");
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：成功
						ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
						ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);    //响应码
						ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
						break;								
					case 2:						//2:表示预授权已经完成
						logger.info("==================>JCK预授权结算通知上笔交易已经完成");
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：成功
						ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
						ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);    //响应码
						ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
						break;								
					default :
						logger.info("==================>JCK预授权结算通知上笔交易已经异常");
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
						ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
						ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);   //响应码
						ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
						break;
					}
					updateJoural.execute(ctx);						// 修改流水交易状态;
				}
			}else{//预授权完成的金额大于预授权金额的1.15倍时，交易失败
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：成功
				ctx.setData(Constants.FIX8583_RESP,Constants.PE_61);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_61);    //响应码
				ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
				updateJoural.execute(ctx);						// 修改流水交易状态;
			}
		}else{//预授权已经过期（超过30天在前置流水中找不到该记录）
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：成功
			ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
			ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);    //响应码
			ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
			updateJoural.execute(ctx);						// 修改流水交易状态;
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
