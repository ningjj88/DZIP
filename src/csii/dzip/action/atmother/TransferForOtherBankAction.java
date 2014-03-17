package csii.dzip.action.atmother;


import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:转账交易
 *@author :陈少启
 *@date：2010-7-29
 */
public class TransferForOtherBankAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	public void execute(final Context ctx) throws PeException {
		logger.info("TransferForOtherBank=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		/* 如果响应码是 00，修改交易流水的状态为成功 ；0：表示成功； */
		if (ctx.getData(Constants.PE_HOST_RESP_CD).equals(Constants.PE_OK)) 
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);       //成功状态 
		else
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);      // 异常状态
		updateJoural.execute(ctx);// 修改交易流水状态
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
