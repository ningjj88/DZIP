package csii.dzip.action.platformAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.core.Errors;

/**
 * 借记IC卡电子钱包交易流水批量记账(记账流水查询)
 * @author xiehai
 * @version 1.0.0
 * @date 2013-9-5 下午04:51:51
 */
public class GetICJournal4ChargeAction extends DzipBaseAction {
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("GetICJournalByChargeFlagAction==========================>Start");
		Map paramMap = new HashMap();
		List ChargeFlag = new ArrayList();
		//记账标识为1和2
		ChargeFlag.add(Constants.PE_ONE);
		ChargeFlag.add(Constants.PE_TWO);
		paramMap.put(Constants.IC_TRANS_DATE, ctx.getData(Constants.IC_TRANS_DATE));//获得交易日期
		paramMap.put(Constants.IC_CHARGE_FLAG, ChargeFlag);
		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();

		try{
			//根据交易日期查询银联数据未对平的交易流水
			recordList = dzipdbAccess.getSqlMap().queryForList("common.getICJournal", paramMap);
			ctx.setData(Constants.RECORDLIST, recordList);//获得结果集
		}catch (Exception e){
			logger.error("GetICJournalByChargeFlagAction==============>获取IC卡银联数据流水出错!");
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
