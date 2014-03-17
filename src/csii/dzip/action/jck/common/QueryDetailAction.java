package csii.dzip.action.jck.common;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:他行余额查询
 *@author :chenshq
 *@date：2010-08-16
 */
public class QueryDetailAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private JdbcAccessAwareProcessor corebankAccess;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;

	public void execute(final Context ctx) throws PeException {
		logger.info("JCK QueryDetailAction ==================>Start");
		String msgTyp = String.valueOf(ctx.getData(Constants.FIX8583_MESG)); //消息类型
		String repMsgTyp =utilProcessor.getResMsgTyp(msgTyp);
		if(Constants.PE_NULL.equals(repMsgTyp))
			ctx.setData(Constants.FIX8583_MESG,msgTyp);						//返回消息类型
		else
			ctx.setData(Constants.FIX8583_MESG,repMsgTyp);					//返回消息类型
	}
	
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public JdbcAccessAwareProcessor getCorebankAccess() {
		return corebankAccess;
	}
	public void setCorebankAccess(JdbcAccessAwareProcessor corebankAccess) {
		this.corebankAccess = corebankAccess;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}
	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}

	}
