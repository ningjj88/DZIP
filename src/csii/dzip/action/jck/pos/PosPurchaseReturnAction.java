
package csii.dzip.action.jck.pos;

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
 *@Description: 他行存款：CDM
 *@author :chenshq
 *@date：2010-09-07
 */
public class PosPurchaseReturnAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK PosPurchaseReturnAction ==================>Start");
		ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1003); //1003：POS消费退货
		int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证冲正是否已经发生
		if(count>0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
			ctx.setData(Constants.FIX8583_RESP, Constants.PE_22); //响应码
			ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);   	//卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);					// 修改流水交易状态;
		}else{
			Map paramMap = new HashMap();
			Map resultMap =utilProcessor.getOriSysTraNum(ctx);
			logger.info("JCK消费退货上笔交易信息(resultMap)==================>"+resultMap);
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
			ctx.setData(Constants.IN_DEPFLAG, Constants.TRS_FLAG_DEP); 			// 出入账标识 0：出账  1：入账
			ctx.setData(Constants.ATMYN, Constants.PE_ZERO); 					//是否ATM  ATM ：1，POS：0
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
			ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1003); //1003：POS消费退货
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_THREE);				//冲正标志 3：消费撤销
			BigDecimal tranAmt = Util.getAmount(String.valueOf(resultMap.get(Constants.PE_TRAN_AMT)));//原消费金额
			BigDecimal returnAmt = Util.getAmount(String.valueOf(ctx.getData(Constants.PE_TRAN_AMT)));//单笔退货金额
			BigDecimal retSumAmt = Util.getAmount(utilProcessor.getOrigPosRetSumAMT(ctx));//已经退货总金额
			//if(retSumAmt+returnAmt<=tranAmt){  //若退货金额不大于原消费金额，准予退货
			if(retSumAmt.add(returnAmt).compareTo(tranAmt)<= 0){  //若退货金额不大于原消费金额，准予退货
				ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO));// 原交易主机流水号
				ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO)); 			// 原平台流水号
				ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
				utilProcessor.deductTranAMT(ctx,Constants.FIX8583);					//执行数据库操作
				ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
				if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
					paramMap.put(Constants.PE_TRANS_STAT,Constants.PE_THREE );   //交易状态: 退货
					paramMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
	//				paramMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
					ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
				}
				updateJoural.execute(ctx,paramMap);					// 修改流水交易状态;		
			}else{	//若退货金额大于原消费金额，不予退货
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：失败
				ctx.setData(Constants.FIX8583_RESP,Constants.PE_61);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_61);   
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
				ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);  //卡有效期
				updateJoural.execute(ctx);						// 修改流水交易状态;
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
