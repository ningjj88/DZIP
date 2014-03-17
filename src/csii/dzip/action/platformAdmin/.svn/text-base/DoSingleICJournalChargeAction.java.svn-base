package csii.dzip.action.platformAdmin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.core.Errors;
import csii.dzip.core.InitData4Dzip;

/**
 * 银联数据交易流水单笔差错记账
 * @author xiehai
 * @version 1.0.0
 * @date 2013-9-11 上午11:09:11
 */
public class DoSingleICJournalChargeAction extends DzipBaseAction {
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("DoSingleICJournalChargeAction====================>Start!");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.IC_ACQ_INST_CODE, ctx.getData(Constants.IC_ACQ_INST_CODE));//受理机构标识
		map.put(Constants.IC_TRACE_NO, ctx.getData(Constants.IC_TRACE_NO));//系统跟踪号
		map.put(Constants.IC_TRANS_DATE, ctx.getData(Constants.IC_TRANS_DATE));//交易日期
		//获得指定的交易流水
		Map<String, Object> resultMap = utilProcessor.getSingleJournal(map);
		try{
			//获得核心系统日期
			ctx.setData(Constants.IN_EFFDATE, dzipProcessTemplate.queryPostDate());
		}catch(SQLException e){
			e.printStackTrace();
		}
		try {
			this.setContextValue(ctx, resultMap);
			utilProcessor.doICJournalCharge(ctx);//记账
		}catch (Exception e){
			throw new PeRuntimeException(Errors.EXP_PREAHTUCOMP_HANDLE_STATUS
					,new Object[] {1, 0, 1});
		}
		resultMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.IN_EFFDATE));
		resultMap.put(Constants.PE_HOST_SEQ_NO, ctx.getData(Constants.PE_HOST_SEQ_NO));
		resultMap.put(Constants.PE_HOST_RESP_CD, ctx.getData(Constants.PE_HOST_RESP_CD));
		resultMap.put(Constants.CHARGEFLAG,ctx.getData(Constants.PE_TRANS_STAT )); //交易状态
		try{
			//修改IC记账流水状态
			dzipProcessTemplate.updICJournal(resultMap);
		}catch(SQLException e){
			logger.info("IC卡单笔差错记账出错!");
			throw new PeException(e);
		}
	}
	public void setContextValue(Context ctx, Map<String, Object> map){
		ctx.setData(Constants.PE_HOST_SEQ_NO, null);
		ctx.setData(Constants.IN_PARENTACCTNBR, null);
		ctx.setData(Constants.IN_PARENTRTXNNBR, null);
		ctx.setData(Constants.PE_OUT_TRANCD, map.get(Constants.PE_OUT_TRANCD));//交易码
		ctx.setData(Constants.ISO8583_ACCTNO, map.get(Constants.PE_ACC_NO));//用户卡
		ctx.setData(Constants.PE_TRAN_AMT, map.get(Constants.PE_TRAN_AMT));
		ctx.setData(Constants.PE_POST_DATE, map.get(Constants.PE_POST_DATE));
		ctx.setData(Constants.PE_MSG_TYP, map.get(Constants.PE_MSG_TYP));
		ctx.setData(Constants.PE_JOURNAL_NO, map.get(Constants.PE_JOURNAL_NO));

		//如果是脱机消费,设置交易性质为本代本,交易来源为CCPT
		if(Constants.TRANCD_022000.equals(map.get(Constants.PE_OUT_TRANCD))){
			ctx.setData(Constants.RTXNCATCD, Constants.PE_ZERO);//业务性质
			ctx.setData(Constants.IN_RTXNSOURCECD, Constants.CCPT);//交易来源
		}else{//否则根据受理机构标识来分辨是本带本还是他带本
			if(InitData4Dzip.getAcqOrgCd().equals(ctx.getData(Constants.ISO8583_ACQCODE))
			   || InitData4Dzip.getAcqOrgCd4Ic().equals(ctx.getData(Constants.ISO8583_ACQCODE))){//如果是本行圈存机
				ctx.setData(Constants.RTXNCATCD, Constants.PE_ZERO);//业务性质
				ctx.setData(Constants.IN_RTXNSOURCECD, Constants.CCPT);//交易来源
			}else{//他带本
				ctx.setData(Constants.RTXNCATCD, Constants.PE_03);//业务性质
				ctx.setData(Constants.IN_RTXNSOURCECD, Constants.YCPT);//交易来源
			}
		}
		//记手续费
		//if(Double.parseDouble((String)map.get(Constants.TRAN_FEE))>0){
		if(Util.getAmount((String)map.get(Constants.TRAN_FEE)).signum() >0){
			ctx.setData(Constants.TRAN_FEE, map.get(Constants.TRAN_FEE));	//普通卡的手续费
			ctx.setData(Constants.DEDUCT_FEE, Constants.PE_Y);	//是否需要扣除交易费： Y 需要，N 不需要
		}
		ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C);
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
