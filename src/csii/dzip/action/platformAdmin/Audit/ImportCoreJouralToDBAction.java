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
 * @author xujin
 * @date 2010.09.07
 * @desc 装载核心交易流水 
 * delete from T_JCKJOURNAL;
 * delete from T_DAYENDTASKLIST;
 * delete from t_procfileinfo;
 * commit;
 */
public class ImportCoreJouralToDBAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;
	
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("ImportCoreJouralToDBAction==========================>Start");
		Map taskMap = new HashMap();
		List listRecord = new ArrayList();
		String postDate = (String)ctx.getData(Constants.POSTDATE); 		//获得账务日期，即交易日期
		String auditType =(String)ctx.getData(Constants.AUDITTYPE);		//获得对账类型
		String taskType =auditProcessor.getTaskType(auditType);    		//获得对账类型
		taskMap.put(Constants.POSTDATE, postDate);						//账务日期
		taskMap.put(Constants.TASKNAME,Constants.TASK_3);				//任务名称
		taskMap.put(Constants.TASKTYP,taskType);				   		//任务类型
		//获得本任务是否可以执行标识
		Map paramMap = auditProcessor.checkTaskExeYN(taskMap);
		boolean flag = Boolean.valueOf(String.valueOf(paramMap.get(Constants.FLAG)));
		if(flag){//检验本任务可以执行
			logger.info("=================>导入核心交易流水任务状态：正在执行");
			taskMap.put(Constants.TASKSTAT,Constants.STAT_ING);
			auditProcessor.updateTaskStatus(taskMap);                 //更新任务状态：正在处理
			taskMap.put(Constants.IN_POSTDATE,postDate);			   //账务日期	
			taskMap.put(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C); //交易类型
			taskMap.put(Constants.IN_RTXNSOURCECD,auditType);		   //交易类别 ：锦城卡，银联
			try {
				listRecord = auditProcessor.exportCoreDB(taskMap);     //导出核心交易流水数据集
			} catch (Exception e) {
				throw new PeException(Errors.EXPORT_CORE_JOURAL_FAILED);
			}			
			try {
				flag = auditProcessor.importCoreToDB(listRecord);       //将数据集导入本地库表中
			} catch (Exception e) {
				throw new PeException(Errors.IMPORT_CORE_JOURAL_FAILED);
			}  
			if (flag) {													//判断是否导入成功
				taskMap.put(Constants.TASKSTAT, Constants.STAT_OK);
				auditProcessor.updateTaskStatus(taskMap); 			// 更新任务状态：成功
				logger.info("=================>导入核心交易流水任务成功");
			}
			else { // 如导入数据失败
				logger.info("=================>导入核心交易流水任务失败");
				taskMap.put(Constants.TASKSTAT, Constants.STAT_ERROR);
				auditProcessor.updateTaskStatus(taskMap); // 更新任务状态：失败
			}
		}else{
			if(paramMap.get(Constants.PRIVTASKSTAT)==null||!paramMap.get(Constants.PRIVTASKSTAT).equals(Constants.STAT_OK))
				throw new PeException(Errors.PRIV_TASK_IS_UNSUCCESSFUL);      			//上个任务没有执行成功
			if(paramMap.get(Constants.TASKEXECYN)==null)
				throw new PeException(Errors.TASK_LIST_NOT_EXSIT);      			    //任务不存在
			if(!paramMap.get(Constants.TASKEXECYN).equals(Constants.EXEC_Y))
				throw new PeException(Errors.TASK_IS_UNENFORCEABLE);      			    //该任务是"不可执行"状态
			if(paramMap.get(Constants.TASKSTAT).equals(Constants.STAT_OK)){
				logger.info("==============================>导入核心数据任务已经执行过");		//已经执行过
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
