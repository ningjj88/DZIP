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
 * @desc 装载任务
 */
public class DaySwicthAction extends DzipBaseAction{
	private AuditProcessor auditProcessor;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("DayEndTaskAction==========================>Start");
		Map paramMap = new HashMap();
		String postDate = (String)ctx.getData(Constants.SYSTEM_DATE); 		//获得账务日期，即交易日期
		String core_PostDate="";										    //核心账务日期
		paramMap= auditProcessor.getCorePostDate(paramMap);				    //获得核心账务日期
		if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.PE_ZERO)){       //判断是否以已经装载任务列表
			logger.info("==========================>get core post date successful!");
			core_PostDate = (String)paramMap.get(Constants.OUT_POSTDATE);   //获得核心账务日期
			paramMap.clear();//清空Map
			paramMap.put(Constants.IN_OLD_POSTDATE,postDate);				   //平台账务日期
			paramMap.put(Constants.IN_NEW_POSTDATE,core_PostDate);			   //核心账务日期
			paramMap.put(Constants.IN_TASKNAME,Constants.TASK_6);			   //任务名称
			auditProcessor.daySwitch(paramMap);
			if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.PE_ZERO))  {
				logger.info("==========================>Day Switch successful!");
				paramMap = auditProcessor.getPEPostDate(paramMap);
				String SystemDate = String.valueOf(paramMap.get(Constants.POSTDATE));
				ctx.setData(Constants.SYSTEM_DATE,SystemDate);
			}else{
				if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1001)){
					logger.info("==========================>History date not clear");  //历史数据没有清理
					throw new PeException(Errors.HISTORY_DATE_NOT_CLEAR);
				}
				if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1002)){
					logger.info("==========================>Audit not occured");		//没有对账
					throw new PeException(Errors.AUDIT_NOT_OCCURED);
				}
				if(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)){//系统错误
					logger.info("==========================>System Error!");
					throw new PeException(Errors.SYSTEM_ERROR);
				}
				logger.info("==========================>DataBase Error!");
				throw new PeException(String.valueOf(paramMap.get(Constants.OUT_ERROR_MSG)));
			}
		}else {
			if (String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_1001)) {
				logger.info("==========================>get core post date failed!");// 没有找到账务日期
				throw new PeException(Errors.GET_CORE_POST_DATE_FAILED);
			}
			if (String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)) {// 系统错误
				logger.info("==========================>System Error!");
				throw new PeException(Errors.SYSTEM_ERROR);
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
