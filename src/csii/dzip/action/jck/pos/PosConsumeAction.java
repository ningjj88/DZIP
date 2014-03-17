
package csii.dzip.action.jck.pos;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:他行POS消费
 *@author :chenshq
 *@date：2010-08-16
 */
public class PosConsumeAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	
	public void execute(final Context ctx) throws PeException {
		logger.info("JCK PosConsumeAction ==================>Start");
		ctx.setData(Constants.ATMYN, Constants.PE_ZERO);//是否ATM  ATM ：1，POS：0
		ctx.setData(Constants.IN_DEPFLAG, Constants.TRS_FLAG_WTH); 	//出入账标识:0:出  1:入
		utilProcessor.valTranLimit(ctx);//验证金额限额和计算手续费
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){  
			ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);// 资金类型
			ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);//冲正标志:0：正常   1：冲正 	2：抹帐
			ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);  //成商需要验证密码
			utilProcessor.deductTranAMT(ctx,Constants.FIX8583);//执行数据库操作
			ctx.setData(Constants.FIX8583_RESP, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.FIX8583_EXPI, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期		
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：失败
			ctx.setData(Constants.FIX8583_RESP,ctx.getData(Constants.PE_HOST_RESP_CD));    //响应码 
			ctx.setData(Constants.FIX8583_EXPI,Constants.PE_EXPIREYEAR);  //卡有效期
		}
		updateJoural.execute(ctx);// 修改流水交易状态;
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
