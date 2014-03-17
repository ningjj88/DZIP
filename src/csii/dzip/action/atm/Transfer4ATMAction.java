package csii.dzip.action.atm;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:转账交易
 *@author :徐锦
 *@date：2012-01-11
 */
public class Transfer4ATMAction extends DzipBaseAction {
	private ActionUtilProcessor  utilProcessor;
	private UpdateJoural updateJoural;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("Transfer4ATMAction=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_RTXNSOURCECD,ATM);//交易来源

		//报表统计卡转账交易有问题，发现是本行卡转账交易的资金类型传入为CASH导致的问题，资金类型应该是EL
		//2013-07-04
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);
//		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);     //资金类型
		ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG,Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐 ,5:境外
//		ctx.setData(Constants.CHECK_TRACK2, Constants.PE_Y);                //需要验证2磁
//		ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);                //需要验证密码
		utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
		ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
		ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
		ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
		updateJoural.execute(ctx);	// 修改流水交易状态
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}



}
