package csii.dzip.action.platformAdmin;

import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.IbsQueryAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.base.constant.TranName;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;
import csii.dzip.core.Dict;
import csii.dzip.core.InitData4Dzip;

/**
 * IC卡销户结清冲正<BR>
 * 不使用销户结清 2014-05-14
 * @author xiehai
 * @date 2014-4-14 下午03:40:10
 */
@Deprecated
public class CloseAccountSettleReversalAction extends IbsQueryAction {
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	private ActionUtilProcessor actionUtilProcessor;
	private IdFactory peJournalNOIdFactory;

	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		log.info("CloseAccountSettleReversalAction(销户结清冲正)=======================>Start");
		String trmcd = String.valueOf(ctx.getData(Dict.TRM_CD));
		if(!dzipProcessTemplate.isExistTerminalCd(trmcd)){//判断终端号是否存在
			throw new PeException("终端号不存在!");
		}else {
			ctx.setData(Constants.IN_ORIGNTWKNODENBR, trmcd);
		}
		String transType = String.valueOf(ctx.getData(Dict.TRANS_TYPE));
		String origTraNumber = String.valueOf(ctx.getData(Constants.PE_ORIGTRACENUM));
		String origMsgType = Constants.MSG_TYPE_0420;
		String origDateTime = String.valueOf(ctx.getData(Constants.PE_TRANDATETIME));
		String origReqorgcd = Util.fill(InitData4Dzip.getForwOrgCd(), 11, Constants.LEFT, Constants.PE_SPACE);
		String origFwdorgcd = Util.fill(InitData4Dzip.getForwOrgCd(), 11, Constants.LEFT, Constants.PE_SPACE);
		ctx.setData(Constants.ISO8583_ORGDATA, origMsgType + origTraNumber + origDateTime + origReqorgcd + origFwdorgcd);
		//做流水插入
		try {
			Map<String, Object> checkMap = new HashMap<String, Object>();
			checkMap.put(Constants.TRANCD,
					(Constants.PE_ONE.equals(transType)
					|| Constants.PE_TWO.equals(transType))
					? Constants.TRANCD_04206031
					: Constants.TRANCD_04206032);
			checkMap.put(Constants.PE_CHANN_ID, Constants.PE_ATMP);//channid 002
			checkMap.put(Constants.IN_CHANNELTYPCD, Constants.PE_06);
			if(dzipProcessTemplate.querychannTranSta(checkMap)){
				ctx.setData(Constants.PE_POST_DATE, dzipProcessTemplate.queryPostDate());//账务日期
				String journalNO = String.valueOf(peJournalNOIdFactory.generate());
				ctx.setData(Constants.PE_REQ_CHANN, Constants.PE_COUNTER);//渠道
				ctx.setData(Constants.PE_JOURNAL_NO, journalNO);//前置流水号
				ctx.setData(Constants.PE_REQ_ORG_CD, InitData4Dzip.getAcqOrgCd());
				ctx.setData(Constants.PE_FOW_ORG_CD, ctx.getData(Constants.PE_REQ_ORG_CD));
				ctx.setData(Constants.PE_RCVCODE, InitData4Dzip.getRcvgCd());
				Map<String, Object> journalMap = dzipProcessTemplate.getJournalMap4ClosingAccountRegisteration(ctx);
				journalMap.put(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB));
				journalMap.put(Constants.PE_OUT_TRANCD,
								(Constants.PE_ONE.equals(transType)
								|| Constants.PE_TWO.equals(transType))
								? Constants.TRANCD_04206031
								: Constants.TRANCD_04206032);
				journalMap.put(Constants.PE_MSG_TYP, Constants.MSG_TYPE_0200);
				dzipProcessTemplate.insertJournal(journalMap);
				log.info("insert journal successfully! " + journalNO);
			}else {
				throw new PeException("渠道不可用!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("IC卡柜面销户结清冲正插入流水出错!" + e.getMessage());
		}
		init4ClosingAccountRegistration(ctx);//初始化记账参数
		//查询原交易
		actionUtilProcessor.getOriSysTraNum(ctx);
		ctx.setData(Dict.TRACENO, ctx.getData(Constants.PE_JOURNAL_NO));
//		super.execute(ctx);
		ctx.setData(Dict.RETCODE, Constants.PE_OK);
		ctx.setData(Dict.FIR_AMOUNT, "12.2");
		ctx.setData(Constants.PE_HOST_RESP_CD, ctx.getData(Dict.RETCODE));
		Map<String, Object> procedureMap = new HashMap<String, Object>();
		if(Constants.PE_OK.equals(ctx.getData(Dict.RETCODE))){//银联数据应答成功
			Map orgDataMap = actionUtilProcessor.getOrgData(ctx.getData(Constants.PE_JOURNAL_NO));
			log.info("柜面IC卡销户结清冲正交易信息(orgDataMap)==================>"+orgDataMap);
			orgDataMap.put(Constants.PE_CHANNELID, ctx.getData(Constants.PE_REQ_CHANN));
			orgDataMap.put(Constants.PE_PAN, ctx.getData(Dict.CARD_NO));
			if(orgDataMap != null){
				int count = Integer.valueOf(actionUtilProcessor.verifyOrigDate(orgDataMap));//验证冲正交易是否已经发生
				if(count > 0){
					throw new PeException("原交易已冲正!");
				}else{
					Map resultMap = actionUtilProcessor.getJournalNO(orgDataMap);
					log.info("柜面IC开销户结清冲正上笔交易信息(resultMap)==================>"+resultMap);
					//记账
					ctx.setData(Constants.PE_TRAN_AMT, resultMap.get(Constants.PE_TRAN_AMT));//填充交易金额
					ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); // 原交易主机流水号
					ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO));// 原平台流水号
					ctx.setData(Constants.IN_ORIGPOSTDATE, resultMap.get(Constants.POSTDATE)); //原始交易日期
					ctx.setData(Constants.IN_RTXNSOURCECD, Constants.CLM); //交易来源
					ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);
					ctx.setData(Constants.TransactionId,
							(Constants.PE_ONE.equals(transType)
							|| Constants.PE_TWO.equals(transType))
							? Constants.TRANCD_04206031
							: Constants.TRANCD_04206032);
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_EC);		//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
					actionUtilProcessor.deductTranAMT(ctx, Constants.ISO8583);
					procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
					ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
				}
			}else {
				throw new PeException("未找到原交易!");
			}
			try {
				//若是不可读卡,更新前置表T_ICCLOSINGPAY
				if(Constants.PE_TWO.equals(transType) || Constants.PE_FOUR.equals(transType)){
					ctx.setData(Dict.ACTCODE, Constants.PE_ZERO);//更新ActCode为结清登记状态
					dzipProcessTemplate.updateIcClosingPay(ctx.getDataMap());//更新表T_ICCLOSINGPAY
				}
//				//若是做补登交易,更新核心表CARDICEWALLETCTL
//				if(Constants.PE_THREE.equals(transType) || Constants.PE_FOUR.equals(transType)){
//					//更新登记余额
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put(Constants.PE_TRAN_AMT, ctx.getData(Constants.PE_TRAN_AMT));
//					map.put(Dict.CARD_NO, ctx.getData(Dict.CARD_NO));
//					dzipProcessTemplate.updateICEWALLETCTL(map);
//				}
				ctx.setData(Constants.PE_SETTLMTDATE, ctx.getData(Dict.TXN_DATE));//清算日期
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_NINE);//更新原交易为已冲正
			} catch (Exception e) {
				e.printStackTrace();
				log.error("IC卡柜面销户结清冲正出错!" + e.getMessage());
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
			}
			//返回柜面替换_TransactionId
			ctx.setData(Constants.TransactionId, TranName.PLATFORM_CLOSE_ACCOUNT_SETTLE_REVERSAL);
		}else {
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
		}
		updateJoural.execute(ctx, procedureMap);
	}

	private void init4ClosingAccountRegistration(Context ctx) {
		ctx.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
		ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Dict.CARD_NO));//卡号
		//清算交易金额
