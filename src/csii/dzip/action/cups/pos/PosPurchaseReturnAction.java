
package csii.dzip.action.cups.pos;

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
 *@Description: 消费退货
 *@author :xujin
 *@date：2010-09-07
 */
public class PosPurchaseReturnAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS PosPurchaseReturnAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_03); 	//业务类型:03_消费及预授权
		ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1003); //1003：POS消费退货
		int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证是否已经发生关联交易
		if(count>0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22);          //响应码
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_22);          //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);  //卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);					                     // 修改流水交易状态;
		}else{
			Map paramMap = new HashMap();
			Map resultMap =utilProcessor.getOriSysTraNum(ctx);
			logger.info("CUPS消费退货上笔交易信息(resultMap)==================>"+resultMap);
			if(Constants.PE_ONE.equals((String)ctx.getData(Constants.PE_OUTSIDEFLAG))){//境外交易
				if(ctx.getData(Constants.ISO8583_CHDAMT)==null)//F6为null,交易金额为消费交易金额
					ctx.setData(Constants.PE_TRAN_AMT,resultMap.get(Constants.PE_TRAN_AMT));//填充交易金额
			}
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
			ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1003); //1003：POS消费退货
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_THREE);				//冲正标志 3：消费撤销
			BigDecimal tranAmt = Util.getAmount(String.valueOf(resultMap.get(Constants.PE_TRAN_AMT)));//原消费金额
			BigDecimal returnAmt = Util.getAmount(String.valueOf(ctx.getData(Constants.PE_TRAN_AMT)));//单笔退货金额
			BigDecimal retSumAmt =Util.getAmount(utilProcessor.getOrigPosRetSumAMT(ctx));//已经退货总金额
			//if(retSumAmt+returnAmt<=tranAmt){  //若退货金额不大于原消费金额，准予退货
			if(retSumAmt.add(returnAmt).compareTo(tranAmt)<= 0){  //若退货金额不大于原消费金额，准予退货
				ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); //原交易主机流水号
				ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));//原平台流水号	
				ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
				utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
				ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
				if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
					paramMap.put(Constants.PE_TRANS_STAT,Constants.PE_THREE );   //交易状态: 退货
					paramMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));//原交易流水号 
//					paramMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));//原 账务日期 
					ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
				}
				updateJoural.execute(ctx,paramMap);						// 修改流水交易状态;	
			}else{	//若退货金额大于原消费金额，不予退货
				logger.info("CUPS消费退货金额大于原消费金额，不予退货");
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：失败
				ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_61);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_61);    //响应码
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
				ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);  //卡有效期
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
