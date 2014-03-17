
package csii.dzip.action.jck.pos;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:他行POS消费撤销
 *@author :chenshq
 *@date：2010-08-16
 */
public class PosConsumeCancelAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK PosConsumeCancelAction ==================>Start");
		Map procedureMap = new HashMap();
		if(ctx.getData(Constants.FIX8583_ORIG)!=null){
			String origDataElement = null;
			origDataElement = String.valueOf(ctx.getData(Constants.FIX8583_ORIG));  // 获得90#上笔交易的信息
			String origTranDate = origDataElement.substring(10,14); 			       // 原交易系统跟踪号
			if(origTranDate.equals(ctx.getData(Constants.FIX8583_LOCD))){      // 防止隔日撤销
				int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证冲正是否已经发生
				if(count>0){
					ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
					ctx.setData(Constants.FIX8583_RESP, Constants.PE_22); //响应码
					ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);   	//卡有效期	
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
					updateJoural.execute(ctx);					// 修改流水交易状态;
				}else{
					Map resultMap =utilProcessor.getOriSysTraNum(ctx);
					logger.info("JCK消费撤销上笔交易信息(resultMap)==================>"+resultMap);
					ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
					ctx.setData(Constants.ATMYN, Constants.PE_ZERO); 					//是否ATM  ATM ：1，POS：0
			    	ctx.setData(Constants.IN_MEDIAACCTSEQNO,Constants.rtxnseace_1002); 	//1002：POS消费撤销
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     //资金类型
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_THREE);				//冲正标志 3：消费撤销
					ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO));//原交易主机流水号
					ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));//原平台流水号
					ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
					utilProcessor.deductTranAMT(ctx,Constants.FIX8583);					//执行数据库操作
					ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
					if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
					procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_ONE );    //交易状态: 已撤销
					procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO)); //原交易流水号 
//					procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
					ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
					}
					if(Constants.PE_25.equals(ctx.getData(Constants.FIX8583_RESP))){
						procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_EIGHT );   //交易状态: 异常
						procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO)); //原交易流水号 
//						procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
						ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
						ctx.setData(Constants.FIX8583_RESP,Constants.PE_OK);           	 //响应码
						ctx.setData(Constants.FIX8583_EXPI,Constants.PE_EXPIREYEAR);    //卡有效期	
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
					}
					updateJoural.execute(ctx,procedureMap);								// 修改流水交易状态;
				}
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
				ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);    //响应码
				ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
				updateJoural.execute(ctx);						// 修改流水交易状态;
			}
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
			ctx.setData(Constants.FIX8583_RESP,Constants.PE_25);    //响应码
			ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_25);    //响应码
			ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
			updateJoural.execute(ctx);								  // 修改流水交易状态;
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
