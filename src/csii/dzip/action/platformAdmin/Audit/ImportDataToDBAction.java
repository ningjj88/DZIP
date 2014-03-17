/**
 * 
 */
package csii.dzip.action.platformAdmin.Audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
 * @desc 装载对账文件
 */
public class ImportDataToDBAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute( Context ctx) throws PeException {
		logger.info("ImportDataToDBAction==========================>Start");

			Map taskMap = new HashMap();
			Map fileInfoMap = new HashMap();
			String postDate = (String)ctx.getData(Constants.POSTDATE);   		//获得账务日期，即交易日期
			String auditType =(String)ctx.getData(Constants.AUDITTYPE);  		//获得对账类型
			String taskType =auditProcessor.getTaskType(auditType);     		//获得对账类型
		
			taskMap.put(Constants.POSTDATE, postDate);                  		//账务日期
			taskMap.put(Constants.TASKNAME,Constants.TASK_2);           		//任务名称
			taskMap.put(Constants.TASKTYP,taskType);				     		//任务类型
			//获得本任务是否可以执行标识
			Map paramMap  = auditProcessor.checkTaskExeYN(taskMap);
			boolean flag = Boolean.valueOf(String.valueOf(paramMap.get(Constants.FLAG)));
			if(flag){						//检验本任务可以执行
				String filePath = auditProcessor.getLocalFilePath(auditType); 	//获得对账文件路径
				fileInfoMap.put(Constants.POSTDATE, postDate);         			//账务日期
				fileInfoMap.put(Constants.FILETYPE, taskType);         			//文件类型
				List list=new ArrayList();
				Map map = new HashMap();
		        list = auditProcessor.getFileName(fileInfoMap);   	//查询文件名
				logger.info("=================>装载对账文件处理状态改为：处理中"); 
				taskMap.put(Constants.TASKSTAT,Constants.STAT_ING);    		//更新任务状态：处理中
				auditProcessor.updateTaskStatus(taskMap);  
				logger.info("=================>装载对账文件数据任务状态：正在执行");
				for(Iterator iterator = list.iterator();iterator.hasNext();){
					map = (Map) iterator.next();
					fileInfoMap.put(Constants.FILENAME,map.get(Constants.FILENAME));
					String fileLocal = filePath + (String)map.get(Constants.FILENAME); //构造解析文件的绝对路径
					fileInfoMap.put(Constants.STATUS, Constants.STAT_ING);  		//更新文件状态：处理中
					auditProcessor.updateFileStatus(fileInfoMap);   
					List  listRecord =new ArrayList();  
					try {
						//判断对账类型 01：锦城卡 02 银联 03 银联数据 
						if(Constants.TASKTYPE_JCK.equals(taskType)){ //锦城卡 	
						    listRecord = auditProcessor.TxtReader(fileLocal);//获得解析文件数据集
						}else if(Constants.TASKTYPE_YLQZ.equals(taskType)){// 银联
							listRecord = auditProcessor.YLQZTxtReader(fileLocal,(String)map.get(Constants.FILENAME));//获得解析文件数据集
						}else if(Constants.TASKTYPE_YLSJ.equals(taskType)){//银联数据 
							listRecord = auditProcessor.YLSJTxtReader(fileLocal,(String)map.get(Constants.FILENAME));//获得解析文件数据集
						}
					} catch (Exception e) {
						logger.info("=================>装载对账文件失败");
						fileInfoMap.put(Constants.STATUS, Constants.STAT_ERROR);  	//更新文件状态：处理失败 
						auditProcessor.updateFileStatus(fileInfoMap);
						logger.info("=================>装载对账文件数据失败");
						taskMap.put(Constants.TASKSTAT, Constants.STAT_ERROR);   	//更新任务状态：执行失败
						auditProcessor.updateTaskStatus(taskMap);
						throw new PeException(Errors.PARSE_DOWN_LOAD_FILE_FAILED);
					}        		
					try {
						if(Constants.TASKTYPE_YLSJ.equals(taskType)){//装载银联数据交易流水
							flag = auditProcessor.importCUPSDJournal(listRecord,postDate);
						}else{//装载银联交易流水
							flag = auditProcessor.importDB(listRecord,postDate);
						}
					} catch (Exception e) {
						logger.info("=================>装载对账文件失败,删除渠道前置交易流水");
						if(Constants.TASKTYPE_YLSJ.equals(taskType)){//银联数据 
							flag = auditProcessor.deleteCUPSDJournal(fileInfoMap);
						}else{
							flag = auditProcessor.deleteDB(fileInfoMap);
						}
						logger.info("=================>装载对账文件失败");
						fileInfoMap.put(Constants.STATUS, Constants.STAT_ERROR);  	//更新文件状态：处理失败
						auditProcessor.updateFileStatus(fileInfoMap);
						logger.info("=================>装载对账文件数据失败");
						taskMap.put(Constants.TASKSTAT,Constants.STAT_ERROR);    		//更新任务状态：失败
						auditProcessor.updateTaskStatus(taskMap);            
						throw new PeException(Errors.IMPORT_FTP_FILE_DATA_FAILED);	
					}
					if(flag){                                                 		//判断是否导入成功					
						fileInfoMap.put(Constants.STATUS, Constants.STAT_OK);   	//更新文件的状态：处理完毕  
						fileInfoMap.put(Constants.ROWNO, listRecord.size());    	//文件的行数
						auditProcessor.updateFileStatus(fileInfoMap); 						
					}
					else{                                                         	//若导入数据不成功
						logger.info("=================>装载对账文件失败,删除渠道前置交易流水");
						if(Constants.TASKTYPE_YLSJ.equals(taskType)){//银联数据 
							flag = auditProcessor.deleteCUPSDJournal(fileInfoMap);
						}else{
							flag = auditProcessor.deleteDB(fileInfoMap);
						}
						logger.info("=================>装载对账文件失败");
						fileInfoMap.put(Constants.STATUS, Constants.STAT_ERROR);  	//更新文件状态：处理失败
						fileInfoMap.put(Constants.ROWNO, listRecord.size());       	//文件行数      
						auditProcessor.updateFileStatus(fileInfoMap);
						logger.info("=================>装载对账文件数据失败");
						taskMap.put(Constants.TASKSTAT, Constants.STAT_ERROR);   	//更新任务状态：执行失败
						auditProcessor.updateTaskStatus(taskMap);
						throw new PeException(Errors.IMPORT_FTP_FILE_DATA_FAILED);	
					}
				}
				logger.info("=================>装载对账处理完毕");
				if(flag){                                                 		//判断是否导入成功
					logger.info("=================>装载对账处理完毕");
					taskMap.put(Constants.TASKSTAT,Constants.STAT_OK);     	//更新任务状态：执行完成
					auditProcessor.updateTaskStatus(taskMap);
					logger.info("=================>装载对账文件数据成功");
				}
			}
			else{
				if(paramMap.get(Constants.PRIVTASKSTAT)==null||!paramMap.get(Constants.PRIVTASKSTAT).equals(Constants.STAT_OK))
					throw new PeException(Errors.PRIV_TASK_IS_UNSUCCESSFUL);      			//上个任务没有执行成功
				if( paramMap.get(Constants.TASKEXECYN)==null)
					throw new PeException(Errors.TASK_LIST_NOT_EXSIT);      			    //任务不存在
				if(!paramMap.get(Constants.TASKEXECYN).equals(Constants.EXEC_Y))
					throw new PeException(Errors.TASK_IS_UNENFORCEABLE);      			    //该任务是"不可执行"状态
				if(paramMap.get(Constants.TASKSTAT).equals(Constants.STAT_OK)){
					logger.info("==============================>装载对账文件任务已经执行过");	//已经执行过
					if(ctx.getTransactionId().equals("platformAdmin.ImportFtpFile"))
					throw new PeException(Errors.IMPORT_FTP_FILE_DATA_HAS_EXCUTED);      	
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
