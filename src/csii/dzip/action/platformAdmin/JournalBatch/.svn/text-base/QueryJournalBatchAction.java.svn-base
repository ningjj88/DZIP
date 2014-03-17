/**
 * 
 */
package csii.dzip.action.platformAdmin.JournalBatch;

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
import csii.dzip.action.util.ConvertUtilProcessor;
import csii.dzip.action.util.Init;
import csii.dzip.core.Errors;

/**
 * @author xujin
 * @date 2011.04.23
 * @desc 批量流水信息查询
 */
public class QueryJournalBatchAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		logger.info("QueryJournalBatchAction==========================>Start");
		List listRe = new ArrayList();
	    final Map paramMap = new HashMap();
	    dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Init.initQueryJournalBatch(ctx, paramMap);
				dzipdbAccess.getSqlMap().queryForObject("platformAdmin.QueryJournalBatch", paramMap);
				return null;
			}
		});
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			listRe = (List) paramMap.get(Constants.OUT_REFCURSOR); // 获得结果集
			//listRe = ConvertUtilProcessor.BatchList(listRe);
			ctx.setData(Constants.RECORDLIST, listRe); // 填充结果集
		} else {
			if (String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)) {
				logger.info("==========================>System Error!");
				throw new PeException(Errors.SYSTEM_ERROR);
			} else {
				logger.info("==========================>DataBase Error!");
				throw new PeException(String.valueOf(paramMap.get(Constants.OUT_ERROR_MSG)));
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
