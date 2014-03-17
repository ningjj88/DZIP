package csii.dzip.action.platformAdmin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
 * 银联数据交易流水差错记账
 * @author xujin
 * @version 1.0.0
 * @since 2013-9-4
 */
public class DoICJournalChargeAction extends DzipBaseAction{
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException{
		logger.info("DoICJournalChargeAction=======================>Start!");
		Map paramMap = new HashMap();
		paramMap.put(Constants.POSTDATE, ctx.getString(Constants.POSTDATE));
		//获得IC交易流水
		List<Map<String, Object>>  resultList = utilProcessor.getICJournalByChargeFlag(paramMap);
		int totalCnt = resultList.size();//总共交易笔数
		if(totalCnt > 0){
			int failureCnt = 0;//交易失败笔数
			//ctx.setData(Constants.IN_RTXNSOURCECD,Constants.CCPT);//交易来源
			try{
				//获得核心系统日期
				ctx.setData(Constants.IN_EFFDATE, dzipProcessTemplate.queryPostDate());
			}catch(SQLException e){
				e.printStackTrace();
			}
			//循环记账
			for(int i = 0; i < resultList.size(); i++){
				Map<String, Object> map = resultList.get(i);
				try{
					this.setContextValue(ctx, map, resultList);
					utilProcessor.doICJournalCharge(ctx);//记账
				}catch(Exception ex){
					map.put(Constants.ERRMSG, ex.getMessage());
				    ++failureCnt;
				}
				map.put(Constants.PE_POST_DATE, ctx.getData(Constants.IN_EFFDATE));
				map.put(Constants.PE_HOST_SEQ_NO, ctx.getData(Constants.PE_HOST_SEQ_NO));
				map.put(Constants.PE_HOST_RESP_CD, ctx.getData(Constants.PE_HOST_RESP_CD));
				map.put(Constants.CHARGEFLAG,ctx.getData(Constants.PE_TRANS_STAT )); //交易状态
				try{
					//修改IC记账流水状态
					dzipProcessTemplate.updICJournal(map);
				}catch(SQLException e){
					throw new PeException(e);
				}
			}
			if(failureCnt > 0){
				throw new PeRuntimeException(Errors.EXP_PREAHTUCOMP_HANDLE_STATUS
						,new Object[] {totalCnt, totalCnt-failureCnt, failureCnt});
			}
		}
	}
	/**
	 * IC卡批量记账时初始化参数
	 * @param ctx
	 * @param map
	 * @param resultList
	 */
	public void setContextValue(Context ctx,Map<String, Object> map,
			List<Map<String, Object>>  resultList){
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
			   || InitData4Dzip.getAcqOrgCd4Ic().equals(ctx.getData(Constants.ISO8583_ACQCODE))){//如果是本行圈存机				ctx.setData(Constants.RTXNCATCD, Constants.PE_ZERO);//业务性质
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
		//如果是冲正交易查找，原始交易流水号
		if(Constants.MSG_TYPE_0420.equals(ctx.getString(Constants.PE_MSG_TYP))){
			ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_EC); //填充交易状态：EC
			//查找原始交易流水号
			for (int j = 0; j < resultList.size(); j++) {
				Map<String, Object> tempMap = resultList.get(j);
				String tmpOrigDateTime=Util.getDateString(Constants.PE_YYYY)+(String)map.get(Constants.PE_ORIGDATETIME);
				String origDatetime=(String)tempMap.get(Constants.PE_TRAN_DATE)+(String)tempMap.get(Constants.PE_TRAN_TIME);
				if(tempMap.get(Constants.PE_REQ_ORG_CD).equals(map.get(Constants.PE_REQ_ORG_CD))
						&& tempMap.get(Constants.PE_FOW_ORG_CD).equals(map.get(Constants.PE_FOW_ORG_CD))
						&& map.get(Constants.PE_ORIGTRACENUM).equals(tempMap.get(Constants.PE_SYS_TRACE_NUM))
						&& tmpOrigDateTime.equals(origDatetime)
						&& tempMap.get(Constants.PE_ACC_NO).equals(map.get(Constants.PE_ACC_NO))){
					ctx.setData(Constants.IN_ORIGTRACKNBR, tempMap.get(Constants.PE_JOURNAL_NO));
				}
			}
		}else{
			ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C); //填充交易状态：C
		}
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
