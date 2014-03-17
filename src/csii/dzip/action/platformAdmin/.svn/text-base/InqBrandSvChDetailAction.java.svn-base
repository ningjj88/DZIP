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

public class InqBrandSvChDetailAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		logger.info("InqBrandSvChDetailAction==========================>Start");
		List listRe = new ArrayList();
	    final Map paramMap = new HashMap();
	    dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				paramMap.put(Constants.IN_POSTDATE, (String)ctx.getData(Constants.POSTDATE));//账务日期
				paramMap.put(Constants.IN_FLAG,(String)ctx.getData(Constants.AUDITTYPE));//对账类型
				dzipdbAccess.getSqlMap().queryForObject("platformAdmin.GetBrandSvChDetail", paramMap);
				return null;
			}
		});
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			listRe = (List) paramMap.get(Constants.OUT_REFCURSOR); // 获得结果集
			ctx.setData(Constants.RECORDLIST, listRe); // 填充结果集
			ctx.setData(Constants.TOTALCOUNT,paramMap.get(Constants.OUT_TOTALCOUNT)); //返回交易总笔数
			ctx.setData(Constants.TOTALPDG1,paramMap.get(Constants.OUT_TOTALPDG1));//受理方收总金额
			ctx.setData(Constants.TOTALPDG2,paramMap.get(Constants.OUT_TOTALPDG2));//对方行收总金额
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
