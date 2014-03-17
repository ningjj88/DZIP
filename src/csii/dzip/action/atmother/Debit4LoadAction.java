package csii.dzip.action.atmother;

import java.math.BigDecimal;
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
 * 电子现金圈提(本行)
 * @author xiehai
 * @date 2013-12-16 下午04:43:33
 */
public class Debit4LoadAction extends DzipBaseAction {
	private ActionUtilProcessor utilProcessor;
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(ctx.getData(Constants.ISO8583_SOURID))){//如果是银联数据发来的报文
			logger.info("Debit4LoadAction(本代本圈提)=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//设置为本代本交易
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){//如果应答成功
				ctx.setData(Constants.PE_ACC_NO, ctx.getData(Constants.ISO8583_ACCTNO));
				ctx.setData(Constants.IN_RTXNSOURCECD, CLM);//交易来源
				String res = dzipProcessTemplate.getSysTranInfo(ctx, Constants.ISO8583);
				if(Constants.PE_OK.equals(res)){
					utilProcessor.selectAcctInfo(ctx, Constants.ISO8583); //账户验证
					//收到圈提不记账,收到圈提确认才记账,只记平台流水
					BigDecimal currentAmt = Util.getAmount(String.valueOf(ctx.getData(Constants.ISO8583_TRANAMT))).movePointLeft(2);//当前圈提金额
					Map paramMap = new HashMap();
					paramMap.put(Constants.IN_MEDIUMID, ctx.getData(Constants.ISO8583_ACCTNO));
					paramMap = utilProcessor.getIcCardBal(paramMap);
					BigDecimal currentBal = Util.getAmount(String.valueOf(paramMap.get(Constants.OUT_BALAMT)));//当前电子钱包余额
					if(currentAmt.compareTo(currentBal) > Constants.PE_NUM_ZERO){//交易金额大于实际电子钱包余额
						ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_51);
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易异常
						throw new PeException("圈提金额大于电子钱包余额!");
					}
					ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD));
					ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
				}
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易异常
			}
			updateJoural.execute(ctx);//更新交易状态
		}
		//圈提没有本代他交易
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
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
