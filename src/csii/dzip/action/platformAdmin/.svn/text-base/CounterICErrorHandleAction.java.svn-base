package csii.dzip.action.platformAdmin;

import java.sql.SQLException;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.core.Dict;

/**
 * 柜面IC卡差错处理
 * @author xiehai
 * @date 2014-6-17 下午04:09:09
 */
public class CounterICErrorHandleAction extends DzipBaseAction{
	private ActionUtilProcessor actionUtilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("CounterICErrorHandleAction(柜面IC卡差错处理)==========================>Start");
		String trmcd = String.valueOf(ctx.getData(Dict.TRM_CD));
		if(!dzipProcessTemplate.isExistTerminalCd(trmcd)){//判断终端号是否存在
			throw new PeException("终端号不存在!");
		}else {
			ctx.setData(Constants.IN_ORIGNTWKNODENBR, trmcd);
		}
		if(!Constants.PE_NULL.equals(ctx.getData(Dict.C_ACCTNO)) && Constants.PE_NULL.equals(ctx.getData(Dict.D_ACCTNO))){//贷方：客户帐 借方：内部帐
			ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Dict.C_ACCTNO));
		}else if(Constants.PE_NULL.equals(ctx.getData(Dict.C_ACCTNO)) && !Constants.PE_NULL.equals(ctx.getData(Dict.D_ACCTNO))){//贷方：内部帐 借方：客户帐
			ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Dict.D_ACCTNO));
		}
		try {
			ctx.setData(Constants.PE_POST_DATE, dzipProcessTemplate.queryPostDate());
			ctx.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
			ctx.setData(Constants.IN_RTXNSOURCECD, Constants.CCPT);//交易来源
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);// 资金类型
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);//冲正标志:0：正常   1：冲正 	2：抹帐
			actionUtilProcessor.doIcErrorHandle(ctx);
		} catch (SQLException e) {
			throw new PeException("IC卡柜面差错处理出错!" + e.getMessage());
		}
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
