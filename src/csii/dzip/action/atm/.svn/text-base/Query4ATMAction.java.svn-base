
package csii.dzip.action.atm;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;
import csii.base.action.util.Util;

/**
 *@ClassName:&{type_name}
 *@Description:查询交易
 *@author :xujin
 *@date：2010-7-20
 */
public class Query4ATMAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("Query4ATMAction=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_09); 	//业务类型:09_余额查询
		ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
		ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
		HashMap procedureMap=utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
		// 余额查询成功，组装余额格式
		if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))) {
			Map formatAmtMap = new HashMap();
			formatAmtMap.put(Constants.ACCTTYPCD, procedureMap.get(Constants.OUT_ACCTTYPCD)); //帐户类型
			formatAmtMap.put(Constants.BALAMT, procedureMap.get(Constants.OUT_BALAMT)); //账面余额
			formatAmtMap.put(Constants.AVAILBALAMT, procedureMap.get(Constants.OUT_AVAILBALAMT)); //可用余额
			String availBalAmt = Util.formatAmt(formatAmtMap);  //返回余额格式化
			ctx.setData(Constants.ISO8583_BALAMT, availBalAmt);           //余额
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
