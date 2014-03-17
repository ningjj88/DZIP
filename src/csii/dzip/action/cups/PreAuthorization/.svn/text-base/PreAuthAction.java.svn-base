
package csii.dzip.action.cups.PreAuthorization;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description：预授权
 *@author :xujin
 *@date：2010-08-16
 */
public class PreAuthAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS PreAuthAction ==================>Start");
		Init.initHoldAmt( ctx.getString(Constants.PE_TRAN_AMT),ctx);//预授权金额
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_03); 	//业务类型:03_消费及预授权
		validTranField(ctx);
		utilProcessor.valTranLimit(ctx);//验证金额限额和计算手续费
		ctx.setData(Constants.IN_HOLDFALG, Constants.FREEZE); 	//冻结解冻标识:0：冻结 1：解冻
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS); //交易来源
		ctx.setData(Constants.IN_OPTNBR,null);  		//in_holdFlag标识判断是否必输项 0：为空	1：必输
		ctx.setData(Constants.IN_PREFLAG,Constants.PE_ZERO);  		//预授权：0
		Map procedureMap  = new HashMap();
		procedureMap = Init.initPreAuth(ctx, procedureMap,Constants.ISO8583);  //初始化
		utilProcessor.preAuthDoAction(ctx, procedureMap);    //执行预授权
		ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
		ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
		if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
			ctx.setData(Constants.ISO8583_AUTRESCOD,ctx.getData(Constants.PE_JOURNAL_NO));  //授权号
		}
		updateJoural.execute(ctx); 					// 修改流水交易状态
	}

	/**
	 * 验证该交易的域是否符合交易规范
	 * @param ctx
	 * @throws PeException
	 */
	public void validTranField(final Context ctx) throws PeException{
		String reacode1314=ctx.getString(Constants.CHECK_ECI);//09:自主识别无卡自助,10:辅助识别无卡自助
		String reacode2323=ctx.getString(Constants.IN_TRANLAUNCHWAYCD);
		/**
		 * 银联在线无卡支付业务逐步
		 * 过渡到《银联卡无卡自助消费业务规则》、《银联卡订购业务规则》、《银联卡代收业务规则》对应的业务处理
		 */
		//自助
		if((Constants.PE_09.equals(reacode1314)||Constants.PE_10.equals(reacode1314))
				&&Constants.PE_TWO.equals(reacode2323)){
			 if(Constants.PE_N.equals( ctx.getData(Constants.CHECK_TRACK2))
				&& Constants.PE_09.equals(reacode1314)){
				 ctx.setData(Constants.IN_BUSITYP, Constants.PE_07); 	//业务类型:07_无卡自助消费及预授权
				 //如果是自主识别无卡自助消费及预授权,就验证是否开通无卡自助
			     logger.info("=========>验证是否已开通无卡自助");
				 String tranStat=dzipProcessTemplate.getOpenNoCardSelf((String)ctx.getData(Constants.PE_ACC_NO));
				 if(tranStat==null||tranStat.equals(Constants.PE_ZERO)){
					 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_57);
					 ctx.setData(Constants.FIX8583_RESP,Constants.PE_57);
					 throw new PeException("没有开通无卡自助,不允许此卡进行无卡自助消费及预授权");
				 }

				 if( Constants.PE_10.equals(reacode1314)){
					 if(Constants.PE_Y.equals(ctx.getData(Constants.CHECK_PIN))){
						 //无卡消费消费及预授权不支持密码验证，如果输入密码，应该返40失败
						 ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_CHANN_INVALIDATE);
						 ctx.setData(Constants.FIX8583_RESP, Constants.PE_CHANN_INVALIDATE);
						 throw new PeException("无卡消费消费及预授权不支持密码验证");
					 }
				 }
			 }else{
				 ctx.setData(Constants.IN_BUSITYP, Constants.PE_06); 	//业务类型:06_有卡自助消费及预授权
			 }
		}

		//第61域AM用法,判断超出发卡方验证范围
		if(!Constants.PE_MINUSONE.equals(ctx.getString(Constants.PE_AM))){
			 int AM=Integer.valueOf(ctx.getString(Constants.PE_AM));
			 String field61=String.valueOf(ctx.getData(Constants.PE_FIELD_61));
			 if(Constants.PE_N.equals( ctx.getData(Constants.CHECK_TRACK2))&&Constants.PE_TWO.equals(reacode2323)){//2:自助
				 if(Constants.PE_09.equals(reacode1314)&&field61.substring(AM+9, AM+10).equals(Constants.PE_ONE)){//自主识别
					 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
					 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
					 throw new PeException("自主识别无卡自助不需要验证姓名，超出发卡方验证范围");
				 }
				 if(Constants.PE_10.equals(reacode1314)&&field61.substring(AM+10, AM+11).equals(Constants.PE_ONE)){//辅助识别
					 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
					 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
					 throw new PeException("辅助识别无卡自助不需要验证手机号码，超出发卡方验证范围");
				 }
			 }
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
