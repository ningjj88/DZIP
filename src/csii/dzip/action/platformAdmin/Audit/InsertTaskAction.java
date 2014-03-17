/**
 * 
 */
package csii.dzip.action.platformAdmin.Audit;

import java.util.ArrayList;
import java.util.List;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.AuditProcessor;
import csii.dzip.core.Errors;

/**
 * @author xujin
 * @date 2010.09.07
 * @desc 装载任务
 */
public class InsertTaskAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("InsertTaskAction==========================>Start");
		String postDate = (String)ctx.getData(Constants.POSTDATE); 		//获得账务日期，即交易日期
		String auditType =(String)ctx.getData(Constants.AUDITTYPE);		//获得对账类型
		String taskType = auditProcessor.getTaskType(auditType);		//获得对账类型
		//判断是否以已经装载任务列表
		if(auditProcessor.checkInsertTaskListYN(postDate, taskType))         
		{
			List taskList =new ArrayList();
			logger.info("==========================>开始装载任务列表");
			try {
				taskList = auditProcessor.parseAuditTaskList(taskType);
			} catch (Exception e) {
				throw new PeException(Errors.PARSE_TASK_LIST_FAILED);
			}
			logger.info("==========================>装载任务列表结束");
			try {
				auditProcessor.insertTaskList(taskList,postDate,taskType);
				logger.info("==========================>装载任务列表成功");
			} catch (Exception e) {
				throw new PeException(Errors.DATEBASE_ERROR);
			}
		}		
	}

	public AuditProcessor getAuditProcessor() {
		return auditProcessor;
	}

	public void setAuditProcessor(AuditProcessor auditProcessor) {
		this.auditProcessor = auditProcessor;
	}

}
