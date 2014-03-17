
package csii.dzip.action.cups.pos;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:他行消费
 *@author :xujin
 *@date：2010-08-16
 */
public class PosConsumeAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;

	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS PosConsumeAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_03); 	//业务类型:03_消费及预授权
		validTranField(ctx);
		utilProcessor.calcTranFee(ctx);//计算手续费
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
		ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);		//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);	//冲正标志:0：正常   1：冲正 	2：抹帐
//			ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);                //需要验证密码
		utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
		ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
		ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
		updateJoural.execute(ctx);						// 修改流水交易状态;
	}

	/**
	 * 验证该交易的域是否符合交易规范
	 * @param ctx
	 * @throws PeException
	 */
	public void validTranField(final Context ctx) throws PeException{
		String reacode0910=ctx.getString(Constants.IN_CHANNELTYPCD);
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
					 && Constants.PE_09.equals(reacode1314)){//09:自主识别无卡自助验证是否开通无卡自助
				 ctx.setData(Constants.IN_BUSITYP, Constants.PE_07); 	//业务类型:07_无卡自助消费及预授权
				 //验证是否开通无卡自助
				 logger.info("=========>验证是否已开通无卡自助");
				 String tranStat=dzipProcessTemplate.getOpenNoCardSelf((String)ctx.getData(Constants.PE_ACC_NO));
				 if(tranStat == null || tranStat.equals(Constants.PE_ZERO)){
					 ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_57);
					 ctx.setData(Constants.FIX8583_RESP, Constants.PE_57);
					 throw new PeException("没有开通无卡自助,不允许此卡交易");
				 }

				 //2013-06-07 xiehai
				 if( Constants.PE_10.equals(reacode1314)){
					 if(Constants.PE_Y.equals(ctx.getData(Constants.CHECK_PIN))){
						 //无卡消费（辅助识别）不支持密码验证，如果输入密码，应该返40失败
						 ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_CHANN_INVALIDATE);
						 ctx.setData(Constants.FIX8583_RESP, Constants.PE_CHANN_INVALIDATE);
						 throw new PeException("无卡消费（辅助识别）不支持密码验证");
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

		// 如果是固话消费交易(F60.2.5=09,17) 校验密码
		if((Constants.PE_09.equals(reacode0910)||Constants.PE_17.equals(reacode0910))){
			 ctx.setData(Constants.CHECK_PIN,  Constants.PE_Y);//验证密码
		}
		//如果是有卡无AM消费就验证密码
		if(Constants.PE_N.equals( ctx.getData(Constants.CHECK_PIN))
				&&Constants.PE_Y.equals( ctx.getData(Constants.CHECK_TRACK2))
				&&Constants.PE_N.equals(ctx.getData(Constants.CHECK_AM))){
			 ctx.setData(Constants.CHECK_PIN,  Constants.PE_Y);
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
