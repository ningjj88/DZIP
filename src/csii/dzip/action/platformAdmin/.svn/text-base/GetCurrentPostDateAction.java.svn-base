/**
 * 
 */
package csii.dzip.action.platformAdmin;

import java.sql.SQLException;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.core.Errors;

/**
 * @author chenshaoqi
 * @date 2010.09.07
 * @desc 装载任务
 */
public class GetCurrentPostDateAction extends DzipBaseAction{
	private DzipProcessTemplate dzipProcessTemplate;

	public void execute(Context ctx) throws PeException {
		logger.info("GetCurrentPostDateAction==========================>Start");
		try {
			String SystemDate = dzipProcessTemplate.queryPostDate();//获得账务日期，即交易日期
			ctx.setData(Constants.SYSTEM_DATE,SystemDate);
		} catch (SQLException e) {
			logger.info("==========================>DataBase Error!");
			throw new PeException(Errors.GET_SYSTEM_DATE_FAILED);
		}
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}


	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
