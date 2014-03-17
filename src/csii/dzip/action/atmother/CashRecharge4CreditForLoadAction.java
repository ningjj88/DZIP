package csii.dzip.action.atmother;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 * 现金充值圈存(本代本,本代他)
 * @author xiehai
 * @date 2013-12-11 下午05:09:12
 */
public class CashRecharge4CreditForLoadAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@Override
	public void execute(Context ctx) throws PeException {
		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(ctx.getData(Constants.ISO8583_SOURID))){//本代本
			logger.debug("CashRecharge4CreditForLoadAction(本行现金充值圈存交易)====================>Start!");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//设置为本代本交易
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){	//若银联数据卡验证响应码为：成功
				String responcd = utilProcessor.getTranParamInfo(ctx, Constants.ISO8583); //获得交易参数信息
				if(Constants.PE_OK.equals(responcd)){
					//设置卡的有效期
					dzipProcessTemplate.getIcCardExpDate(ctx);
					//为了方便对账 设置渠道为CLM
					ctx.setData(Constants.IN_RTXNSOURCECD, CLM); //交易来源
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   // 资金类型
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);							//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
					utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
					ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
				}
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);               //交易状态：失败
			}
			updateJoural.execute(ctx); // 修改流水交易状态
		}else {//本代他
			logger.debug("CashRecharge4CreditForLoadAction(他行现金充值圈存交易)====================>Start!");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){	//若返回响应码为：成功
				String responcd = utilProcessor.getTranParamInfo(ctx, Constants.ISO8583); //获得交易参数信息
				if(Constants.PE_OK.equals(responcd)){
					ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE)+ATM);//交易来源
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   //资金类型
					ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
					try {
						utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
					}catch (PeException e) {
						//对于发卡方交易成功，不管受理方交易成功还是不成功都要返回给ATMP端成功，这样避免发卡方资金流失
						ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK ); //响应码
					}
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);
				}
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);               //交易状态：失败
			}
			updateJoural.execute(ctx); // 修改流水交易状态
		}
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
