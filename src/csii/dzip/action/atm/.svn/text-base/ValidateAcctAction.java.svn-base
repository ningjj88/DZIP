package csii.dzip.action.atm;

import java.util.HashMap;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

public class ValidateAcctAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("ATM ValidateAcctAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
		ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
		HashMap procedureMap=utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
		// 帐户验证成功，修改交易流水的状态为成功
		if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))){
			String tranTyp=ctx.getString(Constants.IN_ASSBUSTYP);
			//如果是自助存款和自助转账就F61.6 NM用法持卡人姓名1处显示转入方部分姓名（隐去姓氏）
			if(Constants.PE_06.equals(tranTyp)||Constants.PE_07.equals(tranTyp)){
				String acctName=Util.formatAcctName((String)procedureMap.get(Constants.OUT_ACCTNAME));
				ctx.setData(Constants.ISO8583_IDENNUMBE, Constants.CUPS_IDENNUMBENM+acctName); //名字
			}
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
