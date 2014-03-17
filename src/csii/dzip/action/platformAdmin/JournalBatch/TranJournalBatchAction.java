/**
 * 
 */
package csii.dzip.action.platformAdmin.JournalBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import csii.dzip.action.util.ConvertUtilProcessor;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;
import csii.dzip.core.Errors;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author chenshaoqi
 * @date 2010.08.23
 * @desc 批量记账
 */
public class TranJournalBatchAction extends DzipBaseAction{
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Context ctx) throws PeException {
		List listRe = new ArrayList();
		final Map paramMap = new HashMap();
		logger.info("TranJournalBatchAction==========================>Start");
		String SysSeqNO=null;
	    int inputflag=0;
		Map map = new HashMap();
		listRe=(ArrayList)ctx.getData(Constants.RECORDLIST);
		for(Iterator iterator = listRe.iterator();iterator.hasNext();){
			map = (Map) iterator.next();
			if(null!=map.get(Constants.PE_SYSSEQNO))
			{
			   SysSeqNO=String.valueOf(map.get(Constants.PE_SYSSEQNO));
			   SysSeqNO=SysSeqNO.replace("[", "").replace("]", "").replace(" ", "");//去掉字符：[,]," "
			   String arrSysSeqNO[]=SysSeqNO.split(",");
			   Arrays.sort(arrSysSeqNO); //从小到大排序
			   Map procedureMap = new HashMap();  
			   Map mapBat= new HashMap(); 
			   Map origMap=new HashMap();
			   for(int i=0;i<arrSysSeqNO.length;i++){				  
				   logger.info("arrSysSeqNO["+i+"]==========================>"+arrSysSeqNO[i]);
				   int count=Integer.valueOf(utilProcessor.verifyJournalBatch(arrSysSeqNO[i]));//验证是否已经执行
				   if(count>0)
					   continue;
				   paramMap.put(Constants.PE_EXECYN,Constants.PE_Y);
				   paramMap.put(Constants.PE_JOURNAL_NO,arrSysSeqNO[i]);
				 //  updateJoural.executeBatch(paramMap,null);
				   mapBat =utilProcessor.getJournalInfo(arrSysSeqNO[i]);
				   if(!Constants.EXEC_N.equals(procedureMap.get(Constants.PE_EXECYN))){//标记是否需要执行，Y：执行，N：不执行
						try {			
							   utilProcessor.deductBatchTranAMT(mapBat,procedureMap);//核心记账
						} catch (Exception PeException) {
						}
					   ++inputflag;
				   }
				   if(Constants.PE_ZERO.equals(procedureMap.get(Constants.OUT_ERRORNBR))){//核心记账后，修改临时批量流水新状态和新主机号
					   paramMap.put(Constants.PE_HSTSEQNONEW,procedureMap.get(Constants.PE_HSTSEQNONEW));
					   paramMap.put(Constants.PE_TRANSTATNEW,Constants.PE_ZERO); 
					   paramMap.put(Constants.PE_OPTNBR,procedureMap.get(Constants.PE_OPTNBR));
					   paramMap.put(Constants.PE_POST_DATE,procedureMap.get(Constants.PE_POST_DATE));
					   if(procedureMap.get(Constants.PE_JOURNAL_NO)!=null){//修改原流水的新状态
						   origMap.put(Constants.PE_JOURNAL_NO, procedureMap.get(Constants.PE_JOURNAL_NO));
						   origMap.put(Constants.PE_OPTNBR, procedureMap.get(Constants.PE_NEWOPTNBR)==null?Constants.PE_ZERO:procedureMap.get(Constants.PE_NEWOPTNBR));
					   }
				   }
				   else{
					   paramMap.put(Constants.PE_TRANSTATNEW,Constants.PE_EIGHT); 
					   if(Constants.EXEC_Y.equals(procedureMap.get(Constants.PE_EXECYN))){//不需要记账的，而已经执行的状态为成功
						   paramMap.put(Constants.PE_TRANSTATNEW,Constants.PE_ZERO); 
						   paramMap.put(Constants.PE_HSTSEQNONEW,mapBat.get(Constants.PE_HOST_SEQ_NO));
					   }
				   }

				   updateJoural.executeBatch(paramMap,origMap);
				   paramMap.clear();
				   origMap.clear();
				   mapBat.clear();
				   procedureMap.clear();
			   }
			}else{
				 logger.info("==========================>no_exec_journal_batch!"); //临时表没有数据
			     throw new PeException(Errors.NO_EXEC_JOURNAL_BATCH);
			}
		}
		if(inputflag==0){
			 logger.debug("==========================>Re_exec_journal_batch!"); //已经记账，不需要重复
		     throw new PeException(Errors.RE_EXEC_JOURNAL_BATCH);
		}
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
