package csii.dzip.action.cups.atm;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 * 他带本现金充值圈存
 * @author xiehai
 * @date 2013-12-26 下午02:54:36
 */
public class CashRecharge4CreditForLoadAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@Override
	public void execute(Context ctx) throws PeException {
		logger.debug("CashRecharge4CreditForLoadAction(他带本现金充值圈存)====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		//设置卡的有效期
		dzipProcessTemplate.getIcCardExpDate(ctx);
		ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE) + CLM); //交易来源
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型 从银联过来的只能用电子现金
		ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C);							//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
		utilProcessor.deductTranAMT(ctx, Constants.ISO8583);					//执行数据库操作
		ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
		updateJoural.execute(ctx); // 修改流水交易状态
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}
	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
