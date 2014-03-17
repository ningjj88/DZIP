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
 * @desc 下载文件信息查询
 */
public class QueryFileInfoAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("QueryFileInfoAction==========================>Start");
		Map paramMap =new HashMap();
		List listRecord = new ArrayList();
		String postDate = (String)ctx.getData(Constants.POSTDATE);  	//获得账务日期，即交易日期
		String auditType =(String)ctx.getData(Constants.AUDITTYPE);  	//获得对账类型
		String taskType =auditProcessor.getTaskType(auditType);    		//获得对账类型
		paramMap.put(Constants.POSTDATE, postDate);						//账务日期
		paramMap.put(Constants.FILETYPE,taskType);				    	//文件类型
		try {
			listRecord = auditProcessor.queryFileInfo(paramMap);		//获得结果集
//			listRecord = ConvertUtilProcessor.auditConvert(listRecord);
			ctx.setData(Constants.RECORDLIST, listRecord);           	//填充结果集
		} catch (Exception e) {
			throw new PeException(Errors.QUERY_FILE_INFO_FAILED);
		}			
		
	}

	public AuditProcessor getAuditProcessor() {
		return auditProcessor;
	}

	public void setAuditProcessor(AuditProcessor auditProcessor) {
		this.auditProcessor = auditProcessor;
	}

}
