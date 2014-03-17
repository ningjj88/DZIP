/**
 * 
 */
package csii.dzip.action.platformAdmin.JournalBatch;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.AuditProcessor;
import csii.dzip.core.Errors;
import csii.base.action.util.Util;
/**
 * @author xujin
 * @date 2010.04.23
 * @desc 导出到临时表
 */
public class ImportJournalBatchAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;
	private ActionUtilProcessor  utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("ImportJournalBatchAction==========================>Start");
		String startDate=(String)ctx.getData(Constants.STARTDATE);
		String startTime=(String)ctx.getData(Constants.STARTTIME);
		String endDateTime=String.valueOf(ctx.getData(Constants.ENDDATE))+String.valueOf(ctx.getData(Constants.ENDTIME));
		String maxDateTime=String.valueOf(auditProcessor.getUnloadChalTranMaxTime());
		Map resultMap=new HashMap();
		if(maxDateTime.compareTo(startDate+startTime)>=0)
			throw new PeException(Errors.RE_IMPORT_JOURNAL_BATCH);
		maxDateTime=startDate+startTime;
		try{
			resultMap=utilProcessor.GetBatchMaxDateTime(maxDateTime);
		}catch(PeException e){
			throw new PeException(Errors.SYSTEM_ERROR);
		}
		if(resultMap==null)
			throw new PeException(Errors.NO_IMPORT_JOURNAL_BATCH);
		if(!endDateTime.equals(Constants.PE_NULL)&&String.valueOf(resultMap.get(Constants.PE_LAST_UPDATE_TIME)).compareTo(endDateTime)>0)
			throw new PeException(Errors.NO_IMPORT_JOURNAL_BATCH);
		Map paramMap = new HashMap();
		paramMap.put(Constants.IN_TRANDATETIME, resultMap.get(Constants.PE_LAST_UPDATE_TIME));
		paramMap.put(Constants.IN_ENDDATETIME,endDateTime);
		paramMap.put(Constants.IN_SYSSEQNO, String.valueOf(resultMap.get(Constants.PE_SYSSEQNO)));
		paramMap.put(Constants.IN_EXCYN, (String)ctx.getData(Constants.EXPORTYN));
		paramMap= auditProcessor.ImportJournalBatch(paramMap);				//数据清理
		if(Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))){//判断是否以已经装载任务列表
			logger.info("==========================>import batch journal successful!");
			auditProcessor.UnloadChalTranMaxTime(String.valueOf(paramMap.get(Constants.OUT_MAXDATETIME)));
		}else{
			if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)){
				logger.info("==========================>System Error!"); //系统错误
				throw new PeException(Errors.SYSTEM_ERROR);
			}else if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1001)){
				logger.info("==========================>more_import_journal_batch!"); //导出流水数据太多，请缩小截止日期时间
				throw new PeException(Errors.MORE_IMPORT_JOURNAL_BATCH);
			}else if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1002)){
				logger.info("==========================>Re_import_journal_batch!"); //没有导出过流水信息 
				throw new PeException(Errors.NO_IMPORT_JOURNAL_BATCH);
			}
			logger.info("==========================>DataBase Error!");
			throw new PeException(String.valueOf(paramMap.get(Constants.OUT_ERROR_MSG)));
		}
	}


	public AuditProcessor getAuditProcessor() {
		return auditProcessor;
	}

	public void setAuditProcessor(AuditProcessor auditProcessor) {
		this.auditProcessor = auditProcessor;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}
	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
}
