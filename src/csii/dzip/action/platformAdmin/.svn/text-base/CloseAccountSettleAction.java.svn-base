package csii.dzip.action.platformAdmin;

import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.IbsQueryAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;

import csii.base.constant.Constants;
import csii.base.constant.TranName;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;
import csii.dzip.core.Dict;
import csii.dzip.core.InitData4Dzip;

/**
 * IC卡销户结清
 * @author xiehai
 * @date 2014-4-14 下午03:40:10
 */
public class CloseAccountSettleAction extends IbsQueryAction {
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	private ActionUtilProcessor actionUtilProcessor;
	private IdFactory peJournalNOIdFactory;

	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
		log.info("CloseAccountSettleAction(销户结清)=======================>Start");
		String trmcd = String.valueOf(ctx.getData(Dict.TRM_CD));
		if(!dzipProcessTemplate.isExistTerminalCd(trmcd)){//判断终端号是否存在
			throw new PeException("终端号不存在!");
		}else {
			ctx.setData(Constants.IN_ORIGNTWKNODENBR, trmcd);
		}
//		log.info("交易类型：1.销户清算(可读) 2.销户清算(不可读或挂失) 3.补登(可读) 4.补登(不可读或挂失)");
		Map<String, Object> paramMap = Init.init4InsertOrUpdateIcclosingpay(ctx);
		try {
			dzipProcessTemplate.insertIcClosingPay(paramMap);
		} catch (Exception e) {
			log.error("插入或更新表T_ICCLOSINGPAY出错!");
			e.printStackTrace();
		}
		//做流水插入
		try {
			Map<String, Object> checkMap = new HashMap<String, Object>();
			checkMap.put(Constants.TRANCD,Constants.TRANCD_02006031);
			checkMap.put(Constants.PE_CHANN_ID, Constants.PE_ATMP);//channid 002
			checkMap.put(Constants.IN_CHANNELTYPCD, Constants.PE_06);
			if(dzipProcessTemplate.querychannTranSta(checkMap)){
				ctx.setData(Constants.PE_POST_DATE, dzipProcessTemplate.queryPostDate());//账务日期
				String journalNO = String.valueOf(peJournalNOIdFactory.generate());
				ctx.setData(Constants.PE_JOURNAL_NO, journalNO);//前置流水号
				ctx.setData(Constants.PE_REQ_CHANN, Constants.PE_COUNTER);//渠道
				ctx.setData(Constants.PE_REQ_ORG_CD, InitData4Dzip.getAcqOrgCd());
				ctx.setData(Constants.PE_FOW_ORG_CD, ctx.getData(Constants.PE_REQ_ORG_CD));
				ctx.setData(Constants.PE_RCVCODE, InitData4Dzip.getRcvgCd());
				Map<String, Object> journalMap = dzipProcessTemplate.getJournalMap4ClosingAccountRegisteration(ctx);
				journalMap.put(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB));
				journalMap.put(Constants.PE_OUT_TRANCD,Constants.TRANCD_02006031);
				journalMap.put(Constants.PE_MSG_TYP, Constants.MSG_TYPE_0200);
				dzipProcessTemplate.insertJournal(journalMap);
				log.info("insert journal successfully!" + journalNO);
			}else {
				throw new PeException("渠道不可用!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("IC卡柜面销户结清插入流水出错!" + e.getMessage());
			throw new PeException("IC卡柜面销户结清插入流水出错!");
		}
		ctx.setData(Dict.TRACENO, ctx.getData(Constants.PE_JOURNAL_NO));
		//是否需要做密码验证
		init4Validate(ctx);
//		super.execute(ctx);
		Map<String, Object> resultMap = actionUtilProcessor.getJournalInfo(String.valueOf(ctx.getData(Dict.TRACENO)));//通过平台流水号查询交易流水
		ctx.setData(Dict.TRACENO, resultMap.get(Constants.PE_SYS_TRACE_NUM));//替换系统跟踪号
		ctx.setData(Dict.RETCODE, Constants.PE_OK);
		ctx.setData(Dict.FIR_AMOUNT, "12.2");
		init4ClosingAccountRegistration(ctx);//初始化记账参数
		ctx.setData(Constants.PE_HOST_RESP_CD, ctx.getData(Dict.RETCODE));
		if(Constants.PE_OK.equals(ctx.getData(Dict.RETCODE))){//银联数据应答成功
			//记账
			ctx.setData(Constants.IN_RTXNSOURCECD, Constants.CLM); //交易来源
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);
			ctx.setData(Constants.TransactionId, Constants.TRANCD_02006031);//结清02006031
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);		//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
			actionUtilProcessor.deductTranAMT(ctx, Constants.ISO8583);
			try {
				//更新前置表T_ICCLOSINGPAY
				dzipProcessTemplate.updateIcClosingPay(ctx.getDataMap());//更新表T_ICCLOSINGPAY
				ctx.setData(Constants.PE_SETTLMTDATE, ctx.getData(Dict.TXN_DATE));//清算日期
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("IC卡柜面销户结清出错!" + e.getMessage());
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
			}
		}else {
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
		}
		updateJoural.execute(ctx.getDataMap());
		//返回柜面替换_TransactionId
		ctx.setData(Constants.TransactionId, TranName.PLATFORM_CLOSE_ACCOUNT_SETTLE);
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

	/**
	 * 若需要账户验证<BR>
	 * 可读卡做证件验证和密码验证<BR>
	 * 不可读卡做密码验证,证件验证在销户登记做
	 * @param ctx
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	private void init4Validate(Context ctx) throws PeException{
		boolean flg = false;
		if(Constants.PE_ONE.equals(ctx.getData(Dict.PINFLAG))){
			ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);
//			ctx.setData(Constants.IN_PIN, ctx.getData(Dict.PIN));
			ctx.setData(Constants.IN_PIN,
					Init.getPinFromOnli(String.valueOf(ctx.getData(Dict.IBS_TIME_STAMP)),
							String.valueOf(ctx.getData(Dict.PIN))));//解密柜面的密码
			ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Dict.CARD_NO));
			String pernbr = dzipProcessTemplate.queryTaxrptForPersNbr(ctx, Constants.ISO8583);
			if(null == pernbr || Constants.PE_NULL.equals(pernbr)){
				throw new PeException("没有找到客户号!");
			}else{
				ctx.setData(Constants.TAXRPTFORPERSNBR, pernbr);
			}
			flg = true;
		}
		if(flg){//如果需要做账户验证
			//登记做账户验证,结清做密码验证
			ctx.setData(Constants.PE_ACC_NO, ctx.getData(Dict.CARD_NO));
			HashMap<String, Object> result = actionUtilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
			if(!Constants.PE_OK.equals(String.valueOf(result.get(Constants.OUT_RESPONCD)))){
				log.error("IC卡销户结清密码验证出错!" + result.get(Constants.OUT_RESPONCD));
				throw new PeException("IC卡销户结清密码验证出错!" + result.get(Constants.OUT_RESPONCD));
			}
			//身份验证和密码验证在前置执行,不用发往银联数据
			ctx.setData(Dict.NAMEFLAG, Constants.PE_ZERO);
			ctx.setData(Dict.PINFLAG, Constants.PE_ZERO);
			ctx.setData(Dict.ID_TYPE, Constants.PE_NULL);
			ctx.setData(Dict.ID_CODE, Constants.PE_NULL);
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
