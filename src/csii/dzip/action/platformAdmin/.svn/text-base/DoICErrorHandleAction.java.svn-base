package csii.dzip.action.platformAdmin;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.core.InitData4Dzip;

public class DoICErrorHandleAction extends DzipBaseAction {

	private ActionUtilProcessor utilProcessor;
	
	private DzipProcessTemplate dzipProcessTemplate;
	
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

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		logger.info("DoICErrorHandleAction=======================>Start");
		try{
			//获得核心系统日期
			ctx.setData(Constants.PE_POST_DATE, dzipProcessTemplate.queryPostDate());
		}catch(SQLException e){
			e.printStackTrace();
		}
		String transactionId = (String) ctx.getData(Constants.TransactionId);
		this.setContextValue(ctx);
		utilProcessor.deductTranAMT(ctx, Constants.ISO8583);
		ctx.setData(Constants.TransactionId, transactionId);
	}
	
	public void setContextValue(Context ctx){
		ctx.setData(Constants.IN_ORGNBR, InitData4Dzip.getYLOrgNbr());
		ctx.setData(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr());//钱箱号
		ctx.setData(Constants.IN_RTXNSOURCECD, Constants.CCPT);//交易来源
		ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//交易性质
		ctx.setData(Constants.TransactionId, ctx.getData(Constants.RTXNTYPCD));//交易码
		ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Constants.ACCTNO));//用户卡
		ctx.setData(Constants.PE_TRAN_AMT, ctx.getData(Constants.TRANAMT));
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);//资金类型：电子现金
		ctx.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
		ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C);
	}

}
