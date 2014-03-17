package csii.dzip.action.platformAdmin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;

import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.core.Errors;

public class DoOffLinePreAuthCompAction extends DzipBaseAction{
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	private UpdateJoural updateJoural;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		int totalCnt=0;
		int failureCnt=0;
		List<Map<String, Object>>  resultList =utilProcessor.getOfflinePreAuthComp();
		totalCnt=resultList.size();
		for (int i = 0; i < resultList.size(); i++) {
			Map<String, Object> map = resultList.get(i);
			logger.info("DoOffLinePreAuthCompAction arrSysSeqNO["+i+"]==========================>"+map.get(Constants.PE_JOURNAL_NO));
			String authrid =(String)map.get(Constants.PE_AUTH_CD);//获得预授权
			Map sqlMap = utilProcessor.getOptNbr(authrid);		//获得帐户冻结编号和状态
			if(sqlMap!=null){
				map.put(Constants.PE_OPTNBR,(String)sqlMap.get(Constants.PE_OPTNBR));//预授权冻结编号
				ctx.setData(Constants.PE_TRAN_AMT,map.get(Constants.PE_TRAN_AMT));
				Init.initPreAuthTranAmtAndHoldAmt(String.valueOf(sqlMap.get(Constants.PE_TRAN_AMT)),ctx);
				map.put(Constants.PE_PRE_AMT,ctx.getData(Constants.PE_PRE_AMT));//预授权冻结编号
				map.put(Constants.PE_TRAN_AMT,ctx.getData(Constants.PE_TRAN_AMT));
				map.put(Constants.PE_RLTSEQNO,authrid);           			//原交易流水号
				try {
					map.put(Constants.IN_EFFDATE, dzipProcessTemplate.queryPostDate());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Map paramMap =Init.initOfflinePreAuthComp(map);		
				try {
				    utilProcessor.deductOfflinePreAuthComp(paramMap);//核心记账
				} catch (Exception PeException) {
				    ++failureCnt;
				}
				Map procedureMap  = new HashMap();
				if(Constants.PE_OK.equals(paramMap.get(Constants.PE_HOST_RESP_CD))){  //客户帐扣除成功
				    procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_TWO );   //交易状态， 2：完成
				    procedureMap.put(Constants.PE_JOURNAL_NO, authrid);    //原交易流水号
			        procedureMap.put(Constants.PE_OPTNBR,Constants.PE_ZERO); 
			        updateJoural.execute(procedureMap);
			        procedureMap.clear();
			        
					procedureMap.put(Constants.PE_JOURNAL_NO, map.get(Constants.PE_JOURNAL_NO));    //交易流水号
					procedureMap.put(Constants.PE_RLTACCTNO,paramMap.get(Constants.PE_RLTACCTNO)); //账号
					procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_ZERO); //交易状态
					procedureMap.put(Constants.PE_HOST_SEQ_NO,paramMap.get(Constants.PE_HOST_SEQ_NO)); //主机交易号
					procedureMap.put(Constants.PE_RLTSEQNO,authrid);           	//原交易流水号
					procedureMap.put(Constants.PE_OPTNBR,Constants.PE_ZERO);
                    updateJoural.execute(procedureMap); // 修改流水交易状态
				}	
			}
		}
		if(failureCnt>0){
			if(Constants.PE_ZERO.equals(ctx.getString(Constants.ISPREAUTHCOMPFLAG))){
				throw new PeRuntimeException(Errors.EXP_PREAHTUCOMP_HANDLE_STATUS,new Object[] {totalCnt,totalCnt-failureCnt,failureCnt});
			} else if(Constants.PE_ONE.equals(ctx.getString(Constants.ISPREAUTHCOMPFLAG))){
				throw new PeRuntimeException(Errors.EXIST_EXP_PREAHTUCOMP_NOT_HANDLE,new Object[] {failureCnt});
			}
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
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
}
