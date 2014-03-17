/**
 * 
 */
package csii.dzip.action.platformAdmin;

import java.sql.SQLException;
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
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.core.Errors;
import csii.dzip.core.InitData4Dzip;

/**
 * @author chenshaoqi
 * @date 2010.08.23
 * @desc 流水信息历史查询
 */
public class InqJournalHistInfoAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		List listRe = new ArrayList();
		final Map paramMap = new HashMap();
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Init.initInqJournalHistInfo(ctx, paramMap);
				dzipdbAccess.getSqlMap().queryForObject("platformAdmin.InqJournalHistInfo", paramMap);
				return null;
			}
		});
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			listRe = (List) paramMap.get(Constants.OUT_REFCURSOR);   //获得结果集
			//listRe = ConvertUtilProcessor.convertList(listRe);
			ctx.setData(Constants.RECORDLIST, listRe);               //填充结果集
			ctx.setData(Constants.TOTALCOUNT,paramMap.get(Constants.OUT_TOTALCOUNT));          //返回交易总笔数
			ctx.setData(Constants.VALIDATECOUNT,paramMap.get(Constants.OUT_VALIDATECOUNT));    //返回成功笔数
			ctx.setData(Constants.INVALIDATECOUNT,paramMap.get(Constants.OUT_INVALIDATECOUNT));//返回失败笔数
			ctx.setData(Constants.OTHERCOUNT,paramMap.get(Constants.OUT_OTHERCOUNT));          //其他
			ctx.setData(Constants.TOTALAMT,paramMap.get(Constants.OUT_TOTALAMT));              //返回交易总金额
			ctx.setData(Constants.VALIDATEAMT,paramMap.get(Constants.OUT_VALIDATEAMT));        //返回成功金额
			ctx.setData(Constants.INVALIDATEAMT,paramMap.get(Constants.OUT_INVALIDATEAMT));    //返回失败金额
			ctx.setData(Constants.OTHERAMT,paramMap.get(Constants.OUT_OTHERAMT));              //其他
		}
		else {
			if (String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)).equals(Constants.Error_99999)) {
				logger.debug("==========================>System Error!");
				throw new PeException(Errors.GET__JOURNAL__HIST_FAILED);
			} else {
				logger.debug("==========================>DataBase Error!");
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
