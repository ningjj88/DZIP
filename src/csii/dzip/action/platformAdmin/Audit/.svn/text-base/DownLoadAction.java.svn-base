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

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.AuditProcessor;
import csii.dzip.action.util.CollectData;
import csii.dzip.core.Errors;
import csii.dzip.core.InitData4Dzip;

/**
 * @author xujin
 * @date 2010.09.07
 * @desc 对账文件下载
 */
public class DownLoadAction extends DzipBaseAction {
	private AuditProcessor auditProcessor;
	private CollectData collectData;
	private String localFilepath;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("DownLoadAction==========================>Start");
		String extend=".txt";
		Map taskMap = new HashMap();
		Map infoMap = new HashMap();
		String postDate = (String) ctx.getData(Constants.POSTDATE); // 获得账务日期： 即交易日期
		String auditType = (String) ctx.getData(Constants.AUDITTYPE); // 获得对账类型
		String taskType = auditProcessor.getTaskType(auditType); // 获得对账类型
		infoMap.put(Constants.POSTDATE, postDate);
		infoMap.put(Constants.FILETYPE, taskType);   // 文件类型
		taskMap.put(Constants.POSTDATE, postDate);
		taskMap.put(Constants.TASKNAME, Constants.TASK_1); // 任务名称
		taskMap.put(Constants.TASKTYP, taskType); // 任务类型
		//获得本任务是否可以执行标识
		Map paramMap = auditProcessor.checkTaskExeYN(taskMap);
		boolean flag = Boolean.valueOf(String.valueOf(paramMap.get(Constants.FLAG)));
		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>() ;// 文件列表
		if (flag) { // 检验本任务可以执行
			logger.info("=================>下载对账文件任务状态：正在执行");
			taskMap.put(Constants.TASKSTAT, Constants.STAT_ING); // 更新任务状态：正在处理
			auditProcessor.updateTaskStatus(taskMap);
			try {
				//对账类型是锦城卡
				if(Constants.TASKTYPE_JCK.equals(taskType))
				{
					Map<String, Object> filename=new HashMap<String, Object>();
					filename.put(Constants.FILENAME, Constants.FTP_PRENAME+ Util.getFormatString(postDate,"MMdd")); // Ftp文件名
					filename.put(Constants.FILEAUDITYN, Constants.PE_Y); // 文件名是否可以参加对账
					fileList.add(filename);
					String localFile=postDate +extend;
					logger.info("========>Start to download ftp file name is :"+ (String)fileList.get(0).get(Constants.FILENAME));
					flag = auditProcessor.downLoad((String)fileList.get(0).get(Constants.FILENAME), localFile,auditType);
				}else if(Constants.TASKTYPE_YLQZ.equals(taskType)){ // 对账类型是银联
					//通过对账类型查询t_cupsauditfile表获得需要下载的对账文件名
					fileList=auditProcessor.queryCupsAuditFile(taskType);
					//循环下载对账文件名并对对账文件名格式转换
					for(int i=0;i<fileList.size();i++)
					{
						if(Constants.PE_Y.equals((String)fileList.get(i).get(Constants.FILEAUDITYN))){
							String filename=(String)fileList.get(i).get(Constants.FILENAME);
							if(filename.indexOf(Constants.PE_YYYYMMDDX)>-1)
								fileList.get(i).put(Constants.FILENAME, filename.replace(Constants.PE_YYYYMMDDX,postDate+InitData4Dzip.getAcqOrgCd()));
							else
								fileList.get(i).put(Constants.FILENAME,filename.replace(Constants.PE_YYMMDD,Util.getFormatString(postDate,"yyMMdd")));
							filename=(String)fileList.get(i).get(Constants.FILENAME);
							logger.info("========>Start to download ftp file name is :"+ filename);
							flag = auditProcessor.downLoad(filename, filename+extend,auditType);
						}
					}
				}else if(Constants.TASKTYPE_YLSJ.equals(taskType)){ //对账类型是银联数据
					//通过对账类型查询t_cupsauditfile表获得需要下载的文件
					fileList=auditProcessor.queryCupsAuditFile(taskType);
					//循环下载对账文件名并对文件名格式转换
					for(int i=0;i<fileList.size();i++)
					{
						if(Constants.PE_Y.equals((String)fileList.get(i).get(Constants.FILEAUDITYN))){
							String filename=(String)fileList.get(i).get(Constants.FILENAME);
							filename=filename.replace(Constants.PE_YYYYMMDD,postDate);
							String acqOrgCd=InitData4Dzip.getAcqOrgCd();
							filename=filename.replace(Constants.PE_$$$$, Constants.IC_YLSJ_ORG_CD);
							//filename=filename.replace(Constants.PE_$$$$,acqOrgCd.substring(acqOrgCd.length()-4, acqOrgCd.length()));
							fileList.get(i).put(Constants.FILENAME,filename);
							logger.info("========>Start to download ftp YLSJ file name is :"+ filename);
							flag = auditProcessor.downLoad(filename, filename+extend,auditType, postDate);
						}
					}
				}

			} catch (PeException e) {
				taskMap.put(Constants.TASKSTAT, Constants.STAT_ERROR); // 更新任务状态： 失败
				auditProcessor.updateTaskStatus(taskMap);
				if(e.getMessage().contains("No such file or directory"))
					throw new PeException(Errors.FTP_FILE_IS_NOT_EXIST);
				else if(e.getMessage().contains("Not logged in"))
						throw new PeException(Errors.LOGGED_IN_FTP_FAILED);
					else if(e.getMessage().contains("Connection refused: connect"))
						 throw new PeException(Errors.CONNECT_FTP_FAILED);
						else
						throw new PeException(Errors.DOWN_LOAD_FTP_FILE_FAILED);
			}
			// 当对账文件下载成功时，如果是银联汇总文件就把文件上传到总账FTP目录下，否则记录对账文件的信息
			if (flag) {
				boolean insertFileFlag=true;
				for(int i=0;i<fileList.size();i++){
					if(Constants.PE_Y.equals((String)fileList.get(i).get(Constants.FILEAUDITYN))){
						String filename=(String)fileList.get(i).get(Constants.FILENAME);
						String filecatcd=(String)fileList.get(i).get(Constants.FILECATCD);
						//如果是银联汇总文件就把文件上传到总账FTP目录下
						if(taskType.equals(Constants.TASKTYPE_YLQZ)&&Constants.PE_SUM.indexOf(filecatcd) !=-1){
							try {
								collectData.uploadFile((String)fileList.get(i).get(Constants.FILENAMECN),localFilepath+filename+extend,postDate+"/");
							}catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("上传银联文件失败,文件名:"+(String)fileList.get(i).get(Constants.FILENAMECN)+",日期:"+postDate+"error message:"+e.getMessage());
							}
						}else{
							//记录对账文件的信息
							infoMap.put(Constants.FILENAME, filename+extend); // 文件名
							infoMap.put(Constants.FILETYPE, taskType); // 文件类型
							infoMap.put(Constants.POSTDATE, postDate); // 账务日期
							infoMap.put(Constants.PROCDATE, postDate); // 处理日期
							infoMap.put(Constants.STATUS, Constants.STAT_INIT); // 文件状态： 初始化
							infoMap.put(Constants.ROWNO, Constants.PE_ZERO); // 处理行数：初始化为：0
							//如果对账文件没有记录就记录对账文件
							if(auditProcessor.checkInsertFileInfoYN(infoMap))
							{
								if (!auditProcessor.insertFileInfo(infoMap)){//记录对账文件的信息
								   insertFileFlag=false;
								   break;
								}
							}
						}
					}
				}
				//更新任务状态
				if(insertFileFlag){
					logger.info("=================>装载对账文件信息成功");
					taskMap.put(Constants.TASKSTAT, Constants.STAT_OK); // 更新任务状态：成功完成
					auditProcessor.updateTaskStatus(taskMap);
					logger.info("=================>下载对账文件成功");
				}else{
					logger.info("=================>装载对账文件信息失败");
					auditProcessor.deleteFileInfo(infoMap);//删除该日期的文件
					taskMap.put(Constants.TASKSTAT, Constants.STAT_ERROR); // 更新任务状态： 失败
					auditProcessor.updateTaskStatus(taskMap);
					throw new PeException(Errors.INSERT_FTP_FILE_INFO_FAILED);
				}
			} else {
				logger.info("==============================>下载对账文件失败");
				logger.info("将任务状态改为：执行失败===>99");
				taskMap.put(Constants.TASKSTAT, Constants.STAT_ERROR); // 更新任务状态： 失败
				auditProcessor.updateTaskStatus(taskMap);
				throw new PeException(Errors.DOWN_LOAD_FTP_FILE_FAILED);
			}
		} else {
			if(paramMap.get(Constants.PRIVTASKSTAT)==null||!paramMap.get(Constants.PRIVTASKSTAT).equals(Constants.STAT_OK))
				throw new PeException(Errors.PRIV_TASK_IS_UNSUCCESSFUL);      			//上个任务没有执行成功
			if( paramMap.get(Constants.TASKEXECYN)==null)
				throw new PeException(Errors.TASK_LIST_NOT_EXSIT);      			    //任务不存在
			if(!paramMap.get(Constants.TASKEXECYN).equals(Constants.EXEC_Y))
				throw new PeException(Errors.TASK_IS_UNENFORCEABLE);      			    //该任务是"不可执行"状态
			if(paramMap.get(Constants.TASKSTAT).equals(Constants.STAT_OK)){
				logger.info("==============================>文件已经下载成功，不需重新下载");		//已经执行过
				if(ctx.getTransactionId().equals("platformAdmin.DownLoadFtpFile"))
				throw new PeException(Errors.DOWNLOAD_TASK_HAS_EXCUTED);
			}
		}
	}

	public AuditProcessor getAuditProcessor() {
		return auditProcessor;
	}

	public void setAuditProcessor(AuditProcessor auditProcessor) {
		this.auditProcessor = auditProcessor;
	}

	public CollectData getCollectData() {
		return collectData;
	}
	public void setCollectData(CollectData collectData) {
		this.collectData = collectData;
	}

	public String getLocalFilepath() {
		return localFilepath;
	}

	public void setLocalFilepath(String localFilepath) {
		this.localFilepath = localFilepath;
	}

}
