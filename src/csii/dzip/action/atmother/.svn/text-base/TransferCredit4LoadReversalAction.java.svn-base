package csii.dzip.action.atmother;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 * 转账圈存冲正
 * @author xiehai
 * @date 2013-11-5 上午11:19:43
 */
public class TransferCredit4LoadReversalAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("TransferCredit4LoadReversalAction(转账圈存冲正交易)====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01);//关联业务类型：01 现金及转账
		Map procedureMap = new HashMap();
		if (Constants.PE_OK.equals(ctx.getString(Constants.PE_HOST_RESP_CD))) {// 如果 交易成功
			Map orgDataMap = utilProcessor.getOrgData(ctx.getData(Constants.PE_JOURNAL_NO));
			logger.info("转账圈存冲正交易信息(orgDataMap)==================>"+orgDataMap);
			orgDataMap.put(Constants.PE_CHANNELID, ctx.getData(Constants.PE_REQ_CHANN));
			orgDataMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
			Map resultMap = utilProcessor.getJournalNO(orgDataMap);
			logger.info("转账圈存冲正上笔交易信息(resultMap)==================>"+resultMap);
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); //交易状态：成功

			procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
			procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_NINE); //原交易状态： 已冲正
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); //交易状态：失败
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
}
