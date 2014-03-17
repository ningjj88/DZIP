package csii.dzip.action.atmother;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 * @author 徐锦
 *@Description:他行存款交易
 *@date：2012-10-23
 */
public class DepForOtherBankAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	public void execute(final Context ctx) throws PeException {
		logger.debug("DepForOtherBank(他行存款交易)====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){	//若返回响应码为：成功
			String responcd =utilProcessor.getTranParamInfo(ctx,Constants.ISO8583); //获得交易参数信息
			if(Constants.PE_OK.equals(responcd)){
				ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM);//交易来源
				ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   //资金类型
				ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
				ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
				try {	
					if(Constants.PRI_SOURCE_YLQZ_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
						utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
					if(Constants.PRI_SOURCE_JCK_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
						utilProcessor.deductTranAMT(ctx, Constants.FIX8583);				//执行数据库操作
				}catch (PeException e) {
					//对于发卡方存款交易成功，不管受理方交易成功还是不成功都要返回给ATMP端成功，这样避免发卡方资金流失
					ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_OK ); //响应码
				}
			}
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);               //交易状态：失败
		}
		updateJoural.execute(ctx); // 修改流水交易状态
		//如果是无卡存款就需要还原交易码为220000并设置交易ID为021022，返回ATMP端
		if(Constants.TRANCD_020022.equals(ctx.getString(Constants.PE_OUT_TRANCD))){
			ctx.setData(Constants.ISO8583_PRO_CODE,Constants.PROCODE_220000);
			ctx.setData(Constants.ISO8583_TRANSACTION_ID,Constants.TRANCD_021022);
		}
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