//		ctx.setData(Constants.PE_TRAN_AMT, "1.2");
		String tranType = String.valueOf(ctx.getData(Dict.TRANS_TYPE));
		if(Constants.PE_ONE.equals(tranType) || Constants.PE_THREE.equals(tranType)){
			ctx.setData(Constants.PE_TRAN_AMT, ctx.getData(Dict.AVAIL_AMT));
		}else {
			//暂时设置金额为银联数据返回的金额
			String firAmount = String.valueOf(ctx.getData(Dict.FIR_AMOUNT));//主币种返回金额
			String secAmount = String.valueOf(ctx.getData(Dict.SEC_AMOUNT));//次币种返回金额
			if((Constants.PE_NULL.equals(firAmount) || null == firAmount)
					&& (Constants.PE_NULL.equals(secAmount) || null == secAmount)){
				ctx.setData(Constants.PE_TRAN_AMT, Constants.PE_NUM_ZERO);
			}else {
				ctx.setData(Constants.PE_TRAN_AMT, Constants.PE_NULL.equals(firAmount) ? secAmount : firAmount);
			}
		}
	}

	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}

	public IdFactory getPeJournalNOIdFactory() {
		return peJournalNOIdFactory;
	}

	public void setPeJournalNOIdFactory(IdFactory peJournalNOIdFactory) {
		this.peJournalNOIdFactory = peJournalNOIdFactory;
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
	public ActionUtilProcessor getActionUtilProcessor() {
		return actionUtilProcessor;
	}
	public void setActionUtilProcessor(ActionUtilProcessor actionUtilProcessor) {
		this.actionUtilProcessor = actionUtilProcessor;
	}
}
