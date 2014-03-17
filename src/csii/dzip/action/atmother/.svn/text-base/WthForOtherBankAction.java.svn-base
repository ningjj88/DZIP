/**
 * ATM本行取款
 */
package csii.dzip.action.atmother;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 * @author xujin
 *@Description:他行取款交易
 * @version 1.0.1
 * @since 2012-10-22
 */
public class WthForOtherBankAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	public void execute(final Context ctx) throws PeException {
		logger.info("WthForOtherBank====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){	//若返回响应码为：成功
			String responcd =utilProcessor.getTranParamInfo(ctx,Constants.ISO8583); //获得交易参数信息
			ctx.setData(Constants.ISO8583_RESCODE, responcd);
			if(Constants.PE_OK.equals(responcd)){
				ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM);//交易来源
				ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   //资金类型
				ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);							//交易状态,正常、冲正：'C'	抹帐:'EC'
				ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
				if(Constants.PRI_SOURCE_YLQZ_VALUE.equals(ctx.getString(Constants.PRI_SOURCE)))
					utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
				if(Constants.PRI_SOURCE_JCK_VALUE.equals(ctx.getString(Constants.PRI_SOURCE)))
					utilProcessor.deductTranAMT(ctx, Constants.FIX8583);				//执行数据库操作
			}
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);               //交易状态：失败
		}
		updateJoural.execute(ctx); 									    // 修改流水交易状态
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
