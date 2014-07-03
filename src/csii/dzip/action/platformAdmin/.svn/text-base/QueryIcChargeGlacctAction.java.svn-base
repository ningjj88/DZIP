package csii.dzip.action.platformAdmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.base.constant.SqlMaps;
import csii.dzip.action.DzipBaseAction;
/**
 * IC卡差错记账的会计科目查询(dzip.t_glaccttitle)
 * @author xiehai
 * @date 2014-6-17 下午02:07:17
 */
public class QueryIcChargeGlacctAction extends DzipBaseAction {
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("QueryIcChargeGlacctAction(IC卡差错记账的会计科目查询)==========================>Start");
		List<Map<String, Object>> recodeList = new ArrayList<Map<String,Object>>();
		try {
			//获得所有IC卡差错记账会计科目
			recodeList = dzipdbAccess.getSqlMap().queryForList(SqlMaps.IC_QUERY_DZIP_GLACCT);
			ctx.setData(Constants.RECORDLIST, recodeList);
		} catch (Exception e) {
			logger.error("IC卡差错记账会计科目查询出错!" + e.getMessage());
			throw new PeException(e.getMessage());
		}
	}
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
}
