package csii.dzip.action.cups.common;

import java.util.HashMap;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;
/**
 *@ClassName:&{type_name}
 *@Description: 汇款验证
 *@author :xujin
 *@date：20012-10-22
 */
public class RemittanceVerificationAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS RemittanceVerificationAction(汇款验证) ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
		ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DEP);
		HashMap procedureMap=utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
		// 帐户验证成功,组装57域CI用法
		if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))){
			ctx.setData(Constants.ISO8583_ADD_DATA_PRI, Init.formatF57ForCI(procedureMap));
		}
		updateJoural.execute(ctx);        //更新交易流水表交易状态
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
