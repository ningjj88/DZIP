
package csii.dzip.action.jck.PreAuthorization;

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
 *@Description：POS预授权
 *@author :chenshq
 *@date：2010-08-16
 */
public class PreAuthAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK PreAuthAction ==================>Start");
		ctx.setData(Constants.IN_HOLDFALG, Constants.FREEZE); 	//冻结解冻标识:0：冻结 1：解冻
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS); //交易来源
		ctx.setData(Constants.IN_OPTNBR,null);  		//in_holdFlag标识判断是否必输项 0：为空	1：必输
		ctx.setData(Constants.IN_PREFLAG,Constants.PE_ZERO);  		//预授权：0
		ctx.setData(Constants.PE_PRE_AMT,ctx.getData(Constants.PE_TRAN_AMT));    //预授权金额
		ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);                //成商需要验证密码
		Map procedureMap  = new HashMap();
		procedureMap = Init.initPreAuth(ctx, procedureMap,Constants.FIX8583);  //初始化	
		utilProcessor.preAuthDoAction(ctx, procedureMap);    //执行预授权
	    ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
		ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
		if(Constants.PE_OK.equals(ctx.getData(Constants.FIX8583_RESP))){
			ctx.setData(Constants.FIX8583_AUTH,ctx.getData(Constants.PE_JOURNAL_NO));  //授权号
		}
		updateJoural.execute(ctx); 					// 修改流水交易状态
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
