
package csii.dzip.action.cups.common;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description: 他行转入记账确认：ATM，CDM
 *@author :xujin
 *@date：2010-09-07
 */
public class TransferInComfirmAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS TransferInComfirmAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		Map depComfirmMap = new HashMap();
		String origDataElement = String.valueOf(ctx.getData(Constants.ISO8583_ORGDATA)); // 获得90#上笔交易的信息
		Map resultMap = utilProcessor.getOriSysTraNum(ctx);
		logger.info("CUPS转入确认上笔交易信息(resultMap)==================>"+resultMap);
		String origAcquInstituIdCd = origDataElement.substring(20, 31);              // 原交易受理机构标识码
		String origiForwInstituIdCd = origDataElement.substring(31, 42);             // 原交易发送机构标识码
		depComfirmMap.put(Constants.IN_ORIGSYSTEMTRACENUM,String.valueOf(resultMap.get(Constants.JOURNAL_NO)));
		depComfirmMap.put(Constants.IN_ORIGSYSDATETIME, String.valueOf(resultMap.get(Constants.POSTDATE)));
		depComfirmMap.put(Constants.IN_ORIGACQUIDCD, origAcquInstituIdCd);
		depComfirmMap.put(Constants.IN_ORIGFORWARDIDCD,	origiForwInstituIdCd); 
		utilProcessor.depComfirm(depComfirmMap);   //调用存款确认存储过程
		ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
		if (Constants.PE_ZERO.equals((String)depComfirmMap.get(Constants.OUT_DEPOSITSUCCYN))){	// 是否已经记账标记 0:记账成功 1：记账失败 
			logger.info("==================>CUPS转入已经记账成功 ，不需重复记账");
			ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));    	//原交易流水号
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); 	//交易状态:成功
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);   	//交易响应码
		}else{
			logger.info("==================>CUPS转入记账没有成功 ，需记账");
			utilProcessor.valTranLimit(ctx);//验证交易限制
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM);
			Init.formatIdenAM(ctx);//组装域61中的AM用法
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);		//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
			utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
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
