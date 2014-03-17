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
 *@ClassName:&{type_name
 *@Description:他行存款确认交易
 *@author :徐锦
 *@date：2012-10-23
 */
public class DepConfirmForOtherBankAction extends DzipBaseAction {

	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.debug("DepConfirmForOtherBankAction========================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		/* 如果响应码是 00，修改交易流水的状态为成功 ；0：表示成功； */
		Map resultMap=new HashMap();
		if (ctx.getData(Constants.PE_HOST_RESP_CD).equals(Constants.PE_OK)) {
			Map map=new HashMap();
			Map depComfirmMap = new HashMap();
			logger.info("获得他行存款确认交易信息(map)==================>"+map);
			map=utilProcessor.getJournalInfo(ctx.getString(Constants.PE_JOURNAL_NO));//通过平台流水号查询交易流水。
			String origAcquInstituIdCd = (String)map.get(Constants.PE_ORIGREQORGCD); // 原交易受理机构标识码
			String origiForwInstituIdCd = (String)map.get(Constants.PE_ORIGFWDORGCD); // 原交易发送机构标识码
			logger.info("获得他行存款确认上笔交易信息(resultMap)==================>"+resultMap);
			Map sqlMap=new HashMap();
			sqlMap.put(Constants.PE_CHANNELID, String.valueOf(ctx.getData(Constants.PE_REQ_CHANN)));
			sqlMap.put(Constants.PE_ORIGSYSTRANUMBER, String.valueOf(map.get(Constants.PE_ORIGTRACENUM)));
			sqlMap.put(Constants.PE_ORIGTRANDATETIME, String.valueOf(map.get(Constants.PE_ORIGDATETIME)));
			sqlMap.put(Constants.PE_ORIGACQUINSTITUIDCD, origAcquInstituIdCd);
			sqlMap.put(Constants.PE_ORIGIFORWINSTITUIDCD, origiForwInstituIdCd);
			sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
			resultMap = utilProcessor.getJournalNO(sqlMap);
			depComfirmMap.put(Constants.IN_ORIGSYSTEMTRACENUM, String.valueOf(resultMap.get(Constants.JOURNAL_NO)));
			depComfirmMap.put(Constants.IN_ORIGSYSDATETIME, String.valueOf(resultMap.get(Constants.POSTDATE)));
			depComfirmMap.put(Constants.IN_ORIGACQUIDCD, origAcquInstituIdCd);
			depComfirmMap.put(Constants.IN_ORIGFORWARDIDCD,	origiForwInstituIdCd); 
			utilProcessor.depComfirm(depComfirmMap);   //调用存款确认存储过程
			ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
			if (Constants.PE_ZERO.equals((String)depComfirmMap.get(Constants.OUT_DEPOSITSUCCYN))){	// 是否已经记账标记 0:记账成功 1：记账失败 
				logger.info("==================>CUPS他行存款已经记账成功 ，不需重复记账");
				ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); //交易状态:成功
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);   //交易响应码
			}else{
			    logger.info("==================>CUPS他行存款记账没有成功 ，需要记账");
			    String responcd =utilProcessor.getTranParamInfo(ctx,Constants.ISO8583); //获得交易参数信息
				if(Constants.PE_OK.equals(responcd)){
					ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM);
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);		//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
					try {	
						if(Constants.PRI_SOURCE_YLQZ_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
							utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
						if(Constants.PRI_SOURCE_JCK_VALUE.equals(ctx.getData(Constants.PRI_SOURCE)))
							utilProcessor.deductTranAMT(ctx, Constants.FIX8583);				//执行数据库操作
					}catch (PeException e) {
						//对于发卡方存款交易成功，不管受理方交易成功还是不成功都要返回给ATMP端成功，这样避免发卡方资金流失
						ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_OK ); //响应码
					}
				}
			}
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);      // 异常状态
		}
		if(ctx.getData(Constants.PE_ORIGSTAT)!=null&&resultMap.get(Constants.JOURNAL_NO)!=null&&  //原流水的状态不为成功时更新
				!Constants.PE_ZERO.equals(ctx.getString(Constants.PE_ORIGSTAT))&&
				Constants.PE_OK.equals(ctx.getString(Constants.ISO8583_RESCODE))){
			Map procedureMap = new HashMap();
		    procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_ZERO );   //交易状态: 成功
			procedureMap.put(Constants.PE_JOURNAL_NO,resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
			updateJoural.execute(ctx,procedureMap);// 修改交易流水状态
		}
		else
			updateJoural.execute(ctx);// 修改交易流水状态	
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
