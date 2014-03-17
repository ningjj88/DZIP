package csii.dzip.action.atm;

import java.util.HashMap;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:户名查询
 *@author :xujin
 *@date：2011-12-27
 */
public class PersNameQueryAction extends DzipBaseAction {

	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("ATM PersNameQueryAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
		HashMap procedureMap=utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
		// 户名查询成功,组装姓名格式
		if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))) {
			String acctName=Util.formatAcctName((String)procedureMap.get(Constants.OUT_ACCTNAME));
			ctx.setData(Constants.ISO8583_ADDDATAPRI,acctName); //名字
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



	