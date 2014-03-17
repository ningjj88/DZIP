package csii.dzip.action.atm;

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
 *@ClassName:&{type_name
 *@Description:冲正交易
 *@author :xujin
 *@date：2010-6-29
 */
public class Reversal4ATMAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("Reversal4ATMAction=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证冲正交易是否已经发生
		if(count>0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK); //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);   	//卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);					// 修改流水交易状态;
		}else{
			Map resultMap =utilProcessor.getOriSysTraNum(ctx);
			logger.info("取款冲正上笔交易信息(resultMap)==================>"+resultMap);
			ctx.setData(Constants.IN_RTXNSOURCECD,ATM); //交易来源
			ctx.setData(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_2); 	// 介质号：2 卡号
//			ctx.setData(Constants.CHECK_TRACK2, Constants.PE_N); 	//不验证二磁
//			ctx.setData(Constants.CHECK_PIN, Constants.PE_N); 	//不验证密码
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   //资金类型
			ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); 	// 原交易主机流水号
			ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO)); 	// 原平台流水号
			ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_EC);	//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
			utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
			ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
			Map procedureMap = new HashMap();
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE );   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
//				procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
			}
			if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){//无法找到原始交易
				procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_EIGHT );   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
//				procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
				ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_OK);          //响应码
				ctx.setData(Constants.ISO8583_EXPDAT,Constants.PE_EXPIREYEAR);   //卡有效期	
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			}
			updateJoural.execute(ctx,procedureMap);					// 修改流水交易状态;
		}
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}



}
