package csii.dzip.action.cups.common;

import java.util.HashMap;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;
import csii.dzip.core.InitData4Dzip;
/**
 *@ClassName:&{type_name}
 *@Description: 账户验证
 *@author :xujin
 *@date：20112-03-017
 */
public class ValidateAcctAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	private ActionUtilProcessor  utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS ValidateAcctAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		String tranTyp = ctx.getString(Constants.IN_ASSBUSTYP);
		//add by xiehai 2013-07-25
		//如果是无卡自助消费开通状态查询16,57#SE用法返回持卡人手机号
		//无卡自助消费开通状态查询暂时不做其他验证
		if(Constants.PE_16.equals(tranTyp)){
			String transtat = dzipProcessTemplate.getOpenNoCardSelf(ctx.getData(Constants.PE_ACC_NO).toString());
			String isOpenNoCardSelf;//是否开通无卡自助 0未开通 1已开通
			String noCardSelfBus;//卡通的无卡自助业务 00未开通 01无卡自助 02小额临时支付
			String phoneNumber = "";//若开通了无卡自助 返回持卡人电话 格式为135****1234
			final String LENGTH = "023";//SE用法长度
			final String SPACE = " ";//空格字符串
			if(transtat == null || "".equals(transtat) || Constants.PE_ZERO.equals(transtat)){//未开通无卡自助
				isOpenNoCardSelf = "0";
				noCardSelfBus = "00";
				phoneNumber = Util.fill("", 20, Constants.RIGHT, SPACE);
				ctx.setData(Constants.ISO8583_ADD_DATA_PRI, Constants.PE_AS+Constants.PE_SE+LENGTH+isOpenNoCardSelf+noCardSelfBus+phoneNumber);
			}else if(Constants.PE_TWO.equals(transtat)){//开通小额临时支付
				isOpenNoCardSelf = "1";
				noCardSelfBus = "02";
				phoneNumber = Util.fill("", 20, Constants.RIGHT, SPACE);
				ctx.setData(Constants.ISO8583_ADD_DATA_PRI, Constants.PE_AS+Constants.PE_SE+LENGTH+isOpenNoCardSelf+noCardSelfBus+phoneNumber);
			}else if(Constants.PE_ONE.equals(transtat)){//开通无卡自助
				String phonenbr = dzipProcessTemplate.getNoCardSelfPhonenbr(ctx.getData(Constants.PE_ACC_NO).toString());
				isOpenNoCardSelf = "1";
				noCardSelfBus = "01";
				phonenbr = Util.formatePhoneNumber(phonenbr);//格式化手机号码为135****1234样式
				phoneNumber = Util.fill(phonenbr, 20, Constants.RIGHT, SPACE);//填充空格
				ctx.setData(Constants.ISO8583_ADD_DATA_PRI, Constants.PE_AS+Constants.PE_SE+LENGTH+isOpenNoCardSelf+noCardSelfBus+phoneNumber);
			}
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);
		    ctx.setData(Constants.FIX8583_RESP, Constants.PE_OK);
		}else{
			validTranField(ctx);
			utilProcessor.valTranLimit(ctx);//验证交易限制
			ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
			ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
			HashMap procedureMap=utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
			// 帐户验证成功，修改交易流水的状态为成功
			if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))){
				System.err.println("tranTyp="+tranTyp);
				//如果是自助存款和自助转账就F61.6 NM用法持卡人姓名1处显示转入方部分姓名（隐去姓氏）
				if(Constants.PE_06.equals(tranTyp)||Constants.PE_07.equals(tranTyp)){
					String acctName=Util.formatAcctName((String)procedureMap.get(Constants.OUT_ACCTNAME));
					ctx.setData(Constants.ISO8583_IDENNUMBE, Constants.CUPS_IDENNUMBENM+acctName); //名字
				}
				//如果是自助转账\互联网转账和柜面转账,就该账户验证交易获取“转入卡所属地”信息用于告知转入卡发卡方
				if(Constants.PE_07.equals(tranTyp)||Constants.PE_08.equals(tranTyp)||Constants.PE_15.equals(tranTyp)){
					ctx.setData(Constants.ISO8583_ADD_DATA_PRI,Constants.PE_AS.concat(Constants.PE_IA)+"008"+InitData4Dzip.getAcqOrgCd());
				}
			}
		}
		updateJoural.execute(ctx);        //更新交易流水表交易状态
	}

	/**
	 * 验证该交易的域是否符合交易规范
	 * @param ctx
	 * @throws PeException
	 */
	public void validTranField(final Context ctx) throws PeException{
		String reacode0910=ctx.getString(Constants.IN_CHANNELTYPCD);
	    String reacode1314=ctx.getString(Constants.CHECK_ECI);
		//String reacode2323=ctx.getString(Constants.IN_TRANLAUNCHWAYCD);
		String tranTyp=ctx.getString(Constants.IN_ASSBUSTYP);
		//账户验证交易不支持密码验证，如果输密应该返40
		if(ctx.getData(Constants.CHECK_PIN).equals(Constants.PE_Y)){
			ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
		    ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
			throw new PeException("账户验证交易不支持密码验证");
		}
		//排除CDM,固话无卡存款两种
		if(!(Constants.PE_06.equals(tranTyp)||Constants.PE_07.equals(tranTyp)||Constants.PE_16.equals(tranTyp))){
			 if(Constants.PE_N.equals( ctx.getData(Constants.CHECK_PIN))&&Constants.PE_Y.equals( ctx.getData(Constants.CHECK_TRACK2))//如果不验证密码必须验证其它信息
					 &&Constants.PE_N.equals(ctx.getData(Constants.CHECK_AM))){
				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
				 throw new PeException("账户验证只有磁道验证信息，超出发卡方验证范围");
			 }
			 if(!tranTyp.equals(Constants.PE_11)&&Constants.PE_Y.equals( ctx.getData(Constants.CHECK_PIN))){
				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
				 throw new PeException("不是预付卡业务的账户验证不需要验证密码，超出发卡方验证范围");
			 }
			 int AM=Integer.valueOf(ctx.getString(Constants.PE_AM));
			 String field61=String.valueOf(ctx.getData(Constants.PE_FIELD_61));
			 if(AM!=-1&&field61.substring(AM+6, AM+7).equals(Constants.PE_ONE)){
				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
				 throw new PeException("账户验证不需要验证委托关系，超出发卡方验证范围");
			 }
			 String addDataPri=String.valueOf(ctx.getData(Constants.PE_FIELD_48));//第48域
			 if(!Constants.PE_09.equals(reacode1314)&&tranTyp.equals(Constants.PE_12)&&addDataPri.indexOf(Constants.PE_ON)!=-1){//F48包含AS+AO+ON，AO取值12
				 String tranStat=dzipProcessTemplate.getOpenNoCardSelf((String)ctx.getData(Constants.PE_ACC_NO));
				 if(tranStat==null||tranStat.equals(Constants.PE_ZERO)){
					 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_57);
					 ctx.setData(Constants.FIX8583_RESP,Constants.PE_57);
					 throw new PeException("自主识别无卡自助(发卡方验证动态码)没有开通,不允许此卡交易");
				 }
			 }
			 //账户验证必须上送61域
			 if(ctx.getData(Constants.PE_FIELD_61)==null){
				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_05);
				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_05);
				 throw new PeException("账户验证交易必须上送61域,暂不承兑");
			 }
		 }
	}

	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}
	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
}
