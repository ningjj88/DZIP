/**
 * 
 */
package csii.dzip.action.platformAdmin.Audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.AuditProcessor;
import csii.dzip.core.Errors;

/**
 * @author chenshaoqi
 * @date 2010.09.07
 * @desc 对账处理
 */
public class AuditAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute( Context ctx) throws PeException {
		logger.info("AuditAction ==================>Start");
		Map taskMap =new HashMap();
		List listRe = new ArrayList();
		String postDate = (String)ctx.getData(Constants.POSTDATE); 		//获得账务日期，即交易日期
		String auditType =(String)ctx.getData(Constants.AUDITTYPE);		//获得对账类型
		String taskType = auditProcessor.getTaskType(auditType); 		// 获得对账类型
		taskMap.put(Constants.POSTDATE, postDate);						//账务日期
		taskMap.put(Constants.TASKNAME,Constants.TASK_6);	      		//任务名称
		taskMap.put(Constants.TASKTYP,taskType);				  		//任务类型
		Map paramMap = auditProcessor.checkTaskExeYN(taskMap);
		boolean flag = Boolean.valueOf(String.valueOf(paramMap.get(Constants.FLAG)));
		if(flag){                    //检验本任务是否可以执行
			logger.info("=================>生成对账不一致信息状态：正在执行"); 
			Map checkRecMap =new HashMap();
			checkRecMap.put(Constants.IN_POSTDATE, postDate);						//账务日期
			auditProcessor.getCheckTransRec(checkRecMap);					//获得对账不一致列表
			if(String.valueOf(checkRecMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.PE_ZERO)){
				logger.info("=================>生成对账不一致信息完成"); 
				listRe = (List)checkRecMap.get(Constants.OUT_REFCURSOR);
				try {
					logger.info("=================>记录账户冻结信息状态：正在执行"); 
					auditProcessor.add_AcctAcctHold(listRe);			//记录账户冻结信息
					logger.info("=================>记录账户冻结信息完成"); 
				} catch (Exception e) {
					throw new PeException(Errors.INSERT__CHECK_TRANS__REC_FAILED);	//抛出异常
				}				
				taskMap.put(Constants.TASKSTAT, Constants.STAT_OK);
				auditProcessor.updateTaskStatus(taskMap);             	// 更新任务状态：成功
				logger.info("=======================>对账执行成功");
			}
			else{
				if(String.valueOf(taskMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)){
					logger.info("==========================>System Error!");
					throw new PeException(Errors.SYSTEM_ERROR);								//系统错误
				}
				else{
					logger.info("==========================>DataBase Error!");
					throw new PeException(String.valueOf(taskMap.get(Constants.OUT_ERROR_MSG)));//获得对账不一致列表错误
				}
			}
		} else {
			if(paramMap.get(Constants.PRIVTASKSTAT)==null||!paramMap.get(Constants.PRIVTASKSTAT).equals(Constants.STAT_OK))
				throw new PeException(Errors.PRIV_TASK_IS_UNSUCCESSFUL);      			//上个任务没有执行成功
			if( paramMap.get(Constants.TASKEXECYN)==null)
				throw new PeException(Errors.TASK_LIST_NOT_EXSIT);      			    //任务不存在
			if(!paramMap.get(Constants.TASKEXECYN).equals(Constants.EXEC_Y))
				throw new PeException(Errors.TASK_IS_UNENFORCEABLE);      			    //该任务是"不可执行"状态
				
			if(paramMap.get(Constants.TASKSTAT).equals(Constants.STAT_OK)){
				logger .info("==============================>对账任务已经执行过"); // 已经执行过
				 throw new PeException(Errors.AUDIT_HAS_OCCURED);
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
