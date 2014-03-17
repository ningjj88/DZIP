
package csii.dzip.action.cups.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:他行余额查询
 *@author :xujin
 *@date：2010-08-16
 */
public class QueryAction extends DzipBaseAction {

	private UpdateJoural updateJoural;
	private JdbcAccessAwareProcessor corebankAccess;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS QueryAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_09); 	//业务类型:09_余额查询
		validTranField(ctx);
		ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
		ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
		HashMap procedureMap=utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
		// 余额查询成功，组装余额格式
		if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))) {
			Map formatAmtMap = new HashMap();
			formatAmtMap.put(Constants.ACCTTYPCD, procedureMap.get(Constants.OUT_ACCTTYPCD)); //帐户类型
			formatAmtMap.put(Constants.BALAMT, procedureMap.get(Constants.OUT_BALAMT)); //账面余额
			formatAmtMap.put(Constants.AVAILBALAMT, procedureMap.get(Constants.OUT_AVAILBALAMT)); //可用余额
			String availBalAmt = Util.formatAmt(formatAmtMap);  //返回余额格式化
			ctx.setData(Constants.ISO8583_BALAMT, availBalAmt);           //余额
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
		String reacode2323=ctx.getString(Constants.IN_TRANLAUNCHWAYCD);
		String reacode1314=ctx.getString(Constants.CHECK_ECI);
		/**
		 * 银联在线无卡支付业务逐步
		 * 过渡到《银联卡无卡自助消费业务规则》、《银联卡订购业务规则》、《银联卡代收业务规则》对应的业务处理
		 */
		//如果是自主识别无卡自助,就验证是否开通无卡自助
		if((Constants.PE_09.equals(reacode1314))&&Constants.PE_TWO.equals(reacode2323)
				&&Constants.PE_N.equals( ctx.getData(Constants.CHECK_TRACK2))){
			 String tranStat=dzipProcessTemplate.getOpenNoCardSelf((String)ctx.getData(Constants.PE_ACC_NO));
			 if(tranStat==null||tranStat.equals(Constants.PE_ZERO)){
				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_57);
				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_57);
				 throw new PeException("没有开通无卡自助,不允许此卡交易");
			 }
		}
		//判断无卡支付业务无AM无密码的查询交易
		if(//Constants.PE_ZERO.equals(reacode2323)//F60.3.5=0:未知交易发起方式;无卡支付默认发起方式为0&&	//不允许只上送卡号的余额查询交易
				Constants.PE_03.equals(reacode0910)||Constants.PE_07.equals(reacode0910)){
			if(Constants.PE_N.equals(ctx.getData(Constants.CHECK_AM))
					 &&Constants.PE_N.equals( ctx.getData(Constants.CHECK_PIN))){//第61域
				 ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
				 ctx.setData(Constants.FIX8583_RESP,Constants.PE_CHANN_INVALIDATE);
				 throw new PeException("银联暂不支持此无AM无密码的查询交易");
			 }
		}
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public JdbcAccessAwareProcessor getCorebankAccess() {
		return corebankAccess;
	}

	public void setCorebankAccess(JdbcAccessAwareProcessor corebankAccess) {
		this.corebankAccess = corebankAccess;
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
