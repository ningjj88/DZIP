/**
 * ATM冲正
 */
package csii.dzip.action.atmother;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;

/**
 * @author xujin
 *@Description:冲正取款
 * @version 1.0.1
 * @since 2012-10-22
 */
public class ReversalForOtherBankAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("ReversalForOtherBank===================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		Map procedureMap = new HashMap();
		if (Constants.PE_OK.equals(ctx.getString(Constants.PE_HOST_RESP_CD))) {// 如果交易成功
			Map orgDataMap = utilProcessor.getOrgData(ctx.getData(Constants.PE_JOURNAL_NO));
			logger.info("本带他取款冲正交易信息(orgDataMap)==================>"+orgDataMap);
			orgDataMap.put(Constants.PE_CHANNELID,ctx.getData(Constants.PE_REQ_CHANN));
			orgDataMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
			if(orgDataMap!=null){
				int count=Integer.valueOf(utilProcessor.verifyOrigDate(orgDataMap));//验证冲正交易是否已经发生
				if(count>0){
					ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
					if(Constants.PRI_SOURCE_YLQZ_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
						ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					if(Constants.PRI_SOURCE_JCK_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
						ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码	
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
				}else{
					String responcd =utilProcessor.getTranParamInfo(ctx,Constants.ISO8583); //获得交易参数信息
					if(Constants.PE_OK.equals(responcd)){
						Map resultMap =utilProcessor.getJournalNO(orgDataMap);
						logger.info("本带他取款冲正上笔交易信息(resultMap)==================>"+resultMap);
						ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM); //交易来源
						ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   //资金类型
						ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); // 原交易主机流水号
						ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));// 原平台流水号
						ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
						ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_EC);							//交易状态,正常、冲正：'C'	抹帐:'EC'
						ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
						utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
						ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
						procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
						ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
						if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
							procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE );   //交易状态: 已冲正
						}
						if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){//无法找到原始交易
							procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_EIGHT );   //交易状态: 已冲正
							ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_OK);          //响应码
							ctx.setData(Constants.ISO8583_EXPDAT,Constants.PE_EXPIREYEAR);   //卡有效期	
							ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
						}
					}
				}
			}else{
				ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_25); //响应码
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);     	//交易状态：失败
				if(Constants.PRI_SOURCE_YLQZ_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
					ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				if(Constants.PRI_SOURCE_JCK_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
					ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			}
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); //交易状态：失败
		}
		updateJoural.execute(ctx,procedureMap);					// 修改流水交易状态;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
}
