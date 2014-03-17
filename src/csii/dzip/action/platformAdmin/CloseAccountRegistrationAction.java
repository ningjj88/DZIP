package csii.dzip.action.platformAdmin;

import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.IbsQueryAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;

import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.core.Dict;
import csii.dzip.core.InitData4Dzip;

/**
 * IC卡销户登记
 * @author xiehai
 * @date 2014-2-17 下午02:35:48
 */
public class CloseAccountRegistrationAction extends IbsQueryAction{
	private DzipProcessTemplate dzipProcessTemplate;
	private ActionUtilProcessor actionUtilProcessor;
	private IdFactory peJournalNOIdFactory;

	public IdFactory getPeJournalNOIdFactory() {
		return peJournalNOIdFactory;
	}

	public void setPeJournalNOIdFactory(IdFactory peJournalNOIdFactory) {
		this.peJournalNOIdFactory = peJournalNOIdFactory;
	}

	public ActionUtilProcessor getActionUtilProcessor() {
		return actionUtilProcessor;
	}

	public void setActionUtilProcessor(ActionUtilProcessor actionUtilProcessor) {
		this.actionUtilProcessor = actionUtilProcessor;
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
	@Override
	public void execute(Context ctx) throws PeException {
		log.info("CloseAccountRegistration(销户登记)=======================>Start");
		ctx.setData(Dict.TRACENO, peJournalNOIdFactory.generate().toString());//系统跟踪号
		Map<String, Object> paramMap = Init.init4InsertOrUpdateIcclosingpay(ctx);
		try {
			dzipProcessTemplate.insertIcClosingPay(paramMap);
		} catch (Exception e) {
			log.error("插入或更新表T_ICCLOSINGPAY出错!");
			e.printStackTrace();
		}
		//是否需要做账户验证(证件验证和密码验证)
		init4Validate(ctx);
		super.execute(ctx);
		dzipProcessTemplate.updateIcClosingPay(ctx.getDataMap());//更新表T_ICCLOSINGPAY
	}

	/**
	 * 若需要账户验证
	 * @param ctx
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	private void init4Validate(Context ctx) throws PeException{
		boolean flg = false;
		if(Constants.PE_ONE.equals(ctx.getData(Dict.NAMEFLAG))){
			ctx.setData(Constants.CHECK_IDEN, Constants.PE_Y);
			//61域组装
			String idType = String.valueOf(ctx.getData(Dict.ID_TYPE));
			String idCode = String.valueOf(ctx.getData(Dict.ID_CODE));
			if(Constants.PE_NUM_MINUSONE != idCode.indexOf(Dict.IDENUMBER_X)){//截取身份证号码的后6位,若为X,向前一位
				idCode = idCode.substring(idCode.length()-7, idCode.length()-1);
			}else{
				idCode = idCode.substring(idCode.length()-6, idCode.length());
			}
			if(Constants.PE_ZERO.equals(idType) || Constants.PE_SEVEN.equals(idType)){//身份证||临时身份证
				idType = Constants.PE_01;
			}else{
				idType = Constants.PE_02;//其他证件类型
			}
			ctx.setData(Constants.PE_FIELD_61,
					Constants.ONLI_IC_IDENNUMBER.replace(Dict.PE_TP, idType).replace(Dict.PE_ID_CODE, idCode));//设置组装的61#
			flg = true;
		}
		if(Constants.PE_ONE.equals(ctx.getData(Dict.PINFLAG))){
			ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);
			ctx.setData(Constants.IN_PIN, ctx.getData(Dict.PIN));
			ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Dict.CARD_NO));
			String pernbr = dzipProcessTemplate.queryTaxrptForPersNbr(ctx, Constants.ISO8583);
			if(null == pernbr || Constants.PE_NULL.equals(pernbr)){
				throw new PeException("没有找到客户号!");
			}else{
				ctx.setData(Constants.TAXRPTFORPERSNBR, pernbr);
			}
			flg = true;
		}
		if(flg){//如果需要做账户验证
			ctx.setData(Constants.PE_ACC_NO, ctx.getData(Dict.CARD_NO));
			ctx.setData(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr());
			HashMap<String, Object> result = actionUtilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
			if(!Constants.PE_OK.equals(String.valueOf(result.get(Constants.OUT_RESPONCD)))){
				try {
					throw new PeException("IC卡销户登记身份/密码验证出错!" + result.get(Constants.OUT_RESPONCD));
				} catch (PeException e) {
					log.error("IC卡销户登记身份/密码验证出错!" + result.get(Constants.OUT_RESPONCD));
					e.printStackTrace();
				}
			}
			//身份验证和密码验证在前置执行,不用发往银联数据
			ctx.setData(Dict.NAMEFLAG, Constants.PE_ZERO);
			ctx.setData(Dict.PINFLAG, Constants.PE_ZERO);
			ctx.setData(Dict.ID_TYPE, Constants.PE_NULL);
			ctx.setData(Dict.ID_CODE, Constants.PE_NULL);
		}
	}
}
