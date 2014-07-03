package csii.dzip.action.platformAdmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.base.constant.SqlMaps;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.core.Dict;

/**
 * 待补登金额查询
 * @author xiehai
 * @date 2014-5-5 上午11:21:21
 */
public class AdditionalAmtQueryAction extends DzipBaseAction {
	private JdbcAccessAwareProcessor corebankAccess;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		log.info("AdditionalAmtQueryAction(待补登金额查询)=======================>Start");
		List<Map<String, Object>> recordList = new ArrayList<Map<String,Object>>();
		Map<String, Object> param = new HashMap<String, Object>();
		String PAN = String.valueOf(ctx.getData(Dict.CARD_NO));//卡号
		param.put(Dict.PAN, PAN);
		try {
			recordList = corebankAccess.getSqlMap().queryForList(SqlMaps.COMMON_QUERY_ADDITIONAL_AMT, param);
			ctx.setData(Constants.RECORDLIST, recordList);
		} catch (Exception e) {
			log.error("AdditionalAmtQueryAction(待补登金额查询)出错!");
			e.printStackTrace();
			throw new PeException(e.getMessage());
		}
	}
	public JdbcAccessAwareProcessor getCorebankAccess() {
		return corebankAccess;
	}
	public void setCorebankAccess(JdbcAccessAwareProcessor corebankAccess) {
		this.corebankAccess = corebankAccess;
	}
}
