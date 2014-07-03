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
 * 非指定账户转入圈存冲正(本代本)
 * @author xiehai
 * @date 2013-12-23 上午10:33:52
 */
public class TransferIn4Credit4LoadReversalAction extends DzipBaseAction {
	private ActionUtilProcessor utilProcessor;
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(
				ctx.getData(Constants.ISO8583_SOURID))){//如果是银联数据发来的报文,则是本代本交易
			logger.info("TransferIn4Credit4LoadReversalAction(本代本转入圈存冲正)=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01 现金及转账
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//交易设置为本代本
			Map transferInRe = new HashMap();
			Map transferRe = new HashMap();
			Map transferIn = new HashMap();
			Map transfer = new HashMap();
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){
			//	ctx.setData(Constants.ISO8583_ACCTNO, tempMap.get(Constants.PE_ACCTID1));//设置记账账户为转出卡
				transferInRe = utilProcessor.getJournalInfo(String.valueOf(ctx.getData(Constants.PE_JOURNAL_NO)));//转入圈存冲正交易信息
				logger.info("本行转入圈存冲正交易信息(transferInRe)==================>"+transferInRe);
				transferIn = utilProcessor.getJournalInfo(String.valueOf(transferInRe.get(Constants.PE_RLTSEQNO)));//转入圈存交易信息
				logger.info("本行转入圈存交易信息(transferIn)==================>"+transferIn);
				transfer = utilProcessor.getJournalInfo(String.valueOf(transferIn.get(Constants.PE_RLTSEQNO)));//转账圈存交易信息
				logger.info("本行转账圈存交易信息(transfer)==================>"+transfer);
				Map map = new HashMap();
				map.put(Constants.PE_TRM_CD, transferInRe.get(Constants.PE_TRM_CD));
				map.put(Constants.PE_TRANDATETIME, transferInRe.get(Constants.PE_TRANDATETIME));
				map.put(Constants.PE_OUT_TRANCD, Constants.TRANCD_042062);
				transferRe = utilProcessor.getJournalInfoByTransferInReversal(map);//转账圈存冲正交易信息
				logger.info("本行转账圈存冲正交易信息(transferRe)==================>"+transferRe);
				Map orgDataMap = utilProcessor.getOrgData(transferRe.get(Constants.PE_JOURNAL_NO));
				orgDataMap.put(Constants.PE_CHANNELID, transferRe.get(Constants.PE_REQ_CHANN));
				orgDataMap.put(Constants.PE_PAN, transferRe.get(Constants.PE_ACC_NO));
				int count = Integer.valueOf(utilProcessor.verifyOrigDate(orgDataMap));//验证冲正交易是否已经发生
				if(count > 0){
					ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_22);          //响应码:已冲正
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
				}else{
					String responcd = Constants.PE_OK;
					if(!Init.isTransactionFromOnli(ctx)){//交易来自atm
						responcd = utilProcessor.getTranParamInfo(ctx, Constants.ISO8583); //获得交易参数信息
					}else{//交易来自柜面
						//设置柜面站点号
						ctx.setData(Constants.IN_ORIGNTWKNODENBR, ctx.getData(Constants.ISO8583_CARDACCID));
					}
					if(Constants.PE_OK.equals(responcd)){
						logger.info("本行转账圈存冲正上笔交易信息(transfer)==================>"+transfer);
						ctx.setData(Constants.ISO8583_ACCTNO, transfer.get(Constants.PE_ACC_NO));
						ctx.setData(Constants.PE_ACCTID2, transfer.get(Constants.PE_ACCTID2));
						//为了方便对账 设置渠道为CLM
						ctx.setData(Constants.IN_RTXNSOURCECD, CLM); //交易来源
						ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
						ctx.setData(Constants.IN_RTXNNBR, transferIn.get(Constants.PE_HOST_SEQ_NO)); // 原交易主机流水号
						ctx.setData(Constants.IN_ORIGTRACKNBR, transferIn.get(Constants.PE_JOURNAL_NO));// 原平台流水号
						ctx.setData(Constants.IN_ORIGPOSTDATE, transferIn.get(Constants.PE_POST_DATE)); //原始交易日期
						ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_EC);		 //交易状态,正常、冲正：'C'	抹帐:'EC'
						ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
						ctx.setData(Constants.TransactionId, Constants.TRANCD_043062);
						utilProcessor.deductTranAMT(ctx, Constants.ISO8583);					//执行数据库操作
						ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
						if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){//如果冲正成功,将转账圈存和转入圈存置为已冲正
							transferRe.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);//更新转入圈存冲正为：成功
							transfer.put(Constants.PE_TRANS_STAT, Constants.PE_NINE);
							updateJoural.execute(transfer);//更新转账圈存状态为:已冲正

							transferIn.put(Constants.PE_TRANS_STAT, Constants.PE_NINE);
							updateJoural.execute(transferIn);//更新转入圈存状态为:已冲正
						}else{
							transferRe.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
						}
					}else {
						ctx.setData(Constants.ISO8583_RESCODE, responcd);
					}
				}
			}else {
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
				transferRe.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
				ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			}
			updateJoural.execute(ctx, transferRe);
			ctx.setData(Constants.ISO8583_ACCTNO, transferRe.get(Constants.PE_ACC_NO));//转账圈存冲正主帐号是转出主帐号
			ctx.setData(Constants.ISO8583_PRO_CODE, Constants.PROCODE_620000);//报文返回ATMP更改为转账圈存冲正交易码
		}
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}
	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
