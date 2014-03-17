package csii.dzip.action.atmother;


import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;
/**
 * @author xujin
 *@Description:脚本通知(本带本、本代他)
 * @version 1.0.1
 * @since 2012-10-22
 */
public class ScriptNoticeAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	public void execute(Context ctx) throws PeException {
		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(ctx.getData(Constants.ISO8583_SOURID))){//本代本
			logger.info("ScriptNoticeAction(本代本脚本通知)===================>Start");
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//设置为本代本交易
		}else {
			logger.info("ScriptNoticeAction(本代他脚本通知)===================>Start");
		}
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		/* 如果响应码是 00，修改交易流水的状态为成功 ；0：表示成功； */
		if (ctx.getData(Constants.PE_HOST_RESP_CD).equals(Constants.PE_OK)){
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);       //成功状态
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);      // 异常状态
		}
		updateJoural.execute(ctx);
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
