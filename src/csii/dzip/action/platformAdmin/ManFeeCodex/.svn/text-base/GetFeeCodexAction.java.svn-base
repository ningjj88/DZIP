package csii.dzip.action.platformAdmin.ManFeeCodex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;

/**
 * @author xujin
 * @date 2011.10.14
 * @desc 获得手续费规则信息
 */
public class GetFeeCodexAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		logger.info("GetFeeCodexAction==========================>Start");
		final Map paramMap = new HashMap();
		List listRe = new ArrayList();
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				dzipdbAccess.getSqlMap().queryForObject("platformAdmin.GetFeeCodex", paramMap);
				return null;
			}
		});
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			listRe = (List) paramMap.get(Constants.OUT_REFCURSOR); // 获得结果集
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