
package csii.dzip.action.cups.check;

import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;

/**
 *@ClassName:&{type_name}
 *@Description:银联对账
 *@author :chenshq
 *@date：2010-08-16
 */
public class CheckAction extends DzipBaseAction {

	/* (non-Javadoc)
	 * @see csii.base.action.BaseAction#execute(com.csii.pe.core.Context)
	 */
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS CheckAction ==================>Start");
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Map sqlMap = ctx.getDataMap();
					try {
						dzipdbAccess.getSqlMap().insert("cups.insertCupsCheck", sqlMap); //添加银联对账记录。
						sqlMap.put(Constants.IN_SYSTRACENUM, ctx.getData(Constants.ISO8583_SYSTRACENUM));   //系统跟踪号
						sqlMap.put(Constants.IN_SETTLMTDATE, ctx.getData(Constants.ISO8583_SETDATE));		//清算日期	
						dzipdbAccess.getSqlMap().update("cups.cupsCheck", sqlMap); //添加银联对账记录。
						logger.info(Constants.OUT_SETTLMTCD+ "===========>" + sqlMap.get(Constants.OUT_SETTLMTCD));
						logger.info(Constants.OUT_ERROR_NBR + "===========>" + sqlMap.get(Constants.OUT_ERROR_NBR));
						logger.info(Constants.OUT_ERROR_MSG + "===========>" + sqlMap.get(Constants.OUT_ERROR_MSG));
						if(sqlMap.get(Constants.OUT_SETTLMTCD)==null 
								|| !Constants.PE_ZERO.equals(String.valueOf(sqlMap.get(Constants.OUT_ERROR_NBR)))){
//							dzipdbAccess.getTransactionTemplate().getTransactionManager().rollback(arg0);
							throw new Exception("对账失败");
							}
						ctx.setData(Constants.ISO8583_SETTLE_CODE, sqlMap.get(Constants.OUT_SETTLMTCD));
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new PeException("对账失败");
						} catch (PeException e1) {
							e1.printStackTrace();
						}
					}
				return null;
			}});
	}

	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
 }
