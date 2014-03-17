package csii.dzip.action.cups.atm;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.UpdateJoural;

/**
 * 他带本脚本通知
 * @author xiehai
 * @date 2014-1-3 上午10:28:04
 */
public class ScriptNoticeAction extends DzipBaseAction{
	private UpdateJoural updateJoural;
	public void execute(Context ctx) throws PeException {
		log.info("ScriptNoticeAction(他带本脚本通知)=================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);
		updateJoural.execute(ctx);
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
}
