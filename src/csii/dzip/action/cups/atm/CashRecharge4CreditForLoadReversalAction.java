package csii.dzip.action.cups.atm;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 * 他带本现金充值圈存冲正
 * @author xiehai
 * @date 2013-12-27 下午04:29:09
 */
public class CashRecharge4CreditForLoadReversalAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		logger.debug("CashRecharge4CreditForLoadReversalAction(他带本现金充值圈存冲正)====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		Map procedureMap = new HashMap();
		//设置卡的有效期
		dzipProcessTemplate.getIcCardExpDate(ctx);
		int count = Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证冲正交易是否已经发生
		if(count > 0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK); //响应码
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
		}else{
			Map resultMap = utilProcessor.getOriSysTraNum(ctx);
			logger.info("他带本现金充值圈存冲正上笔交易信息(resultMap)==================>"+resultMap);
			ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE) + CLM); //交易来源
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   //资金类型
			ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); 	// 原交易主机流水号
			ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO)); 	// 原平台流水号
			ctx.setData(Constants.IN_ORIGPOSTDATE, resultMap.get(Constants.POSTDATE)); //原始交易日期
			ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_EC);	//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
			utilProcessor.deductTranAMT(ctx, Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_NINE );   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
				ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
			}
			if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){//无法找到原始交易
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT );   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
				ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);          //响应码
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			}
		}
		updateJoural.execute(ctx, procedureMap);					// 修改流水交易状态;
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
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
