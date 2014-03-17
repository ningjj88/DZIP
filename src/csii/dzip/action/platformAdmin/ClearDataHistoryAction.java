/**
 * 
 */
package csii.dzip.action.platformAdmin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.AuditProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.core.Errors;
import csii.dzip.core.InitData4Dzip;
/**
 * @author chenshaoqi
 * @date 2010.09.07
 * @desc 历史数据清理
 */
public class ClearDataHistoryAction  extends DzipBaseAction{
	private AuditProcessor auditProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("ClearDataHistoryAction==========================>Start");
		Map paramMap = new HashMap();
		String postDate=null;
		if(ctx.getData(Constants.SYSTEM_DATE)==null){//自动清理
			try {
				postDate = dzipProcessTemplate.queryPostDate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new PeException("获得系统日期出错");
			}
		}else{
			postDate = (String)ctx.getData(Constants.SYSTEM_DATE); 		//获得账务日期，即交易日期
		}		
		paramMap.put(Constants.IN_POSTDATE, postDate);
		paramMap.put(Constants.IN_TASKNAME, "CRETHOLD");
		paramMap.put(Constants.IN_MAXDATERANGE, InitData4Dzip.getDataStoredDays());//流水数据保存天数
		paramMap= auditProcessor.clearHistoryJournal(paramMap);				//数据清理
		if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals("0")){//判断是否以已经装载任务列表
			logger.info("==========================>clear date online successful!");
		}else{
			if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)){
				logger.info("==========================>System Error!"); //系统错误
				throw new PeException(Errors.SYSTEM_ERROR);
			}else if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1001)){
				logger.info("==========================>No_clear_data!"); //没有数据要清理
				throw new PeException(Errors.NO_CLEAR_DATA);
			}else if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1002)){
				logger.info("==========================>Audit not occured");  //没有对账
				throw new PeException(Errors.AUDIT_NOT_OCCURED);
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
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
	
}
