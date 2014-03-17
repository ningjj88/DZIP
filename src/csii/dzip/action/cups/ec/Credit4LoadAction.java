package csii.dzip.action.cups.ec;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;
import csii.dzip.core.InitData4Dzip;

/**
 * 圈存机电子现金指定账户圈存
 * @author xiehai
 * @version 1.0.0
 * @date 2013-8-8 上午10:46:52
 */
public class Credit4LoadAction extends DzipBaseAction{
	private ActionUtilProcessor actionUtilProcessor;
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("Credit4LoadAction=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01);//业务类型：现金及转账
		//指定账户圈存金额限制
		actionUtilProcessor.valTranLimit(ctx);
		//根据受理机构码判断是否是本行圈存机
		if(InitData4Dzip.getAcqOrgCd().equals(ctx.getData(Constants.ISO8583_ACQCODE))
		   || InitData4Dzip.getAcqOrgCd4Ic().equals(ctx.getData(Constants.ISO8583_ACQCODE))){//如果是本行圈存机
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//设置交易来源为本行圈存机
			ctx.setData(Constants.IN_RTXNSOURCECD, CLM);//交易来源
		}else{//他行圈存机
			actionUtilProcessor.calcTranFee(ctx);//计算手续费
			ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE) + CLM);//交易来源
		}
		//设置卡的有效期
		//Init.setIcCardExpDate(ctx);
		dzipProcessTemplate.getIcCardExpDate(ctx);
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);//资金类型：电子现金
		ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C);//交易状态,正常、冲正：'C' 抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);//冲正标志:0：正常 1：冲正 2：抹帐 ,5:境外
		actionUtilProcessor.deductTranAMT(ctx, Constants.ISO8583);
		ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
//		将更新电子钱包余额移至核心处理
//		if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){//如果交易成功,更新电子钱包余额
//			logger.info("交易金额 = " + ctx.getString(Constants.PE_TRAN_AMT));
//			dzipProcessTemplate.updateElectronicCashBal(ctx);
//		}
		updateJoural.execute(ctx);
	}

	public ActionUtilProcessor getActionUtilProcessor() {
		return actionUtilProcessor;
	}
	public void setActionUtilProcessor(ActionUtilProcessor actionUtilProcessor) {
		this.actionUtilProcessor = actionUtilProcessor;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
