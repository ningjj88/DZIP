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
import csii.dzip.core.Errors;

/**
 * @author xujin
 * @date 2011.10.14
 * @desc 手续费规则管理
 */
public class ManagementFeeCodexAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		logger.info("MangementFeeCodexAction==========================>Start");
		final Map paramMap = new HashMap();
		List listRe = new ArrayList();
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				paramMap.put(Constants.IN_BUSICD, ctx.getData(Constants.IN_BUSICD));
				paramMap.put(Constants.IN_BUSITYP, ctx.getData(Constants.IN_BUSITYP));
				paramMap.put(Constants.IN_BUSIDESC, ctx.getData(Constants.IN_BUSIDESC));
				paramMap.put(Constants.IN_DECDIGIT, ctx.getData(Constants.IN_DECDIGIT));
				paramMap.put(Constants.IN_DOWNLINE, ctx.getData(Constants.IN_DOWNLINE));
				paramMap.put(Constants.IN_UPLINE, ctx.getData(Constants.IN_UPLINE));
				paramMap.put(Constants.IN_ARITHMETIC, ctx.getData(Constants.IN_ARITHMETIC));
				paramMap.put(Constants.IN_FLAG, ctx.getData(Constants.IN_FLAG));
				dzipdbAccess.getSqlMap().update("platformAdmin.ManagementFeeCodex",paramMap);
				return null;
			}
		});
		if (!Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))){
			if (String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)) {
				logger.info("==========================>System Error!");
				throw new PeException(Errors.UPDATE_CHANNEL_INFO_FAILED);
			} else {
				logger.info("==========================>DataBase Error!");
				throw new PeException(String.valueOf(paramMap
						.get(Constants.OUT_ERROR_MSG)));
			}
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
