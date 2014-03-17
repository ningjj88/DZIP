package csii.dzip.action.platformAdmin.Audit;

import java.util.List;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.core.Errors;

public class VerifyOffLinePreAuthCompAction extends DzipBaseAction{
	private ActionUtilProcessor  utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
	    if(Constants.PE_Y.equals(ctx.getString(Constants.ISPREAUTHCOMPFLAG))){
			List<Map<String, Object>>  resultList =utilProcessor.getOfflinePreAuthComp();
			if(resultList.size()>0){
				logger.info("异常的离线预授权完成交易没有补入=============>EXP_PREAHTUCOMP_NOT_HANDLE!");
				throw new PeException(Errors.EXP_PREAHTUCOMP_NOT_HANDLE);
			}
	    }
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}
	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
}
