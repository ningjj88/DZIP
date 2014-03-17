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
import csii.dzip.core.Errors;

/**
 * @author chenshaoqi
 * @date 2010.09.07
 * @desc 对账查询
 */
public class QueryAuditAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("QueryAuditAction==========================>Start");
		Map paramMap = new HashMap();
		List listRecord = new ArrayList();
		String postDate = (String) ctx.getData(Constants.POSTDATE);       //获得账务日期， 即交易日期
		String auditType = (String) ctx.getData(Constants.AUDITTYPE);     //获得对账类型
		String auditStatus = (String) ctx.getData(Constants.AUDITSTATUS); //获得对账状态 
		String taskType = auditProcessor.getTaskType(auditType);          //获得对账类型
		paramMap.put(Constants.IN_POSTDATE, postDate);                       //填充账务日期
		paramMap.put(Constants.TASKTYP, taskType);                        //填充任务类型
		paramMap.put(Constants.IN_AUDITSTATUS, auditStatus);              //填充对账状态

		auditProcessor.queryAuditInfo(paramMap);				 // 获得结果集
//		ctx.setData(Constants.RECORDLIST, listRecord); 					 // 填充结果集
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			listRecord = (List) paramMap.get(Constants.OUT_REFCURSOR);   // 获得结果集
			ctx.setData(Constants.RECORDLIST, listRecord);               // 填充结果集
		} else {
			if (String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(
					Constants.Error_99999)) {
				logger.info("==========================>System Error!");
				throw new PeException(Errors.GET_CHANNEL__TRAN_FAILED);
			} else {
				logger.info("==========================>DataBase Error!");
				throw new PeException(String.valueOf(paramMap.get(Constants.OUT_ERROR_MSG)));
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
