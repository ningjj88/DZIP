package csii.dzip.action.atmother;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 * 现金充值圈存撤销冲正(本代本,本代他)
 * @author xiehai
 * @date 2013-11-5 下午04:55:38
 */
public class CashRecharge4CreditForLoadCancelReversalAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
//		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(ctx.getData(Constants.ISO8583_SOURID))){//本代本
		if("00010000".equals(ctx.getData(Constants.ISO8583_SOURID))){
			logger.info("CashRecharge4CreditForLoadCanceReversalAction(本行现金充值圈存撤销冲正)=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//设置为本代本
			Map procedureMap = new HashMap();
			if (Constants.PE_OK.equals(ctx.getString(Constants.PE_HOST_RESP_CD))) {// 如果交易成功
				Map orgDataMap = utilProcessor.getOrgData(ctx.getData(Constants.PE_JOURNAL_NO));
				logger.info("本行现金充值圈存撤销冲正交易信息(orgDataMap)==================>"+orgDataMap);
				orgDataMap.put(Constants.PE_CHANNELID, ctx.getData(Constants.PE_REQ_CHANN));
				orgDataMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
				if(orgDataMap != null){
					int count = Integer.valueOf(utilProcessor.verifyOrigDate(orgDataMap));//验证冲正交易是否已经发生
					if(count > 0){
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
						ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_22); //响应码
					}else{
						String responcd = Constants.PE_OK;
						if(!Init.isTransactionFromOnli(ctx)){//交易来自atm
							responcd = utilProcessor.getTranParamInfo(ctx, Constants.ISO8583); //获得交易参数信息
						}else{//交易来自柜面
							//设置柜面站点号
							ctx.setData(Constants.IN_ORIGNTWKNODENBR, ctx.getData(Constants.ISO8583_CARDACCID));
						}
						if(Constants.PE_OK.equals(responcd)){
							Map resultMap = utilProcessor.getJournalNO(orgDataMap);
							logger.info("本行现金充值圈存撤销冲正上笔交易信息(resultMap)==================>"+resultMap);
							ctx.setData(Constants.IN_RTXNSOURCECD, CLM); //交易来源
							ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   //资金类型
							ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); // 原交易主机流水号
							ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));// 原平台流水号
							ctx.setData(Constants.IN_ORIGPOSTDATE, resultMap.get(Constants.POSTDATE)); //原始交易日期
							ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_EC);							//交易状态,正常、冲正：'C'	抹帐:'EC'
							ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
							utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
							ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
							ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
							ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号

							if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
								/*更新现金充值交易的状态*/
								procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);//交易状态:0 成功
								procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.PE_RLTSEQNO));//交易状态
								updateJoural.execute(procedureMap);
								procedureMap.clear();
								procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));
								procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_NINE);   //交易状态: 已冲正
							}

							if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){//无法找到原始交易
								procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT );   //交易状态: 已冲正
								ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);          //响应码
								ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
							}
						}else{
							ctx.setData(Constants.ISO8583_RESCODE, responcd);
						}
					}
				}else{
					ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_25); //响应码
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);     	//交易状态：失败
				}
			}else{
				ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); //交易状态：失败
			}
			updateJoural.execute(ctx, procedureMap);					// 修改流水交易状态;
		}else{//本代他
			logger.info("CashRecharge4CreditForLoadCanceReversalAction(本代他现金充值圈存撤销冲正)=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
			Map procedureMap = new HashMap();
			if (Constants.PE_OK.equals(ctx.getString(Constants.PE_HOST_RESP_CD))) {// 如果交易成功
				Map orgDataMap = utilProcessor.getOrgData(ctx.getData(Constants.PE_JOURNAL_NO));
				logger.info("本代他现金充值圈存撤销冲正交易信息(orgDataMap)==================>"+orgDataMap);
				orgDataMap.put(Constants.PE_CHANNELID, ctx.getData(Constants.PE_REQ_CHANN));
				orgDataMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
				if(orgDataMap != null){
					int count = Integer.valueOf(utilProcessor.verifyOrigDate(orgDataMap));//验证冲正交易是否已经发生
					if(count > 0){
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
					}else{
						String responcd = utilProcessor.getTranParamInfo(ctx,Constants.ISO8583); //获得交易参数信息
						if(Constants.PE_OK.equals(responcd)){
							Map resultMap = utilProcessor.getJournalNO(orgDataMap);
							logger.info("本代他现金充值圈存撤销冲正上笔交易信息(resultMap)==================>"+resultMap);
							ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE)+ATM); //交易来源
							ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   //资金类型
							ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); // 原交易主机流水号
							ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));// 原平台流水号
							ctx.setData(Constants.IN_ORIGPOSTDATE, resultMap.get(Constants.POSTDATE)); //原始交易日期
							ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_EC);							//交易状态,正常、冲正：'C'	抹帐:'EC'
							ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
							utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
							ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
							ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号

							if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
								/*更新现金充值交易的状态*/
								procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);//交易状态:0 成功
								procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.PE_RLTSEQNO));//交易状态
								updateJoural.execute(procedureMap);
								procedureMap.clear();
								procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));
								procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_NINE);   //交易状态: 已冲正
							}

							if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){//无法找到原始交易
								procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT );   //交易状态: 已冲正
								ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);          //响应码
								ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
							}
						}
					}
				}else{
					ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_25); //响应码
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);     	//交易状态：失败
				}
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); //交易状态：失败
			}
			updateJoural.execute(ctx, procedureMap);					// 修改流水交易状态;
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
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
