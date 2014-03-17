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
 *@Description: 代收
 *@author :xujin
 *@date：20112-03-017
 */
public class CollectionAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS CollectionAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_04); 	//业务类型:04_代收
		validTranField(ctx);
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);//交易来源
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     //资金类型
		ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG,Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐 ,5:境外
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
		updateJoural.execute(ctx);	// 修改流水交易状态
	}
	
	/**
	 * 验证该交易的域是否符合交易规范
	 * @param ctx
	 * @throws PeException 
	 */
	public static void validTranField(final Context ctx) throws PeException{
//		if(!Constants.PE_MINUSONE.equals(ctx.getString(Constants.PE_AM))){//第61域AM用法,判断超出发卡方验证范围
//			 int AM=Integer.valueOf(ctx.getString(Constants.PE_AM));
//			 String field61=String.valueOf(ctx.getData(Constants.PE_FIELD_61));
//			 if(field61.substring(AM+6, AM+7).equals(Constants.PE_ONE)){
//				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
//				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
//				 throw new PeException("代收不需要验证委托关系，超出发卡方验证范围");
//			 }
//		}
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
