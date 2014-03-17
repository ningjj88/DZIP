
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
 *@Description：POS预授权撤销
 *@author :chenshq
 *@date：2010-08-16
 */
public class PreAuthCancelAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK PreAuthCancelAction ==================>Start");
		Map procedureMap  = new HashMap();
		String authrid = ctx.getData(Constants.FIX8583_AUTH).toString();//获得预授权
		Map sqlMap = utilProcessor.getOptNbr(authrid);		//获得帐户冻结编号和状态
		logger.info("JCK预授权撤销上笔交易信息(sqlMap)==================>"+sqlMap);
		String in_optNbr = (String)sqlMap.get(Constants.PE_OPTNBR);
		String tranStat = (String)sqlMap.get(Constants.PE_TRANS_STAT);
		ctx.setData(Constants.PE_RLTSEQNO,authrid);           			//原交易流水号
//		ctx.setData(Constants.FIX8583_AUTH,Constants.PE_NULL);			//
		if(Constants.PE_ZERO.equals(tranStat)){              //0：表示预授权成功
			ctx.setData(Constants.IN_HOLDFALG, Constants.UNFREEZE); 	//冻结解冻标识:0：冻结 1：解冻
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS); //交易来源
			ctx.setData(Constants.IN_OPTNBR,in_optNbr);  		    //in_holdFlag标识判断是否必输项 0：为空	1：必输
			ctx.setData(Constants.PE_PRE_AMT,ctx.getData(Constants.PE_TRAN_AMT));    //预授权金额
			procedureMap = Init.initPreAuth(ctx, procedureMap,Constants.FIX8583);  //初始化		
			utilProcessor.preAuthDoAction(ctx, procedureMap);    //执行预授权
			ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
			if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
				procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_ONE );    //交易状态: 已撤销
				procedureMap.put(Constants.PE_JOURNAL_NO, authrid);    			//原交易流水号 
				procedureMap.put(Constants.PE_OPTNBR,Constants.PE_ZERO);    	//抹去冻结编号
//				procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
				ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);				//抹去冻结编号
			}
			updateJoural.execute(ctx,procedureMap);						// 修改流水交易状态;
		}else{
			switch(Integer.valueOf(tranStat)){
			case 1:						//1:表示预授权已经撤销
				logger.info("==================>JCK预授权撤销上笔交易已经撤销");
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
				ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);    //响应码
				ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
				break;								
			case 2:						//2:表示预授权已经完成
				logger.info("==================>JCK预授权撤销上笔交易已经完成");
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
				ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);    //响应码
				ctx.setData(Constants.FIX8583_EXPI,Constants.PE_EXPIREYEAR);
				break;								
			default :
				logger.info("==================>JCK预授权撤销上笔交易已经异常");
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
				ctx.setData(Constants.FIX8583_RESP,Constants.PE_22);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);   //响应码
				ctx.setData(Constants.FIX8583_EXPI, Constants.PE_EXPIREYEAR);
				break;
			}
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
