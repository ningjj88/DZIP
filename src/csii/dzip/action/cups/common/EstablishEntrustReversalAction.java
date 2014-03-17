package csii.dzip.action.cups.common;

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
 *@Description: 建立委托冲正
 *@author :xujin
 *@date：20112-03-017
 */
public class EstablishEntrustReversalAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS EstablishEntrustReversalAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证消费冲正是否已经发生
		if(count>0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);          //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);  //卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);					                     // 修改流水交易状态;
		}else{
			Map resultMap =utilProcessor.getOriSysTraNum(ctx);
			logger.info("CUPS建立委托冲正上笔交易信息(resultMap)==================>"+resultMap);
			ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
			ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
			HashMap procedureMap=utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
			// 帐户验证成功，修改交易流水的状态为成功
			if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))) {
				procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE );   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
				updateJoural.execute(ctx,procedureMap);					// 修改流水交易状态;
			}else{
				updateJoural.execute(ctx);	
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
