
package csii.dzip.action.jck.common;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description: 他行转出记账：ATM，CDM
 *@author :chenshq
 *@date：2010-09-07
 */
public class TransferOutAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK TransferOutAction ==================>Start");
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM);
		ctx.setData(Constants.IN_DEPFLAG, Constants.TRS_FLAG_WTH); 			// 出入账标识 0：出账  1：入账
		ctx.setData(Constants.ATMYN, Constants.PE_ONE); 					//是否ATM  ATM ：1，POS：0
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
		ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
		ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);                //成商需要验证密码
		ctx.setData(Constants.IN_DEPFLAG, Constants.PE_TWO); 	//出入账标识:0:出  1:入
		utilProcessor.valTranLimit(ctx);//验证金额限额和计算手续费
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){ 
			ctx.setData(Constants.IN_DEPFLAG, Constants.TRS_FLAG_WTH); 
			utilProcessor.deductTranAMT(ctx,Constants.FIX8583);					//执行数据库操作
			ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
			if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
				Map formatAmtMap = new HashMap();
				formatAmtMap.put(Constants.ACCTTYPCD,  ctx.getData(Constants.ACCTTYPCD));		//帐户类型
				formatAmtMap.put(Constants.BALAMT, ctx.getData(Constants.BALAMT));				//账面余额
				formatAmtMap.put(Constants.AVAILBALAMT,  ctx.getData(Constants.AVAILBALAMT));  	//可用余额
				String availBalAmt = Util.formatAmt(formatAmtMap); 				//返回余额格式化
				ctx.setData(Constants.FIX8583_RETU, availBalAmt);           					//余额
			}
		}else{
			ctx.setData(Constants.FIX8583_RESP,ctx.getData(Constants.PE_HOST_RESP_CD)); //获得响应码，并返回
			ctx.setData(Constants.FIX8583_EXPI,Constants.PE_EXPIREYEAR);   					//卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); 							//交易状态：失败
		}
		updateJoural.execute(ctx);															// 修改流水交易状态
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
