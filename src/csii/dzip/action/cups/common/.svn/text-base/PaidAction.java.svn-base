package csii.dzip.action.cups.common;

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
 *@ClassName:&{type_name}
 *@Description: 代付
 *@author :xujin
 *@date：20112-03-017
 */
public class PaidAction  extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS PaidAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_05); 	//业务类型:05_代付
		validTranField(ctx);
		utilProcessor.valTranLimit(ctx);//验证交易限制
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){ 
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);							//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
			utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				Map formatAmtMap = new HashMap();
				formatAmtMap.put(Constants.ACCTTYPCD,  ctx.getData(Constants.ACCTTYPCD));		//帐户类型
				formatAmtMap.put(Constants.BALAMT, ctx.getData(Constants.BALAMT));				//账面余额
				formatAmtMap.put(Constants.AVAILBALAMT,  ctx.getData(Constants.AVAILBALAMT));  	//可用余额
				String availBalAmt = Util.formatAmt(formatAmtMap); 				//返回余额格式化
				ctx.setData(Constants.ISO8583_BALAMT, availBalAmt);           					//余额
			}
		}else{
			ctx.setData(Constants.ISO8583_RESCODE,ctx.getData(Constants.PE_HOST_RESP_CD)); //获得响应码，并返回
			ctx.setData(Constants.ISO8583_EXPDAT,Constants.PE_EXPIREYEAR);   					//卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); 							//交易状态：失败
		}
		updateJoural.execute(ctx); 														// 修改流水交易状态
	}
	
	/**
	 * 验证该交易的域是否符合交易规范
	 * @param ctx
	 * @throws PeException 
	 */
	public void validTranField(final Context ctx) throws PeException{
		//第61域AM用法,判断超出发卡方验证范围
		if(!Constants.PE_MINUSONE.equals(ctx.getString(Constants.PE_AM))){
			 int AM=Integer.valueOf(ctx.getString(Constants.PE_AM));
			 String field61=String.valueOf(ctx.getData(Constants.PE_FIELD_61));
			 if(field61.substring(AM+6, AM+7).equals(Constants.PE_ONE)||field61.substring(AM+10, AM+11).equals(Constants.PE_ONE)){
				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
				 throw new PeException("代付不需要验证委托关系或手机号码，超出发卡方验证范围");
			 }
		}
		//代付交易必须上送61域
		if(ctx.getData(Constants.PE_FIELD_61)==null){
			 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_05);
			 ctx.setData(Constants.FIX8583_RESP,Constants.PE_05);
			 throw new PeException("代付交易必须上送61域,暂不承兑");
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
}
