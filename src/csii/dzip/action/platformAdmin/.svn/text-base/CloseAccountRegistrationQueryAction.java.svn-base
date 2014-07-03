package csii.dzip.action.platformAdmin;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.SqlMaps;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.core.Dict;

/**
 * IC卡销户登记查询
 * @author xiehai
 * @date 2014-4-15 下午03:28:53
 */
public class CloseAccountRegistrationQueryAction extends DzipBaseAction {
	private JdbcAccessAwareProcessor dzipdbAccess;
	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		log.info("CloseAccountRegistrationQueryAction(销户登记查询)=======================>Start");
		Map<String, Object> map = new HashMap<String, Object>();
		String PAN = String.valueOf(ctx.getData(Dict.CARD_NO));
		try {
			map = (Map) dzipdbAccess.getSqlMap().queryForObject(SqlMaps.COMMON_QUERYCLOSEACCOUNTRETISTER, PAN);
			ctx.setDataMap(map);
		}catch (NullPointerException e) {
			throw new PeException("该帐号没有销户登记记录!");
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("CloseAccountRegistrationQueryAction==============>获得IC卡销户登记记录出错!");
			throw new PeException(e.getMessage());
		}
	}
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
}
