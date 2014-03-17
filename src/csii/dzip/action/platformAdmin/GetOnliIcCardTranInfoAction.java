package csii.dzip.action.platformAdmin;

import java.util.ArrayList;
import java.util.List;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.base.constant.SqlMaps;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.core.InitData4Dzip;

/**
 * 柜面发起IC卡电子现金交易前,返回给柜面的数据
 * @author xiehai
 * @date 2014-2-25 下午03:34:01
 */
public class GetOnliIcCardTranInfoAction extends DzipBaseAction{
	private JdbcAccessAwareProcessor dzipdbAccess;

	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}

	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		logger.info("GetOnliIcCardTranInfoAction(柜面电子现金交易前获得数据)==========================>Start");
		List<String> tranList = new ArrayList<String>();//柜面可用的电子现金交易列表
		String TranLLimitAmt = null;
		try {
			tranList = dzipdbAccess.getSqlMap().queryForList(SqlMaps.COMMON_GETACTIVEICTRAN4ONLI);
			ctx.setData(Constants.RECORDLIST, tranList);
		} catch (Exception e) {
			logger.error("获得柜面可用电子现金交易出错!" + e.getMessage());
			e.printStackTrace();
		}
		try {
			TranLLimitAmt = (String) dzipdbAccess.getSqlMap().queryForObject(SqlMaps.COMMON_GETCREDIT4LOADLIMIT);
		} catch (Exception e) {
			logger.error("获得圈存金额上限出错!" + e.getMessage());
			e.printStackTrace();
		}
		ctx.setData(Constants.ISO8583_ACQCODE, InitData4Dzip.getAcqOrgCd());//32#
		ctx.setData(Constants.ISO8583_FORWARDCODE, InitData4Dzip.getForwOrgCd());//33#
		ctx.setData(Constants.TRANLLIMITAMT, TranLLimitAmt);//圈存最大金额限制
	}
}
