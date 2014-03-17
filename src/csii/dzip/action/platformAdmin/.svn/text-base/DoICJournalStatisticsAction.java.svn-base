package csii.dzip.action.platformAdmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.DzipProcessTemplate;

/**
 * IC卡交易流水统计汇总
 * @author xiehai
 * @version 1.0.0
 * @date 2013-9-29 上午09:38:33
 */
public class DoICJournalStatisticsAction extends DzipBaseAction {
	private JdbcAccessAwareProcessor dzipdbAccess;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("ICJournalStatistics=====================>Start!");
		List<Map<String, Object>> recordList = new ArrayList<Map<String,Object>>();
		try {
			recordList = dzipdbAccess.getSqlMap().queryForList("common.getICJournalStatistic", ctx.getData(Constants.IC_TRANS_DATE));
			ctx.setData(Constants.RECORDLIST, recordList);
		} catch (Exception e) {
			logger.error("IC卡交易流水汇总出错!");
		}
	}
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
}
