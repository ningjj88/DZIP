/**
 * 
 */
package csii.dzip.action.platformAdmin.Audit;

import java.util.HashMap;
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
 * @desc 核对前置流水与核心流水
 */
public class CmpCoreToPEAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("CmpCoreToPEAction==========================>Start");
		Map taskMap =new HashMap();
		String postDate = (String)ctx.getData(Constants.POSTDATE); 			//获得账务日期，即交易日期
		String auditType =(String)ctx.getData(Constants.AUDITTYPE);			//获得对账类型
		String taskType =auditProcessor.getTaskType(auditType);    			//获得对账类型
		taskMap.put(Constants.POSTDATE, postDate);							//账务日期
		taskMap.put(Constants.TASKNAME,Constants.TASK_4);			  		//任务名称
		taskMap.put(Constants.TASKTYP,taskType);				  			//任务类型
		//获得本任务是否可以执行标识
		Map paramMap = auditProcessor.checkTaskExeYN(taskMap);
		boolean flag = Boolean.valueOf(String.valueOf(paramMap.get(Constants.FLAG)));
		//检验本任务可以执行
		if(flag){															
			logger.info("=======================>核对核心与PE平台交易流水任务状态：正在执行");
			taskMap.put(Constants.TASKSTAT,Constants.STAT_ING);
			auditProcessor.updateTaskStatus(taskMap);                   	//更新任务状态为正在处理
			taskMap.put(Constants.IN_POSTDATE,postDate);
			auditProcessor.cmpCoreToPE(taskMap);//核对前置流水与核心流水
			//判断存储过程是否成功
			if (Constants.PE_ZERO.equals(String.valueOf(taskMap.get(Constants.OUT_ERROR_NBR)))) {
				//成功
				taskMap.put(Constants.TASKSTAT, Constants.STAT_OK);
				auditProcessor.updateTaskStatus(taskMap); 				// 更新任务状态：成功
				logger.info("=======================>核对核心与PE平台交易流水成功");
			}
			else{//失败
				taskMap.put(Constants.TASKSTAT, Constants.STAT_ERROR);
				auditProcessor.updateTaskStatus(taskMap); 				// 更新任务状态：失败
				logger.info("=======================>核对核心与PE平台交易流水失败");
				if(String.valueOf(taskMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999))
					throw new PeException(Errors.SYSTEM_ERROR);
				else if(String.valueOf(taskMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1001))
					throw new PeException(Errors.CORERTXN_NO_EXIST_DATA);
				else
					throw new PeException(String.valueOf(taskMap.get(Constants.OUT_ERROR_MSG)));
			}
		}else{
			if(paramMap.get(Constants.PRIVTASKSTAT)==null||!paramMap.get(Constants.PRIVTASKSTAT).equals(Constants.STAT_OK))
				throw new PeException(Errors.PRIV_TASK_IS_UNSUCCESSFUL);      			//上个任务没有执行成功
			if( paramMap.get(Constants.TASKEXECYN)==null)
				throw new PeException(Errors.TASK_LIST_NOT_EXSIT);      			    //任务不存在
			if(!paramMap.get(Constants.TASKEXECYN).equals(Constants.EXEC_Y))
				throw new PeException(Errors.TASK_IS_UNENFORCEABLE);      			    //该任务是"不可执行"状态
			if(paramMap.get(Constants.TASKSTAT).equals(Constants.STAT_OK)){
				logger.info("==============================>比较PE平台与核心交易流水任务已经执行过");		//已经执行过
//				throw new PeException(Errors.TASK_HAS_EXCUTED);      			       
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
