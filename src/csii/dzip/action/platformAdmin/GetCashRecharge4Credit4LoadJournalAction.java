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

/**
 * 柜面现金充值撤销前现金充值流水查询
 * @author xiehai
 * @date 2014-3-20 下午12:35:10
 */
public class GetCashRecharge4Credit4LoadJournalAction extends DzipBaseAction {
	private JdbcAccessAwareProcessor dzipdbAccess;
	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		log.info("GetCashRecharge4Credit4LoadJournalAction(为现金充值撤销查询现金充值交易流水)================>start");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));//账务日期
		paramMap.put(Constants.PE_ACC_NO, ctx.getData(Constants.PE_ACC_NO));//卡号
		paramMap.put(Constants.PE_TRAN_AMT, ctx.getData(Constants.PE_TRAN_AMT));//交易金额
		paramMap.put(Constants.PE_PERSNBR, ctx.getData(Constants.PE_PERSNBR));//柜员号
		List<Map<String, Object>> recordList = new ArrayList<Map<String,Object>>();
		try {
			recordList = dzipdbAccess.getSqlMap().queryForList("common.getCashRecharge4Credit4LoadJournal", paramMap);
		} catch (Exception e) {
			log.error("GetCashRecharge4Credit4LoadJournalAction(为现金充值撤销查询现金充值交易流水)出错!" + e.getMessage());
			e.printStackTrace();
		}
		ctx.setData(Constants.RECORDLIST, recordList);
	}
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
}
