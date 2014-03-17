/**
 * 
 */
package csii.dzip.action.platformAdmin;

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
 * @author chenshaoqi
 * @date 2010.08.23
 * @desc 查询渠道信息
 */
public class GetChannelAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		logger.info("GetChannelAction==========================>Start");
		final Map paramMap = new HashMap();
		List listRe = new ArrayList();
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				dzipdbAccess.getSqlMap().queryForObject("platformAdmin.GetChannel",paramMap);
				return null;
			}
		});
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			listRe = (List) paramMap.get(Constants.OUT_REFCURSOR);      //获得结果集
			ctx.setData(Constants.RECORDLIST, listRe);               //填充结果集
		}else{
			if (String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)){
				logger.info("==========================>System Error!");
				throw new PeException(Errors.GET_CHANNEL_INFO_FAILED);
			}else{
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
