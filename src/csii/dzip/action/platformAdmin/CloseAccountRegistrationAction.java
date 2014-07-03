package csii.dzip.action.platformAdmin;

import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.IbsQueryAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.core.Dict;

/**
 * IC卡销户登记
 * @author xiehai
 * @date 2014-2-17 下午02:35:48
 */
public class CloseAccountRegistrationAction extends IbsQueryAction{
	private DzipProcessTemplate dzipProcessTemplate;
	private ActionUtilProcessor actionUtilProcessor;

	@Override
	public void execute(Context ctx) throws PeException {
		log.info("CloseAccountRegistration(销户登记)=======================>Start");
		String trmcd = String.valueOf(ctx.getData(Dict.TRM_CD));
		if(!dzipProcessTemplate.isExistTerminalCd(trmcd)){//判断终端号是否存在
			throw new PeException("终端号不存在!");
		}else {
			ctx.setData(Constants.IN_ORIGNTWKNODENBR, trmcd);
		}
		Map<String, Object> paramMap = Init.init4InsertOrUpdateIcclosingpay(ctx);
		try {
			dzipProcessTemplate.insertIcClosingPay(paramMap);
		} catch (Exception e) {
			log.error("插入或更新表T_ICCLOSINGPAY出错!");
			e.printStackTrace();
		}
		log.info("error info:" + paramMap.get(Constants.OUT_ERROR_NBR) + " " + paramMap.get(Constants.OUT_ERROR_MSG));
		//是否需要做账户验证(证件验证)
		init4Validate(ctx);
//		super.execute(ctx);
		ctx.setData(Dict.RETCODE, Constants.PE_OK);
		if(Constants.PE_OK.equals(ctx.getData(Dict.RETCODE))){
			dzipProcessTemplate.updateIcClosingPay(ctx.getDataMap());//更新表T_ICCLOSINGPAY
		}
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
		if(flg){//如果需要做账户验证
			//登记做账户验证,结清做密码验证
			ctx.setData(Constants.PE_ACC_NO, ctx.getData(Dict.CARD_NO));
			HashMap<String, Object> result = actionUtilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
			if(!Constants.PE_OK.equals(String.valueOf(result.get(Constants.OUT_RESPONCD)))){
				log.error("IC卡销户登记身份出错!" + result.get(Constants.OUT_RESPONCD));
				throw new PeException("IC卡销户登记身份出错!" + result.get(Constants.OUT_RESPONCD));
			}else {
				log.info("identy is ok!");
			}
		}
		//身份验证和密码验证在前置执行,不用发往银联数据
		ctx.setData(Dict.NAMEFLAG, Constants.PE_ZERO);
		ctx.setData(Dict.PINFLAG, Constants.PE_ZERO);
		ctx.setData(Dict.ID_TYPE, Constants.PE_NULL);
		ctx.setData(Dict.ID_CODE, Constants.PE_NULL);
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
}
