
package csii.dzip.action.cups.pos;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:他行消费撤销冲正
 *@author :xujin
 *@date：2010-08-16
 */
public class PosConsumeCancelReversalAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS PosConsumeCancelReversalAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_03); 	//业务类型:03_消费及预授权
		Map procedureMap = new HashMap();
		int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证消费撤销冲正是否已经发生
		if(count>0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);          //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);  //卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);					                     // 修改流水交易状态;
		}else{
			Map resultMap=utilProcessor.getOriSysTraNum(ctx);
			logger.info("CUPS消费撤销冲正上笔交易信息(resultMap)==================>"+resultMap);
			if(Constants.PE_ONE.equals((String)ctx.getData(Constants.PE_OUTSIDEFLAG))){//境外交易
				if(ctx.getData(Constants.ISO8583_CHDAMT)==null)//F6为null,交易金额为消费撤销交易金额
					ctx.setData(Constants.PE_TRAN_AMT,resultMap.get(Constants.PE_TRAN_AMT));//填充交易金额
			}
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
			ctx.setData(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_2); 	// 介质号：2 卡号
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     //资金类型
			ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); // 原交易主机流水号
			ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));// 原平台流水号
			ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_EC);					//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO)	;				 //冲正标志:0：正常   1：冲正 	2：抹帐
			utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				/*更新POS消费交易的状态*/
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);//交易状态:0 成功
				procedureMap.put(Constants.PE_JOURNAL_NO,resultMap.get(Constants.PE_RLTSEQNO));//交易状态 	
				updateJoural.execute(procedureMap);
				procedureMap.clear();
				procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE);   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
//				procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
			}
			if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_EIGHT );   //交易状态: 异常
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
//				procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
				ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_OK);          //响应码
				ctx.setData(Constants.ISO8583_EXPDAT,Constants.PE_EXPIREYEAR);   //卡有效期	
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			}
			updateJoural.execute(ctx,procedureMap);								// 修改流水交易状态;
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
