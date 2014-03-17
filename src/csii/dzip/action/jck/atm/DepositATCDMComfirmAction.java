package csii.dzip.action.jck.atm;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description: 他行存款确认：CDM
 *@author :chenshq
 *@date：2010-09-07
 */
public class DepositATCDMComfirmAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK DepositATCDMComfirmAction ==================>Start");
		Map resultMap=new HashMap();
		Map depComfirmMap = new HashMap();
		String origDataElement = String.valueOf(ctx.getData(Constants.FIX8583_ORIG)); // 获得90#上笔交易的信息
		resultMap =utilProcessor.getOriSysTraNum(ctx);
		logger.info("JCK存款确认上笔交易信息(resultMap)==================>"+resultMap);
		String origAcquInstituIdCd = origDataElement.substring(20, 31);              // 原交易受理机构标识码
		String origiForwInstituIdCd = origDataElement.substring(31, 42);             // 原交易发送机构标识码
		depComfirmMap.put(Constants.IN_ORIGSYSTEMTRACENUM, String.valueOf(resultMap.get(Constants.JOURNAL_NO)));
		depComfirmMap.put(Constants.IN_ORIGSYSDATETIME, String.valueOf(resultMap.get(Constants.POSTDATE)));
		depComfirmMap.put(Constants.IN_ORIGACQUIDCD, origAcquInstituIdCd);
		depComfirmMap.put(Constants.IN_ORIGFORWARDIDCD,	origiForwInstituIdCd); 
		utilProcessor.depComfirm(depComfirmMap);   //调用存款确认存储过程
		ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
		if (Constants.PE_ZERO.equals(depComfirmMap.get(Constants.OUT_DEPOSITSUCCYN))){	// 是否已经记账标记 0:记账成功 1：记账失败 
			logger.debug("==================>JCK存款确认已经记账成功 ，不需重复记账");
			ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); //交易状态:成功
			ctx.setData(Constants.FIX8583_RESP, Constants.PE_OK);   //交易响应码
		}else{
		    logger.debug("==================>JCK存款确认上笔交易没有记账成功，需记账");
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM);
			ctx.setData(Constants.ATMYN, Constants.PE_ONE); 					//是否ATM  ATM ：1，POS：0
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
			ctx.setData(Constants.IN_DEPFLAG, Constants.TRS_FLAG_DEP); 	//出入账标识:0:出  1:入
			utilProcessor.valTranLimit(ctx);//验证金额限额和计算手续费
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){ 
				utilProcessor.deductTranAMT(ctx,Constants.FIX8583);					//执行数据库操作
				ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
				if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
					Map formatAmtMap = new HashMap();
					formatAmtMap.put(Constants.ACCTTYPCD,  ctx.getData(Constants.ACCTTYPCD));		//帐户类型
					formatAmtMap.put(Constants.BALAMT, ctx.getData(Constants.BALAMT));				//账面余额
					formatAmtMap.put(Constants.AVAILBALAMT,  ctx.getData(Constants.AVAILBALAMT));  	//可用余额
					String availBalAmt = Util.formatAmt(formatAmtMap); 				//返回余额格式化
					ctx.setData(Constants.FIX8583_RETU, availBalAmt);           					//余额
				}
			}else{
				ctx.setData(Constants.FIX8583_RESP,ctx.getData(Constants.PE_HOST_RESP_CD)); //获得响应码，并返回
				ctx.setData(Constants.FIX8583_EXPI,Constants.PE_EXPIREYEAR);   					//卡有效期	
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); 							//交易状态：失败
			}
		}
		if(ctx.getData(Constants.PE_ORIGSTAT)!=null&&resultMap.get(Constants.JOURNAL_NO)!=null&&  //原流水的状态不为成功时更新
				ctx.getData(Constants.PE_ORIGSTAT)!=Constants.PE_ZERO&&
				Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
			Map procedureMap = new HashMap();
		    procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_ZERO );   //交易状态: 已冲正
			procedureMap.put(Constants.PE_JOURNAL_NO,resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
			updateJoural.execute(ctx,procedureMap);// 修改交易流水状态
		}
		else
			updateJoural.execute(ctx);// 修改交易流水状态		
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
