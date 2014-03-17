
package csii.dzip.action.atmother;


import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:查询交易
 *@author :陈少启
 *@date：2010-8-9
 */
public class QueryForOtherBankAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	public void execute(Context ctx) throws PeException {
		logger.info("QueryForOtherBank===================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_09); 	//业务类型:09_余额查询
		/* 如果响应码是 00，修改交易流水的状态为成功 ；0：表示成功； */
		if (ctx.getData(Constants.PE_HOST_RESP_CD).equals(Constants.PE_OK)) 
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);       //成功状态 
		else
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);      // 异常状态
		
		updateJoural.execute(ctx);
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
}
