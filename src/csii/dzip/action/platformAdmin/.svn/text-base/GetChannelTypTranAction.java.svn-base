package csii.dzip.action.platformAdmin;

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
 * @author xujin
 * @date 2012.11.15
 * @desc 获得渠道类型交易
 */
public class GetChannelTypTranAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("GetChannelTypTranAction==========================>Start");
		final Map paramMap = new HashMap();
		dzipdbAccess.getSqlMap().queryForObject("platformAdmin.GetChannelTypTran", paramMap);
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			List listRe = (List) paramMap.get(Constants.OUT_REFCURSOR); // 获得结果集
			ctx.setData(Constants.RECORDLIST, listRe); // 填充结果集
		} else {
			logger.info("==========================>DataBase Error!");
			throw new PeException(String.valueOf(paramMap.get(Constants.OUT_ERROR_MSG)));
		}
	}
	/**
	 * @return the dzipdbAccess
	 */
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	/**
	 * @param dzipdbAccess the dzipdbAccess to set
	 */
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
}
