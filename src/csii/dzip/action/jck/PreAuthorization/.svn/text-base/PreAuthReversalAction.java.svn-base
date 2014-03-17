
package  csii.dzip.action.jck.PreAuthorization;

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
 *@Description：POS预授权冲正
 *@author :chenshq
 *@date：2010-08-16
 */
public class PreAuthReversalAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK PreAuthReversalAction ==================>Start");
		Map procedureMap = new HashMap();
		Map paramMap = new HashMap();
		Map resultMap =utilProcessor.getOriSysTraNum(ctx);
		logger.info("JCK预授权冲正上笔交易信息(resultMap)==================>"+resultMap);
		String in_optNbr =String.valueOf(resultMap.get(Constants.PE_OPTNBR));
		String tranStat =String.valueOf(resultMap.get(Constants.TRANSTAT));
		ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));//原交易流水号
		if(!Constants.PE_NINE.equals(tranStat)){              //0：表示预授权成功
			ctx.setData(Constants.IN_HOLDFALG, Constants.UNFREEZE); 	//冻结解冻标识:0：冻结 1：解冻
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS); //交易来源
			ctx.setData(Constants.IN_OPTNBR,in_optNbr);  		    //in_holdFlag标识判断是否必输项 0：为空	1：必输
			ctx.setData(Constants.PE_PRE_AMT,resultMap.get(Constants.PE_TRAN_AMT));    //预授权金额
			ctx.setData(Constants.CHECK_TRACK2, Constants.PE_N);                //成商不需要验证二磁
			ctx.setData(Constants.CHECK_PIN, Constants.PE_N);                //成商不需要验证密码
			procedureMap = Init.initPreAuth(ctx, procedureMap,Constants.FIX8583);  //初始化			
			utilProcessor.preAuthDoAction(ctx, procedureMap);    //执行预授权
			ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
			if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
				paramMap.put(Constants.PE_TRANS_STAT,Constants.PE_NINE );   //交易状态: 已冲正
				paramMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号 
				paramMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
				paramMap.put(Constants.PE_OPTNBR, Constants.PE_ZERO);  //原 账务日期 
				ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);				//抹去冻结编号
			}
			updateJoural.execute(ctx,paramMap);					  // 修改流水交易状态;
		}else{
			ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_26);    //响应码
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
			ctx.setData(Constants.FIX8583_RESP,Constants.PE_OK);      //响应码
			ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
			ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);	//抹去冻结编号
			updateJoural.execute(ctx);						// 修改流水交易状态;
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
