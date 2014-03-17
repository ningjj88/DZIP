package csii.dzip.action.platformAdmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.core.Errors;

/**
 * 借记IC卡电子钱包交易流水打印(打印流水查询)
 * @author xiehai
 * @version 1.0.0
 * @date 2013-9-9 下午05:42:34
 */
public class GetICJournal4PrintAction extends DzipBaseAction {
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("GetICJournal4PrintAction==========================>Start");
		Map map = new HashMap();
		List<Object> chargeList = new ArrayList<Object>();
		List<Map<String, Object>> recordList = new ArrayList<Map<String,Object>>();
		String chargeFlag = (String) ctx.getData(Constants.IC_CHARGE_FLAG);
		if(null == chargeFlag || Constants.PE_NULL.equals(chargeFlag)){
			chargeList = null;
		}else {
			chargeList.add(chargeFlag);//记账状态
		}

		map.put(Constants.IC_TRANS_DATE, ctx.getData(Constants.IC_TRANS_DATE));//交易日期
		map.put(Constants.IC_ACCOUNT, ctx.getData(Constants.IC_ACCOUNT));//卡号
		map.put(Constants.IC_AMT, ctx.getData(Constants.IC_AMT));//交易金额
		map.put(Constants.IC_TRANS_CODE, ctx.getData(Constants.IC_TRANS_CODE));//交易代码
		map.put(Constants.IC_CHARGE_FLAG, chargeList);//记账状态

		try {
			recordList = dzipdbAccess.getSqlMap().queryForList("common.getICJournal", map);
			ctx.setData(Constants.RECORDLIST, recordList);//获得结果
		} catch (Exception e) {
			logger.error("GetICJournal4PrintAction==============>获得打印借记IC卡电子钱包交易流水出错!");
			throw new PeException(Errors.QUERY_YLSJ_JOURNAL_INFO_FAILED);
		}
	}
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
}
