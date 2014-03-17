/**
 * ATM本行取款
 */
package csii.dzip.action.atm;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 * @author xujin
 * @Description:本带本取款交易
 * @version 1.0.1
 * @since 2010-7-7
 */
public class Withdrawal4ATMAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("Withdrawal4ATMAction=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_RTXNSOURCECD,ATM);//交易来源
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);     //资金类型
		ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG,Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐 ,5:境外
//		ctx.setData(Constants.CHECK_TRACK2, Constants.PE_Y);                //需要验证2磁
//		ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);                //需要验证密码
		utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
		ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
		ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
		ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
		if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
			Map formatAmtMap = new HashMap();
			formatAmtMap.put(Constants.ACCTTYPCD,  ctx.getData(Constants.ACCTTYPCD));		//帐户类型
			formatAmtMap.put(Constants.BALAMT, ctx.getData(Constants.BALAMT));				//账面余额
			formatAmtMap.put(Constants.AVAILBALAMT,  ctx.getData(Constants.AVAILBALAMT));  	//可用余额
			String availBalAmt = Util.formatAmt(formatAmtMap); 				//返回余额格式化
			ctx.setData(Constants.ISO8583_BALAMT, availBalAmt);           					//余额
		}
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
