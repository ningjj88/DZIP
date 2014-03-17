package csii.dzip.action.platformAdmin;

import java.util.HashMap;
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
 * @date 2012.11.15
 * @desc 更改渠道类型
 */
public class UpdChannelTypAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		logger.info("UpdChannelTypAction==========================>Start");
		final Map paramMap = new HashMap();
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				paramMap.put(Constants.IN_CHANNID, ctx.getData(Constants.CHANNID));
				paramMap.put(Constants.IN_CHANNELTYPCD, ctx.getData(Constants.CHANNELTYPCD));
				paramMap.put(Constants.IN_CHANNELTYPSTAT, ctx.getData(Constants.CHANNELTYPSTAT));
				dzipdbAccess.getSqlMap().update("platformAdmin.UpdChannelTyp", paramMap);
				return null;
			}
		});
		if (!Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			logger.info("==========================>DataBase Error!");
			throw new PeException(String.valueOf(paramMap
					.get(Constants.OUT_ERROR_MSG)));
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
