/**
 * 
 */
package csii.dzip.action.platformAdmin;

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
 * @desc 联机数据清理
 */
public class ClearDataOnLineAction  extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("ClearDataOnLineAction==========================>Start");
		Map paramMap = new HashMap();
		String postDate = (String)ctx.getData(Constants.SYSTEM_DATE); 		//获得账务日期，即交易日期
		paramMap.put(Constants.IN_POSTDATE, postDate);
		paramMap.put(Constants.IN_TASKNAME, "CRETHOLD");
		paramMap= auditProcessor.clearOnlineJournal(paramMap);				//数据清理
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

}
