package csii.dzip.action.cups.common;

import java.util.HashMap;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;
/**
 *@ClassName:&{type_name}
 *@Description: 建立委托
 *@author :xujin
 *@date：20112-03-017
 */
public class EstablishEntrustAction  extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;//2013-07-23 xiehai
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS EstablishEntrustAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		//建立委托根据22#第3位判断是否验证密码 若为1 则验证 若为2 则不需验证
//		validTranField(ctx);
		ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
		ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
		HashMap procedureMap = utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);//查询及验证账户
		//开通无卡消费和小额支付 2013-07-24 xiehai
		if(Constants.PE_14.equals(ctx.getData(Constants.IN_ASSBUSTYP))){//如果关联业务为小额支付
			if(Constants.PE_OK.equals(procedureMap.get(Constants.OUT_RESPONCD))){//验证通过
				dzipProcessTemplate.OpenNoCardSelf(ctx);//开通无卡消费
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);
				ctx.setData(Constants.FIX8583_RESP, Constants.PE_OK);
			}else if(Constants.PE_P1.equals(procedureMap.get(Constants.OUT_RESPONCD))){//通信号码未在行里存留
				dzipProcessTemplate.openSmallAmountPayment(ctx);//开通小额临时支付
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_P1);
				ctx.setData(Constants.FIX8583_RESP, Constants.PE_P1);
			}else if(Constants.PE_05.equals(procedureMap.get(Constants.OUT_RESPONCD))){//身份验证没通过或电话验证没通过
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_05);
				ctx.setData(Constants.FIX8583_RESP, Constants.PE_05);
				throw new PeException("开通无卡消费验证没通过.");
			}
		}
		updateJoural.execute(ctx);        //更新交易流水表交易状态
	}

	/**
	 * 验证建立委托的域是否符合交易规范
	 * @param ctx
	 * @throws PeException
	 */
	public void validTranField(final Context ctx) throws PeException{
//		 String reacode1314=ctx.getString(Constants.CHECK_ECI);
//		 String tranTyp=ctx.getString(Constants.IN_ASSBUSTYP);
//		 //第61域AM用法,判断超出发卡方验证范围
//		 if(!Constants.PE_MINUSONE.equals(ctx.getString(Constants.PE_AM))){
//			 int AM=Integer.valueOf(ctx.getString(Constants.PE_AM));
//			 String field61=String.valueOf(ctx.getData(Constants.PE_FIELD_61));
//			 if(tranTyp.equals(Constants.PE_14)){//如果是无卡自助银联代发动态验证码的建立委托
//				 if(Constants.PE_09.equals(reacode1314)&&field61.substring(AM+9, AM+10).equals(Constants.PE_ONE)){//自主识别
//					 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
//					 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
//					 throw new PeException("自主识别无卡自助不需要验证姓名，超出发卡方验证范围");
//				 }
//				 if(Constants.PE_10.equals(reacode1314)&&field61.substring(AM+10, AM+11).equals(Constants.PE_ONE)){//辅助识别
//					 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
//					 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
//					 throw new PeException("辅助识别无卡自助不需要验证手机号码，超出发卡方验证范围");
//				 }
//			 }
//		 }
//		 if(tranTyp.equals(Constants.PE_14)&&Constants.PE_N.equals(ctx.getString(Constants.CHECK_AM))){
//			 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
//			 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
//			 throw new PeException("开通无卡自助的建立委托必须包含AM验证信息，超出发卡方验证范围");
//		 }
		//建立委托不输入密码
		if(null == ctx.getData(Constants.ISO8583_PINDATA)){
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_05);
			ctx.setData(Constants.FIX8583_RESP, Constants.PE_05);
			throw new PeException("建立委托时必须输入密码!");
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
