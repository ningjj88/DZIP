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
 * @desc 更改渠道类型交易
 */
public class UpdChannelTypTranAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		logger.info("UpdChannelTypTranAction==========================>Start");
		final Map paramMap = new HashMap();
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				paramMap.put(Constants.IN_OUTSIDEFLAG, ctx.getString(Constants.REGIONRANGEFLAG));
				paramMap.put(Constants.IN_REGIONCD, ctx.getString(Constants.PE_REGIONCD));
				paramMap.put(Constants.IN_BUSITYP, ctx.getData(Constants.BUSTYPDESC));
				paramMap.put(Constants.IN_CHANNELTYPCD, ctx.getData(Constants.CHANNELTYPDESC));
				paramMap.put(Constants.IN_TRANCD, ctx.getData(Constants.TRANDESC));
				paramMap.put(Constants.IN_FLAG, ctx.getData(Constants.OPENYN));
				paramMap.put(Constants.IN_TRANLLIMITAMT, ctx.getData(Constants.TRANLLIMITAMT));
				paramMap.put(Constants.IN_DAILYTRANLLIMITAMT, ctx.getData(Constants.DAILYTRANLLIMITAMT));
				paramMap.put(Constants.IN_DAILYLIMITTRANCNT, ctx.getData(Constants.DAILYLIMITTRANCNT));
				paramMap.put(Constants.IN_ACCTSRCDAILYLIMITTCNT, ctx.getData(Constants.ACCTSRCDAILYLIMITTCNT));
				dzipdbAccess.getSqlMap().update("platformAdmin.UpdChannelTypTran", paramMap);
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
